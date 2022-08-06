## Entity: Template

Description: `Template Entity` is the basis for generic programming in C++. Templates enable to define the operations of a class or function, and let the user specify what concrete types those operations should work on.

### Supported Patterns

```yaml
name: Template
```

#### Syntax: Template Declaration
```text
template < parameter-list > requires-clause(optional) declaration	(1)	
export template < parameter-list > declaration	(2)	(until C++11)
template < parameter-list > concept concept-name = constraint-expression ;	(3)	(since C++20)
```
##### Examples

###### Class Template

```cpp
template<typename T>
class S{};
```

```yaml
    name: Class Template
    entity:
        items:
            -   name: S
                loc: 2:7:2:7
                kind: Class Template
                type: Template
```

###### Struct Template
```cpp
template<typename T>
struct S{};
```

```yaml
    name: Struct Template
    entity:
        items:
            -   name: S
                loc: 2:8:2:8
                kind: Struct Template
                type: Template
```

