/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/10/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/*******************************************************************************
 * <p>
 * Representa o local de realização da ação de extensão. Algumas ações devem
 * informar o local de realização de suas atividades, como projetos, cursos e
 * eventos. <br/>
 * 
 * Exemplos de locais de realização: Centro de convenções de Natal, Auditório da
 * reitoria da UFRN, etc.
 * </p>
 * 
 * @author Gleydson
 * @author Victor Hugo
 ******************************************************************************/
@SuppressWarnings("serial")
@Entity
@Table(schema = "extensao", name = "local_realizacao")
public class LocalRealizacao implements Validatable {

    /** identificador do local de realização */
    @Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_local_realizacao", unique = true, nullable = false)
	private int id;

    /** Descrição do espaço de realização da ação de extensão. */
	@Column(name = "descricao")
	private String descricao;

	/** Município onde a ação será realizada. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_municipio", unique = false, nullable = true, insertable = true, updatable = true)
	private Municipio municipio = new Municipio();

	/** Utilizado em ações de extensão com abrangência internacional. */
	@Column(name = "municipio_internacional")
	private String municipioInternacional;
	
	/** Determina se o registro está ativo no sistema. */
	@Column(name = "ativo")
	private boolean ativo;

	/** Município onde a ação será realizada. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atividade")
    private AtividadeExtensao atividade;

	/** Bairro que vai ocorrer o projeto */
	private String bairro;
    
	/** Armazena a latitude de realização da ação de extensão */
	private String latitude;
	
	/** Armazena a longitude de realização da ação de extensão */
	private String longitude;
	
	public LocalRealizacao() {
	}

	public LocalRealizacao(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return Returns the municipio.
	 */
	public Municipio getMunicipio() {
		return municipio;
	}

	/**
	 * @param municipio
	 *            The municipio to set.
	 */
	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public String toString() {
		return "br.ufrn.sigaa.extensao.dominio.LocalRealizacao[id=" + id + "]";
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		if (municipioInternacional == null || "".equalsIgnoreCase(municipioInternacional.trim())) {
			ValidatorUtil.validateRequired(municipio, "Município", lista);
		}
		return lista;
	}
	
	/** Informa o nome do município do local de 
	 * realização independente da abrangência (nacional ou internacional). */
	public String getMunicipioString() {
		if (ValidatorUtil.isNotEmpty(municipioInternacional)) {
			return municipioInternacional;
		}else {
			if (ValidatorUtil.isNotEmpty(municipio)) {
				return municipio.getNomeUF();
			}
		}		
		return null;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getMunicipioInternacional() {
		return municipioInternacional;
	}

	public void setMunicipioInternacional(String municipioInternacional) {
		this.municipioInternacional = municipioInternacional;
	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "municipio.id", "bairro", "municipio.unidadeFederativa.id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(municipio.getId(), bairro, municipio.getUnidadeFederativa().getId());
	}

}