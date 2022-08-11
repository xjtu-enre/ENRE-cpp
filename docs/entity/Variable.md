## Entity: Variable

Description: `Variable Entity` can be explicitly created by definitions, new-expressions, throw-expressions, changing the active member of a union and evaluating expressions that require temporary Variable.
### Supported Patterns

```yaml
name: Variable
```

#### Syntax: Variable Declaration
```text
- call to following allocating functions, in which case such Variables are created in the allocated storage:
  - operator new
  - operator new[]
  - std::malloc
  - std::calloc
  - std::realloc
  - std::aligned_alloc(since C++17)
- call to following Variable representation copying functions, in which case such Variables are created in the destination region of storage or the result:
  - std::memcpy
  - std::memmove
  - std::bit_cast(since C++20)
```

##### Examples

###### Point Variable
```cpp
int a = 0;
int *p = &a;
```

```yaml
    name: Point Variable
    entity:
        items:
            -   name: p
                type: Variable
                loc: 2:6:2:6
```

###### Variable
```cpp
int x;
```

```yaml
    name: Variable 2
    entity:
        items:
            -   name: x
                type: Variable
                loc: 1:5:1:5
```

