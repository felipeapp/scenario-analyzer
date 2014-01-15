/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 18/06/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dao.TurmaEADDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Esse Managed Bean � respons�vel pela gera��o de um arquivo pdf a lista de presen�a
 * dos alunos matriculados nas turmas.
 * 
 * @author Rafael Gomes
 *
 */
@Component() @Scope("request")
public class ListaPresencaLoteMBean extends SigaaAbstractController<Object> {

	/** Atributo do Objeto Componente a ser utilizado no filtro disciplina do relat�rio. */
	protected ComponenteCurricular disciplina = new ComponenteCurricular();
	/** Cidade Polo de ensino a dist�ncia. */
	protected Polo polo = new Polo(); 
	/** Atributo utilizado para armazenar o valor do filtro ano do relat�rio. */
	protected Integer ano;
	/** Atributo utilizado para armazenar o valor do filtro per�odo do relat�rio. */
	protected Integer periodo;
	
	/** Indica se a lista gerada ir� considerar o p�lo.*/
	private boolean filtroPolo;
	/** Indica se a lista gerada ir� considerar c�digo da disciplina. */
	private boolean filtroCodigo;
	/** Indica se a lista gerada ir� considerar nome da disciplina*/
	private boolean filtroDisciplina;
	
	
	/** Construtor Default */
	public ListaPresencaLoteMBean() {}

	@Override
	public String getDirBase() {
		return "/ead/relatorios";
	}
	
	/** 
	 * M�todo inicial para a emiss�o das listas de frequ�ncia em lote. 
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 * */
	public String iniciar(){
		setAno(CalendarUtils.getAnoAtual());
		setPeriodo(getPeriodoAtual());
		return forward(getDirBase() + "/relatorio_lista_presenca_lote.jsf");
	}
	
	/**
	 * Cria a lista de presen�a das turmas em lote para a disciplina ou p�lo selecionados para a emiss�o das listas.
	 *  * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/ead/relatorios/relatorio_lista_presenca_lote.jsf</li>
	 * </ul>
	 * 
	 * @param turma
	 * @throws Exception
	 */
	public String criarListaPresencaEmLote() {
		
		validarFiltros();
		if (hasErrors()) return null;
		
		TurmaEADDao dao = getDAO(TurmaEADDao.class);
		
		try {
			Collection<Turma> turmas = dao.findAbertasByComponenteCurricular(
								disciplina.getNome(),
								disciplina.getCodigo(),
								null,
								polo,
								ano, 
								periodo, 
								true, 
								NivelEnsino.GRADUACAO, 
								Turma.REGULAR);

			if (turmas == null || turmas.isEmpty()) {
				addMensagemErro("N�o existe nenhuma turma para os filtros informados. ");
				return null;
			}
			
			List<Integer> idTurmas = new ArrayList<Integer>();
			for (Turma t : turmas) {
				idTurmas.add(t.getId());
			}
					
			Collection<MatriculaComponente> matriculas = null;
			matriculas = dao.findMatriculasByTurmas(idTurmas, 
								SituacaoMatricula.MATRICULADO, 
								SituacaoMatricula.APROVADO,
								SituacaoMatricula.REPROVADO, 
								SituacaoMatricula.REPROVADO_FALTA, 
								SituacaoMatricula.REPROVADO_MEDIA_FALTA);
			
			Map<Integer, List<MatriculaComponente>> mapa = new HashMap<Integer, List<MatriculaComponente>>();
			
			List<MatriculaComponente> listMatricula = new ArrayList<MatriculaComponente>();
			for (MatriculaComponente mc : matriculas) {
				if (mapa.get(mc.getTurma().getId()) == null){
					listMatricula = new ArrayList<MatriculaComponente>();
					listMatricula.add(mc);
					mapa.put(mc.getTurma().getId(), listMatricula);
				} else {
					listMatricula = mapa.get(mc.getTurma().getId());
					listMatricula.add(mc);
					mapa.put(mc.getTurma().getId(), listMatricula);
				}
			}
			
			for (Iterator<Turma> iterator = turmas.iterator(); iterator.hasNext();) {
				Turma t = iterator.next();
				if (mapa.get(t.getId()) == null)
					iterator.remove();
				else {
					t.setMatriculasDisciplina(mapa.get(t.getId()));
					Collections.sort((List<MatriculaComponente>) t.getMatriculasDisciplina());
				}	
			}
			

			if (isEmpty(matriculas)) {
				addMensagemErro("N�o h� alunos matriculados.");
				return null;
			}
		
			JRBeanCollectionDataSource rds = new JRBeanCollectionDataSource(turmas);
	
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("pathSubRelListaPresencaDiscentes", getReportSIGAA("ListaPresencaLoteDiscentes.jasper"));
	
			if (polo != null && polo.getId() > 0) {
				polo = turmas.iterator().next().getPolo();
			}
			if ( disciplina != null && (disciplina.getCodigo() != null || disciplina.getNome() != null) ) {
				disciplina = turmas.iterator().next().getDisciplina();
			}
			JasperPrint prt = JasperFillManager.fillReport(getReportSIGAA("ListaPresencaLote.jasper"), parametros, rds);
			String nomeArquivo = "listapresenca_" + (polo != null && polo.getId() > 0 ? polo.getDescricao() + "_" : "") 
								+ (disciplina.getId() > 0 ? disciplina.getCodigo() + "_" : "") 
								+ ano+periodo + ".pdf";
			JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), "pdf");
	
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		return null;
	}
	
	/**
	 * Retorna uma lista de �tens de sele��o de p�los
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPolos() throws DAOException {
		PoloDao dao = getDAO(PoloDao.class);
		List<SelectItem> polos = toSelectItems(dao.findAllPolos(), "id", "cidade.nomeUF");
	
		return polos;
	}
	
	/**
	 * M�todo respons�vel pela valida��o dos filtros e par�metros utilizados para gerar a lista de presen�a em lote.
	 */
	private void validarFiltros(){
		ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequired(periodo, "Per�odo", erros);
		ValidatorUtil.validateMinValue(ano, 1900, "Ano", erros);
		ValidatorUtil.validateRange(periodo, 1, 4, "Per�odo", erros);
		if (filtroPolo)
			ValidatorUtil.validateRequiredId(polo.getId(), "P�lo", erros);
		else 
			polo = null;
		if (filtroCodigo)
			ValidatorUtil.validateRequired(disciplina.getCodigo(), "C�digo do componente", erros);
		else 
			disciplina.setCodigo(null);
		if (filtroDisciplina)
			ValidatorUtil.validateRequired(disciplina.getNome(), "Nome do componente", erros);
		else
			disciplina.setNome(null);
	}

	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
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

	public boolean isFiltroPolo() {
		return filtroPolo;
	}

	public void setFiltroPolo(boolean filtroPolo) {
		this.filtroPolo = filtroPolo;
	}

	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	public boolean isFiltroDisciplina() {
		return filtroDisciplina;
	}

	public void setFiltroDisciplina(boolean filtroDisciplina) {
		this.filtroDisciplina = filtroDisciplina;
	}
}
