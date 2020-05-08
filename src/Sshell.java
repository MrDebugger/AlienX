
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.swing.ImageIcon;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thealien
 */
public class Sshell extends javax.swing.JFrame {

    /**
     * Creates new form Sshell
     * @param Data
     */
    private static String dData = "{}";
    public Sshell(String url,Proxy proXy,String Data) throws JSONException {
        setTitle("AlienX - Ijaz Ur Rahim");
        try{
            ImageIcon img = new ImageIcon("icon.ico");
            setIconImage(img.getImage());
        }catch(Exception ex){
        }
        Sshell.dData = Data;
        site = url;
        this.proxy = proXy;
        JSONObject result3 = new JSONObject(Data);
        root = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root);
        homeDIR=result3.getString(Integer.toString(result3.length()-2));
        root1 = new DefaultMutableTreeNode("Root1");
        boolean en = false;
        if(result3.getString("0").equalsIgnoreCase(".."))
            {   
                String temDir = "";
                String[] dirs = result3.getString(Integer.toString(result3.length())).split("\\/");
                dirs[dirs.length-1]="";
                backDIR = String.join("/",dirs);
                if(!backDIR.equalsIgnoreCase("/"))
                    en = true;
            }
        try{
        File curDir = new File("files");
        File[] filesList = curDir.listFiles();
        for(File f : filesList)        
            if(f.isFile())
                root1.add(new DefaultMutableTreeNode(f.getName()));
        }catch(NullPointerException e){
            boolean success = (new File("files").mkdirs());
            if(!success){
                
            }
        }
        curDIR=result3.getString(Integer.toString(result3.length()));
        for(int i=1;i<=result3.length()-3;i++)
        {
            try{
            root.add(new DefaultMutableTreeNode(result3.getString(Integer.toString(i))));
            }
            catch(JSONException e)
            {
            }
        }
        initComponents();
        setLocationRelativeTo(null);
        btnBack.setEnabled(true);
        btnDown.setEnabled(false);
        btnUpload.setEnabled(false);
        btnForward.setEnabled(false);
        btnEdit.setEnabled(false);
        btnRename.setEnabled(false);
        btnDelete.setEnabled(false);
        txtField.setEnabled(bol);
        
        
        jTree1.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        jTree2.clearSelection();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        jTree1.getLastSelectedPathComponent();
        if (node == null) return;
        Object nodeInfo = node.getUserObject();
        slctdDIR=nodeInfo.toString();
        btnDown.setEnabled(true);
        btnUpload.setEnabled(false);
        btnEdit.setEnabled(true);
        btnNew.setEnabled(true);
        btnRename.setEnabled(true);
        btnDelete.setEnabled(true);
    }
});
        jTree2.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        jTree1.clearSelection();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        jTree2.getLastSelectedPathComponent();
        if (node == null) return;
        Object nodeInfo = node.getUserObject();
        slctdDIR1=nodeInfo.toString();
        btnDown.setEnabled(false);
        btnUpload.setEnabled(true);
        btnEdit.setEnabled(false);
        btnRename.setEnabled(false);
        btnDelete.setEnabled(false);
    }
});
        jTree1.addMouseListener(new MouseAdapter() {
         @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                jTree1.getLastSelectedPathComponent();
                if (node == null) return;
                Object nodeInfo = node.getUserObject();
                try {
                    changeDir(curDIR+nodeInfo.toString()+"/");
                } catch(IOException ex){
                    
                }
                catch(JSONException ex) {
                    System.out.print(ex);
                }
            }
        }
    });
                txtDIR.setText(curDIR);

    }
    
    private void connect(URL url) throws IOException{
        if(proxy!=null)
         conn = (HttpURLConnection) url.openConnection(proxy);
        else
           conn = (HttpURLConnection) url.openConnection(); 
    }
    private void refreshHome()
    {
        try{
            DefaultTreeModel model1 = (DefaultTreeModel)jTree2.getModel();
            DefaultMutableTreeNode root2 = (DefaultMutableTreeNode)model1.getRoot();
            root2.removeAllChildren();
            File curDir = new File("files");
            File[] filesList = curDir.listFiles();
            for(File f : filesList)        
                if(f.isFile())
                    root2.add(new DefaultMutableTreeNode(f.getName()));
            model1.reload();
            }catch(NullPointerException e){
                boolean success = (new File("files").mkdirs());
                if(!success){
                    
                }
        }
    }
    private void changeDir(String dir) throws IOException, JSONException{
        dir = dir.replace(" ", "+");
        URL url = new URL(site+"?path="+dir+"&action=Change");
            connect(url);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            StringBuilder result1;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            result1 = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result1.append(line);
            }
        }
            try{
                JSONObject result = new JSONObject(result1.toString());
                prevDIR = curDIR;
                slctdDIR = "";
                btnUpload.setEnabled(false);
                btnDown.setEnabled(false);
                btnEdit.setEnabled(false);
                btnDelete.setEnabled(false);
                btnRename.setEnabled(false);
            if(!prevDIR.equalsIgnoreCase("")){
                btnForward.setEnabled(true);
            }
            if(result.getString(Integer.toString(result.length())).equals("/")){
                btnBack.setEnabled(false);
            }
            else if(result.getString("0").equalsIgnoreCase(".."))
            {   
                String temDir;
                temDir = "";
                btnBack.setEnabled(true);
                String[] dirs = result.getString(Integer.toString(result.length())).split("\\/");
                try
                {
                    dirs[dirs.length-1]="";
                
                backDIR = String.join("/",dirs);
                }catch(ArrayIndexOutOfBoundsException ex){
                    backDIR = "/";
                }
                
            }
            else{
                btnBack.setEnabled(false);
            }
            curDIR = result.getString(Integer.toString(result.length()));
            homeDIR = result.getString(Integer.toString(result.length()-2));
            txtDIR.setText(curDIR);
            DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
            DefaultMutableTreeNode root3 = (DefaultMutableTreeNode)model.getRoot();
            root3.removeAllChildren();
                for(int i=1;i<=result.length()-3;i++)
            {
                try{
                root3.add(new DefaultMutableTreeNode(result.getString(Integer.toString(i))));
                }
                catch(JSONException | ArrayIndexOutOfBoundsException e)
                {
                }
            }
            model.reload();
            }catch(JSONException ex){
                jInfo.setText("Looks like the Directory is not Writeable");
                new java.util.Timer().schedule( 
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                jInfo.setText("Unable to Download File");
                                refreshHome();
                            }
                        }, 
                        2000 
                    );
            }
    }
    private void searchFile() throws IOException, JSONException{
        String dir = curDIR.replace(" ", "+");
        URL url = new URL(site+"?path="+dir+"&action=Change");
            connect(url);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            StringBuilder result1;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            result1 = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result1.append(line);
            }
        }
            JSONObject result = new JSONObject(result1.toString());
            DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
            DefaultMutableTreeNode root4 = (DefaultMutableTreeNode)model.getRoot();
            root4.removeAllChildren();
                for(int i=1;i<=result.length()-3;i++)
            {
                try{
                    if(result.getString(Integer.toString(i)).contains(txtSearch.getText()))
                        root4.add(new DefaultMutableTreeNode(result.getString(Integer.toString(i))));
                }
                catch(JSONException | ArrayIndexOutOfBoundsException e)
                {
                }
            }
            model.reload();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree(root);
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree2 = new javax.swing.JTree(root1);
        btnUpload = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        btnHome = new javax.swing.JButton();
        jInfo = new javax.swing.JLabel();
        btnNew = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnRename = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        txtField = new javax.swing.JTextField();
        txtDIR = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btnTerminal = new javax.swing.JButton();
        btnBC = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(816, 420));
        setResizable(false);

        jTree1.setToolTipText("Website Files");
        jTree1.setAutoscrolls(true);
        jTree1.setName("Shell Directories"); // NOI18N
        jTree1.setRootVisible(false);
        jTree1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTree1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTree1);

        jTree2.setToolTipText("Computer Files");
        jTree2.setAutoscrolls(true);
        jTree2.setName("Shell Directories"); // NOI18N
        jTree2.setRootVisible(false);
        jScrollPane2.setViewportView(jTree2);

        btnUpload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/back-icon.png"))); // NOI18N
        btnUpload.setToolTipText("Upload");
        btnUpload.setEnabled(false);
        btnUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadActionPerformed(evt);
            }
        });

        btnDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/forward-icon.png"))); // NOI18N
        btnDown.setToolTipText("Download");
        btnDown.setEnabled(false);
        btnDown.setMaximumSize(new java.awt.Dimension(110, 34));
        btnDown.setMinimumSize(new java.awt.Dimension(110, 34));
        btnDown.setPreferredSize(new java.awt.Dimension(110, 34));
        btnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Computer Files");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Website Files");

        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/back-icon.png"))); // NOI18N
        btnBack.setToolTipText("Back");
        btnBack.setBorder(new javax.swing.border.MatteBorder(null));
        btnBack.setEnabled(false);
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/forward-icon.png"))); // NOI18N
        btnForward.setToolTipText("History");
        btnForward.setEnabled(false);
        btnForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForwardActionPerformed(evt);
            }
        });

        btnHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home-icon.png"))); // NOI18N
        btnHome.setToolTipText("Home");
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });

        jInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jInfo.setText("Coded by Ijaz Ur Rahim ( Muhammad Ibraheem )");
        jInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/new-icon.png"))); // NOI18N
        btnNew.setToolTipText("New File");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete-icon.png"))); // NOI18N
        btnDelete.setToolTipText("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnRename.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rename-icon.png"))); // NOI18N
        btnRename.setToolTipText("Rename");
        btnRename.setEnabled(false);
        btnRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRenameActionPerformed(evt);
            }
        });

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edit-icon.png"))); // NOI18N
        btnEdit.setToolTipText("Edit");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        txtField.setEditable(false);
        txtField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtDIR.setToolTipText("DIR Panel");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/forward-icon.png"))); // NOI18N
        jButton1.setToolTipText("Go");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtSearch.setToolTipText("Search Panel");

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/search-icon.png"))); // NOI18N
        btnSearch.setToolTipText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/refresh-icon.png"))); // NOI18N
        jButton4.setToolTipText("Refresh");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        btnTerminal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/terminal-icon.png"))); // NOI18N
        btnTerminal.setText("Terminal");
        btnTerminal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 0, true));
        btnTerminal.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnTerminal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTerminalActionPerformed(evt);
            }
        });

        btnBC.setText("Back Connect");
        btnBC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBCActionPerformed(evt);
            }
        });

        jMenu1.setText("Options");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("New");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Logout");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Contact");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Exit");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(btnDown, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtField, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnUpload, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(btnRename, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(87, 87, 87)))
                                        .addComponent(btnTerminal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnBC, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(96, 96, 96))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnForward, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnHome, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDIR, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
            .addComponent(jInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnForward, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnHome, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtDIR, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtSearch, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSearch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnUpload, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDown, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRename, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnTerminal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addComponent(jInfo)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTree1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTree1MouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jTree1MouseClicked

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        try {
            // TODO add your handling code here:
            if(!backDIR.equalsIgnoreCase(""))
                changeDir(backDIR);
        } catch (IOException | JSONException ex) {
        }
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            if(!prevDIR.equalsIgnoreCase("")){
                changeDir(prevDIR);

            }
        } catch (IOException | JSONException ex) {
        }
    }//GEN-LAST:event_btnForwardActionPerformed

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        try {
            // TODO add your handling code here:
            changeDir(homeDIR);
        } catch (IOException | JSONException ex) {
        }
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownActionPerformed
        // TODO add your handling code here:
       
                try {
                    String file = slctdDIR.replace(" ", "+");
                    URL url = new URL(site+"?path="+curDIR+file+"&action=Download");
                    connect(url);
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    StringBuilder result1;
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        result1 = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            result1.append(line).append("\n");
                        }
                    }
                    FileWriter fstream = new FileWriter("files/"+slctdDIR);
                    try (BufferedWriter out = new BufferedWriter(fstream)) {
                        out.write(result1.toString());
                        out.close();
                    }
                    
                    new java.util.Timer().schedule( 
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                refreshHome();
                            }
                        }, 
                        2000 
                    );
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                    new java.util.Timer().schedule( 
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                jInfo.setText("Unable to Download File");
                                refreshHome();
                            }
                        }, 
                        2000 
                    );
                }
                
        
    }//GEN-LAST:event_btnDownActionPerformed

    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadActionPerformed
        try {
            // TODO add your handling code here:
            upload();
        } catch (IOException | JSONException ex) {
        }
    }//GEN-LAST:event_btnUploadActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        try {
            // TODO add your handling code here:
            String file = slctdDIR.replace(" ", "+");
            URL url = new URL(site+"?path="+curDIR+file+"&action=Delete");
            connect(url);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            if(conn.getContentLength()==0){
                jInfo.setText("File Deleted Successfully");
                changeDir(curDIR);
            }
            else
                jInfo.setText("Failed to Delete File");
            new java.util.Timer().schedule( 
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        jInfo.setText("Coded by Ijaz Ur Rahim ( Muhammad Ibraheem )");
                    }
                },
            2000);  
    } catch (IOException | JSONException ex) {
        }
        
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        if(!bol){
            bol = true;
            txtField.setEditable(bol);
            txtField.setEnabled(bol);
        }
        else
        {
            try{
                bol = false;
                txtField.setEditable(bol);
                txtField.setEnabled(bol);
                String dir = txtField.getText().replace(" ", "+");
                URL url = new URL(site+"?path="+curDIR+dir+"&action=New");
                connect(url);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                if(conn.getContentLength()==0){
                    jInfo.setText("File Created Successfully");
                }
                else
                    jInfo.setText("Failed to Create File");
                new java.util.Timer().schedule( 
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            try{
                            changeDir(curDIR);
                            }catch(JSONException | IOException ex){
                            }
                            jInfo.setText("Coded by Ijaz Ur Rahim ( Muhammad Ibraheem )");
                            txtField.setText("");
                        }
                    },
                2000);  
        } catch (IOException ex) {
            } 
            
        }
        
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenameActionPerformed
        // TODO add your handling code here:
        if(slctdDIR.equals(""))
            jInfo.setText("Please Select a Web File First");
        else
            if(!bol){
                bol = true;
                txtField.setEditable(bol);
                txtField.setEnabled(bol);
                txtField.setText(slctdDIR);
            }
            else
            {   if(txtField.getText().equals(""))
            jInfo.setText("Please Enter Some Text");
                else
                    try{
                        bol = false;
                        txtField.setEditable(bol);
                        txtField.setEnabled(bol);
                        String dir = txtField.getText().replace(" ", "+");
                        String newDIR = slctdDIR.replace(" ", "+");
                        URL url = new URL(site+"?path="+curDIR+"&action=Rename&new="+dir+"&old="+newDIR);
                        connect(url);
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(10000);
                        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                        if(conn.getContentLength()==0){
                            jInfo.setText("File Renamed Successfully");
                        }
                        else
                            jInfo.setText("Failed to Rename File");
                        new java.util.Timer().schedule( 
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    try{
                                    changeDir(curDIR);
                                    }catch(JSONException | IOException ex){
                                    }
                                    jInfo.setText("Coded by Ijaz Ur Rahim ( Muhammad Ibraheem )");
                                    txtField.setText("");
                                    slctdDIR = "";
                                    jTree1.clearSelection();
                                }
                            },
                        2000);  
                } catch (IOException ex) {
                    } 

            }
    }//GEN-LAST:event_btnRenameActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            String file = slctdDIR.replace(" ", "+");
            URL url = new URL(site+"?path="+curDIR+file+"&action=Read");
            connect(url);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            StringBuilder result1;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                result1 = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    result1.append(line);
                }
            }
                new Editor(site+"?action=Edit&path="+curDIR+file,file,proxy,result1.toString()).setVisible(true);
    } catch (IOException ex) {
        }
        
        
    }//GEN-LAST:event_btnEditActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            // TODO add your handling code here:
            changeDir(txtDIR.getText());
        } catch (IOException | JSONException ex) {
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            // TODO add your handling code here:
            changeDir(curDIR);
        } catch (IOException | JSONException ex) {
        }
    }//GEN-LAST:event_jButton4ActionPerformed
public void close(){
    WindowEvent winClosingEvent = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);
    }
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        close();
        new Shell().setVisible(true);

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        new Shell().setVisible(true);

    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void btnTerminalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTerminalActionPerformed
        // TODO add your handling code here:
        new Terminal(site+"?action=CMD&cmd=",proxy).setVisible(true);
    }//GEN-LAST:event_btnTerminalActionPerformed

    private void btnBCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBCActionPerformed
        // TODO add your handling code here:
        new BackConnect(site,homeDIR,proxy).setVisible(true);
    }//GEN-LAST:event_btnBCActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        try {
            // TODO add your handling code here:
            searchFile();
        } catch (IOException | JSONException ex) {
            jInfo.setText("Something Went Wrong");
            new java.util.Timer().schedule( 
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    try{
                                    changeDir(curDIR);
                                    }catch(JSONException | IOException ex){
                                    }
                                    jInfo.setText("Coded by Ijaz Ur Rahim ( Muhammad Ibraheem )");
                                    txtField.setText("");
                                    slctdDIR = "";
                                    jTree1.clearSelection();
                                }
                            },
                        2000);
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        try {
            // TODO add your handling code here:
            openWebpage(new URL("https://www.facebook.com/muibraheem96").toURI());
            openWebpage(new URL("https://www.facebook.com/MisterDebugger").toURI());


        } catch (MalformedURLException | URISyntaxException ex) {
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenuItem4ActionPerformed
    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (IOException e) {
            }
        }
        return false;
    }
    private void upload() throws IOException, JSONException{

        LineNumberReader lnr = new LineNumberReader (new FileReader ("files/"+slctdDIR1));
        StringBuilder content = new StringBuilder();
        String line1;
        while ((line1 = lnr.readLine ()) != null) {
            content.append(line1).append("\n");
        }
        String file = slctdDIR1.replace(" ", "+");
        URL url = new URL(site+"?path="+curDIR+file+"&action=Upload");
            URLConnection http = null;
            if(proxy!=null)
                http = (URLConnection) url.openConnection(proxy);
            else
                http = (URLConnection) url.openConnection();
            conn = (HttpURLConnection)http;
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setDoOutput(true);
            byte[] out = ("contents="+URLEncoder.encode(content.toString(),"utf-8")).getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            conn.setFixedLengthStreamingMode(length);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.connect();
            try(OutputStream os = conn.getOutputStream()) {
                os.write(out);
            }
            StringBuilder result1;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                result1 = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    result1.append(line).append("\n");
                }
            }
            if(result1.toString().equals("Failed"))
                jInfo.setText("Failed to Upload File");
            else{
                jInfo.setText("File Uploaded Successfully");
                changeDir(curDIR);
            }
            new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    jInfo.setText("Coded by Ijaz Ur Rahim ( Muhammad Ibraheem )");
                }
            },
              2000);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Sshell("",null,"").setVisible(true);
                } catch (JSONException ex) {
                }
            }
        });
    }
    private HttpURLConnection conn;
    private String curDIR;
    private Proxy proxy;
    private String backDIR = "";
    private String refDIR;
    private String homeDIR;
    private String slctdDIR = "";
    private String site="";
    private String prevDIR = "";
    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode root1;
    private DefaultTreeModel treeModel;
    private String slctdDIR1 = "";
    private boolean bol = false;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBC;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnRename;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnTerminal;
    private javax.swing.JButton btnUpload;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jInfo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTree jTree1;
    private javax.swing.JTree jTree2;
    private javax.swing.JTextField txtDIR;
    private javax.swing.JTextField txtField;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
