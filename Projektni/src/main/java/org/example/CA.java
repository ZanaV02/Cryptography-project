package org.example;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CA {

    public static void kreirajSertifikat(String korisnickoIme, String lozinka, String grad, String ime) {
        try {
            String korisnikFolder = "./Korisnici/" + korisnickoIme;
            //generisanje kljuca
            Terminal.izvrsiKomandu("openssl genrsa -aes256 -passout pass:" + lozinka + " -out " + korisnikFolder + "/" + korisnickoIme + ".key 2048");
            Terminal.izvrsiKomandu("openssl rsa -in " + korisnikFolder + "/" + korisnickoIme + ".key -passin pass:" + lozinka +
                    " -pubout -out " + korisnikFolder + "/" + korisnickoIme + "Public.key");

            //generisanje zahtjeva za sertifikat
            String subj = "/C=BA/ST=RS/L=" + grad + "/O=Elektrotehnicki fakultet/OU=ETF/CN=" + ime + "/emailAddress=" + ime + "@mail.com";
            Terminal.izvrsiKomandu("openssl req -new -key " + korisnikFolder + "/" + korisnickoIme + ".key -passin pass:" + lozinka + " -out ./req/" + korisnickoIme + ".csr -subj \"" + subj + "\"");
            //potpisivanje od strane CA tijela
            Terminal.izvrsiKomandu("openssl ca -config ./openssl.cnf -in ./req/" + korisnickoIme + ".csr " +
                    "-out " + korisnikFolder + "/" + korisnickoIme + ".crt -keyfile ./root.key -batch -passin pass:sigurnost");


            //ispis putanje do sertifikata i kljuceva
            Path korisnikFolderPath = Paths.get(korisnikFolder).toAbsolutePath().normalize();
            System.out.println("VASA REGISTRACIJA JE USPJESNA!");
            System.out.println("Sertifikat se nalazi na sljedecoj lokaciji: \n--->  " + korisnikFolderPath + "/" + korisnickoIme + ".crt");
            System.out.println("Kljuc se nalazi na sljedecoj lokaciji: \n--->  " + korisnikFolderPath + "/" + korisnickoIme + ".key");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void digitalniPotpis(String korisnickoIme, String lozinka) {
        System.out.println(Terminal.izvrsiKomandu("openssl dgst -sha512 -sign ./Korisnici/" + korisnickoIme + "/" + korisnickoIme + ".key -passin pass:" + lozinka +
                " -out ./Korisnici/" + korisnickoIme + "/" + korisnickoIme + "Potpis.sign " + "./Korisnici/" + korisnickoIme + "/" + "IstorijaSimulacija.txt"));
    }

    public static boolean verifikujPotpis(String korisnickoIme, String lozinka) {
        //provjeriti da li postoji fajl sa potpisom
        if (!Files.exists(Paths.get("./Korisnici/" + korisnickoIme + "/" + korisnickoIme + "Potpis.sign"))) {
            return true;
        }
        String tekst = Terminal.izvrsiKomandu("openssl dgst -sha512 -verify ./Korisnici/" + korisnickoIme + "/" + korisnickoIme + "Public.key " +
                "-signature ./Korisnici/" + korisnickoIme + "/" + korisnickoIme + "Potpis.sign " +
                "./Korisnici/" + korisnickoIme + "/" + "IstorijaSimulacija.txt");
        return tekst.contains("OK");
    }

    public static boolean validirajSertifikat(String nazivSertifikata) {
        try {

            String korisnickoIme = nazivSertifikata.split("\\.")[0];
            Path sertifikatPutanja = Paths.get("./Korisnici/" + korisnickoIme + "/" + nazivSertifikata);
            Path putanjaRoot = Paths.get("root.pem");

            if (Files.exists(sertifikatPutanja)) {
                String[] opensslCommand = {"openssl", "verify", "-CAfile", putanjaRoot.toString(), sertifikatPutanja.toString()};
                ProcessBuilder processBuilder = new ProcessBuilder(opensslCommand);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                // Read the output
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.contains("OK"))
                        return true;
                }
                return false;
            } else {
                System.out.println("Sertifikat nije pronadjen!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean istekaoSertifikat(String nazivSertifikata) {
        try {
            String korisnickoIme = nazivSertifikata.split("\\.")[0];
            Path sertifikatPutanja = Paths.get("./Korisnici/" + korisnickoIme + "/" + nazivSertifikata);
            if (Files.exists(sertifikatPutanja)) {
                String command = "openssl x509 -enddate -noout -in " + sertifikatPutanja.toString();
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();
                reader.close();

                if (line == null || !line.startsWith("notAfter=")) {
                    throw new IllegalArgumentException("Neispravan sertifikat");
                }

                String notAfter = line.substring("notAfter=".length()).trim();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d HH:mm:ss yyyy z");
                LocalDate expiryDate = LocalDate.parse(notAfter, formatter);
                LocalDate currentDate = LocalDate.now();
                long daysRemaining = ChronoUnit.DAYS.between(currentDate, expiryDate);
                return daysRemaining < 0; // Ako je manje od nule, sertifikat je istekao
            } else {
                System.out.println("Sertifikat nije pronađen na datoj putanji: " + sertifikatPutanja.toString());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean crlCheck(String naziSertifikata)
    {
        try
        {
            String koisnickoIme = naziSertifikata.split("\\.")[0];
            Path sertifikatPutanja = Paths.get("./Korisnici/" + koisnickoIme + "/" + naziSertifikata);
            Path putanjaRoot = Paths.get("root.pem");
            Path putanjaCRL = Paths.get("crl.pem");
            if(Files.exists(sertifikatPutanja))
            {
                String[] opensslCommand = {
                        "openssl", "verify",
                        "-CAfile", putanjaRoot.toString(),
                        "-crl_check","-CRLfile",
                        sertifikatPutanja.toString()
                };
                ProcessBuilder processBuilder = new ProcessBuilder(opensslCommand);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                // Read the output
                String line;
                while ((line = reader.readLine()) != null)
                {
                    if (line.contains("error") || line.contains("failed"))
                    {
                        return false;
                    }
                    if (line.contains("OK"))
                    {
                        return true;
                    }
                }
                return false;
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
