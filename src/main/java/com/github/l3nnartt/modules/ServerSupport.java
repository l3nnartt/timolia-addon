package com.github.l3nnartt.modules;

import com.github.l3nnartt.TimoliaAddon;
import com.github.l3nnartt.listener.MessageEnemyReceiveListener;
import net.labymod.api.events.TabListEvent;
import net.labymod.api.permissions.Permissions;
import net.labymod.ingamegui.moduletypes.ColoredTextModule;
import net.labymod.servermanager.ChatDisplayAction;
import net.labymod.servermanager.Server;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.PacketBuffer;
import java.util.Collections;
import java.util.List;

public class ServerSupport extends Server {

    private boolean displayServer;
    private boolean displayEnemy;
    private boolean displayKit;
    private boolean displayMap;
    private boolean displayBlocks;

    public static String enemy = null;
    public static String kit = null;
    public static String latestMap = null;

    public ServerSupport() {
        super("timolia", "timolia.de", "play.timolia.de", "*.timolia.de", "%.timolia.de", "85.190.150.145", "95.156.239.20");
    }

    public boolean isAllowed(Permissions.Permission permission) {
        return (permission.isDefaultEnabled());
    }

    public void addModuleLines(List<Server.DisplayLine> lines) {
        super.addModuleLines(lines);
        try {
            if (displayServer) {
                lines.add(new Server.DisplayLine("Server", Collections.singletonList(ColoredTextModule.Text.getText(TimoliaAddon.getInstance().getLatestserver()))));
            } if (displayEnemy) {
                if (MessageEnemyReceiveListener.enemy != null)
                    lines.add(new Server.DisplayLine("Enemy", Collections.singletonList(ColoredTextModule.Text.getText(MessageEnemyReceiveListener.enemy))));
            } if (displayKit) {
                if (MessageEnemyReceiveListener.kit != null)
                    lines.add(new Server.DisplayLine("Kit", Collections.singletonList(ColoredTextModule.Text.getText(MessageEnemyReceiveListener.kit))));
            } if (displayMap) {
                if (latestMap != null)
                    lines.add(new Server.DisplayLine("Map", Collections.singletonList(ColoredTextModule.Text.getText(latestMap))));
            } if (displayBlocks) {
                if (TimoliaAddon.getInstance().isPixelspace())
                    lines.add(new Server.DisplayLine("Blocks", Collections.singletonList(ColoredTextModule.Text.getText(String.valueOf(TimoliaAddon.getInstance().getPlacedBlocks())))));
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void handleTabInfoMessage(TabListEvent.Type tabInfoType, String formattedText, String unformattedText) {
        if (formattedText.contains("Du spielst auf")) {
            String[] servername = formattedText.split("§6");
            String serveroutput = servername[servername.length-1];
            serveroutput = serveroutput.substring(0,serveroutput.length()-4);
            TimoliaAddon.getInstance().setLatestserver(serveroutput);
        }
    }

    @Override
    public void onJoin(ServerData serverData) {
    }

    public ChatDisplayAction handleChatMessage(String clean, String formatted) {
        if (formatted.contains("1vs1") && formatted.contains("»")) {
            if (formatted.contains("Kit") && formatted.contains("Einstellungen")) {
                String kitname = clean.split("§6")[1].split("§8")[0];
                kitname = kitname.substring(0, kitname.length()-1);
                kit = kitname;
            }
            if (formatted.contains("Kampf") && formatted.contains("beginnt")) {
                String enemyname = clean.split("§6")[1].split("§7")[0];
                enemyname = enemyname.substring(0, enemyname.length()-2);
                enemy = enemyname;
            }  else if (formatted.contains("den Kampf gegen")) {
                enemy = null;
                kit = null;
            }
        }

        if (clean.contains("Mapvoting") && clean.contains("»") && clean.contains("beendet")) {
            String[] mapname = formatted.split("§6");
            String mapoutput = mapname[mapname.length-1];
            mapoutput = mapoutput.substring(0,mapoutput.length()-5);
            latestMap = mapoutput;

            if (TimoliaAddon.getInstance().isListenForMap()) {
                TimoliaAddon.getInstance().setListenForMap(false);
            }
        } return ChatDisplayAction.NORMAL;
    }

    @Override
    public void handlePluginMessage(String s, PacketBuffer packetBuffer) throws Exception {
    }

    public void reset() {
        super.reset();
        //this.variable = null;
    }

    public void loadConfig() {
        this.displayServer = getBooleanAttribute("displayServer", true);
        this.displayEnemy = getBooleanAttribute("displayEnemy", true);
        this.displayKit = getBooleanAttribute("displayKit", true);
        this.displayMap = getBooleanAttribute("displayMap", true);
        this.displayBlocks = getBooleanAttribute("displayBlocks", true);
    }

    public void fillSubSettings(List<SettingsElement> settingsElements) {
        settingsElements.add(new HeaderElement("Server Support Modules"));
        settingsElements.add(new BooleanElement("Display Server", this, new ControlElement.IconData(Material.ANVIL), "displayServer"));
        settingsElements.add(new BooleanElement("Display Enemy", this, new ControlElement.IconData(Material.NAME_TAG), "displayEnemy"));
        settingsElements.add(new BooleanElement("Display Kit", this, new ControlElement.IconData(Material.DIAMOND_SWORD), "displayKit"));
        settingsElements.add(new BooleanElement("Display Current Map", this, new ControlElement.IconData(Material.SIGN), "displayMap"));
        settingsElements.add(new BooleanElement("Display Blocks", this, new ControlElement.IconData(Material.HARD_CLAY), "displayBlocks"));
    }
}