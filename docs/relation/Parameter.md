## Relation: Parameter
Descriptions: `Parameter Relation`  is between `Function Entity` and `Variable Entity`. Information can be passed to functions as a parameter. Parameters act as variables inside the function.


### Supported Patterns
```yaml
name: Parameter
```
#### Syntax: Parameter Declaration

```text

```
##### Examples

###### Parameter
```cpp
int func(int i){
    return i;
}
```

```yaml
name: Parameter
relation:
    items:
        -   type: Parameter
            loc: file0:1:14
            from: Function:'func'
            to: Variable:'i'
```

###### Array Parameter
```cpp
int func(int array[])
    return -1;
}
```

```yaml
name: Array Parameter
relation:
    items:
        -   type: Parameter
            loc: file0:1:14
            from: Function:'func'
            to: Variable:'array'
```