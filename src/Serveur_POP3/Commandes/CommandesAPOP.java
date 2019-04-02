package Serveur_POP3.Commandes;


import Serveur_POP3.Connexion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandesAPOP extends Commandes {

    private String login;
    private int nbrMsg;
    private final String userfile = "src\\Serveur_POP3\\BDD\\Users\\users.txt";

    public CommandesAPOP(Connexion server, String command) {
        super(server, command);
    }

    @Override
    String makeAnswer(String content) {
        if (server.isStateAuthentified()) {
            //extract login and password
            String[] s = extractContent(content);
            //check if user is in db
            if (checkAuthentification(s)) {
                server.setState(server.getSTATE_TRANSACTION());
                setUser(s[0]);
                setNumberOfMessages();
                return "+OK " + login + "'s maildrop has " + nbrMsg + " messages";
            }
            return "-ERR " + server.getNUMBER_OF_CHANCES() + " chances left";
        }
        else
            return "-ERR"  + server.getNUMBER_OF_CHANCES() + " chances left";
    }

    @Override
    String[] extractContent(String content) {
        String login = content.split("\\s+")[1];
        this.login = login;
        String password;
        try {
            password = content.split("\\s+")[2];
        }
        catch (Exception e){
            password="";
        }
        return new String[]{login,password};
    }


    private boolean checkAuthentification(String[] s){
        //test in bd user & pass
        return read(s);
    }

    private void setNumberOfMessages(){
        ParcoursMail unParcours = new ParcoursMail(server.getUser());
        unParcours.listAllFiles(unParcours.folder);

        this.nbrMsg = unParcours.mesMails.size();
        System.out.println(this.nbrMsg + " messages");
    }

    public boolean read(String[] s) {
        try{
            BufferedReader br  = new BufferedReader(new FileReader(userfile));
            String strLine;
            while((strLine = br.readLine()) != null ){
                String user = strLine.split(" ")[0];
                String pass = server.getTimbre()+strLine.split(" ")[1];
                pass = criptageMD5(pass);
                if (user.equals(s[0]) && pass.equals(s[1])){
                    br.close();
                    return true;
                }
            }
            br.close();
        }
        catch(Exception e ){
            System.out.println(e);
        }
        return false;
    }

    private void setUser(String user) {
        server.setUser(user);
    }

    public static String criptageMD5(String pass){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] passBytes = pass.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<digested.length;i++){
                sb.append(Integer.toHexString(0xff & digested[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CommandesAPOP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}

