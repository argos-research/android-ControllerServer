#!/bin/bash

#if the directory is not created then create it
if [ ! -d "build" ]; then
	mkdir build
	echo "Creating new build folder."
fi

#working from EVERYTHING from the main dir!
javac -d build/ -cp libs/java-json.jar:libs/bluecove-2.1.0.jar:libs/bluecove-emu-2.1.0.jar:libs/bluecove-gpl-2.1.0.jar utils/*.java servers/*.java 

#don't continue from this point if there were some build failures 
if [ $? -ne 0 ]; then
	echo -e "\e[41mERROR:\e[49m There were some build failures!"
	exit 1
fi

cd build/ 


if [ $# -ge 1 ]; then
	#take the command
	communicationMethod=$1
	if [ "$communicationMethod" == "UDP" ]; then
		if [ $# -ne 2 ]; then
		    echo -e "\e[44mWrong input!\e[49m You have to specify port number. Example sudo ./masterRun.sh UDP [port number]"
		    exit 1
		else
			port=$2
			
			java -Djava.library.path=../jniLibs/ -cp .:../libs/java-json.jar servers.UDPServer $port
		fi

	elif [ "$communicationMethod" == "TCP" ]; then
		if [ $# -ne 2 ]; then
		    echo -e "\e[46mWrong input!\e[49m You have to specify port number. Example sudo ./masterRun TCP [port number]"
		    exit 1
		else

			port=$2

			java -Djava.library.path=../jniLibs/ -cp .:../libs/java-json.jar servers.TCPServer $port 0 1000

		fi


	elif [ "$communicationMethod" == "Bluetooth" ]; then
		if [ "$EUID" -ne 0 ]
		  then echo -e "\e[41mRoot permission required!\e[49m Please run as root because the Bluetooth server needs to be root."
		  exit 1
		fi

		#start the bluetooth
		#sudo /etc/init.d/bluetooth start #not runnign so make sure bluetooth is on

		java -Djava.library.path=../jniLibs/ -cp .:../libs/java-json.jar:../libs/bluecove-2.1.0.jar:../libs/bluecove-emu-2.1.0.jar:../libs/bluecove-gpl-2.1.0.jar servers.BluetoothServer 1
	fi

else 
	echo -e "\e[43mWrong input!\e[49m Example usage can be sudo ./masterRun [Communication Method] [Port/isSending] [IsClosing]"
	exit 1
fi

