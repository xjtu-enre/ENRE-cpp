# Namespace

## Supported pattern
```yaml
name: Namespace Declaration
```
### Syntax
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


#### Examples: 

- Example1
```cpp
    namespace Lib
    {
        inline namespace Lib_1
        {
            template <typename T> class A; 
        }
        template <typename T> void g(T) { /* ... */ }
    }
```

```yaml
    name: Namespace
    entity:
        items:
            -   name: Lib
                loc: [ 1, 7 ]
                category: Namespace
                r:
                    d: Package
                    e: .
                    s: .
                    u: .
```

- Example2
```cpp
    namespace
    {
        int i; 
    }
```

```yaml
    name: Unnamed Namespace
    entity:
        items:
            -   name: [unnamed namespace]
                loc: [ 1, 7 ]
                category: Namespace
                r:
                    d: Package
                    e: x
                    s: anonymous namespace
                    u: .
```

# Reference
- https://en.cppreference.com/w/cpp/language/namespace

