## Relation: Friend

Descriptions: The `Friend Relation` declaration appears in a class body and grants a function or another class access to private and protected members of the class where the friend declaration appears.

### Supported Patterns
```yaml
name: Friend
```

#### Syntax: Friend Declaration
```text
friend function-declaration	(1)	
friend function-definition	(2)	
friend elaborated-class-specifier ;	(3)

friend simple-type-specifier ;
friend typename-specifier ;         (4)	(since C++11)
```

##### Examples

###### Friend Function

```cpp
class classA{
public:
    void funcA(){
        printf("base class function!");
    }
};
class classB : classA{};
class classC{
    friend void classA::funcA();
    friend classB;
};
```

```yaml
name: Friend Function
relation:
    type: Friend
    items:
        -   from: Class:'classC'
            to: Function:'funcA'
            loc: file0:9:17
        -   from: Class:'classC'
            to: Class:'classB'
            loc: file0:10:12
```

###### Template Class friends

```cpp
template<class T> class A {}; // primary
template<> class A<int> {}; // full
class X {
    friend class A<int>; 
};
```

```yaml
name: Template Class friends
relation:
    type: Friend
    items:
        -   from: Class:'X'
            to: Template:'A'
            loc: file0:4:18
```

###### Template Function friends

```cpp
class classA{
public:
    void funcA(){
        printf("base class function!");
    }
};
template<class T> class A {
    friend void classA::funcA();
}; 
```

```yaml
name: Template Function friends
relation:
    type: Friend
    items:
        -   from: Template:'A'
            to: Function:'funcA'
            loc: file0:4:18
```

###### Struct Function Friends

```cpp
struct X {
    friend void f() {}
};
void f();
```

```yaml
name: Struct Function Friends
relation:
    type: Friend
    items:
        -   from: Struct:'X'
            to: Function:'f'
            loc: file0:4:6
```
###### Struct Class Friends

```cpp
class c{};
struct X {
    friend class c;
};
```

```yaml
name: Struct Class Friends
relation:
    type: Friend
    items:
        -   from: Struct:'X'
            to: Class:'c'
            loc: file0:3:18
```
