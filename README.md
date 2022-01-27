GDSC Analytics
==============

The GDSC Analytics package contains code to use the
[Google Analytics Measurement Protocol](https://developers.google.com/analytics/devguides/collection/protocol/v1/)
to collect usage information from a Java application.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.com/aherbert/gdsc-analytics.svg?branch=master)](https://travis-ci.com/aherbert/gdsc-analytics)
[![Coverage Status](https://coveralls.io/repos/github/aherbert/gdsc-analytics/badge.svg?branch=master)](https://coveralls.io/github/aherbert/gdsc-analytics?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/uk.ac.sussex.gdsc/gdsc-analytics/badge.svg)](https://maven-badges.herokuapp.com/maven-central/uk.ac.sussex.gdsc/gdsc-analytics/)
[![Javadocs](https://javadoc.io/badge2/uk.ac.sussex.gdsc/gdsc-analytics/javadoc.svg)](https://javadoc.io/doc/uk.ac.sussex.gdsc/gdsc-analytics)

[![Total alerts](https://img.shields.io/lgtm/alerts/g/aherbert/gdsc-analytics.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/aherbert/gdsc-analytics/alerts/)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/aherbert/gdsc-analytics.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/aherbert/gdsc-analytics/context:java)

Features
--------

- Builders to construct hit parameter strings
- Java type-safe handling of each protocol parameter `Value Type`
- Configurable asynchronous requests using `java.util.concurrent.ExecutorService`
- Minimal logging using `java.util.logging`
- Configurable session handling
- Graceful disabling when no internet connection
- No external dependencies

Example
-------

```Java
// Create the tracker
String trackingId = "UA-12345-6"; // Your Google Analytics tracking ID
String userId = "Anything";

GoogleAnalyticsClient ga =
    GoogleAnalyticsClient.newBuilder(trackingId)
                         .setUserId(userId)
                         .build();

// Submit requests
String documentHostName = "www.abc.com";
String documentPath = "/path/within/application/";
ga.pageview(documentHostName, documentPath).send();
```

This would create a protocol parameter string of:

    v=1&je=1&tid=UA-12345-6&uid=Anything&dh=www.abc.com&t=pageview&sc=start&&dp=%2Fpath%2Fwithin%2Fapplication%2F

See the [Measurement Protocol Parameter Reference Guide](https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters)
for more details.

Asynchronous Measurements
-------------------------

The main functionality of the code is composed of creating a hit of `name=value`
pairs and then sending the hit to Google Analytics.

The only part that operates inline is storage of the parameter names and values.
Storage is done using Java primitives in a type-safe manner. This effectively
queues the hit for processing.

An `ExecutorService` is used to perform the construction of the hit and the
sending to Google Analytics in the background. Therefore the code has minimal
run-time impact. The Queue Time parameter is used to ensure that hit times
are collated correctly even if the hit is sent some time after the hit
was queued.

The default `ExecutorService` uses a single low priority thread. The service is
configurable and can be shared.

Parameter Caching
-----------------

Parameters that are the same with each hit can be cached at the hit or session
level. For example if the host never changes:

```Java
// Create the tracker
String trackingId = "UA-12345-6"; // Your Google Analytics tracking ID
String userId = "Anything";
String documentHostName = "www.abc.com";

GoogleAnalyticsClient ga =
    GoogleAnalyticsClient.newBuilder(trackingId)
                         .setUserId(userId)
                         .getOrCreatePerHitParameters()
                             .addDocumentHostName(documentHostName)
                             .getParent()
                         .build();

// Submit requests
String documentPath = "/path/within/application/";
ga.hit(HitType.PAGEVIEW).addDocumentPath(documentPath).send();
```

Builder API
-----------

A Measurement Protocol hit is simply a set of `name=value` pairs separated by
the `&` character:

    v=1&t=event&tid=UA-12345-6&uid=Test&cd1=StartUp

The Builder API allows adding named Measurement Protocol parameters to a
collection. The parameters collection can be used to construct a protocol hit
composed of `name=value` pairs.

The following sections of the [Measurement Protocol Parameter Reference](https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters)
are supported:

| Section | Support |
| :- | :-: |
| General | Full |
| User | Full |
| Session | Full |
| Traffic Sources | - |
| System Info | Full |
| Hit | Full |
| Content Information | Full |
| App Tracking | Full |
| Event Tracking | Full |
| E-Commerce | - |
| Enhanced E-Commerce | - |
| Social Interactions | Full |
| Timing | Full |
| Exceptions | Full |
| Custom Dimensions/Metrics | Full |
| Content Experiments | Full |

Example:

```Java
String trackingId = "UA-12345-6"; // Your Google Analytics tracking ID
String hit = Parameters.newRequiredBuilder(trackingId)
                       .addHitType(HitType.EXCEPTION)
                       .addExceptionDescription("Something went wrong")
                       .build()
                       .format();
```

Creates the following hit with the required version (`v`) and client Id (`cid`)
parameters set:

    v=1&je=1&tid=UA-12345-6&t=exception&exd=Something+went+wrong&cid=da51f86a-346d-4aa1-933a-4883887a34cb

The client Id will be automatically added using a random GUID. The Java enabled
(`je`) parameter is also set (because it's Java).

### Note ###

Support was added for parameters that will be of use from within a Java
application. Hence no support for the campaign functionality in the Traffic
Sources section or any E-Commerce functionality.

Support was complete in 2018. Features may have been added to the protocol
since that date (see also [Custom Parameters](#custom-parameters)).

Core API
--------

The builder API is a user-friendly wrapper to an underlying API that provides
type-safe support of the entire Measurement Protocol parameter reference. Thus
any parameter can be added to a hit by adding a `FormattedParameter` to a
builder using the appropriate `ProtocolSpecification`.

The `ProtocolSpecification` enum contains all of the Measurement Protocol
parameters and specifies the:

- Formal Name
- Name (for the `name=value` pair)
- Value Type (for the `name=value` pair)
- Number of indices in the name
- Supported hit types

Examples:

- Queue Time : `qt` : integer : 0 : all
- Custom Dimension : `cd_` : text : 1 : all
- Exception : `exd` : text : 0 : exception
- Is Exception Fatal? : `exf` : boolean : 0 : exception

It is expected that any `_` character in a parameter name is replaced with an
index. The number of indexes within the protocol parameters is in the range
`0-3` and type-safe parameter classes are provided for the entire specification
to allow dynamic index replacement into a hit.

Example:

```Java
String trackingId = "UA-12345-6"; // Your Google Analytics tracking ID
String hit = Parameters.newRequiredBuilder(trackingId)
                       .addHitType(HitType.ITEM)
                       .add(new NoIndexTextParameter(ProtocolSpecification.TRANSACTION_ID, "Trans.1"))
                       .add(new NoIndexTextParameter(ProtocolSpecification.ITEM_NAME, "Item.2"))
                       .add(new OneIndexTextParameter(ProtocolSpecification.PRODUCT_SKU, 23, "SKU.4567"))
                       .build()
                       .format();
```

Creates hit:

    v=1&je=1&tid=UA-12345-6&t=item&ti=Trans.1&in=Item.2&pr23id=SKU.4567&cid=1e96ab9f-bb03-4a9d-b4b3-dc31b7d01b51

### Custom Parameters ###

The supported parameters are based on a snapshot of the
[Measurement Protocol Parameter Reference](https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters).

If the parameter reference changes then the API will be out-of-date. If this
occurs then please provide feedback so the library can be updated.

However it is possible to add any parameter to a set of parameters using either
a generic `name=value` pair or a custom parameter specification:

```Java
// Generic name=value pair
String name = "anything";
String value = "some text";

// Custom indexed parameter
String formalName = "My parameter"; // Used for error messages
String nameFormat = "myp_"; // Underscore for the index
ValueType valueType = ValueType.INTEGER;
int maxLength = 0; // Ignored for non-text types
ParameterSpecification specification = new CustomParameterSpecification(
    formalName, nameFormat, valueType, maxLength);
int index = 44;
int value2 = 123;

String hit = Parameters.newBuilder()
    .add(name, value)
    .add(new OneIndexIntParameter(specification, index, value2))
    .build()
    .format();
```

Will create a hit:

    anything=some+text&myp44=123

Note this is only a partial hit as the required parameters (`v`, `tid`, `cid`/`uid`, `t`) are missing.

Installation
------------

This package is a library to be used used by other Java programs.
The latest version is on Maven central:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/uk.ac.sussex.gdsc/gdsc-analytics/badge.svg)](https://maven-badges.herokuapp.com/maven-central/uk.ac.sussex.gdsc/gdsc-analytics/)

The package can be installed locally from the source.

1. Clone the repository

        git clone https://github.com/aherbert/gdsc-analytics.git

2. Build the code and install using Maven

        cd gdsc-analytics
        mvn install

	This will produce a gdsc-analytics-[VERSION].jar file in the local Maven
	repository. You can now build the other packages that depend on this code.


Background
----------

#### Origin ####

This project originated using ideas from
[JGoogleAnalyticsTracker](https://code.google.com/archive/p/jgoogleanalyticstracker/)
by Daniel Murphy. A similar package is
[JGoogleAnalytics](https://github.com/siddii/jgoogleanalytics) by Siddique
Hameed. These projects dummied the GET request sent to Google Analytics by a web
browser, i.e. used the legacy Google Analytics protocol.

This code uses the new Analytics Measurement Protocol which is designed to
allow any web connected device to measure user interaction via a HTTP POST
request. Version 1 of the code was based on JGoogleAnalyticsTracker to send the
formatted hits to Google analytics. Version 2 has been completely rewritten
to use Java 1.8 features and add a comprehensive test suite. It shares no
similarity to version 1 other than the name.

#### GDSC ImageJ plugins ####

The code is used within the GDSC ImageJ plugins to collect minimal usage
information whenever a plugin is run. This is done by identifying each
ImageJ plugin using the [Document Path](http://goo.gl/a8d4RP#dp) hit parameter
and a `pageview` hit. The data is used to determine what parts of the
code are important to the community.

To comply with the
[General Data Protection Regulation (GDPR)](https://ico.org.uk/for-organisations/guide-to-the-general-data-protection-regulation-gdpr/):
- All data collected is anonymous and cannot be linked to an individual
- The GDSC ImageJ plugins allow tracking to be disabled (this is the default)

Testing
-------

The code has been tested using:
- [AssertJ](http://joel-costigliola.github.io/assertj/) for URL parameter testing
- [Mockito](https://site.mockito.org/) for HTTP connection testing of all conditions including various mocked failures
- [JUnit 5](https://junit.org/junit5/) for everything else

###### Owner(s) ######
Alex Herbert

###### Institution ######
[Genome Damage and Stability Centre, University of Sussex](http://www.sussex.ac.uk/gdsc/)
