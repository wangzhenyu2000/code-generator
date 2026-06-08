package com.example.codegenerator.util;

import java.util.Set;

public class NamingUtil {

    private static final Set<String> JAVA_KEYWORDS = Set.of(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
            "class", "const", "continue", "default", "do", "double", "else", "enum",
            "extends", "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native", "new",
            "package", "private", "protected", "public", "return", "short", "static",
            "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    );

    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        boolean first = true;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '_') {
                upper = !first;
            } else {
                sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                upper = false;
                first = false;
            }
        }
        return sb.toString();
    }

    public static String toPascalCase(String input) {
        String camel = toCamelCase(input);
        if (camel.isEmpty()) return "";
        return Character.toUpperCase(camel.charAt(0)) + camel.substring(1);
    }

    public static String safeFieldName(String columnName) {
        String camel = toCamelCase(columnName);
        if (JAVA_KEYWORDS.contains(camel)) {
            return camel + "Field";
        }
        return camel;
    }

    public static String toPackagePath(String packageName) {
        return packageName.replace('.', '/');
    }

    public static String lowerFirst(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
