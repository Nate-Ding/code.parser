package code.parser.ui.excel.convertor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import code.parser.core.convertor.IConvertor;
import code.parser.core.util.ASTUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Java data（JSONArray） 到excel的转换器
 * 
 * @author dinghn
 *
 */
public class JavaCode2ExcelConvertor implements IConvertor {
	private static final String[] COLUMNS = { "接口", "描述", "方法", "方法描述" };
	private static final String DESC = "desc";
	private static final String METHODS = "methods";
	private static final String PARAMS = "params";
	private static final String DATATYPE = "dataType";

	public static void main(String[] args) throws Exception {
	}

	/**
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public byte[] convert(String content) throws IOException {
		try (XSSFWorkbook wb = new XSSFWorkbook()) {
			XSSFSheet sheet = wb.createSheet();
			JSONArray root = JSON.parseArray(content);
			int rowCount = 0;
			XSSFRow header = sheet.createRow(rowCount++);
			for (int i = 0; i < COLUMNS.length; i++) {
				createCell(header, i).setCellValue(COLUMNS[i]);
			}
			for (Object item : root) {
				int colCount = 0;
				XSSFRow row = sheet.createRow(rowCount++);
				JSONObject obj = (JSONObject) item;
				String _class = obj.getString(ASTUtil.NAME);
				String _package = obj.getString(ASTUtil.PACKAGE);
				if (StringUtils.isNotEmpty(_package))
					_class = new StringBuilder(_package).append(".")
							.append(_class).toString();
				String desc = obj.getString(DESC);
				createCell(row, colCount++).setCellValue(_class);
				createCell(row, colCount++).setCellValue(desc);
				JSONArray methods = obj.getJSONArray(METHODS);
				if (methods.size() == 0)
					continue;
				for (Object object : methods) {
					colCount = 2;
					JSONObject m = (JSONObject) object;
					JSONArray params = m.getJSONArray(PARAMS);
					StringBuilder pb = new StringBuilder();
					for (Object param : params) {
						JSONObject p = (JSONObject) param;
						pb.append(p.getString(DATATYPE)).append(" ")
								.append(p.getString(ASTUtil.NAME)).append(",");
					}
					if (pb.length() > 0)
						pb.deleteCharAt(pb.length() - 1);
					createCell(row, colCount++).setCellValue(
							new StringBuilder()
									.append(m.getString(ASTUtil.NAME))
									.append("(").append(pb).append(")")
									.toString());
					createCell(row, colCount++).setCellValue(
							StringUtils.defaultString(m.getString(DESC)));
					row = sheet.createRow(rowCount++);
				}
				if (methods.size() > 0)
					sheet.removeRowBreak(--rowCount);
			}
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				wb.write(out);
				return out.toByteArray();
			}
		}
	}

	private XSSFCell createCell(XSSFRow row, int columnIndex) {
		XSSFCell cell = row.createCell(columnIndex);
		XSSFCellStyle cellStyle = row.getSheet().getWorkbook()
				.createCellStyle();
		cell.setCellStyle(cellStyle);
		return cell;
	}
}
