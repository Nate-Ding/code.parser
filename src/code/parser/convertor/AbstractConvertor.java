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
	 * @return
	 * @throws IOException
	 */
	private void createOutFile(File outFile) throws IOException {
		if (!outFile.getParentFile().exists())
			outFile.getParentFile().mkdirs();
		if (outFile.exists())
			outFile.delete();
		outFile.createNewFile();
	}

	public void convert(String content, File outFile) throws Exception {
		createOutFile(outFile);
		FileUtils.writeByteArrayToFile(outFile, doConvert(content));
	}

	protected abstract byte[] doConvert(String content) throws Exception;
}
