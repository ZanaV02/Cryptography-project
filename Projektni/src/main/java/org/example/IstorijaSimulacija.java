package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IstorijaSimulacija
{
    public static void serijalizuj(String korisnickoIme, String plainText, String algoritam, String kljuc, String sifrat)
    {
        try
        {
            String putanja = "./Korisnici/" + korisnickoIme + "/" + "IstorijaSimulacija.txt";
            if(!Files.exists(Paths.get(putanja)))
            {
                Files.createFile(Paths.get(putanja));
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(putanja, true));
            writer.write(plainText.toUpperCase() + " | " + algoritam.toUpperCase() + " | " + kljuc.toUpperCase() + " | " + sifrat.toUpperCase() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
