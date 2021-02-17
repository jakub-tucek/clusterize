# Configuration - deep dive

## Iterations
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

For different types and values of **iterations** [matlab example](../examples/clusterize-configuration-matlab.yml).

Example with more options:

```
# defines iterations in given order
iterations:
  - type: ARRAY # will iterate over values given in array
    name: VICINITY_TYPE
    values: [10, 20, 30]

  - type: INT_RANGE # will iterate over given integer range
    name: MIN_TRANSL
    from: 1
    to: 1
    step: 1
    stepOperation: PLUS

  - type: ARRAY
    name: ADDED_LAYERS
    values: ['[10, 20, 30]', '[20, 20]']

  - type: INT_RANGE
    name: SUB_IMG_IDX
    from: 1
    to: 1
    step: 1
    stepOperation: PLUS

  - type: INT_RANGE
    name: RUN_ID
    from: 2
    to: 1
    step: 1
    stepOperation: MINUS

```

## General

General section specifies common options for task.

```
general:
  #  metadataStoragePath: 'customer_metadata_storage_path' # OPTIONAL parameter
  storagePath: 'out-matlab' # Output path
  sourcesPath: 'sources' # Sources location
  maxResubmits: 3 # Maximum resubmit count if job fails
  variables:
    ENV_VAR: SOME_VALUE
  dependentVariables: # Variables depending on some iteration value
    - name: MAX_TRANSL
      dependentVarName: VICINITY_TYPE
      modifier: '+1' # bash expression

```

## Task type

Defines what type of task is executed. Supported values are MATLAB and PYTHON.
Can contain specific values for tasks.

Matlab example:
```
taskType:
  type: MATLAB # type of runner
  functionCall: |-
    main_batch01($MIN_TRANSL, $MAX_TRANSL, $VICINITY_TYPE, $SUB_IMG_IDX, 'useGPU', 'yes', 'layers', $ADDED_LAYERS)

```

Python example:
```
taskType:
  type: PYTHON # type of runner
  command: |-
    python -c "from python_fibonacci import main; main($FROM, $TO)"
```


Check the difference is that matlab is called as **function** while python is called as **bash** expression.
Thanks to this, it is currently teoretically use Python type for any type of execution if proper toolbox and modules
will be added to configuration.


## Resources

Defines resources and queue that will be selected based on wall-time. Most of the values do not have to be
set. CLI will ask for them before submission.


```
resources:
  profile: 'CUSTOM'
  resourceType: 'CPU'
  details: # Optional
    chunks: 1 # Optional
    walltime: "00:04:00" # Optional
    mem: "1gb" # Optional
    ncpus: 1 # Optional
    scratchLocal: "1gb" # Optional
    ngpus: "1" # Optional, Number, used for GPU computation
    cpuFlag: "avx512dq" # Optional, Specifies cpu_flag
  toolboxes: # Optional
    - matlab_Neural_Network_Toolbox
  modules: # Optional
    - jdk-8
```

This section then results in following rows in the script (if all values are set):

```
#PBS -l walltime=00:04:00
#PBS -l select=1:ncpus=1:mem=1gb:scratch_local=1gb:ngpus=2:cpu_flag=avx512dq
#PBS -q gpu - this row will be added only for GPU computation; will set gpu_log for walltime > 24
```
