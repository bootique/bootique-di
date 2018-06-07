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