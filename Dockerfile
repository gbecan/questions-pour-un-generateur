FROM java:latest

RUN apt-get update && apt-get -y install ffmpeg
RUN git clone https://github.com/gbecan/questions-pour-un-generateur.git \
&& cd questions-pour-un-generateur \
&& ./activator stage \
&& mv target/universal/stage/ /var/www/ \
&& cd .. \
&& rm -r questions-pour-un-generateur

WORKDIR /var/www/

ENTRYPOINT ["./bin/questions"]

CMD ["-Dconfig.file=/var/www/qpug/application.prod.conf"]