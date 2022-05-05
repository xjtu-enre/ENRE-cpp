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
class Y {
    int data; // private member
    // the non-member function operator<< will have access to Y's private members
    friend std::ostream& operator<<(std::ostream& out, const Y& o);
    friend char* X::foo(int); // members of other classes can be friends too
    friend X::X(char), X::~X(); // constructors and destructors can be friends
};
std::ostream& operator<<(std::ostream& out, const Y& y){
    return out << y.data; // can access private member Y::data
}
```

```yaml
name: Friend Function
entity:
    items:
        -   id: 0
            name: Y
            category: Class
        -   id: 1
            name: Y::operator<<
            category: Function
        -   id: 2
            name: X::foo
            category: Function
        -   id: 3
            name: X::X
            category: Function
        -   id: 4
            name: X::~X
            category: Function
relation:
    r:
        d: x
        e: x
        s: _
        u: _
    items:
        -   category: Friend
            src: 0
            dest: 1
        -   category: Friend
            src: 0
            dest: 2
        -   category: Friend
            src: 0
            dest: 3
        -   category: Friend
            src: 0
            dest: 4
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
                u: _
        -   category: Friend
            src: 2
            dest: 1
            r:
                d: x
                e: x
                s: r/type use
                u: _
```

# Reference
- https://en.cppreference.com/w/cpp/language/friend