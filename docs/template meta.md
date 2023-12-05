## Template meta

```cpp
template<typename T> class C {
    int x = T::foo(); 
};

struct S { static int foo(); };

C<S> cs;
```
