package gk.clientstuff;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;


public class Chat extends Component {

    private JTextField socketField;
    private JTextField nickField;
    private JTextField messageField;
    private JButton sendMessageButton;
    private JButton stopButton;
    private JButton submitSocketButton;
    public static JTextArea chatTextArea;
    private String nick;
    private int socketNumber;
    private JFrame frame;
    private Boolean sendMessageCondition = false;

    public Chat(String nick, int port) throws IOException {
        this.nick=nick;
        this.socketNumber=port;
        guiInit();
    }

    private void guiInit() {
        frame = new JFrame("lab09");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setResizable(false);
        JPanel menuPanel = new JPanel(new FlowLayout());
        menuPanel.add(new JLabel("Socket :"));
        socketField = new JTextField("4444");
        socketField.setEditable(false);
        nickField = new JTextField(nick);
        nickField.setEditable(false);
        submitSocketButton = new JButton("start");
        submitSocketButton.addActionListener(e -> {
            try {
                start();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        menuPanel.add(socketField);
        menuPanel.add(new JLabel("nick :"));
        menuPanel.add(nickField);
        menuPanel.add(submitSocketButton);
        JButton settingsButton = new JButton("settings");
        stopButton = new JButton("stop");
        stopButton.setEnabled(false);
        menuPanel.add(stopButton);
        settingsButton.addActionListener(e -> settings());
        menuPanel.add(settingsButton);

        chatTextArea = new JTextArea(1, 10);
        chatTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);

        JPanel sendMessagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField("Place for yours message");
        sendMessageButton = new JButton("send");
        sendMessageButton.addActionListener(e -> {
            try {
                send();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        sendMessageButton.setEnabled(false);
        sendMessagePanel.add(messageField, BorderLayout.CENTER);
        sendMessagePanel.add(sendMessageButton, BorderLayout.EAST);

        frame.add(menuPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(sendMessagePanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        StartThread startThread = new StartThread(socketNumber);
        startThread.run();

    }

    private void start() throws IOException {
        sendMessageButton.setEnabled(true);
        stopButton.setEnabled(true);
        submitSocketButton.setEnabled(false);
        socketNumber = Integer.parseInt(socketField.getText());
        nick = nickField.getText();



    }


    private void send() throws IOException {
        if (!messageField.getText().equals("")) {
            sendMessageCondition = true;
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Message cant be empty !",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }


    private void settings() {
        EventQueue.invokeLater(() -> {

        });
    }

    class StartThread implements Runnable {
        private final int portNumber;

        public StartThread(int portNumber) {
            this.portNumber = portNumber;
        }

        @Override
        public void run() {
            try {
                Socket socket = new Socket("localhost", portNumber);

                ServerThread serverThread = new ServerThread(socket, nick);
                Thread serverAccessThread = new Thread(serverThread);
                serverAccessThread.start();
                while (serverAccessThread.isAlive()) {
                    if (sendMessageCondition) {
                        serverThread.addNextMessage(messageField.getText());
                        //serverThread.addNextMessage("hejka");
                        sendMessageCondition = false;
                    }
                }
            } catch (IOException ex) {
                System.err.println("Fatal Connection error!");
                ex.printStackTrace();
            }
        }
    }

    static class ServerThread implements Runnable {
        private Socket socket;
        private String userName;
        private boolean isAlived;
        private final LinkedList<String> messagesToSend;
        private boolean hasMessages = false;

        public ServerThread(Socket socket, String userName) {
            this.socket = socket;
            this.userName = userName;
            messagesToSend = new LinkedList<String>();
        }

        public void addNextMessage(String message) {
            synchronized (messagesToSend) {
                hasMessages = true;
                messagesToSend.push(message);
            }
        }

        @Override
        public void run() {
            System.out.println("Welcome :" + userName);

            System.out.println("Local Port :" + socket.getLocalPort());
            System.out.println("Server = " + socket.getRemoteSocketAddress() + ":" + socket.getPort());

            try {
                PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), false);
                InputStream serverInStream = socket.getInputStream();
                Scanner serverIn = new Scanner(serverInStream);
                // BufferedReader userBr = new BufferedReader(new InputStreamReader(userInStream));
                // Scanner userIn = new Scanner(userInStream);

                while (!socket.isClosed()) {
                    if (serverInStream.available() > 0) {
                        if (serverIn.hasNextLine()) {
                            chatTextArea.append(serverIn.nextLine() + "\n");
                            //System.out.println(serverIn.nextLine());
                        }
                    }
                    if (hasMessages) {
                        String nextSend = "";
                        synchronized (messagesToSend) {
                            nextSend = messagesToSend.pop();
                            hasMessages = !messagesToSend.isEmpty();
                        }
                        serverOut.println(userName + " > " + nextSend);
                        serverOut.flush();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

}


