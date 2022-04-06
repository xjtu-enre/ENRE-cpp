# Set

## Supported pattern
```
name: Set Declaration
```
A set dependency indicates any explicit assignment of a variable.

### Syntax:


#### Examples: 

``` cpp
int function(int i){
    int j;
    j = i;
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
        -   id: 2
            name: j
            kind: Object
dependencies:
    items:
        -   kind: Set
            src: 0
            dest: 2
        -   kind: Use
            src: 0
            dest: 1
```