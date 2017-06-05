package code.parser.core.convertor;

import java.util.HashMap;
import java.util.Map;

import code.parser.core.util.VelocityUtil;

import com.alibaba.fastjson.JSON;

/**
 * Java JSON 到源码的转换器
 * 
 * @author dinghn
 *
 */
public class JavaJson2CodeConvertor implements IConvertor {

	private static final String JAVA_JSON = "java_json";
	private static final String INTERFACE_VM = "/interface.vm";

	public static void main(String[] args) throws Exception {
	}

	/**
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public String convert(String content) throws Exception {
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put(JAVA_JSON, JSON.parseObject(content));
		String code_content = VelocityUtil.getContentForTemplatePath(
				INTERFACE_VM, contextMap);
		return code_content;
	}
}
