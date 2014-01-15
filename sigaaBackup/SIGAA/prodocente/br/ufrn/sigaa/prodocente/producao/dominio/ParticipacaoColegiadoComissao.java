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
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que registra a participação de um servidor em um colegiado ou comissão
 *
 * @author Gleydson
 */
@Entity
@Table(name = "colegiado_comissao", schema="prodocente")
@PrimaryKeyJoinColumn(name="id_colegiado_comissao")
public class ParticipacaoColegiadoComissao extends Producao implements ViewAtividadeBuilder {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_departamento")
	private Unidade departamento;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ies" ,referencedColumnName="id")
	private InstituicoesEnsino instituicao =  new InstituicoesEnsino();

	@Column(name = "periodo_inicio")
	@Temporal(TemporalType.DATE)
	private Date periodoInicio;

	@Column(name = "periodo_fim")
	@Temporal(TemporalType.DATE)
	private Date periodoFim;

	@Column(name = "comissao")
	private String comissao;

	@Column(name = "nato")
	private Boolean nato;

	@Column(name = "numero_reunioes")
	private Integer numeroReunioes;

	@JoinColumn(name = "id_tipo_comissao_colegiado", referencedColumnName = "id_tipo_comissao_colegiado")
	@ManyToOne
	private TipoComissaoColegiado tipoComissaoColegiado = new TipoComissaoColegiado();

	@JoinColumn(name = "id_tipo_membro_colegiado", referencedColumnName = "id_tipo_membro_colegiado")
	@ManyToOne
	private TipoMembroColegiado tipoMembroColegiado = new TipoMembroColegiado();

	/** Creates a new instance of ColegiadoComissao */
	public ParticipacaoColegiadoComissao() {
	}

	@Override
	public String getTitulo() {
		return getComissao();
	}

	public String getComissao() {
		return comissao;
	}

	public void setComissao(String comissao) {
		this.comissao = comissao;
	}

	public Unidade getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	public InstituicoesEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicoesEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public Boolean getNato() {
		return nato;
	}

	public void setNato(Boolean nato) {
		this.nato = nato;
	}

	public Integer getNumeroReunioes() {
		return numeroReunioes;
	}

	public void setNumeroReunioes(Integer numeroReunioes) {
		this.numeroReunioes = numeroReunioes;
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

	public TipoComissaoColegiado getTipoComissaoColegiado() {
		return tipoComissaoColegiado;
	}

	public void setTipoComissaoColegiado(
			TipoComissaoColegiado tipoComissaoColegiado) {
		this.tipoComissaoColegiado = tipoComissaoColegiado;
	}

	public TipoMembroColegiado getTipoMembroColegiado() {
		return tipoMembroColegiado;
	}

	public void setTipoMembroColegiado(TipoMembroColegiado tipoMembroColegiado) {
		this.tipoMembroColegiado = tipoMembroColegiado;
	}

	/*
	 * Campos Obrigatórios: Período Data Inicio, Data Fim (caso comissão não seja permanente), 
	 * 	Tipo Membro Colegiado, Tipo Comissão Colegiado, Instituição, Departamento
	 */
	@Override
	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getComissao(), "Colegiado/Comissão", lista);
		ValidatorUtil.validateMaxLength(getComissao(), 255, "Colegiado/Comissão", lista);
		ValidatorUtil.validateRequired(getInstituicao(), "Instituição", lista);
		ValidatorUtil.validateRequiredId(getTipoComissaoColegiado().getId(),"Natureza", lista);
		ValidatorUtil.validateRequiredId(getTipoMembroColegiado().getId(),"Tipo de Participação", lista);
		//ValidatorUtil.validateRequiredId(getDepartamento().getId(), "Departamento", lista);
		ValidatorUtil.validateRequired(getDataProducao(), "Data da publicação", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de referência", lista);
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Periodo Inicio", lista);
		// Campo data fim sem obrigatoriedade apenas para Comissão Permanente
		if (getTipoComissaoColegiado().getId() != TipoComissaoColegiado.COMISSAO_PERMANENTE)
			ValidatorUtil.validateRequired(getPeriodoFim(), "Período Fim", lista);
		return lista;
	}

	public String getItemView() {
		return "  <td>" + getComissao() + "</td>" +
			   "  <td style=\"text-align:center\">" + Formatador.getInstance().formatarData(periodoInicio) 
			   + " - " + Formatador.getInstance().formatarData(periodoFim) + "</td>" +
			   "  <td style=\"text-align:right\">" + (getNumeroReunioes() != null ? getNumeroReunioes() : 0) 
			   + "</td>";
	}

	public String getTituloView() {
		return  "    <td>Atividade</td>" +
				"    <td style=\"text-align:center\">Período</td>" +
				"	 <td style=\"text-align:right\">Reuniões</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("comissao", null);
		itens.put("periodoInicio", null);
		itens.put("periodoFim", null);
		itens.put("numeroReunioes", null);
		return itens;
	}

	public float getQtdBase() {
		return CalendarUtils.calculoMeses(periodoInicio, periodoFim);
	}
}
