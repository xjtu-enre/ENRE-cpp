## Relation: Call

Descriptions: `Call Relation` can be from 'File', 'Namespace', 'Function', 'Struct', 'Template',  'Class' entity and terminate at 'Function', 'Parameter',  'Object', 'Template', 'Class' entity.


### Supported Patterns 

```yaml
name: Call
```

#### Syntax: Call declaration

```text
CallDeclaration :
    postfix-expression ( argument-expression-list opt )
```

##### Examples

###### Function Call

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
        -   name: epoll
            loc: [ 1, 6 ]
            type: Function
        -   name: run_benchmark
            loc: [ 4, 6 ]
            type: Function
relation:
    items:
        -   from: Function:'epoll'
            to: Function:'run_benchmark'
            loc: file0:3:5
            type: Call
```

###### Deref Call
If an object can deref as a function, there maybe a deref call.

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
        -   name: run_benchmark
            loc: [ 0, 11 ]
            type: Function
        -   name: setup
            loc: [ 1, 15 ]
            type: Object
relation:
    items:
        -   from: Function:run_benchmark
            to: Object:setup
            loc: file0:5:13
            type: Call
```

###### Function Continuous Call
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
    -   name: funcA
        type: Function
    -   name: funcB
        type: Function
    -   name: run_benchmark
        type: Function
relation:
  items:
    -   from: Function:funcB
        to: Function:funcA
        loc: file0:8:11
        type: Call
    -   from: Function:run_benchmark
        to: Function:funcB
        loc: file0:8:5
        type: Call
```


###### Function Extern Call

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
    -   name: func1
        type: Function
    -   name: func
        type: Function
relation:
  items:
    -   from: Function:func
        to: Function:func1
        loc: file0:3:5
        type: Call
```

###### Call Class Method Call
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
name: Call Class Method Call
entity:
    items:
        -   name: A
            type: Class
        -   name: A::func
            type: Function
        -   name: main
            type: Function
        -   name: main::test
            type: Variable
relation:
    items:
      -   from: Function:main
          to: Function:func
          loc: file0:9:10
          type: Call
```
