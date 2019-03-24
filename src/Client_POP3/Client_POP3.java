package Client_POP3;

import Serveur_POP3.Serveur_POP3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client_POP3 {

    OutputStream out;
    InputStream in;
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
                out = clientSocket.getOutputStream();
                in = clientSocket.getInputStream();
            }
            catch(IOException e){
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getMessage());
            }
            byte[] reponse = new byte[1024];
            TimeUnit.SECONDS.sleep(10);
            in.read(reponse);
            System.out.println(new String(reponse));
            return true;
        }
        catch (IOException e){
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
            return false;
        }
        catch (InterruptedException e){
            return false;
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

        //Commande du client
        System.out.println("Veuillez écrire une commande ici: ");
        Scanner sc = new Scanner(System.in);
        String commande;
        while(connectedToServer){
            commande = sc.nextLine();
            if(!commande.contains("APOP") && !connectedToAccount){
                System.out.println("Veuillez vous connecter avant de faire toutes autres commandes: ");
            }
            else if(commande.contains("APOP") && connectedToAccount){
                System.out.println("Vous êtes déjà connecté, entrez une autre commande: ");
            }
            else{
                try{
                    out.write(commande.getBytes());
                    out.flush();
                    byte[] reponse = new byte[1024];
                    in.read(reponse);
                    System.out.println(new String(reponse));
                    System.out.println("Veuillez écrire une commande ici: ");
                }
                catch(IOException e){
                    System.out.println(e.getLocalizedMessage());
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        Client_POP3 cli = new Client_POP3();
        cli.run();
    }
}
