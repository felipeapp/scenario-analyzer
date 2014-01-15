/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe que indica o status da an�lise da foto enviada pelo candidato do
 * vestibular: se � v�lida, inv�lida, n�o possui boa resolu��o, apresenta mais
 * de uma pessoa na imagem, etc.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "status_foto", schema = "vestibular")
public class StatusFoto  implements PersistDB {

	/** Constante que define a foto como sendo n�o analisada. */
	public static final int NAO_ANALISADA = 1;

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_status_foto", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Indica se a foto do candidato � v�lida ou n�o. Caso inv�lida, dever� ser obrigat�rio informar a recomenda��o de como corrigir o problema com a foto, que ser� enviada ao candidato. */ 
	private boolean valida;

	/** Descri��o do status. Por exemplo: v�lida, inv�lida, n�o possui boa resolu��o, apresenta mais de uma pessoa na imagem, etc. */
	private String descricao;
	
	/** Recomenda��o a ser enviada ao candidato, para corrigir o problema com a foto. Por exemplo: "A foto enviada n�o � v�lida. N�o use �culos escuros, bon� ou chap�u, uma vez que dificultam a identifica��o do candidato." */
	private String recomendacao;
	
	/** Construtor padr�o. */
	public StatusFoto() {
	}
	
	/** Construtor parametrizado. 
	 * @param id
	 */
	public StatusFoto(int id){
		this();
		this.id = id;
	}

	/** Retorna a chave prim�ria.
	 * @return
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria.
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a descri��o do status.
	 * @return
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descri��o do status.
	 * @param descricao
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	/** Retorna a recomenda��o a ser enviada ao candidato, para corrigir o problema com a foto. Por exemplo: "A foto enviada n�o � v�lida. N�o use �culos escuros, bon� ou chap�u, uma vez que dificultam a identifica��o do candidato."
	 * @return
	 */
	public String getRecomendacao() {
		return recomendacao;
	}

	/** Seta a recomenda��o a ser enviada ao candidato, para corrigir o problema com a foto. Por exemplo: "A foto enviada n�o � v�lida. N�o use �culos escuros, bon� ou chap�u, uma vez que dificultam a identifica��o do candidato."
	 * @param recomendacao
	 */
	public void setRecomendacao(String recomendacao) {
		this.recomendacao = recomendacao;
	}
	
	/** Indica se a foto do candidato � v�lida ou n�o. Caso inv�lida, dever� ser obrigat�rio informar a recomenda��o de como corrigir o problema com a foto, que ser� enviada ao candidato.
	 * @return
	 */
	public boolean isValida() {
		return valida;
	}

	/** Seta se a foto do candidato � v�lida ou n�o. Caso inv�lida, dever� ser obrigat�rio informar a recomenda��o de como corrigir o problema com a foto, que ser� enviada ao candidato.
	 * @param valida
	 */
	public void setValida(boolean valida) {
		this.valida = valida;
	}

	/** Retorna a descri��o do status da foto.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (valida? "V�lida" : "Inv�lida") + " - " + descricao;
	}
}
