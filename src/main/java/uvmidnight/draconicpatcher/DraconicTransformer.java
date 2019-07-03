package uvmidnight.draconicpatcher;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;


public class DraconicTransformer implements IClassTransformer {
    Logger coreLogger = LogManager.getLogger("draconicpatcher");
    @Override
    public byte[] transform(String obfName, String transformedName, byte[] basicClass) {

        if (transformedName.equals("com.brandon3055.draconicevolution.client.handler.HudHandler")) {
            return patchHUDHandler(basicClass);
        }

        return basicClass;
    }
    private byte[] patchHUDHandler(byte[] basicClass) {
        coreLogger.log(Level.INFO, "Patching Draconic Evolution.");
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        coreLogger.log(Level.INFO, "Found Draconic Evolution's HUDHandler: " + classNode.name);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);


        for (MethodNode method : classNode.methods) {
            if ("drawArmorHUD".equals(method.name)) {
                ListIterator<AbstractInsnNode> it = method.instructions.iterator();
                while (it.hasNext()) {
                    AbstractInsnNode node = it.next();
                    if (node instanceof IntInsnNode && node.getOpcode() == Opcodes.SIPUSH && ((IntInsnNode) node).operand == 3042) {
                        AbstractInsnNode node2 = it.next();
                        if (node2.getOpcode() == Opcodes.INVOKESTATIC) { //it DOES NOT matter what it is. Only that it is gone
                            it.previous();
                            it.remove();
                            it.next();
                            it.remove();
                        }
                    }
                    if (node instanceof IntInsnNode && node.getOpcode() == Opcodes.SIPUSH && ((IntInsnNode) node).operand == 3008) {
                        AbstractInsnNode node2 = it.next();
                        if (node2.getOpcode() == Opcodes.INVOKESTATIC) { //it DOES NOT matter what it is. Only that it is gone
                            it.previous();
                            it.remove();
                            it.next();
                            it.remove();
                        }
                    }
                }
            }
        }

        classNode.accept(writer);

        String methodName = "drawArmorHUD";
        String methodSignature = "(ZZBD)V";
        if (writer.toByteArray() != basicClass) {
            coreLogger.log(Level.INFO, "HUD Handler Modified");
        }
        return writer.toByteArray();
    }
}
