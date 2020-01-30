# Web : Prometheus Metrics - WAR

This sample describes how to expose TIBCO&reg; Streaming node metrics as a Prometheus consumable
REST endpoint.

## Initialize the servlet

See [PrometheusExporterServlet.java](../../main/java/com/tibco/ep/samples/prometheus/PrometheusExporterServlet.java)

The main intialisation method consists in registering the internal metrics in the Prometheus 
collector registry:

```java

import io.prometheus.client.exporter.MetricsServlet;

public class PrometheusExporterServlet extends MetricsServlet {

	private static final long serialVersionUID = 1L;

    public PrometheusExporterServlet() {
        initialize();
    }

    public PrometheusExporterServlet(CollectorRegistry registry) {
        super(registry);
        initialize();
    }

    private void initialize() {

        MetricRegistry metricRegistry = Metrics.getInstance().getMetricRegistry();
        CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry).register());
    }
}

```


## Building this sample from TIBCO StreamBase Studio&trade; 

Use the **Run As -> Maven install** menu option to build from TIBCO StreamBase Studio&trade;.

See this the following capture from the NAR EventFlow build:

![studio](../../../../../../nativelibrary/nar/nar-eventflow/src/site/resources/images/studiounit.gif)


## Building this sample from the command line

Use the [maven](https://maven.apache.org) as **mvn install** to build from the command line or Continuous Integration system.


---
Copyright (c) 2020, TIBCO Software Inc.

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
