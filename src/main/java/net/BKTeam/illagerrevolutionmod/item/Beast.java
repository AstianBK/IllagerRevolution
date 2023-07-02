package net.BKTeam.illagerrevolutionmod.item;

public enum Beast {
    RAKER("raker",0),
    MAULER("mauler",18),
    WILD_RAVAGER("wild_ravager",36),
    SCROUNGER("scrounger",54);

    private final String beastName;
    private final int row;
    Beast(String name,int pRow){
        this.beastName=name;
        this.row=pRow;
    }

    public String getBeastName() {
        return this.beastName;
    }

    public int getRow(){
        return this.row;
    }
}
