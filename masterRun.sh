#!/bin/bash

if [ $# -ge 1 ]; then
	#take the command
	communicationMethod=$1
	if [ "$communicationMethod" == "UDP" ]; then
		if [ $# -ne 2 ]; then
		    echo $0: "ERROR: You have to specify port number. Example ./masterRun.sh UDP [port number]"
		    exit 1
		else
			port=$2
			#navigate to the folder
			#cd Dropbox/Bachelor/9.\ Programming/3.\ P2P\ UDP/3.\ Sending\ on\ thread/
			#cd Dropbox/Bachelor/9.\ Programming/3.\ P2P\ UDP/2.\ Second\ version/

			#if the directory is not created then create it
			if [ ! -d "binary" ]; then
			  	mkdir binary
			  	echo "creating new binary forlder"
			fi

			# javac -d server/ -cp lib/java-json.jar ../utils/Utils.java UDPServer.java 

			# echo "The compiling was successful!"
			# cd server

			# java -cp .:../lib/java-json.jar UDPServer $port

			cd Servers/2.\ UDP/

			#NEW TRY
			javac -d ../../binary/ -cp ../../libs/java-json.jar ../../utils/uInputJNI.java ../../utils/Utils.java UDPServer.java 

			echo "The compiling was successful!"

			cd ../../binary/

			java -Djava.library.path=../jniLibs/ -cp .:../libs/java-json.jar UDPServer $port 
		fi

	elif [ "$communicationMethod" == "TCP" ]; then
		if [ $# -ne 3 ]; then
		    echo $0: "ERROR: You have to specify port number and mode(0 or 1). Example ./masterRun TCP [port number] [0 or 1]"
		    exit 1
		else

			#navigate to the folder
			#cd Dropbox/Bachelor/9.\ Programming/2.\ P2P\ TCP/1.\ Ready\ versions/5.\ Separate\ Utils/

			port=$2
			IsClosing=$3

			#if the directory is not created then create it
			if [ ! -d "binary" ]; then
			  	mkdir binary
			  	echo "creating new binary forlder"
			fi

			cd Servers/1.\ TCP/

			#NEW TRY
			javac -d ../../binary/ -cp ../../libs/java-json.jar ../../utils/uInputJNI.java ../../utils/Utils.java WiFiServer.java 

			echo "The compiling was successful!"

			cd ../../binary/

			java -Djava.library.path=../jniLibs/ -cp .:../libs/java-json.jar WiFiServer $port $IsClosing 1000
			#WORKING!

		fi


	elif [ "$communicationMethod" == "Bluetooth" ]; then
		if [ $# -ne 2 ]; then
		    echo $0: "ERROR: You have to specify whether the server is sending information or not (0 or 1). Example sudo ./masterRun Bluetooth [0 or 1]"
		    exit 1
		else
			#start the bluetooth
			#sudo /etc/init.d/bluetooth start #not runnign so make sure bluetooth is on

			#navigate to the folder
			#cd Dropbox/Bachelor/9.\ Programming/4.\ Java\ Bluetooth\ server/1.\ Ready\ versions/2.\ Flush\,\ downspeed\ measurement/2.\ Server/

			#new version
			cd Servers/3.\ Bluetooth/


			isSending=$2

			javac -d ../../binary/ -cp "../../libs/bluecove-2.1.0.jar:../../libs/bluecove-emu-2.1.0.jar:../../libs/bluecove-gpl-2.1.0.jar:../../libs/java-json.jar" ../../utils/*.java BluetoothServer.java 



			javac -d server/ -cp lib/java-json.jar ../utils/Utils.java WiFiServer.java 

			sudo java -Djava.library.path=../../jniLibs -cp .:../../libs/bluecove-2.1.0.jar:../../libs/bluecove-emu-2.1.0.jar:../../libs/bluecove-gpl-2.1.0.jar:../../libs/java-json.jar BluetoothServer $isSending

			sudo java -Djava.library.path=../../libs/. BluetoothServer 1
		fi
	else 
		echo "WRONG INPUT! Example usage can be sudo ./masterRun [Communication Method] [Port/isSending] [IsClosing]"
		exit 1
	fi
fi
