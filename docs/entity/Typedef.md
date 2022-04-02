# Typedef

## Supported pattern
```
name: Typedef Declaration
```
### Syntax
``` cpp
typedef specifiers-and-qualifiers declarators-and-initializers(optional) ;	
```


#### Examples: 

- Example1
    ``` cpp
    typedef int int_t;
    typedef int A[]; // A is int[]
    A a = {1, 2}, b = {3,4,5}; // type of a is int[2], type of b is int[3]
    ```

    ``` json
    entities:
        filter: Alias
        items:
            -   name: flags
                loc: [ 1, 7 ]
                kind: Alias
    ```

- Example2
    ``` cpp
    typedef struct { double hi, lo; } range;
    range z, *zp;
    ```

    ``` json
    entities:
        filter: Alias
        items:
            -   name: flags
                loc: [ 1, 7 ]
                kind: Alias
    ```

# Reference
- https://en.cppreference.com/w/cpp/language/typedef
- https://en.cppreference.com/w/c/language/typedef
- https://en.cppreference.com/w/c/language/declarations

