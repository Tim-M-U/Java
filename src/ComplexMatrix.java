public class ComplexMatrix {
    private final int height;
    private final int width;
    private final Complex[][] cells;

    public ComplexMatrix(int height, int width) {
        if (height <= 0 || width <= 0) {
            throw new IllegalArgumentException("размеры матрицы должны быть положительными");
        }
        this.height = height;
        this.width = width;
        this.cells = new Complex[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                cells[r][c] = Complex.ZERO;
            }
        }
    }

    public static ComplexMatrix identity(int n) {
        ComplexMatrix e = new ComplexMatrix(n, n);
        for (int k = 0; k < n; k++) {
            e.cells[k][k] = Complex.ONE;
        }
        return e;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean isSquare() {
        return height == width;
    }

    public Complex get(int r, int c) {
        return cells[r][c];
    }

    public void set(int r, int c, Complex value) {
        cells[r][c] = value;
    }

    private ComplexMatrix copy() {
        ComplexMatrix m = new ComplexMatrix(height, width);
        for (int r = 0; r < height; r++) {
            System.arraycopy(cells[r], 0, m.cells[r], 0, width);
        }
        return m;
    }

    private void requireSameSize(ComplexMatrix other, String operation) {
        if (height != other.height || width != other.width) {
            throw new IllegalArgumentException(String.format(
                    "%s возможно только для матриц одного размера (%dx%d и %dx%d)",
                    operation, height, width, other.height, other.width));
        }
    }

    private void requireSquare(String operation) {
        if (!isSquare()) {
            throw new IllegalArgumentException(String.format(
                    "%s определён только для квадратной матрицы, а эта %dx%d",
                    operation, height, width));
        }
    }

    public ComplexMatrix add(ComplexMatrix other) {
        requireSameSize(other, "Сложение");
        ComplexMatrix result = new ComplexMatrix(height, width);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                result.cells[r][c] = cells[r][c].plus(other.cells[r][c]);
            }
        }
        return result;
    }

    public ComplexMatrix subtract(ComplexMatrix other) {
        requireSameSize(other, "Вычитание");
        ComplexMatrix result = new ComplexMatrix(height, width);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                result.cells[r][c] = cells[r][c].minus(other.cells[r][c]);
            }
        }
        return result;
    }

    public ComplexMatrix multiply(ComplexMatrix other) {
        if (width != other.height) {
            throw new IllegalArgumentException(String.format(
                    "умножение невозможно: у A %d столбц(а/ов), а у B %d строк(и)",
                    width, other.height));
        }
        ComplexMatrix result = new ComplexMatrix(height, other.width);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < other.width; c++) {
                Complex acc = Complex.ZERO;
                for (int k = 0; k < width; k++) {
                    acc = acc.plus(cells[r][k].times(other.cells[k][c]));
                }
                result.cells[r][c] = acc;
            }
        }
        return result;
    }

    public ComplexMatrix transpose() {
        ComplexMatrix result = new ComplexMatrix(width, height);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                result.cells[c][r] = cells[r][c];
            }
        }
        return result;
    }

    private static void swapRows(Complex[][] data, int a, int b) {
        Complex[] tmp = data[a];
        data[a] = data[b];
        data[b] = tmp;
    }

    private static int findPivot(Complex[][] data, int col, int from) {
        int best = from;
        for (int r = from + 1; r < data.length; r++) {
            if (data[r][col].abs() > data[best][col].abs()) {
                best = r;
            }
        }
        return best;
    }

    public Complex determinant() {
        requireSquare("Определитель");
        Complex[][] a = copy().cells;
        int n = height;
        Complex det = Complex.ONE;

        for (int col = 0; col < n; col++) {
            int pivot = findPivot(a, col, col);
            if (a[pivot][col].isZero()) {
                return Complex.ZERO;
            }
            if (pivot != col) {
                swapRows(a, pivot, col);
                det = det.negate();
            }
            det = det.times(a[col][col]);
            for (int r = col + 1; r < n; r++) {
                Complex factor = a[r][col].dividedBy(a[col][col]);
                for (int c = col; c < n; c++) {
                    a[r][c] = a[r][c].minus(factor.times(a[col][c]));
                }
            }
        }
        return det;
    }

    public ComplexMatrix inverse() {
        requireSquare("Обратная матрица");
        int n = height;
        Complex[][] a = copy().cells;
        Complex[][] inv = identity(n).cells;

        for (int col = 0; col < n; col++) {
            int pivot = findPivot(a, col, col);
            if (a[pivot][col].isZero()) {
                throw new ArithmeticException("матрица вырождена (det = 0), обратной не существует");
            }
            swapRows(a, pivot, col);
            swapRows(inv, pivot, col);


            Complex lead = a[col][col];
            for (int c = 0; c < n; c++) {
                a[col][c] = a[col][c].dividedBy(lead);
                inv[col][c] = inv[col][c].dividedBy(lead);
            }

            for (int r = 0; r < n; r++) {
                if (r == col || a[r][col].isZero()) {
                    continue;
                }
                Complex factor = a[r][col];
                for (int c = 0; c < n; c++) {
                    a[r][c] = a[r][c].minus(factor.times(a[col][c]));
                    inv[r][c] = inv[r][c].minus(factor.times(inv[col][c]));
                }
            }
        }

        ComplexMatrix result = new ComplexMatrix(n, n);
        for (int r = 0; r < n; r++) {
            result.cells[r] = inv[r];
        }
        return result;
    }

    public ComplexMatrix divide(ComplexMatrix other) {
        if (!other.isSquare()) {
            throw new IllegalArgumentException("делитель B должен быть квадратной матрицей");
        }
        return multiply(other.inverse());
    }

    @Override
    public String toString() {
        String[][] text = new String[height][width];
        int[] colWidth = new int[width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                text[r][c] = cells[r][c].toString();
                colWidth[c] = Math.max(colWidth[c], text[r][c].length());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < height; r++) {
            sb.append("| ");
            for (int c = 0; c < width; c++) {
                sb.append(String.format("%-" + colWidth[c] + "s", text[r][c]));
                sb.append(c + 1 < width ? "   " : " |");
            }
            if (r + 1 < height) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
