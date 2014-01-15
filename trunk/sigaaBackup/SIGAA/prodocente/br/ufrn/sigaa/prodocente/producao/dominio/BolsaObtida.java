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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.prodocente.atividades.dominio.InstituicaoFomento;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoBolsaProdocente;

/**
 * Bolsa remunerada obtida por um servidor
 *
 * @author eric
 */
@Entity
@Table(name = "bolsa_obtida", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_bolsa_obtida")
public class BolsaObtida extends Producao {

	@Column(name = "instituicao")
	private String instituicao;

	@Column(name = "data_cadastro")
	private Date dataCadastro;

	@Column(name = "periodo_inicio")
	@Temporal(TemporalType.DATE)
	private Date periodoInicio;

	@Column(name = "periodo_fim")
	@Temporal(TemporalType.DATE)
	private Date periodoFim;

	@Column(name = "informacao")
	private String informacao;

	@JoinColumn(name = "id_tipo_bolsa", referencedColumnName = "id_tipo_bolsa")
	@ManyToOne
	private TipoBolsaProdocente tipoBolsaProdocente;

	@ManyToOne
	@JoinColumn(name = "id_instituicao_fomento", referencedColumnName = "id_instituicao_fomento")
	private InstituicaoFomento instituicaoFomento = new InstituicaoFomento();

	/** Creates a new instance of BolsaObtida */
	public BolsaObtida() {
	}

	/**
	 * Gets the instituicao of this BolsaObtida.
	 *
	 * @return the instituicao
	 */
	public String getInstituicao() {
		return this.instituicao;
	}

	/**
	 * Sets the instituicao of this BolsaObtida to the specified value.
	 *
	 * @param instituicao
	 *            the new instituicao
	 */
	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}

	@Override
	public Date getDataCadastro() {
		return this.dataCadastro;
	}

	@Override
	public void setDataCadastro(Date datacadastro) {
		this.dataCadastro = periodoInicio;
	}

	/**
	 * Gets the periodoInicio of this BolsaObtida.
	 *
	 * @return the periodoInicio
	 */
	public Date getPeriodoInicio() {
		return this.periodoInicio;
	}

	/**
	 * Sets the periodoInicio of this BolsaObtida to the specified value.
	 *
	 * @param periodoInicio
	 *            the new periodoInicio
	 */
	public void setPeriodoInicio(Date periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	/**
	 * Gets the periodoFim of this BolsaObtida.
	 *
	 * @return the periodoFim
	 */
	public Date getPeriodoFim() {
		return this.periodoFim;
	}

	/**
	 * Sets the periodoFim of this BolsaObtida to the specified value.
	 *
	 * @param periodoFim
	 *            the new periodoFim
	 */
	public void setPeriodoFim(Date periodoFim) {
		this.periodoFim = periodoFim;
	}


	/**
	 * Gets the informacao of this BolsaObtida.
	 *
	 * @return the informacao
	 */
	@Override
	public String getInformacao() {
		return this.informacao;
	}

	/**
	 * Sets the informacao of this BolsaObtida to the specified value.
	 *
	 * @param informacao
	 *            the new informacao
	 */
	@Override
	public void setInformacao(String informacao) {
		this.informacao = informacao;
	}

	/**
	 * Gets the tipoBolsaProdocente of this BolsaObtida.
	 *
	 * @return the tipoBolsaProdocente
	 */
	public TipoBolsaProdocente getTipoBolsa() {
		return this.tipoBolsaProdocente;
	}

	/**
	 * Sets the tipoBolsaProdocente of this BolsaObtida to the specified value.
	 *
	 * @param tipoBolsaProdocente
	 *            the new tipoBolsaProdocente
	 */
	public void setTipoBolsa(TipoBolsaProdocente tipoBolsaProdocente) {
		this.tipoBolsaProdocente = tipoBolsaProdocente;
	}

	/**
	 * @return the instituicaoFomento
	 */
	public InstituicaoFomento getInstituicaoFomento() {
		return instituicaoFomento;
	}

	/**
	 * @param instituicaoFomento
	 *            the instituicaoFomento to set
	 */
	public void setInstituicaoFomento(InstituicaoFomento instituicaoFomento) {
		this.instituicaoFomento = instituicaoFomento;
	}

	/**
	 * @return the tipoBolsaProdocente
	 */
	public TipoBolsaProdocente getTipoBolsaProdocente() {
		return tipoBolsaProdocente;
	}

	/**
	 * @param tipoBolsaProdocente
	 *            the tipoBolsaProdocente to set
	 */
	public void setTipoBolsaProdocente(TipoBolsaProdocente tipoBolsaProdocente) {
		this.tipoBolsaProdocente = tipoBolsaProdocente;
	}

	/**
	 * Returns a string representation of the object. This implementation
	 * constructs that representation based on the id fields.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		return getTitulo();
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Referência", lista);
		ValidatorUtil.validateRequiredId(getServidor().getId(), "Docente", lista);
		ValidatorUtil.validateRequiredId(getTipoBolsaProdocente().getId(), "Tipo de Bolsa", lista);
		ValidatorUtil.validateRequired(getInstituicaoFomento(), "Instituicao Fomento", lista);
		ValidatorUtil.validateRequired(getArea(), "Área de Conhecimento", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Início do Período",	lista);
		ValidatorUtil.validateRequired(getPeriodoFim(), "Fim do Período", lista);
		ValidatorUtil.validateRequired(getDataProducao(), "Data da Produção", lista);

		return lista;

	}

	@Override
	public String getTitulo() {
		return tipoBolsaProdocente.getDescricao();
	}
}
