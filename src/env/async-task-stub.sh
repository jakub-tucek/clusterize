#!/bin/bash
sleep `grep -m1 -ao '[0-9][0-9][0-9]' /dev/urandom | sed s/0/10/ | head -n1`
echo $@
RETURN_ID=`grep -m1 -ao '[0-2]' /dev/urandom | head -n1`
echo $RETURN_ID
exit $RETURN_ID