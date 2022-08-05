## Entity: Union

Description: A union is a user-defined type in which all members share the same memory location.

### Supported Patterns

```yaml
name: Union Declaration
```
#### Syntax: Union Declaration

```text
union attr class-head-name { member-specification }		
```

##### Examples
###### Union
```cpp
union month
{
    int s[2]; 
    float c;    
};  
```

```yaml
name: Union
entity:
    items:
        -   name: month
            loc: 1:7:1:11
            type: Union
```