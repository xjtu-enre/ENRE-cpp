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



###### Template Class friends



###### Template Function friends



###### Struct Function Friends



###### Struct Class Friends


