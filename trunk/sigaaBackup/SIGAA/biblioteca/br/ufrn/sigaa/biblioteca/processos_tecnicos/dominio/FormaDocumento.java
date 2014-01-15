package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * <p>Entidade de domínio que representa a forma de documento em que se encontra o material.</p>
 * <p>Normalmente usado para formatos multimeios (vários formatos de mídia em um único material).  
 * Por exemplo, um material do tipo <strong>**CD-ROM**</strong> pode 
 * conter a forma do documento <strong>**Livro**</strong> ou forma do documento <strong>**Música**</strong>.</p>
 * 
 * @author Mário Rizzi
 *
 */

@Entity
@Table(name = "forma_documento", schema = "biblioteca")
public class FormaDocumento implements Validatable {

	/**
	 * O id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })	
	@Column(name="id_forma_documento")
	private int id;

	/** Denominação que descreve da forma do documento  */
	@Column(name = "denominacao", nullable=false)
	private String denominacao;
	
	/** indica se a forma de documento foi removida do sistema.  */
	private boolean ativo;

	public FormaDocumento(){
	}
	
	public FormaDocumento(Integer id){
		this.id = id;
	}
	
	public FormaDocumento(String denominacao){
		this.denominacao = denominacao;
	}
	
	public FormaDocumento(Integer id, String denominacao){
		this(id);
		this.denominacao = denominacao;
	}
	
	
	public int getId() {
		return this.id;
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
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(this.denominacao, "Denominação", lista);
		
		return lista;
	}
	
}
