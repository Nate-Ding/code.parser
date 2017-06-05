package code.parser.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * 文件保存对话框
 * 
 * @author dinghn
 *
 */
public class FileSaveDialog extends FileDialog {
	/**
	 * 构造器
	 * 
	 * @param shell
	 *            shell
	 * @param title
	 *            标题 可为null
	 * @param fileName
	 *            默认文件名 可为null
	 * @param filterPath
	 *            默认路径 可为null
	 * @param filterExtensions
	 *            文件扩展名过滤 可为null
	 * @param filterNames
	 *            文件扩展名别称过滤 可为null
	 * @param overwrite
	 *            是否提示覆盖已有文件 默认false
	 */
	public FileSaveDialog(Shell shell, String title, String fileName,
			String filterPath, String[] filterExtensions, String[] filterNames,
			boolean overwrite) {
		super(shell, SWT.SAVE);
		if (title != null)
			setText(title);
		if (fileName != null)
			setFileName(fileName);
		if (filterPath != null)
			setFilterPath(filterPath);
		if (filterExtensions != null)
			setFilterExtensions(filterExtensions);
		if (filterNames != null)
			setFilterNames(filterNames);
		setOverwrite(overwrite);
	}

	@Override
	protected void checkSubclass() {
	}
}