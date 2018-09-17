GDSC ImageJ Analytics
=====================

The GDSC Analytics package contains code to use the
[Google Analytics Measurement Protocol](https://developers.google.com/analytics/devguides/collection/protocol/v1/)
to collect usage information about a Java application.

[![Build Status](https://travis-ci.com/aherbert/gdsc-analytics.svg?branch=master)](https://travis-ci.com/aherbert/gdsc-analytics)
[![Coverage Status](https://coveralls.io/repos/github/aherbert/gdsc-analytics/badge.svg?branch=master)](https://coveralls.io/github/aherbert/gdsc-analytics?branch=master)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Functionality includes:

- PageView and Event hit-types
- Custom dimensions and metrics
- Synchronous or asynchronous measurement requests
- Configurable session timeout
- Collection of basic client parameters (screen size, user language, user agent)
- Graceful disabling when no Internet connection
- Configurable logging

Example:

```java
// Create the tracker
String trackingId = "UA-12345-6"; // Your Google Analytics tracking ID
String userId = "Anything";

GoogleAnalyticsClient ga =
    GoogleAnalyticsClient.createBuilder(trackingId)
                         .setUserId(userId)
                         .build();

// Submit requests
String documentHostName = "www.abc.com";
String documentPath = "/path/within/application/";
ga.pageview(documentHostName, documentPath).send();

// Shutdown
ga.getExecutorService().shutdown();
```

This would create a protocol parameter string of:

        v=1&sc=start&tid=UA-12345-6&cid=Anything&je=1&t=pageview&dh=www.abc.com&dp=%2Fpath%2Fwithin%2Fapplication%2F&qt=0

See the [Measurement Protocol Parameter Reference Guide](https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters) for more details.


Maven Installation
------------------

This package is a library to be used used by other Java programs. It is only
necessary to perform an install if you are building other packages that depend
on it.

1. Clone the repository

        git clone https://github.com/aherbert/gdsc-analytics.git

2. Build the code and install using Maven

        cd gdsc-analytics
        mvn install

	This will produce a gdsc-analytics-[VERSION].jar file in the local Maven
	repository. You can now build the other packages that depend on this code.


Background
----------

This project is based on ideas from
[JGoogleAnalyticsTracker](https://code.google.com/archive/p/jgoogleanalyticstracker/)
by Daniel Murphy. A similar package is
[JGoogleAnalytics](https://github.com/siddii/jgoogleanalytics) by Siddique Hameed.
These projects dummied the GET request sent to Google Analytics by a web browser,
i.e. used the legacy Google Analytics protocol.

This code uses the new Analytics Measurement Protocol which is designed to
allow any web connected device to measure user interaction via a HTTP POST
request.

The code is used within the GDSC ImageJ plugins to collect usage information
whenever a plugin is run. To comply with the
[General Data Protection Regulation (GDPR)](https://ico.org.uk/for-organisations/guide-to-the-general-data-protection-regulation-gdpr/):
- All data collected is anonymous and cannot be linked to an individual
- The GDSC ImageJ plugins allow tracking to be disabled

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
