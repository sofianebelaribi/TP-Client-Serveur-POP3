package Serveur_POP3.Commandes;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ParcoursMail {

    public String user;
    public String mail;
    public List<File> mesMails;
    public File folder = new File("src\\Serveur_POP3\\BDD\\Mails");
    public File userfile = new File("src\\Serveur_POP3\\BDD\\Users\\users.txt");

    public ParcoursMail(String user) {
        this.user = user;
        this.mail = findmailbyuser(user);
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
        return this.mesMails;
    }


    public void read(File file) {
        try{
            BufferedReader br  = new BufferedReader(new FileReader(file));
            String strLine;
            while((strLine = br.readLine()) != null ){
                if (strLine.contains("To:")){
                    String toMail = strLine.split("<")[1].split(">")[0];
                    if(toMail.equals(this.mail)) {
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


    public int countoctets(){
        int nbrInt = 0;
        try{
            for (File fichier : this.mesMails) {
                nbrInt += fichier.length();
            }
        }
        catch(Exception e ){
            System.out.println(e);
        }
        return nbrInt;
    }

    public File retrievemessageid(int id){
        return listAllFiles(folder).get(id);
    }

    public String readMailbyid(int id) throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(retrievemessageid(id)));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (!line.equals(".")) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                sb.append(line);
                return sb.toString();
            } finally {
                br.close();
            }
    }

    // Lancement du serveur

    public static void main(String[] args) throws IOException {
        ParcoursMail a = new ParcoursMail("so");
//        a.listAllFiles(a.folder);
        System.out.println(a.findmailbyuser("so"));
        System.out.println(a.retrievemessageid(0));
        System.out.println(a.readMailbyid(1));
    }


}