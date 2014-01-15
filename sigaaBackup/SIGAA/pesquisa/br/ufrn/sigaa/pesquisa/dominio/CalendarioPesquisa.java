/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/06/2007
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validaInicioFim;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que mantém os períodos importantes
 * para o módulo de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name = "calendario_pesquisa", schema = "pesquisa")
public class CalendarioPesquisa implements Validatable{

	/** Chave primária */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_calendario_pesquisa", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Indica se o calendário foi removido na aplicação ou não */
	private boolean ativo;
	
	/** Indica se o calendário está em seu período vigente ou não */
	private boolean vigente;
	
	/** Ano do calendário */
	private Integer ano;

	/** Início do período de envio de relatórios parciais de bolsa de IC */
	@Column(name = "inicio_envio_relatorio_parcial_bolsa")
	private Date inicioEnvioRelatorioParcialBolsa;
	
	/** Fim do período de envio de relatórios parciais de bolsa de IC */
	@Column(name = "fim_envio_relatorio_parcial_bolsa")
	private Date fimEnvioRelatorioParcialBolsa;

	/** Início do período de submissão e renovação de projetos de pesquisa e solicitação de cotas de bolsas */
	@Column(name = "inicio_envio_projetos")
	private Date inicioEnvioProjetos;
	
	/** Fim do período de submissão e renovação de projetos de pesquisa e solicitação de cotas de bolsas */
	@Column(name = "fim_envio_projetos")
	private Date fimEnvioProjetos;

	/** Início do período de avaliação dos projetos de pesquisa pelos consultores externos; */
	@Column(name = "inicio_avaliacao_consultores")
	private Date inicioAvaliacaoConsultores;
	
	/** Fim do período de avaliação dos projetos de pesquisa pelos consultores externos; */
	@Column(name = "fim_avaliacao_consultores")
	private Date fimEnvioAvaliacaoConsultores;

	/** Início do período de submissão de relatórios finais de bolsa de IC */
	@Column(name = "inicio_envio_relatorio_final_bolsa")
	private Date inicioRelatorioFinalBolsa;
	
	/** Fim do período de submissão de relatórios finais de bolsa de IC */
	@Column(name = "fim_envio_relatorio_final_bolsa")
	private Date fimRelatorioFinalBolsa;

	/** Início do período de submissão de resumos do CIC independentes */
	@Column(name = "inicio_envio_resumo_cic")
	private Date inicioResumoCIC;
	
	/** Fim do período de submissão de resumos do CIC independentes */
	@Column(name = "fim_envio_resumo_cic")
	private Date fimResumoCIC;

	/** Início do período de submissão de relatórios finais de projetos de pesquisa */
	@Column(name = "inicio_envio_relatorio_final_projeto")
	private Date inicioRelatorioFinalProjeto;
	
	/** Fim do período de submissão de relatórios finais de projetos de pesquisa */
	@Column(name = "fim_envio_relatorio_final_projeto")
	private Date fimRelatorioFinalProjeto;

	/** Início do período de indicação de novos bolsistas para as cotas aprovadas */
	@Column(name = "inicio_indicacao_bolsista")
	private Date inicioIndicacaoBolsista;
	
	/** Fim do período de indicação de novos bolsistas para as cotas aprovadas */
	@Column(name = "fim_indicacao_bolsista")
	private Date fimIndicacaoBolsista;

	/** Início do segundo período de renovação de projetos */
	@Column(name = "inicio_segunda_renovacao")
	private Date inicioSegundaRenovacao;
	
	/** Fim do segundo período de renovação de projetos */
	@Column(name = "fim_segunda_renovacao")
	private Date fimSegundaRenovacao;

	/** Início do segundo período de envio de relatórios finais de projetos de pesquisa */
	@Column(name = "inicio_envio_relatorio_projeto2")
	private Date inicioRelatorioFinalProjeto2;
	
	/** Fim do segundo período de envio de relatórios finais de projetos de pesquisa */
	@Column(name = "fim_envio_relatorio_projeto2")
	private Date fimRelatorioFinalProjeto2;

	public CalendarioPesquisa() {
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getFimEnvioAvaliacaoConsultores() {
		return fimEnvioAvaliacaoConsultores;
	}

	public void setFimEnvioAvaliacaoConsultores(Date fimEnvioAvaliacaoConsultores) {
		this.fimEnvioAvaliacaoConsultores = fimEnvioAvaliacaoConsultores;
	}

	public Date getFimEnvioProjetos() {
		return fimEnvioProjetos;
	}

	public void setFimEnvioProjetos(Date fimEnvioProjetos) {
		this.fimEnvioProjetos = fimEnvioProjetos;
	}

	public Date getFimEnvioRelatorioParcialBolsa() {
		return fimEnvioRelatorioParcialBolsa;
	}

	public void setFimEnvioRelatorioParcialBolsa(Date fimEnvioRelatorioParcialBolsa) {
		this.fimEnvioRelatorioParcialBolsa = fimEnvioRelatorioParcialBolsa;
	}

	public Date getFimIndicacaoBolsista() {
		return fimIndicacaoBolsista;
	}

	public void setFimIndicacaoBolsista(Date fimIndicacaoBolsista) {
		this.fimIndicacaoBolsista = fimIndicacaoBolsista;
	}

	public Date getFimRelatorioFinalBolsa() {
		return fimRelatorioFinalBolsa;
	}

	public void setFimRelatorioFinalBolsa(Date fimRelatorioFinalBolsa) {
		this.fimRelatorioFinalBolsa = fimRelatorioFinalBolsa;
	}

	public Date getFimRelatorioFinalProjeto() {
		return fimRelatorioFinalProjeto;
	}

	public void setFimRelatorioFinalProjeto(Date fimRelatorioFinalProjeto) {
		this.fimRelatorioFinalProjeto = fimRelatorioFinalProjeto;
	}

	public Date getFimRelatorioFinalProjeto2() {
		return fimRelatorioFinalProjeto2;
	}

	public void setFimRelatorioFinalProjeto2(Date fimRelatorioFinalProjeto2) {
		this.fimRelatorioFinalProjeto2 = fimRelatorioFinalProjeto2;
	}

	public Date getFimSegundaRenovacao() {
		return fimSegundaRenovacao;
	}

	public void setFimSegundaRenovacao(Date fimSegundaRenovacao) {
		this.fimSegundaRenovacao = fimSegundaRenovacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getInicioAvaliacaoConsultores() {
		return inicioAvaliacaoConsultores;
	}

	public void setInicioAvaliacaoConsultores(Date inicioAvaliacaoConsultores) {
		this.inicioAvaliacaoConsultores = inicioAvaliacaoConsultores;
	}

	public Date getInicioEnvioProjetos() {
		return inicioEnvioProjetos;
	}

	public void setInicioEnvioProjetos(Date inicioEnvioProjetos) {
		this.inicioEnvioProjetos = inicioEnvioProjetos;
	}

	public Date getInicioEnvioRelatorioParcialBolsa() {
		return inicioEnvioRelatorioParcialBolsa;
	}

	public void setInicioEnvioRelatorioParcialBolsa(
			Date inicioEnvioRelatorioParcialBolsa) {
		this.inicioEnvioRelatorioParcialBolsa = inicioEnvioRelatorioParcialBolsa;
	}

	public Date getInicioIndicacaoBolsista() {
		return inicioIndicacaoBolsista;
	}

	public void setInicioIndicacaoBolsista(Date inicioIndicacaoBolsista) {
		this.inicioIndicacaoBolsista = inicioIndicacaoBolsista;
	}

	public Date getInicioRelatorioFinalBolsa() {
		return inicioRelatorioFinalBolsa;
	}

	public void setInicioRelatorioFinalBolsa(Date inicioRelatorioFinalBolsa) {
		this.inicioRelatorioFinalBolsa = inicioRelatorioFinalBolsa;
	}

	public Date getInicioRelatorioFinalProjeto() {
		return inicioRelatorioFinalProjeto;
	}

	public void setInicioRelatorioFinalProjeto(Date inicioRelatorioFinalProjeto) {
		this.inicioRelatorioFinalProjeto = inicioRelatorioFinalProjeto;
	}

	public Date getInicioRelatorioFinalProjeto2() {
		return inicioRelatorioFinalProjeto2;
	}

	public void setInicioRelatorioFinalProjeto2(Date inicioRelatorioFinalProjeto2) {
		this.inicioRelatorioFinalProjeto2 = inicioRelatorioFinalProjeto2;
	}

	public Date getInicioSegundaRenovacao() {
		return inicioSegundaRenovacao;
	}

	public void setInicioSegundaRenovacao(Date inicioSegundaRenovacao) {
		this.inicioSegundaRenovacao = inicioSegundaRenovacao;
	}

	public Date getFimResumoCIC() {
		return this.fimResumoCIC;
	}

	public void setFimResumoCIC(Date fimResumoCIC) {
		this.fimResumoCIC = fimResumoCIC;
	}

	public Date getInicioResumoCIC() {
		return this.inicioResumoCIC;
	}

	public void setInicioResumoCIC(Date inicioResumoCIC) {
		this.inicioResumoCIC = inicioResumoCIC;
	}

	public boolean isVigente() {
		return vigente;
	}

	public void setVigente(boolean vigente) {
		this.vigente = vigente;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/**
	 * Retorna o ano do calendário com a indicação se é o ano vigente, caso seja.
	 * @return
	 */
	@Transient
	public String getAnoVigente() {
		StringBuilder s = new StringBuilder();
		s.append(""+getAno());
		if (isVigente())
			s.append(" - Vigente");
		return  s.toString();
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(ano, "Ano", lista);

		validaInicioFim(inicioEnvioRelatorioParcialBolsa, fimEnvioRelatorioParcialBolsa, "Período de Envio de relatórios parciais de bolsa de IC", lista);
		validaInicioFim(inicioEnvioProjetos, fimEnvioProjetos, "Período de Submissão e renovação de projetos de pesquisa", lista);
		validaInicioFim(inicioAvaliacaoConsultores, fimEnvioAvaliacaoConsultores, "Período de Avaliação dos projetos de pesquisa pelos consultores externos", lista);
		validaInicioFim(inicioRelatorioFinalBolsa, fimRelatorioFinalBolsa, "Período de Submissão de relatórios finais de bolsa de IC", lista);
		validaInicioFim(inicioResumoCIC, fimResumoCIC, "Período de Submissão de resumos do CIC independentes", lista);
		validaInicioFim(inicioRelatorioFinalProjeto, fimRelatorioFinalProjeto, "Período de Submissão de relatórios finais de projetos de pesquisa", lista);
		validaInicioFim(inicioIndicacaoBolsista, fimIndicacaoBolsista, "Período de Indicação de novos bolsistas para as cotas aprovadas", lista);
		validaInicioFim(inicioSegundaRenovacao, fimSegundaRenovacao, "Segundo período de renovação de projetos", lista);		
		validaInicioFim(inicioRelatorioFinalProjeto2, fimRelatorioFinalProjeto2, "Segundo período de envio de relatórios finais de projetos de pesquisa", lista);
		
		if(!lista.getMensagens().isEmpty())
			lista.addErro("Por favor, informe todas as datas dos períodos.");
		
		return lista;
	}
	
	/**
	 * Verifica se hoje está no período de envio de relatórios parciais de iniciação a pesquisa.
	 * @return
	 */
	public boolean isPeriodoEnvioRelatorioParcialBolsa() {
		return isOperacaoDentroPeriodo(getInicioEnvioRelatorioParcialBolsa(), getFimEnvioRelatorioParcialBolsa());
	}

	/**
	 * Verifica se hoje está no período de envio de relatórios finais de iniciação a pesquisa.
	 * @return
	 */
	public boolean isPeriodoEnvioRelatorioFinalBolsa() {
		return isOperacaoDentroPeriodo(getInicioRelatorioFinalBolsa(), getFimRelatorioFinalBolsa());
	}
	
	/**
	 * Verifica se hoje está em algum dos dois períodos de envio de relatórios anuais de projetos.
	 * @return
	 */
	public boolean isPeriodoEnvioRelatorioAnualProjeto() {
		return isOperacaoDentroPeriodo(getInicioRelatorioFinalProjeto(), getFimRelatorioFinalProjeto()) 
		|| isOperacaoDentroPeriodo(getInicioRelatorioFinalProjeto2(), getFimRelatorioFinalProjeto2());
	}

	public boolean isPeriodoEnvioResumoCICIndependente() {
		return isOperacaoDentroPeriodo(getInicioResumoCIC(), getFimResumoCIC());
	}
	
	/** Indica se a data atual está dentro do período informado.
	 * @param inicio
	 * @param fim
	 * @return true, caso a data atual esteja dentro do período informado. 
	 */
	private boolean isOperacaoDentroPeriodo(Date inicio, Date fim) {
		return CalendarUtils.isDentroPeriodo(inicio, fim);
	}
}
