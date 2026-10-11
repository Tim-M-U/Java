import java.util.Scanner;

public class MatrixCalculator {
    private final Scanner in = new Scanner(System.in);
    private ComplexMatrix a;
    private ComplexMatrix b;

    public static void main(String[] args) {
        try {
            new MatrixCalculator().run();
        } catch (IllegalStateException e) {
            System.out.println();
            System.out.println("Завершение: " + e.getMessage());
        }
    }

    private void run() {
        System.out.println("=== Калькулятор матриц комплексных чисел ===");
        System.out.println("Числа вводятся в виде: 3, -2.5, i, -4i, 1+2i, 0.5-1.5i");
        a = readMatrix("A");

        while (true) {
            printMenu();
            String choice = prompt("Ваш выбор: ");
            if (choice == null || choice.equals("0")) {
                System.out.println("До свидания!");
                return;
            }
            try {
                handle(choice);
            } catch (IllegalArgumentException | ArithmeticException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("----------------- Меню -----------------");
        System.out.println(" 1. Ввести матрицу A");
        System.out.println(" 2. Ввести матрицу B");
        System.out.println(" 3. Показать матрицы");
        System.out.println(" 4. A + B");
        System.out.println(" 5. A - B");
        System.out.println(" 6. A * B");
        System.out.println(" 7. A / B   (= A * B^-1)");
        System.out.println(" 8. Транспонировать");
        System.out.println(" 9. Определитель");
        System.out.println("10. Обратная матрица");
        System.out.println(" 0. Выход");
        System.out.println("----------------------------------------");
    }

    private void handle(String choice) {
        switch (choice) {
            case "1" -> a = readMatrix("A");
            case "2" -> b = readMatrix("B");
            case "3" -> {
                show("A", a);
                show("B", b);
            }
            case "4" -> show("A + B", a.add(requireB()));
            case "5" -> show("A - B", a.subtract(requireB()));
            case "6" -> show("A * B", a.multiply(requireB()));
            case "7" -> show("A / B", a.divide(requireB()));
            case "8" -> {
                String name = chooseMatrix();
                show(name + "^T", pick(name).transpose());
            }
            case "9" -> {
                String name = chooseMatrix();
                System.out.println("det(" + name + ") = " + pick(name).determinant());
            }
            case "10" -> {
                String name = chooseMatrix();
                show(name + "^-1", pick(name).inverse());
            }
            default -> System.out.println("Неизвестная команда, введите число от 0 до 10.");
        }
    }

    private ComplexMatrix requireB() {
        if (b == null) {
            System.out.println("Матрица B ещё не задана — введите её.");
            b = readMatrix("B");
        }
        return b;
    }

    private String chooseMatrix() {
        if (b == null) {
            return "A";
        }
        while (true) {
            String s = prompt("Для какой матрицы (A/B)? ");
            if (s == null) {
                return "A";
            }
            s = s.toUpperCase();
            if (s.equals("A") || s.equals("B") || s.equals("А")) {
                return s.equals("B") ? "B" : "A";
            }
            System.out.println("Введите A или B.");
        }
    }

    private ComplexMatrix pick(String name) {
        return name.equals("B") ? b : a;
    }

    private static void show(String title, ComplexMatrix m) {
        if (m == null) {
            System.out.println(title + ": не задана");
            return;
        }
        System.out.println(title + " (" + m.getHeight() + "x" + m.getWidth() + "):");
        System.out.println(m);
    }

    private ComplexMatrix readMatrix(String name) {
        System.out.println();
        System.out.println("--- Ввод матрицы " + name + " ---");
        int rows = readPositiveInt("Количество строк: ");
        int cols = readPositiveInt("Количество столбцов: ");
        ComplexMatrix m = new ComplexMatrix(rows, cols);

        System.out.println("Введите " + rows + " строк(и) по " + cols + " элемент(а/ов) через пробел, \", \" или \";\":");
        for (int r = 0; r < rows; r++) {
            while (true) {
                String line = prompt("строка " + (r + 1) + ": ");
                if (line == null) {
                    throw new IllegalStateException("ввод неожиданно закончился");
                }
                String[] parts = line.replaceAll("[,;\\s]+$", "").split("\\s*;\\s*|,\\s+|\\s+");
                if (parts.length != cols) {
                    System.out.println("Нужно ровно " + cols + " элемент(а/ов), а введено " + parts.length + ". Повторите.");
                    continue;
                }
                try {
                    for (int c = 0; c < cols; c++) {
                        m.set(r, c, Complex.parse(parts[c]));
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: " + e.getMessage() + ". Повторите строку.");
                }
            }
        }
        show("Матрица " + name, m);
        return m;
    }

    private int readPositiveInt(String message) {
        while (true) {
            String s = prompt(message);
            if (s == null) {
                throw new IllegalStateException("ввод неожиданно закончился");
            }
            try {
                int value = Integer.parseInt(s);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Нужно целое положительное число.");
        }
    }

    private String prompt(String message) {
        System.out.print(message);
        if (!in.hasNextLine()) {
            return null;
        }
        return in.nextLine().trim();
    }
}
