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

## License Configuration

A valid TIBCO Streaming license is required before running any Streaming applications. This sample does not
contain a license configuration file, so a node uses the default local license directory in the home
directory of the user account under which the node runs:

* Windows: `%USERPROFILE%/.tibco-streaming/licenses`
* macOS or Linux: `$HOME/.tibco-streaming/licenses`

Copy one or more `.bin` license files into that directory before running this sample.

License files can instead be bundled into the application archive by placing them in a `licenses` directory
under `src/main/resources`. An activation configuration can also be specified in the application project, a
fragment project or a node deploy configuration file, and takes precedence over the other mechanisms.

Refer to the **Welcome Guide > Activation in TIBCO Streaming and Model Management Server** page in the
TIBCO Streaming documentation for more details on license configuration.

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
