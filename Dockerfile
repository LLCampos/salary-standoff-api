FROM hseeberger/scala-sbt:11.0.10_1.4.7_2.13.4

ARG projectPath=/opt/salary-standoff-api

RUN mkdir $projectPath
WORKDIR $projectPath

COPY . .

EXPOSE 8080

ENTRYPOINT sbt run
