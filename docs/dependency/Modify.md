# Modify

## Supported pattern
```
name: Modify Declaration
```
A modify indicates a reference where a variable is modified without an explicit assignment statement. The variable is both used and set at the same reference location.

### Syntax: 

#### Examples: 

``` cpp
int function(){
    int i = 0;
    i++;
}
```

``` 
entities:
    items:
        -   id: 0
            name: function
            kind: Function
        -   id: 1
            name: i
            kind: Object
dependencies:
    items:
        -   kind: Modify
            src: 0
            dest: 1
```