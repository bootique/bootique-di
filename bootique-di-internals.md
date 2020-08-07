# Bootique DI container optional features

DI container has several options that allows modifying 
certain aspects of its behavior.

Default values set to match Bootique Core requirements.

Option                            | Default value       |  Description    |
----------------------------------|:-------------------:|:----------|
Dynamic bindings                  | enabled             | bind and inject services in-place if no binding defined
Only declared overrides           | disabled            | allow only explicitly defined overrides
Default scope                     | no scope            | alternatively singleton scope can be set as a default
Method injection                  | disabled            | allow to inject values into methods
Injection trace                   | enabled             | trace injection to allow detailed exception messages
Proxy creation                    | enabled             | allow dynamic proxy creation to break circular dependencies; this feature has limitations, proxy could be created only for interfaces, no byte code generation used

Builder provided by the `DIBootsrap.injectorBuilder(..)` method should be used to modify these options.  

Moreover, Bootique DI allows customization of annotations and interfaces.
API provided by the `javax.inject` spec used by default.

For example, here is how you could set custom `@Inject` annotation:

```java
Injector injector = DIBootstrap.injectorBuilder(
        b -> {
            b.bind(Service.class).to(Service_Impl1.class);
            b.bind(Consumer2.class).to(Consumer2.class);
        })
        .withInjectAnnotationPredicate(o -> o.isAnnotationPresent(MyInject.class))
        .build();
```