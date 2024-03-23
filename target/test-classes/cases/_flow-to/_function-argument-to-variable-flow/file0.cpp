void setValue(int &value) {
    int localValue = value; // Flow from 'value' argument to 'localValue'
}

int main() {
    int num = 10;
    setValue(num);
}