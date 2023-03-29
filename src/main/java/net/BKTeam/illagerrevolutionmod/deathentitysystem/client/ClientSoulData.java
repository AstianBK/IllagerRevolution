package net.BKTeam.illagerrevolutionmod.deathentitysystem.client;

public class ClientSoulData {
    private static float playersoul;


    public static void set(float playersoul) {
        ClientSoulData.playersoul = playersoul;
    }

    public static float getplayersoul() {
        return playersoul;
    }


}
