package Client_POP3;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client_POP3 {

    DataOutputStream out;
    BufferedReader in;
    SSLSocket clientSocket;
    Boolean connectedToServer = false;
    Boolean connectedToAccount = false;
    Integer port;
    String ip;

    String timbre;

    public Client_POP3(){
        initClientSSL();
        ip = "localhost";
        port = 1025;
    }

    public Client_POP3(String ip, Integer port){
        initClientSSL();
        this.ip = ip;
        this.port = port;
    }

    private void initClientSSL(){
        try{
            SocketFactory sslSocketFactory = SSLSocketFactory.getDefault();
            clientSocket = (SSLSocket) sslSocketFactory.createSocket();
            String[] allCipherSuites = clientSocket.getSupportedCipherSuites();
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
            clientSocket.setEnabledCipherSuites(newCipherSuites);
        }
        catch(IOException e){
            e.printStackTrace();
        }
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
            String[] response = in.readLine().split(" ");
            timbre = response[response.length-1];

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
        String user = sc.nextLine();
        if(user.split(" ").length>1){
            System.out.println("Vous n'avez pas le droit de mettre d'espace!");
            return demandeConnection();
        }
        String commande = "APOP "+user;
        System.out.println("Veuillez indiquer votre mot de passe: ");
        String pass = sc.nextLine();
        if(pass.split(" ").length>1){
            System.out.println("Vous n'avez pas le droit de mettre d'espace!");
            return demandeConnection();
        }
        pass = timbre+pass;
        pass = criptageMD5(pass);
        commande += " "+pass;
        return commande;
    }

    public String criptageMD5(String pass){
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("MD5");
            byte[] passBytes = pass.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<digested.length;i++){
                sb.append(Integer.toHexString(0xff & digested[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Client_POP3.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String sendConnection(String commande){
        try{
            out.writeBytes(commande+"\r");
            out.flush();
            String[] response = in.readLine().split(" ");
            if(response[0].startsWith("+")){
                connectedToAccount = true;
                return response[response.length-2];
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
            System.out.println("Probleme de connection au serveur!");
            return;
        }

        //Connection a un compte
        String response;
        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenue dans notre service!");
        String commande = demandeConnection();
        response = sendConnection(commande);
        // Si la connection ne s'est pas faite
        while(!connectedToAccount){
            if(response.equals("0")){
                System.out.println("Vous n'avez plus le droit de vous authentifier. Reessayez une prochaine fois.");
                //Pour quitter le processus completement
                System.exit(0);
            }
            else{
                System.out.println("Oops, vos identifiants ne sont pas corrects, il vous restes "+response+" tentative(s). Veuillez les resaisir:");
                commande = demandeConnection();
                response = sendConnection(commande);
            }
        }
        System.out.println("Vous etes connecte!\nVous avez "+response+" message(s)\n");

        while(connectedToServer){
            System.out.println("Veuillez ecrire une commande ici: ");
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
                    System.out.println("Commande inconnue! Tapez 'help' pour connaitre les commandes possibles");
            }
        }
    }

    private void cmdActualiser(){
        try{
            // On recupere le nombre de mail
            String commande = "STAT";
            out.writeBytes(commande+"\r");
            out.flush();
            String returned = in.readLine();
            String[] response = returned.split(" ");

            //System.out.println(returned);

            if(response[0].startsWith("+")){
                Integer nbMail = Integer.parseInt(response[1]);
                BufferedWriter file = new BufferedWriter(new FileWriter("data/client/text.txt"));
                System.out.println("Vous avez "+nbMail+" messages");
                for(int i=0;i<nbMail;i++){
                    commande = "RETR " + i;
                    out.writeBytes(commande+"\r");
                    out.flush();
                    nouveauMail(file, i+1);
                }

                file.close();
                readFile();
            }
        }
        catch(IOException e){
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
        }
    }

    private void nouveauMail(BufferedWriter file, Integer index){

        try{
            String response;
            file.write("Message "+index);
            file.newLine();
            while(!(response = in.readLine()).equals(".")){
                file.write(response);
                file.newLine();
            }
            file.newLine();
            file.newLine();
        }
        catch (IOException e){
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
        }
    }

    private void readFile() {
        try{
            BufferedReader file = new BufferedReader(new FileReader("data/client/text.txt"));

            String strLine;
            while((strLine = file.readLine()) != null ){
                System.out.println(strLine);
            }
            file.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void cmdQuit(){
        try{
            String commande = "QUIT";
            out.writeBytes(commande+"\r");
            out.flush();
            String response = in.readLine();
            //System.out.println(response);
            if(response.startsWith("+")){
                System.out.println("Vous vous etes deconnecte, nous vous remercions pour votre visite.");
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
        //Client_POP3 cli = new Client_POP3("192.168.43.8",1025);
        Client_POP3 cli = new Client_POP3();
        cli.run();

    }

    private SSLSocket getClientSocket() {
        return clientSocket;
    }
}