# Macro

## Supported pattern
```
name: Macro Declaration
```
### Syntax
``` cpp
#define identifier replacement-list(optional)	(1)	
#define identifier( parameters ) replacement-list(optional)	(2)	
#define identifier( parameters, ... ) replacement-list(optional)	(3)	(since C++11)
#define identifier( ... ) replacement-list(optional)	(4)	(since C++11)
#undef identifier	(5)	
```


#### Examples: 

- Example1
    ``` cpp
    #define F(...) f(0 __VA_OPT__(,) __VA_ARGS__)
    ```

    ``` 
    entities:
        filter:  Macro
        items:
            -   name: F
                loc: [ 1, 7 ]
                kind:  Macro
    ```


# Reference
- https://en.cppreference.com/w/cpp/preprocessor/replace