package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerClass {



    public static void main(String[] args) throws IOException, ClassNotFoundException {
         ArrayList<Student> students = new ArrayList<>();
        ServerSocket welcomeSocket = new ServerSocket(6666);
        while(true){
//            for(Student x: students) {
//                System.out.println(x.getID() + "--" + x.isConnected());
//            }

            System.out.println("Waiting for connection");
            Socket socket = welcomeSocket.accept();
            System.out.println("Connection Established");
            Thread student = new StudentThread(socket,students);
            student.start();
        }
    }
}
