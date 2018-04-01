# bootique-di

Bootique own DI implementation.

_EXPERIMENTAL... A port of `cayenne-di`._

## Changes from `cayenne-di`

* Replaced `Provider` with `javax.inject` implementation.
* Replaced `@Inject` with `javax.inject` implementation.
* Replace individual constructor param injection with full constructor
injection. Now `@Inject` is placed on the constructor, and @Named - on
individual parameters if needed.