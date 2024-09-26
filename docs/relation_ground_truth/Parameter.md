## Relation: Parameter
Descriptions: `Parameter Relation`  is between `Function Entity` and `Variable Entity`. Information can be passed to functions as a parameter. Parameters act as variables inside the function.


### Supported Patterns
```yaml
name: Parameter
```
#### Syntax: Parameter Declaration

```text

```
##### Examples

###### Parameter

```cpp
//// mod_uorbc_platform.c
int mod_uorbc_platform_thread_create(void* id, void* (*start_rtn)(void*), void* arg)
{
	return pthread_create((pthread_t*) id, NULL, start_rtn, arg);
}

```

```yaml
name: Parameter_1
relation:
    items:
        -   type: Parameter
            loc: mod_uorbc_platform:1:44
            from: Function:'mod_uorbc_platform_thread_create'
            to: Variable:'id'
        -   type: Parameter
            loc: mod_uorbc_platform:1:56
            from: Function:'mod_uorbc_platform_thread_create'
            to: Variable:'start_rtn'
        -   type: Parameter
            loc: mod_uorbc_platform:1:81
            from: Function:'mod_uorbc_platform_thread_create'
            to: Variable:'arg'
```

```cpp
//// mod_uorbc_qos.h
int mod_uorbc_qos_enqueue(struct list_head* l);
```

```yaml
name: Parameter_2
relation:
    items:
        -   type: Parameter
            loc: mod_uorbc_qos:1:45
            from: Function:'mod_uorbc_qos_enqueue'
            to: Variable:'l'
```

```cpp
//// mod_uorbc_ringbuffer.h
ring_buffer_t* ring_buffer_init(size_t queue_size, size_t obj_size);
void ring_buffer_queue(ring_buffer_t* buffer, char data);
void ring_buffer_queue_arr(ring_buffer_t* buffer, const char* data, ring_buffer_size_t size);
uint8_t ring_buffer_dequeue(ring_buffer_t* buffer, char* data);
ring_buffer_size_t ring_buffer_dequeue_arr(ring_buffer_t* buffer, char* data, ring_buffer_size_t len);
uint8_t ring_buffer_peek(ring_buffer_t* buffer, char* data, ring_buffer_size_t index);
uint8_t ring_buffer_is_full(ring_buffer_t* buffer);
ring_buffer_size_t ring_buffer_left_space(ring_buffer_t* buffer);
size_t ring_buffer_get_size(ring_buffer_t* p);
ring_buffer_size_t ring_buffer_just_peek(ring_buffer_t* buffer, char* data, ring_buffer_size_t pos, ring_buffer_size_t len);
uint8_t* ring_buffer_pos_pointer(ring_buffer_t* buffer, ring_buffer_size_t pos);
uint32_t ring_buffer_to_the_power_of_2(uint32_t in_data);
```

```yaml
name: Parameter_3
relation:
    items:
        -   type: Parameter
            loc: mod_uorbc_qos:1:40
            from: Function:'ring_buffer_init'
            to: Variable:'queue_size'
        -   type: Parameter
            loc: mod_uorbc_qos:1:59
            from: Function:'ring_buffer_init'
            to: Variable:'obj_size'
        -   type: Parameter
            loc: mod_uorbc_qos:2:39
            from: Function:'ring_buffer_queue'
            to: Variable:'buffer'
        -   type: Parameter
            loc: mod_uorbc_qos:2:52
            from: Function:'ring_buffer_queue'
            to: Variable:'data'
        -   type: Parameter
            loc: mod_uorbc_qos:3:43
            from: Function:'ring_buffer_queue_arr'
            to: Variable:'buffer'
        -   type: Parameter
            loc: mod_uorbc_qos:3:63
            from: Function:'ring_buffer_queue_arr'
            to: Variable:'data'
        -   type: Parameter
            loc: mod_uorbc_qos:3:88
            from: Function:'ring_buffer_queue_arr'
            to: Variable:'size'
        -   type: Parameter
            loc: mod_uorbc_qos:4:44
            from: Function:'ring_buffer_dequeue'
            to: Variable:'buffer'
        -   type: Parameter
            loc: mod_uorbc_qos:4:58
            from: Function:'ring_buffer_dequeue'
            to: Variable:'data'
        -   type: Parameter
            loc: mod_uorbc_qos:5:59
            from: Function:'ring_buffer_dequeue_arr'
            to: Variable:'buffer'
        -   type: Parameter
            loc: mod_uorbc_qos:5:73
            from: Function:'ring_buffer_dequeue_arr'
            to: Variable:'data'
        -   type: Parameter
            loc: mod_uorbc_qos:5:98
            from: Function:'ring_buffer_dequeue_arr'
            to: Variable:'len'
        -   type: Parameter
            loc: mod_uorbc_qos:5:98
            from: Function:'ring_buffer_dequeue_arr'
            to: Variable:'len'
        -   type: Parameter
            loc: mod_uorbc_qos:6:41
            from: Function:'ring_buffer_peek'
            to: Variable:'buffer'
        -   type: Parameter
            loc: mod_uorbc_qos:6:55
            from: Function:'ring_buffer_peek'
            to: Variable:'data'
        -   type: Parameter
            loc: mod_uorbc_qos:6:80
            from: Function:'ring_buffer_peek'
            to: Variable:'index'
        -   type: Parameter
            loc: mod_uorbc_qos:7:44
            from: Function:'ring_buffer_is_full'
            to: Variable:'buffer'
        -   type: Parameter
            loc: mod_uorbc_qos:8:58
            from: Function:'ring_buffer_left_space'
            to: Variable:'buffer'
        -   type: Parameter
            loc: mod_uorbc_qos:9:44
            from: Function:'ring_buffer_get_size'
            to: Variable:'p'
        -   type: Parameter
            loc: mod_uorbc_qos:10:57
            from: Function:'ring_buffer_just_peek'
            to: Variable:'buffer'
        -   type: Parameter
            loc: mod_uorbc_qos:10:71
            from: Function:'ring_buffer_just_peek'
            to: Variable:'data'
        -   type: Parameter
            loc: mod_uorbc_qos:10:96
            from: Function:'ring_buffer_just_peek'
            to: Variable:'pos'
        -   type: Parameter
            loc: mod_uorbc_qos:10:120
            from: Function:'ring_buffer_just_peek'
            to: Variable:'len'
        -   type: Parameter
            loc: mod_uorbc_qos:11:49
            from: Function:'ring_buffer_pos_pointer'
            to: Variable:'buffer'
        -   type: Parameter
            loc: mod_uorbc_qos:11:76
            from: Function:'ring_buffer_pos_pointer'
            to: Variable:'pos'
        -   type: Parameter
            loc: mod_uorbc_qos:11:49
            from: Function:'ring_buffer_to_the_power_of_2'
            to: Variable:'in_data'
```

```cpp
//// mod_uorbc_utils.h
bool mod_uorbc_utils_ismulti(const int orb_id);
int mod_uorbc_utils_get_orbid(const char* name);
E_UORBC_ID mod_uorbc_utils_multi_getpos(const int orb_id);
```

```yaml
name: Parameter_4
relation:
    items:
        -   type: Parameter
            loc: mod_uorbc_utils:1:40
            from: Function:'mod_uorbc_utils_ismulti'
            to: Variable:'orb_id'
        -   type: Parameter
            loc: mod_uorbc_utils:2:43
            from: Function:'mod_uorbc_utils_get_orbid'
            to: Variable:'name'
        -   type: Parameter
            loc: mod_uorbc_utils:3:51
            from: Function:'mod_uorbc_utils_multi_getpos'
            to: Variable:'orb_id'
```

```cpp
//// uORB.h
extern orb_advert_t orb_advertise(const struct orb_metadata* meta, const void* data) ;
extern orb_advert_t orb_advertise_queue(const struct orb_metadata *meta, const void *data, unsigned int queue_size);
extern orb_advert_t orb_advertise_multi(const struct orb_metadata* meta, const void* data, int* instance) ;
extern orb_advert_t orb_advertise_multi_queue(const struct orb_metadata *meta, const void *data, int *instance, unsigned int queue_size);
extern int orb_unadvertise(orb_advert_t *handle);
extern int orb_publish_auto(const struct orb_metadata *meta, orb_advert_t *handle, const void *data, int *instance);
extern int orb_publish(const struct orb_metadata *meta, uint8_t retry, const void *data);
extern int orb_subscribe(const struct orb_metadata *meta);
extern int orb_subscribe_multi(const struct orb_metadata *meta, unsigned instance);
extern int orb_unsubscribe(int handle);
extern int orb_copy(const struct orb_metadata *meta, int handle, void *buffer);
extern int orb_exists(const struct orb_metadata *meta, int instance);
extern int orb_group_count(const struct orb_metadata *meta);
```

```yaml
name: Parameter_5
relation:
    items:
        -   type: Parameter
            loc: uORB:1:62
            from: Function:'orb_advertise'
            to: Variable:'meta'
        -   type: Parameter
            loc: uORB:1:80
            from: Function:'orb_advertise'
            to: Variable:'data'
        -   type: Parameter
            loc: uORB:2:68
            from: Function:'orb_advertise_queue'
            to: Variable:'meta'
        -   type: Parameter
            loc: uORB:2:86
            from: Function:'orb_advertise_queue'
            to: Variable:'data'
        -   type: Parameter
            loc: uORB:2:105
            from: Function:'orb_advertise_queue'
            to: Variable:'queue_size'
        -   type: Parameter
            loc: uORB:3:68
            from: Function:'orb_advertise_multi'
            to: Variable:'meta'
        -   type: Parameter
            loc: uORB:3:86
            from: Function:'orb_advertise_multi'
            to: Variable:'data'
        -   type: Parameter
            loc: uORB:3:97
            from: Function:'orb_advertise_multi'
            to: Variable:'instance'
        -   type: Parameter
            loc: uORB:4:74
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'meta'
        -   type: Parameter
            loc: uORB:4:92
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'data'
        -   type: Parameter
            loc: uORB:4:103
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'instance'
        -   type: Parameter
            loc: uORB:4:126
            from: Function:'orb_advertise_multi_queue'
            to: Variable:'queue_size'
        -   type: Parameter
            loc: uORB:5:42
            from: Function:'orb_unadvertise'
            to: Variable:'handle'
        -   type: Parameter
            loc: uORB:6:56
            from: Function:'orb_publish_auto'
            to: Variable:'meta'
        -   type: Parameter
            loc: uORB:6:76
            from: Function:'orb_publish_auto'
            to: Variable:'handle'
        -   type: Parameter
            loc: uORB:6:96
            from: Function:'orb_publish_auto'
            to: Variable:'data'
        -   type: Parameter
            loc: uORB:6:107
            from: Function:'orb_publish_auto'
            to: Variable:'instance'
        -   type: Parameter
            loc: uORB:7:51
            from: Function:'orb_publish'
            to: Variable:'meta'
        -   type: Parameter
            loc: uORB:7:65
            from: Function:'orb_publish'
            to: Variable:'retry'
        -   type: Parameter
            loc: uORB:7:84
            from: Function:'orb_publish'
            to: Variable:'data'
        -   type: Parameter
            loc: uORB:8:53
            from: Function:'orb_subscribe'
            to: Variable:'meta'
        -   type: Parameter
            loc: uORB:9:59
            from: Function:'orb_subscribe_multi'
            to: Variable:'meta'
        -   type: Parameter
            loc: uORB:9:74
            from: Function:'orb_subscribe_multi'
            to: Variable:'instance'
        -   type: Parameter
            loc: uORB:10:32
            from: Function:'orb_unsubscribe'
            to: Variable:'handle'
        -   type: Parameter
            loc: uORB:11:48
            from: Function:'orb_copy'
            to: Variable:'meta'
        -   type: Parameter
            loc: uORB:11:58
            from: Function:'orb_copy'
            to: Variable:'handle'
        -   type: Parameter
            loc: uORB:11:72
            from: Function:'orb_copy'
            to: Variable:'buffer'
        -   type: Parameter
            loc: uORB:12:50
            from: Function:'orb_exists'
            to: Variable:'meta'
        -   type: Parameter
            loc: uORB:12:60
            from: Function:'orb_exists'
            to: Variable:'instance'
        -   type: Parameter
            loc: uORB:13:55
            from: Function:'orb_group_count'
            to: Variable:'meta'
```
###### Array Parameter
