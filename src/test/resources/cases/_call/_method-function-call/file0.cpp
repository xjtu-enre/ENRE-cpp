class dog
{
public:
   void setAge(int age)     
   {
      _age = age;
   }
private:
   int _age;
};
int main()
{
   dog mongrel;
   mongrel.setAge(5);
}