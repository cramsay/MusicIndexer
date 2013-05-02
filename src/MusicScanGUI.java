import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MusicScanGUI extends JFrame implements ProgressListener{

	private GUI parent;
	private CollectionEngine engine;
	private File root;
	private JButton btnClose;
	private JButton btnCancel;
	private JLabel lblTask;
	private JProgressBar currentProgress;
	private JProgressBar overallProgress;
	private JTextPane infoLog;
	private JTextPane warningLog;
	private Thread scanner;
	private JLabel lblInfoLog;
	private JLabel lblWarningLog;
	
    public MusicScanGUI(GUI parent, CollectionEngine engine, File root) {
    	this.engine = engine;
    	this.root = root;
    	this.parent = parent;
    	makeGUI();
    }

	@Override
	public void done() {
		btnCancel.setEnabled(false);
		btnClose.setEnabled(true);
		JOptionPane.showMessageDialog(null, "Scan Done");
		currentProgress.setIndeterminate(false);
		currentProgress.setValue(currentProgress.getMaximum());
		overallProgress.setValue(overallProgress.getMaximum());
		parent.refreshGUI();
	}

	public void startScan() {
		engine.primeForScan(this, root);
		scanner = new Thread(engine);
		scanner.start();
	}

	@Override
	public void setOverallProgress(int val) {
		final int v=val;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				overallProgress.setValue(v);
			}
		});
	}

	@Override
	public void setOverallMax(int val) {
		final int v=val;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				overallProgress.setMaximum(v);
				overallProgress.setMinimum(0);
			}
		});
	}

	@Override
	public void setCurrentProgress(int val) {
		final int v=val;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				currentProgress.setValue(v);
				}
		});
	}

	@Override
	public void setCurrentMax(int val) {
		final int v=val;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				currentProgress.setIndeterminate(false);
				currentProgress.setMaximum(v);
				currentProgress.setMinimum(0);
				}
		});
	}

	@Override
	public void addInfo(String msg) {
		final String m=msg;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				infoLog.setText(infoLog.getText()+m+"\n");
				}
		});
	}

	@Override
	public void addWarning(String msg) {
		final String m=msg;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				warningLog.setText(warningLog.getText()+m+"\n");
			}
		});
	}

	@Override
	public void setTaskName(String msg){
		final String m=msg;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				lblTask.setText("Task: "+m);
			}
		});
	}
	
	@Override
	public void clearInfo() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				infoLog.setText("");
			}
		});
	}

	@Override
	public void clearWarning() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				warningLog.setText("");
			}
		});
	}
	
	private void cancel(){
		int res = JOptionPane.showConfirmDialog(null,"Cancel Scan", "Do you really want to cancel?",JOptionPane.YES_NO_OPTION);
		if (res==JOptionPane.NO_OPTION)
			return;
		scanner.stop();
		engine.clearCollection();
		done();
	}
	
	private void close(){
		this.setVisible(true);
		this.dispose();
	}
	
	//Swing stuff
	private void makeGUI(){
        this.setSize(378, 357);
        this.setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(new GridLayout(0, 1, 0, 0));
        
        lblTask = new JLabel("Task: ");
        lblTask.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTask);
        
        currentProgress = new JProgressBar();
        currentProgress.setStringPainted(true);
        currentProgress.setIndeterminate(true);
        panel.add(currentProgress);
        
        JLabel lblOverall = new JLabel("Overall");
        lblOverall.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblOverall);
        
        overallProgress = new JProgressBar();
        overallProgress.setStringPainted(true);
        overallProgress.setValue(0);
        panel.add(overallProgress);
        
        JLabel label = new JLabel("");
        panel.add(label);
        
        JSplitPane splitPane = new JSplitPane();
        getContentPane().add(splitPane, BorderLayout.CENTER);
        
        infoLog = new JTextPane();
        infoLog.setEditable(false);
        infoLog.setPreferredSize(new Dimension(240,400));
        JScrollPane scrollPane = new JScrollPane(infoLog);
        splitPane.setLeftComponent(scrollPane);
        
        lblInfoLog = new JLabel("Info Log:");
        lblInfoLog.setHorizontalAlignment(SwingConstants.CENTER);
        scrollPane.setColumnHeaderView(lblInfoLog);
        
        warningLog = new JTextPane();
        warningLog.setEditable(false);
        warningLog.setPreferredSize(new Dimension(240,400));
        JScrollPane scrollPane_1 = new JScrollPane(warningLog);
        splitPane.setRightComponent(scrollPane_1);
        splitPane.setDividerLocation(240);
        lblWarningLog = new JLabel("Warning Log:");
        lblWarningLog.setHorizontalAlignment(SwingConstants.CENTER);
        scrollPane_1.setColumnHeaderView(lblWarningLog);
        
        JPanel jp = new JPanel();
        btnClose = new JButton("Close");
        btnClose.setEnabled(false);
        btnClose.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
				close();
			}});
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
				cancel();
			}});
        jp.add(btnCancel);
        jp.add(btnClose);
        getContentPane().add(jp, BorderLayout.SOUTH);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
	}


}
