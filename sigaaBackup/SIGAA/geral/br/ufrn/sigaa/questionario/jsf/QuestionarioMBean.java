/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 22/11/2007
 *
 */
package br.ufrn.sigaa.questionario.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.questionario.dominio.Alternativa;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;
import br.ufrn.sigaa.questionario.negocio.MovimentoQuestionario;
import br.ufrn.sigaa.questionario.negocio.QuestionarioValidation;

/**
 * MBean respons�vel por controlar o cadastro, atualiza��o e remo��o dos question�rios no sistema
 * @author Victor Hugo
 * @author �dipo Elder F. Melo
 *
 */
@Component("questionarioBean") @Scope("session")
public class QuestionarioMBean extends SigaaAbstractController<Questionario> {

	/** Pergunta que est� sendo cadastrada */
	private PerguntaQuestionario pergunta = new PerguntaQuestionario();
	/** Pegunta antiga selecionada */
	private PerguntaQuestionario oldPergunta = new PerguntaQuestionario();
	/** Alternativa cadastrada */
	private Alternativa alternativa = new Alternativa();
	/** Lista de alternativas do question�rio selecionado */
	private DataModel modelAlternativas;
	/** Lista de perguntas do question�rio selecionado */
	private DataModel modelPerguntas;

	/** perguntas que devem ser removidas do banco de dados */
	private List<PerguntaQuestionario> perguntasRemover;
	/** alternativas que devem ser removidas do banco de dados */
	private List<Alternativa> alternativasRemover;
	/** Indice do gabarito de perguntas de unica escolha */
	private Integer gabaritoUnicaEscolha;
	/** Label do bot�o de adicionar pergunta */
	private String confirmButtonPergunta = "Adicionar Pergunta";

	/** atributo utilizado para controlar se a a��o � alterar ou cadastrar nova pergunta
	 * necess�rio pois se a pergunta ainda n�o tiver sido persistida ela ter� id = 0 mesmo que seja altera��o */
	private boolean acaoAlterarPergunta = false;

	/** Tipo de question�rio que o usu�rio est� trabalhando */
	private TipoQuestionario tipoGerenciado;
	/** Lista de questionarios encontrados */
	private Collection<Questionario> questionarios;
	
	/** Lista com os Tipos de question�rio que o usu�rio poder� cadastrar */
	private Collection<TipoQuestionario> agrupadorTipoQuestionario;
	/** Construtor padr�o */
	public QuestionarioMBean() {
		initObj();
	}

	/**
	 * Inicializa o OBJ
	 */
	private void initObj(){
		obj = new Questionario();
		obj.setAtivo(true);
		obj.setPerguntas( new ArrayList<PerguntaQuestionario>() );
		pergunta = new PerguntaQuestionario();
		pergunta.setAlternativas( new ArrayList<Alternativa>() );
		alternativa = new Alternativa();
		modelAlternativas = new ListDataModel(pergunta.getAlternativas());
		modelPerguntas = new ListDataModel( obj.getPerguntas() );
		setConfirmButtonPergunta("Adicionar Pergunta");
		acaoAlterarPergunta = false;
		setConfirmButton("Cadastrar Question�rio");
	}

	/** SelectItem de Tipos de Pergunta */
	public Collection<SelectItem> getTiposPergunta(){
		Collection<SelectItem> tipos = new ArrayList<SelectItem>();
		tipos.add( new SelectItem( PerguntaQuestionario.UNICA_ESCOLHA, "�NICA ESCOLHA" ) );
		tipos.add( new SelectItem( PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO, "�NICA ESCOLHA COM PESO NA ALTERNATIVA" ) );		
		tipos.add( new SelectItem( PerguntaQuestionario.MULTIPLA_ESCOLHA, "M�LTIPLA ESCOLHA" ) );
		tipos.add( new SelectItem( PerguntaQuestionario.DISSERTATIVA, "DISSERTATIVA" ) );
		tipos.add( new SelectItem( PerguntaQuestionario.NUMERICA, "NUM�RICA" ) );
		tipos.add( new SelectItem( PerguntaQuestionario.VF, "VERDADEIRO OU FALSO" ) );
		tipos.add( new SelectItem( PerguntaQuestionario.ARQUIVO, "ARQUIVO" ) );
		return tipos;
	}

	/** 
	 * Caminho do diret�rio base
	 */
	@Override
	public String getDirBase() {
		return "/geral/questionario";
	}

	/**
	 * Caminho do formul�rio de cadastro
	 */
	@Override
	public String getFormPage() {
		return getDirBase() + "/dados_gerais.jsp";
	}

	/**
	 * Prepara o formul�rio para altera��o de um question�rio.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/questionario/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		initObj();
		super.atualizar();
		
		if (!obj.isAtivo()) {
			addMensagemErro("Este question�rio foi removido.");
			return listar();
		} else {
			return forward(getFormPage());
		}
	}

	/**
	 * Monta a listagem das perguntas para atualiza��o
	 * do question�rio.
	 * <br>
	 * N�o � chamado por JSP.
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_QUESTIONARIO);
		if( !isEmpty( obj.getPerguntas() ) ){
			for( PerguntaQuestionario p : obj.getPerguntas() ){
				if( p.isUnicaEscolha() || p.isMultiplaEscolha() || p.isUnicaEscolhaAlternativaPeso() )
					p.getAlternativas().iterator();
			}
		}else{
			obj.setPerguntas(new ArrayList<PerguntaQuestionario>());
		}
		modelPerguntas = new ListDataModel(obj.getPerguntas());
		setConfirmButtonPergunta("Adicionar Pergunta");
		setConfirmButton("Confirmar Altera��o");
	}

	/** Exibe o formul�rio de cadastro de dados gerais
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/questionario/pergunta.jsp</li>
	 * </ul> 	 * 
	 * @return {@link #getDirBase()} + /dados_gerais.jsf
	 */
	public String telaDadosGerais(){
		return forward( getDirBase() + "/dados_gerais.jsf" );
	}

	/**
	 * Exibe o formul�rio de cadastro de pergunta
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/questionario/pergunta.jsp</li>
	 * </ul> 
	 * @return {@link #getDirBase()} + /pergunta.jsf
	 */
	public String telaPergunta(){
		return forward( getDirBase() + "/pergunta.jsf" );
	}

	/** Exibe o question�rio para confirma��o do cadastro
	 * @return {@link #getDirBase()} + /questionario.jsf
	 */
	private String telaQuestionario(){
		return forward( getDirBase() + "/questionario.jsf" );
	}

	/** Exibe o formul�rio de "remo��o" do question�rio, que de fato ser� inativado.
	 * @return {@link #getDirBase()} + /questionario.jsf
	 */
	private String telaInativacao(){
		return forward( getDirBase() + "/inativacao.jsf" );
	}
	
	/** Exibe o resumo do question�rio
	 * @return {@link #getDirBase()} + /resumo.jsf
	 */
	private String telaResumo(){
		return forward( getDirBase() + "/resumo.jsf" );
	}

	/** Inicia a ger�ncia de question�rios do tipo Processo Seletivo
	 * <br>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/programa.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/menu_coordenador.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/curso.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/formacao_complementar/menus/curso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarProcessosSeletivos() throws ArqException {
		tipoGerenciado = getGenericDAO().findByPrimaryKey( TipoQuestionario.PROCESSO_SELETIVO , TipoQuestionario.class);
		return gerenciarComum();
	}
	
	/**
	 * Inicia a ger�ncia de question�rios do tipo Inscri��o Atividade
	 * 
	 * <br>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>M�todo n�o invocado por JSP</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarInscricaoAtividade() throws ArqException {
		tipoGerenciado = getGenericDAO().findByPrimaryKey( TipoQuestionario.QUESTIONARIO_INSCRICAO_ATIVIDADE , TipoQuestionario.class);
		return gerenciarComum();
	}
	
	/** Inicia a ger�ncia de question�rios do tipo S�cio Econ�mico
	 * <br>
	 * M�todo n�o invocado por JSP's.
	 * M�todo chamado pela(s) seguinte(s) classes:
	 * <ul>
	 * <li>/SIGAA/assistencia/br/ufrn/sigaa/assistencia/jsf/CadastroUnicoBolsaMBean.java</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarSocioEconomico() throws ArqException {
		tipoGerenciado = getGenericDAO().findByPrimaryKey(
				TipoQuestionario.QUESTIONARIO_SOCIO_ECONOMICO , TipoQuestionario.class);
		return gerenciarComum();
	}
	
	/** Inicia a ger�ncia de question�rios utilizados durante a inscri��o de candidatos para o vestibular.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/cadastros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarQuestionarioVestibular() throws ArqException {
		checkRole(SigaaPapeis.VESTIBULAR);
		tipoGerenciado = getGenericDAO().findByPrimaryKey(
				TipoQuestionario.QUESTIONARIO_VESTIBULAR , TipoQuestionario.class);
		return gerenciarComum();
	}

	/** Inicia a ger�ncia de question�rios utilizados pela PROEX para os coordenadores e participantes de uma a��o de extens�o.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarQuestionarioAcaoExtensao() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		tipoGerenciado = getGenericDAO().findByPrimaryKey(
				TipoQuestionario.QUESTIONARIO_ACAO_EXTENSAO, TipoQuestionario.class);
		return gerenciarComum();
	}
	
	/**
	 * Inicia o cadastro de question�rio de est�gio
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/estagio/modulo/geral.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroEstagio() throws ArqException{
		iniciarQuestionarioEstagio();
		return iniciarCadastro();
	}	
	
	/** Inicia a ger�ncia de question�rios utilizados no Relat�rio de Est�gios.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/estagio/modulo/geral.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarQuestionarioEstagio() throws ArqException {
		if ( tipoGerenciado == null || !tipoGerenciado.isAcaoExtensao() )
			iniciarQuestionarioEstagio();
		return listar();
	}
	
	/** Inicia a ger�ncia de question�rios utilizados na Auto Avalia��o do Stricto Sensu.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menus/cadastro.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarQuestionarioAutoAvaliacaoStricto() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_STRICTO);
		tipoGerenciado = getGenericDAO().findByPrimaryKey(TipoQuestionario.AUTO_AVALIACAO_STRICTO_SENSU, TipoQuestionario.class);
		return gerenciarComum();
	}
	
	/** Inicia a ger�ncia de question�rios utilizados na Auto Avalia��o do Stricto Sensu.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menus/cadastro.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarQuestionarioAutoAvaliacaoLato() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_STRICTO, SigaaPapeis.GESTOR_LATO);
		tipoGerenciado = getGenericDAO().findByPrimaryKey(TipoQuestionario.AUTO_AVALIACAO_LATO_SENSU, TipoQuestionario.class);
		return gerenciarComum();
	}

	/**
	 * Inicia o question�rio de est�gio.
	 * 
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	private void iniciarQuestionarioEstagio() throws SegurancaException,
			DAOException {
		checkRole(SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN);
		
		QuestionarioDao dao = getDAO(QuestionarioDao.class);
		try {
			agrupadorTipoQuestionario = dao.findTiposById(TipoQuestionario.RELATORIO_DE_ESTAGIO_DISCENTE, 
					TipoQuestionario.RELATORIO_DE_ESTAGIO_SUPERVISOR,
					TipoQuestionario.RELATORIO_DE_ORIENTADOR_DE_ESTAGIO);
			
			initObj();
			obj.setUnidade(null);	
			tipoGerenciado = new TipoQuestionario();
			agrupadorTipoQuestionario = new LinkedList<TipoQuestionario>();
			agrupadorTipoQuestionario.add(dao.findByPrimaryKey(TipoQuestionario.RELATORIO_DE_ESTAGIO_DISCENTE, TipoQuestionario.class));
			agrupadorTipoQuestionario.add(dao.findByPrimaryKey(TipoQuestionario.RELATORIO_DE_ORIENTADOR_DE_ESTAGIO, TipoQuestionario.class));
			agrupadorTipoQuestionario.add(dao.findByPrimaryKey(TipoQuestionario.RELATORIO_DE_ESTAGIO_SUPERVISOR, TipoQuestionario.class));

		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Retorna os Tipos de Question�rio em formato de Combobox 
	 * <br>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/questionario/_dados_gerais.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getTiposQuestionario(){
		return toSelectItems(agrupadorTipoQuestionario, "id", "descricao");
	}
	
	/**
	 * Seleciona o tipo do questionario gerenciado
	 * <br>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/questionario/_dados_gerais.jsp</li>
	 * </ul>
	 */
	public void selecionaTipoGerenciado(ValueChangeEvent e) throws DAOException, SegurancaException {
		if((Integer)e.getNewValue() != 0){
			tipoGerenciado = getGenericDAO().findByPrimaryKey((Integer)e.getNewValue(), TipoQuestionario.class);			
			obj.setTipo(tipoGerenciado);
		}
	}		

	/** Opera��es comuns de inicializa��o aos m�todos {@link #gerenciarProcessosSeletivos()} e {@link #gerenciarSocioEconomico()}: 
	 * @return @see #listar()
	 * @throws ArqException
	 */
	private String gerenciarComum() throws ArqException {
		initObj();
		// Definir a unidade de trabalho
		if (tipoGerenciado.isQuestionarioVestibular()) {
			obj.setUnidade(null);
		} else if(tipoGerenciado.isQuestionarioInscricaoAtividade()) {
			obj.setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());
		} else {
			Unidade unidade = NivelEnsino.isAlgumNivelStricto( getNivelEnsino() ) ? getProgramaStricto() : getUsuarioLogado().getVinculoAtivo().getUnidade();
			obj.setUnidade(unidade);
		}
		return listar();
	}
	
	/**
	 * Lista todos os question�rios do processo seletivo.
	 * <br>
	 * N�o invocado por JSP
 	 */
	@Override
	public String listar() throws ArqException {
		questionarios = popularListaQuestionarios();
		return super.listar();
	}
	
	/**
	 * Popular a lista de question�rios cadastrados de acordo com 
	 * o tipo de question�rios que est� sendo gerenciado
	 * N�o invocado por JSP
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public Collection<Questionario> popularListaQuestionarios() throws HibernateException, DAOException, SegurancaException {
		Collection<Questionario> questionarios = new ArrayList<Questionario>();
		QuestionarioDao questionarioDao = getDAO(QuestionarioDao.class);
		
		if (!isEmpty(agrupadorTipoQuestionario)){
			TipoQuestionario tipo = agrupadorTipoQuestionario.iterator().next();
			if (tipo.isQuestionarioRelatorioEstagio()){
				checkRole(SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN);
				questionarios = questionarioDao.findByTipos( TipoQuestionario.RELATORIO_DE_ESTAGIO_DISCENTE,
						TipoQuestionario.RELATORIO_DE_ESTAGIO_SUPERVISOR, TipoQuestionario.RELATORIO_DE_ORIENTADOR_DE_ESTAGIO );		
			}			
		} else {
			// Buscar question�rios de processos seletivos
			if ( tipoGerenciado.isProcessoSeletivo() ) {
				questionarios = questionarioDao.findQuestionariosProcessosSeletivos(obj.getUnidade());
			} else if (tipoGerenciado.isQuestionarioInscricaoAtividade()) {
				questionarios = questionarioDao.findQuestionariosInscricaoAtividadeByUnidade(obj.getUnidade());
			} else if (tipoGerenciado.isSocioEconomico()) {
				questionarios = questionarioDao.findByTipo(tipoGerenciado.getId());
			} else if (tipoGerenciado.isQuestionarioVestibular()) {
				checkRole(SigaaPapeis.VESTIBULAR);
				questionarios = questionarioDao.findByTipo(tipoGerenciado.getId());
			} else if ( tipoGerenciado.isAcaoExtensao() ) {
				checkRole(SigaaPapeis.GESTOR_EXTENSAO);
				questionarios = questionarioDao.findByTipo(tipoGerenciado.getId());
			} else if ( tipoGerenciado.isAutoAvaliacaoStrictoSensu() ) {
				checkRole(SigaaPapeis.ADMINISTRADOR_STRICTO);
				questionarios = questionarioDao.findByTipo(tipoGerenciado.getId());
			} else if ( tipoGerenciado.isAutoAvaliacaoLatoSensu() ) {
				checkRole(SigaaPapeis.GESTOR_LATO);
				questionarios = questionarioDao.findByTipo(tipoGerenciado.getId());
			}
		}
		
		return questionarios;
	}
	
	/**
	 * Inicia o caso de uso de cadastro de um question�rio
	 * <br>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/sae/cadastro_unico/lista.jsp</li>
	 * <li>/sigaa.war/sae/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastro() throws ArqException{
		
		// Salvando o tipo de trabalho atual
		TipoQuestionario tipo = tipoGerenciado;
		Unidade unidade = obj.getUnidade();
		
		/** Limpando dados do MBean */
		initObj();
		
		setTipoGerenciado(tipo);
		obj.setUnidade(unidade);
		
		if (!isEmpty(agrupadorTipoQuestionario))
			obj.setTipo(new TipoQuestionario());
		else
			obj.setTipo(tipoGerenciado);
		
		prepareMovimento(SigaaListaComando.CADASTRAR_QUESTIONARIO);
		return telaDadosGerais();
	}
	
	/**
	 * Inicia o caso de uso de cadastro de um question�rio
	 * <br>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroFichaExtensao() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		tipoGerenciado = getGenericDAO().findByPrimaryKey(
				TipoQuestionario.QUESTIONARIO_ACAO_EXTENSAO, TipoQuestionario.class);
		return iniciarCadastro();
	}
	
	/** 
	 * Valida os dados gerais do question�rio
	 * <br>
 	 *M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/geral/questionario/dados_gerais.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String submeterDadosGerais() throws HibernateException, DAOException{
		if (!isEmpty(agrupadorTipoQuestionario))
			tipoGerenciado = obj.getTipo();
		
		ListaMensagens erros = obj.validate();
		// verifica se h� outro relat�rio com o mesmo nome
		if (obj.isRelatorioEstagio()) {
			QuestionarioDao dao = getDAO(QuestionarioDao.class);
			dao.initialize(obj.getTipo());
			Collection<Questionario> outros = dao.findByTipos(obj.getTipo().getId());
			if (!isEmpty(outros)) {
				for (Questionario outro : outros) {
					if (outro.getId() != obj.getId()) { 
						erros.addErro("Existe um question�rio do tipo "+obj.getTipo().getDescricao() + " cadastrado. Por favor, remova o question�rio cadastrado antes de cadastrar outro do mesmo tipo.");
						break;
					}
				}
			}
		}
		if( !erros.isEmpty() ){
			addMensagens(erros);
			return null;
		}
		if( obj.getId() == 0 )
			return telaPergunta();
		else
			return telaQuestionario();
	}

	/**
	 * Vai para a tela de adi��o de pergunta
	 * <br>
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarAdicionarPergunta() throws DAOException{
		ListaMensagens lista = new ListaMensagens();
		QuestionarioValidation.validacaoSolicitacaoBolsa(obj, lista);
		if ( !lista.isEmpty() ) {
			addMensagens(lista);
			return null;
		}
		pergunta = new PerguntaQuestionario();
		pergunta.setAlternativas( new ArrayList<Alternativa>() );
		modelAlternativas = new ListDataModel( pergunta.getAlternativas() );
		setConfirmButtonPergunta("Adicionar Pergunta");
		acaoAlterarPergunta = false;
		return telaPergunta();
	}

	/**
	 * Adiciona/altera uma pergunta ao/do question�rio
	 * <br>
	 * JSP:
	 * <ul>
	 * <li> sigaa.war/geral/questionario/pergunta.jsp,</li>
	 * <li>/sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String adicionarPergunta(){

		ListaMensagens erros = pergunta.validate();
		if( !erros.isEmpty() ){
			addMensagens(erros);
			return null;
		}

		/*
		 * se for de m�ltipla ou �nica escolha deve setar o gabarito
		 */
		if( pergunta.isMultiplaOuUnicaEscolha() ){

			if( (pergunta.isUnicaEscolha() || pergunta.isUnicaEscolhaAlternativaPeso()) && !isEmpty(gabaritoUnicaEscolha) ){
				for( Alternativa a : pergunta.getAlternativas() ){
					if( gabaritoUnicaEscolha.equals( a.getOrdem() ) )
							a.setGabarito(true);
					else
						a.setGabarito(false);
				}
			}

			if( pergunta.isMultiplaEscolha() ){
				for( Alternativa a : pergunta.getAlternativas() ){
					if( a.isSelecionado() )
						a.setGabarito(true);
					else
						a.setGabarito(false);
				}
			}

		}

		if( obj.isPossuiDefinicaoGabarito() && !pergunta.hasGabarito() ){
			addMensagemErro("Informe o gabarito da pergunta.");
			return null;
		}

		pergunta.atualizarOrdemAlternativas();
		if( acaoAlterarPergunta ){ /** se esta alterando a pergunta */
			obj.getPerguntas().remove( pergunta.getOrdem() - 1 );
			obj.getPerguntas().add(pergunta.getOrdem() - 1, pergunta);
		}else{
			pergunta.setQuestionario(obj);
			obj.getPerguntas().add( pergunta );
		}
		obj.atualizarOrdemPerguntas();
		int tipoAtual = pergunta.getTipo();
		pergunta = new PerguntaQuestionario();
		pergunta.setTipo(tipoAtual);
		alternativa = new Alternativa();
		pergunta.setAlternativas( new ArrayList<Alternativa>() );
		modelAlternativas = new ListDataModel( pergunta.getAlternativas() ) ;
		modelPerguntas = new ListDataModel( obj.getPerguntas() );
		setConfirmButtonPergunta("Adicionar Pergunta");
		if( acaoAlterarPergunta ){
			acaoAlterarPergunta = false;
			addMensagemInformation("Pergunta alterada com sucesso!");
			return telaQuestionario();
		} else{
			addMensagemInformation("Pergunta adicionada com sucesso!");
			return telaPergunta();
		}
	}

	/**
	 * Remove uma pergunta do question�rio
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> JSP: /sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public void removerPergunta(ActionEvent evt) throws DAOException{
		ListaMensagens lista = new ListaMensagens();
		QuestionarioValidation.validacaoSolicitacaoBolsa(obj, lista);
		if ( !lista.isEmpty() ) {
			addMensagens(lista);
			redirectMesmaPagina();
		} else {
			//�ndice da alternativa que sera removida
			int indice = modelPerguntas.getRowIndex();
			PerguntaQuestionario perguntaRemovida = obj.getPerguntas().remove(indice);
			modelPerguntas = new ListDataModel( obj.getPerguntas() );
			if( perguntaRemovida.getId() > 0 ){
				/*
				 * se a pergunta j� tiver sido persistida alguma vez tem que ser adicionada 
				 * em uma cole��o para que seja inativada no banco
				 */
				if( perguntasRemover == null )
					perguntasRemover = new ArrayList<PerguntaQuestionario>();
				perguntasRemover.add( perguntaRemovida );
			}
		}
	}

	/**
	 * Altera uma pergunta do question�rio
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String alterarPergunta() throws DAOException{
		ListaMensagens lista = new ListaMensagens();
		QuestionarioValidation.validacaoSolicitacaoBolsa(obj, lista);
		if ( !lista.isEmpty() ) {
			addMensagens(lista);
			return null;
		}
		
		obj.atualizarOrdemPerguntas();
		pergunta = (PerguntaQuestionario) modelPerguntas.getRowData();
		oldPergunta = pergunta.clone();
		
		if( pergunta.isMultiplaEscolha() || pergunta.isUnicaEscolha() || pergunta.isUnicaEscolhaAlternativaPeso() ){
			modelAlternativas = new ListDataModel( pergunta.getAlternativas() );
		}
		setConfirmButtonPergunta("Alterar Pergunta");
		acaoAlterarPergunta = true;
		return telaPergunta();
	}

	/**
	 * Redireciona o usu�rio para o resumo do question�rio. 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/pergunta.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 */
	@Override
	public String cancelar() {
		try {
			return gerenciarComum();
		} catch (ArqException e) {
			notifyError(e);
			addMensagemErroPadrao();
		}
		return null;
	}
	
	/**
	 * Redireciona o usu�rio para o resumo do question�rio. 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/pergunta.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 */
	public String cancelarAdicionarPergunta(){
		desfazerAlterecaoPergunta();
		
		return telaQuestionario();
	}

	/**
	 * Remove todas as perguntas adicionadas � lista de um question�rio, caso o usu�rio tenha clicado
	 * no bot�o 'Resumo do Question�rio' sem antes ter clicado no bot�o cadastrar perguntas.
	 */
	private void desfazerAlterecaoPergunta() {
		pergunta.setPerguntaQuestionario(oldPergunta);
		
		if (alternativasRemover != null) {
			for (Alternativa a : pergunta.getAlternativas()) {
				if (alternativasRemover.contains(a)) {
					alternativasRemover.remove(a);
				}
			}
		}
		
		alternativa = new Alternativa();
		modelAlternativas = new ListDataModel(pergunta.getAlternativas());
	}

	/**
	 * Incrementa a ordem de uma pergunta, o atributo ordem define a ordem de exibi��o das perguntas do formul�rio.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 */
	public void movePerguntaCima(ActionEvent evt) {
		//�ndice da alternativa que sera movida
		int indice = modelPerguntas.getRowIndex();

		if( indice != 0 ){
			PerguntaQuestionario perg = obj.getPerguntas().get(indice);
			obj.getPerguntas().remove(indice);
			obj.getPerguntas().add(indice - 1, perg);
			modelPerguntas = new ListDataModel( obj.getPerguntas() );
		}
	}

	/**
	 * Decrementa a ordem de uma pergunta, o atributo ordem define a ordem de exibi��o das perguntas do formul�rio.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 */
	public void movePerguntaBaixo(ActionEvent evt) {
		//�ndice da alternativa que sera movida
		int indice = modelPerguntas.getRowIndex();

		if( indice < obj.getPerguntas().size() -1 ){
			PerguntaQuestionario perg = obj.getPerguntas().get(indice);
			obj.getPerguntas().remove(indice);
			obj.getPerguntas().add(indice + 1, perg);
			modelPerguntas = new ListDataModel( obj.getPerguntas() );
		}
	}

	/**
	 * Adiciona uma alternativa da pergunta ao question�rio.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/pergunta.jsp</li>
	 * <ul>
	 * 
	 * @return
	 */
	public void adicionarAlternativa(ActionEvent evt){
		
		ListaMensagens erros = new ListaMensagens(); 
		
		if (pergunta.getTipo() == PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO) {
			if (alternativa.getPeso() == null) 
				erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Peso");
		}
		
		if( isEmpty(alternativa.getAlternativa()) )
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Alternativa");
		
		 if (!erros.isEmpty()){
			 addMensagensAjax(erros);
			 return;
		 }		
		
		alternativa.setPergunta(pergunta);
		pergunta.getAlternativas().add(alternativa);
		pergunta.atualizarOrdemAlternativas();
		alternativa = new Alternativa();
	}

	/**
	 * Remove uma alternativa da pergunta do formul�rio.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/pergunta.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public void removerAlternativa(ActionEvent evt){
		//�ndice da alternativa que sera removida
		int indice = modelAlternativas.getRowIndex();
		Alternativa alternativaRemovida = pergunta.getAlternativas().remove(indice);
		if( alternativaRemovida.getId() > 0 ){
			if( alternativasRemover == null )
				alternativasRemover = new ArrayList<Alternativa>();
			alternativasRemover.add(alternativaRemovida);
		}
	}

	/**
	 * Move a alternativa para cima na ordem de exibi��o no formul�rio.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/pergunta.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 */
	public void moveAlternativaCima(ActionEvent evt) {
		//�ndice da alternativa que sera movida
		int indice = modelAlternativas.getRowIndex();

		if( indice != 0 ){
			Alternativa alter = pergunta.getAlternativas().get(indice);
			pergunta.getAlternativas().remove(indice);
			pergunta.getAlternativas().add(indice - 1, alter);
		}
	}

	/**
	 * Move a alternativa para baixo na ordem de exibi��o no formul�rio.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> JSP: /sigaa.ear/sigaa.war/geral/questionario/pergunta.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 */
	public void moveAlternativaBaixo(ActionEvent evt) {
		//�ndice da alternativa que ser� movida
		int indice = modelAlternativas.getRowIndex();

		if( indice < pergunta.getAlternativas().size() - 1 ){
			Alternativa alter = pergunta.getAlternativas().get(indice);
			pergunta.getAlternativas().remove(indice);
			pergunta.getAlternativas().add(indice + 1, alter);
		}

	}
	
	/**
	 * Cadastra um question�rio.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) classes(s)/JSP(s):
	 * <ul>
	 * <li>SIGAA/graduacao/br/ufrn/sigaa/ensino/graduacao/jsf/ParticipacaoEnadeMBean.java</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws ArqException {

		MovimentoQuestionario mov = new MovimentoQuestionario();
		mov.setObjMovimentado( obj );

		mov.setCodMovimento( SigaaListaComando.CADASTRAR_QUESTIONARIO );
		mov.setPerguntasRemover(perguntasRemover);
		mov.setAlternativasRemover(alternativasRemover);

		String msgConfirmacao = "Question�rio cadastrado com sucesso!";
		if( obj.getId() > 0 ) {
			msgConfirmacao = "Question�rio atualizado com sucesso!";
		}

		try {
			execute(mov);
			addMensagemInformation(msgConfirmacao);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}

		return listar();
	}
	
	/**
	 * Prepara formul�rio do question�rio para remo��o.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/questionario/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String preRemover() {
		try {
			prepareMovimento(SigaaListaComando.REMOVER_QUESTIONARIO);
		} catch (ArqException ex) {
			tratamentoErroPadrao(ex, "N�o foi poss�vel remover o question�rio.");
		}
		
		try {
			popularQuestionario();
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		
		if (!obj.isAtivo()) {
			addMensagemErro("Question�rio j� foi removido.");
			return null;
		}

		setConfirmButton("Remover");

		return telaInativacao();
	}

	/** Carrega os dados do question�rio e as perguntas
	 * @throws DAOException
	 */
	private void popularQuestionario() throws DAOException {
		populateObj(true);
		popularPerguntas();
	}

	/** 
	 * Carrega as perguntas do question�rio
	 * N�o invocado por JSP
	 */
	public void popularPerguntas() {
		for( PerguntaQuestionario p : obj.getPerguntas() ){
			if( p.isMultiplaOuUnicaEscolha() )
				p.getAlternativas().iterator();
			
			if( p.isUnicaEscolha() || p.isMultiplaOuUnicaEscolha() ){
				for(Alternativa a : p.getAlternativas()){
					if( a.isGabarito() )
						a.setSelecionado(true);
				}
			}
		}
		modelPerguntas = new ListDataModel( obj.getPerguntas() );
	}

	/**
	 * Visualizar question�rio
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/resumo.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException {
		popularQuestionario();
		return telaResumo();

	}
	
	/**
	 * Remove um question�rio.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/questionario/inativacao.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		MovimentoQuestionario mov = new MovimentoQuestionario();
		mov.setObjMovimentado( obj );
		mov.setCodMovimento( SigaaListaComando.REMOVER_QUESTIONARIO );

		try {
			execute(mov);
			addMensagemInformation("Question�rio removido com sucesso!");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}

		return listar();
	}

	public PerguntaQuestionario getPergunta() {
		return pergunta;
	}

	public void setPergunta(PerguntaQuestionario pergunta) {
		this.pergunta = pergunta;
	}

	public Alternativa getAlternativa() {
		return alternativa;
	}

	public void setAlternativa(Alternativa alternativa) {
		this.alternativa = alternativa;
	}

	public DataModel getModelAlternativas() {
		return modelAlternativas;
	}

	public void setModelAlternativas(DataModel modelAlternativas) {
		this.modelAlternativas = modelAlternativas;
	}

	public DataModel getModelPerguntas() {
		return modelPerguntas;
	}

	public void setModelPerguntas(DataModel modelPerguntas) {
		this.modelPerguntas = modelPerguntas;
	}

	public void setConfirmButtonPergunta(String confirmButtonPergunta) {
		this.confirmButtonPergunta = confirmButtonPergunta;
	}

	public String getConfirmButtonPergunta() {
		return confirmButtonPergunta;
	}

	public Integer getGabaritoUnicaEscolha() {
		return gabaritoUnicaEscolha;
	}

	public void setGabaritoUnicaEscolha(Integer gabaritoUnicaEscolha) {
		this.gabaritoUnicaEscolha = gabaritoUnicaEscolha;
	}

	public TipoQuestionario getTipoGerenciado() {
		return tipoGerenciado;
	}

	public void setTipoGerenciado(TipoQuestionario tipoGerenciado) {
		this.tipoGerenciado = tipoGerenciado;
	}

	public Collection<Questionario> getQuestionarios() {
		return questionarios;
	}

	public void setQuestionarios(Collection<Questionario> questionarios) {
		this.questionarios = questionarios;
	}

}