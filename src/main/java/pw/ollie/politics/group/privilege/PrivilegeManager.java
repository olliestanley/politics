package pw.ollie.politics.group.privilege;

import pw.ollie.politics.Politics;

import java.util.HashMap;
import java.util.Map;

public final class PrivilegeManager {
    private final Politics plugin;
    private final Map<String, Privilege> privileges = new HashMap<>();

    public PrivilegeManager(Politics plugin) {
        this.plugin = plugin;

        loadDefaultPrivileges();
    }

    private final void loadDefaultPrivileges() {
//        registerPrivileges(GroupPrivileges.ALL);
//        registerPrivileges(GroupPlotPrivileges.ALL);
    }

    public boolean registerPrivilege(Privilege privilege) {
        return privileges.put(privilege.getName(), privilege) == null;
    }

    public boolean registerPrivileges(Privilege... privileges) {
        for (Privilege p : privileges) {
            if (!registerPrivilege(p)) {
                return false;
            }
        }
        return true;
    }

    public Privilege getPrivilege(String name) {
        return privileges.get(name.toUpperCase().replaceAll(" ", "_"));
    }
}
