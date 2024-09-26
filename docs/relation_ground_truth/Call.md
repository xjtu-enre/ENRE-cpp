## Relation: Call

Descriptions: `Call Relation` is a kind of postfix-expression, formed by an expression that evaluates to a function or callable Variable followed by the function-call operator, ().

### Supported Patterns 

```yaml
name: Call
```

#### Syntax: Call Declaration

```text
CallDeclaration :
    postfix-expression ( argument-expression-list opt )
```

##### Examples

###### Function Call


```cpp
//// mod_uorbc_ringbuffer.c
uint32_t ring_buffer_to_the_power_of_2(uint32_t in_data){}
ring_buffer_t *ring_buffer_init(size_t queue_size, size_t obj_size)
{
    uint32_t size_q = queue_size * obj_size;
    uint32_t size_new = ring_buffer_to_the_power_of_2(size_q);

    uorbc_platform_log("queue_size(%d)*obj_size(%d):%d size_new:%d", queue_size, obj_size, size_q, size_new);

    ring_buffer_t *p_ring = uorbc_platform_malloc(2 * sizeof(ring_buffer_size_t) + size_new * sizeof(size_t));
    if (NULL != p_ring)
    {
        memset(p_ring, 0x00, (2 * sizeof(ring_buffer_size_t) + size_new * sizeof(size_t)));
        p_ring->tail_index = 0;
        p_ring->head_index = 0;
        p_ring->buffer_size = size_new;
        p_ring->buffer_mask = p_ring->buffer_size - 1;
        uorbc_platform_log("buffer_size:%d buffer_mask:%d", p_ring->buffer_size, p_ring->buffer_mask);
        return p_ring;
    }

    return NULL;
}
```

```yaml
name: Function Call 1
relation:
  type: Call
  items:
    -   from: Function:'ring_buffer_init'
        to: Function:'ring_buffer_to_the_power_of_2'
        loc: mod_uorbc_ringbuffer:5:25
```


```cpp
//// mod_uorbc_ringbuffer.c
void ring_buffer_queue(ring_buffer_t *buffer, char data){}
void ring_buffer_queue_arr(ring_buffer_t *buffer, const char *data, ring_buffer_size_t size)
{
    //	uorbc_platform_log( "in data:(%d)", size );
    //	uorbc_platform_log_bin( data, size );

    /* Add bytes; one by one */
    ring_buffer_size_t i;
    for (i = 0; i < size; i++)
    {
        ring_buffer_queue(buffer, data[i]);
    }
}
```

```yaml
name: Function Call 2
relation:
  type: Call
  items:
    -   from: Function:'ring_buffer_queue_arr'
        to: Function:'ring_buffer_queue'
        loc: mod_uorbc_ringbuffer:11:9
```

```cpp
//// mod_uorbc_ringbuffer.c
static uint8_t ring_buffer_is_empty(ring_buffer_t *buffer){}
uint8_t ring_buffer_dequeue(ring_buffer_t *buffer, char *data)
{
    if (ring_buffer_is_empty(buffer))
    {
        /* No items */
        //		uorbc_platform_log_err( "queue empty" );
        return 0;
    }

    *data = buffer->buffer[buffer->tail_index];
    buffer->tail_index = ((buffer->tail_index + 1) & buffer->buffer_mask);
    return 1;
}
```

```yaml
name: Function Call 3
relation:
  type: Call
  items:
    -   from: Function:'ring_buffer_dequeue'
        to: Function:'ring_buffer_is_empty'
        loc: mod_uorbc_ringbuffer:3:9
```

```cpp
//// mod_uorbc_ringbuffer.c
static uint8_t ring_buffer_is_empty(ring_buffer_t *buffer){}
uint8_t ring_buffer_dequeue(ring_buffer_t *buffer, char *data){}
ring_buffer_size_t ring_buffer_dequeue_arr(ring_buffer_t *buffer, char *data, ring_buffer_size_t len)
{
    if (ring_buffer_is_empty(buffer))
    {
        /* No items */
        //		uorbc_platform_log_err( "queue empty" );
        return 0;
    }

    char *data_ptr = data;
    ring_buffer_size_t cnt = 0;
    while ((cnt < len) && ring_buffer_dequeue(buffer, data_ptr))
    {
        cnt++;
        data_ptr++;
    }

    //	uorbc_platform_log( "out data:(%d) cnt:%d", len, cnt );
    //	uorbc_platform_log_bin( data, len );

    return cnt;
}
```

```yaml
name: Function Call 4
relation:
  type: Call
  items:
    -   from: Function:'ring_buffer_dequeue_arr'
        to: Function:'ring_buffer_is_empty'
        loc: mod_uorbc_ringbuffer:5:9
    -   from: Function:'ring_buffer_dequeue_arr'
        to: Function:'ring_buffer_dequeue'
        loc: mod_uorbc_ringbuffer:14:27
```

```cpp
//// mod_uorbc_ringbuffer.c
static uint8_t ring_buffer_is_empty(ring_buffer_t *buffer){}
ring_buffer_size_t ring_buffer_just_peek(ring_buffer_t *buffer, char *data, ring_buffer_size_t pos, ring_buffer_size_t len)
{
    if (ring_buffer_is_empty(buffer))
    {
        /* No items */
        uorbc_platform_log_err("queue empty");
        return 0;
    }

    //	uorbc_platform_log("peek_pos:%d len:%d", pos, len);

    char *data_ptr = data;
    ring_buffer_size_t cnt = 0;
    ring_buffer_size_t peek_pos = pos;
    while (cnt < len)
    {
        *data_ptr = buffer->buffer[peek_pos];
        peek_pos = ((peek_pos + 1) & buffer->buffer_mask);

        cnt++;
        data_ptr++;
    }

    //	uorbc_platform_log( "out data:(%d) cnt:%d", len, cnt );
    //	uorbc_platform_log_bin( data, len );

    return cnt;
}
```

```yaml
name: Function Call 5
relation:
  type: Call
  items:
    -   from: Function:'ring_buffer_just_peek'
        to: Function:'ring_buffer_is_empty'
        loc: mod_uorbc_ringbuffer:4:9
```

```cpp
//// mod_uorbc_ringbuffer.c
static ring_buffer_size_t ring_buffer_num_items(ring_buffer_t *buffer){}
uint8_t ring_buffer_peek(ring_buffer_t *buffer, char *data, ring_buffer_size_t index)
{
    if (index >= ring_buffer_num_items(buffer))
    {
        /* No items at index */
        return 0;
    }

    /* Add index to pointer */
    ring_buffer_size_t data_index = ((buffer->tail_index + index) & buffer->buffer_mask);
    *data = buffer->buffer[data_index];
    return 1;
}
```

```yaml
name: Function Call 6
relation:
  type: Call
  items:
    -   from: Function:'ring_buffer_peek'
        to: Function:'ring_buffer_num_items'
        loc: mod_uorbc_ringbuffer:4:18
```

```cpp
//// mod_uorbc_ringbuffer.c
static ring_buffer_size_t ring_buffer_num_items(ring_buffer_t *buffer){}
ring_buffer_size_t ring_buffer_left_space(ring_buffer_t *buffer)
{
    //	uorbc_platform_log("buffer_mask:%d ring_buffer_num_items:%d", buffer->buffer_mask, ring_buffer_num_items( buffer ));

    return (buffer->buffer_mask - ring_buffer_num_items(buffer));
}
```

```yaml
name: Function Call 7
relation:
  type: Call
  items:
    -   from: Function:'ring_buffer_left_space'
        to: Function:'ring_buffer_num_items'
        loc: mod_uorbc_ringbuffer:6:35
```

```cpp
//// mod_uorbc_test.c
static int uorbc_check_copy(const struct orb_metadata *meta, int handle, void *buffer, uint8_t *update){}
static void *uorbc_test_thread_sub(void)
{
    uorbc_platform_log("uorbc_test_thread_sub");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;

    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "0 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Call 8
relation:
  type: Call
  items:
    -   from: Function:'uorbc_test_thread_sub'
        to: Function:'uorbc_check_copy'
        loc: mod_uorbc_test:29:15
```

```cpp
//// mod_uorbc_test.c
static int uorbc_check_copy(const struct orb_metadata *meta, int handle, void *buffer, uint8_t *update){}
static void *uorbc_test_thread_sub1(void)
{
    uorbc_platform_log("uorbc_test_thread_sub1");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(5500);
        ++const char *sub = "1 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Call 9
relation:
  type: Call
  items:
    -   from: Function:'uorbc_test_thread_sub1'
        to: Function:'uorbc_check_copy'
        loc: mod_uorbc_test:28:15
```


```cpp
//// mod_uorbc_test.c
static int uorbc_check_copy(const struct orb_metadata *meta, int handle, void *buffer, uint8_t *update){}
static void *uorbc_test_thread_sub2(void)
{
    uorbc_platform_log("uorbc_test_thread_sub2");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "2 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Call 10
relation:
  type: Call
  items:
    -   from: Function:'uorbc_test_thread_sub2'
        to: Function:'uorbc_check_copy'
        loc: mod_uorbc_test:28:15
```

```cpp
//// mod_uorbc_test.c
static int uorbc_check_copy(const struct orb_metadata *meta, int handle, void *buffer, uint8_t *update){}
static void *uorbc_test_thread_sub3(void)
{
    uorbc_platform_log("uorbc_test_thread_sub3");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "3 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Call 11
relation:
  type: Call
  items:
    -   from: Function:'uorbc_test_thread_sub3'
        to: Function:'uorbc_check_copy'
        loc: mod_uorbc_test:28:15
```

```cpp
//// mod_uorbc_test.c
static int uorbc_check_copy(const struct orb_metadata *meta, int handle, void *buffer, uint8_t *update){}
static void *uorbc_test_thread_sub4(void)
{
    uorbc_platform_log("uorbc_test_thread_sub4");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "4 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Call 12
relation:
  type: Call
  items:
    -   from: Function:'uorbc_test_thread_sub4'
        to: Function:'uorbc_check_copy'
        loc: mod_uorbc_test:28:15
```

```cpp
//// mod_uorbc_test.c
static int uorbc_check_copy(const struct orb_metadata *meta, int handle, void *buffer, uint8_t *update){}
static void *uorbc_test_thread_sub5(void)
{
    uorbc_platform_log("uorbc_test_thread_sub5");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "5 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Call 13
relation:
  type: Call
  items:
    -   from: Function:'uorbc_test_thread_sub5'
        to: Function:'uorbc_check_copy'
        loc: mod_uorbc_test:28:15
```

```cpp
//// mod_uorbc_test.c
static int uorbc_check_copy(const struct orb_metadata *meta, int handle, void *buffer, uint8_t *update){}
static void *uorbc_test_thread_sub6(void)
{
    uorbc_platform_log("uorbc_test_thread_sub6");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;
    uint32_t sub_count = 0;

    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "6 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Call 14
relation:
  type: Call
  items:
    -   from: Function:'uorbc_test_thread_sub6'
        to: Function:'uorbc_check_copy'
        loc: mod_uorbc_test:28:15
```

```cpp
//// mod_uorbc.c
static p_orb_data_t *uorbc_instance(void){}
static void orb_init_multi(int i)
{
    int ret = -1;
    int j = 0;
    int k = 0;

    uorbc_platform_log("orb_init_multi");

    p_orb_data_t *orb = uorbc_instance();
    p_orb_data_t p = NULL;

    orb[i] = (p_orb_data_t)uorbc_platform_malloc(sizeof(orb_data_t) * UORB_MULTI_MAX_INSTANCES);
    uorbc_platform_assert(orb[i]);
    memset(orb[i], 0x00, (sizeof(orb_data_t) * UORB_MULTI_MAX_INSTANCES));

    for (j = 0; j < UORB_MULTI_MAX_INSTANCES; ++j)
    {
        p = &(orb[i][j]);

        p->orb_id = -1;
        p->queue_mode = false;
        ret = uorbc_platform_sem_init(&(p->sem), 0, 1);
        if (0 != ret)
        {
            uorbc_platform_perror("uorbc_platform_sem_init");
        }
        p->last_updated_time = 0;
        for (k = 0; k < UORB_MAX_SUB_NUM; ++k)
        {
            p->registered_list[k] = -1;
        }
    }
}
```

```yaml
name: Function Call 15
relation:
  type: Call
  items:
    -   from: Function:'orb_init_multi'
        to: Function:'uorbc_instance'
        loc: mod_uorbc:10:25
```

```cpp
//// mod_uorbc.c
static p_orb_data_t *uorbc_instance(void){}
static void orb_init_nomulti(int i)
{
    int ret = -1;
    int k = 0;

    uorbc_platform_log("orb_init_nomulti");

    p_orb_data_t *orb = uorbc_instance();

    orb[i] = (p_orb_data_t)uorbc_platform_malloc(sizeof(orb_data_t));
    uorbc_platform_assert(orb[i]);
    memset(orb[i], 0x00, (sizeof(orb_data_t)));

    orb[i]->orb_id = -1;
    orb[i]->queue_mode = false;

    ret = uorbc_platform_sem_init(&(orb[i]->sem), 0, 1);
    if (0 != ret)
    {
        uorbc_platform_perror("uorbc_platform_sem_init");
    }
    orb[i]->last_updated_time = 0;
    for (k = 0; k < UORB_MAX_SUB_NUM; ++k)
    {
        orb[i]->registered_list[k] = -1;
    }
}
```

```yaml
name: Function Call 16
relation:
  type: Call
  items:
    -   from: Function:'orb_init_nomulti'
        to: Function:'uorbc_instance'
        loc: mod_uorbc:9:25
```

```cpp
//// mod_uorbc.c
static p_orb_data_t *uorbc_instance(void){}
int orb_exists(const struct orb_metadata *meta, int instance)
{
    int orb_id = mod_uorbc_utils_get_orbid(meta->o_name);
    if (orb_id < 0 || orb_id >= TOTAL_UORBC_NUM)
    {
        return -1;
    }
    if (!mod_uorbc_utils_ismulti(orb_id) && instance > 0)
    {
        return -1;
    }

    p_orb_data_t *orb = uorbc_instance();
    p_orb_data_t p = &(orb[orb_id][instance]);

    if ((p->orb_id) != -1)
    {
        return 0;
    }

    return -1;
}
```

```yaml
name: Function Call 17
relation:
  type: Call
  items:
    -   from: Function:'orb_exists'
        to: Function:'uorbc_instance'
        loc: mod_uorbc:14:25
```

```cpp
//// mod_uorbc.c
orb_advert_t orb_advertise_queue(const struct orb_metadata *meta, const void *data, unsigned int queue_size){}
orb_advert_t orb_advertise_multi(const struct orb_metadata *meta, const void *data, int *instance){}
orb_advert_t orb_advertise(const struct orb_metadata *meta, const void *data)
{
    //	uorbc_platform_log( "orb_advertise" );
    if (UORBC_QOS_1 == meta->qos)
    {
        uorbc_platform_log("QOS1 topic need use queue mode");
        return orb_advertise_queue(meta, data, UORBC_QUEUE_MSG_NUM);
    }

    return orb_advertise_multi(meta, data, NULL);
}
```

```yaml
name: Function Call 18
relation:
  type: Call
  items:
    -   from: Function:'orb_advertise'
        to: Function:'orb_advertise_queue'
        loc: mod_uorbc:9:16
    -   from: Function:'orb_advertise'
        to: Function:'orb_advertise_multi'
        loc: mod_uorbc:12:12
```

```cpp
//// mod_uorbc.c
orb_advert_t orb_advertise_multi_queue(const struct orb_metadata *meta, const void *data, int *instance,unsigned int queue_size){}
orb_advert_t orb_advertise_queue(const struct orb_metadata *meta, const void *data, unsigned int queue_size)
{
    return orb_advertise_multi_queue(meta, data, NULL, queue_size);
}
```

```yaml
name: Function Call 19
relation:
  type: Call
  items:
    -   from: Function:'orb_advertise_queue'
        to: Function:'orb_advertise_multi_queue'
        loc: mod_uorbc:4:12
```

```cpp
//// mod_uorbc.c
orb_advert_t orb_advertise_multi_queue(const struct orb_metadata *meta, const void *data, int *instance,unsigned int queue_size){}
static p_orb_data_t *uorbc_instance(void){}
static int get_subscriber_id(void){}
orb_advert_t orb_advertise_multi(const struct orb_metadata *meta, const void *data, int *instance)
{
    if (UORBC_QOS_1 == meta->qos)
    {
        uorbc_platform_log("QOS1 topic need use queue mode");
        return orb_advertise_multi_queue(meta, data, instance, UORBC_QUEUE_MSG_NUM);
    }

    int inst = -1;
    int orb_id = -1;
    int task_id = -1;
    int i = 0;
    p_orb_data_t p = NULL;
    //	uorbc_platform_log( "orb_advertise_multi" );

    orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb name:%s o_size：%d orb_id:%d", meta->o_name, meta->o_size, orb_id);

    if (orb_id < 0 || orb_id >= TOTAL_UORBC_NUM)
    {
        return NULL;
    }
    else
    {
        if (NULL == instance)
        {
            inst = 0;
        }
        else
        {
            inst = *instance;
        }

        uorbc_platform_log("inst:%d", inst);
        p_orb_data_t *orb = uorbc_instance();
        p = &(orb[orb_id][inst]);

        uorbc_platform_log("set queue_mode as false, this is nonqueue mode");
        p->queue_mode = false;
        p_nonqueue_mode_t nonq = &(p->meta.nonq);

        if (/* ( mod_uorbc_utils_ismulti( orb_id ) ) &&*/ (inst >= 0) && (inst < UORB_MULTI_MAX_INSTANCES))
        {
            uorbc_platform_sem_wait(&(p->sem));
            nonq->published = false;
            //            int atomic_state = orb_lock();
            if (NULL == nonq->data)
            {
                nonq->data = (uint8_t *)uorbc_platform_malloc(sizeof(uint8_t) * meta->o_size);
                uorbc_platform_assert(nonq->data);
                memset(nonq->data, 0x00, (sizeof(uint8_t) * meta->o_size));
            }
            uorbc_platform_memcpy(nonq->data, data, meta->o_size);
            //            orb_unlock(atomic_state);
            p->orb_id = orb_id;
            p->last_updated_time = time(NULL);
            nonq->published = true;
            task_id = get_subscriber_id();
            for (i = 0; i < UORB_MAX_SUB_NUM; ++i)
            {
                nonq->authority_list[i] = -1;
            }
            uorbc_platform_sem_post(&(p->sem));
        }
    }

    if (NULL == p)
    {
        uorbc_platform_log("advert fail");
    }
    else
    {
        uorbc_platform_log("advert succ");
    }

    return (orb_advert_t)p;
}
```

```yaml
name: Function Call 20
relation:
  type: Call
  items:
    -   from: Function:'orb_advertise_multi'
        to: Function:'orb_advertise_multi_queue'
        loc: mod_uorbc:9:16
    -   from: Function:'orb_advertise_multi'
        to: Function:'uorbc_instance'
        loc: mod_uorbc:39:29
    -   from: Function:'orb_advertise_multi'
        to: Function:'get_subscriber_id'
        loc: mod_uorbc:62:23
```

```cpp
//// mod_uorbc.c
static p_orb_data_t *uorbc_instance(void){}
static int get_subscriber_id(void){}
orb_advert_t orb_advertise_multi_queue(const struct orb_metadata *meta, const void *data, int *instance,
                                       unsigned int queue_size)
{
    p_orb_data_t p = NULL;
    int inst = -1;
    int orb_id = -1;
    int task_id = -1;
    int i = 0;

    orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb name:%s o_size:%d orb_id:%d queue_size:%d", meta->o_name, meta->o_size, orb_id, queue_size);

    if (orb_id < 0 || orb_id >= TOTAL_UORBC_NUM)
    {
        return NULL;
    }
    else
    {
        if (NULL == instance)
        {
            inst = 0;
        }
        else
        {
            inst = *instance;
        }

        uorbc_platform_log("inst:%d", inst);

        p_orb_data_t *orb = uorbc_instance();
        p = &(orb[orb_id][inst]);
        p->queue_mode = true;
        p_queue_mode_t q = &(p->meta.q);

        if (/* ( mod_uorbc_utils_ismulti( orb_id ) ) && */ (inst >= 0) && (inst < UORB_MULTI_MAX_INSTANCES))
        {
            uorbc_platform_sem_wait(&(p->sem));

            uint32_t payload_len = sizeof(queue_payload_header_t) + meta->o_size;
            q->queue_perpayload_len = ring_buffer_to_the_power_of_2(payload_len);
            q->one_queue_msg = (uint8_t *)uorbc_platform_malloc(sizeof(uint8_t) * (q->queue_perpayload_len));
            uorbc_platform_assert(q->one_queue_msg);
            memset(q->one_queue_msg, 0x00, (sizeof(uint8_t) * (q->queue_perpayload_len)));
            q->queue = ring_buffer_init(queue_size, q->queue_perpayload_len);
            uorbc_platform_assert(q->queue);
            q->qbuf_size = ring_buffer_get_size(q->queue);
            uorbc_platform_log("meta->o_name:%s qbuf_size:%d", meta->o_name, q->qbuf_size);
            uorbc_platform_log("queue_perpayload_len:%d", q->queue_perpayload_len);

            /* if no space, dequeue a payload msg to p->data */
            if (ring_buffer_left_space(q->queue) < (q->queue_perpayload_len))
            {
                ring_buffer_dequeue_arr(q->queue, q->one_queue_msg, q->queue_perpayload_len);
            }

            /* publish a initial msg */
            p_queue_payload_header_t queue_payload = (p_queue_payload_header_t)(q->one_queue_msg);
            queue_payload->timestamp = time(NULL);
            queue_payload->try_count = 1;
            memset(queue_payload->authority_list, -1, sizeof(queue_payload->authority_list));
            if (NULL != data)
            {
                memcpy(queue_payload->buf, data, meta->o_size);
            }
            ring_buffer_queue_arr(q->queue, (char *)queue_payload, q->queue_perpayload_len);

            p->orb_id = orb_id;
            p->last_updated_time = time(NULL);

            q->cur_msg_pos++;
            q->queue_size = queue_size;
            uorbc_platform_log("cur_msg_pos:%d, last_gen is 0, queue_size:%d", q->cur_msg_pos, queue_size);

            task_id = get_subscriber_id();

            for (i = 0; i < UORB_MAX_SUB_NUM; ++i)
            {
                q->queue_lastmsg_pos[i] = 0;
            }

            uorbc_platform_sem_post(&(p->sem));
        }
    }

    if (NULL == p)
    {
        uorbc_platform_log("advert fail");
    }
    else
    {
        uorbc_platform_log("advert succ");
    }

    return (orb_advert_t)p;
}
```

```yaml
name: Function Call 21
relation:
  type: Call
  items:
    -   from: Function:'orb_advertise_multi_queue'
        to: Function:'uorbc_instance'
        loc: mod_uorbc:33:29
    -   from: Function:'orb_advertise_multi_queue'
        to: Function:'get_subscriber_id'
        loc: mod_uorbc:77:23
```

```cpp
//// mod_uorbc.c
static p_orb_data_t *uorbc_instance(void){}
int orb_publish(const struct orb_metadata *meta, uint8_t retry, const void *data)
{
    int orb_id = -1;
    int multi_pos = 0;
    size_t buffer_space = 0;

    orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb name:%s orb_id:%d", meta->o_name, orb_id);

    if (orb_id < 0 || orb_id >= TOTAL_UORBC_NUM)
    {
        uorbc_platform_log_err("invalid orb_id:%d", orb_id);
        return -1;
    }
    else
    {
        if (mod_uorbc_utils_ismulti(orb_id))
        {
            multi_pos = mod_uorbc_utils_multi_getpos(orb_id);
            if (UORBC_NONE == multi_pos)
            {
                uorbc_platform_log_err("invalid orb_id:%d", orb_id);
                return -1;
            }
        }

        p_orb_data_t *orb = uorbc_instance();
        p_orb_data_t p = &(orb[orb_id][multi_pos]);

        if (false == p->queue_mode)
        { /* nonqueue mode */
            p_nonqueue_mode_t nonq = &(p->meta.nonq);
            uorbc_platform_sem_wait(&(p->sem));
            nonq->published = false;
            //            int atomic_state = orb_lock();
            uorbc_platform_memcpy(nonq->data, data, meta->o_size);
            //            orb_unlock(atomic_state);
            p->orb_id = orb_id;
            p->last_updated_time = time(NULL);
            nonq->published = true;
            for (int i = 0; i < UORB_MAX_SUB_NUM; ++i)
            {
                nonq->authority_list[i] = -1; /* this is a brand new msg */
            }
            uorbc_platform_sem_post(&(p->sem));
        }
        else
        { /* queue mode */
            p_queue_mode_t q = &(p->meta.q);
            uorbc_platform_sem_wait(&(p->sem));

            /* if no space in ringbuffer */
            buffer_space = ring_buffer_left_space(q->queue);
            if (buffer_space < (q->queue_perpayload_len))
            {
                ring_buffer_dequeue_arr(q->queue, q->one_queue_msg, (q->queue_perpayload_len));
            }

            p_queue_payload_header_t one_msg = NULL;
            if (UORB_PUB_RETRY == retry)
            {
                one_msg = (p_queue_payload_header_t)data;
                one_msg->try_count++;
            }
            else
            { /* this is a brand new msg */
                one_msg = (p_queue_payload_header_t)q->one_queue_msg;
                memset(one_msg, 0x00, q->queue_perpayload_len);
                one_msg->timestamp = time(NULL);
                one_msg->try_count = 1;
                memset(one_msg->authority_list, -1, sizeof(one_msg->authority_list));
                memcpy(one_msg->buf, data, meta->o_size);
            }

            /* DEBUGURLVGL TODO: 如果qos是1,将消息推入qos队列,qos队列重发后将消息出队列 */
            if (UORBC_QOS_1 == meta->qos)
            {
                // TODO: mod_uorbc_qos_enqueue
            }

            ring_buffer_queue_arr(q->queue, one_msg, q->queue_perpayload_len);

            p->orb_id = orb_id;
            p->last_updated_time = time(NULL);
            q->cur_msg_pos++;

            uorbc_platform_sem_post(&(p->sem));
        }
    }

    return 0;
}
```

```yaml
name: Function Call 22
relation:
  type: Call
  items:
    -   from: Function:'orb_publish'
        to: Function:'uorbc_instance'
        loc: mod_uorbc:29:29
```

```cpp
//// mod_uorbc.c
static int get_subscriber_id(void){}
static p_orb_data_t *uorbc_instance(void){}
int orb_subscribe(const struct orb_metadata *meta)
{
    int ret = -1;
    int orb_id = -1;
    int instance = 0;
    int task_id = -1;
    uint32_t cur_msg_pos = 0;
    uint queue_size = 0;

    if (meta == NULL || meta->o_name == NULL)
    {
        uorbc_platform_log_err("meta == NULL || meta->o_name == NULL");
        return -1;
    }

    orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb name:%s orb_id:%d", meta->o_name, orb_id);

    if (orb_id > 0 && orb_id < TOTAL_UORBC_NUM)
    {
        if (mod_uorbc_utils_ismulti(orb_id))
        { /* 使用orb_subscribe_multi订阅multi topic */
            return ret;
        }

        task_id = get_subscriber_id();

        //        int atomic_state = orb_lock();
        p_orb_data_t *orb = uorbc_instance();
        p_orb_data_t p = &(orb[orb_id][instance]);

        uorbc_platform_sem_wait(&(p->sem));
        for (int i = 0; i < UORB_MAX_SUB_NUM; ++i)
        {
            if (p->registered_list[i] == task_id)
            {
                ret = (int)((orb_id << 4) | (instance));
                break;
            }

            if (p->registered_list[i] == -1)
            {
                p->registered_list[i] = task_id;
                uorbc_platform_log("p->registered_list[%d]:%d task_id:%d,p:%p",
                                   i, p->registered_list[i], task_id, p);

                // If there were any previous publications allow the subscriber to read them
                if (true == p->queue_mode)
                {
                    p_queue_mode_t q = &(p->meta.q);
                    cur_msg_pos = q->cur_msg_pos;
                    queue_size = q->queue_size;
                    q->queue_lastmsg_pos[i] = cur_msg_pos - uorbc_min(cur_msg_pos, queue_size);
                    uorbc_platform_log("cur_msg_pos:%d", q->cur_msg_pos);
                }

                ret = (int)((orb_id << 4) | (instance));
                break;
            }
        }
        uorbc_platform_sem_post(&(p->sem));
        //        orb_unlock(atomic_state);
    }
    else
    {
        uorbc_platform_log_err("error orb_id:%d", orb_id);
        return -1;
    }

    uorbc_platform_log("sub handle:%d", ret);

    return ret;
}
```

```yaml
name: Function Call 23
relation:
  type: Call
  items:
    -   from: Function:'orb_subscribe'
        to: Function:'get_subscriber_id'
        loc: mod_uorbc:29:19
    -   from: Function:'orb_subscribe'
        to: Function:'uorbc_instance'
        loc: mod_uorbc:32:29
```

```cpp
//// mod_uorbc.c
static int get_subscriber_id(void){}
static p_orb_data_t *uorbc_instance(void){}
int orb_subscribe_multi(const struct orb_metadata *meta, unsigned instance)
{
    int ret = -1;
    int orb_id = -1;
    int task_id = -1;
    int i = 0;

    if (meta == NULL || meta->o_name == NULL)
    {
        uorbc_platform_log_err("meta == NULL || meta->o_name == NULL");
        return -1;
    }

    orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb name:%s orb_id:%d", meta->o_name, orb_id);

    if (orb_id >= 0 && orb_id < TOTAL_UORBC_NUM && mod_uorbc_utils_ismulti(orb_id))
    {
        task_id = get_subscriber_id();
        //        int atomic_state = orb_lock();
        p_orb_data_t *orb = uorbc_instance();
        p_orb_data_t p = &(orb[orb_id][instance]);
        uorbc_platform_sem_wait(&(p->sem));
        for (i = 0; i < UORB_MAX_SUB_NUM; ++i)
        {
            if (p->registered_list[i] == task_id)
            {
                ret = (int)((orb_id << 4) | (instance));
                break;
            }
            if (p->registered_list[i] == -1)
            {
                p->registered_list[i] = task_id;
                ret = (int)((orb_id << 4) | (instance));
                break;
            }
        }
        uorbc_platform_sem_post(&(p->sem));
        //        orb_unlock(atomic_state);
    }

    uorbc_platform_log("sub:%d", ret);

    return ret;
}
```

```yaml
name: Function Call 24
relation:
  type: Call
  items:
    -   from: Function:'orb_subscribe_multi'
        to: Function:'get_subscriber_id'
        loc: mod_uorbc:22:19
    -   from: Function:'orb_subscribe_multi'
        to: Function:'uorbc_instance'
        loc: mod_uorbc:24:29
```

```cpp
//// mod_uorbc.c
static p_orb_data_t *uorbc_instance(void){}
static int get_subscriber_id(void){}
int orb_unsubscribe(int handle)
{
    int task_id = -1;
    int orb_id = -1;
    int instance = -1;
    int i = 0;

    if (handle < 0)
    {
        return -1;
    }

    orb_id = (handle >> 4);
    instance = handle - ((handle >> 4) << 4);

    p_orb_data_t *orb = uorbc_instance();
    p_orb_data_t p = &(orb[orb_id][instance]);

    if (orb_id >= 0 && orb_id < TOTAL_UORBC_NUM)
    {
        task_id = get_subscriber_id();
        //        int atomic_state = orb_lock();
        uorbc_platform_sem_wait(&(p->sem));
        for (i = 0; i < UORB_MAX_SUB_NUM; ++i)
        {
            if (p->registered_list[i] == task_id)
            {
                p->registered_list[i] = -1;
                uorbc_platform_sem_post(&(p->sem));
                //                orb_unlock(atomic_state);
                return 0;
            }
        }
        uorbc_platform_sem_post(&(p->sem));
        //        orb_unlock(atomic_state);
    }

    return -1;
}
```

```yaml
name: Function Call 25
relation:
  type: Call
  items:
    -   from: Function:'orb_unsubscribe'
        to: Function:'uorbc_instance'
        loc: mod_uorbc:28:25
    -   from: Function:'orb_unsubscribe'
        to: Function:'get_subscriber_id'
        loc: mod_uorbc:23:19
```

```cpp
//// mod_uorbc.c
static p_orb_data_t *uorbc_instance(void)
static int orb_check(int handle){}
static int get_subscriber_id(void){}
static int get_subscriber_id_pos(p_orb_data_t p, int subscriber_id){}
static bool subscriber_id_hit(p_queue_payload_header_t queue_msg, int subscriber_id){}
int orb_copy(const struct orb_metadata *meta, int handle, void *buffer)
{
    int multi_pos = -1;
    int task_id = -1;
    int i = 0;
    int registered_len = 0, authority_len = 0;
    uint32_t cur_msg_pos = 0;
    uint queue_size = 0;
    int sub_val = 0;
    ring_buffer_t *p_ring = NULL;
    size_t peek_pos = 0;

    int orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb_id:%d o_name:%s o_size:%d handle:%d", orb_id, meta->o_name, meta->o_size, handle);

    if (orb_id != -1 && orb_id == (handle >> 4))
    {
        multi_pos = handle - ((handle >> 4) << 4);
        p_orb_data_t *orb = uorbc_instance();
        p_orb_data_t p = &(orb[orb_id][multi_pos]);
        uorbc_platform_sem_wait(&(p->sem));

        if (orb_check(handle) <= 0)
        {
            uorbc_platform_log("error or no msg");
            uorbc_platform_sem_post(&(p->sem));
            return -1;
        }

        if (multi_pos > 0 && !mod_uorbc_utils_ismulti(orb_id))
        {
            uorbc_platform_sem_post(&(p->sem));
            uorbc_platform_log_err("error multi_pos:%d not multi", multi_pos);
            return -1;
        }
        else if (multi_pos >= 0 && multi_pos < UORB_MULTI_MAX_INSTANCES)
        {
            task_id = get_subscriber_id();

            //				int atomic_state = orb_lock();
            if (true == p->queue_mode)
            {
                p_queue_mode_t q = &(p->meta.q);
                cur_msg_pos = q->cur_msg_pos;
                int idpos = get_subscriber_id_pos(p, task_id);
                if (-1 == idpos)
                {
                    uorbc_platform_sem_post(&(p->sem));
                    uorbc_platform_log_err("error idpos(%d)", idpos);
                    return -1;
                }

                queue_size = q->queue_size;
                if (cur_msg_pos > (q->queue_lastmsg_pos[idpos] + queue_size))
                {
                    /* Reader is too far behind: some messages are lost */
                    q->queue_lastmsg_pos[idpos] = cur_msg_pos - queue_size;
                }

                uint32_t msg_pan = q->cur_msg_pos - q->queue_lastmsg_pos[idpos];
                uint32_t *que_buf = uorbc_platform_malloc(sizeof(uint32_t) + msg_pan * (meta->o_size));
                uorbc_platform_assert(que_buf);
                memset(que_buf, 0x00, (sizeof(uint32_t) + msg_pan * (meta->o_size)));
                que_buf[0] = msg_pan;
                uint8_t *_que_buf = (uint8_t *)(&que_buf[1]);

                do
                {
                    p_ring = q->queue;
                    peek_pos = (q->queue_lastmsg_pos[idpos]) * (q->queue_perpayload_len);
                    peek_pos %= (q->qbuf_size);
                    p_queue_payload_header_t queue_msg = (p_queue_payload_header_t)ring_buffer_pos_pointer(p_ring, peek_pos);
                    if (queue_msg->try_count > 1)
                    { /* this is a retry msg */
                        if (true == subscriber_id_hit(queue_msg, task_id))
                        { /* this receiver has received this msg */
                            /* noneed get msg again */
                            if (que_buf)
                            {
                                free(que_buf);
                                que_buf = NULL;
                                _que_buf = NULL;
                            }
                        }
                        else
                        {
                            ring_buffer_just_peek(p_ring, _que_buf, peek_pos + sizeof(queue_payload_header_t), meta->o_size);
                            queue_msg->authority_list[idpos] = task_id; /* this msg is received by this subscriber */
                            _que_buf += meta->o_size;
                        }
                    }
                    else
                    {
                        ring_buffer_just_peek(p_ring, _que_buf, peek_pos + sizeof(queue_payload_header_t), meta->o_size);
                        queue_msg->authority_list[idpos] = task_id; /* this msg is received by this subscriber */
                        _que_buf += meta->o_size;
                    }

                    if (q->queue_lastmsg_pos[idpos] < q->cur_msg_pos)
                    {
                        q->queue_lastmsg_pos[idpos]++;
                    }
                    else
                    {
                        q->queue_lastmsg_pos[idpos] = q->cur_msg_pos;
                    }
                } while (q->queue_lastmsg_pos[idpos] != q->cur_msg_pos);
                *((uint32_t *)buffer) = que_buf;
            }
            else
            { /* nonqueue mode */
                p_nonqueue_mode_t nonq = &(p->meta.nonq);
                uorbc_platform_memcpy(buffer, nonq->data, meta->o_size);

                for (int i = 0; i < UORB_MAX_SUB_NUM; ++i)
                {
                    if (p->registered_list[i] != -1)
                    {
                        ++registered_len;
                    }
                    if (nonq->authority_list[i] != -1)
                    {
                        ++authority_len;
                    }
                }

                if (authority_len >= registered_len)
                { /* TODO: this judgement is not strict */
                    nonq->published = false;
                }
            }
            //				orb_unlock ( atomic_state );
        }

        uorbc_platform_sem_post(&(p->sem));
    }

    return 0;
}
```

```yaml
name: Function Call 25
relation:
  type: Call
  items:
    -   from: Function:'orb_copy'
        to: Function:'uorbc_instance'
        loc: mod_uorbc:25:29
    -   from: Function:'orb_copy'
        to: Function:'orb_check'
        loc: mod_uorbc:29:13
    -   from: Function:'orb_copy'
        to: Function:'get_subscriber_id'
        loc: mod_uorbc:44:23
    -   from: Function:'orb_copy'
        to: Function:'get_subscriber_id_pos'
        loc: mod_uorbc:51:29
    -   from: Function:'orb_copy'
        to: Function:'subscriber_id_hit'
        loc: mod_uorbc:81:37
```


```cpp
//// mod_uorbc.c
static p_orb_data_t *uorbc_instance(void){}
static int get_subscriber_id(void){}
static int get_subscriber_id_pos(p_orb_data_t p, int subscriber_id){}
static int orb_check(int handle)
{
    if (handle < 0)
    {
        uorbc_platform_log_err("handle < 0");
        return -1;
    }

    int orb_id = (handle >> 4);
    int multi_pos = handle - ((handle >> 4) << 4);

    if (orb_id < 0 || orb_id >= TOTAL_UORBC_NUM ||
        multi_pos < 0 || multi_pos >= UORB_MULTI_MAX_INSTANCES)
    {
        uorbc_platform_log_err("error orb_id:%d multi_pos:%d", orb_id, multi_pos);
        return -1;
    }

    p_orb_data_t *orb = uorbc_instance();
    p_orb_data_t p = &(orb[orb_id][multi_pos]);

    int task_id = get_subscriber_id();
    int hit = 0;

    hit = get_subscriber_id_pos(p, task_id);
    if (-1 == hit)
    {
        uorbc_platform_log_err("this subcriber(%d) orb_id:%d multi_pos:%d dont subscribe this topic",
                               task_id, orb_id, multi_pos);
        return -1;
    }

    bool authorised = false;
    if (true == p->queue_mode)
    { /* queue mode */
        p_queue_mode_t q = &(p->meta.q);
        int msg_span = 0;
        if (q->cur_msg_pos != q->queue_lastmsg_pos[hit])
        {
            msg_span = q->cur_msg_pos - q->queue_lastmsg_pos[hit];
        }
        else
        {
        }

        return msg_span;
    }
    else
    { /* nonqueue mode */
        p_nonqueue_mode_t nonq = &(p->meta.nonq);
        for (int i = 0; i < UORB_MAX_SUB_NUM; ++i)
        {
            if (nonq->authority_list[i] == task_id)
            {
                authorised = true;
                break;
            }
        }

        if (nonq->published && !authorised)
        {
            return 1;
        }
    }

    return 0;
}
```

```yaml
name: Function Call 26
relation:
  type: Call
  items:
    -   from: Function:'orb_check'
        to: Function:'uorbc_instance'
        loc: mod_uorbc:22:25
    -   from: Function:'orb_check'
        to: Function:'get_subscriber_id'
        loc: mod_uorbc:25:19
    -   from: Function:'orb_check'
        to: Function:'get_subscriber_id_pos'
        loc: mod_uorbc:28:11
```

```cpp
//// mod_uorbc.c
int orb_exists(const struct orb_metadata *meta, int instance){}
int orb_group_count(const struct orb_metadata *meta)
{
    unsigned instance = 0;

    for (int i = 0; i < UORB_MULTI_MAX_INSTANCES; ++i)
    {
        if (orb_exists(meta, i) == 0)
        {
            ++instance;
        }
    }

    return instance;
}
```

```yaml
name: Function Call 27
relation:
  type: Call
  items:
    -   from: Function:'orb_group_count'
        to: Function:'orb_exists'
        loc: mod_uorbc:8:13
```

```cpp
//// mod_uorbc.c
static void orb_init_multi(int i){}
static void orb_init_nomulti(int i){}
void orb_init(void)
{
    uorbc_platform_log("orb_init");

    for (int i = ORBC_SYS_STATE; i < TOTAL_UORBC_NUM; ++i)
    {
        if (mod_uorbc_utils_ismulti(i))
        {
            orb_init_multi(i);
        }
        else
        {
            orb_init_nomulti(i);
        }
    }
}
```

```yaml
name: Function Call 28
relation:
  type: Call
  items:
    -   from: Function:'orb_init'
        to: Function:'orb_init_multi'
        loc: mod_uorbc:11:13
    -   from: Function:'orb_init'
        to: Function:'orb_init_nomulti'
        loc: mod_uorbc:13:15
```

###### Cross-file Function Call

```cpp
//// mod_uorbc_platfrom.c
int mod_uorbc_platform_thread_create(void* id, void* (*start_rtn)(void*), void* arg){}
```
```cpp
//// mod_uorbc_test.c
void mod_uorbc_test(void)
{
    uorbc_platform_log("\n\n\ntest uorbc 0c\n");
    uorbc_platform_delay_ms(3000);

    orb_init();

    struct sysinfo_s init_sysinfo = {
        .machine_type = "Dryer",
        .machine_number = 0,
        .mbn = 0,
        .net = 0};
    orb_advertise(ORB_ID(sysinfo), &init_sysinfo);

    uorbc_platform_pthread_t thread_pub = 0;
    uorbc_platform_pthread_t thread_pub1 = 0;
    uorbc_platform_pthread_t thread_pub2 = 0;
    uorbc_platform_pthread_t thread_pub3 = 0;
    uorbc_platform_pthread_t thread_pub4 = 0;
    uorbc_platform_pthread_t thread_pub5 = 0;
    uorbc_platform_pthread_t thread_pub6 = 0;
    mod_uorbc_platform_thread_create(&thread_pub, uorbc_test_thread_pub, NULL);
    mod_uorbc_platform_thread_create(&thread_pub1, uorbc_test_thread_pub1, NULL);
    mod_uorbc_platform_thread_create(&thread_pub2, uorbc_test_thread_pub2, NULL);
    mod_uorbc_platform_thread_create(&thread_pub3, uorbc_test_thread_pub3, NULL);
    mod_uorbc_platform_thread_create(&thread_pub4, uorbc_test_thread_pub4, NULL);
    mod_uorbc_platform_thread_create(&thread_pub5, uorbc_test_thread_pub5, NULL);
    mod_uorbc_platform_thread_create(&thread_pub6, uorbc_test_thread_pub6, NULL);

    uorbc_platform_pthread_t thread_sub = 0;
    uorbc_platform_pthread_t thread_sub1 = 0;
    uorbc_platform_pthread_t thread_sub2 = 0;
    uorbc_platform_pthread_t thread_sub3 = 0;
    uorbc_platform_pthread_t thread_sub4 = 0;
    uorbc_platform_pthread_t thread_sub5 = 0;
    uorbc_platform_pthread_t thread_sub6 = 0;
    mod_uorbc_platform_thread_create(&thread_sub, uorbc_test_thread_sub, NULL);
    mod_uorbc_platform_thread_create(&thread_sub1, uorbc_test_thread_sub1, NULL);
    mod_uorbc_platform_thread_create(&thread_sub2, uorbc_test_thread_sub2, NULL);
    mod_uorbc_platform_thread_create(&thread_sub3, uorbc_test_thread_sub3, NULL);
    mod_uorbc_platform_thread_create(&thread_sub4, uorbc_test_thread_sub4, NULL);
    mod_uorbc_platform_thread_create(&thread_sub5, uorbc_test_thread_sub5, NULL);
    mod_uorbc_platform_thread_create(&thread_sub6, uorbc_test_thread_sub6, NULL);
}
```

```yaml
name: Cross-file Function Call 1
relation:
  type: Call
  items:
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:26:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:27:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:28:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:29:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:30:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:31:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:32:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:37:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:38:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:39:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:40:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:41:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:42:5
    -   from: Function:'mod_uorbc_test'
        to: Function:'mod_uorbc_platform_thread_create'
        loc: mod_uorbc_test:43:5
```

```cpp
//// mod_uorbc_utils.c
int mod_uorbc_utils_get_orbid(const char* name){}
bool mod_uorbc_utils_ismulti(const int orb_id){}
```

```cpp
//// mod_uorbc.c
int orb_exists(const struct orb_metadata *meta, int instance)
{
    int orb_id = mod_uorbc_utils_get_orbid(meta->o_name);
    if (orb_id < 0 || orb_id >= TOTAL_UORBC_NUM)
    {
        return -1;
    }
    if (!mod_uorbc_utils_ismulti(orb_id) && instance > 0)
    {
        return -1;
    }

    p_orb_data_t *orb = uorbc_instance();
    p_orb_data_t p = &(orb[orb_id][instance]);

    if ((p->orb_id) != -1)
    {
        return 0;
    }

    return -1;
}
```

```yaml
name: Cross-file Function Call 2
relation:
  type: Call
  items:
    -   from: Function:'orb_exists'
        to: Function:'mod_uorbc_utils_get_orbid'
        loc: mod_uorbc:3:18
    -   from: Function:'orb_exists'
        to: Function:'mod_uorbc_utils_ismulti'
        loc: mod_uorbc:8:10
```

```cpp
//// mod_uorbc_utils.c
int mod_uorbc_utils_get_orbid(const char* name){}
```

```cpp
//// mod_uorbc.c
orb_advert_t orb_advertise_multi(const struct orb_metadata *meta, const void *data, int *instance)
{
    if (UORBC_QOS_1 == meta->qos)
    {
        uorbc_platform_log("QOS1 topic need use queue mode");
        return orb_advertise_multi_queue(meta, data, instance, UORBC_QUEUE_MSG_NUM);
    }

    int inst = -1;
    int orb_id = -1;
    int task_id = -1;
    int i = 0;
    p_orb_data_t p = NULL;
    //	uorbc_platform_log( "orb_advertise_multi" );

    orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb name:%s o_size：%d orb_id:%d", meta->o_name, meta->o_size, orb_id);

    if (orb_id < 0 || orb_id >= TOTAL_UORBC_NUM)
    {
        return NULL;
    }
    else
    {
        if (NULL == instance)
        {
            inst = 0;
        }
        else
        {
            inst = *instance;
        }

        uorbc_platform_log("inst:%d", inst);
        p_orb_data_t *orb = uorbc_instance();
        p = &(orb[orb_id][inst]);

        uorbc_platform_log("set queue_mode as false, this is nonqueue mode");
        p->queue_mode = false;
        p_nonqueue_mode_t nonq = &(p->meta.nonq);

        if (/* ( mod_uorbc_utils_ismulti( orb_id ) ) &&*/ (inst >= 0) && (inst < UORB_MULTI_MAX_INSTANCES))
        {
            uorbc_platform_sem_wait(&(p->sem));
            nonq->published = false;
            //            int atomic_state = orb_lock();
            if (NULL == nonq->data)
            {
                nonq->data = (uint8_t *)uorbc_platform_malloc(sizeof(uint8_t) * meta->o_size);
                uorbc_platform_assert(nonq->data);
                memset(nonq->data, 0x00, (sizeof(uint8_t) * meta->o_size));
            }
            uorbc_platform_memcpy(nonq->data, data, meta->o_size);
            //            orb_unlock(atomic_state);
            p->orb_id = orb_id;
            p->last_updated_time = time(NULL);
            nonq->published = true;
            task_id = get_subscriber_id();
            for (i = 0; i < UORB_MAX_SUB_NUM; ++i)
            {
                nonq->authority_list[i] = -1;
            }
            uorbc_platform_sem_post(&(p->sem));
        }
    }

    if (NULL == p)
    {
        uorbc_platform_log("advert fail");
    }
    else
    {
        uorbc_platform_log("advert succ");
    }

    return (orb_advert_t)p;
}
```

```yaml
name: Cross-file Function Call 3
relation:
  type: Call
  items:
    -   from: Function:'orb_advertise_multi'
        to: Function:'mod_uorbc_utils_get_orbid'
        loc: mod_uorbc:16:14
```

```cpp
mod_uorbc_utils.c
int mod_uorbc_utils_get_orbid(const char* name){}
```

```cpp
//// mod_uorbc_ringbuffer.c
uint32_t ring_buffer_to_the_power_of_2(uint32_t in_data){}
ring_buffer_t *ring_buffer_init(size_t queue_size, size_t obj_size){}
size_t ring_buffer_get_size(ring_buffer_t *p){}
ring_buffer_size_t ring_buffer_left_space(ring_buffer_t *buffer){}
ring_buffer_size_t ring_buffer_dequeue_arr(ring_buffer_t *buffer, char *data, ring_buffer_size_t len){}
void ring_buffer_queue_arr(ring_buffer_t *buffer, const char *data, ring_buffer_size_t size){}

```

```cpp
//// mod_uorbc.c
orb_advert_t orb_advertise_multi_queue(const struct orb_metadata *meta, const void *data, int *instance,
                                       unsigned int queue_size)
{
    p_orb_data_t p = NULL;
    int inst = -1;
    int orb_id = -1;
    int task_id = -1;
    int i = 0;

    orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb name:%s o_size:%d orb_id:%d queue_size:%d", meta->o_name, meta->o_size, orb_id, queue_size);

    if (orb_id < 0 || orb_id >= TOTAL_UORBC_NUM)
    {
        return NULL;
    }
    else
    {
        if (NULL == instance)
        {
            inst = 0;
        }
        else
        {
            inst = *instance;
        }

        uorbc_platform_log("inst:%d", inst);

        p_orb_data_t *orb = uorbc_instance();
        p = &(orb[orb_id][inst]);
        p->queue_mode = true;
        p_queue_mode_t q = &(p->meta.q);

        if (/* ( mod_uorbc_utils_ismulti( orb_id ) ) && */ (inst >= 0) && (inst < UORB_MULTI_MAX_INSTANCES))
        {
            uorbc_platform_sem_wait(&(p->sem));

            uint32_t payload_len = sizeof(queue_payload_header_t) + meta->o_size;
            q->queue_perpayload_len = ring_buffer_to_the_power_of_2(payload_len);
            q->one_queue_msg = (uint8_t *)uorbc_platform_malloc(sizeof(uint8_t) * (q->queue_perpayload_len));
            uorbc_platform_assert(q->one_queue_msg);
            memset(q->one_queue_msg, 0x00, (sizeof(uint8_t) * (q->queue_perpayload_len)));
            q->queue = ring_buffer_init(queue_size, q->queue_perpayload_len);
            uorbc_platform_assert(q->queue);
            q->qbuf_size = ring_buffer_get_size(q->queue);
            uorbc_platform_log("meta->o_name:%s qbuf_size:%d", meta->o_name, q->qbuf_size);
            uorbc_platform_log("queue_perpayload_len:%d", q->queue_perpayload_len);

            /* if no space, dequeue a payload msg to p->data */
            if (ring_buffer_left_space(q->queue) < (q->queue_perpayload_len))
            {
                ring_buffer_dequeue_arr(q->queue, q->one_queue_msg, q->queue_perpayload_len);
            }

            /* publish a initial msg */
            p_queue_payload_header_t queue_payload = (p_queue_payload_header_t)(q->one_queue_msg);
            queue_payload->timestamp = time(NULL);
            queue_payload->try_count = 1;
            memset(queue_payload->authority_list, -1, sizeof(queue_payload->authority_list));
            if (NULL != data)
            {
                memcpy(queue_payload->buf, data, meta->o_size);
            }
            ring_buffer_queue_arr(q->queue, (char *)queue_payload, q->queue_perpayload_len);

            p->orb_id = orb_id;
            p->last_updated_time = time(NULL);

            q->cur_msg_pos++;
            q->queue_size = queue_size;
            uorbc_platform_log("cur_msg_pos:%d, last_gen is 0, queue_size:%d", q->cur_msg_pos, queue_size);

            task_id = get_subscriber_id();

            for (i = 0; i < UORB_MAX_SUB_NUM; ++i)
            {
                q->queue_lastmsg_pos[i] = 0;
            }

            uorbc_platform_sem_post(&(p->sem));
        }
    }

    if (NULL == p)
    {
        uorbc_platform_log("advert fail");
    }
    else
    {
        uorbc_platform_log("advert succ");
    }

    return (orb_advert_t)p;
}
```

```yaml
name: Cross-file Function Call 4
relation:
  type: Call
  items:
    -   from: Function:'orb_advertise_multi_queue'
        to: Function:'mod_uorbc_utils_get_orbid'
        loc: mod_uorbc:10:14
    -   from: Function:'orb_advertise_multi_queue'
        to: Function:'ring_buffer_to_the_power_of_2'
        loc: mod_uorbc:41:39
    -   from: Function:'orb_advertise_multi_queue'
        to: Function:'ring_buffer_init'
        loc: mod_uorbc:45:24
    -   from: Function:'orb_advertise_multi_queue'
        to: Function:'ring_buffer_get_size'
        loc: mod_uorbc:47:28
    -   from: Function:'orb_advertise_multi_queue'
        to: Function:'ring_buffer_left_space'
        loc: mod_uorbc:52:17
    -   from: Function:'orb_advertise_multi_queue'
        to: Function:'ring_buffer_dequeue_arr'
        loc: mod_uorbc:54:17
    -   from: Function:'orb_advertise_multi_queue'
        to: Function:'ring_buffer_queue_arr'
        loc: mod_uorbc:66:13
```

```cpp
//// mod_uorbc_utils.c
int mod_uorbc_utils_get_orbid(const char* name){}
bool mod_uorbc_utils_ismulti(const int orb_id){}
```

```cpp
//// mod_uorbc.c
int orb_publish_auto(const struct orb_metadata *meta, orb_advert_t *handle, const void *data, int *instance)
{
    int orb_id = -1;

    if (*handle == NULL)
    {
        int orb_id = mod_uorbc_utils_get_orbid(meta->o_name);
        if (mod_uorbc_utils_ismulti(orb_id))
        {
            *handle = orb_advertise_multi(meta, data, instance);
        }
        else
        {
            *handle = orb_advertise(meta, data);
            if (instance)
            {
                *instance = 0;
            }
        }

        if (*handle != NULL)
        {
            return 0;
        }
    }
    else
    {
        return orb_publish(meta, UORB_PUB_NORMAL, data);
    }

    return -1;
}
```

```yaml
name: Cross-file Function Call 6
relation:
  type: Call
  items:
    -   from: Function:'orb_publish_auto'
        to: Function:'mod_uorbc_utils_get_orbid'
        loc: mod_uorbc:7:22
    -   from: Function:'orb_publish_auto'
        to: Function:'mod_uorbc_utils_ismulti'
        loc: mod_uorbc:8:13
```

```cpp
//// mod_uorbc_utils.c
int mod_uorbc_utils_get_orbid(const char* name){}
bool mod_uorbc_utils_ismulti(const int orb_id){}
E_UORBC_ID mod_uorbc_utils_multi_getpos(const int orb_id){}

```

```cpp
//// mod_uorbc_ringbuffer.c
ring_buffer_size_t ring_buffer_left_space(ring_buffer_t *buffer){}
ring_buffer_size_t ring_buffer_dequeue_arr(ring_buffer_t *buffer, char *data, ring_buffer_size_t len){}
void ring_buffer_queue_arr(ring_buffer_t *buffer, const char *data, ring_buffer_size_t size){}
```

```cpp
//// mod_uorbc.c
int orb_publish(const struct orb_metadata *meta, uint8_t retry, const void *data)
{
    int orb_id = -1;
    int multi_pos = 0;
    size_t buffer_space = 0;

    orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb name:%s orb_id:%d", meta->o_name, orb_id);

    if (orb_id < 0 || orb_id >= TOTAL_UORBC_NUM)
    {
        uorbc_platform_log_err("invalid orb_id:%d", orb_id);
        return -1;
    }
    else
    {
        if (mod_uorbc_utils_ismulti(orb_id))
        {
            multi_pos = mod_uorbc_utils_multi_getpos(orb_id);
            if (UORBC_NONE == multi_pos)
            {
                uorbc_platform_log_err("invalid orb_id:%d", orb_id);
                return -1;
            }
        }

        p_orb_data_t *orb = uorbc_instance();
        p_orb_data_t p = &(orb[orb_id][multi_pos]);

        if (false == p->queue_mode)
        { /* nonqueue mode */
            p_nonqueue_mode_t nonq = &(p->meta.nonq);
            uorbc_platform_sem_wait(&(p->sem));
            nonq->published = false;
            //            int atomic_state = orb_lock();
            uorbc_platform_memcpy(nonq->data, data, meta->o_size);
            //            orb_unlock(atomic_state);
            p->orb_id = orb_id;
            p->last_updated_time = time(NULL);
            nonq->published = true;
            for (int i = 0; i < UORB_MAX_SUB_NUM; ++i)
            {
                nonq->authority_list[i] = -1; /* this is a brand new msg */
            }
            uorbc_platform_sem_post(&(p->sem));
        }
        else
        { /* queue mode */
            p_queue_mode_t q = &(p->meta.q);
            uorbc_platform_sem_wait(&(p->sem));

            /* if no space in ringbuffer */
            buffer_space = ring_buffer_left_space(q->queue);
            if (buffer_space < (q->queue_perpayload_len))
            {
                ring_buffer_dequeue_arr(q->queue, q->one_queue_msg, (q->queue_perpayload_len));
            }

            p_queue_payload_header_t one_msg = NULL;
            if (UORB_PUB_RETRY == retry)
            {
                one_msg = (p_queue_payload_header_t)data;
                one_msg->try_count++;
            }
            else
            { /* this is a brand new msg */
                one_msg = (p_queue_payload_header_t)q->one_queue_msg;
                memset(one_msg, 0x00, q->queue_perpayload_len);
                one_msg->timestamp = time(NULL);
                one_msg->try_count = 1;
                memset(one_msg->authority_list, -1, sizeof(one_msg->authority_list));
                memcpy(one_msg->buf, data, meta->o_size);
            }

            /* DEBUGURLVGL TODO: 如果qos是1,将消息推入qos队列,qos队列重发后将消息出队列 */
            if (UORBC_QOS_1 == meta->qos)
            {
                // TODO: mod_uorbc_qos_enqueue
            }

            ring_buffer_queue_arr(q->queue, one_msg, q->queue_perpayload_len);

            p->orb_id = orb_id;
            p->last_updated_time = time(NULL);
            q->cur_msg_pos++;

            uorbc_platform_sem_post(&(p->sem));
        }
    }

    return 0;
}
```

```yaml
name: Cross-file Function Call 7
relation:
  type: Call
  items:
    -   from: Function:'orb_publish'
        to: Function:'mod_uorbc_utils_get_orbid'
        loc: mod_uorbc:7:14
    -   from: Function:'orb_publish'
        to: Function:'mod_uorbc_utils_ismulti'
        loc: mod_uorbc:18:13
    -   from: Function:'orb_publish'
        to: Function:'mod_uorbc_utils_multi_getpos'
        loc: mod_uorbc:20:25
    -   from: Function:'orb_publish'
        to: Function:'ring_buffer_left_space'
        loc: mod_uorbc:54:28
    -   from: Function:'orb_publish'
        to: Function:'ring_buffer_dequeue_arr'
        loc: mod_uorbc:57:17
    -   from: Function:'orb_publish'
        to: Function:'ring_buffer_queue_arr'
        loc: mod_uorbc:82:13
```

```cpp
//// mod_uorbc_utils.c
int mod_uorbc_utils_get_orbid(const char* name){}
bool mod_uorbc_utils_ismulti(const int orb_id){}
```

```cpp
//// mod_uorbc.c
int orb_subscribe(const struct orb_metadata *meta)
{
    int ret = -1;
    int orb_id = -1;
    int instance = 0;
    int task_id = -1;
    uint32_t cur_msg_pos = 0;
    uint queue_size = 0;

    if (meta == NULL || meta->o_name == NULL)
    {
        uorbc_platform_log_err("meta == NULL || meta->o_name == NULL");
        return -1;
    }

    orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb name:%s orb_id:%d", meta->o_name, orb_id);

    if (orb_id > 0 && orb_id < TOTAL_UORBC_NUM)
    {
        if (mod_uorbc_utils_ismulti(orb_id))
        { /* 使用orb_subscribe_multi订阅multi topic */
            return ret;
        }

        task_id = get_subscriber_id();

        //        int atomic_state = orb_lock();
        p_orb_data_t *orb = uorbc_instance();
        p_orb_data_t p = &(orb[orb_id][instance]);

        uorbc_platform_sem_wait(&(p->sem));
        for (int i = 0; i < UORB_MAX_SUB_NUM; ++i)
        {
            if (p->registered_list[i] == task_id)
            {
                ret = (int)((orb_id << 4) | (instance));
                break;
            }

            if (p->registered_list[i] == -1)
            {
                p->registered_list[i] = task_id;
                uorbc_platform_log("p->registered_list[%d]:%d task_id:%d,p:%p",
                                   i, p->registered_list[i], task_id, p);

                // If there were any previous publications allow the subscriber to read them
                if (true == p->queue_mode)
                {
                    p_queue_mode_t q = &(p->meta.q);
                    cur_msg_pos = q->cur_msg_pos;
                    queue_size = q->queue_size;
                    q->queue_lastmsg_pos[i] = cur_msg_pos - uorbc_min(cur_msg_pos, queue_size);
                    uorbc_platform_log("cur_msg_pos:%d", q->cur_msg_pos);
                }

                ret = (int)((orb_id << 4) | (instance));
                break;
            }
        }
        uorbc_platform_sem_post(&(p->sem));
        //        orb_unlock(atomic_state);
    }
    else
    {
        uorbc_platform_log_err("error orb_id:%d", orb_id);
        return -1;
    }

    uorbc_platform_log("sub handle:%d", ret);

    return ret;
}
```

```yaml
name: Cross-file Function Call 8
relation:
  type: Call
  items:
    -   from: Function:'orb_subscribe'
        to: Function:'mod_uorbc_utils_get_orbid'
        loc: mod_uorbc:16:14
    -   from: Function:'orb_subscribe'
        to: Function:'mod_uorbc_utils_ismulti'
        loc: mod_uorbc:27:19
```

```cpp
//// mod_uorbc_utils.c
int mod_uorbc_utils_get_orbid(const char* name){}
bool mod_uorbc_utils_ismulti(const int orb_id){}
```

```cpp
//// mod_uorbc.c
int orb_subscribe_multi(const struct orb_metadata *meta, unsigned instance)
{
    int ret = -1;
    int orb_id = -1;
    int task_id = -1;
    int i = 0;

    if (meta == NULL || meta->o_name == NULL)
    {
        uorbc_platform_log_err("meta == NULL || meta->o_name == NULL");
        return -1;
    }

    orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb name:%s orb_id:%d", meta->o_name, orb_id);

    if (orb_id >= 0 && orb_id < TOTAL_UORBC_NUM && mod_uorbc_utils_ismulti(orb_id))
    {
        task_id = get_subscriber_id();
        //        int atomic_state = orb_lock();
        p_orb_data_t *orb = uorbc_instance();
        p_orb_data_t p = &(orb[orb_id][instance]);
        uorbc_platform_sem_wait(&(p->sem));
        for (i = 0; i < UORB_MAX_SUB_NUM; ++i)
        {
            if (p->registered_list[i] == task_id)
            {
                ret = (int)((orb_id << 4) | (instance));
                break;
            }
            if (p->registered_list[i] == -1)
            {
                p->registered_list[i] = task_id;
                ret = (int)((orb_id << 4) | (instance));
                break;
            }
        }
        uorbc_platform_sem_post(&(p->sem));
        //        orb_unlock(atomic_state);
    }

    uorbc_platform_log("sub:%d", ret);

    return ret;
}
```

```yaml
name: Cross-file Function Call 9
relation:
  type: Call
  items:
    -   from: Function:'orb_subscribe_multi'
        to: Function:'mod_uorbc_utils_get_orbid'
        loc: mod_uorbc:14:14
    -   from: Function:'orb_subscribe_multi'
        to: Function:'mod_uorbc_utils_ismulti'
        loc: mod_uorbc:18:52
```

```cpp
//// mod_uorbc_utils.c
int mod_uorbc_utils_get_orbid(const char* name){}
bool mod_uorbc_utils_ismulti(const int orb_id){}

```

```cpp
//// mod_uorbc_ringbuffer.c
uint8_t *ring_buffer_pos_pointer(ring_buffer_t *buffer, ring_buffer_size_t pos){}
ring_buffer_size_t ring_buffer_just_peek(ring_buffer_t *buffer, char *data, ring_buffer_size_t pos, ring_buffer_size_t len){}
ring_buffer_size_t ring_buffer_just_peek(ring_buffer_t *buffer, char *data, ring_buffer_size_t pos, ring_buffer_size_t len){}
```

```cpp
//// mod_uorbc.c
int orb_copy(const struct orb_metadata *meta, int handle, void *buffer)
{
    int multi_pos = -1;
    int task_id = -1;
    int i = 0;
    int registered_len = 0, authority_len = 0;
    uint32_t cur_msg_pos = 0;
    uint queue_size = 0;
    int sub_val = 0;
    ring_buffer_t *p_ring = NULL;
    size_t peek_pos = 0;

    int orb_id = mod_uorbc_utils_get_orbid(meta->o_name);

    uorbc_platform_log("orb_id:%d o_name:%s o_size:%d handle:%d", orb_id, meta->o_name, meta->o_size, handle);

    if (orb_id != -1 && orb_id == (handle >> 4))
    {
        multi_pos = handle - ((handle >> 4) << 4);
        p_orb_data_t *orb = uorbc_instance();
        p_orb_data_t p = &(orb[orb_id][multi_pos]);
        uorbc_platform_sem_wait(&(p->sem));

        if (orb_check(handle) <= 0)
        {
            uorbc_platform_log("error or no msg");
            uorbc_platform_sem_post(&(p->sem));
            return -1;
        }

        if (multi_pos > 0 && !mod_uorbc_utils_ismulti(orb_id))
        {
            uorbc_platform_sem_post(&(p->sem));
            uorbc_platform_log_err("error multi_pos:%d not multi", multi_pos);
            return -1;
        }
        else if (multi_pos >= 0 && multi_pos < UORB_MULTI_MAX_INSTANCES)
        {
            task_id = get_subscriber_id();

            //				int atomic_state = orb_lock();
            if (true == p->queue_mode)
            {
                p_queue_mode_t q = &(p->meta.q);
                cur_msg_pos = q->cur_msg_pos;
                int idpos = get_subscriber_id_pos(p, task_id);
                if (-1 == idpos)
                {
                    uorbc_platform_sem_post(&(p->sem));
                    uorbc_platform_log_err("error idpos(%d)", idpos);
                    return -1;
                }

                queue_size = q->queue_size;
                if (cur_msg_pos > (q->queue_lastmsg_pos[idpos] + queue_size))
                {
                    /* Reader is too far behind: some messages are lost */
                    q->queue_lastmsg_pos[idpos] = cur_msg_pos - queue_size;
                }

                uint32_t msg_pan = q->cur_msg_pos - q->queue_lastmsg_pos[idpos];
                uint32_t *que_buf = uorbc_platform_malloc(sizeof(uint32_t) + msg_pan * (meta->o_size));
                uorbc_platform_assert(que_buf);
                memset(que_buf, 0x00, (sizeof(uint32_t) + msg_pan * (meta->o_size)));
                que_buf[0] = msg_pan;
                uint8_t *_que_buf = (uint8_t *)(&que_buf[1]);

                do
                {
                    p_ring = q->queue;
                    peek_pos = (q->queue_lastmsg_pos[idpos]) * (q->queue_perpayload_len);
                    peek_pos %= (q->qbuf_size);
                    p_queue_payload_header_t queue_msg = (p_queue_payload_header_t)ring_buffer_pos_pointer(p_ring, peek_pos);
                    if (queue_msg->try_count > 1)
                    { /* this is a retry msg */
                        if (true == subscriber_id_hit(queue_msg, task_id))
                        { /* this receiver has received this msg */
                            /* noneed get msg again */
                            if (que_buf)
                            {
                                free(que_buf);
                                que_buf = NULL;
                                _que_buf = NULL;
                            }
                        }
                        else
                        {
                            ring_buffer_just_peek(p_ring, _que_buf, peek_pos + sizeof(queue_payload_header_t), meta->o_size);
                            queue_msg->authority_list[idpos] = task_id; /* this msg is received by this subscriber */
                            _que_buf += meta->o_size;
                        }
                    }
                    else
                    {
                        ring_buffer_just_peek(p_ring, _que_buf, peek_pos + sizeof(queue_payload_header_t), meta->o_size);
                        queue_msg->authority_list[idpos] = task_id; /* this msg is received by this subscriber */
                        _que_buf += meta->o_size;
                    }

                    if (q->queue_lastmsg_pos[idpos] < q->cur_msg_pos)
                    {
                        q->queue_lastmsg_pos[idpos]++;
                    }
                    else
                    {
                        q->queue_lastmsg_pos[idpos] = q->cur_msg_pos;
                    }
                } while (q->queue_lastmsg_pos[idpos] != q->cur_msg_pos);
                *((uint32_t *)buffer) = que_buf;
            }
            else
            { /* nonqueue mode */
                p_nonqueue_mode_t nonq = &(p->meta.nonq);
                uorbc_platform_memcpy(buffer, nonq->data, meta->o_size);

                for (int i = 0; i < UORB_MAX_SUB_NUM; ++i)
                {
                    if (p->registered_list[i] != -1)
                    {
                        ++registered_len;
                    }
                    if (nonq->authority_list[i] != -1)
                    {
                        ++authority_len;
                    }
                }

                if (authority_len >= registered_len)
                { /* TODO: this judgement is not strict */
                    nonq->published = false;
                }
            }
            //				orb_unlock ( atomic_state );
        }

        uorbc_platform_sem_post(&(p->sem));
    }

    return 0;
}
```

```yaml
name: Cross-file Function Call 10
relation:
  type: Call
  items:
    -   from: Function:'orb_copy'
        to: Function:'mod_uorbc_utils_get_orbid'
        loc: mod_uorbc:13:18
    -   from: Function:'orb_copy'
        to: Function:'mod_uorbc_utils_ismulti'
        loc: mod_uorbc:31:31
    -   from: Function:'orb_copy'
        to: Function:'ring_buffer_pos_pointer'
        loc: mod_uorbc:80:84
    -   from: Function:'orb_copy'
        to: Function:'ring_buffer_just_peek'
        loc: mod_uorbc:88:29
    -   from: Function:'orb_copy'
        to: Function:'ring_buffer_just_peek'
        loc: mod_uorbc:95:25
```

```cpp
//// mod_uorbc_utils.c
bool mod_uorbc_utils_ismulti(const int orb_id){}
```

```cpp
//// mod_uorbc.c
void orb_init(void)
{
    uorbc_platform_log("orb_init");

    for (int i = ORBC_SYS_STATE; i < TOTAL_UORBC_NUM; ++i)
    {
        if (mod_uorbc_utils_ismulti(i))
        {
            orb_init_multi(i);
        }
        else
        {
            orb_init_nomulti(i);
        }
    }
}
```
```yaml
name: Cross-file Function Call 11
relation:
  type: Call
  items:
    -   from: Function:'orb_init'
        to: Function:'mod_uorbc_utils_ismulti'
        loc: mod_uorbc:7:13
```

###### Method Function Call



###### Deref Call
If an Variable can deref as a function, there maybe a deref call.


###### Function Continuous Call


###### Function Extern Call

```cpp
extern int func1(void);
int func() {
    func1();
}
```

```yaml
name: Function Extern Call
relation:
  items:
    -   from: Function:'func'
        to: Function:'func1'
        loc: file0:3:5
        type: Call
```

```cpp
//// uORB.h
extern int orb_copy(const struct orb_metadata *meta, int handle, void *buffer);
```

```cpp
//// mod_uorbc_test.c
static int uorbc_check_copy(const struct orb_metadata *meta, int handle, void *buffer, uint8_t *update)
{
    *update = 0;

    int ret = orb_copy(meta, handle, buffer);
    if (0 == ret)
    {
        uorbc_platform_log("orb_copy ok! meta->o_name:%s now all_msg is read", meta->o_name);
        *update = 1;
    }
    else
    {
        uorbc_platform_log_err("orb_copy fail:error or no msg in queue");
    }

    return ret;
}
```

```yaml
name: Function Extern Call 1
relation:
  items:
    -   from: Function:'uorbc_check_copy'
        to: Function:'orb_copy'
        loc: mod_uorbc_test:5:15
        type: Call
```

```cpp
//// uORB.h
extern int orb_publish(const struct orb_metadata *meta, uint8_t retry, const void *data);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_pub(void)
{
    uorbc_platform_log("uorbc_test_thread_pub");

    struct sysinfo_s sysinfo_new;

    uint32_t pub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(1500);

        uorbc_platform_log("0 pub_count:%d", pub_count);
        const char *pub = "0 pub";

        sysinfo_new.machine_number++;
        uorbc_platform_log("%s sysinfo_new.machine_number[%d] ++ %s", pub, sysinfo_new.machine_number, pub);
        orb_publish(ORB_ID(sysinfo), UORB_PUB_NORMAL, &sysinfo_new);

        pub_count++;
    }
}
```

```yaml
name: Function Extern Call 2
relation:
  items:
    -   from: Function:'uorbc_test_thread_pub'
        to: Function:'orb_publish'
        loc: mod_uorbc_test:17:9
        type: Call
```


```cpp
//// uORB.h
extern int orb_publish(const struct orb_metadata *meta, uint8_t retry, const void *data);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_pub1(void)
{
    uorbc_platform_log("uorbc_test_thread_pub1");

    struct sysinfo_s sysinfo_new;

    uint32_t pub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(1500);
        const char *pub = "1 pub";

        uorbc_platform_log("1 pub_count:%d", pub_count);

        sysinfo_new.machine_number += 11;
        uorbc_platform_log("%s sysinfo_new.machine_number[%d] +=11 %s", pub, sysinfo_new.machine_number, pub);
        orb_publish(ORB_ID(sysinfo), UORB_PUB_NORMAL, &sysinfo_new);

        pub_count++;
    }
}
```

```yaml
name: Function Extern Call 3
relation:
  items:
    -   from: Function:'uorbc_test_thread_pub1'
        to: Function:'orb_publish'
        loc: mod_uorbc_test:17:9
        type: Call
```

```cpp
//// uORB.h
extern int orb_publish(const struct orb_metadata *meta, uint8_t retry, const void *data);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_pub2(void)
{
    uorbc_platform_log("uorbc_test_thread_pub2");

    struct sysinfo_s sysinfo_new;

    uint32_t pub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(1500);
        const char *pub = "2 pub";

        uorbc_platform_log("2 pub_count:%d", pub_count);

        sysinfo_new.machine_number += 22;
        uorbc_platform_log("%s sysinfo_new.machine_number[%d] +=22 %s", pub, sysinfo_new.machine_number, pub);
        orb_publish(ORB_ID(sysinfo), UORB_PUB_NORMAL, &sysinfo_new);

        pub_count++;
    }
}
```

```yaml
name: Function Extern Call 4
relation:
  items:
    -   from: Function:'uorbc_test_thread_pub2'
        to: Function:'orb_publish'
        loc: mod_uorbc_test:17:9
        type: Call
```

```cpp
//// uORB.h
extern int orb_publish(const struct orb_metadata *meta, uint8_t retry, const void *data);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_pub3(void)
{
    uorbc_platform_log("uorbc_test_thread_pub3");

    struct sysinfo_s sysinfo_new;

    uint32_t pub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(1500);
        const char *pub = "3 pub";

        uorbc_platform_log("3 pub_count:%d", pub_count);

        sysinfo_new.machine_number += 33;
        uorbc_platform_log("%s sysinfo_new.machine_number[%d] +=33 %s", pub, sysinfo_new.machine_number, pub);
        orb_publish(ORB_ID(sysinfo), UORB_PUB_NORMAL, &sysinfo_new);

        pub_count++;
    }
}
```

```yaml
name: Function Extern Call 5
relation:
  items:
    -   from: Function:'uorbc_test_thread_pub3'
        to: Function:'orb_publish'
        loc: mod_uorbc_test:17:9
        type: Call
```

```cpp
//// uORB.h
extern int orb_publish(const struct orb_metadata *meta, uint8_t retry, const void *data);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_pub4(void)
{
    uorbc_platform_log("uorbc_test_thread_pub4");

    struct sysinfo_s sysinfo_new;

    uint32_t pub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(1500);
        const char *pub = "4 pub";

        uorbc_platform_log("4 pub_count:%d", pub_count);

        sysinfo_new.machine_number += 53;
        uorbc_platform_log("%s sysinfo_new.machine_number[%d] +=53 %s", pub, sysinfo_new.machine_number, pub);
        orb_publish(ORB_ID(sysinfo), UORB_PUB_NORMAL, &sysinfo_new);

        pub_count++;
    }
}

```

```yaml
name: Function Extern Call 6
relation:
  items:
    -   from: Function:'uorbc_test_thread_pub4'
        to: Function:'orb_publish'
        loc: mod_uorbc_test:17:9
        type: Call
```

```cpp
//// uORB.h
extern int orb_publish(const struct orb_metadata *meta, uint8_t retry, const void *data);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_pub5(void)
{
    uorbc_platform_log("uorbc_test_thread_pub5");

    struct sysinfo_s sysinfo_new;

    uint32_t pub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(1500);
        const char *pub = "5 pub";

        uorbc_platform_log("5 pub_count:%d", pub_count);

        sysinfo_new.machine_number += 63;
        uorbc_platform_log("%s sysinfo_new.machine_number[%d] +=63 %s", pub, sysinfo_new.machine_number, pub);
        orb_publish(ORB_ID(sysinfo), UORB_PUB_NORMAL, &sysinfo_new);

        pub_count++;
    }
}
```

```yaml
name: Function Extern Call 7
relation:
  items:
    -   from: Function:'uorbc_test_thread_pub5'
        to: Function:'orb_publish'
        loc: mod_uorbc_test:17:9
        type: Call
```

```cpp
//// uORB.h
extern int orb_publish(const struct orb_metadata *meta, uint8_t retry, const void *data);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_pub6(void)
{
    uorbc_platform_log("uorbc_test_thread_pub6");

    struct sysinfo_s sysinfo_new;

    uint32_t pub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(1500);
        const char *pub = "6 pub";

        uorbc_platform_log("6 pub_count:%d", pub_count);

        sysinfo_new.machine_number += 73;
        uorbc_platform_log("%s sysinfo_new.machine_number[%d] +=73 %s", pub, sysinfo_new.machine_number, pub);
        orb_publish(ORB_ID(sysinfo), UORB_PUB_NORMAL, &sysinfo_new);

        pub_count++;
    }
}
```

```yaml
name: Function Extern Call 8
relation:
  items:
    -   from: Function:'uorbc_test_thread_pub6'
        to: Function:'orb_publish'
        loc: mod_uorbc_test:17:9
        type: Call
```

```cpp
//// uORB.h
extern int orb_subscribe(const struct orb_metadata *meta);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_sub(void)
{
    uorbc_platform_log("uorbc_test_thread_sub");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;

    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "0 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Extern Call 9
relation:
  items:
    -   from: Function:'uorbc_test_thread_sub'
        to: Function:'orb_subscribe'
        loc: mod_uorbc_test:5:26
        type: Call
```

```cpp
//// uORB.h
extern int orb_subscribe(const struct orb_metadata *meta);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_sub1(void)
{
    uorbc_platform_log("uorbc_test_thread_sub1");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(5500);
        ++const char *sub = "1 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Extern Call 9
relation:
  items:
    -   from: Function:'uorbc_test_thread_sub1'
        to: Function:'orb_subscribe'
        loc: mod_uorbc_test:5:26
        type: Call
```

```cpp
//// uORB.h
extern int orb_subscribe(const struct orb_metadata *meta);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_sub2(void)
{
    uorbc_platform_log("uorbc_test_thread_sub2");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "2 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Extern Call 9
relation:
  items:
    -   from: Function:'uorbc_test_thread_sub2'
        to: Function:'orb_subscribe'
        loc: mod_uorbc_test:5:26
        type: Call
```

```cpp
//// uORB.h
extern int orb_subscribe(const struct orb_metadata *meta);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_sub3(void)
{
    uorbc_platform_log("uorbc_test_thread_sub3");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "3 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Extern Call 9
relation:
  items:
    -   from: Function:'uorbc_test_thread_sub3'
        to: Function:'orb_subscribe'
        loc: mod_uorbc_test:5:26
        type: Call
```


```cpp
//// uORB.h
extern int orb_subscribe(const struct orb_metadata *meta);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_sub4(void)
{
    uorbc_platform_log("uorbc_test_thread_sub4");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "4 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Extern Call 10
relation:
  items:
    -   from: Function:'uorbc_test_thread_sub4'
        to: Function:'orb_subscribe'
        loc: mod_uorbc_test:5:26
        type: Call
```

```cpp
//// uORB.h
extern int orb_subscribe(const struct orb_metadata *meta);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_sub5(void)
{
    uorbc_platform_log("uorbc_test_thread_sub5");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;

    uint32_t sub_count = 0;
    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "5 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Extern Call 11
relation:
  items:
    -   from: Function:'uorbc_test_thread_sub5'
        to: Function:'orb_subscribe'
        loc: mod_uorbc_test:5:26
        type: Call
```

```cpp
//// uORB.h
extern int orb_subscribe(const struct orb_metadata *meta);
```

```cpp
//// mod_uorbc_test.c
static void *uorbc_test_thread_sub6(void)
{
    uorbc_platform_log("uorbc_test_thread_sub6");

    int handle_sysinfo = orb_subscribe(ORB_ID(sysinfo));
    int ret = -1;
    uint32_t sub_count = 0;

    while (1)
    {
        uorbc_platform_delay_ms(5500);
        const char *sub = "6 sub";
        uorbc_platform_log("%s sub_count:%d", sub, sub_count);
        sub_count++;

        struct _que_msg
        {
            uint32_t num;
            struct sysinfo_s sysinfo[0];
        };
        struct _que_msg *q = NULL;
        uint32_t pointer;

        uint8_t update = 0;

        q = NULL;
        ret = uorbc_check_copy(ORB_ID(sysinfo), handle_sysinfo, &pointer, &update);
        uorbc_platform_log("ret:%d update:%d %s", ret, update, sub);
        q = pointer;
        if (update && q)
        {
            struct sysinfo_s *p_sysinfo = &(q->sysinfo);
            for (int i = 0; i < q->num; i++)
            {
                uorbc_platform_log("%s machine_number[%d] %s total %d msg, cur:%d",
                                   sub, p_sysinfo->machine_number, sub, q->num, i);
                p_sysinfo++;
            }
            free(q);
        }
    }
}
```

```yaml
name: Function Extern Call 12
relation:
  items:
    -   from: Function:'uorbc_test_thread_sub6'
        to: Function:'orb_subscribe'
        loc: mod_uorbc_test:5:26
        type: Call
```

```cpp
//// uORB.h
extern void orb_init();
extern orb_advert_t orb_advertise(const struct orb_metadata *meta, const void *data);
```

```cpp
//// mod_uorbc_test.c
void mod_uorbc_test(void)
{
    uorbc_platform_log("\n\n\ntest uorbc 0c\n");
    uorbc_platform_delay_ms(3000);

    orb_init();

    struct sysinfo_s init_sysinfo = {
        .machine_type = "Dryer",
        .machine_number = 0,
        .mbn = 0,
        .net = 0};
    orb_advertise(ORB_ID(sysinfo), &init_sysinfo);

    uorbc_platform_pthread_t thread_pub = 0;
    uorbc_platform_pthread_t thread_pub1 = 0;
    uorbc_platform_pthread_t thread_pub2 = 0;
    uorbc_platform_pthread_t thread_pub3 = 0;
    uorbc_platform_pthread_t thread_pub4 = 0;
    uorbc_platform_pthread_t thread_pub5 = 0;
    uorbc_platform_pthread_t thread_pub6 = 0;
    mod_uorbc_platform_thread_create(&thread_pub, uorbc_test_thread_pub, NULL);
    mod_uorbc_platform_thread_create(&thread_pub1, uorbc_test_thread_pub1, NULL);
    mod_uorbc_platform_thread_create(&thread_pub2, uorbc_test_thread_pub2, NULL);
    mod_uorbc_platform_thread_create(&thread_pub3, uorbc_test_thread_pub3, NULL);
    mod_uorbc_platform_thread_create(&thread_pub4, uorbc_test_thread_pub4, NULL);
    mod_uorbc_platform_thread_create(&thread_pub5, uorbc_test_thread_pub5, NULL);
    mod_uorbc_platform_thread_create(&thread_pub6, uorbc_test_thread_pub6, NULL);

    uorbc_platform_pthread_t thread_sub = 0;
    uorbc_platform_pthread_t thread_sub1 = 0;
    uorbc_platform_pthread_t thread_sub2 = 0;
    uorbc_platform_pthread_t thread_sub3 = 0;
    uorbc_platform_pthread_t thread_sub4 = 0;
    uorbc_platform_pthread_t thread_sub5 = 0;
    uorbc_platform_pthread_t thread_sub6 = 0;
    mod_uorbc_platform_thread_create(&thread_sub, uorbc_test_thread_sub, NULL);
    mod_uorbc_platform_thread_create(&thread_sub1, uorbc_test_thread_sub1, NULL);
    mod_uorbc_platform_thread_create(&thread_sub2, uorbc_test_thread_sub2, NULL);
    mod_uorbc_platform_thread_create(&thread_sub3, uorbc_test_thread_sub3, NULL);
    mod_uorbc_platform_thread_create(&thread_sub4, uorbc_test_thread_sub4, NULL);
    mod_uorbc_platform_thread_create(&thread_sub5, uorbc_test_thread_sub5, NULL);
    mod_uorbc_platform_thread_create(&thread_sub6, uorbc_test_thread_sub6, NULL);
}
```

```yaml
name: Function Extern Call 13
relation:
  items:
    -   from: Function:'mod_uorbc_test'
        to: Function:'orb_init'
        loc: mod_uorbc_test:6:5
        type: Call
    -   from: Function:'mod_uorbc_test'
        to: Function:'orb_advertise'
        loc: mod_uorbc_test:13:5
        type: Call
```


```cpp
//// uORB.h
extern orb_advert_t orb_advertise_multi(const struct orb_metadata *meta, const void *data, int *instance);
extern orb_advert_t orb_advertise(const struct orb_metadata *meta, const void *data);
extern int orb_publish(const struct orb_metadata *meta, uint8_t retry, const void *data);
```

```cpp
//// mod_uorbc.c
int orb_publish_auto(const struct orb_metadata *meta, orb_advert_t *handle, const void *data, int *instance)
{
    int orb_id = -1;

    if (*handle == NULL)
    {
        int orb_id = mod_uorbc_utils_get_orbid(meta->o_name);
        if (mod_uorbc_utils_ismulti(orb_id))
        {
            *handle = orb_advertise_multi(meta, data, instance);
        }
        else
        {
            *handle = orb_advertise(meta, data);
            if (instance)
            {
                *instance = 0;
            }
        }

        if (*handle != NULL)
        {
            return 0;
        }
    }
    else
    {
        return orb_publish(meta, UORB_PUB_NORMAL, data);
    }

    return -1;
}
```

```yaml
name: Function Extern Call 14
relation:
  items:
    -   from: Function:'orb_publish_auto'
        to: Function:'orb_advertise_multi'
        loc: mod_uorbc:10:23
        type: Call
    -   from: Function:'orb_publish_auto'
        to: Function:'orb_advertise'
        loc: mod_uorbc:14:23
        type: Call
    -   from: Function:'orb_publish_auto'
        to: Function:'orb_publish'
        loc: mod_uorbc:28:16
        type: Call
```



###### Call Class Method Call


###### Template Function Call Function


###### Template Call Template


###### Function Call Template


###### Operator Function call
