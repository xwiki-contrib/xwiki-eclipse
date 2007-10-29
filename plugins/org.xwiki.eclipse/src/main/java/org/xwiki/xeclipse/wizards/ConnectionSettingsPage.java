package org.xwiki.xeclipse.wizards;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;
import org.xwiki.xeclipse.XWikiEclipseConstants;

public class ConnectionSettingsPage extends WizardPage
{
    private NewConnectionWizardState newConnectionWizardState;
    
    public ConnectionSettingsPage(String pageName)
    {
        super(pageName);            
        setTitle("XWiki connection settings");
        setImageDescriptor(XWikiEclipsePlugin.getImageDescriptor(XWikiEclipseConstants.CONNECTION_SETTINGS_BANNER));
    }

    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(composite);
        
        Composite connectionSettingsArea = createConnectionSettingsArea(composite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(connectionSettingsArea);
        
        Composite proxySettingsArea = createProxySettingsArea(composite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(proxySettingsArea);
        
        setControl(composite);        
    }
    
    private Composite createConnectionSettingsArea(Composite parent) {
        newConnectionWizardState = ((NewConnectionWizard)getWizard()).getNewConnectionWizardState();
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(composite);
        
        Group group = new Group(composite, SWT.NONE);        
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(group);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(group);
        group.setText("Connection settings");
                
        /* Server URL */
        Label label = new Label(group, SWT.NONE);
        label.setText("Server URL:");
        
        final Text serverUrlText = new Text(group, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(serverUrlText);
        serverUrlText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                newConnectionWizardState.setServerUrl(serverUrlText.getText());
                getContainer().updateButtons();
            }            
        });
                        
        /* Username */
        label = new Label(group, SWT.NONE);
        label.setText("Username:");
        
        final Text userNameText = new Text(group, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(userNameText);
        userNameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                newConnectionWizardState.setUserName(userNameText.getText());
                getContainer().updateButtons();
            }            
        });
        
        /* Password */
        label = new Label(group, SWT.NONE);
        label.setText("Password:");
        
        final Text passwordText = new Text(group, SWT.BORDER | SWT.PASSWORD);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(passwordText);
        passwordText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                newConnectionWizardState.setPassword(passwordText.getText());
                getContainer().updateButtons();
            }            
        });
        
        return composite;
    }
    
    private Composite createProxySettingsArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(composite);
        
        Group group = new Group(composite, SWT.NONE);        
        GridLayoutFactory.fillDefaults().numColumns(4).margins(5, 5).applyTo(group);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(group);
        group.setText("Proxy settings");
        
        Label label = new Label(group, SWT.NONE);        
        
        Button button = new Button(group, SWT.CHECK);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(3, 1).grab(true, false).applyTo(button);
        button.setText("Use proxy");        
        
        /* Proxy host */
        label = new Label(group, SWT.NONE);
        label.setText("Host:");                
        
        Text text = new Text(group, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(text);        
        
        /* Proxy port */
        label = new Label(group, SWT.NONE);
        label.setText("Port:");
        
        text = new Text(group, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(false, false).applyTo(text);    
        
        return composite;        
    }

}
