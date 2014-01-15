package br.ufrn.sigaa.apedagogica.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe de domínio que representa o instrutor de uma atividade de atualização pedagógica. 
 * @author Mário Rizzi
 *
 */
@Entity
@Table(name = "instrutor_atividade_atualizacao_pedagogica", schema = "apedagogica")
public class InstrutorAtividadeAtualizacaoPedagogica implements Validatable {

	/**
	 * Atributo que define a unicidade.
	 */
	@Id
	@Column(name = "id_instrutor_atividade_atualizacao_pedagogica")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/**
	 * Atributo que define a pessoa associado ao instrutor.
	 */
	@OneToOne
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;
	
	/**
	 * Atributo que define a atividade que o instrutor está associado.
	 */
	@OneToOne
	@JoinColumn(name = "id_atividade_atualizacao_pedagogica")
	private AtividadeAtualizacaoPedagogica atividade;
	
	public InstrutorAtividadeAtualizacaoPedagogica(){
		this.pessoa = new Pessoa();
		this.atividade = new AtividadeAtualizacaoPedagogica();
	}
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
		
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public AtividadeAtualizacaoPedagogica getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeAtualizacaoPedagogica atividade) {
		this.atividade = atividade;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "pessoa", "atividade");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, pessoa, atividade);
	}

}
