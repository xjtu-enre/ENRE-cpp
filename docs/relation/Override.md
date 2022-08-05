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
    public: virtual void func();
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
            from: Function:func
            to: Function:func
            loc: file0:5:18
```