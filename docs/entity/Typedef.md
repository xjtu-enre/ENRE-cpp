# Typedef

## Supported pattern
```yaml
name: Typedef Declaration
```
### Syntax
``` cpp
typedef specifiers-and-qualifiers declarators-and-initializers(optional) ;	
```


#### Examples: 

- Example1
```cpp
    typedef int int_t;
    typedef int A[]; 
    A a = {1, 2}, b = {3,4,5}; 
```

```yaml
    name: typedef built-in type
    entities:
        filter:  Typedef
        items:
            -   name: int_t
                loc: [ 1, 7 ]
                kind:  Typedef
```

- Example2
```cpp
    typedef struct { double hi, lo; } range;
    range z, *zp;
```

```yaml
    name: typedef struct
    entities:
        filter:  Typedef
        items:
            -   name:  range
                loc: [ 1, 7 ]
```

# Reference
- https://en.cppreference.com/w/cpp/language/typedef
- https://en.cppreference.com/w/c/language/typedef
- https://en.cppreference.com/w/c/language/declarations

