# bootique-di

## TODO:

Things to do to turn Cayenne DI into an adequate replacement of Guice in
Bootique:

* `List` vs `Set` bindings (we'd like to preserve a notion of order,
but still make it set-compatible... maybe support both?
* Allow binding generic types. `TypeLiteral` must be public.
* Support for provider methods in the modules.
* Use `javax.inject` instead of own annotations.
* Optional Guice compatibility module - support for Guice annotations,
perhaps multi-binder API.