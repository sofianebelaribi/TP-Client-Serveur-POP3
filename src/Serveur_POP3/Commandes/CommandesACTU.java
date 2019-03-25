package Serveur_POP3.Commandes;


import Serveur_POP3.Connexion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CommandesACTU extends Commandes {

    private String login;
    private int nbrMsg;
    private final String userfile = "src\\Serveur_POP3\\BDD\\Users\\users.txt";

    public CommandesACTU(Connexion server, String command) {
        super(server, command);
    }

    @Override
    String makeAnswer(String content) {
        if (server.isStateTransaction()) {

            Commandes stat = new CommandesSTAT(server, "STAT");
            String answerStat = stat.makeAnswer("");
            answerStat = answerStat + "\n";

            ParcoursMail unParcours = new ParcoursMail(server.getUser());
            unParcours.listAllFiles(unParcours.folder);

            Integer i = 0;
            String answerRetr = "";

            for(File fichier : unParcours.mesMails){
                Commandes retr = new CommandesRETR(server, "RETR");
                answerRetr = answerRetr + retr.makeAnswer(" " + i.toString()) + "\n";
                i++;
            }

            return answerStat + answerRetr;
        }
        else{
            System.out.println("test");
            return "-ERR"  + server.getNUMBER_OF_CHANCES() + " chances left";
        }
    }

    @Override
    String[] extractContent(String content) {return null;}
}