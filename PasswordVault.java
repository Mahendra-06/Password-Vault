import java.io.*;
import java.util.*;

class Credential implements Serializable {
    private final String website;
    private final String username;
    private final String password;
    public Credential(String website, String username, String password) {
        this.website = website;
        this.username = username;
        this.password = password;
    }
    public String getWebsite() {
        return website;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
public class PasswordVault {
    private static final String FILE_NAME = "vault.txt";
    private static final Scanner sc = new Scanner(System.in);
    private static String masterPassword;
    private static String encrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append((char) (c + 3)); 
        }
        return sb.toString();
    }
    private static String decrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append((char) (c - 3));
        }
        return sb.toString();
    }
    private static void saveData(List<Credential> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(list);
            System.out.println("✅ Data saved successfully!");
        } catch (IOException e) {
            System.out.println("❌ Error saving data: " + e.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    private static List<Credential> loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (ArrayList<Credential>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    private static void addCredential(List<Credential> list) {
        System.out.print("Enter Website: ");
        String website = sc.nextLine();
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        String encryptedPassword = encrypt(password);
        list.add(new Credential(website, username, encryptedPassword));
        System.out.println("🔐 Credential added successfully!");
    }
    private static void viewCredentials(List<Credential> list) {
        if (list.isEmpty()) {
            System.out.println("No credentials stored yet!");
            return;
        }
        System.out.println("\n--- Stored Credentials ---");
        for (Credential c : list) {
            System.out.println("Website: " + c.getWebsite());
            System.out.println("Username: " + c.getUsername());
            System.out.println("Password: " + decrypt(c.getPassword()));
            System.out.println("-------------------------");
        }
    }
    private static boolean checkMasterPassword() {
        File f = new File("master.key");
        if (!f.exists()) {
            System.out.print("Set your master password: ");
            masterPassword = sc.nextLine();
            try (FileWriter fw = new FileWriter(f)) {
                fw.write(encrypt(masterPassword));
                System.out.println("Master password created successfully!");
            } catch (IOException e) {
                System.out.println("Error creating master password!");
            }
            return true;
        } else {
            System.out.print("Enter master password: ");
            String entered = sc.nextLine();
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String saved = decrypt(br.readLine());
                if (entered.equals(saved)) {
                    System.out.println("✅ Access Granted!");
                    return true;
                } else {
                    System.out.println("❌ Incorrect master password!");
                    return false;
                }
            } catch (IOException e) {
                System.out.println("Error reading master password!");
                return false;
            }
        }
    }
    public static void main(String[] args) {
        if (!checkMasterPassword()) {
            System.out.println("Exiting program...");
            return;
        }
        List<Credential> credentials = loadData();
        int choice;
        do {
            System.out.println("\n=== Password Vault Menu ===");
            System.out.println("1. Add Credential");
            System.out.println("2. View All Credentials");
            System.out.println("3. Save & Exit");
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1 -> addCredential(credentials);
                case 2 -> viewCredentials(credentials);
                case 3 -> {
                    saveData(credentials);
                    System.out.println("👋 Exiting... Bye!");
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        } while (choice != 3);
    }
}