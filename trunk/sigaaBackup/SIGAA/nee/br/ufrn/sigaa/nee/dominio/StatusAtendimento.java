/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 05/11/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.nee.dominio;

import java.util.ArrayList;
import java.util.Collection;

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
 * Entidade referente aos status de atendimento das solicitações de apoio a CAENE 
 * pelos Discentes com Necessidades Educacionais Especiais.
 * @author Rafael Gomes
 *
 */

@Entity
@Table(schema = "nee", name = "status_atendimento")
public class StatusAtendimento implements Validatable{
	/** Status de Atendimento para quando o Coordenador Submeter a solicitação à CAENE */
	public static final int SUBMETIDO 	    = 1;
	/** Parecer da CAENE inserido a solicitação e Apoio concedido ao aluno */
	public static final int EM_ATENDIMENTO 	= 2; 
	/** Aluno desistiu de solicitar Apoio a CAENE */
	public static final int CANCELADO	    = 3; 
	/** A solicitação de apoio ao aluno foi concluída. */
	public static final int CONCLUIDO 		= 4; 
	
	/** Identificador */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_status_atendimento", unique = true, nullable = false)
	private int id;
	
	/** Nome do status de atendimento da solicitação dos alunos com NEE*/
	private String denominacao;
	
	/** Informar se o status de atendimento está ativo para a sua utilização no sistema.*/
	private Boolean ativo;
	
	/** Informar se o status de atendimento e ativo referenciando-o ao parecer.*/
	@Column(name = "atendimento_ativo")
	private Boolean atendimentoAtivo;
	

	/** default constructor */
	public StatusAtendimento() {
	}

	/** default minimal constructor */
	public StatusAtendimento(int id) {
		this.id = id;
	}
	
	/** minimal constructor */
	public StatusAtendimento(int id, String denominacao) {
		this.id = id;
		this.denominacao = denominacao;
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

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getAtendimentoAtivo() {
		return atendimentoAtivo;
	}

	public void setAtendimentoAtivo(Boolean atendimentoAtivo) {
		this.atendimentoAtivo = atendimentoAtivo;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}

	/** Retornar os status ativos, que estão aptos a receberem parecer técnico. */
	public static Collection<Integer> ativos() {
		Collection<Integer> status = new ArrayList<Integer>(3);
		status.add(EM_ATENDIMENTO);
		status.add(SUBMETIDO);
		return status;
	}

}
