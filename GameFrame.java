import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameFrame extends JFrame{
    private int width, height;
    private String title;
    private JPanel contentPane;
    private GameCanvas gameCanvas;

    public GameFrame(int width, int height, String title){
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void setUpGUI() {
        contentPane = (JPanel) getContentPane();
        contentPane.setFocusable(true);
        gameCanvas = new GameCanvas(width, height);
        setTitle(title);
        contentPane.add(gameCanvas, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        
    }

    public void addKeyBindings(){
        ActionMap actionMap = contentPane.getActionMap();
        InputMap inputMap = contentPane.getInputMap();
        InputHandler ih = new InputHandler();
        
        AbstractAction move = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                // Get the player
                Entity mc = gameCanvas.getGame().getPlayers().get(0);

                // Determine the key pressed
                String keyPressed = ae.getActionCommand();
                System.out.println("keyPressed is " + keyPressed);
                
                // Move the player depending on the key pressed
                ih.handleInput(mc, keyPressed);

                gameCanvas.getGame().changePlayerRoomLocation();
                gameCanvas.repaint();
            }
        };

        actionMap.put("move", move);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "move");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "move");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "move");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "move");

    }

    private class InputHandler {
        public void handleInput(Entity player, String keyPressed) {
            switch (keyPressed) {
                case "w":
                    player.move(0, -1);
                    System.out.println("Pressed W");
                    break;
                case "a":
                    player.move(-1, 0);
                    System.out.println("Pressed A");
                    break;
                case "s":
                    player.move(0, 1);
                    System.out.println("Pressed S");
                    break;
                case "d":
                    player.move(1, 0);
                    System.out.println("Pressed D");
                    break;
                default:
                    break;
            }
        }  
    }


}
