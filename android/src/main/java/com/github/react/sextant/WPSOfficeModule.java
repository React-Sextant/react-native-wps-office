package com.github.react.sextant;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.io.File;
import java.util.List;

public class WPSOfficeModule extends ReactContextBaseJavaModule {

    public WPSOfficeModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private boolean isAvilible( Context context,String packageName ) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    @ReactMethod
    public void open(
            String UriFromReact,
            String MIMETypes,
            ReadableMap options,
            Promise promise
    ){
        if(isAvilible(getReactApplicationContext(), "cn.wps.moffice_eng")){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            //配置wps阅读模式
            if (options.hasKey("OpenMode")) {
                bundle.putString("OpenMode", options.getString("OpenMode"));
            }
            if (options.hasKey("SendSaveBroad")) {
                bundle.putBoolean("SendSaveBroad", options.getBoolean("SendSaveBroad"));
            }
            if (options.hasKey("SendCloseBroad")) {
                bundle.putBoolean("SendCloseBroad", options.getBoolean("SendCloseBroad"));
            }
            if (options.hasKey("ClearBuffer")) {
                bundle.putBoolean("ClearBuffer", options.getBoolean("ClearBuffer"));
            }
            if (options.hasKey("ClearTrace")) {
                bundle.putBoolean("ClearTrace", options.getBoolean("ClearTrace"));
            }
            if (options.hasKey("ClearFile")) {
                bundle.putBoolean("ClearFile", options.getBoolean("ClearFile"));
            }
            if (options.hasKey("AutoJump")) {
                bundle.putBoolean("AutoJump", options.getBoolean("AutoJump"));
            }
            if (options.hasKey("HomeKeyDown")) {
                bundle.putBoolean("HomeKeyDown", options.getBoolean("HomeKeyDown"));
            }
            if (options.hasKey("BackKeyDown")) {
                bundle.putBoolean("BackKeyDown", options.getBoolean("BackKeyDown"));
            }
            if (options.hasKey("EnterReviseMode")) {
                bundle.putBoolean("EnterReviseMode", options.getBoolean("EnterReviseMode"));
            }


            //设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //打开wps
            intent.setClassName("cn.wps.moffice_eng",
                    "cn.wps.moffice.documentmanager.PreStartActivity2");

            //读取文件
            File file = new File(UriFromReact);
            if (file.exists()){
                //传递权限，由于7.0以上的手机需要具有文件权限才能打开，不加权限会崩溃
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(getReactApplicationContext(), getReactApplicationContext().getPackageName() + ".wpsProvider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                intent.setDataAndType(uri,MIMETypes);
                intent.putExtras(bundle);

                //启动activity
                promise.resolve("success");
                getReactApplicationContext().startActivity(intent);
            }else {
                promise.reject("文件不存在");
            }
        }else {
            promise.reject("未安装WPS APP!");
        }
    }

    @Override
    public String getName() {
        return "WPSOffice";
    }
}
