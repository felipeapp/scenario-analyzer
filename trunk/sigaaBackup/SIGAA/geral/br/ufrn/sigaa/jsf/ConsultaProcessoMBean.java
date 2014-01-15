/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '25/09/2008'
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dao.ProcessoDAO;
import br.ufrn.comum.dao.ProcessoDAOImpl;
import br.ufrn.integracao.dto.ProcessoDTO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Classe utilizada para consultar os Processos criados no SIPAC. A busca dos
 * processos é realizada para cada vínculo que o usuário possui. 
 * 
 * @author Henrique André
 *
 */

@SuppressWarnings("serial")
@Component("consultaProcesso") @Scope("session")
public class ConsultaProcessoMBean extends SigaaAbstractController<ProcessoDTO> {

	/**
	 * Os tipos possíveis de interessados no processo.
	 */
	
	private Set<ProcessoDTO> conjunto = new HashSet<ProcessoDTO>();
	
	/**
	 * Busca os processos e põe na view
	 * /sigaa.war/portais/discente/menu_discente.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciar() throws DAOException {
		findProcessosDoAluno();

		findProcessosDaPessoa();
		
		if (conjunto.isEmpty()) {
			addMensagemErro("Não existe nenhum processo cujo identificador é sua matricula. Matricula: " + getDiscenteUsuario().getMatricula());
			return null;
		}

		return forward("/geral/consulta_processo/lista.jsp");		
	}
	
	/**
	 * Faz a busca dos processos e remove os duplicados.
	 * 
	 * A busca é realizada para o vínculo de Discente.  
	 * 
	 * @throws DAOException
	 */
	private void findProcessosDoAluno() throws DAOException {
		ProcessoDAO dao = getDAO(ProcessoDAOImpl.class);
		
		List<ProcessoDTO> processosPessoasFisica = dao.findByInteressado(getDiscenteUsuario().getMatricula().toString());
		
		if (processosPessoasFisica != null && !processosPessoasFisica.isEmpty()) {
			for (ProcessoDTO processo : processosPessoasFisica) {
				conjunto.add(processo);
			}
		}
	}
	
	/**
	 * Faz a busca dos processos e remove os duplicados.
	 * 
	 * A busca é realizada para o vínculo de Discente.  
	 * 
	 * @throws DAOException
	 */
	private void findProcessosDaPessoa() throws DAOException {
		
		if (isEmpty(getDiscenteUsuario().getPessoa().getCpf_cnpj()))
			return;
		
		ProcessoDAO dao = getDAO(ProcessoDAOImpl.class);
		
		List<ProcessoDTO> processosPessoasFisica = dao.findByInteressado(getDiscenteUsuario().getPessoa().getCpf_cnpj().toString());
		
		if (processosPessoasFisica != null && !processosPessoasFisica.isEmpty()) {
			for (ProcessoDTO processo : processosPessoasFisica) {
				conjunto.add(processo);
			}
		}
	}	
	
	public Set<ProcessoDTO> getConjunto() {
		return conjunto;
	}

	public void setConjunto(Set<ProcessoDTO> conjunto) {
		this.conjunto = conjunto;
	}
	
}
