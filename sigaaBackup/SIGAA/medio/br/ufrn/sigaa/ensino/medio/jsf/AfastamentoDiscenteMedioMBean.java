/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 27/06/2011
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TipoMovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean respons�vel pelo cadastro de afastamento de alunos do ensino m�dio. 
 *
 * @author Arlindo
 */
@Component("afastamentoDiscenteMedioMBean") @Scope("request")
public class AfastamentoDiscenteMedioMBean  extends SigaaAbstractController<MovimentacaoAluno> implements OperadorDiscente  {
	
	/** Poss�veis tipos de afastamentos. */
	private Collection<SelectItem> tiposAfastamentos;
	
	/** Cole��o de movimentos do discente. */
	private Collection<MovimentacaoAluno> historicoMovimentacoes;	
	
	/** Opera��o a ser realizada. */
	private int operacao;	
	
	/**
	 * Status do discente que deve retornar ap�s ser realizado opera��o de estorno
	 */
	private Integer statusRetorno;
	
	/** Construtor padr�o. */
	public AfastamentoDiscenteMedioMBean() {
		initObj();
	}

	/** Inicializa os atributos do objeto do controller. */
	private void initObj() {
		obj = new MovimentacaoAluno();
		obj.setDiscente(new Discente());
		obj.setTipoMovimentacaoAluno(new TipoMovimentacaoAluno());
	}	
	
	/**
	 * Inicia o cadastro de afastamento.<br>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAfastamento() throws ArqException {
		checkRole(SigaaPapeis.SECRETARIA_MEDIO, SigaaPapeis.GESTOR_MEDIO,SigaaPapeis.COORDENADOR_MEDIO);
		prepareMovimento(SigaaListaComando.AFASTAR_DISCENTE_MEDIO);
		operacao = OperacaoDiscente.AFASTAR_DISCENTE_MEDIO;
		return buscarDiscente(operacao);
	}	
	
	/**
	 * Inicia o estorno de um registro de afastamento.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarEstorno() throws ArqException {
		checkRole(SigaaPapeis.SECRETARIA_MEDIO, SigaaPapeis.GESTOR_MEDIO,SigaaPapeis.COORDENADOR_MEDIO);
		prepareMovimento(SigaaListaComando.ESTORNAR_AFASTAMENTO_DISCENTE_MEDIO);
		operacao = OperacaoDiscente.ESTORNO_AFASTAMENTO_DISCENTE_MEDIO;
		return buscarDiscente(operacao);
	}	
	
	/**
	 * Inicia a conclus�o de programa.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */		
	public String iniciarConclusaoProgramaMedio() throws ArqException {
		checkRole(SigaaPapeis.SECRETARIA_MEDIO, SigaaPapeis.GESTOR_MEDIO,SigaaPapeis.COORDENADOR_MEDIO);
		prepareMovimento(SigaaListaComando.AFASTAR_DISCENTE_MEDIO);
		operacao = OperacaoDiscente.CONCLUIR_ALUNO_MEDIO;
		return buscarDiscente(operacao);
		
	}
	
	/**
	 * Prepara e redireciona para a p�gina de busca de discente.<br>
	 * @return
	 * @throws SegurancaException
	 */
	private String buscarDiscente(int operacao) throws SegurancaException {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(operacao);
		getCurrentSession().setAttribute("operacao", operacao);
		return buscaDiscenteMBean.popular();
	}	
	
	/** Carrega os tipos de afastamento.
	 * @param statusDiscente
	 * @throws DAOException
	 */
	private void carregarTipos() throws DAOException  {
		TipoMovimentacaoAlunoDao dao = getDAO(TipoMovimentacaoAlunoDao.class);
		try {
			tiposAfastamentos = toSelectItems(dao.findAtivosApenasNivel(NivelEnsino.MEDIO), "id", "descricao");
		} finally {
			if (dao != null)
				dao.close();
		}
	}	
	
	/**
	 * Recebe os dados do afastamento e redireciona para a tela de confirma��o.<br>
	 * M�todo chamado pela seguinte JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/afastamento/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String submeterDadosAfastamento() throws DAOException {
		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class);
		erros = validateAfastamento();
		if (hasErrors())
			return null;
		
		obj.setTipoMovimentacaoAluno( dao.refresh(obj.getTipoMovimentacaoAluno()) );
		// carregando hist�rico de movimenta��es
		carregarHistoricoMovimentacoes(dao);
		
		return telaResumo();
	}	
	
	/** Valida os atributos para uma movimenta��o do tipo afastamento.
	 * <br/><br/>
	 * M�todo n�o chamado por JSP(s)
	 * @return
	 */
	public ListaMensagens validateAfastamento() {
		ListaMensagens erros = new ListaMensagens();	

		ValidatorUtil.validateRequired(obj.getDiscente(), "Discente", erros);
		ValidatorUtil.validateRequired(obj.getTipoMovimentacaoAluno(), "Tipo", erros);
		ValidatorUtil.validateRequired(obj.getAnoReferencia(), "Ano de Refer�ncia", erros);
		ValidatorUtil.validateMinValue(obj.getAnoReferencia(), 1900, "Ano de Refer�ncia", erros);
		if (isConclusao()) 
			ValidatorUtil.validateRequired(obj.getDataColacaoGrau(), "Data de Cola��o", erros);

		return erros;
	}	
	
	/**
	 * Confirma o cadastro de afastamento
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/medio/afastamento/resumo.jsp</li>
	 * <li>sigaa.war/medio/afastamento/confirmacao_estorno.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		if( !confirmaSenha() )
			return null;

		if (getUltimoComando().equals(SigaaListaComando.AFASTAR_DISCENTE_MEDIO)){			
			erros = validateAfastamento();
			
			obj.setTipoMovimentacaoAluno(getGenericDAO().findByPrimaryKey(obj.getTipoMovimentacaoAluno().getId(), TipoMovimentacaoAluno.class));
			obj.setAnoOcorrencia(CalendarUtils.getAnoAtual());
			
		} else if (getUltimoComando().equals(SigaaListaComando.ESTORNAR_AFASTAMENTO_DISCENTE_MEDIO)){

			if( !StatusDiscente.getStatusTodos().contains( new StatusDiscente(statusRetorno) )  ){
				addMensagemErro("� necess�rio selecionar o status que o discente deve possuir ap�s o estorno!");
				return null;
			}
			
			obj.setStatusRetorno( statusRetorno );
			
		}
		
		if (hasErrors())
			return null;
		
		try {
			MovimentoMovimentacaoAluno mov = new MovimentoMovimentacaoAluno();
			mov.setCodMovimento(getUltimoComando());
			mov.setObjMovimentado(obj);
			execute(mov, getCurrentRequest());
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		return cancelar();
	}

	/**
	 * Seleciona o discente da lista de resultados da busca por discente.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * @throws DAOException
	 * @throws NumberFormatException
	 */
	@Override
	public String selecionaDiscente() throws ArqException {
		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class);
		String form = null;
		Integer operacao = (Integer) getCurrentSession().getAttribute("operacao");
		this.operacao = operacao;
		try {
			// carregando hist�rico de movimenta��es
			carregarHistoricoMovimentacoes(dao);

			switch (operacao) {
				
				case OperacaoDiscente.AFASTAR_DISCENTE_MEDIO: {

					// verifica empr�stimos na biblioteca sem devolu��o
					erros.addAll(VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(obj.getDiscente().getDiscente()));
					
					/* Carrega os tipos de afastamento */
					carregarTipos();
					
					//form que ser� redirecionado
					form = getFormPage();
					
					break;
				}
			
				case OperacaoDiscente.ESTORNO_AFASTAMENTO_DISCENTE_MEDIO: {
					
					statusRetorno = DiscenteHelper.getUltimoStatus(obj.getDiscente().getDiscente());
					// Guardando a referencia do discente.
					DiscenteAdapter discente = obj.getDiscente();
					obj = dao.findUltimoAfastamentoByDiscente(obj.getDiscente().getId(), true, true);
					if (obj == null) {
						addMensagemErro("Esse discente n�o possui afastamento para estornar.");
						return null;
					}
					obj.setDiscente(discente);			

					//form que ser� redirecionado
					form = getFormEstorno();					
					
					break;
				}
				
				case OperacaoDiscente.CONCLUIR_ALUNO_MEDIO: {
					
					// verifica empr�stimos na biblioteca sem devolu��o
					erros.addAll(VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(obj.getDiscente().getDiscente()));
					//Indica que o movimento que ser� feito � de conclus�o
					obj.setTipoMovimentacaoAluno( new TipoMovimentacaoAluno(TipoMovimentacaoAluno.CONCLUSAO));
															
					//form que ser� redirecionado
					form = getFormPage();
				}
			}
			
			if (hasErrors()) 
				return null;						
			
			return forward(form);
			
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
		
	}
	
	/** Carrega o hist�rico das movimenta��es.
	 * @param dao
	 * @throws DAOException
	 */
	private void carregarHistoricoMovimentacoes(MovimentacaoAlunoDao dao ) throws DAOException {
		try {
			historicoMovimentacoes = dao.findByDiscente(obj.getDiscente().getId(),true);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("N�o foi poss�vel carregar o hist�rico de movimenta��es do aluno");
		}
	}	

	/** 
	 * Seta o discente escolhido da lista de resultados da busca.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 */
	public void setDiscente(DiscenteAdapter discente) {
		if (obj == null) initObj();
		obj.setDiscente(discente);
	}
	
	/**
	 * Redireciona para a tela dos dados do afastamento
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/afastamento/view.jsp.</li>
	 * </ul>
	 * @return
	 */
	public String telaDadosAfastamento(){
		return forward(getFormPage());
	}
	
	/**
	 * Redireciona para a tela de resumo
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */	
	public String telaResumo(){
		return forward(getViewPage());
	}
	
	/**
	 * Caminho do formul�rio de afastamento
	 */
	@Override
	public String getFormPage() {
		return "/medio/afastamento/form.jsp";
	}
	
	/**
	 * Caminho do formul�rio de estorno
	 * @return
	 */
	public String getFormEstorno(){
		return "/medio/afastamento/estorno.jsp";
	}
	
	/**
	 * Direciona o usu�rio para a tela de busca de discentes.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/afastamento/form.jsp.</li>
	 * </ul>
	 */
	public String telaBuscaDiscentes() {
		return forward("/graduacao/busca_discente.jsp");
	}
	
	/**
	 * Caminho da tela de confirma��o dos dados de afastamento
	 */
	@Override
	public String getViewPage() {
		return "/medio/afastamento/view.jsp";
	}
	
	/** Retorna os poss�veis tipos de afastamentos. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/afastamento/form.jsp.</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getTiposAfastamentos() throws DAOException {
		if (tiposAfastamentos == null) 
			carregarTipos();
		return tiposAfastamentos;
	}

	public Collection<MovimentacaoAluno> getHistoricoMovimentacoes() {
		return historicoMovimentacoes;
	}

	public void setHistoricoMovimentacoes(
			Collection<MovimentacaoAluno> historicoMovimentacoes) {
		this.historicoMovimentacoes = historicoMovimentacoes;
	}

	public Integer getStatusRetorno() {
		return statusRetorno;
	}

	public void setStatusRetorno(Integer statusRetorno) {
		this.statusRetorno = statusRetorno;
	}
	
	public boolean isConclusao() {
		return operacao == OperacaoDiscente.CONCLUIR_ALUNO_MEDIO; 
	}
	
}
