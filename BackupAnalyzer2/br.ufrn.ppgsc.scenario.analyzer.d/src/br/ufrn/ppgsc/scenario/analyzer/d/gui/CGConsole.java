package br.ufrn.ppgsc.scenario.analyzer.d.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import br.ufrn.ppgsc.scenario.analyzer.d.data.DataUtil;
import br.ufrn.ppgsc.scenario.analyzer.d.data.Execution;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeScenario;

public class CGConsole extends JFrame {
	
	public CGConsole() {
		final JTextArea textArea = new JTextArea();
		JButton button = new JButton("Atualizar Relatório");
		
		JScrollPane areaScrollPane = new JScrollPane(
				textArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setSize(new Dimension(500, 500));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		add(areaScrollPane);
		add(button);
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				
				StringBuilder sb = new StringBuilder();
				
				for (RuntimeScenario cg : Execution.getInstance().getAllRuntimeCallGraph()) {
					try {
						DataUtil.printScenarioTree(cg, sb);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				
				textArea.setText(sb.toString());
			}
		});
	}
	
}
