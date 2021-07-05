package me.psek.vehicles.api;

import me.psek.vehicles.Vehicles;

public class APIHandler {
    private final Registering registering;
    private final Data data;

    public APIHandler(Vehicles plugin) {
        registering = new Registering(plugin);
        data = new Data(plugin);
    }

    public Registering getRegisteringAPI() {
        return registering;
    }

    public Data getDataAPI() {
        return data;
    }
}
