import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.awt.image.ImageObserver;

public class GeneticImages {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,500);

        Drawing d = new Drawing();
        frame.add(d);
        frame.setVisible(true);

        //Start Genetic Algorithm
        //Best so far: 105 pop, 5000 mRate (Got .868 Fitness)

        Pic[] pop = new Pic[105];
        Pic best=new Pic();
        Pic.target = null;
        int gen=0;
        try {
            Pic.target = ImageIO.read(new File("miniyoda.png"));
        } catch (IOException e) {
        }
        ArrayList<Pic> matingPool;

        frame.update(frame.getGraphics());

        for (int i=0;i<pop.length;i++)
        {
            Color[][] col= new Color[100][100];
            for (int r=0;r<100;r++)
            {
                for (int c=0;c<100;c++)
                {
                    col[r][c]=new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
                }
            }
            pop[i]=new Pic();
            pop[i].setPic(col);
        }

        while (true)
        {
            gen++;
            System.out.println("Generation: "+gen);
            matingPool = new ArrayList<>();
            double bestfitness=0;
            best=pop[0];
            int bestindex=0;
            for (int i=0;i<pop.length;i++)
            {
                pop[i].fitness();
            }
            //Sort + cull method
            for (int i=0;i<pop.length;i++)
            {
                for (int j=i;j<pop.length;j++)
                {
                    double n = pop[i].fitness;
                    //System.out.println(pop[i].fitness()+" "+n);
                    if (n>bestfitness)
                    {
                        bestfitness=n;
                        best=pop[j];
                        bestindex=j;
                    }
                }
                pop[bestindex]=pop[i];
                pop[i]=best;
            }
            //Fitness scale Method
            /*
            for (int i=0;i<pop.length;i++)
            {
                double n = pop[i].fitness*100;
                if (n>bestfitness)
                {
                    bestfitness=n;
                    best=pop[i];
                }
                for (int j=0;j<n;j++)
                {
                    matingPool.add(pop[i]);
                    //System.out.println("added "+i);
                }
            }
            */
            System.out.println("Best Fitness: "+best.fitness);
            Pic.bestBI=new BufferedImage(100,100, BufferedImage.TYPE_INT_RGB);
            for (int r=0;r<best.pic.length;r++)
            {
                for (int c=0;c<best.pic[0].length;c++)
                {
                    int rgb = 65536 * best.pic[r][c].getRed() + 256 * best.pic[r][c].getGreen() + best.pic[r][c].getBlue();
                    Pic.bestBI.setRGB(r, c, rgb);
                }
            }

            int cullnum=100;
            for (int i=0;i<cullnum;i++)
            {
                for (int j=0;j<Math.pow(cullnum-i,1.5);j++)
                {
                    matingPool.add(pop[i]);
                }
            }


            if (matingPool.size()==0)
            {
                continue;
            }
            for (int i=0;i<pop.length;i++)
            {
                int a = (int)(Math.random()*matingPool.size());
                int b = (int)(Math.random()*matingPool.size());

                Pic parentA = matingPool.get(a);
                Pic parentB = matingPool.get(b);

                Pic child=parentA.crossover(parentB);
                child.mutate();

                pop[i] = child;
            }


            //Graphics Crap
            frame.update(frame.getGraphics());
            try
            {
                Thread.sleep(0);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class Drawing extends JPanel {
    public void paintComponent(Graphics g) {
        g.drawImage(Pic.bestBI,0,0,500,500, 0, 0 , 100, 100, null);
    }
}

class Pic {
    Color[][] pic;
    double fitness;
    public static BufferedImage target;
    public static BufferedImage bestBI;
    Pic()
    {
        pic=new Color[100][100];
    }
    public void setPic(Color[][] p)
    {
        pic=p;
    }
    public Color[][] getPic()
    {
        return pic;
    }
    public double fitness()
    {
        int score=0;
        for (int r=0;r<pic.length;r++)
        {
            for (int c=0;c<pic[0].length;c++) {
                int rgb = target.getRGB(r,c);
               score+=255-(Math.abs(pic[r][c].getRed()-((rgb >> 16) & 0x000000FF)));
               score+=255-(Math.abs(pic[r][c].getGreen()-((rgb >> 8) & 0x000000FF)));
               score+=255-(Math.abs(pic[r][c].getBlue()-((rgb) & 0x000000FF)));
            }
        }
        fitness=((double)score/7650000);
        //System.out.println(fitness);
        return fitness;
    }
    public Pic crossover(Pic parentB)
    {
        Pic child=new Pic();

        for (int r=0;r<pic.length;r++)
        {
            for (int c=0;c<pic[0].length;c++)
            {
                if ((int)(Math.random()*2)==0) child.pic[r][c] = pic[r][c];
                else child.pic[r][c]= parentB.pic[r][c];
            }
        }
        return child;
    }
    public void mutate()
    {
        int mRate=10000;
        for (int r=0;r<pic.length;r++)
        {
            for (int c=0;c<pic[0].length;c++)
            {
                if ((int)(Math.random()*mRate)==1) pic[r][c]= new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
                if ((int)(Math.random()*mRate)==1)
                {
                    int newRed=pic[r][c].getRed()+(int)(Math.random()*20)-10;
                    if (newRed>255) newRed=255;
                    if (newRed<0) newRed=0;
                    int newBlue=pic[r][c].getBlue()+(int)(Math.random()*20)-10;
                    if (newBlue>255) newBlue=255;
                    if (newBlue<0) newBlue=0;
                    int newGreen=pic[r][c].getGreen()+(int)(Math.random()*20)-10;
                    if (newGreen>255) newGreen=255;
                    if (newGreen<0) newGreen=0;
                    pic[r][c]= new Color(newRed,newBlue,newGreen);
                }

            }
        }
    }
}