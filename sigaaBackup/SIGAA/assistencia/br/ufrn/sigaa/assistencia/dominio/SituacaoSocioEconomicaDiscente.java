/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 02/01/2009
 *
 */	
package br.ufrn.sigaa.assistencia.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que agrega as informações da situação sócio-econômica 
 * de um discente.
 * 
 * @author wendell
 *
 */
@Entity 
@Table(name="situacao_socio_economica_discente", schema="sae")
public class SituacaoSocioEconomicaDiscente  implements Validatable {

	public static final double SALARIO_MINIMO = ParametroHelper.getInstance().getParametroDouble(ConstantesParametro.SALARIO_MINIMO);
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id_situacao_socio_economica_discente")
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_discente")
	private Discente discente;
	
	/** Renda familiar do discente (em Reais). */
	@Column(name="renda_familiar")
	private Double rendaFamiliar;
	
	/** Quantidade de membros do grupo familiar do discente. */
	@Column(name="quantidade_membros_familia")
	private Integer quantidadeMembrosFamilia;
	
	/** Data em que as informações foram cadastradas no sistema. */
	@CriadoEm
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	/** Registro de entrada do usuário que cadastrou as informações. */
	@CriadoPor
	@ManyToOne
	@JoinColumn(name="id_registro_entrada")
	private RegistroEntrada registroCadastro;

	public SituacaoSocioEconomicaDiscente() {
		
	}
	
	public SituacaoSocioEconomicaDiscente(Discente discente) {
		this.discente = discente;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Double getRendaFamiliar() {
		return rendaFamiliar;
	}

	public void setRendaFamiliar(Double rendaFamiliar) {
		this.rendaFamiliar = rendaFamiliar;
	}

	public Integer getQuantidadeMembrosFamilia() {
		return quantidadeMembrosFamilia;
	}

	public void setQuantidadeMembrosFamilia(Integer quantidadeMembrosFamilia) {
		this.quantidadeMembrosFamilia = quantidadeMembrosFamilia;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}
 
	/**
	 * Retorna a renda per capita da família do discente
	 * 
	 * @return
	 */
	public Double getRendaPerCapita() {
		if (isEmpty(quantidadeMembrosFamilia) || isEmpty(rendaFamiliar))
			return 0.0;
		
		return rendaFamiliar / quantidadeMembrosFamilia;
	}

	/* 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		//Collection<MensagemAviso> lista = new ArrayList<MensagemAviso>();
		ListaMensagens lista = new ListaMensagens();
		validateRequired(discente, "Discente", lista);
		
		validateRequired(rendaFamiliar, "Renda familiar", lista);
		validateMinValue(rendaFamiliar, 0.0, "Renda familiar", lista);
		validateMaxValue(rendaFamiliar, 999999999999.0, "Renda familiar", lista);

		validateRequired(quantidadeMembrosFamilia, "Quantidade de membros do grupo familiar", lista);
		validateMinValue(quantidadeMembrosFamilia, 1, "Quantidade de membros do grupo familiar", lista);
		
		return lista;
	}
	
}
