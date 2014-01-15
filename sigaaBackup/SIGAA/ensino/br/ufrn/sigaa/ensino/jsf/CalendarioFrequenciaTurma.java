/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 19/07/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.util.Formatador;

/**
 * @author David Pereira
 *
 */
public class CalendarioFrequenciaTurma {

	private List<Date> datasAulas;
	
	private Map<String, DiaFrequencia[][]> calendario;
	
	public CalendarioFrequenciaTurma(List<Date> datasAulas) {
		this.datasAulas = datasAulas;
		montaCalendario();
		
	}
	
	class DiaFrequencia {
		int numero;
		boolean aula;
		
		/**
		 * @return the numero
		 */
		public int getNumero() {
			return numero;
		}
		/**
		 * @param numero the numero to set
		 */
		public void setNumero(int numero) {
			this.numero = numero;
		}
		/**
		 * @return the aula
		 */
		public boolean isAula() {
			return aula;
		}
		/**
		 * @param aula the aula to set
		 */
		public void setAula(boolean aula) {
			this.aula = aula;
		}
	}
	

	private void montaCalendario() {
		calendario = new HashMap<String, DiaFrequencia[][]>();
		
		List<String> meses = getMeses();
		for (String mes : meses) {
			DiaFrequencia[][] dias = new DiaFrequencia[5][7];
			for (int i = 0; i < dias.length; i++)
				for (int j = 0; j < dias[i].length; j++)
					dias[i][j] = new DiaFrequencia();
			calendario.put(mes, dias);
		}
	}
	
	public String getMes(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		int mes = c.get(Calendar.MONTH);
		return Formatador.getInstance().formatarMes(mes);
	}
	
	private List<String> getMeses() {
		List<String> meses = new ArrayList<String>();
		for (Date data : datasAulas) {
			String mes = getMes(data);
			if (!meses.contains(mes))
				meses.add(mes);
		}
		return meses;
	}
	
}
