package plentybase.plugin.hue.bean;


public class LightStateData {

    private int LightID;
    private boolean isOn;
    private boolean isReachable;
    // used means that it has to stay on because the box is finished
    private boolean isUsed;

    public LightStateData(int LightID, boolean isOn, boolean isReachable, boolean isUsed) {
        this.LightID = LightID;
        this.isOn = isOn;
        this.isReachable = isReachable;
        this.isUsed = isUsed;
    }

    public int getLightID() {
        return LightID;
    }

    public void setLightID(int lightID) {
        LightID = lightID;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public boolean isReachable() {
        return isReachable;
    }

    public void setReachable(boolean reachable) {
        isReachable = reachable;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    @Override
    public String toString() {
        return "plentybase.plugin.hue.bean.LightStateData{" +
                "LightID=" + LightID +
                ", isOn=" + isOn +
                ", isReachable=" + isReachable +
                ", isUsed=" + isUsed +
                '}';
    }
}

