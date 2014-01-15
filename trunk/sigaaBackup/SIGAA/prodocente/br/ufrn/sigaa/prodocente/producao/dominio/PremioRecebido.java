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
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que contém as informações sobre prêmios recebidos por docentes da instituição
 *
 * @author Gleydson
 */
@Entity
@Table(name = "premio_recebido", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_premio_recebido")
public class PremioRecebido extends Producao implements ViewAtividadeBuilder {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_instituicao")
	private InstituicoesEnsino instituicao = new InstituicoesEnsino();

	@Column(name = "premio")
	private String premio;

	@Column(name = "data")
	@Temporal(TemporalType.DATE)
	private Date data;

	@Column(name = "destaque")
	private Boolean destaque;

	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoRegiao tipoRegiao = new TipoRegiao();

	/** Creates a new instance of PremioRecebido */
	public PremioRecebido() {
	}

	@Override
	public String getTitulo() {
		return getPremio();
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		setAnoReferencia(CalendarUtils.getAno(data));
		this.data = data;
	}

	public Boolean getDestaque() {
		return destaque;
	}

	public void setDestaque(Boolean destaque) {
		this.destaque = destaque;
	}

	public InstituicoesEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicoesEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public String getPremio() {
		return premio;
	}

	public void setPremio(String premio) {
		this.premio = premio;
	}

	public TipoRegiao getTipoRegiao() {
		return tipoRegiao;
	}

	public void setTipoRegiao(TipoRegiao tipoRegiao) {
		this.tipoRegiao = tipoRegiao;
	}

	/*
	 * Campos Obrigatorios: Instituicao, Premio, Tipo Regiao, Data
	 */

	@Override
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDataProducao(), "Data de Produção", lista);
		ValidatorUtil.validateRequired(getInstituicao(),"Instituição", lista);
		ValidatorUtil.validateRequiredId(getTipoRegiao().getId(),"Abrangência", lista);
		ValidatorUtil.validateRequired(getPremio(),"Prêmio", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Referência", lista);
		return lista;
	}

	public String getItemView() {
		return "  <td>"+getPremio()+ "</td>" +
			   "  <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(getDataProducao())+"</td>";

	}

	public String getTituloView() {
		return  "    <td>Atividade</td>" +
				"    <td style=\"text-align:center\">Data</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("premio", null);
		itens.put("dataProducao", null);
		return itens;
	}

	public float getQtdBase() {
		return 1;
	}

}