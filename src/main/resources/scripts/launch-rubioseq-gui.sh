#!/bin/bash
# Argument = -w war -p port -h

usage()
{
cat << EOF
usage: $0 options

This script runs jetty-runner to deploy rubioseq-gui.war and then launchs the default browser.

OPTIONS:
   -h	Show this message
   -w	war to deploy. If no specified, rubioseq-gui.war is used.
   -p	Port used to deploy the server. If no specified, 8080 is used.
EOF
}

SCRIPT=`basename $0`;
SCRIPTDIR="$(dirname "$0")"

case "$SCRIPTDIR" in
	/*)
		true
		;;
	*)
		SCRIPTDIR="${PWD}/$SCRIPTDIR"
		;;
esac

# Default parameters
WAR="${SCRIPTDIR}/rubioseq-gui.war"
WORKDIR="${HOME}/.local/share/rubioseq-gui"
JAVAPARAMS="-Xmx4G"
PORT="8080"
OPEN="xdg-open"
SERVERPID="${WORKDIR}/server.pid"
SERVERPORT="${WORKDIR}/server.port"

while getopts “hw:p:f” OPTION
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

if [ -f "$SERVERPID" ]
then
  PIDCHECK=`cat $SERVERPID`;
  if ps -p $PIDCHECK > /dev/null
  then
    echo "[$SCRIPT] One instance of RUbioSeq-GUI is already running (pid=$PIDCHECK)." 
    if [ -f "$SERVERPORT" ]
    then
      PORT=`cat $SERVERPORT`;
      $OPEN http://localhost:$PORT/login.zul
    fi
    exit 0
  fi
fi

if netstat -n --listening | grep ":$PORT" > /dev/null
then
  echo "[$SCRIPT] Port $PORT is already in use by other application. Please, use option -p <port> to specify a free port."
  exit -1
fi

if [ -f "$WAR" ]
then
  mkdir -p "$WORKDIR"
  cd "$WORKDIR"
  ( java $JAVAPARAMS -jar "$SCRIPTDIR"/jetty-runner-8.1.9.v20130131.jar --port $PORT "$WAR" & echo $! >&3 ) 3>$SERVERPID | while read line; do
    echo $line;
    case "$line" in 
      *"DB initialized"*)
	$OPEN http://localhost:$PORT/login.zul
	;;
    esac  
  done &
  echo $PORT > $SERVERPORT;
  wait
else
  echo "[$SCRIPT] $WAR does not exist.";
fi
