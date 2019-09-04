// Class to enclose all matrix operations
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader;

public class matrixOps {
    //IO
    // Read matrix from console
    public static double[][] read_matrix() {
        BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
        String encoded_matrix;
        try {
            encoded_matrix = reader.readLine());
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

    public static double[] read_vector() {
        BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
        String encoded_matrix;
        try {
            encoded_matrix = reader.readLine());
        } catch (Exception e) {
            System.err.println("Could not read from System.in");
            return null;
        }
        
        String[] splitted = encoded_matrix.split(" ");
        int length = Integer.parseInt(splitted[0]);
        double[] vector = new double[length]
        for (int i = 0; i < length; i++)
            vector[i] = Double.parseDouble(splitted[i + 1]);
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
}