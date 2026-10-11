package tcp;

import java.math.BigDecimal;
import java.math.MathContext;

public final class CalculatorProtocol {
    private CalculatorProtocol() {
    }

    public static String process(String request) {
        String trimmed = request.trim();
        String[] parts = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
        if (parts.length != 4 || !parts[0].equalsIgnoreCase("CALC")) {
            return "ERR INVALID_FORMAT";
        }

        BigDecimal first;
        BigDecimal second;
        try {
            first = new BigDecimal(parts[2]);
            second = new BigDecimal(parts[3]);
        } catch (NumberFormatException e) {
            return "ERR INVALID_NUMBER";
        }

        BigDecimal result;
        try {
            result = switch (parts[1]) {
                case "+" -> first.add(second);
                case "-" -> first.subtract(second);
                case "*" -> first.multiply(second);
                case "/" -> {
                    if (second.signum() == 0) {
                        yield null;
                    }
                    yield first.divide(second, MathContext.DECIMAL128);
                }
                default -> null;
            };
        } catch (ArithmeticException e) {
            return "ERR INVALID_NUMBER";
        }

        if (parts[1].equals("/") && second.signum() == 0) {
            return "ERR DIVIDE_BY_ZERO";
        }
        if (result == null) {
            return "ERR UNSUPPORTED_OPERATOR";
        }
        return "OK " + result.stripTrailingZeros().toPlainString();
    }
}