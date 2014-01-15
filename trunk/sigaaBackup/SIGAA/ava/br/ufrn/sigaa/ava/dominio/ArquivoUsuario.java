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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Arquivos submetidos pelo usuário em seu porta-arquivos
 *
 * @author Gleydson
 */
@Entity
@Table(name = "arquivo_usuario", schema = "ava")
public class ArquivoUsuario implements Validatable {

	@Id
	@Column(name = "id_arquivo_usuario", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	private String nome;

	private long tamanho;

	@Column(name = "id_arquivo", nullable = false)
	private int idArquivo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@Column(name = "data", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pasta")
	private PastaArquivos pasta;

	@OneToMany(mappedBy="arquivo", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<ArquivoTurma> associacoesTurmas;
	

	
	public ArquivoUsuario() {
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(int idArquivo) {
		this.idArquivo = idArquivo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public ListaMensagens validate() {
		return null;
	}

	public long getTamanho() {
		return tamanho;
	}

	public void setTamanho(long tamanho) {
		this.tamanho = tamanho;
	}

	public PastaArquivos getPasta() {
		return pasta;
	}

	public void setPasta(PastaArquivos pasta) {
		this.pasta = pasta;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getExtensao() {
		int indice = nome.lastIndexOf(".");
		if (indice != -1)
			return nome.substring(indice+1);
		else
			return "";
	}

	public List<ArquivoTurma> getAssociacoesTurmas() {
		return associacoesTurmas;
	}

	public void setAssociacoesTurmas(List<ArquivoTurma> associacoesTurmas) {
		this.associacoesTurmas = associacoesTurmas;
	}
}