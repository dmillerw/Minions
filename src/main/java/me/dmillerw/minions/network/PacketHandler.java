package me.dmillerw.minions.network;

import me.dmillerw.minions.lib.ModInfo;
import me.dmillerw.minions.network.client.CBatchUpdate;
import me.dmillerw.minions.network.client.CStateUpdate;
import me.dmillerw.minions.network.server.*;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.ID);

    public static void addScheduledTask(INetHandler netHandler, Runnable runnable) {
        FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(runnable);
    }

    private static int clientDiscriminator = 1;
    private static int serverDiscriminator = -1;

    private static <REQ extends IMessage, REPLY extends IMessage> void client(Class<REQ> requestMessageType, Class<? extends IMessageHandler<REQ, REPLY>> messageHandler) {
        INSTANCE.registerMessage(messageHandler, requestMessageType, clientDiscriminator, Side.CLIENT);
        clientDiscriminator++;
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void server(Class<REQ> requestMessageType, Class<? extends IMessageHandler<REQ, REPLY>> messageHandler) {
        INSTANCE.registerMessage(messageHandler, requestMessageType, serverDiscriminator, Side.SERVER);
        serverDiscriminator--;
    }

    public static void initialize() {
        server(SSetMinionName.class, SSetMinionName.Handler.class);
        server(SRequestBatchUpdate.class, SRequestBatchUpdate.Handler.class);
        server(SRegisterStateListener.class, SRegisterStateListener.Handler.class);
        server(SUnregisterStateListener.class, SUnregisterStateListener.Handler.class);
        server(SUpdateJob.class, SUpdateJob.Handler.class);

        client(CBatchUpdate.class, CBatchUpdate.Handler.class);
        client(CStateUpdate.JobUpdate.class, CStateUpdate.JobUpdate.Handler.class);
        client(CStateUpdate.MinionUpdate.class, CStateUpdate.MinionUpdate.Handler.class);
    }
}
