package code.parser.ast;

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
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public abstract class ASTUtils {
	private static String[] tags = {};
	private final static String CLASS = "class";
	private final static String DESC = "desc";
	private final static String METHODS = "methods";
	private final static String PARAMS = "params";
	private final static String DATATYPE = "dataType";
	private final static String NAME = "name";
	static {
		try (InputStream is = ASTUtils.class
				.getResourceAsStream("config.properties")) {
			Properties props = new Properties();
			props.load(is);
			String tagsValue = (String) props.get("javadoc.tags");
			tags = StringUtils.split(tagsValue, ",");
			for (int i = 0; i < tags.length; i++) {
				tags[i] = tags[i].toUpperCase();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		String path = "E:/gitRepo/devclouds/src/com/ai/aif/devclouds/dev/service/interfaces/IDevProjResSV.java";
		File javaFile = new File(path);
		String content = FileUtils.readFileToString(javaFile);
		JSONObject parse = parse(content);
		System.out.println(parse);
	}

	public static JSONObject parse(String content) {
		JSONObject classJson = new JSONObject();
		ASTNode root = getAST(content);
		HashMap<String, String> typeSimple2FullMap = new HashMap<String, String>();
		typeSimple2FullMap.put("String", "java.lang.String");
		typeSimple2FullMap.put("Long", "java.lang.Long");
		typeSimple2FullMap.put("Integer", "java.lang.Integer");
		typeSimple2FullMap.put("Double", "java.lang.Double");
		typeSimple2FullMap.put("Float", "java.lang.Float");
		typeSimple2FullMap.put("Short", "java.lang.Short");
		typeSimple2FullMap.put("Character", "java.lang.Character");
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
		TypeDeclaration type = getType(root);
		String _class = type.getName().getFullyQualifiedName();
		classJson.put(CLASS, _class);
		Javadoc typeJavadoc = type.getJavadoc();
		if (typeJavadoc != null) {
			String desc = "";
			for (Object object : typeJavadoc.tags()) {
				TagElement ele = (TagElement) object;
				String tagName = ele.getTagName();
				if (tagName == null) {
					desc = getFragmentsText(ele.fragments());
					break;
				}
			}
			classJson.put(DESC, desc);
		}

		JSONArray methodsJson = new JSONArray();
		String desc = "";
		for (MethodDeclaration methodDecl : type.getMethods()) {
			Javadoc javadoc = methodDecl.getJavadoc();
			JSONObject methodJson = new JSONObject();
			if (javadoc != null) {
				for (Object object : javadoc.tags()) {
					TagElement ele = (TagElement) object;
					String tagName = ele.getTagName();
					if (tagName == null) {
						desc = getFragmentsText(ele.fragments());
					} else if (ArrayUtils.contains(tags, tagName.toUpperCase())) {
						methodJson.put(
								StringUtils.substringAfter(tagName, "@"),
								getFragmentsText(ele.fragments()));
					} else {
						continue;
					}
				}
			}

			JSONArray paramsJson = new JSONArray();
			String dataType = "";
			String name = "";
			for (Object object : methodDecl.parameters()) {
				if (object instanceof SingleVariableDeclaration) {
					SingleVariableDeclaration var = (SingleVariableDeclaration) object;
					JSONObject paramJson = new JSONObject();
					dataType = var.getType().toString();
					int index = dataType.indexOf("[");
					String suffix = "";
					if (index != -1) {
						suffix = dataType.substring(index);
						dataType = dataType.substring(0, index).trim();
					}
					String tmpDataType = typeSimple2FullMap.get(dataType);
					if (tmpDataType != null)
						dataType = tmpDataType;
					if (StringUtils.isNotEmpty(suffix))
						dataType += suffix;

					name = var.getName().toString();
					paramJson.put(DATATYPE, dataType);
					paramJson.put(NAME, name);
					paramsJson.add(paramJson);
				}

			}

			name = methodDecl.getName().toString();
			methodJson.put(NAME, name);
			methodJson.put(DESC, desc);
			methodJson.put(PARAMS, paramsJson);
			methodsJson.add(methodJson);
		}

		classJson.put(METHODS, methodsJson);
		return classJson;
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
				list.add(tEle.getText());
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
