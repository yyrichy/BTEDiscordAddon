package com.github.vaporrrr.btediscordaddon;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class LP {
    private final LuckPerms luckPerms;

    public LP() {
        luckPerms = LuckPermsProvider.get();
    }

    public int getGroupSize(String groupName) {
        Group group = luckPerms.getGroupManager().getGroup(groupName);
        if (group == null) return -1;
        NodeMatcher<InheritanceNode> matcher = NodeMatcher.key(InheritanceNode.builder(group).build());
        return luckPerms.getUserManager().searchAll(matcher).join().size();
    }

    public String getPlayerGroup(Player player) {
        ArrayList<Group> groups = allGroups();
        Collections.reverse(groups);
        for (Group group : groups) {
            if (player.hasPermission("group." + group.getName())) {
                return group.getName();
            }
        }
        return null;
    }

    private ArrayList<Group> allGroups() {
        luckPerms.getGroupManager().loadAllGroups();
        Set<Group> groups = luckPerms.getGroupManager().getLoadedGroups();
        return new ArrayList<>(groups);
    }
}
