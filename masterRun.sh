#!/bin/bash

#root permission needed!
if [ "$EUID" -ne 0 ]
		  then echo -e "\e[41mRoot permission required!\e[49m Please run as root because the Bluetooth server needs to be root."
		  exit 1
fi

#if the directory is not created then create it
if [ ! -d "bin" ]; then
	mkdir bin
	echo "Creating new bin folder."
fi

#working from EVERYTHING from the main dir!
javac -d bin/ -cp libs/java-json.jar:libs/bluecove-2.1.0.jar:libs/bluecove-emu-2.1.0.jar:libs/bluecove-gpl-2.1.0.jar utils/*.java httpClient/*.java servers/*.java 

#don't continue from this point if there were some build failures 
if [ $? -ne 0 ]; then
	echo -e "\e[41mERROR:\e[49m There were some build failures!"
	exit 1
fi

cd bin/ 


if [ $# -ge 2 ]; then
	tcpPort=$1
	udpPort=$2

	java -Djava.library.path=../jniLibs/ -cp .:../libs/java-json.jar:../libs/bluecove-2.1.0.jar:../libs/bluecove-emu-2.1.0.jar:../libs/bluecove-gpl-2.1.0.jar servers.mainServers $tcpPort $udpPort

else 
	echo -e "\e[43mWrong input!\e[49m Example usage can be sudo ./masterRun [TCP port] [UDP port]"
	exit 1
fi


