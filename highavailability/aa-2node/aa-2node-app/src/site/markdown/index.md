# HA : 2-node active active

This sample describes how to deploy an EventFlow fragment in a 2-node active active configuration.

* [License Activation Configuration](#license-activation-configuration)
* [Machines and nodes](#machines-and-nodes)
* [Data partitioning](#data-partitioning)
* [Define the node deployment configuration](#define-the-node-deployment-configuration)
* [Design notes](#design-notes)
* [Failure scenarios](#failure-scenarios)
* [Building this sample from the command line and running the integration test cases](#building-this-sample-from-the-command-line-and-running-the-integration-test-cases)

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

## Machines and nodes

In this sample we name the machines as **A**,  which hosts the Streaming node **A**, 
and **B**, which hosts the Streaming node **B**.

![nodes](images/two-node-active-active-nodes.svg)

A client that uses the service can connect to either machine **A** or **B**.

( service names are omitted in descriptions for clarity )

## Data partitioning

To support an active active configuration, the query table data must be replicated between the nodes.  
In this sample the default **default-cluster-wide-availability-zone** is used - a number of virtual
partitions are created to evenly balance and replicate data around the cluster :

![partitions](images/two-node-active-active-partitions.svg)

( only 2 virtual partitions are shown - the default is 64 )

## Define the node deployment configuration

Since this sample uses the default availability zone, it doesn't need to be specified again, so
no node deployment configuration is required/

## Design notes

* The default dynamic data distribution policy is chosen to distribute the data across the cluster
* Most of the data distribution policy and the availability zone configuration values are not set since defaults work well

## Failure scenarios

The main failure cases for this deployment are outlined below :

Failure case   | Behavior on failure | Steps to resolve | Notes
--- | --- | --- | ---
Machine A fails | 1 Client is disconnected<br/>2 Virtual partitions become active on B<br/>3 Client may connect to B and continue  | 1 Fix machine A<br/>2 Use **epadmin install node** and **epadmin start node** | 1 No data loss<br/>2 No service loss
Machine B fails | 1 Client is disconnected<br/>2 Virtual partitions become active on A<br/>3 Client may connect to A and continue  | 1 Fix machine B<br/>2 Use **epadmin install node** and **epadmin start node** | 1 No data loss<br/>2 No service loss
Network fails  | 1 irtual partitions become active on both A and B<br/>**multi-master** scenario | 1 Fix network<br/>2 Use **epadmin restore availabilityzone** | 1 **Possible data loss**<br/>2 No service loss

With a 2 node configuration node quorums don't apply hence a multi-master scenario is possible on network failure.  
To avoid the risk of data loss when restoring the availability zone, multiple network paths ( such as network bonding )
is recommended.

## Building this sample from the command line and running the integration test cases

In this sample, some HA integration test cases are defined in the pom.xml that :

* start nodes A & B
* use **epadmin start playback** to inject tuples to node A
* use **epadmin read querytable** on node A to verify query table contents
* stop node A
* use **epadmin read querytable** on node B to verify no data loss
* stop node B

**Warning:** This does not constitute an exhaustive non-functional test plan

Use the [maven](https://maven.apache.org) as **mvn install** to build from the command line or Continuous Integration system :

![maven](images/maven.gif)

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
