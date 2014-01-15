/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe que representa um r�tulo utilizado na organiza��o
 * dos t�picos de aula da turma. 
 * 
 * @author Ilueny Santos
 *
 */
@Entity @HumanName(value="R�tulo", genero='M')
@Table(name = "rotulo_turma", schema = "ava")
public class RotuloTurma extends AbstractMaterialTurma implements DominioTurmaVirtual {

	/** Identificador �nico para o objeto. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
			parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_rotulo_turma")
	private int id;

	/** Descri��o do r�tulo. */
	private String descricao;

	/** Habilita a exibi��o do r�tulo no t�pico de aula. */
	private boolean visivel = true;

	/** Data de cadastro do r�tulo. */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Indica se o r�tulo est� ativo no sistema. Utilizado para exclus�o l�gica. */
	@CampoAtivo
	private boolean ativo;
	
	/** Usu�rio autor do r�tulo. */
	@CriadoPor 
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="id_usuario")
	private Usuario usuarioCadastro;

	/** T�pico de aula ao qual o r�tulo faz refer�ncia. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_topico_aula")
	private TopicoAula aula;

	/** Material Turma. */
	@ManyToOne(fetch=FetchType.EAGER, cascade= CascadeType.ALL)
	@JoinColumn(name = "id_material_turma")
	private MaterialTurma material = new MaterialTurma(TipoMaterialTurma.ROTULO);

	
	/** Construtor padr�o. */
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
			lista.addErro("Digite um conte�do para o r�tulo");
		}
		ValidatorUtil.validateRequired(aula, "T�pico de Aula", lista);

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
		return "Novo conte�do adicionado";
	}

	@Override
	public String getDescricaoGeral() {
		return "";
	}

}

