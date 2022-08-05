## Entity: Class
Description: `Class` and `struct` are the constructs whereby you define your own types. Classes and structs can both contain data members and member functions, which enable you to describe the type's state and behavior.

### Supported Patterns

```yaml
name: Class
```
#### Syntax: Class Declaration

```text
class-key attr class-head-name { member-specification }	(1)	
class-key attr class-head-name : base-specifier-list { member-specification }	(2)	
```

##### Examples

###### Class
```cpp
class Matrix {
};
```

```yaml
name: Class
entity:
    items:
        -   name: Matrix
            loc: 1:1:7:12
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