class Student {
public:
    int age;
};

void setAge(Student& student, int newAge) {
    student.age = newAge;
}

void demo() {
    Student s;
    setAge(s, 20);
}