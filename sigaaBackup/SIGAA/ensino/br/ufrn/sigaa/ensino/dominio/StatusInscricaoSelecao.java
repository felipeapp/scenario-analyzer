/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 29/10/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * Define os possíveis status da Inscrição num Processo de Seleção.
 * 
 * @author Édipo Elder F. Melo
 * @author Mário Rizzi
 * 
 */
@Entity
@Table(name = "status_inscricao_selecao",schema = "ensino" )
public class StatusInscricaoSelecao implements Validatable {

	@Id
	@GeneratedValue(generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
    				  parameters = {@Parameter(name = "sequence_name", value = "public.hibernate_sequence")})
    @Column(name = "id_status_inscricao_selecao", nullable = false)
	private int id;
	
	private String denominacao;
	
	private boolean ativo;
	
	/** Inscrição submetida. */
	public static final int SUBMETIDA = 1;
	
	/** Candidato Aprovado na Seleção. */
	public static final int APROVADO_SELECAO = 2;

	/** Inscrição Cancelada. */
	public static final int CANCELADA = 3;

	/**
	 * Inscrição Deferida. Definido quando o candidato cumpriu todos requisitos
	 * definidos em Edital como, por exemplo, ter entregue todos documentos
	 * solicitados.
	 */
	public static final int DEFERIDA = 4;

	/**
	 * Inscrição Indeferida. Definido quando o candidato não cumpre todos
	 * requisitos definidos em Edital como, por exemplo, não ter entregue todos
	 * documentos solicitados.
	 */
	public static final int INDEFERIDA = 5;

	/**
	 * Candidato Eliminado do Processo Seletivo. Definido quando o candidato não
	 * incorre em ítens definido em Edital que definem a eliminação do candidato
	 * no processo como, por exemplo, não ter atingido uma média mínima em
	 * alguma etapa.
	 */
	public static final int ELIMINADO = 6;

	/**
	 * Candidato aprovado, mas que não teve pontuação para ser classificado no
	 * número de vagas definido. Neste caso, o candidato poderá assumir uma vaga
	 * remanescente de uma possível desistência de outro candidato.
	 */
	public static final int SUPLENTE = 7;

	
	/** Mapa com todos possíveis status de uma inscrição. */
	public static final Map<Integer, String> todosStatus = new HashMap<Integer, String>(){{
		put(SUBMETIDA, "Inscrição Submetida");
		put(CANCELADA, "Inscrição Cancelada");
		put(DEFERIDA, "Inscrição Deferida");
		put(INDEFERIDA, "Inscrição Indeferida");
		put(APROVADO_SELECAO, "Candidato Aprovado");
		put(ELIMINADO, "Candidato Eliminado");
		put(SUPLENTE, "Candidato Suplente");
		}};
	
	public StatusInscricaoSelecao(){
		this.ativo = true;
	}
	
	public StatusInscricaoSelecao(int id){
		this(id, null);
	}
	
	public StatusInscricaoSelecao(int id, String denominacao){
		this.id = id;
		this.denominacao = denominacao;
	}
		
	/**
	 * Retorna todos os status ativos de um inscrito
	 * @return
	 */
	public static List<Integer> getStatusAtivos(){
		
		List<Integer> statusAtivos = new ArrayList<Integer>();
		
		statusAtivos.add(StatusInscricaoSelecao.DEFERIDA);
		statusAtivos.add(StatusInscricaoSelecao.APROVADO_SELECAO);
		statusAtivos.add(StatusInscricaoSelecao.SUBMETIDA);
		statusAtivos.add(StatusInscricaoSelecao.SUPLENTE);
		
		return statusAtivos;
	}
	
	/**
	 * Retorna todos os status inativos de um inscrito.
	 * @return
	 */
	public static List<Integer> getStatusInAtivos(){
		
		List<Integer> statusAtivos = new ArrayList<Integer>();
		
		statusAtivos.add(StatusInscricaoSelecao.INDEFERIDA);
		statusAtivos.add(StatusInscricaoSelecao.CANCELADA);
		statusAtivos.add(StatusInscricaoSelecao.ELIMINADO);
		
		return statusAtivos;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;	
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	/**
	 * Implementação do método equals comparando-se os ids das {@link StatusInscricaoMatricula}.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/**
	 * Definição do hashcode da classe.
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return denominacao;
	}
}