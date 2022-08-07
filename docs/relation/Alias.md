## Alias
Descriptions: `Alias Relation` means 

### Supported Patterns
```yaml
name: Alias
```
#### Syntax: Alias Declaration

```text
namespace alias_name = ns_name;	(1)	
namespace alias_name = ::ns_name;	(2)	
namespace alias_name = nested_name::ns_name;	(3)	
```

##### Examples
###### Alias
```cpp
namespace ns{}
namespace text = ns;
```

```yaml
name: Alias
relation:
    type: Alias
    items:
        -   from: Alias:'text'
            to: Namespace:'ns'
            loc: file0:2:18:2:19
```
