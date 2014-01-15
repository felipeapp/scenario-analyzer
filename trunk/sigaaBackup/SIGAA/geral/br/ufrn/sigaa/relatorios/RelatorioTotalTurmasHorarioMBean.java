/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
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
 * MBean respons�vel pelo relat�rio Total de Turmas Por Hor�rio
 * Esse relat�rio esta dispon�vel em:
 * Portal Reitoria, Consultas Gerais -> Total de Turmas por Hor�rios de Aula 
 * Gradua��o, Relat�rios DAE -> Total de Turmas por Hor�rios de Aula
 * Portal CPDI, Indicadores da Situa��o do Departamento -> Total de Turmas por Hor�rios de Aula  
 * 
 * @author agostinho
 *
 */

@SuppressWarnings("unchecked")
@Component("relatorioTotalTurmasHorarioMBean")
@Scope("session")
public class RelatorioTotalTurmasHorarioMBean  extends SigaaAbstractController {
	
	/** Diret�rio base dos arquivos fontes de relat�rios. */
	public static final String BASE_REPORT_PACKAGE = "/br/ufrn/sigaa/relatorios/fontes/";
	
	/** Ano de oferta das turmas. */ 
	private int ano;
	
	/** Per�odo de oferta das turmas. */
	private int periodo;
	
	/** Departamento de oferta das turmas.*/
	private Unidade departamento;
	
	/** Centro de oferta das turmas.*/
	private int centro;
	
	/** Indica se deve exibir no formul�rio a op��o de escolha de departamento. */
	private boolean buscarPorDepartamento;
	
	/** Indica se deve exibir no formul�rio a op��o de escolha de centro. */
	private boolean buscarPorCentro;
	
	/** Arquivo usado para gera��o do relat�rio. */
	private String arquivoRelatorio;
	
	/** Formato do relat�rio. */
	private String formato;
	

	/** Par�metros usados na gera��o do relat�rio. */
	private HashMap<String, Object> parametros = new HashMap<String, Object>();
	
	/** Cole��o de departamentos ligados ao centro. */
	private Collection<UnidadeGeral> departamentos;
	
	/** Construtor padr�o. */
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

	/** Inicia a gera��o do relat�rio de total de turmas por hor�rio.
	 * Chamado por (n�o encontrado)
	 * 
	 * @return
	 */
	private String iniciarTotalTurmasPorHorario() {
		clear();
		return forward("/portais/cpdi/relatorios/relatorio_total_turmas_horarios.jsp");
	}
	
	/** Inicia a gera��o do relat�rio de total de turmas por hor�rio ofertados por um departamento.
	 *  * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Inicia a gera��o do relat�rio de total de turmas por hor�rio ofertadas por um centro.
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Inicia a gera��o do relat�rio de total de turmas por hor�rio ofertadas por um centro ou departamento.
	 * Chamado por /graduacao/menus/relatorios_dae.jsp
	 * Chamado por /portais/rh_plan/abas/graduacao.jsp
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
    /** Gera o relat�rio.
     * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
				
		// passando par�metros p/ a consulta SQL
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
	
	/** Indica se o usu�rio tem acesso completo.  
	 * @return True, caso o usu�rio possua os pap�is MEMBRO_CPDI, DAE, CDP. False, caso contr�rio.
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

	/** Retorna o per�odo de oferta das turmas. 
	 * @return
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o per�odo de oferta das turmas.
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Retorna o arquivo usado para gera��o do relat�rio. 
	 * @return
	 */
	public String getArquivoRelatorio() {
		return arquivoRelatorio;
	}

	/** Seta o arquivo usado para gera��o do relat�rio.
	 * @param arquivoRelatorio
	 */
	public void setArquivoRelatorio(String arquivoRelatorio) {
		this.arquivoRelatorio = arquivoRelatorio;
	}

	/** Retorna o formato do relat�rio. 
	 * @return
	 */
	public String getFormato() {
		return formato;
	}

	/** Seta o formato do relat�rio.
	 * @param formato
	 */
	public void setFormato(String formato) {
		this.formato = formato;
	}

	/** Retorna os par�metros usados na gera��o do relat�rio. 
	 * @return
	 */
	public HashMap<String, Object> getParametros() {
		return parametros;
	}

	/** Seta os par�metros usados na gera��o do relat�rio.
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

	/** Indica se deve exibir no formul�rio a op��o de escolha de departamento. 
	 * @return
	 */
	public boolean isBuscarPorDepartamento() {
		return buscarPorDepartamento;
	}

	/** seta se deve exibir no formul�rio a op��o de escolha de departamento. 
	 * @param buscarPorDepartamento
	 */
	public void setBuscarPorDepartamento(boolean buscarPorDepartamento) {
		this.buscarPorDepartamento = buscarPorDepartamento;
	}

	/** Indica se deve exibir no formul�rio a op��o de escolha de centro. 
	 * @return
	 */
	public boolean isBuscarPorCentro() {
		return buscarPorCentro;
	}

	/** Seta se deve exibir no formul�rio a op��o de escolha de centro. 
	 * @param buscarPorCentro
	 */
	public void setBuscarPorCentro(boolean buscarPorCentro) {
		this.buscarPorCentro = buscarPorCentro;
	}

	/** Listener respons�vel por atualizar a cole��o de departamentos ligados ao centro, quando h� mudan�a no valor do centro.
	 * Chamado por 
	 *     * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Retorna uma cole��o de SelectItem de departamentos ligados ao centro.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getDepartamentoCombo() throws DAOException{
		return toSelectItems(departamentos, "id", "nome");
	}

	/** Valida os dados Departamento, Curso, ano e per�odo.
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
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Centro / Unidade Acad�mica Especializada" );
		}
		else{
		if (unidadeLocalizada == null && centroLocalizado.getTipoAcademica() != TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Departamento" );
		}
		}
		if (ano < 1900 || ano > CalendarUtils.getAnoAtual())
			addMensagemErro("Informe um ano v�lido.");
		if (periodo < 1 || periodo > 4)
			addMensagemErro("Informe um per�odo v�lido.");
		return hasErrors();
	}
}
