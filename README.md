# android-ControllerServer
android smartphone as sensor-based input device for control of an automotive simulator (server part).

## Usage
Once you have cloned the project, navigate to the android-ControllerServer directory and run the servers with the command below and just follow the instructions printed to the console:

```
./masterRun 
```

## Required packages for Ubuntu

The server is tested using openjdk 8 so desirable would be to use openjdk 8. Apart from the java JDK, you will have to install the bluecove library on you machine with the following command:

```
sudo apt-get install libbluetooth-dev 
````


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