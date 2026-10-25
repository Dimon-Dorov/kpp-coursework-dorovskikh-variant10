package ua.edu.nupp.kpp.server;

public class ChargingModule {
    private Long id;
    private long stationId;
    private int portNumber;
    private String portType;
    private double power;

    public ChargingModule(){
    }

    public ChargingModule(Long id,
                          long stationId,
                          int portNumber,
                          String portType,
                          double power) {
        this.id = id;
        this.stationId = stationId;
        this.portNumber = portNumber;
        this.portType = portType;
        this.power = power;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }
}
