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
    - from: Function:'example'
      to: Function:'modifyValue'
      loc: file0:7:5
```

###### Address of Class Member

```CPP
class MyClass {
public:
    int data;
};

void processData(int* dataPtr) {
    // Process data
}

void classExample() {
    MyClass obj;
    processData(&obj.data);
}
```

```yaml
name: Address of Class Member
relation:
  type: Addr Parameter Use
  items:
    - from: Function:'classExample'
      to: Function:'processData'
      loc: file0:10:5
```

###### Array Element Address

```CPP
void processElement(int* elementPtr) {
    // Processing logic
}

void arrayExample() {
    int arr[5] = {1, 2, 3, 4, 5};
    processElement(&arr[2]);
}
```

```yaml
name: Array Element Address
relation:
  type: Addr Parameter Use
  items:
    - from: Function:'arrayExample'
      to: Function:'processElement'
      loc: file0:6:5
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
    - from: Function:'templateExample'
      to: Template:'manipulate'
      loc: file0:7:5
```
