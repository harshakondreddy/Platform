#OS specific support (must be 'true' or 'false').

: ${1?"Usage: $0 ARGUMENT"}

pingcountflag="-c"
case "`uname`" in
  CYGWIN* )
    ;;
  Darwin* )
    ;;
  MINGW* )
    pingcountflag="-n"
    ;;
esac

# Add check to see if dcokerhost is defined. If not, then throw error

export DOCKERHOST=`ping $pingcountflag 1 dockerhost | grep -Eo -m 1 '[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}';`
echo "DOCKERHOST = $DOCKERHOST"

export DOCKERHOST=$DOCKERHOST
export DBHOST=$DOCKERHOST
export EUREKAHOST=$DOCKERHOST
export SCCHOST=$DOCKERHOST
export MSGHOST=$DOCKERHOST
export WMSDUMMYHOST=10.2.55.87
export EFKHOST=$DOCKERHOST

echo "DOCKERHOST=$DOCKERHOST ::-:: DBHOST=$DBHOST ::-:: WMS-DUMMY-HOST=$WMSDUMMYHOST"

echo "SCCHOST=$SCCHOST ::-:: EUREKAHOST=$EUREKAHOST ::-:: MSGHOST=$MSGHOST"

echo "***************Bringing Up Below Service**************"

docker-compose -f $1 config --services

docker-compose -f $1 pull $2 $3 $4 $5 $6 $7 $8 $9
docker-compose -f $1 --verbose up -d $2 $3 $4 $5 $6 $7 $8 $9


