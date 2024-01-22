## Entity: Function

Description: A `Function Entity` is a block of code that performs some operation. A function can optionally define input parameters that enable callers to pass arguments into the function. A function can optionally return a value as output.

### Supported Patterns

```yaml
name: Function
```
#### Syntax: Function Declaration

```text
noptr-declarator ( parameter-list ) cv(optional) ref(optional) except(optional) attr(optional)	(1)	
noptr-declarator ( parameter-list ) cv(optional) ref(optional) except(optional) attr(optional) -> trailing	(2)	(since C++11)
[ captures ] ( params ) specs requires(optional) { body }	(1)	
[ captures ] { body }	(2)	(until C++23)
[ captures ] specs { body }	(2)	(since C++23)
[ captures ] < tparams > requires(optional) ( params ) specs requires(optional) { body }	(3)	(since C++20)
[ captures ] < tparams > requires(optional) { body }	(4)	(since C++20)
(until C++23)
[ captures ] < tparams > requires(optional) specs { body }	(4)	(since C++23)

```

##### Examples

###### Function 
```CPP
void func(){ /* empty */ }
```

```yaml
name: Function
entity:
  items:
      -   name: func
          loc: 1:6:1:9
          type: Function
```

###### Function with Parameter
```CPP
void func(int i){ /* empty */ }
```

```yaml
name: Function with Parameter
entity:
  items:
      -   name: func
          loc: 1:6:1:9
          type: Function
```

###### Inline Function
```CPP
inline void func(){ /* empty */ }
```

```yaml
name: Inline Function
entity:
  items:
      -   name: func
          loc: 1:13:1:16
          type: Function
```

###### Struct Method
```CPP
struct Linear {
    int func() {
        return 0;
    }
};
```

```yaml
    name: Struct Method
    entity:
        items:
            -   name: func
                loc: 2:9:9:12
                type: Function
```

###### Functions with Variable Argument Lists
```CPP
void func( char *szTypes, ... ){ /* empty */ }
```

```yaml
name: Functions with Variable Argument Lists
entity:
  items:
      -   name: func
          loc: 1:6:1:9
          type: Function
```

###### Deleted Functions
```CPP
struct deletedFunction
{
  deletedFunction() =default;
  deletedFunction(const noncopyable&) =delete;
};
```

```yaml
name: Deleted Functions
entity:
  items:
      -   name: deletedFunction
          loc: 4:3:4:17
          type: Function
      -   name: deletedFunction
          loc: 3:3:3:17
          type: Function
```

###### Virtual Functions
```CPP
class Account {
public:
    virtual void func(){ /* empty */ }
}
```

```yaml
name: Virtual Functions
entity:
  items:
      -   name: func
          loc: 3:18:3:21
          type: Function
```

###### Constructors
```CPP
class Box {
public:
    // Default constructor
    Box() {}
    // Initialize a Box with equal dimensions (i.e. a cube)
    explicit Box(int i) : m_width(i), m_length(i), m_height(i) // member init list
    {}

    // Initialize a Box with custom dimensions
    Box(int width, int length, int height)
        : m_width(width), m_length(length), m_height(height) {}
}
```

```yaml
name: Constructors
entity:
  items:
      -   name: Box
          loc: 4:5:4:7
          type: Function
      -   name: Box
          loc: 6:14:6:16
          type: Function
      -   name: Box
          loc: 10:5:10:7
          type: Function
```



###### Overload Function
```CPP
int overloadFunction(){
    /* empty */
}
int overloadFunction(int i){
    /* empty */
}
int overloadFunction(char i){
    /* empty */
}
```

```yaml
    name: Overload Function
    entity:
        items:
            -   name: overloadFunction
                type: Function
                loc: 1:5:1:20
            -   name: overloadFunction
                type: Function
                loc: 4:5:4:20
            -   name: overloadFunction
                type: Function
                loc: 7:5:7:20
```

###### Overload Function Declaration

```CPP
int overloadFunction();
int overloadFunction(int i);
int overloadFunction(const int i);
```

```yaml
    name: Overload Function
    entity:
        items:
            -   name: overloadFunction
                type: Function
                loc: 1:5:1:20
            -   name: overloadFunction
                type: Function
                loc: 2:5:2:20
```

###### Overload Class Method
```CPP
class foo{
public:
    int overloadFunction();
    int overloadFunction(int i);
    int overloadFunction(char i);
}
```

```yaml
    name: Overload Class Method
    entity:
        items:
            -   name: foo::overloadFunction
                type: Function
                kind: Method
                loc: 3:9:3:24
            -   name: foo::overloadFunction
                type: Function
                kind: Method
                loc: 4:9:4:24
            -   name: foo::overloadFunction
                type: Function
                kind: Method
                loc: 5:9:5:24
```
