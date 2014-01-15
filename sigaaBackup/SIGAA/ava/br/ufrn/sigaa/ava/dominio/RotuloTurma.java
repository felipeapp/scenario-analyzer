/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '12/05/2011'
 *
 */
package br.ufrn.sigaa.ava.dominio;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;


/***
 * Classe que representa um rótulo utilizado na organização
 * dos tópicos de aula da turma. 
 * 
 * @author Ilueny Santos
 *
 */
@Entity @HumanName(value="Rótulo", genero='M')
@Table(name = "rotulo_turma", schema = "ava")
public class RotuloTurma extends AbstractMaterialTurma implements DominioTurmaVirtual {

	/** Identificador único para o objeto. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
			parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_rotulo_turma")
	private int id;

	/** Descrição do rótulo. */
	private String descricao;

	/** Habilita a exibição do rótulo no tópico de aula. */
	private boolean visivel = true;

	/** Data de cadastro do rótulo. */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Indica se o rótulo está ativo no sistema. Utilizado para exclusão lógica. */
	@CampoAtivo
	private boolean ativo;
	
	/** Usuário autor do rótulo. */
	@CriadoPor 
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="id_usuario")
	private Usuario usuarioCadastro;

	/** Tópico de aula ao qual o rótulo faz referência. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_topico_aula")
	private TopicoAula aula;

	/** Material Turma. */
	@ManyToOne(fetch=FetchType.EAGER, cascade= CascadeType.ALL)
	@JoinColumn(name = "id_material_turma")
	private MaterialTurma material = new MaterialTurma(TipoMaterialTurma.ROTULO);

	
	/** Construtor padrão. */
	public RotuloTurma() {
	}
	
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

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (descricao == null || "".equals(descricao.trim())) {
			lista.addErro("Digite um conteúdo para o rótulo");
		}
		ValidatorUtil.validateRequired(aula, "Tópico de Aula", lista);

		return lista;
	}

	@Override
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	@Override
	public String getNome() {
		return descricao;
	}

	public boolean isVisivel() {
		return visivel;
	}

	public void setVisivel(boolean visivel) {
		this.visivel = visivel;
	}

	@Override
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

	public MaterialTurma getMaterial() {
		return material;
	}

	public void setMaterial(MaterialTurma material) {
		this.material = material;
	}

	public TopicoAula getAula() {
		return aula;
	}

	public void setAula(TopicoAula aula) {
		this.aula = aula;
	}

	@Override
	public void setTurma(Turma turma) {
		material.setTurma(turma);
	}

	@Override
	public Turma getTurma() {
		return material.getTurma();
	}

	@Override
	public String getMensagemAtividade() {
		return "Novo conteúdo adicionado";
	}

	@Override
	public String getDescricaoGeral() {
		return "";
	}

}

