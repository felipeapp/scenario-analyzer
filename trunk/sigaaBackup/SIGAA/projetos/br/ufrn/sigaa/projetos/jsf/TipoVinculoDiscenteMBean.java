/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '10/01/2011'
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/**
 * MBean responsável por gerenciar o acesso aos tipos de vínculos dos discentes.
 * 
 * @author Ilueny Santos
 * 
 */
@Component("tipoVinculoDiscenteBean")
@Scope("request")
public class TipoVinculoDiscenteMBean extends SigaaAbstractController<TipoVinculoDiscente> {
	
	
	/** 
	 * Construtor Padrão 
	 */
	public TipoVinculoDiscenteMBean() {
		obj = new TipoVinculoDiscente();
	}
	
	/**
	 * Utilizado para disponibilizar todos os tipos de Vínculos de Discentes.
	 * @return
	 */
	public Collection<SelectItem> getAllAtivosCombo() {
		return getAllAtivo(TipoVinculoDiscente.class, "id", "descricao");
	}
	
	/**
	 * Retorna todos os vínculos ativos para o tipo de projeto informado.
	 */	
	private List<TipoVinculoDiscente> getAllAtivosByTipoProjeto(int idTipoProjeto) throws DAOException{
		return (List<TipoVinculoDiscente>) getGenericDAO().findByExactField(TipoVinculoDiscente.class, 
				new String[] {"ativo", "tipoProjeto.id"}, new Object[]{Boolean.TRUE, idTipoProjeto});
	}

	/**
	 * Retorna vínculos ativos remunerados ou não para o tipo de projeto informado.
	 */	
	private List<TipoVinculoDiscente> getAllAtivosByTipoProjeto(int idTipoProjeto, boolean remunerado) throws DAOException{
		return (List<TipoVinculoDiscente>) getGenericDAO().findByExactField(TipoVinculoDiscente.class, 
				new String[] {"ativo", "tipoProjeto.id", "remunerado"}, new Object[]{Boolean.TRUE, idTipoProjeto, remunerado});
	}

	
	/**
	 * Retorna todos os vínculos ativos para projetos de monitoria.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllAtivosEnsinoCombo() throws DAOException {		
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.ENSINO), "id", "descricao");
	}

	/**
	 * Retorna vínculos remunerados e ativos para projetos de monitoria.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getRemuneradosAtivosEnsinoCombo() throws DAOException {		
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.ENSINO, Boolean.TRUE), "id", "descricao");
	}

	/**
	 * Retorna vínculos Não remunerados e ativos para projetos de monitoria.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getNaoRemuneradosAtivosEnsinoCombo() throws DAOException {		
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.ENSINO, Boolean.FALSE), "id", "descricao");
	}

	
	/**
	 * Retorna todos os vínculos ativos para projetos de pesquisa.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllAtivosPesquisaCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.PESQUISA), "id", "descricao");
	}

	/**
	 * Retorna vínculos remunerados e ativos para projetos de pesquisa.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getRemuneradosAtivosPesquisaCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.PESQUISA, Boolean.TRUE), "id", "descricao");
	}

	/**
	 * Retorna vínculos Não remunerados e ativos para projetos de pesquisa.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getNaoRemuneradosAtivosPesquisaCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.PESQUISA, Boolean.FALSE), "id", "descricao");
	}

	
	/**
	 * Retorna todos os vínculos ativos para ações de extensão.
	 * 
     * Método chamado pela(s) seguintes JSPs:
     * <ul>
     * 	<li> /sigaa.war/extensao/PlanoTrabalho/form.jsp </li>
     * 	<li> /sigaa.war/extensao/PlanoTrabalho/indicar_discente.jsp </li>
     * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllAtivosExtensaoCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.EXTENSAO), "id", "descricao");
	}

	/**
	 * Retorna vínculos remunerados e ativos para ações de extensão.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getRemuneradosAtivosExtensaoCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.EXTENSAO,Boolean.TRUE), "id", "descricao");
	}

	/**
	 * Retorna vínculos Não remunerados e ativos para ações de extensão.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getNaoRemuneradosAtivosExtensaoCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.EXTENSAO,Boolean.FALSE), "id", "descricao");
	}

	/**
	 * Retorna todos os vínculos ativos para ações Acadêmicas Associadas.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllAtivosAssociadosCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.ASSOCIADO), "id", "descricao");
	}

	/**
	 * Retorna vínculos remunerados e ativos para ações Acadêmicas Associadas.
     * Método chamado pela(s) seguintes JSPs:
     * <ul>
     * 	<li> /sigaa.war/projetos/PlanoTrabalho/form.jsp </li>
     * 	<li> /sigaa.war/projetos/PlanoTrabalho/indicar_discente.jsp </li>
     * </ul>
 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getRemuneradosAtivosAssociadosCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.ASSOCIADO, Boolean.TRUE), "id", "descricao");
	}

	/**
	 * Retorna vínculos Não remunerados e ativos para ações Acadêmicas Associadas.
     * Método chamado pela(s) seguintes JSPs:
     * <ul>
     * 	<li> /sigaa.war/projetos/PlanoTrabalho/form.jsp </li>
     * 	<li> /sigaa.war/projetos/PlanoTrabalho/indicar_discente.jsp </li>
     * </ul>
     * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getNaoRemuneradosAtivosAssociadosCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.ASSOCIADO, Boolean.FALSE), "id", "descricao");
	}

}
