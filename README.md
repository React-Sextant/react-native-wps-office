# React Native实现调用wps app打开word/pdf文档


## Getting started

`$ npm install react-native-wps@https://github.com/zhijiasoft/react-native-wps.git --save`

### Mostly automatic installation

`$ react-native link react-native-wps`

### Manual installation


#### Android

1. Append the following lines to `android/settings.gradle`:

  	```
  	include ':react-native-wps'
  	project(':react-native-wps').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-wps/android')
  	```
2. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
    
  	```
    compile project(':react-native-wps')
  	```
3. Import Package
  
    **For React Native >= v0.29**
  
    Update the `MainApplication.java` file to use the Plugin via the following changes:
    
    ```java
    ...
    // 1. Import the plugin class.
   import com.zhijia.WpsPackage;
    
    public class MainApplication extends Application implements ReactApplication {
    
        private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
            ...   
            @Override
            protected List<ReactPackage> getPackages() {
                // 2. Instantiate an instance of the Plugin runtime and add it to the list of
                // existing packages.
                return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new WpsPackage()
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
    import com.zhijia.WpsPackage;
    
    public class MainActivity extends ReactActivity {    
        @Override
        protected List<ReactPackage> getPackages() {
            // 2. Instantiate an instance of the Plugin runtime and add it to the list of
            // existing packages.
            return Arrays.<ReactPackage>asList(
                new MainReactPackage(),
                new WpsPackage()
            );
        }
    
        ...
    }
    ```


## Usage

### Define FileProvider

Defining a FileProvider for your app requires an entry in your manifest. 
This entry specifies the authority to use in generating content URIs, as 
well as the name of an XML file that specifies the directories your app can share.

The following snippet shows you how to add the FileProvider to your `AndroidManifest.xml`

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myapp">
    <application
        ...>
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="com.example.myapp.fileprovider"
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


### Open pdf/docx in WPS Office

Finally you can send a `ExternalDirectoryPath` by [react-native-fs](https://github.com/itinance/react-native-fs) to wake up `WPS office` app. 

```javascript
import WpsAndroid from 'react-native-wps';
import {ExternalDirectoryPath,downloadFile} from 'react-native-fs';

const downloadDest = `${RNFS.ExternalDirectoryPath}/test.pdf`;
const options = {
    fromUrl: 'http://example.com/test.pdf',
    toFile: downloadDest,
    background: true,
};
try {
    const ret = downloadFile(options);
    ret.promise.then(res => {
        console.log('file://' + downloadDest)
        WpsAndroid.open(
            downloadDest,
            'com.demo.app.fileprovider',
            'application/pdf'
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

#### `open(String[] args): Promise<string>`
三个必填参数，分别是：
```
filePath: String;   //文件外部存储地址，如/storage/emulated/0/Android/data/com.demo/files/test.pdf
your.package.name.fileprovider: String,;    //当前app包名+fileprovider，如"com.demo.app.fileprovider"
fileType: String;   //文件类型,如："application/pdf"
```
