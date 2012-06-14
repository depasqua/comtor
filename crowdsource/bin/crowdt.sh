#!/bin/bash

CROWDT_HOME=..
#CLIENT=org.comtor.cloud.crowd.FileClient
CLIENT=org.comtor.cloud.crowd.JDocClient
CONFIG_FILE="$CROWDT_HOME"/conf/crowd.properties

java -cp "$CROWDT_HOME"/classes/ "$CLIENT" "$CONFIG_FILE"


