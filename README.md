# CLUSTERIZE

Clusterize is CLI interface that will help you submit and track jobs on your grid network using PBS.


[![Build Status](https://travis-ci.com/jakub-tucek/metacentrum-cli.svg?token=NqFVge8N1yh3apxFedae&branch=master)](https://travis-ci.com/jakub-tucek/metacentrum-cli)

## Installation

Basic installation is installed by running one of the following commands in your terminal. 
You can install this via the command-line with either curl.

(update last part to latest version)
```
$ sh -c "$(curl -fsSL https://raw.githubusercontent.com/jakub-tucek/metacentrum-cli/master/src/scripts/install.sh) v0.12"
```

For easy use, you need to add path **clusterize** to clusterize to **.bashrc**. Path will
be displayed during installation (along with instructions).
To reload configuration, execute
```
$ source ~/.bashrc
```
or logout/login.

To check that **clusterize** is installed run:
```
$ clusterize --version
```
This should return current version that is installed.

## Quick guide

For quick start and examples check [Quick guide page](docs/QUICK_GUIDE.md).

## User guide

For detailed user guide how to use all features that CLI offers check
[User guide page](docs/USER_GUIDE.md).

    
### Env variables


| Name | description |
| ---- | ----------- |
| CLUSTERIZE_PROFILE | enables develop mode if set to **dev** |
| CLUSTERIZE_DISABLE_CLEANUP | disables cleanup if set to **true** |


## Configuration file

