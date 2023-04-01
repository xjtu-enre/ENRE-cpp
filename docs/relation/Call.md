## Relation: Call

Descriptions: `Call Relation` is a kind of postfix-expression, formed by an expression that evaluates to a function or callable Variable followed by the function-call operator, ().

### Supported Patterns 

```yaml
name: Call
```

#### Syntax: Call Declaration

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
relation:
  type: Call
  items:
    -   from: Function:'epoll'
        to: Function:'run_benchmark'
        loc: file0:3:5
```

###### Cross-file Function Call

```cpp
//// @ext h
void run_benchmark() {}
class Dog{
public:
    void bark(){}
}
```

```cpp
#include "file0.h"
void epoll(){
    run_benchmark();
    Dog dog;
    dog.bark();
}
```

```yaml
name: Cross-file Function Call
relation:
  type: Call
  items:
    -   from: Function:'epoll'
        to: Function:'run_benchmark'
        loc: file1:3:5
    -   from: Function:'epoll'
        to: Function:'bark'
        loc: file1:5:9
```

###### Method Function Call

```cpp
class dog
{
public:
   void setAge(int age)     
   {
      _age = age;
   }
private:
   int _age;
};
int main()
{
   dog mongrel;
   mongrel.setAge(5);
}
```

```yaml
name: Method Function Call
relation:
  type: Call
  items:
    -   from: Function:'main'
        to: Function:'setAge'
        loc: file0:14:12
```

###### Deref Call
If an Variable can deref as a function, there maybe a deref call.

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
name: Deref Call
relation:
    type: Call
    items:
        -   from: Function:'run_benchmark'
            to: Variable:'setup'
            loc: file0:5:13
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
relation:
  items:
    #-   from: Function:'funcB'
    -   from: Function:'run_benchmark'
        to: Function:'funcA'
        loc: file0:8:11
        type: Call
    -   from: Function:'run_benchmark'
        to: Function:'funcB'
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
relation:
  items:
    -   from: Function:'func'
        to: Function:'func1'
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
relation:
    items:
      -   from: Function:'main'
          to: Function:'func'
          loc: file0:9:10
          type: Call
```

###### Template Function Call Function
```cpp
int func(){
    return 0;
}
template<typename S>
void func2(S &s){
    func();
}
```

```yaml
name: Template Function Call Function
relation:
    items:
      -   from: Template:'func2'
          to: Function:'func'
          loc: file0:6:5:6:8
          type: Call
```

###### Template Call Template
```cpp
template<typename S>
int func(S &s){
    return 0;
}
template<typename S>
void func2(S &s){
    func(s);
}
```

```yaml
name: Template Call Template
relation:
    items:
      -   from: Template:'func2'
          to: Template:'func'
          loc: file0:7:5:7:8
          type: Call
```

###### Function Call Template
```cpp
template<typename S>
int func(S &s){
    return 0;
}
void func2(){
    int s = 0;
    func(s);
}
```

```yaml
name: Function Call Template
relation:
    items:
      -   from: Function:'func2'
          to: Template:'func'
          loc: file0:7:5:7:8
          type: Call
```

###### Operator Function call
```cpp
struct S
{
   operator ()
   {
      return func;
   }
};
int main()
{
   S s;
   s();
}
```

```yaml
name: Operator Function call
relation:
    items:
      -   from: Function:'main'
          to: Function:'operator ()'
          loc: file0:11:4
          type: Call
```
