package org.xwiki.eclipse.dialogs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.xwiki.eclipse.model.impl.ConflictData;

public class CompareDialog extends Dialog
{
	public static final int ID_USE_LOCAL = 1001;
	public static final int ID_USE_REMOTE = 1002;
	public static final int ID_MERGE = 1003;

	private ConflictData conflictData;

	static class CompareElement implements ITypedElement,
			IEncodedStreamContentAccessor
	{
		private String content;

		public CompareElement(String content)
		{
			this.content = content;
		}

		public Image getImage()
		{
			return null;
		}

		public String getName()
		{
			return "no name";
		}

		public String getType()
		{
			return "txt";
		}

		public String getCharset() throws CoreException
		{
			return "UTF-8";
		}

		public InputStream getContents() throws CoreException
		{
			try
			{
				return new ByteArrayInputStream(content.getBytes("UTF-8")); //$NON-NLS-1$
			} catch (UnsupportedEncodingException e)
			{
				return new ByteArrayInputStream(content.getBytes());
			}
		}

	}

	public CompareDialog(Shell parentShell, ConflictData conflictData)
	{
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		this.conflictData = conflictData;
	}
	
	protected void configureShell(Shell newShell) {
	    super.configureShell(newShell);
	    newShell.setText("Version compare");
	  }

	@Override
	protected Point getInitialSize()
	{
		// TODO Auto-generated method stub
		return new Point(800, 600);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		Button button = createButton(parent, ID_USE_REMOTE, "Use remote", true);
		button.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e)
			{
				CompareDialog.this.setReturnCode(ID_USE_REMOTE);
				CompareDialog.this.close();
			}

		});
		
		button = createButton(parent, ID_USE_LOCAL, "Use local", false);
		button.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e)
			{
				CompareDialog.this.setReturnCode(ID_USE_LOCAL);
				CompareDialog.this.close();
			}

		});

		button = createButton(parent, ID_MERGE, "Merge", false);
		button.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e)
			{
				CompareDialog.this.setReturnCode(ID_MERGE);
				CompareDialog.this.close();
			}
		});

	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().applyTo(composite);
		
		CompareViewerPane pane = new CompareViewerPane(composite, SWT.BORDER
				| SWT.FLAT);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(pane);

		CompareConfiguration cc = new CompareConfiguration();
		cc.setLeftLabel("Local");
		cc.setRightLabel("Remote");
		cc.setRightEditable(true);
		TextMergeViewer viewer = new TextMergeViewer(pane, cc);
		pane.setContent(viewer.getControl());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(viewer.getControl());
		viewer.setInput(new DiffNode(new CompareElement(conflictData
				.getLocalContent()), new CompareElement(conflictData
				.getRemoteContent())));

		return pane;
	}

}
