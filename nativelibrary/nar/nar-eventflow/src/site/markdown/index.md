# Native : NAR - EventFlow calling C++

This sample describes how to use a Native ARchive (.nar) dependency in an EventFlow project.

* [Declare the NAR as a dependency](#declare-the-nar-as-a-dependency)
* [Declare a java wrapper class](#declare-a-java-wrapper-class)
* [Provide a engine configuration for EventFlow use](#provide-a-engine-configuration-for-eventflow-use)
* [Using in EventFlow](#using-in-eventflow)
* [Building this sample from TIBCO StreamBase® Studio and running the unit test cases](#building-this-sample-from-tibco-streambase-reg-studio-and-running-the-unit-test-cases)
* [Building this sample from the command line and running the unit test cases](#building-this-sample-from-the-command-line-and-running-the-unit-test-cases)

## Declare the NAR as a dependency

A NAR can be used in the same way as any other maven dependency, except that a platform-specific
classifier should be specified :

```xml
                <dependency>
                    <groupId>com.tibco.ep.samples.nativelibrary</groupId>
                    <artifactId>nar-cpplib</artifactId>
                    <version>1.0.0</version>
                    <type>nar</type>
                    <classifier>amd64-Linux-gpp-jni</classifier>
                </dependency>
```

In many situations, cross-platform development is required ( for example develop on desktop OS/X but 
test on Linux servers ) - this can be achieved with maven profiles :

```xml
    <!-- Import platform specific NAR dependency -->
    <profiles>
        <profile>
            <id>linux profile</id>
            <activation>
                <os><name>linux</name></os>
            </activation>
            <dependencies>
                <dependency>
                    <groupId>com.tibco.ep.samples.nativelibrary</groupId>
                    <artifactId>nar-cpplib</artifactId>
                    <version>1.0.0</version>
                    <type>nar</type>
                    <classifier>amd64-Linux-gpp-jni</classifier>
                </dependency>
            </dependencies>
        </profile>
        <profile>
            <id>mac profile</id>
            <activation>
                <os><name>mac os x</name></os>
            </activation>
            <dependencies>
                <dependency>
                    <groupId>com.tibco.ep.samples.nativelibrary</groupId>
                    <artifactId>nar-cpplib</artifactId>
                    <version>1.0.0</version>
                    <type>nar</type>
                    <classifier>x86_64-MacOSX-gpp-jni</classifier>
                </dependency>
            </dependencies>
        </profile>
        <profile>
            <id>windows profile</id>
            <activation>
                <property>
                    <name>path.separator</name>
                    <value>;</value>
                </property>
            </activation>
            <dependencies>
                <dependency>
                    <groupId>com.tibco.ep.samples.nativelibrary</groupId>
                    <artifactId>nar-cpplib</artifactId>
                    <version>1.0.0</version>
                    <type>nar</type>
                    <classifier>amd64-Windows-msvc-jni</classifier>
                </dependency>
            </dependencies>
        </profile>
    </profiles>
```

## Declare a java wrapper class

A java wrapper class is required to load the native shared library and provide JNI access :

```java
package com.tibco.ep.samples.nativelibrary.narcpplib;

/**
 * c++ utility functions
 */
public class CallCpp {

    static {
        //
        // Don't need the native part when we're typechecking in studio
        //
        if (System.getProperty("streambase.ide.is-running") == null) {
            System.loadLibrary("nar-cpplib-1.0.0");
            System.out.println("loaded shared lib");
        }
    }


    /**
     * Set the static int in C++ land to some value
     * @param val Value
     * 
     * @return the old value
     */
    public native static int setCppInt(int val);

    /**
     * Increment the int in C++ by the arg.
     * @param val Value
     * 
     * @return the new value
     */
    public native static int incrementCppInt(int val);

}
```

## Provide a engine configuration for EventFlow use

A engine configuration is required to declare the functions available :

```scala
name = "sbapp-calls-cpp-engine"
version = "1.0.0"
type = "com.tibco.ep.streambase.configuration.sbengine"
configuration =
{
    StreamBaseEngine =
    {
        streamBase =
        {
            pluginFunctions =
            {
                java =
                {
                    setCppInt =
                    {
                        type = "simple"
                        alias = "setCppInt"
                        className = "com.tibco.ep.samples.nativelibrary.narcpplib.CallCpp"
                        autoArguments = true
                    }

                    incrementCppInt =
                    {
                        type = "simple"
                        alias = "incrementCppInt"
                        className = "com.tibco.ep.samples.nativelibrary.narcpplib.CallCpp"
                        autoArguments = true
                    }
                }
            }
        }
    }
}
```

## Using in EventFlow

These functions can now be used in EventFlow :

![studio](images/studioFunction.png)

## Building this sample from TIBCO StreamBase&reg; Studio and running the unit test cases

Use the **Run As -> Maven install** menu option to build from TIBCO StreamBase&reg; Studio :

![studio](images/studiounit.gif)

## Building this sample from the command line and running the unit test cases

Use the [maven](https://maven.apache.org) as **mvn install** to build from the command line or Continuous Integration system :

![maven](images/maven.gif)

---
Copyright (c) 2018-2023 Cloud Software Group, Inc.

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
