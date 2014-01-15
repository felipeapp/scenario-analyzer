/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.relatorios.LinhaDadosIndiceAcademico;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoAcompanhamentoBolsas;

/** Controller responsável pela geração de relatórios de acompanhamento de bolsas. 
 * @author Édipo Elder F. Melo
 *
 */
@Component("relatorioAcompanhamentoBolsas")
@Scope("request")
public class RelatorioAcompanhamentoBolsasMBean extends SigaaAbstractController {

	/** Dados do relatório. */
	private List<Map<String, Object>> dadosRelatorio;
	/** Dados do relatório. */
	private List<LinhaDadosIndiceAcademico> dadosRelatorioIndiceAcademico;
	/** Ano a ser consultado. */
	private int ano;
	/** Período a ser consultado. */
	private int periodo;
	/** Ano de ingresso na bolsa. */
	private int anoIngresso;
	/** Período de ingresso na bolsa. */
	private int periodoIngresso;
	/** Tipo de bolsa que se deseja retornar */
	private TipoBolsaAuxilio tipoBolsaAuxilio = new TipoBolsaAuxilio();
	/** A situação da bolsa que se deseja retornar */
	private SituacaoBolsaAuxilio situacaoBolsaAuxilio = new SituacaoBolsaAuxilio(SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA);
	/** Indica se exibe no formulário a opção para "somente destacados". */
	private boolean exibirSomenteDestacados;
	/** Indica o tipo de relatório a gerar.*/
	private int tipoConsulta;
	
	private final int CONSULTA_CARGA_HORARIA_SEMESTRE_ATUAL = 1;
	private final int CONSULTA_DESEMPENHO_ACADEMICO = 2;
	
	private boolean checkIngresso;
	private boolean checkBolsista;
	private boolean checkTipoBolsa;
	private boolean checkSituacaoBolsa;
	/** Indica se lista somente os alunos a serem destacados. */
	private boolean somenteDestacados = false;
	
	/** Inicia o relatório de bolsistas com a frequência e aprovação nas disciplinas do ano/semestre atual.
     * Chamado por /portais/rh_plan/abas/graduacao.jsp
     * Chamado por /sae/menu.jsp
     * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciarRelatorioBolsistaCHMatriculada() throws DAOException {
		ano = getAnoAtual();
		periodo = getPeriodoAtual();
		exibirSomenteDestacados = false;
		tipoConsulta = CONSULTA_CARGA_HORARIA_SEMESTRE_ATUAL;
		return forward("/portais/rh_plan/relatorios/form_ano_periodo_destacado.jsp");
	}
	
	private void clear() {
		ano = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		anoIngresso = CalendarUtils.getAnoAtual();
		periodoIngresso = getPeriodoAtual();
		tipoBolsaAuxilio.setId(0);
		situacaoBolsaAuxilio.setId(SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA);
	}
	
	/** Gera o relatório de bolsistas com CH matriculada no período atual.
     * Chamado por /portais/rh_plan/relatorios/form_ano_periodo_destacado.jsp
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String gerarRelatorioBolsistaCHMatriculada() throws NegocioException, ArqException {
		validacaoDados(erros);
		if (hasErrors())
			return null;
		MovimentoAcompanhamentoBolsas mov = new MovimentoAcompanhamentoBolsas();
		prepareMovimento(SigaaListaComando.GERAR_RELATORIO_ACOMPANHAMENTO_BOLSAS);
		mov.setCodMovimento(SigaaListaComando.GERAR_RELATORIO_ACOMPANHAMENTO_BOLSAS);
		mov.setConsulta(MovimentoAcompanhamentoBolsas.CONSULTA_CARGA_HORARIA_SEMESTRE_ATUAL);
		mov.setAno(ano);
		mov.setPeriodo(periodo);
		dadosRelatorio = (List<Map<String, Object>>) execute(mov);
		return forward("/portais/rh_plan/relatorios/rel_bolsista_ch_matriculada.jsp");
	}
	
	/** Inicia o relatório de bolsistas com a frequência e aprovação nas disciplinas do ano/semestre atual.
     * Chamado por /portais/rh_plan/abas/graduacao.jsp
     * Chamado por /sae/menu.jsp
     * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciarRelatorioBolsistaFrequencia() throws DAOException {
		ano = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		anoIngresso = CalendarUtils.getAnoAtual();
		periodoIngresso = getPeriodoAtual();
		situacaoBolsaAuxilio = new SituacaoBolsaAuxilio();
		exibirSomenteDestacados = true;
		somenteDestacados = false;
		tipoConsulta = CONSULTA_DESEMPENHO_ACADEMICO;
		situacaoBolsaAuxilio = new SituacaoBolsaAuxilio(SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA);
		return forward("/portais/rh_plan/relatorios/form_ano_periodo_destacado.jsp");
	}
	
	/** Gera o relatório de bolsistas com a frequência e aprovação nas disciplinas do ano/semestre atual.
	 * Chamado por /portais/rh_plan/relatorios/form_ano_periodo_destacado.jsp 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String gerarRelatorioBolsistaFrequencia() throws NegocioException, ArqException {
		validacaoDados(erros);
		if (hasErrors()) {
			return null;
		}
		
		try {
			MovimentoAcompanhamentoBolsas mov = new MovimentoAcompanhamentoBolsas();
			prepareMovimento(SigaaListaComando.GERAR_RELATORIO_ACOMPANHAMENTO_BOLSAS);
			mov.setCodMovimento(SigaaListaComando.GERAR_RELATORIO_ACOMPANHAMENTO_BOLSAS);
			mov.setConsulta(MovimentoAcompanhamentoBolsas.CONSULTA_DESEMPENHO_ACADEMICO);
			mov.setAno(ano);
			mov.setPeriodo(periodo);
			mov.setAnoIngresso(anoIngresso);
			mov.setPeriodoIngresso(periodoIngresso);
			mov.setIdSituacaoBolsa(situacaoBolsaAuxilio.getId());
			mov.setIdTipoBolsa(tipoBolsaAuxilio.getId());
			dadosRelatorioIndiceAcademico = (List<LinhaDadosIndiceAcademico>) execute(mov);
			
			if (somenteDestacados)
				dadosRelatorioIndiceAcademico = LinhaDadosIndiceAcademico.returnDiscentesBaixoIndice(dadosRelatorioIndiceAcademico);
			
			if ( dadosRelatorioIndiceAcademico.isEmpty() ) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				clear();
				return null;
			}

			setTipoBolsaAuxilio( getGenericDAO().findByPrimaryKey(tipoBolsaAuxilio.getId(), TipoBolsaAuxilio.class) );
			setSituacaoBolsaAuxilio( getGenericDAO().findByPrimaryKey(situacaoBolsaAuxilio.getId(), SituacaoBolsaAuxilio.class) );
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		return forward("/portais/rh_plan/relatorios/rel_bolsista_aprovacao.jsp");
	}
	
	/** Gera o relatório de bolsistas com CH matriculada no período atual.
     * Chamado por /portais/rh_plan/abas/graduacao.jsp
     * Chamado por /sae/menu.jsp
     * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String iniciarRelatorioBolsistaDuploOuVinculo() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.GERAR_RELATORIO_ACOMPANHAMENTO_BOLSAS);
		MovimentoAcompanhamentoBolsas mov = new MovimentoAcompanhamentoBolsas();
		mov.setCodMovimento(SigaaListaComando.GERAR_RELATORIO_ACOMPANHAMENTO_BOLSAS);
		mov.setConsulta(MovimentoAcompanhamentoBolsas.CONSULTA_BOLSISTA_DUPLO_VINCULO);
		dadosRelatorio = (List<Map<String, Object>>) execute(mov);
		return forward("/portais/rh_plan/relatorios/rel_bolsista_vinculo.jsp");
	}
	
	/** Gera o relatório de acordo com o tipo previamente setado.
	 * Chamado por /portais/rh_plan/relatorios/form_ano_periodo_destacado.jsp 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String gerarRelatorio() throws NegocioException, ArqException {
		switch (tipoConsulta) {
		case CONSULTA_CARGA_HORARIA_SEMESTRE_ATUAL: return gerarRelatorioBolsistaCHMatriculada();
		case CONSULTA_DESEMPENHO_ACADEMICO : return gerarRelatorioBolsistaFrequencia();
		default: addMensagemErro("Não foi possível determinar o tipo do relatório a ser gerado.");
			return null;
		}
	}
	
	/**
	 * Retorna todos os tipo de Bolsa Auxilio cadastrada no SIGAA
	 */
	public Collection<SelectItem> getAllComboBolsaAuxilio() throws ArqException {
		return toSelectItems(getGenericDAO().findAll(TipoBolsaAuxilio.class), "id", "denominacao");
	}
	
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		if ( !( checkBolsista || checkIngresso || checkSituacaoBolsa || checkTipoBolsa ) )
			mensagens.addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		
		if ( mensagens.isEmpty() ) {
			if ( !checkBolsista ) {
				ano = 0;
				periodo = 0;
			}
			
			if ( !checkIngresso ) {
				anoIngresso = 0;
				periodoIngresso = 0;
			}
			
			if ( !checkTipoBolsa )
				tipoBolsaAuxilio.setId(0);
			
			if ( !checkSituacaoBolsa ) {
				situacaoBolsaAuxilio.setId(0);
			}
		}
		
		return mensagens.isEmpty();
	}

	/** Retorna os dados do relatório.  
	 * @return Dados do relatório. 
	 */
	public List<Map<String, Object>> getDadosRelatorio() {
		return dadosRelatorio;
	}

	/** Seta os dados do relatório. 
	 * @param dadosRelatorio Dados do relatório. 
	 */
	public void setDadosRelatorio(List<Map<String, Object>> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	/** Indica se lista somente os alunos a serem destacados. 
	 * @return  
	 */
	public boolean isSomenteDestacados() {
		return somenteDestacados;
	}

	/** Seta se lista somente os alunos a serem destacados. 
	 * @param somenteDestacados
	 */
	public void setSomenteDestacados(boolean somenteDestacados) {
		this.somenteDestacados = somenteDestacados;
	}

	/** Retorna o ano a ser consultado. 
	 * @return Ano a ser consultado. 
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano a ser consultado. 
	 * @param ano Ano a ser consultado. 
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o período a ser consultado. 
	 * @return Período a ser consultado. 
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o período a ser consultado. 
	 * @param periodo Período a ser consultado. 
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Indica se exibe no formulário a opção para "somente destacados".
	 * @return 
	 */
	public boolean isExibirSomenteDestacados() {
		return exibirSomenteDestacados;
	}

	/** Seta se exibe no formulário a opção para "somente destacados".
	 * @param exibirSomenteDestacados
	 */
	public void setExibirSomenteDestacados(boolean exibirSomenteDestacados) {
		this.exibirSomenteDestacados = exibirSomenteDestacados;
	}

	/** Indica o tipo de relatório a gerar.
	 * @return
	 */
	public int getTipoConsulta() {
		return tipoConsulta;
	}

	/** Seta o tipo de relatório a gerar.
	 * @param tipoConsulta
	 */
	public void setTipoConsulta(int tipoConsulta) {
		this.tipoConsulta = tipoConsulta;
	}

	public TipoBolsaAuxilio getTipoBolsaAuxilio() {
		return tipoBolsaAuxilio;
	}

	public void setTipoBolsaAuxilio(TipoBolsaAuxilio tipoBolsaAuxilio) {
		this.tipoBolsaAuxilio = tipoBolsaAuxilio;
	}

	public List<LinhaDadosIndiceAcademico> getDadosRelatorioIndiceAcademico() {
		return dadosRelatorioIndiceAcademico;
	}

	public void setDadosRelatorioIndiceAcademico(
			List<LinhaDadosIndiceAcademico> dadosRelatorioIndiceAcademico) {
		this.dadosRelatorioIndiceAcademico = dadosRelatorioIndiceAcademico;
	}

	public SituacaoBolsaAuxilio getSituacaoBolsaAuxilio() {
		return situacaoBolsaAuxilio;
	}

	public void setSituacaoBolsaAuxilio(SituacaoBolsaAuxilio situacaoBolsaAuxilio) {
		this.situacaoBolsaAuxilio = situacaoBolsaAuxilio;
	}

	public int getAnoIngresso() {
		return anoIngresso;
	}

	public void setAnoIngresso(int anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	public int getPeriodoIngresso() {
		return periodoIngresso;
	}

	public void setPeriodoIngresso(int periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

	public boolean isCheckIngresso() {
		return checkIngresso;
	}

	public void setCheckIngresso(boolean checkIngresso) {
		this.checkIngresso = checkIngresso;
	}

	public boolean isCheckBolsista() {
		return checkBolsista;
	}

	public void setCheckBolsista(boolean checkBolsista) {
		this.checkBolsista = checkBolsista;
	}

	public boolean isCheckTipoBolsa() {
		return checkTipoBolsa;
	}

	public void setCheckTipoBolsa(boolean checkTipoBolsa) {
		this.checkTipoBolsa = checkTipoBolsa;
	}

	public boolean isCheckSituacaoBolsa() {
		return checkSituacaoBolsa;
	}

	public void setCheckSituacaoBolsa(boolean checkSituacaoBolsa) {
		this.checkSituacaoBolsa = checkSituacaoBolsa;
	}
	
}