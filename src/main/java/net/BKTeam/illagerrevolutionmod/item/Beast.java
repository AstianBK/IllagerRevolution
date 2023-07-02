package net.BKTeam.illagerrevolutionmod.item;

public enum Beast {
    RAKER("raker",54),
    MAULER("mauler",36),
    WILD_RAVAGER("wild_ravager",18),
    SCROUNGER("scrounger",0);

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
