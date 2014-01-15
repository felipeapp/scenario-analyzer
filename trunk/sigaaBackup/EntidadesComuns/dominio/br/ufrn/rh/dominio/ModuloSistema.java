package br.ufrn.rh.dominio;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe que representa os modulos de um sistema cadastrado durante o cadastro do processo de trabalho.
 * @author Itamir
 *
 */
@Entity
@Table(schema = "comum", name = "modulo_sistema")
public class ModuloSistema extends AbstractMovimento implements PersistDB{
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Nome do módulo */
	private String modulo;
	
	/** Sistema ao qual o módulo pertence */
	private int sistema;
	
	/** Atributo transiente usado para auxiliar o caso de uso de cadastrar Processo de trabalho */
	@Transient
	private boolean checkd;
	
	@Transient
	private List<DescricaoAtividadeModulo> descricoesAtMod;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public int getSistema() {
		return sistema;
	}

	public void setSistema(int sistema) {
		this.sistema = sistema;
	}

	public List<DescricaoAtividadeModulo> getDescricoesAtMod() {
		return descricoesAtMod;
	}

	public void setDescricoesAtMod(List<DescricaoAtividadeModulo> descricoesAtMod) {
		this.descricoesAtMod = descricoesAtMod;
	}

	public void setCheckd(boolean checkd) {
		this.checkd = checkd;
	}

	public boolean isCheckd() {
		return checkd;
	}
}
