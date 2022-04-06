# Extend

## Supported pattern
```
name: Extend Declaration
```
### Syntax: 
```cpp
attr(optional) access-specifier(optional) virtual-specifier(optional) class-or-decltype		
```

#### Examples: 

``` cpp
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

``` 
entities:
    items:
        -   id: 0
            name: Base
            kind: Struct
        -   id: 1
            name: Derived : Base
            kind: Struct
        -   id: 2
            name: Derived2 : Derived
            kind: Struct
dependencies:
    items:
        -   kind: Extend
            src: 1
            dest: 0
        -   kind: Extend
            src: 2
            dest: 1
```


# Reference
- https://en.cppreference.com/w/cpp/language/derived_class