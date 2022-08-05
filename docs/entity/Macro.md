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