## Entity: Namespace

Description: A `Namespace Entity` is a declarative region that provides a scope to the identifiers (the names of types, functions, variables, etc) inside it. Namespaces are used to organize code into logical groups and to prevent name collisions that can occur especially when your code base includes multiple libraries.

### Supported Patterns

```yaml
name: Namespace
```
#### Syntax: Namespace Declaration
```text
namespace ns-name { declarations }	(1)	
inline namespace ns-name { declarations }	(2)	(since C++11)
namespace { declarations }	(3)	
ns-name :: member-name	(4)	
using namespace ns-name ;	(5)	
using ns-name :: member-name ;	(6)	
namespace name = qualified-namespace ;	(7)	
namespace ns-name :: member-name { declarations }	(8)	(since C++17)
namespace ns-name :: inline member-name { declarations }	(9)	(since C++20)
```

##### Examples

###### Namespace
```cpp
namespace ns{}
```

```yaml
    name: Namespace
    entity:
        items:
            -   name: ns
                loc: 1:11:1:12
                type: Namespace
```

###### Unnamed Namespace
```cpp
namespace{
    int i; 
}
```

```yaml
    name: Unnamed Namespace
    entity:
        items:
            -   name: (unnamed namespace)
                loc: 1:0:1:0
                type: Namespace
```