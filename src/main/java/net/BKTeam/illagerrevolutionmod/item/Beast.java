package net.BKTeam.illagerrevolutionmod.item;

public enum Beast {
    MAULER("mauler"),
    WILD_RAVAGER("wild_ravager"),
    RAKER("raker"),
    SCROUNGER("scrounger");

    private final String beastName;
    Beast(String name){
        this.beastName=name;
    }

    public String getBeastName() {
        return this.beastName;
    }
}
