# ENRE-CPP


## Entity Categories

| Entity Name                        | Definition                                   |
|------------------------------------|----------------------------------------------|
| [File](entity/File.md)             | TBD                                          |
| [Namespace](entity/Namespace.md)   | TBD                                          |
| [Alias](entity/Alias.md)           | TBD                                          |
| [Class](entity/Classes.md)           | TBD                                          |
| [Struct](entity/Classes.md)         | TBD                                          |
| [Union](entity/Classes.md)           | TBD                                          |
| [Macro](entity/Macro.md)           | TBD                                          |
| [Enum](entity/Enum&Enumerator.md)             | TBD                                          |
| [Enumerator](entity/Enum&Enumerator.md) | TBD                                          |
| [Object](entity/Object.md)         | TBD                                          |
| [Function](entity/Function.md)     | TBD                                          |
| [Template](entity/Template.md)     | TBD                                          |
| [Typedef](entity/Typedef.md)       | TBD                                          |

## Dependency Categories


| Dependency Name                        | Definition                                   |
|------------------------------------|----------------------------------------------|
| [Alias](dependency/Alias.md)|                                           |
| [Call](dependency/Call.md) | Call dependency indicates a reference to a known C/C++ function. |
| [Declare](dependency/Declare.md) | Declare dependency indicates the declaration of an entity. |
| [Define](dependency/Define.md)    | Define dependency indicates the definition of an entity.|
| [End](dependency/End.md)         | End dependency marks the end point of functions, classes, enums, and namespaces. Each entity references itself.|
| [Exception](dependency/Exception.md) | Exception dependency indicates exceptions which may throw from function |
| [Extend](dependency/Extend.md)| Extend dependency indicates the inheritance relation between classes.|
| [Friend](dependency/Friend.md) | Friend dependency indicates the granting of friendship to a class or member function.  |
| [Include](dependency/Include.md)|Include dependency indicates a reference to an include file. |
| [Modify](dependency/Modify.md)   | Modify dependency indicates a reference which a variable is modified without an explicit assignment statment.|
| [Override](dependency/Override.md)  | Override dependency indicates when a method in one class overrides a virtual method in a base class.|
| [Set](dependency/Set.md)  | A Set dependency indicates any explicit assignment of a variable.|
| [Use](dependency/Use.md)  | A Use dependency indicates a reference in an active region of code to a known C/C++ variable.|
