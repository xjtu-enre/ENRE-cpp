# Call


## Supported pattern
```yaml
name: Call Declaration
```
### Syntax: 
Call dependency can be from 'File', 'Namespace', 'Function', 'Struct', 'Template',  'Class' entities and terminate at 'Function', 'Parameter',  'Object', 'Template', 'Class' entities.

#### Examples: 

```cpp
void epoll(){
    run_benchmark();
}
void run_benchmark() {}
```

```yaml
name: Function Call
entities:
    items:
        -   id: 0
            name: epoll
            loc: [ 1, 6 ]
            category: Function
        -   id: 1
            name: run_benchmark
            loc: [ 4, 6 ]
            category: Function
dependencies:
    items:
        -   category: Call
            src: 0
            dest: 1
```


### Syntax: Deref Call
If an object can deref as a function, there maybe a deref call.

#### Examples: 
```cpp
void run_benchmark(void (*setup)(void*), void* data) {
    int i;
    for (i = 0; i < count; i++) {
        if (setup != NULL) {
            setup(data);
        }
    }
}
```

```yaml
name: Function Deref Call
entities:
    items:
        -   id: 0
            name: run_benchmark
            loc: [ 0, 11 ]
            category: Function
        -   id: 1
            name: setup
            loc: [ 1, 15 ]
            category: Object
dependencies:
    items:
        -   category: Deref Call
            src: 0
            dest: 1
```