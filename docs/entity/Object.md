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
    name: Object
    entities:
        filter: Object
        items:
            -   name: flags
                loc: [ 1, 7 ]
                kind: Alias
```

- Example2
```cpp
    struct S {
        char c;  
        float f; 
        bool operator==(const S& arg) const { 
            return c == arg.c && f == arg.f;
        }
    };
 
    void f() {
        assert(sizeof(S) == 8);
        S s1 = {'a', 3.14};
        S s2 = s1;
        reinterpret_cast<unsigned char*>(&s1)[2] = 'b'; 
        assert(s1 == s2); 
    }
```

```yaml
    name: Object 2
    entities:
        filter: Object
        items:
            -   name: flags
                loc: [ 1, 7 ]
                kind: Alias
```

# Reference
- https://en.cppreference.com/w/cpp/language/object

