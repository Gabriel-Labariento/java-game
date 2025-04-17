public class GameStarter {
    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame(720, 540, "Biting on Fish");
        DataHandler dataHandler = (gameFrame.getCanvas()).getDataHandler();
        dataHandler.connectToServer(gameFrame.getCanvas().getSendInputsScheduler());
        gameFrame.getCanvas().startRenderLoop();
        dataHandler.closeSocketsOnShutdown();
        gameFrame.setUpGUI();
        gameFrame.addKeyBindings();

    }
}

// Don't delete yet: used for testing map serialization.
// GameStateManager gsm = new GameStateManager(3);
//         System.out.println(gsm.getMapData());