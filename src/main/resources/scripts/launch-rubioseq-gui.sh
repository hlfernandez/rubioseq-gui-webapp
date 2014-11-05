#!/bin/bash
# Argument = -w war -p port -h

usage()
{
cat << EOF
usage: $0 options

This script runs jetty-runner to deploy rubioseq-gui.war and then launchs the default browser.

OPTIONS:
   -h      Show this message
   -w      war to deploy. If no specified, rubioseq-gui.war is used.
   -p      Port used to deploy the server. If no specified, 8080 is used.   
EOF
}

SCRIPT=`basename $0`;
WAR="rubioseq-gui.war"
PORT="8080"
OPEN="xdg-open";

while getopts “hw:p:” OPTION
do
     case $OPTION in
         h)
             usage
             exit 1
             ;;
         w)
             WAR=$OPTARG
             ;;             
         p)
             PORT=$OPTARG
             ;;             
         ?)
             usage
             exit
             ;;
     esac
done

case "$OSTYPE" in
	darwin*)
	  OPEN="open";
	  echo "[$SCRIPT] (O.S. detection) 'OS X' Detected: Using command 'open' to open the main page of the app." ;;
	linux*)
	  echo "[$SCRIPT] (O.S. detection) 'Linux' Detected: Using command 'xdg-open' to open the main page of the app." ;;
	*) 
	  echo "[$SCRIPT] (O.S. detection)[Could not determine the host O.S.: Using command 'xdg-open' to open the main page of the app." ;;
esac

if [ -f $WAR ]
then
  java -jar jetty-runner-8.1.9.v20130131.jar --port $PORT $WAR | while read line; do
  echo $line;
  case "$line" in 
    *"DB initialized"*)
      $OPEN http://localhost:$PORT/login.zul
      ;;
  esac  
  done
else
  echo "[$SCRIPT] $WAR does not exist.";
fi

