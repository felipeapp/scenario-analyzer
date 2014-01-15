/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Pasta para organizar os arquivos
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "pasta_arquivos", schema = "ava")
public class PastaArquivos implements PersistDB, Comparable<PastaArquivos> {

	public static final String RAIZ = "Meus Arquivos";
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_pasta_arquivos", nullable = false)
	private int id;

	private String nome;

	private Date data;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pasta_superior")
	private PastaArquivos pai;

	@OneToMany(mappedBy="pai", cascade=CascadeType.ALL)
	private List<PastaArquivos> filhos;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_usuario")
	private Usuario usuario;
	
	@OneToMany(mappedBy="pasta", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<ArquivoUsuario> arquivos;

	public int compareTo(PastaArquivos other) {
		return this.nome.compareTo(other.nome);
	}

	public int getIdPai() {
		if (pai == null)
			return -1;
		return pai.getId();
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
		PastaArquivos other = (PastaArquivos) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public PastaArquivos getPai() {
		return pai;
	}

	public void setPai(PastaArquivos pai) {
		this.pai = pai;
	}

	public List<PastaArquivos> getFilhos() {
		return filhos;
	}

	public void setFilhos(List<PastaArquivos> filhos) {
		this.filhos = filhos;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<ArquivoUsuario> getArquivos() {
		return arquivos;
	}

	public void setArquivos(List<ArquivoUsuario> arquivos) {
		this.arquivos = arquivos;
	}
	
}
