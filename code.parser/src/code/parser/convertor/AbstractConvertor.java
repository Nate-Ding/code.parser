/**
 * 
 */
package code.parser.convertor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * @author dinghn
 *
 */
public abstract class AbstractConvertor {
	/**
	 * 创建文件
	 * 
	 * @return
	 * @throws IOException
	 */
	public static void createFile(File file) throws IOException {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if (file.exists())
			file.delete();
		file.createNewFile();
	}

	public void convert(String content, File outFile) throws Exception {
		createFile(outFile);
		FileUtils.writeByteArrayToFile(outFile, doConvert(content));
	}

	protected abstract byte[] doConvert(String content) throws Exception;
}
