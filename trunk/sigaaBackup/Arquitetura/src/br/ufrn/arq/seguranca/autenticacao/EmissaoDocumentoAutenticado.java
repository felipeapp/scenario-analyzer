/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2007
 */
package br.ufrn.arq.seguranca.autenticacao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Esta classe é persistida toda vez que um documento autenticado for emitido no
 * sistema.
 * 
 * @author Gleydson Lima
 * 
 */
@Entity
@Table(name = "emissao_documento_autenticado", schema="comum")
public class EmissaoDocumentoAutenticado implements PersistDB {
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_emissao", nullable = false)
	private int id;

	/** Tipo de documento sendo emitido */
	@Column(name = "tipo_documento")
	private int tipoDocumento;

	@Column(name = "sub_tipo_documento")
	private Integer subTipoDocumento;

	/**
	 * Informação utilizada como base para a digitação do usuário. Como por
	 * exemplo a matrícula. Em alguns casos, devido a impossibilidade de
	 * unicidade de matrícula, usa-se dados gravados em dadosAuxiliares para
	 * realizar a busca no banco.
	 */
	private String identificador;

	/**
	 * Código de segurança para que os usuários possam acessar o documento no
	 * sistema
	 */
	@Column(name = "codigo_seguranca")
	private String codigoSeguranca;

	/** Data da emissão do documento autenticado. */
	@Column(name = "data_emissao")
	private Date dataEmissao;

	/** Hora da emissão do documento autenticado. */
	@Column(name = "hora_emissao")
	private Date horaEmissao = new Date();

	/**
	 * Armazena um dado auxiliar para a busca da emissão, como por exemplo o
	 * id_discente da emissão. Este dado não é conhecido pelo usuário, que
	 * digita a matrícula, mas é usado na recuperação do documento caso ele seja
	 * validado.
	 */
	@Column(name = "auxiliar")
	private String dadosAuxiliares;

	/**
	 * Usado para que se dois documentos forem emitidos no mesmo dia gerar hahs
	 * diferentes
	 */
	private String prng;

	/**
	 * Criado nos casos de certificados onde é necessário um número de documento
	 */
	@Column(name = "numero_documento")
	private String numeroDocumento;

	/**
	 * Se este boolean tiver setado, o sistema procura buscar uma emissão com um
	 * número para gerar um novo registro ao invés de uma nova emissão.
	 */
	@Transient
	private boolean emissaoDocumentoComNumero;
	
	/**
	 * Verifica se o subtipo de documento é do tipo declaração de pensionista.
	 * @return
	 */
	public boolean isPensionista () {
		if (!ValidatorUtil.isEmpty(subTipoDocumento)){
			return subTipoDocumento == SubTipoDocumentoAutenticado.DECLARACAO_PENSIONISTA_ESPECIAL;
		}
		return false;
	}

	public String getCodigoSeguranca() {
		return codigoSeguranca;
	}

	public void setCodigoSeguranca(String codigoSeguranca) {
		this.codigoSeguranca = codigoSeguranca;
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public int getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(int tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public Date getHoraEmissao() {
		return horaEmissao;
	}

	public void setHoraEmissao(Date horaEmissao) {
		this.horaEmissao = horaEmissao;
	}

	public String getPrng() {
		return prng;
	}

	public void setPrng(String prng) {
		this.prng = prng;
	}

	public String getDadosAuxiliares() {
		return dadosAuxiliares;
	}

	public void setDadosAuxiliares(String auxiliar) {
		this.dadosAuxiliares = auxiliar;
	}

	public Integer getSubTipoDocumento() {
		return subTipoDocumento;
	}

	public void setSubTipoDocumento(Integer subTipoDocumento) {
		this.subTipoDocumento = subTipoDocumento;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public boolean isEmissaoDocumentoComNumero() {
		return emissaoDocumentoComNumero;
	}

	public void setEmissaoDocumentoComNumero(boolean emissaoDocumentoComNumero) {
		this.emissaoDocumentoComNumero = emissaoDocumentoComNumero;
	}

}
