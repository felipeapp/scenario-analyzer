/**
 *
 */
package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.dao.ead.TutoriaAlunoDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.dominio.TutoriaAluno;
import br.ufrn.sigaa.ead.negocio.TutoriaAlunoMov;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * MBean das operações de associar tutores a alunos
 *
 * @author Andre M Dantas
 *
 */
@Component("tutoriaAluno") @Scope("session")
public class TutoriaAlunoMBean extends SigaaAbstractController<TutoriaAluno> {

	/** Link para o menu SEDIS. */
	public static final String JSP_MENU_SEDIS = "menuSEDIS";
	/** Link para o formulário que escolhe o tutor pra associar a um aluno */
	public static final String JSP_TUTOR = "/ead/tutoria_aluno/tutor.jsp";
	/** Link para a página de busca de discentes */
	public static final String JSP_BUSCA = "/ead/tutoria_aluno/busca.jsp";
	/** Link para o formulário que associa aluno ao tutor */
	public static final String JSP_DADOS_ORIENTACAO = "/ead/tutoria_aluno/form.jsp";
	
	/** Fitros de busca. */
	private boolean buscaMatricula, buscaNome, buscaAnoPeriodo;
	
	/** Tutor da tutoria movimentada. */
	private TutorOrientador tutor = new TutorOrientador();
	
	/** Lista de discentes que serão associados a um tutor para forma a tutoria */
	private Collection<DiscenteGraduacao> discentes = new HashSet<DiscenteGraduacao>();
	
	/** Resultado da busca de discentes */
	private Collection<DiscenteGraduacao> resultadoBusca = new HashSet<DiscenteGraduacao>();
	
	/** Lista de tutorias de um pólo, de um discente ou de um tutor. */
	private Collection<TutoriaAluno> tutorias = new ArrayList<TutoriaAluno>(); 
	
	/** Lista de tutores de um pólo selecionado. */
	private Collection<SelectItem> possiveisTutores = new ArrayList<SelectItem>();

	/** Lista de todos os pólos. */
	private Collection<SelectItem> polos;
	
	/** Lista com todos os cursos à distáncia */
	private Collection<SelectItem> cursosComPolo;
	
	/** Pólo da Tutoria */
	private Polo polo = new Polo();
	
	/** Curso da tutoria */
	private Curso cursoReferencia = new Curso();
	
	/**
	 * atributos para auxiliar na busca dos discentes
	 */
	private Integer anoIngresso, periodoIngresso;
	
	/** Discente da tutoria movimentada. */
	private DiscenteGraduacao discente = new DiscenteGraduacao();
	
	/** Atributo auxiliar na consulta de uma tutoria. */
	private String consultaAluno;

	/** Atributo auxiliar na consulta de uma tutoria. */
	private String consultaTutor;
	
	/** Se a tutoria será removida ou alterada. */
	private boolean remover = false;
	
	/** Opcao de Busca */
	String opcaoBusca;
	
	/** Lista utilizada para armazenar os discentes. */
	private ArrayList<DiscenteGraduacao> listaDiscentes = new ArrayList<DiscenteGraduacao>();
	
	/** PAGINAÇÃO */
	/** Número total de todas as tutorias. */
	private Integer totalTutorias;
	/** Número total de páginas. */
	private Integer totalPaginasPaginacao;
	/** Quantidade de tutorias por página. */
	private Integer quantTutoriasPagina = 50;
	/** Página atual na paginação. */
	private int paginaAtual = 0;
	/** Número do primeiro registro de uma página. */
	private Integer primeiroRegistro;
	/** Se está buscando todas as tutorias. */
	private Boolean buscaTodos = false;
	/** Se a tutoria foi alterada. Usada pra reconhecer se o primeiro registro deve ser zerado. */
	private boolean alterada = false;
	/** Combo box com as páginas */
	ArrayList<SelectItem> paginas = new ArrayList<SelectItem>();
	
	public TutoriaAlunoMBean() {
		obj = new TutoriaAluno();
	}
	
	/**
	 * inicia o caso de uso de associação de orientadores a tutores
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD);
		prepareMovimento(SigaaListaComando.CADASTRAR_TUTORIA_ALUNO);
		
		discente.setPessoa(new Pessoa());
		return forward(JSP_TUTOR);
	}
	
	/**
	 * Seleciona o orientador e envia para página com os dados do mesmo 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/tutor.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String selecionarOrientador() throws DAOException{
		
		if( tutor == null || tutor.getId() == 0 ){
			addMensagemErro("Selecione um docente para cadastrar suas orientações acadêmicas.");
			return null;
		}
		
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_TUTORIA_ALUNO.getId());
		GenericDAO dao = getGenericDAO();
		tutor = dao.findByPrimaryKey(tutor.getId(), TutorOrientador.class);
		resultadoBusca = null;
		listaDiscentes = null;
		discentes = null;
		
		return telaDadosOrientacao();
	}
	
	/**
	 * adiciona os discentes selecionados a lista para criar a associação com o tutor
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/busca.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String adicionarDiscentes() throws DAOException{
		
		String[] selecionados = getParameterValues("selecionados");
		String[] selecionadosLista = getParameterValues("selecionados-lista");
		Collection<DiscenteGraduacao> discentesSelecionados = new HashSet<DiscenteGraduacao>();
		
		if ( discentes == null )
			discentes = new HashSet<DiscenteGraduacao>();
		
		if (selecionados == null && selecionadosLista == null) {
			addMensagemErro("É necessário selecionar no mínimo um discente.");
			return null;
		}
		
		boolean containsDiscentesRepetidos = false;
		TutoriaAlunoDao dao = getDAO( TutoriaAlunoDao.class);
	
		if ( selecionados != null ){
			for (int i = 0; i < selecionados.length; i++) {
				int id = Integer.parseInt(selecionados[i]);
				
				DiscenteGraduacao d = recuperarDiscenteBuscadoById(id);
				
				if(!discenteJaAdicionado(d))
					discentesSelecionados.add( d );
				else
					containsDiscentesRepetidos = true;
			}
		}
		if ( selecionadosLista != null ) {
			for (int i = 0; i < selecionadosLista.length; i++) {
				int id = Integer.parseInt(selecionadosLista[i]);
				
				DiscenteGraduacao d = recuperarDiscenteListadoById(id);
				
				if(!discenteJaAdicionado(d))
					discentesSelecionados.add( d );
				else
					containsDiscentesRepetidos = true;
			}
		}
		
		if(containsDiscentesRepetidos)
			addMensagemWarning("Os discentes que já haviam sido adicionados foram ignorados.");
		
		StringBuffer nomesDiscentes = new StringBuffer();
		String br = "<br/>";
		for( DiscenteGraduacao d : discentesSelecionados ){
			
			TutoriaAluno ta = dao.findUltimoByAluno(d.getId());
			if( ta != null && ta.getId() != 0 ){
				nomesDiscentes.append( ta.getAluno().toString() +" - TUTOR: " + ta.getTutor().getNome() + br );
			}
			
		}
		if( nomesDiscentes.length() > 0 ){
			String msg = " Cada discente só pode ter um tutor por vez. Os seguintes discentes já possuem um tutor cadastrado que serão removidos para que o novo seja gravado: <br/> <br/>" + nomesDiscentes;
			addMessage(msg, TipoMensagemUFRN.WARNING);
		}
		
		discentes.addAll( discentesSelecionados );
		
		return telaDadosOrientacao();
	}

	/**
	 * adiciona os discentes selecionados a lista para criar a associação com o tutor
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/busca.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String adicionarDiscentesLista() throws DAOException{
		
		if ( listaDiscentes == null )
			listaDiscentes = new ArrayList<DiscenteGraduacao>();
		
		String[] selecionados = getParameterValues("selecionados");
		
		if (selecionados == null) {
			addMensagemErro("É necessário selecionar no mínimo um discente.");
			return null;
		}
		
		boolean containsDiscentesRepetidos = false;

		for (int i = 0; i < selecionados.length; i++) {
			int id = Integer.parseInt(selecionados[i]);
			
			DiscenteGraduacao d = recuperarDiscenteBuscadoById(id);
			
			if(!discenteJaAdicionadoLista(d))
				listaDiscentes.add( d );
			else
				containsDiscentesRepetidos = true;
		}
		
		if(containsDiscentesRepetidos)
			addMensagemWarning("Os discentes que já haviam sido adicionados foram ignorados.");
	
		Collections.sort(listaDiscentes,new Comparator<DiscenteGraduacao>(){
			public int compare(DiscenteGraduacao d1, DiscenteGraduacao d2) {
				String nome = StringUtils.toAscii(d1.getNome());
				return nome.compareToIgnoreCase(StringUtils.toAscii( d2.getNome() ));
			}
		});
		
		return telaBusca();
	}
	
	/**
	 * Verifica se o discente passado já foi adicionado na listagem.
	 * 
	 * @param discente
	 * @return
	 */
	private boolean discenteJaAdicionado(DiscenteGraduacao discente) {
		if(!isEmpty(discentes)) {
			for (DiscenteGraduacao d : discentes) {
				if(d.getId() == discente.getId())
					return true;
			}
		}
		
		return false;
	}

	/**
	 * Limpa a lista de discentes
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/busca.jsp</li>
	 * </ul>
	 * @param discente
	 * @return
	 */
	public void limparListaDiscentes() {
		listaDiscentes.clear();
	}
	
	/**
	 * Verifica se o discente passado já foi armazenado na lista auxiliar de discentes.
	 * 
	 * @param discente
	 * @return
	 */
	private boolean discenteJaAdicionadoLista(DiscenteGraduacao discente) {
		if(!isEmpty(listaDiscentes)) {
			for (DiscenteGraduacao d : listaDiscentes) {
				if(d.getId() == discente.getId())
					return true;
			}
		}
		
		return false;
	}
	/**
	 * Recupera, dentro da listagem de discentes previamente buscados, um discente
	 * de acordo com seu id.
	 * 
	 * @param id
	 * @return
	 */
	private DiscenteGraduacao recuperarDiscenteBuscadoById(Integer id) {
		DiscenteGraduacao discenteEncontrado = null;
		
		if(resultadoBusca != null) {
			for (DiscenteGraduacao d : resultadoBusca) {
				if(d.getId() == id) {
					discenteEncontrado = d;
					break;
				}
			}
		}
		
		return discenteEncontrado;
	}
	
	/**
	 * Recupera os discentes armazenados em lista
	 * 
	 * @param id
	 * @return
	 */
	private DiscenteGraduacao recuperarDiscenteListadoById(int id) {
		DiscenteGraduacao discenteEncontrado = null;
		
		if(listaDiscentes != null) {
			for (DiscenteGraduacao d : listaDiscentes) {
				if(d.getId() == id) {
					discenteEncontrado = d;
					break;
				}
			}
		}
		
		return discenteEncontrado;
	}
		
	/**
	 * Remove o discente 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String removerDiscente() throws DAOException{
		
		Integer id = getParameterInt("id");
		if( id == null || id == 0 ){
			addMensagemErro("Selecione um discente.");
			return null;
		}
		
		GenericDAO dao = getGenericDAO();
		Discente disc = dao.findByPrimaryKey(id, Discente.class);
		
		for (Iterator<DiscenteGraduacao> iter = discentes.iterator(); iter.hasNext();) {
			DiscenteGraduacao d = iter.next();
			if( d.getId() == disc.getId() ){
				iter.remove();
			}
		}
		
		return telaDadosOrientacao();
	}
	
	/**
	 * Realiza a busca por discentes de acordo com o tipo de busca
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/busca.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String buscarDiscente() throws DAOException{
		if(!buscaMatricula && !buscaNome && !buscaAnoPeriodo) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}
		
		Long matricula = null;
		String nome = null;
		Integer ano = null, periodo = null;
		
		if(buscaMatricula) {
			if(isEmpty(discente.getMatricula()))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Matrícula");
			else
				matricula = discente.getMatricula();
		}
		if(buscaNome) {
			if(isEmpty(discente.getPessoa().getNome()))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome do Discente");
			else
				nome = discente.getNome();
		}
		if(buscaAnoPeriodo) {
			if(isEmpty(anoIngresso))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano de Ingresso");
			else
				ano = anoIngresso;
			if(isEmpty(periodoIngresso))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período de Ingresso");
			else
				periodo = periodoIngresso;
		}
		
		if(hasErrors())
			return null;
		
		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		
		try {
			resultadoBusca = dao.findDiscentesEad(tutor.getPoloCurso().getPolo(), tutor.getPoloCurso().getCurso(), nome, matricula, ano, periodo);
		} finally {
			dao.close();
		}
		
		return telaBusca();
	}
	
	/**
	 * Cadastra a tutoria de aluno
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() {

		if ( !checkOperacaoAtiva(SigaaListaComando.CADASTRAR_TUTORIA_ALUNO.getId()) ){
			return cancelar();
		}	
		if ( isEmpty(discentes) ) {
			addMensagemErro("Nenhum discente selecionado.");
			return null;
		}
		TutoriaAlunoMov mov = new TutoriaAlunoMov();
		mov.setTutor(tutor);
		mov.setDiscentes(discentes);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_TUTORIA_ALUNO);
		
		try {
			execute(mov, getCurrentRequest());
			addMessage("A associação de discentes ao tutor foi realizada com sucesso.", TipoMensagemUFRN.INFORMATION);
			return cancelar();
		} catch (NegocioException e) {
			notifyError(e);
			addMensagens( e.getListaMensagens() );
			e.printStackTrace();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro inesperado. Contacte a administração do sistema. <br>" + e.getMessage() );
			e.printStackTrace();
		} 
		
		return null;
	}
	
	/**
	 * Remove a tutoria de aluno 
	 * Método não invocado por JSPs
	 * @return
	 * @throws ArqException
	 */
	public String removerOrientacao() throws ArqException {

		prepareMovimento(SigaaListaComando.REMOVER_TUTORIA_ALUNO);
		
		Integer id = getParameterInt("id");
		if( id == null || id == 0 ){
			addMensagemErro("Nenhuma tutoria selecionada.");
			return null;
		}
		
		TutoriaAluno tutoria = new TutoriaAluno(id);
		
		TutoriaAlunoMov mov = new TutoriaAlunoMov();
		mov.setTutoria(tutoria);

		mov.setCodMovimento(SigaaListaComando.REMOVER_TUTORIA_ALUNO);
		try {
			execute(mov, getCurrentRequest());
			addMessage("Operação realizada com sucesso!",TipoMensagemUFRN.INFORMATION);
		}catch(NegocioException e){
			notifyError(e);
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}catch (Exception e) {
			addMensagemErro("Erro Inesperado: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		return forward(getListPage());
	}
	
	/**
	 * Realiza a busca de acordo com os critérios (pode ser discente, orientador ou todos)
	 * Método não invocado por JSPs
	 */
	@Override
	public String buscar() throws Exception {
		String param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o critério de busca.");
			return null;
		}

		TutoriaAlunoDao dao = getDAO(TutoriaAlunoDao.class);
		if ("discente".equalsIgnoreCase(param))
			tutorias = dao.findAtivoByDiscenteCurso(tutor.getPoloCurso().getPolo(), discente.getId());
		else if ("orientador".equalsIgnoreCase(param))
			tutorias = dao.findAtivoByTutorCurso(tutor.getPoloCurso().getPolo(), tutor.getId());
		else if ("todos".equalsIgnoreCase(param))
			tutorias = dao.findAtivoByPolo( tutor.getPoloCurso().getPolo() );
		else
			tutorias = null;

		discente = new DiscenteGraduacao();
		tutor = new TutorOrientador();
		return null;
	}
	
	/**
	 * redireciona para a tela de busca de discentes
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaBusca(){
		return forward(JSP_BUSCA);
	}
	
	/**
	 * redireciona para a tela de seleção do orientador acadêmico
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaTutor(){
		return forward(JSP_TUTOR);
	}
	
	/**
	 * redireciona para a tela que contem os dados da orientação acadêmica
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/busca.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaDadosOrientacao(){
		return forward(JSP_DADOS_ORIENTACAO);
	}

	public boolean isBuscaMatricula() {
		return buscaMatricula;
	}

	public void setBuscaMatricula(boolean buscaMatricula) {
		this.buscaMatricula = buscaMatricula;
	}

	public boolean isBuscaNome() {
		return buscaNome;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}

	public boolean isBuscaAnoPeriodo() {
		return buscaAnoPeriodo;
	}

	public void setBuscaAnoPeriodo(boolean buscaAnoPeriodo) {
		this.buscaAnoPeriodo = buscaAnoPeriodo;
	}

	public TutorOrientador getTutor() {
		return tutor;
	}

	public void setTutor(TutorOrientador tutor) {
		this.tutor = tutor;
	}

	public Collection<DiscenteGraduacao> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<DiscenteGraduacao> discentes) {
		this.discentes = discentes;
	}

	public Collection<DiscenteGraduacao> getResultadoBusca() {
		return resultadoBusca;
	}

	public void setResultadoBusca(Collection<DiscenteGraduacao> resultadoBusca) {
		this.resultadoBusca = resultadoBusca;
	}

	public Collection<TutoriaAluno> getTutorias() {
		return tutorias;
	}

	public void setTutorias(Collection<TutoriaAluno> tutorias) {
		this.tutorias = tutorias;
	}

	public Integer getAnoIngresso() {
		return anoIngresso;
	}

	public void setAnoIngresso(Integer anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	public Integer getPeriodoIngresso() {
		return periodoIngresso;
	}

	public void setPeriodoIngresso(Integer periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}
	
	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	public Curso getCursoReferencia() {
		return cursoReferencia;
	}

	public void setCursoReferencia(Curso cursoReferencia) {
		this.cursoReferencia = cursoReferencia;
	}

	public Collection<SelectItem> getPossiveisTutores() throws DAOException {
		return possiveisTutores;
	}
	
	public void setPossiveisTutores(Collection<SelectItem> possiveisTutores) {
		this.possiveisTutores = possiveisTutores;
	}
	
	/**
	 * Retorna um SelectItem com todos os pólos
	 * Método não invocado por JSPs.
	 * @return the polos
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getPolos() throws DAOException {
		if (polos == null) {
			PoloDao dao = getDAO(PoloDao.class);
			Curso curso = null;

			if (getAcessoMenu().isCoordenadorCursoEad()) {
				curso = getCursoAtualCoordenacao();
				if (curso != null)
					polos = toSelectItems(dao.findAllPolos(curso), "id", "cidade.nomeUF");
				else {
					polos = toSelectItems(dao.findAllPolos(), "id", "cidade.nomeUF");
				}
			} else {
				polos = toSelectItems(dao.findAllPolos(), "id", "cidade.nomeUF");
			}
		}
		return polos;
	}

	public void setPolos(Collection<SelectItem> polos) {
		this.polos = polos;
	}
	
	/**
	 * Carrega os tutores de acordo com o curso selecionado
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/tutor.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarTutoresCurso(ValueChangeEvent e) throws DAOException {
		Integer idCurso = (Integer) e.getNewValue();
		TutorOrientadorDao tDao = getDAO(TutorOrientadorDao.class);
		Collection<TutorOrientador> c = tDao.findByPoloCurso(polo.getId(), idCurso);
		possiveisTutores = toSelectItems(c, "id", "pessoa.nome");
	}
	
	/**
	 * Carrega os tutores de acordo com o pólo selecionado
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/tutor.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarTutoresPolo(ValueChangeEvent e) throws DAOException {
		Integer idPolo = (Integer) e.getNewValue();
		TutorOrientadorDao tDao = getDAO(TutorOrientadorDao.class);
		Collection<TutorOrientador> c = tDao.findByPoloCurso(idPolo, cursoReferencia.getId());
		possiveisTutores = toSelectItems(c, "id", "pessoa.nome");
	}
	
	/**
	 * Retorna um combo com todos os cursos à distância.
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/tutor.jsp</li>
	 * <li> /sigaa.war/ead/tutorOrientador/logarComo.jsp</li>
	 * </ul>
	 * @return the cursosComPolo
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getCursosComPolo() throws DAOException {
		if (cursosComPolo == null) {
			CursoDao dao = getDAO(CursoDao.class);
			cursosComPolo = toSelectItems(dao.findAllCursosADistancia(), "id", "nome");
		}
		return cursosComPolo;
	}

	public void setCursosComPolo(Collection<SelectItem> cursosComPolo) {
		this.cursosComPolo = cursosComPolo;
	}

	public String getConsultaAluno() {
		return consultaAluno;
	}

	public void setConsultaAluno(String consultaAluno) {
		this.consultaAluno = consultaAluno;
	}

	public String getConsultaTutor() {
		return consultaTutor;
	}

	public void setConsultaTutor(String consultaTutor) {
		this.consultaTutor = consultaTutor;
	}
	
	/**
	 * Busca a tutoria de acordo com os critérios informados (pode ser aluno, tutor ou todos). 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/listagem.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception
	 */
	public String buscarTutoria() throws Exception {
		
		setResultadosBusca(null);
		
		opcaoBusca = getParameter("paramBusca");
		
		if ( opcaoBusca  == null) {
			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca");
			return null;
		}

		TutoriaAlunoDao dao = getDAO(TutoriaAlunoDao.class);
		
		
		if ("tutor".equalsIgnoreCase(opcaoBusca)) {
			setBuscaTodos(false);
			setResultadosBusca(dao.findByTutor(consultaTutor));
		}	
		else if ("aluno".equalsIgnoreCase(opcaoBusca)) {
			setBuscaTodos(false);
			setResultadosBusca(dao.findByAluno(consultaAluno));
		}	
		else if ("todos".equalsIgnoreCase(opcaoBusca)) {
			setBuscaTodos(true);
			totalTutorias = dao.countTutorias();
			if ( !alterada )
				primeiroRegistro = 0;
			totalPaginasPaginacao = ((Number) Math.ceil(totalTutorias.doubleValue()/quantTutoriasPagina)).intValue();
			setResultadosBusca(dao.findTutoriaPaginadas(primeiroRegistro, quantTutoriasPagina));
		}	
		else
			setResultadosBusca(new ArrayList<TutoriaAluno>());
		
		if((getResultadosBusca()!=null && getResultadosBusca().isEmpty()) || getResultadosBusca()==null) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}

		return null;
	}
	
	/**
	 * Busca os novos registros de uma página.
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/listagem.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public void page( Integer primeiroRegistroPagina ) throws DAOException {
		TutoriaAlunoDao dao = null;
		try {		
			primeiroRegistro = primeiroRegistroPagina;
			dao = getDAO(TutoriaAlunoDao.class);
			setResultadosBusca(dao.findTutoriaPaginadas(primeiroRegistro, quantTutoriasPagina));
		} finally{
			if ( dao != null )
				dao.close();
		}	
		
	}

	/**
	 * Muda uma página.
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/listagem.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public void changePage( ValueChangeEvent e ) throws DAOException {
		if ( e.getNewValue() != null ) {
			paginaAtual = (Integer) e.getNewValue();
			Integer primeiroRegistroPagina = paginaAtual*quantTutoriasPagina;
			page(primeiroRegistroPagina);
		}	
	}
	
	/**
	 * Cria combo box com as páginas.
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/listagem.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getListagemPaginas (){
				
		if ( totalTutorias > 0 && paginas.isEmpty()) {

			for (int a = 0; a < totalPaginasPaginacao; a++)	{
				SelectItem item = new SelectItem(String.valueOf(a), "Pag. " + (a+1));
				paginas.add(item);
			}
		} else if ( totalTutorias == 0 ) {
			SelectItem item = new SelectItem(String.valueOf(0), "Sem Páginas");
			paginas.add(item);
		}

		return paginas;
	}
	
	/**
	 * Define a página de alteração que será usada para alterar a tutoria do aluno
	 */
	@Override
	public String getFormPage() {
		return "/ead/tutoria_aluno/altera.jsp";
	}
	
	/**
	 * Carrega os possíveis tutores de determinado pólo
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/altera.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String getCarregarPossiveisTutores() throws DAOException {
		TutorOrientadorDao tDao = getDAO(TutorOrientadorDao.class);
		Collection<TutorOrientador> c = tDao.findByPolo(obj.getAluno().getPolo().getId());
		possiveisTutores = toSelectItems(c, "id", "pessoa.nome");
		return null;
	}
	
	/**
	 * Faz o processamento de pré-remoção
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/listagem.jsp</li>
	 * </ul>
	 */
	@Override
	public String preRemover() {
		try {
			prepareMovimento(ArqListaComando.ALTERAR);
			remover = true;
			GenericDAO dao = getGenericDAO();
			setId();

			this.obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		
		setConfirmButton("Remover ");

		afterPreRemover();
		setReadOnly(true);

		return forward(getFormPage());
	}

	/**
	 * Altera a tutoria e redireciona para a listagem de tutorias  
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/altera.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String alterar() throws Exception {
		
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		
		if (remover)
			obj.setAtivo(false);
		else
			prepareMovimento(ArqListaComando.ALTERAR);
			
		remover = false;
		
		checkChangeRole();

		beforeCadastrarAndValidate();

		if (obj != null) {
			erros = new ListaMensagens();
			ListaMensagens lista = obj.validate();

			if (lista != null && !lista.isEmpty()) {
				erros.addAll(lista.getMensagens());
			}
		}
			
		validateEntity(obj, erros);
		doValidate();

		String descDominio = null;
		try {
			descDominio = ReflectionUtils.evalProperty(obj, "descricaoDominio");
		} catch (Exception e) {}
			

		if (!hasErrors()) {
			beforeCadastrarAfterValidate();

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);

			if( !checkOperacaoAtiva(ArqListaComando.ALTERAR.getId()) )
				return cancelar();
					
			mov.setCodMovimento(ArqListaComando.ALTERAR);
					
			try {
				execute(mov);
				if (descDominio != null && !descDominio.equals("")) {
					addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, descDominio);
				} else 
					addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);		
			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return forward(getFormPage());
			}

			removeOperacaoAtiva();
			alterada = true;
		}	
		return listar();
	}
	
	/**
	 * Remove a tutoria
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> /sigaa.war/ead/tutoria_aluno/form.jsp</li>
	 * </ul> 
	 */
	@Override
	public String remover() throws ArqException {
		return super.remover();
	}
	
	/**
	 * Define e retorna a página de listagem
	 */
	@Override
	public String getListPage() {
		return "/ead/tutoria_aluno/listagem.jsf";
	}
	
	/**
	 * Redireciona para Consulta de Tutorias de Alunos
	 * 
	 * Chamado por:
	 * sigaa.war/ead/tutoria_aluno/listagem.jsp
	 */
	public String listar() {
		initObj();
		return forward(getListPage());
	}
	
	/**
	 * Inicializa os campos.
	 */
	private void initObj() {
		setResultadosBusca(new ArrayList<TutoriaAluno>());
		consultaTutor = "";
		consultaAluno = "";
	}

	public void setRemover(boolean remover) {
		this.remover = remover;
	}

	public boolean isRemover() {
		return remover;
	}

	public void setTotalTutorias(Integer totalTutorias) {
		this.totalTutorias = totalTutorias;
	}

	public Integer getTotalTutorias() {
		return totalTutorias;
	}

	public Integer getPaginaAtual() {
		return paginaAtual;
	}
	
	public int getTotalPaginasPaginacao() {
		return totalPaginasPaginacao;
	}

	public void setBuscaTodos(Boolean buscaTodos) {
		this.buscaTodos = buscaTodos;
	}

	public Boolean getBuscaTodos() {
		return buscaTodos;
	}

	public void setListaDiscentes(ArrayList<DiscenteGraduacao> listaDiscentes) {
		this.listaDiscentes = listaDiscentes;
	}

	public ArrayList<DiscenteGraduacao> getListaDiscentes() {
		return listaDiscentes;
	}
	
	/*
	private String consultaAluno;

	private String consultaTutor;

	private Collection<SelectItem> possiveisTutores;

	public void setPossiveisTutores(Collection<SelectItem> possiveisTutores) {
		this.possiveisTutores = possiveisTutores;
	}

	public TutoriaAlunoMBean() {
		initObj();
	}

	private void initObj() {
		obj = new TutoriaAluno();
		obj.setAtivo(true);
		obj.setAluno(new DiscenteGraduacao());
		obj.setTutor(new TutorOrientador());
	}


	public String getConsultaAluno() {
		return consultaAluno;
	}

	public void setConsultaAluno(String consultaAluno) {
		this.consultaAluno = consultaAluno;
	}

	public String getConsultaTutor() {
		return consultaTutor;
	}

	public void setConsultaTutor(String consultaTutor) {
		this.consultaTutor = consultaTutor;
	}


	@Override
	public String preCadastrar() throws ArqException, RemoteException, NegocioException {
		checkRole(SigaaPapeis.SEDIS);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.TUTORIA_ALUNO_EAD);
		prepareMovimento(ArqListaComando.CADASTRAR);
		return buscaDiscenteMBean.popular();
	}

	@Override
	public String preRemover() {
		try {
			checkRole(SigaaPapeis.SEDIS);
		} catch (SegurancaException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		initObj();
		return super.preRemover();
	}

	@Override
	public String atualizar() {
		try {
			checkRole(SigaaPapeis.SEDIS);
		} catch (SegurancaException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		initObj();
		return super.atualizar();
	}

	@Override
	public String forwardCadastrar() {
		return "/ead/menu.jsp";
	}

	@Override
	public String cadastrar() throws NegocioException, ArqException {
		checkRole(SigaaPapeis.SEDIS);
		return super.cadastrar();
	}

	@Override
	protected void afterCadastrar() {
		initObj();
		setResultadosBusca(null);
	}

	@Override
	public String getFormPage() {
		return "/ead/tutoria_aluno/form.jsp";
	}

	@Override
	public String getListPage() {
		return "/ead/tutoria_aluno/lista.jsp";
	}

	@Override
	public String cancelar() {
		setResultadosBusca(null);
		String ret = null;
		if ("cadastrar".equalsIgnoreCase(getConfirmButton()))
			ret = JSP_MENU_SEDIS;
		else
			ret = redirectPage(getListPage());
		super.cancelar();
		return ret;
	}


	public String getCarregarPossiveisTutores() throws DAOException {
		TutorOrientadorDao tDao = (TutorOrientadorDao) getDAO(TutorOrientadorDao.class);
		Collection<TutorOrientador> c = tDao.findByPolo(obj.getAluno().getPolo().getId());
		possiveisTutores = toSelectItems(c, "id", "pessoa.nome");
		return null;
	}

	public String selecionaDiscente() {
		try {
			TutoriaAlunoDao dao = (TutoriaAlunoDao) getDAO(TutoriaAlunoDao.class);
			obj.setAluno((DiscenteGraduacao) dao.findByPrimaryKey(obj.getAluno().getId(), DiscenteGraduacao.class));
			if (obj.getAluno().getPolo()== null || obj.getAluno().getPolo().getId() == 0) {
				addMensagemErro("Esse aluno não possui um pólo associado.<br>" +
				"Provavelmente não é um aluno de curso de ensino a distância.");
				return null;
			}
			TutoriaAluno ultima = dao.findUltimoByAluno(obj.getAluno().getId());
			if (ultima != null) {
				addMensagemErro("Esse aluno atualmente está sob tutoria no seu pólo.<br>"
						+ "Primeiro deve-se submeter a data final da atual tutoria.");
				return null;
			}
		} catch (DAOException e) {
			e.printStackTrace();
			addMensagemErro("Não foi possível carregar a última tutoria desse aluno");
			return null;
		}
		return redirectPage(getFormPage());
	}

	public void setDiscente(Discente discente) {
		try {
			obj.setAluno(getGenericDAO().findByPrimaryKey(discente.getId(), DiscenteGraduacao.class));
		} catch (DAOException e) {
			e.printStackTrace();
			addMensagemErro("Não foi possível carregar o discente escolhido");
		}
	}
	@Override
	public String buscar() throws Exception {
		String param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca");
			return null;
		}

		TutoriaAlunoDao dao = (TutoriaAlunoDao) getDAO(TutoriaAlunoDao.class);
		if ("tutor".equalsIgnoreCase(param))
			setResultadosBusca(dao.findByTutor(consultaTutor));
		else if ("aluno".equalsIgnoreCase(param))
			setResultadosBusca(dao.findByAluno(consultaAluno));
		else if ("todos".equalsIgnoreCase(param))
			setResultadosBusca(dao.findAll());
		else
			setResultadosBusca(null);

		initObj();
		return null;
	}

	public Collection<SelectItem> getPossiveisTutores() {
		return possiveisTutores;
	}*/


}
