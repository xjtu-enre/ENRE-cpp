# Overrides

## Supported pattern
```
name: Overrides Declaration
```
These reference kids indicate when a method in one class overrides a virtual method in a base class.

### Syntax: 

#### Examples: 

``` cpp
class A{
    public: virtual void func();
}
class B : public A{
    public: void func();
}
```

``` 
entities:
    items:
        -   id: 0
            name: A
            kind: Class 
        -   id: 1
            name: A::func
            kind: Function
        -   id: 2
            name: B
            kind: Class
        -   id: 3
            name: B::func
            kind: Function
dependencies:
    items:
        -   kind: Extend
            src: 2
            dest: 0
        -   kind: Overrides
            src: 3
            dest: 1
```