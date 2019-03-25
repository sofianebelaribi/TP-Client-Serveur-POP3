package Serveur_POP3.Commandes;


import Serveur_POP3.Connexion;

public class CommandesSTAT extends Commandes {
    private int nbrMsg;
    private int longueurtotale;

    public CommandesSTAT(Connexion server, String command) {
        super(server, command);
    }

    @Override
    String makeAnswer(String content) {
        if (server.isStateTransaction()) {
            setNumberOfMessages();
            return "+OK " + nbrMsg + " " + longueurtotale;
        }
        else{
            System.out.println("false");
            return "-ERR"  + server.getNUMBER_OF_CHANCES() + " chances left";
        }
    }

    @Override
    String[] extractContent(String content) {
        return null;
    }


    private void setNumberOfMessages(){
        ParcoursMail unParcours = new ParcoursMail(server.getUser());
        unParcours.listAllFiles(unParcours.folder);
        this.nbrMsg = unParcours.mesMails.size();
        this.longueurtotale = unParcours.countoctets();
    }


}