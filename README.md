# bootique-di

Bootique lightweight DI implementation.

_EXPERIMENTAL..._

## Changes from `cayenne-di`

Bootique DI is a port and enhancement over  `cayenne-di`. Here are the
main changes compared to the Apache Cayenne version:

* Replaced `Provider` with `javax.inject` implementation.
* Replaced `@Inject` with `javax.inject` implementation.
* Replace individual constructor param injection with full constructor
injection. Now `@Inject` is placed on the constructor, and @Named - on
individual parameters if needed.
* Turned off module auto-loading. This will be area of responsibility for
Bootique core.