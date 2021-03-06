package GUI;

import Lexer.Lexer;
import Parser.Parser;
import Parser.ParserTree;
import Semantic.Semantic;
import Semantic.SymbolTableNode;
import commons.DataType;
import commons.Error;
import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


/**
 * 编译器的主界面
 *
 * @author wangpengcheng, zhangteng
 */
public class Main extends JFrame {

    /**
     * *****菜单栏、菜单和菜单项********
     */
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("文件");
    JMenu optionMenu = new JMenu("选项");
    JMenu advanceMenu = new JMenu("编译");
    JMenu helpMenu = new JMenu("帮助");
    JMenu editMenu = new JMenu("编辑");
    JMenuItem newMenuItem = new JMenuItem("新建");
    JMenuItem openMenuItem = new JMenuItem("打开文件");
    JMenuItem saveMenuItem = new JMenuItem("保存");
    JMenuItem exitMenuItem = new JMenuItem("退出");
    JMenuItem findMenuItem = new JMenuItem("查找");
    JMenuItem findNextMenuItem = new JMenuItem("查找下一个");
    JMenuItem replaceMenuItem = new JMenuItem("替换");
    JMenuItem compileMenuItem = new JMenuItem("词法分析");
    JMenuItem buildMenuItem = new JMenuItem("语法分析");
    JMenuItem semanticMenuItem = new JMenuItem("语义分析");
    JMenuItem helpMenuItem = new JMenuItem("帮助");
    JMenuItem copyMenuItem = new JMenuItem("复制");
    JMenuItem cutMenuItem = new JMenuItem("剪切");
    JMenuItem pasteMenuItem = new JMenuItem("粘贴");


    /**
     * *****文件内容的显示***
     */
    //用多个文本域存放多个文件内容，文本域放在JScrollPane里
    //而JScrollPane放在JTabbedPane中，这就是一个多页面的布局
    ParserTree parserTree = new ParserTree();
    //多个文本域，每个文本域显示一个文件的内容
    JTextArea[] fileTextAreas = new JTextArea[10];
    //编译或者运行时的控制台信息
    JTextArea consoleTextArea = new JTextArea();

    JScrollPane[] fileScrollPanes = new JScrollPane[10];
    JScrollPane consoleScrollPane;
    //多个文本域放在不同tab里
    JTabbedPane fileTabbedPane = new JTabbedPane();
    Image offScreenImage = null;
    File f;
    Lexer lexer;
    int file_control = 0;
    int byflag = 0;
    int cfflag = 0;
    int compileflag = 0;//判断是否编译
    int backcompile = 0;
    boolean threadflag = false;
    /**
     * *******工具栏以及工具栏上的按钮*********
     */
    JToolBar toolBar = new JToolBar();
    JButton openButton = new JButton(new ImageIcon(loadImage("image/open.gif")));
    JButton newButton = new JButton(new ImageIcon(loadImage("image/new.gif")));
    JButton saveButton = new JButton(new ImageIcon(loadImage("image/save.gif")));
    JButton helpButton = new JButton(new ImageIcon(loadImage("image/help.gif")));
    JButton exitButton = new JButton(new ImageIcon(loadImage("image/close.gif")));
    JButton compileButton = new JButton(new ImageIcon(loadImage("image/cifa.gif")));
    JButton buildButton = new JButton(new ImageIcon(loadImage("image/yufa.gif")));
    JButton semanticButton = new JButton(new ImageIcon(loadImage("image/yuyi.png")));
    JButton copyButton = new JButton(new ImageIcon(loadImage("image/copy.gif")));
    JButton cutButton = new JButton(new ImageIcon(loadImage("image/cut.gif")));
    JButton pasteButton = new JButton(new ImageIcon(loadImage("image/paste.gif")));

    //该文本域显示当前光标在当前文本域中的行号
    JLabel showLineNoTextArea = new JLabel();
    //对话框窗体，程序中所有对话框都显示在该窗体中
    JFrame dialogFrame = new JFrame();

    /**
     * ****组件之间的分隔栏*****
     */
    JSplitPane tabbedConsoleSplitPane;

    /**
     * *******文件选择、存储相关*******
     */
    //文件过滤器
    Filter fileFilter = new Filter();
    //文件选择器
    FileChooser fileChooser = new FileChooser();
    // 文件读写控制，0表示文件选择器读文件，1文件选择器标示写文件
    int fileChooser_control = 0;
    FileWriter fileWriter;

    // tabbedPane中tab页的当前数量
    int tb = 1;
    int find_control = 0;
    //文本域的控制器，指向当前操作的文本域
    int textAreas_control = 0;
    //当前文本域中的文本
    String currentTextInTextArea;

    //标志文件是否为新建的，如果是新建的文件，为true
    boolean[] newFileFlags = new boolean[10];
    //存放打开文件所在的目录
    String[] directory = new String[10];

    /**
     * *****查找替换相关***
     */
    //正在查找的字符串及其长度
    String findWord;
    int fingWordLength;
    //保存正在查找的字符串在文本域中的文本的位置
    int findIndex;
    //被替换的文本的长度
    int replaceLength = 0;


    /**
     * *****帮助相关***
     */
    Font font = new Font("Courier", Font.TRUETYPE_FONT, 14);
    JTextArea helpTextArea = new JTextArea();
    JFrame helpFrame = new JFrame("Help");

    //构造函数
    public Main() {
        super("C-编译器");

        //为窗体添加键盘事件处理器
        //下面这一行非常重要，表示窗体能够接受焦点。
        //如果没有这一句，按键盘会无效。
        this.setFocusable(true);
        this.addKeyListener(new MyKeyListener());

        //为窗体添加窗口事件处理器
        this.addWindowListener(new WindowListener());

        //初始化
        init();
        setLocation(100, 100);
        setVisible(true);
        setSize(600, 600);
    }

    private void init() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        for (int i = 0; i < 10; i++) {
            newFileFlags[i] = true;
            fileTextAreas[i] = new JTextArea();
            // 设置文本域中文本的字体
            fileTextAreas[i].setFont(font);
            // 为文本域的插入光标设置颜色
            fileTextAreas[i].setCaretColor(Color.black);
            // 设置文本域的背景和前景颜色
            fileTextAreas[i].setBackground(new Color(255, 255, 255));
            fileTextAreas[i].setForeground(Color.black);
            // 为文本域插入光标设置事件处理器
            //fileTextAreas[i].addCaretListener(new CaretLis_lineNo());
            // 为文本域键盘设置事件处理器
            fileTextAreas[i].getDocument().addDocumentListener(new Swing_OnValueChanged());
            fileTextAreas[i].addKeyListener(new MyKeyListener());
            directory[i] = new String("/");
            fileScrollPanes[i] = new JScrollPane(fileTextAreas[i],
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }

        //初始化控制台文本域
        consoleTextArea.setFont(font);
        consoleScrollPane = new JScrollPane(consoleTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        consoleTextArea.setForeground(Color.black);
        consoleTextArea.setBackground(Color.white);

        /**************初始化菜单*************/
        //初始化菜单项
        newMenuItem.addActionListener(new Act_NewFile());
        openMenuItem.addActionListener(new Act_OpenFile());
        saveMenuItem.addActionListener(new Act_SaveFile());
        exitMenuItem.addActionListener(new Act_ExitEditor());
        findMenuItem.addActionListener(new Act_Find());
        findNextMenuItem.addActionListener(new Act_FindNext());
        replaceMenuItem.addActionListener(new Act_Replace());
        compileMenuItem.addActionListener(new Act_Compile());
        buildMenuItem.addActionListener(new Act_Parser());
        semanticMenuItem.addActionListener(new Act_Semantic());
        helpMenuItem.addActionListener(new Act_Help());
        copyMenuItem.addActionListener(new Act_Copy());
        cutMenuItem.addActionListener(new Act_Cut());
        pasteMenuItem.addActionListener(new Act_Paste());
        //初始化菜单
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(exitMenuItem);
        optionMenu.add(findMenuItem);
        optionMenu.add(findNextMenuItem);
        optionMenu.add(replaceMenuItem);
        advanceMenu.add(compileMenuItem);
        advanceMenu.add(buildMenuItem);
        advanceMenu.add(semanticMenuItem);
        advanceMenu.addSeparator();
        helpMenu.add(helpMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(cutMenuItem);
        editMenu.add(pasteMenuItem);
        //初始化菜单栏
        menuBar.add(fileMenu);
        menuBar.add(optionMenu);
        menuBar.add(advanceMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
        //将菜单栏添加到窗体中
        setJMenuBar(menuBar);

        /***********初始化工具栏以及按钮**********/
        //初始化按钮
        newButton.addActionListener(new Act_NewFile());
        openButton.addActionListener(new Act_OpenFile());
        saveButton.addActionListener(new Act_SaveFile());
        exitButton.addActionListener(new Act_ExitEditor());
        compileButton.addActionListener(new Act_Compile());
        buildButton.addActionListener(new Act_Parser());
        semanticButton.addActionListener(new Act_Semantic());
        helpButton.addActionListener(new Act_Help());
        copyButton.addActionListener(new Act_Copy());
        cutButton.addActionListener(new Act_Cut());
        pasteButton.addActionListener(new Act_Paste());
        // 为工具栏设置提示信息，当鼠标在工具栏按钮上停留一段时间时，会显示提示信息
        newButton.setToolTipText("新建");
        openButton.setToolTipText("打开文件");
        saveButton.setToolTipText("保存");
        exitButton.setToolTipText("退出");
        helpButton.setToolTipText("帮助");
        compileButton.setToolTipText("词法分析");
        buildButton.setToolTipText("语法分析");
        semanticButton.setToolTipText("语义分析");
        copyButton.setToolTipText("复制");
        cutButton.setToolTipText("剪切");
        pasteButton.setToolTipText("粘贴");
        //初始化工具栏
        toolBar.add(newButton);
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.add(copyButton);
        toolBar.add(cutButton);
        toolBar.add(pasteButton);
        toolBar.add(compileButton);
        toolBar.add(buildButton);
        toolBar.add(semanticButton);
        toolBar.add(exitButton);
        toolBar.add(helpButton);

        /********初始化tab页面板和组件间的分隔栏*********/
        fileTabbedPane.addTab("File1", fileScrollPanes[0]);
        fileTabbedPane.addChangeListener(new Act_ChangeTab());
        tabbedConsoleSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, fileTabbedPane,

                consoleScrollPane);
        //面板与结果面板的分隔栏
        tabbedConsoleSplitPane.setDividerLocation(260);
        //初始化帮助
        initHelp();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(tabbedConsoleSplitPane);
    }

    /**
     * 初始化帮助信息
     */
    private void initHelp() {
        //帮助信息主要显示菜单的快捷方式

        // field存放JTable的表头信息，即表的标题
        String[] field = {"MenuItem", "ShortCut Key"};
        // data存放JTable的数据。
        Object[][] data = {{"    新建          	  ", "    Ctrl+N    "},
                {"    打开         	 ", "    F12       "},
                {"    保存          	 ", "    Ctrl+S    "},
                {"    退出          	 ", "    Ctrl+E    "},
                {"    查找          	 ", "    Ctrl+F    "},
                {"    查找下一个             ", "    Ctrl+Y    "},
                {"    替换          	 ", "    F4    "},
                {"    词法分析      	 ", "    F9        "},
                {"    语法分析        	 ", "    F5        "},
                {"    复制            	 ", "    Ctrl+C    "},
                {"    剪切                          ", "    Ctrl+X    "},
                {"    粘贴      	     ", "    Ctrl+V    "},
                {"    帮助          	 ", "    Ctrl+H    "},};
        // 用表头和数据构造一个表
        JTable help_Table = new JTable(data, field);
        help_Table.setFont(font);
        //不可编辑帮助信息表
        help_Table.setEnabled(false);
        // 为表和文本域设置背景和前景颜色
        helpTextArea.setFont(new Font("Courier", Font.TRUETYPE_FONT, 16));
        helpFrame.getContentPane().setLayout(new BorderLayout());
        help_Table.setForeground(Color.pink);
        helpTextArea.setForeground(Color.pink);
        help_Table.setBackground(new Color(70, 80, 91));
        help_Table.setSelectionBackground(new Color(70, 80, 91));
        helpTextArea.setBackground(new Color(70, 80, 91));
        helpTextArea.setText("    欢迎使用我们的c_minus编译器，我们的编译器包含了\n"
                + "     词法分析，语法分析，语义分析，中间代码的生成和\n"
                + "     目标代码生成。我们开发的组员有组长崔元，组员张腾，涂子豪\n"
                + "    ，王鹏程。最后感谢郑晓娟老师的悉心指导.\n"
        );
        // 将文本域和表加到窗体中
        helpFrame.getContentPane().add(
                new JScrollPane(help_Table,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        helpFrame.getContentPane().add(
                new JScrollPane(helpTextArea,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.NORTH);
    }

    // 退出编辑器
    private void exitEditor() {
        // 弹出一个选择确认对话框，请求确认退出
        if ((JOptionPane.showConfirmDialog(this, "确定退出词法分析器？", "退出",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
            // 如果选择YES，则退出。
            dispose();
            System.exit(0);
        }
    }

    /**
     * 键盘事件处理器
     */
    public class MyKeyListener extends KeyAdapter {
        // 覆盖父类的keyPressed方法，处理键被按下时的事件。
        public void keyPressed(KeyEvent e) {
            // 按F12打开文件
            if (e.getKeyCode() == KeyEvent.VK_F12) {
                (new Act_OpenFile()).actionPerformed(null);
            }
            // 按Ctrl加S键保存文件
            else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                (new Act_SaveFile()).actionPerformed(null);
            }
            // 按Ctrl加F键查找
            else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
                (new Act_Find()).actionPerformed(null);
            }
            // 按Ctrl加K查找下一个
            else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_K) {
                (new Act_FindNext()).actionPerformed(null);
            }
            // 按F4替换
            else if (e.getKeyCode() == KeyEvent.VK_F4) {
                (new Act_Replace()).actionPerformed(null);
            }
            // 按Ctrl加N新建文件
            else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
                (new Act_NewFile()).actionPerformed(null);
            }
            // 按Ctrl加E退出编辑器
            else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_E) {
                (new Act_ExitEditor()).actionPerformed(null);
            }
            // 按F5进行语法分析
            else if (e.getKeyCode() == KeyEvent.VK_F5) {
                (new Act_Parser()).actionPerformed(null);
            }
            // 按F9词法分析
            else if (e.getKeyCode() == KeyEvent.VK_F9) {
                (new Act_Compile()).actionPerformed(null);
            }
            // 按Ctrl加H显示帮助
            else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_H) {
                (new Act_Help()).actionPerformed(null);
            }
        }
    }

    public class Swing_OnValueChanged implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            file_control = 0;
            compileflag = 0;
        }

        public void insertUpdate(DocumentEvent e) {
            file_control = 0;
            compileflag = 0;
        }

        public void removeUpdate(DocumentEvent e) {
            file_control = 0;
            compileflag = 0;
        }
    }

    /**
     * 窗口事件侦听器
     */
    public class WindowListener extends WindowAdapter {
        // 处理关闭窗口事件
        public void windowClosing(WindowEvent e) {
            exitEditor();
        }
    }

    /**
     * 文件选择器
     */
    class FileChooser extends JFileChooser {
        public FileChooser() {
            //文件选择器默认位置为当前文件夹
            super("./");
        }

        /**
         * 提交选择
         */
        public void approveSelection() {
            String filename = null;
            //fileChooser_control == 0表示现在是打开文件，需要读
            if (fileChooser_control == 0) {
                // 新建一个tab页，用于装新打开的文件
                fileTabbedPane.addTab("File" + (tb + 1), fileScrollPanes[tb]);
                fileTabbedPane.setSelectedIndex(tb);
                //将当前文本域设置到新打开的文件上
                textAreas_control = tb;
                tb++;

                //获取待打开的文件名
                filename = fileChooser.getSelectedFile().getName();
                //获取待打开的文件所在的目录，将目录保存至数组，这样在保存文件的时候，能够将文件名保存到目录中
                directory[textAreas_control] = fileChooser.getCurrentDirectory().toString();
                fileTextAreas[textAreas_control].setText(null);
                try {
                    //将文件内容显示到文本域中
                    String str;
                    BufferedReader breader = new BufferedReader(new FileReader(directory[textAreas_control] + "/" + filename));
                    while (true) {
                        str = breader.readLine();
                        if (str == null) {
                            break;
                        }
                        fileTextAreas[textAreas_control].append(str + '\n');
                    }
                } catch (Exception e_open) {
                    JOptionPane
                            .showMessageDialog(dialogFrame.getContentPane(), "读取发生错误");
                }

            } else if (fileChooser_control == 1) {
                //	fileChooser_control == 1表示现在是保存新文件，需要写
                filename = fileChooser.getSelectedFile().getName();
                directory[textAreas_control] = fileChooser.getCurrentDirectory().toString();
                try {
                    //将文本域中的内容写到文件中
                    fileWriter = new FileWriter(directory[textAreas_control] + "/"
                            + filename);
                    fileWriter.write(fileTextAreas[textAreas_control].getText());
                    fileWriter.close();
                } catch (Exception e_save) {
                    JOptionPane
                            .showMessageDialog(dialogFrame.getContentPane(), "读取发生错误");
                }

            }

            //关闭对话框
            dialogFrame.dispose();

            //将tab页的标题改为文件名
            fileTabbedPane.setTitleAt(textAreas_control, filename);
            //无论是打开、还是保存，这个文件不是已经新建的，所以置为false
            newFileFlags[textAreas_control] = false;
            file_control = 1;
            if (cfflag == 1) {

                byflag = 1;
                (new Act_Compile()).actionPerformed(null);
                cfflag = 0;
            }

        }

        /**
         * 取消选择
         */
        public void cancelSelection() {
            dialogFrame.dispose();
        }
    }

    /**
     * 文件过滤器，只支持编辑"*.txt,*.c_minus"文件
     */
    class Filter extends FileFilter {
        // 覆盖FileFilter的accept方法
        public boolean accept(File file1) {
            return (
                    file1.getName().endsWith(".txt") || file1.getName()
                            .endsWith(".c_mius"));
        }

        public String getDescription() {
            return (".java,*.html,*.txt,*.cpp");
        }
    }


    /**
     * 切换tab页事件
     */
    class Act_ChangeTab implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            // 切换tab页时，更新textAreas_control的值。
            textAreas_control = fileTabbedPane.getSelectedIndex();
        }
    }

    /**
     * 新建文件事件
     */
    class Act_NewFile implements ActionListener {
        public void actionPerformed(ActionEvent e_ji0) {
            fileTabbedPane.addTab("File" + (tb + 1), fileScrollPanes[tb]);
            fileTabbedPane.setSelectedIndex(tb);
            textAreas_control = tb;
            tb++;
        }

    }

    /**
     * 打开文件事件
     */
    class Act_OpenFile implements ActionListener {
        public void actionPerformed(ActionEvent e_ji1) {
            //打开已有文件
            //将文件选择器置为打开文件状态
            fileChooser_control = 0;
            fileChooser.setApproveButtonText("打开");
            fileChooser.addChoosableFileFilter(fileFilter);
            dialogFrame.getContentPane().add(fileChooser);
            dialogFrame.setSize(550, 350);
            dialogFrame.setTitle("请选择要打开的文件!");
            dialogFrame.setVisible(true);
            fileTextAreas[textAreas_control].setCaretPosition(0);
        }
    }

    /**
     * 保存文件事件
     */
    class Act_SaveFile implements ActionListener {
        public void actionPerformed(ActionEvent e_ji2) {
            file_control = 1;
            //System.out.println("Act_SaveFile" + textAreas_control);
            if (newFileFlags[textAreas_control]) {
                //对于新建的文件，需要指定文件存储路径，因此调用了文件选择起存储文件
                //将文件选择器置为保存文件状态
                fileChooser_control = 1;
                fileChooser.setApproveButtonText("保存");
                fileChooser.addChoosableFileFilter(fileFilter);

                dialogFrame.getContentPane().add(fileChooser);
                dialogFrame.setTitle("请输入文件名!");
                dialogFrame.setSize(550, 350);
                dialogFrame.setVisible(true);

            } else {
                try {
                    //对于已经存在的文件，直接保存
                    //写文件，将当前文本域中文本写入到文件。
                    fileWriter = new FileWriter(directory[textAreas_control] + "/"
                            + fileTabbedPane.getTitleAt(textAreas_control));
                    fileWriter.write(fileTextAreas[textAreas_control].getText());
                    fileWriter.close();
                } catch (Exception e_save) {
                    JOptionPane.showMessageDialog(null, "读取发生错误");
                }
            }

        }
    }

    /**
     * 退出编辑器事件
     */
    class Act_ExitEditor implements ActionListener {
        public void actionPerformed(ActionEvent e_ji3) {
            //退出编辑器
            exitEditor();
        }
    }

    /**
     * 查找事件
     */
    class Act_Find implements ActionListener {
        public void actionPerformed(ActionEvent e_ji4) {
            //查找对话框
            findWord = JOptionPane
                    .showInputDialog("请输入查找内容");
            if (findWord == null) {
                JOptionPane.showMessageDialog(null, "查找失败！");
            } else {
                //根据查找内容在当前文本域中进行匹配
                fingWordLength = findWord.length();
                currentTextInTextArea = fileTextAreas[textAreas_control].getText();
                findIndex = currentTextInTextArea.indexOf(findWord);
                if (findIndex < 0) {
                    JOptionPane.showMessageDialog(null,
                            "   查找内容不存在  ");
                } else {
                    //如果找到了，则将鼠标键盘焦点放在当前文本域中，并将匹配字符串标示出来
                    fileTextAreas[textAreas_control].requestFocus();
                    fileTextAreas[textAreas_control].select(findIndex, findIndex + fingWordLength);
                }
            }
        }
    }

    /**
     * 查找下一个事件
     */
    class Act_FindNext implements ActionListener {
        public void actionPerformed(ActionEvent e_ji4_next) {
            //查找下一个匹配的字符串
            currentTextInTextArea = fileTextAreas[textAreas_control].getText();
            findIndex = currentTextInTextArea.indexOf(findWord, findIndex + 1);
            if (findIndex < 0) {
                JOptionPane.showMessageDialog(null,
                        " 查找已经到达文件尾！ ");
            } else {
                fileTextAreas[textAreas_control].select(findIndex, findIndex + fingWordLength);
            }
        }
    }

    /**
     * 替换事件
     */
    class Act_Replace implements ActionListener {
        public void actionPerformed(ActionEvent e_ji5) {
            //替换对话框
            Object[] endButton1 = {"Replace", "Cancel"};
            String message1 = "确认替换？";
            currentTextInTextArea = fileTextAreas[textAreas_control].getText();

            //获取被替换的内容
            String seek_word = JOptionPane
                    .showInputDialog("请输入查找内容");
            //获取替换后的内容
            String replace_word = JOptionPane
                    .showInputDialog("请输入替换内容");
            //如果用户输入的查找内容不为null，则开始进行替换操作
            if (seek_word != null) {
                //获取查找内容的长度，也就是将来替换的长度
                replaceLength = seek_word.length();
                while (true) {
                    //先获取当前文本域的文本，再进行查找
                    currentTextInTextArea = fileTextAreas[textAreas_control].getText();
                    findIndex = currentTextInTextArea.indexOf(seek_word, findIndex + replaceLength);
                    if (findIndex < 0) {
                        //文本中不存在查找内容
                        JOptionPane.showMessageDialog(null,
                                "查找已经到达文件尾！");
                        break;
                    } else {
                        //查找成功，则标示出查找内容
                        fileTextAreas[textAreas_control].requestFocus();
                        fileTextAreas[textAreas_control].select(findIndex, findIndex +

                                replaceLength);
                        //替换确认
                        JOptionPane end1 = new JOptionPane(message1,
                                JOptionPane.WARNING_MESSAGE,
                                JOptionPane.DEFAULT_OPTION, null, endButton1);
                        JDialog endD1 = end1.createDialog(end1, "请选择");
                        endD1.setVisible(true);
                        Object push1 = end1.getValue();
                        if (push1 == endButton1[0]) {
                            //如果用户选择替换，则将文本域中被标示的文字用replace替换
                            fileTextAreas[textAreas_control].replaceSelection(replace_word);
                        }
                    }
                }
            }
        }
    }

    /**
     * 词法分析
     */
    class Act_Compile implements ActionListener {
        public void actionPerformed(ActionEvent e_ji6) {
            consoleTextArea.setText(null);

            if (file_control == 0) {
                if ((JOptionPane.showConfirmDialog(fileTabbedPane, "是否保存？") == JOptionPane.YES_OPTION)) {
                    cfflag = 1;
                    (new Act_SaveFile()).actionPerformed(null);
                }
            } else {
                byflag = 1;
            }
            if (byflag == 1) {
                threadflag = false;
                String fileAddress = directory[textAreas_control] + "/"
                        + fileTabbedPane.getTitleAt(textAreas_control);
                f = new File(fileAddress);
                try {
                    lexer = new Lexer(f);
                    lexer.execute();
                    if (lexer.errorList.size() != 0) {
                        consoleTextArea.append("There are some errors:\n");
                        for (int i = 0; i < lexer.errorList.size(); ++i) {
                            lexer.errorList.get(i);
                            consoleTextArea.append("Line: " + lexer.errorList.get(i).getLineNum() + ",Col: "
                                    + lexer.errorList.get(i).getColNum() + ":  "
                                    + lexer.errorList.get(i).getMsg() + "\n");
                        }
                    }
                    consoleTextArea.append("Tokenlist:\n");
                    for (int i = 0; i < lexer.tokenList.size(); ++i) {
                        lexer.tokenList.get(i);
                        consoleTextArea.append("Line: " + lexer.tokenList.get(i).getLineNum()
                                + ",Col:" + lexer.tokenList.get(i).getColNum() + ": "
                                + lexer.tokenList.get(i).getTokenType() + "   "
                                + lexer.tokenList.get(i).getWord() + "\n");
                    }
                    compileflag = 1;
                    if (backcompile == 1) {
                        (new Act_Parser()).actionPerformed(null);
                        backcompile = 0;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 语法分析
     */
    class Act_Parser implements ActionListener {
        public void actionPerformed(ActionEvent e_ji7) {
            consoleTextArea.setText(null);
            if (file_control == 0) {
                if ((JOptionPane.showConfirmDialog(fileTabbedPane, "是否保存？") == JOptionPane.YES_OPTION)) {
                    cfflag = 1;
                    (new Act_SaveFile()).actionPerformed(null);
                }
            } else {
                byflag = 1;
            }
            if (byflag == 1) {
                threadflag = false;
                String fileAddress = directory[textAreas_control] + "/"
                        + fileTabbedPane.getTitleAt(textAreas_control);
                f = new File(fileAddress);
                try {
                    lexer = new Lexer(f);
                    lexer.execute();
                    if(!lexer.errorList.isEmpty()) {
                        for(Error error : lexer.errorList) {
                            consoleTextArea.append("line " + error.getLineNum() + ",col " + error.getColNum() + ":" + error.getMsg());
                        }
                    } else {
                        Parser parser = new Parser(lexer.tokenList);
                        parser.execute();
                        parserTree = parser.getParserTree();
                        if(!parser.errorList.isEmpty()) {
                            for(Error error : parser.errorList) {
                                consoleTextArea.append("line " + error.getLineNum()  + ":" + error.getMsg() + "\n");
                            }
                        } else {
                            parserTree.getNode(0).setChildNum(parserTree.getNode(0).getChildNum() - 1);
                            parserTree.getTreeWidth(parserTree.getNode(0));
                            parserTree.getNode(0).setX(parserTree.getNode(0).getWidth() / 2 - 15);
                            parserTree.getNode(0).setY(0);
                            parserTree.getNode(0).setXmin(0);
                            parserTree.getTreeXY(parserTree.getNode(0), 1);
                            consoleTextArea.setPreferredSize(new Dimension(parserTree.getNode(0).getWidth() + 100, parserTree.getMaxheight()));
                            threadflag = true;
                            new Thread(new PaintThread()).start();
                        }
                    }
                    compileflag = 1;
                    if (backcompile == 1) {
                        (new Act_Parser()).actionPerformed(null);
                        backcompile = 0;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *语义分析
     */
    class Act_Semantic implements ActionListener {
        public void actionPerformed(ActionEvent e_ji18) {
            consoleTextArea.setText(null);

            if (file_control == 0) {
                if ((JOptionPane.showConfirmDialog(fileTabbedPane, "是否保存？") == JOptionPane.YES_OPTION)) {
                    cfflag = 1;
                    (new Act_SaveFile()).actionPerformed(null);
                }
            } else {
                byflag = 1;
            }
            if (byflag == 1) {
                threadflag = false;
                String fileAddress = directory[textAreas_control] + "/"
                        + fileTabbedPane.getTitleAt(textAreas_control);
                f = new File(fileAddress);
                try {
                    lexer = new Lexer(f);
                    lexer.execute();
                    if(!lexer.errorList.isEmpty()) {
                        for(Error error : lexer.errorList) {
                            consoleTextArea.append("line " + error.getLineNum() + ",col " + error.getColNum() + ":" + error.getMsg());
                        }
                    } else {
                        Parser parser = new Parser(lexer.tokenList);
                        parser.execute();
                        if(!parser.errorList.isEmpty()) {
                            for(Error error : parser.errorList) {
                                consoleTextArea.append("line " + error.getLineNum()  + ":" + error.getMsg() + "\n");
                            }
                        } else {
                            Semantic semantic = new Semantic();
                            semantic.init(parser.getParserTree());
                            semantic.scanner();
                            if(!semantic.errorList.isEmpty()) {
                                System.out.println("error");
                                for(Error error : semantic.errorList) {
                                    System.out.println("error");
                                    consoleTextArea.append("line " + error.getLineNum()  + ":" + error.getMsg() + "\n");
                                }
                            } else {
                                consoleTextArea.append("name\ttype\tkind\tlevel\toffset\tarraySize\n");
                                for(SymbolTableNode node : semantic.symbolTable) {
                                    consoleTextArea.append(node.name + "\t" + node.type + "\t" + node.kind + "\t" + node.level.value + "\t" +node.offset.value);
                                    if(node.kind == DataType.IdKind.arrayKind)
                                        consoleTextArea.append("\t" + node.arraySize.value);
                                    consoleTextArea.append("\n");
                                }
                            }
                        }
                    }
                    compileflag = 1;
                    if (backcompile == 1) {
                        (new Act_Parser()).actionPerformed(null);
                        backcompile = 0;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 显示帮助Help事件
     */
    class Act_Help implements ActionListener {
        public void actionPerformed(ActionEvent e_ji9) {
            helpFrame.pack();
            helpFrame.setVisible(true);
            helpFrame.requestFocus();
            helpFrame.setLocation(200, 0);
        }
    }

    class Act_Copy implements ActionListener {
        public void actionPerformed(ActionEvent e_ji10) {
            //复制事件，直接调用JTextArea的copy方法
            fileTextAreas[textAreas_control].copy();
        }
    }

    class Act_Cut implements ActionListener {
        public void actionPerformed(ActionEvent e_ji11) {
            //剪贴事件，直接调用JTextArea的cut方法
            fileTextAreas[textAreas_control].cut();
        }
    }

    class Act_Paste implements ActionListener {
        public void actionPerformed(ActionEvent e_ji12) {
            //粘贴事件，直接调用JTextArea的paste方法
            fileTextAreas[textAreas_control].paste();
        }
    }

    /**
     * 从jar包中读取图片文件
     *
     * @param name
     * @return 返回一个图片对象
     */
    private java.awt.Image loadImage(String name) {
        try {
            java.net.URL url = getClass().getResource(name);
            //根据URL中内容新建一个图片文件
            return createImage((java.awt.image.ImageProducer) url.getContent());
        } catch (Exception ex) {
            return null;
        }
    }

    public void paint(Graphics g) {
        if (threadflag) {
            int i = 1;
            Color c = new Color(0, 255, 0);
            Color d = new Color(254, 235, 188);
            g.setColor(d);
            g.fillRoundRect(parserTree.getNode(0).getX(), parserTree.getNode(0).getY(), 60, 30, 20, 20);
            g.setColor(Color.BLACK);
            g.drawString(parserTree.getNode(0).getType(), parserTree.getNode(0).getX() + 5, parserTree.getNode(0).getY() + 13);
            while (i < parserTree.Size()) {
                if (i == 2) {
                    i++;
                    continue;
                }
                if (parserTree.getNode(i).getChildNum() != 0) {
                    g.setColor(d);
                    g.fillRoundRect(parserTree.getNode(i).getX(), parserTree.getNode(i).getY(), 60, 30, 20, 20);
                    g.setColor(c);
                    g.drawLine(parserTree.getNode(i).getX() + 30, parserTree.getNode(i).getY() + 15, parserTree.getNode(parserTree.getNode(i).getParentId()).getX() + 30, parserTree.getNode(parserTree.getNode(i).getParentId()).getY() + 15);
                    g.setColor(Color.BLACK);
                    g.drawString(parserTree.getNode(i).getType(), parserTree.getNode(i).getX() + 5, parserTree.getNode(i).getY() + 13);
                } else {
                    g.setColor(d);
                    g.fillOval(parserTree.getNode(i).getX() + 10, parserTree.getNode(i).getY(), 39, 29);
                    g.setColor(c);
                    g.drawLine(parserTree.getNode(i).getX() + 30, parserTree.getNode(i).getY() + 15, parserTree.getNode(parserTree.getNode(i).getParentId()).getX() + 30, parserTree.getNode(parserTree.getNode(i).getParentId()).getY() + 15);
                    g.setColor(Color.BLACK);
                    g.drawString(parserTree.getNode(i).getValue(), parserTree.getNode(i).getX() + 22, parserTree.getNode(i).getY() + 22);
                }
                i++;
            }
            return;
        }
    }

    public void update(Graphics g) {
        if (threadflag) {
            offScreenImage = this.createImage(parserTree.getNode(0).getWidth() + 100, parserTree.getMaxheight());
            Graphics gImage = offScreenImage.getGraphics();
            gImage.setColor(Color.white);
            gImage.fillRect(0, 0, parserTree.getNode(0).getWidth() + 100, parserTree.getMaxheight());
            paint(gImage);
            gImage.dispose();
            g.drawImage(offScreenImage, 0, 0, null);
        }
    }

    public static void main(String args[]) throws UnsupportedLookAndFeelException {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        UIManager.setLookAndFeel(new SubstanceOfficeBlue2007LookAndFeel());
        new Main();
    }

    private class PaintThread implements Runnable {

        public void run() {
            while (true) {
                try {
                    update(consoleTextArea.getGraphics());
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}