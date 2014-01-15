package br.ufrn.rh.dominio;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe que representa relacionamento de um módulo de um sistema com uma atividade.
 * @author Itamir
 *
 */
@Entity
@Table(schema = "comum", name = "modulo_atividade")
public class ModuloAtividade extends AbstractMovimento implements PersistDB{
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_modulo")
	private ModuloSistema modulo;
	
	
	@ManyToMany(fetch = FetchType.EAGER)
 	@JoinTable( name = "proctrab_descricao_atividade", schema = "comum", joinColumns = @JoinColumn(name = "id_processo_trabalho"), inverseJoinColumns = @JoinColumn(name = "id_descricao_atividade_modulo"))
	private List<DescricaoAtividadeModulo> descAtsModulo;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ModuloSistema getModulo() {
		return modulo;
	}

	public void setModulo(ModuloSistema modulo) {
		this.modulo = modulo;
	}

	public List<DescricaoAtividadeModulo> getDescAtsModulo() {
		return descAtsModulo;
	}

	public void setDescAtsModulo(List<DescricaoAtividadeModulo> descAtsModulo) {
		this.descAtsModulo = descAtsModulo;
	}

}
