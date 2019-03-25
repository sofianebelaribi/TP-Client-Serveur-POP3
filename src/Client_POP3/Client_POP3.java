package Client_POP3;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client_POP3 {

    DataOutputStream out;
    BufferedReader in;
    Socket clientSocket;
    Boolean connectedToServer = false;
    Boolean connectedToAccount = false;
    Integer port;
    String ip;

    public Client_POP3(){
        clientSocket = new Socket();
        ip = "localhost";
        port = 110;
    }

    public Client_POP3(String ip, Integer port){
        clientSocket = new Socket();
        this.ip = ip;
        this.port = port;
    }

    private boolean connecte(){
        try{
            clientSocket.connect(new InetSocketAddress(ip, port));
            try{
                out = new DataOutputStream(clientSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            }
            catch(IOException e){
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getMessage());
            }
            String response = in.readLine();
            System.out.println(response);
            return true;
        }
        catch (IOException e){
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
            return false;
        }
    }

    private String demandeConnection(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Veuillez indiquer votre nom d'utilisateur: ");
        String commande = "APOP "+sc.nextLine();
        System.out.println("Veuillez indiquer votre mot de passe: ");
        commande += " "+sc.nextLine();
        return commande;
    }

    private String sendConnection(String commande){
        try{
            out.writeBytes(commande+"\r");
            out.flush();
            String[] response = in.readLine().split(" ");
            if(response[0].startsWith("+")){
                connectedToAccount = true;
                return "OK";
            }
            return response[1];
        }
        catch (IOException e){
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
            return "ERR";
        }
    }

    private void run(){
        //Initier la connection
        connectedToServer = this.connecte();
        Integer tries = 0;
        while(!connectedToServer && tries<3){
            connectedToServer = this.connecte();
            tries++;
        }
        if(!connectedToServer){
            System.out.println("Problème de connection au serveur!");
            return;
        }

        //Connection à un compte
        String response;
        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenue dans notre service!");
        String commande = demandeConnection();
        response = sendConnection(commande);
        // Si la connection ne s'est pas faite
        while(!connectedToAccount){
            if(response.equals("0")){
                System.out.println("Vous n'avez plus le droit de vous authentifier.");
            }
            else{
                System.out.println("Oops, vos identifiants ne sont pas corrects, il vous restes "+response+" tentatives. Veuillez les resaisir:");
                commande = demandeConnection();
                response = sendConnection(commande);
            }
        }
        System.out.println("Vous êtes connecté!");
        while(connectedToServer){
            System.out.println("Veuillez écrire une commande ici: ");
            commande = sc.nextLine();
            switch (commande.toUpperCase()){
                case "ACTUALISER":
                    cmdActualiser();
                    break;

                case "QUIT":
                    cmdQuit();
                    break;

                case "HELP":
                    cmdHelp();
                    break;

                default:
                    System.out.println("Commande inconnue! Tapez 'help' pour connaître les commandes possibles");
            }
        }
    }

    private void cmdActualiser(){
        try{
            // On récupère le nombre de mail
            String commande = "STAT";
            out.writeBytes(commande+"\r");
            out.flush();
            String[] response = in.readLine().split(" ");
            if(response[0].startsWith("+")){
                Integer nbMail = Integer.parseInt(response[1]);
                commande = "RETR";
                out.writeBytes(commande+"\r");
                out.flush();
                for(int i=0;i<nbMail;i++){
                    nouveauMail();
                }
            }
        }
        catch(IOException e){
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
        }
    }

    private void nouveauMail(){
        try{
            BufferedWriter file = new BufferedWriter(new FileWriter("data/client/"));
            String commande = "";
            out.writeBytes(commande);
            out.flush();
            String response;
            while(!(response = in.readLine()).equals(".")){
                file.write(response);
                file.newLine();
            }
            file.close();
        }
        catch (IOException e){
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
        }
    }

    private void cmdQuit(){
        try{
            String commande = "QUIT";
            out.writeBytes(commande+"\r");
            out.flush();
            String response = in.readLine();
            if(response.startsWith("+")){
                System.out.println("Vous vous êtes déconnecté, nous vous remerçions pour votre visite.");
                connectedToServer = false;
                connectedToAccount = false;
            }
        }
        catch(IOException e){
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
        }
    }

    private void cmdHelp(){
        System.out.println("Voice les commandes possibles:");
        List<String> cmds = commandeClient();
        for(String cmd : cmds){
            System.out.println(cmd);
        }
    }

    private List<String> commandeClient(){
        List<String> cmd = new ArrayList<>();
        cmd.add("Actualiser");
        cmd.add("Quit");

        return cmd;
    }

    public static void main(String[] args) {
        Client_POP3 cli = new Client_POP3();
        cli.run();
    }
}