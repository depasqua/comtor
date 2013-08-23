/**
 * Comment Mentor: A Comment Quality Assessment Tool 
 * Copyright (C) 2013 The College of New Jersey
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.comtor.cloudclients.netbeansclient;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.JarFile;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.comtor.apiclient.COMTORCloudAPIClient;
import org.comtor.apiclient.COMTORWebServiceResponse;
import org.openide.filesystems.FileUtil;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileObject; 
//import org.openide.loaders.DataObject;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.simple.XHTMLPanel;
//import org.w3c.tidy.TidyUtils;


/**
 * This JPanel is the GUI for using the COMTOR cloud service from Netbeans. 
 * 
 * It has three main components: 
 * 
 * (1) a button for triggering the submission of code to the COMTOR service,
 * (2) a modal dialog for managing the configuration and behavior of this
 * module, and (3) a button for accessing this configuration dialog.  The panel
 * itself consists of the two buttons and is installed into the NetBeans
 * default toolbar. This panel can be torn off and moved by the user.
 * 
 * The configuration dialog contains three main sections: (1) authentication
 * information, (2) format and delivery options, and (3) advanced settings
 * (largely a work-in-progress placeholder, but eventually will allow for
 * specifying the COMTOR cloud service URI endpoint as well as selecting a
 * subset of COMTOR analysis modules to run).  The authentication tab contains
 * a text field for creating or asking for an API key (by supplying an email
 * address), and a text field for registering an API key with the extension
 * itself. This COMTOR cloud service API key is then saved in a configuration
 * file. The format and delivery tab asks the user how they want the report
 * delivered (e.g., a URL to their email or the report contents displayed
 * within the IDE) as well as the format of the delivered response (either
 * HTML or plaintext).
 * 
 * HISTORY:
 *  This component has undergone a number of significant redesigns, most
 *  recently to leave the JEditorPane in favor of the Flying Saucer's 
 *  XHTMLPanel. We have also hidden most of the nasty GUI stuff behind a
 *  configuration modal dialog. Finally, this class works closely with the
 *  {@link COMTORCloudAPIClient} class; that class handles the communications
 *  and formatting of requests and replies to the COMTOR cloud service itself.
 * 
 * TODO:
 * [DONE] load files from the API into a JAR file
 * [DONE] - persist the settings (most important is API key)
 * [DONE] - fix the HTML rendering by adding in Flying Saucer
 * [PART] make COMTORCloudAPIClient URI's (endpoints of the API) configurable in
 *   our GUI configuration dialog under "Advanced--Service Endpoints"
 * [DONE] - remove unused imports, unused components, debugging statements, etc.
 * - break out JAR file stuff into separate utility/class
 * 
 * @author Michael E. Locasto
 */
public class COMTORJPanel 
    extends javax.swing.JPanel 
{

    /** A static handle to the default name for a configuration 
     * properties file for this plug-in. Currently "comtor-nb.properties".
     * Likely causes the creation of this file in the NetBeans 
     * working directory.
     */
    public static final String COMTOR_CONFIG_FILENAME = "comtor-nb.properties";
    
    /** A handle to the public shim for talking to COMTOR's cloud services */
    private COMTORCloudAPIClient m_client = null;
    
    /** A copy of the user's API key.
     * 
     * We can (1) request a new one or (2) request a reminder via the GUI.
     * Once we fetch the key, it should be copied to persistent storage as
     * part of this plug-in/module...perhaps in the manifest or in a properties
     * bundle. This variable is simply a runtime copy of this information. It
     * is also reflected in two GUI components declared below.
     */
    private String m_APIKey = "";
     
    /**
     * A runtime version of our persistent configuration data.
     */
    private Properties m_config = new Properties();
    
    /**
     * Create and display this after getting a COMTOR report.
     */
    private TopComponent m_reportTab = null;

    /** An instance of Tidy for cleaning up the HTML we get from the
     * COMTOR web service.
     */
    private Tidy m_tidy = null;
    
    /**
     * Our main display viewer, capable of rendering XHTML/CSS.
     */
    private XHTMLPanel viewer = null;
    private JPanel panel = null;
    private JScrollPane scroll = null;
    
    /**
     * Creates new form COMTORJPanel
     */
    public COMTORJPanel() 
    {
        initComponents();
        if(null==m_client)
        {
            m_client = new COMTORCloudAPIClient();
        }
        //tidy = new Tidy(); //XXX TODO
        //we should probably check this return value...
        checkLoadConfiguration(); //load prior config from properties file        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jDialog2 = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jToolBar1 = new javax.swing.JToolBar();
        jButton2 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton1 = new javax.swing.JButton();

        jDialog2.setTitle(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jDialog2.title")); // NOI18N
        jDialog2.setLocation(new java.awt.Point(120, 250));
        jDialog2.setPreferredSize(new java.awt.Dimension(690, 420));
        jDialog2.setResizable(false);
        jDialog2.setSize(new java.awt.Dimension(690, 420));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jPanel1.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jLabel4.text")); // NOI18N

        jTextField1.setText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jTextField1.text")); // NOI18N
        jTextField1.setToolTipText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jTextField1.toolTipText")); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.setText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jTextField2.text")); // NOI18N
        jTextField2.setToolTipText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jTextField2.toolTipText")); // NOI18N
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Courier", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jLabel6.text")); // NOI18N
        jLabel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jLabel6.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP)); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextField1)
                    .add(jTextField2)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator5)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3)
                            .add(jLabel4))
                        .add(0, 392, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(14, 14, 14)
                .add(jSeparator5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(34, 34, 34)
                .add(jLabel6)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        buttonGroup1.add(jRadioButton1);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton1, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jRadioButton1.text")); // NOI18N
        jRadioButton1.setToolTipText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jRadioButton1.toolTipText")); // NOI18N
        jRadioButton1.setFocusable(false);
        jRadioButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton2, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jRadioButton2.text")); // NOI18N
        jRadioButton2.setToolTipText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jRadioButton2.toolTipText")); // NOI18N
        jRadioButton2.setFocusable(false);
        jRadioButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jLabel2.text")); // NOI18N

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton3, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jRadioButton3.text")); // NOI18N
        jRadioButton3.setToolTipText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jRadioButton3.toolTipText")); // NOI18N
        jRadioButton3.setFocusable(false);
        jRadioButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        buttonGroup2.add(jRadioButton4);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton4, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jRadioButton4.text")); // NOI18N
        jRadioButton4.setToolTipText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jRadioButton4.toolTipText")); // NOI18N
        jRadioButton4.setFocusable(false);
        jRadioButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator4)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(6, 6, 6)
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jRadioButton2)
                                    .add(jRadioButton4)
                                    .add(jRadioButton3)
                                    .add(jRadioButton1))))
                        .add(0, 445, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(7, 7, 7)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButton2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButton1)
                .add(8, 8, 8)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jSeparator4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2)
                        .add(36, 36, 36))
                    .add(jRadioButton3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButton4)
                .addContainerGap(168, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 617, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 354, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jLabel8.text")); // NOI18N

        jTextField3.setText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jTextField3.text")); // NOI18N
        jTextField3.setEnabled(false);
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jTextField4.setText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jTextField4.text")); // NOI18N
        jTextField4.setEnabled(false);

        jCheckBox1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jCheckBox1.text")); // NOI18N
        jCheckBox1.setToolTipText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jCheckBox1.toolTipText")); // NOI18N
        jCheckBox1.setEnabled(false);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel5)
                        .add(374, 408, Short.MAX_VALUE))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(jLabel7)
                                .add(0, 0, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1)
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(6, 6, 6)
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jTextField4)
                                    .add(jTextField3))))
                        .addContainerGap())
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(6, 6, 6)
                                .add(jCheckBox1))
                            .add(jLabel8))
                        .add(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jCheckBox1)
                .add(36, 36, 36)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jLabel8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextField4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(121, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jTabbedPane1)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jDialog2Layout = new org.jdesktop.layout.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jToolBar1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jToolBar1.border.title"), javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N
        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jButton2.text")); // NOI18N
        jButton2.setToolTipText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jButton2.toolTipText")); // NOI18N
        jButton2.setActionCommand(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jButton2.actionCommand")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jSeparator2.setForeground(new java.awt.Color(0, 51, 51));
        jToolBar1.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jButton1.text")); // NOI18N
        jButton1.setToolTipText(org.openide.util.NbBundle.getMessage(COMTORJPanel.class, "COMTORJPanel.jButton1.toolTipText")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed1(evt);
            }
        });
        jToolBar1.add(jButton1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Return the full canonical path to the target project to be uploaded.
     * Currently this approach is just to grab the last thing returned
     * by the IDE; other projects may be open, and we don't yet supply a
     * way for the user to tell us to use a different project. Perhaps in
     * the GUI configuration dialog we can have a textfield or pulldown of
     * all available projects they can select from.
     * 
     * @return a string containing the canonical path of the chosen project
     */
    private String [] getTargetProjectRoot()
    {
        OpenProjects ops;
        Project [] projects = new Project[0];
        FileObject root;
        String [] paths = new String[2];
       
        ops = OpenProjects.getDefault();
        projects = ops.getOpenProjects();
        System.err.println("["+this.getClass().getName()
                              +"] found "
                              +projects.length
                              +" projects in the IDE");
        for(int i=0;i<projects.length;i++)
        {
            root = projects[i].getProjectDirectory();
            try{
                paths[0] = FileUtil.toFile(root).getCanonicalPath();
                paths[1] = FileUtil.toFile(root).getPath();
            }catch(IOException ioex){
                JOptionPane.showMessageDialog(this, "root path lookup failed: "
                                                     +ioex.getMessage());
            }
        }
        
        //if no prjects found, return String[0]
        if(0==projects.length)
        {
            paths = new String[0];
            JOptionPane.showMessageDialog(this, "No projects found to submit to COMTOR; make sure you have a project opened or defined.");
        }else{        
            paths[0]+=File.separator+"src"+File.separator;
            paths[1]+=File.separator+"src"+File.separator;
        }
        
        System.err.println("["+this.getClass().getName()
                              +"] planning to submit project at path ["
                              +paths[1]
                              +"] as a JAR file");
        return paths;
    }
    
    /**
     * Attempt to locate and load .properties XML file and load any
     * settings from it. For now, just load the API key.
     * 
     * Load comtor-nb.properties
     */
    private boolean checkLoadConfiguration()
    {
        boolean ret = false;
        FileInputStream pin = null;
        
        //assert(null!=m_config);
        
        try{
            pin = new FileInputStream(COMTORJPanel.COMTOR_CONFIG_FILENAME);
            m_config.loadFromXML(pin);
            
            //returns NULL if this key does not exist
            m_APIKey = m_config.getProperty("apikey");

            if(null!=m_APIKey){
                m_client.setKey(m_APIKey);
                jLabel6.setText(m_APIKey);
                jTextField2.setText("");
            }else{
                //no previous key, let GUI tell us what the API key is
            }
            
            ret = true;
        }catch(Exception ex){
            System.err.println("["+this.getClass().getName()
                                  +"]: problem loading properties file: "
                                  +ex.getMessage());
        }
        return ret;
    }
    
    /**
     * Save the values in the GUI Dialog configuration to the
     * properties file.
     */
    private boolean saveConfiguration()
    {
        boolean ret = false;
        FileOutputStream pin = null;
        
        try{
            pin = new FileOutputStream(COMTORJPanel.COMTOR_CONFIG_FILENAME);
            
            //save currently bound API key
            m_config.setProperty("apikey", m_APIKey);
            m_config.storeToXML(pin, "COMTOR Properties version file saved "
                                     +System.currentTimeMillis());
            ret = true;
        }catch(Exception ex){
            System.err.println("["+this.getClass().getName()
                                  +"]: problem saving properties file: "
                                  +ex.getMessage());        
        }
        
        return ret;
    }
    
    /**
     * Recursively gather file names, turn them into files, and
     * then create new JarEntry objects based on them.
     *  
     * @precondition upon first invocation, (prefix == s)
     * 
     * @param prefix a common prefix to remove from file names under
     * this path so that the resulting JAR is not full of absolute
     * file paths relevant only to the generating machine
     * 
     * @param s a string containing the canonical path of a file; this
     * file is either a plain file or a directory; in the latter case,
     * recursively explore all children
     * 
     * @param j a JarOutputStream to write new JarEntry objects to
     */
    private void collectFiles(String prefix,
                              String s, 
                              JarOutputStream jout)
    {
        s = s.replaceFirst(prefix, ""); //remove prefix from s
        File f = new File(prefix, s); //XXX
        File [] children = new File[0];
        JarEntry jent;
        int b;
        BufferedInputStream fin = null;
        
        //common case (plus recursion preamble) / base case 
        try{
            if(s!=null && !s.equals("") && f.canRead())
            {
                System.err.println("Adding file (path) ["+f.getPath()+"]");                
                System.err.println("Adding file (name) ["+f.getName()+"]");

                if(f.isDirectory())
                {
                    jent = new JarEntry(s+File.separator);
                }else{
                    jent = new JarEntry(s);
                }
                jout.putNextEntry(jent);

                //read from f into b, but only if f is not a directory
                if(!f.isDirectory())
                {
                    fin = new BufferedInputStream(new FileInputStream(f));
                    while(-1!=(b=fin.read()))
                    {
                        jout.write(b);
                    }
                }
                jout.closeEntry();
                jout.flush();
            }else{
                //ignore file, we can't read it anyway
                System.err.println("["+this.getClass().getName()+"]: "
                                   +"ignoring non-readable file or empty file path: "
                                   +s);
            }
            }catch(FileNotFoundException fnf){
                System.err.println("file not found: "+fnf.getMessage());
            }catch(IOException ioex){
                System.err.println("I/O problem: "+ioex.getMessage());
            }

        //recursion case (tail)
        if(f.isDirectory())
        {
            try{
                children = f.listFiles();
                for(int i=0;i<children.length;i++)
                {
                    //collectFiles(prefix, children[i].getPath(), jout);
                    collectFiles(prefix, children[i].getCanonicalPath(), jout);
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(this, 
                                          "problem collecting child files:\n "
                                          +e.getMessage());
            }
        }
        return;
    }
    
    /**
     * Iterate over all .java files in the project and construct a JAR file
     * from them. Create this temporary .jar file, store to FS and then return
     * a handle to it.
     * 
     * Although it is tempting to try to recover the files from the open
     * tabs (i.e., TopComponents), this is ultimately the wrong approach.
     * 
     * Two approaches remain: 
     * (1) ask NetBeans to give us handles to each source file derived from
     *     its own internal representation of these files (e.g., how it
     *     refers to them in the tree layout of the project files...)
     * (2) ask for the project root directory and then do our java.io.File
     *     operations relative to the real file system, bypassing NetBeans
     *     entirely.
     * 
     * The one complication here is in building a JAR file. I don't think
     * it suffices to simply create a JAR representing $PROJ_DIR/src; instead,
     * we have to recursively add all the .java files under that directory...
     */
    private File gatherFiles()
    {
        String rootPath = "";
        String rootCanonicalPath = "";
        String [] paths = new String[0];
        String prefix = "";
        StringBuilder jarName = new StringBuilder();
        //srcJAR = new File("/Users/michael/foo.jar");
        File srcJAR = null;
        JarOutputStream jout;
        Manifest manifest = new Manifest();
        Attributes attrib;
        JarEntry jent = new JarEntry("META-INF"+File.separator);
        
        //$PROJECT_DIR/src/
        paths = getTargetProjectRoot();
        //rootPath = getTargetProjectRoot();
        rootCanonicalPath = paths[0];
        rootPath = paths[1]; //interested in ./src
        prefix = rootPath; //includes /src
        
        //create "user.dir"/comtor-submission-jar-$TIME.jar
        jarName.append(System.getProperty("user.dir"));
        jarName.append(File.separator);
        jarName.append("comtor-submission-jar-");
        jarName.append(System.currentTimeMillis());
        jarName.append(".jar");
        
        try{
            srcJAR = new File(jarName.toString());
            
            System.err.println("["+this.getClass().getName()+"] creating JAR file: "+srcJAR.getCanonicalPath());
            jout = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(srcJAR)));
            jout.setComment("NetBeans COMTOR plug-in generated JAR file");
            //put META-INF/
            jout.putNextEntry(jent);
            jout.closeEntry();
            jout.flush();
            
            //create and add Attributes to the manifest
            attrib = manifest.getMainAttributes();
            attrib.putValue("Manifest-Version", "1.0");
            attrib.putValue("Created-By", "COMTORSubmission");
            //put META-INF/MANIFEST.MF
            jent = new JarEntry("META-INF"+File.separator+"MANIFEST.MF");
            jout.putNextEntry(jent);
            manifest.write(jout);
            jout.closeEntry();
            jout.flush();

            //now recursively add all files under 'rootPath' to this JAR file
            //collectFiles(rootPath, jout);
            collectFiles(prefix, rootPath, jout);
            jout.flush();
            jout.close();
        }catch(FileNotFoundException fnf){
            System.err.println("file not found: "+fnf.getMessage());
        }catch(IOException ioex){
            System.err.println("I/O problem: "+ioex.getMessage());
        }
        return srcJAR;
    }
    
    /**
     * Create a new JAR file containing the .java files from the project
     * by asking the IDE what java files belong to the current project.
     * 
     * Pass the JAR file and some parameters to the COMTOR API client's
     * submit() method. Display a valid response according to the parameters;
     * if the parameters indicate that the IDE should display the response,
     * display the HTML or plaintext report response in a new file tab.
     * 
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        COMTORWebServiceResponse ret = null;
        File codeJARFile = null;
        String format = "text";
        String sink = "email";
        boolean dock_result = false;        
        Mode defaultMode;
        
        defaultMode = WindowManager.getDefault().findMode("editor");

        //create JAR file for submission to COMTOR cloud service
        codeJARFile = gatherFiles();
        
        if(null==codeJARFile || !codeJARFile.exists())
        {
            System.err.println("JAR file does not exist");
            return;
        }

        if(jRadioButton1.isSelected())
        {
            format = "text";
        }else{
            format = "html";
        }
        
        if(jRadioButton3.isSelected())
        {
            sink = "http";
        }else{
            sink = "email";
        }
        
        //invoke the COMTOR APIClient utility class to submit to the
        //cloud for us, using our registered API key
        ret = m_client.submit(m_APIKey, codeJARFile, format, sink);
        
        //assert(null!=ret);
        if(null==ret || true==ret.isErr)
        {
            String err = "";
            System.err.println("submit failed");
            if(null!=ret)
            {
                err = ret.errmesg;
            }else{
                err = "null response object";
            }
            JOptionPane.showMessageDialog(this, 
                                          "COMTOR submission failed:\n"+err);
        }else{
            //submission valid; pull apart 'ret' object reference
            System.err.println("["+this.getClass().getName()+"] submit OK");
            
            //check if email or http is the sink
            if(sink.equals("http"))
            {
                if(null!=defaultMode)
                {
                    if(null==m_reportTab)
                    {
                        m_reportTab = new TopComponent();
                        m_reportTab.setName("COMTOR Report");
                        m_reportTab.setToolTipText("Displays COMTOR Output from Cloud Service");
                        m_reportTab.setLayout(new BorderLayout());
                    }
                    
                    if(null==viewer)
                    {
                        viewer = new XHTMLPanel();
                    }                    
                    
                    if(format.equals("text"))
                    {
                        InputStream instream = null;
                        java.io.ByteArrayInputStream bais = 
                                new java.io.ByteArrayInputStream(ret.getTextReportContentsAsXML().getBytes());
                        instream = (InputStream)bais;
                        try{                            
                            viewer.setDocument(instream, "/");                            
                        }catch(Exception ex){
                            System.err.println("["+this.getClass().getName()
                                                  +"] error setting text of document: "
                                                  +ex.getMessage());
                        }
                        
                    }else{
                        //an alternative is to simply tell viewer to fetch
                        //the HTML report URI:
                        // e.g., viewer.setDocument(ret.getHtml_report_url());
                        try{
                            InputStream instream = null;
                            java.io.ByteArrayInputStream bais =
                                 new java.io.ByteArrayInputStream(ret.getHtml_report_contents().getBytes());
                            instream = (InputStream)bais;
                            viewer.setDocument(instream, "/");
                        }catch(Exception ex){
                            //ex.printStackTrace();
                            System.err.println("["+this.getClass().getName()
                                                  +"]: problem reading string for XHTML viewing: "
                                                  + ex.getMessage());
                        }                        
                    }
                                        
                    //System.err.println("--------\n"+ret.get_raw()+"\n-------");
                    System.err.println("m_raw length is "
                                       +ret.get_raw().length()
                                       +" bytes.");
                                        
                    if(null==scroll)
                    {
                        scroll = new JScrollPane(viewer);
                        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                        scroll.validate();
                    }
                    
                    if(null==panel)
                    {
                        panel = new JPanel();
                        panel.setLayout(new BorderLayout());
                        panel.add(scroll, BorderLayout.CENTER);
                        panel.setVisible(true);
                        panel.validate();
                    }
                                                            
                    m_reportTab.add(panel, BorderLayout.CENTER);
                    m_reportTab.setEnabled(true);

                    dock_result = defaultMode.dockInto(m_reportTab);

                    m_reportTab.invalidate();
                    m_reportTab.validate();
                    m_reportTab.open();
                    m_reportTab.requestActive();
                    m_reportTab.requestVisible();
                    //m_reportTab.requestFocus(); //not needed
                    m_reportTab.setVisible(true);
                    //m_reportTab.requestAttention(true); //flash window tab
                    
                    //System.err.println("m_reportTab.getComponentCount(): "
                    //        +m_reportTab.getComponentCount());
                    //System.err.println("displayArea.getComponentCount(): "
                    //        +displayArea.getComponentCount());
                    System.err.println("["+this.getClass().getName()+"] dock result: "+ dock_result);
                    System.err.println("["+this.getClass().getName()+"] tab pos: " + m_reportTab.getTabPosition());
                    System.err.println("["+this.getClass().getName()+"] isOpened(): " + m_reportTab.isOpened());
                }else{
                    System.err.println("["+this.getClass().getName()+"] default Mode object is null. should not happen");
                }
            }else{
                //this was email, do not display a new tab
                JOptionPane.showMessageDialog(this, "COMTOR sent report email to:\n "+ret.getEmail_from_apikey());
            }
        }
        return;
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * This text field contains an email address and is used to create key
     * generation requests to the COMTOR cloud API.
     *
     */
    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        String userMail = "";
        boolean status = false;
        
        try 
        {
            userMail = jTextField1.getText();
            userMail = URLEncoder.encode(userMail, "UTF-8");
            status = m_client.createAPIKey(userMail);
            if(false==status)
            {
                //jTextField1.setText("");
                JOptionPane.showMessageDialog(this, "key lookup request failed");
            }else{
                JOptionPane.showMessageDialog(this, 
                                              "API key found for email:\n"
                                              +URLDecoder.decode(userMail, "UTF-8"));
                jTextField1.setText("");
            }
         } catch (Exception eee) {
            System.err.println("["+this.getClass().getName()
                                  +"] error w/ email input "
                                  +eee.getMessage());
            return;
        }
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        String key = "";
        
        try{
            key = jTextField2.getText();
            m_client.setKey(key);
            m_APIKey = m_client.getKey();
            jLabel6.setText(key);
            jTextField2.setText("");
            saveConfiguration();
        }catch (Exception ex){
            //ex.printStackTrace();
            System.err.println("["+this.getClass().getName()
                                  +"] error with API key "
                                  +ex.getMessage());
        }
    }//GEN-LAST:event_jTextField2ActionPerformed

    /**
     * Pop up the dialog box.
     */
    private void jButton1ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed1
        jDialog2.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed1

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
