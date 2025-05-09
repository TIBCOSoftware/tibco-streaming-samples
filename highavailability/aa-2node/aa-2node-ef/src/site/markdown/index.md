# HA : 2-node active active

This sample describes how to build an EventFlow fragment suitable for 2-node active active deployment.

* [Store tuples in a query table backed by transactional memory](#store-tuples-in-a-query-table-backed-by-transactional-memory)
* [Design notes](#design-notes)
* [Running this sample from TIBCO StreamBase® Studio](#running-this-sample-from-tibco-streambase-reg-studio)
* [Building this sample from TIBCO StreamBase® Studio and running the unit test cases](#building-this-sample-from-tibco-streambase-reg-studio-and-running-the-unit-test-cases)
* [Building this sample from the command line and running the unit test cases](#building-this-sample-from-the-command-line-and-running-the-unit-test-cases)

## Store tuples in a query table backed by transactional memory

In this sample, a query table is used to update, read and delete tuples, so :

* Query table is key'ed by **name** field
* A tuple injected on the **UpdateStream** stream containing **name** & **value** will be routed to target node and insert or update the **name** entry in the table with the new **value**
* A tuple injected on the **ReadStream** stream containing **name** will be routed to target node, read the current value from the table and routed back to the source node
* A tuple injected on the **DeleteStream** stream containing **name** will be routed to target node and delete the name from the table

The query table is configured with transactional memory :

![Table settings](images/studiotablesettings.png)

The default data distribution policy **default-dynamic-data-distribution-policy** is used :

![Data distribution](images/studiodatadistribution.png)

Finally, the tuples stored in the query table are partitioned by the **name** field :

![Schema](images/studioschema.png)

## Design notes

* Tuples are routed to the target node
* In the case of read, the return tuple is routed back to the source node - the source node is recorded in sourcenode field to support this
* Query scope is local ( ie not cluster wide )

## Running this sample from TIBCO StreamBase&reg; Studio

Use the **Run As -> EventFlow Fragment** menu option to run in TIBCO StreamBase&reg; Studio, and then enqueue tuples :

Note that here we are unit testing the business logic rather than high availability - in this sample we test high availability in
the application archive integration test cases.  The unit test cases can test high availability by loading an activating test versions 
of the application definition and node deployment configuration files.

![RunFromStudio](images/studio.gif)

## Building this sample from TIBCO StreamBase&reg; Studio and running the unit test cases

Use the **Run As -> EventFlow Fragment Unit Test** menu option to build from TIBCO StreamBase&reg; Studio :

![RunFromStudio](images/studiounit.gif)

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
