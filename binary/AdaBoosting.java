package adaboosting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

class AdaBoosting
extends Gui {
    static double epsi;
    static double h;
    static double alph;
    static double splitLocation;
    static double z;
    static double e;
    static double e1;
    static int t;
    static int numberOfExamples;
    static double[] xx;
    static double[] p;
    static int[] y;
    static boolean positiveRight;
    static String fOfX;
    static String fX;

    AdaBoosting() {
    }

    protected static void adaBoosting() throws IOException {
        int i = 0;
        while (i < t) {
            bw.write("\nCurrent iteration(T)= " + (i + 1) + "\n");
            h = AdaBoosting.getH();
            String tempF = "";
            if (positiveRight) {
                tempF = "I(x>" + h + ")";
                bw.write("h(x)= I(x>" + h + ")" + "\n");
            } else {
                tempF = "I(x<" + h + ")";
                bw.write("h(x)= I(x<" + h + ")" + "\n");
            }
            epsi = AdaBoosting.getEpsilon();
            bw.write("epsilon= " + epsi + "\n");
            alph = AdaBoosting.getAlpha();
            bw.write("Alpha= " + alph + "\n");
            double[] tempPi = new double[numberOfExamples];
            z = 0.0;
            int k = 0;
            while (k < numberOfExamples) {
                tempPi[k] = AdaBoosting.getnewPi(k);
                z += tempPi[k];
                ++k;
            }
            bw.write("z= " + z + "\n");
            bw.write("Updated p= [ ");
            k = 0;
            while (k < numberOfExamples) {
                AdaBoosting.p[k] = tempPi[k] / z;
                bw.write(String.valueOf(p[k]) + " ");
                ++k;
            }
            bw.write("]\n");
            bw.write("fX= " + fX + "\n");
            fOfX = fOfX == null ? String.valueOf(alph) + tempF : fOfX.concat("   +   " + alph + tempF);
            bw.write("f(x)= " + fOfX + "\n");
            if (fX == null) {
                fX = "";
                int j = 0;
                while (j < numberOfExamples) {
                    fX = xx[j] < splitLocation ? fX.concat(String.valueOf(String.valueOf(positiveRight ? -1.0 * alph : alph)) + ", ") : fX.concat(String.valueOf(String.valueOf(positiveRight ? alph : -1.0 * alph)) + ", ");
                    ++j;
                }
            } else {
                String[] temp = fX.split(", ");
                fX = "";
                int j = 0;
                while (j < numberOfExamples) {
                    temp[j] = xx[j] < splitLocation ? String.valueOf(Double.valueOf(temp[j]) + Double.valueOf(positiveRight ? -1.0 * alph : alph)) : String.valueOf(Double.valueOf(temp[j]) + Double.valueOf(positiveRight ? alph : -1.0 * alph));
                    fX = fX.concat(String.valueOf(temp[j]) + ", ");
                    ++j;
                }
            }
            e1 = AdaBoosting.getE();
            bw.write("Error of boosted classifier Et= " + e1 + "\n");
            bw.write("Bound on Et is given by Et<= " + (e *= z) + "\n");
            ++i;
        }
    }

    private static double getnewPi(int i) {
        if (xx[i] > splitLocation) {
            return p[i] * Math.exp(-1.0 * alph * (double)y[i] * (double)(positiveRight ? 1 : -1));
        }
        return p[i] * Math.exp(-1.0 * alph * (double)y[i] * (double)(positiveRight ? -1 : 1));
    }

    private static double getE() {
        double wrong = 0.0;
        String[] temp = fX.split(", ");
        int i = 0;
        while (i < numberOfExamples) {
            if (Double.valueOf(temp[i]) > 0.0 && y[i] == -1 || Double.valueOf(temp[i]) < 0.0 && y[i] == 1) {
                wrong += 1.0;
            }
            ++i;
        }
        return wrong / (double)numberOfExamples;
    }

    private static double getAlpha() {
        return 0.5 * Math.log((1.0 - epsi) / epsi);
    }

    private static double getEpsilon() {
        return epsi;
    }

    private static double getH() {
        epsi = 0.0;
        int i = 0;
        while (i < numberOfExamples) {
            epsi += p[i];
            ++i;
        }
        positiveRight = false;
        splitLocation = xx[numberOfExamples - 1] + 0.5;
        i = 1;
        while (i < numberOfExamples) {
            double leftErrorPlus = 0.0;
            double leftErrorMinus = 0.0;
            double rightErrorPlus = 0.0;
            double rightErrorMinus = 0.0;
            int j = 0;
            while (j < i) {
                if (y[j] == 1) {
                    leftErrorMinus += p[j];
                } else if (y[j] == -1) {
                    leftErrorPlus += p[j];
                }
                ++j;
            }
            j = i;
            while (j < numberOfExamples) {
                if (y[j] == 1) {
                    rightErrorMinus += p[j];
                } else if (y[j] == -1) {
                    rightErrorPlus += p[j];
                }
                ++j;
            }
            if (leftErrorMinus + rightErrorPlus < epsi || leftErrorPlus + rightErrorMinus < epsi) {
                if (leftErrorMinus + rightErrorPlus <= leftErrorPlus + rightErrorMinus) {
                    epsi = leftErrorMinus + rightErrorPlus;
                    positiveRight = true;
                } else {
                    epsi = leftErrorPlus + rightErrorMinus;
                    positiveRight = false;
                }
                splitLocation = (xx[i - 1] + xx[i]) / 2.0;
            }
            ++i;
        }
        return splitLocation;
    }

    protected static void initialize(String filePath) throws IOException {
        e = 1.0;
        fX = null;
        bw.write("===Initializing===\n");
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line = "";
        line = br.readLine();
        String[] splitLine = line.split(" ");
        t = Integer.parseInt(splitLine[0]);
        numberOfExamples = Integer.parseInt(splitLine[1]);
        bw.write("T= " + t + "\n");
        bw.write("noOfExamples= " + numberOfExamples + "\n");
        xx = new double[numberOfExamples];
        line = br.readLine();
        splitLine = line.split(" ");
        bw.write("x[]= [ ");
        int i = 0;
        while (i < numberOfExamples) {
            AdaBoosting.xx[i] = Double.parseDouble(splitLine[i]);
            bw.write(String.valueOf(xx[i]) + " ");
            ++i;
        }
        bw.write("]\n");
        y = new int[numberOfExamples];
        line = br.readLine();
        splitLine = line.split(" ");
        bw.write("y[]= [ ");
        i = 0;
        while (i < numberOfExamples) {
            AdaBoosting.y[i] = Integer.parseInt(splitLine[i]);
            bw.write(String.valueOf(y[i]) + " ");
            ++i;
        }
        bw.write("]\n");
        p = new double[numberOfExamples];
        line = br.readLine();
        splitLine = line.split(" ");
        bw.write("p[]= [ ");
        i = 0;
        while (i < numberOfExamples) {
            AdaBoosting.p[i] = Double.parseDouble(splitLine[i]);
            bw.write(String.valueOf(p[i]) + " ");
            ++i;
        }
        bw.write("]\n");
        br.close();
        bw.write("===Initialize successful===\n");
    }
}