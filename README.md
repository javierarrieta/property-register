Property Register
=================

Property Register is a REST API server to query data from the property register of Ireland (http://www.propertypriceregister.ie/)

To import data from the files the propery register provides there is an importer module that will insert into the database the records not yet imported

More detailed information in the [wiki](https://github.com/javierarrieta/property-register/wiki)

##Technologies

The code is written in Scala, using Akka and Spray.io as main frameworks. Reactivemongo is used to connect to Mongodb

Sbt is used as build management tool

[Link to Technical Design](https://github.com/javierarrieta/property-register/wiki/Technical-Design)

##Development guide

Requirements:

- sbt 0.12+ must be installed (http://www.scala-sbt.org/)

To build the artefacts (fat jars), just type
```
$ sbt assembly
```
To generate the eclipse configuration:
```
$ sbt eclipse with-sources=true
```
To generate the Intellij Idea configuration:
```
$ sbt gen-idea
```
## LICENSE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
