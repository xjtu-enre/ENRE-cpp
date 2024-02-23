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
```CPP
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

###### Anonymous Union
```CPP
struct widget{
    union{
        long id_num;
    }
}
```

```yaml
name: Anonymous Union
entity:
    items:
        -   name: widget::[unnamed]
            loc: 2:10:2:10
            type: Union
```

###### Unrestricted unions
```CPP
struct Point { \* empty *\ };
union U{
    Point p;
};
```

```yaml
name: Unrestricted unions
entity:
    items:
        -   name: U
            loc: 2:7:2:8
            type: Union
```