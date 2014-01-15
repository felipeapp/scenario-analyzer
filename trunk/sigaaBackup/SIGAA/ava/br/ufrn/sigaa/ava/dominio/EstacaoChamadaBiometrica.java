/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 06/10/2009
 *
 */	
package br.ufrn.sigaa.ava.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa as turmas que podem ter chamada realizada 
 * através de biometria. 
 * 
 * @author agostinho campos
 */

@Entity 
@Table(name="estacao_chamada_bio", schema="ava")
public class EstacaoChamadaBiometrica implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_estacao_chamada_biometrica")
	private int id;
	
	private String ip;
	
	private String localizacao;

	private String mac;
	
	@OneToMany(mappedBy="estacaoChamadaBiometrica", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="id_estacao_chamada_biometrica")
	private List<EstacaoChamadaBiometricaTurma> estacaoChamadaBiometricaTurma = new ArrayList<EstacaoChamadaBiometricaTurma>(); 
	
	@Column(name="senha_estacao_bio")
	private String senhaEstacaoBio;
	
	public String getSenhaEstacaoBio() {
		return senhaEstacaoBio;
	}

	public void setSenhaEstacaoBio(String senhaEstacaoBio) {
		this.senhaEstacaoBio = senhaEstacaoBio;
	}

	public List<EstacaoChamadaBiometricaTurma> getEstacaoChamadaBiometricaTurma() {
		return estacaoChamadaBiometricaTurma;
	}

	public void setEstacaoChamadaBiometricaTurma(
			List<EstacaoChamadaBiometricaTurma> estacaoChamadaBiometricaTurma) {
		this.estacaoChamadaBiometricaTurma = estacaoChamadaBiometricaTurma;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

}
