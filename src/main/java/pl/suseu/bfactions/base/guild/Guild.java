package pl.suseu.bfactions.base.guild;

import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Guild {

    private final UUID uuid;

    private String name;
    private User owner;
    private final Region region;
    private final Field field;

    private final Set<User> members = ConcurrentHashMap.newKeySet();
    private final Map<User, GuildPermissionSet> permissions = new ConcurrentHashMap<>();

    public Guild(UUID uuid, String name, User owner, Region region, Field field) {
        this.uuid = uuid;
        this.name = name;
        this.owner = owner;
        this.region = region;
        this.region.setGuild(this);
        this.field = field;
        this.field.setGuild(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isOwner(User user) {
        return this.owner.equals(user);
    }

    public Region getRegion() {
        return region;
    }

    public Set<User> getMembers() {
        return new HashSet<>(this.members);
    }

    public void addMember(User user) {
        this.members.add(user);
        // todo add default permission set to permissions map
    }

    public void removeMember(User user) {
        this.members.remove(user);
        this.permissions.remove(user);
    }

    public void addMemberPermission(User member, GuildPermission permission) {
        GuildPermissionSet permissionSet = this.permissions.get(member);
        if (permissionSet == null) {
            return;
        }

        permissionSet.addPermission(permission);
    }

    public void removeMemberPermission(User member, GuildPermission permission) {
        GuildPermissionSet permissionSet = this.permissions.get(member);
        if (permissionSet == null) {
            return;
        }

        permissionSet.removePermission(permission);
    }

    public boolean hasPermission(User member, GuildPermission permission) {
        GuildPermissionSet permissionSet = this.permissions.get(member);
        if (permissionSet == null) {
            return false;
        }

        return permissionSet.hasPermission(permission);
    }
}
