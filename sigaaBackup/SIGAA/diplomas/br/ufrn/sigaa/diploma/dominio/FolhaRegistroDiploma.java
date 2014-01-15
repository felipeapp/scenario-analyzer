/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/01/2007'
 *
 */
package br.ufrn.sigaa.diploma.dominio;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/** Classe que representa uma página de registro de diploma.
 * @author Édipo Elder F. Melo
 *
 */
@Entity
@Table(schema = "diploma", name = "folha_registro_diploma")
public class FolhaRegistroDiploma implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_folha_registro_diploma")
	private int id;
	
	/** Array de registros de diplomas. */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_folha_registro_diploma")
	@IndexColumn(name = "indice")
	private RegistroDiploma[] registros;
	
	/** Livro ao qual esta folha pertence. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_livro_registro_diploma",nullable=false)
	private LivroRegistroDiploma livro;

	/** Número da folha (página). */
	@Column(name = "numero_folha")
	private int numeroFolha;
	
	/** Construtor padrão. */
	public FolhaRegistroDiploma() {
		
	}
	
	/** Construtor parametrizado. 
	 * @param id
	 */
	public FolhaRegistroDiploma(int id) {
		this.id = id;
	}
	
	/** Construtor parametrizado.
	 * @param numeroFolha
	 * @param quantidadeRegistros
	 */
	public FolhaRegistroDiploma(int numeroFolha, int quantidadeRegistros) {
		this.numeroFolha = numeroFolha;
		registros = new RegistroDiploma[quantidadeRegistros];
		// Cria um registro livre para uso. 
		for (int i = 0; i < quantidadeRegistros; i++) {
			registros[i] = new RegistroDiploma();
			registros[i].setLivre(true);
			registros[i].setFolha(this);
		}
	}
	
	/** Valida os atributos: número da folha.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(numeroFolha, "Número da Folha", lista);
		return lista;
	}

	/** Retorna a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna um array de registros de diplomas.  
	 * @return
	 */
	public RegistroDiploma[] getRegistros() {
		return registros;
	}

	/** Seta um array de registros de diplomas.
	 * @param registros
	 */
	public void setRegistros(RegistroDiploma[] registros) {
		this.registros = registros;
	}

	/** Retorna o livro ao qual esta folha pertence. 
	 * @return
	 */
	public LivroRegistroDiploma getLivro() {
		return livro;
	}

	/** Seta o livro ao qual esta folha pertence.
	 * @param livro
	 */
	public void setLivro(LivroRegistroDiploma livro) {
		this.livro = livro;
	}

	/** Retorna o número da folha (página). 
	 * @return
	 */
	public int getNumeroFolha() {
		return numeroFolha;
	}

	/** Seta o número da folha (página).
	 * @param numeroFolha
	 */
	public void setNumeroFolha(int numeroFolha) {
		this.numeroFolha = numeroFolha;
	}

	/**
	 * Retorna uma representação textual da folha de registro de diploma no
	 * seguinte formato: "Folha nº", seguido do número da folha, seguido de
	 * " do livro", seguido do título do livro.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Folha nº "+numeroFolha+" do livro " + livro;
	}

	/** Indica se a folha possui registro livre para uso.
	 * @return
	 */
	public boolean hasRegistroLivre() {
		if (registros == null) return true;
		for (int i = 0; i < registros.length; i++) {
			if (registros[i] == null) {
				return true;
			} else if (registros[i].isLivre())
				return true;
		}
		return false;
	}

	/** Retorna o primeiro registro livre para uso da folha. 
	 * @return
	 */
	public RegistroDiploma getPrimeiroRegistroLivre() {
		if (registros == null) {
			registros = new RegistroDiploma[livro.getNumeroRegistroPorFolha()];
		}
		for (RegistroDiploma registro : registros)
			if (registro == null) {
				RegistroDiploma r = new RegistroDiploma();
				addRegistro(r);
				return r;
			} else if (registro.isLivre())
				return registro;
		return null;
	}
	
	/** Retorna o registro na posição informada. 
	 * @param pos (0 = primeiro registro)
	 * @return
	 */
	public RegistroDiploma getRegistroDiploma(int pos) {
		return getRegistros()[pos];
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Adiciona um registro à lista de registros
	 * @param registro
	 */
	public void addRegistro(RegistroDiploma registro) {
		if (registros == null)
			registros = new RegistroDiploma[livro.getNumeroRegistroPorFolha()];
		for (int i = 0; i < livro.getNumeroRegistroPorFolha(); i++) {
			if (registros[i] == null) {
				registros[i] = registro;
				registro.setFolha(this);
				break;
			}
		}
	}
	
}
