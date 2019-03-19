package Serveur_POP3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class Serveur_POP3 {

    //CONSTANTS
    final Boolean SERVER_IS_RUNNING = true;
    final int SERVER_PORT= 1025;

    //INITIALIZE
    private ServerSocket server;
    private Socket client;



    public Serveur_POP3() {
        try {
            server = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {

            while(SERVER_IS_RUNNING) {
                client = server.accept();
                new Thread(new Connexion(client)).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Lancement du serveur

    public static void main(String[] args) {
        Serveur_POP3 srv = new Serveur_POP3();
        srv.run();
    }


}