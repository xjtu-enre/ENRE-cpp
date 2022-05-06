# Alias

## Supported pattern
```yaml
name: AliasDeclaration
```
### Syntax: Type alias(since C++11)
```text
using identifier attr(optional) = type-id;
```
Type alias is a name that refers to a previously defined type (similar to typedef).

#### Examples: 

- Example:
```cpp
namespace base{
    namespace flags{
        namespace child{
        }
    }
}
using flags = base::flags::child;
```

```yaml
name: Using Alias
entitiy:
    filter: Alias
    r:
        d: .
        e: o/nameError
        s: x
        u: Namespace Alias
    items:
        -   name: flags
            loc: [ 1, 7 ]
            category: Alias
```

### Syntax: alias template(since C++11)
```text
template < template-parameter-list >
using identifier attr(optional) = type-id ;
```
Alias template is a name that refers to a family of types.
#### Examples: 

- Example:
```cpp
template<class T>
using ptr = T*; 
ptr<int> x;
```

```yaml
name: Template Alias
entity:
    filter: Alias
    r:
        d: .
        e: o/nameError
        s: xtypedef
        u: Type Alias Template
    items:
        -   name: ptr
            loc: [ 1, 7 ]
            category: Alias Template
```

### Syntax: Namespace aliases



```text
namespace alias_name = ns_name;	(1)	
namespace alias_name = ::ns_name;	(2)	
namespace alias_name = nested_name::ns_name;	(3)
```

alias_name must be a name not previously used. alias_name is valid for the duration of the scope in which it is introduced.

#### Examples: 

- Example:
```cpp
namespace foo {
    namespace bar {
         namespace baz {
             int qux = 42;
         }
    }
}
namespace fbz = foo::bar::baz;
```

```yaml
name: Namespace Alias
entity:
    filter: Namespace Alias
    r:
        d: Package
        e: .
        s: .
        u: .
    items:
        -   name: foo
            loc: [ 0, 11 ]
            category: Namespace
        -   name: foo::bar
            loc: [ 1, 15 ]
            category: Namespace
        -   name: foo::bar::baz
            loc: [ 2, 20 ]
            category: Namespace
        -   name: foo::bar::baz::qux
            loc: [ 3, 18 ]
            category: Variable
            r:
                d: Var
                e: .
                s: global varibale
                u: Object
        -   name: fbz
            loc: [ 7, 11 ]
            category: Namespace Alias
            r:
              d: Alias
              e: .
              s: xNamespace
              u: .
```