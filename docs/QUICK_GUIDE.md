# Quick guide

First install **clusterize** on your front-end node where you submit jobs. Installation
guide can be found [here](../README.md#installation).

## Prepare files

1. Clone repository on the grid front-end node
    1. ```$ git clone https://github.com/jakub-tucek/clusterize```
2. Go to **examples** folder

## Getting started: Fibbonnaci

First example depends on two files: **clusterize-configuration-python.yml**
and **sources/python_fibonacci.py**.

The first one, is *clusterize configuration file* that contains configuration of the execution.
These are:

    * Specification how to generate configuration paraemeters
    * task type (python)
    * directories for metadata, output storage and sources
    * command to execute (what python script to run)
    * resubmit limit if execution fails
    
--------
    
#### Important note: Current directory when job is executed is *sources* directory.

This means that command execution configuration must be relative to **sources** directory.

--------
   

Python script calculates Fibbonacci number for given range and prints them as standard
output. Range is then accepted as two parameters of function, which are changed
based on the configuration generating these parameters.

 
### Submission

To submit task use:

```
$ clusterize submit clusterize-configuration.yml
```

The configuration file is missing some values, like task name or resource configuration.
They have to be inputted interactively before execution but it is possible to just
use default values and press **enter** on each CLI prompt.

### Status

If email notifications were enabled, the email is send when all jobs finish.
Second option is to check status manually using:

```
$ clusterize list
```

### Output

Output is saved in *std-out* file in proper **general.storagePath**. 

**Storage folder** is set to be created in examples folder under **out-python** name.

## Resubmit with email notifications

Clusterize offers easy way to resubmit executed task and to wait for email notification
when all jobs finish.


## Enable notifications
First, enable mail notifications and execute this command:

```
$ clusterize cron start
```

and follow instructions.

To stop cron run ```$ clusterize cron stop``` or just remove it from cron tab.

## Resubmit

First we need to find task identification using status command:
```
$ clusterize list
```

The identification is number on the right side of executed task.

Resubmit is then just:
```
$ clusterize resubmit [task_id]
``` 

Then you can again check status of resubmitted task or wait for mail notification.
Email notification can then take >15minutes to be delivered after task ends.

## Matlab

Matlab example can be executed by submitting different configuration file:
**clusterize-configuration-matlab.yml**.

Matlab script (main_batch01.m) itself only prints given variables but for that, much more variables are used.

## R

It is also possible to execute R script. Example is in:

**clusterize-configuration-r.yml**.

This configuration file also shows usage of other available variables, like **OUT_DIR** that specifies output directory where
script should persist data.

The executed script, **sources/er-script.r** accepts three parameters **input csv** **random value** and **output file**.
After loading input csv, it filters out non-numerical values and prints the result to given output file.

