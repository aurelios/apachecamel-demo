# Apache Camel Demo

This project is a demo of how we can use apache camel to implement integrations with another sistems using files, http, soap, or queue trough routes using integration patterns with Camel DSL.

## Getting Started

Download and configure Tomcat, Maven and ActiveMQ and start up webservices project, then run the integration examples on apachecamel-demo project.

### Prerequisites

* Apache tomcat 8.x
* Apache Maven 3.x
* Apache ActiveMQ 5.x

##webservices
webservices is a simple representation of a sistem that implements soap or rest webservice.
```
1 - Configure your tomcat 8.x folder on webservices/pom.xml
2 - Run mvn cargo:run to deploy and start our webservice on tomcat
```

##Apache Active MQ

```
1 - Run activemq.bat start or sh activemq start (user: admin/password: admin)
2 - Create a new Queue "pedidos"
3 - Create a new Queue "pedidos.DLQ" to receive messages of Dead Letter Queue
```

##apachecamel-demo

Run the integration examples 

OrderFileRoute
```
Example of a route using Message, Event Message, File Sharing integrations to process data from files trough an endpoint(orders) creating a Message Exchange and sending to another endpoint(processed).

```
OrderFileToHttpSoapRoute
```
Example of a route using File Sharing, Splitter, Message Filter, Content Based Router integrations to process data from files trough an endpoint(orders) sending them to a webservice using http and soap.
```

OrderMQToHttpSoapRoute
```
Example of a route using Messaging integration to process data from files and send them to a webservice using http and soap.
```
