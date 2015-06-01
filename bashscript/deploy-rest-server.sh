#!/bin/bash

DEPLOY_MACHINE_IP="127.0.0.1"
DEPLOY_JAR_DIR="/data/codes/Virgo/Virgo-restapi/target/"
DEPLOY_JAR_NAME="Virgo-restapi.war"
DEPLOY_JAR_PATH=${DEPLOY_JAR_DIR}${DEPLOY_JAR_NAME}

REST_BASE_DIR="/data/tomcat/"
REST_JAR_NAME="ROOT.war"
REST_JAR_DIR=${REST_BASE_DIR}"/webapps/"
REST_JAR_PATH=${REST_JAR_DIR}${REST_JAR_NAME}
REST_STARTUP_CMD=${REST_BASE_DIR}"bin/startup.sh"

LOCAL_IP=`hostname -I`


echo "Enter" ${LOCAL_IP}
echo "Shutdown REST service now!"

PID_TO_KILL=`ps aux | grep ${REST_BASE_DIR} | grep -v grep | grep "java" | grep "bootstrap.jar" | awk '{print $2}'`
kill -9 ${PID_TO_KILL}
sleep 2s;
echo "Virgo Restful API on " ${LOCAL_IP} "has been shutdown"

echo "rm -rf ${REST_JAR_DIR}ROOT*"
rm -rf ${REST_JAR_DIR}ROOT*
sleep 2s;

echo "'ubuntu@'${DEPLOY_MACHINE_IP}':'${DEPLOY_JAR_PATH} ${REST_JAR_PATH}"
scp 'ubuntu@'${DEPLOY_MACHINE_IP}':'${DEPLOY_JAR_PATH} ${REST_JAR_PATH}
sleep 2s;

echo "start REST API now!"
sh /data/tomcat/8180/bin/startup.sh
sleep 1s;
