package nycuro.api;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DummyBossBar;
import gt.creeperface.nukkit.scoreboardapi.scoreboard.*;
import nycuro.API;
import nycuro.Loader;
import nycuro.database.Database;
import nycuro.database.objects.ProfileHub;
import nycuro.gui.list.ResponseFormWindow;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * author: NycuRO
 * FactionsCore Project
 * API 1.0.0
 */
public class MechanicAPI {

    public boolean isOnSpawn(Entity entity) {
        double x = entity.getLevel().getSpawnLocation().getX();
        double y = entity.getLevel().getSpawnLocation().getY();
        double z = entity.getLevel().getSpawnLocation().getZ();
        Vector3 vector3 = new Vector3(x, y, z);
        return entity.getLevel().getName().equalsIgnoreCase("world") && entity.getPosition().distance(vector3) <= 300 && !((Player) entity).isOp();
    }

    public boolean isOnPvP(Entity entity) {
        double x = entity.getLevel().getSpawnLocation().getX();
        double y = entity.getLevel().getSpawnLocation().getY();
        double z = entity.getLevel().getSpawnLocation().getZ();
        Vector3 vector3 = new Vector3(x, y, z);
        return entity.getLevel().getName().equalsIgnoreCase("pvp") && entity.getPosition().distance(vector3) <= 34 && !((Player) entity).isOp() && entity.getY() >= 75;
    }

    public boolean isOnBorder(Player player) {
        double x = player.getX();
        double z = player.getZ();
        return (x >= 7500 || x <= -7500) || (z >= 7500 || z <= -7500);
    }

    public void sendToSpawn(Player player) {
        Level level = API.getMainAPI().getServer().getDefaultLevel();
        if (!level.isChunkLoaded(level.getSpawnLocation().getChunkX(), level.getSpawnLocation().getChunkZ())) {
            level.loadChunk(level.getSpawnLocation().getChunkX(), level.getSpawnLocation().getChunkZ());
        }
        player.teleport(level.getSpawnLocation());
        player.setImmobile(false);
    }

    public void sendStats(CommandSender commandSender, IPlayer player) {
        FormWindowCustom infoMenu = new FormWindowCustom("Stats");
        infoMenu.addElement(new ElementLabel(API.getMessageAPI().getStatsCommand(commandSender, player)));
        ((Player) commandSender).showFormWindow(infoMenu);
    }

    /*public void spawnFireworks() {
        entities.forEach(Entity::spawnToAll);
    }*/

    /*public void spawnBoss() {
        for (Player player : API.getMainAPI().getServer().getOnlinePlayers().values()) {
            addDropPartyKey(player);
            new BossEntity();
            API.getMessageAPI().sendDropPartySpawnedMessage(player);
        }
    }

    public void spawnEntities() {
        Entity entities1 = Entity.createEntity(10, API.getMainAPI().getServer().getDefaultLevel().getChunk(129 >> 4, 48 >> 4), API.getAiAPI().getChickenNBT());
        Entity entities2 = Entity.createEntity(11, API.getMainAPI().getServer().getDefaultLevel().getChunk(129 >> 4, 57 >> 4), API.getAiAPI().getCowNBT());
        Entity entities3 = Entity.createEntity(12, API.getMainAPI().getServer().getDefaultLevel().getChunk(129 >> 4, 48 >> 4), API.getAiAPI().getPigNBT());
        Entity entities4 = Entity.createEntity(13, API.getMainAPI().getServer().getDefaultLevel().getChunk(120 >> 4, 48 >> 4), API.getAiAPI().getSheepNBT());
        entities1.spawnToAll();
        entities2.spawnToAll();
        entities3.spawnToAll();
        entities4.spawnToAll();
    }*/

    public void sendDropPartyMessageBroadcast(Player player) {
        API.getMessageAPI().sendDropPartyEventMessage(player);
    }

    private void addDropPartyKey(Player player) {
        Item dropPartyKey = Item.get(Item.TRIPWIRE_HOOK, 1, 1);
        Enchantment enchantment = Enchantment.get(Enchantment.ID_PROTECTION_ALL);
        enchantment.setLevel(1);
        dropPartyKey.setCustomName("DropParty Key");
        dropPartyKey.addEnchantment(enchantment);
        player.getInventory().addItem(dropPartyKey);
        API.getMessageAPI().sendDropPartyReceiveKeyMessage(player);
    }

    public void createBossBar(Player player) {
        API.getMainAPI().bossbar.put(player.getName(), new DummyBossBar.Builder(player)
                .text("bossbar")
                .length(100)
                .color(BlockColor.GREEN_BLOCK_COLOR)
                .build());
        player.createBossBar(API.getMainAPI().bossbar.get(player.getName()));
    }

    public void createScoreboard(Player player) {
        FakeScoreboard fakeScoreboard = new FakeScoreboard();
        Objective object = new Objective("§f§l•§e•§6• FACTIONS §6•§e•§f•", new ObjectiveCriteria("dummy", true));
        DisplayObjective newObject = new DisplayObjective(
                object,
                ObjectiveSortOrder.DESCENDING,
                ObjectiveDisplaySlot.SIDEBAR
        );

        fakeScoreboard.objective = newObject;
        fakeScoreboard.addPlayer(player);
        API.getMainAPI().scoreboard.put(player.getName(), fakeScoreboard);
    }

    public void sendServersModal(Player player) {
        ProfileHub profile = Database.profileHub.get(player.getUniqueId());
        int lang = profile.getLanguage();
        switch (lang) {
            case 0:
                FormWindowSimple serversMenu = new FormWindowSimple("Servers", "                       Hello!\n" +
                        "             Welcome to Servers!\n" +
                        "  Select what you want to do from now.");
                serversMenu.addButton(new ElementButton("How to be Partner?", new ElementButtonImageData("url", "https://i.imgur.com/nujWKR3.png")));
                serversMenu.addButton(new ElementButton("Close"));
                player.showFormWindow(new ResponseFormWindow(serversMenu, new Consumer<Map<Integer, Object>>() {
                    @Override
                    public void accept(Map<Integer, Object> response) {
                        if (!response.isEmpty()) {
                            switch (response.entrySet().iterator().next().getKey()) {
                                case 0:
                                    sendInfoServers(player);
                                    return;
                                case 1:
                                    break;
                            }
                        }
                    }
                }));
                break;
            case 1:
                serversMenu = new FormWindowSimple("Servers", "                      Salut!\n" +
                        "          Bine ai venit la Servere!\n" +
                        "     Alege ce doresti sa faci de acum.");
                serversMenu.addButton(new ElementButton("How to be Partner?", new ElementButtonImageData("url", "https://i.imgur.com/nujWKR3.png")));
                serversMenu.addButton(new ElementButton("Close"));
                player.showFormWindow(new ResponseFormWindow(serversMenu, new Consumer<Map<Integer, Object>>() {
                    @Override
                    public void accept(Map<Integer, Object> response) {
                        if (!response.isEmpty()) {
                            switch (response.entrySet().iterator().next().getKey()) {
                                case 0:
                                    sendInfoServers(player);
                                    return;
                                case 1:
                                    break;
                            }
                        }
                    }
                }));
                break;
        }
    }

    private void sendInfoServers(Player player) {
        FormWindowCustom infoMenu = new FormWindowCustom("Info Partner");
        ProfileHub profile = Database.profileHub.get(player.getUniqueId());
        int lang = profile.getLanguage();
        switch (lang) {
            case 0:
                infoMenu.addElement(new ElementLabel("                      Hello!\n" +
                        "         Welcome to Partner Info!\n\n" +
                        "§c» §aHow can i be Partner?:\n" +
                        "§eFor be a Partner, you need just ask. Contact me on Discord.\n" +
                        "§eDiscord: §7NycuRO#6842\n\n" +
                        "§c» §aWhat benefits i have?: \n" +
                        "§eYour Server appereal here.\n" +
                        "§eYou can get a custom dns for your server.\n" +
                        "§eYour DNS need to be like that: §7{name}.nycuro.us\n\n" +
                        "§c» §aThanks: \n" +
                        "§eThanks so much for all persons who tried to be Partners and who wants.\n" +
                        "§eWe waiting with a message on Discord :).\n" +
                        "§eHave a nice day!"));
                break;
            case 1:
                infoMenu.addElement(new ElementLabel("                      Salut!\n" +
                        "      Bine ai venit la Info Partener!\n\n" +
                        "§c» §aCum pot deveni Partener?:\n" +
                        "§ePentru a deveni partener, trebuie doar sa ceri acest lucru. Contacteaza-ma pe Discord.\n" +
                        "§eDiscord: §7NycuRO#6842\n\n" +
                        "§c» §aCe beneficii am?:\n" +
                        "§eServerul tau va aparea aici.\n" +
                        "§eVei primi un DNS Custom pentru Serverul tau.\n" +
                        "§eAcesta va fi de forma: §7{name}.nycuro.us\n\n" +
                        "§c» §aMultumiri:\n" +
                        "§eVreau sa le multumesc tuturor persoanelor care au incercat sa fie Parteneri si care vor.\n" +
                        "§eVa asteptam cu un mesaj pe Discord :).\n" +
                        "§eO zi placuta!"));
                break;
        }
        player.showFormWindow(infoMenu);
    }
}
