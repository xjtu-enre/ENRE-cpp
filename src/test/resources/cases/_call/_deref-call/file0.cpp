void run_benchmark(void (*setup)(void*), void* data) {
    int i;
    for (i = 0; i < count; i++) {
        if (setup != NULL) {
            setup(data);
        }
    }
}