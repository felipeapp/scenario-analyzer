/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/03/2007
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Entidade que armazena as informa��es referentes � vig�ncia de uma determinada
 * distribui��o de cotas de bolsas
 *
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name = "cota_bolsas", schema = "pesquisa", uniqueConstraints = {})
public class CotaBolsas implements Validatable {

	/** Chave prim�ria */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_cota_bolsas", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Descri��o do per�odo de cota */
	@Column(name = "descricao", unique = false, nullable =  false, insertable =  true, updatable =  true)
	private String descricao;
	
	/** In�cio do per�odo da cota */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = false, insertable = true, updatable = true)
	private Date inicio;

	/** Fim do per�odo da cota */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim", unique = false, nullable = false, insertable = true, updatable = true)
	private Date fim;

	/** Indica se o registro est� ativo ou se foi removido na aplica��o. */
	@CampoAtivo
	private boolean ativo;
	
	/** In�cio do per�odo de envio de relat�rios parciais para esta cota de bolsas */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_envio_relatorio_parcial")
	private Date inicioEnvioRelatorioParcial;
	
	/** Fim do per�odo de envio de relat�rios parciais para esta cota de bolsas */
	@Temporal(TemporalType.DATE)
	@Column(name = "fim_envio_relatorio_parcial")
	private Date fimEnvioRelatorioParcial;
	
	/** In�cio do per�odo de envio de relat�rios finais para esta cota de bolsas */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_envio_relatorio_final")
	private Date inicioEnvioRelatorioFinal;
	
	/** Fim do per�odo de envio de relat�rios finais para esta cota de bolsas */
	@Temporal(TemporalType.DATE)
	@Column(name = "fim_envio_relatorio_final")
	private Date fimEnvioRelatorioFinal;
	
	/** �rg�o financiador da cota de bolsas */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_entidade_financiadora")
	private EntidadeFinanciadora entidadeFinanciadora = new EntidadeFinanciadora();
	
	/** Modalidades das bolsas controladas nesta cota de bolsas. */
	private String modalidades;
	
	/** C�digo da cota de bolsas definido pelo gestor. */
	private String codigo;

	/** In�cio do per�odo de cadastro de planos volunt�rios */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_cadastro_plano_voluntario")
	private Date inicioCadastroPlanoVoluntario;
	
	/** Fim do per�odo de cadastro de planos volunt�rios */
	@Temporal(TemporalType.DATE)
	@Column(name = "fim_cadastro_plano_voluntario")
	private Date fimCadastroPlanoVoluntario;
	
	/** Construtor padr�o. */
	public CotaBolsas() {}

	public CotaBolsas(int id){
		this.id = id;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public Date getFim() {
		return fim;
	}

	public int getId() {
		return id;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public String getDescricaoCompleta() {
		return this.toString();
	}

	@Override
	public String toString() {
		Formatador f = Formatador.getInstance();
		if (inicio != null && fim != null) {
			return descricao + " (" + f.formatarData(inicio) + " a " +  f.formatarData(fim) + ")";
		} else {
			return descricao;
		}
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", lista);
		ValidatorUtil.validateRequired(inicio, "In�cio do Per�odo de Validade", lista);
		ValidatorUtil.validateRequired(fim, "Fim do Per�odo de Validade", lista);
		ValidatorUtil.validateRequired(modalidades, "Modalidades", lista);
		ValidatorUtil.validaInicioFim(inicio, fim, "Per�odo de Validade", lista);
		ValidatorUtil.validaInicioFim(inicioEnvioRelatorioParcial, fimEnvioRelatorioParcial, "Per�odo de Envio de Relat�rios Parciais", lista);
		ValidatorUtil.validaInicioFim(inicioEnvioRelatorioFinal, fimEnvioRelatorioFinal, "Per�odo de Envio de Relat�rios Finais", lista);
		return lista;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getInicioEnvioRelatorioParcial() {
		return inicioEnvioRelatorioParcial;
	}

	public void setInicioEnvioRelatorioParcial(Date inicioEnvioRelatorioParcial) {
		this.inicioEnvioRelatorioParcial = inicioEnvioRelatorioParcial;
	}

	public Date getFimEnvioRelatorioParcial() {
		return fimEnvioRelatorioParcial;
	}

	public void setFimEnvioRelatorioParcial(Date fimEnvioRelatorioParcial) {
		this.fimEnvioRelatorioParcial = fimEnvioRelatorioParcial;
	}

	public Date getInicioEnvioRelatorioFinal() {
		return inicioEnvioRelatorioFinal;
	}

	public void setInicioEnvioRelatorioFinal(Date inicioEnvioRelatorioFinal) {
		this.inicioEnvioRelatorioFinal = inicioEnvioRelatorioFinal;
	}

	public Date getFimEnvioRelatorioFinal() {
		return fimEnvioRelatorioFinal;
	}

	public void setFimEnvioRelatorioFinal(Date fimEnvioRelatorioFinal) {
		this.fimEnvioRelatorioFinal = fimEnvioRelatorioFinal;
	}

	public EntidadeFinanciadora getEntidadeFinanciadora() {
		return entidadeFinanciadora;
	}

	public void setEntidadeFinanciadora(EntidadeFinanciadora entidadeFinanciadora) {
		this.entidadeFinanciadora = entidadeFinanciadora;
	}

	public String getModalidades() {
		return modalidades;
	}

	public void setModalidades(String modalidades) {
		this.modalidades = modalidades;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Date getInicioCadastroPlanoVoluntario() {
		return inicioCadastroPlanoVoluntario;
	}

	public void setInicioCadastroPlanoVoluntario(Date inicioCadastroPlanoVoluntario) {
		this.inicioCadastroPlanoVoluntario = inicioCadastroPlanoVoluntario;
	}

	public Date getFimCadastroPlanoVoluntario() {
		return fimCadastroPlanoVoluntario;
	}

	public void setFimCadastroPlanoVoluntario(Date fimCadastroPlanoVoluntario) {
		this.fimCadastroPlanoVoluntario = fimCadastroPlanoVoluntario;
	}

}