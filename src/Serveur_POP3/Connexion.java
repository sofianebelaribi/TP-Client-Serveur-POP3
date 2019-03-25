package Serveur_POP3;

import Serveur_POP3.Commandes.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Connexion implements Runnable {

    //INITIALIZE
    private BufferedReader inputdata;
    private DataOutputStream outputdata;
    private Socket client;
    private boolean close;
    private ArrayList<Commandes> CommandesList = new ArrayList<>();
    private String userfile;

    //CONSTANTS
    int NUMBER_OF_CHANCES = 3;
    final String STATE_AUTHORIZATION = "authorization";
    final String STATE_TRANSACTION = "transaction";
    final String STATE_UPDATE = "update";

    private String state = STATE_AUTHORIZATION;

    //CONSTRUCTOR
    private void setCommandesList(){
        CommandesList.add(new CommandesAPOP(this,"APOP"));
        CommandesList.add(new CommandesLIST(this,"LIST"));
        CommandesList.add(new CommandesSTAT(this,"STAT"));
        CommandesList.add(new CommandesRETR(this,"RETR"));
        CommandesList.add(new CommandesDELE(this,"DELE"));
        CommandesList.add(new CommandesQUIT(this,"QUIT"));
    }
    Connexion(Socket aClientSocket){
        setCommandesList();
        try {
            client = aClientSocket;
            inputdata = new BufferedReader( new InputStreamReader(client.getInputStream()));
            outputdata =new DataOutputStream( client.getOutputStream());
        }
        catch(IOException e) {
            System.out.println("Connection: "+e.getMessage());
        }
    }

    //GETTER & SETTER
    public void setState(String state){ this.state = state; }
    public int getNUMBER_OF_CHANCES(){ return NUMBER_OF_CHANCES; }
    public String getSTATE_TRANSACTION() {
        return STATE_TRANSACTION;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public void run(){
        try {
            // an echo server
            String data = "+OK alpha POP3 server Ready\r";

            System.out.println ("New connection: " + client.getPort() + ", " + client.getInetAddress());
            outputdata.writeBytes(data); // UTF is a string encoding
            outputdata.flush();
            System.out.println ("send: " + data);

            if(client.isConnected())
                readCommand();
        }
        catch(EOFException e) {
            System.out.println("EOF: "+e.getMessage()); }
        catch(IOException e) {
            System.out.println("IO: "+e.getMessage());}
    }

    private void readCommand(){
        System.out.println("Reading from stream:");
        try {
            String command;
            while ((command = inputdata.readLine()) != null && !close) {
                System.out.println ("receive from : " + client.getInetAddress() + " : " + client.getPort() + ", command : " + command);
                answerCommand(command);
                if(close)
                    break;
            }
            if(close)
            {
//                sendResponse("-ERR number of chances attempt");
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void answerCommand(String data){
        String command = data.split("\\s+")[0];
        command = command.toUpperCase();

        String content = "";
        for (String s : data.split("\\s+"))
            content += s + " ";

        for (Commandes commande : CommandesList)
        {
            if(Objects.equals(commande.getCommand(), command))
            {
                commande.answerCommand(content);
                return;
            }
        }
        sendResponse("-ERR unknown command");
    }

    public void sendResponse(String data){
        data += "\r";
        try {
            outputdata.writeBytes(data);
            outputdata.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isStateAuthentified(){
        if(Objects.equals(state, STATE_AUTHORIZATION))
        {
            NUMBER_OF_CHANCES --;
            if (NUMBER_OF_CHANCES == 0)
            {
                close = true;
            }
            return true;
        }
        else
            return false;
    }

    public boolean isStateTransaction()
    {
        return Objects.equals(state, STATE_TRANSACTION);
    }

    public void setUserFile(String file) {
        this.userfile = file;
    }

    public String getUserfile() {
        return userfile;
    }
}
