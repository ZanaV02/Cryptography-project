package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Scanner;

public class Korisnik
{
    private String korisnickoIme;
    private String lozinka;
    private String grad;
    private String ime;
    public static Path putanjaDoBaze = Path.of("./BazaRegistrovanihKorisnika.txt");


    public Korisnik() {
    }
    public String getKorisnickoIme()
    {
        return korisnickoIme;
    }
    public String getLozinka()
    {
        return lozinka;
    }
    public String getGrad()
    {
        return grad;
    }
    public String getIme()
    {
        return ime;
    }

    public void unosPodataka()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Unesite korisnicko ime: ");
        korisnickoIme = scanner.nextLine();
        System.out.println("Unesite lozinku: ");
        lozinka = scanner.nextLine();

        System.out.println("Unesite podatke za sertifikat:");
        System.out.print("Unesite grad: ");
        grad = scanner.nextLine();
        System.out.print("Unesite ime: ");
        ime = scanner.nextLine();
    }

    public void registrujKorisnika() throws IOException
    {
        //kreira se jedinstveni folder za korisnika
        Path korisnikFolder = Paths.get("./Korisnici/" + korisnickoIme);
        if(!Files.exists(korisnikFolder))
        {
            Files.createDirectories(korisnikFolder);
        }
        //upis korisnika u bazu
        String korisnik = korisnickoIme + "#" + lozinka;
        if(!Files.exists(putanjaDoBaze.getParent()))
        {
            Files.createDirectories(putanjaDoBaze.getParent());
        }
        if(!Files.exists(putanjaDoBaze))
        {
            Files.createFile(putanjaDoBaze);
        }
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(putanjaDoBaze.toFile(),true)))
        {
            writer.write(korisnik);
            writer.newLine();
        }
    }
    public boolean prijaviKorisnika(String korisnickoIme, String lozinka)
    {
        try(BufferedReader br = Files.newBufferedReader(Paths.get("./BazaRegistrovanihKorisnika.txt")))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] podaci = line.split("#");
                if(podaci[0].equals(korisnickoIme) && podaci[1].equals(lozinka))
                {
                    System.out.println("USPJESNA PRIJAVA!");
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }


}
