/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 25/07/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.ObservacaoDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.ObservacaoDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;

/**
 * Managed Bean respons�vel pela cadastro de observa��es por s�rie de discentes do ensino m�dio
 *
 * @author Arlindo
 */
@Component @Scope("request")
public class ObservacaoDiscenteSerieMBean extends SigaaAbstractController<ObservacaoDiscenteSerie> implements OperadorDiscente {

	/** Campo respons�vel por armazenar o valor do identificador da observa��o anterior do discente.*/
	private int idObservacaoAnterior;
	
	/** Discente Selecionado */
	private DiscenteMedio discente;
	
	/** Matr�culas do discente selecionado */
	private List<MatriculaDiscenteSerie> matriculasSerie = new ArrayList<MatriculaDiscenteSerie>();

	/**
	 * Construtor padr�o 
	 */
	public ObservacaoDiscenteSerieMBean() {
		obj = new ObservacaoDiscenteSerie();
		obj.setMatricula(new MatriculaDiscenteSerie());
		discente = new DiscenteMedio();
	}	
	
	/**
	 * Iniciar cadastro de observa��o de discente
	 * <br>
	 * M�todo chamado pela(s) seguintes JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
	
		checkRole(new int[] {SigaaPapeis.COORDENADOR_MEDIO, SigaaPapeis.SECRETARIA_MEDIO, SigaaPapeis.GESTOR_MEDIO});
		
		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE_SERIE);
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}
		return buscarDiscente();
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
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.OBSERVACAO_DISCENTE_SERIE);
		return buscaDiscenteMBean.popular();
	}	
	
	/**
	 * M�todo respons�vel pelo cadastro de observa��es de discente.
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#cadastrar()
	 * <br>
	 * M�todo chamado pela JSP:
	 * <ul><li>/sigaa.war/medio/observacao/observacoes.jsp</li></ul>
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {

		if (getObj().getObservacao() == null || "".equals(getObj().getObservacao().trim())) {
			addMensagemErro("A observa��o n�o pode ser vazia");
			return null;
		}
		
		boolean alterar = obj.getId() > 0;
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(getUltimoComando());
			mov.setObjMovimentado(obj);
			execute( mov );
			
			String msg = "cadastrada";
			if (alterar)
				msg = "alterada";
				
			addMessage("Observa��o "+msg+" com sucesso!", TipoMensagemUFRN.INFORMATION);
			
			prepareMovimento(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE_SERIE);
			
			MatriculaDiscenteSerie matricula = obj.getMatricula();
			obj = new ObservacaoDiscenteSerie();
			obj.setMatricula(matricula);
			
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
	 * <ul><li>/sigaa.war/medio/observacao/observacoes.jsp</li></ul>
	 */
	@Override
	public String remover() throws ArqException {
		try {
			int id = getParameterInt("idObservacao",0);
			
			ObservacaoDiscenteSerie obs = getGenericDAO().findByPrimaryKey(id, ObservacaoDiscenteSerie.class);
			
			if (ValidatorUtil.isEmpty(obs) || !obs.isAtivo()){
				addMensagemErro("Observa��o n�o selecionada ou foi removida.");
				return forward("/medio/observacao/observacoes.jsp");
			}			

			prepareMovimento(SigaaListaComando.REMOVER_OBSERVACAO_DISCENTE_SERIE);
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(getUltimoComando());
			mov.setObjMovimentado(obs);
			execute( mov );
			
			addMessage("Observa��o removida com sucesso!", TipoMensagemUFRN.INFORMATION);
			
			prepareMovimento(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE_SERIE);
			
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
	* <ul><li>/sigaa.war/medio/observacao/observacoes.jsp</li></ul>
    * @return
    * @throws ArqException
    */
	public String alterar() throws ArqException {
		int id = getParameterInt("idObservacao",0);
		
		ObservacaoDiscenteSerie obs = getGenericDAO().findByPrimaryKey(id, ObservacaoDiscenteSerie.class);
		
		if (ValidatorUtil.isEmpty(obs) || !obs.isAtivo()){
			addMensagemErro("Observa��o n�o selecionada ou foi removida.");
			return forward("/medio/observacao/observacoes.jsp");
		}	
		
		obj = obs;

		prepareMovimento(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE_SERIE);
		getCurrentRequest().setAttribute("alteracao", true);
		return null;
	}
	
	/**
	 * Seleciona a matr�cula do discente para exibi��o do boletim
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/observacao/matriculas.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String selecionarMatricula() throws DAOException{
		
		int id = getParameterInt("id",0);
		if (id == 0){
			addMensagemErro("Nenhuma matr�cula selecionada.");
			return null;
		}
			
		MatriculaDiscenteSerie matriculaSerie = getGenericDAO().findByPrimaryKey(id, MatriculaDiscenteSerie.class,
				"id", "situacaoMatriculaSerie.id", "situacaoMatriculaSerie.descricao", "turmaSerie.ano", "turmaSerie.nome", "turmaSerie.serie.descricao",
				"turmaSerie.id", "turmaSerie.serie.numero", "dependencia");
		
		obj.setMatricula(matriculaSerie);
		
		return forward("/medio/observacao/observacoes.jsp");
		
	}	
	
	/**
	 * M�todo chamado durante a sele��o do discente na opera��o geral de busca de aluno.
	 * @throws DAOException 
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 * <br>
	 * M�todo chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/busca_discente.jsp</li></ul>
	 */
	public String selecionaDiscente() throws DAOException {
		return exibirMatriculas();
	}

	/**
	 * Exibe as matr�culas das s�ries cursadas do discente selecionado
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/observacao/observacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String exibirMatriculas() throws DAOException {
		
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class);
		try {
			matriculasSerie = dao.findAllMatriculasByDiscente(discente, null, SituacaoMatriculaSerie.APROVADO, SituacaoMatriculaSerie.CANCELADO, 
					SituacaoMatriculaSerie.MATRICULADO, SituacaoMatriculaSerie.REPROVADO, SituacaoMatriculaSerie.TRANCADO);
			
			if (ValidatorUtil.isEmpty(matriculasSerie)){
				addMensagemErro("O discente selecionado n�o possui nenhuma matr�cula em s�rie cadastrada.");
				return null;
			}
			
			if (matriculasSerie.size() == 1){
				MatriculaDiscenteSerie matricula = matriculasSerie.get(0);
				obj.setMatricula(matricula);
				return forward("/medio/observacao/observacoes.jsp");
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward("/medio/observacao/matriculas.jsp");
	}

	/**
	 * Buscar as observa��es j� cadastradas para o discente selecionado
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/observacao/observacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<ObservacaoDiscenteSerie> getObservacoesDiscente() {
		ObservacaoDiscenteSerieDao dao = getDAO(ObservacaoDiscenteSerieDao.class);
		try {
			return dao.findByDiscenteAndSerie( discente, obj.getMatricula());
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			return null;
		}
	}	
	
	/**
	 * Seta o discente selecionado
	 */
	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = (DiscenteMedio) discente;
	}

	public int getIdObservacaoAnterior() {
		return idObservacaoAnterior;
	}

	public void setIdObservacaoAnterior(int idObservacaoAnterior) {
		this.idObservacaoAnterior = idObservacaoAnterior;
	}

	public DiscenteMedio getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteMedio discente) {
		this.discente = discente;
	}

	public List<MatriculaDiscenteSerie> getMatriculasSerie() {
		return matriculasSerie;
	}

	public void setMatriculasSerie(List<MatriculaDiscenteSerie> matriculasSerie) {
		this.matriculasSerie = matriculasSerie;
	}
}
