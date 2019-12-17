package world.entities.magic;

import java.util.ArrayList;

public class Spellbook {

    private ArrayList<Spell> spells;
    private ArrayList<TechniqueName> discovered_techniques;

    public Spellbook() {
        this.spells = new ArrayList<>();
        this.discovered_techniques = new ArrayList<>();
    }

    public void addSpell(Spell spell) { this.spells.add(spell); }
    public void discoverTechnique(TechniqueName technique) { this.discovered_techniques.add(technique); }
    public boolean hasTechnique(TechniqueName technique) { return this.discovered_techniques.contains(technique); }


}
