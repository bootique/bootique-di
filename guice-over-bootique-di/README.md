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

# Guice API implementation over Bootique DI

This module is a bridge implementation of a subset of Google Guice API 
over Bootique DI implementation.
Intended for easier migration from Guice to Bootique DI, primarily for Bootique 
core and modules.

## Supported features:

* Guice annotations (including `@Provides`) and interfaces (`Provider`, `Module`, etc.)
* General binding (`com.google.inject.Injector` and `com.google.inject.Binder` interfaces)
* Multi bindings: `Multibinder` and `MapBinder`
* `OptionalBinder`
* Eager singletons (_note_ this feature is emulated, as it is not supported by Bootique DI itself)

## Limitations:

* Limited scope support: only no-scope and singleton scope are supported
* Limited Map and Set bindings: no per-element scopes, no provider bindings for elements
* Limited `Injector` API (e.g. no nested injectors, no module install after injector creation, etc.)
* No extensions are supported
* No module checked overrides, all overrides are enabled
* No support for custom annotation instances, only `Names.named("myName")` is supported
* Many of utility classes are missing

## TODO:

* Cleanup exception handling 