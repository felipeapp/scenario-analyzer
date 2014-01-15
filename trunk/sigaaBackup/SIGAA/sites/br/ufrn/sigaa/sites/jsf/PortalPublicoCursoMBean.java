/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/04/2009
 *
 */
package br.ufrn.sigaa.sites.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.estagio.OfertaEstagioDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.ReconhecimentoDao;
import br.ufrn.sigaa.arq.dao.prodocente.TrabalhoFimCursoDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.Reconhecimento;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.tecnico.dao.EstruturaCurricularTecDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.estagio.dominio.OfertaEstagio;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalPublico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;

/**
 * MBean que controla o acesso aos dados dos cursos que serão exibidos na
 * página pública do SIGAA
 * 
 * @author Mário Rizzi
 */
@Component("portalPublicoCurso") @Scope("request")
public class PortalPublicoCursoMBean extends AbstractControllerPortalPublico{

	/** Atributo referente aos currículos associados ao curso de nível Graduação e Lato  */
	private Collection<Curriculo> curriculos;
	
	/** Atributo referente aos currículos associados aos cursos de nível Técnico  */
	private Collection<EstruturaCurricularTecnica> curriculosTecnicos;
	
	/** Atributo referente aos componentes associados aos cursos de nível Lato  */
	private Collection<ComponenteCurricular> componentesLato; 
	
	/** Atributo referente aos processos seletivos associados aos cursos  */
	private Collection<ProcessoSeletivo> processosSeletivos;
	
	/** Atributo referente aos coordenadores do curso  */
	private Collection<CoordenacaoCurso> coordenacoesCurso;
	
	/** Atributo referente as turmas associadas ao curso  */
	private Collection<Turma> turmas;
	
	/** Atributo referente aos discentes do curso  */
	private Collection<Discente> discentes;
	
	/** Atributo referente ao ano populado no formulário de busca das turmas.  */
	private Integer anoTurma = CalendarUtils.getAnoAtual();
	
	/** Atributo referente ao período populado no formulário de busca das turmas.  */
	private Integer periodoTurma = getPeriodoAtual();
	
	/** Atributo referente ao código da turma no formulário de busca das turmas.  */
	private String codigoTurma;
	
	/** Indica se a busca de Monografias será filtrada pelo Nome do Discente */
	private boolean filtroNome;
	/** Indica se a busca de Monografias será filtrada pelo Título da Monografia */
	private boolean filtroTitulo;
	/** Indica se a busca de Monografias será filtrada pelo Ano da Monografia */
	private boolean filtroAno;
	/** Filtro do Nome do discente */
	private String nome;
	/** Filtro do Título do Trabalho */
	private String titulo;
	/** Filtro do ano do trabalho */
	private Integer ano;
	
	/** Lista com o resultado da busca de Monografias */
	private List<TrabalhoFimCurso> listagemMonografias = new ArrayList<TrabalhoFimCurso>();	
	
	/** Lista de Ofertas de Estágio Disponíveis */
	private Collection<OfertaEstagio> listaOfertasEstagio = new ArrayList<OfertaEstagio>();
	
	/** Método construtor */
	public PortalPublicoCursoMBean() throws ArqException{

			
		
	}
	
	/**
	 * Inicializa os atributos utilizados no portal público do curso
	 * <br/>
	 * Chamado por
	 * <ul>
	 * 	<li>
	 * 	/SIGAA/app/sigaa.ear/sigaa.war/public/curso/include/cabecalho.jsp
	 * 	</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String getIniciar() throws ArqException{
		
		// Seta o id passado pela URL ou no objeto
		int id = isEmpty(getParameterInt("id", 0))?getId():getParameterInt("id", 0);
		
		if(!isEmpty(id)) {
			
			setCurso(getGenericDAO().findByPrimaryKey(id,Curso.class));
			setTipoPortalPublico(TipoPortalPublico.CURSO);
			getDetalhesSite();
			getRedirecionarSiteExterno();
	
		}
		
		if (!isEmpty(getCurso())) {
			if (validaPortal(getCurso().getUnidade())) {
				curriculos = null;
				curriculosTecnicos = null;
				listaOfertasEstagio = null;
			}
		}	
		
		return "";
		
	}
	
	/**
	 * Retorna todos os currículos do curso de graduação, stricto e lato  
	 * ordenados por área de concentração e componente
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/curso/curriculo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<Curriculo> getCurriculos()	throws ArqException {
		
		if (isEmpty(curriculos) && !isEmpty(getCurso()))
				curriculos = getDAO(EstruturaCurricularDao.class).findByCurso(getCurso().getId(), getCurso().getNivel());
		return curriculos;
		
	}
	
	/**
	 * Retorna todas as ofertas de estágio disponíveis para o curso 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/curso/estagios.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<OfertaEstagio> getOfertasEstagio()	throws ArqException {
		if (isEmpty(listaOfertasEstagio) && !isEmpty(getCurso())){
			OfertaEstagioDao dao = getDAO(OfertaEstagioDao.class);
			try {
				listaOfertasEstagio = dao.findbyCurso(getCurso(), true);				
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		return listaOfertasEstagio;
		
	}	
	
	/**
	 * Retorna todos os currículos do curso técnico
	 * ordenados por área de concentração e componente
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/curso/curriculo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<EstruturaCurricularTecnica> getCurriculosTecnicos()	throws ArqException {
		
		if (isEmpty(curriculosTecnicos) && !isEmpty(getCurso()))
			curriculosTecnicos = getDAO(EstruturaCurricularTecDao.class).findAll(getCurso().getUnidade().getId(),
						NivelEnsino.TECNICO, true, null); 
		return curriculosTecnicos;
		
	}
	
	/**
	 * Verifica se o portal pertence ao tipo acadêmico correto 
	 * @param unidade
	 * @return
	 */
	private boolean validaPortal(Unidade unidade) {
		//Verifica se o portal que esta acessando é referente ao tipoUnidadeAcademico 
		if(!isEmpty(unidade.getTipoAcademica()) && (unidade.getTipoAcademica().equals(TipoUnidadeAcademica.CENTRO) || 
		   unidade.getTipoAcademica().equals(TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA) ||
		   unidade.getTipoAcademica().equals(TipoUnidadeAcademica.DEPARTAMENTO) ||
		   unidade.getTipoAcademica().equals(TipoUnidadeAcademica.PROGRAMA_POS))) 
			return true;

		return false;
	}
	
	/**
	 * Retorna para página de busca dos centros
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/curso/include/menu.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cancelar() {		
		return redirect("/sigaa/public/home.jsf");
	}
	
	/**
	 * Retorna o chefe do departamento
	 *
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/curso/portal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<CoordenacaoCurso> getCoordenacoesCurso() throws ArqException {
		if(!isEmpty(getCurso()) && (coordenacoesCurso == null ||	coordenacoesCurso.size() == 0)){
			CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class);
			coordenacoesCurso =  dao.findCoordViceByCursoNivel(getCurso().getId(), getCurso().getNivel());
		}
			return coordenacoesCurso;
	}
	
	/**
	 * Retorna os Reconhecimentos do Curso.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/curso/portal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Reconhecimento> getReconhecimentos() throws DAOException {

		if(!isEmpty(getCurso())){
			return getDAO(ReconhecimentoDao.class).
			findReconhecimentosByCurso(getCurso().getId());
		}	
		return null;
	}
	
	/**
	 * Retorna todos os discentes ativos de um determinado curso.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/curso/alunos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<Discente> getDiscentes() throws ArqException {
		if (!isEmpty(getCurso()) && isEmpty(discentes))
			discentes = getDAO(DiscenteDao.class).
			findByCurso(getCurso().getId(), StatusDiscente.ATIVO,StatusDiscente.FORMANDO,StatusDiscente.GRADUANDO);
		return discentes;
	}
	
	/**
	 * Popula uma coleção de turmas de acordo com ano e período que foram passados. 
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/curso/turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws LimiteResultadosException
	 */
	public String buscarTurmas() throws ArqException {
		
		String msgErro = "";
		setCurso(getGenericDAO().findByPrimaryKey(getCurso().getId(),Curso.class));
		
		//Confere se os campos obrigatórios da busca foram preenchidos
		if(isEmpty(getCurso()))
			msgErro += "Curso : campo obrigatório não informado.";
		if(isEmpty(periodoTurma))
			msgErro += "Período : campo obrigatório não informado.";
		if(isEmpty(anoTurma))
			msgErro += "Ano : campo obrigatório não informado.";
	
		try{
			//Caso não haja erro realiza a consulta
			if(isEmpty(msgErro)){	
				TurmaDao tDao = getDAO(TurmaDao.class);
				
				if( getCurso().isLato() )				
					turmas = tDao.findByCursoLato(getCurso().getId(), 
							getAnoTurma(), getPeriodoTurma());
				else if( getCurso().isTecnico() )
					turmas = tDao.findGeral(getCurso().getNivel(), getCurso().getUnidade(), null, getCodigoTurma(),
							null, null, null, getAnoTurma(), getPeriodoTurma(), null, null, 
							new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL),
							null, null, false, null, null, false, null,null,null,null);
				else			
					turmas = tDao.findTurmasGraduacao(getAnoTurma(), 
							getPeriodoTurma(), getCurso().getId());
				
				//Se não existir turma para os parâmetros passados 
				if(isEmpty(turmas))
					msgErro += "Nenhum resultado encontrado para os critérios selecionados.";
			}
		}catch (LimiteResultadosException e) {
			msgErro += e.getMessage();
		}	
		
		if(!isEmpty(msgErro))
			addMensagemErro(msgErro);
		
		getCurrentRequest().setAttribute("turmasCurso", turmas);
		
		return null;
		
	}

	/**
	 * Retorna o calendário de um curso do Primeiro Período
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/curso/calendario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public CalendarioAcademico getCalendarioPrimeiroPeriodo()
			throws ArqException {
		return getCalendarioCurso(1);
	}

	/**
	 * Retorna o calendário de um curso do Segundo Período
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/curso/calendario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public CalendarioAcademico getCalendarioSegundoPeriodo()
			throws ArqException {
		return getCalendarioCurso(2);
	}
	
	/**
	 * Retorna o calendário de um curso de acordo com parâmetro
	 * @param periodo
	 */
	private CalendarioAcademico getCalendarioCurso(int periodo)
			throws ArqException {
		if(!isEmpty(periodo) && !isEmpty(getCurso())){
			return CalendarioAcademicoHelper.getCalendario(CalendarUtils.getAno(new Date()),
				periodo, getCurso().getUnidade(), getCurso().getNivel(), null, null, getCurso());
		}else return null;
	}

	
	/**
	 * Retorna os processos seletivos abertos de um curso
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/curso/portal.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 * @throws HibernateException 
	 */
	public Collection<ProcessoSeletivo> getProcessosSeletivos() throws ArqException {
		
		if(isEmpty(processosSeletivos) && !isEmpty(getCurso())){	
			
			Collection<ProcessoSeletivo> allProcessosSeletivos = 
				getDAO(ProcessoSeletivoDao.class).findByUnidadeCurso(null, getCurso());
			
			if(allProcessosSeletivos!=null){
				processosSeletivos = new ArrayList<ProcessoSeletivo>();
				for (ProcessoSeletivo p : allProcessosSeletivos) {
					if(p.isPublicado()){
						if(processosSeletivos.size() == ParametroHelper.getInstance().getParametroInt(ParametrosPortalPublico.QTD_MAX_PROCESSOS_SELETIVOS))
							break;
						processosSeletivos.add(p);
					}
				}
			}
			
		}
		
		return processosSeletivos;
	}
	
	/**
	 * Busca as Monografias de acordo com os filtros.
	 * <br/><br/>
	 * Método Chamado pela Seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/public/curso/monografias.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 */
	public String buscarMonografias() throws DAOException{
		if ((!filtroNome && !filtroTitulo && !filtroAno) ||
				ValidatorUtil.isEmpty(nome) && ValidatorUtil.isEmpty(titulo) && ValidatorUtil.isEmpty(ano)){
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}
			
		TrabalhoFimCursoDao dao = getDAO(TrabalhoFimCursoDao.class);
		try {			
			listagemMonografias = dao.findConsultaPublica((filtroNome ? nome : null), 
					(filtroTitulo ? titulo : null), (filtroAno ? ano : null), getCurso().getId());
		} finally {
			if (dao != null)
				dao.close();
		}		
		return "";
	}	

	public Integer getAnoTurma() {
		return anoTurma;
	}

	public void setAnoTurma(Integer anoTurma) {
		this.anoTurma = anoTurma;
	}

	public Integer getPeriodoTurma() {
		return periodoTurma;
	}

	public void setPeriodoTurma(Integer periodoTurma) {
		this.periodoTurma = periodoTurma;
	}

	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public void setComponentesLato(Collection<ComponenteCurricular> componentesLato) {
		this.componentesLato = componentesLato;
	}

	public Collection<ComponenteCurricular> getComponentesLato() {
		return componentesLato;
	}

	public void setCoordenacoesCurso(Collection<CoordenacaoCurso> coordenacoesCurso) {
		this.coordenacoesCurso = coordenacoesCurso;
	}

	public boolean isFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	public boolean isFiltroTitulo() {
		return filtroTitulo;
	}

	public void setFiltroTitulo(boolean filtroTitulo) {
		this.filtroTitulo = filtroTitulo;
	}

	public boolean isFiltroAno() {
		return filtroAno;
	}

	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public List<TrabalhoFimCurso> getListagemMonografias() {
		return listagemMonografias;
	}

	public void setListagemMonografias(List<TrabalhoFimCurso> listagemMonografias) {
		this.listagemMonografias = listagemMonografias;
	}
	
	/**
	 * Retorna a logo da instituição de uso excluvio no portal público do programa. 
	 * @return
	 */
	public String getLogoInstituicaoCurso(){
		return ParametroHelper.getInstance().getParametro( ParametrosPortalPublico.LOGO_INSTITUICAO_PORTAL_PROGRAMA );
	}
	

	/**
	 * Retorna o coordenador do curso.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/curso/curso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public CoordenacaoCurso getCoordenador() throws ArqException{
		
		if( isNotEmpty( getCoordenacoesCurso() ) ){
			for ( CoordenacaoCurso cc: getCoordenacoesCurso() ) 
				if( cc.isCoordenador() )
					return cc;
		}
		
		return null;
		
	}
	
	/**
	 * Retorna os parametros que devem ser passados em todos os links durante a navegação do portal. 
	 * Método chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li>/sigaa.war/public/curso/include/menu.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getParametroURL(){
		
		String parametroURL = "lc=" + getLc();
		if(getCurso() != null)
			parametroURL += "&id=" + getCurso().getId();
		
		return parametroURL;
		
	}
	
	/**
	 * Verifica se a página acessada é a principal.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):<br>
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/include/menu.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPrincipal(){
		return getCurrentURL().contains("portal.jsf");
	}
	
	/**
	 * Retorna calendário vigente de um programa.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/curso/portal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public CalendarioAcademico getCalendarioVigente() {
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
		CalendarioAcademico calendarioVigente = null;
		if( !isEmpty(getCurso()) ){
			try {
				calendarioVigente = CalendarioAcademicoHelper.getCalendario( getCurso() );
			} catch (DAOException e) {
				tratamentoErroPadrao(e);
				return null;
			} finally {
				dao.close();
			}
		}
		return calendarioVigente;
	}

	public String getCodigoTurma() {
		return codigoTurma;
	}

	public void setCodigoTurma(String codigoTurma) {
		this.codigoTurma = codigoTurma;
	}
	
	/**
	 * Retorna o id do arquivo (projeto político-pedagógico), verificando se o
	 * id do arquivo referenciado existe no banco.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/curso/ppp.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Integer getIdArquivo() {
		if (getCurso().getIdArquivo() == null || isEmpty(EnvioArquivoHelper.recuperaNomeArquivo(getCurso().getIdArquivo()))) {
			return new Integer(0);
		}
		return getCurso().getIdArquivo();
	}
	
}
