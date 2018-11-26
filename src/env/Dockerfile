# multi-stage build
# use pbsbase image as builder
# build script will be triggered
FROM pbspro/pbspro AS builder

ADD async-task-stub.sh /src/stub/
RUN ls -l /src/stub/
RUN mv /src/stub/stub.sh /usr/local/bin/matlab

# run entrypoint script
ENTRYPOINT ["bash", "/entrypoint.sh"]