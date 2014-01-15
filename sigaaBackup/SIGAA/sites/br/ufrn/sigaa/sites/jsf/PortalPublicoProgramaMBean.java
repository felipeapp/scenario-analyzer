/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/11/2008
 *
 */
package br.ufrn.sigaa.sites.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.AreaConcentracaoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.EquipeProgramaDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.ProgramaProjetoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.EventoExtraSistema;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.jsf.EstruturaCurricularMBean;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.dominio.ParametrosProgramaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.ProgramaProjeto;
import br.ufrn.sigaa.ensino.stricto.negocio.ParametrosProgramaPosHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalPublico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca;
import br.ufrn.sigaa.sites.dao.PortalPublicoDao;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;

/**
 * Classe Manage Bean respons�vel por todas as consultas exibidas 
 * no portal p�blico dos programas
 * 
 * @author sist-sigaa-12
 *
 */
@Component("portalPublicoPrograma") @Scope("request")
public class PortalPublicoProgramaMBean extends AbstractControllerPortalPublico{

	
	/** Limita a exibi��o em 45 caracteres no t�tulo de um processo seletivo */
	private final static int  MAX_CARACT_PROCESSOS_SELETIVOS = 45;
	
	/** Atributo exibe todos os discente ativos do programa **/
	private Collection<OrientacaoAcademica> discentes;
	
	/** Atributo exibe os cursos do programa.	 */
	private Collection<Curso> cursos;
	
	/** Atributo os docentes do programa.	 */
	private Collection<EquipePrograma> equipes ;
	
	/** Atributo utilizado na busca dos programas.	 */
	private Collection<Unidade> programas = new ArrayList<Unidade>();
	
	/** Atributo que contem todas as pesquisa do programa.	 */
	private Collection<ProgramaProjeto>  pesquisas;
	
	/** Atributo que contem todas as grades do programa.	 */
	private Collection<Curriculo> grades;
	
	/** Atributo que contem todas as �reas de pesquisa do programa.	 */
	private Collection<AreaConcentracao> areas;
	
	/** Atributo utilizado na busca das turmas de acordo com ano e per�odo setados.	 */
	private Collection<Turma> turmas = new ArrayList<Turma>();
	
	/** Atributo que contem os 5 �ltimos processos seletivos do programa.	 */
	private Collection<ProcessoSeletivo> processosSeletivos;
	
	/** Atributo que cont�m a coordena��o atual do programa */
	private Collection<CoordenacaoCurso> coordenadorPrograma;
	
	/** Atributo que contem o calend�rio acad�mico atual do programa */
	private CalendarioAcademico calendarioVigente;
	
	/** Atributo que contem o calend�rio do pr�ximo per�odo. */
	private CalendarioAcademico calendarioProximoPeriodo;
	
	/** Atributo que cont�m os eventos extras do calend�rio acad�mico do programa */
	private ArrayList<EventoExtraSistema> calendarioOutrosEventos;
	
	/** Atributo que cont�m todas as defesas publicadas do programa.	 */
	private Collection<BancaPos> defesas;
	
	/** Atributo utilizado como filtro na busca das turmas.	 */
	private Integer anoTurma;
	
	/** Atributo utilizado como filtro na busca das turmas.	 */
	private Integer periodoTurma;
	
	/** Atributo utilizado como filtro na busca das turmas.	 */
	private Character nivelTurma;
	
	/** Atributo utilizado como filtro na busca das defesas.	 */
	private Integer anoBanca;
	
	/** Atributo utilizado como filtro na busca das defesas.	 */
	private Integer tipoBanca;
		
	/** Atributo que contemo nome do discente selecionado na banca de defesa do programa.	 */
	private String nomeDiscenteBanca;
	
	/** Atributo que contem os parametros setados pelo coordenador do programa.	 */
	private ParametrosProgramaPos parametrosPrograma;
	
	/** Informa se uma busca foi ou n�o submetida */
	private boolean submetido = false;

	/**
	 * Construtor da classe.
	 * M�todo n�o invocado por JSP�s.
	 * @throws ArqException
	 */
	public PortalPublicoProgramaMBean()  throws ArqException {
		
		setUnidade(new Unidade());
		getUnidade().setGestora(new Unidade());
		if(getParameterInt("centro",0)>0)
			getUnidade().getGestora().setId(getParameterInt("centro",0));
		
	}
	
	/**
	 * Inicializa os atributos utilizados no portal p�blico
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/programa/include/cabecalho.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String getIniciar() throws ArqException{
		
		// Seta o id passado pela URL ou no objeto isEmpty(getParameterInt("id", 0))?getId():
		int id = getParameterInt("id", 0);
		
		if(!isEmpty(id)) {
			// Popula objeto de acordo com o tipo do portal
			setUnidade(getGenericDAO().findByPrimaryKey(id,Unidade.class));
			setTipoPortalPublico(TipoPortalPublico.UNIDADE);
			setPreVisualizar(false);
			getDetalhesSite();
			getRedirecionarSiteExterno();
			
		}
		
		/**  Se a unidade foi populada verifica se o tipo acad�mico � PROGRAMA_POS */ 
		if ( !isEmpty(getUnidade()) ) {
			
			if (validaPortal(getUnidade())) {
				getParametrosPrograma();
				nivelTurma = null;
				periodoTurma= periodoTurma != null?periodoTurma:getPeriodoAtual();
				anoTurma= anoTurma != null?anoTurma:CalendarUtils.getAnoAtual();
				tipoBanca = tipoBanca != null?tipoBanca:TipoBanca.CONCURSO;
			}else{
				addMensagemErro("A unidade n�o corresponde a um programa de p�s-gradua��o.");
				return redirect(getDirBase());
			}
			
		}
		
		coordenadorPrograma = new ArrayList<CoordenacaoCurso>();
		calendarioVigente = new CalendarioAcademico();
		calendarioOutrosEventos = new ArrayList<EventoExtraSistema>();
		calendarioProximoPeriodo =  new CalendarioAcademico();
		
		return "";
	
	}
	
	@Override
	public String getDirBase() {
		// TODO Auto-generated method stub
		return "/sigaa/public/";
	}
	
	/**
	 * Verifica se o portal pertence ao tipo acad�mico correto.<br/> 
	 * M�todo n�o invocado por JSP�s.
	 * @param unidade
	 * @return
	 */
	private boolean validaPortal(Unidade unidade) {
		// Verifica se o portal que est� acessando � referente ao tipoUnidadeAcademico 
		if( !isEmpty(unidade.getTipoAcademica()) && unidade.getTipoAcademica().equals(TipoUnidadeAcademica.PROGRAMA_POS) ) 
			return true;
		return false;
		
	}
	
	/**
	 * Retorna para o portal p�blico do SIGAA
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/include/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	@Override
	public String cancelar() {		
		return redirect("/sigaa/public/home.jsf");
	}
	
	/**
	 * Retorna todos programas de acordo com os filtros setados
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/busca_programa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String buscarProgramas() throws ArqException {

		programas = getDAO(PortalPublicoDao.class).findUnidadeByNomeCentro(getUnidade().getNome(), getUnidade().getGestora().getId(), TipoUnidadeAcademica.PROGRAMA_POS);
		
		if(!isEmpty(programas)){
			Collections.sort((List<Unidade>) programas, new Comparator<Unidade>(){
				public int compare(Unidade u1, Unidade u2) {
					return u1.getGestora().getNome().compareTo( u2.getGestora().getNome());
				}
			});
			getCurrentRequest().setAttribute("programas", programas);
		}else{
			addMensagemErro("Nenhum programa foi encontrado de acordo com os crit�rios de busca informados");
		}
		
		return "";
		
	}
	
	/**
	 * Retorna todas as �reas de concentra��o do programa e n�vel de ensino mestrado
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/cursos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<Curso> getCursosPrograma() throws ArqException {
		if (isEmpty(cursos) && !isEmpty(getUnidade()))
			cursos = getDAO(CursoDao.class).findByPrograma(getUnidade().getId());
		return cursos;
	}
	
	/**
	 * Retorna todos os docentes do programa ordenados por �rea, n�vel e programa.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/equipe.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<EquipePrograma> getDocentesPrograma() throws ArqException {
		if (isEmpty(equipes) && !isEmpty(getUnidade())){
			equipes = getDAO(EquipeProgramaDao.class).findByPrograma(getUnidade().getId(), null, null);
			Collections.sort((List<EquipePrograma>) equipes, new Comparator<EquipePrograma>() {
				public int compare(EquipePrograma e1,	EquipePrograma e2) {
					return new CompareToBuilder()
					.append(e1.getAreaConcentracaoPrincipal().getDenominacao(), e2.getAreaConcentracaoPrincipal().getDenominacao())	
					.append(e1.getNome(), e2.getNome())
					.toComparison();
				}
			});
			
			Integer idPerfil = 0;
			PerfilPessoa perfil;
			for (EquipePrograma e : equipes) {
				if (e.getServidor() != null && e.getServidor().getIdPerfil() != null){
					idPerfil = e.getServidor().getIdPerfil();
					perfil =  PerfilPessoaDAO.getDao().get(idPerfil);
					e.getServidor().setPerfil(perfil);
				}else if (e.getDocenteExterno() != null && e.getDocenteExterno().getIdPerfil() != null){ 
					idPerfil = e.getDocenteExterno().getIdPerfil();
					perfil =  PerfilPessoaDAO.getDao().get(idPerfil);
					e.getDocenteExterno().setPerfil(perfil);
				}	
			}

		}
		return equipes;
	}
	
	/**
	 * Retorna todos os componentes curriculares do programa
	 * ordenados por �rea de concentra��o e componente.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/grade.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<Curriculo> getGradeCurricular()
		throws ArqException {
		if (isEmpty(grades) && !isEmpty(getUnidade()))
			grades = getDAO( EstruturaCurricularDao.class ).findCompletoAtivo( null, null, getUnidade().getId(), null, true );
		return grades;
	}
	
	/**
	 * Retorna os detalhes do curr�culo selecionado.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.ear/sigaa.war/public/programa/curriculo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String detalharCurriculo() throws DAOException{
		
		EstruturaCurricularMBean curriculo = getMBean("curriculo");
		curriculo.setId();
		curriculo.popularCurriculo();
		
		return redirectJSF("/sigaa/public/programa/curriculo_resumo.jsf?id=" + getUnidade().getId() + "&lc=" + getLc());
		
	}
	
	/**
	 * Retorna todas as �reas de concentra��o do programa
	 * ordenados por �rea.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/areas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<AreaConcentracao> getAreaConcentracao()
		throws ArqException {
		if (isEmpty(areas) && !isEmpty(getUnidade()))
			areas = getDAO(AreaConcentracaoDao.class).findByPrograma(getUnidade().getId());
		return areas;
	}

	/**
	 * Retorna todos os discentes ativos de um determinado programa.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/alunos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<OrientacaoAcademica> getAlunosAtivos() throws ArqException {
		if (!isEmpty(getUnidade()) && isEmpty(discentes))
			discentes = getDAO(OrientacaoAcademicaDao.class).findByProgramas(getUnidade().getId(), null);
		return discentes;
	}
	
	/**
	 * Efetua a busca das turmas do curso por ano e per�odo.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws LimiteResultadosException
	 */
	public String buscarTurmas() throws ArqException, LimiteResultadosException {
		
		turmas = null; 
		periodoTurma= periodoTurma != null?periodoTurma:getPeriodoAtual();
		anoTurma = anoTurma != null?anoTurma:CalendarUtils.getAnoAtual();
		
		String msgErro = "";
		
		
		if(isEmpty(getUnidade()))
			msgErro += "Unidade : o campo obrigat�rio n�o informado.";
		if(isEmpty(periodoTurma))
			msgErro += "Per�odo : o campo obrigat�rio n�o informado.";
		if(isEmpty(anoTurma))
			msgErro += "Ano : o campo obrigat�rio n�o informado.";

		if(!isEmpty(msgErro))
			return null;
		
		setUnidade(getGenericDAO().findByPrimaryKey(getUnidade().getId(),Unidade.class));
		
		if(isEmpty(msgErro) && (turmas == null || turmas.size()==0)){
			turmas = getDAO(TurmaDao.class).findGeral(NivelEnsino.STRICTO, getUnidade(), null, null, null, null,
			null, getAnoTurma(), getPeriodoTurma(), null, null, 
			 new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL), null, null, null, null, null,null,null);
			if(isEmpty(turmas))
				msgErro += "Nenhum resultado encontrado para os crit�rios selecionados.";
			setSubmetido(true);
		}
		
		if(!isEmpty(msgErro))
			addMensagemErro(msgErro);
		
		return "";
		
	}
	
	/**
	 * Retorna os processos seletivos abertos de um programa.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/portal.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 * @throws HibernateException 
	 */
	public Collection<ProcessoSeletivo> getProcessosSeletivos() throws ArqException {
		
		if( isEmpty(processosSeletivos) && !isEmpty(getUnidade()) ){	
			
			Collection<ProcessoSeletivo> allProcessosSeletivos = getDAO(ProcessoSeletivoDao.class).findByUnidadeCurso(getUnidade(), null);
		
			
			if( !isEmpty( allProcessosSeletivos ) ){
			
				processosSeletivos = new ArrayList<ProcessoSeletivo>();
				
				for (ProcessoSeletivo p : allProcessosSeletivos) {
					if( p.isPublicado() && validaDataFimProcesso( p.getEditalProcessoSeletivo().getFimInscricoes() ) ){
						
						p.getCurso().setNome( StringUtils.limitTxt( p.getCurso().getNomeCursoStricto(), MAX_CARACT_PROCESSOS_SELETIVOS ) );
						if( processosSeletivos.size() == ParametroHelper.getInstance().getParametroInt(ParametrosPortalPublico.QTD_MAX_PROCESSOS_SELETIVOS) )
							break;
						processosSeletivos.add(p);
						
					}
					
				}
				
			}
			
			if( !isEmpty(processosSeletivos) ){
				Collections.sort( (List<ProcessoSeletivo>) processosSeletivos, new Comparator<ProcessoSeletivo>(){
					public int compare(ProcessoSeletivo p1,	ProcessoSeletivo p2) {						
						return p2.getEditalProcessoSeletivo().getInicioInscricoes().compareTo(p1.getEditalProcessoSeletivo().getInicioInscricoes()); 					
					}
				});	
			}
			
		}
		
		return processosSeletivos;
		
	}
	
	/**
	 * Verifica se � permitido visualizar o processo seletivo  com a data final de inscri��o maior que 
	 * a quantidade de dias especificadas no par�metro.  
	 * @param data
	 * @return
	 * @throws DAOException 
	 */
	private boolean validaDataFimProcesso(Date data) throws DAOException{
		
		Date dataAtual = new Date();
		dataAtual = CalendarUtils.descartarHoras(dataAtual);
		data = CalendarUtils.descartarHoras(data);
		ParametrosProgramaPos parametrosPrograma = ParametrosProgramaPosHelper.getParametros(getUnidade());
		
		if( parametrosPrograma.getMaxDiasPassadosProcessoSeletivo() != null){
			data = CalendarUtils.adicionaDias(data, parametrosPrograma.getMaxDiasPassadosProcessoSeletivo());
			if ( parametrosPrograma.getMaxDiasPassadosProcessoSeletivo() == 0 || dataAtual.getTime() < data.getTime() )
				return true;
		}
		return false;
		
	}
	
	/**
	 * Retorna somente os processos de stricto-senso de mestrado.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<ProcessoSeletivo> getProcessosSeletivosMestrado() throws ArqException {
		
		Collection<ProcessoSeletivo> processoMestrado = new ArrayList<ProcessoSeletivo>();
		
		for (ProcessoSeletivo p : getProcessosSeletivos()) 
			if( p.getCurso().isMestrado() )
				processoMestrado.add(p);
		
		return processoMestrado;
		
	}
	
	/**
	 * Retorna somente os processos de stricto-senso de doutorado.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<ProcessoSeletivo> getProcessosSeletivosDoutorado() throws ArqException {
		
		Collection<ProcessoSeletivo> processoDoutorado = new ArrayList<ProcessoSeletivo>();
		
		for (ProcessoSeletivo p : getProcessosSeletivos()) 
			if( p.getCurso().isDoutorado() )
				processoDoutorado.add(p);
		
		return processoDoutorado;
		
	}
	
	/**
	 * Retorna calend�rio vigente de um programa.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/calendario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public CalendarioAcademico getCalendarioVigente() {
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
		
		if( !isEmpty(getUnidade()) && isEmpty(calendarioVigente) ){
			try {
				calendarioVigente = dao.findByUnidadeNivel(getUnidade().getId(), NivelEnsino.STRICTO);
			} catch (DAOException e) {
				tratamentoErroPadrao(e);
				return null;
			} finally {
				dao.close();
			}
		}
		return calendarioVigente;
	}
	
	/**
	 * Retorna outros eventos do calend�rio de um programa.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/calendario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public ArrayList<EventoExtraSistema> getCalendarioOutrosEventos() throws ArqException {
		
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);

		Collection<CalendarioAcademico> calendarios = new ArrayList<CalendarioAcademico>();
		calendarios.add(getCalendarioVigente());
		calendarios.add(getCalendarioProximoPeriodo());
			
		
		if(isEmpty(calendarioOutrosEventos)){
			for (CalendarioAcademico ca : calendarios)
				if(ca != null && ca.isAtivo())
					calendarioOutrosEventos.addAll(dao.findByExactField(EventoExtraSistema.class, "calendario.id", ca.getId()));
		}
		
		if ( calendarioOutrosEventos != null ) {
			Collections.sort(calendarioOutrosEventos, new Comparator<EventoExtraSistema>(){
				public int compare(EventoExtraSistema e1, EventoExtraSistema e2) {
					if (e1.getInicio().before(e2.getInicio()))
						return -1;
					else if (e1.getInicio().after(e2.getInicio()))
						return 1;
					else return e1.getFim().compareTo(e2.getFim());
				}
			});
		}
		
		return calendarioOutrosEventos;

	}

	/**
	 * Retorna calend�rio de um programa do Segundo Per�odo
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/calendario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public CalendarioAcademico getCalendarioProximoPeriodo()
			throws ArqException {

		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
		
		if(!isEmpty(getUnidade()) && isEmpty(calendarioProximoPeriodo)){
		
			try {
				CalendarioAcademico vigente = dao.findByUnidadeNivel(getUnidade().getId(), NivelEnsino.STRICTO);
				if (vigente != null) 
					calendarioProximoPeriodo = dao.findProximo(vigente.getAno(),vigente.getPeriodo(), getUnidade().getId(), NivelEnsino.STRICTO, vigente.getConvenio());
			} catch (DAOException e) {
				tratamentoErroPadrao(e);
				return null;
			} finally {
				dao.close();
			}
			
		}
		
		return calendarioProximoPeriodo;
	}
	
	/**
	 * Retorna todos as teses e defesas de um determinado programa.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/defesas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<BancaPos> getDefesas() throws ArqException{
		if (isEmpty(defesas) && !isEmpty(getUnidade()))
			defesas = getDAO(PortalPublicoDao.class).findByUnidade(getUnidade().getId(), null,null, null);
		return defesas;
	}
	
	/**
	 * Retorna o endere�o oficial do portal p�blico do programa
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/include/programa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String getUrlOficial() throws ArqException{
		if(!isEmpty(getDetalhesSite()) && !isEmpty(getDetalhesSite().getUrl()))
			return ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.URL_PROGRAMA) + "/" + getDetalhesSite().getUrl();
		else
			return "";
	}
	
	/**
	 * M�todo retorna todos os projetos de pesquisa ativos de um programa,e que estejam na situa��o
	 *  em andamento, renovado ou finalizado
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/public/programa/pesquisa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<ProgramaProjeto> getProjetosPesquisa() throws ArqException {
		if(!isEmpty(getUnidade()) && isEmpty(pesquisas))
			pesquisas = getDAO(ProgramaProjetoDao.class).findByProgramaStatus(getUnidade().getId(),true);
			return pesquisas;
	}
	
	/**
	 * Retorna uma cole��o contendo coordenador e vice
	 * atuais do programa
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/portal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<CoordenacaoCurso> getCoordenadorPrograma() throws ArqException {
		if(!isEmpty(getUnidade()) && isEmpty(coordenadorPrograma)){
			CoordenacaoCursoDao coordenacaoCursoDao = getDAO(CoordenacaoCursoDao.class);
			coordenadorPrograma =  coordenacaoCursoDao.findByPrograma(getUnidade().getId(), TipoUnidadeAcademica.PROGRAMA_POS, true, null);
		}
		return coordenadorPrograma;
	}
	
	/**
	 * Retorna o coordenador do programa.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/programa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public CoordenacaoCurso getCoordenador() throws ArqException{
		
		for ( CoordenacaoCurso cc: getCoordenadorPrograma() ) 
			if( cc.isCoordenador() )
				return cc;
			
		return null;
		
	}
	
	/**
	 * Retorna os par�metros relacionados ao programa do portal portal p�blico. 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/defesas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public ParametrosProgramaPos getParametrosPrograma() throws ArqException{
		if(parametrosPrograma == null)
			parametrosPrograma = ParametrosProgramaPosHelper.getParametros(getUnidade());
		return parametrosPrograma;
	}
	
	/**
	 * M�todo que armazena o ano da turma setado na busca.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  
	 * <br/>
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/turma.jsp</li>
	 * </ul>
	 * @return
	 */
	public Integer getAnoTurma() {
		return anoTurma;
	}

	/**
	 * M�todo que seta o ano da turma na busca.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  
	 * <br/>
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/turma.jsp</li>
	 * </ul>
	 * @return
	 */
	public void setAnoTurma(Integer anoTurma) {
		this.anoTurma = anoTurma;
	}

	/**
	 * Retorna o periodo da turma que � utilizado na listagem.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  
	 * <br/>
	 * <ul> 
	 * 	<li>/sigaa.war/public/programa/turma.jsp</li>
	 * </ul>
	 * @return
	 */
	public Integer getPeriodoTurma() {
		return periodoTurma;
	}

	/**
	 * Seleciona o periodo da turma que � utilizado na listagem.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li>/sigaa.war/public/programa/turma.jsp</li>
	 * </ul>
	 * @return
	 */
	public void setPeriodoTurma(Integer periodoTurma) {
		this.periodoTurma = periodoTurma;
	}
	
	/**
	 * Retorna o idioma do portal.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li>/sigaa.war/public/programa/portal.jsp</li>
	 * </ul>
	 * @return
	 */
	@Override
	public String getLc() {
		return super.getLc();
	}
	
	/**
	 * Retorna o nome do discente que � utilizado na listagem.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li> /sigaa.war/public/programa/defesa.jsp<li>
	 * </ul>
	 * @return
	 */
	public String getNomeDiscenteBanca() {
		return nomeDiscenteBanca;
	}

	/**
	 * Seleciona o nome do discente que � utilizado na listagem.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li> /sigaa.war/public/programa/defesa.jsp</li>
	 * </ul>
	 * @return
	 */
	public void setNomeDiscenteBanca(String nomeDiscenteBanca) {
		this.nomeDiscenteBanca = nomeDiscenteBanca;
	}
	
	/**
	 * Retorna o ano da turma que � utilizado na listagem.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/public/programa/defesa.jsp</li>
	 * </ul>
	 * @return
	 */
	public Integer getAnoBanca() {
		return anoBanca;
	}

	/**
	 * Seleciona o ano da turma que � utilizado na listagem.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li>/sigaa.war/public/programa/defesa.jsp</li>
	 * </ul>
	 * @return
	 */
	public void setAnoBanca(Integer anoBanca) {
		this.anoBanca = anoBanca;
	}

	/**
	 * Retorna o tipoBanca que � utilizado na listagem.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li> /sigaa.war/public/programa/defesa.jsp</li>
	 * </ul>
	 * @return
	 */
	public Integer getTipoBanca() {
		return tipoBanca;
	}

	/**
	 * Seleciona o tipo  da banca na listagem das defesas do programa.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li> /sigaa.war/public/programa/defesa.jsp</li>
	 * </ul>
	 * @return
	 */
	public void setTipoBanca(Integer tipoBanca) {
		this.tipoBanca = tipoBanca;
	}

	/**
	 * Retorna true se a busca foi realizada.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li> /sigaa.war/public/programa/turma.jsp</li>
	 * </ul>
	 * @return
	 */
	public Boolean getSubmetido() {
		return this.submetido;
	}

	/**
	 * M�todo n�o invocado por JSP.
	 * @param submetido
	 */
	public void setSubmetido(Boolean submetido) {
		this.submetido = submetido;
	}
	
	/**
	 * Retorna uma cole��o de turmas do programa.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li>/sigaa.war/public/programa/turma.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<Turma> getTurmas() {
		return turmas;
	}

	/**
	 * Popula uma cole��o de turmas do programa.
	 * M�todo n�o invocado por JSP.
	 * @param nivelTurma
	 */
	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	/**
	 * Retorna o n�vel selecionado na listagem das turmas.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li>/sigaa.war/public/programa/turma.jsp</li>
	 * </ul>
	 * @return
	 */
	public Character getNivelTurma() {
		return nivelTurma;
	}

	/**
	 * Seleciona o n�vel na listagem das turmas.
	 * M�todo n�o invocado por JSP.
	 * @param nivelTurma
	 */
	public void setNivelTurma(Character nivelTurma) {
		this.nivelTurma = nivelTurma;
	}
	
	/**
	 * Retorna os parametros que devem ser passados em todos os links durante a navega��o do portal. 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):  	
	 * <br/>
	 * <ul> 
	 * 	<li>/sigaa.war/public/programa/include/menu.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getParametroURL(){
		
		String parametroURL = "lc=" + getLc();
		if(getUnidade() != null)
			parametroURL += "&id=" + getUnidade().getId();
		
		return parametroURL;
		
	}
	
	/**
	 * Redireciona para a Base de Dados de Testes e Disserta��es da Institui��o de Ensino
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/programa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String redirectBDTD(){
		
		return redirect( "http://bdtd.bczm.ufrn.br/tedesimplificado/tde_busca/index.php" );
		
	}
	
	/**
	 * Verifica se a p�gina acessada � a principal.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):<br>
	 * <ul>
	 * 	<li>/sigaa.war/public/programa/include/menu.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPrincipal(){
		return getCurrentURL().contains("portal.jsf");
	}
	
	/**
	 * Retorna a logo da institui��o de uso excluvio no portal p�blico do programa. 
	 * @return
	 */
	public String getLogoInstituicaoPrograma(){
		return ParametroHelper.getInstance().getParametro( ParametrosPortalPublico.LOGO_INSTITUICAO_PORTAL_PROGRAMA );
	}

	
}
