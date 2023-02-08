import java.util.*;

public class Genetic1 {
    public static void main(String[] args) {
        DNA[] pop = new DNA[100];
        int popsize=150;
        ArrayList<DNA> matingPool ;
        String best="";
        int gen=0;
        for (int i=0;i<pop.length;i++)
        {
            pop[i] = new DNA();
        }
        String target=pop[0].target;
        while (!best.equals(target))
        {
            gen++;
            matingPool = new ArrayList<>();
            int bestfitness=0;
            int bestfitnessindex=0;
            for (int i=0;i<pop.length;i++)
            {
                int n = (int)(pop[i].fitness()*100);
                for (int j=0;j<n;j++)
                {
                    matingPool.add(pop[i]);
                }
                if (n>bestfitness)
                {
                    bestfitness=n;
                    bestfitnessindex=i;
                    best=pop[i].getPhrase();
                }
            }
            System.out.println(pop[bestfitnessindex].getPhrase()+" (Generation "+gen+")");
            if (matingPool.size()==0)
            {
                matingPool.add(new DNA());
            }
            for (int i=0;i<pop.length;i++)
            {
                int a = (int)(Math.random()*matingPool.size());
                int b = (int)(Math.random()*matingPool.size());

                DNA parentA = matingPool.get(a);
                DNA parentB = matingPool.get(b);

                DNA child=parentA.crossover(parentB);
                child.mutate();

                pop[i] = child;
            }

        }
    }
}

class DNA{
    String target = "Really big phrases are hard";
    char[] genes = new char[target.length()];
    float fitness;

    public DNA()
    {
        for (int i=0;i<genes.length;i++)
        {
            genes[i] = (char)((int)(Math.random()*96+32));
        }
    }
    public float fitness()
    {
        int score = 0;
        for (int i=0;i<genes.length;i++)
        {
            if (genes[i] == target.charAt(i))
            {
                score++;
            }
        }
        //fitness=((float)score)/target.length();
        fitness=((float)score*score/10)/(target.length());
        return fitness;
    }
    public DNA crossover(DNA parentB)
    {
        DNA child=new DNA();

        for (int i=0;i<genes.length;i++)
        {
            if ((int)(Math.random()*2)==0) child.genes[i] = genes[i];
            else child.genes[i]= parentB.genes[i];
        }
        return child;
    }
    public void mutate()
    {
        for (int i=0;i<genes.length;i++)
        {
            if ((int)(Math.random()*100)==1) genes[i]= (char)((int)(Math.random()*96+32));
        }
    }
    public String getPhrase()
    {
        return new String(genes);
    }
}