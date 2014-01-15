/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import java.util.Date;
import java.util.HashMap;

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
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que registra a participa��o de um servidor em comiss�es organizadoras
 * de eventos
 *
 * @author Gleydson
 */
@Entity
@Table(name = "participacao_comissao_org_eventos", schema="prodocente")
@PrimaryKeyJoinColumn(name="id_participacao_comissao_org_eventos")
public class ParticipacaoComissaoOrgEventos extends Producao implements ViewAtividadeBuilder {

	@Column(name = "veiculo")
	private String veiculo;

	@Column(name = "local")
	private String local;

	@Column(name = "periodo_inicio")
	@Temporal(TemporalType.DATE)
	private Date periodoInicio;

	@Column(name = "periodo_fim")
	@Temporal(TemporalType.DATE)
	private Date periodoFim;

	@JoinColumn(name = "id_tipo_participacao_organizacao_eventos", referencedColumnName = "id_tipo_participacao_organizacao_eventos")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoParticipacaoOrganizacaoEventos tipoParticipacaoOrganizacao = new TipoParticipacaoOrganizacaoEventos();

	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoRegiao ambito = new TipoRegiao();

	public ParticipacaoComissaoOrgEventos() {
	}

	@Override
	public String getTitulo() {
		if (getTipoParticipacaoOrganizacao()!= null && getTipoParticipacaoOrganizacao().getId() != 0)
			return getTipoParticipacaoOrganizacao().getDescricao();
		else
			return "Producao Intelectual sem T�tulo";
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
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

	public TipoParticipacaoOrganizacaoEventos getTipoParticipacaoOrganizacao() {
		return tipoParticipacaoOrganizacao;
	}

	public void setTipoParticipacaoOrganizacao(
			TipoParticipacaoOrganizacaoEventos tipoParticipacaoOrganizacao) {
		this.tipoParticipacaoOrganizacao = tipoParticipacaoOrganizacao;
	}

	public TipoRegiao getAmbito() {
		return ambito;
	}

	public void setAmbito(TipoRegiao ambito) {
		this.ambito = ambito;
	}

	public String getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(String veiculo) {
		this.veiculo = veiculo;
	}

	/*
	 * Campos Obrigatorios: Tipo Avaliacao Organizacao, Participacao, Veiculo,
	 * 						Local, Periodo Inicio e Fim
	 */

	@Override
	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequiredId(getAmbito().getId(), "Ambito", lista);
		ValidatorUtil.validateRequired(getLocal(),"Local de Divulga��o", lista);
		ValidatorUtil.validateRequiredId(getTipoParticipacaoOrganizacao() != null ? getTipoParticipacaoOrganizacao().getId() : 0, "Tipo Participa��o Organiza��o", lista);
		ValidatorUtil.validateRequired(getVeiculo(), "Ve�culo", lista);
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Periodo In�cio", lista);
		ValidatorUtil.validateRequired(getArea(), "�rea", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-�rea", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Refer�ncia", lista);
		ValidatorUtil.validateRequired(getDataProducao(), "Data de Produ��o", lista);
		return lista;
	}

	public String getItemView() {
		return " <td>"+getVeiculo()+ "</td>" +
			   " <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(periodoInicio)+" - "+ Formatador.getInstance().formatarData(periodoFim)+"</td>";

	}

	public String getTituloView() {
		return  " <td>Atividade</td>" +
				" <td style=\"text-align:center\">Data</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("veiculo", null);
		itens.put("periodoInicio", null);
		itens.put("periodoFim", null);
		return itens;
	}

	public float getQtdBase() {
		return 1;
	}

}
