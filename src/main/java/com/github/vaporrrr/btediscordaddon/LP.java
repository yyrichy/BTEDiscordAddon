package com.github.vaporrrr.btediscordaddon;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LP {
    private LuckPerms luckPerms;

    public LP() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
    }

    public int getGroupSize(String groupName) {
        if (luckPerms == null) {
            luckPerms = LuckPermsProvider.get();
        }
        Group group = luckPerms.getGroupManager().getGroup(groupName);
        if (group == null) return -1;
        NodeMatcher<InheritanceNode> matcher = NodeMatcher.key(InheritanceNode.builder(group).build());
        return luckPerms.getUserManager().searchAll(matcher).join().size();
    }
}
