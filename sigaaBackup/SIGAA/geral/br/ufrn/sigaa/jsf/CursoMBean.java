/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '17/01/2008'
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.LinhaListaCursos;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.latosensu.jsf.PortalCoordenadorLatoMBean;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaAcompanhamentoCursoLato;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.portal.jsf.PortalCoordenadorGraduacaoMBean;

/**
 * MBean responsável por gerenciar Curso
 * 
 * @author Leonardo
 */
@Component("curso") @Scope("session")
public class CursoMBean extends SigaaAbstractController<Curso> {
	/**  Lista de unidades */
	private Map<Unidade, LinhaListaCursos> lista;
	/** Processo seletivo selecionado */
	private ProcessoSeletivo processoSeletivo;
	/** Nível */
	private Character nivel;
	/** Tipo da busca */
	private String tipoBusca;

	/**
	 * construtor
	 */
	public CursoMBean(){
		obj = new Curso();
	}

	/**
	 * Popula os dados do bean para a busca geral
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *   <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 */
	public String popularBuscaGeral(){
		obj = new Curso();
		tipoBusca = "todos";
		nivel = getUsuarioLogado().getNivelEnsino();
		if ( nivel == null || nivel == ' ' )
			nivel = NivelEnsino.GRADUACAO;

		setResultadosBusca(new ArrayList<Curso>(0));
		return forward("/geral/curso/busca_geral.jsf");
	}

	/**
	 * Efetua buscas de acordo com os parâmetros selecionados pelo usuário.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/coordenacao_curso/lista.jsp</li>
	 *   <li>/sigaa.war/geral/curso/busca_geral.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws DAOException {
		String param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca");
			return null;
		}

		if(nivel == null){
			addMensagemErro("Selecione um nível de ensino");
			return null;
		}

		tipoBusca = param;

		CursoDao dao = getDAO(CursoDao.class);
		if ("nome".equalsIgnoreCase(param)){
			// validação
			if(obj.getNome() == null || "".equals(obj.getNome()) ){
				addMensagemErro("Informe o nome do Curso para a busca");
				return null;
			}else if(obj.getNome().trim().length() < 3){
				addMensagemErro("O nome deve possuir no mínimo 3 caracteres");
				return null;
			}
			setResultadosBusca(dao.findAllOtimizado(nivel, obj.getNome()));
		}
		else if ("todos".equalsIgnoreCase(param)){
			setResultadosBusca(dao.findAllOtimizado(nivel, null));
			obj = new Curso();
		}
		if( getResultadosBusca().isEmpty() )
			addMessage("Nenhum curso encontrado de acordo com os critérios de busca informados.", TipoMensagemUFRN.WARNING);

		return null;
	}

	/**
	 * Exibe os dados do curso
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/geral/curso/busca_geral.jsp</li>
	 * </ul>
	 */
	public String detalhar() throws DAOException {
		GenericDAO dao = getGenericDAO();
		String id = getParameter("id");
		obj.setId( Integer.parseInt(id) );


		obj = dao.findByPrimaryKey(obj.getId(), Curso.class);


		return forward("/geral/curso/resumo.jsp");
	}

	/**
	 * Cancela a operação e retorna para a view apropriada
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/geral/curso/busca_geral.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		super.cancelar();
		if (obj.getId() == 0)
			return "menuCDP";
		else
			return forward("/geral/consultaCurso.jsp");
	}

	/**
	 * Método que possibilitar a criação de autocompletes de Curso.
	 * <br/><br/>
	 * Método não chamado por JSPs
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> autocompleteNomeCurso(Object event) throws DAOException {
		CursoDao dao = getDAO(CursoDao.class);
		String nome = event.toString();
		Collection<Curso> servidoresBuscados = dao.findByNome(nome, 0, Curso.class, getNivelEnsino(), Boolean.TRUE, getPaginacao());
		return servidoresBuscados;  
	}
	
	/**
	 * Método que possibilitar a criação de autocompletes de todos os Curso.
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/biblioteca/associacao_curso_biblioteca/editarAssociacoes.jsp</li>
	 *   <li>/sigaa.war/estagio/oferta_estagio/form.jsp</li>
	 * </ul>
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> autocompleteNomeGeralCursos(Object event) throws DAOException {
		CursoDao dao = getDAO(CursoDao.class);
		try {
			String nome = event.toString();
			Collection<Curso> cursos = dao.findByNome(nome, 0, Curso.class, ' ', Boolean.TRUE, getPaginacao());
			return cursos;  			
		} finally {
			dao.close();
		}
	}	
	
	/**
	 * Retorna todos os cursos de graduação
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/biblioteca/controle_estatistico/formGeral.jsp</li>
	 *   <li>/sigaa.ear/sigaa.war/diplomas/livro_registro_diploma/form.jsp</li>
	 *   <li>/sigaa.war/diplomas/livro_registro_diploma/lista.jsp</li>
	 *   <li>/sigaa.war/diplomas/registro_diplomas/busca_curso.jsp</li>
	 *   <li>/sigaa.war/diplomas/registro_diplomas/impressao_coletivo.jsp</li>
	 *   <li>/sigaa.war/ensino/discente/busca.jsp</li>
	 *   <li>/sigaa.war/ensino/etiquetas/form.jsp</li>
	 *   <li>/sigaa.war/ensino/secretaria_unidade/form.jsp</li>
	 *   <li>/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 *   <li>/sigaa.war/estagio/estagio/include/_busca.jsp</li>
	 *   <li>/sigaa.war/estagio/oferta_estagio/include/_busca.jsp</li>
	 *   <li>/sigaa.war/extensao/DiscenteExtensao/busca_discente.jsp</li>
	 *   <li>/sigaa.war/extensao/Relatorios/alunos_extensao_monitoria_pesquisa.jsp</li>
	 *   <li>/sigaa.war/extensao/Relatorios/dados_bancarios_discentes_form.jsp</li>
	 * </ul>
	 * @return 
	 */
	public Collection<Curso> getAllCursoGraduacao(){
		CursoDao dao = getDAO(CursoDao.class);
		try {
			return dao.findByNivel(NivelEnsino.GRADUACAO);
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<Curso>();
		}
	}

	/**
	 * Retorna todos os cursos de graduação em formato para combo
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul> 
	 *  <li>/sigaa.war/ensino/discente/busca.jsp</li>
	 *  <li>/sigaa.war/ensino/etiquetas/form.jsp</li>
	 *  <li>/sigaa.war/ensino/secretaria_unidade/form.jsp</li>
	 *  <li>/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 *  <li>/sigaa.war/extensao/DiscenteExtensao/busca_discente.jsp</li>
	 *  <li>/sigaa.war/extensao/Relatorios/alunos_extensao_monitoria_pesquisa.jsp</li>
	 *  <li>/sigaa.war/extensao/Relatorios/dados_bancarios_discentes_form.jsp</li>
	 *  <li>/sigaa.war/graduacao/colecao_coletiva/busca_curso.jsp</li>
	 *  <li>/sigaa.war/graduacao/habilitacao/form.jsp</li>
	 *  <li>/sigaa.war/graduacao/relatorios/turma/seleciona_turma.jsp</li>
	 *  <li>/sigaa.war/graduacao/relatorios/turmas_oferecidas.jsp</li>
	 *  <li>/sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li>
	 *  <li>/sigaa.war/monitoria/ConsultarMonitor/lista.jsp</li>
	 *  <li>/sigaa.war/monitoria/Relatorios/projetos_monitores_form.jsp</li>
	 *  <li>/sigaa.war/monitoria/Relatorios/quantitativo_monitores_form.jsp</li>
	 *  <li>/sigaa.war/prodocente/atividades/Pet/form.jsp</li>
	 *  <li>/sigaa.war/public/pesquisa/consulta_bolsistas.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getAllCursoGraduacaoCombo(){
		return toSelectItems(getAllCursoGraduacao(), "id", "descricao");
	}
	
	/**
	 * Retorna todos os curso com o nível informado.
	 * Método não chamado por JSP.
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getCursoNivelCombo(char nivel) throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		return toSelectItems(dao.findByNivel(nivel,true),"id","descricao");
	}

	/**
	 * Retorna todos os cursos de STRICTO em formato para combo
	 * @return 
	 * JSP: Não tem.
	 */
	public Collection<SelectItem> getAllCursoStrictoCombo() throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		return toSelectItems(dao.findByNivel(NivelEnsino.STRICTO, true), "id", "descricao");
	}

	/**
	 * Retorna todos os cursos de ESPECIALIZAÇÃO (LATO) em formato para combo
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 *	</ul>
	 * @return 
	 * 
	 */
	public Collection<SelectItem> getAllCursoEspecializacaoCombo() throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		return toSelectItems(dao.findByNivel(NivelEnsino.LATO, true, false), "id", "descricao");
	}
	
	/**
	 * Retorna todos os cursos de ESPECIALIZAÇÃO (LATO) onde a situação da proposta está aceita em formato para combo
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/secretaria_unidade/form.jsp</li>
	 *		<li>sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCursoEspecializacaoAceitoCombo() throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		return toSelectItems(dao.findByNivelAceito(NivelEnsino.LATO, true, false), "id", "descricao");
	}

	/**
	 * Retorna todos os cursos que o usuário logado é coordenador em formato combo
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino/secretaria_unidade/form.jsp</li>
	 *   <li>/sigaa.war/ensino/secretaria_unidade/substituicao.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getAllCoordenadorCombo(){
		return toSelectItems(getAllCursosCoordenacaoNivel(), "id", "descricao");
	}

	/**
	 * Retorna todos os cursos de MESTRADO em formato para combo
	 * JSP: Não tem.
	 * @return 
	 */
	public Collection<SelectItem> getAllCursoMestradoCombo() throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		return toSelectItems(dao.findByNivel(NivelEnsino.MESTRADO, true), "id", "descricao");
	}

	/**
	 * Retorna todos os cursos de DOUTORADO em formato para combo
	 * JSP: Não tem.
	 * @return 
	 */
	public Collection<SelectItem> getAllCursoDoutoradoCombo() throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		return toSelectItems(dao.findByNivel(NivelEnsino.DOUTORADO, true), "id", "descricao");
	}

	/**
	 * Retorna todos os cursos de TÉCNICO em formato para combo
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino/discente/busca.jsp</li>
	 *   <li>/sigaa.war/stricto/relatorios/form_orientacoes.jsp</li>
	 * </ul>
	 * @return 
	 */
	public Collection<SelectItem> getAllCursoTecnicoCombo() throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		return toSelectItems(dao.findByNivel(getNivelEnsino(), true, null, getUsuarioLogado().getVinculoAtivo().getUnidade()), "id", "descricao");
	}

	/**
	 * Retorna todos os cursos do nível e unidade gestora acadêmica atuais
	 * definidos na sessão em formato para combo <br/>
	 * <br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 * <li>/sigaa.war/ensino/tecnico/modulo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllCursoNivelAtualCombo() throws ArqException{
		CursoDao dao = getDAO(CursoDao.class);
		return toSelectItems(dao.findByNivel(getNivelEnsino(), true, null, new Unidade(getUnidadeGestora())), "id", "descricao");
	}
	
	/**
	 * Buscar os cursos aos quais um usuário tem acesso para gerenciamento
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/relatorios/form_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllCursosUsuarioCombo() throws ArqException{
		CursoDao dao = getDAO(CursoDao.class);
		Collection<Curso> cursos = null;

		DadosAcesso acesso = getAcessoMenu();

		if(acesso.isPpg() && getSubSistema().equals(SigaaSubsistemas.STRICTO_SENSU)){
			cursos = dao.findByNivel(NivelEnsino.STRICTO);
		}else if(acesso.isPpg() && getSubSistema().equals(SigaaSubsistemas.LATO_SENSU)){	
			cursos = dao.findByNivel(NivelEnsino.LATO);
		}else if ((acesso.isSecretariaPosGraduacao() || acesso.isCoordenadorCursoStricto())
				&& getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO)) {
			cursos = dao.findByUnidade(getProgramaStricto().getId(), NivelEnsino.STRICTO);
		} else if(acesso.isTecnico() && getSubSistema().equals(SigaaSubsistemas.TECNICO)){
			cursos = dao.findByUnidade(getUnidadeGestora(), NivelEnsino.TECNICO);
		} else if(acesso.isFormacaoComplementar() && getSubSistema().equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR)){
			cursos = dao.findByUnidade(getUnidadeGestora(), NivelEnsino.FORMACAO_COMPLEMENTAR);
		} else if((acesso.isCoordenadorCursoLato() || acesso.isSecretarioLato()) 
				&& getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)){
			cursos = new HashSet<Curso>();
			cursos.add(getCursoAtualCoordenacao());
		}else if(acesso.isAdministradorDAE() && getSubSistema().equals(SigaaSubsistemas.GRADUACAO)){
			cursos = getAllCursoGraduacao();
		} 

		return toSelectItems(cursos, "id", "descricao");
	}

	public Map<Unidade, LinhaListaCursos> getLista() {
		return lista;
	}

	public void setLista(Map<Unidade, LinhaListaCursos> lista) {
		this.lista = lista;
	}

	public ProcessoSeletivo getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivo processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	/**
	 * Retorna uma lista com todos os cursos que o usuário tem acesso
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/coordenacao.jsp</li>
	 *   <li>/sigaa.war/graduacao/coordenacao.jsp</li>
	 *   <li>/sigaa.war/lato/coordenacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getAllCursosCoordenacaoNivelCombo(){
		if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO) ){

			ArrayList<Unidade> programas = new ArrayList<Unidade>();
			if( isEmpty( getAcessoMenu().getProgramas() ) )
				return null;
			for(Unidade prog : getAcessoMenu().getProgramas() ){
				if( getProgramaStricto().getId() != prog.getId())
					programas.add(prog);
			}
			return toSelectItems(programas, "id", "nome");

		}else{
			ArrayList<Curso> cursos = new ArrayList<Curso>();
			Collection<Curso> cursosCoord = getAllCursosCoordenacaoNivel();
			if( cursosCoord == null )
				return null;
			for(Curso c : cursosCoord ){
				if( getCursoAtualCoordenacao().getId() != c.getId())
					cursos.add(c);
			}
			return toSelectItems(cursos, "id", "descricao");
		}
	}
	
	
	/**
	 * Utilizado nos casos de uso de cadastro e alteracao de estrutura curricular pelo
	 * coordenador de curso stricto sensu.
	 * 
	 * Chamado por:
	 * sigaa.war/stricto/curriculo/curso_estrutura.jsp
	 * sigaa.war/graduacao/curriculo/geral.jsp
	 * 
	 * @return
	 */
	public List<SelectItem> getAllProgramasAcesso() {		
		return toSelectItems(getAcessoMenu().getProgramas(), "id", "nome");
	}

	/**
	 * Retorna uma lista com todos os cursos que o coordenador logado tem acesso.
	 * Esta coleção só possui resultados quando o programa do coordenador possui mais de um 
	 * calendário ou parametros da gestora configurados para os cursos do programa
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/coordenacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getAllCursosCoordenadorStrictoCombo() throws DAOException{
		
		CursoDao dao = getDAO(CursoDao.class);
		
		Collection<Curso> cursos = new ArrayList<Curso>();
		
		Collection<Curso> cursosDoPrograma = new ArrayList<Curso>();
		
		if(!isEmpty(getProgramaStricto())) {
			cursosDoPrograma = dao.findByPrograma( getProgramaStricto().getId() );
		}	
		
		//HashSet idsCalendarios = new HashCodeUtil()
		HashSet<Integer> idsCursos = new HashSet<Integer>();
		
		if(!isEmpty(cursosDoPrograma)){
			for(Curso c : cursosDoPrograma){			
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(c);		
				
				if( cal != null && cal.getCurso() != null && cal.getCurso().getId() > 0 ){
					idsCursos.add(cal.getCurso().getId());
				}
			}
		}
		
		if( ! isEmpty(idsCursos) ){
			for( Integer i : idsCursos ){				
				if(isEmpty(getCursoAtualCoordenadroStricto()) || getCursoAtualCoordenadroStricto().getId() != i)
					cursos.add( dao.findByPrimaryKey(i, Curso.class) );			
			}
		}
		return toSelectItems(cursos, "id", "descricao");
		//return toSelectItems(cursosDoPrograma, "id", "descricao");
		
	}
	
	/**
	 * Combo com a lista de todos os cursos do ensino a distância
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ead/matricula_lote/selecionar_discentes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getAllCursosGraduacaoEADCombo() throws DAOException {
		CursoDao dao = getDAO(CursoDao.class);
		
		List<Curso> allCursosEAD = dao.findAllCursosGraduacaoADistancia();
		
		return toSelectItems(allCursosEAD, "id", "descricao");
		
	}

	/**
	 * Combo com a lista de todos os cursos presenciais de graduação
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getAllCursosGraduacaoPresenciaisCombo() throws DAOException {
		CursoDao dao = getDAO(CursoDao.class);
		
		List<Curso> allCursos = dao.findAllCursosGraduacaoPresenciais();
		return toSelectItems(allCursos, "id", "descricao");
		
	}
	
	/**
	 * Combo com a lista de todos os cursos a distãncia de graduação
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getAllCursosGraduacaoADistanciaCombo() throws DAOException {
		CursoDao dao = getDAO(CursoDao.class);
		
		List<Curso> allCursos = dao.findAllCursosADistancia();
		return toSelectItems(allCursos, "id", "descricao");
		
	}
	
	/**
	 * Troca o curso em sessão do usuário
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/coordenador.jsp</li>
	 *   <li>/sigaa.war/lato/coordenador.jsp</li>
	 *   <li>/sigaa.war/stricto/coordenador.jsp</li>
	 * </ul>
	 * @param e
	 * @throws SegurancaException
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws NegocioException
	 */
	public void trocarCurso(ValueChangeEvent e) throws SegurancaException {

		int id = new Integer(e.getNewValue().toString());
		GenericDAO dao = getGenericDAO();
		try {
			getAcessoMenu();
			if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) 
			        && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO) ) {

				Unidade programaAtual = getProgramaStricto();
				if(id > 0 && id != programaAtual.getId()){
					Unidade novoPrograma = dao.findByPrimaryKey(id, Unidade.class);
					if( novoPrograma != null ){
						novoPrograma.getNomeMunicipio();
						getCurrentSession().setAttribute(SigaaAbstractController.PROGRAMA_ATUAL, novoPrograma);
						getCurrentSession().setAttribute(SigaaAbstractController.CURSO_ATUAL_COORDENADOR_STRICTO, null);
						getCurrentSession().setAttribute(SigaaAbstractController.CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario(novoPrograma));
					}
				}

			}else if( isUserInRole( SigaaPapeis.SECRETARIA_GRADUACAO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.COORDENADOR_CURSO ) 
			        && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR) ) {

				Curso cursoAtual = getCursoAtualCoordenacao();
				if(id > 0 && id != cursoAtual.getId()){
					Curso novoCurso;
						novoCurso = dao.findByPrimaryKey(id, Curso.class);
					if(novoCurso != null)
						getCurrentSession().setAttribute("cursoAtual", novoCurso);
				}
				PortalCoordenadorGraduacaoMBean mbean = (PortalCoordenadorGraduacaoMBean) getMBean("portalCoordenadorGrad");
				mbean.setCurso( getCursoAtualCoordenacao() );
				mbean.recarregarInformacoesPortal();

			}else if( isUserInRole( SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.COORDENADOR_LATO ) 
			        && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO) ){
				
				Curso cursoAtual = getCursoAtualCoordenacao();
				if(id > 0 && id != cursoAtual.getId()){
					Curso novoCurso;
						novoCurso = dao.findByPrimaryKey(id, Curso.class);
					if(novoCurso != null){
						getCurrentSession().setAttribute("cursoAtual", novoCurso);
						PortalCoordenadorLatoMBean mBean = getMBean("portalCoordenadorLato");
						mBean.setAcompanhamentoCurso(new ArrayList<LinhaAcompanhamentoCursoLato>());
					}
				}
				
			}else if( isUserInRole( SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO ) ){
				Curso cursoAtual = getCursoAtualCoordenacao();
				if(id > 0 && id != cursoAtual.getId()){
					Curso novoCurso;
						novoCurso = dao.findByPrimaryKey(id, Curso.class);
					if(novoCurso != null){
						getCurrentSession().setAttribute("cursoAtual", novoCurso);
					}
				}	
				
			}else
				throw new SegurancaException();

		} catch (DAOException e1) {
			tratamentoErroPadrao(e1);
		}
	}

	/**
	 * Seta na sessão o curso do ccordenador selecionado.
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/coordenador.jsp</li>
	 * </ul>
	 * @param e
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void carregarCursoCoordenadorStricto(ValueChangeEvent e) throws SegurancaException, DAOException {
		
		int id = new Integer(e.getNewValue().toString());
		GenericDAO dao = getGenericDAO();
		
		Curso cursoAtual = getCursoAtualCoordenadroStricto();
		if(id > 0 && (cursoAtual == null || id != cursoAtual.getId() ) ){
			Curso novoCurso = dao.findByPrimaryKey(id, Curso.class);
			if( novoCurso != null ){
				getCurrentSession().setAttribute(SigaaAbstractController.CURSO_ATUAL_COORDENADOR_STRICTO, novoCurso);
				
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(novoCurso);
				getCurrentSession().setAttribute("calendarioAcademico", cal);
				
				getCurrentSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametros(novoCurso));
			}
				
		}else{
			getCurrentSession().setAttribute(SigaaAbstractController.CURSO_ATUAL_COORDENADOR_STRICTO, null);
			getCurrentSession().setAttribute(PROGRAMA_ATUAL, getProgramaStricto());
			getCurrentSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario(getProgramaStricto()));
		}
		
	}
	
	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	public String getDescricaoNivel() {
		return NivelEnsino.getDescricao(this.nivel);
	}

	public String getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(String tipoBusca) {
		this.tipoBusca = tipoBusca;
	}
}
