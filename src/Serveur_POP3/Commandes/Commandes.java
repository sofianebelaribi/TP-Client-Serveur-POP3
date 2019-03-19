package Serveur_POP3.Commandes;

import Serveur_POP3.Connexion;

public abstract class Commandes {
    //FIELDS
    Connexion server;
    private String command;

    //CONSTRUCTEUR
    Commandes(Connexion server, String command) {
        this.server = server;
        this.command = command;
    }

    //GETTER SETTER
    public String getCommand() {
        return command;
    }

    //COMMANDES
    public void answerCommand(String content)
    {
        server.sendResponse(makeAnswer(content));
    }

    //ABSTRACT
    abstract String makeAnswer(String content);
    abstract String[] extractContent(String content);

}

