package Serveur_POP3;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Serveur_POP3 {

    //CONSTANTS
    final Boolean SERVER_IS_RUNNING = true;
    final int SERVER_PORT= 1025;

    //INITIALIZE
    private SSLServerSocket server;
    private SSLSocket client;



    public Serveur_POP3() {
        initServeurSSL();
    }

    private void initServeurSSL(){
        try{
            ServerSocketFactory sslServerSocketFactory = SSLServerSocketFactory.getDefault();
            server = (SSLServerSocket) sslServerSocketFactory.createServerSocket(SERVER_PORT);
            String[] allCipherSuites = server.getSupportedCipherSuites();
            List<String> listAnonCipherSuites = new ArrayList<>();
            for(String cipherSuites : allCipherSuites){
                if(cipherSuites.contains("anon")){
                    listAnonCipherSuites.add(cipherSuites);
                }
            }
            String[] newCipherSuites = new String[listAnonCipherSuites.size()];
            for(int i=0;i<newCipherSuites.length;i++){
                newCipherSuites[i] = listAnonCipherSuites.get(i);
            }
            server.setEnabledCipherSuites(newCipherSuites);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void run() {
        try {

            while(SERVER_IS_RUNNING) {
                client = (SSLSocket) server.accept();
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