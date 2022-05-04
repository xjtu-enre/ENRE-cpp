# Set

## Supported pattern
```yaml
name: Set Declaration
```
A set dependency indicates any explicit assignment of a variable.

### Syntax:


#### Examples: 

```cpp
int function(int i){
    int j;
    j = i;
}
```

```yaml
name: Set
entities:
    items:
        -   id: 0
            name: function
            category: Function
        -   id: 1
            name: i
            category: Object
        -   id: 2
            name: j
            category: Object
dependencies:
    items:
        -   category: Set
            src: 0
            dest: 2
        -   category: Use
            src: 0
            dest: 1
```