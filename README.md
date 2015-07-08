MMX Hackathon - Server Side Enchancement
========================================
Hacked by: Rahul Phadnis & Sujay Datar

Simplify Magnet Message server integration using Webhooks.
----------------------------------------------------------

Webhooks provide a mechanism for integrating different systems using HTTP. Webhooks are typically used for
for event notifications.

Magnet Message has been enhanced to support webhooks for the following events:

| Type Identifier | Description |
| ------------- | ------------- | -----|
| _MESSAGE_WITH_META_ | Message with specific meta key value pair is processed |
| _USER_CREATED_        | New user is created |


An HTTP POST request is sent to the registered target URL when these events occur.

In future we can add support for events such as:
- Push message acknowledged
- MMX Message acknowledged
- Message timed out
and more....

## Payload details
Details about the JSON payload that is posted to the target URL for the hook.
 
* Message with specific meta key value pair
````
{
        "recipientUsernames":["rahuldemo"],
        "deviceId":"3992B549-50F5-48BC-8527-A86E291B8CED",
        "content":"I need help with stuff in Aisle 10",
        "metadata":
            {   "mtype":"support",
                "date":"20150701"
            }
}
````    

## Database changes

- Add a new table for registering Webhooks.

## Rest API
API has been added for:
- Creating new webhooks
- Retrieving a webhook using its id

###  Create Webhook API
* Method: Post
* Endpoint: http:\<server\>:5220/mmxmgmt/api/v1/apps/hooks
* Required headers: X-mmx-app-id, X-mmx-api-key
* Sample request body:
```
{
   "hookName" : "SupportMessage",
   "targetURL" : "http://localhost:8443/api/webhook/message",
   "eventType" : "MESSAGE_WITH_HEADER",
   "eventConfig" : {"mtype" : "support" }
}
```
## Demo
For demonstration, we setup the hacked magnet message server. We then defined a hook for getting notified when a message with specific meta key value pair is 
processed by the server. Blowfish server was used for providing the controller which served as a target URL. 
In the controller we wrote information about the message to a log file.

Here is an example:
```
10:44:57.288 [qtp504829511-1075] WARN  c.m.e.c.MessageHookController - Message Info received:MessageInfo{content='I need help with stuff in Aisle 10', recipientUsernames=[rahuldemo], deviceId='3992B549-50F5-48BC-8527-A86E291B8CED', metadata={mtype=support, date=20150701}}

```

## MMX - Slack integration demo

A simple python server in MMX-Slack directory can be used to register a webhook and send a message to a slack channel
To run the demo:

Pre-requisites:

- python2.7 is installed
- pip is installed https://pip.pypa.io/en/latest/installing.html
- Install the following packages using pip:
```
		pip install flask
		pip install requests
```
- Slack Web API token - https://api.slack.com/web
- A magnet message installation, appId and apiKey
- Slack channel name that you want to post a message to.

Run the server:

python server.py

Fill in the assicated fields and click submit.

Now whenever Magnet Message app receives a message with a header specified in the key and value fields, it will invoke a webhook which in turn will send a Slack message to the configured channel

---
#Build Instructions for Magnet Messaging Server

(For more information about the product, please visit [https://www.magnet.com/developer/magnet-message/](https://www.magnet.com/developer/magnet-message/))
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
