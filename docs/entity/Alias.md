## Entity: Alias

Description: `Alias` is a name that refers to a previously defined type (similar to typedef) or a family of types.

### Supported Patterns

```yaml
name: Alias 
```
#### Syntax: Alias Declaration

```text
(1) using alias
using identifier attr(optional) = type-id;
(2) template alias 
template < template-parameter-list >
using identifier attr(optional) = type-id ;
(3) namespace alias
namespace alias_name = ns_name;	(1)	
namespace alias_name = ::ns_name;	(2)	
namespace alias_name = nested_name::ns_name;	(3)

```

##### Examples

###### Using Alias
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
entity:
    items:
        -   name: flags
            loc: 7:7:7:11
            type: Alias
```
###### alias template(since C++11)
```cpp
template<class T>
using ptr = T*; 
```

```yaml
name: Template Alias
entity:
    items:
        -   name: ptr
            loc: 2:7:2:9
            type: Alias Template
```

###### Namespace aliases
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
    items:
        -   name: fbz
            loc: 8:11:8:13
            type: Namespace Alias
```