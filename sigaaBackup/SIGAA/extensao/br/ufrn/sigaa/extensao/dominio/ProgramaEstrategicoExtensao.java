package br.ufrn.sigaa.extensao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;

@Entity
@Table(schema = "extensao", name = "programa_estrategico_extensao")
public class ProgramaEstrategicoExtensao implements Validatable {

    @Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_programa_estrategico", unique = true, nullable = false)
    private int id;
    
    private String descricao;
    
	/** Define a data em que a série foi cadastrada. */
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Indica se a série está ativa, permitindo sua utilização no SIGAA. */
	@CampoAtivo(true)
	@Column(name = "ativo", nullable = false)
	private boolean ativo = true;

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

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}