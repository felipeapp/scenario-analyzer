/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/03/2009
 *
 */	
package br.ufrn.sigaa.assistencia.cadunico.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;

/**
 * Informação sobre a localização da família do discente, que realizou o cadastro único.
 * 
 * @author Henrique André
 *
 */
@Entity
@Table(name = "contato_familia", schema = "sae")
public class ContatoFamilia implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_contato")
	private int id;
	
	/** Endereço da família */
	@ManyToOne()
	@JoinColumn(name = "id_endereco")
	private Endereco endereco;

	/** Código de área do telefone fixo. */
	@Column(name = "cod_fixo")
	private Short codigoAreaNacionalTelefoneFixo;

	/** Código de área do telefone celular. */
	@Column(name = "cod_celular")
	private Short codigoAreaNacionalTelefoneCelular;

	/** Número de telefone fixo. */
	@Column(name = "numero_fixo")
	private String telefone;

	/** Número de telefone celular. */
	@Column(name = "numero_telefone")
	private String celular;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public Short getCodigoAreaNacionalTelefoneFixo() {
		return codigoAreaNacionalTelefoneFixo;
	}

	public void setCodigoAreaNacionalTelefoneFixo(
			Short codigoAreaNacionalTelefoneFixo) {
		this.codigoAreaNacionalTelefoneFixo = codigoAreaNacionalTelefoneFixo;
	}

	public Short getCodigoAreaNacionalTelefoneCelular() {
		return codigoAreaNacionalTelefoneCelular;
	}

	public void setCodigoAreaNacionalTelefoneCelular(
			Short codigoAreaNacionalTelefoneCelular) {
		this.codigoAreaNacionalTelefoneCelular = codigoAreaNacionalTelefoneCelular;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public void clear() {
	
		int cepPadrao =  ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.CEP_PADRAO );
		int dddPadrao = ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.DDD_PADRAO );
		
		if (getEndereco() == null || this.getEndereco().getId() == 0) {
			setEndereco(new Endereco());
			getEndereco().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
			getEndereco().setCep(cepPadrao + "");
			getEndereco().setTipoLogradouro(new TipoLogradouro(TipoLogradouro.RUA));
			setCodigoAreaNacionalTelefoneCelular((short) dddPadrao);
			setCodigoAreaNacionalTelefoneFixo((short) dddPadrao);
			getEndereco().setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
		}
		
		if (getEndereco() != null && getEndereco().getUnidadeFederativa() == null) {
			this.getEndereco().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		}
		
		if (getEndereco() != null && getEndereco().getMunicipio() == null) {
			this.getEndereco().setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
		}
	}	
	
}
