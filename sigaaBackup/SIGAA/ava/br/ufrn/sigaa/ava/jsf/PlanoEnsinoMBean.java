/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 22/01/2008
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.PlanoEnsino;


/**
 * Managed Bean para cadastro de Planos de Ensino
 * 
 * @author David Pereira
 *
 */
@Component("planoEnsino") @Scope("session")
public class PlanoEnsinoMBean extends CadastroTurmaVirtual<PlanoEnsino> {

	/**
	 * Exibe o formulário para se editar um plano de ensino.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/PlanoEnsino.listar.jsp
	 */
	@Override
	public String editar() {
		object = getDAO(TurmaVirtualDao.class).buscarPlanoEnsino(turma());
		if (object == null) {
			object = new PlanoEnsino();
			object.setTurma(turma());
		}
		return forward("/ava/PlanoEnsino/editar.jsp");
	}
	
	/**
	 * Salva um plano de ensino.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/planoEnsino/editar.jsp
	 * @return
	 * @throws ArqException
	 */
	public String salvar() throws ArqException {
		if (object.getId() == 0) {
			return cadastrar();
		} else {
			return atualizar();
		}
	}
	
	/**
	 * Exibe a lista com todos os planos de ensino.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/PlanoEnsino.listar.jsp
	 */
	@Override
	public List<PlanoEnsino> lista() {
		return null;
	}	
	
}
