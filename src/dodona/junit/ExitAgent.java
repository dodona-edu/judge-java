package dodona.junit;

import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.ClassTransform;
import java.lang.classfile.CodeElement;
import java.lang.classfile.CodeModel;
import java.lang.classfile.instruction.InvokeDynamicInstruction;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import static java.lang.constant.ConstantDescs.CD_int;
import static java.lang.constant.ConstantDescs.CD_void;

/**
 * Java agent that replaces System.exit, Runtime.exit and Runtime.halt in the
 * exercise's and the submission's classes by a SystemExitError.
 */
public class ExitAgent implements ClassFileTransformer {
    private static final ClassDesc AGENT = ClassDesc.of(ExitAgent.class.getName());
    private static final ClassDesc SYSTEM = ClassDesc.of("java.lang.System");
    private static final ClassDesc RUNTIME = ClassDesc.of("java.lang.Runtime");
    private static final MethodTypeDesc STATUS = MethodTypeDesc.of(CD_void, CD_int);

    public static void premain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new ExitAgent());
    }

    public static void exit(int status) {
        throw new SystemExitError("System.exit", status);
    }

    public static void exit(Runtime runtime, int status) {
        throw new SystemExitError("Runtime.exit", status);
    }

    public static void halt(Runtime runtime, int status) {
        throw new SystemExitError("Runtime.halt", status);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain domain, byte[] classfileBuffer) {
        if (!isFromDirectory(domain)) {
            return null;
        }
        ClassFile classFile = ClassFile.of();
        ClassModel model = classFile.parse(classfileBuffer);
        boolean callsExit = model.methods().stream()
                .flatMap(method -> method.code().stream())
                .flatMap(CodeModel::elementStream)
                .anyMatch(ExitAgent::callsExit);
        if (!callsExit) {
            return null;
        }
        return classFile.transformClass(model, ClassTransform.transformingMethodBodies((builder, element) -> {
            switch (element) {
                case InvokeInstruction call when callsExit(call) ->
                        builder.invokestatic(AGENT, call.name().stringValue(), helperType(call.owner().asSymbol()));
                case InvokeDynamicInstruction call when callsExit(call) ->
                        builder.invokedynamic(DynamicCallSiteDesc.of(call.bootstrapMethod(), call.name().stringValue(),
                                call.typeSymbol(), call.bootstrapArgs().stream().map(ExitAgent::helperHandle)
                                        .toArray(ConstantDesc[]::new)));
                default -> builder.with(element);
            }
        }));
    }

    // run compiles the exercise and the submission into the working directory; the judge and libraries are jars.
    private static boolean isFromDirectory(ProtectionDomain domain) {
        CodeSource source = domain == null ? null : domain.getCodeSource();
        URL location = source == null ? null : source.getLocation();
        return location != null && location.getProtocol().equals("file") && location.getPath().endsWith("/");
    }

    private static boolean callsExit(CodeElement element) {
        return switch (element) {
            case InvokeInstruction call ->
                    isExit(call.owner().asSymbol(), call.name().stringValue(), call.typeSymbol());
            // A method reference such as System::exit.
            case InvokeDynamicInstruction call -> call.bootstrapArgs().stream().anyMatch(ExitAgent::isExitHandle);
            default -> false;
        };
    }

    private static boolean isExitHandle(ConstantDesc constant) {
        return constant instanceof DirectMethodHandleDesc handle
                && isExit(handle.owner(), handle.methodName(), MethodTypeDesc.ofDescriptor(handle.lookupDescriptor()));
    }

    private static boolean isExit(ClassDesc owner, String name, MethodTypeDesc type) {
        return type.equals(STATUS) && (owner.equals(SYSTEM) && name.equals("exit")
                || owner.equals(RUNTIME) && (name.equals("exit") || name.equals("halt")));
    }

    private static MethodTypeDesc helperType(ClassDesc owner) {
        return owner.equals(RUNTIME) ? STATUS.insertParameterTypes(0, RUNTIME) : STATUS;
    }

    private static ConstantDesc helperHandle(ConstantDesc constant) {
        if (!isExitHandle(constant)) {
            return constant;
        }
        DirectMethodHandleDesc handle = (DirectMethodHandleDesc) constant;
        return MethodHandleDesc.ofMethod(DirectMethodHandleDesc.Kind.STATIC, AGENT, handle.methodName(),
                helperType(handle.owner()));
    }
}
