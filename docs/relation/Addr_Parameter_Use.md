## Relation: Addr Parameter Use

Descriptions: The `Addr Parameter Use` Relation in C++ is used to represent the passing of an object's address as a parameter to a function. This is commonly seen with pointer parameters, where a function requires a reference to an object rather than the object itself.

### Supported Patterns

```yaml
name: Addr Parameter Use
```

#### Syntax: Addr Parameter Use Declaration

```text
AddrParameterUseDeclaration :
    function-call ( &object )
```

##### Examples

###### Passing Object Address

```CPP
void modifyValue(int* ptr) {
    *ptr = 10;
}

void example() {
    int value = 5;
    modifyValue(&value);
}
```

```yaml
name: Passing Object Address
relation:
  type: Addr Parameter Use
  items:
    - from: Function:'modifyValue'
      to: Variable:'example::value'
      loc: file0:7:5
```

###### Address in Template Function


```CPP
template <typename T>
void manipulate(T* ptr) {
    // Manipulation logic
}

void templateExample() {
    double num = 3.14;
    manipulate(&num);
}
```

```yaml
name: Address in Template Function
relation:
  type: Addr Parameter Use
  items:
    - from: Function:'manipulate'
      to: Variable:'templateExample::num'
      loc: file0:8:5
```
