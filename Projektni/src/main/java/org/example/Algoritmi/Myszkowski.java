package org.example.Algoritmi;

import java.util.*;
import java.util.stream.Collectors;

public class Myszkowski
{

        public static int pronadjiPozicijuUAlfabetu(char c)
        {
            String alfabet = "abcdefghijklmnopqrstuvwxyz";
            int pozicija = alfabet.indexOf(c);
            return pozicija+1;
        }
        public static String Myszkowski(String unos, String kljuc, boolean nullKarakter)
        {
            String plainText = unos.replaceAll("\\s", "").toUpperCase();
            int brojKolona = kljuc.length();
            int brojRedova = (int)Math.ceil((double)plainText.length() / brojKolona);
            char[][] matrica = new char[brojRedova][brojKolona];
            int k = 0;
            for(int i=0;i<brojRedova;i++)
            {
                for(int j=0;j<brojKolona;j++)
                {
                    if(k < plainText.length())
                    {
                        matrica[i][j] = plainText.charAt(k);
                        k++;
                    }
                    else
                    {
                        if(nullKarakter)
                        {
                            matrica[i][j] = 'X';
                        }
                        else
                            break;
                    }
                }
            }

            Map<Integer, List<Character>> koloneMapa = new HashMap<>();
            for(int i= 0;i<brojKolona;i++)
            {
                List<Character> kolona = new ArrayList<>();
                for(int j=0;j<brojRedova;j++)
                {
                    if(matrica[j][i] != 0)
                    {
                        kolona.add(matrica[j][i]);
                    }
                }
                koloneMapa.put(i, kolona);
            }
            List<Map.Entry<Character, Integer>> sortiraniKljucevi = new ArrayList<>();
            for(int i=0;i<kljuc.length();i++)
            {
                sortiraniKljucevi.add(new AbstractMap.SimpleEntry<>(kljuc.charAt(i), i));
            }
            sortiraniKljucevi.sort(Comparator.comparing(Map.Entry::getKey));

            StringBuilder sifrat = new StringBuilder();
            for (Map.Entry<Character, Integer> entry : sortiraniKljucevi)
            {
                int kolona = entry.getValue();
                List<Character> kolonaKaraktera = koloneMapa.get(kolona);
                for(Character c : kolonaKaraktera)
                {
                    sifrat.append(c);
                }
            }
            return sifrat.toString();
        }
}
