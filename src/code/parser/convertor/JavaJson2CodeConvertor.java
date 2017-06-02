package code.parser.convertor;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;

import code.parser.util.VelocityGeneratorCodeUtil;

import com.alibaba.fastjson.JSON;

/**
 * Java data（JSONArray） 到excel的转换器
 * 
 * @author dinghn
 *
 */
public class JavaJson2CodeConvertor extends AbstractConvertor {

	private static final String JAVA_JSON = "java_json";
	private static final String INTERFACE_VM = "/interface.vm";

	public static void main(String[] args) throws Exception {
	}

	/**
	 * @param is
	 * @return
	 * @throws Exception
	 */
	protected byte[] doConvert(String content) throws Exception {
		Map contextMap = new HashMap<String, Object>();
		contextMap.put(JAVA_JSON, JSON.parseObject(content));
		String code_content = VelocityGeneratorCodeUtil
				.getContentForTemplatePath(INTERFACE_VM, contextMap);
		return code_content.getBytes();
	}

	private XSSFCell createCell(XSSFRow row, int columnIndex) {
		XSSFCell cell = row.createCell(columnIndex);
		XSSFCellStyle cellStyle = row.getSheet().getWorkbook()
				.createCellStyle();
		cell.setCellStyle(cellStyle);
		return cell;
	}
}
