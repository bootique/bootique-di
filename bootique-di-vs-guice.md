
# Per feature comparision of Bootique DI and Google Guice

 Feature                          | Bootique DI         |  Guice    |
----------------------------------|:-------------------:|:---------:|
Map injection                     | yes                 | yes       
Set injection                     | yes                 | yes       
Ordered list injection            | yes                 | no        
Per-element binding in collection | no                  | yes       
Optional injection                | no (#11)            | yes
Binding override                  | always allowed (#5) | should be declared per module
Default binding scope             | singleton           | no scope  
Eager singleton                   | no                  | yes
Injection w/o binding             | not allowed         | allowed by default
Binding decorators                | yes                 | no?
