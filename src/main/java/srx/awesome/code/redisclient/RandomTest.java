package srx.awesome.code.redisclient;

import java.util.Random;

public class RandomTest {
    public static void main(String[] args){
        for (int i = 0; i < 10; i++) {
            System.out.println("j:"+getRandomDouble(0.85,6));
        }
    }

    public static Double getRandomDouble(double a,int len){
        int size = (int)Math.pow(10, len);
        if(a>1){
            return null;
        }
        Random rand=new Random();
        Double j =(rand.nextInt(100000)+(a*size))/size;
        return j;
    }
}
