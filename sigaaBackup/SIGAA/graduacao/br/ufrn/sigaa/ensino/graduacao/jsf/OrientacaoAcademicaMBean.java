/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 09/07/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.notificacoes.Notificacao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.OrientacaoMatriculaDao;
import br.ufrn.sigaa.arq.dao.ensino.SolicitacaoTrancamentoMatriculaDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.EquipeProgramaDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.monitoria.EquipeDocenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.OrientacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MensagemOrientacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.negocio.OrientacaoAcademicaValidator;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean respons�vel pelo controle das orienta��es acad�micas (cadastro, remo��o, atualiza��o)
 * @author Victor Hugo
 */
@Component("orientacaoAcademica")
@Scope("session")
public class OrientacaoAcademicaMBean extends SigaaAbstractController<OrientacaoAcademica> implements OperadorDiscente{


	/** Define o link para o formul�rio de defini��o do orientador acad�mico. */
	public static final String JSP_ORIENTADOR = "/graduacao/orientacao_academica/orientador.jsp";
	/** Define o link para o formul�rio para a busca de orienta��es acad�micas. */
	public static final String JSP_BUSCA = "/graduacao/orientacao_academica/busca.jsp";
	/** Define o link para o formul�rio de dados da orienta��o acad�mica. */
	public static final String JSP_DADOS_ORIENTACAO = "/graduacao/orientacao_academica/form.jsp";
	/** Define o link para o formul�rio de lista de orienta��es acad�mica. */
	public static final String JSP_LISTA = "/graduacao/orientacao_academica/lista.jsp";

	/** Define se o usu�rio busca por docentes do programa de p�s gradua��o. */
	public static final int DOCENTE_PROGRAMA = 1;
	/** Define se o usu�rio busca por docentes externos ao programa. */
	public static final int DOCENTE_EXTERNO_PROGRAMA = 2;

	/** Indica se � permitido realizar a opera��o de cadastro. Uso excluso de Stricto-Sensu. **/
	public static final int CADASTRAR = 1;
	/** Indica se � permitido realizar a opera��o de atualiza��o. Uso excluso de Stricto-Sensu. **/
	public static final int ATUALIZAR = 2;
	/** Indica se � permitido realizar a opera��o de finaliza��o da orietan��o. Uso excluso de Stricto-Sensu. **/
	public static final int FINALIZAR = 3;
	/** Indica se � permitido realizar a opera��o de cencelamento da orienta��o. Uso excluso de Stricto-Sensu. **/
	public static final int CANCELAR = 4;
	/** Define a opera��o o valor indicativo da opera��o de envio de e-mail para o orientando. Uso excluso de Stricto-Sensu. **/
	public static final int ENVIAR_EMAIL = 5;
	
	/** Opera��o a realizar pelo controller.*/
	public int operacao = 0;
	
	/** ID da orienta��o acad�mica tratada na opera��o. */
	public int id=0;
	
	/** Corpo do e-mail a enviar. */
	public static String email;
	
	/** Orientador acad�mico */
	private Servidor orientador = new Servidor();

	/** Mensagem de notifica��o a ser enviada. */
	private Notificacao notificacao = new Notificacao(); 
	
	/** Resultado da busca por discentes. */
	private Collection<Discente> resultadoBusca = new HashSet<Discente>();

	/** Ano de ingresso utilizado para buscar discentes. */
	private Integer anoIngresso;
	
	/** Per�odo de ingresso utilizado para buscar discentes. */
	private Integer periodoIngresso;

	/** Total de orientados ativos.*/
	private int totalOrientandos;

	/** Discente utilizado na busca. */
	private DiscenteAdapter discenteBusca;

	/** Cole��o de orienta��es acad�micas, utilizada na busca/altera��o/remo��o. */
	private Collection<OrientacaoAcademica> lista = new ArrayList<OrientacaoAcademica>();
	
	/** Dados referentes ao orientando. */
	private List<Map<String,Object>> envio = new ArrayList<Map<String, Object>>();
	
	/** Cole��o de orienta��es acad�micas. */
	private Collection<OrientacaoAcademica> orientacoes = new ArrayList<OrientacaoAcademica>();

	/** Docente orientador da Equipe do Programa de P�s-Gradua��o. */
	private EquipePrograma equipe = new EquipePrograma();

	/** ID do docente orientador. */
	private int idDocente;
	
	/** Define o tipo de busca por docente.*/
	private int tipoBuscaDocente;
	
	/** Filtro da matr�cula do discente utilizado na busca.  **/
	private boolean chkMatricula = false;
	/** Filtro do nome do discente utilizado na busca.  **/
	private boolean chkNome = false;
	/** Filtro do ano/per�odo de ingresso utilizado na busca.  **/
	private boolean chkAnoIngresso = false;
	/** Filtro que permite a busca dos discente que n�o possuem orienta��o.  **/
	private boolean chkSemOrientacao = false;

	/** Orienta��o utilizada no cadastro de orienta��o para p�s gradua��o, para adicionar na lista. */
	private OrientacaoAcademica orientacao = new OrientacaoAcademica();

	/**
	 * Indica se a opera��o � lista de orienta��es ou ger�ncia de orienta��es.
	 * Como o formul�rio de lista das orienta��es (do orientador acad�mico) e
	 * ger�ncias das orienta��es (do coordenador de gradua��o) s�o o mesmo, este
	 * atributo controla se a opera��o de listar ou gerenciar. Se for ger�ncia
	 * deve exibir o bot�o para remover e o formul�rio de consulta.
	 */
	private boolean gerenciarOrientacoes = false;

	/** Lista de orienta��es de matr�culas dadas � um discente.*/
	private Collection<OrientacaoMatricula> orientacoesMatricula;
	
	/** Lista de orienta��es de trancamentos dadas � um discente.*/
	private Collection<SolicitacaoTrancamentoMatricula> solicitacoesTrancamento;
	
	/** Construtor padr�o. */
	public OrientacaoAcademicaMBean() {
		super();
	}

	/**
	 * Inicializa o objeto gerenciado pelo MBean.
	 */
	private void init(){
		orientacao = new OrientacaoAcademica();
		orientacao.setServidor( new Servidor() );
		orientacao.setDocenteExterno( new DocenteExterno() );
		orientacao.setDiscente( new Discente() );		
		equipe = new EquipePrograma();
		idDocente = 0;

		if(ValidatorUtil.isEmpty(orientacoes))
			orientacoes = new ArrayList<OrientacaoAcademica>();
		
		gerenciarOrientacoes = false;
		lista = null;
		orientador = new Servidor();
		notificacao = new Notificacao();
	}

	/**
	 * Inicia o caso de uso de cadastro de orienta��o acad�mica realizado pelo
	 * coordenador de curso. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/menus/discente.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.SECRETARIA_POS , 
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO);
		init();
		prepareMovimento(SigaaListaComando.CADASTRAR_ORIENTACAO_ACADEMICA);
		if( getAcessoMenu().isAlgumUsuarioStricto() ){
			BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CADASTRAR_ORIENTADOR_POS);
			return buscaDiscenteMBean.popular();
		}
		return forward(JSP_ORIENTADOR);
	}

	/**
	 * Seleciona um orientador quando n�o � um secret�rio de p�s. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String selecionarOrientador() throws DAOException, SegurancaException{

		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);

		if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO ) ){
			 // Se n�o for secret�rio de p�s
			if( orientador == null || orientador.getId() == 0 ){
				addMensagemErro("Selecione um docente para cadastrar seus orientandos.");
				return null;
			}

			orientador = dao.findByPrimaryKey(orientador.getId(), Servidor.class);

			ListaMensagens lista = new ListaMensagens();
			OrientacaoAcademicaValidator.validaMaximoOrientacoesAcademicas(getOrientador(), null, lista);
			if( !lista.isEmpty() ){
				addMensagens(lista);
				return null;
			}
			discenteBusca = new Discente();
			totalOrientandos = dao.findTotalOrientandosAtivos( orientador.getId(), OrientacaoAcademica.ACADEMICO );
		} else if( isUserInRole( SigaaPapeis.SECRETARIA_POS , SigaaPapeis.COORDENADOR_CURSO_STRICTO ) ){
			 // Se for secret�rio de p�s
			if( equipe == null || equipe.getId() == 0 ){
				addMensagemErro("Selecione um docente para cadastrar seus orientandos.");
				return null;
			}

			equipe = dao.findByPrimaryKey(equipe.getId(), EquipePrograma.class);
			totalOrientandos = dao.findTotalOrientandosAtivos( equipe.getServidor().getId(), OrientacaoAcademica.ORIENTADOR );
			orientador = equipe.getServidor();
			discenteBusca = new Discente();
		} else{
			throw new SegurancaException("Usu�rio n�o autorizado.");
		}
		return telaBusca();
	}

	/**
	 * Adiciona os discentes selecionados a lista para criar a orienta��o
	 * acad�mica <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String adicionarDiscentes() throws DAOException{

		char tipoOrientacao;

		String[] selecionados = getParameterValues("selecionados");
		Collection<OrientacaoAcademica> orientacoesSelecionadas = new ArrayList<OrientacaoAcademica>();

		if (selecionados == null) {
			addMensagemErro("� necess�rio selecionar no m�nimo um discente.");
			return null;
		}

		OrientacaoAcademicaDao dao = getDAO( OrientacaoAcademicaDao.class);
		forExterno: for (int i = 0; i < selecionados.length; i++) {
			int id = Integer.parseInt(selecionados[i]);

			Discente d = dao.findByPrimaryKey(id, Discente.class, "id", "pessoa.nome", "matricula", "status", "nivel");

			 // Verificando se este discente j� foi adicionado a lista de discente para cadastrar as orienta��es
			for( OrientacaoAcademica jaAdicionado : orientacoes  ){
				if( jaAdicionado.getDiscente().getId() == d.getId() ){
					addMessage( d.getNome() +  " j� foi adicionado a lista de orientandos e n�o ser� novamente.", TipoMensagemUFRN.WARNING);
					continue forExterno;
				}
			}

			
			List<OrientacaoAcademica> jaExisteOrientacao = dao.findByDiscenteAndOrientador(d.getId(), orientador.getId());
			if (jaExisteOrientacao != null && !jaExisteOrientacao.isEmpty()) {
				addMessage( d.getNome() +  " n�o foi adicionado porque j� � orientando de: " + orientador.getNome(), TipoMensagemUFRN.WARNING);
				continue forExterno;
			}
			
			OrientacaoAcademica orientacao = new OrientacaoAcademica();
			orientacao.setDiscente( d );
			orientacao.setServidor( orientador );
//			orientacao.setTipoOrientacao( TeseOrientada.ORIENTADOR );

			if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS ) ){
				String tipo = getParameter("tipo_" + id);
				if( tipo == null || tipo.length() == 0 ){
					addMensagemErro("Selecione o tipo de orienta��o para todos os alunos selecionados.");
					return null;
				}
				tipoOrientacao = tipo.charAt(0);

			} else {
				// Se for gradua��o
				// A = ACADEMICA
				tipoOrientacao = OrientacaoAcademica.ACADEMICO;
			}

			orientacao.setTipoOrientacao( tipoOrientacao );
			orientacoesSelecionadas.add( orientacao );
		}

		ListaMensagens lista = new ListaMensagens();
		OrientacaoAcademicaValidator.validaOrientacoes(orientacoesSelecionadas, lista);
		if( !lista.isEmpty() ){
			addMensagens(lista);
			return null;
		}

		orientacoes.addAll( orientacoesSelecionadas );

		if (orientacoes.isEmpty()) {
			return null;
		}
		
		return telaDadosOrientacao();
	}

	/**
	 * Remove o aluno da orienta��o do professor. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String removerDiscente(){

		Integer id = getParameterInt("id");
		if( id == null || id == 0 ){
			addMensagemErro("Selecione um discente.");
			return null;
		}

		Discente d = new Discente(id);
		//GenericDAO dao = getGenericDAO();
		//OrientacaoAcademica orientacaoMarcada = dao.findByPrimaryKey(id, OrientacaoAcademica.class);

		for (Iterator<OrientacaoAcademica> iter = orientacoes.iterator(); iter.hasNext();) {
			OrientacaoAcademica oa = iter.next();
			if( oa.getDiscente().getId() == d.getId() ){
				iter.remove();
			}
		}

		return telaDadosOrientacao();
	}

	/**
	 * Busca os discentes para adicionar como orientandos de um professor. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String buscarDiscente() throws ArqException{

		Long matricula = null;
		String nome = null;

		DiscenteDao dao = getDAO( DiscenteDao.class );
		// Verificar os crit�rios de busca utilizados
		resultadoBusca = new ArrayList<Discente>();

		if(isChkMatricula()){	
				
			if( discenteBusca.getMatricula() == null || discenteBusca.getMatricula() == 0 ){
				addMensagemErro("Informe a Matr�cula.");
				return null;
			}
			matricula = discenteBusca.getMatricula();

			Discente d = null;
			if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) ){
				d = dao.findAtivosByMatriculaPrograma(matricula, getProgramaStricto().getId(), null );
			}else if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO ) ){
				// quando n�o � secret�rio de p�s 
				d = dao.findAtivosByMatriculaPrograma(matricula, null, NivelEnsino.GRADUACAO);
					// s� pode definir orienta��o acad�mica para aluno do curso do coordenador logado 
				if( d != null && !d.getCurso().equals(getCursoAtualCoordenacao()) )
					d = null;
			} else if (isUserInRole(SigaaPapeis.GESTOR_TECNICO)) {
				d = dao.findAtivosByMatriculaUnidadeNivel(matricula, getUnidadeGestora(), NivelEnsino.TECNICO);
			}
			if( d != null )
				resultadoBusca.add(d);
				
		}else{
			
			if( !isChkNome() && !isChkAnoIngresso() && !isChkSemOrientacao() )
				addMensagemErro("Informe pelo menos um filtro para busca.");
			
			if( isChkNome() ){
				if( isEmpty(discenteBusca.getNome()) )
					addMensagemErro("Informe o nome.");
				else 
					nome = discenteBusca.getNome().trim().toUpperCase();
			}
			
			if( isChkAnoIngresso() )
				if( this.anoIngresso == null || this.anoIngresso == 0)
					addMensagemErro("Informe o ano de ingresso.");
				else if( this.periodoIngresso == null || this.periodoIngresso == 0)
					addMensagemErro("Informe o per�odo de ingresso.");
			
			if( hasErrors() )
				return null;

			List<Curso> cursos = new ArrayList<Curso>();
			cursos.add(getCursoAtualCoordenacao());
			if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO ) )
				resultadoBusca = dao.findPossiveisDiscentesOrientandos(nome, getCursoAtualCoordenacao().getNivel(), 
									StatusDiscente.getStatusComVinculo(), null, getCursoAtualCoordenacao().getId(),
									getAnoIngresso(), getPeriodoIngresso(), isChkSemOrientacao());
			else if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) )
				resultadoBusca = dao.findPossiveisDiscentesOrientandos(nome, NivelEnsino.STRICTO,
									StatusDiscente.getStatusComVinculo(), getProgramaStricto().getId(), 
									null, getAnoIngresso(), getPeriodoIngresso(), isChkSemOrientacao());
			else if (isUserInRole(SigaaPapeis.GESTOR_TECNICO))
				resultadoBusca = dao.findPossiveisDiscentesOrientandos(nome, NivelEnsino.TECNICO, 
									StatusDiscente.getStatusComVinculo(), getUnidadeGestora(),
									null, getAnoIngresso(), getPeriodoIngresso(), isChkSemOrientacao());
		}
			
		if ( resultadoBusca == null || resultadoBusca.isEmpty() ) {
			addMensagemErro("Nenhum discente do seu curso foi encontrado de acordo com os crit�rios de busca informados.");
		}else{
			// removendo alunos trancados 
			for (Iterator<Discente> it = resultadoBusca.iterator(); it.hasNext();) {
				Discente discente = it.next();
				if( discente.getStatus() == StatusDiscente.TRANCADO )
					it.remove();
			}
		}

		return telaBusca();
	}

	/**
	 * Gera o relat�rio de discentes sem orienta��o acad�mica. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String gerarRelatorioSemOrientacaoAcademica() throws ArqException {
		DiscenteDao dao = getDAO( DiscenteDao.class );
		resultadoBusca = null;
		if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO ) ){
			resultadoBusca = dao.findDiscentesAtivosSemOrientacaoAcademica(null, getCursoAtualCoordenacao().getId(), NivelEnsino.GRADUACAO, null);
		} else if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) ){
			resultadoBusca = dao.findDiscentesAtivosSemOrientacaoAcademica(null, getProgramaStricto().getId(), NivelEnsino.STRICTO, null);
		} else if ( isUserInRole(SigaaPapeis.GESTOR_TECNICO))
			resultadoBusca = dao.findDiscentesAtivosSemOrientacaoAcademica(getUnidadeGestora(), null, NivelEnsino.TECNICO, null);
		if (resultadoBusca == null || resultadoBusca.isEmpty()) {
			addMensagemWarning("N�o foram encontrados discentes sem orienta��o.");
			return null;
		}
		return forward("/graduacao/orientacao_academica/discentes_sem_orientacao.jsp");
	}

	/**
	 * Cadastra a orienta��o acad�mica no banco de dados.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		validarOrientadores(erros);

		// Captura tamb�m erros oriundos do DataConverter
		if (hasErrors())
			return null;
		
		MovimentoOrientacaoAcademica mov = new MovimentoOrientacaoAcademica();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_ORIENTACAO_ACADEMICA);
		String msgSucesso = null;

		if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.GESTOR_TECNICO ) && 
			( NivelEnsino.isGraduacao( getNivelEnsino() ) || getNivelEnsino() == NivelEnsino.TECNICO  )){	

			if( orientacoes == null || orientacoes.isEmpty() ){
				addMensagemErro("� necess�rio selecionar pelo menos um aluno pra cadastrar orienta��o acad�mica.");
				return null;
			}
	
			OrientacaoAcademicaValidator.validaOrientacoes(orientacoes, erros);
			mov.setOrientacoes(orientacoes);
			mov.setOrientacao(null);
			msgSucesso = "Cadastro de orienta��o acad�mica para o docente " + orientador.getNome() + " realizado com sucesso.";
		} else if( isUserInRole(SigaaPapeis.SECRETARIA_POS , SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG) && (NivelEnsino.isAlgumNivelStricto( getNivelEnsino() ))  ){

			if( isAtualizar() ){
				ValidatorUtil.validateRequired(orientacao.getInicio(), "Data de In�cio", erros);
				
				if(hasErrors())
					return null;
				
				mov.setCodMovimento(SigaaListaComando.ALTERAR_ORIENTACAO_ACADEMICA);
				msgSucesso = "Cadastro de orienta��o do discente " + orientacao.getDiscente().toString() + " ATUALIZADA com sucesso.";
			} else if( isFinalizar() ){
				ValidatorUtil.validateRequired(orientacao.getFim(), "Data de Finaliza��o", erros);
				
				if(hasErrors())
					return null;
				
				mov.setCodMovimento( SigaaListaComando.DESATIVAR_ORIENTACAO_ACADEMICA );
				msgSucesso = "Orienta��o do discente " + orientacao.getDiscente().toString() + " FINALIZADA com sucesso.";
			}else if( isCancelar() ){
				mov.setCodMovimento( SigaaListaComando.CANCELAR_ORIENTACAO_ACADEMICA );
				msgSucesso = "Orienta��o do discente " + orientacao.getDiscente().toString() + " CANCELADA com sucesso.";
			}else if( isCadastrar() ){
				
				EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class);
				String tipoDocente = getParameter("tipoAjaxDocente_1"); 
				if( tipoBuscaDocente == DOCENTE_PROGRAMA ) {
					EquipePrograma equipeBanco = dao.findByPrimaryKey( equipe.getId() , EquipePrograma.class);
					
					
					if(!validaLimiteOrientacoes(equipeBanco, orientacao.getDiscente())) {
						addMensagemErro("Limite de Orienta��es ultrapassada para o docente.");
						return null;
					}
					
					if( equipeBanco == null ){
						addMensagemErro("Selecione um orientador.");
						return null;
					} else {
						equipe = equipeBanco;
					}
					if( equipe.isServidorUFRN() )
						orientacao.setServidor(equipe.getServidor());
					else
						orientacao.setDocenteExterno( equipe.getDocenteExterno() );
					
					
					
					
				}if( tipoBuscaDocente == DOCENTE_EXTERNO_PROGRAMA ) {
					DocenteExterno docenteExterno = getGenericDAO().findByPrimaryKey(idDocente, DocenteExterno.class);
					if( "externo".equals(tipoDocente) ){
						if (docenteExterno == null){
							addMensagemErro("O Docente informado n�o � Externo");
							return null;
						}
						orientacao.setDocenteExterno( new DocenteExterno( idDocente ) );
						orientacao.setServidor( null );
					}else{
						Servidor servidor = getGenericDAO().findByPrimaryKey(idDocente, Servidor.class);
						if ("unidade".equals(tipoDocente) && (servidor == null || servidor.getUnidade().getId() != getUsuarioLogado().getVinculoAtivo().getUnidade().getId())){
							addMensagemErro("O Docente informado n�o pertence a sua Unidade.");
							return null;							
						}
						if (docenteExterno != null){
							addMensagemErro("O Docente informado � Externo. Para a Op��o selecionada informe apenas Docentes Internos.");
							return null;
						}
						orientacao.setDocenteExterno( null );
						orientacao.setServidor( new Servidor(idDocente) );
					}
				}

				OrientacaoAcademicaValidator.validaOrientacao(orientacao, erros);

				if (hasErrors())
					return null;
				
				msgSucesso = "Cadastro de orienta��o para o discente " + orientacao.getDiscente().toString() + " realizado com sucesso.";
			}

			mov.setOrientacao(orientacao);
			mov.setOrientacoes(null);

		}else
			throw new SegurancaException();


		try {
			execute(mov, getCurrentRequest());
			addMessage(msgSucesso, TipoMensagemUFRN.INFORMATION);
			if( getAcessoMenu().isAlgumUsuarioStricto() )
				return telaListaOrientadores();
			return cancelar();
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * Verifica se o limite de orienta��es foi ultrapassado.
	 * @param e
	 * @param d
	 * @return
	 * @throws DAOException
	 */
	private Boolean validaLimiteOrientacoes(EquipePrograma e, Discente d) throws DAOException {
		//Preven��o para caso um cliente use o m�todo sem todas as propriedades necess�rias preenchidas. 
		e = getGenericDAO().findByPrimaryKey( e.getId() , EquipePrograma.class);
		d = getGenericDAO().findByPrimaryKey( d.getId() , Discente.class); 
		
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		
		Unidade unidade = new Unidade();
		
		if(isPortalCoordenadorStricto() && getProgramaStricto() !=  null)
			unidade  = getProgramaStricto();
		
		if(d.getNivel() == NivelEnsino.MESTRADO && d.getTipo() == Discente.REGULAR) {
			if(e.getMaxOrientandoRegularMestrado() != null) {
				Integer totalOrientacoesMestradoRegular = dao.findTotalOrientacoesServidorNivel(e.getServidor(), e.getDocenteExterno(), d.getNivel(), Discente.REGULAR, unidade.getId());
				if(totalOrientacoesMestradoRegular+1 > e.getMaxOrientandoRegularMestrado()) {
					return false;
				}
			}
		}
		
		if(d.getNivel() == NivelEnsino.MESTRADO && d.getTipo() == Discente.ESPECIAL) {
			if(e.getMaxOrientandoEspecialMestrado() != null) {
				Integer totalOrientacoesMestradoEspecial = dao.findTotalOrientacoesServidorNivel(e.getServidor(), e.getDocenteExterno(), d.getNivel(), Discente.ESPECIAL, unidade.getId());
				if(totalOrientacoesMestradoEspecial+1 > e.getMaxOrientandoEspecialMestrado()) {
					return false;
				}
			}
		}
		
		if(d.getNivel() == NivelEnsino.DOUTORADO && d.getTipo() == Discente.REGULAR) {
			if(e.getMaxOrientandoRegularDoutorado() != null) {
				Integer totalOrientacoesDoutoradoRegular = dao.findTotalOrientacoesServidorNivel(e.getServidor(), e.getDocenteExterno(), d.getNivel(), Discente.REGULAR, unidade.getId());
				if(totalOrientacoesDoutoradoRegular+1 > e.getMaxOrientandoRegularDoutorado()) {
					return false;
				}
			}
		}
		
		if(d.getNivel() == NivelEnsino.DOUTORADO && d.getTipo() == Discente.ESPECIAL) {
			if(e.getMaxOrientandoEspecialDoutorado() != null) {
				Integer totalOrientacoesDoutoradoEspecial = dao.findTotalOrientacoesServidorNivel(e.getServidor(), e.getDocenteExterno(), d.getNivel(), Discente.ESPECIAL, unidade.getId());
				if(totalOrientacoesDoutoradoEspecial+1 > e.getMaxOrientandoEspecialDoutorado()) {
					return false;
				}
			}
		}		
		
		return true;
	}

	/**
	 * Verifica se o usu�rio possui o pap�l adequado para realizar a opera��o de cadastro.
	 * M�todo n�o invocado por JSP.
	 * @param erros
	 */
	private void validarOrientadores(ListaMensagens erros) {
		if( isUserInRole(SigaaPapeis.SECRETARIA_POS , SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG) ) {
			if (isCadastrar()) {
				if(!hasErrors())
					ValidatorUtil.validateRequired(orientacao.getInicio(), "Data de In�cio", erros);
				// valida��o dos dados do formul�rio 
				if( tipoBuscaDocente == DOCENTE_EXTERNO_PROGRAMA && idDocente <= 0 )
					addMensagemErro("Selecione um docente.");
				if( tipoBuscaDocente == DOCENTE_PROGRAMA && equipe.getId() <= 0 )
					addMensagemErro("Selecione um docente.");
			}
		}
			
		
	}

	/**
	 * Remove uma orienta��o ACAD�MICA selecionada. APENAS PARA DISCENTES DE
	 * GRADUA��O E T�CNICO. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String removerOrientacao() throws ArqException {

		prepareMovimento(SigaaListaComando.DESATIVAR_ORIENTACAO_ACADEMICA);

		Integer id = getParameterInt("id");
		if( id == null || id == 0 ){
			addMensagemErro("Nenhuma orienta��o acad�mica selecionada.");
			return null;
		}

		OrientacaoAcademica orientacao = getGenericDAO().findByPrimaryKey(id, OrientacaoAcademica.class);
		orientacao.getDiscente().getStatus();
		orientacao.getDiscente().getId();


		MovimentoOrientacaoAcademica mov = new MovimentoOrientacaoAcademica();
		mov.setOrientacao(orientacao);

		mov.setCodMovimento(SigaaListaComando.DESATIVAR_ORIENTACAO_ACADEMICA);
		try {
			execute(mov, getCurrentRequest());
			addMensagemWarning("Orienta��o acad�mica removida com sucesso!");
			lista.remove(orientacao);
		}catch(NegocioException e){
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}catch (Exception e) {
			return tratamentoErroPadrao(e);
		}

		gerenciarOrientacoes = true;
		return forward(getListPage());
	}

	/**
	 * Inicia o cadastro de orientador para um discente stricto. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastro() throws ArqException {
		checkRole(SigaaPapeis.SECRETARIA_POS , SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG);
		init();
		prepareMovimento(SigaaListaComando.CADASTRAR_ORIENTACAO_ACADEMICA);
		operacao = CADASTRAR;
		orientacao.setDiscente(discenteBusca.getDiscente());
		orientacao.setTipoOrientacao( OrientacaoAcademica.CoORIENTADOR );
		setConfirmButton( "Cadastrar Orienta��o" );
		this.tipoBuscaDocente = DOCENTE_PROGRAMA; 
		return telaFormCadastroOrientacaoStricto();
	}

	/**
	 * Inicia o caso de uso de FINALIZAR uma orienta��o de um discente de p�s
	 * gradua��o. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li>
	 * </ul>
	 */
	@Override
	public String preRemover() {

		try {
			checkRole( SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO );

			int id = getParameterInt("id");
			orientacao = getGenericDAO().findByPrimaryKey(id, OrientacaoAcademica.class);
			orientacao.getDiscente().getStatus();
			orientacao.getDiscente().getId();

			if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) && getProgramaStricto()!= null && getProgramaStricto().getId() != orientacao.getDiscente().getGestoraAcademica().getId() ){
				addMensagemErro("S� � poss�vel remover orienta��o de discentes do seu programa.");
				return null;
			}
			if( orientacao.getFim() != null ){
				addMensagemErro("S� � poss�vel finalizar orienta��o ativas.");
				return null;
			}

			prepareMovimento(SigaaListaComando.DESATIVAR_ORIENTACAO_ACADEMICA);
			operacao = FINALIZAR;
			setConfirmButton("Finalizar Orienta��o");
		} catch (SegurancaException e) {
			addMensagemErro( e.getMessage() );
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}

		return telaFormCadastroOrientacaoStricto();
	}

	/**
	 * Inicia a altera��o do orientador ou co-orientador de um discente de
	 * STRICTO. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	@Override
	public String atualizar() throws ArqException {
		checkRole( SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO );
		try {

			int id = getParameterInt("id");
			orientacao = getGenericDAO().findByPrimaryKey(id, OrientacaoAcademica.class);
			orientacao.getDiscente().getStatus();
			orientacao.getDiscente().getId();

			if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) && isPortalCoordenadorStricto() && getProgramaStricto().getId() != orientacao.getDiscente().getGestoraAcademica().getId() ){
				addMensagemErro("S� � poss�vel alterar orienta��o de discentes do seu programa.");
				return null;
			}
			if( orientacao.getFim() != null ){
				addMensagemErro("S� � poss�vel alterar orienta��o n�o finalizadas.");
				return null;
			}

			prepareMovimento(SigaaListaComando.ALTERAR_ORIENTACAO_ACADEMICA);
			operacao = ATUALIZAR;
			setConfirmButton("Alterar Orienta��o");
		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		}

		return telaFormCadastroOrientacaoStricto();
	}

	/**
	 * Inicia o cancelamento de uma orienta��o, utilizado apenas para discente
	 * de stricto. O cancelamento deve ser utilizado APENAS QUANDO A ORIENTA��O
	 * FOI CADASTRADA ERRADA. SER� CANCELADA A ORIENTA��O DO ALUNO E AS TESES
	 * ORIENTADAS CRIADAS DEVIDO A ESTA ORIENTA��O. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public String cancelarOrientacao() throws ArqException {
		try {
			int id = getParameterInt("id");
			orientacao = getGenericDAO().findByPrimaryKey(id, OrientacaoAcademica.class);
			orientacao.getDiscente().getStatus();
			orientacao.getDiscente().getId();

			prepareMovimento(SigaaListaComando.CANCELAR_ORIENTACAO_ACADEMICA);
			operacao = CANCELAR;
			setConfirmButton("Cancelar Orienta��o");
		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		}

		return telaFormCadastroOrientacaoStricto();
	}

	/**
	 * Busca orienta��es acad�micas. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/tutoria_aluno/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO , SigaaPapeis.PPG,
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO);

		lista = null;
		
		String param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o crit�rio de busca.");
			return null;
		}

		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		if ("discente".equalsIgnoreCase(param)){

			if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO ) ){
				lista = dao.findAtivoByDiscenteCurso(getCursoAtualCoordenacao().getId(), null, discenteBusca.getId());
			} else if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) ){
				lista = dao.findAtivoByDiscenteCurso(null, getProgramaStricto().getId() ,discenteBusca.getId());
			} else if ( isUserInRole( SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO ))
				lista = dao.findAtivoByDiscenteUnidadeNivel(discenteBusca.getId(), getUnidadeGestora(), NivelEnsino.TECNICO);

		}else if ("orientador".equalsIgnoreCase(param)){

			if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO ) ){
				lista = dao.findAtivoByServidorCurso(getCursoAtualCoordenacao().getId(), null, orientador.getId());
			} else if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) ){
				lista = dao.findAtivoByServidorCurso(null, getProgramaStricto().getId(), orientador.getId());
			} else if ( isUserInRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO))
				lista = dao.findAtivoByServidorUnidadeNivel(getUnidadeGestora(), orientador.getId(), NivelEnsino.TECNICO);

		}else if ("todos".equalsIgnoreCase(param)) {

			if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO ) ){
				lista = dao.findAtivoByCurso( getCursoAtualCoordenacao().getId(), null );
			} else if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) ){
				lista = dao.findAtivoByCurso( null, getProgramaStricto().getId() );
			} else if ( isUserInRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO))
				lista = dao.findAtivoByUnidadeNivel(getUnidadeGestora(), NivelEnsino.TECNICO);

		} else {
			lista = null;
		}

		discenteBusca = new Discente();
		orientador = new Servidor();

		if ( lista == null || lista.isEmpty()) {
			addMensagemErro("Nenhuma orienta��o acad�mica foi encontrada de acordo com os crit�rios de busca");
		}else{
			Collections.sort((List<OrientacaoAcademica>) lista, new Comparator<OrientacaoAcademica>(){
				public int compare(OrientacaoAcademica o1, OrientacaoAcademica o2) {
					String nome = StringUtils.toAscii(o1.getServidor().getPessoa().getNome());
					return nome.compareToIgnoreCase(StringUtils.toAscii( o2.getServidor().getPessoa().getNome() ));
				}
			});
		}

		return null;
	}

	/**
	 * Lista as orienta��es dadas � um discente. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String visualizarOrientacoesDadas() throws DAOException {
		String nivelDiscente = getParameter("nivelDiscente");
		int idDiscente = getParameterInt("idDiscente", 0);
		if (idDiscente == 0) {
			addMensagemErro("Selecione um discente v�lido.");
			return null;
		}
		OrientacaoMatriculaDao omDao = getDAO(OrientacaoMatriculaDao.class);
		SolicitacaoTrancamentoMatriculaDao stmDao = getDAO(SolicitacaoTrancamentoMatriculaDao.class);
		orientacoesMatricula = omDao.findByDiscente(new Discente(idDiscente));
		solicitacoesTrancamento = stmDao.findByDiscente(idDiscente, null);
		if (orientacoesMatricula == null && solicitacoesTrancamento == null ||
				orientacoesMatricula.isEmpty() && solicitacoesTrancamento.isEmpty()){
			addMensagemErro("N�o h� orienta��es acad�micas para este discente");
			return null;
		}
		char nivel = nivelDiscente.charAt(0);
		if(nivel == NivelEnsino.GRADUACAO)
			discenteBusca = omDao.findByPrimaryKey(idDiscente, DiscenteGraduacao.class);
		else if(NivelEnsino.isAlgumNivelStricto(nivel))
			discenteBusca = omDao.findByPrimaryKey(idDiscente, DiscenteStricto.class);
		return forward("/graduacao/orientacao_academica/orientacao_dada.jsp");
	}

	/**
	 * Listar os orientados de p�s-gradua��o de um servidor. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarStricto() throws DAOException {
		// Verificar se o usu�rio possui orientandos
		orientacoes = null;
		if (getServidorUsuario() != null) {
			orientacoes = getDAO(OrientacaoAcademicaDao.class).findAllStrictoByServidor(getServidorUsuario());
		}
		if (getUsuarioLogado().getVinculoAtivo().getDocenteExterno() != null) {
			orientacoes = getDAO(OrientacaoAcademicaDao.class).findAllStrictoByDocenteExterno(getUsuarioLogado().getVinculoAtivo().getDocenteExterno());
		}


		if ( isEmpty(orientacoes) ) {
			addMensagemErro("N�o foram encontrados discentes de p�s-gradua��o sob sua orienta��o");
			return cancelar();
		}

		return forward("/stricto/orientacao/orientandos.jsp");
	}

	/**
	 * Inicia a ger�ncia de orienta��es, realizado pela coordena��o de cursos de
	 * gradua��o. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarOrientacoes() throws ArqException{
		init();
		discenteBusca = new Discente();
		gerenciarOrientacoes = true;
		return listar();
	}

	/**
	 * Retorna uma cole��o de SelectItem de docentes do programa.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientador.jsp</li>
	 * </ul>	
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getDocentesPrograma() throws DAOException {
		if( isUserInRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO) ){
			EquipeProgramaDao dao = getDAO( EquipeProgramaDao.class );
			return toSelectItems(dao.findByPrograma( getProgramaStricto().getId() ), "id", "descricao");
		}
		return null;
	}

	/**
	 * Tem como fun��o redirecionar para a o formul�rio de envio de e-mail,
	 * incluindo os dados do destinat�rio do email. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String redirecionar() throws ArqException{
		id = getParameterInt("idDiscente");

		selecionarOrientacao();
		
		setOperacaoAtiva(ENVIAR_EMAIL);
		prepareMovimento(SigaaListaComando.CADASTRAR_MENSAGEM_ORIENTACAO);

		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		envio = dao.findDadosOrientacaoByDiscente(id, null);
		
		return forward("/graduacao/orientacao_academica/enviar_email.jsp");
	}

	/**
	 * Seleciona a {@link OrientacaoAcademica} de acordo com o id passado pela view.
	 */
	private void selecionarOrientacao() {
		OrientacaoAcademica orientacaoAux = new OrientacaoAcademica(getParameterInt("idOrientacao"));
		
		if(!isEmpty(orientacaoAux)) {
			for (OrientacaoAcademica o : lista) {
				if(o.equals(orientacaoAux)) {
					orientacao = o;
					break;
				}
			}
		}
	}

	/**
	 * Envio de um email para um �nico orientando. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/enviar_email.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public String enviarMensagem() throws DAOException, NegocioException {

		if( !checkOperacaoAtiva(ENVIAR_EMAIL)) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}

		if (notificacao.getMensagem().equals("")) {
			addMensagemErro("Nenhuma mensagem digitada.");
			return null;
		}
		
		String assunto = "Orienta��o Acad�mica";
		String mensagem = "Caro(a) " + orientacao.getDiscente().getPessoa().getNome() + ", <br/><br/>" +
		notificacao.getMensagem() + "<br/><br/> Atenciosamente,<br/>"
		+getUsuarioLogado().getNome();
		
		MensagemOrientacao mensagemOrientacao = new MensagemOrientacao();
		
		mensagemOrientacao.setOrientacaoAcademica(orientacao);
		mensagemOrientacao.setAssunto(assunto);
		mensagemOrientacao.setMensagem(mensagem);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_MENSAGEM_ORIENTACAO);
		mov.setObjMovimentado(mensagemOrientacao);
		
		try {
			execute(mov);
		} catch (ArqException e) {
			return tratamentoErroPadrao(e);
		}

		removeOperacaoAtiva();
		init();

		addMensagemInformation("E-mail enviado com sucesso!");
		return forward(JSP_LISTA);
	}
	
	/**
	 * Lista todas as mensagens de orienta��o enviadas pelo orientador e garante a presen�a do bot�o voltar na tela.
	 * <b /><br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarMensagens() throws DAOException {
		MensagemOrientacaoMBean bean = getMBean("mensagemOrientacao");
		
		bean.setPermiteVoltar(true);
		
		return bean.listarMensagens();
	}
	
	/**
	 * Serve para verificar se o Docente tem permiss�o necess�rio para acessar a listagem dos 
	 * orientandos.
	 * <br />
	 * 		M�todo n�o invocado por JSPs.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkListRole()
	 */
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO,SigaaPapeis.SECRETARIA_COORDENACAO, 
				SigaaPapeis.ORIENTADOR_ACADEMICO,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO,  SigaaPapeis.PPG,
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO);
	}

	/** Retorna o diret�rio base dos formul�rios JSP. M�todo n�o invocado por JSP.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String getDirBase() {
		return "/graduacao/orientacao_academica";
	}

	/**
	 * Redireciona para o formul�rio de busca de discentes. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String telaBusca(){
		return forward(JSP_BUSCA);
	}

	/**
	 * Redireciona para o formul�rio de sele��o do orientador acad�mico.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String telaOrientador(){
		return forward(JSP_ORIENTADOR);
	}

	/**
	 * Redireciona para o formul�rio que cont�m os dados da orienta��o
	 * acad�mica. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String telaDadosOrientacao(){
		return forward(JSP_DADOS_ORIENTACAO);
	}

	/** Retorna o orientador acad�mico.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Servidor getOrientador() {
		return orientador;
	}

	/** Seta o orientador acad�mico.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientador.jsp</li>
	 * </ul>
	 * 
	 * @param orientador
	 */
	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}

	/** Retorna o ano de ingresso utilizado para buscar discentes.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Integer getAnoIngresso() {
		if(!isChkAnoIngresso())
			this.anoIngresso = null;
		return anoIngresso;
	}

	/** Seta o ano de ingresso utilizado para buscar discentes. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientador.jsp</li>
	 * </ul>
	 * 
	 * @param anoIngresso
	 */
	public void setAnoIngresso(Integer anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	/** Retorna o per�odo de ingresso utilizado para buscar discentes.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Integer getPeriodoIngresso() {
		if(!isChkAnoIngresso())
			this.periodoIngresso = null;
		return periodoIngresso;
	}

	/** Seta o per�odo de ingresso utilizado para buscar discentes. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientador.jsp</li>
	 * </ul>
	 * 
	 * @param periodoIngresso
	 */
	public void setPeriodoIngresso(Integer periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

	/** Retorna o resultado da busca por discentes.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/discentes_sem_orientacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<Discente> getResultadoBusca() {
		return resultadoBusca;
	}

	/** Seta o resultado da busca por discentes. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/discentes_sem_orientacao.jsp</li>
	 * </ul>
	 * 
	 * @param resultadoBusca
	 */
	public void setResultadoBusca(Collection<Discente> resultadoBusca) {
		this.resultadoBusca = resultadoBusca;
	}

	/** Retorna a cole��o de orienta��es acad�micas.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/ead/tutoria_aluno/lista.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientacao_dada.jsp</li>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<OrientacaoAcademica> getOrientacoes() {
		return orientacoes;
	}

	/** Retorna a cole��o de orienta��es acad�micas ordenadas pelo status do aluno.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientandos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<OrientacaoAcademica> getOrientacoesOrdenadas() {
		
		Collections.sort((List<OrientacaoAcademica>) orientacoes, new Comparator<OrientacaoAcademica>(){
			public int compare(OrientacaoAcademica o1, OrientacaoAcademica o2) {
				String nome = StringUtils.toAscii(o1.getDiscente().getNome());
				String nivel = StringUtils.toAscii(o1.getDiscente().getNivelDesc());
				String status = StringUtils.toAscii(o1.getDiscente().getStatusString());
				
				if ( nivel.equalsIgnoreCase(StringUtils.toAscii(o2.getDiscente().getNivelDesc())) ){
					if ( status.equalsIgnoreCase(StringUtils.toAscii(o2.getDiscente().getStatusString())) )
						return nome.compareToIgnoreCase(StringUtils.toAscii( o2.getDiscente().getNome() ));
					else {
						if ( !o1.getDiscente().isCancelado() && !o2.getDiscente().isCancelado() )
							return status.compareToIgnoreCase(StringUtils.toAscii( o2.getDiscente().getStatusString()));
						else if ( o1.getDiscente().isCancelado() ) return 1;
						else if ( o1.getDiscente().isCancelado() ) return -1;
						else return nome.compareToIgnoreCase(StringUtils.toAscii( o2.getDiscente().getNome() ));
					}					
				} else {
					return StringUtils.toAscii( o2.getDiscente().getNivelDesc() ).compareToIgnoreCase(nivel);
				}
				
			}
		});
		
		return orientacoes;
	}
	
	/** Seta a cole��o de orienta��es acad�micas. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/ead/tutoria_aluno/lista.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientacao_dada.jsp</li>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientandos.jsp</li>
	 * </ul>
	 * 
	 * @param orientacoes
	 */
	public void setOrientacoes(Collection<OrientacaoAcademica> orientacoes) {
		this.orientacoes = orientacoes;
	}

	/** Retorna o total de orientados ativos.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public int getTotalOrientandos() {
		return totalOrientandos;
	}

	/** Seta o total de orientados ativos.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * </ul>
	 * 
	 * @param totalOrientandos
	 */
	public void setTotalOrientandos(int totalOrientandos) {
		this.totalOrientandos = totalOrientandos;
	}

	/** Retorna o docente orientador da Equipe do Programa de P�s-Gradua��o.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientador.jsp</li>
 	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>  
	 * </ul>
	 * 
	 * @return
	 */
	public EquipePrograma getEquipe() {
		return equipe;
	}

	/** Seta o docente orientador da Equipe do Programa de P�s-Gradua��o. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/form.jsp</li>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
 	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientador.jsp</li>
 	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>  
	 * </ul>
	 * 
	 * @param equipe
	 */
	public void setEquipe(EquipePrograma equipe) {
		this.equipe = equipe;
	}

	/**
	 * Retorna a lista de orienta��es acad�micas do professor logado. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/docente.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> getLista() throws DAOException {
		if( isEmpty(lista) && isUserInRole(SigaaPapeis.ORIENTADOR_ACADEMICO) ){
			OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
			lista = dao.findAtivoByServidorCursoTipo(null, null, getServidorUsuario().getId(), OrientacaoAcademica.ACADEMICO);
			if ( lista == null || lista.isEmpty()) {
				addMensagemErro("Nenhuma orienta��o acad�mica foi encontrada.");
			}else{
				Collections.sort((List<OrientacaoAcademica>) lista, new Comparator<OrientacaoAcademica>(){
					public int compare(OrientacaoAcademica o1, OrientacaoAcademica o2) {
						String nome = StringUtils.toAscii(o1.getDiscente().getNome());
						String status = StringUtils.toAscii(o1.getDiscente().getStatusString());
						
						if ( status.equalsIgnoreCase(StringUtils.toAscii(o2.getDiscente().getStatusString())) )
								 return nome.compareToIgnoreCase(StringUtils.toAscii( o2.getDiscente().getNome() ));
						else {
							if ( !o1.getDiscente().isCancelado() && !o2.getDiscente().isCancelado() )
								return status.compareToIgnoreCase(StringUtils.toAscii( o2.getDiscente().getStatusString()));
							else if ( o1.getDiscente().isCancelado() ) return 1;
							else if ( o1.getDiscente().isCancelado() ) return -1;
							else return nome.compareToIgnoreCase(StringUtils.toAscii( o2.getDiscente().getNome() ));
						}
					}
				});
			}
		}
		return lista;
	}

	/** Seta a lista de orienta��es acad�micas do professor logado. 
	 * @param lista
	 */
	public void setLista(Collection<OrientacaoAcademica> lista) {
		this.lista = lista;
	}

	/**
	 * Seleciona um discente da busca geral de discente.
	 * <br />
	 * M�todo n�o invocado por JSPs.
	 * 
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente(br.ufrn.sigaa.ensino.dominio.DiscenteAdapter)
	 */
	public String selecionaDiscente() throws ArqException {
		if( !discenteBusca.isStricto() )
			addMensagemErro("Esta opera��o s� � permitida para discentes de p�s-gradua��o.");

		Collection<Integer> status = StatusDiscente.getAtivos();
		status.add( StatusDiscente.DEFENDIDO );
		status.add( StatusDiscente.CONCLUIDO );
		if( !status.contains( discenteBusca.getStatus() ) ){
			addMensagemErro("N�o � poss�vel cadastrar orienta��o para " +discenteBusca.toString() + " pois ele est� " + discenteBusca.getStatusString());
		}

		if( hasOnlyErrors() )
			return null;

		init();
		orientacao.setDiscente( discenteBusca.getDiscente() );
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		orientacoes = dao.findByDiscente(discenteBusca.getId(), null, false);
		
		/* Verifica se o discente est� conclu�do, se estiver permite apenas gerenciar as orienta��es ativas ou cadastrar caso n�o tenha */
		if (discenteBusca.getDiscente().isConcluido()){
			if (!isEmpty(orientacoes)){
				orientacoes = dao.findByDiscente(discenteBusca.getId(), null, true);
				if (isEmpty(orientacoes)){
					addMensagemErro("O Discente selecionado j� possui orienta��es e encontra-se Conclu�do");
					return null;					
				}
			}
		}
		
		lista = orientacoes;
		return telaListaOrientadores();
	}

	/**
	 * Redireciona para o formul�rio de cadastro de orienta��es de stricto.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String telaFormCadastroOrientacaoStricto(){
		return forward("/stricto/orientacao/form.jsp");
	}

	/**
	 * Redireciona para a lista de orienta��es acad�micas de stricto.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacoes/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String telaListaOrientadores() throws DAOException{
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		/* Para alunos conclu�ntes apenas exibe as orienta��es ativas */
		if (discenteBusca.getDiscente().isConcluido())
			orientacoes = dao.findByDiscente(discenteBusca.getId(), null, true);
		else
			orientacoes = dao.findByDiscente(discenteBusca.getId(), null, false);
		lista = orientacoes;
		return forward("/stricto/orientacao/orientadores.jsp");
	}

	
	/** Seta o discente selecionado no formul�rio de busca por discente.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		discenteBusca = discente;
	}

	/** Retorna o discente utilizado na busca. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientacao_dada.jsp</li> 
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li> 
	 * </ul>
	 * 
	 * @return
	 */
	public DiscenteAdapter getDiscenteBusca() {
		return discenteBusca;
	}

	/** Seta o discente utilizado na busca.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientacao_dada.jsp</li> 
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li> 
	 * </ul>
	 * 
	 * @param discenteBusca
	 */
	public void setDiscenteBusca(DiscenteAdapter discenteBusca) {
		this.discenteBusca = discenteBusca;
	}

	/** Retorna a orienta��o utilizada no cadastro de orienta��o para p�s gradua��o, para adicionar na lista.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public OrientacaoAcademica getOrientacao() {
		return orientacao;
	}

	/** Seta a orienta��o utilizada no cadastro de orienta��o para p�s gradua��o, para adicionar na lista.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * </ul>
	 *  
	 * @param orientacao
	 */
	public void setOrientacao(OrientacaoAcademica orientacao) {
		this.orientacao = orientacao;
	}

	/** Indica se a opera��o � do tipo CADASTRAR
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isCadastrar(){
		return operacao == CADASTRAR;
	}

	/** Indica se a opera��o � do tipo ATUALIZAR
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isAtualizar(){
		return operacao == ATUALIZAR;
	}

	/** Indica se a opera��o � do tipo FINALIZAR
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isFinalizar(){
		return operacao == FINALIZAR;
	}

	/** Indica se a opera��o � do tipo CANCELAR
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/orientadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isCancelar(){
		return operacao == CANCELAR;
	}

	/** Retorna o ID do docente orientador. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public int getIdDocente() {
		return idDocente;
	}

	/** Seta o ID do docente orientador. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * </ul>
	 * 
	 * @param idDocente
	 */
	public void setIdDocente(int idDocente) {
		this.idDocente = idDocente;
	}

	/** Retorna o tipo de busca por docente.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public int getTipoBuscaDocente() {
		return tipoBuscaDocente;
	}

	/** Define o tipo de busca por docente.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * </ul>
	 * 
	 * @param tipoBuscaDocente
	 */
	public void setTipoBuscaDocente(int tipoBuscaDocente) {
		this.tipoBuscaDocente = tipoBuscaDocente;
	}

	/**
	 * Indica se a opera��o � lista de orienta��es ou ger�ncia de orienta��es.
	 * Como o formul�rio de lista das orienta��es (do orientador acad�mico) e
	 * ger�ncias das orienta��es (do coordenador de gradua��o) s�o o mesmo, este
	 * atributo controla se a opera��o de listar ou gerenciar. Se for ger�ncia
	 * deve exibir o bot�o para remover e o formul�rio de consulta.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/menu/coordenacao.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isGerenciarOrientacoes() {
		return gerenciarOrientacoes;
	}

	/**
	 * Seta se a opera��o � lista de orienta��es ou ger�ncia de orienta��es.
	 * Como o formul�rio de lista das orienta��es (do orientador acad�mico) e
	 * ger�ncias das orienta��es (do coordenador de gradua��o) s�o o mesmo, este
	 * atributo controla se a opera��o de listar ou gerenciar. Se for ger�ncia
	 * deve exibir o bot�o para remover e o formul�rio de consulta.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/menu/coordenacao.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>
	 * 
	 * @param gerenciarOrientacoes
	 */
	public void setGerenciarOrientacoes(boolean gerenciarOrientacoes) {
		this.gerenciarOrientacoes = gerenciarOrientacoes;
	}

	/** Retorna a mensagem de notifica��o a ser enviada.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/enviar_email.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 */
	public Notificacao getNotificacao() {
		return notificacao;
	}

	/** Seta a mensagem de notifica��o a ser enviada. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/enviar_email.jsp</li>
	 * </ul> 
	 * 
	 * @param notificacao
	 */
	public void setNotificacao(Notificacao notificacao) {
		this.notificacao = notificacao;
	}

	/** Retorna o ID da orienta��o acad�mica tratada na opera��o. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/** Seta o ID da orienta��o acad�mica tratada na opera��o.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/orientacao/form.jsp</li>
	 * </ul> 
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna os dados referentes ao orientando. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientacao_dada.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 */
	public List<Map<String,Object>> getEnvio() {
		return envio;
	}

	/** Seta os dados referentes ao orientando. 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientacao_dada.jsp</li>
	 * </ul> 
	 * 
	 * @param envio
	 */
	public void setEnvio(List<Map<String,Object>> envio) {
		this.envio = envio;
	}

	/** Retorna a lista de orienta��es de matr�culas dadas � um discente.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientacao_dada.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 */
	public Collection<OrientacaoMatricula> getOrientacoesMatricula() {
		return orientacoesMatricula;
	}

	/** Seta a lista de orienta��es de matr�culas dadas � um discente.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientacao_dada.jsp</li>
	 * </ul> 
	 * 
	 * @param orientacoesMatricula
	 */
	public void setOrientacoesMatricula(
			Collection<OrientacaoMatricula> orientacoesMatricula) {
		this.orientacoesMatricula = orientacoesMatricula;
	}

	/** Retorna a lista de orienta��es de trancamentos dadas � um discente.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientacao_dada.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 */
	public Collection<SolicitacaoTrancamentoMatricula> getSolicitacoesTrancamento() {
		return solicitacoesTrancamento;
	}

	/** Seta a lista de orienta��es de trancamentos dadas � um discente.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/orientacao_dada.jsp</li>
	 * </ul> 
	 * 
	 * @param solicitacoesTrancamento
	 */
	public void setSolicitacoesTrancamento(
			Collection<SolicitacaoTrancamentoMatricula> solicitacoesTrancamento) {
		this.solicitacoesTrancamento = solicitacoesTrancamento;
	}

	/**
	 * Retorna a lista de e-mails dos discentes orientados ou encontrados na
	 * busca, separado por v�rgula.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String getEmailDiscentes() {
		StringBuilder listaEmail = new StringBuilder();
		boolean virgula = false;
		for (OrientacaoAcademica orientacao : lista) {
			Discente discente = orientacao.getDiscente();
			String email = discente.getPessoa().getEmail();
			if ( email != null && !email.isEmpty()) {
				if (virgula) {
					listaEmail.append("; " + email);
				}
				else {
					listaEmail.append(email);
					virgula = true;
				}
			}
		}
		return listaEmail.toString();
	}

	/**
	 * Inicializa os filtros
	 */
	private void clearFiltros(){
		this.chkMatricula = false;
		this.chkNome = false;
		this.chkAnoIngresso = false;
		this.chkSemOrientacao = false;
	}
	
	public boolean isChkMatricula() {
		return chkMatricula;
	}

	/**
	 * Define se a busca do discente deve ser realizada pela matr�cula.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * </ul>
	 * 
	 * @param chkMatricula
	 */
	public void setChkMatricula(boolean chkMatricula) {
		if(chkMatricula)
			clearFiltros();
		this.chkMatricula = chkMatricula;
	}

	/**
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isChkNome() {
		return chkNome;
	}

	/**
	 * Define se a busca do discente deve ser realizada pelo nome.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * </ul>
	 * 
	 * @param chkNome
	 */
	public void setChkNome(boolean chkNome) {
		if(chkNome)
			setChkMatricula(false);
		this.chkNome = chkNome;
	}

	/**
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isChkAnoIngresso() {
		return chkAnoIngresso;
	}

	/**
	 * Define se a busca do discente deve ser realizada pelo ano/per�odo de ingresso.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * </ul>
	 * 
	 * @param chkAnoIngresso
	 */
	public void setChkAnoIngresso(boolean chkAnoIngresso) {
		if(chkAnoIngresso)
			setChkMatricula(false);
		this.chkAnoIngresso = chkAnoIngresso;
	}

	/**
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isChkSemOrientacao() {
		return chkSemOrientacao;
	}

	/**
	 * Define se a busca deve retornar somente os discentes sem orienta��o.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/orientacao_academica/busca.jsp</li>
	 * </ul>
	 * 
	 * @param chkSemOrientacao
	 */
	public void setChkSemOrientacao(boolean chkSemOrientacao) {
		if(chkSemOrientacao)
			setChkMatricula(false);
		this.chkSemOrientacao = chkSemOrientacao;
	}

}