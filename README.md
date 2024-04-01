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

[![build test deploy](https://github.com/bootique/bootique-di/actions/workflows/maven.yml/badge.svg)](https://github.com/bootique/bootique-di/actions/workflows/maven.yml)

# bootique-di

**Since Bootique 3, `bootique-di` is no longer developed as a standalone module, and is instead included in Bootique core. So this GitHub project is retired, while the DI functionality continues to live and thrive at https://github.com/bootique/bootique .**

A [lightweight and fast](https://github.com/andrus/di-comparison#results-java-8) dependency injection (DI) engine used by Bootique framework. Can be used in the context of Bootique or as a standalone DI engine.

## Docs

- [Docs](https://bootique.io/docs/2.x/bootique-docs/#_bqruntime_and_di) (in the context of Bootique)
- [Migration from Guice](https://bootique.io/docs/2.x/migrate-from-guice/)

## Standalone Example

Starting and using Bootique DI 
```java
BQModule m = binder -> binder.bind(MyService.class).to(MySericeImpl.class);
Injector injector = DIBootstrap.createInjector(m);
MyService s = injector.getInstance(MyService.class);
```

