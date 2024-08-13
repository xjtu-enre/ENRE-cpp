## Relation: Embed

Description:`Embed Relation` in C++ signifies the inclusion of one type or entity within another, typically representing composition or a strong association between classes or structures.

### Supported Patterns

```yaml
name: Embed
```

#### Syntax: Embed Declaration

```text
EmbedDeclaration :
    type-specifier-seq declarator
```

##### Examples

###### Struct Embedding

```CPP
struct wslay_stack {
  stuct wslay_stack_cell *top;
};
```

```yaml
name: Struct Embedding
relation:
  type: Embed
  items:
    - from: Struct:'wslay_stack'
      to: Struct:'wslay_stack::wslay_stack_cell'
      loc: file0:2:9
```
