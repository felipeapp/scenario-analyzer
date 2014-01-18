package br.ufrn.sigaa.ensino_rede.academico.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Entidade que representa os estorno de movimentações do ensino em rede.
 * 
 * @author Jeferson Queiroga
 *
 */
@Entity
@Table(schema="ensino_rede", name = "estorno_movimento_discente_associado")
@SuppressWarnings("serial")
public class EstornoMovimentoDiscenteAssociado  implements PersistDB {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_estorno", nullable = false)
	private int id;
	
	/** Registro de entrada*/
	@ManyToOne
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;
	
	/** Data de estorno*/
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_estorno")
	private Date dataEstorno;
	
	/** Movimentação que foi estornada */
	@ManyToOne
	@JoinColumn(name="id_movimentacao")
	private MovimentacaoDiscenteAssociado movimentacao;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataEstorno() {
		return dataEstorno;
	}

	public void setDataEstorno(Date dataEstorno) {
		this.dataEstorno = dataEstorno;
	}

	public MovimentacaoDiscenteAssociado getMovimentacao() {
		return movimentacao;
	}

	public void setMovimentacao(MovimentacaoDiscenteAssociado movimentacao) {
		this.movimentacao = movimentacao;
	}

	
}
