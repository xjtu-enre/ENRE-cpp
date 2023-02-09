## Entity:  Struct

Description: Classes and structs are the constructs whereby you define your own types. Classes and structs can both contain data members and member functions, which enable you to describe the type's state and behavior.
### Supported Patterns

```yaml
name: Struct
```
#### Syntax: Struct Declaration

```text
struct-key attr struct-head-name { member-specification }	(1)	
struct-key attr struct-head-name : base-specifier-list { member-specification }	(2)	
```

##### Examples

###### Struct
```cpp
struct MyStruct {
    int value;
};
```
```yaml
name: Struct
entity:
    items:
        -   name: MyStruct
            loc: 1:8:1:15
            type: Struct
```

###### Anonymous Struct
```cpp
struct{
    int value;
};
```
```yaml
name: Anonymous Struct
entity:
    items:
        -   name: <Anonymous as="Struct">
            loc: 1:0:1:0
            type: Struct
```

###### Struct use macro
```cpp
#define Mystruct struct 
Mystruct cell{
    int value;
};
```
```yaml
name: Struct use macro
entity:
    items:
        -   name: cell
            loc: 2:10:2:13
            type: Struct
```