package gk;

import gk.clientstuff.Chat;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
            if(args.length!=0){
                System.out.println("Put two arguments :\n1-nick\n2-port");
            }else{
                try {


                    //int port = Integer.parseInt(args[1]);
                    new Chat("grzesiek", 4444);
                }catch (Exception e){
                    System.out.println("second argument must be integer type");
                }
            }


    }
}
