/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Managed Bean respons�vel pela cadastro de observa��es de discentes
 *
 * @author Ricardo Wendell
 *
 */
public class ObservacaoDiscenteMBean extends SigaaAbstractController<ObservacaoDiscente>
	implements OperadorDiscente{

	/** Campo respons�vel por armazenar o valor do identificador da observa��o anterior do discente.*/
	private int idObservacaoAnterior;

	public ObservacaoDiscenteMBean() {
		this.obj = new ObservacaoDiscente();
		this.obj.setDiscente(new Discente());
		this.obj.setObservacaoAnterior(new ObservacaoDiscente());
	}

	/**
	 * Iniciar cadastro de observa��o de discente
	 * <br>
	 * M�todo chamado pela(s) seguintes JSP(s):
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
	 * M�todo respons�vel pelo cadastro de observa��es de discente.
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#cadastrar()
	 * <br>
	 * M�todo chamado pela JSP:
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
			addMensagemErro("A observa��o n�o pode ser vazia");
			return null;
		}
		
		try {
			obj.setCodMovimento(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE);
			execute(obj, getCurrentRequest() );
			addMessage("Observa��o cadastrada com sucesso!",
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
	 * M�todo respons�vel pela opera��o de remo��o das observa��es inseridas ao discente.
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#remover()
	 *  <br>
	 * M�todo chamado pela JSP:
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
			addMessage("Observa��o removida com sucesso!", TipoMensagemUFRN.INFORMATION);
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
	* M�todo respons�vel pela opera��o de edi��o das observa��es inseridas ao discente.
    *  <br>
	* M�todo chamado pela JSP:
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
	 * Redirecionar para o Managed Bean para a busca de discentes de gradua��o
	 * <br>
	 * M�todo chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/busca_discente.jsp</li></ul>
	 * @return
	 */
	public String buscarDiscente() throws SegurancaException{
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.OBSERVACAO_DISCENTE);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * M�todo chamado durante a sele��o do discente na opera��o geral de busca de aluno.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 * <br>
	 * M�todo chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/busca_discente.jsp</li></ul>
	 */
	public String selecionaDiscente() {
		return forward("/graduacao/discente/observacoes.jsp");
	}

	/**
	 * Buscar as observa��es j� cadastradas para o discente selecionado
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
			addMensagemErro("Um erro ocorreu durante a busca das observa��es do discente. Por favor, contacte o suporte atrav�s do \"Abrir Chamado\"");
			return null;
		}
	}
	
	/**
     * Redireciona para a tela com o resultado de discente da busca de discentes.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
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
