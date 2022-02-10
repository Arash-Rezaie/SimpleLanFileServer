package com.arash.launcher.tools;

import java.util.List;

public class TablePrinter {
    private static int[] getMaxColLengths(List<String[]> rows) {
        int[] lengths = new int[rows.get(0).length];
        rows.forEach(row -> {
            for (int i = row.length - 1; i >= 0; i--)
                lengths[i] = Math.max(lengths[i], row[i].length());
        });
        return lengths;
    }

    private static void print(String txt, int neededLength) {
        StringBuilder sb = new StringBuilder(neededLength + 1);
        sb.append(txt);
        for (int i = txt.length(); i < neededLength; i++)
            sb.append(' ');
        sb.append('\t');
        System.out.print(sb);
    }

    public static void print(List<String[]> rows) {
        int[] lengths = getMaxColLengths(rows);
        int l = lengths.length;
        rows.forEach(row -> {
            for (int i = 0; i < l; i++)
                print(row[i], lengths[i]);
            System.out.println();
        });
    }
}
