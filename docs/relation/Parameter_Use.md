## Relation: Parameter_Use

Description:The `Parameter_Use` Relation in C++ refers to the usage of function parameters within the body of the function. It involves the direct use of the parameter's value, without modifications or address referencing.

### Supported Patterns

```yaml
name: Parameter Use
```

#### Syntax: Parameter Use Declaration

```text
ParameterUseDeclaration :
    function-body ( parameter )
```

##### Examples

###### Direct Parameter Use

```CPP
void display(int number) {
    cout << "Number: " << number;
}

void callDisplay() {
    int n = 5;
    display(n);
}

```

```yaml
name: Direct Parameter Use
relation:
  type: Parameter Use
  items:
    - from: Function:'display'
      to: Variable:'callDisplay::n'
      loc: file0:7:118

```

###### Template Function Parameter Use

```CPP
template<typename T>
void process(T item) {
    // Process item
}

void templateFunc() {
    int val = 5;
    process(val);
}
```

```yaml
name: Template Function Parameter Use
relation:
  type: Parameter Use
  items:
    - from: Function template:'process'
      to: Variable:'templateFunc::val'
      loc: file0:8:5
```

###### Parameter in Lambda Function


```CPP
void lambdaExample() {
    auto lambda = [](int x) { cout << x * 2; };
    int num = 3;
    lambda(num);
}
```

```yaml
name: Parameter in Lambda Function
relation:
  type: Parameter Use
  items:
    - from: Variable:'lambdaExample::lambda'
      to: Variable:'lambdaExample::num'
      loc: file0:4:5
```
