package adaboosting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

class RealAdaBoosting
extends Gui {
    static double epsi;
    static double epsi1;
    static double h1;
    static double z1;
    static double e;
    static double e1;
    static double splitLocation;
    static double g;
    static double prPlus;
    static double prMinus;
    static double pwPlus;
    static double pwMinus;
    static double cPlus;
    static double cMinus;
    static int t;
    static int noOfExamples;
    static double[] x;
    static double[] p;
    static int[] y;
    static boolean rightPositive;
    static String fOfX;

    RealAdaBoosting() {
    }

    static void realAdaBoosting() throws IOException {
        int i = 0;
        while (i < t) {
            bw.write("\nCurrent iteration(T)= " + (i + 1) + "\n");
            h1 = RealAdaBoosting.getH();
            if (rightPositive) {
                bw.write("h(x)= I(x>" + h1 + ")" + "\n");
            } else {
                bw.write("h(x)= I(x<" + h1 + ")" + "\n");
            }
            g = RealAdaBoosting.getG();
            bw.write("Pr+ = " + prPlus + "    Pr- = " + prMinus + "    Pw+ = " + pwPlus + "    Pw- = " + pwMinus + "\n");
            bw.write("G= " + g + "\n");
            cPlus = RealAdaBoosting.getCPlus();
            cMinus = RealAdaBoosting.getCMinus();
            bw.write("C+ = " + cPlus + "\nC- =" + cMinus + "\n");
            double[] tempPi = new double[noOfExamples];
            z1 = 0.0;
            int k = 0;
            while (k < noOfExamples) {
                tempPi[k] = RealAdaBoosting.getnewPi(k);
                z1 += tempPi[k];
                ++k;
            }
            bw.write("z= " + z1 + "\n");
            bw.write("Updated p= [ ");
            k = 0;
            while (k < noOfExamples) {
                RealAdaBoosting.p[k] = tempPi[k] / z1;
                bw.write(String.valueOf(p[k]) + " ");
                ++k;
            }
            bw.write("]\n");
            if (fOfX == null) {
                fOfX = "";
                int j = 0;
                while (j < noOfExamples) {
                    fOfX = x[j] < splitLocation ? fOfX.concat(String.valueOf(String.valueOf(rightPositive ? cMinus : cPlus)) + ", ") : fOfX.concat(String.valueOf(String.valueOf(rightPositive ? cPlus : cMinus)) + ", ");
                    ++j;
                }
            } else {
                String[] temp = fOfX.split(", ");
                fOfX = "";
                int j = 0;
                while (j < noOfExamples) {
                    temp[j] = x[j] < splitLocation ? String.valueOf(Double.valueOf(temp[j]) + Double.valueOf(rightPositive ? cMinus : cPlus)) : String.valueOf(Double.valueOf(temp[j]) + Double.valueOf(rightPositive ? cPlus : cMinus));
                    fOfX = fOfX.concat(String.valueOf(temp[j]) + ", ");
                    ++j;
                }
            }
            bw.write("F(x)= " + fOfX + "\n");
            e1 = RealAdaBoosting.getE();
            bw.write("Error of boosted classifier Et= " + e1 + "\n");
            bw.write("Bound on Et is given by Et<= " + (e *= z1) + "\n");
            ++i;
        }
    }

    private static double getE() {
        double wrong = 0.0;
        String[] temp = fOfX.split(", ");
        int i = 0;
        while (i < noOfExamples) {
            if (Double.valueOf(temp[i]) > 0.0 && y[i] == -1 || Double.valueOf(temp[i]) < 0.0 && y[i] == 1) {
                wrong += 1.0;
            }
            ++i;
        }
        return wrong / (double)noOfExamples;
    }

    private static double getnewPi(int i) {
        if (x[i] > splitLocation) {
            return p[i] * Math.exp((double)(-1 * y[i]) * (rightPositive ? cPlus : cMinus));
        }
        return p[i] * Math.exp((double)(-1 * y[i]) * (rightPositive ? cMinus : cPlus));
    }

    private static double getCMinus() {
        return 0.5 * Math.log((pwPlus + epsi) / (prMinus + epsi));
    }

    private static double getCPlus() {
        return 0.5 * Math.log((prPlus + epsi) / (pwMinus + epsi));
    }

    private static double getG() {
        prPlus = 0.0;
        prMinus = 0.0;
        pwPlus = 0.0;
        pwMinus = 0.0;
        int i = 0;
        while (i < noOfExamples) {
            if (x[i] < splitLocation) {
                if (rightPositive) {
                    if (y[i] == 1) {
                        pwPlus += p[i];
                    } else {
                        prMinus += p[i];
                    }
                } else if (y[i] == 1) {
                    prPlus += p[i];
                } else {
                    pwMinus += p[i];
                }
            } else if (rightPositive) {
                if (y[i] == 1) {
                    prPlus += p[i];
                } else {
                    pwMinus += p[i];
                }
            } else if (y[i] == 1) {
                pwPlus += p[i];
            } else {
                prMinus += p[i];
            }
            ++i;
        }
        g = Math.sqrt(prPlus * pwMinus) + Math.sqrt(pwPlus * prMinus);
        return g;
    }

    private static double getH() {
        epsi1 = 0.0;
        int i = 0;
        while (i < noOfExamples) {
            epsi1 += p[i];
            ++i;
        }
        rightPositive = false;
        splitLocation = x[noOfExamples - 1] + 0.5;
        i = 1;
        while (i < noOfExamples) {
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
            while (j < noOfExamples) {
                if (y[j] == 1) {
                    rightErrorMinus += p[j];
                } else if (y[j] == -1) {
                    rightErrorPlus += p[j];
                }
                ++j;
            }
            if (leftErrorMinus + rightErrorPlus < epsi1 || leftErrorPlus + rightErrorMinus < epsi1) {
                if (leftErrorMinus + rightErrorPlus <= leftErrorPlus + rightErrorMinus) {
                    epsi1 = leftErrorMinus + rightErrorPlus;
                    rightPositive = true;
                } else {
                    epsi1 = leftErrorPlus + rightErrorMinus;
                    rightPositive = false;
                }
                splitLocation = (x[i - 1] + x[i]) / 2.0;
            }
            ++i;
        }
        return splitLocation;
    }

    static void initialize(String filePath) throws IOException {
        bw.write("===Initializing===\n");
        e = 1.0;
        fOfX = null;
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line = "";
        line = br.readLine();
        String[] splitLine = line.split(" ");
        t = Integer.parseInt(splitLine[0]);
        noOfExamples = Integer.parseInt(splitLine[1]);
        epsi = Double.parseDouble(splitLine[2]);
        bw.write("T= " + t + "\n");
        bw.write("noOfExamples= " + noOfExamples + "\n");
        bw.write("epsilon= " + epsi + "\n");
        x = new double[noOfExamples];
        line = br.readLine();
        splitLine = line.split(" ");
        bw.write("x[]= [ ");
        int i = 0;
        while (i < noOfExamples) {
            RealAdaBoosting.x[i] = Double.parseDouble(splitLine[i]);
            bw.write(String.valueOf(x[i]) + " ");
            ++i;
        }
        bw.write("]\n");
        y = new int[noOfExamples];
        line = br.readLine();
        splitLine = line.split(" ");
        bw.write("y[]= [ ");
        i = 0;
        while (i < noOfExamples) {
            RealAdaBoosting.y[i] = Integer.parseInt(splitLine[i]);
            bw.write(String.valueOf(y[i]) + " ");
            ++i;
        }
        bw.write("]\n");
        p = new double[noOfExamples];
        line = br.readLine();
        splitLine = line.split(" ");
        bw.write("p[]= [ ");
        i = 0;
        while (i < noOfExamples) {
            RealAdaBoosting.p[i] = Double.parseDouble(splitLine[i]);
            bw.write(String.valueOf(p[i]) + " ");
            ++i;
        }
        bw.write("]\n");
        br.close();
        bw.write("===Initialize successful===\n");
    }
}