/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 02/04/2008
*
*/
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.UFRNUtils.prepararNomeFormal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * MBean responsável pela geração de etiquetas de identificação
 * de discentes para utilização em arquivos.
 *
 * @author Ricardo Wendell
 *
 */
@Component("etiquetasDiscentesBean") @Scope("request")
public class EtiquetasDiscentesMBean extends SigaaAbstractController {

	// Constantes para a definição do documento com as etiquetas
	/** Margem vertical do documento. */
	private static final float VERTICAL_MARGIN = 24.6f;
	/** Margem horizontal do documento. */
	private static final float HORIZONTAL_MARGIN = 27.2f;

	/** Curso selecionado para geração das etiquetas. */
	private Curso curso;
	/** Ano informado para geração das etiquetas. */
	private Integer ano;
	/** Período informado para geração das etiquetas. */
	private Integer periodo;
	/** Forma de ingresso selecionada para geração das etiquetas. */
	private FormaIngresso formaIngresso;
	/** Matrículas informadas para geração das etiquetas. */
	private String matriculas;

	/** Coleção de matrículas informadas pelo usuário de forma textual. */
	private Collection<Long> numerosMatricula;

	/** Indica se o filtro de curso foi selecionado para busca. */
	private boolean filtroCurso;
	/** Indica se o filtro de ano-período foi selecionado para busca. */
	private boolean filtroAnoPeriodo;
	/** Indica se o filtro de forma de ingresso foi selecionado para busca. */
	private boolean filtroFormaIngresso;
	/** Indica se o filtro de matrículas foi selecionado para busca. */
	private boolean filtroMatriculas;

	public EtiquetasDiscentesMBean() {
		CalendarioAcademico calendario = getCalendarioVigente();
		ano = calendario.getAno();
		periodo = calendario.getPeriodo();
		curso = new Curso();
		formaIngresso = new FormaIngresso();
	}

	/**
	 * Preparar formulário para a definição dos critérios
	 * de busca dos discentes
	 *
	 * Método chamado pela JSP:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		checkRole(SigaaPapeis.DAE);
		return forward("/ensino/etiquetas/form.jsf");
	}
	
	/**
	 * Confere a existência de alunos sem tipo de ingresso cadastrado.
	 * 
	 * Método chamado pela JSP:
	 * <ul>
	 * 		<li>/sigaa.war/ensino/etiquetas/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {
		// Validar campos selecionados
		ListaMensagens erros = validarFiltros();
		if ( !erros.isEmpty() ) {
			addMensagens(erros);
			return null;
		}
		
		
		// Buscar discentes de acordo com os critérios
		List<Map<String, Object>> discentes = getDAO(DiscenteDao.class).findParaEtiquetas(
				getNivelEnsino(),
				filtroCurso ? curso : null,
				filtroAnoPeriodo ? ano : null,
				filtroAnoPeriodo ? periodo : null,
				filtroFormaIngresso ? formaIngresso : null,
				filtroMatriculas ? numerosMatricula : null);

			if (filtroMatriculas) {
				List<Map<String, Object>> ds = new ArrayList<Map<String,Object>>();
				for(Long nm :numerosMatricula) {
					for(Map<String, Object> d:discentes) {
						if (d.get("matricula").equals(nm)) {
							ds.add(d);
							break;
						}
					}
				}
				discentes = ds;
			}
		
		
		for (Map<String, Object> m : discentes) {
			if(m.get("formaingresso") == null){
				return forward("/ensino/etiquetas/form_confirmar.jsf");
			}
		}
		
		return gerar(discentes);
	}

	/**
	 * Buscar os discentes de acordo com os critérios selecionados e
	 * gerar as etiquetas. Utilizado após método confirmar()
	 * 
	 * Método não invocado por JSPs.
	 *
	 * @return
	 * @throws ArqException
	 */
	public String gerar(List<Map<String, Object>> discentes) throws ArqException {

		// Gerar etiquetas para impressão
		if (discentes != null && !discentes.isEmpty() ) {
			gerarEtiquetas(discentes);
		} else {
			addMensagemWarning("Nenhum discente foi encontrado de acordo com os critérios informados");
		}

		return null;
	}
	
	/**
	 * Buscar os discentes de acordo com os critérios selecionados e
	 * gerar as etiquetas. Utilizado após método confirmar()
	 *
	 * Método chamado pela JSP:
	 * <ul>
	 * 		<li>/sigaa.war/ensino/etiquetas/form_confirmar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String gerar() throws ArqException {

		// Buscar discentes de acordo com os critérios
		List<Map<String, Object>> discentes = getDAO(DiscenteDao.class).findParaEtiquetas(
				getNivelEnsino(),
				filtroCurso ? curso : null,
				filtroAnoPeriodo ? ano : null,
				filtroAnoPeriodo ? periodo : null,
				filtroFormaIngresso ? formaIngresso : null,
				filtroMatriculas ? numerosMatricula : null);

		// Gerar etiquetas para impressão
		if (discentes != null && !discentes.isEmpty() ) {
			gerarEtiquetas(discentes);
		} else {
			addMensagemWarning("Nenhum discente foi encontrado de acordo com os critérios informados");
		}

		return null;
	}

	/**
	 * Validação dos campos utilizados para a consulta dos discentes
	 */
	private ListaMensagens validarFiltros() {
		ListaMensagens erros = new ListaMensagens();
		if (filtroAnoPeriodo) {
			ValidatorUtil.validaInt(ano, "Ano", erros);
			ValidatorUtil.validaInt(periodo, "Período", erros);
		}
		if (filtroFormaIngresso) {
			ValidatorUtil.validateRequired(formaIngresso, "Forma de Ingresso", erros);
		}
		if (filtroCurso) {
			ValidatorUtil.validateRequired(curso, "Curso", erros);
		}
		if (filtroMatriculas) {
			ValidatorUtil.validateRequired(matriculas, "Matrículas", erros);
			String[] arrayMatriculas = matriculas.split("[\\D]+");


			numerosMatricula = new ArrayList<Long>();
			if (arrayMatriculas.length != 0) {
				try {
					for (String mat : arrayMatriculas) {
						numerosMatricula.add(Long.valueOf(mat));
					}
				} catch (NumberFormatException e) {
				}
			}
			if (numerosMatricula.isEmpty()) {
				erros.addErro("É necessário informar alguma matrícula válida");
			}
		}

		if (!filtroAnoPeriodo && !filtroFormaIngresso && !filtroCurso && !filtroMatriculas) {
			erros.addErro("É necessário selecionar pelo menos um dos filtros disponíveis");
		}

		return erros;
	}

	/**
	 * Gerar o PDF com as etiquetas dos discentes
	 *
	 * @param discentes
	 * @throws ArqException
	 */
	private void gerarEtiquetas(List<Map<String, Object>> discentes) throws ArqException {
		HttpServletResponse res = getCurrentResponse();

		// Preparação do documento
		Document document = new Document(PageSize.A4, HORIZONTAL_MARGIN, HORIZONTAL_MARGIN, VERTICAL_MARGIN, 0);
		document.addAuthor("SIGAA");

		try {
			PdfWriter.getInstance(document, res.getOutputStream());
			document.open();
			document.newPage();

			// Grade dos discentes
			PdfPTable table = new PdfPTable(4);
			table.getDefaultCell().setBorder(0);
			table.getDefaultCell().setPaddingLeft(1f);

			// Preencher grade de etiquetas
			for (Map<String, Object> m : discentes) {
				PdfPTable discenteTable = new PdfPTable(1);

				// Curso e Cidade
				PdfPCell cellTop = new PdfPCell();
				cellTop.setBorder(0);
				cellTop.setFixedHeight(39.5f);
				cellTop.addElement(new Paragraph((String) m.get("curso"), FontFactory.getFont("Verdana", 6)));
				cellTop.addElement(new Paragraph((String) m.get("municipio"), FontFactory.getFont("Verdana", 6)));

				// Nome do discente
				PdfPCell cellMiddle = new PdfPCell();
				cellMiddle.setBorder(0);
				cellMiddle.setFixedHeight(14.5f);

				String nome = (String) m.get("nome");
				
				String nomeFormal = null;
				boolean abreviar = false;
				if(nome.length() > 30)
					abreviar = true;
					
				nomeFormal = prepararNomeFormal(nome, abreviar);
				
				Paragraph pNome = new Paragraph(nomeFormal , FontFactory.getFont("Helvetica", 6, Font.BOLD));
				cellMiddle.addElement(pNome);

				// Matrícula e Forma de Ingresso
				PdfPCell cellBottom = new PdfPCell();
				cellBottom.setBorder(0);
				cellBottom.setFixedHeight(41f);

				cellBottom.addElement(new Paragraph(((Long) m.get("matricula")).toString(), FontFactory.getFont("Verdana", 8)));
				if(m.get("formaingresso") == null){
					cellBottom.addElement(new Paragraph("SEM FORMA DE INGRESSO CADASTRADA", FontFactory.getFont("Verdana", 7)));
				}
				else{
					cellBottom.addElement(new Paragraph((String) m.get("formaingresso"), FontFactory.getFont("Verdana", 7)));
				}

				discenteTable.addCell(cellTop);
				discenteTable.addCell(cellMiddle);
				discenteTable.addCell(cellBottom);

				table.addCell(discenteTable);
			}

			// Preencher células vazias para completar uma linha
			int total = 4 - (discentes.size() % 4);
			for (int i = 0; i < total; i++) {
				PdfPCell cell = new PdfPCell();
				cell.setBorder(0);
				table.addCell(cell);
			}

			table.setWidthPercentage(100.0f);
			document.add(table);

			document.close();

			res.setContentType("application/pdf");
			res.addHeader("Content-Disposition", "attachment; filename=etiquetas.pdf");
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			throw new ArqException(e);
		} finally {
		}
	}

	/**
	 * Popula a coleção de cursos que o usuário tem acesso
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getCursosCombo() throws DAOException {
		CursoDao dao = getDAO(CursoDao.class);
		Collection<Curso> cursos = dao.findByNivel( NivelEnsino.GRADUACAO );
		return toSelectItems(cursos, "id", "descricao");
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

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean isFiltroAnoPeriodo() {
		return filtroAnoPeriodo;
	}

	public void setFiltroAnoPeriodo(boolean filtroAnoPeriodo) {
		this.filtroAnoPeriodo = filtroAnoPeriodo;
	}

	public boolean isFiltroFormaIngresso() {
		return filtroFormaIngresso;
	}

	public void setFiltroFormaIngresso(boolean filtroFormaIngresso) {
		this.filtroFormaIngresso = filtroFormaIngresso;
	}

	public boolean isFiltroMatriculas() {
		return filtroMatriculas;
	}

	public void setFiltroMatriculas(boolean filtroMatriculas) {
		this.filtroMatriculas = filtroMatriculas;
	}

	public FormaIngresso getFormaIngresso() {
		return formaIngresso;
	}

	public void setFormaIngresso(FormaIngresso formaIngresso) {
		this.formaIngresso = formaIngresso;
	}

	public String getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(String matriculas) {
		this.matriculas = matriculas;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

}
