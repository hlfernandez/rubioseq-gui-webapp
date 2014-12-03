#!/bin/bash
# Argument = -h

usage()
{
cat << EOF
usage: $0 options

This script stops the jetty-runner used to deploy rubioseq-gui.war that has been launched with ./launch-rubioseq-gui.sh.

OPTIONS:
   -h      Show this message
EOF
}

while getopts “hw:p:f” OPTION
do
     case $OPTION in
         h)
             usage
             exit 1
             ;;
         ?)
             usage
             exit
             ;;
     esac
done

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
WORKDIR="${HOME}/.local/share/rubioseq-gui"
SERVERPID="${WORKDIR}/server.pid"
SERVERPORT="${WORKDIR}/server.port"

if [ -f "$SERVERPID" ]
then
  echo "[$SCRIPT] Stopping RUbioSeq-GUI...";
  pid=`cat $SERVERPID`;
  if ps -p $pid > /dev/null
  then
    echo "[$SCRIPT] Stopping process with pid = $pid";
    kill -9 $pid;
  fi  
  rm $SERVERPID;
  rm $SERVERPORT;
  echo "[$SCRIPT] RUbioSeq-GUI has been stopped. Use ./launch-rubioseq-gui.sh to run it again.";
else
  echo "[$SCRIPT] Could not determine the pid of the process.";
fi
