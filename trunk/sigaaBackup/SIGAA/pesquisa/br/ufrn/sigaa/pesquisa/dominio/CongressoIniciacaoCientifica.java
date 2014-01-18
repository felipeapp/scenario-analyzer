/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/07/2007
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade representativa de um Congresso de Iniciação Científica
 *
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "congresso_iniciacao_cientifica", schema = "pesquisa", uniqueConstraints = {})
public class CongressoIniciacaoCientifica implements Validatable{

	private int id;

	/** Número da edição do congresso */
	private String edicao;
	/** Ano do congresso */
	private Integer ano;
	/** Data de início do congresso */
	private Date inicio;
	/** Data de término do congresso */
	private Date fim;
	/** Verifica se o congresso está ativo na base de dados*/
	private boolean ativo;
	/** Data de início de justificativa de ausência  no congresso **/
	private Date inicioJustificativa;
	/** Data final de justificativa de ausência  no congresso **/
	private Date fimJustificativa;

	public Date getInicioJustificativa() {
		return inicioJustificativa;
	}

	public void setInicioJustificativa(Date inicioJustificativa) {
		this.inicioJustificativa = inicioJustificativa;
	}

	public Date getFimJustificativa() {
		return fimJustificativa;
	}

	public void setFimJustificativa(Date fimJustificativa) {
		this.fimJustificativa = fimJustificativa;
	}

	private int sequenciaResumos;

	private List<RestricaoEnvioResumoCIC> restricoes;
	
	public CongressoIniciacaoCientifica() {
		restricoes = new ArrayList<RestricaoEnvioResumoCIC>();
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public String getEdicao() {
		return edicao;
	}

	public void setEdicao(String edicao) {
		this.edicao = edicao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fim", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "inicio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Column(name = "sequencia_resumos", unique = false, nullable = true, insertable = true, updatable = true)
	public int getSequenciaResumos() {
		return sequenciaResumos;
	}

	public void setSequenciaResumos(int sequenciaResumos) {
		this.sequenciaResumos = sequenciaResumos;
	}
	
	@Transient
	public String getDescricao(){
		return getEdicao() + " Congresso de Iniciação Científica - " + getAno();
	}
	
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "congresso")
	public List<RestricaoEnvioResumoCIC> getRestricoes() {
		return restricoes;
	}

	public void setRestricoes(List<RestricaoEnvioResumoCIC> restricoes) {
		this.restricoes = restricoes;
	}

	@Transient
	public void removerRestricao(RestricaoEnvioResumoCIC restricao){
		int posicao = 0;
		for (RestricaoEnvioResumoCIC restricaoAtual : restricoes) {
			if (restricao.getId() != 0 && restricao.getId() == restricaoAtual.getId() 
					|| ( !isEmpty(restricao.getCotaBolsa()) && !isEmpty(restricaoAtual.getCotaBolsa()) 
							&& restricao.getCotaBolsa().getId() == restricaoAtual.getCotaBolsa().getId() ) 
					|| ( !isEmpty(restricao.getTipoBolsa()) && !isEmpty(restricaoAtual.getTipoBolsa()) 
							&& restricao.getTipoBolsa().getId() == restricaoAtual.getTipoBolsa().getId() ) ) {
				restricoes.remove(posicao);
				break;
			}
			posicao++;
		}
	}
	
	@Transient
	public String getDescricaoResumida(){
		return getEdicao() + " CIC - " + getAno();
	}
	
	@Transient
	public String getDescricaoPeriodo(){
		String descricaoPeriodo = "";
		Calendar c = Calendar.getInstance();
		c.setTime(inicio);
		int diaInicio = c.get(Calendar.DATE);
		int mesInicio = c.get(Calendar.MONTH);
		c.setTime(fim);
		int diaFim = c.get(Calendar.DATE);
		int mesFim = c.get(Calendar.MONTH);
		int ano = c.get(Calendar.YEAR);
		
		Formatador f = Formatador.getInstance();
		
		if(mesInicio != mesFim){
			descricaoPeriodo = diaInicio + " de " + f.formatarMes(mesInicio) + " a " + diaFim + " de " + f.formatarMes(mesFim) + " de " + ano;
		}else{
			descricaoPeriodo = diaInicio + " a " + diaFim + " de " + f.formatarMes(mesFim) + " de " + ano;
		}
		
		return descricaoPeriodo;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(edicao, "Edição", lista);
		ValidatorUtil.validateRequired(inicio, "Início Período", lista);
		ValidatorUtil.validateRequired(fim, "Fim Período", lista);
		ValidatorUtil.validateRequired(ano, "Ano", lista);
		ValidatorUtil.validateRequired(fimJustificativa, "Fim Período Justificativa", lista);
		ValidatorUtil.validateRequired(inicioJustificativa, "Início Período Justificativa", lista);

		if ( !isEmpty(inicio) || !isEmpty(fim) )
			ValidatorUtil.validaOrdemTemporalDatas(inicio, fim, false, "Período", lista);

		return lista;
	}

}