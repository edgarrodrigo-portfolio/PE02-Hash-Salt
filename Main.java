public class Main {

    public static void main(String[] args) {
        int opcio;
        do {
            System.out.println();
            System.out.println("===== PE02 Hash i Salt =====");
            System.out.println("1. Part 1: Calcul de resums");
            System.out.println("2. Part 2: Verificacio d'integritat de missatges");
            System.out.println("3. Part 3: Registre d'usuaris amb salt");
            System.out.println("0. Sortir");
            System.out.print("Opcio: ");
            opcio = Integer.parseInt(Parts.scanner.nextLine());

            switch (opcio) {
                case 1: Parts.part1(); break;
                case 2: Parts.part2(); break;
                case 3: Parts.registrar(); break;
            }
        } while (opcio != 0);

        Parts.scanner.close();
    }
}
