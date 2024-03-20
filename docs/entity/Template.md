## Entity: Template

Description: `Template Entity` is the basis for generic programming in C++. Templates enable to define the operations of a class or function, and let the user specify what concrete types those operations should work on.

### Supported Patterns

```yaml
name: Template
```

#### Syntax: Template Declaration
```text
template < parameter-list > requires-clause(optional) declaration	(1)	
export template < parameter-list > declaration	(2)	(until C++11)
template < parameter-list > concept concept-name = constraint-expression ;	(3)	(since C++20)
```
##### Examples

###### Class Template

```CPP
template<typename T>
class S{};
```

```yaml
    name: Class Template
    entity:
        items:
            -   name: S
                loc: 2:7:2:7
                kind: Class Template
                type: Template
```

###### Struct Template
```CPP
template<typename T>
struct S{};
```

```yaml
    name: Struct Template
    entity:
        items:
            -   name: S
                loc: 2:8:2:8
                kind: Struct Template
                type: Template
```

###### Function Template
```CPP
template<class T>
void func(T x);
```

```yaml
    name: Function Template
    entity:
        items:
            -   name: func
                loc: 2:6:2:8
                kind: Function Template
                type: Template
```

###### Extern template
```CPP
template<typename T> class tuple;
extern template class tuple<int>;
```

```yaml
    name: Extern template
    entity:
        items:
            -   name: tuple
                loc: 1:23:1:27
                kind: Class Template
                type: Template
```

###### Templates as template parameters
```CPP
template<typename T, template<typename U, int I> class Arr>
class tuple
{ /* empty */ };
```

```yaml
    name: Templates as template parameters
    entity:
        items:
            -   name: Arr
                loc: 1:56:1:58
                kind: Class Template
                type: Template
            -   name: tuple
                loc: 2:7:2:11
                kind: Class Template
                type: Template
```


###### Templates with Default template arguments
```CPP
template <class T, class Allocator = allocator<T>> class vector;
```

```yaml
    name: Templates with Default template arguments
    entity:
        items:
            -   name: vector
                loc: 1:58:1:63
                kind: Class Template
                type: Template
```

###### Template specialization
```CPP
template <typename K, typename V>
class MyMap{ /* Empty */ };
template<typename V>
class MyMap<string, V> { /* Empty */ };
```

```yaml
    name: Template specialization
    entity:
        items:
            -   name: MyMap
                loc: 2:7:2:11
                kind: Class Template
                type: Template
            -   name: MyMap
                loc: 4:7:4:11
                kind: Class Template
                type: Template
```


###### Function Template Instantiation
```CPP

template<class T> void f(T) { }
template void f<int> (int);
```

```yaml
    name: Function Template Instantiation
    entity:
        items:
            -   name: f
                loc: 0:24:0:24
                kind: Function Template
                type: Template
```