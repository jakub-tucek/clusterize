# User guide


## Details

## Dictionary

- Task - represented by configuration file, consisted of jobs
- Job - One configuration of function that is submitted for computation

### Storages

CLI uses two types of storage types: **storage** and **metadata** storage.
Each run creates new folder in these directories with name that corresponds
to current timestamp.

#### Storage

Every run has it's folder which name corresponds to task id and its timestamp.
Content of task folder is:
- generated script
- pid of job
- stdout, stderr logs from qsub
- others outputs, such as stdout of executed matlab function


#### Metadata Storage

Metadata storage contains files that are required for tracking status of job 
(time, result and others) and making it possible to rerun task with exactly same configuration.

Contains:

- **sources** folder contains files used to run task (such as matlab files with used function)
- metadata about run (configuration, output files location)
- mapping between RUN_ID <-> PID for run <-> configuration for RUN_ID


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
        * -c [optional path to configuration file] - *path to configuration file with specified metadat folder*
            * *uses clusterize-configuration.yml is current directory if path not specified*
     * ```
       $ clusterize list -p my/path/metadatastorage"
       ```
       ``` 
       $ clusterize -c
       $ clusterize -c path/to/config.yml
 * **cron** [start|stop|rest]
    * updates cron status; sends email notifications if enabled
 * **help**
    * displays help and usage examples   
