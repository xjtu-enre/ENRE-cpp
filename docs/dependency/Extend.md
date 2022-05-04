# Extend

## Supported pattern
```yaml
name: Extend Declaration
```
### Syntax: 
```cpp
attr(optional) access-specifier(optional) virtual-specifier(optional) class-or-decltype		
```

#### Examples: 

```cpp
struct Base {
    int a, b, c;
};
struct Derived : Base {
    int b;
};
struct Derived2 : Derived {
    int c;
};
```

```yaml
name: Struct Extend 
entities:
    items:
        -   id: 0
            name: Base
            category: Struct
        -   id: 1
            name: Derived::Base
            category: Struct
        -   id: 2
            name: Derived2::Derived
            category: Struct
dependencies:
    items:
        -   category: Extend
            src: 1
            dest: 0
        -   category: Extend
            src: 2
            dest: 1
```


# Reference
- https://en.cppreference.com/w/cpp/language/derived_class