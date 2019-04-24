# User guide

## Dictionary

- Task - represented by configuration file, consisted of jobs
- Job - One configuration of function that is submitted for computation


## Configuration - deep dive

In this section

Lets take first part of configuration file from [first example](QUICK_GUIDE.md#getting-started-fibbonnaci):

```
iterations:
  - type: INT_RANGE
    name: FROM
    from: 1
    to: 4
    step: 1
    stepOperation: PLUS
  - type: INT_RANGE
    name: TO
    from: 5
    to: 8
    step: 1
    stepOperation: PLUS
```

This part is defines generated configuration and its dimension. This code could be translated to for-cycles:
```
for (int FROM = 1; FROM<=4; FROM++) {
    for (int TO = 1; FROM<=4; FROM++) {
        configurations += (FROM, TO)
    }
}
```

For different types and values of **iterations** [matlab example](../examples/clusterize-configuration.yml).



## Storages

CLI uses two types of storage types: **storage** and **metadata** storage.
Each run creates new folder in these directories with name that corresponds
to current timestamp.

### Storage

Every run has it's folder which name corresponds to task id and its timestamp.
Content of task folder is:
- generated script
- pid of job
- stdout, stderr logs from qsub
- others outputs, such as stdout of executed matlab function

### Metadata Storage

Metadata storage contains files that are required for tracking status of job 
(time, result and others) and making it possible to rerun task with exactly same configuration.

Contains:
- metadata about run (configuration, output files location)
- mapping between RUN_ID <-> PID for run <-> configuration for RUN_ID

Default path is **~/.clusterize/metadataStorage**.




## Usage
CLI can be executed under **clusterize** command that should be available in $PATH variable.
Usage can be defined as:
```
clustize <command> [...additional parameters for command]
```

### Commands

 * **submit** [optional path to config file] 
    * base command for submitting new task to cluster according to configuration structure
    * if no additional parameter is specified, clusterize-configuration.yml must exist in current directory
    * ```
      $ clusterize submit
      ```
      ``` 
      $ clusterize submit my/path/to/config.yml  
 * **list** [parameters] 
     * checks all run tasks and display their status
     * Parameters:
        * -p [path to metadata folder] - *specification of metadata folder where info about tasks is saved*
        * -c [optional path to configuration file] - *path to configuration file with specified metadata folder*
            * *uses clusterize-configuration.yml is current directory if path not specified*
     * ```
       $ clusterize list -p my/path/metadatastorage"
       ```
       ``` 
       $ clusterize -c
       $ clusterize -c path/to/config.yml
 * **resubmit** [task id]
     * resubmits given task while keeping historical data
 * **cron** [start|stop|rest]
    * updates cron status; sends email notifications if enabled
 * **help**
    * displays help and usage examples   
