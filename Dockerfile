FROM java:latest

RUN git clone https://github.com/gbecan/questions-pour-un-generateur.git
WORKDIR questions-pour-un-generateur
RUN ./activator stage

ENTRYPOINT ["./target/universal/stage/bin/questions"]