/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 03/01/2008
 */
package br.ufrn.sigaa.ensino.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Managed bean para a realiza��o do trancamento de matr�culas
 * de um discente pelo seu tutor de ensino a dist�ncia.
 *  
 * @author David Pereira
 *
 */
@Component("trancamentoMatriculaTutor")
@Scope("request")
public class TrancamentoMatriculaTutorMBean extends AbstractController {

	private DiscenteAdapter discente;
	
	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de gradua��o
	 */
	public String buscarDiscente() throws SegurancaException{
		return forward("/ensino/trancamento_matricula/trancamento_tutor.jsp");
	}
	
	/**
	 * Seleciona um discente e prepara o managed bean de trancamento
	 * para trancar esse discente.
	 * @throws NegocioException 
	 */
	public String selecionaDiscente() throws ArqException, NegocioException {
		int id = getParameterInt("id");
		discente = getDAO(DiscenteDao.class).findByPK(id);		
		TrancamentoMatriculaMBean trancamento = (TrancamentoMatriculaMBean) getMBean("trancamentoMatricula");
		trancamento.setDiscente(discente);
		trancamento.setTutorEad(true);
		return trancamento.popularSolicitacaoDiscenteInjetado();
	}

}
