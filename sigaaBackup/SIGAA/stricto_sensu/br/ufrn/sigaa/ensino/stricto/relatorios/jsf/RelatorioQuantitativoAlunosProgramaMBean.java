/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Aug 5, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;

import java.util.HashMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Gera o relatório quantitativo de alunos por programa
 * e o total de alunos matriculados no período anterior.
 * @author Victor Hugo
 */
@Component("relatorioQuantitativoAlunosPrograma") @Scope("request")
public class RelatorioQuantitativoAlunosProgramaMBean extends RelatoriosStrictoMBean {

	/** Ano ao qual o relatório se restringe. */
	private int ano;

	/** Período ao qual o relatório se restringe. */
	private int periodo;
	
	/** Tipo de discentes ao qual o relatório se restringe.  */
	private int tipo = Discente.REGULAR;

	/** Relatório de discentes Regulares Ativo / Matriculado. */
	public static final String NOME_RELATORIO_REGULARES = "trf6600_PosStrictoAtv_X_Matr_r.jasper";
	/** Relatório de discentes Especiais  Ativo / Matriculado.  */
	public static final String NOME_RELATORIO_ESPECIAIS = "trf6600_PosStrictoAtv_X_Matr_e.jasper";
	/** Relatório de discentes Especiais.  */
	public static final String NOME_RELATORIO_ATIVO = "trf18453_qtv_discente_programa.jasper";
	
	/** Indica se o formulário deve exibir a opção de ano-período para o usuário escolher. */
	private boolean requerAnoPeriodo;
	/** Indica se o formulário deve exibir a opção de tipo de discente para o usuário escolher. */
	private boolean requerTipoDiscente;
	
	/** Define o tipo de Relatório de Discentes Ativos / Matriculados. */
	public static final int DISCENTES_ATIVOS_MATRICULADOS = 7;
	/** Define o tipo de Relatório de Discentes Ativos. */
    public static final int DISCENTES_ATIVOS = 8;
	   
    /** Inicia a geração dos relatórios quantitativos de discentes ativos / matriculados.
    * <br />
    * Método chamado pela(s) seguinte(s) JSP(s):
    *   <ul>
    *    <li>sigaa.war\stricto\menus\relatorios.jsp</li>
    *    <li>sigaa.war\stricto\menu_coordenador.jsp</li>
    *   </ul>
    *
    * @throws SegurancaException
    * @throws DAOException
    */
	public String iniciarQuantitativoMatriculado() throws DAOException {
		clear();
		ano = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		titulo = "Quantitativo de Discentes Ativos / Matriculados por Programa";
		origemDados = Sistema.SIGAA;
		tipoUnidade = PROGRAMA;
		requerAnoPeriodo = true;
		requerTipoDiscente = true;
		this.tipoRelatorio = DISCENTES_ATIVOS_MATRICULADOS;
		unidade = new Unidade();
		return forward("/stricto/relatorios/form_quantitativos_alunos_programa.jsp");
	}
	
	/** Inicia a geração dos relatórios quantitativos de discentes ativos / matriculados.
    * <br />
    * Método chamado pela(s) seguinte(s) JSP(s):
    *   <ul>
    *    <li>sigaa.war\stricto\menus\relatorios.jsp</li>
    *   </ul>
    *
    * @throws SegurancaException
    * @throws DAOException
    */
	public String iniciarQuantitativoAtivo() throws DAOException {
		clear();
		titulo = "Quantitativo de Discentes Ativos por programa";
		origemDados = Sistema.SIGAA;
		tipoUnidade = PROGRAMA;
		requerAnoPeriodo = false;
		requerTipoDiscente = false;
		this.tipoRelatorio = DISCENTES_ATIVOS;
		return forward("/stricto/relatorios/form_quantitativos_alunos_programa.jsp");
	}

	/** Gera o relatório correspondente.
    * <br />
    * Método chamado pela(s) seguinte(s) JSP(s):
    *   <ul>
    *    <li>sigaa.war\stricto\relatorios\form_quantitativos_alunos_programa.jsp</li>
    *   </ul>
    * @see br.ufrn.sigaa.ensino.stricto.relatorios.jsf.RelatoriosStrictoMBean#gerarRelatorio()
    * 
    */
	@Override
	public String gerarRelatorio() throws DAOException {
		if (requerAnoPeriodo){
			if ( (getParameterInt("anoRelatorio")  == null) || (getParameterInt("periodoRelatorio") == null) ){
				addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano-Período");
				return null;
			}

			ano = getParameterInt("anoRelatorio");
			periodo = getParameterInt("periodoRelatorio");
			ValidatorUtil.validateRequired(ano, "Ano", erros);
			ValidatorUtil.validateRequired(periodo, "Periodo", erros);
		}
		if (tipoRelatorio == DISCENTES_ATIVOS_MATRICULADOS) {

			br.ufrn.arq.util.ValidatorUtil.validateMinValue(ano, 1970, "Ano", erros);
			br.ufrn.arq.util.ValidatorUtil.validateMinValue(periodo, 1, "Período", erros); 
			br.ufrn.arq.util.ValidatorUtil.validateMaxValue(periodo, 2, "Período", erros);
			if( tipo == Discente.REGULAR )
				relatorio = JasperReportsUtil.getReportSIGAA(NOME_RELATORIO_REGULARES);
			else
				relatorio = JasperReportsUtil.getReportSIGAA(NOME_RELATORIO_ESPECIAIS);
		} else if (tipoRelatorio == DISCENTES_ATIVOS) {
			relatorio = JasperReportsUtil.getReportSIGAA(NOME_RELATORIO_ATIVO);
		}
		if( hasErrors() ){
			return null;
		}
		
		parametros = new HashMap<String, Object>();
		if (getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO) ) 
			parametros.put("unidade",getParameterInt("id")) ;
		else
			parametros.put("unidade", unidade.getId());
		parametros.put("ano", ano);
		parametros.put("periodo", periodo);
		parametros.put("subSistema", getSubSistema().getNome());
        parametros.put("subSistemaLink", getSubSistema().getLink());

		return super.gerarRelatorio();
	}

	/** Retorna o ano ao qual o relatório se restringe.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/ /sigaa.war/stricto/relatorios/form_quantitativos_alunos_programa.jsp</li>
	 *   </ul>
	 * @return Ano ao qual o relatório se restringe. 
	 */
	public Integer getAno() {
		return ano;
	}

	/** Seta o ano ao qual o relatório se restringe. 
	 * Método não invocado por JSP's.
	 * @param ano Ano ao qual o relatório se restringe. 
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o período ao qual o relatório se restringe.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/ /sigaa.war/stricto/relatorios/form_quantitativos_alunos_programa.jsp</li>
	 *   </ul>
	 * @return Período ao qual o relatório se restringe. 
	 */
	public Integer getPeriodo() {
		return periodo;
	}

	/** Seta o período ao qual o relatório se restringe. 
	 * Método não invocado por JSP's.
	 * @param periodo Período ao qual o relatório se restringe. 
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Retorna o tipo de discentes ao qual o relatório se restringe.
	 * Método não invocado por JSP's.
	 * @return Tipo de discentes ao qual o relatório se restringe. 
	 */
	public int getTipo() {
		return tipo;
	}

	/** Seta o tipo de discentes ao qual o relatório se restringe. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/ /sigaa.war/stricto/relatorios/form_quantitativos_alunos_programa.jsp</li>
	 *   </ul>
	 * @param tipo Tipo de discentes ao qual o relatório se restringe. 
	 */
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	/** Indica se o formulário deve exibir a opção de ano-período para o usuário escolher. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/ /sigaa.war/stricto/relatorios/form_quantitativos_alunos_programa.jsp</li>
	 *   </ul>
	 * @return caso true, o formulário deve exibir a opção de ano-período para o usuário escolher. 
	 */
	public boolean isRequerAnoPeriodo() {
		return requerAnoPeriodo;
	}

	/** Seta se o formulário deve exibir a opção de ano-período para o usuário escolher. 
	 * Método não invocado por JSP's.
	 * @param requerAnoPeriodo True, se o formulário deve exibir a opção de ano-período para o usuário escolher. 
	 */
	public void setRequerAnoPeriodo(boolean requerAnoPeriodo) {
		this.requerAnoPeriodo = requerAnoPeriodo;
	}

	/** Indica se o formulário deve exibir a opção de tipo de discente para o usuário escolher. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/ /sigaa.war/stricto/relatorios/form_quantitativos_alunos_programa.jsp</li>
	 *   </ul>
	 * @return Caso true, o formulário deve exibir a opção de tipo de discente para o usuário escolher. 
	 */
	public boolean isRequerTipoDiscente() {
		return requerTipoDiscente;
	}

	/** Seta se o formulário deve exibir a opção de tipo de discente para o usuário escolher. 
	 * Método não invocado por JSP's.
	 * @param requerTipoDiscente True, se o formulário deve exibir a opção de tipo de discente para o usuário escolher. 
	 */
	public void setRequerTipoDiscente(boolean requerTipoDiscente) {
		this.requerTipoDiscente = requerTipoDiscente;
	}

}