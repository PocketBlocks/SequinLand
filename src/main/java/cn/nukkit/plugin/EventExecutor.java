package cn.nukkit.plugin;

import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.utils.EventException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.pocketdreams.sequinland.event.executor.MethodHandleEventExecutor;
import net.pocketdreams.sequinland.event.executor.StaticMethodHandleEventExecutor;
import net.pocketdreams.sequinland.event.executor.asm.ASMEventExecutorGenerator;
import net.pocketdreams.sequinland.event.executor.asm.ClassDefiner;
import com.google.common.base.Preconditions;

/**
 * author: iNevet
 * Nukkit Project
 */
public interface EventExecutor {

    void execute(Listener listener, Event event) throws EventException;

    // Paper start
    public static EventExecutor create(Method m, Class<? extends Event> eventClass) {
        Preconditions.checkNotNull(m, "Null method");
        Preconditions.checkArgument(m.getParameterCount() != 0, "Incorrect number of arguments %s", m.getParameterCount());
        Preconditions.checkArgument(m.getParameterTypes()[0] == eventClass, "First parameter %s doesn't match event class %s", m.getParameterTypes()[0], eventClass);
        ClassDefiner definer = ClassDefiner.getInstance();
        if (Modifier.isStatic(m.getModifiers())) {
            return new StaticMethodHandleEventExecutor(eventClass, m);
        } if (definer.isBypassAccessChecks() || Modifier.isPublic(m.getDeclaringClass().getModifiers()) && Modifier.isPublic(m.getModifiers())) {
            String name = ASMEventExecutorGenerator.generateName();
            byte[] classData = ASMEventExecutorGenerator.generateEventExecutor(m, name);
            Class<? extends EventExecutor> c = definer.defineClass(m.getDeclaringClass().getClassLoader(), name, classData).asSubclass(EventExecutor.class);
            try {
                EventExecutor asmExecutor = c.newInstance();
                // Define a wrapper to conform to bukkit stupidity (passing in events that don't match and wrapper exception)
                return new EventExecutor() {
                    @Override
                    public void execute(Listener listener, Event event) throws EventException {
                        if (!eventClass.isInstance(event)) return;
                        try {
                            asmExecutor.execute(listener, event);
                        } catch (Exception e) {
                            throw new EventException(e);
                        }
                    }
                };
            } catch (InstantiationException | IllegalAccessException e) {
                throw new AssertionError("Unable to initialize generated event executor", e);
            }
        } else {
            return new MethodHandleEventExecutor(eventClass, m);
        }
    }
    // Paper end
}
