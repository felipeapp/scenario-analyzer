package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import br.ufrn.sigaa.dominio.Telefone;

/**
 * Classe guarda os dados do usuário enquanto Coordenador de Unidade.
 * 
 */
@Embeddable
public class DadosCoordenacaoUnidade {

	/**Telefones da coordenação.*/
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name="ensino_rede.coordenador_unidade_telefone", joinColumns={@JoinColumn(name="id_coordenador_unidade")}, inverseJoinColumns={@JoinColumn(name="id_telefone")})
	private List<Telefone> telefones = new ArrayList<Telefone>();
	
	/**E-mail do Coordenador*/
	@Column(name = "email")
	private String email = new String();
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Telefone> getTelefones() {
		return telefones;
	}

	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}

}
