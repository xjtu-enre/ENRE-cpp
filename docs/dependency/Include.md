# Include

## Supported pattern
```yaml
name: Include Declaration
```
### Syntax: 
```text
#include < h-char-sequence > new-line	(1)	
#include " q-char-sequence " new-line	(2)	
#include pp-tokens new-line	(3)	

__has_include ( " q-char-sequence " )
__has_include ( < h-char-sequence > )	(4)	(since C++17)

__has_include ( string-literal )
__has_include ( < h-pp-tokens > )	(5)	(since C++17)
```

#### Examples: 

- Example1
```cpp
//fileA.cpp

#include <experimental/optional>
#include <utility.h>

//experimental/optional.h

int main(）{
}

//utility.h

int main(）{
}
```

```yaml
name: Include 
entity:
    items:
        -   id: 0
            name: fileA.cpp
            category: File
        -   id: 1
            name: optional
            category: File
        -   id: 2
            name: experimental/optional.h
            category: File
        -   id: 3
            name: utility.h
            category: File
relation:
    items:
        -   category: Include
            src: 0
            dest: 1
        -   category: Include
            src: 0
            dest: 2
        -   category: Include
            src: 0
            dest: 3
```

# Reference
- https://en.cppreference.com/w/cpp/preprocessor/include