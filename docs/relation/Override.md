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
```CPP
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
            from: Function:'func'[@loc=5]
            to: Function:'func'[@loc=2]
            loc: file0:5:18
```

######  Virtual Function Override
```CPP
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
            from: Function:'func'[@loc=5]
            to: Function:'func'[@loc=2]
            loc: file0:5:18
```


<!-- ######  Operator Function Override
```CPP
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
``` -->


######  Function Override with Override keyword
```CPP
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
name: Function Override with Override keyword
relation:
    items:
        -   type: Override
            from: Function:'BaseClass::funcA'[@loc=7]
            to: Function:'DerivedClass::funcA'[@loc=3]
            loc: file0:7:18
```
