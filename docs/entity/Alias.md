## Entity: Alias

Description: `Alias` is a name that refers to a previously defined type (similar to typedef) or a family of types.

### Supported Patterns

```yaml
name: Alias Entity
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
        class child{
        };
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

###### Template Alias
```cpp
template<class T>
using Vec = vector<T, Alloc<T>>; // 类型标识为 vector<T, Alloc<T>>
```

```yaml
name: template alias
entity:
    items:
        -   name: Vec
            loc: 2:7:2:9
            type: Alias
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
namespace f = foo;
```

```yaml
name: Namespace Alias
entity:
    items:
        -   name: fbz
            loc: 8:11:8:13
            type: Namespace Alias
        -   name: f
            loc: 9:11:9:11
            type: Namespace Alias
```