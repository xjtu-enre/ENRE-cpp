# Modify

## Supported pattern
```yaml
name: Modify Declaration
```
A modify indicates a reference where a variable is modified without an explicit assignment statement. The variable is both used and set at the same reference location.

### Syntax: 

#### Examples: 

- Example1
```cpp
int function(){
    int i = 0;
    i++;
}
```

```yaml
name: Modify 1
entity:
    items:
        -   id: 0
            name: function
            category: Function
        -   id: 1
            name: i
            category: Variable
relation:
    items:
        -   category: Modify
            src: 0
            dest: 1
            r:
                d: x
                e: .
                s: x
                u: .
```

- Example2
```cpp
int function(){
    int i = 0;
    ~i;
}
```

```yaml
name: Modify 2
entity:
    items:
        -   id: 0
            name: function
            category: Function
        -   id: 1
            name: i
            category: Variable
relation:
    items:
        -   category: Modify
            src: 0
            dest: 1
            r:
                d: x
                e: x
                s: x
                u: x
```


- Example3
```cpp
int main(){
    int *a,*b=0;
    ++*a; // deref modify of a
}

```

```yaml
name: Modify 3
entity:
    items:
        -   id: 0
            name: main
            category: Function
        -   id: 1
            name: a
            category: Variable
        -   id: 2
            name: b
            category: Variable
relation:
    items:
        -   category: Modify
            src: 0
            dest: 1
            r:
                d: x
                e: .
                s: x
                u: .
```
