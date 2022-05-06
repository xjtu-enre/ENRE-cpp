# Macro

## Supported pattern
```yaml
name: Macro Declaration
```
### Syntax
```text
#define identifier replacement-list(optional)	(1)	
#define identifier( parameters ) replacement-list(optional)	(2)	
#define identifier( parameters, ... ) replacement-list(optional)	(3)	(since C++11)
#define identifier( ... ) replacement-list(optional)	(4)	(since C++11)
#undef identifier	(5)	
```


#### Examples: 

- Example1
```cpp
    #define F(...) f(0 __VA_OPT__(,) __VA_ARGS__)
```

```yaml
    name: Macro
    entity:
        filter:  Macro
        items:
            -   name: F
                loc: [ 1, 7 ]
                kind:  Macro
                r:
                    d: xFunction
                    e: .
                    s: .
                    u: .
```


# Reference
- https://en.cppreference.com/w/cpp/preprocessor/replace