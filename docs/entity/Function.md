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
```cpp
int func(){
    return 0;
}
```

```yaml
name: Function
entity:
  items:
      -   name: func
          loc: 1:5:1:8
          type: Function
```

###### Struct Method
```cpp
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

###### Overload Function
```cpp
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

```cpp
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
```cpp
class foo{
public:
    int overloadFunction();
    int overloadFunction(int i);
    int overloadFunction(char i);
}
```

```yaml
    name: Overload Function
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
