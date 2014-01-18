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
import br.ufrn.sigaa.ouvidoria.dominio.AcompanhamentoManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;


/**
 * Esta entidade representa um Tutor no IMD. O tutor é uma figura 
 * diferenciada nos cursos do IMD pois atua nas aulas presenciais.
 * 
 * Esta entidade indica que uma determinada pessoa é um Tutor do IMD,
 * os dados específicos e o seu tempo de atuação no Instituto.
 * 
 * 
 * @author Gleydson, Rafael Silva, Rafael Barros
 *
 */
@Entity
@Table(name = "tutor_imd", schema = "metropole_digital")
public class TutorIMD implements PersistDB, Validatable {

	/**
     * Chave primária da tabela tutor_imd
     */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "metropole_digital.tutor_imd_id_tutor_imd_seq") })
	@Column(name = "id_tutor_imd", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
     * Pólo do IMD em que o tutor está vinculado para exercer tutoria
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_polo")
	private Polo polo;

	/**
     * Pessoa na qual será atribuído a permissão de tutor do IMD
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;

	/**
     * Data correspondente ao inicio do periodo em que o tutor exercerá tutoria
     */
	@Column(name="data_inicio")
	private Date dataInicio;

	/**
     * Data correspondente ao fim do periodo em que o tutor exercerá tutoria
     */
	@Column(name="data_fim")
	private Date dataFim;
	
	public TutorIMD() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
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
		TutorIMD other = (TutorIMD) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequiredId(getPessoa().getId(), "Pessoa", lista);
		ValidatorUtil.validateRequiredId(getPolo().getId(),"Pólo", lista);
		ValidatorUtil.validateRequired(getDataInicio(),"Data início", lista);
		ValidatorUtil.validateRequired(getDataFim(),"Data fim", lista);
		return lista;
	}
}
