## Alias
Descriptions: `Alias Relation` means 

### Supported Patterns
```yaml
name: Alias Relation
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
###### Namespace Alias
```cpp
namespace ns{}
namespace text = ns;
```

```yaml
name: Alias
relation:
    type: Namespace Alias
    items:
        -   from: Alias:'text'
            to: Namespace:'ns'
            loc: file0:2:18:2:19
```


###### Using Alias
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
namespace ds = ::foo;
```

```yaml
name: Using Alias
relation:
    items:
      -   from: Alias:'fbz'
          to: Namespace:'foo::bar::baz'
          loc: file0:8:0:8:0
      -   from: Alias:'f'
          to: Namespace:'foo'
          loc: file0:9:0:9:0
      -   from: Alias:'fs'
          to: Namespace:'foo'
          loc: file0:10:0:10:0
```

###### Template Alias
```cpp
template<class T>
using Vec = vector<T, Alloc<T>>; 
```

```yaml
name: template alias
relation:
    items:
      -   from: Alias:'Vec'
          to: Variable:'vector<T, Alloc<T>>'
          loc: file0:2:0:2:0
```
