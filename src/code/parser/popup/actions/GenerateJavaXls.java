package code.parser.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.dnd.SwtUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import code.parser.ast.ASTUtils;
import code.parser.convertor.Interfaces2CSVConvertor;

public class GenerateJavaXls implements IObjectActionDelegate {

	private Shell shell;
	private Object[] selects;

	/**
	 * Constructor for Action1.
	 */
	public GenerateJavaXls() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (selects == null || selects.length == 0) {
			return;
		}
		try {
			JSONArray array = new JSONArray();
			String projectPath = null;
			for (Object object : selects) {
				if (!(object instanceof IFile)) {
					continue;
				}
				IFile ifile = (IFile) object;
				if (StringUtils.isEmpty(projectPath))
					projectPath = ifile.getProject().getLocation().toOSString();
				File file = ifile.getLocation().toFile();
				JSONObject parse = ASTUtils.parse(FileUtils
						.readFileToString(file));
				array.add(parse);
			}
			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			dialog.setFilterExtensions(new String[] { "*.xls" });
			dialog.setFilterNames(new String[] { "工作表格文件 (*.xls)" });
			dialog.setFileName("code.parser.result.xls");
			dialog.setText("please select the file");
			dialog.setFilterPath(projectPath);
			dialog.setOverwrite(true);
			String path = dialog.open();
			if (StringUtils.isEmpty(path))
				return;
			File outFile = new File(path);
			if (outFile.exists())
				outFile.delete();
			outFile.createNewFile();

			String jsonString = array.toJSONString();
			new Interfaces2CSVConvertor().convert(jsonString, outFile);
			MessageDialog.openInformation(shell, "Parser",
					"Generate java xls was executed.");
		} catch (Exception e) {
			MessageDialog.openError(shell, "Parser",
					"Generate java xls write Failed.\n" + e.toString());
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;
		IStructuredSelection ss = (IStructuredSelection) selection;
		selects = ss.toArray();
	}

}
