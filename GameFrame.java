import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameFrame extends JFrame{
    private int width, height;
    private String title;
    private String serverIP;
    private int serverPort;
    private JLayeredPane lp;
    private JPanel cp;  
    private ImageIcon btnBG;
    private GameCanvas gameCanvas;
    private Entity[] entities;
    private GameClient gameClient;
    private JButton btn1;
    private JButton btn2;
    private JButton btn3;
    private JButton btn4;
    private JButton btn5;
    private JButton btn6;
    private JLabel label1;
    private JLabel label2;
    private JTextField textField1;
    private JTextField textField2;


    public GameFrame(int width, int height, String title){
        this.width = width;
        this.height = height;
        this.title = title;
        cp = (JPanel) this.getContentPane();
        // btnBG = new ImageIcon("/UI Assets/btnBG");
        gameCanvas = new GameCanvas(width, height);
        gameClient = gameCanvas.getGameClient();
        btn1 = new JButton("Play");
        btn2 = new JButton("Quit");
        btn3 = new JButton("Connect");
        btn4 = new JButton("Back");
        btn5 = new JButton("Host Server");
        btn6 = new JButton("Enter Game");
        label1 = new JLabel();
        label2 = new JLabel("Port: ");
        textField1 = new JTextField(10);
        textField2 = new JTextField(10);
        lp = new JLayeredPane();
        // try {
        //     Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        //     while (e.hasMoreElements()) {
        //         NetworkInterface networkInterface = (NetworkInterface) e.nextElement();

        //         //Check if interface is down or is only visible to the host
        //         if (!networkInterface.isUp() || networkInterface.isLoopback())
        //             continue;

        //         Enumeration<InetAddress> a = networkInterface.getInetAddresses();
        //         while (a.hasMoreElements()) {
        //             InetAddress inetAddress = a.nextElement();

        //             // Check if address is an Ipv4 address and is not only visible to host
        //             if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
        //                 System.out.println("IPv4 Address: " + inetAddress.getHostAddress());
        //             }
        //         }
        //     }
        // } catch (SocketException e) {
        //     System.out.println("SocketException at GameFrame Constructor");
        // }   


    }

    public void setUpGUI() {     
        setTitle(title);
        setResizable(false);
        cp.setFocusable(true);
        lp.setPreferredSize(new Dimension(width, height));

        gameCanvas.setBounds(0, 0, width, height);
        lp.add(gameCanvas, Integer.valueOf(0)); 
        
        loadStartUI();
        refreshFrame();

        cp.add(lp, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        

    }

    public void loadStartUI(){
        btn1.setBounds(54, 116, 158, 33);
        lp.add(btn1, Integer.valueOf(1));

        btn2.setBounds(54, 169, 158, 33);
        lp.add(btn2 , Integer.valueOf(1));
    }

    public void loadClientUI(){

        label1.setForeground(Color.WHITE);
        label1.setText("IP Address: ");
        label1.setBounds(17, 133, 232, 15);
        lp.add(label1, Integer.valueOf(1));
        
        label2.setForeground(Color.WHITE);
        label2.setText("Port Number: ");
        label2.setBounds(17, 194, 232, 15);
        lp.add(label2, Integer.valueOf(1));

        textField1.setBounds(245, 131, 159,  28);
        lp.add(textField1, Integer.valueOf(1));

        textField2.setBounds(245, 192, 159, 28);
        lp.add(textField2, Integer.valueOf(1));
        
        btn3.setBounds(54, 280, 159, 35);
        lp.add(btn3, Integer.valueOf(1));

        btn4.setBounds(245, 280, 159, 35);
        lp.add(btn4, Integer.valueOf(1));

        btn5.setBounds(54, 385, 159, 35);
        lp.add(btn5, Integer.valueOf(1));
    }

    public void loadHostUI(){
        label1.setForeground(Color.WHITE);
        label1.setText("IP Address: " + serverIP);
        label1.setBounds(17, 133, 232, 15);
        lp.add(label1, Integer.valueOf(1));
        
        label2.setForeground(Color.WHITE);
        label2.setText("Port: " + serverPort);
        label2.setBounds(17, 194, 232, 15);
        lp.add(label2, Integer.valueOf(1));

        btn4.setBounds(245, 280, 159, 35);
        lp.add(btn4, Integer.valueOf(1));

        btn6.setBounds(54, 280, 159, 35);
        lp.add(btn6, Integer.valueOf(1));
    }

    public void clearGUI(){
        Component[] components = lp.getComponentsInLayer(1);
        for (Component c:components){
            lp.remove(c);
        }
    }

    private void refreshFrame(){
        lp.revalidate();
        lp.repaint();
    }

    public void setUpButtons(){
        ActionListener btnListener = (ActionEvent ae) -> {
            Object o = ae.getSource();
            
            if (o == btn1){
                clearGUI();
                loadClientUI();
                refreshFrame();
            }
            else if (o == btn2){
                System.exit(0);                
            }
            else if (o == btn3){
                serverIP = textField1.getText();
                serverPort = Integer.parseInt(textField2.getText());
                startPlay();
            }
            else if (o == btn4){
                clearGUI();
                loadStartUI();
                refreshFrame();
            }
            else if (o == btn5){
                gameClient.hostServer();
                serverIP = gameClient.getServerIP();
                serverPort = gameClient.getServerPort();
                clearGUI();
                loadHostUI();
                refreshFrame();
                
            }
            else if (o == btn6){
                startPlay();
            }

 
        };
        //Assign an event handler for all of the btns
        btn1.addActionListener(btnListener);
        btn2.addActionListener(btnListener);
        btn3.addActionListener(btnListener);
        btn4.addActionListener(btnListener);
        btn5.addActionListener(btnListener);
        btn6.addActionListener(btnListener);
    }

    private void startPlay(){
        gameClient.connectToServer(serverIP, serverPort);
        clearGUI();
        addKeyBindings();
        addMouseListener();
        refreshFrame();
        //Force reload
        cp.requestFocusInWindow();
    }

    public void addMouseListener(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                gameClient.clickInput(e.getX(), e.getY());
            }
        });
    }

    public void addKeyBindings(){
        ActionMap am = cp.getActionMap(); 
        InputMap im = cp.getInputMap();

        AbstractAction keyInputW = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                gameClient.keyInput("W", true);
            }
        };

        AbstractAction keyInputS = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                gameClient.keyInput("S", true);
            }
        };

        AbstractAction keyInputA = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                gameClient.keyInput("A", true);
            }
        };

        AbstractAction keyInputD = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                gameClient.keyInput("D", true);
            }
        };

        AbstractAction stopInputW = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                gameClient.keyInput("W", false);
            }
        };

        AbstractAction stopInputS = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                gameClient.keyInput("S", false);
            }
        };

        AbstractAction stopInputA = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                gameClient.keyInput("A", false);
            }
        };

        AbstractAction stopInputD = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                gameClient.keyInput("D", false);
            }
        };

        am.put("keyInputW", keyInputW);
        am.put("keyInputS", keyInputS);
        am.put("keyInputA", keyInputA);
        am.put("keyInputD", keyInputD);
        am.put("stopInputW", stopInputW);
        am.put("stopInputS", stopInputS);
        am.put("stopInputA", stopInputA);
        am.put("stopInputD", stopInputD);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "keyInputW");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "keyInputA");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "keyInputS");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "keyInputD");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "stopInputW");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "stopInputA");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "stopInputS");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "stopInputD");
    }

    public GameCanvas getCanvas(){
        return gameCanvas;
    }
}
