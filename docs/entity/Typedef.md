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
```CPP
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
```CPP
typedef struct{ double hi, lo; } range;
```

```yaml
    name: struct typdef 
    entity:
        items:
            -   name: range
                type: Typedef
                loc: 1:34:1:38
```

###### Function Pointer Typedef
```CPP
typedef int (*t_somefunc)(int,int);
```
```yaml
    name: Function Pointer Typedef
    entity:
        items:
            -   name: t_somefunc
                type: Function Pointer
                loc: 1:15:1:24
```
