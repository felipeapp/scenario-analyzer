/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Criado em 29/01/2007 14:59:58
 * Autor: Edson Anibal de Macêdo Reis Batista (ambar@info.ufrn.br)
 */
package br.ufrn.sigaa.cv.dominio;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;


/**
 * Representa uma enquete de uma determinada Turma.
 * 
 * @author Edson Anibal de Macedo Reis Batista (ambar@info.ufrn.br)
 * @author David Pereira
 */
@Entity @Table(name="enquete", schema="cv")
@HumanName(value="Enquete", genero='F')
public class EnqueteComunidade extends MaterialComunidade implements DominioComunidadeVirtual {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id_enquete", nullable=false)
	private int id;

	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_comunidade")
	private ComunidadeVirtual comunidade;
	
	private String pergunta;

	@Transient
	private boolean multiplaEscolha;
	
	@Column(nullable=true)
	private Boolean publicada;
		
	@OneToMany(mappedBy="enquete", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<EnqueteRespostaComunidade> respostas = new ArrayList<EnqueteRespostaComunidade>();
	
	@CriadoPor @ManyToOne @JoinColumn(name="id_usuario")
	private Usuario usuario;

	@CriadoEm @Column(name="data_criacao")
	private Date data;

	/**
	 * Retorna o número total de votos realizados para esta enquete através da
	 * soma da quantidade de votos de cada resposta.
	 * 
	 * @return Total de votos da enquete
	 */
	public int getTotalVotos() {
		int total = 0;
		if (!isEmpty(respostas)) {
			for (EnqueteRespostaComunidade resposta : respostas)
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
			for (EnqueteRespostaComunidade resposta : respostas) {
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

	public List<EnqueteRespostaComunidade> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<EnqueteRespostaComunidade> respostas) {
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
	 * Adiciona uma resposta a Enquete da Comunidade
	 */
	public void adicionarResposta() {
		EnqueteRespostaComunidade resposta = new EnqueteRespostaComunidade();
		resposta.setEnquete(this);
		respostas.add(resposta);
	}
	
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
		return "Nova enquete cadastrada";
	}

	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}

	@Override
	public Date getDataCadastro() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNome() {
		return pergunta;
	}

	@Override
	public String getTipoMaterial() {
		return "Enquete";
	}

	@Override
	public Usuario getUsuarioCadastro() {
		return usuario;
	}
	
}
