package br.ufrn.sigaa.apedagogica.dominio;

import java.util.HashMap;
import java.util.Map;

/**
 * Status da participa��o do docente na atividade de atualiza��o pegag�gica.
 * 
 * @author Gleydson
 * 
 */
public enum StatusParticipantesAtualizacaoPedagogica  {

	/**
	 * Status da inscri��o do participante.
	 */
	INSCRITO(1), CONCLUIDO(2), AUSENTE(3);

	/**
	 * Define o valor do status.
	 */
	private int id;

	/**
	 * Define o nome para visualiza��o do status.
	 */
	private String denominacao;
	
	/** Mapa com todos poss�veis status de uma inscri��o. */
	public static final Map<Integer, String> descricaoStatus = new HashMap<Integer, String>(){{
		put(INSCRITO.getId(), "Inscrito");
		put(CONCLUIDO.getId(), "Presente");
		put(AUSENTE.getId(), "Ausente");
	}};

	private StatusParticipantesAtualizacaoPedagogica(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	/**
	 * Retorna o status por extenso.
	 * @return
	 */
	public static Map<Integer, String> getDescricaostatus() {
		return descricaoStatus;
	}

}
