# Template

## Supported pattern
```
name: Template Declaration
```
### Syntax
``` cpp
template < parameter-list > requires-clause(optional) declaration	(1)	
export template < parameter-list > declaration	(2)	(until C++11)
template < parameter-list > concept concept-name = constraint-expression ;	(3)	(since C++20)
```


#### Examples: 

- Example1
    ``` cpp
    template<typename T>
    concept C1 = sizeof(T) != sizeof(int);
 
    template<C1 T>
    struct S1 {};
 
    template<C1 T>
    using Ptr = T*;
 
    template<typename T>
    struct S3 { Ptr<T> x; };         

 
    template<typename T>
    concept C2 = sizeof(T) == 1;
 
    template<C2 T> struct S {};
    ```

    ``` json
    entities:
        filter: Alias
        items:
            -   name: flags
                loc: [ 1, 7 ]
                kind: Alias
    ```

- Example2
    ``` cpp
    template<typename T>
    struct A{
        void f() {}
    };
    ```

    ``` json
    entities:
        filter: Alias
        items:
            -   name: flags
                loc: [ 1, 7 ]
                kind: Alias
    ```

# Reference
- https://en.cppreference.com/w/cpp/language/templates
- https://en.cppreference.com/w/cpp/language/class_template
- https://en.cppreference.com/w/cpp/language/function_template

