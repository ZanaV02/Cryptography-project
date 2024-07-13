package org.example.Algoritmi;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Playfair
{
    public static char[][] playfairKvadrat = new char[5][5];
    public static int[] pronadjiPoziciju(char c, char[][] playfairKvadrat)
    {
        int[] pozicija = new int[2];
        for(int i=0;i<5;i++)
        {
            for(int j=0;j<5;j++)
            {
                if(playfairKvadrat[i][j] == c)
                {
                    pozicija[0] = i;
                    pozicija[1] = j;
                    return pozicija;
                }
            }
        }
        return null;
    }
    public static String Playfair(String plainText, String kljuc)
    {
        //string za formiranje kvadrata
        List<Character> kljucBezDuplikata = kljuc.chars().mapToObj(c -> (char)c).distinct().collect(Collectors.toList());

        String alphabet = "abcdefghiklmnopqrstuvwxyz";
        List<Character> alfabetStream = alphabet.chars().mapToObj(c -> (char)c).collect(Collectors.toList());

        List<Character> alfabetBezKljuca = new ArrayList<>();
        for(Character c: alfabetStream)
        {
            if(!kljucBezDuplikata.contains(c))
            {
                alfabetBezKljuca.add(c);
            }
        }
        //spajanje kljuca i ostatka abecede
        List<Character> listaZaMatricu = Stream.concat(kljucBezDuplikata.stream(), alfabetBezKljuca.stream()).collect(Collectors.toList());

        //playfair kvadrat
        for(int i=0; i<5; i++)
        {
            for(int j=0; j<5; j++)
            {
                playfairKvadrat[i][j] = listaZaMatricu.get(i*5+j);
            }
        }

        //formatiranje plaintexta
        plainText = plainText.toLowerCase().replaceAll("[^a-z]", "").replace('j', 'i');
        for(int i=0; i< plainText.length(); i+=2)
        {
            StringBuilder digram = new StringBuilder();
            char a = plainText.charAt(i);
            char b = (i+1) < plainText.length() ? plainText.charAt(i+1) : 'x';
            if(a == b)
            {
                digram.append(a).append('x');
                i--;
            }
            else
            {
                digram.append(a).append(b);
            }
        }
        if(plainText.length() % 2 != 0)
        {
            plainText += 'x';
        }

        //sifrovanje
        StringBuilder sifrat = new StringBuilder();
        for(int i=0;i<plainText.length();i+=2)
        {
            char a = plainText.charAt(i);
            char b = plainText.charAt(i+1);
            int[] pozicijaA = pronadjiPoziciju(a, playfairKvadrat);
            int[] pozicijaB = pronadjiPoziciju(b, playfairKvadrat);
            if(pozicijaA[0] == pozicijaB[0])//u istom redu
            {
                sifrat.append(playfairKvadrat[pozicijaA[0]][(pozicijaA[1]+1)%5]);
                sifrat.append(playfairKvadrat[pozicijaB[0]][(pozicijaB[1]+1)%5]);
            }
            else if(pozicijaA[1] == pozicijaB[1])//u istoj koloni
            {
                sifrat.append(playfairKvadrat[(pozicijaA[0]+1)%5][pozicijaA[1]]);
                sifrat.append(playfairKvadrat[(pozicijaB[0]+1)%5][pozicijaB[1]]);
            }
            else//razlici redovi i kolone
            {
                sifrat.append(playfairKvadrat[pozicijaA[0]][pozicijaB[1]]);
                sifrat.append(playfairKvadrat[pozicijaB[0]][pozicijaA[1]]);
            }
        }
        return sifrat.toString().toUpperCase();
    }
}
