/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/01/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.graduacao.dao.ImportacaoDiscenteOutrosConcursosDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.vestibular.dominio.ImportacaoDiscenteOutrosConcursos;
import br.ufrn.sigaa.vestibular.dominio.LinhaAuxiliar;
import br.ufrn.sigaa.vestibular.dominio.LinhaImpressaoDocumentosConvocados;

/**
 * MBean responsável pela emissão da documentação dos discentes importados.
 * @author Édipo Elder F. de Melo
 *
 */
@Component 
@Scope("session")
public class DocumentosDiscentesImportadosMBean extends SigaaAbstractController<DiscenteGraduacao> {
	
	/** O controller deverá gerar o PDF com as etiquetas para impressão. */
	public static final int ETIQUETAS = 2;
	/** O controller deverá gerar o PDF com os documentos necessários ao cadastro dos discentes para impressão. */
	public static final int DOCUMENTOS_PARA_CADASTRO_DE_DISCENTES = 3;
	/** O controller deverá gerar o PDF com a lista de convocação dos aprovados para impressão. */
	public static final int LISTA_DE_CONVOCACAO_DE_APROVADOS = 4; 
	
	/** ID do processo de importação. */
	private int idImportacao;
	
	/** Relatório escolhido para geração da documentação */
	private Integer idRelatorio = ETIQUETAS;
	
	/** Lista de ID de matrizes selecionadas para impressão dos documentos. */
	private List<String> idsMatrizSelecionadas = new ArrayList<String>();
	/** Lista de SelectItem de matrizes curriculares que tiveram discentes importados para o processo de importação selecionado. */ 
	private Collection<SelectItem> matrizesCombo;
	/** Lista de SelectItem de possíveis relatórios a serem gerados. */
	private Collection<SelectItem> relatoriosCombo;
	/** Lista de SelectItem de processos de importação. */
	private Collection<SelectItem> allImportacaoCombo;
	
	/** Construtor padrão. */
	public DocumentosDiscentesImportadosMBean() {
		relatoriosCombo = new ArrayList<SelectItem>();
		relatoriosCombo.add(new SelectItem(new Integer(ETIQUETAS), "Etiquetas"));
		relatoriosCombo.add(new SelectItem(new Integer(DOCUMENTOS_PARA_CADASTRO_DE_DISCENTES), "Documentos para Cadastro de Discentes"));
		relatoriosCombo.add(new SelectItem(new Integer(LISTA_DE_CONVOCACAO_DE_APROVADOS), "Lista de Convocação de Aprovados"));
	}
	
	/**
	 * Inicia a impressão de documentos utilizados para o cadastramento de alunos
	 * aprovados.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/cadastros.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 * */
	public String iniciarImpressaoDocumentos() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		return forward("/graduacao/discente/importacao/documentos.jsp");
	}	
	
	/**
	 * Tem função de carregar os cursos e as chamadas do processo seletivo selecionado.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_curso_fase.jsp</li>
	 * </ul>
	 * 
	 * @param vce
	 * @throws DAOException
	 */
	public void carregarMatrizes(ValueChangeEvent evt) throws DAOException {
		idImportacao = (Integer) evt.getNewValue();
		idsMatrizSelecionadas = new ArrayList<String>();
		matrizesCombo = new ArrayList<SelectItem>();
		if (idImportacao != 0) {
			ImportacaoDiscenteOutrosConcursosDao dao = getDAO(ImportacaoDiscenteOutrosConcursosDao.class);
			Collection<MatrizCurricular> matrizes = dao.findAllMatrizesByImportacao(idImportacao);
			matrizesCombo = toSelectItems(matrizes, "id", "descricao");
		}
	}
	
	/**
	 * Metódo utilizado na geração dos relatórios e/ou documentos dos candidatos aprovados.
	 *
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_curso_fase.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws JRException
	 * @throws IOException
	 * @throws SQLException
	 * @throws DAOException 
	 */
	public String gerarRelatorio() throws JRException, IOException, SQLException, DAOException{
		validateRequiredId(idRelatorio, "Documento", erros);
		validateRequiredId(idImportacao, "Processo de Importação", erros);
		validateRequired(idsMatrizSelecionadas, "Matriz(es) Curricular(es)", erros);
		if (hasOnlyErrors()) 
			return null;
		
		ImportacaoDiscenteOutrosConcursosDao dao = getDAO(ImportacaoDiscenteOutrosConcursosDao.class);
		
		List<Integer> idsMatrizCurricular = new LinkedList<Integer>();
		Collection<LinhaAuxiliar> linhas = new LinkedList<LinhaAuxiliar>();
		
		for (Object idMatrizSelecionada : idsMatrizSelecionadas) {
			int idSelecionado =  Integer.parseInt(idMatrizSelecionada.toString()) ;
			idsMatrizCurricular.add(idSelecionado);
		} 
		
		if ( idsMatrizCurricular.contains(0) ){
			idsMatrizCurricular = null;
		}
		
		Collection<LinhaImpressaoDocumentosConvocados> discentesConvocados = dao.findAllImportados(idImportacao, idsMatrizCurricular);
		for (LinhaImpressaoDocumentosConvocados linhaImpressao : discentesConvocados) {
			LinhaAuxiliar linhaAux = new LinhaAuxiliar();
			linhaAux.getLinha().add(linhaImpressao);
			linhas.add(linhaAux);
		}
		
		
		Map<String, Object> hs = new HashMap<String, Object>();
		hs.put("SUBREPORT_DIR", JasperReportsUtil.PATH_RELATORIOS_SIGAA);
		JRDataSource jrds = null;
		JasperPrint prt = null;
		String nomeArquivo = "relatorio.pdf";
		ImportacaoDiscenteOutrosConcursos importacao = dao.findAndFetch(idImportacao, ImportacaoDiscenteOutrosConcursos.class, "id", "descricao");
		
		if (idRelatorio == DOCUMENTOS_PARA_CADASTRO_DE_DISCENTES) {
			
			hs.put("declaracao", getReportSIGAA("declaracao_2011.jasper"));
			hs.put("comprovante", getReportSIGAA("comprovante_2011.jasper"));
			hs.put("lista_aprovados", getReportSIGAA("lista_dos_aprovados_por_curso.jasper"));
			hs.put("lista_assinaturas_aprovados", getReportSIGAA("lista_de_assinatura_dos_aprovados_por_curso.jasper"));
			hs.put("discentesConvocados", discentesConvocados);
//			hs.put("processoSeletivo", importacao.getDescricao());
//			hs.put("ano", importacao.getAnoIngresso());
			
			jrds = new JRBeanCollectionDataSource(linhas);
			prt = JasperFillManager.fillReport(getReportSIGAA("relatorios_convocacao_2011.jasper"), hs, jrds);

		} else if (idRelatorio == ETIQUETAS) {
			jrds = new JRBeanCollectionDataSource(discentesConvocados);
			prt = JasperFillManager.fillReport(getReportSIGAA("etiquetas.jasper"), hs, jrds);
			nomeArquivo = "Etiquetas_Discentes_Importados.pdf";
		
		} else if (idRelatorio == LISTA_DE_CONVOCACAO_DE_APROVADOS) {
			hs.put("listaIdMatriz", UFRNUtils.gerarStringIn(idsMatrizCurricular));
//			hs.put("convocacao", importacao.getDescricao());
//			hs.put("ano", importacao.getAnoIngresso());
			
			jrds = new JRBeanCollectionDataSource(discentesConvocados);
			prt = JasperFillManager.fillReport(getReportSIGAA("lista_convocacao.jasper"), hs, jrds);
			nomeArquivo = "Lista_Convocacao_Discentes_Importados.pdf";
		} 
		
		if (prt == null || prt.getPages().size() == 0) {
			addMensagemWarning("Não há alunos convocados.");
			return null;
		}
		
		JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(),getCurrentResponse(), "pdf");
		FacesContext.getCurrentInstance().responseComplete();
		
		return null;
	}

	/** Retorna a lista de SelectItem de processos de importação.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllImportacaoCombo() throws DAOException {
		if (allImportacaoCombo == null)
			allImportacaoCombo = toSelectItems(getGenericDAO().findAll(ImportacaoDiscenteOutrosConcursos.class), "id", "descricao");
		return allImportacaoCombo;
	}
	
	/**
	 * Adiciona os relatórios que podem ser impresso.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getRelatoriosCombo() throws ArqException {
		return relatoriosCombo;
	}

	public int getIdImportacao() {
		return idImportacao;
	}

	public void setIdImportacao(int idImportacao) {
		this.idImportacao = idImportacao;
	}

	public Integer getIdRelatorio() {
		return idRelatorio;
	}

	public void setIdRelatorio(Integer idRelatorio) {
		this.idRelatorio = idRelatorio;
	}

	public List<String> getIdsMatrizSelecionadas() {
		return idsMatrizSelecionadas;
	}

	public void setIdsMatrizSelecionadas(List<String> idsMatrizSelecionadas) {
		this.idsMatrizSelecionadas = idsMatrizSelecionadas;
	}

	public Collection<SelectItem> getMatrizesCombo() {
		return matrizesCombo;
	}

	public void setMatrizesCombo(Collection<SelectItem> matrizesCombo) {
		this.matrizesCombo = matrizesCombo;
	}
	
}
