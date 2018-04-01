# bootique-di

Bootique own DI implementation.

_EXPERIMENTAL... A port of `cayenne-di`._

## TODO:

Things to do to turn Cayenne DI into an adequate replacement of Guice in
Bootique:

* `List` vs `Set` bindings (we'd like to preserve a notion of order,
but still make it set-compatible... maybe support both?
* Allow binding generic types. `TypeLiteral` must be public.
* Use annotations as injection markers instead of Strings (@Qualifier?)
* Support for provider methods in the modules.
* Handling overrides (throw instead of ignore ... or maybe make it optional)
* Error handling - make sure we report multiple errors
* Optional Guice compatibility module - support for Guice annotations,
perhaps multi-binder API.

## Changes from `cayenne-di`

* Replaced `Provider` with `javax.inject` implementation.
* Replaced `@Inject` with `javax.inject` implementation.
* Replace individual constructor param injection with full constructor
injection. Now `@Inject` is placed on the constructor, and @Named - on
individual parameters if needed.