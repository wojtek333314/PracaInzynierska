package brotherhood.onboardcomputer.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import brotherhood.onboardcomputer.ecuCommands.Pid;

public class EngineData implements Serializable {
    private ArrayList<String> rpm = new ArrayList<>();
    private ArrayList<String> speed = new ArrayList<>();
    private ArrayList<String> engineLoad = new ArrayList<>();
    private ArrayList<String> coolantTemperature = new ArrayList<>();
    private ArrayList<String> fuelLevel = new ArrayList<>();
    private ArrayList<String> oilTemperature = new ArrayList<>();
    private ArrayList<String> fuelRailAbsolutePressure = new ArrayList<>();
    private ArrayList<String> fuelRate = new ArrayList<>();
    private LinkedHashMap<String, Pid> supportedPids;

    public String getLast(ArrayList<String> list) {
        return list.size() > 0 ? list.get(list.size() - 1) : "NO DATA";
    }

    public ArrayList<String> getRpm() {
        return rpm;
    }

    public ArrayList<String> getSpeed() {
        return speed;
    }

    public ArrayList<String> getEngineLoad() {
        return engineLoad;
    }

    public ArrayList<String> getCoolantTemperature() {
        return coolantTemperature;
    }

    public ArrayList<String> getFuelLevel() {
        return fuelLevel;
    }

    public ArrayList<String> getOilTemperature() {
        return oilTemperature;
    }

    public ArrayList<String> getFuelRailAbsolutePressure() {
        return fuelRailAbsolutePressure;
    }

    public ArrayList<String> getFuelRate() {
        return fuelRate;
    }

    public EngineData addRpm(String rpm) {
        this.rpm.add(rpm);
        return this;
    }

    public EngineData addSpeed(String speed) {
        this.speed.add(speed);
        return this;
    }

    public EngineData addEngineLoad(String engineLoad) {
        this.engineLoad.add(engineLoad);
        return this;
    }

    public EngineData addCoolantTemperature(String coolantTemperature) {
        this.coolantTemperature.add(coolantTemperature);
        return this;
    }

    public EngineData addFuelLevel(String fuelLevel) {
        this.fuelLevel.add(fuelLevel);
        return this;
    }

    public EngineData addOilTemperature(String oilTemperature) {
        this.oilTemperature.add(oilTemperature);
        return this;
    }

    public EngineData addFuelRailAbsolutePressure(String fuelRailAbsolutePressure) {
        this.fuelRailAbsolutePressure.add(fuelRailAbsolutePressure);
        return this;
    }

    public EngineData addFuelRate(String fuelRate) {
        this.fuelRate.add(fuelRate);
        return this;
    }

    public EngineData addSupportedPids(String supportedPids) {
        this.fuelRate.add(supportedPids);
        return this;
    }

    public HashMap<String, Pid> getSupportedPids() {
        return supportedPids;
    }

    public EngineData setSupportedPids(LinkedHashMap<String, Pid> supportedPids) {
        this.supportedPids = supportedPids;
        return this;
    }


}
