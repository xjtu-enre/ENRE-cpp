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
        items:
            -   name: S1
                loc: [ 1, 7 ]
                category: Template
                r:
                    d: Type
                    e: .
                    s: Class
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
        items:
            -   name: A
                loc: [ 1, 7 ]
                category: Template
                r:
                    d: Type
                    e: .
                    s: xStruct
                    u: .
```

# Reference
- https://en.cppreference.com/w/cpp/language/templates
- https://en.cppreference.com/w/cpp/language/class_template
- https://en.cppreference.com/w/cpp/language/function_template

