package com.ui;

import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.networking.Client;
import com.ui.Conversation.Message;
import com.util.Button.ActivateAction;
import com.util.Button.ButtonCircle;
import com.util.Button.CustomButton;
import com.util.Sounds.SoundPlayer;

public class Panel extends JPanel implements ActionListener {
    private static JFrame frame = new JFrame("Semantic Compression");
    private static UserInputField inputArea;
    private static ArrayList<ChatBubble> chatBubbles = new ArrayList<>();
    private static ArrayList<Response> responses = new ArrayList<>();

    private static BufferedImage base;

    public static int changey = 0;
    public static int nexty = 100;

    private static CustomButton newButton;
    private static ArrayList<CustomButton> chatButtons = new ArrayList<>();
    private static ButtonCircle circle = new ButtonCircle(new int[] { 0 });

    public static Conversation currentConversation;
    private static ArrayList<Conversation> conversations = new ArrayList<>();

    private static Client client;
    static {
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);

    }


    public Conversation createEmptyConversation() {
        Conversation con = new Conversation("000", "New Chat");
        conversations.add(con);
        setCustomButton("000");
        return con;

    }

    public void drawConvo(ArrayList<Message> messages) {
        for (Message m : messages) {
            if (m.getRole().equals("user")) {
                ChatBubble chatBubble = new ChatBubble(m.getContent(), nexty);
                chatBubbles.add(chatBubble);
                add(chatBubble);
            }else{
                Response r = new Response(m.getContent(), nexty, false);
                responses.add(r);
                add(r.label);
            }
        }
    }

    public void setCustomButton(String uuid) {
        String name = "";

            for (int i = 0; i<conversations.size(); i++){
                
                if (conversations.get(i).getID().equals(uuid)) {
                    name = conversations.get(i).getName();

                }
            }
        CustomButton button = new CustomButton(() -> {
            if(uuid.equals("000")){
                
            }else{
            for (int i = 0; i<conversations.size(); i++){
                
                if (conversations.get(i).getID().equals(uuid)) {
                    
                    drawConvo(conversations.get(i).load(client));
                    break;
                }
            }
        }

            // conversation.load(uuid);

        }, new ActivateAction());

        button.addText(name);
        button.setBounds(144,120+(80*chatButtons.size()-1));
        chatButtons.add(button);
 
        CustomButton.addButton(chatButtons.get(chatButtons.size() - 1));
        
        circle.addIndex(chatButtons.size());
        //CustomButton.addCircle(circle);

        addMouseListener(chatButtons.get(chatButtons.size() - 1).getMyAction());
        addMouseMotionListener(chatButtons.get(chatButtons.size() - 1).getMyAction());
        requestFocus();
        frame.revalidate();

    }

    public static void startChat(String chatid) {

    }

    public void initGraphics() {
        try {
            //backgroundImage = ImageIO.read(Panel.class.getResourceAsStream("/im/backgrounds/outline.png"));
            base = ImageIO.read(Panel.class.getResource("/im/backgrounds/background.png"));

            newButton = new CustomButton(() -> {
                /*
                 * Requests new Conversation if not already loaded.
                 */

                currentConversation = createEmptyConversation();

            }, new ActivateAction());
            newButton.setFont("/fonts/Sundori.ttf", 20f);
            newButton.addImage(ImageIO.read(Panel.class.getResourceAsStream("/im/buttons/newChatButton.png")));
            newButton.addButtonPressedImage(
                    ImageIO.read(Panel.class.getResourceAsStream("/im/buttons/newChatButtonSel.png")));
            newButton.setBounds(163, 35);

            CustomButton.addButton(newButton);
            CustomButton.addCircle(circle);
            addMouseListener(newButton.getMyAction());
            addMouseMotionListener(newButton.getMyAction());

            // chatButton.setBounds()

            // chatButton = new CustomButton(()->{
            // /*
            // * Requests new Conversation if not already loaded.
            // */

            // });
            // chatButton.addText("Cool Chat");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(null);
        setSize(1920, 1080);
        inputArea = new UserInputField(this, client);

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                changey -= notches * 20;
                if (changey > 0)
                    changey = 0;
                if (changey < -nexty + 200)
                    changey = -nexty + 200;
            }

        });

        add(inputArea);

        frame.setContentPane(this);
        // frame.add(this);
        frame.setVisible(true);

        Timer t = new Timer(33, this);
        t.start();

    }

    public Panel(Client client) {
        Panel.client = client;
        initGraphics();
            
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
          //  frame.setUndecorated(true);
            gd.setFullScreenWindow(frame);

            frame.createBufferStrategy(2); // You can also use 3 for triple buffering

        } else {

            System.out.println("Fullscreen not supported. Using maximized window.");
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SoundPlayer();
            new Panel(new Client());
        });

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(base, -1, -8, null);


            
            if(chatButtons.size()>0)
        chatButtons.get(0).addText(currentConversation.getName());

        for (ChatBubble cb : chatBubbles) {
            cb.draw(g2);
        }

        for (Response r : responses) {
            r.draw(g2);
        }


        newButton.draw(g2);
        for(CustomButton b: chatButtons){
            b.draw(g2);
        }
    }

    public void sendMessage(String message, Client client) {

        ChatBubble chatBubble = new ChatBubble(message, nexty);
        chatBubbles.add(chatBubble);
        add(chatBubble);
        System.out.println("asking server: " + message);
        if(currentConversation==null){
            currentConversation = createEmptyConversation();
        }
        if (currentConversation.getUuid() == "000") {
            currentConversation.addMessage("user", message);
            System.out.println("1");

            // client.sendMessage(message);
            Response r = client.newConversation(message);
            conversations.add(currentConversation);
                        // System.out.println(currentConversation.getName());

            responses.add(r);
                        System.out.println("3");

            add(r.label);
                        System.out.println("4");



            return;
        }
        currentConversation.addMessage("user", message);
        client.sendMessage(message);

        responses.add(new Response(client.receiveMessages(), nexty, true));
        add(responses.get(responses.size() - 1).label);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
