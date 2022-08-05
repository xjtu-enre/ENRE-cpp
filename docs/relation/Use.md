## Relation: Use
Descriptions: `Use Relation` can be divided into following kinds:
A Use dependency indicates a reference in an active region of code to a known C/C++ variable.

### Supported Patterns
```yaml
name: Use Declaration
```
#### Syntax: Use Declaration

```text
```

##### Examples

###### Ordinary Use

```cpp
extern int var1;
int func() {
    int local_var = var1; // use of var1
}
```

```yaml
name: Use
relation:
    items:
        -   type: Use
            from: Function:func
            to: Object:var1
            loc: file0:3:21
```
###### Deref Use
```cpp
int func(int i){
    int a,*b=0;
    a = *b; 
}
```

```yaml
name: Deref Use
relation:
    items:
        -   type: Use
            loc: file0:3:10
            from: Function:func
            to: Variable:b
```
###### Class Member Use
```cpp
class A{
public:
    int x;
    int func(){
        return x;
    }
}
```

```yaml
name: Class Member Use
relation:
    items:
        -   type: Use
            loc: file0:5:16
            from: Function:func
            to: Variable:x
```