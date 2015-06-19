#Build Instructions for Magnet Messaging Server

(For more information, please visit [https://www.magnet.com/developer/magnet-message/](https://www.magnet.com/developer/magnet-message/))
###Build environment:

- Java 1.7.x SDK
- Maven 3.2.3+, but below Maven 3.3.1
- Verified on Ubuntu 14.0.4 and Mac OSX Yosemite
- MySQL 5.6 (for unit tests)


##Download and build source from one or more of the following repos

1. [https://github.com/magnetsystems/magnet-openfire](https://www.magnet.com/developer/magnet-message/)

   Note: You can skip this step if you're not modifying any code here. The release version is hosted.

   ```
   % make
   % ./mvn_install_local.sh
   ```
2. Build Magnet plugin and create messaging server zip: [https://github.com/magnetsystems/message-java](https://github.com/magnetsystems/message-java)

   ```
   % mvn clean install -s settings-magnet.xml
   ```
   You can find the messaging server zip file under “tools/mmx-server-zip/target”

3. Run the server:
   - unzip “mmx-server-1.0.2.zip”
   - Edit “mmx-server-1.0.2/conf/startup.properties
   - Start the server: 
   
     ```
     % cd ./mmx-server-1.0.2/bin
     % ./mmx-server.sh start
     ```
 


#Build Instructions for Magnet Message SDK for Android
For latest information on how to get setup for Android builds , refer to [http://developer.android.com/sdk/index.html](http://developer.android.com/sdk/index.html)

###Build environment:

- Android Studio
- Java 1.7 SDK
- Linux or Mac OSX Yosemite
- Gradle 2.2.1
- Android SDK 19, build tools version ”22.0.1"

```
% cd message-java/client/android
% ./gradlew clean build
```

# Upload Android aar library to local maven repository for building Android messaging apps locally
```
% ./gradlew uploadLocal
```
