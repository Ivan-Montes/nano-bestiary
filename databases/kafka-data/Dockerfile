FROM frolvlad/alpine-glibc:latest

RUN apk update && apk add --no-cache bash curl openjdk8-jre kcat

ENV KAFKA_VERSION=3.8.0
ENV SCALA_VERSION=2.13

WORKDIR /opt/kafka

RUN curl -sL "https://downloads.apache.org/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz" | tar -xzf - -C /opt/kafka --strip-components=1

ENV PATH=${PATH}:/opt/kafka/bin

CMD ["bash"]