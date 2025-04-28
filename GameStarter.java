public class GameStarter {
    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame(720, 540, "Biting on Fish");
        GameClient gameClient = (gameFrame.getCanvas()).getGameClient();
        gameClient.connectToServer();
        gameFrame.getCanvas().startRenderLoop();
        gameClient.closeSocketsOnShutdown();
        gameFrame.setUpGUI();
        gameFrame.addKeyBindings();
    }
}
