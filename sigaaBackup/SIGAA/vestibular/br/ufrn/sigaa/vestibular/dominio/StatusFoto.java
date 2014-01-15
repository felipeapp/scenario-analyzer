/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 22/06/2010
 *
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

/**
 * Classe que indica o status da análise da foto enviada pelo candidato do
 * vestibular: se é válida, inválida, não possui boa resolução, apresenta mais
 * de uma pessoa na imagem, etc.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "status_foto", schema = "vestibular")
public class StatusFoto  implements PersistDB {

	/** Constante que define a foto como sendo não analisada. */
	public static final int NAO_ANALISADA = 1;

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_status_foto", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Indica se a foto do candidato é válida ou não. Caso inválida, deverá ser obrigatório informar a recomendação de como corrigir o problema com a foto, que será enviada ao candidato. */ 
	private boolean valida;

	/** Descrição do status. Por exemplo: válida, inválida, não possui boa resolução, apresenta mais de uma pessoa na imagem, etc. */
	private String descricao;
	
	/** Recomendação a ser enviada ao candidato, para corrigir o problema com a foto. Por exemplo: "A foto enviada não é válida. Não use óculos escuros, boné ou chapéu, uma vez que dificultam a identificação do candidato." */
	private String recomendacao;
	
	/** Construtor padrão. */
	public StatusFoto() {
	}
	
	/** Construtor parametrizado. 
	 * @param id
	 */
	public StatusFoto(int id){
		this();
		this.id = id;
	}

	/** Retorna a chave primária.
	 * @return
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária.
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a descrição do status.
	 * @return
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descrição do status.
	 * @param descricao
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	/** Retorna a recomendação a ser enviada ao candidato, para corrigir o problema com a foto. Por exemplo: "A foto enviada não é válida. Não use óculos escuros, boné ou chapéu, uma vez que dificultam a identificação do candidato."
	 * @return
	 */
	public String getRecomendacao() {
		return recomendacao;
	}

	/** Seta a recomendação a ser enviada ao candidato, para corrigir o problema com a foto. Por exemplo: "A foto enviada não é válida. Não use óculos escuros, boné ou chapéu, uma vez que dificultam a identificação do candidato."
	 * @param recomendacao
	 */
	public void setRecomendacao(String recomendacao) {
		this.recomendacao = recomendacao;
	}
	
	/** Indica se a foto do candidato é válida ou não. Caso inválida, deverá ser obrigatório informar a recomendação de como corrigir o problema com a foto, que será enviada ao candidato.
	 * @return
	 */
	public boolean isValida() {
		return valida;
	}

	/** Seta se a foto do candidato é válida ou não. Caso inválida, deverá ser obrigatório informar a recomendação de como corrigir o problema com a foto, que será enviada ao candidato.
	 * @param valida
	 */
	public void setValida(boolean valida) {
		this.valida = valida;
	}

	/** Retorna a descrição do status da foto.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (valida? "Válida" : "Inválida") + " - " + descricao;
	}
}
