package Serveur_POP3.Commandes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ParcoursMail {

    public String user;
    public List<File> mesMails;
    public File folder = new File("src\\Serveur_POP3\\BDD\\Mails");
    public File userfile = new File("src\\Serveur_POP3\\BDD\\Users\\users.txt");

    public ParcoursMail(String user) {
        this.user = user;
        this.mesMails = new ArrayList<>();
    }

    public List<File> listAllFiles(File folder){
        File[] fileNames = folder.listFiles();
        for(File file : fileNames){
            if(file.isDirectory()){
                listAllFiles(file);
            }else{
                try {
                    read(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        System.out.println(this.mesMails);
        return this.mesMails;
    }

    public void read(File file) {
        try{
            BufferedReader br  = new BufferedReader(new FileReader(file));
            String strLine;
            while((strLine = br.readLine()) != null ){
                if (strLine.contains("To:")){
                    String toMail = strLine.split("<")[1].split(">")[0];
                    if(toMail.equals(this.user)) {
//                        System.out.println("TO : -->"+toMail);
//                        System.out.println(file.length());
//                        System.out.println(file.getName());
                        this.mesMails.add(file);
                    }
                }
            }
        }
        catch(Exception e ){
            System.out.println(e);
        }

    }

    public String findmailbyuser(String usercheck){
        try{
            BufferedReader br  = new BufferedReader(new FileReader(userfile));
            String strLine;
            while((strLine = br.readLine()) != null ){
                String[] s = strLine.split(" ");
                String user = s[0];
                String pass = s[1];

                if (user.equals(usercheck)){
                    return s[2].split("<")[1].split(">")[0];
                }
            }
        }
        catch(Exception e ){
            System.out.println(e);
        }
        return null;
    }



    // Lancement du serveur

    public static void main(String[] args) {
        ParcoursMail a = new ParcoursMail("guillaume.l@gmail.com");
//        a.listAllFiles(a.folder);
        System.out.println(a.findmailbyuser("Sofiane"));
    }
}