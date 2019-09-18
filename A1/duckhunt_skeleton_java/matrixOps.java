
// Class to enclose all matrix operations
import java.awt.List;
import java.io.BufferedReader;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;

public class matrixOps {
    // IO
    // Read matrix from console
    public static double[][] read_matrix(BufferedReader reader) {
        String encoded_matrix;
        try {
            encoded_matrix = reader.readLine();
        } catch (Exception e) {
            System.err.println("Could not read from System.in");
            return null;
        }
        String[] splitted = encoded_matrix.split(" ");
        int rows = Integer.parseInt(splitted[0]);
        int cols = Integer.parseInt(splitted[1]);
        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrix[i][j] = Double.parseDouble(splitted[i * cols + j + 2]);
        return matrix;
    }

    public static int[] read_vector(BufferedReader reader) {
        String encoded_matrix;
        try {
            encoded_matrix = reader.readLine();
        } catch (Exception e) {
            System.err.println("Could not read from System.in");
            return null;
        }

        String[] splitted = encoded_matrix.split(" ");
        int length = Integer.parseInt(splitted[0]);
        int[] vector = new int[length];
        for (int i = 0; i < length; i++)
            vector[i] = Integer.parseInt(splitted[i + 1]);
        return vector;
    }

    // Given a console line, returns the encoded matrix
    public static void print_matrix(double[][] mat) {
        System.err.print(mat.length); // Print rows
        System.err.print(" ");
        System.err.print(mat[0].length); // Print cols
        System.err.println(); // Print cols
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                System.err.print(" ");
                double roundOff = Math.round(mat[i][j] * 100.0) / 100.0;
                System.err.print(roundOff);
            }
            System.err.println();
            System.err.println();
        }
    }

    // Given a console line, returns the encoded matrix
    public static void print_vector(double[] mat) {
        System.err.print(mat.length); // Print rows
        System.err.print(": ");
        System.err.println(); // Print cols
        for (int i = 0; i < mat.length; i++) {
            System.err.print(" ");
            double roundOff = Math.round(mat[i] * 10000.0) / 10000.0;
            System.err.print(roundOff);
        }
        System.err.println();
    }

    // Given a console line, returns the encoded matrix
    public static void print_matrix_as_row(double[][] mat) {
        System.err.print(mat.length); // Print rows
        System.err.print(" ");
        System.err.print(mat[0].length); // Print cols
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                System.err.print(" ");
                System.err.print(mat[i][j]);
            }
        }
        System.err.println();
    }

    // ARITHMETIC
    // returns mat_a*mat_b
    public static double[][] multiply(double[][] mat_a, double[][] mat_b) {
        int rows_a = mat_a.length;
        int cols_a = mat_a[0].length;
        int rows_b = mat_b.length;
        int cols_b = mat_b[0].length;
        if (cols_a != rows_b)
            return null;
        double[][] result = new double[rows_a][cols_b];
        double sum = 0;
        for (int i = 0; i < rows_a; i++)
            for (int j = 0; j < cols_b; j++) {
                sum = 0;
                for (int k = 0; k < cols_a; k++)
                    sum += mat_a[i][k] * mat_b[k][j];
                result[i][j] = sum;
            }
        return result;
    }

    // given a vector, a matrix and a col_id, returns vector of element-wise
    // multiplication of vector values and given column (useful at forward alg)
    public static double[][] vector_col_elem_wise_mult(double[][] vect, double[][] mat, int col) {
        int rows_mat = mat.length;
        int cols_vect = vect[0].length;
        if (cols_vect != rows_mat)
            return null;
        double[][] result = new double[1][cols_vect];
        for (int i = 0; i < cols_vect; i++)
            result[0][i] = vect[0][i] * mat[i][col];
        return result;
    }

    public static Pair<Double, Integer> maxVectorMatrixCol(double[][] vector, double[][] matrix, int col) {

        int rows_mat = matrix.length;
        int cols_vect = vector[0].length;
        if (cols_vect != rows_mat)
            return null;

        double maximum = -1;
        int max_position = -1;
        for (int i = 0; i < vector[0].length; i++) {
            double value = vector[0][i] * matrix[i][col];
            if (value > maximum) {
                maximum = value;
                max_position = i;
                // max_positions.clear();
                // max_positions.add(i);
            }
            // else if(value == maximum) {
            // max_positions.add(i);
            // }
        }

        Pair<Double, Integer> result = new Pair<Double, Integer>();
        result.first = maximum;
        result.second = max_position;
        return result;

    }

    public static double normFrob(double[][] matrix) {
        double norm = 0.0;
        int rows = matrix.length;
        int cols = matrix[0].length;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                norm = Math.hypot(norm, matrix[i][j]);
            }
        }
        return norm;
    }

    // Returns average of matrices
    public static double distance(double[][] A, double[][] B) {
        int rows = A.length;
        int cols = A[0].length;
        // double[][] solution = new double[rows][cols];
        double dist = 0;
        for (int i = 0; i < rows; ++i)
            for (int j = 0; j < cols; ++j)
                dist += Math.abs(A[i][j] - B[i][j]);
        return dist;
    }

    // Returns average of matrices
    public static double[][] average(double[][][] matrices) {
        int matrices_num = matrices.length;
        int rows = matrices[0].length;
        int cols = matrices[0][0].length;
        double[][] solution = new double[rows][cols];
        for (int k = 0; k < matrices_num; k++)
            for (int i = 0; i < rows; ++i)
                for (int j = 0; j < cols; ++j)
                    solution[i][j] += matrices[k][i][j] / matrices_num;
        return solution;
    }

    public static double[][] average2(double[][] A, double[][] B) {
        int matrices_num = 2;
        int rows = A.length;
        int cols = A[0].length;
        double[][] solution = new double[rows][cols];
        for (int i = 0; i < rows; ++i)
            for (int j = 0; j < cols; ++j)
                solution[i][j] = (A[i][j] + B[i][j]) / matrices_num;
        return solution;
    }

    // OTHER
    // public static int[] push(int[] arr, int item) {
    // int[] tmp = Arrays.copyOf(arr, arr.length + 1);
    // tmp[tmp.length - 1] = item;
    // return tmp;
    // }

    public static int[] push(int[] arr, int item) {
        int[] tmp = Arrays.copyOf(arr, arr.length + 1);
        tmp[tmp.length - 1] = item;
        return tmp;
    }

    public static double[][][] pushMat(double[][][] arr, double[][] mat) {
        double[][][] tmp = Arrays.copyOf(arr, arr.length + 1);
        tmp[tmp.length - 1] = mat;
        return tmp;
    }

    // Returns random array that adds up to 1
    public static double[] randomArray(int length, int enhance) {
        double[] vector = new double[length];
        // Generate random values
        double sum = 0;
        for (int i = 0; i < length; i++) {
            vector[i] = Math.random();
            if (i == enhance) // Makes diagonal elements higher to avoid bad conditioning
                vector[i] = 4 * Math.random();
            sum += vector[i];
        }
        // Divide each element by sum
        for (int i = 0; i < length; i++) {
            vector[i] = vector[i] / sum;
        }
        return vector;
    }

    // Returns a random matrix where rows add up to 1
    public static double[][] randomMatrix(int size_x, int size_y, Boolean enhance) {
        double[][] matrix = new double[size_x][size_y];
        for (int i = 0; i < size_x; i++) {
            double[] row_values;
            if (enhance)
                row_values = randomArray(size_y, i);
            else
                row_values = randomArray(size_y, -1);
            for (int j = 0; j < size_y; j++) {
                matrix[i][j] = row_values[j];
            }
        }
        return matrix;
    }

    // Given a matrix, copies it into another one
    public static void copy_matrix(double[][] o, double[][] d) {
        for (int i = 0; i < o.length; i++) {
            for (int j = 0; j < o[0].length; j++) {
                d[i][j] = o[i][j];
            }
        }
    }

    public static double[][] get_matrix(double[][] o) {
        double[][] d = new double[o.length][o[0].length];
        for (int i = 0; i < o.length; i++) {
            for (int j = 0; j < o[0].length; j++) {
                d[i][j] = o[i][j];
            }
        }
        return d;
    }
    
}
