# Overrides

## Supported pattern
```yaml
name: Overrides Declaration
```
These reference kids indicate when a method in one class overrides a virtual method in a base class.

### Syntax: 

#### Examples: 

- Example1
```cpp
class A{
    public: virtual void func();
}
class B : public A{
    public: void func();
}
```

```yaml
name: Override
entity:
    items:
        -   id: 0
            name: A
            category: Class 
        -   id: 1
            name: A::func
            category: Function
        -   id: 2
            name: B
            category: Class
        -   id: 3
            name: B::func
            category: Function
relation:
    items:
        -   category: Extend
            src: 2
            dest: 0
        -   category: Overrides
            src: 3
            dest: 1
```