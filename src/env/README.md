# Launch a PBS Pro container

Open up a terminal and build it:
```
docker build -t pbsmnt .
```

Run container and mount local files to share files in development 
(MUST BE DONE IN FOLDER WHERE dockershared is):
```
docker run -it -v $PWD/dockershared:$PWD/dockershared -v ~/.clusterize:$HOME/.clusterize --name pbsmnt -h pbs -e PBS_START_MOM=1 pbsmnt bash
```
This also tells docker to launch a PBS Pro container with an interactive shell and name it pbs. 
Docker will download the PBS Pro docker image from Docker Hub if the image is not already
on your system.

By default PBS Pro init script does not start the mom deamon. Therefore we use the -e option
to override the value for PBS_START_MOM environment variable.
You can use -e to passing additional environment variables if you need to. 

You should now see a terminal window that looks like:
```
pbsuser@pbs ~
```
Note that you are logged into a default non-root user account. Before we can submit and run jobs, we need to add some configurations using root account. Exit the current shell and you should return to a root shell. Run: 
```
qmgr -c "create node pbs"
qmgr -c "set  node pbs queue=workq"
qmgr -c "create resource scratch_local type=float,flag=h"
echo "add resource scratch_local" >> /var/spool/pbs/sched_priv/sched_config
```
to create a node named pbs and add a queue to it. Then switch back to the default user account and move to its home directory:

```
su pbsuser
cd
```
Submit a job
You should now be able to submit and view jobs.
```
qsub -- /bin/sleep 10
qstat
```

To keep container running in background:
```
docker container start pbsmnt
```


To go into existing container:
```
docker exec -it --user pbsuser pbsmnt bash -l 
```
