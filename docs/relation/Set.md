## Relation: Set
Descriptions: A `set dependency` indicates any explicit assignment of a variable.

### Supported Patterns
```yaml
name: Set
```

#### Syntax: Set Declaration

```text

```
##### Examples

###### Set

```CPP
int func(int i){
    int j;
    j = i;
}
```

```yaml
name: Set
relation:
    items:
        -   type: Set
            loc: file0:3:5
            from: Function:'func'
            to: Variable:'j'
```

###### Deref Set
```CPP
int func(int i){
    int *a,b=0;
    *a = b; 
}
```

```yaml
name: Deref Set
relation:
    items:
        -   type: Set
            from: Function:'func'
            to: Variable:'a'
            loc: file0:3:6
```
