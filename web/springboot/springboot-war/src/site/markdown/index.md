# Web: Springboot WAR

This sample describes how to use Spring Boot to build a WAR Archive (.war). 
The WAR contains a GET endpoint **"springboot-war/test"**, which returns "Hello, TIBCO!" when 
gets called. The resulting archive can be used in a downstream EventFlow fragment.

* [Create a GET endpoint](#create-a-get-endpoint)
* [Generate WAR archive](#generate-war-archive)
* [Building this WAR from the command line](#building-this-sample-from-the-command-line)

## Create a GET endpoint

Add the following maven dependencies into the pom.xml. 
Since runtime node web server is an embedded Jetty web server, the
 **org.springframework.boot:spring-boot-starter-tomcat** must be 
excluded from **org.springframework.boot:spring-boot-starter-web**, and 
**org.springframework.boot:spring-boot-starter-jetty** has to be added with the **provided** scope.

```xml

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>2.2.1.RELEASE</version>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jetty</artifactId>
            <version>2.2.1.RELEASE</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```

The [sample java code folder](../../../main/java/com/tibco/ep/samples/web/springboot/springbootwar)([html](https://github.com/TIBCOSoftware/tibco-streaming-samples/tree/master/web/websocket/websocket-war/src/main/java/com/tibco/ep/samples/web/springboot/springbootwar)) 
provides a GET endpoint to response "Hello, TIBCO!" when gets called.


## Generate WAR archive

The project's packaging type is **war**.

```xml
    <groupId>com.tibco.ep.samples.web</groupId>
    <artifactId>springboot-war</artifactId>
    <packaging>war</packaging>
    <version>1.0.0</version>

```

The [maven war plugin](https://maven.apache.org/plugins/maven-war-plugin/) 
is used to build the WAR archive (.war).  The following maven build rule is used:

```xml
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <version>3.2.3</version>
            <configuration>
                <failOnMissingWebXml>false</failOnMissingWebXml>
            </configuration>
        </plugin>
    </plugins>
```

## Building this sample from the command line

Use [maven](https://maven.apache.org) to build from the command line or Continuous Integration System by running **mvn install**:"

![maven](images/maven.gif)

---
Copyright (c) 2020-2023 Cloud Software Group, Inc.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the copyright holder nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
