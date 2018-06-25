import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;


public class Starter {

    public static void main(String[] args) {
        ArrayList<Socket> clients = new ArrayList<>();
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            AtomicReference<String> sendMessage = new AtomicReference<>("");
            new Thread(() -> {
                while (sendMessage.get().equals("bye"))
                {
                    Scanner scanner = new Scanner(System.in);
                    sendMessage.set(scanner.nextLine());
                    for (Socket s :clients) {
                        try {
                            DataOutputStream dataOutputStream =
                                    new DataOutputStream(s.getOutputStream());
                            dataOutputStream.writeUTF(sendMessage.get());
                            dataOutputStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }).start();
            while (true)
            {
                final Socket[] socket = new Socket[1];
                socket[0] = serverSocket.accept();
                new Thread(() -> {
                    try {
                        clients.add(socket[0]);
                        DataOutputStream dos = new DataOutputStream(socket[0].getOutputStream());
                        dos.writeUTF("Hello!");
                        dos.flush();
                        DataInputStream dis = new DataInputStream(socket[0].getInputStream());
                        if(dis.readUTF().equals("Hello!"))
                        {
                            System.out.println("New client accepted!");
                            dos.writeUTF("Please send your login:");
                            dos.flush();
                            String login = dis.readUTF();
                            dos.writeUTF("Please send your password");
                            dos.flush();
                            String password = dis.readUTF();
                            if(DatabaseManager.check(login,password))
                            {
                                while (socket[0].isConnected())
                                {
                                    String message = login + ":" + dis.readUTF();
                                    System.out.println(message);
                                    for (Socket s : clients) {
                                        if(s != socket[0])
                                        {
                                            DataOutputStream d =  new DataOutputStream(s.getOutputStream());
                                            d.writeUTF(message);
                                            d.flush();
                                        }
                                    }
                                }
                            }else
                            {
                                dos.writeUTF("Wrong login or password!");
                                dos.flush();
                                socket[0].close();
                                dis.close();
                                dos.close();
                            }

                        }else
                        {
                            JOptionPane.showMessageDialog(null,
                                    "Handshake failure!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
