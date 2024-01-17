package net.BKTeam.illagerrevolutionmod.deathentitysystem.data;

public class PlayerSouls {

    private float soul;


    public float getsoul() {
        return soul;
    }

    public void setsoul(float soul) {
        this.soul = soul;
    }

    public  void addsoul(float soul) {
        this.soul += soul;
    }
    

    public void copyFrom(PlayerSouls source) {
        soul = source.soul;
    }


}
