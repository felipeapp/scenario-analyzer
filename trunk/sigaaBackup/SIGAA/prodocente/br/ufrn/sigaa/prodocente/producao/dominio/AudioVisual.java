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
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Produção intelectual de cunho audio-visual
 *
 * @author Gleydson
 */
@Entity
@Table(name = "audio_visual" , schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_audio_visual")
public class AudioVisual extends ProducaoArtisticaLiterariaVisual {

	@Column(name = "data_divulgacao")
	@Temporal(TemporalType.DATE)
	private Date dataDivulgacao;

	@Column(name = "veiculo")
	private String veiculo;

	@Column(name = "duracao_divulgacao")
	private Short duracaoDivulgacao;

	@Column(name = "divulgado")
	private Boolean divulgado;

	@Column(name="premiada")
	private Boolean premiada;



	/** Creates a new instance of AudioVisual */
	public AudioVisual() {
	}

	public Date getDataDivulgacao() {
		return dataDivulgacao;
	}

	public void setDataDivulgacao(Date dataDivulgacao) {
		setAnoReferencia(CalendarUtils.getAno(dataDivulgacao));
		this.dataDivulgacao = dataDivulgacao;
	}

	public Boolean getDivulgado() {
		return divulgado;
	}

	public void setDivulgado(Boolean divulgado) {
		this.divulgado = divulgado;
	}

	@Override
	public Boolean getPremiada()
	{
		return this.premiada;
	}

	@Override
	public void setPremiada(Boolean premiada)
	{
		this.premiada = premiada;
	}

	public Short getDuracaoDivulgacao() {
		return duracaoDivulgacao;
	}

	public void setDuracaoDivulgacao(Short duracaoDivulgacao) {
		this.duracaoDivulgacao = duracaoDivulgacao;
	}

	public String getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(String veiculo) {
		this.veiculo = veiculo;
	}

	/*
	 * Campos Obrigatórios: Titulo, Tipo Artistico, Tipo Regiao, Participacao,
	 * 						Subtipo Artistico, Data
	 */

	@Override
	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getTitulo(), "Título", lista);
		ValidatorUtil.validateRequired(getAutores(),"Autores", lista);
		ValidatorUtil.validateRequired(getLocal(),"Local", lista);
		if(getTipoParticipacao()!=null)
			ValidatorUtil.validateRequiredId(getTipoParticipacao().getId(),"Tipo de Participação", lista);
		else
			ValidatorUtil.validateRequired(getTipoParticipacao(),"Tipo de Participação", lista);
		if(getTipoRegiao()!=null)
			ValidatorUtil.validateRequiredId(getTipoRegiao().getId(), "Âmbito", lista);
		else
			ValidatorUtil.validateRequired(getTipoRegiao(), "Âmbito", lista);
		if(getTipoArtistico()!=null)
			ValidatorUtil.validateRequiredId(getTipoArtistico().getId(), "Tipo Artístico", lista);
		else
			ValidatorUtil.validateRequired(getTipoArtistico(), "Tipo Artístico", lista);
		if(getSubTipoArtistico()!=null)
			ValidatorUtil.validateRequiredId(getSubTipoArtistico().getId(), "Classificação", lista);
		else
			ValidatorUtil.validateRequired(getSubTipoArtistico(), "Classificação", lista);
		ValidatorUtil.validateRequired(getDataProducao(), "Data de Produção", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Referência", lista);


		return lista;
	}

}
