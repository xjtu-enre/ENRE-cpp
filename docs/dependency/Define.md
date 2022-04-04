# Define


## Supported pattern
```
name: Define Declaration
```
### Syntax: 

#### Examples: 

``` cpp
# in file.h
class Units{
};
```

``` 
entities:
    items:
        -   id: 0
            name: file.h
            loc: [ -1, -1 ]
            kind: File
        -   id: 1
            name: Units
            loc: [ 1, 7]
            kind: Class

dependencies:
    items:
        -   kind: Define
            src: 0
            dest: 1
            loc: [1, 7]

```

### Syntax: Use Macrodefine
Macro uses dest entities in his definition. The source entity for "Use Macrodefine" dependency must be a macro entity.
#### Examples: 

``` cpp
class Helper{};

#define ASSERT_DEBUG_LOG(message) Helper PASTE2(helper, __COUNTER__)(message)
```

``` 
entities:
    items:
        -   id: 0
            name: Helper
            kind: Class
        -   id: 1
            name: ASSERT_DEBUG_LOG
            kind: Macro
dependencies:
    items:
        -   kind: Use Macrodefine
            src: 1
            dest: 0
            loc: [3, 0]
```