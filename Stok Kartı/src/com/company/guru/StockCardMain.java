package com.company.guru;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.company.guru.dal.DatabaseHelper;
import com.company.guru.entity.StockCard;

import javax.swing.JOptionPane;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.RowFilter;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.swing.SwingConstants;
import java.awt.Toolkit;

public class StockCardMain extends JFrame {

	private JPanel contentPane;
	private static DatabaseHelper dbHelper = new DatabaseHelper();
	private JTable tableCards;
	private JTextField textFieldStockCode;
	private JTextField textFieldStockName;
	private JTextField textFieldBarcode;
	private JTextField textFieldSearch;
	private JComboBox comboBoxStockType;
	private JComboBox comboBoxUnit;
	private JComboBox comboBoxVatType;
	private JTextArea textAreaDescription;
	private JFormattedTextField formattedTextFieldCreatedAt;
	private JButton btnEdit;
	private JButton btnDelete;
	private StockCard myStockCard = null;
	private JTextField textFieldEditStockCode;
	private JTextField textFieldEditStockName;
	private JTextField textFieldEditBarcode;
	private JComboBox comboBoxEditStockType;
	private JComboBox comboBoxEditUnit;
	private JComboBox comboBoxEditVatType;
	private JTextArea textAreaEditDescription;
	private JFormattedTextField formattedTextFieldEditCreatedAt;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private JButton btnUpdate;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					dbHelper.createTable();
					
					StockCardMain frame = new StockCardMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws SQLException 
	 */
	public StockCardMain() throws SQLException {
		setResizable(false);
		setType(Type.POPUP);
		setTitle("Stok Kartı Uygulaması");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(150, 60, 967, 767);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 14));
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setBounds(0, 0, 953, 737);
		contentPane.add(tabbedPane);
		
		JPanel panelAllCards = new JPanel();
		tabbedPane.addTab("Tüm Stok Kartları", null, panelAllCards, null);
		panelAllCards.setLayout(null);
		
		JPanel panelEdit = new JPanel();
		panelEdit.setBackground(Color.LIGHT_GRAY);
		panelEdit.setBounds(10, 431, 828, 265);
		panelAllCards.add(panelEdit);
		panelEdit.setLayout(null);
		panelEdit.setVisible(false);
		
		btnEdit = new JButton("Düzenle");
		btnEdit.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnEdit.setBounds(843, 190, 85, 30);
		panelAllCards.add(btnEdit);
		
		btnDelete = new JButton("Sil");
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnDelete.setBounds(843, 230, 85, 30);
		btnDelete.setVisible(true);
		panelAllCards.add(btnDelete);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 66, 828, 355);
		panelAllCards.add(scrollPane);
		
		Object[][] stockCardArray = convertListToObjectArray();
		
		tableCards = new JTable();
		tableCards.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				DefaultTableModel model = (DefaultTableModel) tableCards.getModel();
				int selectedRowIndex = tableCards.getSelectedRow();
				
				String stockCode = model.getValueAt(selectedRowIndex, 0).toString().trim();
				
				try {
					myStockCard = dbHelper.getCardByStockCode(stockCode);
					btnEdit.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							setInfos(myStockCard);
							panelEdit.setVisible(true);
						}
					});
					
					
					btnDelete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Object[] options = {"Evet", "İptal"};
							Object defaultOption = options[0];
							
							int dialogResult = JOptionPane.showOptionDialog(null, "Kayıt silinecek?", "Emin misiniz?", 
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, defaultOption);
							
							if(dialogResult == JOptionPane.YES_OPTION) {
								try {
									dbHelper.deleteStockCard(stockCode);
									DefaultTableModel model = (DefaultTableModel) tableCards.getModel();
									tableCards.setModel(model);
									model.removeRow(selectedRowIndex);	
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
							} 
						}
					});
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		tableCards.setModel(new DefaultTableModel(stockCardArray,
			new String[] {
				"Stok Kodu", "Stok Adı", "Stok Tipi", "Birimi", "Barkodu", "KDV Tipi", "Açıklama", "Oluşturma Tarihi"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			boolean[] columnEditables = new boolean[] {
				false, false, false, false, false, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		scrollPane.setViewportView(tableCards);
		
		textFieldSearch = new JTextField();
		textFieldSearch.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldSearch.setBounds(214, 20, 624, 30);
		panelAllCards.add(textFieldSearch);
		textFieldSearch.setColumns(10);
		
		TableRowSorter<TableModel> sort = new TableRowSorter<>(tableCards.getModel());
		TableColumnModel columnModel = tableCards.getColumnModel();
		columnModel.getColumn(1).setPreferredWidth(200);
		tableCards.setRowSorter(sort);
		
		textFieldSearch.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
            public void insertUpdate(DocumentEvent e) {
                String str = textFieldSearch.getText();
                if (str.trim().length() == 0) {
                    sort.setRowFilter(null);
                } else {
                    sort.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                String str = textFieldSearch.getText();
                if (str.trim().length() == 0) {
                    sort.setRowFilter(null);
                } else {
                    sort.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                }
            }
			
			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		
		JButton btnExport = new JButton("<html>Kopyala &nbsp;veya <br> &nbsp;Kaydet</html>");
		btnExport.setVerticalAlignment(SwingConstants.TOP);
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Object[] options = {"Kopyala", "Kaydet"};
					Object defaultOption = options[0];
					
					int dialogResult = JOptionPane.showOptionDialog(null, "Kartı kopyala veya kaydet?", "Hangisini istersiniz?", 
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, defaultOption);
					
					DefaultTableModel model = (DefaultTableModel) tableCards.getModel();
					int selectedRowIndex = tableCards.getSelectedRow();
					String stockCode = model.getValueAt(selectedRowIndex, 0).toString().trim();
					StockCard sc = dbHelper.getCardByStockCode(stockCode);
	
					if(dialogResult == 0) {
						String newStockCode = stockCode + "_copy";
						
						dbHelper.copyStockCard(sc);
						
						StockCard copyOfSc = dbHelper.getCardByStockCode(newStockCode);
						
						model.insertRow(tableCards.getSelectedRow()+1, new Object[] {
								copyOfSc.getStockCode(), 
								copyOfSc.getStockName(), 
								copyOfSc.getStockType(), 
								copyOfSc.getUnit(), 
								copyOfSc.getBarcode(), 
								copyOfSc.getVatType(), 
								copyOfSc.getDescription(), 
								copyOfSc.getCreatedAt(), 
							}
						);
					} else {
						FileWriter fw = new FileWriter(new File(sc.getStockCode() + "_Stok Kartı.csv"), StandardCharsets.UTF_8);
			            BufferedWriter writer = new BufferedWriter(fw);
					 
						StringBuilder stringBuilder = new StringBuilder();
						String columnNames = "Stok Kodu, Stok Adı, Stok Tipi, Birimi, Barkodu, KDV Tipi, Açıklama, Oluşturma Tarihi";
						
						stringBuilder.append(columnNames + "\n");
						stringBuilder.append(sc.getStockCode() + ",");
						stringBuilder.append(sc.getStockName() + ",");
						stringBuilder.append(sc.getStockType() + ",");
						stringBuilder.append(sc.getUnit() + ",");
						stringBuilder.append(sc.getBarcode() + ",");
						stringBuilder.append(sc.getVatType() + ",");
						stringBuilder.append(sc.getDescription() + ",");
						stringBuilder.append(sc.getCreatedAt() + ",");
						 
						writer.append(stringBuilder.toString());
						writer.close();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}

			
		});
		btnExport.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnExport.setBounds(843, 105, 85, 63);
		panelAllCards.add(btnExport);
		
		JLabel lblNewLabel = new JLabel("Aramak için metin girin:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(10, 20, 183, 30);
		panelAllCards.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Stok Kodu");
		lblNewLabel_1.setBounds(4, 14, 67, 15);
		panelEdit.add(lblNewLabel_1);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		textFieldEditStockCode = new JTextField();
		textFieldEditStockCode.setBounds(68, 8, 253, 30);
		panelEdit.add(textFieldEditStockCode);
		textFieldEditStockCode.setColumns(10);
		textFieldEditStockCode.setEditable(false);
		
		textFieldEditStockName = new JTextField();
		textFieldEditStockName.setBounds(68, 48, 253, 30);
		panelEdit.add(textFieldEditStockName);
		textFieldEditStockName.setColumns(10);
		
		JLabel lblNewLabel_1_1 = new JLabel("Stok Adı");
		lblNewLabel_1_1.setBounds(6, 55, 63, 13);
		panelEdit.add(lblNewLabel_1_1);
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JLabel lblNewLabel_1_1_1 = new JLabel("Stok Tipi");
		lblNewLabel_1_1_1.setBounds(6, 96, 63, 13);
		panelEdit.add(lblNewLabel_1_1_1);
		lblNewLabel_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		comboBoxEditStockType = new JComboBox<Object>(new String[] {"10", "20", "30", "40", "50"});
		comboBoxEditStockType.setBounds(68, 88, 253, 30);
		panelEdit.add(comboBoxEditStockType);
		
		JLabel lblNewLabel_1_1_2 = new JLabel("Birimi");
		lblNewLabel_1_1_2.setBounds(6, 136, 63, 13);
		panelEdit.add(lblNewLabel_1_1_2);
		lblNewLabel_1_1_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		comboBoxEditUnit = new JComboBox<Object>(new String[] {"A", "B", "C", "D", "E", "F", "G"});
		comboBoxEditUnit.setBounds(68, 128, 253, 30);
		panelEdit.add(comboBoxEditUnit);
		
		JLabel lblNewLabel_1_2 = new JLabel("Açıklama");
		lblNewLabel_1_2.setBounds(331, 14, 63, 13);
		panelEdit.add(lblNewLabel_1_2);
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		textAreaEditDescription = new JTextArea();
		textAreaEditDescription.setBounds(403, 11, 408, 105);
		panelEdit.add(textAreaEditDescription);
		textAreaEditDescription.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		textFieldEditBarcode = new JTextField();
		textFieldEditBarcode.setBounds(68, 175, 253, 30);
		panelEdit.add(textFieldEditBarcode);
		textFieldEditBarcode.setColumns(10);
		
		JLabel lblNewLabel_1_1_3 = new JLabel("Barkodu");
		lblNewLabel_1_1_3.setBounds(6, 182, 63, 13);
		panelEdit.add(lblNewLabel_1_1_3);
		lblNewLabel_1_1_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		comboBoxEditVatType = new JComboBox<Object>(new String[] {"0.25", "0.12", "0.05", "0.18"});
		comboBoxEditVatType.setBounds(68, 224, 253, 30);
		panelEdit.add(comboBoxEditVatType);
		
		JLabel lblNewLabel_1_1_3_1 = new JLabel("KDV Tipi");
		lblNewLabel_1_1_3_1.setBounds(6, 232, 63, 13);
		panelEdit.add(lblNewLabel_1_1_3_1);
		lblNewLabel_1_1_3_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		btnUpdate = new JButton("Kaydet");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// @TODO Eğer güncelleme işlemi başarılı ise alttaki satır çalıştırılacak
				try {
					StockCard myStockCard2 = getEditInfos();
					System.out.println(myStockCard2.getStockName());
					dbHelper.updateStockCard(myStockCard2);
					myStockCard2 = dbHelper.getCardByStockCode(myStockCard2.getStockCode());
					DefaultTableModel model = (DefaultTableModel) tableCards.getModel();
					tableCards.setModel(model);
					model.insertRow(tableCards.getSelectedRow(), new Object[] {
							myStockCard2.getStockCode(), 
							myStockCard2.getStockName(), 
							myStockCard2.getStockType(), 
							myStockCard2.getUnit(), 
							myStockCard2.getBarcode(), 
							myStockCard2.getVatType(), 
							myStockCard2.getDescription(), 
							myStockCard2.getCreatedAt(), 
						}
					);
					model.removeRow(tableCards.getSelectedRow());
					panelEdit.setVisible(false);
				} catch (SQLException e1) {
					panelEdit.setVisible(true);
					e1.printStackTrace();
				}
				
			}
		});
		btnUpdate.setBounds(666, 128, 145, 30);
		panelEdit.add(btnUpdate);
		btnUpdate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		formattedTextFieldEditCreatedAt = new JFormattedTextField(dateFormat);
		formattedTextFieldEditCreatedAt.setBounds(403, 128, 253, 30);
		panelEdit.add(formattedTextFieldEditCreatedAt);
		
		JLabel lblNewLabel_1_2_1 = new JLabel("<html>Oluşturma <br>&nbsp&nbsp;&nbsp;Tarihi</html>");
		lblNewLabel_1_2_1.setBounds(331, 127, 63, 26);
		panelEdit.add(lblNewLabel_1_2_1);
		lblNewLabel_1_2_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JPanel panelNewCard = new JPanel();
		tabbedPane.addTab("Yeni Stok Kart\u0131", null, panelNewCard, null);
		panelNewCard.setLayout(null);
		
		JLabel lblStockCode = new JLabel("Stok Kodu:");
		lblStockCode.setBounds(20, 25, 150, 30);
		lblStockCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelNewCard.add(lblStockCode);
		
		textFieldStockCode = new JTextField();
		textFieldStockCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldStockCode.setBounds(180, 25, 500, 30);
		panelNewCard.add(textFieldStockCode);
		textFieldStockCode.setColumns(10);
		
		JLabel lblStockName = new JLabel("Stok Ad\u0131:");
		lblStockName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblStockName.setBounds(20, 85, 150, 30);
		panelNewCard.add(lblStockName);
		
		textFieldStockName = new JTextField();
		textFieldStockName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldStockName.setColumns(10);
		textFieldStockName.setBounds(180, 85, 500, 30);
		panelNewCard.add(textFieldStockName);
		
		JLabel lblStokTipi = new JLabel("Stok Tipi:");
		lblStokTipi.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblStokTipi.setBounds(20, 145, 150, 30);
		panelNewCard.add(lblStokTipi);
		
		comboBoxStockType = new JComboBox<Object>();
		comboBoxStockType.setFont(new Font("Tahoma", Font.PLAIN, 14));
		comboBoxStockType.setModel(new DefaultComboBoxModel<Object>(new String[] {"10", "20", "30", "40", "50"}));
		comboBoxStockType.setBounds(180, 145, 500, 30);
		panelNewCard.add(comboBoxStockType);
		
		JLabel lblUnit = new JLabel("Birim:");
		lblUnit.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblUnit.setBounds(20, 205, 150, 30);
		panelNewCard.add(lblUnit);
		
		comboBoxUnit = new JComboBox<Object>();
		comboBoxUnit.setModel(new DefaultComboBoxModel<Object>(new String[] {"A", "B", "C", "D", "E", "F", "G"}));
		comboBoxUnit.setFont(new Font("Tahoma", Font.PLAIN, 14));
		comboBoxUnit.setBounds(180, 205, 500, 30);
		panelNewCard.add(comboBoxUnit);
		
		JLabel lblBarcode = new JLabel("Barkod:");
		lblBarcode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblBarcode.setBounds(20, 265, 150, 30);
		panelNewCard.add(lblBarcode);
		
		textFieldBarcode = new JTextField();
		textFieldBarcode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldBarcode.setColumns(10);
		textFieldBarcode.setBounds(180, 265, 500, 30);
		panelNewCard.add(textFieldBarcode);
		
		comboBoxVatType = new JComboBox<Object>();
		comboBoxVatType.setModel(new DefaultComboBoxModel<Object>(new String[] {"0.25", "0.12", "0.05", "0.18"}));
		comboBoxVatType.setFont(new Font("Tahoma", Font.PLAIN, 14));
		comboBoxVatType.setBounds(180, 325, 500, 30);
		panelNewCard.add(comboBoxVatType);
		
		JLabel lblVatType = new JLabel("KDV Tipi:");
		lblVatType.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblVatType.setBounds(20, 325, 150, 30);
		panelNewCard.add(lblVatType);
		
		JLabel lblDescription = new JLabel("A\u00E7\u0131klama:");
		lblDescription.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDescription.setBounds(20, 385, 150, 30);
		panelNewCard.add(lblDescription);
		
		textAreaDescription = new JTextArea();
		textAreaDescription.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textAreaDescription.setBounds(180, 385, 500, 200);
		panelNewCard.add(textAreaDescription);
		
		JLabel lblCreatedAt = new JLabel("Olu\u015Fturulma Tarihi: ");
		lblCreatedAt.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCreatedAt.setBounds(20, 620, 150, 30);
		panelNewCard.add(lblCreatedAt);
		
		formattedTextFieldCreatedAt = new JFormattedTextField(dateFormat);
		formattedTextFieldCreatedAt.setFont(new Font("Tahoma", Font.PLAIN, 14));
		formattedTextFieldCreatedAt.setValue(new Date());
		formattedTextFieldCreatedAt.setBounds(180, 620, 500, 30);
		panelNewCard.add(formattedTextFieldCreatedAt);
		
		JButton btnSaveCard = new JButton("KAYDET");
		btnSaveCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StockCard stockCard = getInfos();
				try {
					dbHelper.add(stockCard);
					DefaultTableModel model = (DefaultTableModel) tableCards.getModel();
					tableCards.setModel(model);
					model.insertRow(model.getRowCount(), new Object[] {
							stockCard.getStockCode(), 
							stockCard.getStockName(), 
							stockCard.getStockType(), 
							stockCard.getUnit(), 
							stockCard.getBarcode(), 
							stockCard.getVatType(), 
							stockCard.getDescription(), 
							stockCard.getCreatedAt(), 
						}
					);
					cleanAllFields();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnSaveCard.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnSaveCard.setBounds(719, 584, 199, 66);
		panelNewCard.add(btnSaveCard);
	}
	
	private StockCard getInfos() {
		StockCard stockCard = new StockCard();
		
		stockCard.setStockCode(textFieldStockCode.getText());
		stockCard.setStockName(textFieldStockName.getText());
		stockCard.setStockType(Integer.parseInt(comboBoxStockType.getSelectedItem().toString()));
		stockCard.setUnit(comboBoxUnit.getSelectedItem().toString());
		stockCard.setBarcode(textFieldBarcode.getText());
		stockCard.setVatType(Double.parseDouble(comboBoxVatType.getSelectedItem().toString()));
		stockCard.setDescription(textAreaDescription.getText());
		stockCard.setCreatedAt(formattedTextFieldCreatedAt.getText());
		
		return stockCard;
	}
	
	private void setInfos(StockCard stockCard) {
		textFieldEditStockCode.setText(stockCard.getStockCode());
		textFieldEditStockName.setText(stockCard.getStockName());
		comboBoxEditStockType.setSelectedItem(Integer.toString(stockCard.getStockType()));
		comboBoxEditUnit.setSelectedItem(stockCard.getUnit());
		textFieldEditBarcode.setText(stockCard.getBarcode());
		comboBoxEditVatType.setSelectedItem(Double.toString(Math.round((stockCard.getVatType() * 100.0)) / 100.0));
		textAreaEditDescription.setText(stockCard.getDescription());
		formattedTextFieldEditCreatedAt.setText(stockCard.getCreatedAt());
	}
	
	private StockCard getEditInfos() {
		StockCard stockCard = new StockCard();
		
		stockCard.setStockCode(textFieldEditStockCode.getText());
		stockCard.setStockName(textFieldEditStockName.getText());
		stockCard.setStockType(Integer.parseInt(comboBoxEditStockType.getSelectedItem().toString()));
		stockCard.setUnit(comboBoxEditUnit.getSelectedItem().toString());
		stockCard.setBarcode(textFieldEditBarcode.getText());
		stockCard.setVatType(Float.parseFloat(comboBoxEditVatType.getSelectedItem().toString()));
		stockCard.setDescription(textAreaEditDescription.getText());
		stockCard.setCreatedAt(formattedTextFieldEditCreatedAt.getText());
		
		return stockCard;
	}
	
	private void cleanAllFields() {
		textFieldStockCode.setText("");
		textFieldStockName.setText("");
		textFieldBarcode.setText("");
		textAreaDescription.setText("");
	}
	
	private Object[][] convertListToObjectArray() throws SQLException {
		final int TABLE_COL_NUMBERS = 8;
		
		List<StockCard> stockCards = dbHelper.getAllStockCards();
		Object[][] stockCardInfos = new Object[stockCards.size()][TABLE_COL_NUMBERS];
		
		for (int i = 0; i < stockCards.size(); i++) {
			int x = 0;
			
			while(x <= TABLE_COL_NUMBERS) {
				stockCardInfos[i][0] = stockCards.get(i).getStockCode();
				stockCardInfos[i][1] = stockCards.get(i).getStockName();
				stockCardInfos[i][2] = stockCards.get(i).getStockType();
				stockCardInfos[i][3] = stockCards.get(i).getUnit();
				stockCardInfos[i][4] = stockCards.get(i).getBarcode();
				stockCardInfos[i][5] = (double)Math.round(stockCards.get(i).getVatType() * 100) / 100;
				stockCardInfos[i][6] = stockCards.get(i).getDescription();
				stockCardInfos[i][7] = stockCards.get(i).getCreatedAt();
				
				x++;
			}
		}
		
		return stockCardInfos;
	}
	
	public StockCard getEditableStockCard(StockCard stockCard) {
		return stockCard;
	}
	
	public StockCard sendEditableStockCard() {
		return getEditableStockCard(myStockCard);
	}
}
