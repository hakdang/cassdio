#!/bin/sh

echo "Cassdio Build!! Start!!!!"

./gradlew :cassdio-web:clean :cassdio-web:build -Pfrontend=true

echo "Cassdio Build!! Complete!!!"
