package Serveur_POP3.Commandes;

import  Serveur_POP3.Connexion;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CommandesQUIT extends Commandes {

    private int nbMessages;
    public CommandesQUIT(Connexion server, String command) {
        super(server, command);
    }

    @Override
    String makeAnswer(String content) {
        if (server.isStateTransaction()){
                server.setClose(true);
                return "+OK alpha POP3 server signing off";
        }
        return null;
    }

    @Override
    String[] extractContent(String content) {
        return new String[0];
    }

}
