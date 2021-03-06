package sourcecoded.core;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.FMLInjectionData;
import net.minecraft.launchwrapper.Launch;
import sourcecoded.core.configuration.SCConfigManager;
import sourcecoded.core.configuration.VersionConfig;
import sourcecoded.core.configuration.gui.SourceConfigGuiFactory;
import sourcecoded.core.configuration.gui.SourceConfigGuiManager;
import sourcecoded.core.crash.CrashHandler;
import sourcecoded.core.proxy.IProxy;
import sourcecoded.core.util.SourceLogger;
import sourcecoded.core.version.ThreadTrashRemover;
import sourcecoded.core.version.VersionChecker;

import java.io.File;
import java.io.IOException;

@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION, guiFactory = "sourcecoded.core.configuration.gui.SourceConfigGuiFactoryBase")
public class SourceCodedCore {

    @Mod.Instance(Constants.MODID)
    public static SourceCodedCore instance;

    @SidedProxy(clientSide = "sourcecoded.core.proxy.ClientProxy", serverSide = "sourcecoded.core.proxy.ServerProxy")
    public static IProxy proxy;

    public static VersionChecker checker;
    public static boolean isDevEnv = false;

    public static SourceLogger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException {
        logger = new SourceLogger("SourceCodedCore");
        isDevEnv = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        SCConfigManager.init(VersionConfig.createNewVersionConfig(event.getSuggestedConfigurationFile(), "0.4", Constants.MODID));

        if (SCConfigManager.getBoolean(SCConfigManager.Properties.VERS_ON))
            checker = new VersionChecker(Constants.MODID, "https://raw.githubusercontent.com/MSourceCoded/SourceCodedCore/master/version/{MC}.txt", Constants.VERSION, SCConfigManager.getBoolean(SCConfigManager.Properties.VERS_AUTO), SCConfigManager.getBoolean(SCConfigManager.Properties.VERS_SILENT));

        FMLCommonHandler.instance().bus().register(new SourceConfigGuiManager());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerKeybindings();
        proxy.registerRenderers();
        proxy.registerClientMisc();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) throws IOException {
        if (!isDevEnv && checker != null)
            checker.check();

        SourceConfigGuiFactory factory = SourceConfigGuiFactory.create(Constants.MODID, instance, SCConfigManager.getConfig());
        factory.inject();

        ThreadTrashRemover.initCleanup();

        if (SCConfigManager.getBoolean(SCConfigManager.Properties.CRASH))
            CrashHandler.init();
    }

    public static String getForgeRoot() {
        return ((File) (FMLInjectionData.data()[6])).getAbsolutePath().replace(File.separatorChar, '/');
    }

    public static ModContainer getContainer(String modid) {
        for (ModContainer container : Loader.instance().getActiveModList())
            if (container.getModId().equals(modid)) return container;

        return null;
    }

}