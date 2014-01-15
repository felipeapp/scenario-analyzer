/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/01/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.Collection;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Classe respons�vel por realizar o controle do relat�rio de Componentes Curriculares por departamento.
 * 
 * @author Ricardo Wendell
 *
 */
public class RelatorioPorDepartamentoMBean extends SigaaAbstractController<Object> {

	// Campos do Relat�rio
	private Unidade unidade;
	private Integer ano;
	private Integer periodo;
	
	// Controles dos relat�rios
	private int tipoRelatorio;
	private String titulo;
	
	// Tipos de relat�rio
	private static final int COMPONENTES_COM_PROGRAMA = 1;
	private static final int COMPONENTES_COM_PROBLEMA_CADASTRO_PROGRAMA = 2;
	
	public RelatorioPorDepartamentoMBean() {
		unidade = new Unidade();
	}
	
	/**
	 * Inicia relat�rio de componentes com programa cadastrado
	 * para um determinado ano-per�odo.
	 * <br><br>
	 * M�todo chamado pela(s) JSP(s):
	 * <ul>
	 *  <li> /sigaa.war/portais/docente/menu_docente.jsp
	 *  <li> /sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * </ul> 
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String iniciarRelatorioComponentesComPrograma() throws SegurancaException, DAOException {
		titulo = "Relat�rio de Componentes Curriculares com programas cadastrados";
		tipoRelatorio = COMPONENTES_COM_PROGRAMA;
		return iniciar();
	}
	
	/**
	 * Inicia relat�rio de componentes com programa cadastrado e dados incompletos no cadastro do programa.
	 * 
	 * <br><br>
	 * M�todo chamado pela(s) JSP(s):
	 * <ul>
	 *  <li> /sigaa.war/portais/docente/menu_docente.jsp
	 *  <li> /sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * </ul> 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciarRelatorioComponentesComProblemasCadastroEmPrograma() throws SegurancaException, DAOException {
		titulo = "Relat�rio de Componentes Curriculares com Dados Incompletos no Cadastro do Programa";
		tipoRelatorio = COMPONENTES_COM_PROBLEMA_CADASTRO_PROGRAMA;
		return iniciar();
	}

	/**
	 * M�todo utilizado para redirecionar o relat�rio espec�fico.
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	private String iniciar() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.COORDENADOR_CURSO);
		
		// Popular dados iniciais
		if ( isUnidadeSelecionavel() ) {
			unidade = new Unidade();
		} else {
			unidade = getUnidadeResponsabilidade();
			unidade = getGenericDAO().findByPrimaryKey(getUnidadeResponsabilidade().getId(), Unidade.class);
		}
		
		if (ano == null || ano == 0) {
			ano = getCalendarioVigente().getAno();
		}
		if (periodo == null || periodo == 0) {
			periodo = getCalendarioVigente().getPeriodo();
		}
		if(isRelatorioComponenteComPrograma())
			return forward("/graduacao/relatorios/form_departamento.jsp");
		else{
			gerarRelatorio();
			return null;
		}
		
	}
	/**
	 * M�todo respons�vel por receber os par�metros do relat�rio e redireciona-lo para o m�todo correspondente.
	 * <br><br>
	 * M�todo chamado pela(s) JSP(s):
	 * <ul>
	 *  <li> /sigaa.war/graduacao/relatorios/componentes/componentes_programa.jsp
	 *  <li> /sigaa.war/graduacao/relatorios/componentes/componentes_problemas_cadastro_programa.jsp
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws DAOException {
		
		// Validar dados
		if (unidade.getId() == 0 && isRelatorioComponenteComPrograma()) {
			addMensagemErro("� necess�rio selecionar uma unidade detentora de componentes curriculares");
			return null;
		}
		
		unidade = getGenericDAO().refresh(unidade);
		
		// Redirecionar para o relat�rio espec�fico
		switch (tipoRelatorio) {
			case COMPONENTES_COM_PROGRAMA: return gerarRelatorioComponentesComPrograma();
			case COMPONENTES_COM_PROBLEMA_CADASTRO_PROGRAMA: return gerarRelatorioComponentesComProblemaCadastroPrograma();
			default: 
				addMensagemErro("� neces�rio selecionar o tipo de relat�rio desejado.");
				redirect(getSubSistema().getLink());
				return null;
		}
		
	}

	/**
	 * Gera relat�rio de componentes com programa cadastrado
	 * para um determinado ano-per�odo.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private String gerarRelatorioComponentesComPrograma() throws DAOException {
		ComponenteCurricularDao componenteDao = getDAO(ComponenteCurricularDao.class);
		
		// Buscar totais
		Collection<ComponenteCurricular> componentes = componenteDao.findResumoComponentesPrograma(unidade, ano, periodo);
		
		if (componentes.isEmpty()) {
			addMensagemWarning("N�o foram encontrados componentes curriculares de acordo com os crit�rios informados");
			return null;
		}
		
		getCurrentRequest().setAttribute("componentes", componentes);
		return forward("/graduacao/relatorios/componentes/componentes_programa.jsp");
	}
	
	/**
	 * Gera relat�rio de componentes com dados incompletos no cadastro do programa.
	 * 
	 * @return
	 * @throws DAOException
	 */
	private String gerarRelatorioComponentesComProblemaCadastroPrograma() throws DAOException {
		ComponenteCurricularDao componenteDao = getDAO(ComponenteCurricularDao.class);
		
		// Buscar totais
		Collection<ComponenteCurricular> componentes = componenteDao.findProgramasIncompletosByNivel(unidade, NivelEnsino.GRADUACAO, ano, periodo);
		
		if (componentes.isEmpty()) {
			addMensagemWarning("N�o foram encontrados componentes curriculares de acordo com os crit�rios informados");
			return null;
		}
		
		getCurrentRequest().setAttribute("componentes", componentes);
		return forward("/graduacao/relatorios/componentes/componentes_problemas_cadastro_programa.jsp");
	}

	/**
	 * Utilizado para verificar se o usu�rio est� utilizando o relat�rio de componentes curriculares com programas cadastrados.
	 * @return
	 */
	public boolean isUnidadeSelecionavel() {
		return getUsuarioLogado().isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE);
	}
	
	public boolean isRelatorioComponenteComPrograma(){
		return tipoRelatorio == COMPONENTES_COM_PROGRAMA;
	}
	
	public Unidade getUnidade() {
		return this.unidade;
	}

	public void setUnidade(Unidade departamento) {
		this.unidade = departamento;
	}

	public Integer getAno() {
		return this.ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return this.periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public int getTipoRelatorio() {
		return this.tipoRelatorio;
	}

	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
}
