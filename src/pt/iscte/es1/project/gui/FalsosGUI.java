package pt.iscte.es1.project.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import pt.iscte.es1.project.resources.msg.Mensagem;
import pt.iscte.es1.project.utils.ReadFile;

/**
 * Creation of the window to configure and evaluate
 * @author smmoa
 * @author ruijs
 */

//public abstract class FalsosGUI extends JFrame{
public class FalsosGUI extends JFrame{

	private static final long serialVersionUID = 5240224811001438094L;
	
	private JTable tabela;
	private JButton avaliar = new JButton("Avaliar");
	private JButton guardar = new JButton("Guardar");
	private JLabel fake_neg_label = new JLabel("Falsos Negativos", SwingConstants.CENTER);
	private JTextField fake_neg = new JTextField();
	private JLabel fake_pos_label = new JLabel("Falsos Positivos", SwingConstants.CENTER);
	private JTextField fake_pos = new JTextField();
	private ArrayList<String> rules ;
	
	private String rules_path;
	private String ham_path;
	private String spam_path;
	private ArrayList<Mensagem> hamList = new ArrayList<Mensagem>();
	private ArrayList<Mensagem> spamList = new ArrayList<Mensagem>();
	
	
	/**
	 * Constructor
	 */
	public FalsosGUI(){
		buildGUI();
	}
	
	/**
	 * Constructor
	 * @param title
	 * @param rules_path
	 * @param ham_path
	 * @param spam_path
	 */
	public FalsosGUI(String title, String rules_path, String ham_path, String spam_path) {
		setTitle(title);
		setRules_path(rules_path);
		hamList = ReadFile.messageReader(ham_path);
		spamList = ReadFile.messageReader(spam_path);
		buildGUI();
	}

	/**
	 * Method that creates the window look
	 */
	protected void buildGUI() {
		
		setLayout(new BorderLayout());
		setSize(400, 400);
		setResizable(false);
		
		JPanel downpanel = new JPanel();
		downpanel.setLayout(new GridLayout(0,1));
		
		JPanel buttons = new JPanel();
		buttons.add(avaliar);
		buttons.add(guardar);
		
		JPanel results = new JPanel();
		results.setLayout(new GridLayout(2,0));
		results.add(fake_neg_label);
		results.add(fake_neg);
		results.add(fake_pos_label);
		results.add(fake_pos);
		results.setBorder(new EmptyBorder(0,0,10,20));
		
		downpanel.add(buttons);
		downpanel.add(results);
		
		JScrollPane table = buildTable();
		table.setBorder(new EmptyBorder(10,20,10,0));
		
		add(table, BorderLayout.CENTER);
		add(downpanel, BorderLayout.SOUTH);
		
	}

	public void open() {
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Sets the local variable rules_path to the path given to the method
	 * @param rules_path
	 */
	public void setRules_path(String rules_path) {
		this.rules_path = rules_path;
	}
	

	/**
	 * Creates a JScrollPane including a table with the rules from the file specified in the previous screen
	 * @return table is a JScrollPane 
	 */
	public JScrollPane buildTable(){
		
		JScrollPane table = new JScrollPane();
		
		rules = ReadFile.rulesReader(rules_path);
		String[] tableColumns = new String[] {"Regras", "Pesos"};
		String[][] tableData = new String[rules.size()][2];
		
		for(int i = 0; i < rules.size(); i++){
			tableData[i][0] = rules.get(i);
		}
		
		tabela = new JTable(new DefaultTableModel(tableData, tableColumns));
		
		table.add(tabela);
		table.setViewportView(tabela);
		
		return table;
	}
	
	public static void main(String[] args) {
		new FalsosGUI().open();
	}
	
	public HashMap<String, Double> transformIntoHashMap(double[] pesos){
		HashMap<String,Double> hash = new HashMap<String,Double>();
		for(int i = 0 ; i<pesos.length;i++) {
			hash.put(rules.get(i) , pesos[i]);
		}
		return hash ;
	}
	
	public int[] evaluate(HashMap<String,Double> regrasComPesos) {
		int falsosPositivos=0;
		int falsosNegativos= 0;
		for(Mensagem spam : spamList) {
			double pesoFinalMensagem = getPesoFinalMensagem(regrasComPesos ,spam);
			if(pesoFinalMensagem < 5) {
				falsosNegativos++;
			}
		}
		
		for(Mensagem ham : hamList) {
			double pesoFinalMensagem = getPesoFinalMensagem(regrasComPesos, ham);
			if(pesoFinalMensagem > 5) {
				falsosPositivos++;
			}
		}
		
		int[] falsosPositivosNegativos= new int[2] ;
		falsosPositivosNegativos[0] = falsosPositivos;
		falsosPositivosNegativos[0] = falsosNegativos;
		
		return falsosPositivosNegativos ;
			
		
	}
	
	public double getPesoFinalMensagem(HashMap<String,Double> regrasComPesos , Mensagem mensagem) {
		double pesoFinal= 0;
		for(String regra  : mensagem.getRules()) {
			pesoFinal+= regrasComPesos.get(regra) ;
		}
		return pesoFinal;
	}

	
	
	
	
	
	
	
	
	
	
}