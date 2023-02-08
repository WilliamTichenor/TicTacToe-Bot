import java.io.IOException;
import java.util.Scanner;

public class PreceptronLine {
    public static void main(String[] args) throws IOException {
        boolean terror=true;
        double[] weights={Math.random()*2-1,Math.random()*2-1};
        double bias=Math.random()*2-1;
        double lrate=0.01;
        while (terror)
        {
            System.out.println("Total Error = "+terror);
            terror=false;
            for (int i=0;i<100;i++) {
                double[] datac = new double[3];
                datac[0]=Math.random()*20-10;
                datac[1]=Math.random()*20-10;
                if (2.0/3.0*datac[0]+2>datac[1]) datac[2]=0;
                else datac[2]=1;
                System.out.println(datac[0] + " " + datac[1] + " " + datac[2]);
                double ans = datac[0] * weights[0] + datac[1] * weights[1] + bias;
                int ans2;
                if (ans>0) ans2=1;
                else ans2=0;
                int error = (int)datac[2] - ans2;
                bias += lrate * error;
                weights[0] += lrate * error * datac[0];
                weights[1] += lrate * error * datac[1];
                System.out.println(ans2 + " " + error);
                if (error!=0) terror=true;
            }
        }
        Scanner scan=new Scanner(System.in);
        while (true)
        {
            double x = Double.parseDouble(scan.nextLine());
            double y = Double.parseDouble(scan.nextLine());
            int realAns;
            if (2.0/3.0*x+2<y) realAns=1;
            else realAns=0;
            double ans = x*weights[0]+y*weights[1]+bias;
            int ans2;
            if (ans>0) ans2=1;
            else ans2=0;
            double error = realAns-ans;
            System.out.println("Predict: "+ans2+" Real Answer: "+realAns);
        }
    }
}