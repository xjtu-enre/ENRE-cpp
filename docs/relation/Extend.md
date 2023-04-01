## Relation: Extend
Descriptions: New classes can be derived from existing classes using a mechanism called `Inheritance/Extend Relation`. Classes that are used for derivation are called `base classes` of a particular derived class. 

### Supported Patterns
```yaml
name: Extend Declaration
```
#### Syntax: Extend Declaration
```text
attr(optional) access-specifier(optional) virtual-specifier(optional) class-or-decltype		
```

##### Examples

###### Struct Extend 
```cpp
struct Base {
    int a, b, c;
};
struct Derived : Base {
    int b;
};
struct Derived2 : Derived {
    int c;
};
```

```yaml
name: Struct Extend 
relation:
    type: Extend
    extra: false
    items:
        -   from: Struct:'Derived'
            to: Struct:'Base'
            loc: file0:4:19
        -   from: Struct:'Derived2'
            to: Struct:'Derived'
            loc: file0:7:19
```


###### Multi Extend

```cpp
class Collection { };
class Book {};
class CollectionOfBook : public Book, public Collection {};
```
```yaml
name: Multi Extend
relation:
  type: Extend
  extra: false
  items:
    -   from: Class:'CollectionOfBook'
        to: Class:'Book'
        loc: file0:3:7
    -   from: Class:'CollectionOfBook'
        to: Class:'Collection'
        loc: file0:3:7
```

###### Template Struct Extend

```cpp
template<class T>
struct Base<T>{};
struct Derived : public Base<std::string>{};
```
```yaml
name: Template Struct Extend
relation:
  type: Extend
  extra: false
  items:
    -   from: Struct:'Derived'
        to: Template:'Base'
        loc: file0:3:8:3:14
```


###### Template Class Extend

```cpp
template<class T>
class Base<T>{};
class Derived : public Base<std::string>{};
```
```yaml
name: Template Class Extend
relation:
  type: Extend
  extra: false
  items:
    -   from: Class:'Derived'
        to: Template:'Base'
        loc: file0:3:8:3:14
```