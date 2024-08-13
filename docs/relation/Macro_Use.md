## Relation: Macro Use

Description:The `Macro Use` Relation in C++ is a way to capture the utilization of macros within the code. It identifies instances where macros, defined by the #define preprocessor directive, are employed.

### Supported Patterns

```yaml
name: Macro Use
```

#### Syntax: Macro Use Declaration

```text
MacroUseRelationDeclaration :
    macro-name
    usage-point
```

##### Examples

###### Macro Use

```CPP
#define A2_LOG_WARN(msg) A2_LOG(Logger::A2_WARN, msg)

WrDiskCacheEntry::~WrDiskCacheEntry()
{
  if (!set_.empty()) {
    A2_LOG_WARN(fmt("WrDiskCacheEntry is not empty size=%lu",
                    static_cast<unsigned long>(size_)));
  }
  deleteDataCells();
}
```

```yaml
name: Macro Use
relation:
  type: Macro Use
  items:
    - from: Function:'~WrDiskCacheEntry'
      to: Macro:'A2_LOG_WARN'
      loc: file0:6:5
```
