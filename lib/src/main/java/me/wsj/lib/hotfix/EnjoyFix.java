package me.wsj.lib.hotfix;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class EnjoyFix {

    private static final String TAG = "EnjoyFix";

    private static File initHack(Context context) {

        File hackDir = context.getDir("hack", Context.MODE_PRIVATE);
        File hackFile = new File(hackDir, "hack.jar");
        if (!hackFile.exists()) {
            BufferedInputStream is = null;
            BufferedOutputStream os = null;
            try {
                is = new BufferedInputStream(context.getAssets().open("hack" +
                        ".jar"));
                os = new BufferedOutputStream(new FileOutputStream(hackFile));
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return hackFile;

    }

    public static void installPatch(Application application, File patch) {
        File hackFile = initHack(application);
        ClassLoader classLoader = application.getClassLoader();
        List<File> files = new ArrayList<>();
        if (patch.exists()) {
            files.add(patch);
        }
        files.add(hackFile);
        File dexOptDir = application.getCacheDir();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                ClassLoaderInjector.inject(application, classLoader, files);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            try {
                //23 6.0及以上
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    V23.install(classLoader, files, dexOptDir);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    V19.install(classLoader, files, dexOptDir); //4.4以上
                } else {  // >= 14
                    V14.install(classLoader, files, dexOptDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static final class V23 {

        private static void install(ClassLoader loader, List<File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException,
                IOException {
            //找到 pathList
            Field pathListField = ShareReflectUtil.findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);

            ArrayList<IOException> suppressedExceptions = new ArrayList<>();
            // 从 pathList找到 makePathElements 方法并执行
            // 得到补丁创建的 Element[]
            Object[] objects = makePathElements(dexPathList,
                    new ArrayList<>(additionalClassPathEntries), optimizedDirectory,
                    suppressedExceptions);

            //将原本的 dexElements 与 makePathElements生成的数组合并
            ShareReflectUtil.expandFieldArray(dexPathList, "dexElements", objects);
            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    Log.w(TAG, "Exception in makePathElement", e);
                    throw e;
                }

            }
        }

        /**
         * 把dex转化为Element数组
         */
        private static Object[] makePathElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory,
                ArrayList<IOException> suppressedExceptions)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            //通过阅读android6、7、8、9源码，都存在makePathElements方法
            Method makePathElements = ShareReflectUtil.findMethod(dexPathList, "makePathElements",
                    List.class, File.class,
                    List.class);
            return (Object[]) makePathElements.invoke(dexPathList, files, optimizedDirectory,
                    suppressedExceptions);
        }
    }

    private static final class V19 {

        private static void install(ClassLoader loader, List<File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException,
                IOException {
            Field pathListField = ShareReflectUtil.findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);
            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            ShareReflectUtil.expandFieldArray(dexPathList, "dexElements",
                    makeDexElements(dexPathList,
                            new ArrayList<File>(additionalClassPathEntries), optimizedDirectory,
                            suppressedExceptions));
            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    Log.w(TAG, "Exception in makeDexElement", e);
                    throw e;
                }
            }
        }

        private static Object[] makeDexElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory,
                ArrayList<IOException> suppressedExceptions)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Method makeDexElements = ShareReflectUtil.findMethod(dexPathList, "makeDexElements",
                    ArrayList.class, File.class,
                    ArrayList.class);


            return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory,
                    suppressedExceptions);
        }
    }

    /**
     * 14, 15, 16, 17, 18.
     */
    private static final class V14 {


        private static void install(ClassLoader loader, List<File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException {

            Field pathListField = ShareReflectUtil.findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);

            ShareReflectUtil.expandFieldArray(dexPathList, "dexElements",
                    makeDexElements(dexPathList,
                            new ArrayList<File>(additionalClassPathEntries), optimizedDirectory));
        }

        private static Object[] makeDexElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory)
                throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            Method makeDexElements =
                    ShareReflectUtil.findMethod(dexPathList, "makeDexElements", ArrayList.class,
                            File.class);
            return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory);
        }
    }

}
