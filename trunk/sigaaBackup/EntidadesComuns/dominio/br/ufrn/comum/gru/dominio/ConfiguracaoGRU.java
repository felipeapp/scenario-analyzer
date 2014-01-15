/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 11/05/2011
 *
 */
package br.ufrn.comum.gru.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.comum.dominio.UnidadeGeral;

/**
 * Configura��oo de parametros padr�es a serem utilizados na gera��o de uma GRU.<br/>
 * Esta classe define um conjunto de valores padr�es de:
 * <ul>
 * <li>tipo de GRU a ser gerado (simples ou cobran�a)</li>
 * <li>tipo de arrecada��o</li>
 * <li>c�digo de recolhimento (GRU simples)</li>
 * <li>gest�o (GRU simples)</li>
 * <li>ag�ncia (GRU cobran�a)</li>
 * <li>c�digo do cedente (GRU cobran�a)</li>
 * </ul>
 * c�digo de recolhimento a serem utilizados na gera��o de GRU como, por
 * exemplo, inscri��es em Processos Seletivos.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "configuracao_gru", schema = "gru")
public class ConfiguracaoGRU implements Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="gru.gru_sequence") })
	@Column(name = "id_configuracao_gru")
	private int id;
	
	/** Indica se o modelo da GRU a ser gerada dever� ser Simples. Caso contr�rio, ser� gerado uma GRU Cobran�a. */ 
	@Column(name = "gru_simples")
	private boolean gruSimples = true;
	
	/** Indica se esta configura��o est� ativa para utiliza��o nos sistemas. */
	@CampoAtivo
	private boolean ativo = true;
	
	/** Data de cadastro desta configura��o. */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	// dados comuns da GRU
	/** Tipo de arrecada��o da GRU a ser gerada. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_arrecadacao")
	private TipoArrecadacao tipoArrecadacao;

	/** Descri��o textual da configura��o da GRU. */ 
	private String descricao;
	
	/** Unidade de Custo que ir� receber os cr�ditos desta GRU. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade")
	private UnidadeGeral unidade;
	
	/** Cole��o de GRUs geradas por esta configura��o. */
	@OneToMany(mappedBy="configuracaoGRU",fetch = FetchType.LAZY)
	private List<GuiaRecolhimentoUniao> grus;
	
	/** Grupo de GRU ao qual pertence esta configura��o. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_grupo_emissao_gru")
	private GrupoEmissaoGRU grupoEmissaoGRU;
	
	public ConfiguracaoGRU() {
		this.grupoEmissaoGRU = new GrupoEmissaoGRU();
		this.tipoArrecadacao = new TipoArrecadacao();
		this.unidade = new UnidadeGeral();
	}
	
	public ConfiguracaoGRU(int id) {
		super();
		this.id = id;
	}

	@Override
	public String toString() {
		return id + "-" + (gruSimples ? "GRU Simples" : "GRU Cobran�a") + ": "
				+ tipoArrecadacao.getDescricao() + ": " + descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isGruSimples() {
		return gruSimples;
	}

	public void setGruSimples(boolean gruSimples) {
		this.gruSimples = gruSimples;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public TipoArrecadacao getTipoArrecadacao() {
		return tipoArrecadacao;
	}

	public void setTipoArrecadacao(TipoArrecadacao tipoArrecadacao) {
		this.tipoArrecadacao = tipoArrecadacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public UnidadeGeral getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadeGeral unidade) {
		this.unidade = unidade;
	}

	public void setGrus(List<GuiaRecolhimentoUniao> grus) {
		this.grus = grus;
	}

	public List<GuiaRecolhimentoUniao> getGrus() {
		return grus;
	}

	public GrupoEmissaoGRU getGrupoEmissaoGRU() {
		return grupoEmissaoGRU;
	}

	public void setGrupoEmissaoGRU(GrupoEmissaoGRU grupoEmissaoGRU) {
		this.grupoEmissaoGRU = grupoEmissaoGRU;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(unidade, "Unidade", lista);
		validateRequired(tipoArrecadacao, "Tipo de Arrecada��o", lista);
		if (grupoEmissaoGRU != null) {
			if (gruSimples) {
				validateRequired(grupoEmissaoGRU.getCodigoGestao(), "C�digo da Gest�o", lista);
				validateRequired(grupoEmissaoGRU.getCodigoUnidadeGestora(), "C�digo da Unidade Gestora", lista);
			} else {
				validateRequired(grupoEmissaoGRU.getAgencia(), "Ag�ncia", lista);
				validateRequired(grupoEmissaoGRU.getCodigoCedente(), "C�digo Cedente", lista);
				validateRequired(grupoEmissaoGRU.getConvenio(), "N�mero do Conv�nio", lista);
			}
		} else {
			validateRequired(grupoEmissaoGRU, "Grupo de Emiss�o da GRU", lista);
		}
		return lista;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfiguracaoGRU other = (ConfiguracaoGRU) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	/**
	 * Total Emitidos por configura��o 
	 * @return
	 */
	public double getTotalEmitido(){
		double total = 0;
		for (GuiaRecolhimentoUniao gru : grus)
			total += gru.getValorTotal();
		
		return total;
	}
	
	/**
	 * Total Pago por configura��o 
	 * @return
	 */
	public double getTotalPago(){
		double total = 0;
		for (GuiaRecolhimentoUniao gru : grus)
			total += gru.getValorPago() == null ? 0 : gru.getValorPago();
		
		return total;
	}
	
}
