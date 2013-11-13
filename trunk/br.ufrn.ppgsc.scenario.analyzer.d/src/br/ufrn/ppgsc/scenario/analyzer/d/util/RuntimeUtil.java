package br.ufrn.ppgsc.scenario.analyzer.d.util;

import javax.swing.SwingUtilities;

import br.ufrn.ppgsc.scenario.analyzer.d.data.Execution;
import br.ufrn.ppgsc.scenario.analyzer.d.gui.CGConsole;

public class RuntimeUtil {

	/*
	 * TODO: Ver como retirar este código daqui depois posso criar um arquivo
	 * jsp para visualizar as informações do grafo
	 */
	static {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CGConsole().setVisible(true);
			}
		});
	}

	private static final Execution execution = new Execution();

	public static Execution getCurrentExecution() {
		return execution;
	}

}
