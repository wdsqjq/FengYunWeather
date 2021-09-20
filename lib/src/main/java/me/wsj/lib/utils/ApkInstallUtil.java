package me.wsj.lib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

public class ApkInstallUtil {


    public static void installApk(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri apkUri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            apkUri = uri;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            apkUri = uri;
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
//            String apkPath = uri.getPath();
//            File file = new File(apkPath);
//            apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            apkUri = uri;
            // 添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

        context.startActivity(intent);
    }
}
