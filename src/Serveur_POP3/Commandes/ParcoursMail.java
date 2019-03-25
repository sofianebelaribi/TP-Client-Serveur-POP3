package Serveur_POP3.Commandes;

import java.io.BufferedReader;
import java.io.File;
//import java.io.FileInputStream;
import java.io.FileReader;

public class ParcoursMail {

    public ParcoursMail() {
    }

    public static void read(String nom) {
    File fichier = new File(nom);
        try{
            BufferedReader br  = new BufferedReader(new FileReader(fichier));
            String strLine;
            // Read lines from the file, returns null when end of stream
            // is reached
            while((strLine = br.readLine()) != null ){
                System.out.println(strLine);
            }
        }
        catch(Exception e ){
            System.out.println(e);

        }

    }



    // Lancement du serveur

    public static void main(String[] args) {
        read("src\\Serveur_POP3\\BDD\\Mails\\mail1.txt");
    }
}