public class Main {

    public static void main(String[] args) {
        int opcio;
        do {
            System.out.println();
            System.out.println("===== PE02 Hash i Salt =====");
            System.out.println("1. Part 1: Calcul de resums");
            System.out.println("0. Sortir");
            System.out.print("Opcio: ");
            opcio = Integer.parseInt(Parts.scanner.nextLine());

            switch (opcio) {
                case 1: Parts.part1(); break;
            }
        } while (opcio != 0);

        Parts.scanner.close();
    }
}
