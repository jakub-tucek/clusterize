# multi-stage build
# use pbsbase image as builder
# build script will be triggered
FROM pbspro/pbspro AS builder

ADD async-task-stub.sh /src/stub/
RUN mv /src/stub/async-task-stub.sh /usr/bin/matlab
# run entrypoint script
ENTRYPOINT ["bash", "/entrypoint.sh"]