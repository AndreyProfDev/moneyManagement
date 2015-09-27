package com.moneyManagement.gui.utils;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CDisplay
{
    public static void warning(String message)
    {
        JOptionPane.showOptionDialog(null, message, "Warning",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"OK"}, "OK");
    }

    public static void error(String message)
    {
        JOptionPane.showOptionDialog(null, message, "Warning",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
    }

    public static void error(String message, Throwable ex)
    {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter( writer );
        ex.printStackTrace( printWriter );
        printWriter.flush();

        String stackTrace = writer.toString();
        String[] stackTraceLines = stackTrace.split("\n");

        StringBuilder stackTraceBuilder = new StringBuilder();

        int realLinesNumber = 0;
        for (int lineNumber = 0; lineNumber < stackTraceLines.length; lineNumber++)
        {
            if (realLinesNumber > 0 && stackTraceLines[lineNumber].startsWith("\tat") &&
                    !stackTraceLines[lineNumber].contains("at com.moneyManagement"))
            {
                continue;
            }

            realLinesNumber++;
            stackTraceBuilder.append(stackTraceLines[lineNumber]).append("\n");

            if (realLinesNumber == 15)
            {
                break;
            }

        }

        JOptionPane.showOptionDialog(null, message + ":\n" + stackTraceBuilder.toString(), "Warning",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
    }
//
//    public static void error(CDatabaseException ex)
//    {
//        error(ex.getMessage());
//        ex.printStackTrace();
//    }
}
