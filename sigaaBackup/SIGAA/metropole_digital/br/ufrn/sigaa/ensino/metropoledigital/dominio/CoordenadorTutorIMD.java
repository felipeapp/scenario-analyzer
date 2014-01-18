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
import br.ufrn.sigaa.pessoa.dominio.Pessoa;


/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Entidade correspondente a figura do Coordenador de Tutores do IMD
 * 
 * @author Rafael Barros
 *
 */

@Entity
@Table(name="coordenador_tutor_imd", schema="metropole_digital")
public class CoordenadorTutorIMD  implements PersistDB, Validatable {

	/**ID da Classe*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="metropole_digital.coordenador_tutor_imd_id_coordenador_tutor_imd_seq") })
	@Column(name="id_coordenador_tutor_imd", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/**Objeto que corresponde a pessoa que receberá o vínculo de coordenador de pólo do IMD*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;
	

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



	@Override
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequiredId(getPessoa().getId(), "Pessoa", lista);
		return lista;
	}
	
}
