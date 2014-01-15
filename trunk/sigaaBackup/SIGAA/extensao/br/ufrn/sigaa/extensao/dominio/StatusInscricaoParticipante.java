/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2009
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Representa a situação da inscrição para participante em um período de inscrição.
 * 
 * @author Daniel Augusto
 *
 */
@Entity
@Table(schema = "extensao", name = "status_inscricao_participante")
public class StatusInscricaoParticipante implements Validatable, Comparable<StatusInscricaoParticipante> {
	
	/** Caso a inscrição exija a aprovação do coordenador, o participante se inscreve na atividade e fica com status inscrito. 
	 *  Se não ele já vai diretor para APROVADO.
	 *  
	 *  Ele também vai para esse status se a inscrição requerer pagamento. Só irá para aprovado quando o pagamento for confirmado no sistema.
	 */
	public final static Integer INSCRITO = 1; 
	
	/** Participante confirma participação por email.  @Deprecated O usuário não precisa mais confirmar sua inscrição */
	@Deprecated
	public final static Integer CONFIRMADO = 2;
	
	/** Coordenador aprova participação do aluno. Ou se a inscrição não exije confirmação vai direto para esse status. */
	public final static Integer APROVADO = 3;
	/** Coordenador recusa a participação do aluno. */
	public final static Integer RECUSADO = 4;
	/** Aluno cancela a participação no evento/curso. */
	public final static Integer CANCELADO = 5;
	
	
	/** O identificador */
    @Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_status_inscricao_participante", nullable = false)
    private int id;
	
	/** A descrição do status para mostrar aos usuários. */
	private String descricao;
	
	/** Status Inscrição Participantes removidos do sistema vão ser inativados.*/
	private boolean ativo;
	
	public StatusInscricaoParticipante() {}
	
	public StatusInscricaoParticipante(int id) {
		this.id = id;
	}
	
	public StatusInscricaoParticipante(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	
	/**
	 * Retorna os Status da Inscrição considerados inativos, são aquele que o aluno não vai 
	 * participar do curso ou evento, seja porque ele cancelou ou o coordenadou recusou a sua inscrição.
	 *
	 * @StatusInscricaoParticipante[]
	 */
	public static StatusInscricaoParticipante[] getStatusInativos(){
		return new StatusInscricaoParticipante[]{ new StatusInscricaoParticipante(RECUSADO), new StatusInscricaoParticipante(CANCELADO)};
	}
	
	/**
	 * Retorna os Status da Inscrição considerados ativos, são aquele que o aluno provavelmente vai 
	 * participar do curso ou evento, se não ocorrer nenhum imprevisto no caminho.
	 *
	 *
	 * @StatusInscricaoParticipante[]
	 */
	public static StatusInscricaoParticipante[] getStatusAtivos(){
		return new StatusInscricaoParticipante[]{ new StatusInscricaoParticipante(INSCRITO), new StatusInscricaoParticipante(CONFIRMADO), new StatusInscricaoParticipante(APROVADO)};
	}
	
	/**
	 * Diz se esse é o status inscrito
	 * @return
	 */
	public boolean isStatusInscrito(){
		return this.id == INSCRITO;
	}
	/**
	 * Diz se esse é o status aprovado
	 * @return
	 */
	public boolean isStatusAprovado(){
		return this.id == APROVADO;
	}
	/**
	 * Diz se esse é o status cancelado
	 * @return
	 */
	public boolean isStatusCancelado(){
		return this.id == CANCELADO;
	}
	/**
	 * Diz se esse é o status recusado
	 * @return
	 */
	public boolean isStatusRecusado(){
		return this.id == RECUSADO;
	}
	
	/**
	 * Compara os status das inscrições pelo id.
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */	
	@Override
	public int compareTo(StatusInscricaoParticipante o) {
		return new Integer(id).compareTo(o.getId());
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param ativo the ativo to set
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * @return the ativo
	 */
	public boolean isAtivo() {
		return ativo;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatusInscricaoParticipante other = (StatusInscricaoParticipante) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public ListaMensagens validate() {
		return null;
	}

	@Override
	public String toString() {
		switch (id) {
			case 0: return "INSCRITO";
			case 3: return "APROVADO";
			case 4: return "RECUSADO";
			case 5: return "CANCELADO";
		}
		return "";
	}

	

}
