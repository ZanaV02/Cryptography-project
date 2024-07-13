package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Terminal
{
    public static String izvrsiKomandu(String komanda)
    {
        StringBuilder tekst = new StringBuilder();
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", komanda);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    tekst.append(line).append("\n");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tekst.toString();
    }
    public static void prikaziKomandu(String komanda) throws IOException
    {
        Process process = Runtime.getRuntime().exec(komanda);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
        {
            System.out.println(line);
        }
    }
}
