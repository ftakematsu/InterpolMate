/*
 * InterpolMateView.java
 */
package interpolmate;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Integer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.*;
import java.io.*;
import javax.swing.ButtonGroup;
import metodos_numericos.Splines;

/**
 * O mainframe da aplicacao.
 */
public class InterpolMateView extends FrameView {

    MTGbase MTGbaseInicial = new MTGbase(true); //cria um objeto MTGbase p/ armazenar informacoes do mtg base inicial
    MTGbase MTGbaseFinal = new MTGbase(false); //cria um objeto MTGbase p/ armazenar informacoes do mtg base final

    static ArrayList<MTGaSerGerado> ListaMTGsAGerar = new ArrayList<MTGaSerGerado>(); //cria uma lista para os MTGs que serao gerados.

    int progresso_da_barra=0;

    final int PORTUGUES = 0;
    final int INGLES = 1;
    final int FRANCES = 2;

    int INTERNACIONALIZACAO = PORTUGUES;

    final static char ASPAS = (char)34; //constante char p/ representar aspas dupla (")
    final char BARRAINVERTIDA = (char)92; //constante char p/ representar barra invertida (\)

    static Periodo periodo = new Periodo();

    Grafico GraficoCompGalhos = new Grafico("COMPGALHOS");
    Grafico GraficoEmissaoFolhas = new Grafico("EMISSAOFOLHAS");
    Grafico GraficoQuedaFolhas = new Grafico("QUEDAFOLHAS");
    Grafico GraficoAreaFoliar = new Grafico("AREAFOLIAR");

    // Novos gráficos
    Grafico GraficoNumeroMetameros = new Grafico("NUMEROMETAMEROS_");
    Grafico GraficoTamanhoFolhas = new Grafico("TAMANHOMEDIOFOLHAS_");

    Interpolacao Interp;

    ThreadProgressBar thread_progressbar;

    static Locale locale;

    static ResourceBundle colecaomsgs  = ResourceBundle.getBundle("Messages");

    static ResourceBundle colecaomsgsgui  = ResourceBundle.getBundle("MessagesGUI");
    //new ThreadProgressBar().start();



    DefaultTableModel ModeloTabelaMTGsAGerar =  //modelo de tabela p/ mostrar o conjunto de MTGs escolhidos para a exibicao 3D
    new DefaultTableModel(new Object [][] {}, new String [] {colecaomsgs.getString("Nome_do_Arquivo"), colecaomsgs.getString("Dia")})
    {
        Class[] types = new Class [] {java.lang.String.class, java.lang.String.class};
        boolean[] canEdit = new boolean [] { false, false };
        public Class getColumnClass(int columnIndex) { return types [columnIndex]; }
        public boolean isCellEditable(int rowIndex, int columnIndex) { return canEdit [columnIndex]; }
    };

    DefaultTableModel ModeloTabelaMTGsExistentes = //modelo de tabela p/ mostrar todos os MTGs existentes apos a interpolacao (incluindo tambem os 2 estagios base)
    new DefaultTableModel(new Object [][] {}, new String [] {colecaomsgs.getString("Nome_do_Arquivo"), colecaomsgs.getString("Dia")})
    {
        Class[] types = new Class [] {java.lang.String.class, java.lang.String.class};
        boolean[] canEdit = new boolean [] { false, false };
        public Class getColumnClass(int columnIndex) { return types [columnIndex]; }
        public boolean isCellEditable(int rowIndex, int columnIndex) { return canEdit [columnIndex]; }
    };

    DefaultTableModel ModeloTabelaConjuntoPara3D =  //modelo de tabela p/ mostrar o conjunto de MTGs escolhidos para a exibicao 3D
    new DefaultTableModel(new Object [][] {}, new String [] {colecaomsgs.getString("Nome_do_Arquivo"), colecaomsgs.getString("Dia")})
    {
        Class[] types = new Class [] {java.lang.String.class, java.lang.String.class};
        boolean[] canEdit = new boolean [] { false, false };
        public Class getColumnClass(int columnIndex) { return types [columnIndex]; }
        public boolean isCellEditable(int rowIndex, int columnIndex) { return canEdit [columnIndex]; }
    };
    
    //renderizador de tabela para deixar celulas centralizadas:
    DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
    // centralizado.setHorizontalAlignment(SwingConstants.CENTER);
    //seta celulas da segunda coluna (coluna dos dias da tabela como "centralizado":
    //jTableMTGsAGerar.getColumnModelo().getColumn(1).setCellRenderer(centralizado);

    public InterpolMateView(SingleFrameApplication app) throws IOException {
        super(app);

        initComponents();
        atualizarTextoComponentes();

        //PainelAbasGraficos.setSelectedIndex(0);
        PainelComAba.setEnabledAt(1, false);
        PainelComAba.setEnabledAt(2, false);
        PainelComAba.setEnabledAt(3, false);
        PainelComAba.setEnabledAt(4, false);
        PainelComAba.setSelectedIndex(0);

        //verifica se ja existe o arquivo config.ini (se nao existe, esta rodando o programa pela primeira vez):
        File config_ini = new File("config.ini");
        if (config_ini.exists() == false) //se nao existir:
        {
           GeradorConfigIni gci = new GeradorConfigIni(); //gera o arquivo, verificando se o amapmod esta instalado no sistema
           gci.executar();
        }

        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }

        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });

        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);
        progressBar.setMaximum(100);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(false);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = InterpolMate.getApplication().getMainFrame();
            aboutBox = new JanelaSobre(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        InterpolMate.getApplication().show(aboutBox);
    }

    @SuppressWarnings("unchecked")
    
    private void initComponents() {

        /** PAINEL INICIAL **/
        PainelPrincipal = new javax.swing.JPanel();
        PainelComAba = new javax.swing.JTabbedPane();
        jPainel1 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jTextFieldEstInicial = new javax.swing.JTextField();
        jButtonBuscarMTGInicial = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jButtonVisualizarNoAMAPmod1 = new javax.swing.JButton();
        jButtonAplicarMTGsBase = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jTextFieldEstFinal = new javax.swing.JTextField();
        jButtonBuscarMTGFinal = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jButtonVisualizarNoAMAPmod2 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();

        /** PAINEL DA DEFINIÇÃO DOS MTGs A SEREM GERADOS **/
        jPainel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jButtonInserirMTG = new javax.swing.JButton();
        jButtonAlterarMTG = new javax.swing.JButton();
        jButtonExcluirMTG = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMTGsAGerar = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jTextFieldDiretorio = new javax.swing.JTextField();
        jButtonBuscarDiretorio = new javax.swing.JButton();
        jButtonAplicarMTGsAGerar = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabe15 = new javax.swing.JLabel();
        jLabe16 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabelDiferencaDias = new javax.swing.JLabel();
        jLabelDataFinal = new javax.swing.JLabel();
        jLabelDataInicial = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabelAlometria = new javax.swing.JLabel();

        
        /**** PAINEL DE GRÁFICOS ****/
        jPainel3 = new javax.swing.JPanel();
        jLabelMonitorGrafico = new javax.swing.JLabel();
        PainelAbasGraficos = new javax.swing.JTabbedPane(); // Painel geral

       
        /* Demais itens */
        jCheckBoxMostrarDiasReq = new javax.swing.JCheckBox(); // Caixa de seleção
        jScrollPane6 = new javax.swing.JScrollPane();
        jListaGraficosExistentes = new javax.swing.JList();
        jButtonAbrirGraficoRapido = new javax.swing.JButton();
        jLabelAvisoAbrirGraficos = new javax.swing.JLabel();
        
        jButtonProcessarInterpolacao = new javax.swing.JButton(); // Botão Proccess >>
        jSpinner = new javax.swing.JSpinner();
        jLabelYmax = new javax.swing.JLabel();
        /** Fim dos painéis de gráficos **/


        /** PAINEL DO LOG **/
        jPainel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaLog = new javax.swing.JTextArea();
        jButtonSalvarRelatorio = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jComboBoxLogGalhos = new javax.swing.JComboBox();
        jComboBoxLogEstagios = new javax.swing.JComboBox();
        jCheckBoxFiltroTotFolhas = new javax.swing.JCheckBox();
        jCheckBoxFiltroFolhCaid = new javax.swing.JCheckBox();
        jCheckBoxFiltroFolhSurg = new javax.swing.JCheckBox();
        jCheckBoxFIltroLArFolTot = new javax.swing.JCheckBox();
        jCheckBoxFiltroGanArFol = new javax.swing.JCheckBox();
        jCheckBoxFiltroCompGal = new javax.swing.JCheckBox();
        jCheckBoxAlongGal = new javax.swing.JCheckBox();
        jCheckBoxFiltroTotRam = new javax.swing.JCheckBox();
        jCheckBoxFiltroRamSurg = new javax.swing.JCheckBox();
        jCheckBoxFiltroENSurg = new javax.swing.JCheckBox();
        jCheckBoxFiltroTotEN = new javax.swing.JCheckBox();


        /** PAINEL DA EXIBIÇÃO 3D **/
        jPainel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableMTGsExistentes = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableConjuntoPara3D = new javax.swing.JTable();
        jButtonInserirConj3D = new javax.swing.JButton();
        jButtonRemoverConj3D = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jCheckBoxIncluirEstBases = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jButtonVisualizarConj3D = new javax.swing.JButton();
        jRadioButtonSomenteGalhos = new javax.swing.JRadioButton();
        jRadioButtonSomenteFolhas = new javax.swing.JRadioButton();
        jRadioButtonPlantaInteira = new javax.swing.JRadioButton();

        BarraDeMenus = new javax.swing.JMenuBar();
        javax.swing.JMenu MenuArquivo = new javax.swing.JMenu();
        jMenuItemNovo = new javax.swing.JMenuItem();
        jMenuItemIntegrar = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        javax.swing.JMenuItem jMenuItemSair = new javax.swing.JMenuItem();
        MenuLingua = new javax.swing.JMenu();
        MenuItemPortugues = new javax.swing.JCheckBoxMenuItem();
        MenuItemIngles = new javax.swing.JCheckBoxMenuItem();
        MenuItemFrances = new javax.swing.JCheckBoxMenuItem();
        javax.swing.JMenu MenuAjuda = new javax.swing.JMenu();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem MenuItemSobre = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jDialogInserirEstagio = new javax.swing.JDialog();
        jButtonAplicarInsercaoEstagio = new javax.swing.JButton();
        jButtonCancelarInsercaoEstagio = new javax.swing.JButton();
        jPanelInsercaoEstagio = new javax.swing.JPanel();
        Dia = new javax.swing.JLabel();
        jTextFieldNomeEstagio = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldDiaEstagio = new javax.swing.JTextField();
        jCheckBoxNomenclaturaDia = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabe10 = new javax.swing.JLabel();
        jLabe9 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabelDiferencaDias1 = new javax.swing.JLabel();
        jLabelDataInicial1 = new javax.swing.JLabel();
        jLabelDataFinal1 = new javax.swing.JLabel();
        jDialogAlterarEstagio = new javax.swing.JDialog();
        jButtonCancelarAlterarEstagio = new javax.swing.JButton();
        jButtonAplicarAlteracaoEstagio = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabe11 = new javax.swing.JLabel();
        jLabe12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabelDiferencaDias2 = new javax.swing.JLabel();
        jLabelDataFinal2 = new javax.swing.JLabel();
        jLabelDataInicial2 = new javax.swing.JLabel();
        jPanelInsercaoEstagio1 = new javax.swing.JPanel();
        jTextFieldNomeEstagioAAlterar = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jCheckBoxNomenclaturaDiaB = new javax.swing.JCheckBox();
        jTextFieldDiaEstagioAAlterar = new javax.swing.JTextField();
        Dia1 = new javax.swing.JLabel();
        buttonGroupInternacionalizacao = new javax.swing.ButtonGroup();
        jDialogBarraDeProgresso = new javax.swing.JDialog();
        jProgressBar1 = new javax.swing.JProgressBar();
        jDialogIntegracaoAMAPmod = new javax.swing.JDialog();
        jCheckBoxAtivacaoAMAPmod = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jButtonBuscarAMAPmod = new javax.swing.JButton();
        jTextFieldCaminhoAMAPmod = new javax.swing.JTextField();
        jButtonAplicarAMAPmod = new javax.swing.JButton();
        jButtonCancJanelaIntAMAPmod = new javax.swing.JButton();
        jLabelIntegracao = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();


        /* EDIÇÃO DO PAINEL PRINCIPAL */
        PainelPrincipal.setMaximumSize(new java.awt.Dimension(600, 700));
        PainelPrincipal.setMinimumSize(new java.awt.Dimension(600, 700));
        PainelPrincipal.setName("PainelPrincipal"); // NOI18N
        PainelPrincipal.setPreferredSize(new java.awt.Dimension(600, 700));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(interpolmate.InterpolMate.class).getContext().getResourceMap(InterpolMateView.class);
        PainelComAba.setBackground(resourceMap.getColor("PainelComAba.background")); // NOI18N
        PainelComAba.setName("PainelComAba"); // NOI18N
        
        PainelComAba.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PainelComAbaMouseClicked(evt);
            }
        });

        
        PainelComAba.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                PainelComAbaComponentShown(evt);
            }
        });

        jPainel1.setName("jPainel1"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("null"));
        jPanel1.setToolTipText(resourceMap.getString("jPainelEstagioInicial.toolTipText")); // NOI18N
        jPanel1.setName("jPainelEstagioInicial"); // NOI18N

        jTextFieldEstInicial.setText(resourceMap.getString("jTextFieldEstInicial.text")); // NOI18N
        jTextFieldEstInicial.setToolTipText("null");
        jTextFieldEstInicial.setName("jTextFieldEstInicial"); // NOI18N
        jTextFieldEstInicial.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextFieldEstInicialCaretUpdate(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("MessagesGUI"); // NOI18N
        jButtonBuscarMTGInicial.setText(bundle.getString("BOTAO1")); // NOI18N
        jButtonBuscarMTGInicial.setToolTipText("null");
        jButtonBuscarMTGInicial.setName("jButtonBuscarMTGInicial"); // NOI18N
        jButtonBuscarMTGInicial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBuscarMTGInicialActionPerformed(evt);
            }
        });

        jLabel5.setText("null");
        jLabel5.setName("jLabel5"); // NOI18N

        jButtonVisualizarNoAMAPmod1.setText("null");
        jButtonVisualizarNoAMAPmod1.setToolTipText("null");
        jButtonVisualizarNoAMAPmod1.setEnabled(false);
        jButtonVisualizarNoAMAPmod1.setName("jButtonVisualizarNoAMAPmod1"); // NOI18N
        jButtonVisualizarNoAMAPmod1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVisualizarNoAMAPmod1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldEstInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonBuscarMTGInicial)
                .addGap(99, 99, 99))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(341, Short.MAX_VALUE)
                .addComponent(jButtonVisualizarNoAMAPmod1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jButtonBuscarMTGInicial)
                    .addComponent(jTextFieldEstInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jButtonVisualizarNoAMAPmod1)
                .addContainerGap())
        );

        jButtonAplicarMTGsBase.setText("null");
        jButtonAplicarMTGsBase.setName("jButtonAplicarMTGsBase"); // NOI18N
        jButtonAplicarMTGsBase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCarregarMTGsBase(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("null"));
        jPanel2.setToolTipText(resourceMap.getString("jPanel2.toolTipText")); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jTextFieldEstFinal.setToolTipText("null");
        jTextFieldEstFinal.setName("jTextFieldEstFinal"); // NOI18N
        jTextFieldEstFinal.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextFieldEstFinalCaretUpdate(evt);
            }
        });

        jButtonBuscarMTGFinal.setText("null");
        jButtonBuscarMTGFinal.setToolTipText("null");
        jButtonBuscarMTGFinal.setName("jButtonBuscarMTGFinal"); // NOI18N
        jButtonBuscarMTGFinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBuscarMTGFinalActionPerformed(evt);
            }
        });

        jLabel7.setText("null");
        jLabel7.setName("jLabel7"); // NOI18N

        jButtonVisualizarNoAMAPmod2.setText("null");
        jButtonVisualizarNoAMAPmod2.setToolTipText("null");
        jButtonVisualizarNoAMAPmod2.setEnabled(false);
        jButtonVisualizarNoAMAPmod2.setName("jButtonVisualizarNoAMAPmod2"); // NOI18N
        jButtonVisualizarNoAMAPmod2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVisualizarNoAMAPmod2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldEstFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonBuscarMTGFinal)
                .addGap(112, 112, 112))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(341, Short.MAX_VALUE)
                .addComponent(jButtonVisualizarNoAMAPmod2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jButtonBuscarMTGFinal)
                    .addComponent(jTextFieldEstFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButtonVisualizarNoAMAPmod2)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jLabel10.setText("null");
        jLabel10.setName("jLabel10"); // NOI18N

        jSeparator4.setName("jSeparator4"); // NOI18N

        javax.swing.GroupLayout jPainel1Layout = new javax.swing.GroupLayout(jPainel1);
        jPainel1.setLayout(jPainel1Layout);
        jPainel1Layout.setHorizontalGroup(
            jPainel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPainel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPainel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(130, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPainel1Layout.createSequentialGroup()
                .addContainerGap(463, Short.MAX_VALUE)
                .addComponent(jButtonAplicarMTGsBase)
                .addContainerGap())
            .addGroup(jPainel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPainel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        jPainel1Layout.setVerticalGroup(
            jPainel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPainel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(jButtonAplicarMTGsBase, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.getAccessibleContext().setAccessibleName(resourceMap.getString("jPanel1.AccessibleContext.accessibleName")); // NOI18N

        PainelComAba.addTab("null", jPainel1);

        jPainel2.setName("jPainel2"); // NOI18N

        jLabel1.setText("null");
        jLabel1.setName("jLabel1"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        jLabel2.setText("null");
        jLabel2.setName("jLabel2"); // NOI18N

        jButtonInserirMTG.setText("null");
        jButtonInserirMTG.setName("jButtonInserirMTG"); // NOI18N
        jButtonInserirMTG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInserirMTGActionPerformed(evt);
            }
        });

        jButtonAlterarMTG.setText("null");
        jButtonAlterarMTG.setName("jButtonAlterarMTG"); // NOI18N
        jButtonAlterarMTG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAlterarMTGActionPerformed(evt);
            }
        });

        jButtonExcluirMTG.setText("null");
        jButtonExcluirMTG.setName("jButtonExcluirMTG"); // NOI18N
        jButtonExcluirMTG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirMTGActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTableMTGsAGerar.setBackground(resourceMap.getColor("jTableMTGsAGerar.background")); // NOI18N
        jTableMTGsAGerar.setModel(ModeloTabelaMTGsAGerar);
        jTableMTGsAGerar.setGridColor(resourceMap.getColor("jTableMTGsAGerar.gridColor")); // NOI18N
        jTableMTGsAGerar.setName("jTableMTGsAGerar"); // NOI18N
        jTableMTGsAGerar.setSelectionBackground(resourceMap.getColor("jTableMTGsAGerar.selectionBackground")); // NOI18N
        jTableMTGsAGerar.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableMTGsAGerar.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTableMTGsAGerar);

        jLabel11.setText("null");
        jLabel11.setName("jLabel11"); // NOI18N

        jTextFieldDiretorio.setText(resourceMap.getString("jTextFieldDiretorio.text")); // NOI18N
        jTextFieldDiretorio.setName("jTextFieldDiretorio"); // NOI18N

        jButtonBuscarDiretorio.setText("null");
        jButtonBuscarDiretorio.setName("jButtonBuscarDiretorio"); // NOI18N
        jButtonBuscarDiretorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBuscarDiretorioActionPerformed(evt);
            }
        });

        jButtonAplicarMTGsAGerar.setText("null");
        jButtonAplicarMTGsAGerar.setName("jButtonAplicarMTGsAGerar"); // NOI18N
        jButtonAplicarMTGsAGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    jButtonAplicarMTGsAGerarActionPerformed(evt);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("null"));
        jPanel7.setName("jPanel7"); // NOI18N

        jLabe15.setText("null");
        jLabe15.setName("jLabe15"); // NOI18N

        jLabe16.setText("null");
        jLabe16.setName("jLabe16"); // NOI18N

        jLabel20.setText("null");
        jLabel20.setName("jLabel20"); // NOI18N

        jLabelDiferencaDias.setText(resourceMap.getString("jLabelDiferencaDias.text")); // NOI18N
        jLabelDiferencaDias.setName("jLabelDiferencaDias"); // NOI18N

        jLabelDataFinal.setText(resourceMap.getString("jLabelDataFinal.text")); // NOI18N
        jLabelDataFinal.setName("jLabelDataFinal"); // NOI18N

        jLabelDataInicial.setText(resourceMap.getString("jLabelDataInicial.text")); // NOI18N
        jLabelDataInicial.setName("jLabelDataInicial"); // NOI18N

        jLabel6.setText(bundle.getString("LABEL100")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabelAlometria.setText(resourceMap.getString("jLabelAlometria.text")); // NOI18N
        jLabelAlometria.setName("jLabelAlometria"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabe16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelDataInicial))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabe15)
                        .addGap(6, 6, 6)
                        .addComponent(jLabelDataFinal))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelDiferencaDias))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelAlometria)))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabe16)
                    .addComponent(jLabelDataInicial))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabe15)
                    .addComponent(jLabelDataFinal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabelDiferencaDias))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabelAlometria)))
        );

        javax.swing.GroupLayout jPainel2Layout = new javax.swing.GroupLayout(jPainel2);
        jPainel2.setLayout(jPainel2Layout);
        jPainel2Layout.setHorizontalGroup(
            jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPainel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPainel2Layout.createSequentialGroup()
                        .addGroup(jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPainel2Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addGroup(jPainel2Layout.createSequentialGroup()
                                        .addGroup(jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPainel2Layout.createSequentialGroup()
                                                .addComponent(jTextFieldDiretorio, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonBuscarDiretorio)
                                                .addGap(51, 51, 51))
                                            .addGroup(jPainel2Layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(31, 31, 31)))
                                        .addGroup(jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jButtonInserirMTG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButtonAlterarMTG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButtonExcluirMTG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(jPainel2Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonAplicarMTGsAGerar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))))
                        .addGap(165, 165, 165))
                    .addGroup(jPainel2Layout.createSequentialGroup()
                        .addGroup(jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
                                .addGroup(jPainel2Layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPainel2Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jLabel2)
                                .addGap(84, 84, 84)))
                        .addGap(81, 81, 81))))
        );
        jPainel2Layout.setVerticalGroup(
            jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPainel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPainel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11))
                    .addGroup(jPainel2Layout.createSequentialGroup()
                        .addComponent(jButtonInserirMTG)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAlterarMTG)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExcluirMTG)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPainel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonBuscarDiretorio)
                    .addComponent(jTextFieldDiretorio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAplicarMTGsAGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(54, Short.MAX_VALUE))
        );

        PainelComAba.addTab("null", jPainel2);

        jPainel3.setName("jPainel3"); // NOI18N
        jPainel3.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPainel3ComponentShown(evt);
            }
        });

        jLabelMonitorGrafico.setText(resourceMap.getString("jLabelMonitorGrafico.text")); // NOI18N
        jLabelMonitorGrafico.setName("jLabelMonitorGrafico"); // NOI18N


        // Painel principal das abas do gráfico
        PainelAbasGraficos.setName("PainelAbasGraficos"); // NOI18N

        // ************** Painel 1 - Comprimento dos galhos
     
/**********************************************/



        jCheckBoxMostrarDiasReq.setText("null");
        jCheckBoxMostrarDiasReq.setName("jCheckBoxMostrarDiasReq"); // NOI18N
        jCheckBoxMostrarDiasReq.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMostrarDiasReqItemStateChanged(evt);
            }
        });

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        jListaGraficosExistentes.setName("jListaGraficosExistentes"); // NOI18N
        jScrollPane6.setViewportView(jListaGraficosExistentes);

        jButtonAbrirGraficoRapido.setText("");
        jButtonAbrirGraficoRapido.setName("jButtonAbrirGraficoRapido"); // NOI18N
        jButtonAbrirGraficoRapido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirGraficoRapidoActionPerformed(evt);
            }
        });

        jLabelAvisoAbrirGraficos.setText("null");
        jLabelAvisoAbrirGraficos.setName("jLabelAvisoAbrirGraficos"); // NOI18N

        jButtonProcessarInterpolacao.setText("null");
        jButtonProcessarInterpolacao.setName("jButtonProcessarInterpolacao"); // NOI18N
        jButtonProcessarInterpolacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProcessarInterpolacao(evt);
            }
        });
        
        
        jSpinner.setName("jSpinner"); // NOI18N
        jSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerStateChanged(evt);
            }
        });

        jLabelYmax.setText(resourceMap.getString("jLabelYmax.text")); // NOI18N
        jLabelYmax.setName("jLabelYmax"); // NOI18N
        
        /*javax.swing.GroupLayout jPainel3Layout = new javax.swing.GroupLayout(jPainel3);
        jPainel3Layout.addLayoutComponent(jButtonProcessarInterpolacao, centralizado);*/
       
        javax.swing.JLabel label_proc = new javax.swing.JLabel();
        label_proc.setText("Clique no botão para processar a interpolação");
        jPainel3.add(jButtonProcessarInterpolacao);

        PainelComAba.addTab("null", jPainel3);

        jPainel4.setName("jPainel4"); // NOI18N
        jPainel4.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPainel4ComponentShown(evt);
            }
        });

        jScrollPane4.setName("jScrollPane4"); // NOI18N
        
        jTextAreaLog.append("\n*** LOG ***\n");
        
        jTextAreaLog.setColumns(20);
        jTextAreaLog.setEditable(false);
        jTextAreaLog.setFont(resourceMap.getFont("jTextAreaLog.font")); // NOI18N
        jTextAreaLog.setRows(5);
        jTextAreaLog.setAutoscrolls(false);
        jTextAreaLog.setName("jTextAreaLog"); // NOI18N
        jScrollPane4.setViewportView(jTextAreaLog);

        jButtonSalvarRelatorio.setText("null");
        jButtonSalvarRelatorio.setName("jButtonSalvarRelatorio"); // NOI18N
        jButtonSalvarRelatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalvarRelatorioActionPerformed(evt);
            }
        });

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("null"));
        jPanel8.setName("jPanel8"); // NOI18N

        jComboBoxLogGalhos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nenhum Galho", "Todos os Galhos" }));
        jComboBoxLogGalhos.setName("jComboBoxLogGalhos"); // NOI18N
        jComboBoxLogGalhos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxLogGalhosActionPerformed(evt);
            }
        });

        jComboBoxLogEstagios.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Todos os Estágios" }));
        jComboBoxLogEstagios.setName("jComboBoxLogEstagios"); // NOI18N
        jComboBoxLogEstagios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxLogEstagiosActionPerformed(evt);
            }
        });

        jCheckBoxFiltroTotFolhas.setSelected(true);
        jCheckBoxFiltroTotFolhas.setText("null");
        jCheckBoxFiltroTotFolhas.setName("jCheckBoxFiltroTotFolhas"); // NOI18N
        jCheckBoxFiltroTotFolhas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFiltroTotFolhasActionPerformed(evt);
            }
        });

        jCheckBoxFiltroFolhCaid.setSelected(true);
        jCheckBoxFiltroFolhCaid.setText("null");
        jCheckBoxFiltroFolhCaid.setName("jCheckBoxFiltroFolhCaid"); // NOI18N
        jCheckBoxFiltroFolhCaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFiltroFolhCaidActionPerformed(evt);
            }
        });

        jCheckBoxFiltroFolhSurg.setSelected(true);
        jCheckBoxFiltroFolhSurg.setText("null");
        jCheckBoxFiltroFolhSurg.setName("jCheckBoxFiltroFolhSurg"); // NOI18N
        jCheckBoxFiltroFolhSurg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFiltroFolhSurgActionPerformed(evt);
            }
        });

        jCheckBoxFIltroLArFolTot.setSelected(true);
        jCheckBoxFIltroLArFolTot.setText("null");
        jCheckBoxFIltroLArFolTot.setName("jCheckBoxFIltroLArFolTot"); // NOI18N
        jCheckBoxFIltroLArFolTot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFIltroLArFolTotActionPerformed(evt);
            }
        });

        jCheckBoxFiltroGanArFol.setSelected(true);
        jCheckBoxFiltroGanArFol.setText("null");
        jCheckBoxFiltroGanArFol.setName("jCheckBoxFiltroGanArFol"); // NOI18N
        jCheckBoxFiltroGanArFol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFiltroGanArFolActionPerformed(evt);
            }
        });

        jCheckBoxFiltroCompGal.setSelected(true);
        jCheckBoxFiltroCompGal.setText("null");
        jCheckBoxFiltroCompGal.setName("jCheckBoxFiltroCompGal"); // NOI18N
        jCheckBoxFiltroCompGal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFiltroCompGalActionPerformed(evt);
            }
        });

        jCheckBoxAlongGal.setSelected(true);
        jCheckBoxAlongGal.setText("null");
        jCheckBoxAlongGal.setName("jCheckBoxAlongGal"); // NOI18N
        jCheckBoxAlongGal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAlongGalActionPerformed(evt);
            }
        });

        jCheckBoxFiltroTotRam.setSelected(true);
        jCheckBoxFiltroTotRam.setText("null");
        jCheckBoxFiltroTotRam.setName("jCheckBoxFiltroTotRam"); // NOI18N
        jCheckBoxFiltroTotRam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFiltroTotRamActionPerformed(evt);
            }
        });

        jCheckBoxFiltroRamSurg.setSelected(true);
        jCheckBoxFiltroRamSurg.setText("null");
        jCheckBoxFiltroRamSurg.setName("jCheckBoxFiltroRamSurg"); // NOI18N
        jCheckBoxFiltroRamSurg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFiltroRamSurgActionPerformed(evt);
            }
        });

        jCheckBoxFiltroENSurg.setSelected(true);
        jCheckBoxFiltroENSurg.setText("null");
        jCheckBoxFiltroENSurg.setName("jCheckBoxFiltroENSurg"); // NOI18N
        jCheckBoxFiltroENSurg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFiltroENSurgActionPerformed(evt);
            }
        });

        jCheckBoxFiltroTotEN.setSelected(true);
        jCheckBoxFiltroTotEN.setText("null");
        jCheckBoxFiltroTotEN.setName("jCheckBoxFiltroTotEN"); // NOI18N
        jCheckBoxFiltroTotEN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFiltroTotENActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxLogEstagios, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxLogGalhos, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxFiltroFolhCaid)
                    .addComponent(jCheckBoxFiltroFolhSurg)
                    .addComponent(jCheckBoxFiltroTotFolhas)
                    .addComponent(jCheckBoxFIltroLArFolTot))
                .addGap(12, 12, 12)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxFiltroTotRam)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxFiltroCompGal)
                            .addComponent(jCheckBoxAlongGal)
                            .addComponent(jCheckBoxFiltroGanArFol))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxFiltroRamSurg)
                            .addComponent(jCheckBoxFiltroENSurg)
                            .addComponent(jCheckBoxFiltroTotEN))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBoxFiltroTotFolhas)
                            .addComponent(jComboBoxLogEstagios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addComponent(jComboBoxLogGalhos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBoxFiltroGanArFol)
                            .addComponent(jCheckBoxFiltroRamSurg))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBoxFiltroCompGal)
                            .addComponent(jCheckBoxFiltroFolhSurg)
                            .addComponent(jCheckBoxFiltroTotEN))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBoxAlongGal)
                            .addComponent(jCheckBoxFiltroFolhCaid)
                            .addComponent(jCheckBoxFiltroENSurg))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBoxFiltroTotRam)
                            .addComponent(jCheckBoxFIltroLArFolTot))))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPainel4Layout = new javax.swing.GroupLayout(jPainel4);
        jPainel4.setLayout(jPainel4Layout);
        jPainel4Layout.setHorizontalGroup(
            jPainel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPainel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPainel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPainel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonSalvarRelatorio, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addContainerGap())
        );
        jPainel4Layout.setVerticalGroup(
            jPainel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPainel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSalvarRelatorio)
                .addGap(122, 122, 122))
        );

        jPanel8.getAccessibleContext().setAccessibleName("null");

        PainelComAba.addTab("null", jPainel4);

        jPainel5.setName("jPainel5"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTableMTGsExistentes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Nome do Arquivo", "Dia"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableMTGsExistentes.setGridColor(resourceMap.getColor("jTableMTGsExistentes.gridColor")); // NOI18N
        jTableMTGsExistentes.setName("jTableMTGsExistentes"); // NOI18N
        jTableMTGsExistentes.setSelectionBackground(resourceMap.getColor("jTableMTGsExistentes.selectionBackground")); // NOI18N
        jTableMTGsExistentes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableMTGsExistentes.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTableMTGsExistentes);
        jTableMTGsExistentes.getColumnModel().getColumn(0).setResizable(false);
        jTableMTGsExistentes.getColumnModel().getColumn(0).setPreferredWidth(120);
        jTableMTGsExistentes.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTableMTGsExistentes.columnModel.title0")); // NOI18N
        jTableMTGsExistentes.getColumnModel().getColumn(1).setResizable(false);
        jTableMTGsExistentes.getColumnModel().getColumn(1).setPreferredWidth(1);
        jTableMTGsExistentes.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTableMTGsExistentes.columnModel.title1")); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTableConjuntoPara3D.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Nome do Arquivo", "Dia"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableConjuntoPara3D.setGridColor(resourceMap.getColor("jTableConjuntoPara3D.gridColor")); // NOI18N
        jTableConjuntoPara3D.setName("jTableConjuntoPara3D"); // NOI18N
        jTableConjuntoPara3D.setSelectionBackground(resourceMap.getColor("jTableConjuntoPara3D.selectionBackground")); // NOI18N
        jTableConjuntoPara3D.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableConjuntoPara3D.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jTableConjuntoPara3D);
        jTableConjuntoPara3D.getColumnModel().getColumn(0).setResizable(false);
        jTableConjuntoPara3D.getColumnModel().getColumn(0).setPreferredWidth(120);
        jTableConjuntoPara3D.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTableConjuntoPara3D.columnModel.title0")); // NOI18N
        jTableConjuntoPara3D.getColumnModel().getColumn(1).setResizable(false);
        jTableConjuntoPara3D.getColumnModel().getColumn(1).setPreferredWidth(1);
        jTableConjuntoPara3D.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTableConjuntoPara3D.columnModel.title1")); // NOI18N

        jButtonInserirConj3D.setText(resourceMap.getString("jButtonInserirConj3D.text")); // NOI18N
        jButtonInserirConj3D.setName("jButtonInserirConj3D"); // NOI18N
        jButtonInserirConj3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInserirConj3DActionPerformed(evt);
            }
        });

        jButtonRemoverConj3D.setText(resourceMap.getString("jButtonRemoverConj3D.text")); // NOI18N
        jButtonRemoverConj3D.setName("jButtonRemoverConj3D"); // NOI18N
        jButtonRemoverConj3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoverConj3DActionPerformed(evt);
            }
        });

        jLabel4.setText("null");
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel12.setText("null");
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel13.setText("null");
        jLabel13.setName("jLabel13"); // NOI18N

        jSeparator5.setName("jSeparator5"); // NOI18N

        jCheckBoxIncluirEstBases.setSelected(true);
        jCheckBoxIncluirEstBases.setText("null");
        jCheckBoxIncluirEstBases.setName("jCheckBoxIncluirEstBases"); // NOI18N
        jCheckBoxIncluirEstBases.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxIncluirEstBasesItemStateChanged(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("null"));
        jPanel3.setName("jPanel3"); // NOI18N

        jButtonVisualizarConj3D.setText("null");
        jButtonVisualizarConj3D.setToolTipText("null");
        jButtonVisualizarConj3D.setEnabled(false);
        jButtonVisualizarConj3D.setName("jButtonVisualizarConj3D"); // NOI18N
        jButtonVisualizarConj3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVisualizarConj3DActionPerformed(evt);
            }
        });

        jRadioButtonPlantaInteira.setSelected(true);
        jRadioButtonPlantaInteira.setText("null");
        jRadioButtonPlantaInteira.setName("jRadioButtonPlantaInteira"); // NOI18N

        jRadioButtonSomenteGalhos.setSelected(false);
        jRadioButtonSomenteGalhos.setText("null");
        jRadioButtonSomenteGalhos.setName("jRadioButtonSomenteGalhos"); // NOI18N

        jRadioButtonSomenteFolhas.setSelected(false);
        jRadioButtonSomenteFolhas.setText("null");
        jRadioButtonSomenteFolhas.setName("jRadioButtonSomenteFolhas");

        

        javax.swing.ButtonGroup grupo_botao = new ButtonGroup();
        grupo_botao.add(jRadioButtonPlantaInteira);
        grupo_botao.add(jRadioButtonSomenteGalhos);
        grupo_botao.add(jRadioButtonSomenteFolhas);

        jPanel3.setLayout(new GridLayout(4,1));
        jPanel3.add(jButtonVisualizarConj3D);
        jPanel3.add(jRadioButtonPlantaInteira);
        jPanel3.add(jRadioButtonSomenteGalhos);
        jPanel3.add(jRadioButtonSomenteFolhas);

        /*
        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jRadioButtonPlantaInteira)
                        .addComponent(jRadioButtonSomenteGalhos).
                        addComponent(jRadioButtonSomenteFolhas))
                    .addComponent(jButtonVisualizarConj3D))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonVisualizarConj3D)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonPlantaInteira)
                .addComponent(jRadioButtonSomenteGalhos)
                .addComponent(jRadioButtonSomenteFolhas)
                .addContainerGap(38, Short.MAX_VALUE))
        );*/

        javax.swing.GroupLayout jPainel5Layout = new javax.swing.GroupLayout(jPainel5);
        jPainel5.setLayout(jPainel5Layout);
        jPainel5Layout.setHorizontalGroup(
            jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPainel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPainel5Layout.createSequentialGroup()
                        .addGroup(jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPainel5Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addGroup(jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButtonInserirConj3D)
                                    .addComponent(jButtonRemoverConj3D)))
                            .addComponent(jCheckBoxIncluirEstBases))
                        .addGroup(jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPainel5Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                                    .addComponent(jLabel12)))
                            .addGroup(jPainel5Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPainel5Layout.setVerticalGroup(
            jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPainel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addGroup(jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPainel5Layout.createSequentialGroup()
                        .addGap(76, 76, 76)
                        .addComponent(jButtonInserirConj3D, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRemoverConj3D, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPainel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxIncluirEstBases)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        PainelComAba.addTab("null", jPainel5);

        javax.swing.GroupLayout PainelPrincipalLayout = new javax.swing.GroupLayout(PainelPrincipal);
        PainelPrincipal.setLayout(PainelPrincipalLayout);
        PainelPrincipalLayout.setHorizontalGroup(
            PainelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PainelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PainelComAba, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        PainelPrincipalLayout.setVerticalGroup(
            PainelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PainelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PainelComAba, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(118, Short.MAX_VALUE))
        );

        BarraDeMenus.setName("BarraDeMenus"); // NOI18N

        MenuArquivo.setText("null");
        MenuArquivo.setName("MenuArquivo"); // NOI18N

        jMenuItemNovo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNovo.setText("null");
        jMenuItemNovo.setName("jMenuItemNovo"); // NOI18N
        jMenuItemNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNovoActionPerformed(evt);
            }
        });
        MenuArquivo.add(jMenuItemNovo);

        jMenuItemIntegrar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemIntegrar.setText("null");
        jMenuItemIntegrar.setName("jMenuItemIntegrar"); // NOI18N
        jMenuItemIntegrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemIntegrarActionPerformed(evt);
            }
        });
        MenuArquivo.add(jMenuItemIntegrar);

        jSeparator3.setName("jSeparator3"); // NOI18N
        MenuArquivo.add(jSeparator3);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(interpolmate.InterpolMate.class).getContext().getActionMap(InterpolMateView.class, this);
        jMenuItemSair.setAction(actionMap.get("quit")); // NOI18N
        jMenuItemSair.setText("null");
        jMenuItemSair.setToolTipText("null");
        jMenuItemSair.setName("menuItemSair"); // NOI18N
        MenuArquivo.add(jMenuItemSair);

        BarraDeMenus.add(MenuArquivo);

        MenuLingua.setText("null");
        MenuLingua.setName("MenuLingua"); // NOI18N

        MenuItemPortugues.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK));
        buttonGroupInternacionalizacao.add(MenuItemPortugues);
        MenuItemPortugues.setSelected(true);
        MenuItemPortugues.setText(resourceMap.getString("MenuItemPortugues.text")); // NOI18N
        MenuItemPortugues.setName("MenuItemPortugues"); // NOI18N
        MenuItemPortugues.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                MenuItemPortuguesItemStateChanged(evt);
            }
        });
        MenuLingua.add(MenuItemPortugues);

        MenuItemIngles.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK));
        buttonGroupInternacionalizacao.add(MenuItemIngles);
        MenuItemIngles.setText(resourceMap.getString("MenuItemIngles.text")); // NOI18N
        MenuItemIngles.setName("MenuItemIngles"); // NOI18N
        MenuItemIngles.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                MenuItemInglesItemStateChanged(evt);
            }
        });
        MenuLingua.add(MenuItemIngles);

        MenuItemFrances.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK));
        buttonGroupInternacionalizacao.add(MenuItemFrances);
        MenuItemFrances.setText(resourceMap.getString("MenuItemFrances.text")); // NOI18N
        MenuItemFrances.setName("MenuItemFrances"); // NOI18N
        MenuItemFrances.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                MenuItemFrancesItemStateChanged(evt);
            }
        });
        MenuLingua.add(MenuItemFrances);

        BarraDeMenus.add(MenuLingua);

        MenuAjuda.setText("null");
        MenuAjuda.setName("menuAjuda"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N
        MenuAjuda.add(jSeparator2);

        MenuItemSobre.setAction(actionMap.get("showAboutBox")); // NOI18N
        MenuItemSobre.setText("null");
        MenuItemSobre.setToolTipText(resourceMap.getString("MenuItemSobre.toolTipText")); // NOI18N
        MenuItemSobre.setName("MenuItemSobre"); // NOI18N
        MenuAjuda.add(MenuItemSobre);

        BarraDeMenus.add(MenuAjuda);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        statusAnimationLabel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                statusAnimationLabelMouseWheelMoved(evt);
            }
        });
        statusAnimationLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                statusAnimationLabelMouseClicked(evt);
            }
        });

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 430, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        jDialogInserirEstagio.setTitle(resourceMap.getString("jDialogInserirEstagio.title")); // NOI18N
        jDialogInserirEstagio.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jDialogInserirEstagio.setIconImage(null);
        jDialogInserirEstagio.setMinimumSize(new java.awt.Dimension(300, 475));
        jDialogInserirEstagio.setName("jDialogInserirEstagio"); // NOI18N
        jDialogInserirEstagio.setResizable(false);
        jDialogInserirEstagio.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                jDialogInserirEstagioComponentHidden(evt);
            }
        });

        jButtonAplicarInsercaoEstagio.setText("null");
        jButtonAplicarInsercaoEstagio.setName("jButtonAplicarInsercaoEstagio"); // NOI18N
        jButtonAplicarInsercaoEstagio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAplicarInsercaoEstagioActionPerformed(evt);
            }
        });

        jButtonCancelarInsercaoEstagio.setText("null");
        jButtonCancelarInsercaoEstagio.setName("jButtonCancelarInsercaoEstagio"); // NOI18N
        jButtonCancelarInsercaoEstagio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLimparInsercaoEstagioActionPerformed(evt);
            }
        });

        jPanelInsercaoEstagio.setBorder(javax.swing.BorderFactory.createTitledBorder("null"));
        jPanelInsercaoEstagio.setName("jPanelInsercaoEstagio"); // NOI18N

        Dia.setText("null");
        Dia.setName("Dia"); // NOI18N

        jTextFieldNomeEstagio.setText(resourceMap.getString("jTextFieldNomeEstagio.text")); // NOI18N
        jTextFieldNomeEstagio.setName("jTextFieldNomeEstagio"); // NOI18N

        jLabel3.setText("null");
        jLabel3.setName("jLabel3"); // NOI18N

        jTextFieldDiaEstagio.setText(resourceMap.getString("jTextFieldDiaEstagio.text")); // NOI18N
        jTextFieldDiaEstagio.setName("jTextFieldDiaEstagio"); // NOI18N
        jTextFieldDiaEstagio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldDiaEstagioKeyReleased(evt);
            }
        });

        jCheckBoxNomenclaturaDia.setSelected(true);
        jCheckBoxNomenclaturaDia.setText("null");
        jCheckBoxNomenclaturaDia.setName("jCheckBoxNomenclaturaDia"); // NOI18N
        jCheckBoxNomenclaturaDia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxNomenclaturaDiaItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelInsercaoEstagioLayout = new javax.swing.GroupLayout(jPanelInsercaoEstagio);
        jPanelInsercaoEstagio.setLayout(jPanelInsercaoEstagioLayout);
        jPanelInsercaoEstagioLayout.setHorizontalGroup(
            jPanelInsercaoEstagioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInsercaoEstagioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInsercaoEstagioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxNomenclaturaDia)
                    .addComponent(Dia)
                    .addComponent(jTextFieldDiaEstagio, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldNomeEstagio, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        jPanelInsercaoEstagioLayout.setVerticalGroup(
            jPanelInsercaoEstagioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInsercaoEstagioLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(Dia)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldDiaEstagio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldNomeEstagio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxNomenclaturaDia))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("null"));
        jPanel4.setName("jPanel4"); // NOI18N

        jLabe10.setText("null");
        jLabe10.setName("jLabe10"); // NOI18N

        jLabe9.setText("null");
        jLabe9.setName("jLabe9"); // NOI18N

        jLabel9.setText("null");
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel14.setText("null");
        jLabel14.setName("jLabel14"); // NOI18N

        jLabelDiferencaDias1.setText(resourceMap.getString("jLabelDiferencaDias1.text")); // NOI18N
        jLabelDiferencaDias1.setName("jLabelDiferencaDias1"); // NOI18N

        jLabelDataInicial1.setText(resourceMap.getString("jLabelDataInicial1.text")); // NOI18N
        jLabelDataInicial1.setName("jLabelDataInicial1"); // NOI18N

        jLabelDataFinal1.setText(resourceMap.getString("jLabelDataFinal1.text")); // NOI18N
        jLabelDataFinal1.setName("jLabelDataFinal1"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabe10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelDataFinal1))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabe9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelDataInicial1))
                    .addComponent(jLabel9)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelDiferencaDias1)))
                .addContainerGap(99, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabe9)
                    .addComponent(jLabelDataInicial1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabe10)
                    .addComponent(jLabelDataFinal1))
                .addGap(27, 27, 27)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabelDiferencaDias1))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDialogInserirEstagioLayout = new javax.swing.GroupLayout(jDialogInserirEstagio.getContentPane());
        jDialogInserirEstagio.getContentPane().setLayout(jDialogInserirEstagioLayout);
        jDialogInserirEstagioLayout.setHorizontalGroup(
            jDialogInserirEstagioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogInserirEstagioLayout.createSequentialGroup()
                .addGroup(jDialogInserirEstagioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogInserirEstagioLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jDialogInserirEstagioLayout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(jButtonCancelarInsercaoEstagio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAplicarInsercaoEstagio, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDialogInserirEstagioLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanelInsercaoEstagio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jDialogInserirEstagioLayout.setVerticalGroup(
            jDialogInserirEstagioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogInserirEstagioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jPanelInsercaoEstagio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialogInserirEstagioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonCancelarInsercaoEstagio)
                    .addComponent(jButtonAplicarInsercaoEstagio))
                .addContainerGap(103, Short.MAX_VALUE))
        );

        jDialogAlterarEstagio.setTitle(resourceMap.getString("jDialogAlterarEstagio.title")); // NOI18N
        jDialogAlterarEstagio.setAlwaysOnTop(true);
        jDialogAlterarEstagio.setBackground(resourceMap.getColor("jDialogAlterarEstagio.background")); // NOI18N
        jDialogAlterarEstagio.setMinimumSize(new java.awt.Dimension(300, 450));
        jDialogAlterarEstagio.setName("jDialogAlterarEstagio"); // NOI18N
        jDialogAlterarEstagio.setResizable(false);

        jButtonCancelarAlterarEstagio.setText("null");
        jButtonCancelarAlterarEstagio.setName("jButtonCancelarAlterarEstagio"); // NOI18N
        jButtonCancelarAlterarEstagio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarAlterarEstagioActionPerformed(evt);
            }
        });

        jButtonAplicarAlteracaoEstagio.setText("null");
        jButtonAplicarAlteracaoEstagio.setName("jButtonAplicarAlteracaoEstagio"); // NOI18N
        jButtonAplicarAlteracaoEstagio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAplicarAlteracaoEstagioActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("null"));
        jPanel5.setName("jPanel5"); // NOI18N

        jLabe11.setText("null");
        jLabe11.setName("jLabe11"); // NOI18N

        jLabe12.setText("null");
        jLabe12.setName("jLabe12"); // NOI18N

        jLabel15.setText("null");
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel16.setText("null");
        jLabel16.setName("jLabel16"); // NOI18N

        jLabelDiferencaDias2.setText(resourceMap.getString("jLabelDiferencaDias2.text")); // NOI18N
        jLabelDiferencaDias2.setName("jLabelDiferencaDias2"); // NOI18N

        jLabelDataFinal2.setText(resourceMap.getString("jLabelDataFinal2.text")); // NOI18N
        jLabelDataFinal2.setName("jLabelDataFinal2"); // NOI18N

        jLabelDataInicial2.setText(resourceMap.getString("jLabelDataInicial2.text")); // NOI18N
        jLabelDataInicial2.setName("jLabelDataInicial2"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelDiferencaDias2))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabe11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelDataFinal2))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabe12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelDataInicial2)))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabe12)
                    .addComponent(jLabelDataInicial2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabe11)
                    .addComponent(jLabelDataFinal2))
                .addGap(27, 27, 27)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabelDiferencaDias2))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanelInsercaoEstagio1.setBorder(javax.swing.BorderFactory.createTitledBorder("null"));
        jPanelInsercaoEstagio1.setName("jPanelInsercaoEstagio1"); // NOI18N

        jTextFieldNomeEstagioAAlterar.setName("jTextFieldNomeEstagioAAlterar"); // NOI18N

        jLabel17.setText("null");
        jLabel17.setName("jLabel17"); // NOI18N

        jCheckBoxNomenclaturaDiaB.setText("null");
        jCheckBoxNomenclaturaDiaB.setName("jCheckBoxNomenclaturaDiaB"); // NOI18N
        jCheckBoxNomenclaturaDiaB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxNomenclaturaDiaBItemStateChanged(evt);
            }
        });

        jTextFieldDiaEstagioAAlterar.setName("jTextFieldDiaEstagioAAlterar"); // NOI18N
        jTextFieldDiaEstagioAAlterar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldDiaEstagioAAlterarKeyReleased(evt);
            }
        });

        Dia1.setText("null");
        Dia1.setName("Dia1"); // NOI18N

        javax.swing.GroupLayout jPanelInsercaoEstagio1Layout = new javax.swing.GroupLayout(jPanelInsercaoEstagio1);
        jPanelInsercaoEstagio1.setLayout(jPanelInsercaoEstagio1Layout);
        jPanelInsercaoEstagio1Layout.setHorizontalGroup(
            jPanelInsercaoEstagio1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInsercaoEstagio1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInsercaoEstagio1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxNomenclaturaDiaB)
                    .addComponent(jTextFieldNomeEstagioAAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldDiaEstagioAAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(Dia1))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanelInsercaoEstagio1Layout.setVerticalGroup(
            jPanelInsercaoEstagio1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInsercaoEstagio1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(Dia1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldDiaEstagioAAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldNomeEstagioAAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxNomenclaturaDiaB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDialogAlterarEstagioLayout = new javax.swing.GroupLayout(jDialogAlterarEstagio.getContentPane());
        jDialogAlterarEstagio.getContentPane().setLayout(jDialogAlterarEstagioLayout);
        jDialogAlterarEstagioLayout.setHorizontalGroup(
            jDialogAlterarEstagioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogAlterarEstagioLayout.createSequentialGroup()
                .addGroup(jDialogAlterarEstagioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jDialogAlterarEstagioLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanelInsercaoEstagio1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jDialogAlterarEstagioLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jDialogAlterarEstagioLayout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(jButtonCancelarAlterarEstagio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonAplicarAlteracaoEstagio, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jDialogAlterarEstagioLayout.setVerticalGroup(
            jDialogAlterarEstagioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogAlterarEstagioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelInsercaoEstagio1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialogAlterarEstagioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAplicarAlteracaoEstagio)
                    .addComponent(jButtonCancelarAlterarEstagio))
                .addContainerGap(142, Short.MAX_VALUE))
        );

        jDialogBarraDeProgresso.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        jDialogBarraDeProgresso.setTitle(resourceMap.getString("jDialogBarraDeProgresso.title")); // NOI18N
        jDialogBarraDeProgresso.setAlwaysOnTop(true);
        jDialogBarraDeProgresso.setIconImage(null);
        jDialogBarraDeProgresso.setIconImages(null);
        jDialogBarraDeProgresso.setMinimumSize(new java.awt.Dimension(200, 100));
        jDialogBarraDeProgresso.setName("jDialogBarraDeProgresso"); // NOI18N
        jDialogBarraDeProgresso.setResizable(false);
        jDialogBarraDeProgresso.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jDialogBarraDeProgressoComponentShown(evt);
            }
        });

        jProgressBar1.setName("jProgressBar1"); // NOI18N

        javax.swing.GroupLayout jDialogBarraDeProgressoLayout = new javax.swing.GroupLayout(jDialogBarraDeProgresso.getContentPane());
        jDialogBarraDeProgresso.getContentPane().setLayout(jDialogBarraDeProgressoLayout);
        jDialogBarraDeProgressoLayout.setHorizontalGroup(
            jDialogBarraDeProgressoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogBarraDeProgressoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jDialogBarraDeProgressoLayout.setVerticalGroup(
            jDialogBarraDeProgressoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogBarraDeProgressoLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jDialogIntegracaoAMAPmod.setTitle(resourceMap.getString("jDialogIntegracaoAMAPmod.title")); // NOI18N
        jDialogIntegracaoAMAPmod.setMinimumSize(new java.awt.Dimension(310, 280));
        jDialogIntegracaoAMAPmod.setName("jDialogIntegracaoAMAPmod"); // NOI18N
        jDialogIntegracaoAMAPmod.setResizable(false);

        jCheckBoxAtivacaoAMAPmod.setText("null");
        jCheckBoxAtivacaoAMAPmod.setName("jCheckBoxAtivacaoAMAPmod"); // NOI18N
        jCheckBoxAtivacaoAMAPmod.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxAtivacaoAMAPmodItemStateChanged(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("null"));
        jPanel6.setName("jPanel6"); // NOI18N

        jButtonBuscarAMAPmod.setText("null");
        jButtonBuscarAMAPmod.setName("jButtonBuscarAMAPmod"); // NOI18N
        jButtonBuscarAMAPmod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBuscarAMAPmodActionPerformed(evt);
            }
        });

        jTextFieldCaminhoAMAPmod.setText(resourceMap.getString("jTextFieldCaminhoAMAPmod.text")); // NOI18N
        jTextFieldCaminhoAMAPmod.setName("jTextFieldCaminhoAMAPmod"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldCaminhoAMAPmod, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(204, Short.MAX_VALUE)
                .addComponent(jButtonBuscarAMAPmod)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextFieldCaminhoAMAPmod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonBuscarAMAPmod)
                .addContainerGap())
        );

        jButtonAplicarAMAPmod.setText("null");
        jButtonAplicarAMAPmod.setName("jButtonAplicarAMAPmod"); // NOI18N
        jButtonAplicarAMAPmod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAplicarAMAPmodActionPerformed(evt);
            }
        });

        jButtonCancJanelaIntAMAPmod.setText("null");
        jButtonCancJanelaIntAMAPmod.setName("jButtonCancJanelaIntAMAPmod"); // NOI18N
        jButtonCancJanelaIntAMAPmod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancJanelaIntAMAPmodActionPerformed(evt);
            }
        });

        jLabelIntegracao.setText("null");
        jLabelIntegracao.setName("jLabelIntegracao"); // NOI18N

        javax.swing.GroupLayout jDialogIntegracaoAMAPmodLayout = new javax.swing.GroupLayout(jDialogIntegracaoAMAPmod.getContentPane());
        jDialogIntegracaoAMAPmod.getContentPane().setLayout(jDialogIntegracaoAMAPmodLayout);
        jDialogIntegracaoAMAPmodLayout.setHorizontalGroup(
            jDialogIntegracaoAMAPmodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogIntegracaoAMAPmodLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialogIntegracaoAMAPmodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxAtivacaoAMAPmod)
                    .addGroup(jDialogIntegracaoAMAPmodLayout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(jButtonCancJanelaIntAMAPmod)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAplicarAMAPmod, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelIntegracao)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialogIntegracaoAMAPmodLayout.setVerticalGroup(
            jDialogIntegracaoAMAPmodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogIntegracaoAMAPmodLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelIntegracao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jCheckBoxAtivacaoAMAPmod)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialogIntegracaoAMAPmodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAplicarAMAPmod)
                    .addComponent(jButtonCancJanelaIntAMAPmod))
                .addContainerGap())
        );

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setName("jList1"); // NOI18N
        jScrollPane5.setViewportView(jList1);

        setComponent(PainelPrincipal);
        setMenuBar(BarraDeMenus);
        setStatusBar(statusPanel);
        addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                formPropertyChange(evt);
            }
        });
    }// </editor-fold>
    /** FIM DA INICIALIZAÇÃO DOS COMPONENTES **/



    
private void formPropertyChange(java.beans.PropertyChangeEvent evt) {
// TODO add your handling code here:
}

private void jButtonInserirMTGActionPerformed(java.awt.event.ActionEvent evt) {


    //variáveis 'nome_arq1_sem_ext' e 'nome_arq2_sem_ext' recebem o nome dos dois arquivo sem extensao.
    String nome_arq1_sem_ext = MTGbaseInicial.getNomeArquivoSemExtenxao();
    String nome_arq2_sem_ext = MTGbaseFinal.getNomeArquivoSemExtenxao();

    //une o nome dos 2 arquivos p/ gerar a nomenclatura:
    String nomenclatura = nome_arq1_sem_ext +  "-" + nome_arq2_sem_ext + ".xls";

    jTextFieldNomeEstagio.setText(nomenclatura); //insere o nome de arquivo (nomenclatura) no textfield

    //Prepara a visualizacao da janela:
    jDialogInserirEstagio.setLocation(300, 300);
    jDialogInserirEstagio.setVisible(true);

}

private void jButtonBuscarMTGFinalActionPerformed(java.awt.event.ActionEvent evt) {
//abrirá uma janela do tipo JFileChooser    jDialogInserirEstagio.setLocation(300, 300); para carregar uma topologia na forma MTG,
    //e se um arquivo foi escolhido, salva o seu caminho no JTextField passado por parametro na funcao:
    buscarArquivoMTG(jTextFieldEstFinal);
}

private void jButtonCarregarMTGsBase(java.awt.event.ActionEvent evt) {

    

    jProgressBar1.setVisible(true);
    jDialogBarraDeProgresso.setLocation(300, 300);
    //jDialogBarraDeProgresso.setVisible(true);

    thread_progressbar = new ThreadProgressBar();
    //thread_progressbar.start();

    boolean flag_leu_mtg = true;

    File arquivo_mtginicial = new File(jTextFieldEstInicial.getText());

    if (arquivo_mtginicial.exists() == false) {
        JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO1"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
        flag_leu_mtg = false;
    } else
    {
        MTGbaseInicial.setArquivo(arquivo_mtginicial);


        try {
            flag_leu_mtg = LerMtgBase(MTGbaseInicial);

            } catch (WriteException ex) {
                Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BiffException ex) {
                Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
            }


        if (flag_leu_mtg == false)  //"Não foi possivel reconhecer a topologia de MTG no arquivo"
            JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO3") + MTGbaseInicial.getArquivo().getName() + " !", colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
    }



    File arquivo_mtgfinal = new File(jTextFieldEstFinal.getText());

    if (arquivo_mtgfinal.exists() == false) { //"Arquivo representando o estágio final não existe!"
        JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO2"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
        flag_leu_mtg = false;
    } else
    {
        MTGbaseFinal.setArquivo(arquivo_mtgfinal);
        try {

            flag_leu_mtg = LerMtgBase(MTGbaseFinal);

            } catch (WriteException ex) {
                Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BiffException ex) {
                Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
            }


        if (flag_leu_mtg == false)  //"Não foi possivel reconhecer a topologia de MTG no arquivo"
            JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO3") + MTGbaseFinal.getArquivo().getName() + " !", colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);

        // f1imv
        //MTGbaseFinal.getPlanta().NumeroEntrenosPorUC();
        //ImprimeDadosMTGsInicialFinal();
    }



    if (flag_leu_mtg == true) {

            ValidacaoMTG valMTG = new ValidacaoMTG();
            if (valMTG.VerificaTamanho(MTGbaseInicial.getPlanta(), MTGbaseFinal.getPlanta()))
            {

                //seta as datas no objeto Periodo, e retorna a diferenca de dias:
                int diferenca_dias = periodo.setDatasEGetDiasEntre(MTGbaseInicial.getData(), MTGbaseFinal.getData());

                //System.out.println("Diferença de dias: " + diferenca_dias);

                if (diferenca_dias == -1) { //"Data definida para o segundo estágio é menor que a data definida para o primeiro estágio! Verifique as datas declaradas nos arquivos MTG."
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO4"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                } else if (diferenca_dias == 0) { //"Não há diferença entre a data definida para o estágio inicial e a data definida para o estágio final!\nNecessita de pelo menos 2 dias de diferença. Verifique as datas declaradas nos arquivos MTG."
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO5"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                } else if (diferenca_dias == 1) { //"Existe apenas um dia de diferença entre a data definida para o estágio inicial e a data definida para o estágio final!\nNecessita de pelo menos 2 dias de diferença. Verifique as datas declaradas nos arquivos MTG."
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO6"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                } else if (diferenca_dias > 1) {
                    //"OK!"
                    //seta a diferenca de dias obtida em labels p/ exibi-lo ao usuario:
                    jLabelDiferencaDias.setText(""+diferenca_dias);
                    jLabelDiferencaDias1.setText(""+diferenca_dias);
                    jLabelDiferencaDias2.setText(""+diferenca_dias);

                    //seta as datas em labels p/ exibi-las ao usuario:
                    jLabelDataInicial.setText(MTGbaseInicial.getDataString());
                    jLabelDataFinal.setText(MTGbaseFinal.getDataString());
                    jLabelDataInicial1.setText(MTGbaseInicial.getDataString());
                    jLabelDataFinal1.setText(MTGbaseFinal.getDataString());
                    jLabelDataInicial2.setText(MTGbaseInicial.getDataString());
                    jLabelDataFinal2.setText(MTGbaseFinal.getDataString());


                    GraficoCompGalhos.setQtdeDias(diferenca_dias); //insere a quantidade de dias p/ o grafico.
                    GraficoEmissaoFolhas.setQtdeDias(diferenca_dias); //insere a quantidade de dias p/ o grafico.
                    GraficoQuedaFolhas.setQtdeDias(diferenca_dias);//insere a quantidade de dias p/ o grafico.
                    GraficoAreaFoliar.setQtdeDias(diferenca_dias); //insere a quantidade de dias p/ o grafico.

                    GraficoNumeroMetameros.setQtdeDias(diferenca_dias);
                    GraficoTamanhoFolhas.setQtdeDias(diferenca_dias);

                    //desativa a primeira aba e vai para a segunda (iniciando ETAPA 2):
                    PainelComAba.setEnabledAt(0, false);
                    PainelComAba.setEnabledAt(1, true);
                    PainelComAba.setSelectedIndex(1);

                    Planta p = MTGbaseInicial.getPlanta();

                    if       ((p.getAmbiente().compareTo("SOMBRA")==0 || p.getAmbiente().compareTo("FUS")==0) && p.getSexo().compareTo("F")==0)
                                p.setAlometriaFolha(0.4976);
                    else if  ((p.getAmbiente().compareTo("SOMBRA")==0 || p.getAmbiente().compareTo("FUS")==0) && p.getSexo().compareTo("M")==0)
                                p.setAlometriaFolha(0.4815);
                    else if  ((p.getAmbiente().compareTo("SOL")==0 || p.getAmbiente().compareTo("MO")==0) && p.getSexo().compareTo("F")==0)
                                p.setAlometriaFolha(0.5394);
                    else if  ((p.getAmbiente().compareTo("SOL")==0 || p.getAmbiente().compareTo("MO")==0) && p.getSexo().compareTo("M")==0)
                                p.setAlometriaFolha(0.5466);
                    else p.setAlometriaFolha(0.5);

                    System.out.println("\n Informações das plantas\n");
                    System.out.println(" * AMBIENTE: " + p.getAmbiente());
                    System.out.println(" * SEXO: " + p.getSexo());
                    jLabelAlometria.setText(String.valueOf(p.getAlometriaFolha()));
 
                }
            }
    }

    else
    {
        System.out.println("\n Não foi possível ler os MTGs bases.\n");
    }
}

private void ImprimeDadosMTG(MTGbase mtg_base)
{
    System.out.println("Dados da planta " + mtg_base.getNomeArquivoSemExtenxao());
    System.out.println(" Qtde folhas: " + mtg_base.getPlanta().getQtdeFolhas());
    System.out.println(" Qtde de entrenos: " + mtg_base.getPlanta().getQtdeEntrenos());
    System.out.println(" Area foliar total: " + mtg_base.getPlanta().getTotalAreaFoliar());
    System.out.println(" Qtde de ramificacoes: " + mtg_base.getPlanta().getQtdeRams());
    System.out.println(" Comp. galhos: " + mtg_base.getPlanta().getCompTotalGalhos());
    System.out.println(" Tamanho Medio de Folhas: " + mtg_base.getPlanta().getTamanhoMedioFolhas());
    System.out.println(" Comp. tronco: " + mtg_base.getPlanta().getCompTronco());
    System.out.println(" Qtde de Suportes: " + mtg_base.getPlanta().getQtdeSuportes());
    System.out.println(" Comp. eixo principal: " + mtg_base.getPlanta().getCompTotalEP());
    //System.out.println(" Qtde. total de EP: " + mtg_base.getPlanta().getQtdEP());
    
    /*System.out.println(" Dados por suporte: ");
    System.out.println("\t\tEntrenos");
    for (int i=0; i<mtg_base.getPlanta().getQtdeSuportes(); i++)
    {
        System.out.println("\tSuporte " + (i+1) + ": " + mtg_base.getPlanta().getSuporte(i).getQtdeEntrenos());
    }*/
}

private void ImprimeDadosMTGsInicialFinal()
{
    // Imprime no console os dados dos MTGs
    System.out.println("\n ***********Planta INICIAL************\n");
    ImprimeDadosMTG(MTGbaseInicial);
    System.out.println("\n ***********Planta FINAL************\n");
    ImprimeDadosMTG(MTGbaseFinal);
};

private boolean LerMtgBase(MTGbase _mtgbase) throws WriteException, IOException, BiffException
{
        Workbook workbook_temp;
        WritableWorkbook workbook_fonte;
        WritableSheet sheet_fonte;

        thread_progressbar.setValor(1);

    if (_mtgbase.isXLS()) //se o arquivo a ser lido for de extensao xls:
    {
        //workbook_fonte recebe o conteudo do arquivo xls:
        workbook_temp = Workbook.getWorkbook(_mtgbase.getArquivo()); //abre o arquivo xls contendo a planilha a ser lida
        //cria uma copia do workbook para escrita (p/ pode realizar algumas alteracoes antes de ser percorrido para leitura):
        workbook_fonte = Workbook.createWorkbook(new File("temp/temp_leitura.xls"), workbook_temp);
        //sheet_fonte eh a planilha (a primeira) do arquivo:
        sheet_fonte = workbook_fonte.getSheet(0);
    }
    else
    {

        if (_mtgbase.isInicial())
        {
            workbook_fonte = Workbook.createWorkbook(new File("temp/MTGparaXLS-estagioinicial.xls"));
            sheet_fonte = workbook_fonte.createSheet(colecaomsgs.getString("TITULOPLANILHAESTAGIOINICIAL"), 0); //"Estagio Inicial"
        }
        else
        {
            workbook_fonte = Workbook.createWorkbook(new File("temp/MTGparaXLS-estagiofinal.xls"));
            sheet_fonte = workbook_fonte.createSheet(colecaomsgs.getString("TITULOPLANILHAESTAGIOFINAL"), 0); //"Estagio Final"
        }


        //Converte o arquivo que esta em texto (MTG) para o formato XLS:
        //_mtgbase.getArquivo() possui o arquivo Texto (MTG) a ser lido, e sheet_fonte eh onde sera guardado os dados em XLS
        ConversorTextoParaXLS conversor_txtxls = new ConversorTextoParaXLS(_mtgbase.getArquivo(), sheet_fonte);
        if (!conversor_txtxls.converte())
        {
            workbook_fonte.close();
            return false;
        }

        workbook_fonte.write();
        //workbook_fonte.close();
    }


    thread_progressbar.setValor(2);

    int linha_atual; //indicara a linha do MTG sendo lida.

    LeituraMTG leitorMTG = new LeituraMTG(sheet_fonte);  //instancia uma classe para executar leitura deste MTG

    if (!leitorMTG.verificarColunasValidas()) //verifica se o numero de colunas deste MTG é valido.
    {                                               //"O MTG do arquivo "                                       //" não possui o número de colunas necessárias!\nNecessita ter pelo menos 20 colunas."
        JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO7") + _mtgbase.getArquivo().getName() + colecaomsgs.getString("ERRO8"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
        workbook_fonte.close();
        return false;
    }

    thread_progressbar.setValor(3);

    linha_atual = leitorMTG.encontrarLinhaInicial(); //linha_atual recebe a linha em que iniciam os dados geometricos da topologia do MTG.
    if (linha_atual==0) //se funcao retornar 0 indica que nao encontrou linha inicial dos dados do MTG
    {                                               //"O MTG do arquivo "                                       //" não encontrou a linha inicial de onde começam os dados da topologia.\nVerifique no arquivo MTG se existe o texto 'ENTITY-CODE' e se a estrutura se inicia apos ele."
        JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO7") + _mtgbase.getArquivo().getName() + colecaomsgs.getString("ERRO9"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
        workbook_fonte.close();
        return false;
    }

    progresso_da_barra = 4;

    //flag recebe o valor de retorno verificar se reconheceu a data no MTG. Sera 1 se for OK, ou 0 ou -1 se for erro.
    int flag_reconheceu_data = leitorMTG.lerEArrumarData(_mtgbase);

    if (flag_reconheceu_data == 0)
    {                                               //"Nao existe data declarada no MTG do arquivo "            //"!\nNecessita ter uma data declarada na topologia do MTG."
        JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO10") + _mtgbase.getArquivo().getName() + colecaomsgs.getString("ERRO11"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
        //System.out.println("(Arquivo " + _mtgbase.getArquivo().getName() + ") Tipo da célula onde deveria ser data : " + sheet_fonte.getCell(14, linha_atual).getType());
        workbook_fonte.close();
        return false;
    }

    if (flag_reconheceu_data == -1)
    {                                       //Erro ao converter de String para Data!
        JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO12"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
        workbook_fonte.close();
        return false;
    }

    /* Se flag_receonheceu_data nao for 0 nem -1, entao eh 1 (conseguiu reconhecer a data no MTG),
       e entao continua a sequencia do codigo:   */


    progresso_da_barra = 5;

    //*********** PROXIMO MODULO ***********
    leitorMTG.arrumarPadraoNumerosReais();  //Substituir virgulas por ponto: (procura ocorrencias em todo o arquivo)

    progresso_da_barra = 6;

    //intanciacao dos objetos p/ armazenar a estrutura de dados do MTG a ser lido:
    Planta p = new Planta();

    //le os dados inicias da planta: (se der algum erro, retorna falso)
    if (!leitorMTG.lerDadosIniciasPlanta(p, _mtgbase)) return false;

    progresso_da_barra = 7;

    //le os dados do tronco: (se der algum erro, retorna falso)
    if (!leitorMTG.lerDadosTronco(p, _mtgbase)) return false;

    progresso_da_barra = 8;

    //le os dados do tronco: (se der algum erro, retorna falso)
    if (!leitorMTG.lerDadosSuporte(p, _mtgbase)) return false;

    progresso_da_barra = 9;

    //le os dados do galho: (se der algum erro, retorna falso)
    if (!leitorMTG.lerDadosGalho(p, _mtgbase)) return false;

    progresso_da_barra = 10;

    //insere a estrutura topologica da planta no objeto MTGbase.
    _mtgbase.setPlanta(p);

    progresso_da_barra = 11;

    if (_mtgbase.isInicial())
    {
            GraficoCompGalhos.setIntensidadeMinima(p.getCompTotalGalhos());
            GraficoEmissaoFolhas.setIntensidadeMinima(0);
            GraficoQuedaFolhas.setIntensidadeMinima(0);
            GraficoAreaFoliar.setIntensidadeMinima(p.getTotalAreaFoliar());

            GraficoNumeroMetameros.setIntensidadeMinima(p.getQtdeEntrenos());
            GraficoTamanhoFolhas.setIntensidadeMinima(p.getTamanhoMedioFolhas());
    }
    else
    {
            GraficoCompGalhos.setIntensidadeMaxima(p.getCompTotalGalhos());
            GraficoEmissaoFolhas.setIntensidadeMaxima(100);
            GraficoQuedaFolhas.setIntensidadeMaxima(100);
            GraficoAreaFoliar.setIntensidadeMaxima(p.getTotalAreaFoliar());

            GraficoNumeroMetameros.setIntensidadeMaxima(p.getQtdeEntrenos());
            GraficoTamanhoFolhas.setIntensidadeMaxima(p.getTamanhoMedioFolhas());
    }


    /* ********************* ESCRITA TESTE MTG ******************** //
    EscritaMTG EscritaMTG_;

    if (_mtgbase.isInicial())
            EscritaMTG_ = new EscritaMTG(new File("mtgtesteescrita.xls"), p);
    else
            EscritaMTG_ = new EscritaMTG(new File("mtgtesteescrita2.xls"), p);
    // ************************************************************ */


    progresso_da_barra = 12;

    workbook_fonte.close();

    return true;            //entao retorna true (representando que nao ocorreu nenhum erro)
}



private void jButtonBuscarMTGInicialActionPerformed(java.awt.event.ActionEvent evt) {
//abrirá uma janela do tipo JFileChooser para carregar uma topologia na forma MTG,
    //e se um arquivo foi escolhido, salva o seu caminho no JTextField passado por parametro na funcao:
    buscarArquivoMTG(jTextFieldEstInicial);
}

private void jButtonLimparInsercaoEstagioActionPerformed(java.awt.event.ActionEvent evt) {

    //limpa e fecha a janela de Inserção de estágio
    jTextFieldDiaEstagio.setText(""); //limpa texto
    jTextFieldNomeEstagio.setText(""); //limpa texto
    jCheckBoxNomenclaturaDia.setSelected(true); //reseta checkbox como selecionada.
    jDialogInserirEstagio.setVisible(false);
}


private void jButtonAplicarInsercaoEstagioActionPerformed(java.awt.event.ActionEvent evt) {

            boolean jaexiste=false;

            try
            {
                 //dias corridos desde a data de início. (recebe do textfield p/ inserir o dia)
                int dias_corridos = Integer.parseInt(jTextFieldDiaEstagio.getText());

                if (dias_corridos < 1) {                //"Dia definido para este estágio deve ter valor maior que 0."
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO13"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                } else if (dias_corridos > periodo.getDiferencaDias()) { //"Dia definido para este estágio é maior que o dia do estágio final base! \nNecessita ter um dia menor."
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO14"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                } else if (dias_corridos == periodo.getDiferencaDias()) { //"Não há diferença entre o dia definido para este estágio e o dia do estágio final base!\nNecessita ter um dia menor que a data do estágio final base."
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO15"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                } else
                {
                    //"OK!"

                    if (jTextFieldNomeEstagio.getText().length()>0) //se nome do arquivo removido do textbox existir (for > 0 caracteres)
                    {
                        //tenta acrescentar o arquivo + dia na tabela


                        //Percorre os itens da tabela p/ verificar se ja nao existe algum item com mesmo "nome de arquivo" ou mesmo "dia"...
                        for (int i=0; i<ModeloTabelaMTGsAGerar.getRowCount(); i++)
                        {

                            if (ModeloTabelaMTGsAGerar.getValueAt(i, 0)!=null && ModeloTabelaMTGsAGerar.getValueAt(i, 1)!=null) //precisa verificar primeiramente as celulas dessa linha nao sao nulas. Se nao forem:
                            {

                                 //se ja houver algum nome existente igual ao que foi solicitado p/ a inclusao:
                                if (ModeloTabelaMTGsAGerar.getValueAt(i, 0).toString().equalsIgnoreCase(jTextFieldNomeEstagio.getText())==true)
                                {   //disponibiliza uma mensagem avisando o ocorrido, ativa um flag e sai do loop. "Este nome de arquivo já existe na tabela de MTGs a serem gerados.\nEscolha um outro nome."
                                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO16"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                                    jaexiste=true;
                                    break;
                                }

                                //se ja houver algum dia existente igual ao que foi solicitado p/ a inclusao:
                                if (ModeloTabelaMTGsAGerar.getValueAt(i, 1).equals(dias_corridos))
                                {   //disponibiliza uma mensagem avisando o ocorrido, ativa um flag e sai do loop.
                                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO17"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                                    jaexiste=true;
                                    break;
                                }
                            }

                        }


                        if (jaexiste==false) //se solicitacao p/ inclusao ainda nao existe na tabela...
                        {
                            //entao realizara a insercao (insere ordenado):
                            InsereOrdenadoNaTabela(ModeloTabelaMTGsAGerar, jTextFieldNomeEstagio.getText(), (""+dias_corridos));


                            /*
                            //se a tabela esta inteiramente vazia:
                            if (ModeloTabelaMTGsAGerar.getRowCount()==0)
                            {
                                  ModeloTabelaMTGsAGerar.insertRow(0, new Object[] {}); //cria uma nova linha no modelo
                                  //insere o nome do arquivo e dia nesta linha:
                                  ModeloTabelaMTGsAGerar.setValueAt(jTextFieldNomeEstagio.getText(), 0, 0);
                                  ModeloTabelaMTGsAGerar.setValueAt(dias_corridos, 0, 1);
                            }
                            else //senao:

                            for (int i=0; i<ModeloTabelaMTGsAGerar.getRowCount(); i++)  //percorre toda a matriz da tabela
                             {

                                   //Se valor lido na tabela eh maior que o dia a inserir, entao insere ordenado na tabela:
                                   if (Integer.parseInt(ModeloTabelaMTGsAGerar.getValueAt(i, 1).toString())>dias_corridos)
                                   {
                                        ModeloTabelaMTGsAGerar.insertRow(i, new Object[] {}); //cria uma nova linha no modelo na posicao "i"
                                        //insere o nome do arquivo e dia nesta posicao:
                                        ModeloTabelaMTGsAGerar.setValueAt(jTextFieldNomeEstagio.getText(), i, 0);
                                        ModeloTabelaMTGsAGerar.setValueAt(dias_corridos, i, 1);
                                        break; //pode sair do loop
                                   }

                                   //"CONDICAO ESSENCIAL SE O ELEMENTO A SER INSERIDO DEVE SER O ULTIMO DA LISTA":
                                   if (i==ModeloTabelaMTGsAGerar.getRowCount()-1) //se estiver na ultima posicao da lista
                                   {
                                        //e se o valor encontrado do dia for menor que o valor a inserir:
                                        if (Integer.parseInt(ModeloTabelaMTGsAGerar.getValueAt(i, 1).toString())<dias_corridos)
                                        {
                                            ModeloTabelaMTGsAGerar.insertRow(i+1, new Object[] {}); //cria uma nova linha no final
                                            //insere o nome do arquivo e dia nesta posicao:
                                            ModeloTabelaMTGsAGerar.setValueAt(jTextFieldNomeEstagio.getText(), i+1, 0);
                                            ModeloTabelaMTGsAGerar.setValueAt(dias_corridos, i+1, 1);
                                            break;
                                        }
                                   }
                            }*/


                            //limpa e fecha a janela de insercao:
                            jButtonLimparInsercaoEstagioActionPerformed(null);
                        }



                    }                                        //"Insira o nome do arquivo."
                    else JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO18"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);

                }

            }
            catch (NumberFormatException e)
            {                                       //O valor a ser inserido no campo 'Dia a partir do estágio inicial'\ndeve ser um número inteiro.
                JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO19"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
            }

}

private void jButtonCancelarAlterarEstagioActionPerformed(java.awt.event.ActionEvent evt) {
    //fecha a janela de Alteracao de estágio
    jDialogAlterarEstagio.setVisible(false);
    jCheckBoxNomenclaturaDiaB.setSelected(false); //reseta checkbox como nao-selecionada.
}

private void jButtonAplicarAlteracaoEstagioActionPerformed(java.awt.event.ActionEvent evt) {

            boolean jaexiste=false;

            try
            {
                 //dias corridos desde a data de início. (recebe do textfield p/ inserir o dia)
                int dias_corridos = Integer.parseInt(jTextFieldDiaEstagioAAlterar.getText());

                if (dias_corridos < 1) {                                     //"Dia definido para este estágio deve ter valor maior que 0."
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO20"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                } else if (dias_corridos > periodo.getDiferencaDias()) {     //"Dia definido para este estágio é maior que o dia do estágio final base!\nNecessita ter um dia menor."
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO21"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                } else if (dias_corridos == periodo.getDiferencaDias()) {    //"Não há diferença entre o dia definido para este estágio e o dia do estágio final base!\nNecessita ter um dia menor que a data do estágio final base."
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO22"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                } else
                {
                    //"OK!"

                    if (jTextFieldNomeEstagioAAlterar.getText().length()>0) //se nome do arquivo removido do textbox existir (for > 0 caracteres)
                    {
                        //tenta acrescentar o arquivo + dia na tabela


                        if (jTableMTGsAGerar.getSelectedRow() > -1) //se alguma linha esta selecionada:
                        {
                             //pega os dados da linha selecionada (nome do arquivo e dia):
                             String str_nome_do_arquivo = ModeloTabelaMTGsAGerar.getValueAt(jTableMTGsAGerar.getSelectedRow(), 0).toString();
                             String str_dia = ModeloTabelaMTGsAGerar.getValueAt(jTableMTGsAGerar.getSelectedRow(), 1).toString();

                             //se houve alteracao (entre o valor atual nos textfield's e os valores selecionados da tabela)
                             if (!(str_nome_do_arquivo.equalsIgnoreCase(jTextFieldNomeEstagioAAlterar.getText())
                                         &&  str_dia.equals(jTextFieldDiaEstagioAAlterar.getText())))
                             {
                                 //entao devera fazer nova inclusao na tabela




                                 //Percorre os itens da tabela p/ verificar se ja nao existe algum item com mesmo "nome de arquivo" ou mesmo "dia"...
                                 for (int i=0; i<ModeloTabelaMTGsAGerar.getRowCount(); i++)
                                 {

                                    if (ModeloTabelaMTGsAGerar.getValueAt(i, 0)!=null && ModeloTabelaMTGsAGerar.getValueAt(i, 1)!=null) //precisa verificar primeiramente as celulas dessa linha nao sao nulas. Se nao forem:
                                    {

                                         //se ja houver algum nome existente igual ao que foi solicitado p/ a alteracao:
                                        if (ModeloTabelaMTGsAGerar.getValueAt(i, 0).toString().equalsIgnoreCase(jTextFieldNomeEstagioAAlterar.getText())==true
                                        &&  (jTableMTGsAGerar.getSelectedRow() != i)) //numa linha diferente da linha selecionada
                                        {   //disponibiliza uma mensagem avisando o ocorrido, ativa um flag e sai do loop. "Este nome de arquivo já existe na tabela de MTGs a serem gerados.\nEscolha um outro nome."
                                            JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO16"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                                            jaexiste=true;
                                            break;
                                        }

                                        //se ja houver algum dia existente igual ao que foi solicitado p/ a alteracao:
                                        if (ModeloTabelaMTGsAGerar.getValueAt(i, 1).equals(dias_corridos)
                                        &&  (jTableMTGsAGerar.getSelectedRow() != i)) //numa linha diferente da linha selecionada
                                        {   //disponibiliza uma mensagem avisando o ocorrido, ativa um flag e sai do loop. "Este dia já existe na tabela de MTGs a serem gerados."
                                            JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO17"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                                            jaexiste=true;
                                            break;
                                        }
                                    }

                                 }

                                 if (jaexiste==false)
                                 {

                                     //primeiramente remove a linha onde existem os valores antigos:
                                     ModeloTabelaMTGsAGerar.removeRow(jTableMTGsAGerar.getSelectedRow());

                                     for (int i=0; i<ModeloTabelaMTGsAGerar.getRowCount(); i++)  //percorre toda a matriz da tabela
                                     {

                                       //Se valor lido na tabela eh maior que o dia a inserir, entao insere ordenado na tabela:
                                       if (Integer.parseInt(ModeloTabelaMTGsAGerar.getValueAt(i, 1).toString())>dias_corridos)
                                       {
                                            ModeloTabelaMTGsAGerar.insertRow(i, new Object[] {}); //cria uma nova linha no modelo na posicao "i"
                                            //insere o nome do arquivo e dia nesta posicao:
                                            ModeloTabelaMTGsAGerar.setValueAt(jTextFieldNomeEstagioAAlterar.getText(), i, 0);
                                            ModeloTabelaMTGsAGerar.setValueAt(dias_corridos, i, 1);
                                            jTableMTGsAGerar.setRowSelectionInterval(i,i); //"marca" a linha inserida na tabela
                                            break; //pode sair do loop
                                       }

                                       //"CONDICAO ESSENCIAL SE O ELEMENTO A SER INSERIDO DEVE SER O ULTIMO DA LISTA":
                                       if (i==ModeloTabelaMTGsAGerar.getRowCount()-1) //se estiver na ultima posicao da lista
                                       {
                                            //e se o valor encontrado do dia for menor que o valor a inserir:
                                            if (Integer.parseInt(ModeloTabelaMTGsAGerar.getValueAt(i, 1).toString())<dias_corridos)
                                            {
                                                ModeloTabelaMTGsAGerar.insertRow(i+1, new Object[] {}); //cria uma nova linha no final
                                                //insere o nome do arquivo e dia nesta posicao:
                                                ModeloTabelaMTGsAGerar.setValueAt(jTextFieldNomeEstagioAAlterar.getText(), i+1, 0);
                                                ModeloTabelaMTGsAGerar.setValueAt(dias_corridos, i+1, 1);
                                                jTableMTGsAGerar.setRowSelectionInterval(i+1,i+1); //"marca" a linha inserida na tabela
                                                break;
                                            }
                                       }
                                    }

                                    //fecha a janela de insercao:
                                    jDialogAlterarEstagio.setVisible(false);
                                 }
                             }
                             else //fecha a janela de insercao:
                             jDialogAlterarEstagio.setVisible(false);
                        }
                    }                                        //"Insira o nome do arquivo."
                    else JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO18"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);

                }

            }
            catch (NumberFormatException e)
            {                                       //"O valor a ser inserido no campo 'Dia a partir do estágio inicial' deve ser um número inteiro."
                JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO19"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
            }

}

private void jButtonAlterarMTGActionPerformed(java.awt.event.ActionEvent evt) {

    String str_nome_do_arquivo;
    String str_dia;

    if (jTableMTGsAGerar.getSelectedRow() > -1) //se alguma linha esta selecionada:
    {
         //pega os dados da linha selecionada (nome do arquivo e dia):
         str_nome_do_arquivo = ModeloTabelaMTGsAGerar.getValueAt(jTableMTGsAGerar.getSelectedRow(), 0).toString();
         str_dia = ModeloTabelaMTGsAGerar.getValueAt(jTableMTGsAGerar.getSelectedRow(), 1).toString();
         //seta os jTextField's com esses valores:
         jTextFieldNomeEstagioAAlterar.setText(str_nome_do_arquivo);
         jTextFieldDiaEstagioAAlterar.setText(str_dia);

         jLabelDiferencaDias2.setText(""+periodo.getDiferencaDias()); //seta a diferenca de dias obtida em um label p/ exibi-lo ao usuario

         //disponibiliza a janela para alteracao:
         jDialogAlterarEstagio.setLocation(300, 300);
         jDialogAlterarEstagio.setVisible(true);
    }

}


private void jTextFieldDiaEstagioKeyReleased(java.awt.event.KeyEvent evt) {

    ControlaNomenclatura(jTextFieldDiaEstagio, jTextFieldNomeEstagio, jCheckBoxNomenclaturaDia, evt);
}

private void ControlaNomenclatura(JTextField CaixaTextoDiaEstagio, JTextField CaixaTextoNomeEstagio, JCheckBox CheckBoxNomenclaturaDia, java.awt.event.KeyEvent evt)
{

    //variáveis 'nome_arq1_sem_ext' e 'nome_arq2_sem_ext' recebem o nome dos dois arquivo sem extensao.
    String nome_arq1_sem_ext = MTGbaseInicial.getNomeArquivoSemExtenxao();
    String nome_arq2_sem_ext = MTGbaseFinal.getNomeArquivoSemExtenxao();


    //string onde sera gerada o nome do arquivo do estagio a ser inserido:
    String nomenclatura;


    /* ******** VALIDACAO *********** */
     //se o caractere digitado no textfield for um caractere imprimivel nao-numerico
    if ((evt.getKeyCode() >= 32 && evt.getKeyCode() <=36) ||
        (evt.getKeyCode() >= 41 && evt.getKeyCode() <=47) ||
        (evt.getKeyCode() >= 58))
    {
          //remove este ultimo caractere da string do textfield: (correcao)

          String str_correta_temp = CaixaTextoDiaEstagio.getText().substring(0 , CaixaTextoDiaEstagio.getText().length()-1);
          CaixaTextoDiaEstagio.setText(str_correta_temp);
    }

    //Loop para limpar os '0's que podem acontecer antes de um numero:
    //enquanto primeiro caractere for um "0"
    if (CaixaTextoDiaEstagio.getText().substring(0,0).equals("0") == true)
    {   //copia a propria string removendo este primeiro caractere.
        CaixaTextoDiaEstagio.setText( CaixaTextoDiaEstagio.getText().substring(1) );
    }


     //se checkbox p/ ativar a insercao de dias automatico na nomenclatura esta ativado:
     if (CheckBoxNomenclaturaDia.isSelected()==true)
     {
         if (CaixaTextoDiaEstagio.getText().length()>0)
             //gera a nomenclatura: (sempre que o texto for alterado no jTextFieldDiaEstagio, o nome do arquivo sera renovado)
             nomenclatura = nome_arq1_sem_ext +  "-" + nome_arq2_sem_ext + "-" + CaixaTextoDiaEstagio.getText() + ".xls";
         else
             nomenclatura = nome_arq1_sem_ext +  "-" + nome_arq2_sem_ext + ".xls"; //gera a mesma nomenclatura (vazia, sem o "hifen")

          CaixaTextoNomeEstagio.setText(nomenclatura); //insere o nome de arquivo (nomenclatura) no textfield
     }
}

private void MenuItemPortuguesItemStateChanged(java.awt.event.ItemEvent evt) {

    if (MenuItemPortugues.getState()==true)
    {
        INTERNACIONALIZACAO = 0; //"PORTUGUES"

        colecaomsgs  = ResourceBundle.getBundle("Messages");
        colecaomsgsgui  = ResourceBundle.getBundle("MessagesGUI");

        atualizarTextoComponentes();
    }

    //GraficoAreaFoliar.desenhaGraficoEm(jPainelGrafico1, INTERNACIONALIZACAO, ListaMTGsAGerar, jCheckBoxMostrarDiasReq.isSelected());
}


private void jDialogInserirEstagioComponentHidden(java.awt.event.ComponentEvent evt) {

    jButtonLimparInsercaoEstagioActionPerformed(null);
}



private void jButtonExcluirMTGActionPerformed(java.awt.event.ActionEvent evt) {

    if (jTableMTGsAGerar.getSelectedRow() > -1) //se alguma linha esta selecionada:
    {
         ModeloTabelaMTGsAGerar.removeRow(jTableMTGsAGerar.getSelectedRow()); //remove esta linha da tabela
    }

}

private void jTextFieldDiaEstagioAAlterarKeyReleased(java.awt.event.KeyEvent evt) {

     ControlaNomenclatura(jTextFieldDiaEstagioAAlterar, jTextFieldNomeEstagioAAlterar, jCheckBoxNomenclaturaDiaB, evt);
}


//Fara todas as condicoes (dos MTGs a gerar) para verificar se pode passar para o proximo passo:
private void jButtonAplicarMTGsAGerarActionPerformed(java.awt.event.ActionEvent evt) throws FileNotFoundException, IOException {

    int dia;
    String nome_arquivo;


    if (jTableMTGsAGerar.getRowCount()==0)  //se tabela de mtgs a serem gerados esta vazia,
    {                                       //Tabela de MTGs a serem gerados está vazia.\nNecessita de pelo menos uma inserção na tabela.
            JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO25"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
    }
    else //se tem elemento:
    {
        File diretorio = new File(jTextFieldDiretorio.getText()); //"diretorio" recebe o caminho do diretorio registrado no TextField

        if (diretorio.isDirectory() == false) //se por acaso nao for um diretorio (erro):
        {                                    //"Diretório entrado na caixa de texto não é um diretório válido! Insira um diretório válido e tente novamente."
            JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO26"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
        }
        else //se for um diretorio:
        {   //verifica se ele realmente existe:
            if (diretorio.exists() == false) //se nao existir:
            {                                //"Diretório entrado na caixa de texto não existe! Insira um diretório válido e tente novamente."
                JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO27"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
            }
            else    //se existir:
            {
                //verifica se um dos arquivos ja nao existe com o mesmo nome, e entao pergunta se quer subescrever
                if (ConfereArquivosNaPasta(diretorio.list(), jTableMTGsAGerar))
                {   //se tudo esta OK entao:

                                                                       //"Estágio "                                      //" - Dia "
                    jComboBoxLogEstagios.addItem((colecaomsgs.getString("TITULOCOMBOITEM1") + "1" + colecaomsgs.getString("TITULOCOMBOITEM2") + "0" ));

                    int i;
                    //percorre todas as linhas existentes da tabela:
                    for (i=0; i<jTableMTGsAGerar.getRowCount(); i++)
                    {
                        nome_arquivo = jTableMTGsAGerar.getValueAt(i, 0).toString();
                        dia = Integer.parseInt(jTableMTGsAGerar.getValueAt(i, 1).toString());

                        //insere todos as requisicoes de MTGs a serem gerados numa lista:
                        ListaMTGsAGerar.add(new MTGaSerGerado(i, dia, nome_arquivo));
                                                                           //"Estágio "                                         //" - Dia "
                        jComboBoxLogEstagios.addItem((colecaomsgs.getString("TITULOCOMBOITEM1") + (i+2) + colecaomsgs.getString("TITULOCOMBOITEM2") + dia ));
                    }
                                                                        //"Estágio "                                        //" - Dia "
                    jComboBoxLogEstagios.addItem((colecaomsgs.getString("TITULOCOMBOITEM1") + (i+2) + colecaomsgs.getString("TITULOCOMBOITEM2") + periodo.getDiferencaDias() ));


                    //desativa a segunda aba e vai para a terceira (iniciando ETAPA 3):
                    PainelComAba.setEnabledAt(1, false);
                    PainelComAba.setEnabledAt(2, true);
                    PainelComAba.setSelectedIndex(2);
                }
            }
        }
    }
    
    // RNA001
    // Processar as redes neurais aqui
    
    // Dados para redes neurais
    double ann_ac[] = new double[25];
    double ann_anf[] = new double[25];
    double ann_qf[] = new double[25];
    double ann_nm[] = new double[25];
    double ann_out[][] = new double[4][25];
    
    String ann_file[] = {"nm.out", "ac.out", "anf.out", "qf.out"};
    
    String amb, sex;
    amb = MTGbaseInicial.getPlanta().getAmbiente();
    sex = MTGbaseInicial.getPlanta().getSexo();
    
    System.out.println("\n Ambiente " + amb + " SEX " + sex);
    
    // Defininedo os parâmetros de entrada da rede neural
    // Aqui serão acrescentados os treinamentos da rede neural
    System.out.println(" >>>> Calculando os parametros da rede neural");
    
    if (amb.equals("SOL"))
    {

        if (sex.equals("M"))
        {
           ann_out[0] = Database.ANN_NM_MO_M;
           ann_out[1] = Database.ANN_AC_MO_M;
           ann_out[2] = Database.ANN_NF_MO_M;
           ann_out[3] = Database.ANN_QF_MO_M;
        }

        else if (sex.equals("F"))
        {
           ann_out[0] = Database.ANN_NM_MO_F;
           ann_out[1] = Database.ANN_AC_MO_F;
           ann_out[2] = Database.ANN_NF_MO_F;
           ann_out[3] = Database.ANN_QF_MO_F;
        }
    }

    // Ambiente SOMBRA
    else if (amb.equals("SOMBRA"))
    {
        if (sex.equals("M"))
        {
            ann_out[0] = Database.ANN_NM_FUS_M;
            ann_out[1] = Database.ANN_AC_FUS_M;
            ann_out[2] = Database.ANN_NF_FUS_M;
            ann_out[3] = Database.ANN_QF_FUS_M;            
        }

        // Espécie e ambiente em teste F -Sombra
        else if (sex.equals("F"))
        {
            ann_out[0] = Database.ANN_NM_FUS_F;
            ann_out[1] = Database.ANN_AC_FUS_F;
            ann_out[2] = Database.ANN_NF_FUS_F;
            ann_out[3] = Database.ANN_QF_FUS_F;
        }
    }
    
    InputStream is;
    InputStreamReader isr;
    BufferedReader buffer;
    
    double all_input[] = {12.57,9.94,10.18,8.61,14.85,15.22,16.98,17.45,18.44,16.29,16.19,12.78,9.45,12.15,8.85,11.17,13.00,12.90,16.68,17.42,17.34,17.21,16.32,11.71,14.52,19.57,18.87,21.57,20.17,24.84,24.43,27.70,28.29,28.30,27.02,27.73,20.67,16.82,20.05,17.54,22.05,23.82,22.97,27.26,28.02,28.42,27.90,25.00,20.16,23.29,13.60,13.40,12.82,12.06,11.22,10.50,10.16,10.28,10.68,11.77,12.57,13.08,13.56,13.51,13.00,12.24,11.35,10.60,10.26,10.28,11.05,11.85,12.69,13.34,13.52,254.50,282.10,326.20,281.70,460.10,444.80,398.80,625.40,384.60,699.60,508.80,257.35,268.50,332.90,254.20,359.80,456.00,388.05,322.50,779.80,643.70,509.10,501.15,338.95,194.65};
    
    File training_file[] = new File[4];
    PrintWriter training_file_dat[] = new PrintWriter[4];
    
    for (int j=0; j<4; j++)
    {
        is = new FileInputStream("all_input.dat");
        isr = new InputStreamReader(is); //InputStreamReader é uma classe para converter os bytes em char
        buffer = new BufferedReader(isr); //BufferedReader é uma classe para armazenar os chars em memoria
    
        training_file[j] = new File("training"+j+".dat");
        training_file_dat[j] = new PrintWriter(new FileOutputStream(training_file[j]));
        AnexarNoArquivo(training_file_dat[j], buffer);

        /*for (int i=0; i<ann_ac.length; i++)
        {
            training_file_dat[j].append(all_input[i] + " ");
        } training_file_dat[j].append("\n");
        */
        // Para o parâmetro de alongamento de caule
        for (int i=0; i<ann_out[j].length; i++)
        {
            training_file_dat[j].append(ann_out[j][i] + " ");
        }
        
        training_file_dat[j].close();
        
        Runtime runtime = Runtime.getRuntime(); // Em tempo de execução do aplicativo atual
        Process process = runtime.exec("cmd.exe /c start backpropagation " + "Y"+ann_file[j] + " training"+j+".dat norm"+j+".dat " + ann_file[j]);

        isr.close();
    }
    
}




public void AnexarNoArquivo(PrintWriter script_python, BufferedReader buffer) throws IOException
    {
        String s;
        // A leitura está sendo feita por linha
        s = buffer.readLine(); //primeira linha
        script_python.println(s);
        while (s != null)
        {
            //System.out.println(s);
            s = buffer.readLine(); //primeira linha
            if (s!=null) script_python.println(s);
        }
    };


public void DefineParametrosConhecidos(Planta planta)
    {
        // Ambiente SOL
        if (planta.getAmbiente().equals("SOL"))
        {

            if (planta.getSexo().equals("M"))
            {

            }

            else if (planta.getSexo().equals("F"))
            {

            }
        }

        // Ambiente SOMBRA
        else if (planta.getAmbiente().equals("SOMBRA"))
        {
            if (planta.getSexo().equals("M"))
            {

            }

            // Espécie e ambiente em teste F -Sombra
            else if (planta.getSexo().equals("F"))
            {

            }
        }
    };








/* Metodo para verificar se os nomes dos arquivos definidos a serem gerados
 * conflita com algum nome ja existente na pasta a ser salva.
 *
 *      Recebe por parametro:
 *                  arquivos_da_pasta[]:    nomes dos arquivos que ja existem na pasta a ser salva
 *                  MTGsAGerar:             tabela contendo os nomes dos arquivos dos MTGs a serem gerados
 *
 *      Retorna: true se tudo esta OK, ou falso se encontrou algum nome que ja existe.
 */
private boolean ConfereArquivosNaPasta(String arquivos_da_pasta[], JTable MTGsAGerar)
{

    for (int i=0; i<arquivos_da_pasta.length; i++) //percorre os arquivos da pasta
    {

        for (int j=0; j<jTableMTGsAGerar.getRowCount(); j++) //percorre os arquivos da tabela
        {
            if (jTableMTGsAGerar.getValueAt(j, 0).toString().equals(arquivos_da_pasta[i])) //se algum nome coincidir:
            {
                //JOptionPane.showMessageDialog(null, "Já existe um arquivo no diretório " + jTextFieldDiretorio.getText() + " com o nome " + arquivos_da_pasta[i] + "!\nApague-o ou então escolha um outro nome de arquivo.", colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);

                //exibe um JOptionPane questionando o usuario se deseja sobescrever o arquivo de nome ja existente, ou se deseja desistir:
                Object[] options = { colecaomsgs.getString("RESPOSTA_SIM"), colecaomsgs.getString("RESPOSTA_NAO") };
                int n = JOptionPane.showOptionDialog(null, //"Já existe um arquivo no diretório "                         //" com o nome "                                          //=!\nDeseja salvar por cima dele? (obs: o conteúdo do arquivo antigo será perdido)"
                                                    colecaomsgs.getString("PERGUNTA1") + jTextFieldDiretorio.getText() + colecaomsgs.getString("PERGUNTA2") + arquivos_da_pasta[i] + colecaomsgs.getString("PERGUNTA3"),
                                                    colecaomsgs.getString("Alerta"), JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (n!=0) //se o usuario nao deseja que o arquivo seja salvo por cima:
                {   //exibe uma mensagem de recomendacao e retorna falso. "Escolha um outro nome de arquivo ou apague-o, e tente novamente."
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("AVISO1"), colecaomsgs.getString("Aviso"), JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        }
    }

    return true; //se tudo esta ok retorna true
}



private void jCheckBoxMostrarDiasReqItemStateChanged(java.awt.event.ItemEvent evt) {


      //pega o painel do grafico atual sendo mostrado na tela:
    JPanel PainelDoGrafico = (JPanel)PainelAbasGraficos.getSelectedComponent();

    //Grafico_ recebe qual é o grafico do painel atual mostrado na tela:
    Grafico Grafico_ = null;
    if (PainelAbasGraficos.getSelectedIndex()==0)   Grafico_ = GraficoCompGalhos;
    if (PainelAbasGraficos.getSelectedIndex()==1)   Grafico_ = GraficoEmissaoFolhas;
    if (PainelAbasGraficos.getSelectedIndex()==2)   Grafico_ = GraficoQuedaFolhas;
    if (PainelAbasGraficos.getSelectedIndex()==3)   Grafico_ = GraficoAreaFoliar;
    if (PainelAbasGraficos.getSelectedIndex()==4)   Grafico_ = GraficoNumeroMetameros;
    if (PainelAbasGraficos.getSelectedIndex()==5)   Grafico_ = GraficoTamanhoFolhas;

    //tenta desenha este grafico:
           try {
            Grafico_.desenhaGraficoEm(PainelDoGrafico, INTERNACIONALIZACAO, ListaMTGsAGerar, jCheckBoxMostrarDiasReq.isSelected());
        } catch (InterruptedException ex) {
            Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
        }


}

private void jCheckBoxNomenclaturaDiaItemStateChanged(java.awt.event.ItemEvent evt) {

        //variáveis 'nome_arq1_sem_ext' e 'nome_arq2_sem_ext' recebem o nome dos dois arquivo sem extensao.
    String nome_arq1_sem_ext = MTGbaseInicial.getNomeArquivoSemExtenxao();
    String nome_arq2_sem_ext = MTGbaseFinal.getNomeArquivoSemExtenxao();

    //string onde sera gerada o nome do arquivo do estagio a ser inserido:
    String nomenclatura;

         //se checkbox p/ ativar a insercao de dias automatico na nomenclatura esta ativado:
     if (jCheckBoxNomenclaturaDia.isSelected()==true)
     {
         if (jTextFieldDiaEstagio.getText().length()>0)
             //gera a nomenclatura: (sempre que o texto for alterado no jTextFieldDiaEstagio, o nome do arquivo sera renovado)
             nomenclatura = nome_arq1_sem_ext +  "-" + nome_arq2_sem_ext + "-" + jTextFieldDiaEstagio.getText() + ".xls";
         else
             nomenclatura = nome_arq1_sem_ext +  "-" + nome_arq2_sem_ext + ".xls"; //gera a mesma nomenclatura (vazia, sem o "hifen")

        jTextFieldNomeEstagio.setText(nomenclatura); //insere o nome de arquivo (nomenclatura) no textfield
     }

}

private void jCheckBoxNomenclaturaDiaBItemStateChanged(java.awt.event.ItemEvent evt) {

    //variáveis 'nome_arq1_sem_ext' e 'nome_arq2_sem_ext' recebem o nome dos dois arquivo sem extensao.
    String nome_arq1_sem_ext = MTGbaseInicial.getNomeArquivoSemExtenxao();
    String nome_arq2_sem_ext = MTGbaseFinal.getNomeArquivoSemExtenxao();

    //string onde sera gerada o nome do arquivo do estagio a ser inserido:
    String nomenclatura;

         //se checkbox p/ ativar a insercao de dias automatico na nomenclatura esta ativado:
     if (jCheckBoxNomenclaturaDiaB.isSelected()==true)

     if (jCheckBoxNomenclaturaDiaB.isSelected())
     {
         if (jTextFieldDiaEstagioAAlterar.getText().length()>0)
             //gera a nomenclatura: (sempre que o texto for alterado no jTextFieldDiaEstagio, o nome do arquivo sera renovado)
             nomenclatura = nome_arq1_sem_ext +  "-" + nome_arq2_sem_ext + "-" + jTextFieldDiaEstagioAAlterar.getText() + ".xls";
         else
             nomenclatura = nome_arq1_sem_ext +  "-" + nome_arq2_sem_ext + ".xls"; //gera a mesma nomenclatura (vazia, sem o "hifen")

        jTextFieldNomeEstagioAAlterar.setText(nomenclatura); //insere o nome de arquivo (nomenclatura) no textfield
     }

}

private void MenuItemInglesItemStateChanged(java.awt.event.ItemEvent evt) {

    if (MenuItemIngles.getState()==true)
    {
        INTERNACIONALIZACAO = 1; //"INGLES"

        locale = new Locale("en","US");
        colecaomsgs  = ResourceBundle.getBundle("Messages", locale);
        colecaomsgsgui  = ResourceBundle.getBundle("MessagesGUI", locale);

        atualizarTextoComponentes();
    }

    //GraficoAreaFoliar.desenhaGraficoEm(jPainelGrafico1, INTERNACIONALIZACAO, ListaMTGsAGerar, jCheckBoxMostrarDiasReq.isSelected());

}

private void MenuItemFrancesItemStateChanged(java.awt.event.ItemEvent evt) {

    if (MenuItemFrances.getState()==true)
    {
        INTERNACIONALIZACAO = 2; //"FRANCES"

        locale = new Locale("fr","FR");
        colecaomsgs  = ResourceBundle.getBundle("Messages", locale);
        colecaomsgsgui  = ResourceBundle.getBundle("MessagesGUI", locale);

        atualizarTextoComponentes();
    }


   //GraficoAreaFoliar.desenhaGraficoEm(jPainelGrafico1, INTERNACIONALIZACAO, ListaMTGsAGerar, jCheckBoxMostrarDiasReq.isSelected());
}

private void PainelComAbaMouseClicked(java.awt.event.MouseEvent evt) {

    if (PainelComAba.getSelectedIndex()==2)
    {


    //pega o painel do grafico atual sendo mostrado na tela:
    JPanel PainelDoGrafico = (JPanel)PainelAbasGraficos.getSelectedComponent();

    //Grafico_ recebe qual é o grafico do painel atual mostrado na tela:
    Grafico Grafico_ = null;
    if (PainelAbasGraficos.getSelectedIndex()==0)   Grafico_ = GraficoCompGalhos;
    if (PainelAbasGraficos.getSelectedIndex()==1)   Grafico_ = GraficoEmissaoFolhas;
    if (PainelAbasGraficos.getSelectedIndex()==2)   Grafico_ = GraficoQuedaFolhas;
    if (PainelAbasGraficos.getSelectedIndex()==3)   Grafico_ = GraficoAreaFoliar;

    if (PainelAbasGraficos.getSelectedIndex()==4)   Grafico_ = GraficoNumeroMetameros;
    if (PainelAbasGraficos.getSelectedIndex()==5)   Grafico_ = GraficoTamanhoFolhas;

    //tenta desenha este grafico:
           try {
            Grafico_.desenhaGraficoEm(PainelDoGrafico, INTERNACIONALIZACAO, ListaMTGsAGerar, jCheckBoxMostrarDiasReq.isSelected());
        } catch (InterruptedException ex) {
            Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

private void jDialogBarraDeProgressoComponentShown(java.awt.event.ComponentEvent evt) {
    
}

private void jButtonBuscarDiretorioActionPerformed(java.awt.event.ActionEvent evt) {

    JFileChooser fc = new JFileChooser(); //cria um "escolhedor de diretorio"
    //seta os textos presentes neste JFileChooser:
    fc.setDialogTitle(colecaomsgs.getString("Buscar_diretorio"));

    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //seta o modo do file chooser para selecionar apenas diretorios

    int status = fc.showOpenDialog(null); //este escolhedor de diretorio abrirá uma janela de "abertura de diretorios"

    if (status == JFileChooser.APPROVE_OPTION) //se um diretorio foi escolhido:
        jTextFieldDiretorio.setText(fc.getSelectedFile().getAbsolutePath()); //seta este textfield com o "caminho do diretorio" aberto pelo file chooser.

}

private void jButtonInserirConj3DActionPerformed(java.awt.event.ActionEvent evt) {

    String str_nome_do_arquivo;
    String str_dia;

    int linha_selecionada = jTableMTGsExistentes.getSelectedRow();

    if (linha_selecionada > -1) //se alguma linha esta selecionada:
    {
         //pega os dados da linha selecionada (nome do arquivo e dia):
         str_nome_do_arquivo = ModeloTabelaMTGsExistentes.getValueAt(jTableMTGsExistentes.getSelectedRow(), 0).toString();
         str_dia = ModeloTabelaMTGsExistentes.getValueAt(jTableMTGsExistentes.getSelectedRow(), 1).toString();

         //insere o nome do arquivo e dia do "estagio selecionado" ordenado na tabela de conjunto p/ exibicao 3D:
         InsereOrdenadoNaTabela(ModeloTabelaConjuntoPara3D, str_nome_do_arquivo, str_dia);

         //remove a linha da tabela fonte:
         ModeloTabelaMTGsExistentes.removeRow(linha_selecionada);

         //Para facilitar ao usuario, tenta deixar a linha anterior (da linha q estava anteriormente selecionada) ja selecionada na tabela:
         if (jTableMTGsExistentes.getRowCount() > 0) //se existir elemento na tabela fonte:
         {

             if (linha_selecionada == 0)    //se a linha que havia sido selecionada previamente era a primeira da tabela:
                 jTableMTGsExistentes.setRowSelectionInterval(0, 0); //deixa a primeira linha selecionada da tabela (pq nao ha nenhuma anterior à linha_selecionada.
             else   //se a linha que havia sido selecionada nao foi a primeira tabela (foi alguma adiante)
                 jTableMTGsExistentes.setRowSelectionInterval(linha_selecionada-1, linha_selecionada-1); //entao deixa agora selecionada a anterior à que tinha sido selecionada antes.

            //tambem deixa o botao p/ inserir no conjunto 3D ja selecionado (p/ facilitar p/ o usuario):
            jButtonInserirConj3D.setSelected(true);
         }

         if (jTableConjuntoPara3D.getRowCount() > 0 && GeradorConfigIni.isAMAPmodAtivado()) //se existir algum MTG na tabela de Conjunto para 3D:
             jButtonVisualizarConj3D.setEnabled(true); //seta o botao p/ visualizacao como ativo
         else
             jButtonVisualizarConj3D.setEnabled(false); //caso contrario seta como falso
    }
}


/* Procecimendo para inserir um estagio (seu nome de arquivo e dia) ordenado numa tabela
 *
 *      Recebe por parametro:
                    ModeloTabela:       modelo da tabela aonde sera inserido o elemento
 *                  nome_arquivo:       nome do arquivo do estagio a ser inserido
 *                  dia:                dia do estagio a ser inserido
 */
private void InsereOrdenadoNaTabela(DefaultTableModel ModeloTabela, String nome_arquivo, String dia)
{
    //se a tabela esta inteiramente vazia:
    if (ModeloTabela.getRowCount()==0)
    {
        ModeloTabela.insertRow(0, new Object[] {}); //cria uma nova linha no modelo
        //insere o nome do arquivo e dia nesta linha:
        ModeloTabela.setValueAt(nome_arquivo, 0, 0);
        ModeloTabela.setValueAt(dia, 0, 1);
    }
    else //senao:

    for (int i=0; i<ModeloTabela.getRowCount(); i++)  //percorre todas as linhas da tabela
    {

        //Se valor lido na tabela eh maior que o dia a inserir, entao insere ordenado na tabela:
        if (Integer.parseInt(ModeloTabela.getValueAt(i, 1).toString())>Integer.parseInt(dia))
        {
            ModeloTabela.insertRow(i, new Object[] {}); //cria uma nova linha no modelo na posicao "i"
            //insere o nome do arquivo e dia nesta posicao:
            ModeloTabela.setValueAt(nome_arquivo, i, 0);
            ModeloTabela.setValueAt(dia, i, 1);
            break; //pode sair do loop
        }

        //"CONDICAO ESSENCIAL SE O ELEMENTO A SER INSERIDO DEVE SER O ULTIMO DA LISTA":
        if (i==ModeloTabela.getRowCount()-1) //se estiver na ultima posicao da lista
        {
            //e se o valor encontrado do dia for menor que o valor a inserir:
            if (Integer.parseInt(ModeloTabela.getValueAt(i, 1).toString())<Integer.parseInt(dia))
            {
                ModeloTabela.insertRow(i+1, new Object[] {}); //cria uma nova linha no final
                //insere o nome do arquivo e dia nesta posicao:
                ModeloTabela.setValueAt(nome_arquivo, i+1, 0);
                ModeloTabela.setValueAt(dia, i+1, 1);
            }
        }
    }
}



private void jCheckBoxIncluirEstBasesItemStateChanged(java.awt.event.ItemEvent evt) {

    if (jCheckBoxIncluirEstBases.isSelected()) //se checkbox foi ativado:
    {

         /* ***** Insercao dos estagios base na tabela de MTGs existentes: ***** */

        //cria uma linha no inicio da tabela de MTGs existentes:
        ModeloTabelaMTGsExistentes.insertRow(0, new Object[] {}); //cria uma nova linha no modelo
        //insere o nome do arquivo e dia do estagio base na primeira linha:
        ModeloTabelaMTGsExistentes.setValueAt(MTGbaseInicial.getNomeArquivo(), 0, 0);
        ModeloTabelaMTGsExistentes.setValueAt("0", 0, 1);

        //cria uma linha no final da tabela de MTGs existentes:
        ModeloTabelaMTGsExistentes.addRow(new Object[] {}); //cria uma nova linha no modelo
        //insere o nome do arquivo e dia do estagio final na ultima linha:
        ModeloTabelaMTGsExistentes.setValueAt(MTGbaseFinal.getNomeArquivo(), ModeloTabelaMTGsExistentes.getRowCount()-1, 0);
        ModeloTabelaMTGsExistentes.setValueAt(periodo.getDiferencaDias(), ModeloTabelaMTGsExistentes.getRowCount()-1, 1);
        /* ********************************************************************* */
    }
    else //se checkbox foi desativado:
    {



       if (jTableMTGsExistentes.getRowCount()>0)
       {
            if (Integer.parseInt(ModeloTabelaMTGsExistentes.getValueAt(0, 1).toString()) == 0)
                ModeloTabelaMTGsExistentes.removeRow(0);
       }

       if (jTableMTGsExistentes.getRowCount()>0)
       {
            if (Integer.parseInt(ModeloTabelaMTGsExistentes.getValueAt(jTableMTGsExistentes.getRowCount()-1, 1).toString()) == periodo.getDiferencaDias());
                ModeloTabelaMTGsExistentes.removeRow(jTableMTGsExistentes.getRowCount()-1);
       }

       if (jTableConjuntoPara3D.getRowCount()>0)
       {
            if (Integer.parseInt(ModeloTabelaConjuntoPara3D.getValueAt(0, 1).toString()) == 0)
            {
                ModeloTabelaConjuntoPara3D.removeRow(0);
            }
       }

       if (jTableConjuntoPara3D.getRowCount()>0)
       {
           if (Integer.parseInt(ModeloTabelaConjuntoPara3D.getValueAt(jTableConjuntoPara3D.getRowCount()-1, 1).toString()) == periodo.getDiferencaDias())
           {
              ModeloTabelaConjuntoPara3D.removeRow(jTableConjuntoPara3D.getRowCount()-1);
           }
       }

    }



    if (jTableConjuntoPara3D.getRowCount() > 0) //se existir algum MTG na tabela de Conjunto para 3D:
        jButtonVisualizarConj3D.setEnabled(true); //seta o botao p/ visualizacao como ativo
    else
        jButtonVisualizarConj3D.setEnabled(false); //caso contrario seta como falso


}

private void jButtonRemoverConj3DActionPerformed(java.awt.event.ActionEvent evt) {

    String str_nome_do_arquivo;
    String str_dia;

    int linha_selecionada = jTableConjuntoPara3D.getSelectedRow();

    if (linha_selecionada > -1) //se alguma linha esta selecionada:
    {
         //pega os dados da linha selecionada (nome do arquivo e dia):
         str_nome_do_arquivo = ModeloTabelaConjuntoPara3D.getValueAt(jTableConjuntoPara3D.getSelectedRow(), 0).toString();
         str_dia = ModeloTabelaConjuntoPara3D.getValueAt(jTableConjuntoPara3D.getSelectedRow(), 1).toString();

         //insere o nome do arquivo e dia do "estagio selecionado" ordenado na tabela de MTGs existentes:
         InsereOrdenadoNaTabela(ModeloTabelaMTGsExistentes, str_nome_do_arquivo, str_dia);

         //remove a linha da tabela fonte:
         ModeloTabelaConjuntoPara3D.removeRow(linha_selecionada);

         //Para facilitar ao usuario, tenta deixar a linha anterior (da linha q estava anteriormente selecionada) ja selecionada na tabela:
         if (jTableConjuntoPara3D.getRowCount() > 0) //se existir elemento na tabela fonte:
         {

             if (linha_selecionada == 0)    //se a linha que havia sido selecionada previamente era a primeira da tabela:
                 jTableConjuntoPara3D.setRowSelectionInterval(0, 0); //deixa a primeira linha selecionada da tabela (pq nao ha nenhuma anterior à linha_selecionada.
             else   //se a linha que havia sido selecionada nao foi a primeira tabela (foi alguma adiante)
                 jTableConjuntoPara3D.setRowSelectionInterval(linha_selecionada-1, linha_selecionada-1); //entao deixa agora selecionada a anterior à que tinha sido selecionada antes.

            //tambem deixa o botao p/ remover do conjunto 3D ja selecionado (p/ facilitar p/ o usuario):
            jButtonRemoverConj3D.setSelected(true);
         }


         if (jTableConjuntoPara3D.getRowCount() > 0 && GeradorConfigIni.isAMAPmodAtivado()) //se existir algum MTG na tabela de Conjunto para 3D:
             jButtonVisualizarConj3D.setEnabled(true); //seta o botao p/ visualizacao como ativo
         else
             jButtonVisualizarConj3D.setEnabled(false); //caso contrario seta como falso
    }
}

private void jButtonProcessarInterpolacao(java.awt.event.ActionEvent evt) {

    //cada um destes graficos cria uma lista de todos os pixels que criam a sua reta (pois sera utilizada adiante)
    GraficoCompGalhos.CriarListaTodosOsPontos();
    GraficoEmissaoFolhas.CriarListaTodosOsPontos();
    GraficoQuedaFolhas.CriarListaTodosOsPontos();
    GraficoAreaFoliar.CriarListaTodosOsPontos();

    GraficoNumeroMetameros.CriarListaTodosOsPontos();
    GraficoTamanhoFolhas.CriarListaTodosOsPontos();

    //lista para guardar os valores lidos nos graficos (cada posicao do vetor é um dia. Lista é crescente para os dias a serem gerados).
    //double compgalhos_no_est[] = null;

    ArrayList<AtributosGrafico> ListaAtribGraficos =  new ArrayList<AtributosGrafico>();

    double comp_galhos_acumulado = MTGbaseInicial.getPlanta().getCompTotalGalhos();
    int    emissao_folhas_acumulado = 0;
    int    queda_folhas_acumulado = 0;
    double area_foliar_acumulado = MTGbaseInicial.getPlanta().getTotalAreaFoliar();

    // Novos atributos
    int num_entrenos_acumulado = MTGbaseInicial.getPlanta().getQtdeEntrenos();
    double tamanho_folhas_acumulado = MTGbaseInicial.getPlanta().getTamanhoMedioFolhas();

    //percorre a lista de MTGs a serem gerados, e entao resgata os valores no grafico de cada parametro para cada respectivo dia:
    for (int estagio=0; estagio < ModeloTabelaMTGsAGerar.getRowCount(); estagio++)
    {
        //percorre a lista de estagios a serem gerados e pega o dia de cada estagio:
        int dia  = Integer.parseInt(ModeloTabelaMTGsAGerar.getValueAt(estagio, 1).toString());

        double compgalhos = GraficoCompGalhos.descobrirValorParaDia(dia) -  comp_galhos_acumulado;
        int    emissaofolhas = (int)GraficoEmissaoFolhas.descobrirValorParaDia(dia) - emissao_folhas_acumulado;
        int    quedafolhas = (int)GraficoQuedaFolhas.descobrirValorParaDia(dia) - queda_folhas_acumulado;
        double areafoliar = GraficoAreaFoliar.descobrirValorParaDia(dia) -  area_foliar_acumulado;

        // Novos atributos
        int numentrenos = (int)GraficoNumeroMetameros.descobrirValorParaDia(dia) - num_entrenos_acumulado;
        double tamanhofolhas = GraficoTamanhoFolhas.descobrirValorParaDia(dia) - tamanho_folhas_acumulado;

        //para este dia, resgata os valores dos parametros e guarda-os na lista de atributos (cada posicao da lista é p/ um dia)
        ListaAtribGraficos.add(new AtributosGrafico(compgalhos, emissaofolhas, quedafolhas, areafoliar, numentrenos,tamanhofolhas));

        comp_galhos_acumulado = comp_galhos_acumulado + compgalhos;
        emissao_folhas_acumulado = emissao_folhas_acumulado + emissaofolhas;
        queda_folhas_acumulado = queda_folhas_acumulado + quedafolhas;
        area_foliar_acumulado = area_foliar_acumulado + areafoliar;
        num_entrenos_acumulado = num_entrenos_acumulado + numentrenos;
        tamanho_folhas_acumulado = tamanho_folhas_acumulado + tamanhofolhas;

    }

    try {

            Interp = new Interpolacao(ModeloTabelaMTGsAGerar, MTGbaseInicial, MTGbaseFinal, periodo, jTextFieldDiretorio.getText(), ListaAtribGraficos);
            
            

            Interp.executar();

            //desativa a terceira aba e vai para a quarta (iniciando ETAPA 4):
            PainelComAba.setEnabledAt(2, false);
            PainelComAba.setEnabledAt(3, true); //desativa a aba "Relatorio"
            PainelComAba.setEnabledAt(4, true); //desativa a aba "Exibicao 3D"


            PainelComAba.setSelectedIndex(3);

            /* ******************************************************************** */
            //O CODIGO ABAIXO É PARA AJUSTAR AS TABELAS NO PROXIMO PAINEL:
            //copia o modelo que estava na tabela de MTGs a gerar p/ um outro modelo (a ser inserido na tabela de MTGs existentes):
            ModeloTabelaMTGsExistentes = ModeloTabelaMTGsAGerar;

            //inserir o modelo na tabela de MTGs existentes:
            jTableMTGsExistentes.setModel(ModeloTabelaMTGsExistentes);


            /* ***** Insercao dos estagios base na tabela de MTGs existentes: ***** */

            //cria uma linha no inicio da tabela de MTGs existentes:
            ModeloTabelaMTGsExistentes.insertRow(0, new Object[] {}); //cria uma nova linha no modelo
            //insere o nome do arquivo e dia do estagio base na primeira linha:
            ModeloTabelaMTGsExistentes.setValueAt(MTGbaseInicial.getNomeArquivo(), 0, 0);
            ModeloTabelaMTGsExistentes.setValueAt("0", 0, 1);

            //cria uma linha no final da tabela de MTGs existentes:
            ModeloTabelaMTGsExistentes.addRow(new Object[] {}); //cria uma nova linha no modelo
            //insere o nome do arquivo e dia do estagio final na ultima linha:
            ModeloTabelaMTGsExistentes.setValueAt(MTGbaseFinal.getNomeArquivo(), ModeloTabelaMTGsExistentes.getRowCount()-1, 0);
            ModeloTabelaMTGsExistentes.setValueAt(periodo.getDiferencaDias(), ModeloTabelaMTGsExistentes.getRowCount()-1, 1);
            /* ********************************************************************* */


    //inserir o modelo na tabela de conjunto p/ exibicao 3D (obs: modelo esta inicialmente vazio)
    jTableConjuntoPara3D.setModel(ModeloTabelaConjuntoPara3D);

        } catch (IOException ex) {
            Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WriteException ex) {
            Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
        }
}


private void jButtonVisualizarNoAMAPmod1ActionPerformed(java.awt.event.ActionEvent evt) {

    try
    {
       VisualizarAMAPmodPrimeiraTela(jTextFieldEstInicial); //tenta visualizar o arquivo (do estágio inicial) que esta no jtextfield no AMAPmod.
    } catch (WriteException ex)
    {
       Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
    }
}


// Função para visualização da reconstrução 3D da erva-mate
// Primeira tela
private void VisualizarAMAPmodPrimeiraTela(JTextField jTextField) throws WriteException
{
    File arquivo_do_textfield = new File(jTextField.getText());

    Workbook workbook_temp;
    WritableWorkbook workbook_fonte;
    Sheet sheet_fonte;

    File config_ini = new File("config.ini");

    if (arquivo_do_textfield.exists() == false) { //"Arquivo não existe!"
        JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO28") , colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
    }
    else // Se o arquivo existe
    {
        String nome_arq = arquivo_do_textfield.getName(); //extrai apenas o "nome do arquivo" (sem o caminho)
        String extensao_do_arquivo = nome_arq.substring(nome_arq.length()-3); //extrai a extensao deste arquivo

        
        
        if (extensao_do_arquivo.equals("mtg") || extensao_do_arquivo.equals("xls"))
        {
            String caminho_mtg = null;

            // Se a extensão for .xls, é necessário converter para .mtg
            if (extensao_do_arquivo.equals("xls"))
            {
                //cria uma copia do workbook para escrita (p/ pode realizar algumas alteracoes antes de ser percorrido para leitura):
                try {
                        //workbook_fonte recebe o conteudo do arquivo xls:
                        workbook_temp = Workbook.getWorkbook(arquivo_do_textfield); //abre o arquivo xls contendo a planilha a ser lida
                        //cria uma copia do workbook para escrita (p/ pode realizar algumas alteracoes antes de ser percorrido para leitura):
                        workbook_fonte = Workbook.createWorkbook(new File("temp/temp_leitura.xls"), workbook_temp);
                        //sheet_fonte eh a planilha (a primeira) do arquivo:
                        sheet_fonte = workbook_fonte.getSheet(0);

                        // O arquivo "temp.mtg" é o arquivo XLS convertido para MTG
                        ConversorXLSParaTexto conversorxlspmtg = new ConversorXLSParaTexto("temp/temp.mtg", sheet_fonte);
                        conversorxlspmtg.converte();

                        //fecha workbooks e apaga arquivo temporario xls p/ conversao:
                        workbook_fonte.close();
                        workbook_temp.close();
                        File templeitura = new File("temp/temp_leitura.xls");
                        if (templeitura.exists()) templeitura.delete();

                } catch (IOException ex) {
                    Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BiffException ex) {
                    Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                }

               File arquivo_tempmtg = new File("temp/temp.mtg"); //captura o arquivo "temp.mtg"
               if (arquivo_tempmtg.exists() == false) //"Não foi possível visualizar o arquivo "   //"! Erro na conversão de XLS para MTG."
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO29") + arquivo_do_textfield.getName() + colecaomsgs.getString("ERRO30"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
               else
               {
                   caminho_mtg = arquivo_tempmtg.getAbsolutePath(); //pega o caminho absoluto deste arquivo
                   arquivo_tempmtg.deleteOnExit(); //apaga este arquivo qdo o aplicativo java for finalizado
               }
            }
            else caminho_mtg = jTextField.getText();

            
            // ************************************************************
            // * Aqui já tem o diretório do arquivo MTG a ser processado
            // * Agora cria-se o arquivo para processamento
            // ************************************************************
            //System.out.println(" Caminho do mtg: " + caminho_mtg);
            
            BufferedReader br; //criar um leitor para percorrer o conteudo do arquivo
            try
            {
                br = new BufferedReader(new FileReader("config.ini"));
                String linha = "";
                try
                {
                    linha = br.readLine();
                    
                    // ****************************************
                    // * Se o AMAPmod estiver integrado
                    // ****************************************
                    if (linha.startsWith("INTEGRARAMAPMOD")) 
                    {
                        //cria um novo arquivo chamado amlparte1.aml, p/ escrever o caminho do MTG (que sera parte do codigo AML)
                        PrintWriter prwriter = null;
                        try {
                            prwriter = new PrintWriter(new FileOutputStream("amlparte1.aml"));
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //imprime no arquivo amlparte1.aml:
                        prwriter.println("EchoOn()\n\ng=MTG(" + ASPAS + caminho_mtg.replaceAll("" + ASPAS, "") + ASPAS + ")\n");
                        prwriter.close(); //fecha o printwriter
                        Runtime rt = Runtime.getRuntime();
                        String caminho_amapmod = GeradorConfigIni.getCaminhoAMAPmod();
                        try {
                            /*caminho_amapmod recebe o caminho do executavel do amapmod com o ":" trocado por ":\"
                            Exemplo: de c:\Arquivos de Programas\aml.exe... para c:\\Arquivos de Programas\aml.exe... */
                            caminho_amapmod = ArrumarCaminhoPadraoWindows(caminho_amapmod);
                            Process proc = rt.exec("cmd.exe /c start " + caminho_amapmod + " +i amlparte1.aml amlparte2-win.aml amlparte3cf.aml");
                        } catch (Exception e) {
                            // Nao conseguiu o caminho do Windows
                            // Tenta no Linux
                            try {
                                Process proc = rt.exec("xterm -e " + caminho_amapmod.replaceAll("" + ASPAS, "") + " +i amlparte1.aml amlparte2-lnx.aml amlparte3cf.aml");
                            } catch (Exception e2) {
                                //Nao conseguiu o caminho do Linux //"Extensão do arquivo não é válida!\nExtensão necessita ser MTG ou XLS."
                                JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO31"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }

                    // ****************************************
                    // * Se for o VPlants integrado (Python)
                    // ****************************************
                    else if (linha.startsWith("INTEGRAVPLANTS"))
                    {
                        //JOptionPane.showMessageDialog(null, "Integração do VPlants em construção...", "VPlants", JOptionPane.WARNING_MESSAGE);
                        String caminho_python = GeradorConfigIni.getCaminhoAMAPmod();

                        File arq_py = new File("python_files/modelo_ervamate_3D.py");
                        PrintWriter script_python = new PrintWriter(new FileOutputStream(arq_py));
                        InputStream is = new FileInputStream("python_files/estrutura_3D.py");
                        InputStream is_folhas = new FileInputStream("python_files/folhas_3D.py");
                        String pasta_scripts_py = arq_py.getAbsolutePath().replace("modelo_ervamate_3D.py", "").replaceAll("\\\\", "/");

                        //InputStreamReader é uma classe para converter os bytes em char
                        InputStreamReader isr = new InputStreamReader(is);
                        InputStreamReader isr_folhas = new InputStreamReader(is_folhas);

                        //BufferedReader é uma classe para armazenar os chars em memoria
                        BufferedReader buffer = new BufferedReader(isr);
                        BufferedReader buffer2 = new BufferedReader(isr_folhas);

                        // Escrevendo o cabeçalho no arquivo modelo_ervamate_3D.py
                        script_python.println("from openalea.aml import *");
                        script_python.println("from math import *");

                        // Escrevendo o caminho do arquivo do MTG a ser processado
                        script_python.println("\nmtg_ervamate=MTG(" + ASPAS + caminho_mtg.replaceAll("\\\\", "/") + ASPAS + ")\n");

                        // Escrevendo a parte para armazenar um vetor com todas as plantas e para leitura do DressingData
                        System.out.println("Caminho do arquivo: " + arq_py.getParent());
                        script_python.println("\n# Vetor com todas as plantas do MTG");
                        script_python.println("plantas = VtxList(Scale=1)");
                        script_python.println("\ndress = DressingData(" + ASPAS + pasta_scripts_py + "ervamate.drf" + ASPAS + ")\n");
                        
                        // Montando o script para processamento do MTG
                        // Escrevendo as demais partes do script no arquivo
                        String s = buffer.readLine(); //primeira linha
                        script_python.println(s);

                        // Parte do script que faz leitura da estrutura da planta
                        while (s != null){
                            //System.out.println(s);
                            s = buffer.readLine();
                            if (s!=null) script_python.println(s);
                        }

                        // Parte do script que faz leitura dos atributos foliares
                        s = buffer2.readLine(); //primeira linha
                        script_python.println(s);
                        while (s != null)
                        {
                            //System.out.println(s);
                            s = buffer2.readLine(); //primeira linha
                            if (s!=null) script_python.println(s);
                        }
                        
                        script_python.println("# Plota a planta da erva-mate em 3D");
                        script_python.println("Plot(plant_frame, VirtualLeaves=folha_virtual, DressingData=dress)");
                        
                        script_python.append("raw_input(\"Pressione qualquer tecla para continuar...\")");

                        script_python.close();
                        isr.close();
                        isr_folhas.close();
                        
                        // Executa o script em Python
                        Runtime runtime = Runtime.getRuntime();                      
                        Process process = runtime.exec("cmd.exe /c start " + caminho_python + " python_files/modelo_ervamate_3D.py");
                    }

                } catch (IOException ex) { Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex); }
            } catch (FileNotFoundException ex) { Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex); }
            
        } //Extensão do arquivo não é válida!\nExtensão necessita ser MTG ou XLS.
        else JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO31"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
    }
}


// Função para o menu de integração do AMAPmod/VPlants
private void jMenuItemIntegrarActionPerformed(java.awt.event.ActionEvent evt) {

    jDialogIntegracaoAMAPmod.setLocation(300, 300);
    jDialogIntegracaoAMAPmod.setVisible(true);

    // Arquivo config.ini para armazenar o diretorio do programa a ser integrado
    File config_ini = new File("config.ini");

    //verifica se existe o arquivo config.ini na pasta do programa:
    if (config_ini.exists() == false) //se nao existir:
    {
        //cria um novo arquivo chamado config.ini p/ guardar configuracoes de integracao com AMAPMOD:
        PrintWriter prwriter = null;
        try
        {
            prwriter = new PrintWriter(new FileOutputStream("config.ini")); //ira criar o arquivo a ser escrito com nome config.ini
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
        }
        prwriter.println("INTEGRARAMAPMOD=NAO"); //imprime "INTEGRARAMAPMOD=NAO" neste arquivo (como default)
        prwriter.close(); //fecha o writer
    }


    // Começa a leitura do MTG
    try
    {
        BufferedReader br = new BufferedReader(new FileReader("config.ini")); //criar um leitor para percorrer o conteudo do arquivo
        String linha = null;
        try
        {
           linha = br.readLine(); //captura a primeira linha deste arquivo
           if (linha.substring(16).compareTo("SIM")==0  || linha.substring(15).compareTo("SIM")==0  ) // Se VPlants estiver integrados
           {
               linha = br.readLine(); // Segunda linha do arquivo
               if (linha.startsWith("INTEGRARAMAPMOD")) System.out.println("\nAMAPmod integrado\n");
               else System.out.println("\nVPlants integrado\n");

               jCheckBoxAtivacaoAMAPmod.setSelected(true);
               jTextFieldCaminhoAMAPmod.setEnabled(true);
               jTextFieldCaminhoAMAPmod.setEditable(true);
               jTextFieldCaminhoAMAPmod.setText(linha.substring(8));
               jButtonBuscarAMAPmod.setEnabled(true);
           }
           else
           {
               jCheckBoxAtivacaoAMAPmod.setSelected(false);

               jTextFieldCaminhoAMAPmod.setText("");
               jTextFieldCaminhoAMAPmod.setEnabled(false);
               jTextFieldCaminhoAMAPmod.setEditable(false);

               jButtonBuscarAMAPmod.setEnabled(false);
           }
        }
        catch (IOException ex)
        {
          Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    catch (FileNotFoundException ex)
    {
        Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
    }

}





private void jCheckBoxAtivacaoAMAPmodItemStateChanged(java.awt.event.ItemEvent evt) {
    //se o checkbox p/ a integracao c/ AMAPmod foi ativado:
    if (jCheckBoxAtivacaoAMAPmod.isSelected())
    {   //ativa o textbox p/ configuracao do caminho do AMAPmod:
        jTextFieldCaminhoAMAPmod.setEnabled(true);
        jTextFieldCaminhoAMAPmod.setEditable(true);
        //ativa o botao p/ buscar o caminho do AMAPmod:
        jButtonBuscarAMAPmod.setEnabled(true);
    }
    else //se o checkbox p/ a integracao c/ AMAPmod foi desativado:
    {  //desativa o textbox p/ configuracao do caminho do AMAPmod:
        jTextFieldCaminhoAMAPmod.setEnabled(false);
        jTextFieldCaminhoAMAPmod.setEditable(false);
        //desativa o botao p/ buscar o caminho do AMAPmod:
        jButtonBuscarAMAPmod.setEnabled(false);
    }
}

private void jButtonCancJanelaIntAMAPmodActionPerformed(java.awt.event.ActionEvent evt) {
    //qdo clicado no botao "Cancelar", fecha a janela de configuracao de integracao c/ AMAPmod
    jDialogIntegracaoAMAPmod.setVisible(false);
}

private void jButtonBuscarAMAPmodActionPerformed(java.awt.event.ActionEvent evt) {

            //abrir janela de busca de arquivo
            JFileChooser fc = new JFileChooser(); //cria um "escolhedor de arquivos"
            //seta os textos presentes neste JFileChooser: //"Procure o arquivo executável do AMAPmod..."
            fc.setDialogTitle(colecaomsgs.getString("MENSAGEM1"));

            FileNameExtensionFilter filtro_grf = new FileNameExtensionFilter("*.exe", "exe");  //cria um filtro de arquivo para a extensao "grf" (grafico).
            fc.addChoosableFileFilter(filtro_grf); //adiciona este filtro para o "escolhedor de arquivos" exibir arquivos do tipo "exe".

            int status = fc.showOpenDialog(null); //este escolhedor de arquivos abrirá uma janela de "abertura de arquivos"

            if (status == JFileChooser.APPROVE_OPTION) //se um arquivo foi escolhido:
            {   //se este arquivo se chamar aml:
                if (fc.getSelectedFile().getName().compareTo("aml.exe")==0 || fc.getSelectedFile().getName().compareTo("aml")==0 ||
                    fc.getSelectedFile().getName().compareTo("python.exe")==0 || fc.getSelectedFile().getName().compareTo("python")==0)
                {
                    //insere o caminho na textbox:
                    jTextFieldCaminhoAMAPmod.setText(fc.getSelectedFile().getAbsolutePath());
                }
                else
                {
                    //senao, mostra mensagem de erro: //Arquivo executável a ser encontrado deve se chamar aml.
                    JOptionPane.showMessageDialog(null,colecaomsgs.getString("ERRO32"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                    jTextFieldCaminhoAMAPmod.setText(""); //limpa o textbox
                }
            }
}



private void jButtonAplicarAMAPmodActionPerformed(java.awt.event.ActionEvent evt) {

    if (jCheckBoxAtivacaoAMAPmod.isSelected())
    {

       File arq_amapmod = new File(jTextFieldCaminhoAMAPmod.getText());

       if (arq_amapmod.exists()) //se nao existir:
       {
            if (arq_amapmod.getName().compareTo("aml.exe")==0 || arq_amapmod.getName().compareTo("aml")==0
                || arq_amapmod.getName().compareTo("python.exe")==0  || arq_amapmod.getName().compareTo("python")==0)
            {
                //cria um novo arquivo chamado config.ini p/ guardar configuracoes de integracao com AMAPMOD:
                PrintWriter prwriter = null;
                    try {
                        prwriter = new PrintWriter(new FileOutputStream("config.ini"));
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                    }

                if (arq_amapmod.getName().compareTo("aml.exe")==0 || arq_amapmod.getName().compareTo("aml")==0)
                {
                    //imprime no arquivo config.ini:
                    prwriter.println("INTEGRARAMAPMOD=SIM\nCAMINHO=" + jTextFieldCaminhoAMAPmod.getText());
                    prwriter.close(); //fecha o printwriter
                    //mostra mensagem de sucesso: //"AMAPmod setado com sucesso!"
                    JOptionPane.showMessageDialog(null, "AMAPmod setado com sucesso", "Integração do AMAPmod", JOptionPane.WARNING_MESSAGE);
                }
                else
                {
                    //imprime no arquivo config.ini:
                    prwriter.println("INTEGRAVPLANTS=SIM\nCAMINHO=" + jTextFieldCaminhoAMAPmod.getText());
                    prwriter.close(); //fecha o printwriter
                    //mostra mensagem de sucesso: //"AMAPmod setado com sucesso!"
                    JOptionPane.showMessageDialog(null, colecaomsgs.getString("AVISO2"), colecaomsgs.getString("Aviso"), JOptionPane.WARNING_MESSAGE);
                }
                

                 jTextFieldEstInicialCaretUpdate(null);
                 jTextFieldEstFinalCaretUpdate(null);

                 //if (jTableConjuntoPara3D.getRowCount() > 0)
                     jButtonVisualizarConj3D.setEnabled(true);


                //Fecha a janela de configuracao de integracao c/ AMAPmod:
                jDialogIntegracaoAMAPmod.setVisible(false);
            } //senao, mostra mensagem de erro: //"Arquivo executável a ser encontrado deve se chamar aml."
            else JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO32"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);

       }                                        //"Caminho do arquivo setado no textbox não existe!"
       else JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO33"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);

    }
    else
    {
        //cria um novo arquivo chamado config.ini p/ guardar configuracoes de integracao com AMAPMOD:
        PrintWriter prwriter = null;
            try {
                prwriter = new PrintWriter(new FileOutputStream("config.ini"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
            }
        prwriter.println("INTEGRAVPLANTS=NAO");
        prwriter.close(); //fecha o printwriter

        //desativa todos os botoes de visualizacao no AMAPmod:
        jButtonVisualizarNoAMAPmod1.setEnabled(false);
        jButtonVisualizarNoAMAPmod2.setEnabled(false);
        jButtonVisualizarConj3D.setEnabled(false);

        //Fecha a janela de configuracao de integracao c/ AMAPmod:
        jDialogIntegracaoAMAPmod.setVisible(false);
    }

}

private void jTextFieldEstInicialCaretUpdate(javax.swing.event.CaretEvent evt) {
    File arquivo_mtg = new File(jTextFieldEstInicial.getText());

    if (arquivo_mtg.exists() && GeradorConfigIni.isAMAPmodAtivado())
        jButtonVisualizarNoAMAPmod1.setEnabled(true);
    else
        jButtonVisualizarNoAMAPmod1.setEnabled(false);
}

private void jTextFieldEstFinalCaretUpdate(javax.swing.event.CaretEvent evt) {
    File arquivo_mtg = new File(jTextFieldEstFinal.getText());

    if (arquivo_mtg.exists() && GeradorConfigIni.isAMAPmodAtivado())
        jButtonVisualizarNoAMAPmod2.setEnabled(true);
    else
        jButtonVisualizarNoAMAPmod2.setEnabled(false);
}

private void jButtonVisualizarNoAMAPmod2ActionPerformed(java.awt.event.ActionEvent evt) {

    try
    {
       VisualizarAMAPmodPrimeiraTela(jTextFieldEstFinal); //tenta visualizar o arquivo (do estágio final) que esta no jtextfield no AMAPmod.
    } catch (WriteException ex)
    {
       Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
    }

}


private void jButtonVisualizarConj3DActionPerformed(java.awt.event.ActionEvent evt) {

    ExibicaoConjunto3D exibicao3D = new ExibicaoConjunto3D(ModeloTabelaConjuntoPara3D, jTextFieldDiretorio.getText(), periodo.getDiferencaDias(), jTextFieldEstInicial.getText(), jTextFieldEstFinal.getText());

    // Se o botão de mostrar a planta inteira estiver selecionado
    // habilita a exibição de galhos e folhas na exibicao3D
    if (jRadioButtonPlantaInteira.isSelected())
    {
        exibicao3D.setMostrarFolhagem(true);
        exibicao3D.setMostrarGalhos(true);
    }
    // Senão, somente um dos outros dois pode estar selecionado
    // então habilita um e desabilita o outro ou vice e versa, de acordo com o botão selecionado
    else
    {
        exibicao3D.setMostrarFolhagem(jRadioButtonSomenteFolhas.isSelected());
        exibicao3D.setMostrarGalhos(jRadioButtonSomenteGalhos.isSelected());
    }

    boolean flag_deu_certo=true;

    try {
            flag_deu_certo = exibicao3D.exibir();
        }
    catch (IOException ex) {
            Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WriteException ex) {
            Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
        }

    if (flag_deu_certo==false) //exibe todos os estagios da planta no AMAPmod em série.
        JOptionPane.showMessageDialog(null, "Não foi possível exibir o conjunto 3D no AMAPmod!", "Erro", JOptionPane.WARNING_MESSAGE);
}


private void jMenuItemNovoActionPerformed(java.awt.event.ActionEvent evt) {

    jTextFieldEstInicial.setText("");
    jTextFieldEstFinal.setText("");
    jTextFieldDiretorio.setText("");

    while (ModeloTabelaMTGsAGerar.getRowCount()>0) ModeloTabelaMTGsAGerar.removeRow(0);
    while (ModeloTabelaMTGsExistentes.getRowCount()>0) ModeloTabelaMTGsExistentes.removeRow(0);
    while (ModeloTabelaConjuntoPara3D.getRowCount()>0) ModeloTabelaConjuntoPara3D.removeRow(0);

    jCheckBoxIncluirEstBases.setSelected(true);
    jRadioButtonSomenteGalhos.setSelected(true);


    //if (jComboBoxLogEstagios.getItemCount()>0) jComboBoxLogEstagios.setSelectedIndex(0);
    //if (jComboBoxLogGalhos.getItemCount()>0)   jComboBoxLogGalhos.setSelectedIndex(0);
    jTextAreaLog.setText("");

    //Limpa os graficos:
    GraficoCompGalhos.limpaPontosFixos();
    GraficoEmissaoFolhas.limpaPontosFixos();
    GraficoQuedaFolhas.limpaPontosFixos();
    GraficoAreaFoliar.limpaPontosFixos();

    GraficoNumeroMetameros.limpaPontosFixos();
    GraficoTamanhoFolhas.limpaPontosFixos();

    //seta apenas a primeira aba como ativa e retorna para ela:
    PainelComAba.setEnabledAt(0, true);
    PainelComAba.setEnabledAt(1, false);
    PainelComAba.setEnabledAt(2, false);
    PainelComAba.setEnabledAt(3, false);
    PainelComAba.setEnabledAt(4, false);
    PainelComAba.setSelectedIndex(0); //retorna para a primeira aba
    //ativa o botao para processar interpolacao:
    jButtonProcessarInterpolacao.setEnabled(true);
}


private void jPainel4ComponentShown(java.awt.event.ComponentEvent evt) {
    atualizarRelatorio();
}

private void jButtonSalvarRelatorioActionPerformed(java.awt.event.ActionEvent evt) {

JFileChooser fc = new JFileChooser();
    fc.setDialogTitle(colecaomsgs.getString("Salvar_em"));
    fc.showSaveDialog(null);

    File arquivo = new File(fc.getSelectedFile().toString());

    FileWriter writer = null;
    try {
        writer = new FileWriter(arquivo);
        writer.write(jTextAreaLog.getText());
    }
    catch(IOException ex){
       // Possiveis erros aqui
   }
   finally {
        if(writer != null)
        {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
   }

}

private void jComboBoxLogEstagiosActionPerformed(java.awt.event.ActionEvent evt) {

    if (jComboBoxLogEstagios.getSelectedIndex()==0)
    {
        //limpa o jComboBoxLogGalhos e deixa apenas o primeiro item (nomeado "Todos os Galhos")
        while (jComboBoxLogGalhos.getItemCount() > 2)
            jComboBoxLogGalhos.removeItemAt(jComboBoxLogGalhos.getItemCount()-1);

    }
    else
    {
        //pega a lista com todas as plantas (estagios):
        ArrayList<Planta> ListaPlantas = Interp.getListaplantas();
        //pega a planta do estágio respectivo com o escolhido no checkbox:
        Planta planta = ListaPlantas.get(jComboBoxLogEstagios.getSelectedIndex()-1);

        jComboBoxLogGalhos.removeAllItems();
        jComboBoxLogGalhos.addItem(colecaomsgs.getString("TITULOCOMBOITEM4")); //"Nenhum Galho"
        jComboBoxLogGalhos.addItem(colecaomsgs.getString("TITULOCOMBOITEM5")); //"Todos os Galhos"

        for (int g=0; g<planta.getQtdeTotalGalhos(); g++)
        {                                                    //"Galho "
            jComboBoxLogGalhos.addItem((colecaomsgs.getString("TITULOCOMBOITEM3") + (g+1)));
        }
    }


    atualizarRelatorio();
}

private void jComboBoxLogGalhosActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}

private void jCheckBoxFiltroTotFolhasActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}

private void jCheckBoxFiltroFolhSurgActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}

private void jCheckBoxFiltroFolhCaidActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}

private void jCheckBoxFIltroLArFolTotActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}

private void jCheckBoxFiltroGanArFolActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}

private void jCheckBoxFiltroCompGalActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}

private void jCheckBoxAlongGalActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}

private void jCheckBoxFiltroTotRamActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}

private void jCheckBoxFiltroRamSurgActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}

private void jCheckBoxFiltroTotENActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}

private void jCheckBoxFiltroENSurgActionPerformed(java.awt.event.ActionEvent evt) {
    atualizarRelatorio();
}



private void jButtonAbrirGraficoRapidoActionPerformed(java.awt.event.ActionEvent evt) {

    if (jListaGraficosExistentes.isSelectionEmpty()==false)
    {
        String nome_do_arquivo = (String)jListaGraficosExistentes.getModel().getElementAt(jListaGraficosExistentes.getSelectedIndex());

        //pega o painel do grafico atual sendo mostrado na tela:
        JPanel PainelDoGrafico = (JPanel)PainelAbasGraficos.getSelectedComponent();

        //Grafico_ recebe qual é o grafico do painel atual mostrado na tela:
        Grafico Grafico_ = null;
        if (PainelAbasGraficos.getSelectedIndex()==0)   Grafico_ = GraficoCompGalhos;
        if (PainelAbasGraficos.getSelectedIndex()==1)   Grafico_ = GraficoEmissaoFolhas;
        if (PainelAbasGraficos.getSelectedIndex()==2)   Grafico_ = GraficoQuedaFolhas;
        if (PainelAbasGraficos.getSelectedIndex()==3)   Grafico_ = GraficoAreaFoliar;

        if (PainelAbasGraficos.getSelectedIndex()==4)   Grafico_ = GraficoNumeroMetameros;
        if (PainelAbasGraficos.getSelectedIndex()==5)   Grafico_ = GraficoTamanhoFolhas;


        int c;
        String str_rec_grafico = "";

        try
        {
            FileReader reader = new FileReader("graficos/" + nome_do_arquivo); //criar um FileReader para ler o conteudo do arquivo escolhido a ser aberto

            try
            {
                while ((c = reader.read()) != -1) //le o conteudo de caractereem caractere, armazenando no inteiro c. (até chegar no fim do arquivo)
                {
                    str_rec_grafico = str_rec_grafico + ((char)c);  //concatena o conteudo do que esta sendo lido na string (ate terminar)
                }
                reader.close(); //fecha o arquivo
            }
            catch (IOException e) //se nao conseguiu fechar o arquivo imprime uma mensagem avisando que ocorreu excecao:
            {                                       //"Incapaz de fechar o arquivo."
                JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO23"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
            }
        }
        catch (FileNotFoundException e) //se o arquivo nao foi encontrado imprime uma mensagem avisando que ocorreu excecao:
        {                                           //"Arquivo não encontrado!"
             JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO24"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
        }

        String linhas[] = str_rec_grafico.split("\n");
        String str_tipo_do_grafico = linhas[0].substring(5); //remove o trecho "TIPO:" da string.

        if (str_tipo_do_grafico.compareTo("EMISSAOFOLHAS")==0 || str_tipo_do_grafico.compareTo("QUEDAFOLHAS")==0)
        {
            if (linhas.length>=3)
            {
             if (linhas[2].length() >= 12 && linhas[3].length() >= 12)
             {
                    Grafico_.setIntensidadeMinima(Double.parseDouble(linhas[2].substring(12)));
                    Grafico_.setIntensidadeMaxima(Double.parseDouble(linhas[3].substring(12)));
                    jSpinner.setValue(Grafico_.getValorMaximo());
             }
            }
        }

        String s[] = str_rec_grafico.split("Pontos:\n");
        if (s.length == 2) //se arquivo for valido: (se possuir texto depois 'Pontos:\n')
        {
            str_rec_grafico = s[1]; //str_rec_grafico recebe o texto com os pontos (recorte de string)

            Grafico_.setStringPontosFixos(str_rec_grafico); //carrega os pontos fixos (que estava no arquivo) no objeto 'Grafico'
            try {
                //carrega os pontos fixos (que estava no arquivo) no objeto 'Grafico'
                Grafico_.desenhaGraficoEm(PainelDoGrafico, INTERNACIONALIZACAO, ListaMTGsAGerar, jCheckBoxMostrarDiasReq.isSelected()); //desenha o grafico no componente;
            } catch (InterruptedException ex) {
                Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {                                        //"Arquivo de gráfico inválido!"
             JOptionPane.showMessageDialog(null, colecaomsgs.getString("ERRO74"), colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
        }



    }
}

private void PainelComAbaComponentShown(java.awt.event.ComponentEvent evt) {

    //pega o painel do grafico atual sendo mostrado na tela:
    JPanel PainelDoGrafico = (JPanel)PainelAbasGraficos.getSelectedComponent();

    //Grafico_ recebe qual é o grafico do painel atual mostrado na tela:
    Grafico Grafico_ = null;
    if (PainelAbasGraficos.getSelectedIndex()==0)   Grafico_ = GraficoCompGalhos;
    if (PainelAbasGraficos.getSelectedIndex()==1)   Grafico_ = GraficoEmissaoFolhas;
    if (PainelAbasGraficos.getSelectedIndex()==2)   Grafico_ = GraficoQuedaFolhas;
    if (PainelAbasGraficos.getSelectedIndex()==3)   Grafico_ = GraficoAreaFoliar;

    if (PainelAbasGraficos.getSelectedIndex()==4)   Grafico_ = GraficoNumeroMetameros;
    if (PainelAbasGraficos.getSelectedIndex()==5)   Grafico_ = GraficoTamanhoFolhas;

    //tenta desenha este grafico:
    try {
        Grafico_.desenhaGraficoEm(PainelDoGrafico, INTERNACIONALIZACAO, ListaMTGsAGerar, jCheckBoxMostrarDiasReq.isSelected());
    } catch (InterruptedException ex) {
        Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
    }
}

private void statusAnimationLabelMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
    statusAnimationLabel.setVisible(false);
}

private void statusAnimationLabelMouseClicked(java.awt.event.MouseEvent evt) {
// TODO add your handling code here:
}

private void jPainel3ComponentShown(java.awt.event.ComponentEvent evt) {

    //pega o painel do grafico atual sendo mostrado na tela:
    JPanel PainelDoGrafico = (JPanel)PainelAbasGraficos.getSelectedComponent();

    //Grafico_ recebe qual é o grafico do painel atual mostrado na tela:
    Grafico Grafico_ = null;
    if (PainelAbasGraficos.getSelectedIndex()==0)   Grafico_ = GraficoCompGalhos;
    if (PainelAbasGraficos.getSelectedIndex()==1)   Grafico_ = GraficoEmissaoFolhas;
    if (PainelAbasGraficos.getSelectedIndex()==2)   Grafico_ = GraficoQuedaFolhas;
    if (PainelAbasGraficos.getSelectedIndex()==3)   Grafico_ = GraficoAreaFoliar;

    if (PainelAbasGraficos.getSelectedIndex()==4)   Grafico_ = GraficoNumeroMetameros;
    if (PainelAbasGraficos.getSelectedIndex()==5)   Grafico_ = GraficoTamanhoFolhas;

    //tenta desenha este grafico:
//    try {
//        Grafico_.desenhaGraficoEm(PainelDoGrafico, INTERNACIONALIZACAO, ListaMTGsAGerar, jCheckBoxMostrarDiasReq.isSelected());
//    } catch (InterruptedException ex) {
//        Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
//    }

}

private void jSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {

    //pega o painel do grafico atual sendo mostrado na tela:
    JPanel PainelDoGrafico = (JPanel)PainelAbasGraficos.getSelectedComponent();

    //Grafico_ recebe qual é o grafico do painel atual mostrado na tela:
    Grafico Grafico_ = null;
    if (PainelAbasGraficos.getSelectedIndex()==0)   Grafico_ = GraficoCompGalhos;
    if (PainelAbasGraficos.getSelectedIndex()==1)   Grafico_ = GraficoEmissaoFolhas;
    if (PainelAbasGraficos.getSelectedIndex()==2)   Grafico_ = GraficoQuedaFolhas;
    if (PainelAbasGraficos.getSelectedIndex()==3)   Grafico_ = GraficoAreaFoliar;

    if (PainelAbasGraficos.getSelectedIndex()==4)   Grafico_ = GraficoNumeroMetameros;
    if (PainelAbasGraficos.getSelectedIndex()==5)   Grafico_ = GraficoTamanhoFolhas;

    Grafico_.setIntensidadeMaxima(Math.round(Float.parseFloat(jSpinner.getValue().toString())));
    //tenta desenha este grafico:
    try {
        Grafico_.desenhaGraficoEm(PainelDoGrafico, INTERNACIONALIZACAO, ListaMTGsAGerar, jCheckBoxMostrarDiasReq.isSelected());
    } catch (InterruptedException ex) {
        Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
    }
}


private void atualizarRelatorio()
{
    int estagio;
    int galho;

    estagio = jComboBoxLogEstagios.getSelectedIndex()-1;

    if (jComboBoxLogGalhos.isEnabled())
         galho=jComboBoxLogGalhos.getSelectedIndex();
    else
         galho=0;

    Relatorio relat = new Relatorio(Interp.getListaplantas(),
                                    estagio,
                                    galho,
                                    jCheckBoxFiltroTotFolhas.isSelected(),
                                    jCheckBoxFiltroFolhSurg.isSelected(),
                                    jCheckBoxFiltroFolhCaid.isSelected(),
                                    jCheckBoxFIltroLArFolTot.isSelected(),
                                    jCheckBoxFiltroGanArFol.isSelected(),
                                    jCheckBoxFiltroCompGal.isSelected(),
                                    jCheckBoxAlongGal.isSelected(),
                                    jCheckBoxFiltroTotRam.isSelected(),
                                    jCheckBoxFiltroRamSurg.isSelected(),
                                    jCheckBoxFiltroTotEN.isSelected(),
                                    jCheckBoxFiltroENSurg.isSelected());

    jTextAreaLog.setText(relat.criarTexto());
}

 private void buscarArquivoMTG(JTextField CaixaDeTexto)
 {

    JFileChooser fc = new JFileChooser(); //cria um "escolhedor de arquivos"
    //seta os textos presentes neste JFileChooser:
    fc.setDialogTitle(colecaomsgs.getString("Buscar_arquivo"));
    //fc.setApproveButtonText("Abrir");

    FileNameExtensionFilter filtro_mtg = new FileNameExtensionFilter("Multiscale Tree Graph (*.mtg)", "mtg");  //cria um filtro de arquivo para as extensoes "mtg" e "xls".
    FileNameExtensionFilter filtro_xls = new FileNameExtensionFilter("Excel Spreadsheet (*.xls)", "xls");  //cria um filtro de arquivo para as extensoes "mtg" e "xls".

    fc.addChoosableFileFilter(filtro_xls); //adiciona este filtro para o "escolhedor de arquivos" exibir arquivos do tipo "xls".
    fc.addChoosableFileFilter(filtro_mtg); //adiciona este filtro para o "escolhedor de arquivos" exibir arquivos do tipo "mtg".
    fc.setAcceptAllFileFilterUsed(false); //seta para desativar a opcao de mostrar todos os arquivos.

    int status = fc.showOpenDialog(null); //este escolhedor de arquivos abrirá uma janela de "abertura de arquivos"

    if (status == JFileChooser.APPROVE_OPTION) //se um arquivo foi escolhido:
    {
        CaixaDeTexto.setText(fc.getSelectedFile().getAbsolutePath()); //seta este textfield com o "caminho do arquivo" aberto pelo "escolhedor de arquivos".
    }
}

 //atualiza todas as mensagens dos componentes da GUI:
 private void atualizarTextoComponentes()
 {
	jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(colecaomsgsgui.getString("NOMEBORDA1")));
	jTextFieldEstInicial.setToolTipText(colecaomsgsgui.getString("DICA0"));
	jButtonBuscarMTGInicial.setText(colecaomsgsgui.getString("BOTAO1"));
	jButtonBuscarMTGInicial.setToolTipText(colecaomsgsgui.getString("DICA1"));
	jLabel5.setText(colecaomsgsgui.getString("LABEL2"));
        jButtonVisualizarNoAMAPmod1.setText(colecaomsgsgui.getString("BOTAO2"));
        jButtonVisualizarNoAMAPmod1.setToolTipText(colecaomsgsgui.getString("DICA2"));
        jButtonAplicarMTGsBase.setText(colecaomsgsgui.getString("BOTAO4"));
	jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(colecaomsgsgui.getString("NOMEBORDA2")));
	jTextFieldEstFinal.setToolTipText(colecaomsgsgui.getString("DICA5"));
        jButtonBuscarMTGFinal.setText(colecaomsgsgui.getString("BOTAO1"));
        jButtonBuscarMTGFinal.setToolTipText(colecaomsgsgui.getString("DICA3")); // NOI18
        jLabel7.setText(colecaomsgsgui.getString("LABEL2"));
        jButtonVisualizarNoAMAPmod2.setText(colecaomsgsgui.getString("BOTAO2"));
        jButtonVisualizarNoAMAPmod2.setToolTipText(colecaomsgsgui.getString("DICA4"));
        jLabel10.setText(colecaomsgsgui.getString("LABEL1"));
        PainelComAba.setTitleAt(0, colecaomsgsgui.getString("ABA1"));
        jLabel1.setText(colecaomsgsgui.getString("LABEL7"));
        jLabel2.setText(colecaomsgsgui.getString("LABEL8"));
        jButtonInserirMTG.setText(colecaomsgsgui.getString("BOTAO5"));
        jButtonAlterarMTG.setText(colecaomsgsgui.getString("BOTAO6"));
        jButtonExcluirMTG.setText(colecaomsgsgui.getString("BOTAO7"));
        jLabel11.setText(colecaomsgsgui.getString("LABEL9"));
        jButtonBuscarDiretorio.setText(colecaomsgsgui.getString("BOTAO1"));
        jButtonAplicarMTGsAGerar.setText(colecaomsgsgui.getString("BOTAO4"));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(colecaomsgsgui.getString("NOMEBORDA3")));
        jLabe15.setText(colecaomsgsgui.getString("LABEL4"));
        jLabe16.setText(colecaomsgsgui.getString("LABEL5"));
        jLabel20.setText(colecaomsgsgui.getString("LABEL6"));
        PainelComAba.setTitleAt(1, colecaomsgsgui.getString("ABA2"));

        jCheckBoxMostrarDiasReq.setText(colecaomsgsgui.getString("CHECKBOX1"));
        jButtonAbrirGraficoRapido.setText(colecaomsgsgui.getString("BOTAO8"));
        jLabelAvisoAbrirGraficos.setText(colecaomsgsgui.getString("LABEL11"));
        jButtonProcessarInterpolacao.setText(colecaomsgsgui.getString("BOTAO3"));

        PainelComAba.setTitleAt(2, colecaomsgsgui.getString("ABA3"));
        jButtonSalvarRelatorio.setText(colecaomsgsgui.getString("BOTAO12"));
        jCheckBoxFiltroTotFolhas.setText(colecaomsgsgui.getString("CHECKBOX2"));
        jCheckBoxFiltroFolhCaid.setText(colecaomsgsgui.getString("CHECKBOX4"));
        jCheckBoxFiltroFolhSurg.setText(colecaomsgsgui.getString("CHECKBOX3"));
        jCheckBoxFIltroLArFolTot.setText(colecaomsgsgui.getString("CHECKBOX5"));
        jCheckBoxFiltroGanArFol.setText(colecaomsgsgui.getString("CHECKBOX6"));
        jCheckBoxFiltroCompGal.setText(colecaomsgsgui.getString("CHECKBOX7"));
        jCheckBoxAlongGal.setText(colecaomsgsgui.getString("CHECKBOX8"));
        jCheckBoxFiltroTotRam.setText(colecaomsgsgui.getString("CHECKBOX9"));
        jCheckBoxFiltroRamSurg.setText(colecaomsgsgui.getString("CHECKBOX10"));
        jCheckBoxFiltroENSurg.setText(colecaomsgsgui.getString("CHECKBOX12"));
        jCheckBoxFiltroTotEN.setText(colecaomsgsgui.getString("CHECKBOX11"));
        jPanel8.getAccessibleContext().setAccessibleName(colecaomsgsgui.getString("NOMEBORDA4"));
        PainelComAba.setTitleAt(3, colecaomsgsgui.getString("ABA4"));
        jLabel4.setText(colecaomsgsgui.getString("LABEL13"));
        jLabel12.setText(colecaomsgsgui.getString("LABEL14"));
        jLabel13.setText(colecaomsgsgui.getString("LABEL12"));
        jCheckBoxIncluirEstBases.setText(colecaomsgsgui.getString("CHECKBOX13"));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(colecaomsgsgui.getString("NOMEBORDA5")));
        jButtonVisualizarConj3D.setText(colecaomsgsgui.getString("BOTAO13"));
        jButtonVisualizarConj3D.setToolTipText(colecaomsgsgui.getString("DICA6"));
        jRadioButtonSomenteGalhos.setText(colecaomsgsgui.getString("CHECKBOX14"));
        jRadioButtonSomenteFolhas.setText(colecaomsgsgui.getString("CHECKBOX17"));
        jRadioButtonPlantaInteira.setText(colecaomsgsgui.getString("CHECKBOX18"));
        PainelComAba.setTitleAt(4, colecaomsgsgui.getString("ABA5"));
        BarraDeMenus.getMenu(0).setText(colecaomsgsgui.getString("MENU1"));
        jMenuItemNovo.setText(colecaomsgsgui.getString("SUBMENU1"));
        jMenuItemIntegrar.setText(colecaomsgsgui.getString("SUBMENU2"));
        BarraDeMenus.getMenu(0).getItem(3).setText(colecaomsgsgui.getString("SUBMENU3"));
        BarraDeMenus.getMenu(0).getItem(3).setToolTipText(colecaomsgsgui.getString("DICA7"));
        BarraDeMenus.getMenu(1).setText(colecaomsgsgui.getString("MENU2"));
        BarraDeMenus.getMenu(2).setText(colecaomsgsgui.getString("MENU3"));
        BarraDeMenus.getMenu(2).getItem(1).setText(colecaomsgsgui.getString("SUBMENU5"));
        jButtonAplicarInsercaoEstagio.setText(colecaomsgsgui.getString("BOTAO15"));
        jButtonCancelarInsercaoEstagio.setText(colecaomsgsgui.getString("BOTAO14"));
        jPanelInsercaoEstagio.setBorder(javax.swing.BorderFactory.createTitledBorder(colecaomsgsgui.getString("NOMEBORDA7")));
        Dia.setText(colecaomsgsgui.getString("LABEL17"));
        jLabel3.setText(colecaomsgsgui.getString("LABEL18"));
        jCheckBoxNomenclaturaDia.setText(colecaomsgsgui.getString("CHECKBOX15"));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(colecaomsgsgui.getString("NOMEBORDA6")));
        jLabe10.setText(colecaomsgsgui.getString("LABEL4"));
        jLabe9.setText(colecaomsgsgui.getString("LABEL5"));
        jLabel9.setText(colecaomsgsgui.getString("LABEL15"));
        jLabel14.setText(colecaomsgsgui.getString("LABEL16"));
        jButtonCancelarAlterarEstagio.setText(colecaomsgsgui.getString("BOTAO14"));
        jButtonAplicarAlteracaoEstagio.setText(colecaomsgsgui.getString("BOTAO15"));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(colecaomsgsgui.getString("NOMEBORDA6")));
        jLabe11.setText(colecaomsgsgui.getString("LABEL4"));
        jLabe12.setText(colecaomsgsgui.getString("LABEL5"));
        jLabel15.setText(colecaomsgsgui.getString("LABEL15"));
        jLabel16.setText(colecaomsgsgui.getString("LABEL16"));
        jPanelInsercaoEstagio1.setBorder(javax.swing.BorderFactory.createTitledBorder(colecaomsgsgui.getString("NOMEBORDA8")));
        jLabel17.setText(colecaomsgsgui.getString("LABEL18"));
        jCheckBoxNomenclaturaDiaB.setText(colecaomsgsgui.getString("CHECKBOX15"));
        Dia1.setText(colecaomsgsgui.getString("LABEL17"));
        jCheckBoxAtivacaoAMAPmod.setText(colecaomsgsgui.getString("CHECKBOX16"));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(colecaomsgsgui.getString("NOMEBORDA9")));
        jButtonBuscarAMAPmod.setText(colecaomsgsgui.getString("BOTAO16"));
        jButtonAplicarAMAPmod.setText(colecaomsgsgui.getString("BOTAO15"));
        jButtonCancJanelaIntAMAPmod.setText(colecaomsgsgui.getString("BOTAO14"));
        jLabelIntegracao.setText(colecaomsgsgui.getString("LABEL19"));
 }


public static boolean isDate(String dateStr) throws java.text.ParseException
{
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    Calendar cal = new GregorianCalendar();

    // gerando o calendar
    cal.setTime(df.parse(dateStr));

    // separando os dados da string para comparacao e validacao
    String[] data = dateStr.split("/");
    String dia = data[0];
    String mes = data[1];
    String ano = data[2];

    // testando se hah discrepancia entre a data que foi
    // inserida no caledar e a data que foi passada como
    // string. se houver diferenca, a data passada era
    // invalida
    if ( (new Integer(dia)).intValue() != (new Integer(cal.get(Calendar.DAY_OF_MONTH))).intValue() )
    {
        // dia nao casou
        return(false);
    } else if ( (new Integer(mes)).intValue() != (new Integer(cal.get(Calendar.MONTH)+1)).intValue() )
           {
                // mes nao casou
                return(false);
           } else if ( (new Integer(ano)).intValue() != (new Integer(cal.get(Calendar.YEAR))).intValue() )
                  {
                     // ano nao casou
                     return(false);
                  }

    return(true);
}


public static String ArrumarCaminhoPadraoWindows(String caminho)
{
    caminho.replaceFirst(":", ":\\\\");


    String[] pedacos_caminho = caminho.split("\\\\");
    caminho = "";
    for (int i=0; i < pedacos_caminho.length; i++)
    {
        if (pedacos_caminho[i].length()>8)
            caminho = caminho + ASPAS + pedacos_caminho[i] + ASPAS;
        else
            caminho = caminho + pedacos_caminho[i];

        if (i+1 < pedacos_caminho.length) caminho = caminho + "\\";
    }

    return caminho;
}


    @Action
    public void showDialogInserirEstagio() {
    }

    private javax.swing.JMenuBar BarraDeMenus;
    private javax.swing.JLabel Dia;
    private javax.swing.JLabel Dia1;
    private javax.swing.JCheckBoxMenuItem MenuItemFrances;
    private javax.swing.JCheckBoxMenuItem MenuItemIngles;
    private javax.swing.JCheckBoxMenuItem MenuItemPortugues;
    private javax.swing.JMenu MenuLingua;
    private javax.swing.JTabbedPane PainelAbasGraficos;
    private javax.swing.JTabbedPane PainelComAba;
    private javax.swing.JPanel PainelPrincipal;
    private javax.swing.ButtonGroup buttonGroupInternacionalizacao;

    private javax.swing.JButton jButtonAbrirGraficoRapido;
    private javax.swing.JButton jButtonAlterarMTG;
    private javax.swing.JButton jButtonAplicarAMAPmod;
    private javax.swing.JButton jButtonAplicarAlteracaoEstagio;
    private javax.swing.JButton jButtonAplicarInsercaoEstagio;
    private javax.swing.JButton jButtonAplicarMTGsAGerar;
    private javax.swing.JButton jButtonAplicarMTGsBase;
    private javax.swing.JButton jButtonBuscarAMAPmod;
    private javax.swing.JButton jButtonBuscarDiretorio;
    private javax.swing.JButton jButtonBuscarMTGFinal;
    private javax.swing.JButton jButtonBuscarMTGInicial;
    private javax.swing.JButton jButtonCancJanelaIntAMAPmod;
    private javax.swing.JButton jButtonCancelarAlterarEstagio;
    private javax.swing.JButton jButtonCancelarInsercaoEstagio;
    private javax.swing.JButton jButtonExcluirMTG;
    private javax.swing.JButton jButtonInserirConj3D;
    private javax.swing.JButton jButtonInserirMTG;

    private javax.swing.JButton jButtonProcessarInterpolacao;
    private javax.swing.JButton jButtonRemoverConj3D;

    private javax.swing.JButton jButtonSalvarRelatorio;
    private javax.swing.JButton jButtonVisualizarConj3D;
    private javax.swing.JButton jButtonVisualizarNoAMAPmod1;
    private javax.swing.JButton jButtonVisualizarNoAMAPmod2;
    private javax.swing.JCheckBox jCheckBoxAlongGal;
    private javax.swing.JCheckBox jCheckBoxAtivacaoAMAPmod;
    private javax.swing.JRadioButton jRadioButtonSomenteGalhos;
    private javax.swing.JRadioButton jRadioButtonSomenteFolhas;
    private javax.swing.JRadioButton jRadioButtonPlantaInteira;
    private javax.swing.JCheckBox jCheckBoxFIltroLArFolTot;
    private javax.swing.JCheckBox jCheckBoxFiltroCompGal;
    private javax.swing.JCheckBox jCheckBoxFiltroENSurg;
    private javax.swing.JCheckBox jCheckBoxFiltroFolhCaid;
    private javax.swing.JCheckBox jCheckBoxFiltroFolhSurg;
    private javax.swing.JCheckBox jCheckBoxFiltroGanArFol;
    private javax.swing.JCheckBox jCheckBoxFiltroRamSurg;
    private javax.swing.JCheckBox jCheckBoxFiltroTotEN;
    private javax.swing.JCheckBox jCheckBoxFiltroTotFolhas;
    private javax.swing.JCheckBox jCheckBoxFiltroTotRam;
    private javax.swing.JCheckBox jCheckBoxIncluirEstBases;
    private javax.swing.JCheckBox jCheckBoxMostrarDiasReq;
    private javax.swing.JCheckBox jCheckBoxNomenclaturaDia;
    private javax.swing.JCheckBox jCheckBoxNomenclaturaDiaB;
    private javax.swing.JComboBox jComboBoxLogEstagios;
    private javax.swing.JComboBox jComboBoxLogGalhos;
    private javax.swing.JDialog jDialogAlterarEstagio;
    private javax.swing.JDialog jDialogBarraDeProgresso;
    private javax.swing.JDialog jDialogInserirEstagio;
    private javax.swing.JDialog jDialogIntegracaoAMAPmod;
    private javax.swing.JLabel jLabe10;
    private javax.swing.JLabel jLabe11;
    private javax.swing.JLabel jLabe12;
    private javax.swing.JLabel jLabe15;
    private javax.swing.JLabel jLabe16;
    private javax.swing.JLabel jLabe9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelAlometria;
    private javax.swing.JLabel jLabelAvisoAbrirGraficos;
    private javax.swing.JLabel jLabelDataFinal;
    private javax.swing.JLabel jLabelDataFinal1;
    private javax.swing.JLabel jLabelDataFinal2;
    private javax.swing.JLabel jLabelDataInicial;
    private javax.swing.JLabel jLabelDataInicial1;
    private javax.swing.JLabel jLabelDataInicial2;
    private javax.swing.JLabel jLabelDiferencaDias;
    private javax.swing.JLabel jLabelDiferencaDias1;
    private javax.swing.JLabel jLabelDiferencaDias2;
    private javax.swing.JLabel jLabelIntegracao;
    private javax.swing.JLabel jLabelMonitorGrafico;
    private javax.swing.JLabel jLabelYmax;
    private javax.swing.JList jList1;
    private javax.swing.JList jListaGraficosExistentes;
    private javax.swing.JMenuItem jMenuItemIntegrar;
    private javax.swing.JMenuItem jMenuItemNovo;
    private javax.swing.JPanel jPainel1;
    private javax.swing.JPanel jPainel2;
    private javax.swing.JPanel jPainel3;
    private javax.swing.JPanel jPainel4;
    private javax.swing.JPanel jPainel5;

    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelInsercaoEstagio;
    private javax.swing.JPanel jPanelInsercaoEstagio1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSpinner jSpinner;
    private javax.swing.JTable jTableConjuntoPara3D;
    private javax.swing.JTable jTableMTGsAGerar;
    private javax.swing.JTable jTableMTGsExistentes;
    private javax.swing.JTextArea jTextAreaLog;
    private javax.swing.JTextField jTextFieldCaminhoAMAPmod;
    private javax.swing.JTextField jTextFieldDiaEstagio;
    private javax.swing.JTextField jTextFieldDiaEstagioAAlterar;
    private javax.swing.JTextField jTextFieldDiretorio;
    private javax.swing.JTextField jTextFieldEstFinal;
    private javax.swing.JTextField jTextFieldEstInicial;
    private javax.swing.JTextField jTextFieldNomeEstagio;
    private javax.swing.JTextField jTextFieldNomeEstagioAAlterar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

}

