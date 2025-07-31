import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "1";
        String hash = encoder.encode(password);
        
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("Verification: " + encoder.matches(password, hash));
        
        // Also test with the existing hash
        String existingHash = "$2a$10$eBv9DdHqXPkU5U/5zW6B8.7kW5Z7nZdLxqF7qWyYOXtT0JhLdTuQu";
        System.out.println("\nTesting existing hash:");
        System.out.println("Password '1' matches existing hash: " + encoder.matches("1", existingHash));
    }
} 