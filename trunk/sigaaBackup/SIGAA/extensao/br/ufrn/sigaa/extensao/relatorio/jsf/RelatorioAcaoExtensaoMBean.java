/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/02/2009
 *
 */
package br.ufrn.sigaa.extensao.relatorio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.PlanoTrabalhoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioAcaoExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.AndamentoObjetivoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoParecerAvaliacaoExtensao;
import br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao;
import br.ufrn.sigaa.extensao.jsf.ParticipanteAcaoExtensaoMBean;
import br.ufrn.sigaa.extensao.jsf.helper.DesignacaoFuncaoProjetoHelper;
import br.ufrn.sigaa.extensao.relatorio.dominio.DetalhamentoRecursos;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * 
 * Mbean responsável por controlar o cadastro de relatórios
 * de ação de extensão.
 * 
 * @author Ilueny Santos
 *
 */
@Scope("session")
@Component("relatorioAcaoExtensao")
public class RelatorioAcaoExtensaoMBean extends SigaaAbstractController<RelatorioAcaoExtensao> {

	/** Armazena os relatórios de uma ação de extensão. **/
	private Collection<RelatorioAcaoExtensao> relatoriosLocalizados = new ArrayList<RelatorioAcaoExtensao>();
	
	/** Armazena os relatórios pendentes de autorização pela PROEX. **/
	private Collection<RelatorioAcaoExtensao> relatoriosPendentesProex;
	
	/** Armazena um membro do projeto. **/
	private MembroProjeto membroEquipe = new MembroProjeto();
	
	/** Armazena todas as atividades coordenadas pelo coordenador atual.  **/
	private Collection<AtividadeExtensao> acoesCoordenador;
	
	/** Utilizado para armazenar as atividades do coordenador logado que estão pendentes quanto ao envio de relatórios*/
	private Collection<AtividadeExtensao> atividadesPendentesRelatoriosCoordenador = new ArrayList<AtividadeExtensao>();
	
	/** Atributos utilizados para a seleção de opções de busca(filtros) em telas de busca. **/
	/** Filtro busca título */
	private boolean checkBuscaTitulo;
	/** Filtro busca tipo relatório */
	private boolean checkBuscaTipoRelatorio;
	/** Filtro busca ano */
	private boolean checkBuscaAno;
	/** Filtro busca período */
	private boolean checkBuscaPeriodo;
	/** Filtro busca servidor */
	private boolean checkBuscaServidor;
	/** Filtro busca período de conclusão */
	private boolean checkBuscaPeriodoConclusao;
	/** Filtro busca tipo ação */
	private boolean checkBuscaTipoAcao;
	/** Filtro busca edital */
	private Boolean checkBuscaEdital = false;
	
	/** Atributos utilizados para a inserção de informações em telas de buscas. **/
	/** Título ação extensão */
	private String buscaTitulo;
	/** Tipo de relatório */
	private Integer buscaTipoRelatorio;
	/** Ano */
	private Integer buscaAno;
	/** Data início */
	private Date buscaInicio;
	/** Data fim */
	private Date buscaFim;
	/** Data início conclusão */
	private Date buscaInicioConclusao;
	/** Data fim conclusão */
	private Date buscaFimConclusao;
	/** Verifica tipo projeto */
	private boolean buscaTipoAcaoProjeto;
	/** Verifica tipo curso */
	private boolean buscaTipoAcaoCurso;
	/** Verifica tipo evento */
	private boolean buscaTipoAcaoEvento;
	/** Verifica tipo produto */
	private boolean buscaTipoAcaoProduto;
	/** Verifica tipo programa */
	private boolean buscaTipoAcaoPrograma;
	/** Indica se os casos de uso de manipulação de relatórios tiverem origem de notificações */
	private boolean telaNotificacoes = false;	
	/** Edital  */
	private Integer buscaEdital;
	
	/** Informa qual a aba de coordenação de origem da operação realizada. CCEP ou CPP. */
	private String coordenacao = "";

	/** Informa o tipo de atividade de extensão. */
	private TipoAtividadeExtensao tipoAtividade;

	public RelatorioAcaoExtensaoMBean() {
		clear();
	}
	
	/**
     * Inicializa objetos importantes no cadastro de uma novo relatório.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>Não é chamado por JSPs.</li>
	 * </ul>
     */	
	private void clear() {
		obj = new RelatorioAcaoExtensao();
		tipoAtividade = new TipoAtividadeExtensao();
	}

	/**
	 * Lista todos os tipos de relatórios possíveis Utilizado na busca por
	 * relatórios através do menu de extensão
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/RelatorioAcaoExtensao/form_busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getTiposRelatorioCombo() {
		return getAll(TipoRelatorioExtensao.class, "id", "descricao");
	}

	/**
	 * Lista todos os tipos de relatórios possíveis Utilizado na busca por
	 * relatórios através do menu de extensão
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/RelatorioAcaoExtensao/form_busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String buscar() throws DAOException {

		erros.getMensagens().clear();

		if (relatoriosLocalizados != null) {
		    relatoriosLocalizados.clear();
		}

		/* Analisando filtros selecionados */
		String titulo = null;
		Integer idTipoRelatorio = null;
		Integer ano = null;
		Date inicio = null;
		Date fim = null;
		Date inicioConclusao = null;
		Date fimConclusao = null;
		Integer[] idsTipos = null;
		Integer idEdital = null;
		Integer idServidor = null;

		ListaMensagens erros = new ListaMensagens();

		// Definição dos filtros e validações

		if (checkBuscaServidor) {
			idServidor = membroEquipe.getServidor().getId();			
			ValidatorUtil.validaInt(idServidor, "Servidor", true, erros);
		}


		if (checkBuscaTitulo) {
			titulo = buscaTitulo;
			ValidatorUtil.validateRequired(titulo, "Título da Ação", erros);
		}

		if (checkBuscaTipoAcao) {

			if(!buscaTipoAcaoProjeto && !buscaTipoAcaoEvento && !buscaTipoAcaoCurso && !buscaTipoAcaoPrograma && !buscaTipoAcaoProduto) {
				erros.addErro("Tipo de Ação: Campo Obrigatório não informado.");								
			}

			ArrayList<Integer> idsTipoAcao = new ArrayList<Integer>();
			if (buscaTipoAcaoProjeto)
				idsTipoAcao.add(TipoAtividadeExtensao.PROJETO);

			if (buscaTipoAcaoEvento)
				idsTipoAcao.add(TipoAtividadeExtensao.EVENTO);

			if (buscaTipoAcaoCurso)
				idsTipoAcao.add(TipoAtividadeExtensao.CURSO);

			if (buscaTipoAcaoPrograma)
				idsTipoAcao.add(TipoAtividadeExtensao.PROGRAMA);

			if (buscaTipoAcaoProduto)
				idsTipoAcao.add(TipoAtividadeExtensao.PRODUTO);

			idsTipos = idsTipoAcao.toArray(new Integer[] {});
			ValidatorUtil.validateRequired(idsTipos, "Tipo de Ação", erros);

		}

		if (checkBuscaTipoRelatorio) {
			idTipoRelatorio = buscaTipoRelatorio;
			ValidatorUtil.validateRequiredId(idTipoRelatorio, "Tipo da Ação", erros);
		}
		if (checkBuscaAno) {
			ano = buscaAno;
			ValidatorUtil.validaInt(ano, "Ano", erros);
		}
		if (checkBuscaPeriodo) {
			inicio = buscaInicio;
			fim = buscaFim;

			ValidatorUtil.validateRequired(inicio,"Data Início Período Envio", erros);			
			ValidatorUtil.validateRequired(fim,"Data Fim Período Envio", erros);			

			if ((inicio != null) && (fim != null))
				ValidatorUtil.validaInicioFim(inicio, fim, "Data Início", erros);
		}
		
		if(checkBuscaPeriodoConclusao){
			inicioConclusao = buscaInicioConclusao;
			fimConclusao = buscaFimConclusao;

			ValidatorUtil.validateRequired(inicioConclusao,"Data Início Período Conclusão", erros);			
			ValidatorUtil.validateRequired(fimConclusao,"Data Fim Período Conclusão", erros);			

			if ((inicioConclusao != null) && (fimConclusao != null))
				ValidatorUtil.validaInicioFim(inicioConclusao, fimConclusao, "Data Início Conclusão", erros);
		}
		
		if(checkBuscaEdital)
		{
			idEdital = getBuscaEdital();			
			ValidatorUtil.validaInt(idEdital, "Edital", true, erros);
		}		

		if (!checkBuscaTitulo && !checkBuscaTipoRelatorio && !checkBuscaAno
				&& !checkBuscaPeriodo && !checkBuscaTipoAcao && !checkBuscaEdital && !checkBuscaServidor && !checkBuscaPeriodoConclusao ) {

			addMensagemErro("Selecione uma opção para efetuar a busca por relatórios.");

		} else {

			if (erros.isEmpty()) {

				RelatorioAcaoExtensaoDao dao = getDAO(RelatorioAcaoExtensaoDao.class);
				relatoriosLocalizados = dao.buscaRelatorioAcaoExtensao(titulo, idTipoRelatorio,
						idsTipos, ano, inicio, fim, inicioConclusao, fimConclusao , idEdital, idServidor);
				if(relatoriosLocalizados.isEmpty()) {
					addMensagemWarning("Nenhum Relatório encontrado com os parâmetros de busca informados.");
				}

			} else {
				addMensagens(erros);
			}

		} 
		return forward("/extensao/RelatorioAcaoExtensao/busca.jsp");
	}

	/**
	 * Carrega relatório e prepara MBeans para visualização.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/extensao/AutorizacaoDepartamento/form_busca_autorizar_relatorios.jsp</li>
	 * 	<li>sigaa.war/extensao/AutorizacaoDepartamento/lista_relatorio.jsp</li>
	 * 	<li>sigaa.war/extensao/RelatorioAcaoExtensao/busca.jsp</li>
	 * 	<li>sigaa.war/extensao/RelatorioAcaoExtensao/lista.jsp</li>
	 * 	<li>sigaa.war/extensao/ValidacaoRelatorioProex/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String view() throws DAOException {
		
		AndamentoObjetivoDao dao = getDAO(AndamentoObjetivoDao.class);
		try {
			Integer id = getParameterInt("id",0);
			obj = getGenericDAO().findByPrimaryKey(id, RelatorioAcaoExtensao.class);
			obj.getAtividade().setMembrosEquipe(dao.findByExactField(MembroProjeto.class, "projeto.id", obj.getAtividade().getProjeto().getId()));
			
			//Evitar erro de Lazy.
			Collection<MembroProjeto> membros = dao.findByExactField(MembroProjeto.class, "projeto.id", obj.getAtividade().getProjeto().getId());
			obj.getAtividade().setMembrosEquipe(membros);
			obj.setAndamento( dao.findAndamentoAtividades(obj.getAtividade().getId(), obj.getTipoRelatorio().getId(), false ));
			
			//chama o visualizar específico dependendo do tipo de relatório
			if (obj.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROJETO) {
				return forward(ConstantesNavegacao.RELATORIOPROJETO_VIEW);
				
			} else if ((obj.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO)
					|| (obj.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO)) {
				return forward(ConstantesNavegacao.RELATORIOCURSOSEVENTOS_VIEW);
				
			} else if (obj.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PRODUTO) {
				return forward(ConstantesNavegacao.RELATORIO_PRODUTO_VIEW);
				
			} else if (obj.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROGRAMA) {
				return forward(ConstantesNavegacao.RELATORIO_PROGRAMA_VIEW);
				
			} else {
				addMensagemErro("Relatório ainda não definido para o tipo de ação selecionada.");
				return null;
			}
			
		} finally {
			dao.close();
		}
	}

	/**
	 * Verifica permissão e retorna todos os relatórios parciais e finais
	 * cadastrados pelo usuário atual nas atividades onde ele é coordenador
	 * ativo
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP´s</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public Collection<RelatorioAcaoExtensao> getAllRelatoriosCoordenador() throws DAOException, SegurancaException {
		if (!getAcessoMenu().isCoordenadorExtensao()) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
		}
		RelatorioAcaoExtensaoDao dao = getDAO(RelatorioAcaoExtensaoDao.class);
		return dao.findAllRelatoriosCoordenador(getServidorUsuario().getId());
	}

	/**
	 * Retorna todas as atividades coordenadas pelo usuário atual que podem
	 * receber cadastros de relatórios
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP´s</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> carregarAcoesCoordenador()	throws DAOException {
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);		
		Integer[] idSituacoesValidas = new Integer[] {TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO, TipoSituacaoProjeto.EXTENSAO_CONCLUIDO, TipoSituacaoProjeto.EXTENSAO_PENDENTE_DE_RELATORIO };
		return dao.findAcoesComRelatorioByCoordenador(getUsuarioLogado().getServidor(), idSituacoesValidas);
	}

	/**
	 * Lista de relatórios pendentes de autorização pela PROEX
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP´s</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public Collection<RelatorioAcaoExtensao> getAllRelatoriosPendenteProex() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO,
				SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO);
		RelatorioAcaoExtensaoDao dao = getDAO(RelatorioAcaoExtensaoDao.class);
		Collection<RelatorioAcaoExtensao> result = new ArrayList<RelatorioAcaoExtensao>();
		
		if (getParameter("coordenacao") != null)
			coordenacao = getParameter("coordenacao"); 

		if (coordenacao != null) {
			//Coordenação de cursos, eventos e produtos
			if ("ccep".equals(coordenacao)) {
				result.addAll(dao.findAllRelatoriosPendentesProex(new Integer[] {
						TipoAtividadeExtensao.CURSO,
						TipoAtividadeExtensao.EVENTO, TipoAtividadeExtensao.PRODUTO }));
			}

			//coordenação de projetos e programas
			if ("cpp".equals(coordenacao)) {
				result.addAll(dao.findAllRelatoriosPendentesProex(new Integer[] { 
						TipoAtividadeExtensao.PROJETO, TipoAtividadeExtensao.PROGRAMA }));
			}
		}

		return result;
	}

	/**
	 * Escolhe um relatório que ainda não foi analisado pela PROEX
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/ValidacaoRelatorioProex/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * 
	 */
	public String analisarRelatorioProex() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO,
				SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO);
		obj = getGenericDAO().findByPrimaryKey(getParameterInt("id"), RelatorioAcaoExtensao.class);
		obj.setTipoParecerProex(new TipoParecerAvaliacaoExtensao());
		obj.getTipoParecerProex().setId(TipoParecerAvaliacaoExtensao.APROVADO); //valor padrão
		prepareMovimento(SigaaListaComando.VALIDAR_RELATORIO_ACAO_EXTENSAO_PROEX);
		setConfirmButton("Confirmar Validação");

		//chama o visualizar específico dependendo do tipo de relatório
		if (obj.getAtividade().isTipoProjeto()) {
			return forward(ConstantesNavegacao.VALIDACAORELATORIO_PROJETO_PROEX_FORM);

		} else if ( obj.getAtividade().isTipoCurso() || obj.getAtividade().isTipoEvento()) {
			return forward(ConstantesNavegacao.VALIDACAORELATORIO_CURSO_EVENTO_PROEX_FORM);

		} else if (obj.getAtividade().isTipoProduto()) {
			return forward(ConstantesNavegacao.VALIDACAO_RELATORIO_PRODUTO_PROEX_FORM);

		} else if (obj.getAtividade().isTipoPrograma()) {
			return forward(ConstantesNavegacao.VALIDACAO_RELATORIO_PROGRAMA_PROEX_FORM);

		} else {
			addMensagemErro("Relatório ainda não definido para o tipo de ação selecionada.");
			return null;
		}
	}

	/**
	 * Autorizar relatório de extensão pela PROEX, através do menu da PROEX,
	 * acessa o link de validar relatórios de cursos e eventos de extensão na
	 * lista de relatórios após selecionar um deles o usuário é levado pra um
	 * formulário onde visualiza o relatório e pode validá-lo
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/ValidacaoRelatorioProex/curso_evento_form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public String validarRelatorioProex() throws SegurancaException, ArqException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO,
				SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO);

		try {
			
			obj.setDataValidacaoProex(new Date());
			if (ValidatorUtil.isEmpty(obj.getTipoParecerProex())) {
				addMensagemErro("Parecer: Campo obrigatório não informado");
				return null;
			}
			
			// Verifica se é necessário ter a justificativa
			if(!obj.isAprovadoProex() && ValidatorUtil.isEmpty(obj.getParecerProex())){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Justificativa");
				return null;
			}

			obj.setTipoParecerProex(getGenericDAO().findByPrimaryKey(obj.getTipoParecerProex().getId(), TipoParecerAvaliacaoExtensao.class));			
			PlanoTrabalhoExtensaoDao dao = getDAO(PlanoTrabalhoExtensaoDao.class);
			obj.getAtividade().setPlanosTrabalho(dao.findByAtividade(obj.getAtividade().getId()));
			obj.getAtividade().getMembrosEquipe().iterator();

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.VALIDAR_RELATORIO_ACAO_EXTENSAO_PROEX);
			execute(mov, getCurrentRequest());
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return redirectMesmaPagina();
		}

		return iniciarValidacaoRelatorios();
	}

	/**
	 * Inicia a validação dos relatórios de extensão. Verifica permissões e
	 * direciona para a lista de relatórios.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciarValidacaoRelatorios() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO,
				SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO);

		relatoriosPendentesProex = getAllRelatoriosPendenteProex();
		return forward(ConstantesNavegacao.VALIDACAORELATORIO_PROEX_LISTA);
	}

	/**
	 * Verifica permissões e redireciona para tela de lista de relatórios
	 * cadastrados pelo usuário atual.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * 	<li>sigaa.war/extensao/menu_ta.jsp</li>
	 * <ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciarCadastroRelatorio() throws SegurancaException, DAOException {
		if ( !DesignacaoFuncaoProjetoHelper.
				isCoordenadorOrDesignacaoCoordenador(getUsuarioLogado().getPessoa().getId())  ) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
		}		
		
		acoesCoordenador = carregarAcoesCoordenador();
		return forward(ConstantesNavegacao.RELATORIOACAOEXTENSAO_LISTA);
	}

	/**
	 * Prepara o MBean (participanteAcaoExtensao) e o processador que irá
	 * realizar o cadastro do participante. Valida somente para coordenadores de
	 * ações realizarem esta operação. Redireciona o usuário para o form de
	 * cadastro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/extensao/RelatorioCursoEvento/form.jsp</li>
	 * 	<li>sigaa.war/extensao/RelatorioProjeto/form.jsp</li>	  
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String alterarListaParticipantes() throws ArqException,
	NegocioException {

		prepareMovimento(SigaaListaComando.SALVAR_RELATORIO_ACAO_EXTENSAO);

		int id = getParameterInt("id", 0);
		AtividadeExtensao acao = getGenericDAO().findByPrimaryKey(id,
				AtividadeExtensao.class);

		// salvando o relatório
		try {
			setTipoAtividade(acao.getTipoAtividadeExtensao());
			if (acao.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROJETO) {
				obj = ((RelatorioProjetoMBean) getMbean()).submeter(SigaaListaComando.SALVAR_RELATORIO_ACAO_EXTENSAO);
				
			} else if (acao.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PRODUTO) {
				obj = ((RelatorioProdutoMBean) getMbean()).submeter(SigaaListaComando.SALVAR_RELATORIO_ACAO_EXTENSAO);

			} else if (acao.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROGRAMA) {
				obj = ((RelatorioProgramaMBean) getMbean()).submeter(SigaaListaComando.SALVAR_RELATORIO_ACAO_EXTENSAO);

			} else if ((acao.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO)
					|| (acao.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO)) {
				obj = ((RelatorioCursoEventoMBean) getMbean()).submeter(SigaaListaComando.SALVAR_RELATORIO_ACAO_EXTENSAO);
			} else {
				addMensagemErro("Relatório ainda não definido para o tipo de ação selecionada.");
				return null;
			}

		} catch (NegocioException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		// configurando o mbean que controla o cadastro de participações...
		getGenericDAO().lock(acao);

		ParticipanteAcaoExtensaoMBean pMbean = getMBean("participanteAcaoExtensao");
		pMbean.setObj(new ParticipanteAcaoExtensao());
		//pMbean.setAcao(acao);
		//pMbean.getObj().setAcaoExtensao(acao);
		//carregar participantes
		acao = getGenericDAO().findByPrimaryKey(acao.getId(), AtividadeExtensao.class);
		acao.setParticipantes(getDAO(ParticipanteAcaoExtensaoDao.class).findByAcao(acao.getId()));
		pMbean.setResultadosBusca(acao.getParticipantesOrdenados());
		pMbean.getPaginacao().setTotalRegistros(pMbean.getResultadosBusca().size());
		//resultadosBusca.size
		pMbean.setTamanhoPagina(50);
		pMbean.getPaginacao().setPaginaAtual(0);
		
		// Carregar Municípios no campo de Filtro
		//pMbean.carregarMunicipios();
		
		//pMbean.setContinuarCadastroRelatorio(true);

		setConfirmButton("Cadastrar");
		setReadOnly(false);

		// redirecionando para o cadastro de participantes
		prepareMovimento(ArqListaComando.CADASTRAR);
		getCurrentRequest().setAttribute("origem", "cadastroDeRelatorios");
		return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_LISTA);
	}


	/**
	 * Prepara para adicionar relatório de uma ação de extensão
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/RelatorioAcaoExtensao/lista.jsp</li>
	 * </ul>
	 * @throws ArqException
	 */ 
	public String preAdicionarRelatorio() throws ArqException {
		
		clear();
		Integer id = getParameterInt("id", 0);
		telaNotificacoes = new Boolean (getParameter("telaNotificacoes"));
		
		AtividadeExtensao acao = getGenericDAO().findByPrimaryKey(id, AtividadeExtensao.class);
		
		obj.setAtividade(acao);
		
		setTipoAtividade(acao.getTipoAtividadeExtensao());
		
		Date dataLimite = ParametroHelper.getInstance()
				.getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA);
		
		//chama o pre-adicionar específico dependendo do tipo de relatório
		if (acao.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROJETO) {
			((RelatorioProjetoMBean) getMbean()).preAdicionarRelatorio(acao, dataLimite);

		} else if ((acao.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO)
				|| (acao.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO)) {
			((RelatorioCursoEventoMBean) getMbean()).preAdicionarRelatorio(acao, dataLimite);

		} else if (acao.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PRODUTO) {
			((RelatorioProdutoMBean) getMbean()).preAdicionarRelatorio(acao, dataLimite);

		} else if (acao.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROGRAMA) {
			((RelatorioProgramaMBean) getMbean()).preAdicionarRelatorio(acao, dataLimite);

		} else {
			addMensagemErro("Relatório ainda não definido para o tipo de ação selecionada.");
		}

		if (hasOnlyErrors()) {
			return null;
		}
		
		return direcionar(dataLimite, acao);
	}

	/**
	 * Realizar o redirecionamento adequado de acordo com cada tipo de relatório.
	 * Método não chamado por JSP's.
	 * @param dataLimite
	 * @param acao
	 * @return
	 */
	private String direcionar(Date dataLimite, AtividadeExtensao acao) {
		 if ( acao.getProjeto().getDataCadastro().before(dataLimite) ) {
			 switch (tipoAtividade.getId()) {
				case TipoAtividadeExtensao.PROJETO:
					return forward(ConstantesNavegacao.RELATORIOPROJETO_FORM);
				case TipoAtividadeExtensao.CURSO:
					return forward(ConstantesNavegacao.RELATORIOCURSOSEVENTOS_FORM);
				case TipoAtividadeExtensao.EVENTO:
					return forward(ConstantesNavegacao.RELATORIOCURSOSEVENTOS_FORM);
				case TipoAtividadeExtensao.PRODUTO:
					return forward(ConstantesNavegacao.RELATORIO_PRODUTO_FORM);
				case TipoAtividadeExtensao.PROGRAMA:
					return forward(ConstantesNavegacao.RELATORIO_PROGRAMA_FORM);
				default:
					return forward(ConstantesNavegacao.RELATORIOPROJETO_FORM);
			} 
		 } else {
			 return forward(ConstantesNavegacao.RELATORIO_EXTENSAO_FORM);
		 }
	}

	
	/**
	 * Método não chamado por JSP's
	 * @return
	 * @throws DAOException
	 */
	public TipoRelatorioExtensao carregarTipoRelatorio() throws DAOException {
		Boolean relatorioFinal = getParameterBoolean("relatorioFinal");
		TipoRelatorioExtensao tipoRelatorio;
		if (relatorioFinal)
			tipoRelatorio = getGenericDAO().findByPrimaryKey(TipoRelatorioExtensao.RELATORIO_FINAL,
					TipoRelatorioExtensao.class);
		else
			tipoRelatorio = getGenericDAO().findByPrimaryKey(TipoRelatorioExtensao.RELATORIO_PARCIAL,
					TipoRelatorioExtensao.class);
		return tipoRelatorio;
	}

	/**
	 * Método não chamado por JSP's.
	 * @param atividade
	 * @param dateLimite
	 * @param relatorio
	 * @throws ArqException
	 */
	public <T extends RelatorioAcaoExtensao> void carregarObjetivos( AtividadeExtensao atividade, Date dateLimite, T relatorio ) throws ArqException {
		AndamentoObjetivoDao dao = getDAO(AndamentoObjetivoDao.class);
		ParticipanteAcaoExtensaoDao participantesDao = getDAO(ParticipanteAcaoExtensaoDao.class);
		try {
			prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_EXTENSAO);
			relatorio.setAtividade(atividade);
			relatorio.getAtividade().setMembrosEquipe(dao.findByExactField(MembroProjeto.class, "projeto.id", atividade.getProjeto().getId()));
			relatorio.getAtividade().setOrcamentosDetalhados(dao.findByExactField(OrcamentoDetalhado.class, "projeto.id", atividade.getProjeto().getId()));		
			relatorio.getAtividade().setParticipantes(participantesDao.findByAcao(obj.getAtividade().getId()));
			relatorio.setDataCadastro(new Date());
			relatorio.setRegistroEntradaCadastro(getUsuarioLogado().getRegistroEntrada());
			relatorio.setTipoRelatorio(carregarTipoRelatorio());
			relatorio.setAtivo(true);
			relatorio.setAndamento( dao.findAndamentoAtividades(atividade.getId(), relatorio.getTipoRelatorio().getId(), false ) );
			relatorio.setAcaoRealizada( relatorio.getAcaoRealizada() != null ? relatorio.getAcaoRealizada() : Boolean.TRUE );
			
			if ( relatorio.getAtividade().getProjeto().getDataCadastro().before(dateLimite) ) {
				// Criando detalhamento de recursos do projeto (recursos utilizados)
				Collection<DetalhamentoRecursos> recursos = new ArrayList<DetalhamentoRecursos>();
				for (OrcamentoConsolidado oc : atividade.getOrcamentosConsolidados()) {
					DetalhamentoRecursos recurso = new DetalhamentoRecursos();
					recurso.setElemento(oc.getElementoDespesa());
					recurso.setFaex(0.0);
					recurso.setFunpec(0.0);
					recurso.setOutros(0.0);
					recurso.setRelatorioAcaoExtensao(relatorio);
					recursos.add(recurso);
				}
				
				relatorio.setDetalhamentoRecursos(recursos);
			}
			
		getCurrentSession().setAttribute("aba", "objetivos");
		setReadOnly(false);
		setConfirmButton("Enviar Relatório");
		
		} finally {
			dao.close();
			participantesDao.close();
		}
	}
	
	/**
	 * 
	 * Método não chamado por JSP's.
	 * @return
	 */
	public Object getMbean(){
		switch (tipoAtividade.getId()) {
			case TipoAtividadeExtensao.PROJETO:
				return ((RelatorioProjetoMBean) getMBean("relatorioProjeto"));
			case TipoAtividadeExtensao.CURSO:
				return ((RelatorioCursoEventoMBean) getMBean("relatorioCursoEvento"));
			case TipoAtividadeExtensao.EVENTO:
				return ((RelatorioCursoEventoMBean) getMBean("relatorioCursoEvento"));
			case TipoAtividadeExtensao.PRODUTO:
				return ((RelatorioProdutoMBean) getMBean("relatorioProdutoMBean"));
			case TipoAtividadeExtensao.PROGRAMA:
				return ((RelatorioProgramaMBean) getMBean("relatorioProgramaMBean"));
			default:
				return ((RelatorioProjetoMBean) getMBean("relatorioProjeto"));
		}
	}

	/**
	 * Prepara para alterar relatório de uma ação de extensão
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li> 
	 * </ul>
	 * @throws ArqException
	 */
	public String preAlterarRelatorio() throws ArqException {

		clear();
		Integer id = getParameterInt("idRelatorio", 0);
		telaNotificacoes = new Boolean (getParameter("telaNotificacoes"));
		
		RelatorioAcaoExtensao rela = getGenericDAO().findByPrimaryKey(id,
				RelatorioAcaoExtensao.class);
		setObj(rela);
		setTipoAtividade(rela.getAtividade().getTipoAtividadeExtensao());

		Date dataLimite = ParametroHelper.getInstance()
				.getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA);
		
		if (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROJETO) {
			((RelatorioProjetoMBean) getMbean()).preAlterarRelatorio(rela.getAtividade(), dataLimite);

		} else if ((rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO)
				|| (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO)) {
			((RelatorioCursoEventoMBean) getMbean()).preAlterarRelatorio(rela.getAtividade(), dataLimite);

		} else if (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PRODUTO) {
			((RelatorioProdutoMBean) getMbean()).preAlterarRelatorio(rela.getAtividade(), dataLimite);	

		} else if (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROGRAMA) {
			((RelatorioProgramaMBean) getMbean()).preAlterarRelatorio(rela.getAtividade(), dataLimite);	

		} else {
			addMensagemErro("Relatório ainda não definido para o tipo de ação selecionada.");
		}
		
		Date dateLimite = ParametroHelper.getInstance()
				.getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA);
		
		return direcionar(dataLimite, rela.getAtividade());
	}

	/**
	 * Prepara para remover relatório de uma ação de extensão
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/RelatorioAcaoExtensao/lista.jsp</li> 
	 * </ul>
	 * @throws ArqException
	 */
	public void preRemoverRelatorio() throws ArqException {

		Integer id = getParameterInt("idRelatorio", 0);
		telaNotificacoes = new Boolean (getParameter("telaNotificacoes"));
		RelatorioAcaoExtensao rela = getGenericDAO().findByPrimaryKey(id,
				RelatorioAcaoExtensao.class);

		setTipoAtividade(rela.getAtividade().getTipoAtividadeExtensao());
		if (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROJETO) {
			((RelatorioProjetoMBean) getMbean()).preRemoverRelatorio();

		} else if ((rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO)
				|| (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO)) {
			((RelatorioCursoEventoMBean) getMbean()).preRemoverRelatorio();
		} else if (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PRODUTO) {
			((RelatorioProdutoMBean) getMbean()).preRemoverRelatorio();	

		} else if (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROGRAMA) {
			((RelatorioProgramaMBean) getMbean()).preRemoverRelatorio();	

		} else {
			addMensagemErro("Relatório ainda não definido para o tipo de ação selecionada.");
		}

	}

	/**
	 * Devolve o relatório para que o coordenador possa reeditá-lo
	 * procedimento permitido somente para os membros da PROEX e quando o coordenador 
	 * alega que errou no preenchimento do relatório.
	 *	<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/RelatorioAcaoExtensao/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String devolverRelatorioCoordenador() throws ArqException {		
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		int id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, RelatorioAcaoExtensao.class);
		
		try {

			prepareMovimento(SigaaListaComando.DEVOLVER_RELATORIO_COORDENADOR_EXTENSAO);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.DEVOLVER_RELATORIO_COORDENADOR_EXTENSAO);
			mov.setObjMovimentado(obj);
			execute(mov, getCurrentRequest());			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);			

		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
		}
		buscar();
		return null;
	}
	
	/**
	 * Permite listar os tipos de autorizações possíveis
	 * 
	 * Chamado por: sigaa.war/extensao/menu.jsp
	 * Chamado por: sigaa.war/extensao/AutorizacaoDepartamento/form.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public List<SelectItem> getTiposParecerCombo() throws DAOException {

		GenericDAO dao = getGenericDAO();
		return toSelectItems(dao.findAll(TipoParecerAvaliacaoExtensao.class), "id",
				"descricao");
		
	}
	
	/** Salva um rascunho do relatório.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/RelatorioProjeto/form.jsp</li> 
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String salvar() throws ArqException, NegocioException {
		if (obj.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROJETO) {
			return ((RelatorioProjetoMBean) getMBean("relatorioProjeto")).salvar();

		} else if ((obj.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO)
				|| (obj.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO)) {
			return ((RelatorioCursoEventoMBean) getMBean("relatorioCursoEvento")).salvar();

		} else if (obj.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PRODUTO) {
			return  ((RelatorioProdutoMBean) getMBean("relatorioProdutoMBean")).salvar();	

		} else if (obj.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROGRAMA) {
			return ((RelatorioProgramaMBean) getMBean("relatorioProgramaMBean")).salvar();	
		}
		
		return null;
	}
	
	/**
	 * Remover o relatório de uma ação de extensão
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioProduto/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String removerRelatorio() throws NegocioException, ArqException {
		Integer id = getParameterInt("idRelatorio", 0);
		RelatorioAcaoExtensao rela = getGenericDAO().findByPrimaryKey(id, RelatorioAcaoExtensao.class);
		setObj(rela);
		setTipoAtividade(rela.getAtividade().getTipoAtividadeExtensao());

		if (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROJETO) {
			((RelatorioProjetoMBean) getMbean()).removerRelatorio();

		} else if ((rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO)
				|| (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO)) {
			((RelatorioCursoEventoMBean) getMbean()).removerRelatorio();

		} else if (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PRODUTO) {
			((RelatorioProdutoMBean) getMbean()).removerRelatorio();	

		} else if (rela.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROGRAMA) {
			((RelatorioProgramaMBean) getMbean()).removerRelatorio();
		}
		
		setAtividadesPendentesRelatoriosCoordenador(getDAO(RelatorioAcaoExtensaoDao.class).findAcosPendentesRelatorio(getUsuarioLogado().getPessoa()));
		return redirectMesmaPagina();
	}
	
	/**
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioCursoEvento/view.jsp
	 * /extensao/RelatorioProduto/view.jsp
	 * /extensao/RelatorioPrograma/view.jsp
	 * /extensao/RelatorioProjeto/view.jsp
	 * 
	 * @return
	 */
	public boolean isNovoFormulario() {
		Date dataLimite = ParametroHelper.getInstance()
				.getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA);
		return obj.getAtividade().getProjeto().getDataCadastro().after(dataLimite);		
	}
	
	public boolean isCheckBuscaTipoAcao() {
		return checkBuscaTipoAcao;
	}

	public void setCheckBuscaTipoAcao(boolean checkBuscaTipoAcao) {
		this.checkBuscaTipoAcao = checkBuscaTipoAcao;
	}

	public boolean isBuscaTipoAcaoProjeto() {
		return buscaTipoAcaoProjeto;
	}

	public void setBuscaTipoAcaoProjeto(boolean buscaTipoAcaoProjeto) {
		this.buscaTipoAcaoProjeto = buscaTipoAcaoProjeto;
	}

	public boolean isBuscaTipoAcaoCurso() {
		return buscaTipoAcaoCurso;
	}

	public void setBuscaTipoAcaoCurso(boolean buscaTipoAcaoCurso) {
		this.buscaTipoAcaoCurso = buscaTipoAcaoCurso;
	}

	public boolean isBuscaTipoAcaoEvento() {
		return buscaTipoAcaoEvento;
	}

	public void setBuscaTipoAcaoEvento(boolean buscaTipoAcaoEvento) {
		this.buscaTipoAcaoEvento = buscaTipoAcaoEvento;
	}

	public Collection<AtividadeExtensao> getAcoesCoordenador() {
		return acoesCoordenador;
	}

	public void setAcoesCoordenador(Collection<AtividadeExtensao> acoesCoordenador) {
		this.acoesCoordenador = acoesCoordenador;
	}

	public Boolean getCheckBuscaEdital() {
		return checkBuscaEdital;
	}

	public void setCheckBuscaEdital(Boolean checkBuscaEdital) {
		this.checkBuscaEdital = checkBuscaEdital;
	}

	public Integer getBuscaEdital() {
		return buscaEdital;
	}

	public void setBuscaEdital(Integer buscaEdital) {
		this.buscaEdital = buscaEdital;
	}

	public Collection<RelatorioAcaoExtensao> getRelatoriosLocalizados() {
		return relatoriosLocalizados;
	}

	public void setRelatoriosLocalizados(
			Collection<RelatorioAcaoExtensao> relatoriosLocalizados) {
		this.relatoriosLocalizados = relatoriosLocalizados;
	}

	public boolean isCheckBuscaTitulo() {
		return checkBuscaTitulo;
	}

	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
		this.checkBuscaTitulo = checkBuscaTitulo;
	}

	public String getBuscaTitulo() {
		return buscaTitulo;
	}

	public void setBuscaTitulo(String buscaTitulo) {
		this.buscaTitulo = buscaTitulo;
	}

	public boolean isCheckBuscaTipoRelatorio() {
		return checkBuscaTipoRelatorio;
	}

	public void setCheckBuscaTipoRelatorio(boolean checkBuscaTipoRelatorio) {
		this.checkBuscaTipoRelatorio = checkBuscaTipoRelatorio;
	}

	public Integer getBuscaTipoRelatorio() {
		return buscaTipoRelatorio;
	}

	public void setBuscaTipoRelatorio(Integer buscaTipoRelatorio) {
		this.buscaTipoRelatorio = buscaTipoRelatorio;
	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public Integer getBuscaAno() {
		return buscaAno;
	}

	public void setBuscaAno(Integer buscaAno) {
		this.buscaAno = buscaAno;
	}

	public boolean isCheckBuscaPeriodo() {
		return checkBuscaPeriodo;
	}

	public void setCheckBuscaPeriodo(boolean checkBuscaPeriodo) {
		this.checkBuscaPeriodo = checkBuscaPeriodo;
	}

	public Date getBuscaInicio() {
		return buscaInicio;
	}

	public void setBuscaInicio(Date buscaInicio) {
		this.buscaInicio = buscaInicio;
	}

	public Date getBuscaFim() {
		return buscaFim;
	}

	public void setBuscaFim(Date buscaFim) {
		this.buscaFim = buscaFim;
	}

	public Collection<RelatorioAcaoExtensao> getRelatoriosPendentesProex() {
		return relatoriosPendentesProex;
	}

	public void setRelatoriosPendentesProex(Collection<RelatorioAcaoExtensao> relatoriosPendentesProex) {
		this.relatoriosPendentesProex = relatoriosPendentesProex;
	}

	public MembroProjeto getMembroEquipe() {
		return membroEquipe;
	}

	public void setMembroEquipe(MembroProjeto membroEquipe) {
		this.membroEquipe = membroEquipe;
	}

	public boolean isCheckBuscaServidor() {
		return checkBuscaServidor;
	}

	public void setCheckBuscaServidor(boolean checkBuscaServidor) {
		this.checkBuscaServidor = checkBuscaServidor;
	}

	public boolean isCheckBuscaPeriodoConclusao() {
		return checkBuscaPeriodoConclusao;
	}

	public void setCheckBuscaPeriodoConclusao(boolean checkBuscaPeriodoConclusao) {
		this.checkBuscaPeriodoConclusao = checkBuscaPeriodoConclusao;
	}

	public Date getBuscaInicioConclusao() {
		return buscaInicioConclusao;
	}

	public void setBuscaInicioConclusao(Date buscaInicioConclusao) {
		this.buscaInicioConclusao = buscaInicioConclusao;
	}

	public Date getBuscaFimConclusao() {
		return buscaFimConclusao;
	}

	public void setBuscaFimConclusao(Date buscaFimConclusao) {
		this.buscaFimConclusao = buscaFimConclusao;
	}

	public boolean isBuscaTipoAcaoProduto() {
		return buscaTipoAcaoProduto;
	}

	public void setBuscaTipoAcaoProduto(boolean buscaTipoAcaoProduto) {
		this.buscaTipoAcaoProduto = buscaTipoAcaoProduto;
	}

	public boolean isBuscaTipoAcaoPrograma() {
		return buscaTipoAcaoPrograma;
	}

	public void setBuscaTipoAcaoPrograma(boolean buscaTipoAcaoPrograma) {
		this.buscaTipoAcaoPrograma = buscaTipoAcaoPrograma;
	}

	public String getCoordenacao() {
		return coordenacao;
	}

	public void setCoordenacao(String coordenacao) {
		this.coordenacao = coordenacao;
	}
	
	public boolean isExibeNovoRelatorioFinalExtensao() {
		return obj.getAtividade().getProjeto().getDataCadastro().after(ParametroHelper.getInstance()
				.getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA));
	}
	
	/**
	 * Preenche coleção com atividades pendentes quanto ao envio de relatórios(Usuário Logado)
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> getAtividadesPendentesRelatoriosCoordenador() throws DAOException {		
		if( ! ValidatorUtil.isEmpty(atividadesPendentesRelatoriosCoordenador)) {
				return atividadesPendentesRelatoriosCoordenador;
		}
		atividadesPendentesRelatoriosCoordenador = getDAO(RelatorioAcaoExtensaoDao.class).findAcosPendentesRelatorio(getUsuarioLogado().getPessoa());
		return atividadesPendentesRelatoriosCoordenador;
		
	}
	
	public void setAtividadesPendentesRelatoriosCoordenador(
			Collection<AtividadeExtensao> atividadesPendentesRelatoriosCoordenador) {
		this.atividadesPendentesRelatoriosCoordenador = atividadesPendentesRelatoriosCoordenador;
	}

	public boolean isTelaNotificacoes() {
		return telaNotificacoes;
	}

	public void setTelaNotificacoes(boolean telaNotificacoes) {
		this.telaNotificacoes = telaNotificacoes;
	}

	public TipoAtividadeExtensao getTipoAtividade() {
		return tipoAtividade;
	}

	public void setTipoAtividade(TipoAtividadeExtensao tipoAtividade) {
		this.tipoAtividade = tipoAtividade;
	}

}