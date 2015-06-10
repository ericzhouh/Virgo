#!/bin/sh

CURRENT_PATH=`pwd`
echo ${CURRENT_PATH}

TOMCAT_ROOT="/data/tomcat"
TOMCAT_BIN_PATH=${TOMCAT_ROOT}"/bin"
DEPLOY_PATH=${TOMCAT_ROOT}"/webapps/ROOT.war"
VIRGO_CODE_ROOT="/data/codes/Virgo"

echo "tomcat root: ${TOMCAT_ROOT}"
echo "deploy path: ${DEPLOY_PATH}"
echo "tomcat bin path: ${TOMCAT_BIN_PATH}"
echo "virgo code root: ${VIRGO_CODE_ROOT}"

#echo "building virgo..."
cd ${VIRGO_CODE_ROOT}
git pull origin master
mvn -U clean package
#echo "building virgo complete"

echo "shutdown tomcat..."
pid=`ps aux | grep tomcat | grep Bootstrap |awk '{print $2}'`
if [ "x$pid" != "x" ]; then
    echo "kill pid "${pid}
    kill -9 ${pid};
fi;
echo "shutdown tomcat complete"

echo "mv package..."
mv ${VIRGO_CODE_ROOT}/Virgo-restapi/target/Virgo-restapi.war ${DEPLOY_PATH}
echo "mv package complete"

echo "start tomcat..."
${TOMCAT_BIN_PATH}/startup.sh
echo "start tomcat complete"

cd ${CURRENT_PATH}