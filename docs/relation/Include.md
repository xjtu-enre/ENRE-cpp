## Include
Descriptions: `Include Relation` means include other source file into current source file at the line immediately after the directive.

### Supported Patterns
```yaml
name: Include
```
#### Syntax: Include Declaration

```text
#include < h-char-sequence > new-line	(1)	
#include " q-char-sequence " new-line	(2)	
#include pp-tokens new-line	(3)	

__has_include ( " q-char-sequence " )
__has_include ( < h-char-sequence > )	(4)	(since C++17)

__has_include ( string-literal )
__has_include ( < h-pp-tokens > )	(5)	(since C++17)
```

##### Examples 

```cpp
//file0.h

//file1.h

//file.cpp
#include <file0.h>
#include <file1.h>
```

```yaml
name: Include 
relation:
    type: Include
    items:
        -   from: File:file.cpp
            to: File:file0.h
            loc: file:1:11
        -   from: File:file.cpp
            to: File:file1.h
            loc: file:2:11
```