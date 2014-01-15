/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 21/11/2007
 */
package br.ufrn.sigaa.ead.dominio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;


/**
 * Classe para encapsular os dados de uma semana de avaliação.
 * 
 * @author David Pereira
 *
 */
public class SemanaAvaliacao extends AbstractMovimentoAdapter {

	private MetodologiaAvaliacao metodologia;
	
	private int unidade;
	
	private int semana;
	
	private boolean habilitada;
	
	private ComponenteCurricular componente;

	/**
	 * Indica se a Semana de Avaliação está habilitada ou não
	 * @return
	 */
	public String getEstadoAtual() {
		if (habilitada) {
			return "Habilitada";
		} else {
			return "Desabilitada";
		}
	}
	
	public int getSemana() {
		return semana;
	}

	public void setSemana(int semana) {
		this.semana = semana;
	}

	public boolean isHabilitada() {
		return habilitada;
	}

	public void setHabilitada(boolean habilitada) {
		this.habilitada = habilitada;
	}

	public int getUnidade() {
		return unidade;
	}

	public void setUnidade(int unidade) {
		this.unidade = unidade;
	}

	public MetodologiaAvaliacao getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(MetodologiaAvaliacao metodologia) {
		this.metodologia = metodologia;
	}

	/**
	 * Retorna uma lista de Semanas de Avaliações de acordo com a metodologia e uma lista de componentes
	 * @param metodologia
	 * @param componentes
	 * @return
	 */
	public static List<SemanaAvaliacao> fromComponentes(MetodologiaAvaliacao metodologia, List<ComponenteCurricular> componentes) {
		List<SemanaAvaliacao> semanas = new ArrayList<SemanaAvaliacao>();
		for (ComponenteCurricular cc : componentes) {
			SemanaAvaliacao sa = new SemanaAvaliacao();
			sa.setMetodologia(metodologia);
			sa.setSemana(cc.getId());
			sa.setComponente(cc);
			sa.setUnidade(1);
			sa.setHabilitada(metodologia.getAulasAtivasSet().contains(cc.getId()));
			semanas.add(sa);
		}
		return semanas;
	}

	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}
	
	/**
	 * Retorna uma descrição da semana de acordo com a semana 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getDescricao() throws DAOException {
		if (metodologia.isUmaProva()) {
			GenericDAO dao = DAOFactory.getGeneric(Sistema.SIGAA);
			
			try {
				return dao.findByPrimaryKey(semana, ComponenteCurricular.class).getCodigoNome();
			} finally {
				dao.close();
			}
		} else {
				if (semana < 10) 
					return "0" + semana;
				else
					return String.valueOf(semana);
				
		}
	
	}
	
}