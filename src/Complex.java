public final class Complex {
    public static final double EPS = 1e-10;

    public static final Complex ZERO = new Complex(0, 0);
    public static final Complex ONE = new Complex(1, 0);

    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public static Complex parse(String text) {
        String s = text.trim().replace(',', '.').replace('I', 'i').replace('j', 'i');
        if (s.isEmpty()) {
            throw new NumberFormatException("пустая строка");
        }
        try {
            if (!s.endsWith("i")) {
                return new Complex(Double.parseDouble(s), 0);
            }
            String body = s.substring(0, s.length() - 1);

            int split = -1;
            for (int k = body.length() - 1; k > 0; k--) {
                char ch = body.charAt(k);
                if ((ch == '+' || ch == '-') && Character.toLowerCase(body.charAt(k - 1)) != 'e') {
                    split = k;
                    break;
                }
            }
            String realPart = split == -1 ? "0" : body.substring(0, split);
            String imagPart = split == -1 ? body : body.substring(split);
            return new Complex(Double.parseDouble(realPart), parseImag(imagPart));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("не удалось распознать число \"" + text.trim() + "\"");
        }
    }

    private static double parseImag(String s) {
        return switch (s) {
            case "", "+" -> 1;
            case "-" -> -1;
            default -> Double.parseDouble(s);
        };
    }

    public double re() {
        return re;
    }

    public double im() {
        return im;
    }

    public double abs() {
        return Math.hypot(re, im);
    }

    public boolean isZero() {
        return abs() < EPS;
    }

    public Complex plus(Complex z) {
        return new Complex(re + z.re, im + z.im);
    }

    public Complex minus(Complex z) {
        return new Complex(re - z.re, im - z.im);
    }

    public Complex times(Complex z) {
        return new Complex(re * z.re - im * z.im, re * z.im + im * z.re);
    }

    public Complex dividedBy(Complex z) {
        double denom = z.re * z.re + z.im * z.im;
        if (denom < EPS * EPS) {
            throw new ArithmeticException("деление на ноль (комплексное число равно 0)");
        }
        return new Complex((re * z.re + im * z.im) / denom,
                           (im * z.re - re * z.im) / denom);
    }

    public Complex negate() {
        return new Complex(-re, -im);
    }

    private static String fmt(double x) {
        if (Math.abs(x) < EPS) {
            x = 0;
        }
        if (x == Math.rint(x) && Math.abs(x) < 1e15) {
            return String.valueOf((long) x);
        }
        String s = String.format(java.util.Locale.US, "%.4f", x);

        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    @Override
    public String toString() {
        String r = fmt(re);
        String i = fmt(im);
        if (i.equals("0")) {
            return r;
        }
        String imPart = switch (i) {
            case "1" -> "i";
            case "-1" -> "-i";
            default -> i + "i";
        };
        if (r.equals("0")) {
            return imPart;
        }
        return imPart.startsWith("-") ? r + imPart : r + "+" + imPart;
    }
}
