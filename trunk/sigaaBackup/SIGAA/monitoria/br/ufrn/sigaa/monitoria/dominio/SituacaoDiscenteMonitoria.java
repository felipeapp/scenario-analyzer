package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/*******************************************************************************
 * Representa a situação do aluno vinculado a um projeto de monitoria.
 * 
 ******************************************************************************/
@Entity
@Table(name = "situacao_discente_monitoria", schema = "monitoria")
public class SituacaoDiscenteMonitoria implements PersistDB {

	/**
	 * estas constantes devem estar sincronizadas com os valores do banco.
	 */
	/** usado quando a prograd validava a prova seletiva */
	public static final int AGUARDANDO_VALIDACAO_PROGRAD 			= 1;
	/** discente aprovado, mas sua classificação não atingiu o 
	 *  número de vagas disponíveis */
	public static final int AGUARDANDO_CONVOCACAO 				= 2;
	/** prograd não aceitou o resultado cadastrado da prova seletiva */
	public static final int INVALIDADO_PROGRAD 				= 3;
	/** discente não assumiu a bolsa */
	public static final int CONVOCADO_MAS_REJEITOU_MONITORIA 		= 4;
	/** monitoria ativa discente foi aprovado no limite das vagas da prova */
	public static final int ASSUMIU_MONITORIA 				= 5;
	/** finalizado pelo orientador ou a pedido do próprio monitor */
	public static final int MONITORIA_FINALIZADA 				= 6; 
	/** cancelada por descumprimento de alguma cláusula da resolução... */
	public static final int MONITORIA_CANCELADA 				= 7; 
	/** excluído pela prograd, geralmente erro de migração */
	public static final int EXCLUIDO 					= 8; 
	/** prograd confirma não aprovação do aluno na seleção */
	public static final int NAO_APROVADO 					= 9; 
	/** desfaz a operação de validação do cadastro de seleção 
	 *  feito pelo professor. Que já tinha sido validado por 
	 *  ele mas que por algum motivo, deve ser desfeito. */
	public static final int DESVALIDADO_PELA_PROGRAD 			= 10; 
	/** discente aprovado na seleção dentro da quantidade 
	 *  vagas disponíveis, após aceitação da vaga no portal discente, 
	 *  vai para assumiu monitoria */
	public static final int CONVOCADO_MONITORIA	 			= 11;

	/** Grupo de que representa ações de extensão inválidas. */
	public static final Integer[] SITUACAO_DISCENTE_VALIDA = {ASSUMIU_MONITORIA, MONITORIA_FINALIZADA};	
	
	// Fields
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_situacao_discente_monitoria")
	private int id;

	@Column(name = "descricao")
	private String descricao;

	// Constructors

	/** default constructor */
	public SituacaoDiscenteMonitoria() {
	}

	/** minimal constructor */
	public SituacaoDiscenteMonitoria(int id) {
		this.id = id;
	}

	/** full constructor */
	public SituacaoDiscenteMonitoria(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj == null)
			return false;

		if (obj instanceof SituacaoDiscenteMonitoria) {
			SituacaoDiscenteMonitoria o = (SituacaoDiscenteMonitoria) obj;
			if (o.getId() > 0 && this.getId() > 0) {
			    result = this.getId() == o.getId();
			} else {
			    result = this.getDescricao().equalsIgnoreCase(o.getDescricao());
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		int result = 17;
		if (this.getId() > 0) {
		    result = 37 * result + new Integer(this.getId()).hashCode();
		} else {
		    result = 37 * result + this.getDescricao().hashCode();
		}
		return result;
	}

	
}
