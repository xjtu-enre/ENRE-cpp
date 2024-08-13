## Relation: Define

Descriptions: `Define Relation` occurs when a  `data aggregation entity` defines its internal entities.

### Supported Patterns

```yaml
name: Define Declaration
```
#### Syntax: Define Declaration
```text

```

##### Examples

###### Define

```CPP
class Units{
    int x;
};
int main(){
    Units unit;
}
```

```yaml
name: Define
relation:
    type: Define
    items:
        -   from: Class:'Units'
            to: Variable:'Units::x'
            loc: file0:2:9
        -   from: File:'file0.CPP'
            to: Class:'Units'
            loc: file0:1:7
        -   from: File:'file0.CPP'
            to: Function:'main'
            loc: file0:4:0
        -   from: Function:'main'
            to: Variable:'main::unit'
            loc: file0:5:5
```

###### Define Macro

```CPP
#define PI 3.14159
```

```yaml
name: Define Macro
relation:
    type: Define
    items:
        -   from: File:'file0.CPP'
            to: Macro:'PI'
            loc: file0:1:9
```