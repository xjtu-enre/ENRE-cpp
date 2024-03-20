# category table
- The column headers represents the parameter type of the function.
- Row headers indicate whether the function's parameters are of reference type.
  
    |       | is_referece | non_reference |
    | :-----:| :----: | :----: |
    | Expression |❌| ✅ |
    | Array |❌| ✅ |
    | Struct-Object-member | ✅ |❌|
    | Variable |❌| ✅|
    | Array-Struct-Object-member |✅ |❌|
    | String-struct-Object-member-Variable |❌| ✅ |
    | String-Variable |❌| ✅ |
    | Expression-Struct-Object |❌| ✅ |
    | Multi-Struct-Object-member-Variable |❌| ✅ |
    | Variable-Struct-Object-member |❌|✅|
    | String-Multi-Struct-Object-member |❌|✅|
    | Multi-Variable |❌|✅|
    | String-Multi-Struct-Object-member-Multi-Variable |❌|✅|
    | Expression-Struct-Object-member-Variable |❌|✅|
    | Expression-Struct-Object-member-Multi-Variable |❌|✅|
    | String-Multi-Variable |❌|✅|

# category

**ID**： 1

**Name**: Expression-like-function-call-passing
            <!--表达式-->

**Num**：3

**Case**：
        
* function

        g_orb_buf = (p_orb_data_t*)uorbc_platform_malloc(sizeof(p_orb_data_t) * TOTAL_UORBC_NUM);

* data

        {
            "fromEntityShortName": "uorbc_platform_malloc",
            "toEntityShortName": "p_orb_data_t",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "uorbc_instance",
                "startLine": 43
            },
            "category": "Expression-like-function-call-passing"
        }

**ID**： 2

**Name**: Array-like-function-call-passing
            <!--数组-->

**Num**：2

**Case**：
        
* function

       uorbc_platform_assert(orb[i]);

* data


        {
            "fromEntityShortName": "uorbc_platform_assert",
            "toEntityShortName": "orb",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_init_Multi",
                "startLine": 64
            },
            "category": "Array-like-function-call-passing"
        }

**ID**： 3

**Name**: Struct-Object-member-like-function-call-referece-passing
            <!--结构体-->

**Num**：42

**Case**：
        
* function

       ret = uorbc_platform_sem_init(& (p->sem), 0, 1);

* data

        {
            "fromEntityShortName": "uorbc_platform_sem_init",
            "toEntityShortName": "p->sem",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_init_Multi",
                "startLine": 72
            },
            "category": "Struct-Object-member-like-function-call-referece-passing"
        }

**ID**： 4

**Name**: Variable-like-function-call-passing
            <!--普通变量-->

**Num**：76

**Case**：
        
* function

       mod_uorbc_utils_ismulti(orb_id)

* data

        {
            "fromEntityShortName": "mod_uorbc_utils_isMulti",
            "toEntityShortName": "orb_id",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_exists",
                "startLine": 115
            },
            "category": "Variable-like-function-call-passing"
        }

**ID**： 5

**Name**: Array-Struct-Object-member-like-function-call-referece-passing
            <!--数组-结构体变量-->

**Num**：1

**Case**：
        
* function

       ret = uorbc_platform_sem_init(& (orb[i]->sem), 0, 1);

* data

        {
            "fromEntityShortName": "uorbc_platform_sem_init",
            "toEntityShortName": "orb->sem",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_init_noMulti",
                "startLine": 99
            },
            "category": "Array-Struct-Object-member-like-function-call-referece-passing"
        }

**ID**： 6

**Name**: String-struct-Object-member-Variable-like-function-call-passing
            <!--字符串-结构体变量-普通变量-->

**Num**：7

**Case**：
        
* function

       uorbc_platform_log("orb name:%s o_size：%d orb_id:%d", meta->o_name, meta->o_size, orb_id);

* data

        {
            "fromEntityShortName": "uorbc_platform_log",
            "toEntityShortName": "string, meta->o_name, meta->o_size, orb_id",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_advertise_Multi",
                "startLine": 168
            },
            "category": "String-struct-Object-member-Variable-like-function-call-passing"
        }

**ID**： 7

**Name**: String-Variable-like-function-call-passing
            <!--字符串-普通变量-->

**Num**：11

**Case**：
        
* function

       uorbc_platform_log("inst:%d", inst);

* data

        {
            "fromEntityShortName": "uorbc_platform_log",
            "toEntityShortName": "string,inst",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_advertise_Multi",
                "startLine": 181
            },
            "category": "String-Variable-like-function-call-passing"
        }

**ID**： 8

**Name**: Expression-Struct-Object-member-like-function-call-passing
            <!--字符串-普通变量-->

**Num**：3

**Case**：
        
* function

       nonq->data = (uint8_t*)uorbc_platform_malloc(sizeof(uint8_t) * meta->o_size);

* data

        {
            "fromEntityShortName": "uorbc_platform_malloc",
            "toEntityShortName": "p->sem",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_advertise_Multi",
                "startLine": 194
            },
            "category": "Expression-Struct-Object-member-like-function-call-passing"
        }

**ID**： 9

**Name**: Multi-Struct-Object-member-Variable-like-function-call-passing
            <!--多结构体-普通变量-->

**Num**：6

**Case**：
        
* function

       uorbc_platform_memcpy(nonq->data, data, meta->o_size);

* data

        {
            "fromEntityShortName": "uorbc_platform_memcpy",
            "toEntityShortName": "nonq->data, data, meta->o_size",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_advertise_Multi",
                "startLine": 198
            },
            "category": "Multi-Struct-Object-member-Variable-like-function-call-passing"
        }

**ID**： 10

**Name**: Variable-Struct-Object-member-like-function-call-passing
            <!--普通变量-结构体-->

**Num**：1

**Case**：
        
* function

       q->queue = ring_buffer_init(queue_size, q->queue_perpayload_len);

* data

        {
            "fromEntityShortName": "ring_buffer_init",
            "toEntityShortName": "queue_size, q->queue_perpayload_len",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_advertise_Multi_queue",
                "startLine": 260
            },
            "category": "Variable-Struct-Object-member-like-function-call-passing"
        }


**ID**： 11

**Name**: String-Multi-Struct-Object-member-like-function-call-passing
            <!--字符串-多结构体-->

**Num**：3

**Case**：
        
* function

       uorbc_platform_log("meta->o_name:%s qbuf_size:%d", meta->o_name, q->qbuf_size);

* data

        {
            "fromEntityShortName": "uorbc_platform_log",
            "toEntityShortName": "string，meta->o_name, q->qbuf_size",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_advertise_Multi_queue",
                "startLine": 263
            },
            "category": "String-Multi-Struct-Object-member-like-function-call-passing"
        }



**ID**： 12

**Name**: Multi-Variable-like-function-call-passing
            <!--多普通变量-->

**Num**：32

**Case**：
        
* function

       *handle = orb_advertise_multi(meta, data, instance);

* data

        {
            "fromEntityShortName": "orb_advertise_Multi",
            "toEntityShortName": "meta, data, instance",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_publish_auto",
                "startLine": 321
            },
            "category": "Multi-Variable-like-function-call-passing"
        }


**ID**： 13

**Name**: String-Multi-Struct-Object-member-Multi-Variable-like-function-call-passing
            <!--字符串-多结构体-普通变量-->

**Num**：1

**Case**：
        
* function

       uorbc_platform_log("orb_id:%d o_name:%s o_size:%d handle:%d", orb_id, meta->o_name, meta->o_size, handle);

* data

        {
            "fromEntityShortName": "uorbc_platform_log",
            "toEntityShortName": "string,orb_id, meta->o_name, meta->o_size, handle",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_copy",
                "startLine": 611
            },
            "category": "String-Multi-Struct-Object-member-Multi-Variable-like-function-call-passing"
        }


**ID**： 14

**Name**: Expression-Struct-Object-member-Variable-like-function-call-passing
            <!--表达式-结构体-普通变量-->

**Num**：1

**Case**：
        
* function

       uint32_t* que_buf = uorbc_platform_malloc(sizeof(uint32_t)+msg_pan*(meta->o_size));

* data

        {
            "fromEntityShortName": "uorbc_platform_malloc",
            "toEntityShortName": "uint32_t,msg_pan,meta->o_size",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_copy",
                "startLine": 651
            },
            "category": "Expression-Struct-Object-member-Variable-like-function-call-passing"
        }


**ID**： 15

**Name**: Expression-Struct-Object-member-Multi-Variable-like-function-call-passing
            <!--表达式-结构体-多普通变量-->

**Num**：2

**Case**：
        
* function

       ring_buffer_just_peek(p_ring, _que_buf, peek_pos+sizeof(queue_payload_header_t), meta->o_size);

* data

        {
            "fromEntityShortName": "ring_buffer_just_peek",
            "toEntityShortName": "p_ring, _que_buf, peek_pos,queue_payload_header_t, meta->o_size",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_copy",
                "startLine": 672
            },
            "category": "Expression-Struct-Object-member-Multi-Variable-like-function-call-passing"
        }

**ID**： 16

**Name**: String-Multi-Variable-like-function-call-passing
            <!--字符串-多普通变量-->

**Num**：3

**Case**：
        
* function

       uorbc_platform_log_err("error orb_id:%d multi_pos:%d", orb_id, multi_pos);

* data

        {
            "fromEntityShortName": "uorbc_platform_log_err",
            "toEntityShortName": "string,or_id,Multi_pos",
            "loc": {
                "entityFile": "mod_uorbc.c",
                "dataAggregateEntity": "orb_check",
                "startLine": 731
            },
            "category": "String-Multi-Variable-like-function-call-passing"
        }

