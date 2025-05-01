public class GameStarter {
    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame(720, 540, "Biting on Fish");
        GameClient gameClient = (gameFrame.getCanvas()).getDataHandler();
        gameClient.connectToServer(gameFrame.getCanvas().getSendInputsScheduler());
        gameClient.closeSocketsOnShutdown();
        gameFrame.getCanvas().startRenderLoop();
        gameFrame.setUpGUI();
        gameFrame.addKeyBindings();
    }
}
