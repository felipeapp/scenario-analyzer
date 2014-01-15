/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 08/10/2007 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.ws.WebServiceException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.integracao.dto.FormacaoAcademicaDTO;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pid.dao.PlanoIndividualDocenteDao;
import br.ufrn.sigaa.pid.dominio.PlanoIndividualDocente;
import br.ufrn.sigaa.prodocente.relatorios.jsf.RelatorioTodaProdutividadeMBean;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * MBean que controla o acesso aos dados do portal p�blico dos docentes da Institui��o.
 *
 * @author Ricardo Wendell
 *
 */
//@SuppressWarnings("unchecked")
public class PortalPublicoDocenteMBean extends SigaaAbstractController<Object> {

	/** Respons�vel pelo armazenamento do servidor. */
	Servidor docente;

	/** Collection respons�vel pelo armazenamento das Forma��es acad�micas */
	Collection<FormacaoAcademicaDTO> formacoes = new ArrayList<FormacaoAcademicaDTO>();

	/** Respons�vel por mostrar toda a produ��o de um docente */
	RelatorioTodaProdutividadeMBean produtividade;

	/** Respons�vel pelo armazenamento do perfil da Pessoa, no portal p�blico Docente. */
	private PerfilPessoa perfil;
	
	/** Respons�vel pelo armazenamento do Usu�rio no portal p�blico Docente */
	private Usuario usuario;
	
	/** Cole��o de Turma de n�vel infantil */
	private Collection<Turma> turmasInfantil;

	/** Cole��o de Turma de n�vel m�dio */
	private Collection<Turma> turmasMedio;

	/** Cole��o de Turma de n�vel t�cnico */
	private Collection<Turma> turmasTecnico;

	/** Cole��o de Turma de Gradua��o */
	private Collection<Turma> turmasGraduacao;
	
	/** Cole��o de Turma de P�s Gradua��o */
	private Collection<Turma> turmasPosGraduacao;

	/** Define o tamanho m�nimo que dever� ter o nome do docente na busca */
	public static final int MINIMO_NOME_DOCENTE = 4;
	
	public PortalPublicoDocenteMBean() {
		reset();
	}
	
	/**
	 * Para ver o coment�rio verificar o link.
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractController#getCreate()
	 */
	@Override
	public String getCreate() {
		reset();
		return "";
	}

	/**
	 * Inicializa os dados necess�rios para o portal p�blico do docente
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/docente/cabecalho.jsp</li>
	 *    <li>/sigaa.war/public/docente/menu.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String getIniciar() throws NegocioException, ArqException, RemoteException {

		Integer siape = null;
		try {
			siape = Integer.valueOf(getCurrentRequest().getParameter("siape")).intValue();
		} catch (Exception e) {
		}
		if (siape == null)
			return "";
		
		// Buscar servidor
		ServidorDao dao = getDAO(ServidorDao.class);
		docente = dao.findBySiape(siape, Ativo.SERVIDOR_ATIVO, Ativo.APOSENTADO, Ativo.EXCLUIDO, Ativo.CEDIDO);

		if ( isEmpty(docente) || !docente.isDocente() ) 
			return redirect("/public/");
		
		try{
			// Popula uma cole��o  de forma��o acad�mica ativa do docente
			FormacaoAcademicaRemoteService service = getMBean("formacaoAcademicaInvoker");
			formacoes =  service.consultarFormacaoAcademica(docente.getId(), null, null, null, null, null, null);		
		} catch (WebServiceException e) {
			addMensagemWarning("Forma��o acad�mica indispon�vel no momento. " +
								"N�o foi poss�vel estabelecer um contato com o servi�o.");
		}
		
		// Popular MBean de Produ��o Intelectual
		produtividade = (RelatorioTodaProdutividadeMBean) getMBean("todaProducao");
		produtividade.setServidor(getDocente());
		produtividade.setAnoInicial(1900);
		produtividade.setAnoFinal(CalendarUtils.getAnoAtual());
		produtividade.emiteRelatorioPublico();
		perfil = PerfilPessoaDAO.getDao().get(docente.getIdPerfil());

		UsuarioDao usuarioDao = getDAO(UsuarioDao.class);
		usuario = usuarioDao.findPrimeiroUsuarioByPessoa(docente.getPessoa().getId());
		
		turmasInfantil = null;
		turmasMedio = null;
		turmasTecnico = null;
		turmasGraduacao = null;
		turmasPosGraduacao = null;

		return "";
	}

	/**
	 * M�todo de busca de docente, validando a inser��o de dados obrigat�rios para a filtragem.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\public\docente\busca_docentes.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String buscarDocentes() throws DAOException {

		// Valida��es
		if ( StringUtils.isEmpty(docente.getNome()) && docente.getUnidade().getId() == 0 ) {
			addMensagemErro("Especifique um nome ou escolha uma unidade para realizar a busca");
			return null;
		}

		if ( !StringUtils.isEmpty(docente.getNome()) ) {
			docente.getPessoa().setNome( docente.getNome().replaceAll("%", "") );

			if (docente.getNome().length() < MINIMO_NOME_DOCENTE) {
				addMensagemErro("� necess�rio informar pelo menos 4 caracteres do nome do docente");
				return null;
			}
		}

		ServidorDao servidorDao = getDAO(ServidorDao.class);
		Collection<Servidor> docentes = servidorDao.findDocenteByUnidadeNome(docente.getUnidade().getId(), docente.getNome());

		if ( docentes.isEmpty() ) {
			addMensagemErro("Nenhum docente foi encontrado de acordo com os crit�rios de busca informados");
		}

		getCurrentRequest().setAttribute("docentes", docentes);
		return null;
	}

	/**
	 * Coment�rio pode ser visualizado no link indicado.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/docente/busca_docentes.jsp</li>
	 *   </ul>
	 * @see br.ufrn.arq.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		reset();
		return forward("/public/index.jsp");
	}

	/**
	 * Reinicializa os atributos para visualiza��o do portal p�blico do docente
	 * M�todo n�o invocado por JSP.
	 */
	private void reset() {
		docente = new Servidor();
		docente.setUnidade( new Unidade() );
	}

	/**
	 * Buscar turmas de p�s-gradua��o que o docente ministrou
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/public/docente/disciplinas.jsp</li>
	 *   </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> getTurmasPosGraduacao() throws DAOException {
		if (turmasPosGraduacao == null) {
			List<Character> niveis  = new ArrayList<Character>();
			niveis.add(NivelEnsino.STRICTO);
			niveis.add(NivelEnsino.LATO);
			turmasPosGraduacao = getTurmas(niveis);
		}
		return turmasPosGraduacao;
	}

	/**
	 * Buscar turmas de gradua��o que o docente ministrou
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/public/docente/disciplinas.jsp</li>
	 *   </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> getTurmasGraduacao() throws DAOException {
		if (turmasGraduacao == null) {
			turmasGraduacao = getTurmas( NivelEnsino.GRADUACAO);
		}
		return turmasGraduacao;
	}

	/**
	 * Buscar turmas de t�cnico que o docente ministrou
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/public/docente/disciplinas.jsp</li>
	 *   </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> getTurmasTecnico() throws DAOException {
		if (turmasTecnico == null) {
			turmasTecnico = getTurmas(NivelEnsino.TECNICO);
		}
		return turmasTecnico;
	}

	/**
	 * Buscar turmas de n�vel medio que o docente ministrou
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/public/docente/disciplinas.jsp</li>
	 *   </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> getTurmasMedio() throws DAOException {
		if (turmasMedio == null) {
			turmasMedio = getTurmas( NivelEnsino.MEDIO);
		}
		return turmasMedio;
	}
	
	/**
	 * Buscar turmas de nivel infantil que o docente ministrou
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/public/docente/disciplinas.jsp</li>
	 *   </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> getTurmasInfantil() throws DAOException {
		if (turmasInfantil == null) {
			turmasInfantil = getTurmas( NivelEnsino.INFANTIL);
		}
		return turmasInfantil;
	}
	
	/**
	 * M�todo de implementa��o da busca de turmas por n�vel e docente
	 * M�todo n�o invocado por JSP's.
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	private Collection<Turma> getTurmas(char nivel) throws DAOException {
		List<Character> niveis  = new ArrayList<Character>();
		niveis.add(nivel);
		return getTurmas(niveis);
	}
	
	/**
	 * M�todo de implementa��o da busca de turmas por docente e uma lista de n�veis de ensino
	 * M�todo n�o invocado por JSP's.
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	private Collection<Turma> getTurmas(List<Character> nivel) throws DAOException {
		Collection<Turma> turmas  = new ArrayList<Turma>(0);
	
		if(!isEmpty(docente))
			turmas = getDAO(TurmaDao.class).findByDocente(getDocente(), null, null, nivel, null,
					false, true,  new Integer[0]);
			List<Turma> ts = null;
			if (turmas != null) {
				ts = new ArrayList<Turma>(turmas);
				Collections.sort(ts, new Comparator<Turma>() {
					@Override
					public int compare(Turma t1, Turma t2) {
						if (t1.getAno() < t2.getAno())
							return 1;
						if (t1.getAno() > t2.getAno())
							return -1;
						
						
						if (t1.getPeriodo() < t2.getPeriodo())
							return 1;
						if (t1.getPeriodo() > t2.getPeriodo())
							return -1;
						
						return 0;
					}
				});
			}
					
		return ts;
	}

	/**
	 * Projetos de pesquisa que o docente participou
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/public/docente/pesquisa.jsp</li>
	 *   </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProjetoPesquisa> getProjetosPesquisa() throws DAOException {
		Collection<ProjetoPesquisa> projetosPesquisa = new ArrayList<ProjetoPesquisa>(0);
		if(!isEmpty(docente))
			projetosPesquisa = getDAO(ProjetoPesquisaDao.class).findByMembro(docente.getPessoa());
		return projetosPesquisa;
	}
	
	/**
	 * Exibe todos os PIDS com status HOMOLOGADO do docente selecionado 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/public/docente/listagem_pids.jsp</li>
	 *   </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<PlanoIndividualDocente> getListagemPIDDocente() throws DAOException {
		Collection<PlanoIndividualDocente> pids = new ArrayList<PlanoIndividualDocente>();
		if(!isEmpty(docente))
			pids = getDAO(PlanoIndividualDocenteDao.class).findAllPIDsHomologadosByServidor(docente);
		return pids;
	}
	
	/**
	 * Projetos de extens�o que o docente coordena
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>\SIGAA\app\sigaa.ear\sigaa.war\public\docente\extensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> getProjetosExtensaoCoordena() throws DAOException {

		Collection<AtividadeExtensao> atividadeExtensao = new ArrayList<AtividadeExtensao>();
		Collection<AtividadeExtensao> atividadeExtensaoValidas = new ArrayList<AtividadeExtensao>();
		if(!isEmpty(docente)){
			atividadeExtensao = getDAO(AtividadeExtensaoDao.class).findAtivasByServidor(docente, null, FuncaoMembro.COORDENADOR);
		}
		// evita que seja exibida a��es que n�o foram executadas
		for (AtividadeExtensao atv : atividadeExtensao) {
			if(atv.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO
					|| atv.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_CONCLUIDO
					|| atv.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO
					|| atv.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO){
				atividadeExtensaoValidas.add(atv);
			}
		}
		return atividadeExtensaoValidas;
		
	}
	
	/**
	 * Projetos de extens�o que o docente participa
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li> \SIGAA\app\sigaa.ear\sigaa.war\public\docente\extensao.jsp</li>
	 *   </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> getProjetosExtensaoParticipa() throws DAOException {
	
		Collection<AtividadeExtensao> atividadeExtensaoParticipa = new ArrayList<AtividadeExtensao>();
		Collection<AtividadeExtensao> atividadeExtensaoValidas = new ArrayList<AtividadeExtensao>();
		if(!isEmpty(docente)){
			atividadeExtensaoParticipa = getDAO(AtividadeExtensaoDao.class).findAtivasByServidor(docente, null, null);
		}
		// evita que seja exibida a��es que n�o foram executadas
		for (AtividadeExtensao atv : atividadeExtensaoParticipa) {
			if(atv.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO
					|| atv.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_CONCLUIDO
					|| atv.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO
					|| atv.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO){
				atividadeExtensaoValidas.add(atv);
			}
		}
		return atividadeExtensaoValidas;
		
	}

	/**
	 * Projetos de monitoria que o docente participou
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/public/docente/monitoria.jsp</li>
	 *   </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProjetoEnsino> getProjetosMonitoria() throws DAOException {
		Collection<ProjetoEnsino> projetos = new ArrayList<ProjetoEnsino>(0);
		if(!isEmpty(docente))
			projetos = getDAO(ProjetoMonitoriaDao.class).findValidosByServidor( docente.getId(), null );
		return projetos; 
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public Collection<FormacaoAcademicaDTO> getFormacoes() {
		return formacoes;
	}

	public void setFormacoes(Collection<FormacaoAcademicaDTO> formacoes) {
		this.formacoes = formacoes;
	}

	public RelatorioTodaProdutividadeMBean getProdutividade() {
		return produtividade;
	}

	public void setProdutividade(RelatorioTodaProdutividadeMBean produtividade) {
		this.produtividade = produtividade;
	}

	public PerfilPessoa getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}

	public Turma getTurma() {
		return new TurmaVirtualMBean().turma();
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}