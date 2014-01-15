package br.ufrn.rh.dominio;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(schema = "comum", name = "descricao_atividade_modulo")
public class DescricaoAtividadeModulo extends AbstractMovimento implements PersistDB{
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Descrição da atividade de um módulo */
	private String descricao;
	
	/** Módulo do sistema */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_modulo")
	private ModuloSistema modulo;
	
	/** Atributo transiente usado para auxiliar o caso de uso de cadastrar Processo de trabalho */
	@Transient
	private boolean checkd;

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

	public ModuloSistema getModulo() {
		return modulo;
	}

	public void setModulo(ModuloSistema modulo) {
		this.modulo = modulo;
	}

	public void setCheckd(boolean checkd) {
		this.checkd = checkd;
	}

	public boolean isCheckd() {
		return checkd;
	}
	
}
