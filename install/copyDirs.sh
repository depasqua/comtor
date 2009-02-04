#!/bin/bash

# Make www directory first
if [ ! -d $1 ]; then
  mkdir $1
  if [ $? != 0 ]; then
    exit 1
  fi
fi

cp -r ../web/* $1
if [ $? != 0 ]; then
  exit 1
fi

if [ ! -d $2 ]; then
  mkdir $2
  if [ $? != 0 ]; then
    exit 2
  fi
fi

if [ ! -d $3 ]; then
  mkdir $3
  if [ $? != 0 ]; then
    exit 3
  fi
fi

if [ ! -d $4 ]; then
  mkdir $4
  if [ $? != 0 ]; then
    exit 4
  fi
fi

if [ ! -d $5 ]; then
  mkdir $5
  if [ $? != 0 ]; then
    exit 5
  fi
fi

exit 0
