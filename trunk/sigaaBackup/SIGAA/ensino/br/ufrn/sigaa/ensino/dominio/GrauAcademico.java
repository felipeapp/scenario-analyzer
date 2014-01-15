/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
 * Created on 13/09/2006
*/
package br.ufrn.sigaa.ensino.dominio;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que armazena o grau acadêmico que é um título conferido 
 * pelo estabelecimento de ensino superior em reconhecimento 
 * oficial pela conclusão.
 * 
 * @author gleydson
 */
@Entity
@Table(name = "grau_academico", schema = "ensino", uniqueConstraints = {})
public class GrauAcademico implements Validatable {

	// Fields

	/** Chave primária */
	private int id;

	/** Descrição do grau acadêmico */
	private String descricao;
	
	/** Título no gênero masculino que o discente receberá após a conclusão */
	private String tituloMasculino;
	
	/** Título no gênero feminino que o discente receberá após a conclusão */
	private String tituloFeminino;

	/** Título é Licenciatura Plena */
	public static final int LICENCIATURA_PLENA = 2;

	/** Tipos de Licenciatura */
	public static final List<Integer> LICENCIATURAS = Arrays
		.asList(new Integer[] { 2, 5, 6, 7 });

	/** Título é Bacharelado */
	public static final int BACHARELADO = 1;


	// Constructors

	/** default constructor */
	public GrauAcademico() {
	}

	/** default minimal constructor */
	public GrauAcademico(int id) {
		this.id = id;
	}

	/** full constructor */
	public GrauAcademico(int idGrauAcademico, String descricao) {
		this.id = idGrauAcademico;
		this.descricao = descricao;
	}

	public GrauAcademico(String s) {
		descricao = s;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_grau_academico", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idGrauAcademico) {
		this.id = idGrauAcademico;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", erros);
		return erros;
	}

	@Column(name = "titulo_masculino", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getTituloMasculino() {
		return tituloMasculino;
	}

	public void setTituloMasculino(String tituloMasculino) {
		this.tituloMasculino = tituloMasculino;
	}
	
	@Column(name = "titulo_feminino", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getTituloFeminino() {
		return tituloFeminino;
	}

	public void setTituloFeminino(String tituloFeminino) {
		this.tituloFeminino = tituloFeminino;
	}

	@Override
	public String toString() {
		return id + " - " + descricao;
	}
	
}
