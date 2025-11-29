package serverfacade;

public interface MessageHandlers {
    public void onMove(String message);
    public void onResign(String message);
    public void onLeave(String message);
    public void onObserve(String message);
    public void on(String message);
}
