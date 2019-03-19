package Serveur_POP3.Commandes;


import Serveur_POP3.Connexion;

public class CommandesAPOP extends Commandes {
    private String login;
    private int nbrMsg;

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
        return true;
    }

//        private void setNumberOfMessages(){}

    private void setUserFile(String file) {
        server.setUserFile(file);
    }


}

