## Relation: Use
Descriptions: A `Use Relation`indicates a reference in an active region of code to a known C/C++ variable.

### Supported Patterns
```yaml
name: Use Declaration
```
#### Syntax: Use Declaration

```text
```

##### Examples

###### Ordinary Use

```CPP
extern int var1;
int func() {
    int local_var = var1; // use of var1
}
```

```yaml
name: Ordinary Use
relation:
    items:
        -   type: Use
            from: Function:'func'
            to: Variable:'var1'
            loc: file0:3:21
```
###### Deref Use
```CPP
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
            from: Function:'func'
            to: Variable:'b'
```
###### Class Member Use
```CPP
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
            from: Function:'func'
            to: Variable:'x'
```


###### Class Use
```CPP
class A{ /* empty */ }
int main(){
    A a;
}
```

```yaml
name: Class Use
relation:
    items:
        -   type: Use
            loc: file0:3:5;
            from: Function:'main'
            to: Class:'A'
```


###### Function Use Macro
```CPP
#define dou double
int main(){
    dou number;
}
```

```yaml
name: Function Use Macro
relation:
    items:
        -   type: Use
            loc: file0:3:5;
            from: Function:'main'
            to: Macro:'dou'
```




