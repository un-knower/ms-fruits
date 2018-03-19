package wowjoy.fruits.ms.module.team;

import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.user.FruitUser;

import java.util.List;

/**
 * Created by wangziwen on 2018/3/13.
 */
public class FruitTeamUser extends FruitUser {
    private transient String teamRole;

    private transient String teamId;

    private List<FruitNotepadDao> multiNotepad;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public List<FruitNotepadDao> getMultiNotepad() {
        return multiNotepad;
    }

    public void setMultiNotepad(List<FruitNotepadDao> multiNotepad) {
        this.multiNotepad = multiNotepad;
    }

    public String getTeamRole() {
        return teamRole;
    }

    public void setTeamRole(String teamRole) {
        this.teamRole = teamRole;
    }
}
