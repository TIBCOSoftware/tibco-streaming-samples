# Structure : Application

This sample describes how to structure the application archive module of a maven aggregator project.

A [maven aggregator project](http://maven.apache.org/pom.html#Aggregation) consists of at least one application archive module and at least one fragment module.  
With multiple maven modules in one project, maven can work out dependencies across maven modules and build and test in the right order.

The directory structure consists of :

* **pom.xml** - [Maven aggregator project object model](http://maven.apache.org/pom.html#Aggregation) that references all the contained modules
* **[application]/pom.xml** - Maven project object module that controls the build, tests, installation and deployment of this application archive.  Must list the fragment dependencies.
* **[application]/src/main/configurations/** - Directory containing any configuration files.  These are included in the built application archive.
* **[application]/src/main/docker/base** - Directory containing docker file to build base image.
* **[application]/src/main/docker/application** - Directory containing docker file to build application image.
* **[application]/src/main/resources/** - Directory containing any resources files.  These are included in the built application archive.
* **[application]/src/site/** - Directory containing any site documentation.  The maven build process can create html from these and publish to a web site.
* **[application]/src/test/java/[package]/** - Directory containing any junit test cases
* **[application]/src/test/configurations/** - Directory containing any configuration files used by test cases.  These are *not* included in the built fragment.
* **[application]/src/test/resources/** - Directory containing any resources files used by test cases.  These are *not* included in the built fragment.

This sample's structure is shown below :

```
├── pom.xml
├── one or more fragment modules
├── application-app
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── configurations
│       │   │   └── app.conf
│       │   │   └── license.conf
│       │   └── docker
│       │       ├── application
│       │       │   └── Dockerfile
│       │       └── base
│       │           ├── Dockerfile
│       │           └── start-node
│       ├── site
│       │   ├── markdown
│       │   │   ├── index.md
│       │   │   └── using.md
│       │   └── site.xml
│       └── test
│           ├── configurations
│           ├── java
│           │   └── com
│           │       └── tibco
│           │           └── ep
│           │               └── samples
│           │                   └── application
│           └── resources
```

## License Activation Configuration

A valid TIBCO Activation Service configuration is required before running any Streaming applications.
This sample contains a license configuration file in `src/main/configurations/license.conf`, which requires
a Maven property `activation.service.urls` be defined with the quoted URL(s) of one or more TIBCO Activation
Service instances. This property may be defined in the properties section of the `pom.xml` file:

    <properties>
        <!-- single activation server -->
        <activation.service.urls>"https://example.com:7070"</activation.service.urls>

        <!-- multiple activation servers -->
        <activation.service.urls>"https://example1.com:7070","https://example2.com:7070"</activation.service.urls>
    </properties>

It may also be defined on the maven command line:

    mvn install -Dactivation.service.urls=\"https://example.com:7070\"
    mvn install -Dactivation.service.urls=\"https://example1.com:7070\",\"https://example2.com:7070\"

Note that the quotes must be escaped on the command line.

It is also possible to define a local license file under the user's home directory, containing the Activation
Service URL(s). However, because a license configuration file in a Streaming application archive takes
precedence over a local license file, the `src/main/configurations/license.conf` file must be deleted from
the project in order for the local license file to be used. Note that the use of local license files is
not recommended for production deployments, because it requires that each machine running Streaming
Application(s) have a local license configuration for the user account under which the node runs.

Refer to the **Welcome Guide > Activation in TIBCO Streaming and Model Management Server** page in the
TIBCO Streaming documentation for more details on license activation.

---
Copyright (c) 2018-2025 Cloud Software Group, Inc.

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
