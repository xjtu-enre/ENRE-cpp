# End

## Supported pattern
```
name: End Declaration
```
### Syntax: 

End dependency occur in the 'Object', 'Union', 'Enum', 'Template', 'Struct',  'Class', 'Namespace' and 'Function' entity itself. It represents where the entity definition ends.


#### Examples: 

``` cpp
class class_name{

}
```

``` 
entities:
    filter: Alias
    items:
        -   name: flags
            loc: [ 1, 7 ]
            kind: Alias
dependencies:
    items:
        -   src: class_name
            dest: class_name
            kind: End
            loc: [3, 1]
```
