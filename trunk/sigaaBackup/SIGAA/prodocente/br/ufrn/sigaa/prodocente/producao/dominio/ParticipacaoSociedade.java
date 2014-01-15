/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que registra a participação de um docente em Sociedades Científicas e Culturais
 *
 * @author Gleydson
 */
@Entity
@Table(name = "participacao_sociedade",schema="prodocente")
@PrimaryKeyJoinColumn(name = "id_participacao_sociedade")
public class ParticipacaoSociedade extends Producao {


	@Column(name = "nome_sociedade")
	private String nomeSociedade;

	@Column(name = "periodo_inicio")
	@Temporal(TemporalType.DATE)
	private Date periodoInicio;

	@Column(name = "periodo_fim")
	@Temporal(TemporalType.DATE)
	private Date periodoFim;

	@JoinColumn(name = "id_tipo_participacao_sociedade", referencedColumnName = "id_tipo_participacao_sociedade")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoParticipacaoSociedade tipoParticipacaoSociedade = new TipoParticipacaoSociedade();

	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoRegiao ambito = new TipoRegiao();

	/** Creates a new instance of ParticipacaoSociedade */
	public ParticipacaoSociedade() {
	}

	@Override
	public String getTitulo() {
		return getNomeSociedade();
	}

	public String getNomeSociedade() {
		return nomeSociedade;
	}

	public void setNomeSociedade(String nomeSociedade) {
		this.nomeSociedade = nomeSociedade;
	}

	public Date getPeriodoFim() {
		return periodoFim;
	}

	public void setPeriodoFim(Date periodoFim) {
		this.periodoFim = periodoFim;
	}

	public Date getPeriodoInicio() {
		return periodoInicio;
	}

	public void setPeriodoInicio(Date periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	public TipoParticipacaoSociedade getTipoParticipacaoSociedade() {
		return tipoParticipacaoSociedade;
	}

	public void setTipoParticipacaoSociedade(
			TipoParticipacaoSociedade tipoParticipacaoSociedade) {
		this.tipoParticipacaoSociedade = tipoParticipacaoSociedade;
	}

	public TipoRegiao getAmbito() {
		return ambito;
	}


	public void setAmbito(TipoRegiao ambito) {
		this.ambito = ambito;
	}

	/*
	 * Campos Obrigatorios: Participacao, Ambito, Nome Sociedade,
	 * 						Periodo Inicio e Fim,
	 */

	@Override
	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDataProducao(), "Data de Produção", lista);
		ValidatorUtil.validateRequired(getAmbito(),"Âmbito", lista);
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Periodo Início", lista);
		ValidatorUtil.validateRequired(getNomeSociedade(), "Nome da Sociedade", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Referência", lista);
		return lista;
	}

}
