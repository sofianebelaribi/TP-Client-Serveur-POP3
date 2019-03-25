package Serveur_POP3.Commandes;

import Serveur_POP3.Connexion;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CommandesRETR extends Commandes {

    public CommandesRETR(Connexion server, String command) {
        super(server, command);
    }

    @Override
    String makeAnswer(String content) {
        if (server.isStateTransaction()) {
            //extract login and password
            int id = Integer.parseInt(extractContent(content)[0]); //Check try catch
            ParcoursMail unPar = new ParcoursMail(server.getUser());
            unPar.listAllFiles(unPar.folder);
            if(id > unPar.mesMails.size()){
                return "-ERR";
            }
            String mail = getMailbyid(id);
            return mail;
//            return messageFactory(id);
        }
        else
            return "-ERR";
    }

    @Override
    String[] extractContent(String content) {
        return new String[] {content.split("\\s+")[1]};
    }

    String getMailbyid(int id){
        ParcoursMail unParcours = new ParcoursMail(server.getUser());
        try {
            return unParcours.readMailbyid(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
