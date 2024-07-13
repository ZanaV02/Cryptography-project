package org.example;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.example.Algoritmi.Playfair;
import org.example.Algoritmi.RailFence;

import static org.example.Algoritmi.Playfair.*;
import static org.example.Algoritmi.Myszkowski.*;
import static org.example.Algoritmi.RailFence.*;
import static org.example.Terminal.izvrsiKomandu;
import static org.example.Terminal.prikaziKomandu;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;



public class Main
{
    public static Scanner scanner = new Scanner(System.in);
    public static String imeZaFunkciju;
    public static String lozinkaZaFunkciju;

    public static void main(String[] args) throws Exception
    {
        try
        {
            //krerianje CA sertifikata
            System.out.println(izvrsiKomandu("openssl genrsa -out ./root.key"));
            System.out.println(izvrsiKomandu("openssl req -new -x509 -" +
                    "config ./openssl.cnf -key ./root.key -out ./root.pem -days 365 -subj \"" +
                    "/C=BA/ST=RS/L=Banja Luka/O=Elektrotehnicki fakultet/OU=ETF/CN=Zana/emailAddress=zana@mail.com" + "\""));

            //System.out.println("Podaci o CA sertifikatu:");
            //prikaziKomandu("openssl x509 -in ./root.pem -noout -text");

            //meni za korisnika
            Korisnik korisnik = new Korisnik();
            int izbor;
            do
            {
                System.out.println("\nIzaberite opciju:");
                System.out.print("Registracija korisnika [1]  Prijavljivanje korisnika [2]  Izlaz [3]\n");
                izbor = scanner.nextInt();
                scanner.nextLine();//ciscenje buffera

                switch (izbor)
                {
                    //registracija korisnika
                    case 1:
                        korisnik.unosPodataka();
                        korisnik.registrujKorisnika();
                        CA.kreirajSertifikat(korisnik.getKorisnickoIme(), korisnik.getLozinka(), korisnik.getGrad(), korisnik.getIme());
                        break;
                    //prijavljivanje korisnika
                    case 2:
                        //sertif
                        System.out.println("Unesite sertifikat: ");
                        String sertifikatUnos = scanner.nextLine();
                        if(!CA.validirajSertifikat(sertifikatUnos))
                        {
                            System.out.println("Sertifikat nije validan. Program se prekida.");
                            break;
                        }
                        if(CA.crlCheck(sertifikatUnos))
                        {
                            System.out.println("Sertifikat je povucen. Program se prekida.");
                            break;
                        }
                        if(CA.istekaoSertifikat(sertifikatUnos))
                        {
                            System.out.println("Sertifikat je istekao. Program se prekida.");
                            break;
                        }
                        System.out.println("Sertifikat je validan.");
                        boolean prijavljen = false;
                        while(!prijavljen)
                        {
                            System.out.println("Unesite korisnicko ime: ");
                            String korisnickoIme = scanner.nextLine();
                            imeZaFunkciju = korisnickoIme;
                            System.out.println("Unesite lozinku: ");
                            String lozinka = scanner.nextLine();
                            lozinkaZaFunkciju = lozinka;
                            if(korisnik.prijaviKorisnika(korisnickoIme, lozinka))
                                prijavljen = true;
                            else
                                System.out.println("Pogresno korisnicko ime ili lozinka. Pokusajte ponovo.");
                        }
                        if(prijavljen)
                        {
                            if(!CA.verifikujPotpis(imeZaFunkciju, lozinkaZaFunkciju))
                            {
                                System.out.println("Vasa datoteka je komprovitovana! Program se prekida.");
                                break;
                            }
                            int izborOpcije;
                            do
                            {
                                System.out.println("\nIzaberite opciju:");
                                System.out.print("Simulacija kriptovanja [1] Prikaz istorije simulacija [2]  Povratak na prethodni meni [3]\n");
                                izborOpcije = scanner.nextInt();
                                scanner.nextLine();//ciscenje buffera
                                switch (izborOpcije)
                                {
                                    case 1:
                                        System.out.println("Unesite tekst za kriptovanje: ");
                                        String plainText = scanner.nextLine();
                                        System.out.println("Izaberite algoritam za kriptovanje:");
                                        System.out.print("Rail Fence [1]  Playfair [2]  Myszkowski [3] Izlaz [4]\n");
                                        int izborAlgoritma = scanner.nextInt();
                                        scanner.nextLine();//ciscenje buffera
                                        switch (izborAlgoritma)
                                        {
                                            case 1:
                                                System.out.println("Unesite kljuc: ");
                                                int kljuc = scanner.nextInt();
                                                scanner.nextLine();//ciscenje buffera
                                                if(kljuc < 2)
                                                {
                                                    System.out.println("Kljuc mora biti veci od 1. Pokusajte ponovo.");
                                                    break;
                                                }
                                                String sifrat = railFence(plainText, kljuc);
                                                System.out.println("Sifrat: " + sifrat);
                                                IstorijaSimulacija.serijalizuj(imeZaFunkciju, plainText, "Rail Fence", String.valueOf(kljuc), sifrat);
                                                break;
                                            case 2:
                                                System.out.println("Unesite kljuc: ");
                                                String kljucPlayfair = scanner.nextLine();
                                                String sifratPlayfair = Playfair(plainText, kljucPlayfair);
                                                System.out.println("Sifrat: " + sifratPlayfair);
                                                IstorijaSimulacija.serijalizuj(imeZaFunkciju, plainText, "Playfair", kljucPlayfair, sifratPlayfair);
                                                break;
                                            case 3:
                                                System.out.println("Unesite kljuc: ");
                                                String kljucMyszkowski = scanner.nextLine();
                                                String sifratMyszkowski = Myszkowski(plainText, kljucMyszkowski, false);
                                                System.out.println("Sifrat: " + sifratMyszkowski);
                                                IstorijaSimulacija.serijalizuj(imeZaFunkciju, plainText, "Myszkowski", kljucMyszkowski, sifratMyszkowski);
                                                break;
                                            case 4:
                                                break;
                                            default:
                                                System.out.println("Nepoznata opcija. Pokusajte ponovo.");
                                        }
                                        CA.digitalniPotpis(imeZaFunkciju, lozinkaZaFunkciju);
                                        break;
                                    case 2:
                                        prikaziKomandu("cat ./Korisnici/" + imeZaFunkciju + "/IstorijaSimulacija.txt");
                                        break;
                                    case 3:
                                        break;
                                    default:
                                        System.out.println("Nepoznata opcija. Pokusajte ponovo.");
                                }
                            }while(izborOpcije != 3);
                        }
                        break;
                        //izlaz
                    case 3:
                        break;
                        //pogresna opcija
                    default:
                        System.out.println("Nepoznata opcija. Pokusajte ponovo.");
                }

            }while (izbor != 3);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

