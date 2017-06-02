package code.parser.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public abstract class ASTUtil {
	private static String[] tags = {};
	public final static String PACKAGE = "package";
	public final static String SUPER_INTERFACE = "superInterfaces";
	private final static String DESC = "desc";
	private final static String METHODS = "methods";
	private final static String PARAMS = "params";
	private final static String RET_PARAM = "retParam";
	private final static String THROWS = "throws";
	private final static String DATATYPE = "dataType";
	private final static String JAVADOC_TAGS = "javadocTags";
	public final static String NAME = "name";
	public final static String VALUE = "value";
	static {
		try (InputStream is = ASTUtil.class
				.getResourceAsStream("/config.properties")) {
			Properties props = new Properties();
			props.load(is);
			String tagsValue = (String) props.get("javadoc.tags");
			tags = StringUtils.split(tagsValue, ",");
			for (int i = 0; i < tags.length; i++) {
				tags[i] = tags[i].toUpperCase();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws IOException {
		String path = "E:/gitRepo/devclouds/src/USPATest.java";
		File javaFile = new File(path);
		String content = FileUtils.readFileToString(javaFile);
		JSONObject parse = parse(content);
		System.out.println(parse);
	}

	public static JSONObject parse(String content) {
		HashMap<String, String> typeSimple2FullMap = new HashMap<String, String>();
		initMapByCommonTypes(typeSimple2FullMap);

		ASTNode root = getAST(content);
		initMapByImports(typeSimple2FullMap, root);

		TypeDeclaration type = getType(root);
		List<?> superInterfaceTypes = type.superInterfaceTypes();
		JSONArray superInterfaces = new JSONArray();
		for (Object object : superInterfaceTypes) {
			if (object instanceof SimpleType)
				superInterfaces.add(getTypeFullName(typeSimple2FullMap,
						((SimpleType) object).getName().getFullyQualifiedName()
								.toString()));
		}
		JSONObject classJson = getJavadocJson(type.getJavadoc());
		classJson.put(PACKAGE, getPackage(type));
		classJson.put(NAME, type.getName().getFullyQualifiedName());
		if (!superInterfaces.isEmpty())
			classJson.put(SUPER_INTERFACE, superInterfaces);
		JSONArray methodsJson = new JSONArray();
		for (MethodDeclaration methodDecl : type.getMethods()) {
			Javadoc javadoc = methodDecl.getJavadoc();
			JSONObject methodJson = getJavadocJson(javadoc);
			JSONArray paramsJson = new JSONArray();
			String dataType = "";
			String name = "";
			for (Object object : methodDecl.parameters()) {
				if (object instanceof SingleVariableDeclaration) {
					SingleVariableDeclaration var = (SingleVariableDeclaration) object;
					JSONObject paramJson = new JSONObject();
					dataType = getTypeFullName(typeSimple2FullMap, var
							.getType().toString());
					name = var.getName().toString();
					paramJson.put(DATATYPE, dataType);
					paramJson.put(NAME, name);
					paramsJson.add(paramJson);
				}

			}

			name = methodDecl.getName().toString();
			methodJson.put(NAME, name);
			if (!paramsJson.isEmpty())
				methodJson.put(PARAMS, paramsJson);
			methodJson.put(
					RET_PARAM,
					getTypeFullName(typeSimple2FullMap, methodDecl
							.getReturnType2().toString()));

			@SuppressWarnings("deprecation")
			List<?> exceptions = methodDecl.thrownExceptions();
			JSONArray exceptionArray = new JSONArray();
			for (Object object : exceptions) {
				if (object instanceof SimpleName)
					exceptionArray.add(((SimpleName) object).toString());
			}
			if (!exceptionArray.isEmpty())
				methodJson.put(THROWS, exceptionArray);
			methodsJson.add(methodJson);
		}
		if (!methodsJson.isEmpty())
			classJson.put(METHODS, methodsJson);
		return classJson;
	}

	/**
	 * @param typeSimple2FullMap
	 * @param var
	 * @return
	 */
	private static String getTypeFullName(
			HashMap<String, String> typeSimple2FullMap, String typeName) {
		String dataType = typeName;
		String suffix = "";
		int index = dataType.indexOf("<");
		if (index == -1)
			index = dataType.indexOf("[");
		if (index != -1) {
			suffix = dataType.substring(index);
			dataType = dataType.substring(0, index).trim();
		}
		String tmpDataType = typeSimple2FullMap.get(dataType);
		if (tmpDataType != null)
			dataType = tmpDataType;
		if (StringUtils.isNotEmpty(suffix))
			dataType += suffix;
		return dataType;
	}

	/**
	 * @param type
	 * @return
	 */
	private static String getPackage(TypeDeclaration type) {
		ASTNode root = type.getRoot();
		if (root instanceof CompilationUnit) {
			PackageDeclaration packageDecl = ((CompilationUnit) root)
					.getPackage();
			if (packageDecl != null)
				return packageDecl.getName().getFullyQualifiedName();
		}
		return null;
	}

	/**
	 * @param javadoc
	 * @param methodJson
	 * @return
	 */
	private static JSONObject getJavadocJson(Javadoc javadoc) {
		JSONObject javadocJson = new JSONObject();
		if (javadoc == null) {
			return javadocJson;
		}
		String desc = "";
		JSONArray javadocTagsJson = new JSONArray();
		for (Object object : javadoc.tags()) {
			TagElement ele = (TagElement) object;
			String tagName = ele.getTagName();
			String value = getFragmentsText(ele.fragments());
			if (tagName == null) {
				desc = value;
			} else if (ArrayUtils.contains(tags, tagName.toUpperCase())) {
				JSONObject tagJson = new JSONObject();
				tagJson.put(NAME, tagName);
				tagJson.put(VALUE, value);
				javadocTagsJson.add(tagJson);
			} else {
				continue;
			}
		}
		javadocJson.put(DESC, desc);
		if (!javadocTagsJson.isEmpty())
			javadocJson.put(JAVADOC_TAGS, javadocTagsJson);
		return javadocJson;
	}

	/**
	 * @param typeSimple2FullMap
	 * @param root
	 */
	private static void initMapByImports(
			HashMap<String, String> typeSimple2FullMap, ASTNode root) {
		if (root instanceof CompilationUnit) {
			List<?> imports = ((CompilationUnit) root).imports();
			for (Object object : imports) {
				ImportDeclaration importDecl = (ImportDeclaration) object;
				String fullname = importDecl.getName().toString();
				String simplename = fullname.contains(".") ? StringUtils
						.substringAfterLast(fullname, ".") : fullname;
				typeSimple2FullMap.put(simplename, fullname);
			}
		}
	}

	/**
	 * @param typeSimple2FullMap
	 */
	private static void initMapByCommonTypes(
			HashMap<String, String> typeSimple2FullMap) {
		typeSimple2FullMap.put("String", "java.lang.String");
		typeSimple2FullMap.put("Long", "java.lang.Long");
		typeSimple2FullMap.put("Integer", "java.lang.Integer");
		typeSimple2FullMap.put("Double", "java.lang.Double");
		typeSimple2FullMap.put("Float", "java.lang.Float");
		typeSimple2FullMap.put("Short", "java.lang.Short");
		typeSimple2FullMap.put("Character", "java.lang.Character");
		typeSimple2FullMap.put("Class", "java.lang.Class");
		typeSimple2FullMap.put("Object", "java.lang.Object");

		typeSimple2FullMap.put("List", "java.util.List");
		typeSimple2FullMap.put("Set", "java.util.Set");
		typeSimple2FullMap.put("Map", "java.util.Map");
	}

	/**
	 * @param list
	 * @param fragments
	 * @return
	 */
	private static String getFragmentsText(List<?> fragments) {
		ArrayList<String> list = new ArrayList<String>();
		for (Object fragment : fragments) {
			if (fragment instanceof TextElement) {
				TextElement tEle = (TextElement) fragment;
				list.add(tEle.getText().trim());
			} else {
				list.add(fragment.toString());
			}
		}
		return StringUtils.join(list, "\n");
	}

	public static ASTNode getAST(String content) {
		ASTNode root = null;
		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(content.toCharArray());
		root = parser.createAST(null);
		return root;
	}

	public static TypeDeclaration getType(ASTNode root) {
		final TypeDeclaration[] types = new TypeDeclaration[1];
		if (root != null) {
			root.accept(new ASTVisitor() {
				@Override
				public boolean visit(TypeDeclaration node) {
					types[0] = node;
					return false;
				}
			});
		}
		return types[0];
	}
}
