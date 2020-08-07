<!--
  Licensed to ObjectStyle LLC under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ObjectStyle LLC licenses
  this file to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

[![Build Status](https://travis-ci.org/bootique/bootique-di.svg)](https://travis-ci.org/bootique/bootique-di)

# bootique-di

Bootique lightweight DI implementation.

_EXPERIMENTAL..._

## Changes from `cayenne-di`

Bootique DI is a port and enhancement over  `cayenne-di`. Here are the
main changes compared to the Apache Cayenne version:

* Replaced `Provider` with `javax.inject` implementation.
* Replaced `@Inject` with `javax.inject` implementation.
* Replace individual constructor param injection with full constructor
injection. Now `@Inject` is placed on the constructor, and `@Named` - on
individual parameters if needed.
* Turned off module auto-loading. This will be area of responsibility for
Bootique core.
* Supporting provider methods annotated with `@Provides`.

## Dev notes

[Customization options](https://github.com/bootique/bootique-di/blob/master/bootique-di-internals.md)
[Migration from Guice](https://github.com/bootique/bootique-di/blob/master/bootique-di-vs-guice.md)