# Template

## Supported pattern
```yaml
name: Template Declaration
```
### Syntax
```text
template < parameter-list > requires-clause(optional) declaration	(1)	
export template < parameter-list > declaration	(2)	(until C++11)
template < parameter-list > concept concept-name = constraint-expression ;	(3)	(since C++20)
```


#### Examples: 

- Example1

```cpp
    template<C1 T>
    class S1 {};
```

```yaml
    name: Class Template
    entity:
        filter: Template
        items:
            -   name: S1
                loc: [ 1, 7 ]
                kind: Template
                r:
                    d: xType
                    e: .
                    s: xclass
                    u: .
```

- Example2
```cpp
    template<typename T>
    struct A{
        void f() {}
    };
```

```yaml
    name: Struct Template
    entity:
        filter: Struct Template
        items:
            -   name: A
                loc: [ 1, 7 ]
                kind: Template
                r:
                    d: xType
                    e: .
                    s: xstruct
                    u: .
```

# Reference
- https://en.cppreference.com/w/cpp/language/templates
- https://en.cppreference.com/w/cpp/language/class_template
- https://en.cppreference.com/w/cpp/language/function_template

