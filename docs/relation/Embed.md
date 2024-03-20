## Relation: Embed

Description:`Embed Relation` in C++ signifies the inclusion of one type or entity within another, typically representing composition or a strong association between classes or structures.

### Supported Patterns

```yaml
name: Embed
```

#### Syntax: Embed Declaration

```text
EmbedDeclaration :
    type-specifier-seq declarator
```

##### Examples

###### Simple Embedding

```CPP
class Engine {
    // Engine class details
};

class Car {
    Engine engine;
};
```

```yaml
name: Simple Embedding
relation:
  type: Embed
  items:
    - from: Class:'Car'
      to: Class:'Engine'
      loc: file0:6:12
```

###### Nested Embedding

```CPP
struct Wheel {
    // Wheel struct details
};

class Vehicle {
    struct Chassis {
        Wheel wheel;
    } chassis;
};
```

```yaml
name: Nested Embedding
relation:
  type: Embed
  items:
    - from: Struct:'Vehicle::Chassis'
      to: Struct:'Wheel'
      loc: file0:7:16
```

###### Embedding in Template Class

```CPP
template <typename T>
class Container {
    T content;
};
```

```yaml
name: Embedding in Template Class
relation:
  type: Embed
  items:
    - from: Template:'Container'
      to: Template Parameter:'T'
      loc: file0:3:7
```

###### Embedding with Pointer


```CPP
class Battery {
    // Battery class details
};

class ElectronicDevice {
    Battery* battery;
};
```

```yaml
name: Embedding with Pointer
relation:
  type: Embed
  items:
    - from: Class:'ElectronicDevice'
      to: Class:'Battery'
      loc: file0:6:14
```

###### Embedding an Array
```CPP
class Sensor {
    // Sensor class details
};

class ControlUnit {
    Sensor sensors[5];
};
```

```yaml
name: Embedding an Array
relation:
  type: Embed
  items:
    - from: Class:'ControlUnit'
      to: Class:'Sensor'
      loc: file0:6:13
```


###### Embedding with Smart Pointers
```CPP
#include <memory>
class Processor {
    // Processor class details
};

class Computer {
    std::unique_ptr<Processor> processor;
};
```

```yaml
name: Embedding with Smart Pointers
relation:
  type: Embed
  items:
    - from: Class:'Computer'
      to: Class:'Processor'
      loc: file0:7:31
```