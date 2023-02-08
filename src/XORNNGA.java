import java.io.IOException;
import java.util.*;
public class XORNNGA {
    public static void main(String[] args) throws IOException {

        /*XOR Table:
            0 0 0
            0 1 1
            1 0 0
            1 1 0
        */

        int popsize = 100;
        Network[] pop = new Network[popsize];
        for (int i = 0; i < popsize; i++) {
            pop[i] = new Network();
        }
        Network bestNetwork=null;
        double overallBestFitness=-1;
        for (int p = 0; p < 100000; p++) {
            double bestfitness=-1;
            ArrayList<Network> matingPool = new ArrayList<>();
            for (int i = 0; i < popsize; i++) {
                double fitness = pop[i].calcFitness();
                if (fitness>bestfitness) bestfitness=fitness;
                if (fitness>overallBestFitness)
                {
                    overallBestFitness=fitness;
                    bestNetwork=pop[i];
                }
                for (int n = 0; n < fitness; n++) {
                    matingPool.add(pop[i]);
                }
            }
            System.out.println("Generation "+p+": "+bestfitness);
            for (int i=0;i<popsize;i++)
            {
                Network one=matingPool.get((int)(Math.random()*matingPool.size()));
                Network two=matingPool.get((int)(Math.random()*matingPool.size()));
                Network child=one.breed(two);
                child.mutate();
                pop[i]=child;
            }
        }
        System.out.println("Manual Testing for XOR Phase:");
        Scanner scan = new Scanner(System.in);
        while (true)
        {
            int one=scan.nextInt();
            int two=scan.nextInt();
            double ans=bestNetwork.calc(one,two);
            System.out.println("Guess: "+ans+" ("+Math.round(ans)+")");
            if (one==0&&two==0) System.out.println("Answer: 0");
            else if (one==1&&two==0) System.out.println("Answer: 1");
            else if (one==0&&two==1) System.out.println("Answer: 1");
            else if (one==1&&two==1) System.out.println("Answer: 0");
            else System.out.println("Invalid Input!");
        }
    }
}

class Network
{
    double[] weights;
    public Network()
    {
        weights=new double[9];
        for (int i = 0; i < 9; i++) {
            weights[i]=Math.random()*2-1;
        }
    }
    public Network(double[] w)
    {
        weights=w;
    }
    public double[] getWeights()
    {
        return weights;
    }
    public double calc(int one, int two)
    {
        //double ans=one
        double h1=sigmoid(one*weights[0]+two*weights[1]);
        double h2=sigmoid(one*weights[2]+two*weights[3]);
        double h3=sigmoid(one*weights[4]+two*weights[5]);
        return sigmoid(h1*weights[6]+h2*weights[7]+h3*weights[8]);
    }
    public double calcFitness()
    {
        //Max Fitness = 64
        //All 0s = 16
        double error1=Math.abs(0-calc(0,0));
        double error2=Math.abs(1-calc(1,0));
        double error3=Math.abs(1-calc(0,1));
        double error4=Math.abs(0-calc(1,1));
        return Math.pow(8-2*(error1+error2+error3+error4),2);
    }
    public double sigmoid(double d)
    {
        return Math.pow(Math.E,d)/(Math.pow(Math.E,d)+1);
    }
    public Network breed(Network other)
    {
        double[] otherWeights=other.getWeights();
        double[] newWeights = new double[9];
        for (int i = 0; i < 9; i++) {
            if (Math.random()>.5) newWeights[i]=weights[i];
            else newWeights[i]=otherWeights[i];
        }
        return new Network(newWeights);
    }
    public void mutate()
    {
        double mutationChance=.5;
        double[] newWeights = new double[9];
        for (int i=0;i<9;i++)
        {
            if (Math.random()*100<mutationChance)
            {
                if (Math.random()>.5) newWeights[i]=Math.random()*2-1;
                else newWeights[i]=weights[i]+Math.random()*2-1;
            }
            else newWeights[i]=weights[i];
        }
        weights=newWeights;
    }
    public String toString()
    {
        String out="";
        for (int i=0;i<9;i++)
        {
            out+=weights[i]+" ";
        }
        return out;
    }
}