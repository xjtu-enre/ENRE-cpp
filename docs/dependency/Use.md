# Use
'Asm Use', 'Use Ptr', 'Addr Use', 'Addr Use Return', 'Cast Use', 'Use Return', 'Use Macrodefine',


## Supported pattern
```
name: AliasDeclaration
```
### Syntax: Type alias(since C++11)
``` cpp
using identifier attr(optional) = type-id;
```
Type alias is a name that refers to a previously defined type (similar to typedef).

#### Examples: 

``` cpp
using flags = std::ios_base::fmtflags;
```

``` 
entities:
    filter: Alias
    items:
        -   name: flags
            loc: [ 1, 7 ]
            kind: Alias
```

### Syntax: alias template(since C++11)
``` cpp
template < template-parameter-list >
using identifier attr(optional) = type-id ;
```
Alias template is a name that refers to a family of types.
#### Examples: 

``` cpp
template<class T>
using ptr = T*; 
ptr<int> x;
```

``` 
entities:
    filter: Alias
    items:
        -   name: ptr
            loc: [ 1, 7 ]
            kind: Alias Template
```

### Syntax: Namespace aliases



``` cpp
namespace alias_name = ns_name;	(1)	
namespace alias_name = ::ns_name;	(2)	
namespace alias_name = nested_name::ns_name;	(3)
```

alias_name must be a name not previously used. alias_name is valid for the duration of the scope in which it is introduced.

#### Examples: 
``` cpp
namespace foo {
    namespace bar {
         namespace baz {
             int qux = 42;
         }
    }
}
namespace fbz = foo::bar::baz;
```

``` 
name: flags
entities:
    filter: Namespace Alias
    items:
        -   name: foo
            loc: [ 0, 11 ]
            kind: Namespace
        -   name: foo::bar
            loc: [ 1, 15 ]
            kind: Namespace
        -   name: foo::bar::baz
            loc: [ 2, 20 ]
            kind: Namespace
        -   name: foo::bar::baz::qux
            loc: [ 3, 18 ]
            kind: Object
        -   name: fbz
            loc: [ 7, 11 ]
            kind: Namespace Alias
```