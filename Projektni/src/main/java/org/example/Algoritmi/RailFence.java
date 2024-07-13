package org.example.Algoritmi;

public class RailFence
{
    public static String railFence(String plainText, int kljuc)
    {
        String bezPraznina = plainText.replaceAll("\\s", "").toUpperCase();
        int brojRedova = kljuc;
        int brojKolona = bezPraznina.length();
        char[][] matrica = new char[brojRedova][brojKolona];
        boolean down = false;
        int red = 0;
        for(int i=0;i<brojKolona;i++)
        {
            if(red == 0 || red == brojRedova-1)
            {
                down = !down;
            }
            matrica[red][i] = bezPraznina.charAt(i);
            if(down)
                red++;
            else
                red--;
        }
        String sifrat = "";
        for(int i=0;i<brojRedova;i++)
        {
            for(int j=0;j<brojKolona;j++)
            {
                if(matrica[i][j] != 0)
                    sifrat += matrica[i][j];
            }
        }
        return sifrat;
    }
}
