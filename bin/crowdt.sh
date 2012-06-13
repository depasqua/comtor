#!/bin/bash

CROWDT_HOME=../code
CLIENT=org.comtor.cloud.crowd.FileClient
CONFIG_FILE=src/org/comtor/cloud/crowd/crowd.properties

java -cp "$CROWDT_HOME"/classes/ "$CLIENT" "$CROWDT_HOME/$CONFIG_FILE"


