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
name: Namespace Alias
relation:
    type: Alias
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
namespace fb = foo::bar;
namespace f = foo;
```

```yaml
name: Using Alias
relation:
    type: alias
    items:
      -   from: Alias:'fbz'
          to: Namespace:'baz'
          loc: file0:8:0:8:0
      -   from: Alias:'fb'
          to: Namespace:'bar'
          loc: file0:10:0:10:0
      -   from: Alias:'f'
          to: Namespace:'foo'
          loc: file0:11:0:11:0
```

###### Template Alias
```cpp
class vector{};
template<class T>
using Vec = vector; 
```

```yaml
name: template alias
relation:
    type: alias
    items:
      -   from: Alias:'Vec'
          to: Variable:'vector'
          loc: file0:3:0:3:0
```
