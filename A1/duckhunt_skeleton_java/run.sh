#!/bin/bash

mode=$1

environment=$2

make

if [ -z "$environment" ]
then
    if [ "$mode" -eq 1 ]
    then
        java Main verbose server < player2server > server2player
        gnome-terminal --noclose -e java Main verbose > player2server < server2player
    fi
    if [ "$mode" -eq 2 ]
    then
        java Main server < player2server | java Main verbose > player2server
    fi
else
    java Main server load "$environment" < player2server | java Main verbose > player2server
fi