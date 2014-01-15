/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '18/04/2011'
 *
 */
package br.ufrn.sigaa.ava.forum.relacionamentos.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
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
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TipoMaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeral;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Representa o v�nculo de um f�rum com uma turma. 
 * 
 * @author Ilueny Santos
 *
 */
@Entity
@Table(name = "forum_turma", schema = "ava")
@HumanName(value="F�rum", genero='M')
public class ForumTurma extends AbstractMaterialTurma implements DominioTurmaVirtual, Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_forum_turma")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** F�rum da turma. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_forum")
	private ForumGeral forum = new ForumGeral();

	/** Turma do f�rum. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma = new Turma();

	/** T�pico aula onde se concentram as discuss�es do f�rum. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_topico_aula")
	private TopicoAula topicoAula = new TopicoAula();
	
	/** Material Turma. */
	@ManyToOne(fetch=FetchType.EAGER, cascade= CascadeType.ALL)
	@JoinColumn(name = "id_material_turma")
	private MaterialTurma material = new MaterialTurma(TipoMaterialTurma.FORUM);

	/** Indica se � um tipo de f�rum v�lido. Utilizado para exclus�o l�gica. */
	@CampoAtivo
	private boolean ativo;
	
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	public ForumTurma() {
	}

	public ForumTurma(int id) {
		this.id = id;
	}
	
	public ForumGeral getForum() {
		return forum;
	}

	public void setForum(ForumGeral forum) {
		this.forum = forum;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public TopicoAula getAula (){
		return getTopicoAula();
	}
	
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Valida os campos obrigat�rios de um f�rum para turma.
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();		
		if (forum != null) {
			lista.addAll(forum.validate());
		}
		
		ValidatorUtil.validateRequired(turma, "Turma", lista);		
		return lista;
	}

	public TopicoAula getTopicoAula() {
		return topicoAula;
	}

	public void setTopicoAula(TopicoAula topicoAula) {
		this.topicoAula = topicoAula;
	}

	@Override
	public String getMensagemAtividade() {
		return "Novo f�rum cadastrado.";
	}

	public boolean isVincularTopico() {
		return ValidatorUtil.isNotEmpty(topicoAula);
	}

	@Override
	public Usuario getUsuarioCadastro() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getDataCadastro() {
		return getForum().getDataCadastro();
	}

	@Override
	public String getNome() {
		return getForum().getTitulo();
	}

	@Override
	public MaterialTurma getMaterial() {
		return material;
	}

	public void setMaterial(MaterialTurma material) {
		this.material = material;
	}

	@Override
	public String getDescricaoGeral() {
		return forum.getDescricaoSemFormatacao();
	}

}
