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
```CPP
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
            to: Variable:'func::i'
            loc: file0:3:5
```

######  One’s Compliment Modify
```CPP
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
            to: Variable:'func::i'
            loc: file0:3:6
```


######  Increment Deref Modify
```CPP
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
```CPP
int i = 0;
int j = i++;
```

```yaml
name: File Modify
relation:
    type: Modify
    extra: false
    items:
        -   from: File:'file0.CPP'
            to: Variable:'i'
            loc: file0:2:9
```

######  Template Modify
```CPP
template<typename T>
void func(){
    int i = 0;
    i++;
}
```

```yaml
name: Template Modify
relation:
    type: Modify
    extra: false
    items:
        -   from: Template:'func'
            to: Variable:'i'
            loc: file0:4:5
```


######  Modify in for loop
```CPP
void main(){
    for(int i=0;i<10;i++){}
}
```

```yaml
name: Modify in for loop
relation:
    type: Modify
    extra: false
    items:
        -   from: Function:'main'
            to: Variable:'i'
            loc: file0:2:22
```