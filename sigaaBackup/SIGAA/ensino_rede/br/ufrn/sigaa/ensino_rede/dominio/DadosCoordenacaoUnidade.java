package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import org.hibernate.annotations.CollectionOfElements;

@Embeddable
public class DadosCoordenacaoUnidade {

	@CollectionOfElements(fetch = FetchType.EAGER)
	@JoinTable(name="ensino_rede.coordenador_unidade_telefone", joinColumns= @JoinColumn (name="id_coordenador_unidade"))
	@Column(name = "telefone")
	private List<String> telefones;
	
	@Column(name = "email")
	private String email;
	
	public List<String> getTelefones() {
		return telefones;
	}

	public void setTelefones(List<String> telefones) {
		this.telefones = telefones;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
