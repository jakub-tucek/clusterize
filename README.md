
# Metacentrum CLI

[![Build Status](https://travis-ci.com/jakub-tucek/metacentrum-cli.svg?token=NqFVge8N1yh3apxFedae&branch=master)](https://travis-ci.com/jakub-tucek/metacentrum-cli)


### Dictionary

- Task - high level task that consists of runs (also called as jobs)
- Run/Job - Smallest piece action. Each run/job is submitted to task planer. Task is 
considered as completed when all runs/jobs are finished.  

### Storages

CLI uses two types of storage types: storage and metadata storage.
Each run creates new folder in these directories with name that corresponds
to current timestamp.

#### Storage

Every run has it's folder which name corresponds to run id (index).
Content of those folders is:
- generated script
- pid of job
- stdout, stderr from qsub
- others outputs such is final result of task


#### Metadata Storage

Metadata storage contains files that are required for tracking status of job 
(time, result and others) and making it possible to rerun task with exactly same configuration.

Contains:

- sources used to run task (such as matlab files)
- metadata about run (configuration, output files location)
- mapping between RUN_ID <-> PID for run <-> configuration for RUN_ID


