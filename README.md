# CLUSTERIZE

Clusterize is CLI interface that will help you submit and track jobs on your grid network using PBS.


[![Build Status](https://travis-ci.com/jakub-tucek/clusterize.svg?branch=master)](https://travis-ci.com/jakub-tucek/clusterize)

## Installation

The tool can be installed by execution of following command in the terminal:
```
$ sh -c "$(curl -fsSL https://raw.githubusercontent.com/jakub-tucek/metacentrum-cli/master/src/scripts/install.sh) v0.16"
```

For easy usage, you need to modify PATH variable in **.profile** so it contains executable **clusterize** command. Path will
be displayed during installation (along with instructions).

To reload configuration, execute
```
$ source ~/.profile
```
or logout/login.

To check that **clusterize** is installed run:
```
$ clusterize --version
```
This should return current version.

## Quick guide

For quick start and examples check [Quick guide page](docs/QUICK_GUIDE.md).

## User guide

For detailed user guide see
[User guide page](docs/USER_GUIDE.md).

## Configuration file

Detailed configuration documentation is located at [configuration guide page](docs/CONFIGURATION.md)


## Build

For successful build make sure you have installed JDK8+ and have java is available under PATH variable.

Building source code:
```
$ ./gradlew build installDist
```

Execute result by running wrapping bash script in **build/installl/clusterize/bin/clusterize**.

```
$ ./build/installl/clusterize/bin/clusterize --version
```


## Advanced configuration
    
### Env variables


| Name | description |
| ---- | ----------- |
| CLUSTERIZE_PROFILE | enables develop mode if set to **dev** |
| CLUSTERIZE_DISABLE_CLEANUP | disables cleanup if set to **true** |

