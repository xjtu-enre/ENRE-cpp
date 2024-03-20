## Relation: Flow To

Description:The `Flow To Relation` in C++ is used to represent the flow of data or control from one entity to another. It is especially relevant in the context of data flow analysis, where understanding how data moves through a program is crucial.

### Supported Patterns

```yaml
name: Flow To
```

#### Syntax: Flow to Declaration

```text
FlowToRelationDeclaration :
    source-entity ( variable, function, etc. )
    flow-to-entity ( variable, function, etc. )
```

##### Examples

###### Variable to Variable Flow

```CPP
int main() {
    int a = 5;
    int b = a;  // Flow from 'a' to 'b'
}
```

```yaml
name: Variable to Variable Flow
relation:
  type: Flow To
  items:
    - from: Variable:'a'
      to: Variable:'b'
      loc: file0:3:9
```

###### Function Argument to Variable Flow

```CPP
void setValue(int &value) {
    int localValue = value; // Flow from 'value' argument to 'localValue'
}

int main() {
    int num = 10;
    setValue(num);
}
```

```yaml
name: Function Argument to Variable Flow
relation:
  type: Flow To
  items:
    - from: Parameter:'value'
      to: Variable:'localValue'
      loc: file0:2:17
```

###### Return Value to Variable Flow

```CPP
int getValue() {
    return 42;
}

int main() {
    int val = getValue(); // Flow from 'getValue()' to 'val'
}
```

```yaml
name: Return Value to Variable Flow
relation:
  type: Flow To
  items:
    - from: Function:'getValue'
      to: Variable:'val'
      loc: file0:5:13
```

###### Template Parameter to Class Member Flow


```CPP
template<typename T>
class Container {
    T data;
    void setData(T newData) {
        data = newData; // Flow from 'newData' to 'data'
    }
};
```

```yaml
name: Template Parameter to Class Member Flow
relation:
  type: Flow To
  items:
    - from: Parameter:'newData'
      to: Field:'data'
      loc: file0:4:9
```