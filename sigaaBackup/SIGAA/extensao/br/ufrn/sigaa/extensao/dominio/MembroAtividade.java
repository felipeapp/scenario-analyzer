package br.ufrn.sigaa.extensao.dominio;

import java.util.Collection;

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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

@Entity
@Table(schema = "extensao", name = "membro_atividade")
public class MembroAtividade implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_membro_atividade")
	private int id;

	/** Deve ser definido para todos */ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_membro_projeto", nullable = false)
	private MembroProjeto membroProjeto = new MembroProjeto();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_objetivo_atividade", unique = false, nullable = true, insertable = true, updatable = true)
	private ObjetivoAtividades objetivoAtividade;

	@Column(name = "carga_horaria")
	private int cargaHoraria;

	/** Indica se o componente é ativo ou não */
	@CampoAtivo(true)
	private boolean ativo = true;
	
	public MembroAtividade() {
	}

	public MembroAtividade( int idMembroProjeto ) {
		this.membroProjeto.setId(idMembroProjeto);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MembroProjeto getMembroProjeto() {
		return membroProjeto;
	}

	public void setMembroProjeto(MembroProjeto membroProjeto) {
		this.membroProjeto = membroProjeto;
	}

	public ObjetivoAtividades getObjetivoAtividade() {
		return objetivoAtividade;
	}

	public void setObjetivoAtividade(ObjetivoAtividades objetivoAtividade) {
		this.objetivoAtividade = objetivoAtividade;
	}

	public int getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(int cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "membroProjeto.id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(membroProjeto.getId());
	}
	
	public static boolean containsMembro(Collection<MembroAtividade> membrosAtividade, MembroProjeto membroProjeto) {
		for (MembroAtividade membroAtividade : membrosAtividade) {
			if ( membroAtividade.getMembroProjeto().getId() == membroProjeto.getId() )
				return true;
		}
		return false;
	}
	
	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}