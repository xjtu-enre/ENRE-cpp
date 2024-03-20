## Relation: Type

Description:The `Type Relation` in C++ is used to express the type association between a variable and its data type, or a function and its return type. This relation is crucial for understanding how data is structured and manipulated within the code.

### Supported Patterns

```yaml
name: Type
```

#### Syntax: Type Declaration

```text
TypeRelationDeclaration :
    variable-declaration ( variable : type )
    function-declaration ( function : return-type )
```

##### Examples

###### Variable Type Relation

```CPP
int main() {
    int count = 0;
    double price = 10.99;
}
```

```yaml
name: Variable Type Relation
relation:
  type: Type
  items:
    - from: Variable:'count'
      to: Type:'int'
      loc: file0:2:5
    - from: Variable:'price'
      to: Type:'double'
      loc: file0:3:12
```

###### Function Return Type Relation

```CPP
int calculateSum(int a, int b) {
    return a + b;
}

double getArea(double radius) {
    return 3.14 * radius * radius;
}
```

```yaml
name: Function Return Type Relation
relation:
  type: Type
  items:
    - from: Function:'calculateSum'
      to: Type:'int'
      loc: file0:1:1
    - from: Function:'getArea'
      to: Type:'double'
      loc: file0:5:1
```

###### Template Variable Type Relation

```CPP
template<typename T>
void processValue(T value) {
    T result = value * 2;
}
```

```yaml
name: Template Variable Type Relation
relation:
  type: Type
  items:
    - from: Variable:'result'
      to: Template:'T'
      loc: file0:3:5
```

###### Class Member Type Relation


```CPP
class Student {
public:
    int age;
    std::string name;
};

```

```yaml
name: Class Member Type Relation
relation:
  type: Type
  items:
    - from: Field:'age'
      to: Type:'int'
      loc: file0:3:5
    - from: Field:'name'
      to: Type:'std::string'
      loc: file0:4:16
```