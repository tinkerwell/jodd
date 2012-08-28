// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.asm.AsmConst;
import jodd.proxetta.ProxyAspect;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static jodd.proxetta.asm.ProxettaAsmUtil.CLINIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.INIT;

public class ProxettaWrapperClassBuilder extends ProxettaClassBuilder {

	protected final Class targetClassOrInterface;
	protected final Class targetInterface;

	public ProxettaWrapperClassBuilder(Class targetClassOrInterface, Class targetInterface, ClassVisitor dest, ProxyAspect[] aspects, String suffix, String reqProxyClassName, TargetClassInfoReader targetClassInfoReader) {
		super(dest, aspects, suffix, reqProxyClassName, targetClassInfoReader);
		this.targetClassOrInterface = targetClassOrInterface;
		this.targetInterface = targetInterface;
		this.createInitMethod = false;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

		wd.init(name, superName, this.suffix, this.reqProxyClassName);

		// no superclass
		wd.superName = AsmConst.SIGNATURE_JAVA_LANG_OBJECT;

		// change access of destination
		access &= ~AsmConst.ACC_ABSTRACT;
		access &= ~AsmConst.ACC_INTERFACE;

		// write destination class
		if (targetClassOrInterface.isInterface()) {
			interfaces = new String[] {"L" + targetClassOrInterface.getName().replace(".", "/") + ";"};
		} else {
			interfaces = new String[] {"L" + targetInterface.getName().replace(".", "/") + ";"};
		}
		wd.dest.visit(version, access, wd.thisReference, signature, wd.superName, interfaces);

		wd.proxyAspects = new ProxyAspectData[aspects.length];
		for (int i = 0; i < aspects.length; i++) {
			wd.proxyAspects[i] = new ProxyAspectData(wd, aspects[i], i);
		}


		// create new field wrapper field and store it's reference into work-data
		wd.wrapperRef = "_wrapper";
		wd.wrapperType = "L" + name + ";";
		FieldVisitor fv  = wd.dest.visitField(AsmConst.ACC_PUBLIC, wd.wrapperRef, wd.wrapperType, null, null);
		fv.visitEnd();

		createEmptyCtor();
	}

	/**
	 * Created empty default constructor.
	 */
	protected void createEmptyCtor() {
		MethodVisitor mv = wd.dest.visitMethod(AsmConst.ACC_PUBLIC, INIT, "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_OBJECT, INIT, "()V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}


	/**
	 * Creates proxified methods and constructors.
	 * Destination proxy will have all constructors as a target class, using {@link jodd.proxetta.asm.ProxettaCtorBuilder}.
	 * Static initializers are removed, since they will be execute in target anyway.
	 * For each method, {@link ProxettaMethodBuilder} determines if method matches pointcut. If so, method will be proxified.
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodSignatureVisitor msign = targetClassInfo.lookupMethodSignatureVisitor(access, name, desc, wd.superReference);
		if (msign == null) {
			return null;
		}

		// ignore all destination constructors
		if (name.equals(INIT) == true) {
			return null;
		}
		// ignore all destination static block
		if (name.equals(CLINIT) == true) {
			return null;
		}

		return applyProxy(msign);
	}


}
