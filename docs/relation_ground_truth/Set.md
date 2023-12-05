## Relation: Set
Descriptions: A `set dependency` indicates any explicit assignment of a variable.

### Supported Patterns
```yaml
name: Set
```

#### Syntax: Set Declaration

```text

```
##### Examples

###### Set

```cpp
//// mod_uorbc_ringbuffer.c
uint32_t ring_buffer_to_the_power_of_2(uint32_t in_data)
{
    uint32_t tmp = in_data;
    while (1)
    {
        if (0 == (tmp & (tmp - 1)))
        {
            return tmp;
        }
        tmp++;
    }
}
```

```yaml
name: Set 1
relation:
    items:
        -   type: Set
            loc: mod_uorbc_ringbuffer:3:14
            from: Function:'ring_buffer_to_the_power_of_2'
            to: Variable:'tmp'
```

```cpp
//// mod_uorbc_ringbuffer.c
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
name: Set 2
relation:
    items:
        -   type: Set
            loc: mod_uorbc_ringbuffer:3:14
            from: Function:'ring_buffer_init'
            to: Variable:'size_q'
        -   type: Set
            loc: mod_uorbc_ringbuffer:4:14
            from: Function:'ring_buffer_init'
            to: Variable:'size_new'
        -   type: Set
            loc: mod_uorbc_ringbuffer:8:20
            from: Function:'ring_buffer_init'
            to: Variable:'p_ring'
        -   type: Set
            loc: mod_uorbc_ringbuffer:12:17
            from: Function:'ring_buffer_init'
            to: Variable:'tail_index'
        -   type: Set
            loc: mod_uorbc_ringbuffer:13:17
            from: Function:'ring_buffer_init'
            to: Variable:'head_index'
        -   type: Set
            loc: mod_uorbc_ringbuffer:14:17
            from: Function:'ring_buffer_init'
            to: Variable:'buffer_size'
        -   type: Set
            loc: mod_uorbc_ringbuffer:15:17
            from: Function:'ring_buffer_init'
            to: Variable:'buffer_mask'
```

```cpp
//// mod_uorbc_ringbuffer.c
void ring_buffer_queue(ring_buffer_t *buffer, char data)
{
    /* Is buffer full? */
    if (ring_buffer_is_full(buffer))
    {
        /* Is going to overwrite the oldest byte */
        /* Increase tail index */
        //		uorbc_platform_log( "queue full" );
        buffer->tail_index = ((buffer->tail_index + 1) & buffer->buffer_mask);
    }

    //	uorbc_platform_log( "data:%d", data );
    //	uorbc_platform_log( "head_index:%d", buffer->head_index );

    /* Place data in buffer */
    buffer->buffer[buffer->head_index] = data;
    buffer->head_index = ((buffer->head_index + 1) & buffer->buffer_mask);

    //	uorbc_platform_log( "buffer_mask:%d head_index:%d", buffer->buffer_mask, buffer->head_index );
}
```

```yaml
name: Set 3
relation:
    items:
        -   type: Set
            loc: mod_uorbc_ringbuffer:9:17
            from: Function:'ring_buffer_queue'
            to: Variable:'tail_index'
        -   type: Set
            loc: mod_uorbc_ringbuffer:16:13
            from: Function:'ring_buffer_queue'
            to: Variable:'buffer'
        -   type: Set
            loc: mod_uorbc_ringbuffer:17:13
            from: Function:'ring_buffer_queue'
            to: Variable:'head_index'
```

```cpp
//// mod_uorbc_ringbuffer.c
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
name: Set 4
relation:
    items:
        -   type: Set
            loc: mod_uorbc_ringbuffer:10:6
            from: Function:'ring_buffer_dequeue'
            to: Variable:'data'
        -   type: Set
            loc: mod_uorbc_ringbuffer:13:11
            from: Function:'ring_buffer_dequeue'
            to: Variable:'tail_index'
```

```cpp
//// mod_uorbc_ringbuffer.c
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
name: Set 5
relation:
    items:
        -   type: Set
            loc: mod_uorbc_ringbuffer:10:11
            from: Function:'ring_buffer_dequeue_arr'
            to: Variable:'data_ptr'
        -   type: Set
            loc: mod_uorbc_ringbuffer:11:24
            from: Function:'ring_buffer_dequeue_arr'
            to: Variable:'cnt'
```

```cpp
//// mod_uorbc_ringbuffer.c
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
name: Set 6
relation:
    items:
        -   type: Set
            loc: mod_uorbc_ringbuffer:12:11
            from: Function:'ring_buffer_just_peek'
            to: Variable:'data_ptr'
        -   type: Set
            loc: mod_uorbc_ringbuffer:13:24
            from: Function:'ring_buffer_just_peek'
            to: Variable:'cnt'
        -   type: Set
            loc: mod_uorbc_ringbuffer:14:24
            from: Function:'ring_buffer_just_peek'
            to: Variable:'peek_pos'
        -   type: Set
            loc: mod_uorbc_ringbuffer:17:10
            from: Function:'ring_buffer_just_peek'
            to: Variable:'data_ptr'
        -   type: Set
            loc: mod_uorbc_ringbuffer:18:9
            from: Function:'ring_buffer_just_peek'
            to: Variable:'peek_pos'
```

```cpp
//// mod_uorbc_ringbuffer.c
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
name: Set 7
relation:
    items:
        -   type: Set
            loc: mod_uorbc_ringbuffer:10:24
            from: Function:'ring_buffer_peek'
            to: Variable:'data_index'
        -   type: Set
            loc: mod_uorbc_ringbuffer:11:6
            from: Function:'ring_buffer_peek'
            to: Variable:'data'
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
name: Set 8
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:3:6
            from: Function:'uorbc_check_copy'
            to: Variable:'update'
        -   type: Set
            loc: mod_uorbc_test:5:9
            from: Function:'uorbc_check_copy'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:9:10
            from: Function:'uorbc_check_copy'
            to: Variable:'update'
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
name: Set 9
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:7:14
            from: Function:'uorbc_test_thread_pub'
            to: Variable:'pub_count'
        -   type: Set
            loc: mod_uorbc_test:13:21
            from: Function:'uorbc_test_thread_pub'
            to: Variable:'pub'
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
name: Set 10
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:7:14
            from: Function:'uorbc_test_thread_pub1'
            to: Variable:'pub_count'
        -   type: Set
            loc: mod_uorbc_test:11:21
            from: Function:'uorbc_test_thread_pub1'
            to: Variable:'pub'
        -   type: Set
            loc: mod_uorbc_test:15:21
            from: Function:'uorbc_test_thread_pub1'
            to: Variable:'machine_number'
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
name: Set 11
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:7:14
            from: Function:'uorbc_test_thread_pub2'
            to: Variable:'pub_count'
        -   type: Set
            loc: mod_uorbc_test:11:21
            from: Function:'uorbc_test_thread_pub2'
            to: Variable:'pub'
        -   type: Set
            loc: mod_uorbc_test:15:21
            from: Function:'uorbc_test_thread_pub2'
            to: Variable:'machine_number'
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
name: Set 12
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:7:14
            from: Function:'uorbc_test_thread_pub3'
            to: Variable:'pub_count'
        -   type: Set
            loc: mod_uorbc_test:11:21
            from: Function:'uorbc_test_thread_pub3'
            to: Variable:'pub'
        -   type: Set
            loc: mod_uorbc_test:15:21
            from: Function:'uorbc_test_thread_pub3'
            to: Variable:'machine_number'
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
name: Set 13
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:7:14
            from: Function:'uorbc_test_thread_pub4'
            to: Variable:'pub_count'
        -   type: Set
            loc: mod_uorbc_test:11:21
            from: Function:'uorbc_test_thread_pub4'
            to: Variable:'pub'
        -   type: Set
            loc: mod_uorbc_test:15:21
            from: Function:'uorbc_test_thread_pub4'
            to: Variable:'machine_number'
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
name: Set 14
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:7:14
            from: Function:'uorbc_test_thread_pub5'
            to: Variable:'pub_count'
        -   type: Set
            loc: mod_uorbc_test:11:21
            from: Function:'uorbc_test_thread_pub5'
            to: Variable:'pub'
        -   type: Set
            loc: mod_uorbc_test:15:21
            from: Function:'uorbc_test_thread_pub5'
            to: Variable:'machine_number'
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
name: Set 15
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:7:14
            from: Function:'uorbc_test_thread_pub6'
            to: Variable:'pub_count'
        -   type: Set
            loc: mod_uorbc_test:11:21
            from: Function:'uorbc_test_thread_pub6'
            to: Variable:'pub'
        -   type: Set
            loc: mod_uorbc_test:15:21
            from: Function:'uorbc_test_thread_pub6'
            to: Variable:'machine_number'
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
name: Set 16
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:5:9
            from: Function:'uorbc_test_thread_sub'
            to: Variable:'handle_sysinfo'
        -   type: Set
            loc: mod_uorbc_test:6:9
            from: Function:'uorbc_test_thread_sub'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:8:14
            from: Function:'uorbc_test_thread_sub'
            to: Variable:'sub_count'
        -   type: Set
            loc: mod_uorbc_test:13:21
            from: Function:'uorbc_test_thread_sub'
            to: Variable:'sub'
        -   type: Set
            loc: mod_uorbc_test:22:26
            from: Function:'uorbc_test_thread_sub'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:25:17
            from: Function:'uorbc_test_thread_sub'
            to: Variable:'update'
        -   type: Set
            loc: mod_uorbc_test:27:9
            from: Function:'uorbc_test_thread_sub'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:28:9
            from: Function:'uorbc_test_thread_sub'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:30:9
            from: Function:'uorbc_test_thread_sub'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:33:31
            from: Function:'uorbc_test_thread_sub'
            to: Variable:'p_sysinfo'
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
name: Set 17
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:5:9
            from: Function:'uorbc_test_thread_sub1'
            to: Variable:'handle_sysinfo'
        -   type: Set
            loc: mod_uorbc_test:6:9
            from: Function:'uorbc_test_thread_sub1'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:8:14
            from: Function:'uorbc_test_thread_sub1'
            to: Variable:'sub_count'
        -   type: Set
            loc: mod_uorbc_test:12:21
            from: Function:'uorbc_test_thread_sub1'
            to: Variable:'sub'
        -   type: Set
            loc: mod_uorbc_test:21:26
            from: Function:'uorbc_test_thread_sub1'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:24:17
            from: Function:'uorbc_test_thread_sub1'
            to: Variable:'update'
        -   type: Set
            loc: mod_uorbc_test:26:9
            from: Function:'uorbc_test_thread_sub1'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:27:9
            from: Function:'uorbc_test_thread_sub1'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:29:9
            from: Function:'uorbc_test_thread_sub1'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:32:31
            from: Function:'uorbc_test_thread_sub1'
            to: Variable:'p_sysinfo'
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
name: Set 18
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:5:9
            from: Function:'uorbc_test_thread_sub2'
            to: Variable:'handle_sysinfo'
        -   type: Set
            loc: mod_uorbc_test:6:9
            from: Function:'uorbc_test_thread_sub2'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:8:14
            from: Function:'uorbc_test_thread_sub2'
            to: Variable:'sub_count'
        -   type: Set
            loc: mod_uorbc_test:12:21
            from: Function:'uorbc_test_thread_sub2'
            to: Variable:'sub'
        -   type: Set
            loc: mod_uorbc_test:21:26
            from: Function:'uorbc_test_thread_sub2'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:24:17
            from: Function:'uorbc_test_thread_sub2'
            to: Variable:'update'
        -   type: Set
            loc: mod_uorbc_test:26:9
            from: Function:'uorbc_test_thread_sub2'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:27:9
            from: Function:'uorbc_test_thread_sub2'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:29:9
            from: Function:'uorbc_test_thread_sub2'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:32:31
            from: Function:'uorbc_test_thread_sub2'
            to: Variable:'p_sysinfo'
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
name: Set 19
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:5:9
            from: Function:'uorbc_test_thread_sub3'
            to: Variable:'handle_sysinfo'
        -   type: Set
            loc: mod_uorbc_test:6:9
            from: Function:'uorbc_test_thread_sub3'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:8:14
            from: Function:'uorbc_test_thread_sub3'
            to: Variable:'sub_count'
        -   type: Set
            loc: mod_uorbc_test:12:21
            from: Function:'uorbc_test_thread_sub3'
            to: Variable:'sub'
        -   type: Set
            loc: mod_uorbc_test:21:26
            from: Function:'uorbc_test_thread_sub3'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:24:17
            from: Function:'uorbc_test_thread_sub3'
            to: Variable:'update'
        -   type: Set
            loc: mod_uorbc_test:26:9
            from: Function:'uorbc_test_thread_sub3'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:27:9
            from: Function:'uorbc_test_thread_sub3'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:29:9
            from: Function:'uorbc_test_thread_sub3'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:32:31
            from: Function:'uorbc_test_thread_sub3'
            to: Variable:'p_sysinfo'
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
name: Set 20
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:5:9
            from: Function:'uorbc_test_thread_sub4'
            to: Variable:'handle_sysinfo'
        -   type: Set
            loc: mod_uorbc_test:6:9
            from: Function:'uorbc_test_thread_sub4'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:8:14
            from: Function:'uorbc_test_thread_sub4'
            to: Variable:'sub_count'
        -   type: Set
            loc: mod_uorbc_test:12:21
            from: Function:'uorbc_test_thread_sub4'
            to: Variable:'sub'
        -   type: Set
            loc: mod_uorbc_test:21:26
            from: Function:'uorbc_test_thread_sub4'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:24:17
            from: Function:'uorbc_test_thread_sub4'
            to: Variable:'update'
        -   type: Set
            loc: mod_uorbc_test:26:9
            from: Function:'uorbc_test_thread_sub4'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:27:9
            from: Function:'uorbc_test_thread_sub4'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:29:9
            from: Function:'uorbc_test_thread_sub4'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:32:31
            from: Function:'uorbc_test_thread_sub4'
            to: Variable:'p_sysinfo'
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
name: Set 21
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:5:9
            from: Function:'uorbc_test_thread_sub5'
            to: Variable:'handle_sysinfo'
        -   type: Set
            loc: mod_uorbc_test:6:9
            from: Function:'uorbc_test_thread_sub5'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:8:14
            from: Function:'uorbc_test_thread_sub5'
            to: Variable:'sub_count'
        -   type: Set
            loc: mod_uorbc_test:12:21
            from: Function:'uorbc_test_thread_sub5'
            to: Variable:'sub'
        -   type: Set
            loc: mod_uorbc_test:21:26
            from: Function:'uorbc_test_thread_sub5'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:24:17
            from: Function:'uorbc_test_thread_sub5'
            to: Variable:'update'
        -   type: Set
            loc: mod_uorbc_test:26:9
            from: Function:'uorbc_test_thread_sub5'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:27:9
            from: Function:'uorbc_test_thread_sub5'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:29:9
            from: Function:'uorbc_test_thread_sub5'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:32:31
            from: Function:'uorbc_test_thread_sub5'
            to: Variable:'p_sysinfo'
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
name: Set 22
relation:
    items:
        -   type: Set
            loc: mod_uorbc_test:5:9
            from: Function:'uorbc_test_thread_sub6'
            to: Variable:'handle_sysinfo'
        -   type: Set
            loc: mod_uorbc_test:6:9
            from: Function:'uorbc_test_thread_sub6'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:8:14
            from: Function:'uorbc_test_thread_sub6'
            to: Variable:'sub_count'
        -   type: Set
            loc: mod_uorbc_test:12:21
            from: Function:'uorbc_test_thread_sub6'
            to: Variable:'sub'
        -   type: Set
            loc: mod_uorbc_test:21:26
            from: Function:'uorbc_test_thread_sub6'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:24:17
            from: Function:'uorbc_test_thread_sub6'
            to: Variable:'update'
        -   type: Set
            loc: mod_uorbc_test:26:9
            from: Function:'uorbc_test_thread_sub6'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:27:9
            from: Function:'uorbc_test_thread_sub6'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc_test:29:9
            from: Function:'uorbc_test_thread_sub6'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc_test:32:31
            from: Function:'uorbc_test_thread_sub6'
            to: Variable:'p_sysinfo'
```

```cpp
//// mod_uorbc.c
static p_orb_data_t *uorbc_instance(void)
{
    static p_orb_data_t *g_orb_buf = NULL;

    if (NULL == g_orb_buf)
    {
        g_orb_buf = (p_orb_data_t *)uorbc_platform_malloc(sizeof(p_orb_data_t) * TOTAL_UORBC_NUM);
        uorbc_platform_assert(g_orb_buf);
        memset(g_orb_buf, 0x00, (sizeof(p_orb_data_t) * TOTAL_UORBC_NUM));
    }
    //    uorbc_platform_log("g_orb_buf:%p", g_orb_buf);

    return g_orb_buf;
}
```

```yaml
name: Set 23
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:26
            from: Function:'uorbc_instance'
            to: Variable:'g_orb_buf'
```

```cpp
//// mod_uorbc.c
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
name: Set 24
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:9
            from: Function:'orb_init_multi'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc:4:9
            from: Function:'orb_init_multi'
            to: Variable:'j'
        -   type: Set
            loc: mod_uorbc:5:9
            from: Function:'orb_init_multi'
            to: Variable:'k'
        -   type: Set
            loc: mod_uorbc:9:19
            from: Function:'orb_init_multi'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:10:18
            from: Function:'orb_init_multi'
            to: Variable:'p'
        -   type: Set
            loc: mod_uorbc:18:9
            from: Function:'orb_init_multi'
            to: Variable:'p'
        -   type: Set
            loc: mod_uorbc:20:12
            from: Function:'orb_init_multi'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:21:12
            from: Function:'orb_init_multi'
            to: Variable:'queue_mode'
        -   type: Set
            loc: mod_uorbc:22:9
            from: Function:'orb_init_multi'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc:27:12
            from: Function:'orb_init_multi'
            to: Variable:'last_updated_time'
        -   type: Set
            loc: mod_uorbc:30:16
            from: Function:'orb_init_multi'
            to: Variable:'registered_list'
```

```cpp
//// mod_uorbc.c
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
name: Set 25
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:9
            from: Function:'orb_init_nomulti'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc:4:9
            from: Function:'orb_init_nomulti'
            to: Variable:'k'
        -   type: Set
            loc: mod_uorbc:8:19
            from: Function:'orb_init_nomulti'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:10:5
            from: Function:'orb_init_nomulti'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:14:5
            from: Function:'orb_init_nomulti'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:17:5
            from: Function:'orb_init_nomulti'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc:22:13
            from: Function:'orb_init_nomulti'
            to: Variable:'last_updated_time'
        -   type: Set
            loc: mod_uorbc:25:17
            from: Function:'orb_init_nomulti'
            to: Variable:'registered_list'
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
name: Set 26
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:9
            from: Function:'orb_exists'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:13:19
            from: Function:'orb_exists'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:14:18
            from: Function:'orb_exists'
            to: Variable:'p'
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
name: Set 27
relation:
    items:
        -   type: Set
            loc: mod_uorbc:9:9
            from: Function:'orb_advertise_multi'
            to: Variable:'inst'
        -   type: Set
            loc: mod_uorbc:10:9
            from: Function:'orb_advertise_multi'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:11:9
            from: Function:'orb_advertise_multi'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:12:9
            from: Function:'orb_advertise_multi'
            to: Variable:'i'
        -   type: Set
            loc: mod_uorbc:13:18
            from: Function:'orb_advertise_multi'
            to: Variable:'p'
        -   type: Set
            loc: mod_uorbc:16:5
            from: Function:'orb_advertise_multi'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:28:13
            from: Function:'orb_advertise_multi'
            to: Variable:'inst'
        -   type: Set
            loc: mod_uorbc:32:13
            from: Function:'orb_advertise_multi'
            to: Variable:'inst'
        -   type: Set
            loc: mod_uorbc:36:23
            from: Function:'orb_advertise_multi'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:37:9
            from: Function:'orb_advertise_multi'
            to: Variable:'p'
        -   type: Set
            loc: mod_uorbc:40:12
            from: Function:'orb_advertise_multi'
            to: Variable:'queue_mode'
        -   type: Set
            loc: mod_uorbc:41:27
            from: Function:'orb_advertise_multi'
            to: Variable:'nonq'
        -   type: Set
            loc: mod_uorbc:46:19
            from: Function:'orb_advertise_multi'
            to: Variable:'published'
        -   type: Set
            loc: mod_uorbc:50:23
            from: Function:'orb_advertise_multi'
            to: Variable:'data'
        -   type: Set
            loc: mod_uorbc:56:16
            from: Function:'orb_advertise_multi'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:57:16
            from: Function:'orb_advertise_multi'
            to: Variable:'last_updated_time'
        -   type: Set
            loc: mod_uorbc:58:19
            from: Function:'orb_advertise_multi'
            to: Variable:'published'
        -   type: Set
            loc: mod_uorbc:59:13
            from: Function:'orb_advertise_multi'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:62:23
            from: Function:'orb_advertise_multi'
            to: Variable:'authority_list'
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
name: Set 28
relation:
    items:
        -   type: Set
            loc: mod_uorbc:4:18
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'p'
        -   type: Set
            loc: mod_uorbc:5:9
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'inst'
        -   type: Set
            loc: mod_uorbc:6:9
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:7:9
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:8:9
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'i'
        -   type: Set
            loc: mod_uorbc:10:5
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:22:13
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'inst'
        -   type: Set
            loc: mod_uorbc:26:13
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'inst'
        -   type: Set
            loc: mod_uorbc:31:23
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:32:12
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'queue_mode'
        -   type: Set
            loc: mod_uorbc:34:24
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc:40:22
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'payload_len'
        -   type: Set
            loc: mod_uorbc:41:16
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'queue_perpayload_len'
        -   type: Set
            loc: mod_uorbc:42:16
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'one_queue_msg'
        -   type: Set
            loc: mod_uorbc:45:16
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'queue'
        -   type: Set
            loc: mod_uorbc:47:16
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'qbuf_size'
        -   type: Set
            loc: mod_uorbc:58:38
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'queue_payload'
        -   type: Set
            loc: mod_uorbc:59:28
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'timestamp'
        -   type: Set
            loc: mod_uorbc:60:28
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'try_count'
        -   type: Set
            loc: mod_uorbc:68:16
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:69:16
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'last_updated_time'
        -   type: Set
            loc: mod_uorbc:72:16
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'queue_size'
        -   type: Set
            loc: mod_uorbc:75:13
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:79:20
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'queue_lastmsg_pos'
```

```cpp
//// mod_uorbc.c
int orb_unadvertise(orb_advert_t *handle)
{
    *handle = NULL;
    return 0;
}
```

```yaml
name: Set 29
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:6
            from: Function:'orb_unadvertise'
            to: Variable:'handle'
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
name: Set 30
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:9
            from: Function:'orb_publish_auto'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:7:13
            from: Function:'orb_publish_auto'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:10:14
            from: Function:'orb_publish_auto'
            to: Variable:'handle'
        -   type: Set
            loc: mod_uorbc:14:14
            from: Function:'orb_publish_auto'
            to: Variable:'handle'
        -   type: Set
            loc: mod_uorbc:17:18
            from: Function:'orb_publish_auto'
            to: Variable:'instance'
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
name: Set 31
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:9
            from: Function:'orb_publish'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:4:9
            from: Function:'orb_publish'
            to: Variable:'multi_pos'
        -   type: Set
            loc: mod_uorbc:5:12
            from: Function:'orb_publish'
            to: Variable:'buffer_space'
        -   type: Set
            loc: mod_uorbc:7:5
            from: Function:'orb_publish'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:20:13
            from: Function:'orb_publish'
            to: Variable:'multi_pos'
        -   type: Set
            loc: mod_uorbc:28:23
            from: Function:'orb_publish'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:29:22
            from: Function:'orb_publish'
            to: Variable:'p'
        -   type: Set
            loc: mod_uorbc:33:31
            from: Function:'orb_publish'
            to: Variable:'nonq'
        -   type: Set
            loc: mod_uorbc:35:19
            from: Function:'orb_publish'
            to: Variable:'published'
        -   type: Set
            loc: mod_uorbc:39:16
            from: Function:'orb_publish'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:40:16
            from: Function:'orb_publish'
            to: Variable:'last_updated_time'
        -   type: Set
            loc: mod_uorbc:41:19
            from: Function:'orb_publish'
            to: Variable:'published'
        -   type: Set
            loc: mod_uorbc:44:23
            from: Function:'orb_publish'
            to: Variable:'authority_list'
        -   type: Set
            loc: mod_uorbc:50:28
            from: Function:'orb_publish'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc:54:13
            from: Function:'orb_publish'
            to: Variable:'buffer_space'
        -   type: Set
            loc: mod_uorbc:60:38
            from: Function:'orb_publish'
            to: Variable:'one_msg'
        -   type: Set
            loc: mod_uorbc:63:17
            from: Function:'orb_publish'
            to: Variable:'one_msg'
        -   type: Set
            loc: mod_uorbc:63:17
            from: Function:'orb_publish'
            to: Variable:'one_msg'
        -   type: Set
            loc: mod_uorbc:68:17
            from: Function:'orb_publish'
            to: Variable:'one_msg'
        -   type: Set
            loc: mod_uorbc:70:26
            from: Function:'orb_publish'
            to: Variable:'timestamp'
        -   type: Set
            loc: mod_uorbc:71:26
            from: Function:'orb_publish'
            to: Variable:'try_count'
        -   type: Set
            loc: mod_uorbc:84:16
            from: Function:'orb_publish'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:85:16
            from: Function:'orb_publish'
            to: Variable:'last_updated_time'
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
name: Set 32
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:9
            from: Function:'orb_subscribe'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc:4:9
            from: Function:'orb_subscribe'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:5:9
            from: Function:'orb_subscribe'
            to: Variable:'instance'
        -   type: Set
            loc: mod_uorbc:6:9
            from: Function:'orb_subscribe'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:7:14
            from: Function:'orb_subscribe'
            to: Variable:'cur_msg_pos'
        -   type: Set
            loc: mod_uorbc:8:10
            from: Function:'orb_subscribe'
            to: Variable:'queue_size'
        -   type: Set
            loc: mod_uorbc:16:5
            from: Function:'orb_subscribe'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:27:9
            from: Function:'orb_subscribe'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:30:23
            from: Function:'orb_subscribe'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:31:22
            from: Function:'orb_subscribe'
            to: Variable:'p'
        -   type: Set
            loc: mod_uorbc:38:17
            from: Function:'orb_subscribe'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc:44:20
            from: Function:'orb_subscribe'
            to: Variable:'registered_list'
        -   type: Set
            loc: mod_uorbc:51:36
            from: Function:'orb_subscribe'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc:52:21
            from: Function:'orb_subscribe'
            to: Variable:'cur_msg_pos'
        -   type: Set
            loc: mod_uorbc:54:24
            from: Function:'orb_subscribe'
            to: Variable:'queue_lastmsg_pos'
        -   type: Set
            loc: mod_uorbc:58:17
            from: Function:'orb_subscribe'
            to: Variable:'ret'
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
name: Set 32
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:9
            from: Function:'orb_subscribe_multi'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc:4:9
            from: Function:'orb_subscribe_multi'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:5:9
            from: Function:'orb_subscribe_multi'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:6:9
            from: Function:'orb_subscribe_multi'
            to: Variable:'i'
        -   type: Set
            loc: mod_uorbc:14:5
            from: Function:'orb_subscribe_multi'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:20:9
            from: Function:'orb_subscribe_multi'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:22:23
            from: Function:'orb_subscribe_multi'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:23:22
            from: Function:'orb_subscribe_multi'
            to: Variable:'p'
        -   type: Set
            loc: mod_uorbc:29:17
            from: Function:'orb_subscribe_multi'
            to: Variable:'ret'
        -   type: Set
            loc: mod_uorbc:34:20
            from: Function:'orb_subscribe_multi'
            to: Variable:'registered_list'
        -   type: Set
            loc: mod_uorbc:35:17
            from: Function:'orb_subscribe_multi'
            to: Variable:'ret'
```

```cpp
//// mod_uorbc.c
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
name: Set 33
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:9
            from: Function:'orb_unsubscribe'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:4:9
            from: Function:'orb_unsubscribe'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:5:9
            from: Function:'orb_unsubscribe'
            to: Variable:'instance'
        -   type: Set
            loc: mod_uorbc:6:9
            from: Function:'orb_unsubscribe'
            to: Variable:'i'
        -   type: Set
            loc: mod_uorbc:13:5
            from: Function:'orb_unsubscribe'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:14:5
            from: Function:'orb_unsubscribe'
            to: Variable:'instance'
        -   type: Set
            loc: mod_uorbc:16:19
            from: Function:'orb_unsubscribe'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:17:18
            from: Function:'orb_unsubscribe'
            to: Variable:'p'
        -   type: Set
            loc: mod_uorbc:21:9
            from: Function:'orb_unsubscribe'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:28:20
            from: Function:'orb_unsubscribe'
            to: Variable:'registered_list'
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
name: Set 34
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:9
            from: Function:'orb_copy'
            to: Variable:'multi_pos'
        -   type: Set
            loc: mod_uorbc:4:9
            from: Function:'orb_copy'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:5:9
            from: Function:'orb_copy'
            to: Variable:'i'
        -   type: Set
            loc: mod_uorbc:6:9
            from: Function:'orb_copy'
            to: Variable:'registered_len'
        -   type: Set
            loc: mod_uorbc:6:29
            from: Function:'orb_copy'
            to: Variable:'authority_len'
        -   type: Set
            loc: mod_uorbc:7:14
            from: Function:'orb_copy'
            to: Variable:'cur_msg_pos'
        -   type: Set
            loc: mod_uorbc:8:10
            from: Function:'orb_copy'
            to: Variable:'queue_size'
        -   type: Set
            loc: mod_uorbc:9:9
            from: Function:'orb_copy'
            to: Variable:'sub_val'
        -   type: Set
            loc: mod_uorbc:10:20
            from: Function:'orb_copy'
            to: Variable:'p_ring'
        -   type: Set
            loc: mod_uorbc:11:12
            from: Function:'orb_copy'
            to: Variable:'peek_pos'
        -   type: Set
            loc: mod_uorbc:13:9
            from: Function:'orb_copy'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:19:9
            from: Function:'orb_copy'
            to: Variable:'multi_pos'
        -   type: Set
            loc: mod_uorbc:20:23
            from: Function:'orb_copy'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:21:22
            from: Function:'orb_copy'
            to: Variable:'p'
        -   type: Set
            loc: mod_uorbc:39:13
            from: Function:'orb_copy'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:44:32
            from: Function:'orb_copy'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc:45:17
            from: Function:'orb_copy'
            to: Variable:'cur_msg_pos'
        -   type: Set
            loc: mod_uorbc:46:21
            from: Function:'orb_copy'
            to: Variable:'idpos'
        -   type: Set
            loc: mod_uorbc:54:17
            from: Function:'orb_copy'
            to: Variable:'queue_size'
        -   type: Set
            loc: mod_uorbc:58:24
            from: Function:'orb_copy'
            to: Variable:'queue_lastmsg_pos'
        -   type: Set
            loc: mod_uorbc:61:26
            from: Function:'orb_copy'
            to: Variable:'msg_pan'
        -   type: Set
            loc: mod_uorbc:62:27
            from: Function:'orb_copy'
            to: Variable:'que_buf'
        -   type: Set
            loc: mod_uorbc:65:17
            from: Function:'orb_copy'
            to: Variable:'que_buf'
        -   type: Set
            loc: mod_uorbc:66:26
            from: Function:'orb_copy'
            to: Variable:'_que_buf'
        -   type: Set
            loc: mod_uorbc:70:21
            from: Function:'orb_copy'
            to: Variable:'p_ring'
        -   type: Set
            loc: mod_uorbc:71:21
            from: Function:'orb_copy'
            to: Variable:'peek_pos'
        -   type: Set
            loc: mod_uorbc:72:21
            from: Function:'orb_copy'
            to: Variable:'peek_pos'
        -   type: Set
            loc: mod_uorbc:73:46
            from: Function:'orb_copy'
            to: Variable:'queue_msg'
        -   type: Set
            loc: mod_uorbc:82:33
            from: Function:'orb_copy'
            to: Variable:'que_buf'
        -   type: Set
            loc: mod_uorbc:84:33
            from: Function:'orb_copy'
            to: Variable:'_que_buf'
        -   type: Set
            loc: mod_uorbc:89:40
            from: Function:'orb_copy'
            to: Variable:'authority_list'
        -   type: Set
            loc: mod_uorbc:90:29
            from: Function:'orb_copy'
            to: Variable:'_que_buf'
        -   type: Set
            loc: mod_uorbc:96:36
            from: Function:'orb_copy'
            to: Variable:'authority_list'
        -   type: Set
            loc: mod_uorbc:97:25
            from: Function:'orb_copy'
            to: Variable:'_que_buf'
        -   type: Set
            loc: mod_uorbc:106:28
            from: Function:'orb_copy'
            to: Variable:'queue_lastmsg_pos'
        -   type: Set
            loc: mod_uorbc:109:31
            from: Function:'orb_copy'
            to: Variable:'buffer'
        -   type: Set
            loc: mod_uorbc:113:35
            from: Function:'orb_copy'
            to: Variable:'nonq'
        -   type: Set
            loc: mod_uorbc:130:27
            from: Function:'orb_copy'
            to: Variable:'published'
```

```cpp
//// mod_uorbc.c
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
name: Set 34
relation:
    items:
        -   type: Set
            loc: mod_uorbc:9:9
            from: Function:'orb_check'
            to: Variable:'orb_id'
        -   type: Set
            loc: mod_uorbc:10:9
            from: Function:'orb_check'
            to: Variable:'multi_pos'
        -   type: Set
            loc: mod_uorbc:19:19
            from: Function:'orb_check'
            to: Variable:'orb'
        -   type: Set
            loc: mod_uorbc:20:18
            from: Function:'orb_check'
            to: Variable:'p'
        -   type: Set
            loc: mod_uorbc:22:9
            from: Function:'orb_check'
            to: Variable:'task_id'
        -   type: Set
            loc: mod_uorbc:23:9
            from: Function:'orb_check'
            to: Variable:'hit'
        -   type: Set
            loc: mod_uorbc:33:10
            from: Function:'orb_check'
            to: Variable:'authorised'
        -   type: Set
            loc: mod_uorbc:36:24
            from: Function:'orb_check'
            to: Variable:'q'
        -   type: Set
            loc: mod_uorbc:37:13
            from: Function:'orb_check'
            to: Variable:'msg_span'
        -   type: Set
            loc: mod_uorbc:40:13
            from: Function:'orb_check'
            to: Variable:'msg_span'
        -   type: Set
            loc: mod_uorbc:50:27
            from: Function:'orb_check'
            to: Variable:'nonq'
        -   type: Set
            loc: mod_uorbc:55:17
            from: Function:'orb_check'
            to: Variable:'authorised'
```

```cpp
//// mod_uorbc.c
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
name: Set 35
relation:
    items:
        -   type: Set
            loc: mod_uorbc:3:14
            from: Function:'orb_group_count'
            to: Variable:'instance'
```