package br.ufrn.sigaa.apedagogica.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.apedagogica.dominio.RegistroParticipacaoAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Controller que gerencia o CRUD dos registro das participações dos docentes anteriores
 * a inscrição on-line.
 * @author Mário Rizzi
 *
 */
@Component("registroParticipacaoAP") @Scope("request")
public class RegistroParticipacaoAPMBean 
	extends SigaaAbstractController<RegistroParticipacaoAtualizacaoPedagogica> {

	public RegistroParticipacaoAPMBean(){
		obj = new RegistroParticipacaoAtualizacaoPedagogica();
		all = null;
	}
	
	/**
	 * Define o diretório base para as operações de CRUD.
	 * Método não invocado por JSP's.
	 */
	@Override
	public String getDirBase() {
		return "/apedagogica/RegistroParticipacaoAP";
	}
	
	/**
	 * Define os usuário que poderão realizar as ações de CRUD.
	 * Método não invocado por JSP's.
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.DOCENTE);
	}
		
	/**
	 * Seta o servidor no registro.
	 * Método não invocado por JSP's.
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {
		obj.setServidor( getServidorUsuario() );
	}

	@Override
	public void afterRemover() {
		all = null;
	}
	
	/**
	 * Exibe a listagem de todos os registros do docente.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() throws ArqException {
		return forward(getListPage());
	}
	
	@Override
	public Collection<RegistroParticipacaoAtualizacaoPedagogica> getAll()
			throws ArqException {
		if(all == null)
			all = getGenericDAO().findByExactField(RegistroParticipacaoAtualizacaoPedagogica.class, "servidor.id", getServidorUsuario().getId());
		
		
		return all;
	}
		
	/**
	 * Exibe a listagem de todos os registros do docente.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/ensino/pap/lista.jsp</li>
	 * </ul>
	 */
	public String view() throws DAOException {
		populateObj(true);
		setReadOnly(true);
		return forward(getViewPage());
	}
	
}
