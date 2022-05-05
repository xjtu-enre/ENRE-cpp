# Classes

## Supported pattern
```yaml
name: ClassDeclaration
```
### Syntax
```text
class-key attr class-head-name { member-specification }	(1)	
class-key attr class-head-name : base-specifier-list { member-specification }	(2)	
```
class-key can be one of class, struct, union.


#### Examples: 

- Example1: class and struct without base-specifier-list
```cpp
class Matrix {
};
```

```yaml
name: Class
entity:
    filter: classes
    r:
        d: Type
        e: .
        s: .
        u: _
    items:
        -   name:  Matrix
            loc: [ 1, 7 ]
            kind: Class
```
- Example2
```cpp
struct MyStruct {
    int value;
};
```
```yaml
name: Struct
entity:
    filter: classes
    items:
        -   name: MyStruct
            loc: [ 1, 7 ]
            kind: Struct
            r:
                d: .
                e: .
                s: .
                u: _
        -   name: MyStruct::value
            loc: [ 1, 7 ]
            kind: Variable
            r:
                d: Var
                e: .
                s: public field
                u: _
```

- Example3
```cpp
class Vector final {
};
```

```yaml
    name: Class Vector
    entity:
        filter: classes
        items:
            -   name: Vector
                loc: [ 1, 7 ]
                kind: Class
                r:
                    d: Type
                    e: .
                    s: .
                    u: _
```

### Syntax: Type alias
```text
union attr class-head-name { member-specification }		
```


#### Examples: 

- Example4:
```cpp
union month
{
    std::uint16_t s[2]; // occupies 4 bytes
    std::uint8_t c;     // occupies 1 byte
};  
```

```yaml
name: Union
entity:
    filter: Classes
    items:
        -   name: month
            loc: [ 1, 7 ]
            kind: Union
            r:
                d: e/Type
                e: .
                s: _
                u: _
        -   name: month::s
            loc: [ 1, 7 ]
            kind: Variable
            r:
                d: Var
                e: .
                s: _
                u: _
        -   name: month::c
            loc: [ 1, 7 ]
            kind: Variable
            r:
                d: Var
                e: .
                s: _
                u: _
```