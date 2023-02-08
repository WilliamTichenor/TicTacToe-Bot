import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TicTacToe {
    public static void main(String[] args) throws IOException {
        int popsize = 120;
        double[] fitnesses=new double[popsize];
        double bestfitness=-1;
        TNetwork pop[] = new TNetwork[popsize];
        for (int i = 0; i < popsize; i++) {
            pop[i] = new TNetwork();
        }
        TNetwork bestNetwork=new TNetwork();
        int gens = 10000;
        for (int p = 0; p < gens; p++) {
            bestfitness = -1;
            fitnesses=new double[popsize];
            ArrayList<TNetwork> matingPool = new ArrayList<>();
            for (int i = 0; i < popsize; i++) {
                double fitness = pop[i].calcFitness(bestNetwork);
                fitnesses[i]=fitness;
                //double fitness = pop[i].fitnessPlaceholder();
                //System.out.println(pop[i]);
                //System.out.println(fitness+" "+pop[i].calc(new int[] {1,1,1,1,1,1,1,1,1})+"\n");
                if (fitness > bestfitness) {
                    bestfitness = fitness;
                    bestNetwork = pop[i];
                }
                for (int n = 0; n < fitness; n++) {
                    matingPool.add(pop[i]);
                }
            }
            if (p%10000==0) System.out.println("Generation "+p+": "+bestfitness+" "+(bestfitness-27));
            if (p==gens-1) break;
            for (int i=0;i<popsize;i++)
            {
                TNetwork one=matingPool.get((int)(Math.random()*matingPool.size()));
                TNetwork two=matingPool.get((int)(Math.random()*matingPool.size()));
                TNetwork child=one.breed(two);
                child.mutate();
                pop[i]=child;
            }
        }
        int index=-1;
        while (true)
        {
            boolean pause=false;
            System.out.println("Play Game now");
            Scanner scan = new Scanner(System.in);
            int winner=0;
            int moves=0;
            int place=-1;
            int[] board=new int[9];
            for (int i=0;i<9;i++)
                board[i]=-1;
            while (winner==0&&moves<9)
            {
                place=(int)Math.round(bestNetwork.calc(board));
                if (place<0) place=0;
                if (place>8) place=8;
                while (board[place]!=-1)
                {
                    place++;
                    if (place>8) place=0;
                }
                moves++;
                board[place]=1;
                winner=bestNetwork.winnerCheck(board);
                if (winner!=0||moves>8) break;

                for (int i=0;i<3;i++)
                {
                    for (int n=0;n<3;n++)
                    {
                        if (board[i*3+n]==1) System.out.print("X ");
                        else if (board[i*3+n]==2) System.out.print("O ");
                        else System.out.print(". ");
                        //else System.out.print(((i*3)+n)+" ");
                    }
                    System.out.println();
                }
                System.out.println("Print Tile ID or 9/10/11 for save, load, and cycle: ");
                place=scan.nextInt();
                if (place==9||place==10||place==11) break;
                moves++;
                board[place]=2;
                winner=bestNetwork.winnerCheck(board);
            }
            if (place==9)
            {
                System.out.println(bestNetwork);
                continue;
            }
            if (place==10)
            {
                System.out.println("Enter Network Here: ");
                scan.nextLine();
                String[] newbots=scan.nextLine().split(" ");
                double[] newbotd=new double[newbots.length];
                for (int i=0;i<newbots.length;i++)
                {
                    newbotd[i]=Double.parseDouble(newbots[i]);
                }
                bestNetwork = new TNetwork(newbotd);
                continue;
            }
            if (place==11)
            {
                do
                {
                    index++;
                    if (index>=popsize) index=0;
                } while (fitnesses[index]!=bestfitness);
                System.out.println("Testing Network "+index);
                bestNetwork=pop[index];
                continue;
            }
            for (int i=0;i<3;i++)
            {
                for (int n=0;n<3;n++)
                {
                    if (board[i*3+n]==1) System.out.print("X ");
                    else if (board[i*3+n]==2) System.out.print("O ");
                    else System.out.print(". ");
                }
                System.out.println();
            }
        }
    }
}

class TNetwork
{
    double[] weights;
    public TNetwork()
    {
        weights=new double[50];
        for (int i = 0; i < 50; i++) {
            weights[i]=Math.random()*2-1;
        }
    }
    public TNetwork(double[] w)
    {
        weights=w;
    }
    public double[] getWeights()
    {
        return weights;
    }
    public double sigmoid(double d)
    {
        return Math.pow(Math.E,d)/(Math.pow(Math.E,d)+1);
    }
    public double calc(int[] board)
    {
        double[] hiddenVals = new double[5];
        double finalVal = 0;
        for (int i=0;i<5;i++)
        {
            for (int n=0;n<9;n++)
            {
                hiddenVals[i]+=board[n]*weights[(9*i)+n];
            }
        }
        for (int i=0;i<5;i++)
        {
            finalVal+=hiddenVals[i]*weights[45+i];
        }
        return finalVal;
    }
    public double fitnessPlaceholder()
    {
        return Math.pow(8-Math.abs(5-calc(new int[] {1,1,1,1,1,1,1,1,1})),2);
    }
    public double calcFitness(TNetwork champ)
    {
        int[] board = new int[9];
        for (int i=0;i<9;i++)
        {
            board[i]=-1;
        }
        int winner=0;
        int moves=0;
        int place;
        boolean nerf=false;
        if (Math.random()>0.5)
        {
            nerf=true;
            place=(int)Math.round(champ.calc(board));
            if (place<0) place=0;
            if (place>8) place=8;
            while (board[place]!=-1)
            {
                place++;
                if (place>8) place=0;
            }
            moves++;
            board[place]=2;
        }
        while (winner==0&& moves<9)
        {
            place=(int)Math.round(calc(board));
            if (place<0) place=0;
            if (place>8) place=8;
            while (board[place]!=-1)
            {
                place++;
                if (place>8) place=0;
            }
            moves++;
            board[place]=1;
            winner=winnerCheck(board);
            if (winner!=0||moves>8) break;

            place=(int)Math.round(champ.calc(board));
            if (place<0) place=0;
            if (place>8) place=8;
            while (board[place]!=-1)
            {
                place++;
                if (place>8) place=0;
            }
            moves++;
            board[place]=2;
            winner=winnerCheck(board);
        }
        double fitness=0;
        if (winner==2)
        {
            if (nerf) moves++;
            fitness=Math.pow(moves-4,2)-2;
        }
        if (winner==0) fitness=25;
        if (winner==1)
        {
            moves--;
            fitness=Math.pow(14-moves,2)+2;
        }
        //if (nerf) fitness+=5;

        /*
        for (int i=0;i<3;i++)
        {
            for (int n=0;n<3;n++)
            {
                System.out.print(board[i*3+n]);
            }
            System.out.println();
        }
        System.out.println(winner);

         */

        return fitness;
    }
    public int winnerCheck(int[] board)
    {
        if (board[0]!=-1&&board[0]==board[1]&&board[1]==board[2]) return board[0];
        if (board[3]!=-1&&board[3]==board[4]&&board[4]==board[5]) return board[3];
        if (board[6]!=-1&&board[6]==board[7]&&board[7]==board[8]) return board[6];

        if (board[0]!=-1&&board[0]==board[3]&&board[3]==board[6]) return board[0];
        if (board[1]!=-1&&board[1]==board[4]&&board[4]==board[7]) return board[1];
        if (board[2]!=-1&&board[2]==board[5]&&board[5]==board[8]) return board[2];

        if (board[0]!=-1&&board[0]==board[4]&&board[4]==board[8]) return board[0];
        if (board[2]!=-1&&board[2]==board[4]&&board[4]==board[6]) return board[2];

        return 0;
    }
    public TNetwork breed(TNetwork other)
    {
        double[] otherWeights=other.getWeights();
        double[] newWeights = new double[50];
        for (int i = 0; i < 50; i++) {
            if (Math.random()>.5) newWeights[i]=weights[i];
            else newWeights[i]=otherWeights[i];
        }
        return new TNetwork(newWeights);
    }
    public void mutate()
    {
        double mutationChance=2;
        double[] newWeights = new double[50];
        for (int i=0;i<50;i++)
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
        for (int i=0;i<50;i++)
        {
            out+=weights[i]+" ";
        }
        return out;
    }
}