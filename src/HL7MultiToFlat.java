import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

public class HL7MultiToFlat
{
	JLabel jlab;
	JFileChooser fc;
	File[] fileToOpen;
	File fileToSave;
	JFrame jfrm;
	JTextField jtxtOpen;
	JTextField jtxtSave;
	JButton jbtnConvert;
	JTextArea textArea;

	HL7MultiToFlat()
	{
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		final int WIDTH = screenSize.width;
		final int HEIGHT = screenSize.height;
		// Setup the frame accordingly

		this.jfrm = new JFrame("Medical File Conversion");

		this.jfrm.setSize(500, 600);

		this.jfrm.setLocation(WIDTH/4, HEIGHT/4);

		this.jfrm.setDefaultCloseOperation(3);

		this.jfrm.setVisible(true);

		JMenu menu = new JMenu("File");
		JMenuBar menuBar = new JMenuBar();
		menu.setMnemonic(65);
		menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
		menuBar.add(menu);

		JMenuItem menuItem1 = new JMenuItem("Open", 84);
		menuItem1.setAccelerator(KeyStroke.getKeyStroke(49, 8));
		menuItem1.getAccessibleContext().setAccessibleDescription("Open a file for conversion");
		menu.add(menuItem1);

		JMenuItem menuItem2 = new JMenuItem("Exit", 84);

		menuItem2.getAccessibleContext().setAccessibleDescription("Exit Program");
		menu.add(menuItem2);

		this.jfrm.setJMenuBar(menuBar);

		menuItem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				HL7MultiToFlat.this.openFile(ae);
			}
		});
		menuItem2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int result = JOptionPane.showConfirmDialog(HL7MultiToFlat.this.jfrm, 
						"Are you sure you want to exit the application?", 
						"Exit Application", 
						0);
				if (result == 0)
					System.exit(0);
			}
		});
		this.jfrm.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(0, null, null, null, null));
		panel.setBounds(16, 38, 465, 136);
		this.jfrm.getContentPane().add(panel);
		panel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(26, 349, 455, 190);
		this.jfrm.getContentPane().add(scrollPane);

		this.textArea = new JTextArea();
		this.textArea.setEditable(false);
		this.textArea.setBounds(16, 349, 478, 201);

		scrollPane.setViewportView(this.textArea);

		this.jlab = new JLabel("This Program will convert multiple Calinx HL7 files to a .txt $ deliminated file.");
		this.jlab.setBounds(26, 10, 436, 16);
		this.jfrm.getContentPane().add(this.jlab);

		JLabel lblFrom = new JLabel("From:");
		lblFrom.setBounds(6, 6, 43, 16);
		panel.add(lblFrom);

		JButton jbtnOpen = new JButton("Open");
		jbtnOpen.setBounds(31, 24, 77, 29);
		panel.add(jbtnOpen);

		this.jtxtOpen = new JTextField("File to open");
		this.jtxtOpen.setBounds(137, 26, 306, 22);
		panel.add(this.jtxtOpen);

		JLabel lblTo = new JLabel("To:");
		lblTo.setBounds(6, 72, 35, 16);
		panel.add(lblTo);

		JButton jbtnSave = new JButton("Save To: ");
		jbtnSave.setBounds(31, 93, 94, 29);
		panel.add(jbtnSave);

		this.jtxtSave = new JTextField("File to save");
		this.jtxtSave.setBounds(137, 95, 306, 22);
		panel.add(this.jtxtSave);

		this.jbtnConvert = new JButton("Convert");
		this.jbtnConvert.setEnabled(true);
		this.jbtnConvert.setFont(new Font("Lucida Grande", 3, 13));
		this.jbtnConvert.setBounds(16, 249, 117, 29);
		this.jfrm.getContentPane().add(this.jbtnConvert);

		JLabel lblErrorLog = new JLabel("Activity log:");

		lblErrorLog.setBounds(26, 321, 90, 16);
		this.jfrm.getContentPane().add(lblErrorLog);

		jbtnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				HL7MultiToFlat.this.openFile(ae);
			}
		});
		jbtnSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogType(1);
				int returnVal = fc.showSaveDialog(HL7MultiToFlat.this.jfrm);
				if (returnVal == 0) {
					HL7MultiToFlat.this.fileToSave = fc.getSelectedFile();
					HL7MultiToFlat.this.setSaveTxt(HL7MultiToFlat.this.fileToSave.getPath());
				} else {
					HL7MultiToFlat.this.textArea.append("Save command cancelled by user.\n");
				}
			}
		});
		this.jtxtOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String fileName = HL7MultiToFlat.this.jtxtOpen.getText();
				if (fileName != null)
					HL7MultiToFlat.this.fileToOpen[0] = new File(fileName);
			}
		});
		this.jtxtSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String fileName = HL7MultiToFlat.this.jtxtSave.getText();
				if (fileName != null)
					HL7MultiToFlat.this.fileToSave = new File(fileName);
			}
		});
		this.jbtnConvert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					if ((HL7MultiToFlat.this.fileToOpen[0] == null) || (HL7MultiToFlat.this.fileToSave == null)) {
						throw new Exception("Both files not selected");
					}
					if (!HL7MultiToFlat.this.fileToOpen[0].exists()) {
						throw new Exception("Both files not selected");
					}
					if (!HL7MultiToFlat.this.fileToOpen[0].getPath().equals(HL7MultiToFlat.this.fileToSave.getPath())) {
						HL7MultiToFlat.this.jfrm.setCursor(Cursor.getPredefinedCursor(3));
						HL7MultiToFlat.this.jbtnConvert.setEnabled(false);
						HL7MultiToFlat.this.start(HL7MultiToFlat.this.fileToOpen, HL7MultiToFlat.this.fileToSave);
						Toolkit.getDefaultToolkit().beep();
						HL7MultiToFlat.this.jbtnConvert.setEnabled(true);
						HL7MultiToFlat.this.jfrm.setCursor(null);
						HL7MultiToFlat.this.updateTextPane("\nProcess Completed Successfully, File saved at: " + HL7MultiToFlat.this.fileToSave.getPath());
					} else {
						throw new Exception("Opened file and selected save file are the same");
					}
				} catch (Exception e) {
					HL7MultiToFlat.this.jfrm.setCursor(null);
					HL7MultiToFlat.this.textArea.append(e.getMessage() + "\n");
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
				}
			}
		});
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new HL7MultiToFlat();
			}
		});
	}

	public void setOpenTxt(String string)
	{
		this.jtxtOpen.setText(string);
	}

	public void setSaveTxt(String string) {
		this.jtxtSave.setText(string);
	}
	public void updateTextPane(String s) {
		this.textArea.append(s + "\n");
	}

	public void openFile(ActionEvent ae)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogType(0);
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(this.jfrm);
		if (returnVal == 0) {
			this.fileToOpen = fc.getSelectedFiles();
			
			String s = "";
			for (File file:fileToOpen){
				s += file.getName()+",";
				HL7MultiToFlat.this.updateTextPane("\nFile Selected: " + file.getPath());
			}
			setOpenTxt(s);
			
			
		} else {
			this.textArea.append("Open command cancelled by user.\n");
		}
	}

	public void start(File[] fileToOpen, File fileToSave) throws Exception
	{
		
		BufferedWriter out = new BufferedWriter(new FileWriter(fileToSave));
		
		for (File file:fileToOpen){
			BufferedReader in = new BufferedReader(new FileReader(file));
			String rawLine = "";
			
			

			while ((rawLine = in.readLine()) != null)
			{

				String linetype = rawLine.substring(0, 3);
				
				System.out.println(linetype);
				
				if(linetype.equals("FHS")){
					out.write(rawLine + "$");
				}
				if(linetype.equals("MSH")){
					//previous
					out.write(rawLine + "$");
				}
				if(linetype.equals("PID")){
					out.write(rawLine + "$");
				}
				if(linetype.equals("OBR")){
					out.write(rawLine + "$");
				}
				if(linetype.equals("OBX")){
					out.write(rawLine + "$");
				}
				

			}
			in.close();
		}
		
		 out.flush(); out.close();
	}
	
}