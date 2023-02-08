public class PreceptronTest {
    public static void main(String[] args) {
        boolean terror=true;
        int[][] data = {{1,1,1},{0,1,1},{0,0,0},{1,0,1}};
        double[] weights={Math.random()*2-1,Math.random()*2-1};
        double bias=Math.random()*2-1;
        double lrate=0.01;
        while (terror)
        {
            System.out.println("Total Error = "+terror);
            terror=false;
            for (int i=0;i<100;i++) {
                int[] datac = data[(int) (Math.random() * 4)];
                System.out.println(datac[0] + " " + datac[1] + " " + datac[2]);
                double ans = datac[0] * weights[0] + datac[1] * weights[1] + bias;
                int ans2;
                if (ans>0) ans2=1;
                else ans2=0;
                int error = datac[2] - ans2;
                bias += lrate * error;
                weights[0] += lrate * error * datac[0];
                weights[1] += lrate * error * datac[1];
                System.out.println(ans2 + " " + error);
                if (error<-0.01||error>0.01) terror=true;
            }
        }
        for (int[] d:data)
        {
            System.out.println(d[0]+" "+d[1]+" "+d[2]);
            double ans = d[0]*weights[0]+d[1]*weights[1]+bias;
            double error = d[2]-ans;
            System.out.println(Math.ceil(ans));
        }
    }
}