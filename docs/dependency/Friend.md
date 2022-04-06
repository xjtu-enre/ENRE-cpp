# Friend
The friend declaration appears in a class body and grants a function or another class access to private and protected members of the class where the friend declaration appears.

## Supported pattern
```
name: Friend Declaration
```
### Syntax: 
``` cpp
friend function-declaration	(1)	
friend function-definition	(2)	
friend elaborated-class-specifier ;	(3)

friend simple-type-specifier ;
friend typename-specifier ;         (4)	(since C++11)
```
Type alias is a name that refers to a previously defined type (similar to typedef).

#### Examples: 

``` cpp
class Y {
    int data; // private member
    // the non-member function operator<< will have access to Y's private members
    friend std::ostream& operator<<(std::ostream& out, const Y& o);
    friend char* X::foo(int); // members of other classes can be friends too
    friend X::X(char), X::~X(); // constructors and destructors can be friends
};
// friend declaration does not declare a member function
// this operator<< still needs to be defined, as a non-member
std::ostream& operator<<(std::ostream& out, const Y& y){
    return out << y.data; // can access private member Y::data
}
```

``` 
entities:
    items:
        -   id: 0
            name: Y
            kind: Class
        -   id: 1
            name: Y::operator<<
            kind: Function
        -   id: 2
            name: X::foo
            kind: Function
        -   id: 3
            name: X::X
            kind: Function(constructor)
        -   id: 4
            name: X::~X
            kind: Function(destructor)
dependencies:
    items:
        -   kind: Friend
            src: 0
            dest: 1
        -   kind: Friend
            src: 0
            dest: 2
        -   kind: Friend
            src: 0
            dest: 3
        -   kind: Friend
            src: 0
            dest: 4
```

### Syntax: Template friends

#### Examples: 

``` cpp
template<class T> class A {}; // primary
template<> class A<int> {}; // full
class X {
    friend class A<int>; 
};
```

``` 
entities:
    items:
        -   id: 0
            name: A
            kind: Class Template
        -   id: 1
            name: A
            kind: Class Template Specoalization
        -   id: 2
            name: X
            kind: Class
dependencies:
    items:
        -   kind: Specoalization
            src: 0
            dest: 1
        -   kind: Friend
            src: 2
            dest: 1
```

# Reference
- https://en.cppreference.com/w/cpp/language/friend