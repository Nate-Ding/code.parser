/**
 * 
 */
package code.parser.core.util;

import java.io.File;
import java.io.IOException;

/**
 * @author dinghn
 *
 */
public abstract class FileUtil {
	private FileUtil() {
	}

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
}
