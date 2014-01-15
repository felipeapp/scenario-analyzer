/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 01/03/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.parametros.dominio.MensagensStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;

/**
 * Managed Bean responsável pela cadastro de observações de discentes
 *
 * @author Ricardo Wendell
 *
 */
public class ObservacaoDiscenteMBean extends SigaaAbstractController<ObservacaoDiscente>
	implements OperadorDiscente{

	/** Campo responsável por armazenar o valor do identificador da observação anterior do discente.*/
	private int idObservacaoAnterior;

	public ObservacaoDiscenteMBean() {
		this.obj = new ObservacaoDiscente();
		this.obj.setDiscente(new Discente());
		this.obj.setObservacaoAnterior(new ObservacaoDiscente());
	}

	/**
	 * Iniciar cadastro de observação de discente
	 * <br>
	 * Método chamado pela(s) seguintes JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/menus/aluno.jsp</li>
	 *  <li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 *  <li>/sigaa.war/stricto/menus/discente.jsp</li>
	 *  <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 *  <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		
		checkRole(new int[] {SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.PPG, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, 
				SigaaPapeis.GESTOR_NEE, SigaaPapeis.COORDENADOR_MEDIO, SigaaPapeis.SECRETARIA_MEDIO, SigaaPapeis.GESTOR_MEDIO});
		
		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE);
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}
		return buscarDiscente();
	}

	/**
	 * Método responsável pelo cadastro de observações de discente.
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#cadastrar()
	 * <br>
	 * Método chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/discente/observacoes.jsp</li></ul>
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {

		// Buscar dados do discente
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			obj.setDiscente(dao.findByPK(obj.getDiscente().getId()));
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro( e.getMessage() );
			return null;
		} finally {
			dao.close();
		}

		if (getObj().getObservacao() == null || "".equals(getObj().getObservacao().trim())) {
			addMensagemErro("A observação não pode ser vazia");
			return null;
		}
		
		try {
			obj.setCodMovimento(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE);
			execute(obj, getCurrentRequest() );
			addMessage("Observação cadastrada com sucesso!",
					TipoMensagemUFRN.INFORMATION);
			prepareMovimento(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE);
			getObj().setObservacao("");
			getObj().setObservacaoAnterior(new ObservacaoDiscente());
			obj.setDiscente( getDAO(DiscenteDao.class).findByPK(obj.getDiscente().getId()) );
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			return null;
		}
		
		return null;
	}

	/**
	 * Método responsável pela operação de remoção das observações inseridas ao discente.
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#remover()
	 *  <br>
	 * Método chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/discente/observacoes.jsp</li></ul>
	 */
	@Override
	public String remover() throws ArqException {
		try {
			int id = getParameterInt("idObservacao");
			ObservacaoDiscente observacao = getGenericDAO().findByPrimaryKey(id, ObservacaoDiscente.class);

			prepareMovimento(SigaaListaComando.REMOVER_OBSERVACAO_DISCENTE);
			observacao.setCodMovimento(SigaaListaComando.REMOVER_OBSERVACAO_DISCENTE);
			execute(observacao, getCurrentRequest() );
			obj.setDiscente(getDAO(DiscenteDao.class).findByPK(obj.getDiscente().getId()));
			addMessage("Observação removida com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
			return null;
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}

		return null;
	}
    
	/**
	* Método responsável pela operação de edição das observações inseridas ao discente.
    *  <br>
	* Método chamado pela JSP:
	* <ul><li>/sigaa.war/graduacao/discente/observacoes.jsp</li></ul>
    * @return
    * @throws ArqException
    */
	public String alterar() throws ArqException {
		int id = getParameterInt("idObservacao");
		GenericDAO dao = getGenericDAO();
		ObservacaoDiscente anterior = dao.findByPrimaryKey(id, ObservacaoDiscente.class);

		obj.setObservacaoAnterior(anterior);
		obj.setDiscente( getDAO(DiscenteDao.class).findByPK(obj.getDiscente().getId()) );
		obj.setObservacao(anterior.getObservacao());

		prepareMovimento(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE);
		getCurrentRequest().setAttribute("alteracao", true);
		return null;
	}

	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação
	 * <br>
	 * Método chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/busca_discente.jsp</li></ul>
	 * @return
	 */
	public String buscarDiscente() throws SegurancaException{
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.OBSERVACAO_DISCENTE);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Método chamado durante a seleção do discente na operação geral de busca de aluno.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 * <br>
	 * Método chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/busca_discente.jsp</li></ul>
	 */
	public String selecionaDiscente() {
		return forward("/graduacao/discente/observacoes.jsp");
	}

	/**
	 * Buscar as observações já cadastradas para o discente selecionado
	 *
	 * @return
	 */
	public Collection<ObservacaoDiscente> getObservacoesDiscente() {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			return dao.findObservacoesDiscente( obj.getDiscente());
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Um erro ocorreu durante a busca das observações do discente. Por favor, contacte o suporte através do \"Abrir Chamado\"");
			return null;
		}
	}
	
	/**
     * Redireciona para a tela com o resultado de discente da busca de discentes.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/discente/observacoes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltar() {
		return forward("/graduacao/busca_discente.jsp");
	}

	/**
	 * Seta o discente selecionado
	 */
	public void setDiscente(DiscenteAdapter discente) {
		obj.setDiscente(discente);
	}

	public int getIdObservacaoAnterior() {
		return this.idObservacaoAnterior;
	}

	public void setIdObservacaoAnterior(int idObservacaoAnterior) {
		this.idObservacaoAnterior = idObservacaoAnterior;
	}

}
