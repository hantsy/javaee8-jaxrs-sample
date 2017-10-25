# javaee8-jaxrs-sample(WIP)



I have created [ a RESTful  API sample ](https://github/hantsy/angularjs-ee7-sample) to  demostrate how to build RESTful APIs with  Java EE 7 and JAX-RS 2.0.

In this sample, I will  rewrite it with the latest Java EE 8. I will use Glassfish v5 as target runtime, because  Java EE 8 supprt in other application servers are still under construction, as I know the newest Wildfly v11 still does not support Java EE 8, and IBM Open Liberty has began to add Java EE 8 features as its further rolling updates.   

The following specifications are used in this sample appliacation:

* JAX-RS 2.1
* JSON-P 1.1 and JSON-B 1.0
* JPA 2.2(and EJB, JTA) for database operations
* CDI 2
* Bean Validation 2
* Java EE Security 1.0


## What's it?

You have to use a blog system, a CMS like publishing platform etc. In this sample, I will demonstrate how to create a simple blog application.

* **backend** is the RESTful APIs, stateless, protected by JWT authentication.
* **frontend UI**, a Angular SPA applicatoin(in plan)

In the initial version, it could includes the following features:

* CRUD operation for post resouces
* Comment on a certtain post
* User can favorite/unfavorite a certain post


I have also created some some variants to demonstrate varied technology stack in the past, you can browse which you are interested in.

* [Build RESTful APIs with Spring MVC](https://github.com/hantsy/angularjs-springmvc-sample)
* [Build RESTful APIs with Spring MVC and Spring Boot](https://github.com/hantsy/angularjs-springmvc-sample-boot), please read the [online Gitbook](https://www.gitbook.com/book/hantsy/build-a-restful-app-with-spring-mvc-and-angularjs/details) for more details.
* [Build RESTful APIs with Java EE 7 and JAX-RS](https://github.com/hantsy/angularjs-ee7-sample),  please check the [wiki pages](https://github.com/hantsy/angularjs-ee7-sample/wiki) for the developer notes.


## Build the project 

### Prerequisites

To try this appliacation in your local system, make sure you have already installed the following software.

* [Oracle Java 8](https://java.oracle.com) 
* [Apache Maven](https://maven.apache.org)
* [NetBeans IDE](http://www.netbeans.org), NetBeans 9 nightly is recommended.
* [Glassfish v5](https://javaee.github.io/glassfish/)


### Get the source codes

Clone the source codes from github. 

```
git clone https://github.com/hantsy/javaee8-jaxrs-sample
```

Or check out the codes NetBeans IDE which have great Git support.

Now you can run it from mvn command line or NetBeans IDE.

#### Command line

```
mvn verify cargo:run
```

#### NetBeans IDE

1. Adds Glassfish in **Server** node in the **Service** view.
2. Open the project(if you used NetBeans to check out the codes, it could be open by default)
3. Right click the project node, and select **Run** to run this project on Glassfish.

## Contribution 

Welcome to contribute this project, you can fork it and send a pull request, or share your idea on Github issues.

