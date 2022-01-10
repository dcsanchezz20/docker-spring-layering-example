# Docker Spring Layering Example

The project has 2 files `Dockerfile` and `DockerfileClasic` examples.

## DockerfileClassic
```dockerfile
FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

This Dockerfile example copies the full application jar and creates an image with the application.

- All information is condensed in one docker layer.
- As Docker file you can configure it.


## Dockerfile
```dockerfile
FROM openjdk:11-jdk-slim as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:11-jdk-slim
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
```

To be able to use the spring boot layered mode you need to add these configuration lines (layers tags).

```xml
    <build>
      <plugins>
        ...
        <plugin>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-maven-plugin</artifactId>
          <configuration>
            <layers>
              <enabled>true</enabled>
              <includeLayerTools>true</includeLayerTools>
            </layers>
          </configuration>
        </plugin>
        ...
      </plugins>
    </build>
```

This dockerfile example uses the spring boot layered mode. These layers can be used ad independent layer in other dockerfiles if the application has the same java dependencies.

```shell
# You should see the following output which tells us the layers and the order that they should be added: 
$ java -Djarmode=layertools -jar target/docker-spring-layering-example-1.0.jar list

dependencies
spring-boot-loader
snapshot-dependencies
application
```

---
## Docker Commands

```shell
$ docker build . --tag demo # Creates docker image with Dockerfile file
$ docker build -f DockerfileClassic . --tag democlassic # Creates docker image with DockerfileClassic file

$ docker run -it -p8080:8080 demo:latest
```
---
## Fabric8 Docker Plugin
This is a Maven plugin for managing Docker images and containers. It focuses on two major aspects for a Docker build integration, more info at [Fabric8io - Docker Maven Plugin](https://dmp.fabric8.io/)
- You can define the Dockerfile full path at `dockerFile` tag, if you don't do that, this plugin is going to search the file at `src/main/docker`path.  

```xml
<build>
  <plugins>
    ...
    <plugin>
      <groupId>io.fabric8</groupId>
      <artifactId>docker-maven-plugin</artifactId>
      <version>0.38.1</version>
      <configuration>
        <verbose>true</verbose>
        <images>
          <image>
            <name>${docker.image.prefix}/${docker.image.name}</name>
            <alias>${project.artifactId}</alias>
            <build>
              <!--<dockerFile>${project.basedir}/DockerfileClassic</dockerFile>-->
              <dockerFile>DockerfileClassic</dockerFile>
              <tags>
                <tag>latest</tag>
                <tag>${project.version}</tag>
              </tags>
            </build>
          </image>
        </images>
      </configuration>
    </plugin>
    ...
  </plugins>
</build>
```

---
## Documentation

- [Spring Boot with Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [Creating Docker images with Spring Boot 2.3.0.M1](https://spring.io/blog/2020/01/27/creating-docker-images-with-spring-boot-2-3-0-m1)
- [Why You Should be Using Spring Boot Docker Layers](https://springframework.guru/why-you-should-be-using-spring-boot-docker-layers/)
- [Reusing Docker Layers with Spring Boot](https://www.baeldung.com/docker-layers-spring-boot)