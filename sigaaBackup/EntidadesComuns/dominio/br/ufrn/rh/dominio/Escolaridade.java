package br.ufrn.rh.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Indicação da escolaridade do servidor: analfabeto,  ensino fundamental, ensino médio,
 * graduado, etc.
 * @author yoshi
 */
@Entity
@Table(name = "escolaridade", schema = "rh")
public class Escolaridade implements PersistDB  {


	// constantes
	/**
	 * estas constantes devem estar sincronizadas com os valores do banco
	 * para outros tipos, verificar diretamente a tabela em rh.escolaridade
	 */
	public static final int ANALFABETO		 	= 1;

	public static final int ENSINO_FUNDAMENTAL	= 6;

	public static final int ENSINO_MEDIO		= 8;

	public static final int ENSINO_SUPERIOR		= 10;

	public static final int ESPECIALIZACAO		= 12;

	public static final int MESTRADO			= 13;

	public static final int DOUTORADO			= 14;


	/** Identificador */
	@Id
	@Column(name = "id_escolaridade")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="comum.general_seq") })
	private int id;

	/** Descrição: Analfabeto, Ensino Medio, Doutorado, etc. */
	private String descricao;

	/** Se a escolaridade está ativa ou não */
	private boolean ativo;

	public String getDescricao() {
		return descricao;
	}

	/** Sigla da escolaridade referente ao SCDP - Sistema de Concessão de Diárias e Passagens. */
	@Column(name = "sigla_scdp", nullable = true)
	private String siglaScdp;
	
	public Escolaridade() {

	}
	
	public Escolaridade(int id) {
		this.id = id;
	}

	public Escolaridade(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public Escolaridade(int id, String descricao, String siglaScdp) {
		this.id = id;
		this.descricao = descricao;
		this.siglaScdp = siglaScdp;
	}

	/**
	 * Utilizado por BolsaAuxilioMBebea, para exibir no cadastro
	 * os principais elementos de ensin
	 * @return
	 */

	public static List<Escolaridade> getElementosPrincipais() {
		List<Escolaridade> escol = new ArrayList<Escolaridade>();

		escol.add( new Escolaridade(ANALFABETO, getDescricaoPorId(ANALFABETO)) );
		escol.add( new Escolaridade(ENSINO_FUNDAMENTAL, getDescricaoPorId(ENSINO_FUNDAMENTAL)) );
		escol.add( new Escolaridade(ENSINO_MEDIO, getDescricaoPorId(ENSINO_MEDIO)) );
		escol.add( new Escolaridade(ENSINO_SUPERIOR, getDescricaoPorId(ENSINO_SUPERIOR)) );
		escol.add( new Escolaridade(ESPECIALIZACAO, getDescricaoPorId(ESPECIALIZACAO)) );
		escol.add( new Escolaridade(MESTRADO, getDescricaoPorId(MESTRADO)) );
		escol.add( new Escolaridade(DOUTORADO, getDescricaoPorId(DOUTORADO)) );

		return escol;
	}

	public static String getDescricaoPorId(int id) {

		switch (id) {
			case ANALFABETO:
				return "ANALFABETO";
			case ENSINO_FUNDAMENTAL:
				return "ENSINO FUNDAMENTAL";
			case ENSINO_MEDIO:
				return "ENSINO MEDIO";
			case ENSINO_SUPERIOR:
				return "ENSINO SUPERIOR";
			case ESPECIALIZACAO:
				return "ESPECIALIZACAO";
			case MESTRADO:
				return "MESTRADO";
			case DOUTORADO:
				return "DOUTORADO";
			default:
				break;
			}
		return "";
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean hasNivelSuperior(){
		return (id == Escolaridade.ENSINO_SUPERIOR) 
				|| (id == Escolaridade.DOUTORADO)
				|| (id == Escolaridade.ESPECIALIZACAO)
				|| (id == Escolaridade.MESTRADO);			
	}

	public String getSiglaScdp() {
		return siglaScdp;
	}

	public void setSiglaScdp(String siglaScdp) {
		this.siglaScdp = siglaScdp;
	}
}