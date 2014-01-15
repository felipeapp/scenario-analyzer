/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
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
 * Define os poss�veis status da Inscri��o num Processo de Sele��o.
 * 
 * @author �dipo Elder F. Melo
 * @author M�rio Rizzi
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
	
	/** Inscri��o submetida. */
	public static final int SUBMETIDA = 1;
	
	/** Candidato Aprovado na Sele��o. */
	public static final int APROVADO_SELECAO = 2;

	/** Inscri��o Cancelada. */
	public static final int CANCELADA = 3;

	/**
	 * Inscri��o Deferida. Definido quando o candidato cumpriu todos requisitos
	 * definidos em Edital como, por exemplo, ter entregue todos documentos
	 * solicitados.
	 */
	public static final int DEFERIDA = 4;

	/**
	 * Inscri��o Indeferida. Definido quando o candidato n�o cumpre todos
	 * requisitos definidos em Edital como, por exemplo, n�o ter entregue todos
	 * documentos solicitados.
	 */
	public static final int INDEFERIDA = 5;

	/**
	 * Candidato Eliminado do Processo Seletivo. Definido quando o candidato n�o
	 * incorre em �tens definido em Edital que definem a elimina��o do candidato
	 * no processo como, por exemplo, n�o ter atingido uma m�dia m�nima em
	 * alguma etapa.
	 */
	public static final int ELIMINADO = 6;

	/**
	 * Candidato aprovado, mas que n�o teve pontua��o para ser classificado no
	 * n�mero de vagas definido. Neste caso, o candidato poder� assumir uma vaga
	 * remanescente de uma poss�vel desist�ncia de outro candidato.
	 */
	public static final int SUPLENTE = 7;

	
	/** Mapa com todos poss�veis status de uma inscri��o. */
	public static final Map<Integer, String> todosStatus = new HashMap<Integer, String>(){{
		put(SUBMETIDA, "Inscri��o Submetida");
		put(CANCELADA, "Inscri��o Cancelada");
		put(DEFERIDA, "Inscri��o Deferida");
		put(INDEFERIDA, "Inscri��o Indeferida");
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
	 * Implementa��o do m�todo equals comparando-se os ids das {@link StatusInscricaoMatricula}.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/**
	 * Defini��o do hashcode da classe.
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