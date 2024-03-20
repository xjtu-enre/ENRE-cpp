## Relation: Parameter Use Field Reference

Description:The `Parameter Use Field Reference` Relation in C++ captures the usage of a class or struct member field through a parameter within a function. This relation indicates that the parameter is utilized to access and potentially modify the member fields of a class or struct instance.

### Supported Patterns

```yaml
name: Parameter Use Field Reference
```

#### Syntax: Parameter Use Field Reference Declaration

```text
ParameterUseFieldReferenceDeclaration :
    function-body ( parameter.field )
```

##### Examples

###### Direct Field Reference from Parameter

```CPP
class Student {
public:
    int age;
};

void setAge(Student& student, int newAge) {
    student.age = newAge;
}

void demo() {
    Student s;
    setAge(s, 20);
}
```

```yaml
name: Direct Field Reference from Parameter
relation:
  type: Parameter Use Field Reference
  items:
    - from: Function:'setAge'
      to: Field:'age'
      loc: file0:5:5
```

###### Field Reference in Member Function

```CPP
class Car {
public:
    int speed;

    void setSpeed(int s) {
        speed = s;
    }
};

void testCar() {
    Car myCar;
    myCar.setSpeed(60);
}
```

```yaml
name: Field Reference in Member Function
relation:
  type: Parameter Use Field Reference
  items:
    - from: Function:'setSpeed'
      to: Field:'speed'
      loc: file0:6:5
```

###### Template Class Field Reference

```CPP
template<typename T>
class Box {
public:
    T content;
};

template<typename T>
void fillBox(Box<T>& box, T item) {
    box.content = item;
}

void useBox() {
    Box<int> intBox;
    fillBox(intBox, 100);
}
```

```yaml
name: Template Class Field Reference
relation:
  type: Parameter Use Field Reference
  items:
    - from: Template:'fillBox'
      to: Field:'content'
      loc: file0:8:5
```

###### Field Reference in Lambda Function


```CPP
class Device {
public:
    int batteryLevel;
};

void checkBattery(Device& device) {
    auto batteryCheck = [&device]() {
        if (device.batteryLevel < 20) {
            cout << "Battery low";
        }
    };
    batteryCheck();
}
```

```yaml
name: Field Reference in Lambda Function
relation:
  type: Parameter Use Field Reference
  items:
    - from: Function:'checkBattery'
      to: Field:'batteryLevel'
      loc: file0:5:9
```