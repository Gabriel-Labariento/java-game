public class GameStarter {
    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame(720, 540, "Biting on Fish");
        GameCanvas.DataHandler dataHandler = (gameFrame.getCanvas()).getDataHandler();
        dataHandler.connectToServer();
        dataHandler.closeSocketsOnShutdown();
        gameFrame.setUpGUI();
        gameFrame.addKeyBindings();
    }
}
