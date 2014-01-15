/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 22/10/2010
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;

/**
 * Entidade que registra a preferência de módulo do ensino técnico optada pelo aluno
 * na matrícula por horário.
 * 
 * @author Leonardo Campos
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "opcao_modulo", schema = "ensino", uniqueConstraints = {})
public class OpcaoModulo implements Validatable{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") }) 	
	@Column(name = "id_opcao_modulo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_modulo")
	private Modulo modulo;
	
	private int ordem;
	
	@ManyToOne
	@JoinColumn(name="id_matricula_horario")
	private MatriculaHorario matriculaHorario;
	
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "opcaoModulo")
	@OrderBy("ordem ASC")
	private List<OpcaoHorario> opcoesHorario = new ArrayList<OpcaoHorario>();
	
	public OpcaoModulo() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public MatriculaHorario getMatriculaHorario() {
		return matriculaHorario;
	}

	public void setMatriculaHorario(MatriculaHorario matriculaHorario) {
		this.matriculaHorario = matriculaHorario;
	}

	public List<OpcaoHorario> getOpcoesHorario() {
		return opcoesHorario;
	}

	public void setOpcoesHorario(List<OpcaoHorario> opcoesHorario) {
		this.opcoesHorario = opcoesHorario;
	}
	
	public boolean addOpcaoHorario(OpcaoHorario obj){
		obj.setOpcaoModulo(this);
		return opcoesHorario.add(obj);
	}
	
	public boolean removeOpcaoHorario(OpcaoHorario obj){
		obj.setOpcaoModulo(null);
		return opcoesHorario.remove(obj);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "modulo.id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId(), modulo.getId());
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
}
