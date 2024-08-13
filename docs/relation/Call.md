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

```CPP
void run_benchmark() {}
void epoll(){
    run_benchmark();
}
```

```yaml
name: Function Call
relation:
  type: Calls
  items:
    -   from: Function:'epoll'
        to: Function:'run_benchmark'
        loc: file0:3:5
```

###### Cross-file Function Call

```CPP
//// @ext h
void run_benchmark() {}
class Dog{
public:
    void bark(){}
}
```

```CPP
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
  type: Calls
  items:
    -   from: Function:'epoll'
        to: Function:'run_benchmark'
        loc: file1:3:5
    -   from: Function:'epoll'
        to: Function:'Dog::bark'
        loc: file1:5:9
```

###### Method Function Call

```CPP
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
  type: Calls
  items:
    -   from: Function:'main'
        to: Function:'setAge'
        loc: file0:14:12
```


###### Function Continuous Call
```CPP
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
    type: Calls
    items:
    #-   from: Function:'funcB'
    -   from: Function:'run_benchmark'
        to: Function:'funcA'
        loc: file0:8:11
        type: Calls
    -   from: Function:'run_benchmark'
        to: Function:'funcB'
        loc: file0:8:5

```


###### Function Extern Call

```CPP
extern int func1(void);
int func() {
    func1();
}
```

```yaml
name: Function Extern Call
relation:
    type: Calls
    items:
    -   from: Function:'func'
        to: Function:'func1'
        loc: file0:3:5

```

###### Call Class Method Call
```CPP
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
    type: Calls
    items:
      -   from: Function:'main'
          to: Function:'A::func'
          loc: file0:9:10

```

###### Template Function Call Function
```CPP
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
    type: Calls
    items:
      -   from: Template:'func2'
          to: Function:'func'
          loc: file0:6:5:6:8

```

###### Template Call Template
```CPP
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
    type: Calls
    items:
      -   from: Template:'func2'
          to: Template:'func'
          loc: file0:7:5:7:8

```

###### Function Call Template
```CPP
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
    type: Calls
    items:
      -   from: Function:'func2'
          to: Template:'func'
          loc: file0:7:5:7:8

```

###### Operator Function call
```CPP
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
    type: Calls
    items:
      -   from: Function:'main'
          to: Function:'operator ()'
          loc: file0:11:4

```
