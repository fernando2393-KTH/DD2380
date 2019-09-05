// Class to enclose all matrix operations
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader;
import java.lang.Math;

public class matrixOps {
    //IO
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
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                matrix[i][j] = Double.parseDouble(splitted[i * cols + j + 2]); // Two is added since 2 first values were rows and cols
        return matrix;
    }

    public static int[] read_int_vect(BufferedReader reader) { // TODO(Fernando); Use generics instead of int
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
        System.out.print(mat.length);  // Print rows
        System.out.print(" ");
        System.out.print(mat[0].length);  // Print cols
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                System.out.print(" ");
                System.out.print(mat[i][j]);
            }
            System.out.println();
        }
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
                for (int k = 0; k < cols_a; k++) sum += mat_a[i][k]*mat_b[k][j];
                result[i][j] = sum;
            }
        return result;
    }

    // multiplies given matrices element-wise (UNUSED)
    public static double[][] elem_wise_mult(double[][] mat_a, double[][] mat_b) {
        int rows_a = mat_a.length;
        int cols_a = mat_a[0].length;
        int rows_b = mat_b.length;
        int cols_b = mat_b[0].length;
        if (cols_a != cols_b || rows_a != rows_b)
            return null;
        double[][] result = new double[rows_a][cols_a];
        for (int i = 0; i < rows_a; i++)
            for (int j = 0; j < cols_b; j++)
                result[i][j] = mat_a[i][j]*mat_b[i][j];
        return result;
    }

    // given a vector, a matrix and  a col_id, returns vector of element-wise
    // multiplication of vector values and given column (useful at forward alg)
    public static double[][] vector_col_elem_wise_mult(double[][] vect, double[][] mat, int col) {
        int rows_mat = mat.length;
        int cols_mat = mat[0].length;
        int rows_vect = vect.length;
        int cols_vect = vect[0].length;
        if (cols_vect != rows_mat)
            return null;
        double[][] result = new double[1][cols_vect];
        for (int i = 0; i < cols_vect; i++)
            result[0][i] = vect[0][i]*mat[i][col];
        return result;
    }

    // BASIC ARITHMETIC
    // Returns the ssave multiplication of two doubles
    private static double save_mult(double a, double b) {
        double log_sum = Math.log(a) + Math.log(b);
        return Math.exp(log_sum);
    }
}