# Friend
The friend declaration appears in a class body and grants a function or another class access to private and protected members of the class where the friend declaration appears.

## Supported pattern
```yaml
name: Friend Declaration
```
### Syntax: 
```text
friend function-declaration	(1)	
friend function-definition	(2)	
friend elaborated-class-specifier ;	(3)

friend simple-type-specifier ;
friend typename-specifier ;         (4)	(since C++11)
```
Type alias is a name that refers to a previously defined type (similar to typedef).

#### Examples: 

- Example1
```cpp
class classA
{
public:
    void funcA(){
        printf("base class function!");
    }
};

class classB : classA
{
};

class classC{
    friend void classA::funcA();
    friend classB;
};
```

```yaml
name: Friend Function
entity:
    items:
        -   id: 0
            name: classA
            category: Class
        -   id: 1
            name: classA::funcA
            category: Function
        -   id: 2
            name: classA::classB
            category: Class
        -   id: 3
            name: classC
            category: Class
relation:
    items:
        -   category: Friend
            src: 3
            dest: 2
            r:
                d: x
                e: .
                s: x
                u: .
        -   category: Friend
            src: 3
            dest: 1
            r:
                d: x
                e: .
                s: x
                u: x
        -   category: Extend
            src: 2
            dest: 0
            r:
                d: x
                e: .
                s: Inheritance
                u: Public Base
```

### Syntax: Template friends

#### Examples: 

- Example2
```cpp
template<class T> class A {}; // primary
template<> class A<int> {}; // full
class X {
    friend class A<int>; 
};
```

```yaml
name: Friend Class
entity:
    items:
        -   id: 0
            name: A
            category: Class Template
        -   id: 1
            name: A
            category: Class Template Specialization
        -   id: 2
            name: X
            category: Class
relation:
    items:
        -   category: Template Specialization
            src: 0
            dest: 1
            r:
                d: x
                e: x
                s: .
                u: x
        -   category: Friend
            src: 2
            dest: 1
            r:
                d: x
                e: x
                s: xtypeuse
                u: .
```

# Reference
- https://en.cppreference.com/w/cpp/language/friend