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
```CPP
class Base { /* empty */};
class Extend : public Base {/* empty */ };
```

```yaml
name: Class
entity:
  items:
    -   name: Base
        loc: 1:7:1:10
        type: Class
    -   name: Extend
        loc: 2:7:2:12
        type: Class
```

###### Class With Object
```CPP
class objClass { /* empty */} obj;
```

```yaml
name: Class With Object
entity:
  items:
    -   name: objClass
        loc: 1:7:1:14
        type: Class
```

###### Anonymous Class
```CPP
class { /* empty */ };   
```

```yaml
    name: Anonymous Class
    entity:
      items:
        -   name: unnamed
            loc: 1:7:1:7
            type: Class
```

###### Nested Class
```CPP
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
        -   name: enclose::nested
            loc: 3:11:3:16
            type: Class
        # Class but not nested Class
        -   name: enclose
            loc: 1:7:1:13
            type: Class
```

###### Class with final specifier
```CPP
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

###### Class with private
```CPP
class ExampleClass {
private:
    int privateVar;
    void privateMethod();
};

void ExampleClass::privateMethod() {
    // implementation code for private method
}

```

```yaml
    name: Class with private
    entity:
      items:
        -   name: ExampleClass::privateVar
            kind: int
            type: variable
            visibility: private
            loc: 7:9:7:18
        -   name: ExampleClass::privateMethod
            return_type: void
            type: function
            visibility: private
            loc: 8:10:8:22
```

###### Class with public
```CPP
class ExampleClass {
public:
    int publicVar;
    void publicMethod();
};

void ExampleClass::publicMethod() {
    // implementation code for public method
}
```

```yaml
    name: Class with public
    entity:
      items:
        -   name: ExampleClass::publicVar
            kind: int
            type: variable
            visibility: public
            loc: 3:9:3:17
        -   name: ExampleClass::publicMethod
            return_type: void
            type: function
            visibility: public
            loc: 4:10:4:21
```

###### Class with protected
```CPP
class ExampleClass {
protected:
    int protectedVar;
    void protectedMethod();
};

void ExampleClass::protectedMethod() {
    // implementation code for protected method
}
```

```yaml
    name: Class with protected
    entity:
      items:
        -   name: ExampleClass::protectedVar
            kind: int
            type: variable
            visibility: protected
            loc: 11:9:11:18
        -   name: ExampleClass::protectedMethod
            return_type: void
            type: function
            visibility: protected
            loc: 12:10:12:24
```

