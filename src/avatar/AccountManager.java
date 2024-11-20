package avatar;

import java.io.*;

public class AccountManager {
    private static final String FILE_PATH = "src/accounts.txt";

    // Save account to text file
    public static void saveAccount(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(username + "," + password);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving account: " + e.getMessage());
        }
    }

    // Validate account credentials
    public static boolean isValidAccount(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[0].equals(username) && credentials[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading accounts: " + e.getMessage());
        }
        return false;
    }
}
