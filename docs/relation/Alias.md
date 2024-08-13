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
```CPP
namespace ns{}
namespace text = ns;
```

```yaml
name: Namespace Alias
relation:
  type: Alias
  items:
    -   from: Namespace Alias:'text'
        to: Namespace:'ns'
        loc: file0:2:18:2:19
```


###### Using Alias
```CPP
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
    type: Alias
    items:
      -   from: Namespace Alias:'fbz'
          to: Namespace:'foo::bar::baz'
          loc: file0:8:114:8:144
      -   from: Namespace Alias:'fb'
          to: Namespace:'foo::bar'
          loc: file0:9:146:9:170
      -   from: Namespace Alias:'f'
          to: Namespace:'foo'
          loc: file0:10:172:10:190
```

###### Template Alias
```CPP
class vector{};
template<class T>
using Vec = vector; 
```

```yaml
name: template alias
relation:
    type: Alias
    items:
      -   from: Alias:'Vec'
          to: Class:'vector'
          loc: file0:3:36:3:55
```
