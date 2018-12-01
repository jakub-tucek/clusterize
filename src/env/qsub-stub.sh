#!/bin/bash

NEW_UUID=$(cat /dev/urandom | tr -dc '0-9' | fold -w 32 | head -n 1)
echo $NEW_UUID.PID
