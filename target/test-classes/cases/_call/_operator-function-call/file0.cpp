struct S
{
   operator ()
   {
      return func;
   }
};
int main()
{
   S s;
   s();
}