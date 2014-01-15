/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 08/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Classe que modela a situa��o do candidato no processo seletivo.
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "situacao_candidato", schema = "vestibular")
public class SituacaoCandidato implements PersistDB, Validatable{
	
	/** Indica que o candidato � AUSENTE no processo seletivo. */
	public static final SituacaoCandidato AUSENTE = new SituacaoCandidato(1, 'A');
	/** Indica que o candidato � ELIMINADO na prova discursiva no processo seletivo. */
	public static final SituacaoCandidato ELIMINADO_D = new SituacaoCandidato(2, 'D');
	/** Indica que o candidato � SUPLENTE no processo seletivo. */
	public static final SituacaoCandidato SUPLENTE = new SituacaoCandidato(3, 'E');
	/** Indica que o candidato � INDEFERIDO no processo seletivo. */
	public static final SituacaoCandidato INDEFERIDO = new SituacaoCandidato(4, 'F');
	/** Indica que o candidato � N�O TEVE A PROVA DISCURSIVA CORRIGIDA no processo seletivo. */
	public static final SituacaoCandidato SEM_CORRECAO_PROVA_DISCURSIVA_I = new SituacaoCandidato(5, 'I');
	/** Indica que o candidato � NAO FOI CLASSIFICADO NO AMA no processo seletivo. */
	public static final SituacaoCandidato NAO_CLASSIFICADO_AMA = new SituacaoCandidato(6, 'M');
	/** Indica que o candidato � N�O TEVE A REDA��O CORRIGIDA no processo seletivo. */
	public static final SituacaoCandidato SEM_CORRECAO_PROVA_DISCURSIVA_R = new SituacaoCandidato(7, 'R');
	/** Indica que o candidato � APROVADO no processo seletivo. */
	public static final SituacaoCandidato APROVADO = new SituacaoCandidato(8, 'S');
	/** Indica que o candidato � ELIMINADO no processo seletivo. */
	public static final SituacaoCandidato ELIMINADO_X = new SituacaoCandidato(9, 'X');
	/** Indica que o candidato � ELIMINADO no processo seletivo. */
	public static final SituacaoCandidato ELIMINADO_Y = new SituacaoCandidato(10, 'Y');
	/** Indica que o candidato � ELIMINADO no processo seletivo. */
	public static final SituacaoCandidato ELIMINADO_Z = new SituacaoCandidato(11, 'Z');

// 	Descri��es situa��o vestibular	
//	F - Inscri��o indeferida por n�o cumprimento da al�nea "c" do Item 20 do Edital.
//	A - Ausente (deixou de comparecer a pelo menos um dos tr�s dias).
//	Z - Eliminado(a) de acordo com a al�nea "b" do Item 30 do Edital.
//	Y - Eliminado(a) de acordo com a al�nea "i" do Item 30 do Edital.
//	R - N�o teve Provas Discursivas (incluindo a Prova de Reda��o) corrigidas de acordo com as al�neas "a", "b" ou "c" do Item 43.
//	I - N�o ter� Provas Discursivas (incluindo a Prova de Reda��o) corrigidas de acordo com a al�nea "d" do Item 43.
//	X - Eliminado(a) de acordo com a al�nea "b" do Item 44 do Edital.
//	D - Eliminado(a) de acordo com a al�nea "a" do Item 44 do Edital.
//	M - N�o classificado(a) por argumento m�nimo de aprova��o (AMA). 
//	E - Aprovado mas N�O classificado dentro do n�mero de vagas (suplente)
//	S - Aprovado e classificado dentro do n�mero de vagas
	 
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_situacao_candidato", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** C�digo do status. */
	@Column(name = "codigo", unique = true, nullable = false)
	private char codigo;
	
	/** Descri��o do status. */
	@Column(name = "descricao")
	private String descricao;

	/** Default Constructor */
	public SituacaoCandidato(){
	}
	
	/** Construtor parametrizado. 
	 * @param id
	 */
	public SituacaoCandidato(int id) {
		this.id = id;
	}
	
	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param descricao
	 */
	public SituacaoCandidato(int id, char codigo, String descricao) {
		this.id = id;
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 */
	public SituacaoCandidato(int id, char codigo) {
		this.id = id;
		this.codigo = codigo;
	}
	
	/** Construtor parametrizado.
	 * @param codigo
	 */
	public SituacaoCandidato(char codigo) {
		this.codigo = codigo;
	}
	
	
	/** Getter and Setters **/
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public char getCodigo() {
		return codigo;
	}

	public void setCodigo(char codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Retorna a situa��o de um candidato dado um c�digo.
	 * @param codigo
	 * @return
	 */
	public static SituacaoCandidato getSituacao(char codigo){
		switch (codigo) {
		case 'A':  return AUSENTE;
		case 'D':  return ELIMINADO_D;
		case 'E':  return SUPLENTE;
		case 'F':  return INDEFERIDO;
		case 'I':  return SEM_CORRECAO_PROVA_DISCURSIVA_I;
		case 'M':  return NAO_CLASSIFICADO_AMA;
		case 'R':  return SEM_CORRECAO_PROVA_DISCURSIVA_R;
		case 'S':  return APROVADO;
		case 'X':  return ELIMINADO_X;
		case 'Y':  return ELIMINADO_Y;
		case 'Z':  return ELIMINADO_Z;
		}
		return null;
	}
	
	@Override
	public ListaMensagens validate() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
	    return HashCodeUtil.hashAll(getId());
	}
	
	/** Retorna uma descri��o textual da situa��o do candidato.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return descricao;
	}
}
