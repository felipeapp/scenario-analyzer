/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/04/2008
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.CategoriaUnidade;
import br.ufrn.comum.dominio.TipoUnidadeOrganizacional;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.dominio.LinhaPesquisaStricto;

/**
 * Managed-Bean respons�vel por opera��es com Unidade
 *
 * @author Gleydson
 *
 */
@Component(value = "unidade")
@Scope("request")
public class UnidadeMBean extends SigaaAbstractController<Unidade> {

	/** Lista de unidades retornadas na busca. */
	private Collection<UnidadeGeral> lista;

	/** Lista de departamentos retornadas na busca. */
	private static Collection<Unidade> departamentos;

	/** Nome utilizado como par�metro de busca. */
	private String nome;

	/** Tipo de unidade utilizado como par�metro de busca. */
	private int tipo;
	
	/** Tipo de unidade utilizado como par�metro de busca. */
	private boolean ativo = true;
	
	private boolean checkAtivo = true;

	/** Par�metro utilizado na busca. */
	private String param;
	
	/** Quantidade de resultados de uma pesquisa. */
	private int quantResultado;
	
	/**
	 * Unidade para ser utilizada em qualquer lugar na jsp que precise de uma lista de selectitem.
	 */
	protected Collection<SelectItem> unidades = new ArrayList<SelectItem>();

	/** Lista de linhas de pesquisa retornadas na busca. */
	private ArrayList<LinhaPesquisaStricto> linhasPesquisas;

	/** Lista de �reas retornadas na busca. */
	private ArrayList<AreaConcentracao> areas;

	/** Lista de equipes de programas retornadas na busca. */
	private ArrayList<EquipePrograma> equipePrograma;
	/** Armazenar os tipos de unidade consideradas na busca de turmas */
	private Integer[] tiposUnidadesConsideradasBusca;
	// cole��es utilizada para gera��o de "Comboboxes", para evitar v�rias consultas numa mesma request
	
	/** Colece��o de SelectItem com todas as Unidades existentes. */
	private Collection<SelectItem> allCombo;
	/** Cole��o de selectItem de unidades que possuem componentes curriculares. */
	private Collection<SelectItem> allDetentorasComponentesCombo;
	/** Cole��o de selectItem de unidades que possuem componentes curriculares, informado a sigla da unidade. */
	private Collection<SelectItem> allDetentorasComponentesBySilgaAcademicaCombo;
	/** Cole��o de selectItem de unidades que possuem componentes curriculares de gradua��o. */
	private Collection<SelectItem> allDetentorasComponentesGraduacaoCombo;
	/** Cole��o de selectItem de todas as unidades gestoras acad�micas(Somente se elas s�o de fato gestora de alguma outra unidade) */
	private Collection<SelectItem> allGestorasAcademicasCombo;
	/** Cole��o de selectItem de todas as unidades gestoras acad�micas que podem definir hor�rios. */
	private Collection<SelectItem> allGestorasAcademicasDefinemHorariosCombo;
	/** Cole��o de selectItem de todas as unidades gestoras acad�micas do tipo UNID_ACADEMICA_ESPECIALIZADA do n�vel Gradua��o e T�cnico. */
	private Collection<SelectItem> allCentrosUnidAcademicaGraduacaoTecnicoCombo;
	/** Cole��o de selectItem de Centros.*/
	private Collection<SelectItem> allCentroCombo;
	/** Cole��o de selectItem de todos os departamentos. */
	private Collection<SelectItem> allDeptoCombo;
	/** Cole��o de selectItem de centros acad�micos, as unidades acad�micas espec�ficas e os �rg�os suplementares. */
	private Collection<SelectItem> centrosEspecificasEscolas;
	/** Cole��o de selectItem de todos os Centros ou Unidades Especializadas. */
	private Collection<SelectItem> allCentrosEscolasCombo;
	/** Cole��o de selectItem de departamentos e unidades acad�micas especializadas. */
	private Collection<SelectItem> allDeptosEscolasCombo;
	/** Cole��o de selectItem de departamentos e unidades acad�micas especializadas. */
	private Collection<SelectItem> allDeptosEscolasCoordCursosCombo;
	/** Cole��o de selectItem de departamentos, programas de p�s gradua��o e unidades acad�micas especializadas. */
	private Collection<SelectItem> allDeptosProgramasEscolasCombo;
	/** Cole��o de selectItem de departamentos, programas de p�s gradua��o e unidades acad�micas especializadas. */
	private Collection<SelectItem> allDeptosProgramasEscolasOrgaoCombo;
	/** Cole��o de selectItem de unidades do tipo Departamento. */
	private Collection<SelectItem> allDepartamentoCombo;
	/** Cole��o de selectItem de unidades do tipo Departamento e unidade acad�mica especializada. */
	private Collection<SelectItem> allDepartamentoUnidAcademicaCombo;
	/** Cole��o de selectItem de unidades do tipo unidade acad�mica especializada. */
	private Collection<SelectItem> allEscolaCombo;
	/** Cole��o de selectItem de programas de p�s gradua��o. */
	private Collection<SelectItem> allProgramaPosCombo;
	/** Cole��o de selectItem de coordena��es de curso. */
	private Collection<SelectItem> allCoordenacaoCursoCombo;
	/** Cole��o de selectItem de programas de resid�ncia de acordo com a permiss�o do usu�rio. */
	private Collection<SelectItem> allProgramaResidenciaCombo;
	/** Cole��o de selectItem de todos os programas que o usu�rio tem acesso. */
	private Collection<SelectItem> allProgramasSecretariaCombo;
	/** Cole��o de selectItem de unidades do tipo Hospital. */
	private Collection<SelectItem> hospitais;
	/** Cole��o de selectItem de unidades que tem permiss�o de propor projetos. */
	private Collection<SelectItem> unidadesProponentesProjetosCombo;
	/** Cole��o de selectItem de todas as unidades or�ament�rias.	 */
	private Collection<SelectItem> allUnidadesOrcamentariasCombo;
	/** Cole��o de selectItem de todas as unidades or�ament�rias de extens�o. */ 
	private Collection<SelectItem> unidadesOrcamentariasExtensaoCombo;
	
	/** Construtor padr�o. */
	public UnidadeMBean(){
		obj = new Unidade();
	}

	/**
	 * Retorna todas as Unidades existentes
	 * @author Edson Anibal
	 */
	@Override
	public Collection<SelectItem> getAllCombo()	{
		if (allCombo == null) {
			allCombo = new ArrayList<SelectItem>();
			UnidadeDao uniDao = getDAO(UnidadeDao.class);
			try	{
				allCombo = toSelectItems(uniDao.findAll(Unidade.class, "nome", "asc"), "id", "nome");
			}
			catch(DAOException e) { notifyError(e); }
		}
		return allCombo;
	}

	/**
	 * Retorna todas as unidades gestoras acad�micas.
	 *
	 * @return
	 */
	public Collection<Unidade> getAllGestorasAcademicas() {
		UnidadeDao uniDao = getDAO(UnidadeDao.class);
		try {
			ArrayList<Unidade> unidades = (ArrayList<Unidade>) uniDao.findAllGestorasAcademicas();
			Collections.sort( unidades, new Comparator<Unidade>() {
				public int compare(Unidade u1, Unidade u2){
					int result = 0;
					if( u1.getTipoAcademica() != null && u2.getTipoAcademica() != null) {
						result = u1.getTipoAcademica() - u2.getTipoAcademica();
					}
					if( result == 0 ){
						String nome1 =  StringUtils.toAscii( u1.getNome() );
						result = nome1.compareToIgnoreCase(  StringUtils.toAscii( u2.getNome() ) );
					}

					return result;
				}
			});
			return unidades;
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<Unidade>();
		}

	}
	
	
	/**
	 * Retorna todas as unidades gestoras acad�micas que definem hor�rios.
	 *
	 * @return
	 */
	public Collection<Unidade> getAllGestorasAcademicasDefinemHorarios() {
		UnidadeDao uniDao = getDAO(UnidadeDao.class);
		try {
			ArrayList<Unidade> unidades = (ArrayList<Unidade>) uniDao.findAllGestorasAcademicasDefinemHorarios();
						return unidades;
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<Unidade>();
		}

	}

	/** Retorna uma cole��o de selectItem de unidades que possuem componentes curriculares.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getAllDetentorasComponentesCombo() {
		if (isNecessarioRegarregar()) {
			UnidadeDao uniDao = getDAO(UnidadeDao.class);
		
			Collection<Unidade> unidades;
			Collection<Unidade> detentoras = new ArrayList<Unidade>(0);
			try {
				tiposUnidadesConsideradasBusca = getTiposUnidadesAcademicas();
				unidades = uniDao.findDetentoraComponentesByTipoUnidadeAcademica(tiposUnidadesConsideradasBusca);				
				if (unidades != null) {
					detentoras.addAll(unidades);
				}
			} catch (DAOException e) {
				notifyError(e);
			}
			allDetentorasComponentesCombo = toSelectItems(detentoras, "id", "nomeMunicipio");
		}
		return allDetentorasComponentesCombo;
	}

	
	/**
	 * Utilizado para decidir quais tipos de unidade ser�o consideradas na busca de unidades.
	 * @return
	 */
	private Integer[] getTiposUnidadesAcademicas() {
		if(NivelEnsino.isAlgumNivelStricto(getNivelEnsino())) {
			Integer[] programasPos = {TipoUnidadeAcademica.PROGRAMA_POS};
			return programasPos;
		} else {
			Integer[] unidades = {TipoUnidadeAcademica.DEPARTAMENTO,TipoUnidadeAcademica.COORDENACAO_CURSO,
					TipoUnidadeAcademica.ESCOLA, TipoUnidadeAcademica.PROGRAMA_POS, TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA};
			return unidades;
		}
	}
	
	/**
	 * Indica se ser� necess�rio regarregar as unidades para a busca de turmas.
	 */
	private boolean isNecessarioRegarregar() {
		if(allDetentorasComponentesCombo == null || ! isTiposIguais(tiposUnidadesConsideradasBusca, getTiposUnidadesAcademicas()) ) {
			return true;
		} else {		
			return false;
		}
	}
	
	/**
	 * 
	 * Utilizado para verificar se os tipos de unidade a 
	 * considerar na busca de turmas s�o iguais.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean isTiposIguais(Integer [] a, Integer[] b ) {		
		if( a == null || b == null || a.length != b.length )
			return false;	
		List<Integer> tiposA = Arrays.asList(a);
		List<Integer> tiposB = Arrays.asList(b);		
		Collections.sort(tiposA);
		Collections.sort(tiposB);		
		return tiposA.equals(tiposB);				
	}
	
	/** Retorna uma cole��o de selectItem de unidades que possuem componentes curriculares, informado a sigla da unidade.
	 * @return
	 */
	public Collection<SelectItem> getAllDetentorasComponentesBySilgaAcademicaCombo() {
		if (allDetentorasComponentesBySilgaAcademicaCombo == null) {
			Collection<Unidade> unidades = getAllUnidadeAcademica(TipoUnidadeAcademica.DEPARTAMENTO,TipoUnidadeAcademica.COORDENACAO_CURSO,
					TipoUnidadeAcademica.ESCOLA, TipoUnidadeAcademica.PROGRAMA_POS, TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA );
			Collection<Unidade> detentoras = new ArrayList<Unidade>(0);
			if (unidades != null) {
				detentoras.addAll(unidades);
			}
			allDetentorasComponentesBySilgaAcademicaCombo = toSelectItems(detentoras, "siglaAcademica", "nomeMunicipio");
		}
		return allDetentorasComponentesBySilgaAcademicaCombo;
	}

	/** Retorna uma cole��o de selectItem de unidades que possuem componentes curriculares de gradua��o.
	 * @return
	 */
	public Collection<SelectItem> getAllDetentorasComponentesGraduacaoCombo() {
		if (allDetentorasComponentesGraduacaoCombo == null) {
			Collection<Unidade> unidades = getAllUnidadeAcademica(TipoUnidadeAcademica.DEPARTAMENTO,TipoUnidadeAcademica.COORDENACAO_CURSO,
					TipoUnidadeAcademica.ESCOLA, TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA );
			Collection<Unidade> detentoras = new ArrayList<Unidade>(0);
			if (unidades != null) {
				detentoras.addAll(unidades);
			}
			allDetentorasComponentesGraduacaoCombo = toSelectItems(detentoras, "id", "nomeMunicipio");
		}
		return allDetentorasComponentesGraduacaoCombo;
	}

	/**
	 * Retorna todas as unidades gestoras acad�micas(Somente se elas s�o de fato gestora de alguma outra unidade)
	 *
	 * @return
	 */
	public Collection<SelectItem> getAllGestorasAcademicasCombo() {
		if (allGestorasAcademicasCombo == null) {
			allGestorasAcademicasCombo = new ArrayList<SelectItem>();
			allGestorasAcademicasCombo.add(new SelectItem("0", "-- SELECIONE --"));
			allGestorasAcademicasCombo.addAll(toSelectItems(getAllGestorasAcademicas(),"id", "nome"));
		}
		return allGestorasAcademicasCombo;
	}
	
	/**
	 * Retorna todas as unidades gestoras acad�micas que podem definir hor�rios
	 *
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/administracao/cadastro/horarios.jsp
	 *
	 * @return
	 */
	public Collection<SelectItem> getAllGestorasAcademicasDefinemHorariosCombo() {
		if (allGestorasAcademicasDefinemHorariosCombo == null) {
			allGestorasAcademicasDefinemHorariosCombo = new ArrayList<SelectItem>();
			allGestorasAcademicasDefinemHorariosCombo.add(new SelectItem("0", "-- SELECIONE --"));
			allGestorasAcademicasDefinemHorariosCombo.addAll(toSelectItems(getAllGestorasAcademicasDefinemHorarios(),"id", "nome"));
		}
		return allGestorasAcademicasDefinemHorariosCombo;
	}

	/**
	 * Retorna todas as unidades gestoras acad�micas
	 *
	 * @return
	 */
	public Collection<Unidade> getAllDepartamento() {
		
		if ( departamentos == null) {
			departamentos = getAllUnidadeAcademica(TipoUnidadeAcademica.DEPARTAMENTO);
		}
		if (departamentos == null ) {
			departamentos = new ArrayList<Unidade>();
		}
		return departamentos;
	}
	
	/**
	 * Retorna todas as unidades gestoras acad�micas do tipo DEPARTAMENTO e UNID_ACADEMICA_ESPECIALIZADA.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> getAllDepartamentoUnidAcademica() throws DAOException {
		return getDAO(UnidadeDao.class).findByTipoUnidadeAcademica(TipoUnidadeAcademica.DEPARTAMENTO,
				TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
	}
	
	/**
	 * Retorna todas as unidades gestoras acad�micas do tipo UNID_ACADEMICA_ESPECIALIZADA do n�vel Gradua��o e T�cnico
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> getAllCentrosUnidAcademicaGraduacaoTecnico() throws DAOException {
		Collection<Integer> tiposAcademicas = new ArrayList<Integer>();
		Collection<Character> niveis = new ArrayList<Character>();
		
		tiposAcademicas.add(TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
		tiposAcademicas.add(TipoUnidadeAcademica.CENTRO);
		niveis.add(NivelEnsino.GRADUACAO);
		niveis.add(NivelEnsino.TECNICO);
		
		return getDAO(UnidadeDao.class).findByTipoUnidadeAcademicaNivel(tiposAcademicas,niveis);
	}
	
	
	/**
	 * Retorna itens de combo com todas as unidades gestoras acad�micas do tipo UNID_ACADEMICA_ESPECIALIZADA do n�vel Gradua��o e T�cnico
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCentrosUnidAcademicaGraduacaoTecnicoCombo() throws DAOException {
		if (allCentrosUnidAcademicaGraduacaoTecnicoCombo == null)
			allCentrosUnidAcademicaGraduacaoTecnicoCombo = toSelectItems(getAllCentrosUnidAcademicaGraduacaoTecnico(), "id", "nome");
		return allCentrosUnidAcademicaGraduacaoTecnicoCombo;
	}

	/** Retorna uma cole��o unidades de acordo com o(s) tipo(s) especificados.
	 * @param tipo
	 * @return
	 */
	private Collection<Unidade> getAllUnidadeAcademica(int... tipo) {
		
		UnidadeDao uniDao = getDAO(UnidadeDao.class);
		try {
			return uniDao.findByTipoUnidadeAcademica(tipo);
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<Unidade>();
		}
	}

	/** Retorna uma cole��o unidades do tipo CENTRO.
	 * @return
	 */
	public Collection<Unidade> getAllCentro() {
		return getAllUnidadeAcademica(TipoUnidadeAcademica.CENTRO);
	}

	/** Retorna uma cole��o unidades do tipo CENTRO ou UNID_ACADEMICA_ESPECIALIZADA.
	 * @return
	 */
	public Collection<Unidade> getAllCentroEscolas() {
		UnidadeDao uniDao = getDAO(UnidadeDao.class);
		try {
			return uniDao.findByTipoUnidadeAcademica(TipoUnidadeAcademica.CENTRO, TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<Unidade>();
		}
	}

	/** Retorna uma cole��o de unidades do tipo UNID_ACADEMICA_ESPECIALIZADA.
	 * @return
	 */
	public Collection<Unidade> getAllEscola() {
		return getAllUnidadeAcademica(TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
	}


	/** Retorna uma lista de selectItem de Centros.
	 * @return
	 */
	public Collection<SelectItem> getAllCentroCombo() {
		if (allCentroCombo == null)
			allCentroCombo = toSelectItems(getAllCentro(), "id", "nome");
		return allCentroCombo;
	}
	
	/**
	* Retorna todos os departamentos
	*/
	public Collection<SelectItem> getAllDeptoCombo() {
		if (allDeptoCombo == null)
			allDeptoCombo = toSelectItems(getAllDepartamento(), "id", "nome");
		return allDeptoCombo;
	}
	
	/**
	 * Retorna os centros acad�micos, as unidades acad�micas espec�ficas e os �rg�os suplementares
	 * @return
	 */
	public Collection<SelectItem> getCentrosEspecificasEscolas() {
		if (centrosEspecificasEscolas == null) {
			UnidadeDao uniDao = getDAO(UnidadeDao.class);
			try {
				centrosEspecificasEscolas = toSelectItems(uniDao.findByTipoUnidadeAcademica(TipoUnidadeAcademica.CENTRO,
						TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA, TipoUnidadeAcademica.ORGAO_SUPLEMENTAR
						), "id", "nomeMunicipio");
			} catch (DAOException e) {
				notifyError(e);
				centrosEspecificasEscolas = new ArrayList<SelectItem>();
			}
		}
		return centrosEspecificasEscolas;
	}

	/**
	 * Permite listar todos os Centros ou Unidades Especializadas
	 * @return
	 */
	public Collection<SelectItem> getAllCentrosEscolasCombo() {
		if (allCentrosEscolasCombo == null)
			allCentrosEscolasCombo = toSelectItems(getAllCentroEscolas(), "id", "nome");
		return allCentrosEscolasCombo;
	}
	
	/** Retorna uma cole��o unidades do tipo DEPARTAMENTO, UNID_ACADEMICA_ESPECIALIZADA ou COORDENACAO_CURSO);
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> getAllDeptosEscolas() throws DAOException {
		UnidadeDao uniDao = getDAO(UnidadeDao.class);
		return uniDao.findByTipoUnidadeAcademica(TipoUnidadeAcademica.DEPARTAMENTO,
				TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
	}

	
	/** Retorna uma cole��o unidades do tipo DEPARTAMENTO);
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> getAllDeptosEscolasCoordCursos() throws DAOException {
		UnidadeDao uniDao = getDAO(UnidadeDao.class);
		return uniDao.findByTipoUnidadeAcademica(TipoUnidadeAcademica.DEPARTAMENTO,
				TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA, TipoUnidadeAcademica.COORDENACAO_CURSO);
	}
	
	
	/** Retorna uma cole��o de selectItem de departamentos, unidades acad�micas especializadas e coordenacoes de cursos.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllDeptosEscolasCoordCursosCombo() throws DAOException {
		if (allDeptosEscolasCoordCursosCombo == null)
			allDeptosEscolasCoordCursosCombo = toSelectItems(getAllDeptosEscolasCoordCursos(), "id", "nomeMunicipio");
		return allDeptosEscolasCoordCursosCombo;
	}

	
	/** Retorna uma cole��o de selectItem de departamentos e unidades acad�micas especializadas.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllDeptosEscolasCombo() throws DAOException {
		if (allDeptosEscolasCombo == null)
			allDeptosEscolasCombo = toSelectItems(getAllDeptosEscolas(), "id", "nomeMunicipio");
		return allDeptosEscolasCombo;
	}

	/** Retorna uma cole��o de selectItem de departamentos, programas de p�s gradua��o e unidades acad�micas especializadas.
	 * @return
	 */
	public Collection<SelectItem> getAllDeptosProgramasEscolasCombo() {
		if (allDeptosProgramasEscolasCombo == null) {
			UnidadeDao uniDao = getDAO(UnidadeDao.class);
			try {
				allDeptosProgramasEscolasCombo = toSelectItems(uniDao.findByTipoUnidadeAcademica(TipoUnidadeAcademica.DEPARTAMENTO,
						TipoUnidadeAcademica.PROGRAMA_POS,
						TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA
						), "id", "nomeMunicipio");
			} catch (DAOException e) {
				notifyError(e);
				allDeptosProgramasEscolasCombo = new ArrayList<SelectItem>();
			}
		}
		return allDeptosProgramasEscolasCombo;
	}

	/** Retorna uma cole��o de selectItem de departamentos, programas de p�s gradua��o e unidades acad�micas especializadas.
	 * @return
	 */
	public Collection<SelectItem> getAllDeptosProgramasEscolasOrgaoCombo() {
		if (allDeptosProgramasEscolasOrgaoCombo == null) {
			UnidadeDao uniDao = getDAO(UnidadeDao.class);
			try {
				allDeptosProgramasEscolasOrgaoCombo = toSelectItems(uniDao.findByTipoUnidadeAcademica(TipoUnidadeAcademica.DEPARTAMENTO,
						TipoUnidadeAcademica.ESCOLA,
						TipoUnidadeAcademica.PROGRAMA_POS,
						TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA,
						TipoUnidadeAcademica.ORGAO_SUPLEMENTAR
						), "id", "nomeMunicipio");
			} catch (DAOException e) {
				notifyError(e);
				allDeptosProgramasEscolasOrgaoCombo = new ArrayList<SelectItem>();
			}
		}
		return allDeptosProgramasEscolasOrgaoCombo;
	}

	/** Retorna uma cole��o de selectItem de unidades do tipo Departamento.
	 * @return
	 */
	public Collection<SelectItem> getAllDepartamentoCombo() {
		if (allDepartamentoCombo == null)
			allDepartamentoCombo = toSelectItems(getAllDepartamento(), "id", "nome");
		return allDepartamentoCombo;
	}
	
	/** Retorna uma cole��o de selectItem de unidades do tipo Departamento e unidade acad�mica especializada.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getAllDepartamentoUnidAcademicaCombo() throws DAOException {
		if (allDepartamentoUnidAcademicaCombo == null)
			allDepartamentoUnidAcademicaCombo = toSelectItems(getAllDepartamentoUnidAcademica(), "id", "nome");
		return allDepartamentoUnidAcademicaCombo;
	}

	/** Retorna uma cole��o de selectItem de unidades do tipo unidade acad�mica especializada.
	 * @return
	 */
	public Collection<SelectItem> getAllEscolaCombo() {
		if (allEscolaCombo == null)
			allEscolaCombo = toSelectItems(getAllEscola(), "id", "nome");
		return allEscolaCombo;
	}


	/** Retorna uma cole��o de programas de p�s gradua��o
	 * @return
	 */
	public Collection<Unidade> getAllProgramaPos(){
		return getAllUnidadeAcademica(TipoUnidadeAcademica.PROGRAMA_POS);
	}

	/** Retorna uma cole��o de selectItem de programas de p�s gradua��o
	 * @return
	 */
	public Collection<SelectItem> getAllProgramaPosCombo(){
		if (allProgramaPosCombo == null)
			allProgramaPosCombo = toSelectItems(getAllProgramaPos(), "id", "nome");
		return allProgramaPosCombo;
	}

	/** Retorna uma cole��o de coordena��es de curso.
	 * @return
	 */
	public Collection<Unidade> getAllCoordenacaoCurso(){
		return getAllUnidadeAcademica(TipoUnidadeAcademica.COORDENACAO_CURSO);
	}

	/** Retorna uma cole��o de selectItem de coordena��es de curso.
	 * @return
	 */
	public Collection<SelectItem> getAllCoordenacaoCursoCombo(){
		if (allCoordenacaoCursoCombo == null)
			allCoordenacaoCursoCombo = toSelectItems(getAllCoordenacaoCurso(), "id", "nome");
		return allCoordenacaoCursoCombo;
	}

	/**
	 * Retorna uma cole��o de selectItem de programas de resid�ncia de acordo com a permiss�o do usu�rio.
	 * @return
	 */
	public Collection<SelectItem> getAllProgramaResidenciaCombo() {
		if (allProgramaResidenciaCombo == null) {
			Collection<Unidade> tmp = new ArrayList<Unidade>();
			if(isUserInRole(SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA,SigaaPapeis.SECRETARIA_RESIDENCIA) && isPortalComplexoHospitalar()){
				tmp.addAll(getAcessoMenu().getResidencias());
			}
			else if(isUserInRole(SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR) && isPortalComplexoHospitalar())
				tmp = getAllUnidadeAcademica(TipoUnidadeAcademica.PROGRAMA_RESIDENCIA);
			allProgramaResidenciaCombo = toSelectItems(tmp, "id", "nome");
		}
		return allProgramaResidenciaCombo;
	}
	
	/**
	 * Busca unidades por nome
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs: 
	 * <ul>
	 *   <li>/sigaa.war/administracao/cadastro/Unidade/lista.jsp</li>
	 *   <li>/sigaa.war/geral/unidade/busca_geral.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void buscar(ActionEvent evt) throws DAOException {
		UnidadeDao uniDao = getDAO(UnidadeDao.class);
		lista = uniDao.findByNomeTipo(nome,tipo);
		obj = new Unidade();
	}

	/** Inicia a busca por unidades.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs: 
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *   <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 *   <li>/sigaa.war/stricto/menus/cadastro.jsp</li>
	 * </ul>
	 * @return
	 */
	public String popularBuscaGeral(){
		param = null;
		nome = "";
		lista = new ArrayList<UnidadeGeral>(0);
		return forward("/geral/unidade/busca_geral.jsf");
	}

	/** Inicia a busca por unidades acad�micas
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String popularUnidadeAcademica() throws DAOException {

		nome = "";
		lista = new ArrayList<UnidadeGeral>(0);

		UnidadeDao uniDao = getDAO(UnidadeDao.class);
		try {
			lista = uniDao.findUnidadeAcademicaByNome(nome, TipoUnidadeAcademica.PROGRAMA_POS);
			if( lista.isEmpty() ) {
				addMessage("Nenhuma unidade acad�mica encontrada de acordo com os crit�rios de busca informados.", TipoMensagemUFRN.WARNING);
			}
		} finally {
			if (uniDao != null)
				uniDao.close();
		}

		return forward("/geral/unidade/busca_geral.jsf");
	}

	/**
	 * Busca apenas Unidades Acad�micas
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/geral/unidade/busca_geral.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public String buscarAcademica() throws DAOException {
		String gerarRelatorio = getParameter( "paramRelatorio" );
		param = getParameter("paramBusca");
		String tipoBusca = getParameter( "paramBusca" );
		if( isEmpty(tipoBusca) ){
			addMensagem( MensagensArquitetura.SELECIONE_OPCAO_BUSCA, "tipo" );
			return null;
		}

			//Boolean checkNome = Boolean.valueOf( getParameter( "checkNome" ) );
			//Boolean checkTipo = Boolean.valueOf( getParameter( "checkTipo" ) );

		if( "tipo".equals(tipoBusca)){
			this.nome = "";
		}
		else {
			this.tipo = 0;
		}
		
		if( "nome".equals(tipoBusca) && isEmpty(nome) ){
			addMensagemErro("Nome: Campo de preenchimento obrigat�rio n�o informado.");
			return null;
		} else if( "nome".equals(tipoBusca) && nome != null && nome.length() < 3){
			addMensagemErro("Infome pelo menos 3 caracteres para a busca.");
			lista = new ArrayList<UnidadeGeral>(0);
			return null;
		} else if( "tipo".equals(tipoBusca) && isEmpty(tipo) ){
			addMensagemErro("Tipo: Campo de preenchimento obrigat�rio n�o informado.");
			return null;
		}
		
		UnidadeDao uniDao = getDAO(UnidadeDao.class);
		lista = uniDao.findUnidadeAcademicaByNome(nome, tipo, checkAtivo ? ativo : null);
		quantResultado = lista.size();
		if( lista.isEmpty() ) {
			addMessage("Nenhuma unidade acad�mica encontrada de acordo com os crit�rios de busca informados.", TipoMensagemUFRN.WARNING);
		}

		if (!isEmpty(gerarRelatorio)) {
			return forward("/geral/unidade/busca_relatorio.jsp");
		} else {
			return forward("/geral/unidade/busca_geral.jsf");
		}
	}
	/**
	 * Exibe os dados da unidade
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/geral/unidade/busca_geral.jsp</li>
	 * </ul>
	 * @return
	 */
	public String detalhar(){
		GenericDAO dao = getGenericDAO();
		String id = getParameter("id");
		obj.setId( Integer.parseInt(id) );

		try {
			obj = dao.findByPrimaryKey(obj.getId(), Unidade.class);
		} catch (DAOException e) {
			addMensagemErro(e.getMessage());
		}

		UnidadeDao uniDao = getDAO(UnidadeDao.class);

		linhasPesquisas = uniDao.buscarLinhasPesquisa( Integer.parseInt(id) );
		areas = uniDao.buscarAreas( Integer.parseInt(id) );
		equipePrograma = uniDao.buscarEquipePrograma( Integer.parseInt(id) );

		return forward("/geral/unidade/resumo.jsp");
	}


	/** Retorna a lista de unidades retornadas na busca.
	 * @return
	 */
	public Collection<UnidadeGeral> getLista() {
		return lista;
	}

	/** Seta a lista de unidades retornadas na busca. 
	 * @param lista
	 */
	public void setLista(Collection<UnidadeGeral> lista) {
		this.lista = lista;
	}

	/** Retorna o nome utilizado como par�metro de busca.
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/** Seta o nome utilizado como par�metro de busca. 
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public boolean isCheckAtivo() {
		return checkAtivo;
	}

	public void setCheckAtivo(boolean checkAtivo) {
		this.checkAtivo = checkAtivo;
	}

	/** Retorna o tipo de unidade utilizado como par�metro de busca.
	 * @return
	 */
	public int getTipo() {
		return tipo;
	}

	/** Seta o tipo de unidade utilizado como par�metro de busca. 
	 * @param tipo
	 */
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	/** Retorna uma cole��o selectItem de tipos de unidades.
	 * @return
	 */
	public List<SelectItem> getTipos() {
		return TipoUnidadeAcademica.tipos;
	}


	/**
	 * Retorna uma lista com todos os programas que o usu�rio tem acesso
	 * @return
	 */
	public Collection<SelectItem> getAllProgramasSecretariaCombo(){
		if (allProgramasSecretariaCombo == null)
			allProgramasSecretariaCombo = toSelectItems(getAcessoMenu().getProgramas(), "id", "nome");
		return allProgramasSecretariaCombo;
	}

	/**
	 * Troca o programa em sess�o do usu�rio.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/stricto/coordenador.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void trocarPrograma(ValueChangeEvent e) throws DAOException {
		Unidade programaAtual = getProgramaStricto();
		int id = new Integer(e.getNewValue().toString());
		if(id > 0 && id != programaAtual.getId()){
			Unidade novoPrograma = getGenericDAO().findByPrimaryKey(id, Unidade.class);
			if(novoPrograma != null) {
				getCurrentSession().setAttribute("programa", novoPrograma);
			}
		}
	}

	/**
	 * Altera o centro no select e carrega os departamentos do centro selecionado.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs: 
	 * <ul>
	 *   <li>/sigaa.war/graduacao/relatorios/docente/seleciona_lista_docente.jsp</li>
	 *   <li>/sigaa.war/graduacao/relatorios/turma/seleciona_turma.jsp</li>
	 *   <li>/sigaa.war/infra_fisica/turma/seleciona_turma.jsp</li>
	 *   <li>/sigaa.war/public/curso_aberto/portal.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void changeCentro(ValueChangeEvent e) throws DAOException{
		Integer id = Integer.valueOf( e.getNewValue().toString() );
		Unidade pai = new Unidade(id);

		UnidadeDao dao = getDAO(UnidadeDao.class);
		unidades = toSelectItems( dao.findBySubUnidades(pai, TipoUnidadeAcademica.DEPARTAMENTO, TipoUnidadeAcademica.COORDENACAO_CURSO),"id","nome");
	}

	/** Retorna a unidade para ser utilizada em qualquer lugar na jsp que precise de uma lista de selectItem.
	 * @return
	 */
	public Collection<SelectItem> getUnidades() {
		return unidades;
	}

	/** Seta a unidade para ser utilizada em qualquer lugar na jsp que precise de uma lista de selectItem.
	 * @param unidades
	 */
	public void setUnidades(Collection<SelectItem> unidades) {
		this.unidades = unidades;
	}

	/** Retorna uma lista de SelectItem de unidades do tipo Hospital.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getHospitais() throws DAOException {
		if (hospitais == null) { 
			hospitais = toSelectItems(
				getGenericDAO().findByExactField(Unidade.class,
						"tipoOrganizacional.id",
						TipoUnidadeOrganizacional.HOSPITAL, "asc", new String[] {"nome"}),
				"id", "codigoNome");
		}
		return hospitais;
	}

	/** Retorna a lista de linhas de pesquisa retornadas na busca.
	 * @return
	 */
	public ArrayList<LinhaPesquisaStricto> getLinhasPesquisas() {
		return linhasPesquisas;
	}

	/** Seta a lista de linhas de pesquisa retornadas na busca. 
	 * @param linhasPesquisas
	 */
	public void setLinhasPesquisas(ArrayList<LinhaPesquisaStricto> linhasPesquisas) {
		this.linhasPesquisas = linhasPesquisas;
	}

	/** Retorna a lista de �reas retornadas na busca.
	 * @return
	 */
	public ArrayList<AreaConcentracao> getAreas() {
		return areas;
	}

	/** Seta a lista de �reas retornadas na busca. 
	 * @param areas
	 */
	public void setAreas(ArrayList<AreaConcentracao> areas) {
		this.areas = areas;
	}
	
	
	/** Retorna uma cole��o de unidade que tem permiss�o de propor projetos.
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> getUnidadesProponentesProjetos() throws DAOException {
        	try {
			return getDAO(UnidadeDao.class).findUnidadesProponentesProjetos();
        	} catch (DAOException e) {
        		notifyError(e);
        		return new ArrayList<Unidade>();
        	}
	}
	
	/** Retorna uma cole��o de selectItem de unidades que tem permiss�o de propor projetos.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getUnidadesProponentesProjetosCombo() throws DAOException {
		if (unidadesProponentesProjetosCombo == null)
			unidadesProponentesProjetosCombo = toSelectItems(getUnidadesProponentesProjetos(),	"id", "codigoNome");
		return unidadesProponentesProjetosCombo;
	}

	
	/** Retorna todas as unidades or�ament�rias.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> getAllUnidadesOrcamentarias() throws DAOException {
        	try {
			return getDAO(UnidadeDao.class).findAllUnidadeOrcamentaria();
        	} catch (DAOException e) {
        		notifyError(e);
        		return new ArrayList<UnidadeGeral>();
        	}
	}
	
	/** Retorna uma cole��o de selectItem todas as unidades or�ament�rias.	  
	 * Utilizado por: 
	 * <ul><li>sigaa.war/projetos/VincularUnidadeOrcamentaria/form.jsp</li></ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllUnidadesOrcamentariasCombo() throws DAOException {
		if (allUnidadesOrcamentariasCombo == null) {
			Collection<UnidadeGeral> lista = getAllUnidadesOrcamentarias();
			for (UnidadeGeral unidade : lista) {
				if (unidade.getNomeCapa() != null && unidade.getNomeCapa().length() > 80) { 		    
					unidade.setNomeCapa(unidade.getNomeCapa().substring(0, 80) + "...");
				} else if (unidade.getNomeCapa() == null){
					if (unidade.getNome() != null && unidade.getNome().length() > 80) 
						unidade.setNome(unidade.getNome().substring(0, 80) + "...");
				}
			}			
			allUnidadesOrcamentariasCombo = toSelectItems(lista, "id", "codigoNome");
		}
		return allUnidadesOrcamentariasCombo;
	}
	
	
	/** Retorna todas as unidades or�ament�rias da categoria extens�o.
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> getUnidadesOrcamentariasExtensao() throws DAOException {
        	try {
			return getDAO(UnidadeDao.class).findAllUnidadeOrcamentariaByCategoria(CategoriaUnidade.EXTENSAO, CategoriaUnidade.UNIDADE);
        	} catch (DAOException e) {
        		notifyError(e);
        		return new ArrayList<UnidadeGeral>();
        	}
	}

	/**
	 * Retorna uma cole��o de selectItem todas as unidades or�ament�rias de extens�o.
	 * Utilizado por: 
	 * <ul><li>sigaa.war/extensao/VincularUnidadeOrcamentaria/form.jsp</li></ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getUnidadesOrcamentariasExtensaoCombo() throws DAOException {
		if ( unidadesOrcamentariasExtensaoCombo == null ){
			Collection<UnidadeGeral> lista = getUnidadesOrcamentariasExtensao();
			for (UnidadeGeral unidade : lista) {
				if (unidade.getNomeCapa() != null && unidade.getNomeCapa().length() > 80) { 		    
					unidade.setNomeCapa(unidade.getNomeCapa().substring(0, 80) + "...");
				} else if (unidade.getNomeCapa() == null){
					if (unidade.getNome() != null && unidade.getNome().length() > 80) 
						unidade.setNome(unidade.getNome().substring(0, 80) + "...");
				}
			}
		    unidadesOrcamentariasExtensaoCombo = toSelectItems(lista, "id", "codigoNome");
		}
		return unidadesOrcamentariasExtensaoCombo;
	}

	
	/**
	 *  AutoComplete que busca as unidades a medida que o usu�rio vai digitando o nome da unidade, isso diminui a 
	 *  quantidade de unidades que precis�o ser recuperadas no banco, tornando a busca mais leve.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/estagio/convenio_estagio/form_analise.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 *  
	 */
	public List<Unidade> autocompleteUnidades(Object suggest) throws DAOException {
	
		String nomeUnidade = (String)suggest;
		
		UnidadeDao dao = null;
		
		List<Unidade> unidades = new ArrayList<Unidade>();
		
		try{
			dao = getDAO(UnidadeDao.class);
			unidades = dao.findAtivasByNome(nomeUnidade);
		}finally{
			if(dao != null) dao.close();
		}
		
		return unidades;
		
	}	
	

	/** Retorna a lista de equipes de programas retornadas na busca.
	 * @return
	 */
	public ArrayList<EquipePrograma> getEquipePrograma() {
		return equipePrograma;
	}

	/** Seta a lista de equipes de programas retornadas na busca. 
	 * @param equipePrograma
	 */
	public void setEquipePrograma(ArrayList<EquipePrograma> equipePrograma) {
		this.equipePrograma = equipePrograma;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getParam() {
		return param;
	}

	public void setQuantResultado(int quantResultado) {
		this.quantResultado = quantResultado;
	}

	public int getQuantResultado() {
		return quantResultado;
	}

	public Integer[] getTiposUnidadesConsideradasBusca() {
		return tiposUnidadesConsideradasBusca;
	}

	public void setTiposUnidadesConsideradasBusca(
			Integer[] tiposUnidadesConsideradasBusca) {
		this.tiposUnidadesConsideradasBusca = tiposUnidadesConsideradasBusca;
	}
	
}
