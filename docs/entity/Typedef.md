## Entity: Typedef

Description: A `Typedef Entity` introduces a name that, within its scope, becomes a synonym for the type given by the type-declaration portion of the declaration.

### Supported Patterns

```yaml
name: Typedef
```

#### Syntax: Typedef Declaration
```text
typedef specifiers-and-qualifiers declarators-and-initializers(optional) ;	
```
##### Examples

###### typedef built-in type
```cpp
typedef int int_t;
```

```yaml
    name: typedef built-in type
    entity:
        items:
            -   name: int_t
                loc: 1:13:1:17
                type: Typedef
```

###### struct typdef 
```cpp
typedef struct{ double hi, lo; } range;
```

```yaml
    name: typedef struct
    entity:
        items:
            -   name: range
                type: Typedef
                loc: 1:34:1:38
```

