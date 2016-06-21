FROM maven:3.3.9-jdk-9-onbuild
EXPOSE 7777
CMD ["mvn", "clean",  "compile",  "exec:java"]
