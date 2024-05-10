public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World");
        int x =10,y=20;
        while(x>0){
            System.out.println(x);
            x--;
        }

        sumar(x,y);


    }
    public static void sumar(int a, int b){
        System.out.println(a+b);
    }
}
