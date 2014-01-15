/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.InscricaoSelecaoDao;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioRespostasDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.EditalProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.StatusInscricaoSelecao;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.dominio.Resposta;

/**
 * Managed Bean responsável pela geração de relatórios relacionados aos inscritos 
 * nos processos seletivos
 *  
 * @author Mário Rizzi
 *
 */
@Component("relatorioInscricaoSelecao") @Scope("request")
public class RelatorioInscricaoSelecaoMBean extends SigaaAbstractController<InscricaoSelecao> {

	//Constantes de navegação
	private final String JSP_FORM_BUSCA = "/administracao/cadastro/ProcessoSeletivo/form_relatorio.jsp";
	
	//Constantes de tipo de relatório
	public static final int REL_INSCRITOS = 4;
	public static final int REL_INSCRITOS_AGENDADOS = 5;
	public static final int REL_QTD_DIA = 6;
	public static final int REL_QTD_CURSO = 7;
	
	//Constantes relacionadas ao jasper
	private static final String BASE_REPORT_PACKAGE = "/br/ufrn/sigaa/relatorios/fontes/";
	private static final String REPORT_INSCRITOS = "trf_transf_vol_inscritos";
	private static final String REPORT_AGENDADOS = "trf_transf_vol_agendados";
	private static final String REPORT_QTD_DIA = "trf_transf_vol_qtd";

	private String formatoJasper;
	private Integer tipoRelatorio;
	private Integer statusInscricao;
	
	private Collection<InscricaoSelecao> inscritos;
	
	private List<Map<String, Object>> qtdInscritos;

	public RelatorioInscricaoSelecaoMBean(){
		
		if(isEmpty(this.tipoRelatorio))
			this.tipoRelatorio = getParameterInt("tipoRelatorio", 0);
		
		obj = new InscricaoSelecao();
		obj.setProcessoSeletivo(new ProcessoSeletivo());
		obj.getProcessoSeletivo().setEditalProcessoSeletivo(new EditalProcessoSeletivo());
		
		formatoJasper = "pdf";	
		
	}
	
	public Collection<SelectItem> getFormatosCombo(){
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		//itens.add(new SelectItem("","Html"));
		itens.add(new SelectItem("xls","Planilha"));
		itens.add(new SelectItem("pdf","PDF"));
		return itens;
	}

	@Override
	public String getFormPage() {
		return JSP_FORM_BUSCA;
	}
	
	/**
	 * Inicia o formulário de busca para o relatório dos inscritos no processo seletivo
	 * de transferência voluntária 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * 
	 * @throws ArqException 
	 */
	public String iniciarRelatoriosInscritos() throws ArqException {
		tipoRelatorio = REL_INSCRITOS;
		return forward(getFormPage());
	}
	
	 /**
	 * Inicia o formulário de busca para o relatório dos inscritos no processo seletivo
	 * de transferência voluntária 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * 
	 * @throws ArqException 
	 */
	public String iniciarRelatoriosInscritosAgendamento() throws ArqException {
		tipoRelatorio = REL_INSCRITOS_AGENDADOS;
		return forward(getFormPage());
	}
	
	 /**
	 * Inicia o formulário de busca para o relatório quantitativo de inscritos por dia
	 * de transferência voluntária 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * 
	 * @throws ArqException 
	 */
	public String iniciarRelatorioQuantitativo() throws ArqException {
		tipoRelatorio = REL_QTD_DIA;
		return forward(getFormPage());
	}
	
	 /**
	 * Inicia o formulário de busca para o relatório quantitativo de inscritos por curso
	 * de transferência voluntária 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * 
	 * @throws ArqException 
	 */
	public String iniciarRelatorioQuantitativoCurso() throws ArqException {
		tipoRelatorio = REL_QTD_CURSO;
		return forward(getFormPage());
	}
	
	/**
	 * Gera o relatório de acordo com o tipo e o período do processo seletivo
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_relatorio.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String gerarRelatorioTransferenciaVoluntaria() throws ArqException{
		
		inscritos = null;
		qtdInscritos = null;
		
		//Validações envolvendo os campos obrigatórios do formulário de busca.
		if(isEmpty(obj.getProcessoSeletivo().getEditalProcessoSeletivo()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Edital");
		else{
			
		
			//Se o resultado for zero, retorna para página de busca do relatório.
			Collection<?> dados = getDados();
			if(isEmpty(dados)){
				addMensagemErro(" Nenhum resultado encontrado para a busca! ");
				return null;
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			EditalProcessoSeletivo edital = getGenericDAO().findByPrimaryKey(
						obj.getProcessoSeletivo().getEditalProcessoSeletivo().getId(),
						EditalProcessoSeletivo.class);
			
			
			map.put("edital",edital.getNome());
			map.put("tipoRelatorio",getTipoRelatorio());
			if(getTipoRelatorio().equals(REL_INSCRITOS))
				map.put("pathQuestionarios",JasperReportsUtil.getReport(BASE_REPORT_PACKAGE,"trf_sub_questionario.jasper"));
			
						
			//Exporta a consulta no formato xls ou pdf, se o campo for setado no formulário de busca.
			if(!isEmpty(formatoJasper))
				gerarRelatorios(getCurrentResponse(), getCurrentRequest(),getReport(),
						formatoJasper, getDados(), map);
			
		}
		return forward(getFormPage());
		
	}
	
	/**
	 * Popula a coleção/lista de inscritos ou datas de inscrição
	 * dependendo do tipo do relatório setado. 
	 * @return
	 * @throws ArqException
	 */
	private Collection<?> getDados() throws ArqException{
		
		if(isRelInscritosAgendados() || isRelInscritos()){
			return getInscritosTransferenciaVoluntaria();
		}else if(isRelQtdInscritos()){
			getQtdInscriosDia();
			return qtdInscritos;
		}else{
			getQtdInscriosCurso();
			return qtdInscritos;
		}
	}
	
	private String getReport(){
		
		switch(tipoRelatorio){
			case REL_INSCRITOS_AGENDADOS: return REPORT_AGENDADOS;
			case REL_QTD_DIA: return REPORT_QTD_DIA;
			case REL_QTD_CURSO: return REPORT_QTD_DIA;
			default: return REPORT_INSCRITOS;
		}
		
	}
	
	/**
	 * Gerar um relatório conforme os parâmetros
	 * Página - nenhum arquivo jsp, chamado somente por outros métodos 
	 * @param response
	 * @param request
	 * @param nomeArquivo
	 * @param formato
	 * @param lista
	 * @throws ArqException
	 */
	private void gerarRelatorios(HttpServletResponse response, HttpServletRequest request, String nomeArquivo,
			String formato, Collection<?> lista, Map<String, Object> map) throws ArqException {
		
		try {
				
			if(map == null)
				map = new HashMap<String, Object>();
			
			InputStream report = JasperReportsUtil.getReport(BASE_REPORT_PACKAGE,nomeArquivo+".jasper");
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint prt = JasperFillManager.fillReport(report, map, jrds);
			
			JasperReportsUtil.exportar(prt, nomeArquivo + "." + formato, request, response, formato);
	
			FacesContext.getCurrentInstance().responseComplete();
			
		} catch (JRException e) {
			throw new ArqException(e);
		} catch (IOException e) {
			throw new ArqException(e);
		}
		
	}
	
	public void setQtdInscritosDia(List<Map<String, Object>> qtdInscritosDia) {
		this.qtdInscritos = qtdInscritosDia;
	}

	
	public Collection<SelectItem> getAllStatusCombo() {
		return toSelectItems(StatusInscricaoSelecao.todosStatus);
	}
	
	/**
	 * Retorna todos inscritos de um processo seletivo de transferência voluntária 
	 * @return
	 * @throws ArqException
	 */
	private Collection<Map<String, Object>> getInscritosTransferenciaVoluntaria()throws ArqException {
		
		Collection<Map<String, Object>> listaMapa = new ArrayList<Map<String,Object>>();
		Collection<Map<String, Object>> listaMapaQuestionario = new ArrayList<Map<String,Object>>();
		Map<String, Object> mapaQuestionario = new HashMap<String, Object>();
		
		inscritos = getDAO(InscricaoSelecaoDao.class).findInscritosByPeriodoAgendamento(
				obj.getProcessoSeletivo().getEditalProcessoSeletivo(),
				null, getStatusInscricao(),(obj.getProcessoSeletivo().isPossuiAgendamento() && tipoRelatorio.equals(REL_INSCRITOS_AGENDADOS))?true:false);
		
		
		QuestionarioRespostasDao questionarioRespDao = getDAO(QuestionarioRespostasDao.class);
		
		if ( !ValidatorUtil.isEmpty( inscritos ) ) {
			
			int numero = 0;
			for (InscricaoSelecao i : inscritos) {
				
				QuestionarioRespostas qr = questionarioRespDao.findByInscricaoSelecao(i);
				
				Map<String, Object> mapaInscricao = new HashMap<String, Object>();
				mapaInscricao.put("numero", numero);
				mapaInscricao.put("inscricao", i);
				
				
				if( qr != null ){
					for (Resposta r : qr.getRespostas()){
						mapaQuestionario = new HashMap<String, Object>();
						mapaQuestionario.put("num", numero);
						mapaQuestionario.put("pergunta", r.getPergunta().getPergunta());
						mapaQuestionario.put("resposta", r.getRespostaDissertativa());
						listaMapaQuestionario.add(mapaQuestionario);
					}
					mapaInscricao.put("questionarios", listaMapaQuestionario);
				}
				
				listaMapa.add(mapaInscricao);
				listaMapaQuestionario = new ArrayList<Map<String,Object>>();
				questionarioRespDao.close();
				questionarioRespDao = getDAO(QuestionarioRespostasDao.class);
				numero++;
			}
			
		}
		
		return  listaMapa;

	}
	
	/**
	 * Retorna os números de inscritos de um edital por data de cadastro. 
	 * @return
	 * @throws ArqException 
	 */
	private List<Map<String, Object>> getQtdInscriosDia() throws ArqException {
		
		qtdInscritos =  getDAO(InscricaoSelecaoDao.class).findQtdInscritosDiaByEdital(
				obj.getProcessoSeletivo().getEditalProcessoSeletivo());
		
		if( !isEmpty(qtdInscritos) ){
			int i = 0;
			for (Map<String, Object> item : qtdInscritos) {
				item.put("numero", i);
				i++;
			}
		}
		
		return qtdInscritos;

	}
	
	/**
	 * Retorna os números de inscritos de um edital por curso 
	 * @return
	 * @throws ArqException 
	 */
	private List<Map<String, Object>> getQtdInscriosCurso() throws ArqException {
		
		qtdInscritos =  getDAO(InscricaoSelecaoDao.class).findQtdInscritosCursoByEdital(
				obj.getProcessoSeletivo().getEditalProcessoSeletivo());
		
		if( !isEmpty(qtdInscritos) ){
			int i = 0;
			for (Map<String, Object> item : qtdInscritos) {
				item.put("numero", i);
				i++;
			}
		}
		
		return qtdInscritos;

	}

	public Integer getTipoRelatorio() {
		return tipoRelatorio;
	}

	/**
	 * Método indica o tipo relatório a ser gerado
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * 		/SIGAA/app/sigaa.ear/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_relatorio.jsp
	 * @param tipoRelatorio
	 */
	public void setTipoRelatorio(Integer tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	
	/**
	 * Método indica o tipo relatório a ser gerado
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/relatorios/processoSeletivo/relatorio_inscritos.jsp
	 * 
	 * @param tipoRelatorio
	 */
	public Collection<InscricaoSelecao> getInscritos() {
		return inscritos;
	}

	
	public void setInscritos(Collection<InscricaoSelecao> inscritos) {
		this.inscritos = inscritos;
	}

	
	public String getFormatoJasper() {
		return formatoJasper;
	}

	
	public void setFormatoJasper(String formatoJasper) {
		this.formatoJasper = formatoJasper;
	}

		
	public Integer getStatusInscricao() {
		return statusInscricao;
	}

	
	public void setStatusInscricao(Integer statusInscricao) {
		this.statusInscricao = statusInscricao;
	}

	
	public boolean isRelInscritosDeferidos(){
		return this.tipoRelatorio.equals(REL_INSCRITOS) 
			&& this.statusInscricao.equals(StatusInscricaoSelecao.DEFERIDA);
	}
	
	
	public boolean isRelInscritos(){
		return this.tipoRelatorio.equals(REL_INSCRITOS);
	}
	
	public boolean isRelInscritosAgendados(){
		return this.tipoRelatorio.equals(REL_INSCRITOS_AGENDADOS);
	}
	
	public boolean isRelQtdInscritos(){
		return this.tipoRelatorio.equals(REL_QTD_DIA);
	}

}
