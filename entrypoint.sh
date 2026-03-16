#!/bin/sh
set -e
# Escribir config de BD desde env (por si Railway no las pasa a Spring)
mkdir -p /app/config
if [ -n "$SPRING_DATASOURCE_URL" ]; then
  echo "spring.datasource.url=$SPRING_DATASOURCE_URL" > /app/config/application-railway.properties
  echo "spring.datasource.username=$SPRING_DATASOURCE_USERNAME" >> /app/config/application-railway.properties
  echo "spring.datasource.password=$SPRING_DATASOURCE_PASSWORD" >> /app/config/application-railway.properties
  export SPRING_CONFIG_ADDITIONAL_LOCATION="file:/app/config/"
elif [ -n "$MYSQLHOST" ]; then
  echo "spring.datasource.url=jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQLDATABASE}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" > /app/config/application-railway.properties
  echo "spring.datasource.username=$MYSQLUSER" >> /app/config/application-railway.properties
  echo "spring.datasource.password=$MYSQLPASSWORD" >> /app/config/application-railway.properties
  export SPRING_CONFIG_ADDITIONAL_LOCATION="file:/app/config/"
fi
exec java ${JAVA_OPTS:--Dserver.address=0.0.0.0} -Dserver.port=${PORT:-8080} -jar /app/app.jar
