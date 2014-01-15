/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 01/04/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import  br.ufrn.sigaa.ensino.graduacao.dao.RelatorioDiscenteMatriculaSqlDao;

/**
 * Controller respons�vel pela gera��o de diversos relat�rios referentes a discentes e matr�culas.
 * @author Rafael Gomes
 *
 */
@Component("relatorioDiscenteMatricula") @Scope("request")
public class RelatorioDiscenteMatriculaMBean extends AbstractRelatorioGraduacaoMBean {

	// 	Dados do relat�rio.
	
	/** Lista utilizada para exibir o resultado do relat�rio. */
	private List<Map<String,Object>> listaDiscente = new ArrayList<Map<String,Object>>();
	
	/** Construtor padr�o. */
	public RelatorioDiscenteMatriculaMBean(){
		initObj();
		setAmbito("discente/");
	}
	
	/**
	 * M�todo respons�vel pela gera��o de um relat�rio com todos os discentes que n�o possuem registro de solicita��o de 
	 * matr�cula ou matr�culas em espera para o per�odo.
	 * 
	 * <br>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>sigaa.war/graduacao/relatorios/discente/seleciona_total_alunos_regulares_curso.jsp</li>
	 *  </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String alunosIngressantesSemMatricula() throws DAOException {
		RelatorioDiscenteMatriculaSqlDao dao = getDAO(RelatorioDiscenteMatriculaSqlDao.class);
		
		try {
			listaDiscente = dao.findAlunosIngressantesSemMatricula(ano, periodo);
		} finally {
			dao.close();
		}
		
		if (listaDiscente.size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(CONTEXTO + "discente/quantitativo_aluno_sem_matricula.jsp");
	}
	
	/**
	 * the listaDiscente
	 */
	public List<Map<String, Object>> getListaDiscente() {
		return listaDiscente;
	}

	/**
	 * listaDiscente the listaDiscente to set
	 */
	public void setListaDiscente(List<Map<String, Object>> listaDiscente) {
		this.listaDiscente = listaDiscente;
	}
	
}
