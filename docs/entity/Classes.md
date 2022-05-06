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
    r:
        d: Type
        e: .
        s: .
        u: .
    items:
        -   name:  Matrix
            loc: [ 1, 7 ]
            category: Class
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
    items:
        -   name: MyStruct
            loc: [ 1, 7 ]
            category: Struct
            r:
                d: Type
                e: .
                s: .
                u: .
        -   name: MyStruct::value
            loc: [ 1, 7 ]
            category: Variable
            r:
                d: Var
                e: .
                s: public field
                u: public Object
```

- Example3
```cpp
class Vector final {
};
```

```yaml
    name: Class Vector
    entity:
        items:
            -   name: Vector
                loc: [ 1, 7 ]
                category: Class
                r:
                    d: Type
                    e: .
                    s: .
                    u: .
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
    int s[2]; 
    float c;    
};  
```

```yaml
name: Union
entity:
    items:
        -   name: month
            loc: [ 1, 7 ]
            category: Union
            r:
                d: Type
                e: .
                s: .
                u: .
        -   name: month::s
            loc: [ 1, 7 ]
            category: Variable
            r:
                d: Var
                e: .
                s: Public Field
                u: Public Member Object
        -   name: month::c
            loc: [ 1, 7 ]
            category: Variable
            r:
                d: Var
                e: .
                s: Public Field
                u: Public Member Object
```