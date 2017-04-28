package code.parser.popup.actions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import code.parser.ast.ASTUtils;

public class GenerateJavaData implements IObjectActionDelegate {

	private Shell shell;
	private Object[] selects;

	/**
	 * Constructor for Action1.
	 */
	public GenerateJavaData() {
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
		if (selects == null) {
			return;
		}

		File outFile = null;
		JSONArray array = new JSONArray();
		for (Object object : selects) {
			if (!(object instanceof IFile)) {
				continue;
			}
			IFile ifile = (IFile) object;
			if (outFile == null) {
				outFile = ifile.getProject().getLocation()
						.append("code.parser.result.json").toFile();
			}

			File file = ifile.getLocation().toFile();
			try {
				JSONObject parse = ASTUtils.parse(FileUtils
						.readFileToString(file));
				array.add(parse);
			} catch (IOException e) {
				MessageDialog.openError(shell, "Parser",
						"Generate java data parse Failed.\n" + e.toString());
				return;
			}
		}
		if (outFile != null)
			try {
				if (outFile.exists())
					outFile.delete();
				outFile.createNewFile();
				FileUtils.writeStringToFile(outFile, array.toJSONString());
			} catch (IOException e) {
				MessageDialog.openError(shell, "Parser",
						"Generate java data write Failed.\n" + e.toString());
				return;
			}

		MessageDialog.openInformation(shell, "Parser",
				"Generate java data was executed.");
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
