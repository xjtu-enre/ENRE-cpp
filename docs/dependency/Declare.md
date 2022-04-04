# Declare


## Supported pattern
```
name: Declare Declaration
```
### Syntax: 


#### Examples: 

``` cpp
# in file.h
class Units{
public:
    int rowCount();
};
# in file.cpp
int Units::rowCount() {}
```

``` 
entities:
    items:
        -   id: 0
            name: Units
            loc: [ file.h, 7, 11]
            kind: Class
        -   id: 1
            name: Units::run_benchmark
            loc: [ file.cpp, 5, 19 ]
            kind: Function
dependencies:
    items:
        -   kind: Declare
            src: 0
            dest: 1
            loc: [file.h, 9, 16]
```