## Relation: Parameter Use

Description:The `Parameter Use` Relation in C++ refers to the usage of function parameters within the body of the function. It involves the direct use of the parameter's value, without modifications or address referencing.

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
    - from: Function:'callDisplay'
      to: Function:'display'
      loc: file0:6:5

```

###### Member Function Parameter Use

```CPP
class Calculator {
public:
    void add(int a, int b) {
        cout << "Sum: " << a + b;
    }
};

void useCalculator() {
    Calculator calc;
    calc.add(10, 20);
}
```

```yaml
name: Member Function Parameter Use
relation:
  type: Parameter Use
  items:
    - from: Function:'useCalculator'
      to: Function:'add'
      loc: file0:9:5
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
    - from: Function:'templateFunc'
      to: Template:'process'
      loc: file0:7:5
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
    - from: Function:'lambdaExample'
      to: Variable:'lambda'
      loc: file0:4:5
```