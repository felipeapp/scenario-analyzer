/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '30/09/2008'
 *
 */
package br.ufrn.sigaa.relatorios;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * MBean responsável pelo relatório Total de Turmas Por Horário
 * Esse relatório esta disponível em:
 * Portal Reitoria, Consultas Gerais -> Total de Turmas por Horários de Aula 
 * Graduação, Relatórios DAE -> Total de Turmas por Horários de Aula
 * Portal CPDI, Indicadores da Situação do Departamento -> Total de Turmas por Horários de Aula  
 * 
 * @author agostinho
 *
 */

@SuppressWarnings("unchecked")
@Component("relatorioTotalTurmasHorarioMBean")
@Scope("session")
public class RelatorioTotalTurmasHorarioMBean  extends SigaaAbstractController {
	
	/** Diretório base dos arquivos fontes de relatórios. */
	public static final String BASE_REPORT_PACKAGE = "/br/ufrn/sigaa/relatorios/fontes/";
	
	/** Ano de oferta das turmas. */ 
	private int ano;
	
	/** Período de oferta das turmas. */
	private int periodo;
	
	/** Departamento de oferta das turmas.*/
	private Unidade departamento;
	
	/** Centro de oferta das turmas.*/
	private int centro;
	
	/** Indica se deve exibir no formulário a opção de escolha de departamento. */
	private boolean buscarPorDepartamento;
	
	/** Indica se deve exibir no formulário a opção de escolha de centro. */
	private boolean buscarPorCentro;
	
	/** Arquivo usado para geração do relatório. */
	private String arquivoRelatorio;
	
	/** Formato do relatório. */
	private String formato;
	

	/** Parâmetros usados na geração do relatório. */
	private HashMap<String, Object> parametros = new HashMap<String, Object>();
	
	/** Coleção de departamentos ligados ao centro. */
	private Collection<UnidadeGeral> departamentos;
	
	/** Construtor padrão. */
	public RelatorioTotalTurmasHorarioMBean() {
		clear();
	}

	/** Reseta os atributos do controller. 
	 * 
	 */
	private void clear() {
		departamento = new Unidade();
		ano = getAnoAtual();
		periodo = getPeriodoAtual();
		parametros.clear();
		centro = 0;
	}

	/** Inicia a geração do relatório de total de turmas por horário.
	 * Chamado por (não encontrado)
	 * 
	 * @return
	 */
	private String iniciarTotalTurmasPorHorario() {
		clear();
		return forward("/portais/cpdi/relatorios/relatorio_total_turmas_horarios.jsp");
	}
	
	/** Inicia a geração do relatório de total de turmas por horário ofertados por um departamento.
	 *  * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/portais/cpdi/abas/sitdepartamento.jsp</li>
	 *   </ul>
	 * @throws SegurancaException
	 * @throws DAOException
	 * @return
	 */
	public String iniciarTotalTurmasPorHorarioDepartamento() {
		buscarPorCentro = true;                
		buscarPorDepartamento = true;
		return iniciarTotalTurmasPorHorario();
	}
	
	/** Inicia a geração do relatório de total de turmas por horário ofertadas por um centro.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>jsp na encontrada</li>
	 *   </ul>
	 * @throws SegurancaException
	 * @throws DAOException
	 * @return
	 */
	public String iniciarTotalTurmasPorHorarioCentro() {
		buscarPorDepartamento = false;
		buscarPorCentro = true;
		return iniciarTotalTurmasPorHorario();
	}
	
	/** Inicia a geração do relatório de total de turmas por horário ofertadas por um centro ou departamento.
	 * Chamado por /graduacao/menus/relatorios_dae.jsp
	 * Chamado por /portais/rh_plan/abas/graduacao.jsp
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/graduacao/menus/relatorios_dae.jsp</li>
	 *    <li>/portais/rh_plan/abas/graduacao.jsp</li>
	 *   </ul>
	 * @throws SegurancaException
	 * @throws DAOException
	 * @return
	 */
	public String iniciarTotalTurmasPorHorarioDepartamentoOuCentro() {
		buscarPorCentro = true;
		buscarPorDepartamento = true;
		return iniciarTotalTurmasPorHorario();
	}
	
    /** Gera o relatório.
     * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/portais/cpdi/relatorios</li>
	 *   </ul>
	 * @throws SegurancaException

     * @return
     * @throws DAOException
     * @throws SQLException
     * @throws JRException
     * @throws IOException
     */
    public String gerarRelatorio() throws DAOException, SQLException, JRException, IOException {
    	
    	validacaoDados(erros.getMensagens());
    	
		if (hasErrors())
			return null;
		Unidade unidadeLocalizada;
		Unidade centroLocalizado;
		unidadeLocalizada = getDAO(UnidadeDAOImpl.class).findByPrimaryKey(departamento.getId(), Unidade.class);
		centroLocalizado = getDAO(UnidadeDAOImpl.class).findByPrimaryKey(centro, Unidade.class);

		if(centroLocalizado.getTipoAcademica() == TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA){
			parametros.put("centroNome", centroLocalizado.getNome() == null ? "" : centroLocalizado.getNome());
		}
		else if (unidadeLocalizada != null && unidadeLocalizada.getUnidadeGestora().getUnidadeGestora() != null)
			parametros.put("centroNome", unidadeLocalizada.getUnidadeGestora().getNome());
		else
			parametros.put("centroNome", null);
				
		if(centroLocalizado.getTipoAcademica() == TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA){
			parametros.put("departamentoNome", null);
		}
		else if (unidadeLocalizada != null && unidadeLocalizada.getUnidadeGestora().getUnidadeGestora() != null){
			parametros.put("departamentoNome", unidadeLocalizada.getNome());
		}
				
		// passando parâmetros p/ a consulta SQL
		if (buscarPorCentro == true && centro > 0 && centroLocalizado.getTipoAcademica() != TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA) {
			parametros.put("centro", centro);
		}
		else{
			parametros.put("centro", centroLocalizado.getGestora().getId());
		}
		
		if (buscarPorDepartamento == true && departamento.getId() > 0 && centroLocalizado.getTipoAcademica() != TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA) {
			parametros.put("departamento", departamento.getId() );
		}
		else{
			parametros.put("departamento", centro);
		}
		
		arquivoRelatorio = "trf9209_Relatorio_Qtd_Turmas_Por_Horario";
		formato = "pdf";
		parametros.put("ano", ano);
		parametros.put("periodo", periodo);
		parametros.put("subSistema", getSubSistema().getNome());
        parametros.put("subSistemaLink", getSubSistema().getLink());
		
		if (parametros.get("centro") != null && (parametros.get("departamento") != null || centroLocalizado.getTipoAcademica() == TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA) && arquivoRelatorio != null && formato != null) {
			Connection con = Database.getInstance().getSigaaConnection();
	        JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReport(BASE_REPORT_PACKAGE, arquivoRelatorio+".jasper"), parametros, con );
	        String nomeArquivo = "relatorio_" + arquivoRelatorio + "." + formato;
	        
	        if (prt.getPages().size() == 0) {
	        	addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
	        	
	        	
	        } else {
		        JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
		        FacesContext.getCurrentInstance().responseComplete();
	        }
	        
	        return forward("/portais/cpdi/relatorios/relatorio_total_turmas_horarios.jsp");
		}
		return null;
    }
	
	/** Retorna o departamento de oferta das turmas.
	 * @return
	 */
	public Unidade getDepartamento() {
		return departamento;
	}
	
	/** Seta o departamento de oferta das turmas.
	 * @param departamento
	 */
	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}
	
	/** Indica se o usuário tem acesso completo.  
	 * @return True, caso o usuário possua os papéis MEMBRO_CPDI, DAE, CDP. False, caso contrário.
	 */
	public boolean isAcessoCompleto() {
    	return isUserInRole(SigaaPapeis.MEMBRO_CPDI, SigaaPapeis.DAE, SigaaPapeis.CDP , SigaaPapeis.PORTAL_PLANEJAMENTO );
    }
	
	/** Retorna o ano de oferta das turmas. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano de oferta das turmas.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o período de oferta das turmas. 
	 * @return
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o período de oferta das turmas.
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Retorna o arquivo usado para geração do relatório. 
	 * @return
	 */
	public String getArquivoRelatorio() {
		return arquivoRelatorio;
	}

	/** Seta o arquivo usado para geração do relatório.
	 * @param arquivoRelatorio
	 */
	public void setArquivoRelatorio(String arquivoRelatorio) {
		this.arquivoRelatorio = arquivoRelatorio;
	}

	/** Retorna o formato do relatório. 
	 * @return
	 */
	public String getFormato() {
		return formato;
	}

	/** Seta o formato do relatório.
	 * @param formato
	 */
	public void setFormato(String formato) {
		this.formato = formato;
	}

	/** Retorna os parâmetros usados na geração do relatório. 
	 * @return
	 */
	public HashMap<String, Object> getParametros() {
		return parametros;
	}

	/** Seta os parâmetros usados na geração do relatório.
	 * @param parametros
	 */
	public void setParametros(HashMap<String, Object> parametros) {
		this.parametros = parametros;
	}

	/** Retorna o centro de oferta das turmas.
	 * @return
	 */
	public int getCentro() {
		return centro;
	}

	/** Seta o centro de oferta das turmas.
	 * @param centro
	 */
	public void setCentro(int centro) {
		this.centro = centro;
	}

	/** Indica se deve exibir no formulário a opção de escolha de departamento. 
	 * @return
	 */
	public boolean isBuscarPorDepartamento() {
		return buscarPorDepartamento;
	}

	/** seta se deve exibir no formulário a opção de escolha de departamento. 
	 * @param buscarPorDepartamento
	 */
	public void setBuscarPorDepartamento(boolean buscarPorDepartamento) {
		this.buscarPorDepartamento = buscarPorDepartamento;
	}

	/** Indica se deve exibir no formulário a opção de escolha de centro. 
	 * @return
	 */
	public boolean isBuscarPorCentro() {
		return buscarPorCentro;
	}

	/** Seta se deve exibir no formulário a opção de escolha de centro. 
	 * @param buscarPorCentro
	 */
	public void setBuscarPorCentro(boolean buscarPorCentro) {
		this.buscarPorCentro = buscarPorCentro;
	}

	/** Listener responsável por atualizar a coleção de departamentos ligados ao centro, quando há mudança no valor do centro.
	 * Chamado por 
	 *     * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/portais/cpdi/relatorios/relatorio_total_turmas_horarios.jsp</li>
	 *   </ul>
	 * @throws SegurancaException

     * @return
     * @throws DAOException
     * @throws SQLException
     * @throws JRException
     * @throws IOException
	 * @param evt
	 */
	public void centroListener(ValueChangeEvent evt) throws DAOException {
		UnidadeDao dao = null;
		
		try{
			
			centro = (Integer) evt.getNewValue();
			dao = getDAO(UnidadeDao.class);
			departamentos = dao.findBySubUnidades(new Unidade(centro),TipoUnidadeAcademica.DEPARTAMENTO);
			forward("/portais/cpdi/relatorios/relatorio_total_turmas_horarios.jsp");
		
		} finally {
			if ( dao != null )
				dao.close();
		}
		
	
		
	}
	
	/** Retorna uma coleção de SelectItem de departamentos ligados ao centro.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getDepartamentoCombo() throws DAOException{
		return toSelectItems(departamentos, "id", "nome");
	}

	/** Valida os dados Departamento, Curso, ano e período.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(Collection mensagens) {
		Unidade unidadeLocalizada;
		Unidade centroLocalizado=null;
		try {
			centroLocalizado = getDAO(UnidadeDAOImpl.class).findByPrimaryKey(centro, Unidade.class);
			unidadeLocalizada = getDAO(UnidadeDAOImpl.class).findByPrimaryKey(departamento.getId(), Unidade.class);
		} catch (DAOException e) {
			unidadeLocalizada = null;
		}
		if (centro <= 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Departamento" );
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Centro / Unidade Acadêmica Especializada" );
		}
		else{
		if (unidadeLocalizada == null && centroLocalizado.getTipoAcademica() != TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Departamento" );
		}
		}
		if (ano < 1900 || ano > CalendarUtils.getAnoAtual())
			addMensagemErro("Informe um ano válido.");
		if (periodo < 1 || periodo > 4)
			addMensagemErro("Informe um período válido.");
		return hasErrors();
	}
}
