## Relation: Return

Descriptions: `Return Relation` identifies the relationship between a function or method and the type or value it returns.

### Supported Patterns

```yaml
name: Return
```

#### Syntax: Return Declaration

```text
ReturnDeclaration :
    FunctionOrMethod 'returns' ReturnType
```

##### Examples

###### Function Returns Primitive Type

```CPP
int myFunction() {
    return 10;
}
```

```yaml
name: Function Returns Primitive Type
relation:
  type: Return
  items:
    -   from: Function:'myFunction'
        to: Type:'int'
        loc: file0:1:1
```

###### Method Returns Class Type

```CPP
class MyClass {
public:
    MyClass getObject() {
        return MyClass();
    }
};
```

```yaml
name: Method Returns Class Type
relation:
  type: Return
  items:
    -   from: Method:'getObject'
        to: Class:'MyClass'
        loc: file0:3:5
```

###### Template Function Returns Template Type

```CPP
template <typename T>
T getTemplateObject() {
    return T();
}
```

```yaml
name: Template Function Returns Template Type
relation:
  type: Return
  items:
    -   from: Template:'getTemplateObject'
        to: Type:'T'
        loc: file0:2:1
```

###### Function Returns Void

```CPP
void doSomething() {
    // Some code
}
```

```yaml
name: Function Returns Void
relation:
  type: Return
  items:
    -   from: Function:'doSomething'
        to: Type:'void'
        loc: file0:1:1
```

###### Method Returns Pointer Type
```CPP
class MyClass {
public:
    int* getPointer() {
        return new int(10);
    }
};
```

```yaml
name: Method Returns Pointer Type
relation:
  type: Return
  items:
    -   from: Method:'getPointer'
        to: Type:'int*'
        loc: file0:3:5
```
