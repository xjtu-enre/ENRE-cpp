## Relation: Define
Descriptions:

### Supported patterns

```yaml
name: Define Declaration
```
#### Syntax: Define
```text

```

##### Examples

###### Define

```cpp
class Units{
    int x;
};
```

```yaml
name: Define
relation:
    type: Define
    items:
        -   from: Class:'Units'
            to: Object:'x'
            loc: file0:2:9
        -   from: File:'file0'
            to: Class:'Units'
            loc: file0:1:7

```