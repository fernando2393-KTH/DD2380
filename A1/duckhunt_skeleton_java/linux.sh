#!/bin/bash
rm player2server
rm server2player

mode=$1

environment=$2

make

echo "make finished!"

mkfifo player2server server2player

echo "mkfifo finished!"

if [ -z "$environment" ]
then
    if [ "$mode" -eq 1 ]
    then
        java Main verbose server < player2server > server2player
        # osascript -e 'tell app "Terminal" to do script "cd \"`pwd`\"; java Main verbose > player2server < server2player"'
        gnome-terminal -e java Main verbose > player2server < server2player
    fi
    if [ "$mode" -eq 2 ]
    then
        java Main server < player2server | java Main verbose > player2server
    fi
else
    java Main server load "$environment" < player2server | java Main verbose > player2server
fi

make clean