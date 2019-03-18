package Serveur_POP3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class Serveur_POP3 {

    private ServerSocket server;
    private Socket client;
    private InputStream dis;
    private BufferedInputStream bis;
    private OutputStream dos;
    private BufferedOutputStream bos;


    public Serveur_POP3() {
        try {
            server = new ServerSocket(1025);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            client = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            bos = new BufferedOutputStream(client.getOutputStream());

            String header = "test";
            bos.write(header.getBytes());
            System.out.println("Message test envoyé");
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String listen ="";
        while(true) {
            try {
                bis = new BufferedInputStream(client.getInputStream());
                int ascii = bis.read();
//                System.out.println(Character.toString ((char) ascii));
                listen = listen + Character.toString ((char) ascii);
                System.out.println(listen);
            } catch (IOException e) {

            }
        }

    }

    // Lancement du serveur

    public static void main(String[] args) {
        Serveur_POP3 srv = new Serveur_POP3();
        srv.run();
    }


}