# AndroidRaspberry
Communication between a Raspberry Pi and an Android with sockets.

AndroidClient repository is an AndroidStudio project containing the source code of the android application of the client.
AndroidClient.apk is the android application of the client.
JavaServer.java and JavaMultiSever.java are two differents server the first work only for one client and the second can work with multiple client.

To compile the JavaServer.java or the JavaMultiSever.java, you have to 
1) Install the library pi4j
2) Compile with the command "sudo pi4j JavaServer.java" or  "sudo pi4j JavaMultiSever.java" 
3) Launch the server by copying the command given in 2) and replace "javac" by "java"

To use the AndroidClient application you have to:
1) Allow unknown sources in your phone
2) Copy the file AndroidClient.apk on your phone
3) Execute it (by for example use and file explorer)

When server and client are running (on the same network) you just have to connect your phone on the same id used by the server application (use ifconfig to know your ip).

If you want to modify and rebuild the AndroidClient project, you have to download the folder "AndroidClient" and import it in you IDE (AndroidStudio here).
The only important files into this folder are:
- MainActivity.java
- ClientThread.java
- activity_main.xml
- AndroidManifest.xml 

Bug known: In the android application, the boutton disconnect doesn't work, you have to push on other button after pushing it to disconnect.
