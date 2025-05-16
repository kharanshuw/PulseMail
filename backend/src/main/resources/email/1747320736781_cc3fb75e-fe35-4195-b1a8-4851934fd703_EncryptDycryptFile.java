package FileEncryption;

import java.io.*;

public class EncryptDycryptFile {

    public static void encrypt(String source,String dest) throws IOException {

        BufferedReader bufferedReader=new BufferedReader(new FileReader(source));
        BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(dest));
        String name= bufferedReader.readLine();
        String opeartion[];
        while(name!=null)
        {
            opeartion=name.split(" ");
            for (int i = 0; i <opeartion.length; i++)
            {

                for (int j = 0; j <opeartion[i].length(); j++)
                {
                      int value=opeartion[i].codePointAt(j);
                      System.out.println(value);
                      bufferedWriter.write(String.valueOf(value));
                      bufferedWriter.write(",");
                }
                bufferedWriter.write(" ");
            }
            name= bufferedReader.readLine();
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

    }


    public static void dycrypt(String source,String dest) throws IOException
    {
        BufferedReader bufferedReader=new BufferedReader(new FileReader(source));
        BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(dest));
        String name= bufferedReader.readLine();
        String word[];
        while(name!=null)
        {
            word=name.split(" ");
            for (int i = 0; i <word.length ; i++)
            {
                 String numbers[]=word[i].split(",");
                for (int j = 0; j <numbers.length ; j++)
                {

                    bufferedWriter.write(Integer.parseInt(numbers[j]));
                }
                bufferedWriter.write(" ");
            }


            name= bufferedReader.readLine();
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

    }
}
