/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 15/08/2012
 */
package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ead.PoloCursoDao;
import br.ufrn.sigaa.arq.dao.ead.TurmaEadDao;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.PermissaoAvaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.negocio.MovimentoTutorOrientador;
import br.ufrn.sigaa.ead.negocio.MovimentoUsuarioTutor;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaPessoaMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoPessoa;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorPessoa;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensEAD;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed bean responsável pelas operações sobre os tutores à distância.
 *
 * @author Diego Jácome
 *
 */
@Component("tutorDistancia") @Scope("session")
public class TutorDistanciaMBean extends SigaaAbstractController<TutorOrientador> implements OperadorPessoa {

	/** Atalho para a view do menu SEDIS. */
	private static final String JSP_MENU_SEDIS = "menuSEDIS";
	
	// Busca de Turma para Tutor à Distância
	/** Ano utilizado na busca de turmas. */
	private Integer ano;
	/** Período utilizado na busca de turmas. */
	private Integer periodo;
	/** Nome do docente utilizado na busca de turmas. */
	private String nomeDocente;
	/** Código do componente utilizado na busca de turmas. */
	private String codigoComponente;
	/** Nome do componente utilizado na busca de turmas. */
	private String nomeComponente;
	/** Lista contendo os ids dos pólos utilizados na busca de turmas. */
	private List<String> buscarEm;
	/** Indica se filtra a busca por nome do docente. */
	private boolean filtroNomeDocente;
	/** Indica se filtra a busca por código da disciplina. */
	private boolean filtroCodigoComponente;
	/** Indica se filtra a busca por nome da disciplina. */
	private boolean filtroNomeComponente;
	/** Indica se filtra a busca por pólos. */
	private boolean filtroPolo;
	
	/** Lista de turmas buscadas para o cadastro de permissão. */
	private ArrayList<Turma> turmasBuscadas;
	/** Lista de turmas selecionadas para o cadastro de permissão. */
	private ArrayList<Turma> turmasSelecionadas;
	/** Lista de turmas removidas (no qual o tutor perderá permissão). */
	private ArrayList<Turma> turmasRemovidas;
	/** Lista de turmas do tutor (utilizadas na atualização). */
	private ArrayList<Turma> turmasTutor;
	/** Se a operação é cadastro ou atualização. */
	private boolean alterar = false;
	
	// Busca de Tutor à Distância
	/** Indica se o usuário irá realizar a busca pelo ano e período da turmas do tutor. */
	private boolean buscaAnoPeriodo;
	/** Indica se o usuário irá realizar a busca pelo nome do tutor. */
	private boolean buscaNome;
	/** Indica se o usuário irá realizar a busca pelo nome da disciplina. */
	private boolean buscaDisciplina;
	/** Indica se o usuário irá realizar a busca somente de tutores ativos.*/
	private boolean buscaSomenteAtivos;
	/** Nome do Tutor - utilizado na busca do tutor. */
	private String nome;
	/** Nome da Disciplina - utilizado na busca do tutor. */
	private String nomeDisciplina;
	/** Ano utilizado na busca de tutor */
	private Integer anoTutor;
	/** Período utilizado na busca de tutor */
	private Integer periodoTutor;
	
	public TutorDistanciaMBean() {
		initObj();
	}

	/**
	 * Inicia o objeto de domínio para se usado no bean.
	 */
	private void initObj() {
		obj = new TutorOrientador();
		ano = null;
		periodo = null;
		nomeDocente = null;
		codigoComponente = null;
		nome = null;
		buscaNome = false;
		buscaSomenteAtivos = false;
		buscaAnoPeriodo = true;
		alterar = false;
		buscarEm = new ArrayList<String>();
		turmasBuscadas = new ArrayList<Turma>();
		turmasSelecionadas = new ArrayList<Turma>();
		turmasRemovidas = new ArrayList<Turma>();
		setResultadosBusca(new ArrayList<TutorOrientador>());
		filtroPolo = false;
		filtroCodigoComponente = false;
		filtroNomeComponente = false;
		filtroNomeDocente = false;
		anoTutor = getCalendarioVigente().getAno();
		periodoTutor = getCalendarioVigente().getPeriodo();
		
	}

	/**
	 * Faz o pré-processamento para cadastrar o tutor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		initObj();
		checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD });
		prepareMovimento(SigaaListaComando.CADASTRAR_TUTOR_DISTANCIA);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_TUTOR_DISTANCIA.getId());
		BuscaPessoaMBean mbean = (BuscaPessoaMBean) getMBean("buscaPessoa");
		mbean.setCodigoOperacao(OperacaoPessoa.TUTOR_DISTANCIA);
		setConfirmButton("Cadastrar");
		return mbean.popular();
	}

	/**
	 * Faz o pré-processamento para alterar o tutor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/listar.jsp</li>
	 * </ul>
	 */
	public String preAlterar() throws ArqException, NegocioException {
		checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD });
		
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		
		Integer idTutor = getParameterInt("idTutor");
		PermissaoAvaDao pDao = getDAO(PermissaoAvaDao.class);
		obj = pDao.findByPrimaryKey(idTutor, TutorOrientador.class);
		turmasTutor = (ArrayList<Turma>) pDao.findTurmasPermitidasByPessoaAnoPeriodo(obj.getPessoa(),PermissaoAva.DOCENTE,ano,periodo);
		getDAO(TurmaEadDao.class).findQtdMatriculados(turmasTutor);
		alterar = true;
		prepareMovimento(SigaaListaComando.CADASTRAR_TUTOR_DISTANCIA);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_TUTOR_DISTANCIA.getId());
		setConfirmButton("Alterar");
		return forward("/ead/TutorDistancia/dadosUsuario.jsp");
	}
	
	/**
	 * Busca as turmas que o tutor possui permissão.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/listar.jsp</li>
	 * </ul>
	 */
	public String buscarTurmasTutor() throws ArqException, NegocioException {
		checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD });
		
		if (ano == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");	
		if (periodo == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");	

		if (hasErrors())
			return null;
		
		PermissaoAvaDao pDao = getDAO(PermissaoAvaDao.class);
		turmasTutor = (ArrayList<Turma>) pDao.findTurmasPermitidasByPessoaAnoPeriodo(obj.getPessoa(),PermissaoAva.DOCENTE,ano,periodo);
		getDAO(TurmaEadDao.class).findQtdMatriculados(turmasTutor);
		return forward("/ead/TutorDistancia/dadosUsuario.jsp");
	}
	
	/**
	 * Método chamado para ativar novamente um tutor.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 * @throws Exception 
	 */
	public String reativarTutor() throws SegurancaException, ArqException, NegocioException  {
		
		TutorOrientadorDao tutorDao = null;
		try {			
			int id = getParameterInt("id");
			this.obj.setId(id);
			populateObj();
			
			tutorDao = getDAO(TutorOrientadorDao.class);
			TutorOrientador t = tutorDao.findByExactField(TutorOrientador.class, new String[]{"pessoa.id","tipo","ativo"},
					new Object[]{obj.getPessoa().getId(),TutorOrientador.TIPO_TUTOR_PRESENCIAL,true}, true);
			
			// Verifica se o tutor possui tipo presencial ativo.
			if (t!=null)
				addMensagemErro("Esta pessoa é um tutor presencial, é necessário primeiro desativá-lo em Tutores Presenciais->Listar/Alterar.");
			
			this.obj.setAtivo(true); 
			prepareMovimento(ArqListaComando.ALTERAR);
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			super.cadastrar();
		} finally {
			if (tutorDao != null)
				tutorDao.close();
		}
		return buscar();
	}
	
	/**
	 * Método chamado para ativar novamente um tutor.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String inativarTutor() throws Exception {
		int id = getParameterInt("id");
		this.obj.setId(id);
		populateObj();
		this.obj.setAtivo(false); 
		prepareMovimento(ArqListaComando.ALTERAR);
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		super.cadastrar();

		return buscar();
	}
	
	/**
	 * Cancela a operação atual.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/confirmar.jsp</li>
	 * 		<li>/sigaa.war/ead/TutorDistancia/dadosUsuario.jsp</li>
	 * 		<li>/sigaa.war/ead/TutorDistancia/selecaoTurma.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		super.cancelar();
		return JSP_MENU_SEDIS;
	}

	/**
	 * Após a pessoa ser selecionada, redireciona para o formulário de cadastro do tutor.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É public porque precisa ser acessado pela classe BuscarPessoaMBean, no método redirecionarPessoa(Pessoa)
	 */
	public String selecionaPessoa() {
		buscarEm = new ArrayList<String>();
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		
		TutorOrientadorDao tutorDao = null;
		try {
			tutorDao = getDAO(TutorOrientadorDao.class);			
			TutorOrientador t = tutorDao.findByExactField(TutorOrientador.class, new String[]{"pessoa.id","tipo","ativo"},
					new Object[]{obj.getPessoa().getId(),TutorOrientador.TIPO_TUTOR_A_DISTANCIA,true}, true);			
			if (obj.getId() == 0 && t != null ) {
				if (t.isDistancia())
					addMensagemErro("Esta pessoa já é um tutor à distância, para acrescentar uma nova turma acesse a opção de Listar/Alterar.");
			}
			
			if (hasErrors()){
				BuscaPessoaMBean mbean = (BuscaPessoaMBean) getMBean("buscaPessoa");
				mbean.setCodigoOperacao(OperacaoPessoa.TUTOR_DISTANCIA);
				mbean.setResultadosBusca(null);
				setConfirmButton("Cadastrar");
				return mbean.popular();
			}
		} catch (DAOException e) {
			e.printStackTrace();
			notifyError(e);
		} catch (ArqException e) {
			e.printStackTrace();
			notifyError(e);
		} finally {
			if (tutorDao!=null)
				tutorDao.close();
		}
		
		return forward("/ead/TutorDistancia/selecaoTurma.jsp");
	}

	/**
	 * Prepara o bean e redireciona para tela de busca de turmas.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/dadosUsuario.jsp</li>
	 * </ul>
	 */
	public String telaBuscarTurmas() {
		buscarEm = new ArrayList<String>();
		if (ano == null || periodo == null){
			CalendarioAcademico cal = getCalendarioVigente();
			ano = cal.getAno();
			periodo = cal.getPeriodo();
		}
		return forward("/ead/TutorDistancia/selecaoTurma.jsp");
	}
	
	/**
	 * Prepara o bean e redireciona para tela de busca de turmas.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/dadosUsuario.jsp</li>
	 * </ul>
	 */
	public String telaListar() {
		return forward(getListPage());
	}
	
	/**
	 * Define a página de confirmação de tutor à distância
	 */
	public String getConfirmPage() {
		return "/ead/TutorDistancia/confirmar.jsp";
	}
	
	/**
	 * Define a página de listagem de tutor orientador
	 */
	@Override
	public String getListPage() {
		return "/ead/TutorDistancia/listar.jsf";
	}
	
	/**
	 * Retorna para a tela de busca de turmas.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/confirmar.jsp</li>
	 * </ul>
	 */
	public String forwardPageSelecaoTurmas(){
		return forward("/ead/TutorDistancia/selecaoTurma.jsf");
	}
	
	/**
	 * Busca as turmas para o tutor.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/selecaoTurma.jsp</li>
	 * </ul>
	 */
	public String buscarTurma () throws HibernateException, DAOException {

		TurmaEadDao tDao = null;
		
		if (periodo == null || periodo == 0){
			periodo = null;
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");	
		}
		if (ano == null || ano == 0){
			ano = null;
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");	
		}
		if (filtroNomeDocente && isEmpty(nomeDocente))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome do Docente");	
		if (filtroNomeComponente && isEmpty(nomeComponente))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome do Componente");	
		if (filtroCodigoComponente && isEmpty(codigoComponente))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Código do Componente");
		if (filtroPolo && isEmpty(buscarEm))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Pólo");
		
		if (hasErrors())
			return null;
		
		tDao = getDAO(TurmaEadDao.class);
		ArrayList<Integer> idsPolo = new ArrayList<Integer>();
		if (buscarEm!=null)
			for (String id : buscarEm)
				idsPolo.add(Integer.valueOf(id));
		turmasBuscadas = tDao.findTurmaEad( filtroNomeDocente ? nomeDocente : null,
											filtroNomeComponente ? nomeComponente : null,
											filtroCodigoComponente ? codigoComponente : null,
										 	ano, periodo, 
										 	filtroPolo ? idsPolo : null);

		if(turmasBuscadas == null || turmasBuscadas.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		
		return null;
	}

	/**
	 * Seleciona as turmas que serão cadastradas as permissões para o tutor.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/selecaoTurma.jsp</li>
	 * </ul>
	 */
	public String selecionarTurmas () throws DAOException {
		
		if ( isOperacaoAtiva(SigaaListaComando.CADASTRAR_TUTOR_DISTANCIA.getId()) ){
			PoloCursoDao pDao = null;
			TurmaEadDao tDao = null;

			try {
				pDao = getDAO(PoloCursoDao.class);
				tDao = getDAO(TurmaEadDao.class);
				
				if (!alterar && turmasSelecionadas == null)
					turmasSelecionadas = new ArrayList<Turma>();
				else if (alterar)
					turmasSelecionadas = turmasTutor;
					
				ArrayList<Integer> idsTurma = new ArrayList<Integer>();
				for (Turma t : turmasBuscadas){
					if (t.isSelecionada() && !turmasSelecionadas.contains(t)){
						turmasSelecionadas.add(t);
						idsTurma.add(t.getId());
					}	
				}
				
				if (turmasSelecionadas.isEmpty()){
					addMensagemErro("É necessário selecionar pelo menos uma turma.");
					return null;
				}
				
				tDao.findQtdMatriculados(turmasSelecionadas);
				TurmaUtil.ordenarTurmas(turmasSelecionadas);
				
			} finally {
				if (pDao!=null)
					pDao.close();
				if (tDao!=null)
					tDao.close();
			}
		} else {
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			return redirectJSF(getSubSistema().getLink());
		}
		
		return forward(getConfirmPage());
	}
	
	/**
	 * Volta para o método de selecionar pessoa.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/selecaoTurma.jsp</li>
	 * </ul>
	 */
	public String voltarSelecaoTurma() throws ArqException, NegocioException{
		if (!alterar){
			BuscaPessoaMBean mbean = (BuscaPessoaMBean) getMBean("buscaPessoa");
			mbean.setCodigoOperacao(OperacaoPessoa.TUTOR_DISTANCIA);
			setConfirmButton("Cadastrar");
			return mbean.popular();
		}	
		else return forward("/ead/TutorDistancia/dadosUsuario.jsp");
	}
	
	/**
	 * Altera o tutor, modificando suas turmas com permissão.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/confirmar.jsp</li>
	 * </ul>
	 */
	public String salvar () throws SegurancaException, DAOException {
		
		if ( isOperacaoAtiva(SigaaListaComando.CADASTRAR_TUTOR_DISTANCIA.getId()) ){

			checkRole(SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD);	
	
			if (obj.getPessoa() == null) {
				addMensagem(MensagensEAD.TUTOR_INVALIDO);
				return redirect("/verMenuSedis.do");
			}
			
			turmasSelecionadas = turmasTutor;
			validarTutor();
			
			if(hasErrors())
				return null;
			
			try {
				//Mudar para ativo apenas no cadastro.
				if (obj.getId() == 0){
					obj.setAtivo(true);
					obj.setTipo(TutorOrientador.TIPO_TUTOR_A_DISTANCIA);
				}
				
				// Salva as turmas na primeira tela de alteração.
				MovimentoTutorOrientador mov = new MovimentoTutorOrientador();
				mov.setObjMovimentado(obj);
				mov.setColObjMovimentado(turmasSelecionadas);	
				mov.setUsuarioLogado(getUsuarioLogado());
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_TUTOR_DISTANCIA);
				mov.setTurmasARemover(turmasRemovidas);
				execute(mov);
				setOperacaoAtiva(null);
				addMensagem(OPERACAO_SUCESSO);
				
				if (alterar)
					return listar();
				
			} catch(Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
				return null;
			}
			return JSP_MENU_SEDIS;
		}else {
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
					" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			return redirectJSF(getSubSistema().getLink());
		}
	}
	
	/**
	 * Cadastra ou altera o tutor, modificando suas turmas com permissão.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/confirmar.jsp</li>
	 * </ul>
	 */
	public String cadastrar () throws SegurancaException, DAOException {
		
		if ( isOperacaoAtiva(SigaaListaComando.CADASTRAR_TUTOR_DISTANCIA.getId()) ){

			checkRole(SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD);	
	
			if (obj.getPessoa() == null) {
				addMensagem(MensagensEAD.TUTOR_INVALIDO);
				return redirect("/verMenuSedis.do");
			}
			
			validarTutor();
			
			if(hasErrors())
				return null;
			
			try {
				//Mudar para ativo apenas no cadastro.
				if (obj.getId() == 0){
					obj.setAtivo(true);
					obj.setTipo(TutorOrientador.TIPO_TUTOR_A_DISTANCIA);
				}
				
				MovimentoTutorOrientador mov = new MovimentoTutorOrientador();
				mov.setObjMovimentado(obj);
				mov.setColObjMovimentado(turmasSelecionadas);	
				mov.setUsuarioLogado(getUsuarioLogado());
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_TUTOR_DISTANCIA);
				mov.setTurmasARemover(turmasRemovidas);
				execute(mov);
				setOperacaoAtiva(null);
				addMensagem(OPERACAO_SUCESSO);
				
				if (alterar)
					return listar();
				
			} catch(Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
				return null;
			}
			return JSP_MENU_SEDIS;
		}else {
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
					" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			return redirectJSF(getSubSistema().getLink());
		}
	}
	
	/**
	 * Valida o tutor.<br/><br/>
	 * 
	 * Método não invocado por JSPs. 
	 */
	private void validarTutor() throws DAOException {
		
		TutorOrientadorDao tutorDao = null;
		try {
			tutorDao = getDAO(TutorOrientadorDao.class);
			TutorOrientador t = tutorDao.findByExactField(TutorOrientador.class, new String[]{"pessoa.id","tipo","ativo"},
					new Object[]{obj.getPessoa().getId(),TutorOrientador.TIPO_TUTOR_A_DISTANCIA,true}, true);
			if (obj.getId() == 0 && t != null){
				if (t.isDistancia())
					addMensagemErro("Este usuário já é um tutor à distância.");
			}
			
			Collection<Usuario> usuarios = tutorDao.findByExactField(Usuario.class, new String [] {"pessoa.id","inativo"}, new Object [] {obj.getPessoa().getId(),false});
			if (ValidatorUtil.isEmpty(usuarios))
				addMensagemWarning("Esta pessoa não possui usuário, cadastre o usuário na opção de Listar/Alterar.");
			
			if (turmasSelecionadas == null || turmasSelecionadas.isEmpty())
				addMensagemErro("É necessário selecionar pelo menos uma turma");
			
			ValidatorUtil.validateRequired(obj.getPessoa(), "Pessoa", erros);
		} finally {
			if (tutorDao!=null)
				tutorDao.close();
		}
	}
	
	/**
	 * Realiza busca de tutores de acordo com o curso, polo ou nome da pessoa.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorOrientador/lista.jsp
	 * @throws DAOException 
	 */
	@Override
	public String buscar() throws DAOException {
				
		if (!buscaAnoPeriodo) { anoTutor = null;periodoTutor = null; }
		if (!buscaNome)	nome = null;
		if (!buscaDisciplina) nomeDisciplina = null;
		
		TutorOrientadorDao dao = getDAO(TutorOrientadorDao.class);
		setResultadosBusca(dao.findADistanciaByNomeAtivo( nome, nomeDisciplina, buscaSomenteAtivos,anoTutor,periodoTutor));
		
		if((getResultadosBusca()!=null && getResultadosBusca().isEmpty()) || getResultadosBusca()==null) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		return forward(getListPage());
	}
	
	/**
	 * Remove uma turma selecionada do tutor.<br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/confirmar.jsp</li>
	 * </ul>
	 */
	public String removerTurma () throws HibernateException, DAOException {
		
		Integer idTurma = getParameterInt("idTurma");
		
		for (Iterator<Turma> it = turmasSelecionadas.iterator(); it.hasNext();) {
			Turma t = it.next();
			if (t.getId() == idTurma){
				it.remove();
			}	
		}
		
		if (alterar && !hasErrors()){
			Turma removida = new Turma();
			removida.setId(idTurma);
			turmasRemovidas.add(removida);	
		}	
		
		return forward(getConfirmPage());
	}
	
	/**
	 * Remove uma turma no qual o tutor já possui permissão, tal turma terá sua permissão removida no processador.<br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorDistancia/confirmar.jsp</li>
	 * </ul>
	 */
	public String removerTurmaTutor () throws HibernateException, DAOException {
		
		Integer idTurma = getParameterInt("idTurma");
		turmasTutor = (ArrayList<Turma>) getDAO(PermissaoAvaDao.class).findTurmasPermitidasByPessoaAnoPeriodo(obj.getPessoa(),PermissaoAva.DOCENTE,ano,periodo);
		
		for (Turma removida : turmasRemovidas)
			turmasTutor.remove(removida);	
		
		for (Iterator<Turma> it = turmasTutor.iterator(); it.hasNext();) {
			Turma t = it.next();
			if (t.getId() == idTurma){
				Turma aux = new Turma();
				aux.setId(t.getId());
				turmasRemovidas.add(aux);					
			}	
		}

		for (Turma removida : turmasRemovidas)
			turmasTutor.remove(removida);	
		
		return forward("/ead/TutorDistancia/dadosUsuario.jsp");
	}
	
	/**
	 * Realiza o cadastramento de usuário para o tutor selecionado.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorOrientador/cadastro_usuario.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String cadastrarUsuario() throws DAOException {
		
		if (validacaoDados(erros))
			return null;
		
		MovimentoUsuarioTutor mov = new MovimentoUsuarioTutor();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_USUARIO_TUTOR);
		mov.setUsuario(obj.getUsuario());
		mov.setTutor(obj);

		try {
			executeWithoutClosingSession(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			
			buscar();
			return forward(getListPage());
			
		} catch(Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
		return null;

	}
	
	/**
	 * Redireciona para página de cadastro de usuários.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorDistancia/listar.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String formUsuario() throws ArqException {
		populateObj(true);
		obj.setUsuario(new Usuario());
		obj.setTurmas((ArrayList<Turma>)getDAO(TurmaVirtualDao.class).findTurmasEADHabilitadasByPessoa(obj.getPessoa()));
		prepareMovimento(SigaaListaComando.CADASTRAR_USUARIO_TUTOR);
		return forward("/ead/TutorDistancia/cadastro_usuario.jsp");
	}

	/**
	 * Validação de dados do cadastro de usuário.<br/><br/>
	 * Método não invocado por JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		mensagens = new ListaMensagens();
		if (obj.getUsuario().getLogin().contains(" ")) 
			mensagens.addMensagem(MensagensEAD.LOGIN_COM_ESPACO);
		ValidatorUtil.validateRequired(obj.getUsuario().getLogin(), "Login", mensagens);
		if (obj.getUsuario().getEmail() == null || obj.getUsuario().getEmail().equals(""))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Email");
		ValidatorUtil.validateEmail(obj.getUsuario().getEmail(), "Email", mensagens);
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return true;
		}
		return false;
	}
	
	/**
	 * Direciona para a página de listagem de tutor orientador. 
	 * 
	 * Chamado por:
	 * sigaa.war/ead/TutorOrientador/lista.jsp
	 * @throws DAOException 
	 */
	public String listar() throws DAOException {
		initObj();
		buscar();
		return forward(getListPage());
	}
	
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getAno() {
		return ano;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setNomeDocente(String nomeDocente) {
		this.nomeDocente = nomeDocente;
	}

	public String getNomeDocente() {
		return nomeDocente;
	}

	public void setBuscarEm(List<String> buscarEm) {
		this.buscarEm = buscarEm;
	}

	public List<String> getBuscarEm() {
		return buscarEm;
	}

	public void setCodigoComponente(String codigoComponente) {
		this.codigoComponente = codigoComponente;
	}

	public String getCodigoComponente() {
		return codigoComponente;
	}

	/**
	 * Seta a pessoa do {@link TutorOrientador} trabalhado.
	 */
	public void setPessoa(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}
	
	public void setTurmasBuscadas(ArrayList<Turma> turmasBuscadas) {
		this.turmasBuscadas = turmasBuscadas;
	}

	public ArrayList<Turma> getTurmasBuscadas() {
		return turmasBuscadas;
	}

	public void setTurmasSelecionadas(ArrayList<Turma> turmasSelecionadas) {
		this.turmasSelecionadas = turmasSelecionadas;
	}

	public ArrayList<Turma> getTurmasSelecionadas() {
		return turmasSelecionadas;
	}

	public void setBuscaSomenteAtivos(boolean buscaSomenteAtivos) {
		this.buscaSomenteAtivos = buscaSomenteAtivos;
	}

	public boolean isBuscaSomenteAtivos() {
		return buscaSomenteAtivos;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}

	public boolean isBuscaNome() {
		return buscaNome;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setTurmasRemovidas(ArrayList<Turma> turmasRemovidas) {
		this.turmasRemovidas = turmasRemovidas;
	}

	public ArrayList<Turma> getTurmasRemovidas() {
		return turmasRemovidas;
	}

	public void setTurmasTutor(ArrayList<Turma> turmasTutor) {
		this.turmasTutor = turmasTutor;
	}

	public ArrayList<Turma> getTurmasTutor() {
		return turmasTutor;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setNomeComponente(String nomeComponente) {
		this.nomeComponente = nomeComponente;
	}

	public String getNomeComponente() {
		return nomeComponente;
	}

	public void setFiltroNomeDocente(boolean filtroNomeDocente) {
		this.filtroNomeDocente = filtroNomeDocente;
	}

	public boolean isFiltroNomeDocente() {
		return filtroNomeDocente;
	}

	public void setFiltroCodigoComponente(boolean filtroCodigoComponente) {
		this.filtroCodigoComponente = filtroCodigoComponente;
	}

	public boolean isFiltroCodigoComponente() {
		return filtroCodigoComponente;
	}

	public void setFiltroNomeComponente(boolean filtroNomeComponente) {
		this.filtroNomeComponente = filtroNomeComponente;
	}

	public boolean isFiltroNomeComponente() {
		return filtroNomeComponente;
	}

	public void setFiltroPolo(boolean filtroPolo) {
		this.filtroPolo = filtroPolo;
	}

	public boolean isFiltroPolo() {
		return filtroPolo;
	}

	public void setAnoTutor(Integer anoTutor) {
		this.anoTutor = anoTutor;
	}

	public Integer getAnoTutor() {
		return anoTutor;
	}

	public void setPeriodoTutor(Integer periodoTutor) {
		this.periodoTutor = periodoTutor;
	}

	public Integer getPeriodoTutor() {
		return periodoTutor;
	}

	public void setBuscaAnoPeriodo(boolean buscaAnoPeriodo) {
		this.buscaAnoPeriodo = buscaAnoPeriodo;
	}

	public boolean isBuscaAnoPeriodo() {
		return buscaAnoPeriodo;
	}

	public void setBuscaDisciplina(boolean buscaDisciplina) {
		this.buscaDisciplina = buscaDisciplina;
	}

	public boolean isBuscaDisciplina() {
		return buscaDisciplina;
	}

	public void setNomeDisciplina(String nomeDisciplina) {
		this.nomeDisciplina = nomeDisciplina;
	}

	public String getNomeDisciplina() {
		return nomeDisciplina;
	}

}