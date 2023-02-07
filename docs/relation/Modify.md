## Relation: Modify
Descriptions: `Modify Relation` indicates a reference where a variable is modified without an explicit assignment statement. The variable is both used and set at the same reference location.

### Supported Patterns
```yaml
name: Modify
```

#### Syntax: Modify Declaration
```text

```

##### Examples

######  Increment Modify
```cpp
int func(){
    int i = 0;
    i++;
}
```

```yaml
name: Increment Modify
relation:
    items:
        -   type: Modify
            from: Function:'func'
            to: Variable:'i'
            loc: file0:3:5
```

######  One’s Compliment Modify
```cpp
int func(){
    int i = 0;
    ~i;
}
```

```yaml
name: One’s Compliment Modify
relation:
    items:
        -   type: Modify
            from: Function:'func'
            to: Variable:'i'
            loc: file0:3:6
```


######  Increment Deref Modify
```cpp
int main(){
    int *a,*b=0;
    ++*a; // deref modify of a
}
```

```yaml
name: Increment Deref Modify
relation:
    items:
        -   type: Modify
            from: Function:'main'
            to: Variable:'a'
            loc: file0:3:8
```

######  File Modify
```cpp
int i = 0;
int j = i++;
```

```yaml
name: File Modify
relation:
    items:
        -   type: Modify
            from: File:'file0'
            to: Variable:'i'
            loc: file0:2:9
```

######  Template Modify
```cpp
template<typename T>
void func(){
    int i = 0;
    i++;
}
```

```yaml
name: Template Modify
relation:
    items:
        -   type: Modify
            from: Template:'func'
            to: Variable:'i'
            loc: file0:4:5
```


######  Modify in for loop
```cpp
void main(){
    for(int i=0;i<10;i++){}
}
```

```yaml
name: Template Modify
relation:
    items:
        -   type: Modify
            from: Function:'main'
            to: Variable:'i'
            loc: file0:2:22
```