package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

@Entity 
@Table(name = "movimentacao_aviso_falta_homologado", schema = "ensino")
public class MovimentacaoAvisoFaltaHomologado implements PersistDB {

	public final static MovimentacaoAvisoFaltaHomologado PENDENTE_APRESENTACAO_PLANO = new MovimentacaoAvisoFaltaHomologado(1);
	public final static MovimentacaoAvisoFaltaHomologado PLANO_PENDENTE_APROVACAO_CHEFIA = new MovimentacaoAvisoFaltaHomologado(2);
	public final static MovimentacaoAvisoFaltaHomologado PENDENTE_REAPRESENTACAO_PLANO = new MovimentacaoAvisoFaltaHomologado(3);
	public final static MovimentacaoAvisoFaltaHomologado PLANO_APROVADO = new MovimentacaoAvisoFaltaHomologado(4);
	public final static MovimentacaoAvisoFaltaHomologado ESTORNADO = new MovimentacaoAvisoFaltaHomologado(5);
	public final static MovimentacaoAvisoFaltaHomologado HOMOLOGACAO_NEGADA = new MovimentacaoAvisoFaltaHomologado(6);
	
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") }) 		
	@Column(name = "id_movimentacao")
	private int id;
	
	@Column(name = "descricao")
	private String descricao;

	public MovimentacaoAvisoFaltaHomologado() {
	}	
	
	public MovimentacaoAvisoFaltaHomologado(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
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
		MovimentacaoAvisoFaltaHomologado other = (MovimentacaoAvisoFaltaHomologado) obj;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	
	/**
	 * Retorna os ids das movimentações pendentes para uso em sql.
	 * @return
	 */
	public static String getMovimentacoesPendentes(){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(PENDENTE_APRESENTACAO_PLANO.getId());
		sb.append(",");
		sb.append(PLANO_PENDENTE_APROVACAO_CHEFIA.getId());
		sb.append(",");
		sb.append(PENDENTE_REAPRESENTACAO_PLANO.getId());
		sb.append(",");
		sb.append(PLANO_APROVADO.getId());
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * Retorna os ids das movimentações sem plano apresentado para uso em sql.
	 * @return
	 */
	public static String getMovimentacoesSemPlano(){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(PENDENTE_APRESENTACAO_PLANO.getId());
		sb.append(",");
		sb.append(PENDENTE_REAPRESENTACAO_PLANO.getId());
		sb.append(")");
		return sb.toString();
	}
}
