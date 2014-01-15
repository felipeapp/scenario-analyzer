package br.ufrn.sigaa.bolsas.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.bolsas.negocio.IntegracaoTipoBolsaHelper;

/**
 * Entidade que a integração dos tipos de bolsa do sigaa para o sipac
 * 
 * @author Jean Guerethes
 */
@Entity
@Table(name = "integracao_tipo_bolsa", schema="comum")
public class IntegracaoTipoBolsa implements Validatable {

	@Id
	@Column(name = "id_tipo_bolsa", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;

	@Column(name="id_bolsa_sigaa")
	private int idBolsaSigaa;
	
	@Column(name="id_bolsa_sipac")
	private int idBolsaSipac;
	
	private String uf;
	
	private String municipio;

	@CampoAtivo(true)
	private boolean ativo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdBolsaSigaa() {
		return idBolsaSigaa;
	}

	public void setIdBolsaSigaa(int idBolsaSigaa) {
		this.idBolsaSigaa = idBolsaSigaa;
	}

	public int getIdBolsaSipac() {
		return idBolsaSipac;
	}

	public void setIdBolsaSipac(int idBolsaSipac) {
		this.idBolsaSipac = idBolsaSipac;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getDescricaoBolsaSigaa() throws DAOException {
		return IntegracaoTipoBolsaHelper.bolsasSigaa().get(idBolsaSigaa);
	}

	public String getDescricaoBolsaSipac() {
		return IntegracaoTipoBolsaHelper.bolsasSipac().get(idBolsaSipac);
	}
	
	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
	
}