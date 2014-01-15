/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * MBean respons�vel por gerenciar o acesso aos tipos de v�nculos dos discentes.
 * 
 * @author Ilueny Santos
 * 
 */
@Component("tipoVinculoDiscenteBean")
@Scope("request")
public class TipoVinculoDiscenteMBean extends SigaaAbstractController<TipoVinculoDiscente> {
	
	
	/** 
	 * Construtor Padr�o 
	 */
	public TipoVinculoDiscenteMBean() {
		obj = new TipoVinculoDiscente();
	}
	
	/**
	 * Utilizado para disponibilizar todos os tipos de V�nculos de Discentes.
	 * @return
	 */
	public Collection<SelectItem> getAllAtivosCombo() {
		return getAllAtivo(TipoVinculoDiscente.class, "id", "descricao");
	}
	
	/**
	 * Retorna todos os v�nculos ativos para o tipo de projeto informado.
	 */	
	private List<TipoVinculoDiscente> getAllAtivosByTipoProjeto(int idTipoProjeto) throws DAOException{
		return (List<TipoVinculoDiscente>) getGenericDAO().findByExactField(TipoVinculoDiscente.class, 
				new String[] {"ativo", "tipoProjeto.id"}, new Object[]{Boolean.TRUE, idTipoProjeto});
	}

	/**
	 * Retorna v�nculos ativos remunerados ou n�o para o tipo de projeto informado.
	 */	
	private List<TipoVinculoDiscente> getAllAtivosByTipoProjeto(int idTipoProjeto, boolean remunerado) throws DAOException{
		return (List<TipoVinculoDiscente>) getGenericDAO().findByExactField(TipoVinculoDiscente.class, 
				new String[] {"ativo", "tipoProjeto.id", "remunerado"}, new Object[]{Boolean.TRUE, idTipoProjeto, remunerado});
	}

	
	/**
	 * Retorna todos os v�nculos ativos para projetos de monitoria.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllAtivosEnsinoCombo() throws DAOException {		
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.ENSINO), "id", "descricao");
	}

	/**
	 * Retorna v�nculos remunerados e ativos para projetos de monitoria.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getRemuneradosAtivosEnsinoCombo() throws DAOException {		
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.ENSINO, Boolean.TRUE), "id", "descricao");
	}

	/**
	 * Retorna v�nculos N�o remunerados e ativos para projetos de monitoria.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getNaoRemuneradosAtivosEnsinoCombo() throws DAOException {		
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.ENSINO, Boolean.FALSE), "id", "descricao");
	}

	
	/**
	 * Retorna todos os v�nculos ativos para projetos de pesquisa.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllAtivosPesquisaCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.PESQUISA), "id", "descricao");
	}

	/**
	 * Retorna v�nculos remunerados e ativos para projetos de pesquisa.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getRemuneradosAtivosPesquisaCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.PESQUISA, Boolean.TRUE), "id", "descricao");
	}

	/**
	 * Retorna v�nculos N�o remunerados e ativos para projetos de pesquisa.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getNaoRemuneradosAtivosPesquisaCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.PESQUISA, Boolean.FALSE), "id", "descricao");
	}

	
	/**
	 * Retorna todos os v�nculos ativos para a��es de extens�o.
	 * 
     * M�todo chamado pela(s) seguintes JSPs:
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
	 * Retorna v�nculos remunerados e ativos para a��es de extens�o.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getRemuneradosAtivosExtensaoCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.EXTENSAO,Boolean.TRUE), "id", "descricao");
	}

	/**
	 * Retorna v�nculos N�o remunerados e ativos para a��es de extens�o.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getNaoRemuneradosAtivosExtensaoCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.EXTENSAO,Boolean.FALSE), "id", "descricao");
	}

	/**
	 * Retorna todos os v�nculos ativos para a��es Acad�micas Associadas.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllAtivosAssociadosCombo() throws DAOException {
		return toSelectItems(getAllAtivosByTipoProjeto(TipoProjeto.ASSOCIADO), "id", "descricao");
	}

	/**
	 * Retorna v�nculos remunerados e ativos para a��es Acad�micas Associadas.
     * M�todo chamado pela(s) seguintes JSPs:
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
	 * Retorna v�nculos N�o remunerados e ativos para a��es Acad�micas Associadas.
     * M�todo chamado pela(s) seguintes JSPs:
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
