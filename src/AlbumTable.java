import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;

public class AlbumTable extends JPanel{
	
	private JTable albumTable;
	private TableRowSorter<TableModel> tableSorter;
	private CollectionEngine engine;
	private JTextField txtArt;
	private JTextField txtAlb;
	private JTextField txtYear;
	private JLabel lblStatus;
	private JComboBox<String> cmbYear;
	private JComboBox<String> cmbOwn;
	private JButton btnApply;
	private JButton btnClear;

	public AlbumTable(CollectionEngine engine){
	
		this.engine = engine;
		makeGUI();
	}
	
	public void populate(){
		//Just adapted default table model to have a check box
		//in the 3rd column
		class AlbumTableModel extends DefaultTableModel{
			public AlbumTableModel (Object[][] d, Object[] c){
				super(d,c);
			}
			@Override  
			public Class getColumnClass(int col) {
				if (col==3) return Boolean.class;  
				else if (col==2) return Integer.class;
				else return String.class;
			}  
			@Override
			public boolean isCellEditable(int row,int col) {
				return false;
			}
		}

		Object[][] tableData = engine.getAlbumDetailsArray();
		AlbumTableModel model = new AlbumTableModel(tableData,
				new String[] {"Artist", "Album", "Year", "Owned?"	}
				);

		tableSorter = new TableRowSorter<TableModel>(model);
		albumTable.setModel(model);
		albumTable.setRowSorter(tableSorter);

	}

	private void applyAllFilters(){
		
		ArrayList<RowFilter<Object, Object>> filters = new ArrayList<>() ;
		
		//Apply Artist
		String art = txtArt.getText();
		if (!art.isEmpty())
			filters.add(RowFilter.regexFilter(art, 0));

		
		//Apply Album
		String alb = txtAlb.getText();
		if (!alb.isEmpty())
			filters.add(RowFilter.regexFilter(alb, 1));
		
		//Apply Year
		if (!txtYear.getText().isEmpty()){
			int year = Integer.parseInt(txtYear.getText());
			if (cmbYear.getSelectedItem().equals("="))
				filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, year, 2));
			else if (cmbYear.getSelectedItem().equals(">"))
				filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.BEFORE, year, 2));
			else if (cmbYear.getSelectedItem().equals("<"))
				filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.AFTER, year, 2));
		}
		
		//Apply Ownership
		if (cmbOwn.getSelectedItem().equals("Only Owned")){
			filters.add(new RowFilter<Object,Object>() {
				  public boolean include(Entry<? extends Object, ? extends Object> entry) {
					    for (int i = entry.getValueCount() - 1; i >= 0; i--) {
					      if (entry.getValue(i).equals(true))
					       return true;
						}
					    return false;
					 }
				  });
		}
		else if (cmbOwn.getSelectedItem().equals("Only Not Owned")){
			filters.add(new RowFilter<Object,Object>() {
				  public boolean include(Entry<? extends Object, ? extends Object> entry) {
					    for (int i = entry.getValueCount() - 1; i >= 0; i--) {
					      if (entry.getValue(i).equals(true))
					       return false;
						}
					    return true;
					 }
				  });
		}
		
		//Set status
		if (filters.size()>0){
			tableSorter.setRowFilter(RowFilter.andFilter(filters));
			lblStatus.setText("Status: Filter Applied");
		}
		
		//Clear components
		clearFilterComponents();
	}
	
	public void clearFilters(){
		lblStatus.setText("Status: No Filters");
		populate();
		clearFilterComponents();
	}
	
	private void clearFilterComponents(){
		txtAlb.setText("");
		txtArt.setText("");
		txtYear.setText("");
		cmbYear.setSelectedIndex(0);
		cmbOwn.setSelectedIndex(0);
	}

	private void makeGUI(){
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{2, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		super.setLayout(gridBagLayout);
		
		lblStatus = new JLabel("Status: No Filters");
		GridBagConstraints gbc_lblStatus = new GridBagConstraints();
		gbc_lblStatus.anchor = GridBagConstraints.WEST;
		gbc_lblStatus.gridwidth = 4;
		gbc_lblStatus.insets = new Insets(0, 0, 5, 5);
		gbc_lblStatus.gridx = 1;
		gbc_lblStatus.gridy = 0;
		super.add(lblStatus, gbc_lblStatus);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 12;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 5;
		gbc_scrollPane.gridy = 0;
		super.add(scrollPane, gbc_scrollPane);
		
		albumTable = new JTable();
		albumTable.setModel(new DefaultTableModel(null,
			new String[] {
				"Artist", "Album", "Year", "Owned?"
			}
		));
		scrollPane.setViewportView(albumTable);
		
		JLabel lblArtistName = new JLabel("Artist Name:");
		GridBagConstraints gbc_lblArtistName = new GridBagConstraints();
		gbc_lblArtistName.gridwidth = 4;
		gbc_lblArtistName.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblArtistName.insets = new Insets(0, 0, 5, 5);
		gbc_lblArtistName.gridx = 1;
		gbc_lblArtistName.gridy = 2;
		super.add(lblArtistName, gbc_lblArtistName);
		
		txtArt = new JTextField();
		GridBagConstraints gbc_txtArt = new GridBagConstraints();
		gbc_txtArt.anchor = GridBagConstraints.NORTH;
		gbc_txtArt.gridwidth = 4;
		gbc_txtArt.insets = new Insets(0, 0, 5, 5);
		gbc_txtArt.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtArt.gridx = 1;
		gbc_txtArt.gridy = 3;
		super.add(txtArt, gbc_txtArt);
		txtArt.setColumns(10);
		
		JLabel lblAlbumName = new JLabel("Album Name:");
		GridBagConstraints gbc_lblAlbumName = new GridBagConstraints();
		gbc_lblAlbumName.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblAlbumName.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlbumName.gridx = 1;
		gbc_lblAlbumName.gridy = 4;
		super.add(lblAlbumName, gbc_lblAlbumName);
		
		txtAlb = new JTextField();
		txtAlb.setColumns(10);
		GridBagConstraints gbc_txtAlb = new GridBagConstraints();
		gbc_txtAlb.anchor = GridBagConstraints.NORTH;
		gbc_txtAlb.gridwidth = 4;
		gbc_txtAlb.insets = new Insets(0, 0, 5, 5);
		gbc_txtAlb.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtAlb.gridx = 1;
		gbc_txtAlb.gridy = 5;
		super.add(txtAlb, gbc_txtAlb);
		
		JLabel lblYearFilter = new JLabel("Year Filter:");
		GridBagConstraints gbc_lblYearFilter = new GridBagConstraints();
		gbc_lblYearFilter.gridwidth = 4;
		gbc_lblYearFilter.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblYearFilter.insets = new Insets(0, 0, 5, 5);
		gbc_lblYearFilter.gridx = 1;
		gbc_lblYearFilter.gridy = 6;
		super.add(lblYearFilter, gbc_lblYearFilter);
		
		txtYear = new JTextField();
		txtYear.setColumns(10);
		GridBagConstraints gbc_txtYear = new GridBagConstraints();
		gbc_txtYear.anchor = GridBagConstraints.NORTH;
		gbc_txtYear.gridwidth = 2;
		gbc_txtYear.insets = new Insets(0, 0, 5, 5);
		gbc_txtYear.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtYear.gridx = 1;
		gbc_txtYear.gridy = 7;
		super.add(txtYear, gbc_txtYear);
		
		cmbYear = new JComboBox();
		cmbYear.setFont(new Font("Dialog", Font.PLAIN, 8));
		cmbYear.setModel(new DefaultComboBoxModel(new String[] {"=", ">", "<"}));
		GridBagConstraints gbc_cmbYear = new GridBagConstraints();
		gbc_cmbYear.anchor = GridBagConstraints.NORTH;
		gbc_cmbYear.gridwidth = 2;
		gbc_cmbYear.insets = new Insets(0, 0, 5, 5);
		gbc_cmbYear.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbYear.gridx = 3;
		gbc_cmbYear.gridy = 7;
		super.add(cmbYear, gbc_cmbYear);
		
		JLabel lblOwnedFilter = new JLabel("Owned Filter:");
		GridBagConstraints gbc_lblOwnedFilter = new GridBagConstraints();
		gbc_lblOwnedFilter.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblOwnedFilter.insets = new Insets(0, 0, 5, 5);
		gbc_lblOwnedFilter.gridx = 1;
		gbc_lblOwnedFilter.gridy = 8;
		super.add(lblOwnedFilter, gbc_lblOwnedFilter);
		
		cmbOwn = new JComboBox();
		cmbOwn.setFont(new Font("Dialog", Font.PLAIN, 10));
		cmbOwn.setModel(new DefaultComboBoxModel(new String[] {"All", "Only Owned", "Only Not Owned"}));
		GridBagConstraints gbc_cmbOwn = new GridBagConstraints();
		gbc_cmbOwn.anchor = GridBagConstraints.NORTH;
		gbc_cmbOwn.gridwidth = 4;
		gbc_cmbOwn.insets = new Insets(0, 0, 5, 5);
		gbc_cmbOwn.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbOwn.gridx = 1;
		gbc_cmbOwn.gridy = 9;
		super.add(cmbOwn, gbc_cmbOwn);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridx = 1;
		gbc_separator.gridy = 10;
		super.add(separator, gbc_separator);
		
		btnApply= new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				applyAllFilters();
				
			}
		});
		btnApply.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_btnApplyFilter = new GridBagConstraints();
		gbc_btnApplyFilter.fill = GridBagConstraints.VERTICAL;
		gbc_btnApplyFilter.insets = new Insets(0, 0, 0, 5);
		gbc_btnApplyFilter.gridx = 1;
		gbc_btnApplyFilter.gridy = 11;
		super.add(btnApply, gbc_btnApplyFilter);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				clearFilters();
				
			}
		});
		btnClear.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.fill = GridBagConstraints.VERTICAL;
		gbc_btnClear.insets = new Insets(0, 0, 0, 5);
		gbc_btnClear.gridx = 2;
		gbc_btnClear.gridy = 11;
		super.add(btnClear, gbc_btnClear);

	}
}
