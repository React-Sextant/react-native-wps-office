# React Native实现调用wps app打开word/pdf文档


## Getting started

`$ npm install git+https://git@github.com/react-sextant/react-native-wps-office.git --save`

### Mostly automatic installation

`$ react-native link react-native-wps-office`

### Manual installation


#### Android

1. Append the following lines to `android/settings.gradle`:

  	```
  	include ':react-native-wps-office'
  	project(':react-native-wps-office').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-wps-office/android')
  	```
2. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
    
  	```
    compile project(':react-native-wps-office')
  	```
3. Import Package
  
    **For React Native >= v0.29**
  
    Update the `MainApplication.java` file to use the Plugin via the following changes:
    
    ```java
    ...
    // 1. Import the plugin class.
   import com.github.react.sextant.WPSOfficePackage;
    
    public class MainApplication extends Application implements ReactApplication {
    
        private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
            ...   
            @Override
            protected List<ReactPackage> getPackages() {
                // 2. Instantiate an instance of the Plugin runtime and add it to the list of
                // existing packages.
                return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new WPSOfficePackage()
                );
            }
        };
    }
    ```
    
    **For React Native v0.19 - v0.28**
  
    Update the `MainActivity.java` file to use the Plugin via the following changes:
    
    ```java
    ...
    // 1. Import the plugin class (if you used RNPM to install the plugin, this
    // should already be done for you automatically so you can skip this step).
    import com.github.react.sextant.WPSOfficePackage;
    
    public class MainActivity extends ReactActivity {    
        @Override
        protected List<ReactPackage> getPackages() {
            // 2. Instantiate an instance of the Plugin runtime and add it to the list of
            // existing packages.
            return Arrays.<ReactPackage>asList(
                new MainReactPackage(),
                new WPSOfficePackage()
            );
        }
    
        ...
    }
    ```


## Usage

### Open pdf/docx in WPS Office

Finally you can send a `ExternalDirectoryPath` by [react-native-fs](https://github.com/itinance/react-native-fs) to wake up `WPS office` app. 

```javascript
import WPSOffice from 'react-native-wps-office';
import {ExternalDirectoryPath,downloadFile} from 'react-native-fs';

const downloadDest = `${ExternalDirectoryPath}/test.pdf`;
const fileOptions = {
    fromUrl: 'http://example.com/test.pdf',
    toFile: downloadDest,
    background: true,
};

const wpsOptions = {
    "OpenMode":"ReadOnly",//只读模式
    "ClearTrace": true    //关闭文件时删除使用记录
};

try {
    const ret = downloadFile(fileOptions);
    ret.promise.then(res => {
        console.log('file://' + downloadDest)
        WPSOffice.open(
            downloadDest,   //or: /storage/emulated/0/Android/data/com.your.package/files/test.pdf
            'application/pdf',
            wpsOptions
        )
            .then(res=>console.log(res))
            .catch(err=>console.log(err))
    }).catch(err => {
        console.log('err', err);
    });
}
catch (e) {
    console.log(error);
}
```

### API

#### `open(FilePath: String, MIMEType: String, Options: Object): Promise<string>`
两个必填参数，分别是：
```
FilePath: String;   //文件外部存储地址，如/storage/emulated/0/Android/data/com.demo/files/test.pdf
MIMEType: String;   //mime type of file,eg:"application/pdf" See details in Reference[1]
Options: Object;    //第三方启动模式，详见#Options
```

### Options

**1: 文档打开参数列表**


| 参数名 | 参数说明 | 类型 | 默认值 |
|------|------|------|------|
|OpenMode|打开文件的模式|String|Normal| |
| EnterReviseMode | 以修订模式打开文档 | boolean | false |
|SendSaveBroad|文件保存时是否发送广播|boolean|false|
|SendCloseBroad|文件关闭时是否发送广播|boolean|false|
| HomeKeyDown | 监听home键并发广播 | boolean | false |
| BackKeyDown | 监听back键并发广播 | boolean | false |
| ClearBuffer | 关闭文件时是否请空临时文件 | boolean | false |
| ClearTrace | 关闭文件时是否删除使用记录 | boolean | false |
| ClearFile | 关闭文件时是否删除打开的文件 | boolean | false |
| AutoJump | 是否自动跳转到上次查看的进度 | boolean | false |
------

**2.打开模式参数列表 {"OpenMode": `*`}**

| 模式 | 说明 | 备注 |
|------|------|------|
|ReadOnly|只读模式||
|Normal|正常模式||
|ReadMode|打开直接进入阅读器模式|仅Word、TXT文档支持|
|SaveOnly|保存模式(打开文件,另存,关闭)|仅Word、TXT文档支持|
------

## Other( read only )

#### Use Android's FileProvider to get rid of the Storage Permission[2]

#### Define FileProvider

Defining a FileProvider for your app requires an entry in your manifest. 
This entry specifies the authority to use in generating content URIs, as 
well as the name of an XML file that specifies the directories your app can share.

The following snippet shows you how to add the FileProvider to your `AndroidManifest.xml`

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.packageName.app">
    <application
        ...>
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="com.packageName.app.fileprovider"
            android:grantUriPermissions="true"
            android:exported="false">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
        ...
    </application>
</manifest>
```
### Specify directories

Once you have added the FileProvider to your app manifest, you need to specify the 
directories that contain the files you want to share. To specify the directories, 
start by creating the file `file_paths.xml` in the `res/xml/` subdirectory of your project.
 
In this file, specify the directories by adding an XML element for each directory. 
The following snippet shows you an example of the contents of `res/xml/file_paths.xml`. 
The snippet also demonstrates how to share a subdirectory of the files/ directory 
in your internal storage area:

```xml
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path name="files" path="/" />
</paths>
```

For setting up other directories (cache, external storage, ...) follow the guide at 
[https://developer.android.com/reference/android/support/v4/content/FileProvider.html](https://developer.android.com/reference/android/support/v4/content/FileProvider.html) 


## Reference
 - [1] [Office 2007 File Format MIME Types for HTTP Content Streaming](https://blogs.msdn.microsoft.com/vsofficedeveloper/2008/05/08/office-2007-file-format-mime-types-for-http-content-streaming-2/)
 - [2] [Use Android's FileProvider to get rid of the Storage Permission](https://drivy.engineering/android-fileprovider/)
