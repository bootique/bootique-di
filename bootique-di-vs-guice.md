
# Comparision of Bootique DI and Google Guice

 Feature                          | Bootique DI         |  Guice    |
----------------------------------|:-------------------:|:---------:|
Method injection                  | configurable        | yes
Map injection                     | yes                 | yes       
Set injection                     | yes                 | yes       
Ordered list injection            | yes                 | no        
Per-element binding in collection | no                  | yes       
Optional injection                | yes                 | yes
Binding override                  | always allowed ([#5](https://github.com/bootique/bootique-di/issues/5)) | should be declared per module
Default binding scope             | configurable        | no scope  
Binding decorators                | yes                 | no?
Eager singleton                   | no                  | yes
Just-in-time Bindings             | configurable        | allowed by default
@ImplementedBy/@ProvidedBy        | no                  | yes
Auto resolution of circular dependecies | no            | yes
Multiple failures report          | no                  | yes
