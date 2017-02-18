package net.pocketdreams.sequinland;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;

import cn.nukkit.Server;
import cn.nukkit.utils.MainLogger;

public class WatchdogThread extends Thread {
    private static WatchdogThread instance;
    private final long timeoutTime;
    private volatile long lastTick;
    private volatile boolean stopping;

    private WatchdogThread(long timeoutTime, boolean restart)
    {
        super( "SequinLand Watchdog Thread" );
        this.timeoutTime = timeoutTime;
    }

    public static void doStart(int timeoutTime, boolean restart)
    {
        if ( instance == null )
        {
            instance = new WatchdogThread( timeoutTime * 1000L, restart );
            instance.start();
        }
    }

    public static void tick()
    {
        instance.lastTick = System.currentTimeMillis();
    }

    public static void doStop()
    {
        if ( instance != null )
        {
            instance.stopping = true;
        }
    }

    @Override
    public void run()
    {
        while ( !stopping )
        {
            //
            if ( lastTick != 0 && System.currentTimeMillis() > lastTick + timeoutTime )
            {
                MainLogger log = Server.getInstance().getLogger();
                log.critical( "The server has stopped responding!" );
                log.critical( "Please report this to https://github.com/PocketDreams/SequinLand" );
                log.critical( "Be sure to include ALL relevant console errors and Minecraft crash reports" );
                log.critical( "SequinLand version: " + Server.getInstance().getVersion() );
                //
                log.critical( "------------------------------" );
                log.critical( "Server thread dump (Look for plugins here before reporting to SequinLand!):" );
                dumpThread( ManagementFactory.getThreadMXBean().getThreadInfo( Server.getInstance().currentThread.getId(), Integer.MAX_VALUE ), log );
                log.critical( "------------------------------" );
                //
                log.critical( "Entire Thread Dump:" );
                ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads( true, true );
                for ( ThreadInfo thread : threads )
                {
                    dumpThread( thread, log );
                }
                log.critical( "------------------------------" );
                break;
            }

            try
            {
                sleep( 10000 );
            } catch ( InterruptedException ex )
            {
                interrupt();
            }
        }
    }

    private static void dumpThread(ThreadInfo thread, MainLogger log)
    {
        log.critical( "------------------------------" );
        //
        log.critical( "Current Thread: " + thread.getThreadName() );
        log.critical( "\tPID: " + thread.getThreadId()
                + " | Suspended: " + thread.isSuspended()
                + " | Native: " + thread.isInNative()
                + " | State: " + thread.getThreadState() );
        if ( thread.getLockedMonitors().length != 0 )
        {
            log.critical( "\tThread is waiting on monitor(s):" );
            for ( MonitorInfo monitor : thread.getLockedMonitors() )
            {
                log.critical( "\t\tLocked on:" + monitor.getLockedStackFrame() );
            }
        }
        log.critical( "\tStack:" );
        //
        for ( StackTraceElement stack : thread.getStackTrace() )
        {
            log.critical( "\t\t" + stack );
        }
    }
}