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
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que registra as informações sobre visitas científicas realizadas por docentes da instituição
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "visita_cientifica",schema="prodocente")
@PrimaryKeyJoinColumn(name = "id_visita_cientifica")
public class VisitaCientifica extends Producao implements ViewAtividadeBuilder {

	@JoinColumn(name = "id_ies" , referencedColumnName ="id")
	@ManyToOne(fetch = FetchType.EAGER)
	private InstituicoesEnsino ies = new InstituicoesEnsino();

	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoRegiao ambito = new TipoRegiao();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_departamento")
	private Unidade departamento = new Unidade();

	@Column(name = "local")
	private String local;

	@Column(name = "periodo_inicio")
	@Temporal(TemporalType.DATE)
	private Date periodoInicio;

	@Column(name = "periodo_fim")
	@Temporal(TemporalType.DATE)
	private Date periodoFim;

	@Column(name = "financiamento_externo")
	private Boolean financiamentoExterno;

	@Column(name = "entidade")
	private String entidade;

	@Column (name = "valor")
	private Double valor;

	@Column(name = "instituicao_outro")
	private String instituicaoOutro;
	
	@Column(name = "departamento_outro")
	private String DepartamentoOutro;
	
	@Transient
	private boolean outraInstituicao = false;
		
	
	public Unidade getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	public TipoRegiao getAmbito() {
		return ambito;
	}

	public void setAmbito(TipoRegiao ambito) {
		this.ambito = ambito;
	}

	public Boolean getFinanciamentoExterno() {
		return financiamentoExterno;
	}

	public void setFinanciamentoExterno(Boolean financiamentoExterno) {
		this.financiamentoExterno = financiamentoExterno;
	}

	public InstituicoesEnsino getIes() {
		return ies;
	}

	public void setIes(InstituicoesEnsino ies) {
		this.ies = ies;
	}


	/**
	 * @return the entidade
	 */
	public String getEntidade() {
		return entidade;
	}

	/**
	 * @param entidade the entidade to set
	 */
	public void setEntidade(String entidade) {
		this.entidade = entidade;
	}

	/**
	 * @return the valor
	 */
	public Double getValor() {
		return valor;
	}

	/**
	 * @param valor the valor to set
	 */
	public void setValor(Double valor) {
		this.valor = valor;
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

	/*
	 * Campos Obrigatórios: Tipo Região, Instituição, Departamento, Local,
	 * 						Data, Instituição Financiadora
	 */

	@Override
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDataProducao(), "Data de Produção", lista);
		ValidatorUtil.validateRequired(getAmbito(),"Âmbito", lista);
		ValidatorUtil.validateRequired(getLocal(), "Local", lista);
		ValidatorUtil.validateRequired(getFinanciamentoExterno(), "Financiamento Externo", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Referência", lista);
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Período Início", lista);
		ValidatorUtil.validateRequired(getPeriodoFim(), "Período Fim", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		ValidatorUtil.validateRequired(getEntidade(), "Entidade", lista);
		ValidatorUtil.validateRequired(getValor(), "Valor", lista);
		if (isOutraInstituicao()) {
			ValidatorUtil.validateRequired(getInstituicaoOutro(), "Instituição Visitada", lista);
			// evitar erro de unsaved transient instance
			setIes(null);
		} else
			ValidatorUtil.validateRequiredId(getIes().getId(),"Instituição", lista);
		
		ValidatorUtil.validateRequiredId(getDepartamento().getId(),"Departamento", lista);
		if (getPeriodoInicio() != null && getPeriodoFim() != null){
			if (getPeriodoInicio().after(getPeriodoFim()))
				lista.addErro("O campo Período Início não pode ultrapassar o campo Período Fim");
			if (CalendarUtils.getMesByData(periodoInicio) > 11)
				lista.addErro("O campo Período Início possui mês inválido");
			if (CalendarUtils.getMesByData(periodoFim) > 11)
				lista.addErro("O campo Período Fim possui mês inválido");
		}
		
		//ValidatorUtil.validateRequired(getTipoParticipacao(), "Tipo de Participação", lista.getMensagens());
		return lista;
	}

	public String getInstituicaoOutro() {
		return instituicaoOutro;
	}

	public void setInstituicaoOutro(String instituicaoOutro) {
		this.instituicaoOutro = instituicaoOutro;
	}

	public String getDepartamentoOutro() {
		return DepartamentoOutro;
	}

	public void setDepartamentoOutro(String departamentoOutro) {
		DepartamentoOutro = departamentoOutro;
	}

	/**
	 * @param outraInstituicao the outraInstituicao to set
	 */
	public void setOutraInstituicao(boolean outraInstituicao) {
		this.outraInstituicao = outraInstituicao;
	}

	/**
	 * @return the outraInstituicao
	 */
	public boolean isOutraInstituicao() {
		return outraInstituicao;
	}

	@Override
	public String getItemView() {
		return " <td>"+getEntidade()+ "</td>" +
			   " <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(periodoInicio)+" - "+ Formatador.getInstance().formatarData(periodoFim)+"</td>";
	}

	@Override
	public String getTituloView() {
		return  " <td>Entidade</td>" +
				" <td style=\"text-align:center\">Data</td>";
	}

	@Override
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("entidade", null);
		itens.put("periodoInicio", null);
		itens.put("periodoFim", null);
		return itens;
	}

	@Override
	public float getQtdBase() {
		return 1;
	}


}