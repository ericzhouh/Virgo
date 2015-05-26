#!/bin/sh

CURRENT_PATH=`pwd`
echo ${CURRENT_PATH}

TOMCAT_ROOT="/data/tomcat"
TOMCAT_BIN_PATH=${TOMCAT_ROOT}"/bin"
DEPLOY_PATH=${TOMCAT_ROOT}"/webapps/ROOT.war"
VIRGO_CODE_ROOT=${CURRENT_PATH}

echo "tomcat root: ${TOMCAT_ROOT}"
echo "deploy path: ${DEPLOY_PATH}"
echo "tomcat bin path: ${TOMCAT_BIN_PATH}"
echo "virgo code root: ${VIRGO_CODE_ROOT}"

echo "shutdown tomcat..."
${TOMCAT_BIN_PATH}/shutdown.sh
echo "shutdown tomcat complete"

echo "mv package..."
cp ${VIRGO_CODE_ROOT}/Virgo-restapi/target/Virgo-restapi.war ${DEPLOY_PATH}
echo "mv package complete"

echo "start tomcat..."
${TOMCAT_BIN_PATH}/startup.sh
echo "start tomcat complete"

