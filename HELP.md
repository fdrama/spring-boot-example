# Read Me First

The following was discovered as part of building this project:

* The JVM level was changed from '1.8' to '17', review
  the [JDK Version Range](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Versions#jdk-version-range)
  on the wiki for more details.

# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.5/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.5/reference/htmlsingle/#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

Throughout this tutorial, you have engaged in various tactics to build REST APIs. As it turns out, REST isn’t just about pretty URIs and returning JSON instead of XML.

Instead, the following tactics help make your services less likely to break existing clients you may or may not control:

Don’t remove old fields. Instead, support them.

Use rel-based links so clients don’t have to hard code URIs.

Retain old links as long as possible. Even if you have to change the URI, keep the rels so older clients have a path onto the newer features.

Use links, not payload data, to instruct clients when various state-driving operations are available.

It may appear to be a bit of effort to build up RepresentationModelAssembler implementations for each resource type and to use these components in all of your controllers. But this extra bit of server-side setup (made easy thanks to Spring HATEOAS) can ensure the clients you control (and more importantly, those you don’t) can upgrade with ease as you evolve your API.

This concludes our tutorial on how to build RESTful services using Spring. Each section of this tutorial is managed as a separate subproject in a single github repo:

nonrest — Simple Spring MVC app with no hypermedia

rest — Spring MVC + Spring HATEOAS app with HAL representations of each resource

evolution — REST app where a field is evolved but old data is retained for backward compatibility

links — REST app where conditional links are used to signal valid state changes to clients

To view more examples of using Spring HATEOAS see https://github.com/spring-projects/spring-hateoas-examples.

To do some more exploring check out the following video by Spring teammate Oliver Gierke:
But that is not the only thing needed to build a truly RESTful service with Spring.