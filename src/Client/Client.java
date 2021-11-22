package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost",6666);
        System.out.println("Connection established");
        System.out.println("Remote port: " + socket.getPort());
        System.out.println("Local port: " + socket.getLocalPort());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        System.out.println("Enter Your ID");
        Scanner sc = new Scanner(System.in);
        String id = sc.nextLine();
        out.writeObject(id);
        String reply = (String)in.readObject();
        System.out.println(reply);
        if(Objects.equals(reply, "Connection Successful")) {
            while(true) {
                System.out.println("Enter Your Command");
                String cmd = sc.nextLine();
                out.writeObject(cmd);
                String replyFromServer = (String)in.readObject();
                System.out.println(replyFromServer);
                if(replyFromServer.equals("LO")) {
                    break;
                }
            }
        }

    }
}
