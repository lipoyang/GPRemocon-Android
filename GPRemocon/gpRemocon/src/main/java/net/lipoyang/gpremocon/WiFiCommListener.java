package net.lipoyang.gpremocon;

/**
 * Event Listener of WiFi Communication
 */
public interface WiFiCommListener {

    public void onConnect();
    public void onDisconnect();
    public void onReceive(byte[] data);
}