package com.amin.battlearena.domain.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.amin.battlearena.domain.character.Character;

public abstract class Team {
    private final List<Character> members;
    private final boolean isPlayerTeam;
    
    public Team(boolean isPlayerTeam) {
        this.members = new ArrayList<>();
        this.isPlayerTeam = isPlayerTeam;
    }
    
    protected void addMember(Character character) {
        members.add(character);
    }
    
    public List<Character> getMembers() {
        return Collections.unmodifiableList(members);
    }
    
    public List<Character> getAliveMembers() {
        List<Character> alive = new ArrayList<>();
        for (Character c : members) {
            if (c.isAlive()) {
                alive.add(c);
            }
        }
        return alive;
    }
    
    public boolean isPlayerTeam() {
        return isPlayerTeam;
    }
    
    public boolean isDefeated() {
        return getAliveMembers().isEmpty();
    }
    
    public int getAliveCount() {
        return getAliveMembers().size();
    }
    
    public abstract void initialize();
}
