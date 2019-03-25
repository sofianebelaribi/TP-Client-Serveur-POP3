package Serveur_POP3.Commandes;


import Serveur_POP3.Connexion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CommandesAPOP extends Commandes {
    private String login;
    private int nbrMsg;
    final String userfile = "src\\Serveur_POP3\\BDD\\Mails\\users.txt";

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

//        private void setNumberOfMessages(){
// }

    private void setUserFile(String file) {

        server.setUserFile(file);
    }

    public boolean read(String[] s) {
        try{
            BufferedReader br  = new BufferedReader(new FileReader(userfile));
            String strLine;
            while((strLine = br.readLine()) != null ){
                String user = strLine.split(" ")[0];
                String pass = strLine.split(" ")[1];

                if (user.equals(s[0]) && pass.equals(s[1])){
                    return true;
                }
            }
        }
        catch(Exception e ){
            System.out.println(e);
        }
        return false;
    }
}

