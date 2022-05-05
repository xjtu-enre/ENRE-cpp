# Extend

## Supported pattern
```yaml
name: Extend Declaration
```
### Syntax: 
```text
attr(optional) access-specifier(optional) virtual-specifier(optional) class-or-decltype		
```

#### Examples: 

- Example1
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
entity:
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
relation:
    r:
        d: .
        e: .
        s: Inheritance
        u: .
    items:
        -   category: Extend
            src: 1
            dest: 0
        -   category: Extend
            src: 2
            dest: 1
```


#### Examples: 

- Example2
```cpp
class Collection {

};
class Book {};
class CollectionOfBook : public Book, public Collection {
    
};
```

```yaml
name: Multi Extend
entity:
    items:
        -   id: 0
            name: Collection
            category: Class
        -   id: 1
            name: Book
            category: Class
        -   id: 2
            name: CollectionOfBook
            category: Class
relation:
    r:
        d: .
        e: .
        s: Inheritance
        u: .
    items:
        -   category: Extend
            src: 2
            dest: 0
        -   category: Extend
            src: 2
            dest: 1
```

# Reference
- https://en.cppreference.com/w/cpp/language/derived_class