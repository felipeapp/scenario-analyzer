package br.ufrn.sigaa.ensino.metropoledigital.dominio;

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
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;


/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Entidade correspondente a figura do Coordenador de P�lo do IMD
 * 
 * @author Rafael Barros
 *
 */

@Entity
@Table(name="coordenador_polo_imd", schema="metropole_digital")
public class CoordenadorPoloIMD  implements PersistDB, Validatable {

	/**ID da Classe*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="metropole_digital.coordenador_polo_imd_id_coordenador_polo_imd_seq") })
	@Column(name="id_coordenador_polo_imd", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/**Objeto que corresponde a pessoa que receber� o v�nculo de coordenador de p�lo do IMD*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;
	
	/**Objeto que corresponde a op��o p�lo grupo em que o coordenador ser� vinculado*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_polo")
	private Polo polo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}


	@Override
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequiredId(getPessoa().getId(), "Pessoa", lista);
		ValidatorUtil.validateRequiredId(getPolo().getId(),"P�lo", lista);
		return lista;
	}
	
}
