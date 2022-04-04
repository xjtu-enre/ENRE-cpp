# Call


## Supported pattern
```
name: Call Declaration
```
### Syntax: 
Call dependency can be from 'File', 'Namespace', 'Function', 'Struct', 'Template',  'Class' entities and terminate at 'Function', 'Parameter',  'Object', 'Template', 'Class' entities.

#### Examples: 

``` cpp
void epoll(){
    run_benchmark();
}
void run_benchmark() {}
```

``` 
entities:
    items:
        -   id: 0
            name: epoll
            loc: [ 1, 6 ]
            kind: Function
        -   id: 1
            name: run_benchmark
            loc: [ 4 6 ]
            kind: Function
dependencies:
    items:
        -   kind: Call
            src: 0
            dest: 1
```


### Syntax: Deref Call
If an object can deref as a function, there maybe a deref call.

#### Examples: 
``` cpp
void run_benchmark(void (*setup)(void*), void* data) {
    int i;
    for (i = 0; i < count; i++) {
        if (setup != NULL) {
            setup(data);
        }
    }
}
```

``` 
entities:
    items:
        -   id: 0
            name: run_benchmark
            loc: [ 0, 11 ]
            kind: Function
        -   id: 1
            name: setup
            loc: [ 1, 15 ]
            kind: Object
dependencies:
    items:
        -   kind: Deref Call
            src: 0
            dest: 1
```