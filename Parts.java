import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Scanner;

public class Parts {

    static Scanner scanner = new Scanner(System.in);

    // Usuaris en memòria: nom -> {salt, hash}
    static HashMap<String, String[]> usuaris = new HashMap<>();

    // ---------- Funcions auxiliars ----------

    // Calcula el SHA-256 d'un array de bytes i el retorna en hexadecimal
    static String sha256(byte[] bytesIn) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hashResult = sha256.digest(bytesIn);
            return aHex(hashResult);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No s'ha trobat l'algoritme SHA-256: " + e.getMessage());
            return null;
        }
    }

    static String sha256(String text) {
        return sha256(text.getBytes(StandardCharsets.UTF_8));
    }

    // Converteix bytes a hexadecimal
    static String aHex(byte[] bytes) {
        StringBuilder textOut = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            textOut.append(String.format("%02X", bytes[i] & 0xFF));
        }
        return textOut.toString();
    }

    // Genera una salt aleatoria de 16 bytes en hexadecimal
    static String generarSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return aHex(salt);
    }

    // Hash de (salt + contrasenya)
    static String hashAmbSalt(String password, String salt) {
        return sha256(salt + password);
    }

    // ---------- Part 1 ----------

    static void part1() {
        // 1. Un missatge i el seu hash
        System.out.print("Entra un missatge: ");
        String m1 = scanner.nextLine();
        System.out.println("Hash: " + sha256(m1));

        // 2. Dos missatges: coincideixen?
        System.out.print("Entra el segon missatge: ");
        String m2 = scanner.nextLine();
        String h1 = sha256(m1);
        String h2 = sha256(m2);
        System.out.println("Hash 1: " + h1);
        System.out.println("Hash 2: " + h2);
        System.out.println(h1.equals(h2) ? "Els hashes COINCIDEIXEN" : "Els hashes son DIFERENTS");

        // 3. Modificar el contingut i tornar a calcular
        System.out.print("Modifica el missatge original: ");
        String modificat = scanner.nextLine();
        System.out.println("Hash original : " + h1);
        System.out.println("Hash modificat: " + sha256(modificat));

        // 4. Calcular diverses vegades el mateix missatge
        System.out.println("Calculem 3 vegades el hash del primer missatge:");
        for (int i = 1; i <= 3; i++) {
            System.out.println(i + ": " + sha256(m1));
        }
        System.out.println("El resultat es manté sempre igual.");
    }

    // ---------- Part 2 ----------

    static void part2() {
        try {
            // Emissor: desa el missatge i el hash original
            System.out.print("Emissor, escriu el missatge: ");
            String missatge = scanner.nextLine();
            Files.write(Paths.get("missatge.txt"), missatge.getBytes(StandardCharsets.UTF_8));
            String hashOriginal = sha256(missatge);
            Files.write(Paths.get("missatge.hash"), hashOriginal.getBytes(StandardCharsets.UTF_8));
            System.out.println("Missatge desat a missatge.txt");
            System.out.println("Hash original desat a missatge.hash: " + hashOriginal);

            // Receptor
            System.out.println("\n--- Recepcio del missatge ---");
            System.out.println("(Pots crear a ma un fitxer missatgeCanviat.txt amb una versio modificada)");
            System.out.print("Nom del fitxer rebut: ");
            String nomFitxer = scanner.nextLine();

            byte[] bytesRebuts = Files.readAllBytes(Paths.get(nomFitxer));
            String hashRebut = sha256(bytesRebuts);
            String hashGuardat = new String(Files.readAllBytes(Paths.get("missatge.hash")), StandardCharsets.UTF_8);

            System.out.println("Hash original: " + hashGuardat);
            System.out.println("Hash rebut   : " + hashRebut);
            if (hashRebut.equals(hashGuardat)) {
                System.out.println("Els hashes coincideixen: el missatge NO ha estat modificat.");
            } else {
                System.out.println("ATENCIO: s'ha detectat una modificacio del missatge!");
            }
        } catch (IOException e) {
            System.out.println("Error amb els fitxers: " + e.getMessage());
        }
    }

    // ---------- Part 3 ----------

    // Registra un usuari; retorna false si ja existeix
    static boolean registrarUsuari(String nom, String password) {
        if (usuaris.containsKey(nom)) {
            return false;
        }
        String salt = generarSalt();
        String hash = hashAmbSalt(password, salt);
        usuaris.put(nom, new String[]{salt, hash});
        return true;
    }

    static void registrar() {
        System.out.print("Nom d'usuari: ");
        String nom = scanner.nextLine();
        System.out.print("Contrasenya: ");
        String password = scanner.nextLine();

        if (registrarUsuari(nom, password)) {
            System.out.println("Usuari registrat correctament.");
        } else {
            System.out.println("Aquest usuari ja existeix.");
        }
        // La contrasenya no es guarda ni es mostra
    }

    // ---------- Part 4 ----------

    // Comprova les credencials amb la salt guardada
    static boolean login(String nom, String password) {
        String[] dades = usuaris.get(nom);
        if (dades == null) {
            return false;
        }
        String hashCalculat = hashAmbSalt(password, dades[0]);
        return hashCalculat.equals(dades[1]);
    }

    static void iniciarSessio() {
        System.out.print("Nom d'usuari: ");
        String nom = scanner.nextLine();
        System.out.print("Contrasenya: ");
        String password = scanner.nextLine();

        if (!usuaris.containsKey(nom)) {
            System.out.println("Error: l'usuari no esta registrat.");
        } else if (login(nom, password)) {
            System.out.println("Acces permes. Benvingut, " + nom + "!");
        } else {
            System.out.println("Error: contrasenya incorrecta.");
        }
    }

    // ---------- Part 5 ----------

    static void part5() {
        usuaris.clear();

        System.out.println("\n--- Prova 1: dos usuaris amb la mateixa contrasenya ---");
        registrarUsuari("anna", "1234");
        registrarUsuari("pere", "1234");
        System.out.println("anna -> salt: " + usuaris.get("anna")[0]);
        System.out.println("anna -> hash: " + usuaris.get("anna")[1]);
        System.out.println("pere -> salt: " + usuaris.get("pere")[0]);
        System.out.println("pere -> hash: " + usuaris.get("pere")[1]);
        System.out.println(usuaris.get("anna")[1].equals(usuaris.get("pere")[1])
                ? "Hashes iguals" : "Resultat: els hashes son DIFERENTS gracies a la salt.");

        System.out.println("\n--- Prova 2: contrasenya correcta ---");
        System.out.println(login("anna", "1234") ? "Acces permes" : "Acces denegat");

        System.out.println("\n--- Prova 3: contrasenya incorrecta ---");
        System.out.println(login("anna", "abcd") ? "Acces permes" : "Acces denegat");

        System.out.println("\n--- Prova 4: salt diferent de la guardada ---");
        String saltFalsa = generarSalt();
        String hashAmbSaltFalsa = hashAmbSalt("1234", saltFalsa);
        System.out.println("Hash guardat       : " + usuaris.get("anna")[1]);
        System.out.println("Hash amb altra salt: " + hashAmbSaltFalsa);
        System.out.println(hashAmbSaltFalsa.equals(usuaris.get("anna")[1])
                ? "Coincideixen" : "Resultat: NO coincideixen, tot i que la contrasenya es correcta.");

        System.out.println("\n--- Explicacio ---");
        System.out.println("Les salts han de ser diferents per a cada registre perque aixi dues");
        System.out.println("contrasenyes iguals donen hashes diferents. Aixo evita que un atacant");
        System.out.println("detecti contrasenyes repetides i impedeix usar taules precalculades");
        System.out.println("(rainbow tables) o atacar tots els usuaris alhora.");
        System.out.println("Cal conservar la salt de cada usuari perque sense ella no es pot");
        System.out.println("recalcular el hash en iniciar sessio i no es podria verificar la");
        System.out.println("contrasenya. La salt no es secreta, nomes cal que sigui unica.");
    }
}
