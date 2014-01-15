/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Managed Bean responsável pela cadastro de observações por série de discentes do ensino médio
 *
 * @author Arlindo
 */
@Component @Scope("request")
public class ObservacaoDiscenteSerieMBean extends SigaaAbstractController<ObservacaoDiscenteSerie> implements OperadorDiscente {

	/** Campo responsável por armazenar o valor do identificador da observação anterior do discente.*/
	private int idObservacaoAnterior;
	
	/** Discente Selecionado */
	private DiscenteMedio discente;
	
	/** Matrículas do discente selecionado */
	private List<MatriculaDiscenteSerie> matriculasSerie = new ArrayList<MatriculaDiscenteSerie>();

	/**
	 * Construtor padrão 
	 */
	public ObservacaoDiscenteSerieMBean() {
		obj = new ObservacaoDiscenteSerie();
		obj.setMatricula(new MatriculaDiscenteSerie());
		discente = new DiscenteMedio();
	}	
	
	/**
	 * Iniciar cadastro de observação de discente
	 * <br>
	 * Método chamado pela(s) seguintes JSP(s):
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
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação
	 * <br>
	 * Método chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/busca_discente.jsp</li></ul>
	 * @return
	 */
	public String buscarDiscente() throws SegurancaException{
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.OBSERVACAO_DISCENTE_SERIE);
		return buscaDiscenteMBean.popular();
	}	
	
	/**
	 * Método responsável pelo cadastro de observações de discente.
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#cadastrar()
	 * <br>
	 * Método chamado pela JSP:
	 * <ul><li>/sigaa.war/medio/observacao/observacoes.jsp</li></ul>
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {

		if (getObj().getObservacao() == null || "".equals(getObj().getObservacao().trim())) {
			addMensagemErro("A observação não pode ser vazia");
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
				
			addMessage("Observação "+msg+" com sucesso!", TipoMensagemUFRN.INFORMATION);
			
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
	 * Método responsável pela operação de remoção das observações inseridas ao discente.
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#remover()
	 *  <br>
	 * Método chamado pela JSP:
	 * <ul><li>/sigaa.war/medio/observacao/observacoes.jsp</li></ul>
	 */
	@Override
	public String remover() throws ArqException {
		try {
			int id = getParameterInt("idObservacao",0);
			
			ObservacaoDiscenteSerie obs = getGenericDAO().findByPrimaryKey(id, ObservacaoDiscenteSerie.class);
			
			if (ValidatorUtil.isEmpty(obs) || !obs.isAtivo()){
				addMensagemErro("Observação não selecionada ou foi removida.");
				return forward("/medio/observacao/observacoes.jsp");
			}			

			prepareMovimento(SigaaListaComando.REMOVER_OBSERVACAO_DISCENTE_SERIE);
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(getUltimoComando());
			mov.setObjMovimentado(obs);
			execute( mov );
			
			addMessage("Observação removida com sucesso!", TipoMensagemUFRN.INFORMATION);
			
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
	* Método responsável pela operação de edição das observações inseridas ao discente.
    *  <br>
	* Método chamado pela JSP:
	* <ul><li>/sigaa.war/medio/observacao/observacoes.jsp</li></ul>
    * @return
    * @throws ArqException
    */
	public String alterar() throws ArqException {
		int id = getParameterInt("idObservacao",0);
		
		ObservacaoDiscenteSerie obs = getGenericDAO().findByPrimaryKey(id, ObservacaoDiscenteSerie.class);
		
		if (ValidatorUtil.isEmpty(obs) || !obs.isAtivo()){
			addMensagemErro("Observação não selecionada ou foi removida.");
			return forward("/medio/observacao/observacoes.jsp");
		}	
		
		obj = obs;

		prepareMovimento(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE_SERIE);
		getCurrentRequest().setAttribute("alteracao", true);
		return null;
	}
	
	/**
	 * Seleciona a matrícula do discente para exibição do boletim
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/observacao/matriculas.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String selecionarMatricula() throws DAOException{
		
		int id = getParameterInt("id",0);
		if (id == 0){
			addMensagemErro("Nenhuma matrícula selecionada.");
			return null;
		}
			
		MatriculaDiscenteSerie matriculaSerie = getGenericDAO().findByPrimaryKey(id, MatriculaDiscenteSerie.class,
				"id", "situacaoMatriculaSerie.id", "situacaoMatriculaSerie.descricao", "turmaSerie.ano", "turmaSerie.nome", "turmaSerie.serie.descricao",
				"turmaSerie.id", "turmaSerie.serie.numero", "dependencia");
		
		obj.setMatricula(matriculaSerie);
		
		return forward("/medio/observacao/observacoes.jsp");
		
	}	
	
	/**
	 * Método chamado durante a seleção do discente na operação geral de busca de aluno.
	 * @throws DAOException 
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 * <br>
	 * Método chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/busca_discente.jsp</li></ul>
	 */
	public String selecionaDiscente() throws DAOException {
		return exibirMatriculas();
	}

	/**
	 * Exibe as matrículas das séries cursadas do discente selecionado
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
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
				addMensagemErro("O discente selecionado não possui nenhuma matrícula em série cadastrada.");
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
	 * Buscar as observações já cadastradas para o discente selecionado
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
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
