/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Criado em 29/01/2007 14:59:58
 * Autor: Edson Anibal de Macêdo Reis Batista (ambar@info.ufrn.br)
 */
package br.ufrn.sigaa.ava.dominio;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;


/**
 * Classe de domínio que representa uma enquete de uma determinada Turma.
 * 
 * @author Edson Anibal de Macedo Reis Batista (ambar@info.ufrn.br)
 * @author David Pereira
 */
@Entity @HumanName(value="Enquete", genero='F') 
@Table(name="enquete", schema="ava")
public class Enquete extends AbstractMaterialTurma implements DominioTurmaVirtual {

	/** Chave Primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_enquete", nullable = false)
	private int id;

	/** Turma da enquete. */
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_turma")
	private Turma turma;
	
	/** Pergunta da enquete. */
	private String pergunta;

	/** Se a enquete é de multiplaEscolha */
	@Transient
	private boolean multiplaEscolha;
	
	/** Se a enquete deve ser publicada. */
	@Column(nullable=true)
	private Boolean publicada;
	
	/** Referencia o tópico de aula ao qual esta enquete está associado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_topico_aula", nullable = true )
	private TopicoAula aula;
	
	/** Data em que a enquete finaliza. */
	@Column(name="data_fim")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataFim;
	/** Respostas da enquete */
	@OneToMany(mappedBy="enquete", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<EnqueteResposta> respostas = new ArrayList<EnqueteResposta>();
	
	/** Usuário que cadastrou a enquete.  */
	@CriadoPor @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_usuario")
	private Usuario usuario;

	/** Data de cadastro da enquete. */
	@CriadoEm @Column(name="data_criacao")
	private Date data;

	/** Material Turma. */
	@ManyToOne(fetch=FetchType.EAGER, cascade= CascadeType.ALL)
	@JoinColumn(name = "id_material_turma")
	private MaterialTurma material = new MaterialTurma(TipoMaterialTurma.ENQUETE);
	

	/**
	 * Retorna o número total de votos realizados para esta enquete através da
	 * soma da quantidade de votos de cada resposta.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/portais/turma/cabecalho.jsp</li>
	 * </ul>
	 * @return Total de votos da enquete
	 */
	public int getTotalVotos() {
		int total = 0;
		if (!isEmpty(respostas)) {
			for (EnqueteResposta resposta : respostas)
				total += resposta.getTotalVotos();
		}
		return total;
	}
	
	/**
	 * Identifica se um usuário já votou anteriormente
	 * na enquete.
	 */
	public boolean usuarioJaVotou(Usuario usuario) {
		if (!isEmpty(respostas)) {
			for (EnqueteResposta resposta : respostas) {
				return resposta.usuarioVotou(usuario);
			}
		}
		
		return false;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	public boolean isMultiplaEscolha() {
		return multiplaEscolha;
	}

	public void setMultiplaEscolha(boolean multiplaEscolha) {
		this.multiplaEscolha = multiplaEscolha;
	}

	public List<EnqueteResposta> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<EnqueteResposta> respostas) {
		this.respostas = respostas;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	/**
	 * Adiciona uma resposta a enquete.
	 * Método não invocado por JSPs.
	 */
	public void adicionarResposta() {
		EnqueteResposta resposta = new EnqueteResposta();
		resposta.setEnquete(this);
		respostas.add(resposta);
	}
	
	/**
	 * Indica se já expirou o prazo de votação.
	 */
	public boolean isFimPrazoVotacao (){
		if (dataFim == null)
			return false;
		
		return dataFim.getTime() < new Date().getTime();
	}
	/** Remove uma resposta de uma enquete.
	 *	Não invocado por JSPs. 
	 */
	public void removerResposta(int index) {
		respostas.remove( index );
	}

	public Boolean getPublicada() {
		return publicada;
	}

	public void setPublicada(Boolean publicada) {
		this.publicada = publicada;
	}

	public String getMensagemAtividade() {
		return "Nova Enquete: " + pergunta;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	public TopicoAula getAula() {
		return aula;
	}

	public void setAula(TopicoAula aula) {
		this.aula = aula;
	}
	
	/**
	 * Indica se a data final da enquete já passou. Retorna true caso não tenha data fim.
	 * @return
	 */
	public boolean isPrazoExpirado (){
		if (dataFim == null)
			return false;
		
		if (dataFim.getTime() < new Date().getTime())
			return true;
		
		return false;
	}

	@Override
	public Usuario getUsuarioCadastro() {
		return usuario;
	}

	@Override
	public Date getDataCadastro() {
		return data;
	}

	@Override
	public String getNome() {
		return pergunta;
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
		if (dataFim != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String result = "Finaliza em " + sdf.format(dataFim);
			return result;
		}
		return null;
	}

}