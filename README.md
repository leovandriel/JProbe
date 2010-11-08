JProbe
======

*JProbe is a servlet for inspecting running thread inside a servlet container and detect those gone haywire.*

About
-----
JProbe is designed to be a minimalisitc tool to list and inspect all threads running on the JVM. Packaged in a deployable web archive, JProbe can be deployed next to a running servlet and inspect this servlet's threads. This allows for finding threads that are stuck with a minimal impact on the running web applications.

Build using Ant
---------------
JProbe can be build using [Apache Ant](http://ant.apache.org/). The build script is located in `build.xml` and can be configured in `build.properties`.

The minimum configuration required is setting the `servlet-libs.location` property to point to a folder containing the Java Servlet library (servlet-api.jar). This library is bundled with all Java Servlet implementions, e.g. [Apache Tomcat](http://tomcat.apache.org/).

A war (Web ARchive) file can be bundled by running: `ant all`

*The default password is 's3cr3t' and should be set to a secure password in web.xml before deployment in a production environment.*

License
-------
JProbe is licensed under the terms of the Apache License version 2.0, see the included LICENSE file.

Author
------
[Leo Vandriel](http://www.leovandriel.com/)