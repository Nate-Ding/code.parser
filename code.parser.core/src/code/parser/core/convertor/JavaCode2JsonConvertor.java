package code.parser.core.convertor;

import code.parser.core.util.ASTUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * Java 源码 到JSON的转换器
 * 
 * @author dinghn
 *
 */
public class JavaCode2JsonConvertor implements IConvertor {

	public static void main(String[] args) throws Exception {
	}

	/**
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public byte[] convert(String content) throws Exception {
		JSONObject parse = ASTUtil.parse(content);
		return parse.toJSONString().getBytes();
	}
}
