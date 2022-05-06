# Object

## Supported pattern
```yaml
name: Object Declaration
```
### Syntax
Objects can be explicitly created by definitions, new-expressions, throw-expressions, changing the active member of a union and evaluating expressions that require temporary objects. The created object is uniquely defined in explicit object creation.

Objects of implicit-lifetime types can also be implicitly created by

- operations that begin lifetime of an array of type char, unsigned char, or std::byte, (since C++17) in which case such objects are created in the array,
- call to following allocating functions, in which case such objects are created in the allocated storage:
  - operator new
  - operator new[]
  - std::malloc
  - std::calloc
  - std::realloc
  - std::aligned_alloc(since C++17)
- call to following object representation copying functions, in which case such objects are created in the destination region of storage or the result:
  - std::memcpy
  - std::memmove
  - std::bit_cast(since C++20)



#### Examples: 

- Example1
```cpp
    struct X { int a, b; };
    X *MakeX()
    {
        X *p = static_cast<X*>(std::malloc(sizeof(X)));
        p->a = 1;
        p->b = 2;
        return p;
    }
```

```yaml
    name: Variable 1
    entity:
        items:
            -   name: p
                category: Variable
                r:
                    d: Type
                    e: .
                    s: x
                    u: Local Object
```

- Example2
```cpp
    int x;
```

```yaml
    name: Variable 2
    entity:
        items:
            -   name: x
                category: Variable
                r:
                    d: Var
                    e: .
                    s: Global Variable
                    u: Global Object
```

# Reference
- https://en.cppreference.com/w/cpp/language/object

