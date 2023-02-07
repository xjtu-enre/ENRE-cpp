## Entity: Class
Description: `Class` and `struct` are the constructs whereby you define your own types. Classes and structs can both contain data members and member functions, which enable you to describe the type's state and behavior.

### Supported Patterns

```yaml
name: Class
```
#### Syntax: Class Declaration

```text
class-key attr(optional) class-head-name final(optional) base-clause(optional) { member-specification }	(1)	
class-key attr(optional) class-head-name : base-specifier-list { member-specification } [declarators];	(2)	
```

##### Examples

###### Class
```cpp
class Base { /* empty */};
class Extend : public Base {/* empty */ };
```

```yaml
name: Class
entity:
    items:
        -   name: Base
            loc: 1:1:7:10
            type: Class
        -   name: Extend
            loc: 2:2:7:12
            type: Class
```

###### Class With Object
```cpp
class objClass { /* empty */} obj;
```

```yaml
name: Class With Object
entity:
    items:
        -   name: objClass
            loc: 1:1:7:14
            type: Class
```

###### Anonymous Class
```cpp
class { /* empty */ };   
```

```yaml
    name: Anonymous Class
    entity:
        items:
            -   name: <Anonymous as="Class">
                loc: 1:7:1:7
                type: Class
```

###### Nested Class
```cpp
class enclose
{
    class nested // private member
    {
    };
};
```

```yaml
    name: Nested Class
    entity:
        items:
            -   name: enclose
                loc: 1:7:1:13
                type: Class
            -   name: nested
                loc: 3:11:3:16
                type: Class
```

###### Class with final specifier
```cpp
class Vector final {
};
```

```yaml
    name: Class with final specifier
    entity:
        items:
            -   name: Vector
                loc: 1:7:1:12
                type: Class
```