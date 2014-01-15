/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/07/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.TipoRedeEnsino;

/**
 * Dados referentes às Escolas (vindos da base de dados do INEP).
 *
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(name = "escola_inep", schema = "vestibular", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class EscolaInep implements PersistDB {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_escola")
	private int id;
	
	/** Nome da escola. */
	private String nome;
	
	/** Nome da escola sem acentos. */
	@Column(name = "nome_ascii")
	private String nomeAscii;
	
	/** Tipo da rede de ensino. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_rede_ensino")
	private TipoRedeEnsino tipoRedeEnsino;

	/** Zona da escola (true = Urbana, false = Rural). */
	@Column(name = "zona_urbana")
	private boolean zonaUrbana;

	/** Código da escola no senso do INEP. */
	@Column(name = "codigo_inep")
	private int codigoINEP;

	/** Endereço da escola. */
	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_endereco")
	private Endereco endereco;
	
	/** Código da área nacional de telefone fixo da escola. */
	@Column(name = "ddd")
	private Short codigoAreaNacionalTelefoneFixo;
	
	/** Telefone da escola. */
	private String telefone;
	
	/** Primeiro telefone público da escola. */
	@Column(name = "telefone_publico_1")
	private String telefonePublico1;
	
	/** Segundo telefone público da escola */
	@Column(name = "telefone_publico_2")
	private String telefonePublico2;
	
	/** Fax da escola. */
	private String fax;
	
	/** E-mail de contato da escola. */
	private String email;
	
	/** Construtor padrão. */
	public EscolaInep() {
		this.endereco = new Endereco();
		this.tipoRedeEnsino = new TipoRedeEnsino();
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;		
	}

	/** Retorna o nome da escola.
	 * @return nome da escola.
	 */
	public String getNome() {
		return nome;
	}

	/** Retorna o tipo da rede de ensino.
	 * @return tipo da rede de ensino
	 */
	public TipoRedeEnsino getTipoRedeEnsino() {
		return tipoRedeEnsino;
	}

	/** Indica se a escola pertence a zona urbana.
	 * @return Se true, a escola pertence a zona urbana. Se false, a escola pertence a zona rural.
	 */
	public boolean isZonaUrbana() {
		return zonaUrbana;
	}

	/** retorna o código da escola na base do INEP.
	 * @return código da escola no INETP
	 */
	public int getCodigoINEP() {
		return codigoINEP;
	}

	/** Retorna o endereço da escola.
	 * @return endereço da escola
	 */
	public Endereco getEndereco() {
		return endereco;
	}

	/** Retorna o código de área do telefone fixo.
	 * @return código de área do telefone fixo.
	 */
	public Short getCodigoAreaNacionalTelefoneFixo() {
		return codigoAreaNacionalTelefoneFixo;
	}

	/** Retorna o número do telefone.
	 * @return Número do telefone.
	 */
	public String getTelefone() {
		return telefone;
	}

	/** Retorna o número do primeiro telefone público instalado na escola.
	 * @return Número do telefone público.
	 */
	public String getTelefonePublico1() {
		return telefonePublico1;
	}

	/** Retorna o número do segundo telefone público instalado na escola.
	 * @return Número do telefone público.
	 */
	public String getTelefonePublico2() {
		return telefonePublico2;
	}

	/** Retorna o número do fax da escola.
	 * @return Número do fax da escola.
	 */
	public String getFax() {
		return fax;
	}

	/** Retorna o e-mail para contato da escola.
	 * @return E-mail de contato da escola.
	 */
	public String getEmail() {
		return email;
	}

	/** Retorna o nome da escola.
	 * @param Nome da escola.
	 */
	public void setNome(String nome) {
		this.nome = nome;
		setNomeAscii(nome);
	}

	/** Seta o tipo da rede de ensino da escola.
	 * @param tipoRedeEnsino
	 */
	public void setTipoRedeEnsino(TipoRedeEnsino tipoRedeEnsino) {
		this.tipoRedeEnsino = tipoRedeEnsino;
	}

	/** Seta se a escola pertence à zona urbana. True, caso pertença. False, caso contrário.
	 * @param zonaUrbana
	 */
	public void setZonaUrbana(boolean zonaUrbana) {
		this.zonaUrbana = zonaUrbana;
	}

	/** Seta o código da escola no INEP.
	 * @param codigoINEP
	 */
	public void setCodigoINEP(int codigoINEP) {
		this.codigoINEP = codigoINEP;
	}

	/** Seta o endereço da escola.
	 * @param endereco
	 */
	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	/** Seta o código de área do telefone fixo.
	 * @param codigoAreaNacionalTelefoneFixo
	 */
	public void setCodigoAreaNacionalTelefoneFixo(
			Short codigoAreaNacionalTelefoneFixo) {
		this.codigoAreaNacionalTelefoneFixo = codigoAreaNacionalTelefoneFixo;
	}

	/** Seta o telefone da escola.
	 * @param telefone
	 */
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	/** Seta o número do primeiro telefone público da escola.
	 * @param telefonePublico1
	 */
	public void setTelefonePublico1(String telefonePublico1) {
		this.telefonePublico1 = telefonePublico1;
	}

	/** Seta o número do segundo telefone público da escola.
	 * @param telefonePublico2
	 */
	public void setTelefonePublico2(String telefonePublico2) {
		this.telefonePublico2 = telefonePublico2;
	}

	/** Seta o número do fax da escola.
	 * @param fax
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	/** Seta o e-mail de contato da escola.
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/** Converte o objeto para uma String no formato: id seguido de vírgula, seguido do nome,
	 * seguido de parênteses, seguido do nome do município, seguido da sigla da UF, seguido de parênteses.
	 */
	@Override
	public String toString() {
		String municipio = !ValidatorUtil.isEmpty(endereco.getMunicipio()) ?  endereco.getMunicipio().getNome() : "";
		return id + ", " + nome + "(" + municipio + ", "
				+ endereco.getUnidadeFederativa().getSigla() + ")";
	}

	/** Retorna o nome da escola sem acentos. 
	 * @return
	 */
	public String getNomeAscii() {
		return nomeAscii;
	}

	/** Seta o nome da escola sem acentos. 
	 * @param nomeAscii
	 */
	public void setNomeAscii(String nomeAscii) {
		if ( nomeAscii != null)
			this.nomeAscii = StringUtils.toAscii(nomeAscii.toUpperCase());
	}
}
