import java.util.*;
public class XORNN {
    public static void main(String[] args) {
        int[][] xorTable = {{1,0,1},{1,0,1},{1,0,1},{1,0,1}};
        XORNeuron input1=new XORNeuron();
        XORNeuron input2=new XORNeuron();
        XORNeuron[] inputs = new XORNeuron[]{input1,input2};
        XORNeuron hidden1=new XORNeuron(inputs);
        XORNeuron hidden2=new XORNeuron(inputs);
        XORNeuron hidden3=new XORNeuron(inputs);
        XORNeuron output=new XORNeuron(new XORNeuron[]{hidden1,hidden2,hidden3});

        //Start Propagation
        for (int p=0;p<10000;p++) {
            int randCase = (int) (Math.random() * 4);
            /*
            int randCase=0;
            if (p>2500) randCase=1;
            if (p>5000) randCase=2;
            if (p>7500) randCase=3;
            */

            /*
            input1 = new XORNeuron();
            input2 = new XORNeuron();
            inputs = new XORNeuron[]{input1, input2};
            hidden1 = new XORNeuron(inputs);
            hidden2 = new XORNeuron(inputs);
            hidden3 = new XORNeuron(inputs);
            output = new XORNeuron(new XORNeuron[]{hidden1, hidden2, hidden3});
            */
            //System.out.println(output.getValue());

            input1.setValue((double) xorTable[randCase][0]);
            input2.setValue((double) xorTable[randCase][1]);

            hidden1.fwdPropagation();
            hidden2.fwdPropagation();
            hidden3.fwdPropagation();
            double sum = output.getValue();
            output.fwdPropagation();
            System.out.println(input1.getValue() + " " + input2.getValue() + " = " + output.getValue());

            //Back Propagation
            double outputError = xorTable[randCase][2] - output.getValue();
            System.out.println("Error: " + outputError+"\n");
            //System.out.println("Debug: "+Arrays.toString(hidden1.getWeights())+"\n");
            if (outputError==0) outputError=0.00001;
            double deltaOutputSum = output.sigmoidDerivative(output.getValueNoAF()) * outputError;
            double[] changeWeights = new double[3];
            double[] layerResults = output.getLayerResults();
            double[] oldWeights = output.getWeights();
            /*
            for (int i = 0; i < 3; i++)
                changeWeights[i] = deltaOutputSum / layerResults[i];
            */
            changeWeights[0]=deltaOutputSum/hidden1.getValue();
            changeWeights[1]=deltaOutputSum/hidden2.getValue();
            changeWeights[2]=deltaOutputSum/hidden3.getValue();
            double[] newWeights = new double[3];
            for (int i = 0; i < 3; i++)
                newWeights[i] = oldWeights[i] + changeWeights[i];
            output.setWeights(newWeights);

            double[] hiddenSum = new double[3];
            hiddenSum[0] = hidden1.getValueNoAF();
            hiddenSum[1] = hidden2.getValueNoAF();
            hiddenSum[2] = hidden3.getValueNoAF();

            double deltaHiddenSum[] = new double[3];
            for (int i = 0; i < 3; i++)
                deltaHiddenSum[i] = deltaOutputSum / oldWeights[i] * output.sigmoidDerivative(hiddenSum[i]);
            double deltaInputWeights[] = new double[6];
            //This code sponsored by Mark Snyder Racing

            deltaInputWeights[0] = deltaHiddenSum[0]/2;// * input1.getValue();
            //if (input1.getValue()==0) deltaInputWeights[0]=0;
            deltaInputWeights[1] = deltaHiddenSum[1]/2;// * input1.getValue();
            //if (input1.getValue()==0) deltaInputWeights[1]=0;
            deltaInputWeights[2] = deltaHiddenSum[2]/2;// * input1.getValue();
            //if (input1.getValue()==0) deltaInputWeights[2]=0;
            deltaInputWeights[3] = deltaHiddenSum[0]/2;// * input2.getValue();
            //if (input2.getValue()==0) deltaInputWeights[3]=0;
            deltaInputWeights[4] = deltaHiddenSum[1]/2;// * input2.getValue();
            //if (input2.getValue()==0) deltaInputWeights[4]=0;
            deltaInputWeights[5] = deltaHiddenSum[2]/2;// * input2.getValue();
            //if (input2.getValue()==0) deltaInputWeights[5]=0;
            double[] oldHWeights1 = hidden1.getWeights();
            double[] oldHWeights2 = hidden2.getWeights();
            double[] oldHWeights3 = hidden3.getWeights();
            double[] newHWeights1 = new double[2];
            double[] newHWeights2 = new double[2];
            double[] newHWeights3 = new double[2];
            newHWeights1[0] = oldHWeights1[0] + deltaInputWeights[0];
            newHWeights1[1] = oldHWeights1[1] + deltaInputWeights[1];
            newHWeights2[0] = oldHWeights2[0] + deltaInputWeights[2];
            newHWeights2[1] = oldHWeights2[1] + deltaInputWeights[3];
            newHWeights3[0] = oldHWeights3[0] + deltaInputWeights[4];
            newHWeights3[1] = oldHWeights3[1] + deltaInputWeights[5];
            hidden1.setWeights(newHWeights1);
            hidden2.setWeights(newHWeights2);
            hidden3.setWeights(newHWeights3);
        }
        while (true)
        {
            Scanner scan= new Scanner(System.in);
            double i1=scan.nextInt();
            double i2=scan.nextInt();
            input1.setValue(i1);
            input2.setValue(i2);

            hidden1.fwdPropagation();
            hidden2.fwdPropagation();
            hidden3.fwdPropagation();
            double sum = output.getValue();
            output.fwdPropagation();
            System.out.println(input1.getValue() + " " + input2.getValue() + " = " + output.getValue());
        }
    }
}

class XORNeuron
{
    XORNeuron[] inputs;
    double value;
    double valueNoAF;
    double[] weights;
    double bias;
    double layerResults[];
    public XORNeuron()
    {
        //This constructor is for input layer
    }
    public XORNeuron(XORNeuron[] inputNeurons)
    {
        //This constructor is for hidden/output layers
        inputs=inputNeurons;
        bias=Math.random();
        weights=new double[inputNeurons.length];
        for (int i=0;i<weights.length;i++)
        {
            weights[i]=Math.random();
        }
    }
    public void fwdPropagation()
    {
        double sum=0;
        layerResults=new double[inputs.length];
        for (int i=0;i<inputs.length;i++)
        {
            double temp=inputs[i].getValue()*weights[i];
            sum+=temp;
            layerResults[i]=temp;
        }
        //sum+=bias; //May not need
        setValueNoAF(sum);
        setValue(sigmoid(sum)); //Activation Function
    }
    public void setValue(double v)
    {
        //Set inputs
        value=v;
    }
    public double getValue()
    {
        //Get output
        return value;
    }
    public void setValueNoAF(double v)
    {
        //Set inputs
        valueNoAF=v;
    }
    public double getValueNoAF()
    {
        return valueNoAF;
    }
    public double sigmoid(double d)
    {
        d=1/(1+Math.pow(Math.E,-d));
        return d;
    }
    public double sigmoidDerivative(double d)
    {
        d=Math.pow(Math.E,-d)/Math.pow(1+Math.pow(Math.E,-d),2);
        return d;
    }
    public double[] getWeights()
    {
        return weights;
    }
    public double getBias()
    {
        return bias;
    }
    public void setWeights(double[] w)
    {
        weights=w;
    }
    public void setBias(double b)
    {
        bias=b;
    }
    public double[] getLayerResults()
    {
        return layerResults;
    }
}