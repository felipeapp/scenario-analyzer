/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/04/2009
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateCPF_CNPJ;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.ensino.InscricaoSelecaoDao;
import br.ufrn.sigaa.arq.dao.ensino.PessoaInscricaoDao;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.ParametrosProgramaPosDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.AgendaProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.PessoaInscricao;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.StatusInscricaoSelecao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.DiscenteGraduacaoMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.EstruturaCurricularMBean;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.latosensu.jsf.DiscenteLatoMBean;
import br.ufrn.sigaa.ensino.negocio.InscricaoSelecaoValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoInscricaoSelecao;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.dominio.LinhaPesquisaStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.ParametrosProgramaPos;
import br.ufrn.sigaa.ensino.stricto.jsf.DiscenteStrictoMBean;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.jsf.DiscenteTecnicoMBean;
import br.ufrn.sigaa.jsf.AgendaProcessoSeletivoMBean;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.ProcessoSeletivoMBean;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosLatoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.dominio.Resposta;
import br.ufrn.sigaa.questionario.jsf.QuestionarioRespostasMBean;

/**
 * ManagedBean respons�vel pelo cadastro e consultas a inscri��es em processos
 * seletivos.
 * 
 * @author Ricardo Wendell
 * 
 */
@Component("inscricaoSelecao") @Scope("session") 
public class InscricaoSelecaoMBean extends SigaaAbstractController<InscricaoSelecao> {
		
	/** Cole��o de poss�veis datas de agendamento. Utilizado somente para processos de Transfer�ncia Volunt�ria */
	private Collection<SelectItem> datasAgendamento = new ArrayList<SelectItem>(0);

	/** Lista de inscritos no processo seletivo. Utilizado somente para processos de Transfer�ncia Volunt�ria */
	private Collection<InscricaoSelecao> inscritos;
	
	/** Define se a opera��o � de atualiza��o de status. Utilizado somente para processos de Transfer�ncia Volunt�ria */
	private boolean atualizacaoStatus;
	
	/** Define se a opera��o � de atualiza��o da inscri��o. */ 
	private boolean atualizacaoInscricao;
	
	/** Arquivo do projeto enviado pelo usu�rio. */
	private UploadedFile arquivoProjeto;

	/** Par�metros do programa de p�s. */
	private ParametrosProgramaPos parametrosPos;
	
	/** Cole��o de selectItem de status. */
	private Collection<SelectItem> comboStatus;

	/** Construtor padr�o. */
	public InscricaoSelecaoMBean() {
		initObj();
	}

	/**
	 * Inicializa o objeto da inscri��o
	 * 
	 * @param processo
	 */
	private void initObj(ProcessoSeletivo processo) {
		obj = new InscricaoSelecao();
		obj.setPessoaInscricao(new PessoaInscricao());
		obj.setLinhaPesquisa(new LinhaPesquisaStricto());
		obj.setOrientador(new EquipePrograma());
		obj.setAgenda(new AgendaProcessoSeletivo());
		inscritos =  new ArrayList<InscricaoSelecao>();
		if (processo != null) {
			obj.setProcessoSeletivo(processo);
		} else {
			obj.setProcessoSeletivo(new ProcessoSeletivo());
		}
		atualizacaoStatus = false;
		atualizacaoInscricao = false;
	}

	/** Inicializa o objeto da inscri��o.*/
	private void initObj() {
		initObj(null);
	}

	/**
	 * Inicia inscri��o em um processo seletivo
	 *<br/> 
	 *<ul>
	 *	<li>\SIGAA\app\sigaa.ear\sigaa.war\administracao\cadastro\ProcessoSeletivo\resumo.jsp</li>
	 *	<li>\SIGAA\app\sigaa.ear\sigaa.war\public\processo_seletivo\view_vestibular.jsp</li>
	 *</ul> 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String iniciarInscricao() throws ArqException, NegocioException {

		Integer id = getParameterInt("id");
		
		if( isEmpty(id) ){
			addMensagem( MensagensArquitetura.OBJETO_NAO_FOI_SELECIONADO, "Processo Seletivo" );
			if( isEmpty( getUsuarioLogado() ) ){
				return redirectJSF("public/processo_seletivo/lista.jsp");
			}
			return  redirectJSF("administracao/cadastro/ProcessoSeletivo/lista.jsp");
		}
		
		removeOperacaoAtiva();
		prepareMovimento(SigaaListaComando.INSCREVER_PROCESSO_SELETIVO);
		setOperacaoAtiva(SigaaListaComando.INSCREVER_PROCESSO_SELETIVO.getId());
		setConfirmButton("Confirmar Inscri��o");
		ProcessoSeletivoMBean selecaoMBean = getProcessoSeletivoMBean();
		PessoaInscricaoMBean pessoaMBean = getPessoaInscricaoMBean();
		AgendaProcessoSeletivoMBean agendaMBean = getMBean("agendaProcessoSeletivo");
		try {
			
			// Popular processo Seletivo
			ProcessoSeletivoDao selecaoDao = getDAO(ProcessoSeletivoDao.class);
			ProcessoSeletivo processo = selecaoDao.findByPrimaryKey(id,
					ProcessoSeletivo.class);
			
			initObj(processo);
			
			InscricaoSelecaoValidator.validarDentroPeriodo(obj.getProcessoSeletivo(), erros);
			InscricaoSelecaoValidator.validarExisteVagas(obj.getProcessoSeletivo(), erros);


			// Popular MBeans auxiliares
			getProcessoSeletivoMBean().setObj(processo);
			getProcessoSeletivoMBean().popularDadosAuxiliares();
			pessoaMBean.iniciarInscricao();

			//Popular a cole��o de datas dispon�veis para agendamento
			if(processo.getEditalProcessoSeletivo() != null
					&& processo.isPossuiAgendamento()
					&& processo.getMatrizCurricular()!= null
					&& processo.getMatrizCurricular().getCurso().isGraduacao()){
				 
				Collection<AgendaProcessoSeletivo> agendas = 
					 agendaMBean.getAllDisponiveis(processo.getEditalProcessoSeletivo().getId());
				
				if(agendas==null ||	agendas.size()==0)
						addMensagemErro("Todas as data para agendamento foram preenchidas." +
								" Por favor entre em contato com setor respons�vel.");
			}
			
			// Popular question�rio, caso aplic�vel 
			if ( processo.getQuestionario() !=  null ) {
				getQuestionarioRespostasMBean().inicializar(obj);
			}
			
			// buscar os par�metros de p�s
			populaParametrosPos(processo);
			
			if (hasErrors())
				return null;
			
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Um erro ocorreu durante a prepara��o para a realiza��o de sua inscri��o.");
			return forward(selecaoMBean.getDirBase() + "/lista.jsp");
		}

		return forward(getDirBase() + "/form_inscricao.jsp");
	}

	/**
	 * Gera um PDF da GRU para pagamento da taxa de inscri��o.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/processo_seletivo/comprovante.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String imprimirGRU() throws ArqException, NegocioException {
		int idInscricao = getParameterInt("id", 0);
		InscricaoSelecao inscricao = getGenericDAO().findByPrimaryKey(idInscricao, InscricaoSelecao.class);
		if (inscricao == null) {
			addMensagemErro("Erro na identifica��o da inscri��o.");
		} else if (inscricao.getProcessoSeletivo().getEditalProcessoSeletivo().getTaxaInscricao() == 0) {
			addMensagemErro("O Edital deste Processo Seletivo n�o define um valor de Taxa de Inscri��o.");
		} else {
			obj = inscricao;
			try {
				if (inscricao.getIdGRU() != 0) { 
					getCurrentResponse().setContentType("application/pdf");
					getCurrentResponse().addHeader("Content-Disposition", "attachment;filename=GRU.pdf");
					Date vencimento = obj.getProcessoSeletivo().getEditalProcessoSeletivo().getDataVencimentoBoleto();
					GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.getGRUByID(obj.getIdGRU());
					if (!vencimento.after(gru.getVencimento()))
						vencimento = null;
					GuiaRecolhimentoUniaoHelper.gerarPDF(getCurrentResponse().getOutputStream(), inscricao.getIdGRU(), vencimento);

					FacesContext.getCurrentInstance().responseComplete();
				} else {
					addMensagemErro("N�o foi poss�vel gerar a Guia de Recolhimento da Uni�o");
				}
			} catch (IOException e) {
				tratamentoErroPadrao(e);
			}
		}
		return null;
	}

	/**
	 * Retorna o Comprovante de Inscri��o formatado para impress�o.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/processo_seletivo/comprovante.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String imprimirComprovante(){
		return forward("/public/processo_seletivo/comprovante_impressao.jsp");
	}
	
	/**
	 * Retorna o Requerimento de Inscri��o da Transfer�ncia Volunt�ria formatado para impress�o.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/processo_seletivo/comprovante.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public String imprimirRequerimento() throws DAOException{
		
		obj.getPessoaInscricao().getIdentidade().setUnidadeFederativa(
					getGenericDAO().findByPrimaryKey(obj.getPessoaInscricao().getIdentidade().getUnidadeFederativa().getId(), UnidadeFederativa.class));
		
		if( !isEmpty( obj.getProcessoSeletivo().getMatrizCurricular() ) &&  !isEmpty( obj.getProcessoSeletivo().getMatrizCurricular().getHabilitacao() ) )
			obj.getProcessoSeletivo().getMatrizCurricular().setHabilitacao(getGenericDAO().findByPrimaryKey( obj.getPessoaInscricao().getIdentidade().getUnidadeFederativa().getId(), Habilitacao.class ) );
		
		// Popular respostas do question�rio, quando aplic�vel
		QuestionarioRespostasMBean questionarioRespostasMBean = (QuestionarioRespostasMBean) getMBean("questionarioRespostasBean");
		if ( obj.getProcessoSeletivo().getQuestionario() != null ) {
			questionarioRespostasMBean.popularVizualicacaoRespostas(obj);
		}
		
		return forward("/public/processo_seletivo/requerimento_impressao.jsp");
	}

	/**
	 * Retorna um question�rio relacionado a inscri��o de um processo seletivo.
	 * @return
	 */
	private QuestionarioRespostasMBean getQuestionarioRespostasMBean() {
		return (QuestionarioRespostasMBean) getMBean("questionarioRespostasBean");
	}
	
	/**
	 * Retorna o ProcessoSeletivoMBean em uso
	 * 
	 * @return
	 */
	private ProcessoSeletivoMBean getProcessoSeletivoMBean() {
		return (ProcessoSeletivoMBean) getMBean("processoSeletivo");
	}

	/**
	 * Retorna a inst�ncia de PessoaInscricaoMBean
	 * @return
	 */
	private PessoaInscricaoMBean getPessoaInscricaoMBean() {
		return (PessoaInscricaoMBean) getMBean("pessoaInscricao");
	}

	/**
	 * Grava inscri��o de um candidato em um processo
	 *<br/> 
	 *<ul>
	 *	<li> SIGAA\app\sigaa.ear\sigaa.war\public\processo_seletivo\form_inscricao.jsp</li>
	 *</ul> 
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String inscrever() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.INSCREVER_PROCESSO_SELETIVO.getId(),
				SigaaListaComando.ALTERAR_INSCRICAO_PROCESSO_SELETIVO.getId() )) {
			if(atualizacaoInscricao || (!isEmpty(getUsuarioLogado()) && isUserInRole(SigaaPapeis.PPG)))
				return super.cancelar();
			else
				return null;	
		}
		
		InscricaoSelecaoDao dao = getDAO(InscricaoSelecaoDao.class);
		
		// Recuperar dados do formul�rio
		obj.setPessoaInscricao(getPessoaInscricaoMBean().getObj());
		
		// Validar dados
		validacaoDados(erros.getMensagens());
		
		if (hasErrors()) 
				return null;	
		
		// Salvar IP
		obj.setIp(getCurrentRequest().getRemoteAddr());

		// trata nulos
		if (obj.getOrientador() != null && obj.getOrientador().getId() == 0)
			obj.setOrientador(null);
		if (obj.getLinhaPesquisa() != null && obj.getLinhaPesquisa().getId() == 0)
			obj.setLinhaPesquisa(null);
		
		// Persistir inscri��o
		MovimentoInscricaoSelecao mov = new MovimentoInscricaoSelecao();
		
		// determina o tipo de arrecada��o
		
		mov.setInscricaoSelecao(obj);
		if (arquivoProjeto != null) {
			mov.setArquivoProjeto(arquivoProjeto);
		} else { 
			mov.setArquivoProjeto(null);
		}
		if (obj.getQuestionario() != null) {
			getQuestionarioRespostasMBean().getObj().setQuestionario(obj.getQuestionario());
			mov.setQuestionarioRespostas( getQuestionarioRespostasMBean().getObj() );
		}
		
		if( isEmpty(obj.getId()) ){
			mov.setCodMovimento(SigaaListaComando.INSCREVER_PROCESSO_SELETIVO);
			obj.setRegistroPublico( getAcessoPublico() );
		}else
			mov.setCodMovimento(SigaaListaComando.ALTERAR_INSCRICAO_PROCESSO_SELETIVO);
		
		if(hasErrors())
			return null;
		
		try {
			execute(mov);
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			e.printStackTrace();
			return null;
		}
		
		if (atualizacaoInscricao || (!isEmpty(getUsuarioLogado()) && isUserInRole(SigaaPapeis.PPG))) {
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Inscri��o");
			return getProcessoSeletivoMBean().buscarInscritos(obj.getProcessoSeletivo());
		} else 
			return verComprovante();
		
	}
	
	/**
	 * Busca as inscri��es de um candidato em processos seletivos, a partir do
	 * cpf informado.
	 *<br/> 
	 *<ul>
	 *	<li>\SIGAA\app\sigaa.ear\sigaa.war\public\processo_seletivo\lista_transferencia_voluntaria.jsp</li>
	 *	<li>\SIGAA\app\sigaa.ear\sigaa.war\public\processo_seletivo\lista.jsp</li>
	 *</ul> 
	 * 		  
	 * @return
	 * @throws ArqException 
	 */
	public String buscarInscricoes(ActionEvent e) throws ArqException {

		InscricaoSelecaoDao inscricaoDao = getDAO(InscricaoSelecaoDao.class);

		Character nivel = (Character) e.getComponent().getAttributes().get("nivel");
		String urlProcessoSeletivo = getCurrentURL() + "?nivel=" + nivel; 
		
			// Validar CPF ou Passaporte
			ListaMensagens erros = new ListaMensagens();
			if( !getObj().getPessoaInscricao().isEstrangeiro() )
				validateCPF_CNPJ(getObj().getPessoaInscricao()
					.getCpf(), "CPF", erros);
			else if ( isEmpty(getObj().getPessoaInscricao().getPassaporte()) ) 
				validateRequired(getObj().getPessoaInscricao().getPassaporte(), "Passaporte", erros);
			
					
			if (!erros.isEmpty()) {
				addMensagens(erros);
				return redirect(urlProcessoSeletivo);
			}

			// Buscar inscri��es
			Collection<InscricaoSelecao> inscricoes = inscricaoDao
					.findByCpf( !getObj().getPessoaInscricao().isEstrangeiro() ? getObj().getPessoaInscricao().getCpf() : null,
							getObj().getPessoaInscricao().isEstrangeiro() ? getObj().getPessoaInscricao().getPassaporte() : null);

			if ( isEmpty(inscricoes) ) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return redirect(urlProcessoSeletivo);
			}
			
			obj.getPessoaInscricao().setCpf(null);
			obj.getPessoaInscricao().setPassaporte(null);
			this.resultadosBusca = inscricoes;
			
		return forward(getDirBase() + "/lista_inscricoes.jsp?nivel=" + nivel);
	}

	/**
	 * Ver comprovante da inscri��o selecionada
	 *<br/> 
	 *<ul>
	 *	<li>\SIGAA\app\sigaa.ear\sigaa.war\public\processo_seletivo\lista_inscricoes.jsp</li>
	 *</ul> 
	 * @return
	 */
	public String verComprovante() {
		
		try {
			Integer id = getParameterInt("id");
			if( !isEmpty(id) )
				setId();
			
			obj = getGenericDAO().findByPrimaryKey(obj.getId(),
					InscricaoSelecao.class);
			
			obj.getPessoaInscricao().setEstrangeiro(
						!isEmpty(obj.getPessoaInscricao().getPassaporte()) 
						&& !obj.getPessoaInscricao().getPais().isBrasil() );
	
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Um erro ocorreu durante a visualiza��o do comprovante de inscri��o selecionado.");
			return null;
		}
		getCurrentRequest().setAttribute("visualizacao", true);
		return emitirComprovante();
	}

	/**
	 * Emitir comprovante de inscri��o para um determinado candidato.<br>
	 *  M�todo n�o invocado por JSP.
	 * @return
	 */
	public String emitirComprovante() {
		return redirectJSF("public/processo_seletivo/comprovante.jsf");
	}

	/** 
	 * Retorna o diret�rio base dos formul�rios.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s): <br/> 
	 * <ul>
	 *	<li>N�o invocado por jsp.</li>
	 * </ul> 
	 */
	@Override
	public String getDirBase() {
		return "/public/processo_seletivo";
	}

	/**
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *<br/> 
	 *<ul>
	 *	<li>\SIGAA\app\sigaa.ear\sigaa.war\administracao\cadastro\ProcessoSeletivo\form_relatorio.jsp</li>
	 *	<li>\SIGAA\app\sigaa.ear\sigaa.war\administracao\cadastro\ProcessoSeletivo\lista_inscritos.jsp</li>
	 *	<li>\SIGAA\app\sigaa.ear\sigaa.war\public\processo_seletivo\form_inscricao.jsp</li>
	 *</ul> 
	 * 		  
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		finalizarControladores();
		resetBean();

		if(atualizacaoInscricao || (!isEmpty(getUsuarioLogado()) && isUserInRole(SigaaPapeis.PPG)))
			return super.cancelar();
			
		return redirect("/public");
		
	}

	/**
	 * Remove da sess�o os controladores utilizados na inscri��o.
	 */
	private void finalizarControladores() {
		resetBean("pessoaInscricao");
		resetBean("inscricaoSelecao");
		resetBean("questionarioBean");
		resetBean("questionarioRespostasBean");
	}

	/**
	 * Usado para atualizar a �rea de concentra��o, via Ajax, no formul�rio de
	 * inscri��o.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>SIGAA\app\sigaa.ear\sigaa.war\public\processo_seletivo\form_inscricao.jsp</li>
	 * </ul> 
	 * @throws DAOException
	 */
	public void atualizaAreaConcentracao() throws DAOException {
		LinhaPesquisaStricto linha = getGenericDAO().findByPrimaryKey(
				obj.getLinhaPesquisa().getId(), LinhaPesquisaStricto.class);
		if (linha != null)
			obj.setAreaConcentracao(linha.getArea());
		else
			obj.setAreaConcentracao(null);
	}
	
		
	/**
	 * Utilizado para processo de Transfer�ncia Volunt�ria.
	 * Registra status do inscrito.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>\SIGAA\app\sigaa.ear\sigaa.war\administracao\cadastro\ProcessoSeletivo\lista_inscritos.jsp</li>
	 * </ul>
	 *  
	 * @param e
	 * @throws ArqException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 * @throws SegurancaException 
	 */
	public void registrarStatus(ValueChangeEvent e) throws ArqException, NegocioException{

		populateObj(true);

		String status = String.valueOf(e.getNewValue());
		obj.setStatus(Integer.valueOf(status));
		
		if(obj.getAgenda() != null && obj.getAgenda().getId() == 0)
			obj.setAgenda(null);
		
		// Persistir novo status
		prepareMovimento(ArqListaComando.ALTERAR);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(ArqListaComando.ALTERAR);
		setAtualizacaoStatus(true);

		execute(mov);

		
	}
	
	/**
	 * Registra resultado da sele��o para a inscri��o.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li> \SIGAA\app\sigaa.ear\sigaa.war\administracao\cadastro\ProcessoSeletivo\lista_inscritos.jsp</li>
	 * </ul>
	 * @param e
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String registrarAprovacao() throws SegurancaException, ArqException, NegocioException {
		populateObj(true);
		verificaObjRemovido();
		

		if(isCadastrado())
			addMensagemErro("N�o � poss�vel alterar o status da inscri��o, pois o inscrito j� foi cadastrado como discente.");
		
		if(hasErrors())
			return null;
		
		// Definir status resultante
		if (obj.isAprovada()) {
			obj.setStatus( StatusInscricaoSelecao.SUBMETIDA );
		} else {
			obj.setStatus( StatusInscricaoSelecao.APROVADO_SELECAO );
		}
		
		// Persistir novo status
		prepareMovimento(ArqListaComando.ALTERAR);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(ArqListaComando.ALTERAR);
		execute(mov);
		addMensagemInformation("Inscri��o de " + obj.getPessoaInscricao().getNome() + " alterada com sucesso!");
		
		// Listar inscritos
		return getProcessoSeletivoMBean().buscarInscritos(obj.getProcessoSeletivo());
	}
	
	/**
	 * Verifica se o inscrito possui vinculo no mesmo n�vel de ensino do curso do processo seletivo
	 * @return
	 * @throws ArqException 
	 */
	private boolean isCadastrado() throws ArqException{
		
		if( isEmpty( obj.getProcessoSeletivo() ) && isEmpty( obj.getPessoaInscricao() ) )
			return false;
			
		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		char nivelEnsino = obj.getProcessoSeletivo().getNivelEnsino();
		
		if(  NivelEnsino.isLato(nivelEnsino) 
				&& ParametroHelper.getInstance().getParametroBoolean( 
						ParametrosLatoSensu.PERMITE_CADASTRAR_DISCENTE_COM_MATRICULA_ATIVA ) )
			return false;
		
		if( nivelEnsino != NivelEnsino.TECNICO && nivelEnsino != NivelEnsino.FORMACAO_COMPLEMENTAR ){
			return  discenteDao.possuiVinculo(nivelEnsino, obj.getPessoaInscricao().getCpf());
		} else {
			return  discenteDao.possuiVinculo(nivelEnsino, obj.getPessoaInscricao().getCpf(), getUnidadeGestora());
		}
		
	}

	/** Atualiza a inscri��o.<br>
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li> /administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {

		ProcessoSeletivoMBean selecaoMBean = getProcessoSeletivoMBean();
		PessoaInscricaoMBean pessoaMBean = getPessoaInscricaoMBean();
		
		Collection<InscricaoSelecao> inscritos = selecaoMBean.getObj().getInscritos();
		// Popular dados da inscri��o selecionada
        setId();
        verificaObjRemovido();
        setObj(getGenericDAO().findAndFetch(obj.getId(), InscricaoSelecao.class,"processoSeletivo.questionario"));
        
        
		// Popular MBeans auxiliares
		selecaoMBean.setObj(obj.getProcessoSeletivo());
		selecaoMBean.popularDadosAuxiliares();
			
		PessoaInscricaoDao pessoaInscricaoDao = getDAO(PessoaInscricaoDao.class);
		obj.setPessoaInscricao( pessoaInscricaoDao.findCompleto( obj.getPessoaInscricao().getId() ) );
		obj.getPessoaInscricao().setEstrangeiro(!isEmpty(obj.getPessoaInscricao().getPassaporte()));
		
		setOperacaoAtiva(SigaaListaComando.ALTERAR_INSCRICAO_PROCESSO_SELETIVO.getId());
		prepareMovimento(SigaaListaComando.ALTERAR_INSCRICAO_PROCESSO_SELETIVO);
		setConfirmButton("Alterar Inscri��o");
		
		atualizacaoInscricao = true;
		
		//Popular a cole��o de datas dispon�veis para agendamento
		if(obj.getProcessoSeletivo().getMatrizCurricular() != null && obj.getProcessoSeletivo().isPossuiAgendamento()){
			AgendaProcessoSeletivoMBean agendaMBean = getMBean("agendaProcessoSeletivo");
			agendaMBean.getAllDisponiveis(obj.getProcessoSeletivo().getEditalProcessoSeletivo().getId());
		}	
		
		
		obj.getPessoaInscricao().clear();
		pessoaMBean.setObj( obj.getPessoaInscricao() );
		pessoaMBean.popularMunicipios();
		
		obj.prepararDados();
		
		getQuestionarioRespostasMBean().popularVizualicacaoRespostas(obj);
		
		populaParametrosPos(obj.getProcessoSeletivo());
		
		if (ValidatorUtil.isEmpty(selecaoMBean.getObj().getInscritos())) 
			selecaoMBean.getObj().setInscritos(new ArrayList<InscricaoSelecao>());
		
		selecaoMBean.getObj().getInscritos().addAll(inscritos);
		
		return forward(getDirBase() + "/form_inscricao.jsp");
	}
	
	
	/**
	 * Caso o Processo Seletivo seja de Stricto-Sensu 
	 * carrega os  par�metros do programa
	 * @param processo
	 * @throws DAOException
	 */
	private void populaParametrosPos(ProcessoSeletivo processo) throws DAOException{
		
		if( !isEmpty(processo.getCurso()) && processo.getCurso().isStricto() ){
			ParametrosProgramaPosDao dao = getDAO(ParametrosProgramaPosDao.class);
			parametrosPos = dao.findByPrograma(processo.getCurso().getUnidade());
		}	
		
	}

	/** 
	 * Verifica se o objeto manipulado j� foi removido.
	 * @throws DAOException
	 * @throws ArqException
	 */
	private void verificaObjRemovido() throws DAOException, ArqException {
		if (isEmpty(obj) || obj.getStatus()==StatusInscricaoSelecao.CANCELADA)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
	}
	
	/**
	 * Redireciona para o formul�rio de cadastro de discentes, de acordo com o n�vel do curso.<br>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 	<li>/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public String cadastrarDiscente() throws ArqException {
		// Popular dados pessoais do candidato
		GenericDAO dao = new GenericDAOImpl(getSistema(), getSessionRequest());
		setId();
		this.obj = dao.refresh(this.obj);
		if (this.obj.getOrientador() != null) {
			EquipePrograma orientador = dao.refresh(this.obj.getOrientador()); // evita lay exception
			this.obj.setOrientador(orientador);
		}
		
		verificaObjRemovido();

		/** Verifica o status da inscri��o antes de prosseguir
			para a tela de cadastro de discente. */
		if(!obj.isAprovada())
			addMensagemErro("Para cadastrar um discente � necess�rio aprovar sua inscri��o.");
		
		if( isCadastrado() ) {
			String descricaoNivel = NivelEnsino.getDescricao( obj.getProcessoSeletivo().getNivelEnsino() );
			if( isMenuTecnico() || isFormacaoComplementar() )
				addMensagemErro("A pessoa informada j� possui um v�nculo de discente ativo no n�vel " + descricaoNivel
						+ " na unidade " + getUsuarioLogado().getVinculoAtivo().getUnidade().getNome());
			else
				addMensagem( MensagensGerais.DISCENTE_JA_POSSUI_VINCULO_ATIVO, descricaoNivel );
		}
		
		if(hasErrors())
			return null;
		
		PessoaInscricaoDao pessoaInscricaoDao = getDAO(PessoaInscricaoDao.class);
		Pessoa pessoa = pessoaInscricaoDao.findCompleto(obj.getPessoaInscricao().getId()).toPessoa();
		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		Pessoa pessoaBanco = pessoaDao.findMaisRecenteByCPF(pessoa.getCpf_cnpj());
		
		if (pessoaBanco != null) { 
				if ( (isNotEmpty(pessoaBanco.getNomeMae()) && isNotEmpty(pessoa.getNomeMae()) && isNotEmpty(pessoaBanco.getDataNascimento()) && isNotEmpty(pessoa.getDataNascimento()))
						&& pessoaBanco.getNomeMae().equals(pessoa.getNomeMae())
						&& pessoaBanco.getDataNascimento().equals(pessoa.getDataNascimento()) ) {
					addMensagemWarning("Existe o cadastro desta pessoa no sistema e os dados pessoais ser�o atualizados com o dados informados na inscri��o");
					pessoa.setId(pessoaBanco.getId());
				} else {
					addMensagemWarning("Existe um cadastro com este CPF no sistema e ser�o utilizados os dados pessoais previamente cadastrados durante o cadastramento do discente. Por favor, verifique se todos dados est�o corretos.");
					pessoa = pessoaDao.findCompleto(pessoaBanco.getId());
					pessoa.prepararDados();
				}
		}
		
		if(hasErrors())
			return null;
		
		String forward = null;

		getCurrentSession().setAttribute("discenteProcessoSeletivo", Boolean.TRUE);
		
		if (NivelEnsino.isEnsinoBasico(obj.getNivelEnsino())) {
			
			DiscenteTecnicoMBean discenteTecnicoMBean = getMBean("discenteTecnico");
			forward = discenteTecnicoMBean.iniciarCadastroDiscenteNovo();

			// Popula dados informados pelo discente espec�ficos para o ensino t�cnico
			DiscenteTecnico discente = discenteTecnicoMBean.getObj();
			discente.setNivel(obj.getNivelEnsino());
			discente.setCurso(obj.getProcessoSeletivo().getCurso());
			discente.setHasVariosCursos(false);
			
			CursoTecnico cursoTecnico = getGenericDAO().findByPrimaryKey(obj.getProcessoSeletivo().getCurso().getId(), CursoTecnico.class);
			discente.getEstruturaCurricularTecnica().setCursoTecnico(cursoTecnico);
			discente.getTurmaEntradaTecnico().setCursoTecnico(cursoTecnico);
			
			discente.setFormaIngresso(FormaIngresso.PROCESSO_SELETIVO);
			discente.setProcessoSeletivo(obj.getProcessoSeletivo());
			
			discenteTecnicoMBean.setObj(discente);
			discenteTecnicoMBean.popularCombos();
			//discenteTecnicoMBean.carregarCurriculosTurmasEntrada(null);

			
		}if (NivelEnsino.isLato(obj.getNivelEnsino())) {
			
			DiscenteLatoMBean discenteLatoMBean = getMBean("discenteLato");
			forward = discenteLatoMBean.iniciarCadastroDiscenteNovo();

			// Popular dados informados pelo discente espec�ficos para p�s-gradua��o
			DiscenteLato discente = discenteLatoMBean.getObj();
			discente.setNivel(obj.getNivelEnsino());
			discente.setCurso(obj.getProcessoSeletivo().getCurso());
			
			CursoLato cursoLato = getGenericDAO().findByPrimaryKey(obj.getProcessoSeletivo().getCurso().getId(), CursoLato.class);
			discente.getTurmaEntrada().setCursoLato(cursoLato);
			discente.setFormaIngresso(FormaIngresso.PROCESSO_SELETIVO);
			discente.setProcessoSeletivo(obj.getProcessoSeletivo());
			
			discenteLatoMBean.setObj(discente);
			discenteLatoMBean.popularCombos();
			discenteLatoMBean.carregarTurmas(null);

			
		}else if (NivelEnsino.isAlgumNivelStricto(obj.getNivelEnsino())) {
			
			DiscenteStrictoMBean discenteStrictoMBean = getMBean("discenteStricto");
			forward = discenteStrictoMBean.iniciarCadastroDiscenteProcessoSeletivo();
			discenteStrictoMBean.setBlockAnoSemestre(false);
			
			// Popular dados informados pelo discente espec�ficos para p�s-gradua��o
			DiscenteStricto discente = discenteStrictoMBean.getObj();
			discente.setNivel(obj.getNivelEnsino());
			discente.setCurso(obj.getProcessoSeletivo().getCurso());
			discente.setGestoraAcademica(obj.getProcessoSeletivo().getCurso().getUnidade());
			discente.setFormaIngresso(FormaIngresso.SELECAO_POS_GRADUACAO);
			discente.setTipo(Discente.REGULAR);
			discente.setPessoa(pessoa);
			discenteStrictoMBean.setProcessosSeletivosCombo(toSelectItems(getDAO(ProcessoSeletivoDao.class).
					findByNivel(obj.getProcessoSeletivo().getCurso().getNivel()), "id", "nomeCompleto"));
			discente.setProcessoSeletivo(obj.getProcessoSeletivo());
			discenteStrictoMBean.filtraFormaEntrada();
			
			if (obj.getOrientador() != null) {
				discenteStrictoMBean.setOrientador(obj.getOrientador());
			}
			if ( obj.getLinhaPesquisa() != null) {
				discente.setLinha(obj.getLinhaPesquisa());
				discente.setArea(obj.getLinhaPesquisa().getArea());
				discenteStrictoMBean.carregarAreasLinhas(null);
			}
			discenteStrictoMBean.setObj(discente);
			
	
		}else if (NivelEnsino.isGraduacao(obj.getNivelEnsino())) {
			
			DiscenteGraduacaoMBean discenteGraduacaoMBean = getMBean("discenteGraduacao");
			forward = discenteGraduacaoMBean.iniciarCadastroDiscenteNovo();

			// Popular dados informados pelo discente espec�ficos para gradua��o
			DiscenteGraduacao discente = discenteGraduacaoMBean.getObj();
			discente.setNivel(obj.getNivelEnsino());
			discente.setCurso(obj.getProcessoSeletivo().getMatrizCurricular().getCurso());
			discente.setMatrizCurricular(obj.getProcessoSeletivo().getMatrizCurricular());
			discente.setFormaIngresso(FormaIngresso.TRANSFERENCIA_VOLUNTARIA);
			discente.setPolo(null);
			
			//Carrega o combo contendo as matrizes curriculares pertences ao curso selecionado
			EstruturaCurricularMBean curriculoMBean = (EstruturaCurricularMBean) getMBean("curriculo");
			curriculoMBean.carregarMatrizes(discente.getCurso().getId(), true);
			
			discenteGraduacaoMBean.setObj(discente);
		}
		
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.popular();
		dadosPessoaisMBean.setObj(pessoa);
		dadosPessoaisMBean.carregarMunicipios();
		dadosPessoaisMBean.setExibirPainel(false);
		dadosPessoaisMBean.setOrdemBotoes(false);
		
		return forward;
	}

	/** Valida os dados utilizados na inscri��o.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
 	 *  M�todo n�o invocado por JSP.
	 */
	@Override
	public boolean validacaoDados(Collection<MensagemAviso> mensagens) {
		ListaMensagens erros = obj.validate();
		if(obj.getProcessoSeletivo().getMatrizCurricular() == null)
			getQuestionarioRespostasMBean().validarRepostas();
		
		if( obj.getPessoaInscricao().isEstrangeiro() && obj.getPessoaInscricao().getPais().isBrasil() )
			erros.addErro("Pa�s: O Brasil n�o pode ser selecionado para um candidato que � estrangeiro.");
		
		// Validar dados espec�ficos de programas de p�s-gradua��o
		if (obj.getProcessoSeletivo().getCurso()!= null && obj.getProcessoSeletivo().getCurso().isStricto()) {
			if (parametrosPos != null) {
				if (parametrosPos.isSolicitarAreaLinhaProcessoSeletivo()
						&& obj.getLinhaPesquisa().getId() == 0) {
					erros.addErro("Selecione uma linha de pesquisa v�lida");
				}
				if (parametrosPos.isSolicitaProjetoNaInscricao() && obj.getId() == 0) {
						if (arquivoProjeto == null) 
							erros.addErro("Arquivo PDF do projeto de trabalho de pesquisa n�o informado.");
						else 
							if (!arquivoProjeto.getContentType().equalsIgnoreCase("application/pdf")
									&& !arquivoProjeto.getContentType().equalsIgnoreCase("application/x-pdf"))
								erros.addErro("O arquivo do projeto de trabalho de pesquisa deve ser no formato PDF");
					
				}
			}
		}
		
		if(obj.getProcessoSeletivo().isPossuiAgendamento() && obj.getProcessoSeletivo().getMatrizCurricular() != null && getDatasAgendamento().size() > 0)
			ValidatorUtil.validateRequired(obj.getAgenda(), "Data de Agendamento", erros);
		mensagens.addAll(erros.getMensagens());
		return hasErrors();
	}
		
	/**
	 * M�todo utilizado para os processos de Transfer�ncia Volunt�ria.
	 * Popula um combobox listando as datas dispon�veis para agendamento ignorando s�bados e domingos
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/processo_seletivo/form_inscricao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getDatasAgendamento() {
		
		datasAgendamento = new ArrayList<SelectItem>(0);
		if(obj.getProcessoSeletivo().getEditalProcessoSeletivo() != null){
			Integer qtdDias = CalendarUtils.calculaQuantidadeDiasEntreDatasIntervaloFechado(
					obj.getProcessoSeletivo().getEditalProcessoSeletivo().getInicioPeriodoAgenda(), 
					obj.getProcessoSeletivo().getEditalProcessoSeletivo().getFimPeriodoAgenda());
			
			Date dataInicio = obj.getProcessoSeletivo().getEditalProcessoSeletivo().getInicioPeriodoAgenda();
			for (int i = 0; i <= qtdDias; i++) {
				Calendar c = Calendar.getInstance();
				c.setTime(dataInicio);
				if(c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
					datasAgendamento.add(new SelectItem(CalendarUtils.format(c,"dd/MM/yyyy"), CalendarUtils.format(c,"dd/MM/yyyy")));
				dataInicio = CalendarUtils.adicionaUmDia(dataInicio);
			}
		}

		return datasAgendamento;
	}

	/** 
	 * Retorna uma cole��o de SelectItem com todos poss�veis status da inscri��o.
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/>  
	 * <ul>
	 * 	<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsp</li>
	 * </ul>
	 * @return Cole��o de SelectItem com todos poss�veis status da inscri��o.
	 */
	public Collection<SelectItem> getComboStatus() {

		if( isEmpty(comboStatus) ) 
			comboStatus = getAllAtivo(StatusInscricaoSelecao.class, "id", "denominacao");
		
		return comboStatus;
	}

	/** Retorna a lista de inscritos no processo seletivo.
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/relatorios/processoSeletivo/relatorio_inscritos.jsp</li>
	 * </ul>
	 * @return Lista de inscritos no processo seletivo. 
	 */
	public Collection<InscricaoSelecao> getInscritos() {
		return inscritos;
	}

	/** Seta a lista de inscritos no processo seletivo.
 	 *  M�todo n�o invocado por JSP.
	 * @param inscritos Lista de inscritos no processo seletivo.  
	 */
	public void setInscritos(Collection<InscricaoSelecao> inscritos) {
		this.inscritos = inscritos;
	}

	/** Indica se a opera��o � de atualiza��o de status. 
 	 *  M�todo n�o invocado por JSP.
	 * @return
	 */
	public boolean isAtualizacaoStatus() {
		return atualizacaoStatus;
	}

	/** Define se a opera��o � de atualiza��o de status.  
 	 *  M�todo n�o invocado por JSP.
	 * @param atualizacaoStatus
	 */
	public void setAtualizacaoStatus(boolean atualizacaoStatus) {
		this.atualizacaoStatus = atualizacaoStatus;
	}

	/** Retorna o arquivo do projeto enviado pelo usu�rio. 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>/sigaa.ear/sigaa.war/public/processo_seletivo/form_inscricao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public UploadedFile getArquivoProjeto() {
		return arquivoProjeto;
	}

	/** Seta o arquivo do projeto enviado pelo usu�rio.
 	 *  M�todo n�o invocado por JSP.
	 * @param arquivoProjeto
	 */
	public void setArquivoProjeto(UploadedFile arquivoProjeto) {
		this.arquivoProjeto = arquivoProjeto;
	}

	public void setComboStatus(Collection<SelectItem> comboStatus) {
		this.comboStatus = comboStatus;
	}
	
	/**
	 * Retorna o endere�o (URL) para baixar e exportar o arquivos texto para PDF.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>/sigaa.war/public/processo_seletivo/form_inscricao.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String getEnderecoExportarPDF(){
		return ParametroHelper.getInstance().getParametro( ParametrosGerais.ENDERECO_EXPORTAR_FORMATO_PDF );
	}
	
}

