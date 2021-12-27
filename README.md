# Posterer #

Posterer is a JavaFX application to manage and send data to HTTP-based services such as SOAP and REST.


## Table of Contents ##

- [Posterer's Origins](#posterers-origins)
- [Installation ](#installation)
- [Design Decisions ](#design-decisions)
- [License](#license)


## Posterer's Origins ##

It's handy to have an app that you can use to fire off HTTP transactions at services - Like a web browser for example. But browsers are driven by HTML, and for testing Web Services (i.e. SOAP) you need finer grain control. On Firefox, there is the _Poster_ plug-in. I've used this a lot and it's been very useful. 

As the number and the authentication and authorization requirements for hitting web services increase, _Poster_ is showing it's age. I need more power at my finger tips, so I am writing a poster-er. And here it is. _Posterer_.

Some feature:
* Store end-points and payloads
* Asynchronous requests
* Proxy control
* Call statistics
* Full headers
* Crypto controls

<a href="./docs/posterer.png"><img src="./docs/posterer.png" width="350" height="210" /></a>

## Installation ##

_Posterer_  version 1.0.* required  _JavaFX 8_ , and therefore _Java 8_ . 
_Posterer_  version 1.1.* required  _OpenJFX 11_ , and therefore _OpenJDK 11_ . 

Java 8 vs 11 includes the move to _Modules_ that requires extra configuration to get _OpenJFX_  modules into play. Consider the following JVM arguments:

	--module-path=/usr/share/openjfx/lib/ --add-modules=ALL-MODULE-PATH



## Design Decisions ##

Given that there are many options to provide the HTTP transport framework (and perhaps other network protocols and transports), Google _Guice_ was chosen to provide lightweight IOC to minimize linkage. Initially, _Posterer_ comes with _Apache HTTP client_. 


## License ##

Posterer - Copyright 2021 technosf [https://github.com/technosf]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.