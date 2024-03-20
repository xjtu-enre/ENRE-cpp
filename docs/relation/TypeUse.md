## Relation: TypeUse

Description:`TypeUse Relation` in C++ denotes the usage of a type in various contexts such as variable declarations, function parameters, return types, or casting. It signifies the application of a defined type within the scope of a program.

### Supported Patterns

```yaml
name: TypeUse
```

#### Syntax: TypeUse Declaration

```text
TypeUseDeclaration :
    type-specifier-seq
```

##### Examples

###### Variable Declaration

```CPP
class MyClass {
    // MyClass details
};

void function() {
    MyClass obj;
}
```

```yaml
name: Variable Declaration
relation:
  type: TypeUse
  items:
    - from: Function:'function'
      to: Class:'MyClass'
      loc: file0:6:5
```

###### Function Parameter

```CPP
struct MyStruct {
    // MyStruct details
};

void process(MyStruct s) {
    // process logic
}
```

```yaml
name: Function Parameter
relation:
  type: TypeUse
  items:
    - from: Function:'process'
      to: Struct:'MyStruct'
      loc: file0:6:14
```

###### Return Type

```CPP
enum MyEnum {
    VALUE1, VALUE2
};

MyEnum getStatus() {
    return VALUE1;
}
```

```yaml
name: Return Type
relation:
  type: TypeUse
  items:
    - from: Function:'getStatus'
      to: Enum:'MyEnum'
      loc: file0:5:1
```

###### Casting


```CPP
class AnotherClass {
    // AnotherClass details
};

void example() {
    void* ptr;
    AnotherClass* ac = static_cast<AnotherClass*>(ptr);
}
```

```yaml
name: Casting
relation:
  type: TypeUse
  items:
    - from: Function:'example'
      to: Class:'AnotherClass'
      loc: file0:7:28

```