## Entity: Class
Description: `Class` and `struct` are the constructs whereby you define your own types. Classes and structs can both contain data members and member functions, which enable you to describe the type's state and behavior.

### Supported Patterns

```yaml
name: Class
```
#### Syntax: Class Declaration

```text
class-key attr(optional) class-head-name final(optional) base-clause(optional) { member-specification }	(1)	
class-key attr(optional) class-head-name : base-specifier-list { member-specification } [declarators];	(2)	
```

##### Examples

###### Class
```cpp
class Base { /* empty */};
class Extend : public Base {/* empty */ };
```

```yaml
name: Class
entity:
    items:
        -   name: Base
            loc: 1:1:7:10
            type: Class
        -   name: Extend
            loc: 2:2:7:12
            type: Class
```

###### Class With Object
```cpp
class objClass { /* empty */} obj;
```

```yaml
name: Class With Object
entity:
    items:
        -   name: objClass
            loc: 1:1:7:14
            type: Class
```

###### Anonymous Class
```cpp
class { /* empty */ };   
```

```yaml
    name: Anonymous Class
    entity:
        items:
            -   name: <Anonymous as="Class">
                loc: 1:7:1:7
                type: Class
```

###### Nested Class
```cpp
class enclose
{
    class nested // private member
    {
    };
};
```

```yaml
    name: Nested Class
    entity:
        items:
            -   name: enclose
                loc: 1:7:1:13
                type: Class
            -   name: nested
                loc: 3:11:3:16
                type: Class
```

###### Class with final specifier
```cpp
class Vector final {
};
```

```yaml
    name: Class with final specifier
    entity:
        items:
            -   name: Vector
                loc: 1:7:1:12
                type: Class
```

###### Class with private-public-protected
```cpp
class ExampleClass {
public:
    int publicVar;
    void publicMethod();

private:
    int privateVar;
    void privateMethod();

protected:
    int protectedVar;
    void protectedMethod();
};

void ExampleClass::publicMethod() {
    // implementation code for public method
}

void ExampleClass::privateMethod() {
    // implementation code for private method
}

void ExampleClass::protectedMethod() {
    // implementation code for protected method
}
```

```yaml
    name: ExampleClass
    entity:
      kind: class
      items:
        -   name: publicVar
            kind: int
            type: variable
            visibility: public
            loc: 3:9:3:17
        -   name: publicMethod
            return_type: void
            type: function
            visibility: public
            loc: 4:10:4:21
        -   name: privateVar
            kind: int
            type: variable
            visibility: private
            loc: 8:9:8:18
        -   name: privateMethod
            return_type: void
            type: function
            visibility: private
            loc: 9:10:9:22
        -   name: protectedVar
            kind: int
            type: variable
            visibility: protected
            loc: 13:9:13:18
        -   name: protectedMethod
            return_type: void
            type: function
            visibility: protected
            loc: 14:10:14:24
```
