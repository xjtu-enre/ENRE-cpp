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

```CPP
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
        to: Function:'classA::funcA'
        loc: file0:9:17
    -   from: Class:'classC'
        to: Class:'classB'
        loc: file0:10:12
```

###### Template Function friends

```CPP
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
            to: Function:'classA::funcA'
            loc: file0:4:18
```

###### Struct Class Friends(can't find)

```CPP
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
