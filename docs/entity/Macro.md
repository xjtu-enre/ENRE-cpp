## Entity:  Macro

Description: The preprocessor expands `Macro Entity` in all lines except preprocessor directives, lines that have a # as the first non-white-space character. The `#define` directive is typically used to associate meaningful identifiers with constants, keywords, and commonly used statements or expressions.

### Supported Patterns

```yaml
name: Macro
```
#### Syntax: Macro Declaration
```text
#define identifier replacement-list(optional)	(1)	
#define identifier( parameters ) replacement-list(optional)	(2)	
#define identifier( parameters, ... ) replacement-list(optional)	(3)	(since C++11)
#define identifier( ... ) replacement-list(optional)	(4)	(since C++11)
#undef identifier	(5)	
```

##### Examples

###### Macro
```cpp
#define F(...) f(0 __VA_OPT__(,) __VA_ARGS__)
```

```yaml
    name: Macro
    entity:
        items:
            -   name: F
                loc: 1:9:1:9
                type: Macro
```


###### NULL Macro
```cpp
#define EMPTY
```

```yaml
    name: NULL Macro
    entity:
        items:
            -   name: EMPTY
                loc: 1:9:1:13
                type: Macro
```


###### Conditional Macro
```cpp
#define EMPTY
#ifdef EMPTY
#define FULL
#endif
```

```yaml
    name: Conditional Macro
    entity:
        items:
            -   name: EMPTY
                loc: 1:9:1:13
                type: Macro
            -   name: FULL
                loc: 3:9:3:12
                type: Macro
```