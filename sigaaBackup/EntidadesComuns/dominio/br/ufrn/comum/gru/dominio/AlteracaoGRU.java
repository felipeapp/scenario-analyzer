package br.ufrn.comum.gru.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.seguranca.log.CriadoEm;

@Entity 
@Table(name = "alteracao_gru", schema = "gru")
public class AlteracaoGRU implements PersistDB {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="gru.gru_sequence") })
	@Column(name = "id_alteracao_gru")
	private int id;
	
	/** Data/hora de alteração. */
	@CriadoEm
	@Column(name = "criado_em")
	private Date criadoEm;
	
	/** Código de barras anterior da GRU. */
	@Column(name = "codigo_barras")
	private String codigoBarras;
	
	/** Linha digitável anterior da GRU. */
	@Column(name = "linha_digitavel")
	private String linhaDigitavel;
	
	/** GRU que foi alterada. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_gru")
	private GuiaRecolhimentoUniao gruAlterada;

	/** Construtor parametrizado. 
	 * 
	 * @param gru
	 */
	public AlteracaoGRU(GuiaRecolhimentoUniao gru) {
		this.codigoBarras = gru.getCodigoBarras();
		this.linhaDigitavel = gru.getLinhaDigitavel();
		this.gruAlterada = gru;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public String getLinhaDigitavel() {
		return linhaDigitavel;
	}

	public void setLinhaDigitavel(String linhaDigitavel) {
		this.linhaDigitavel = linhaDigitavel;
	}

	public GuiaRecolhimentoUniao getGruAlterada() {
		return gruAlterada;
	}

	public void setGruAlterada(GuiaRecolhimentoUniao gruAlterada) {
		this.gruAlterada = gruAlterada;
	}
	
}
