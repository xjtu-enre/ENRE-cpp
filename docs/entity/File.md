## Entity:  File

Description: A `File Entity` is mostly a source file.

### Supported Patterns

```yaml
name: File
```

#### Syntax: File Declaration

```text
.h,.cpp,.cxx etc
```

##### Examples

###### File
```CPP
//// file.cpp
```
```yaml
    name: File
    entity:
      items:
        -   name: file.cpp
            loc: -1:-1:-1:-1
            type: File
```
