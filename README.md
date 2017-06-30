# android-ControllerServer
android smartphone as sensor-based input device for control of an automotive simulator (server part).

## Usage
Once you have cloned the project, navigate to the android-ControllerServer directory. From there open the masterRun.sh and change the 17th line in order to match your java version (if you skip this and the java version is not the same, you will get the error "jni.h" not found!). After that just run the servers with the command from below and follow the instructions printed to the console:

```
./masterRun 
```
Keep in mind, that the server should run before you open Speed Dreams 2 otherwise Speed Dreams 2 won't be able to find and use the newly created Joystick pointer of the uInput device.

## Required packages for Ubuntu

This server application is tested on Ubuntu 15.10 with openjdk version "1.8.0_91" (OpenJDK Runtime Environment (build 1.8.0_91-8u91-b14-3ubuntu1~15.10.1-b14) & OpenJDK 64-Bit Server VM (build 25.91-b14, mixed mode)). You will have to install in addition the bluecove library on you machine with the following command:

```
sudo apt-get install libbluetooth-dev 
````

## Known errors

If you are getting the error:
```
javax.bluetooth.ServiceRegistrationException: Can not open SDP session. [2] No such file or directory
```
you will have to edit line **9** in the file */etc/systemd/system/bluetooth.target.wants/bluetooth.service* (you will need root permissions for this) from:
```
ExecStart=/usr/lib/bluetooth/bluetoothd
```
to
```
ExecStart=/usr/lib/bluetooth/bluetoothd -C
```
After changing the line, you will have to reload and restart the bluetooth device with:
```
#reload
systemctl daemon-reload

#restart
sudo service bluetooth restart
```


## Bluetooth Server
For the bluetooth server you have to make sure that the client (the android application) and the server (the ubuntu PC) are paired.