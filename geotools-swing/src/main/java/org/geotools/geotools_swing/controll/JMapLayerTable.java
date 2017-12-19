package org.geotools.geotools_swing.controll;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;

import org.geotools.geotools_swing.MapListener;
import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.event.MapPaneEvent;
import org.geotools.geotools_swing.event.MapPaneListener;
import org.geotools.geotools_swing.old.JSimpleStyleDialog;
import org.geotools.map.Layer;
import org.geotools.map.StyleLayer;
import org.geotools.map.event.MapLayerListEvent;
import org.geotools.map.event.MapLayerListListener;
import org.geotools.styling.Style;
import org.jdesktop.swingx.JXTable;
import org.sam.swing.resource.ResourceLoader;
import org.sam.swing.table.JSTableBuilder;
import org.sam.swing.table.JSTableColumn;
import org.sam.swing.table.JSTableColumnModel;
import org.sam.swing.table.JSTableModel;
import org.sam.swing.table.defaultImpl.JSTableDefaultBuilderImpl;
import org.sam.swing.table.defaultImpl.JSTableModelDefaultAdapter;
import org.sam.swing.table.editor.JSTableCheckboxEditor;
import org.sam.swing.table.editor.JSTableImageButtonEditor;
import org.sam.swing.table.renderer.JSTableCheckboxRenderer;
import org.sam.swing.table.renderer.JSTableImageButtonRenderer;

/**
 * 图层树控件
 * 
 * @author sam
 *
 */
public class JMapLayerTable extends JPanel implements MapListener, MapPaneListener,MapLayerListListener {

	private static final long serialVersionUID = -2455449236847315868L;

	/**
	 * 当前的表格控件
	 */
	private JXTable table;

	/**
	 * 表格的model
	 */
	private JSTableModel<Collection<Layer>> tableModel;

	/**
	 * 表格列的model
	 */
	private JSTableColumnModel tableColumnModel;

	/**
	 * 当前的画布对象
	 */
	private MapPane mapPane;

	/**
	 * 初始化控件显示
	 * 
	 * @param mapPane
	 */
	public JMapLayerTable(MapPane mapPane) {
		this.initCompenets();
		this.setMapPane(mapPane);
	}

	/**
	 * 初始化控件显示
	 * @throws Exception 
	 */
	private void initCompenets() {
		this.setLayout(new BorderLayout());

		DefaultTableCellRenderer renderl = new DefaultTableCellRenderer();
		renderl.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);

		JSTableColumn column0 = new JSTableColumn();
		column0.setModelIndex(0);
		column0.setHeaderValue("可见");
		column0.setTitle("可见");
		column0.setIdentifier("visible");
		column0.setMaxWidth(50);
		column0.setResizable(false);
		column0.setDefaultValue(true);
		JSTableCheckboxRenderer cbxRenderer = new JSTableCheckboxRenderer();
		column0.setCellRenderer(cbxRenderer);
		JCheckBox checkbox = new JCheckBox();
		checkbox.setHorizontalAlignment(JCheckBox.CENTER);
		column0.setCellEditor(new JSTableCheckboxEditor(checkbox));

		JSTableColumn column1 = new JSTableColumn();
		column1.setModelIndex(1);
		column1.setHeaderValue("名称");
		column1.setTitle("名称");
		column1.setIdentifier("title");
		column1.setWidth(80);
		column1.setEditable(false);
		column1.setCellRenderer(renderl);

		JSTableColumn column2 = new JSTableColumn();
		column2.setModelIndex(2);
		column2.setHeaderValue("编辑");
		column2.setTitle("编辑");
		column2.setIdentifier("selected");
		column2.setMaxWidth(50);
		column2.setResizable(false);
		column2.setDefaultValue(false);
		column2.setCellRenderer(cbxRenderer);
		JCheckBox editcheckbox = new JCheckBox();
		editcheckbox.setHorizontalAlignment(JCheckBox.CENTER);
		column2.setCellEditor(new JSTableCheckboxEditor(editcheckbox));

		JSTableColumn column3 = new JSTableColumn();
		column3.setModelIndex(3);
		column3.setHeaderValue("样式");
		column3.setTitle("样式");
		column3.setIdentifier("style");
		column3.setMaxWidth(50);
		column3.setCellRenderer(new JSTableImageButtonRenderer(new ImageIcon(ResourceLoader.getResource("style.gif"))));
		JButton btnStyle = new JButton(new ImageIcon(ResourceLoader.getResource("style.gif")));
		JSTableImageButtonEditor imageButtonEditor = new JSTableImageButtonEditor(btnStyle);
		column3.setCellEditor(imageButtonEditor);
		// 给button添加按钮事件
		btnStyle.addActionListener(e -> {
			Object layer = null;
			try {
				layer = tableModel.getData(table.convertRowIndexToModel(table.getSelectedRow()) );
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (layer instanceof StyleLayer) {
				StyleLayer styleLayer = (StyleLayer) layer;
				Style style = JSimpleStyleDialog.showDialog(this, styleLayer);
				if (style != null) {
					styleLayer.setStyle(style);
				}
			}
		});
		
		JSTableBuilder<Collection<Layer>> builder = new JSTableDefaultBuilderImpl<>(Layer.class,column0, column1 , column2,column3);
		try {
			table = builder.buildTable();
			tableModel = builder.getTableModel();
			tableModel.setTableModelLinster(new JSTableModelDefaultAdapter<Layer>() {
				
				/**
				 * 检索数据
				 */
				@Override
				public Collection<Layer> onRetrieve() throws Exception {
					return getMapPane().getMapContent().layers();
				}
				
			});
			tableColumnModel = builder.getTableColumnModel();
			tableModel.setEditable(true);

			this.add(new JScrollPane(table), BorderLayout.CENTER);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	// begin implemnts 接口

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapPane getMapPane() {
		return this.mapPane;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMapPane(MapPane mapPane) {
		this.mapPane = mapPane;
		if (this.mapPane != null){
			this.mapPane.addMapPaneListener(this);
			this.mapPane.getMapContent().addMapLayerListListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onNewContext(MapPaneEvent ev) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onNewRenderer(MapPaneEvent ev) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResized(MapPaneEvent ev) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisplayAreaChanged(MapPaneEvent ev) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRenderingStarted(MapPaneEvent ev) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRenderingStopped(MapPaneEvent ev) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRenderingProgress(MapPaneEvent ev) {

	}

	@Override
	public void layerAdded(MapLayerListEvent event) {
		try {
			tableModel.insert(0, event.getElement());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void layerRemoved(MapLayerListEvent event) {
		
	}

	@Override
	public void layerChanged(MapLayerListEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layerMoved(MapLayerListEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layerPreDispose(MapLayerListEvent event) {
		// TODO Auto-generated method stub
		
	}

	// end
}