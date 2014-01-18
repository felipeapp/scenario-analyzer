package br.ufrn.sigaa.assistencia.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.ResidenciaUniversitaria;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.jsf.StatusDiscenteMBean;

@Component @Scope("session")
public class ConsultaBolsaAuxilioMBean extends SigaaAbstractController<DiscenteAdapter> {
	
	/**  Limite de resultados da busca */
	private static final long LIMITE_RESULTADOS_MAXIMO = 1800;

	/** Lista de bolsas auxílios . */
	private List<BolsaAuxilio> listaBolsaAuxilio;
	
	/** Indicação do filtro da busca por ano e período. */
	private boolean checkAnoPeriodo = false;
	
	/** Indicação do filtro da busca por tipo de bolsa. */
	private boolean checkTipoBolsa = false;
	
	/** Indicação do filtro da busca por curso. */
	private boolean checkCurso = false;
	
	/** Indicação do filtro da busca por nível de ensino. */
	private boolean checkNivel = false;
	
	/** Indicação do filtro da busca por residência. */
	private boolean checkResidencia = false;
	
	/** Indicação do filtro da busca por deferimento. */
	private boolean checkDeferimento = true;
	
	/** Indicação do filtro da busca por campus. */
	private boolean checkMunicipio = false;
	
	/** Indicação do filtro da busca pelo Sexo. */
	private boolean checkSexo = false;

	/** Indicação do filtro da busca pelo Status. */
	private boolean checkStatus = false;
	
	/** Parâmetros da busca: com deferimento. */
	private Integer deferimento = 1;

	/**  Indicação da escolha do menu: residência . */
	private Integer idResidencia;

	/**  Indicação da escolha do menu: campus . */
	private Integer idMunicipio;

	/**  Indicação da escolha do menu: tipo de bolsa auxílio . */
	private Integer idTipoBolsaAuxilio;

	/** Parâmetros da busca: ano. */
	private Integer ano;
	
	/** Parâmetros da busca: período. */
	private Integer periodo;

	/** Parâmetros da busca: nível de ensino. */
	private char nivel;

	/**  Indicação da escolha do menu: curso . */
	private Integer idCurso;

	/**  Indicação do assistido. */
	private char sexo;

	/**  Status dos Discentes. */
	private Integer idStatusDiscente;

	/** Coleção de todos os cursos. */
	private Collection<SelectItem> allCursos;

	/** Coleção de todos os Status. */
	private Collection<SelectItem> allStatus;

	public ConsultaBolsaAuxilioMBean() throws DAOException {
		clear();
	}

	public void clear() throws DAOException {
		checkAnoPeriodo = false;
		checkTipoBolsa = false;
		checkCurso = false;
		checkNivel = false;
		checkResidencia = false;
		checkDeferimento = true;
		checkMunicipio = false;
		checkSexo = false;
		checkStatus = false;
		deferimento = SituacaoBolsaAuxilio.EM_ANALISE;
		idResidencia = 1;
		idMunicipio = 1;
		idMunicipio = 1;
		idTipoBolsaAuxilio = TipoBolsaAuxilio.RESIDENCIA_GRADUACAO;
		ano = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		nivel = NivelEnsino.GRADUACAO;
		idCurso = 0;
		sexo = PessoaGeral.SEXO_MASCULINO;
		definirNivelEnsino();
		definirStatusDiscente();
	}
	
	public void clearResumido() throws DAOException {
		if (!checkAnoPeriodo) {
			ano = CalendarUtils.getAnoAtual();
			periodo = getPeriodoAtual();
		} 
		if (!checkSexo)
			sexo = PessoaGeral.SEXO_MASCULINO;
		if (!checkDeferimento)
			deferimento = SituacaoBolsaAuxilio.EM_ANALISE;
	}
	
	public List<BolsaAuxilio> buscarBolsaAuxilio() throws Exception {
		BolsaAuxilioDao buscaBolsaAuxilioDao = getDAO(BolsaAuxilioDao.class);

		boolean paramsValido = true;

		if (checkDeferimento && deferimento == null) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Deferimento");
			paramsValido = false;
		} else if (!checkDeferimento) {
			deferimento = 0;
		}
		if (checkResidencia && idResidencia == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Residência");
			paramsValido = false;
		} else if (!checkResidencia) {
			idResidencia = 0;
		}
		if (checkMunicipio && idMunicipio == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Município");
			paramsValido = false;
		} else if (!checkMunicipio) {
			idMunicipio = 0;
		}
		if (idTipoBolsaAuxilio == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo da Bolsa");
			paramsValido = false;
		}
		if (checkAnoPeriodo && (ano == null || periodo == null)) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano/Semestre");
			paramsValido = false;
		} else if (!checkAnoPeriodo) {
			ano = 0;
			periodo = 0;
		}
		if (checkNivel && nivel == '0') {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nível");
			paramsValido = false;
		} else if (!checkNivel) {
			nivel = '0';
		}
		if (checkCurso && idCurso == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
			paramsValido = false;
		} else if (!checkCurso) {
			idCurso = 0;
		}
		if (checkSexo && sexo == ' ') {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Sexo");
			paramsValido = false;
		} else if (!checkSexo) {
			sexo = ' ';
		}

		if (checkStatus && idStatusDiscente == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Status Discente");
			paramsValido = false;
		} else if (!checkStatus) {
			idStatusDiscente = 0;
		}
		
		if (paramsValido) {

			// primeiro verifica se não ultrapassa limite da busca.
			Long total = buscaBolsaAuxilioDao.findCountBolsas(
					idTipoBolsaAuxilio, deferimento, ano, periodo, idCurso,
					idMunicipio, sexo, idStatusDiscente);

			if (total > LIMITE_RESULTADOS_MAXIMO) {
				addMensagem(MensagensArquitetura.BUSCA_MAXIMO_RESULTADOS,
						LIMITE_RESULTADOS_MAXIMO);
			} else {

				listaBolsaAuxilio = new ArrayList<BolsaAuxilio>();
				listaBolsaAuxilio = buscaBolsaAuxilioDao.findBolsas(
						idTipoBolsaAuxilio, idResidencia, deferimento, ano,
						periodo, idCurso, idMunicipio, nivel, null, sexo, idStatusDiscente);

				if (!checkResidencia) {
					idResidencia = 0;
				}

				if (sexo == ' ') {
					sexo = PessoaGeral.SEXO_MASCULINO;
				}

				if (listaBolsaAuxilio.size() == 0) {
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				}
			}

		}

		return listaBolsaAuxilio;
	}

	/**
	 * Define o nível de ensino 
	 */
	public void definirNivelEnsino() throws DAOException {
		if ( NivelEnsino.isGraduacao(nivel))
			allCursos = getAllCursoGraduacaoCombo();
		else
			allCursos = getAllCursoStrictoCombo();
		
		definirStatusDiscente();
	}

	/**
	 * Define o Status do discente 
	 */
	public void definirStatusDiscente() throws DAOException {
		StatusDiscenteMBean mBean = getMBean("statusDiscente");
		if ( NivelEnsino.isGraduacao(nivel))
			allStatus = mBean.getAllGraduacaoCombo();
		else
			allStatus = mBean.getAllStrictoCombo();
	}
	
	/**
	 * Retorna Nível de ensino como SelectItem
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> Não invocado por JSP </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCursoStrictoCombo() throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		Collection<Curso> cursos = dao.findByNivel(NivelEnsino.STRICTO, true);
		removeCursoForaMunicipio(cursos);
		return toSelectItems(cursos, "id", "descricao");
	}

	/**
	 * Retorna Nível de ensino como SelectItem
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> Não invocado por JSP </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCursoGraduacaoCombo() throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		Collection<Curso> cursos = dao.findByNivel(NivelEnsino.GRADUACAO);
		removeCursoForaMunicipio(cursos);
		return toSelectItems(cursos, "id", "descricao");
	}
	
	/**
	 * Retorna Tipos de Bolsa como SelectItem
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> Não invocado por JSP </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllTiposBolsas() throws DAOException{
		return getAll(TipoBolsaAuxilio.class, "id", "denominacao");
	}
	
	/**
	 * Retorna Residências como SelectItem
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> Não invocado por JSP </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllResidencias() throws DAOException{
		return getAll(ResidenciaUniversitaria.class, "id", "localizacao");
	}

	/**
	 * Retorna Residências como SelectItem
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> Não invocado por JSP </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllMunicipiosCampusCombo() throws DAOException{
		return toSelectItems(getDAO(MunicipioDao.class).findAllMunicipiosCampus(),"id","nome");
	}
	
	/**
	 * Remove os cursos fora do município do campus selecionado no
	 * formulário de busca.
	 * @param cursos
	 * @throws DAOException
	 */
	private void removeCursoForaMunicipio(Collection<Curso> cursos) throws DAOException {

		if( !isEmpty(cursos) && idMunicipio != null && idMunicipio != 1 ){
			Collection<Curso> cursosMunicipio = new ArrayList<Curso>(); 
			for (Curso curso : cursos) {
				if( !isEmpty(curso.getMunicipio() ) 
						&& curso.getMunicipio().getId() == getIdMunicipio() )
					cursosMunicipio.add(curso);
			}	
			cursos.clear();
			cursos.addAll(cursosMunicipio);
		}
	}

	public List<BolsaAuxilio> getListaBolsaAuxilio() {
		return listaBolsaAuxilio;
	}

	public void setListaBolsaAuxilio(List<BolsaAuxilio> listaBolsaAuxilio) {
		this.listaBolsaAuxilio = listaBolsaAuxilio;
	}

	public Integer getDeferimento() {
		return deferimento;
	}

	public void setDeferimento(Integer deferimento) {
		this.deferimento = deferimento;
	}

	public Integer getIdResidencia() {
		return idResidencia;
	}

	public void setIdResidencia(Integer idResidencia) {
		this.idResidencia = idResidencia;
	}

	public Integer getIdMunicipio() {
		return idMunicipio;
	}

	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public Integer getIdTipoBolsaAuxilio() {
		return idTipoBolsaAuxilio;
	}

	public void setIdTipoBolsaAuxilio(Integer idTipoBolsaAuxilio) {
		this.idTipoBolsaAuxilio = idTipoBolsaAuxilio;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public Integer getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

	public char getSexo() {
		return sexo;
	}

	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	public Collection<SelectItem> getAllCursos() {
		return allCursos;
	}

	public void setAllCursos(Collection<SelectItem> allCursos) {
		this.allCursos = allCursos;
	}

	public boolean isCheckAnoPeriodo() {
		return checkAnoPeriodo;
	}

	public void setCheckAnoPeriodo(boolean checkAnoPeriodo) {
		this.checkAnoPeriodo = checkAnoPeriodo;
	}

	public boolean isCheckTipoBolsa() {
		return checkTipoBolsa;
	}

	public void setCheckTipoBolsa(boolean checkTipoBolsa) {
		this.checkTipoBolsa = checkTipoBolsa;
	}

	public boolean isCheckCurso() {
		return checkCurso;
	}

	public void setCheckCurso(boolean checkCurso) {
		this.checkCurso = checkCurso;
	}

	public boolean isCheckNivel() {
		return checkNivel;
	}

	public void setCheckNivel(boolean checkNivel) {
		this.checkNivel = checkNivel;
	}

	public boolean isCheckResidencia() {
		return checkResidencia;
	}

	public void setCheckResidencia(boolean checkResidencia) {
		this.checkResidencia = checkResidencia;
	}

	public boolean isCheckDeferimento() {
		return checkDeferimento;
	}

	public void setCheckDeferimento(boolean checkDeferimento) {
		this.checkDeferimento = checkDeferimento;
	}

	public boolean isCheckMunicipio() {
		return checkMunicipio;
	}

	public void setCheckMunicipio(boolean checkMunicipio) {
		this.checkMunicipio = checkMunicipio;
	}

	public boolean isCheckSexo() {
		return checkSexo;
	}

	public void setCheckSexo(boolean checkSexo) {
		this.checkSexo = checkSexo;
	}
	
	public boolean isCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(boolean checkStatus) {
		this.checkStatus = checkStatus;
	}

	public Integer getIdStatusDiscente() {
		return idStatusDiscente;
	}

	public void setIdStatusDiscente(Integer idStatusDiscente) {
		this.idStatusDiscente = idStatusDiscente;
	}

	public Collection<SelectItem> getAllStatus() {
		return allStatus;
	}

	public void setAllStatus(Collection<SelectItem> allStatus) {
		this.allStatus = allStatus;
	}
	
}