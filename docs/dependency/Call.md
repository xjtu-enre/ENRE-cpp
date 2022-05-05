# Call


## Supported pattern
```yaml
name: Call Declaration
```
### Syntax: 
Call dependency can be from 'File', 'Namespace', 'Function', 'Struct', 'Template',  'Class' entity and terminate at 'Function', 'Parameter',  'Object', 'Template', 'Class' entity.

#### Examples: 

- Example1
```cpp
void run_benchmark() {}
void epoll(){
    run_benchmark();
}
```

```yaml
name: Function Call
entity:
    items:
        -   id: 0
            name: epoll
            loc: [ 1, 6 ]
            category: Function
        -   id: 1
            name: run_benchmark
            loc: [ 4, 6 ]
            category: Function
relation:
    items:
        -   category: Call
            src: 0
            dest: 1
            r:
                d: .
                e: .
                s: .
                u: .
```


### Syntax: Deref Call
If an object can deref as a function, there maybe a deref call.

#### Examples: 

- Example2
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
entity:
    items:
        -   id: 0
            name: run_benchmark
            loc: [ 0, 11 ]
            category: Function
        -   id: 1
            name: setup
            loc: [ 1, 15 ]
            category: Object
relation:
    items:
        -   category: Deref Call
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
int funcA(){
    return -1;
}
int funcB(int i){
    return 0;
}
void run_benchmark(){
    funcB(funcA());
}
    
```

```yaml
name: Function Continuous Call
entity:
    items:
        -   id: 0
            name: funcA
            category: Function
        -   id: 1
            name: funcB
            category: Function
        -   id: 2
            name: run_benchmark
            category: Function
relation:
    items:
        -   category: Call
            src: 2
            dest: 1
            r:
                d: .
                e: .
                s: .
                u: .
        -   category: Call
            src: 2
            dest: 0
            r:
                d: .
                e: .
                s: .
                u: .
```



- Example4
```cpp
extern int func1(void);
int func() {
    func1();
}
    
```

```yaml
name: Function Extern Call
entity:
    items:
        -   id: 0
            name: func1
            category: Function
        -   id: 1
            name: func
            category: Function
relation:
    items:
        -   category: Call
            src: 1
            dest: 0
            r:
                d: .
                e: .
                s: .
                u: .
```


- Example5
```cpp
class A{
public:
    int func(){
        printf("Print test");
    }
}
int main(){
    A test = new A();
    test.func();
}
```

```yaml
name: Call Class Method
entity:
    items:
        -   id: 0
            name: A
            category: Class
        -   id: 1
            name: A::func
            category: Function
        -   id: 2
            name: main
            category: Function
        -   id: 3
            name: main::test
            category: Variable
relation:
    items:
        -   category: Call
            src: 1
            dest: 2
            r:
                d: .
                e: .
                s: .
                u: .
```
