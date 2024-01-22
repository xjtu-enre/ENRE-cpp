/**
 * Defines schema for meta describing single testcase.
 */
export const schemaObj = {
  type: 'object',
  properties: {
    /**
     * Case's name.
     *
     * Any non-alphabetical character will be converted to `-`.
     */
    name: {type: 'string'},
    /**
     * Config fields in package.json
     */
    pkg: {
      type: 'object',
      properties: {
        /**
         * Determine what module system does the file use.
         */
        type: {enum: ['commonjs', 'module']}
      },
      additionalProperties: false,
    },
    /**
     * Assert what errors/warnings should be thrown.
     *
     * // TODO: Decide whether to use error id number, name or full string
     */
    throw: {
      type: 'array',
      uniqueItems: true,
      items: {
        type: 'string',
      }
    },
    /**
     * Defines entity's fetching properties.
     */
    entity: {
      type: 'object',
      properties: {
        /**
         * Set `type` property for all entity items conveniently.
         *
         * If another `type` is presented in an item,
         * that value will override this.
         */
        type: {
          enum: [
              'Alias',
              'Class',
              'Enum',
              'Enumerator',
              'File',
              'Function',
              'Macro',
              'Namespace',
              'Struct',
              'Template',
              'Typedef',
              'Union',
              'Variable',
          ],
        },
        /**
         * Whether to allow unlisted entities to exist.
         *
         * Only items without `negative` will be counted.
         *
         * Rules:
         * 1. If `entity.type` is set: no more entities with the explicit `entity.type`, other types are still allowed;
         * 2. If `entity.type` is not set: no more entities other than those in items;
         * 3. Items that `item.negative: true` will always be ignored in any circumstance.
         *
         * @default true
         */
        extra: {type: 'boolean', default: true},
        /**
         * Entities to be validated.
         */
        items: {
          type: 'array',
          uniqueItems: true,
          items: {
            type: 'object',
            properties: {
              /**
               * Entity's name.
               */
              name: {type: 'string'},
              /**
               * Entity's qualified name.
               */
              qualified: {type: 'string'},
              /**
               * Entity's location (String format explained in packages/enre-location).
               */
              loc: {type: 'string'},
              /**
               * Whether it is a negative test item.
               *
               * A negative test item is entity that should NOT be extracted.
               */
              negative: {type: 'boolean', default: false},
              additionalProperties: false,
            },
            required: ['name', 'loc', 'type'],
            oneOf: [
              /**
               * Alias
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Alias'},
                },
              },
              /**
               * Class
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Class'},
                  visibility: {enum: ['public', 'private', 'protected']},
                },
              },
              /**
               * Enum
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'enum'},
                },
              },
              /**
               * Enumerator
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Enumerator'},
                },
              },
              /**
               * File
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'File'},
                },
              },
              /**
               * Function
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'function'},
                },
              },
              /**
               * Macro
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Macro'},
                },
              },
              /**
               * Namespace
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Namespace'},
                },
              },
              /**
               * Struct
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Struct'},
                },
              },
              /**
               * Template
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Template'},
                  kind: {enum: ['Struct Template', 'Function Template', 'Class Template']}
                },
              },
              /**
               * Typedef
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Typedef'},
                },
              },
              /**
               * Union
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Union'},
                },
              },
              /**
               * Variable
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Variable'},
                  storage_class: {enum: ['register', 'auto', 'static','external']},
                },
              },
            ],
          },
        },
      },
      additionalProperties: false,
    },
    relation: {
      type: 'object',
      properties: {
        type: {
          enum: [
            'Alias',
            'Call',
            'Define',
            'Except',
            'Extend',
            'Friend',
            'Include',
            'Modify',
            'Override',
            'Parameter',
            'Set',
            'Use',
            'Using',
          ]
        },
        extra: {type: 'boolean', default: true},
        items: {
          type: 'array',
          uniqueItems: true,
          items: {
            type: 'object',
            properties: {
              from: {type: 'string'},
              to: {type: 'string'},
              loc: {type: 'string'},
              /**
               * Negative relation expects both entities, and the relation does not exist.
               */
              negative: {type: 'boolean', default: false},
              additionalProperties: false,
            },
            required: ['from', 'to', 'loc'],
            oneOf: [
              /**
               * Alias
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Alias'},
                },
              },
              /**
               * Call
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Call'},
                },
              },
              /**
               * Define
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Define'},
                },
              },
              /**
               * Except
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Except'},
                },
              },
              /**
               * Extend
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Extend'},
                },
              },
              /**
               * Friend
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Friend'},
                },
              },
              /**
               * Include
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Include'},
                },
              },
              /**
               * Modify
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Modify'},
                },
              },
              /**
               * Override
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Override'},
                },
              },
              /**
               * Parameter
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Parameter'},
                },
              },
              /**
               * Set
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Set'},
                },
              },
              /**
               * Use
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Use'},
                },
              },
              /**
               * Using
               */
              {
                type: 'object',
                properties: {
                  type: {const: 'Using'},
                },
              },
            ],
          }
        }
      },
      additionalProperties: false,
    }
  },
  required: ['name'],
  additionalProperties: false,
};

// TODO: Typing it
export type CaseSchema = any;
