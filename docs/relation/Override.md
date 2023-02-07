## Relation: Overrides

Descriptions:  `Override Relation` indicate when a method in one class overrides a virtual method in a base class.

### Supported Patterns
```yaml
name: Override
```
#### Syntax: Overrides Declaration
```text
```

##### Examples

######  Override
```cpp
class A{
    public: void func();
}
class B : public A{
    public: void func();
}
```

```yaml
name: Override
relation:
    items:
        -   type: Override
            from: Function:'func'
            to: Function:'func'
            loc: file0:5:18
```

######  Virtual Function Override
```cpp
class A{
    public: virtual void func();
}
class B : public A{
    public: void func();
}
```

```yaml
name: Virtual Function Override
relation:
    items:
        -   type: Override
            from: Function:'func'
            to: Function:'func'
            loc: file0:5:18
```


######  Operator Function Override
```cpp
struct Complex {
   Complex( double r, double i ) : re(r), im(i) {}
   Complex operator+( Complex &other );
};
```

```yaml
name: Operator Function Override
relation:
    items:
        -   type: Override
            from: Function:'Complex::operator+'
            to: Function:'operator+'
            loc: file0:3:12
```


######  Function Override with Override keyword
```cpp
class BaseClass
{
    virtual void funcA();
};
class DerivedClass: public BaseClass
{
    virtual void funcA() override;
}
```

```yaml
name: Operator Function Override
relation:
    items:
        -   type: Override
            from: Function:'BaseClass::funcA'
            to: Function:'DerivedClass::funcA'
            loc: file0:7:18
```
