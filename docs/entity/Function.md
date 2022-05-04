# Classes

## Supported pattern
```yaml
name: FunctionDeclaration
```
### Syntax
```cpp
noptr-declarator ( parameter-list ) cv(optional) ref(optional) except(optional) attr(optional)	(1)	
noptr-declarator ( parameter-list ) cv(optional) ref(optional) except(optional) attr(optional) -> trailing	(2)	(since C++11)
```


#### Examples: 

- Example1
```cpp
    int max(int a, int b, int c)
    {
        int m = (a > b) ? a : b;
        return (m > c) ? m : c;
    }
```

```yaml
    name: Function
    entities:
        filter: Function
        items:
            -   name: max
                loc: [ 1, 7 ]
```


### Syntax
``` cpp
[ captures ] ( params ) specs requires(optional) { body }	(1)	
[ captures ] { body }	(2)	(until C++23)
[ captures ] specs { body }	(2)	(since C++23)
[ captures ] < tparams > requires(optional) ( params ) specs requires(optional) { body }	(3)	(since C++20)
[ captures ] < tparams > requires(optional) { body }	(4)	(since C++20)
(until C++23)
[ captures ] < tparams > requires(optional) specs { body }	(4)	(since C++23)
```


#### Examples: 

- Example1
``` cpp
    auto glambda = [](auto a, auto&& b) { return a < b; };
    bool b = glambda(3, 3.14); 
```

```yaml
    name: Lambda Function
    entities:
        filter: Function
        items:
            -   name: glambda
                loc: [ 1, 7 ]
                kind: Function
```


### Syntax
``` cpp
operator op	(1)	
operator type	(2)	
```


#### Examples: 

- Example1
``` cpp
    struct Linear {
        double a, b;
 
        double operator()(double x) const {
            return a*x + b;
        }
    };
```

```yaml
    name: Struct Method
    entities:
        filter: Function
        items:
            -   name: operator()
                loc: [ 1, 7 ]
                kind: Function
```


# Reference
- https://en.cppreference.com/w/cpp/language/operators
- https://en.cppreference.com/w/cpp/language/lambda
- https://en.cppreference.com/w/cpp/language/function
- https://en.cppreference.com/w/cpp/language/functions