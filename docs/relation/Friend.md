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
        -   from: Class:classC
            to: Function:funcA
            loc: file0:9:17
        -   from: Class:classC
            to: Class:classB
            loc: file0:10:12
```

###### Template friends

```cpp
template<class T> class A {}; // primary
template<> class A<int> {}; // full
class X {
    friend class A<int>; 
};
```

```yaml
name: Friend Class
relation:
    type: Friend
    items:
        -   from: Class:X
            to: Template Class:A
            loc: file0:4:18

```