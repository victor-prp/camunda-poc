# Orchestrate Microservices By A Spring Boot Worker

This project contains a worker that can connect a BPMN service task to whatever you need to.

Requirements:

* Java >= 8
* Maven

How to run:

* Download/clone the code in this folder.
* You need to set your Camunda cloud client connection details in the file `application.properties`. Simply replace the existing sample values.
* Run the application:


Now you need to  deploy BPMN files (under resources/processes) to camunda modeler.

## BatchItems
Demonstrates batch job.

![batch flow](batch.png)

'create-items' task receives 'count' variable and creates items collection as an output.
'item-processor' task receives a single item and prints it.
once all items are processed 'notify-done' tasks prints the number of total items count.

How to run:
- Deploy the [batch.bpmn](src/main/resources/processes/batch.bpmn) to camunda cloud modeler
- Start Instance with json variable: {"count":100}
- Set proper zeebe.client.cloud.clientSecret in [application.properties](src/main/resources/application.properties)
- Run [BatchItems](src/main/java/victor/prp/cammunda/poc/batch/BatchItems.java) using your IDE or maven

