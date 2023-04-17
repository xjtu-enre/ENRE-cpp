## Relation: Using

Descriptions: `Using Relation` introduces an unqualified name as a synonym for an entity declared elsewhere. It allows a single name from a specific namespace to be used without explicit qualification in the declaration region in which it appears. 
### Supported Patterns

```yaml
name: Using
```

#### Syntax: Using Declaration

```text
using [typename] nested-name-specifier unqualified-id ;
using declarator-list ;
```

##### Examples

###### Function using Namespace

```cpp
namespace ns{}
void func(){
    using namespace ns;
}
```

```yaml
name: Function using Namespace
relation:
  type: Using
  items:
    -   from: Function:'func'
        to: Namespace:'ns'
        loc: file0:3:21:3:22
```

###### File using Namespace

```cpp
namespace ns{}
using namespace ns;
```

```yaml
name: File using Namespace
relation:
  type: Using
  items:
    -   from: File:'file0.cpp'
        to: Namespace:'ns'
        loc: file0:2:17:2:18
```

###### Class Using Function

```cpp
class Base {
private:
    void func() {  }
};
class Derived : private Base {
public:
    using Base::func;
};
```

```yaml
name: Class Using Function
relation:
  type: Using
  items:
    -   from: Class:'Derived'
        to: Function:'func'
        loc: file0:7:17:7:20
```

###### Class Using Variable

```cpp
class Base {
private:
    int value = 0;
};
class Derived : private Base {
public:
    using Base::value;
};
```

```yaml
name: Class Using Variable
relation:
  type: Using
  items:
    -   from: Class:'Derived'
        to: Variable:'value'
        loc: file0:7:17:7:21
```