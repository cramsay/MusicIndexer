import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class AlbumTable extends JPanel{
	private JTable albumTable;
	private TableRowSorter<TableModel> tableSorter;
	private CollectionEngine engine;
	
	public AlbumTable(CollectionEngine engine){
	
		this.engine = engine;
		
		TableModel model = new DefaultTableModel(engine.getAlbumDetailsArray(),
				new String[] {"Artist", "Album", "Year", "Owned?"	}
			);
		albumTable = new JTable();
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		albumTable.setRowSorter(sorter);
		super.add(new JScrollPane(albumTable));
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

	public void applyArtistFilter(String match){
		
	}
	
	public void applyAlbumFilter(String match){
		
	}
	
	public void applyYearFilter(int match){
		
	}
	
	public void applyOwnedFilter(Boolean match){
		
	}
}
