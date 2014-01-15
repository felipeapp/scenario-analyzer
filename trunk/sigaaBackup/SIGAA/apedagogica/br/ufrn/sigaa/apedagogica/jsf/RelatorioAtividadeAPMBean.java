package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.apedagogica.dao.AtividadeAtualizacaoPedagogicaDAO;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.nee.dominio.RecursoNee;
import br.ufrn.sigaa.nee.dominio.TipoRecursoNee;

/**
 * Controller para geração do relatório de atividade
 * @author Diego Jácome
 *
 */
@Component("relatorioAtividadeAP") @Scope("request")
public class RelatorioAtividadeAPMBean extends SigaaAbstractController<AtividadeAtualizacaoPedagogica> {

	/** Link para a página de listagem das atividades */
	private static final String LIST_PAGE = "/apedagogica/RelatorioAtividadeAP/selecionar_atividade.jsp";
	/** Link para a página dos detalhes da atividade */
	private static final String VIEW_PAGE = "/apedagogica/RelatorioAtividadeAP/view_atividade.jsp";
	/** Link para a página do formulário do relatório quantitativo de atividades */
	private static final String FORM_PAGE = "/apedagogica/RelatorioAtividadeAP/form_quant_atividades_pap.jsp";
	
	/** Ano das atividades a serem buscadas */
	private Integer ano;
	
	/** Período das atividades a serem buscadas */
	private Integer periodo;
	
	/** Data que determina o limite inicial da busca das atividade */
	private Date dataInicio;
	
	/** Data que determina o limite inicial da busca das atividade */
	private Date dataFim;
	
	/** Formato do relatório */ 
	private String formato;
	
    /** Mapa dos parâmetros */
    private HashMap<String, Object> parametros = new HashMap<String, Object>();
	
	/** Atividades selecionadas para determinado ano-período */ 
	private ArrayList<AtividadeAtualizacaoPedagogica> atividades;
	
	/** participantes da atividade selecionada */
	private ArrayList<ParticipanteAtividadeAtualizacaoPedagogica> participantesAtividade;
	
	/** Recursos NEE escolhidos pelos participantes da atividade selecionada */
	private ArrayList<RecursoNee> recursosNEEAtividade;

	/**
	 * Limpa os dados do bean.
	 * Método não invocado por JSPs
	 */
	private void clear() {
		ano = null;
		periodo = null;
		atividades = null;
		participantesAtividade = null;
		recursosNEEAtividade = null;
		parametros = new HashMap<String, Object>();
		dataInicio = null;
		dataFim = null;
		formato = null;
	}
	
 	/**
 	 * Entra na tela de listagens das atividades do formulário
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/apedagogica/geral.jsp</li>
	 *    <li>sigaa.war/apedagogica/RelatorioAtividadeAP/selecionar_atividade.jsp</li>
	 * </ul>
 	 * @return
 	 * @throws DAOException 
 	 * @throws HibernateException 
 	 * @throws SegurancaException 
 	 */
	public String consultar() throws HibernateException, DAOException, SegurancaException {
		
 		checkRole(SigaaPapeis.GESTOR_PAP);
	
		int mesAux = br.ufrn.arq.util.CalendarUtils.getMesAtual();
		ano = br.ufrn.arq.util.CalendarUtils.getAnoAtual();
		periodo = (( mesAux < 6 ) ? 1 :  2);
		
		AtividadeAtualizacaoPedagogicaDAO aDao = null;
		
		try {
			
			aDao = getDAO(AtividadeAtualizacaoPedagogicaDAO.class);
			atividades = (ArrayList<AtividadeAtualizacaoPedagogica>) aDao.findByAnoPeriodo(ano, periodo);
			
		} finally {
			if (aDao!=null)
				aDao.close();
		}
		
		atividades = new ArrayList<AtividadeAtualizacaoPedagogica>();
		return forward(LIST_PAGE);
	}
	
 	/**
 	 * Retorna as atividades filtradas por ano e período.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/apedagogica/RelatorioAtividadeAP/selecionar_atividade.jsp</li>
	 * </ul>
 	 * @return
 	 * @throws DAOException 
 	 * @throws HibernateException 
 	 */
	public String filtrar() throws HibernateException, DAOException {
		
		if (ano == null || periodo == null)		
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano-Período");
		
		if (hasErrors())
			return null; 
		
		AtividadeAtualizacaoPedagogicaDAO aDao = null;
		
		try {
			
			aDao = getDAO(AtividadeAtualizacaoPedagogicaDAO.class);
			atividades = (ArrayList<AtividadeAtualizacaoPedagogica>) aDao.findByAnoPeriodo(ano, periodo);
			
		} finally {
			if (aDao!=null)
				aDao.close();
		}
		
		return forward(LIST_PAGE);
	}

 	/**
 	 * Seleciona uma atividade para mostrar seus detalhes
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/apedagogica/RelatorioAtividadeAP/selecionar_atividade.jsp</li>
	 * </ul>
 	 * @return
 	 * @throws DAOException 
 	 * @throws HibernateException 
 	 */
	public String selecionar() throws HibernateException, DAOException {
		
		Integer idAtividade = getParameterInt("id",0);
		
		obj = getGenericDAO().findAndFetch(idAtividade, AtividadeAtualizacaoPedagogica.class, "instrutores");
		participantesAtividade = (ArrayList<ParticipanteAtividadeAtualizacaoPedagogica>) getGenericDAO().findByExactField(ParticipanteAtividadeAtualizacaoPedagogica.class, "atividade.id", idAtividade, "asc" , "docente.pessoa.nome");

		recursosNEEAtividade = new ArrayList<RecursoNee>();
		ArrayList<RecursoNee> outrosRecursos = new ArrayList<RecursoNee>();
		int numBraille = 0;
		int numFonte = 0;
		int numLibras = 0;
		ArrayList<Integer> recursosAdicionados = new ArrayList<Integer>();
		for (ParticipanteAtividadeAtualizacaoPedagogica p : participantesAtividade){
			
			for (RecursoNee r : p.getRecursosNee() ){
				
				if (r.getTipoRecursoNee().getId() != TipoRecursoNee.OUTROS){
					
					if (r.getTipoRecursoNee().getId() == TipoRecursoNee.MATERIAL_BRAILLE )
						numBraille++;
					else if (r.getTipoRecursoNee().getId() == TipoRecursoNee.FONTE_AMPLIADA)
						numFonte++;
					else if (r.getTipoRecursoNee().getId() == TipoRecursoNee.TRADUTOR_LIBRAS)
						numLibras++;
					
					if (!recursosAdicionados.contains(r.getTipoRecursoNee().getId())){
						recursosAdicionados.add(r.getTipoRecursoNee().getId());
						recursosNEEAtividade.add(r);
					}						
				} else { 

					boolean adicionado = false;
					if (!outrosRecursos.isEmpty()){
						for (RecursoNee outros : outrosRecursos){
							if (outros.getOutros().equalsIgnoreCase(r.getOutros())){
								adicionado = true;
								outros.setSolicitacoes(outros.getSolicitacoes()+1);
								break;
							}	
						}									
					}
					if (!adicionado){
						r.setSolicitacoes(1);
						outrosRecursos.add(r);
					}
				}	
			}			
		}
		
		// Conta o número de solicitações de cada material.
		for (RecursoNee r : recursosNEEAtividade){
			if (r.getTipoRecursoNee().getId() == TipoRecursoNee.MATERIAL_BRAILLE )
				r.setSolicitacoes(numBraille);
			else if (r.getTipoRecursoNee().getId() == TipoRecursoNee.FONTE_AMPLIADA)
				r.setSolicitacoes(numFonte);
			else if (r.getTipoRecursoNee().getId() == TipoRecursoNee.TRADUTOR_LIBRAS)
				r.setSolicitacoes(numLibras);
		}
		
		recursosNEEAtividade.addAll(outrosRecursos);
		
		return forward(VIEW_PAGE);
	}
	
 	/**
 	 * Entra na tela de listagens das atividades do formulário
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/apedagogica/geral.jsp</li>
	 * </ul>
 	 * @return
 	 * @throws DAOException 
 	 * @throws HibernateException 
 	 * @throws SegurancaException 
 	 */
	public String consultarQuantitativo() throws HibernateException, DAOException, SegurancaException {
		
 		checkRole(SigaaPapeis.GESTOR_PAP);
 		clear();
		ano = br.ufrn.arq.util.CalendarUtils.getAnoAtual();
		
		atividades = new ArrayList<AtividadeAtualizacaoPedagogica>();
		return forward(FORM_PAGE);
	}
	
 	/**
 	 * Gera o relatório quantitativo de atividades
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/apedagogica/RelatorioAtividadeAP/form_quant_atividades.jsp</li>
	 * </ul>
 	 * @return
 	 * @throws DAOException 
 	 * @throws HibernateException 
 	 * @throws SegurancaException 
 	 */
	public String gerarQuantitativo() throws HibernateException, DAOException, SegurancaException {
		
 		if (dataInicio == null || dataFim == null){
 			addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");
			return null;
 		}
 		
 		if (dataInicio != null && dataFim != null && dataInicio.getTime() > dataFim.getTime())
			addMensagemErro("Período Inválido: Período Final deve ser maior que Período Inicial.");

 		if (formato == null || formato.isEmpty()){
 			addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Formato");
			return null;
 		}
			
		if (hasErrors())
			return null;

		
		parametros = new HashMap<String, Object>();
		parametros.put("dataInicial", dataInicio);
		parametros.put("dataFinal", dataFim);
		parametros.put("titulo", "Relatório Quantitativo de Atividades PAP");
		parametros.put("subSistema", getSubSistema().getNome());
        parametros.put("subSistemaLink", getSubSistema().getLink());
        
        // Gerar relatório
        Connection con = null;
        try {

            // Preencher relatório
            con = Database.getInstance().getSigaaConnection();
			InputStream relatorio = JasperReportsUtil.getReportSIGAA("trf99193_qtv_atividades_pap.jasper");
            JasperPrint prt = JasperFillManager.fillReport(relatorio, parametros, con );

			if (prt.getPages().size() == 0) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}

            // Exportar relatório de acordo com o formato escolhido
            String nomeArquivo = "relatorio_quantitativo_pap."+formato;
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e);
            addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor,contacte o suporte através do \"Abrir Chamado\"");
            return null;
        } finally {
            Database.getInstance().close(con);
        }

		return null;
	}
	
	public String voltar(){
		return forward(LIST_PAGE);
	}
	
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getAno() {
		return ano;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setAtividades(ArrayList<AtividadeAtualizacaoPedagogica> atividades) {
		this.atividades = atividades;
	}

	public ArrayList<AtividadeAtualizacaoPedagogica> getAtividades() {
		return atividades;
	}

	public void setParticipantesAtividade(ArrayList<ParticipanteAtividadeAtualizacaoPedagogica> participantesAtividade) {
		this.participantesAtividade = participantesAtividade;
	}

	public ArrayList<ParticipanteAtividadeAtualizacaoPedagogica> getParticipantesAtividade() {
		return participantesAtividade;
	}

	public void setRecursosNEEAtividade(ArrayList<RecursoNee> recursosNEEAtividade) {
		this.recursosNEEAtividade = recursosNEEAtividade;
	}

	public ArrayList<RecursoNee> getRecursosNEEAtividade() {
		return recursosNEEAtividade;
	}

	public void setParametros(HashMap<String, Object> parametros) {
		this.parametros = parametros;
	}

	public HashMap<String, Object> getParametros() {
		return parametros;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	public String getFormato() {
		return formato;
	}


	
}
