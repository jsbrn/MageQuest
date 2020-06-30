package world.events.event;

import world.events.Event;

public class HumanoidRespawnEvent extends Event {

    private Integer humanoid;

    public HumanoidRespawnEvent(Integer humanoidEntity) {
        this.humanoid = humanoidEntity;
    }

    public Integer getHumanoid() {
        return humanoid;
    }

}
