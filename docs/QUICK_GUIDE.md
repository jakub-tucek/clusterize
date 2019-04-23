# Quick guide

First install **clusterize** on your front-end node where you submit jobs. Installation
can be found [here](../README.md#installation).

## Prepare files

1. Clone repository on the grid front-end node
    1. ```$ git clone https://github.com/jakub-tucek/clusterize```
2. Go to **examples** folder

## Getting started: Fibbonnaci

Example folder should contain two files **clusterize-configuration-python.yml**
and **sources/python_fibonacci.py**.

The first one, is *clusterize configuration file* that contains what parameters
will be calculated in task execution among other configuration options.

Second is **source** file. Source file can be function, script or just your dataset.

Configuration will calculate Fibbonnaci numbers. Output is saved to standalone files
for each job calculating number.

Content of outputs will look like this:

1, 1, 2, 3, 5, 1, 2, 3, 5, 2, 3, 5, 3, 5, 5

### Submission

For submission use:

```
$ clusterize submit clusterize-configuration.yml
```

The configuration file is missing some values, like task name or resource configuration.
They have to be inputted interactively before execution but it is possible to just
use default values and press **enter** on each CLI prompt.


## Enable notifications

To enable mail notifications execute this command:

```
$ clusterize cron start
```

and follow instructions.


To stop cron run ```$ clusterize cron stop``` or just remove it from cron tab.
