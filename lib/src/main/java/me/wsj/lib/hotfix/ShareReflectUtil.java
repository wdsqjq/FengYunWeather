package me.wsj.lib.hotfix;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ShareReflectUtil {


    /**
     * 从 instance 到其父类 找 name 属性
     *
     * @param instance
     * @param name
     * @return
     * @throws NoSuchFieldException
     */
    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                //查找当前类的 属性(不包括父类)
                Field field = clazz.getDeclaredField(name);

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }
        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    /**
     * 从 instance 到其父类 找  name 方法
     *
     * @param instance
     * @param name
     * @return
     * @throws NoSuchFieldException
     */
    public static Method findMethod(Object instance, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }
        throw new NoSuchMethodException("Method "
                + name
                + " with parameters "
                + Arrays.asList(parameterTypes)
                + " not found in " + instance.getClass());
    }


    /**
     * @param instance
     * @param fieldName
     * @param fixs      补丁的Element数组
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void expandFieldArray(Object instance, String fieldName, Object[] fixs)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        //拿到 classloader中的dexelements 数组
        Field jlrField = findField(instance, fieldName);
        //old Element[]
        Object[] old = (Object[]) jlrField.get(instance);


        //合并后的数组
        Object[] newElements = (Object[]) Array.newInstance(old.getClass().getComponentType(),
                old.length + fixs.length);

        // 先拷贝新数组
        System.arraycopy(fixs, 0, newElements, 0, fixs.length);
        System.arraycopy(old, 0, newElements, fixs.length, old.length);

        //修改 classLoader中 pathList的 dexelements
        jlrField.set(instance, newElements);
    }


}
