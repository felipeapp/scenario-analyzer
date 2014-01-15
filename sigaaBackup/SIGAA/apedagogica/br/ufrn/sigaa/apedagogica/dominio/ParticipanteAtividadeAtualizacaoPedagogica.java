package br.ufrn.sigaa.apedagogica.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.HashMap;
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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.nee.dominio.RecursoNee;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * 
 * Representa um docente que se inscreveu na atividade de atualização
 * pedagógica.
 * 
 * @author Gleydson
 *
 */
@Entity
@Table(name = "participante_atividade_atualizacao_pedagogica", schema = "apedagogica")
public class ParticipanteAtividadeAtualizacaoPedagogica implements Validatable, ViewAtividadeBuilder {

	/**
	 * Atributo que define a unicidade.
	 */
	@Id
	@Column(name = "id_participante_atividade_atualizacao_pedagogica")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/**
	 * Atributo que define o docente que vai participar da atividade.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor")
	private Servidor docente;
	
	/**
	 * Atributo que define a atividade que o doncente vai participar.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_atividade_atualizacao_pedagogica")
	private AtividadeAtualizacaoPedagogica atividade;
	
	/**
	 * Lista de recursos NEE solicitados pelo participante.
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "participante")
	private List<RecursoNee> recursosNee;
	
	/**
	 * Atributo que define a situação da inscrição.
	 * @see br.ufrn.sigaa.apedagogica.dominio.StatusParticipantesAtualizacaoPedagogica 
	 */
	private Integer situacao;
	
	/**
	 * Atributo utilizado na view's dos casos de uso onde se faz necessário selecionar mais de um particpante 
	 * na listagem.
	 */
	@Transient
	private boolean selecionado = false;
	
	/**
	 * Se o participante selecionou recurso NEE.
	 */
	@Column(name="solicitacao_recurso_nee") 
	private Boolean solicitacaoRecursoNEE;
	
	public ParticipanteAtividadeAtualizacaoPedagogica(){
		this.docente = new Servidor();
		this.atividade = new AtividadeAtualizacaoPedagogica();
		this.situacao = StatusParticipantesAtualizacaoPedagogica.INSCRITO.getId();
		this.solicitacaoRecursoNEE = false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public AtividadeAtualizacaoPedagogica getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeAtualizacaoPedagogica atividade) {
		this.atividade = atividade;
	}

	public Integer getSituacao() {
		return situacao;
	}

	public void setSituacao(Integer situacao) {
		this.situacao = situacao;
	}
	
	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	/**
	 * Retorna a descrição da situação do participante.
	 * @return
	 */
	public String getDescricaoSituacao(){
		
		if( isEmpty(situacao) )
			return "Pendente";
		
		else if( situacao.equals(StatusParticipantesAtualizacaoPedagogica.AUSENTE.getId()) )
			return "Ausente";
		else if( situacao.equals(StatusParticipantesAtualizacaoPedagogica.CONCLUIDO.getId()) )
			return "Presente";
		else if( situacao.equals(StatusParticipantesAtualizacaoPedagogica.INSCRITO.getId()) )
			return "Inscrito";
		
		return null; 
		
	}
	
	/**
	 * Indica se participante está inscrito
	 * @return
	 */
	public boolean isInscrito(){
		return !isEmpty(situacao) && situacao.equals(StatusParticipantesAtualizacaoPedagogica.INSCRITO.getId());
	}
	
	/**
	 * Indica se participante está ausente
	 * @return
	 */
	public boolean isAusente(){
		return !isEmpty(situacao) && situacao.equals(StatusParticipantesAtualizacaoPedagogica.AUSENTE.getId());
	}
	
	/**
	 * Indica se participante concluíu.
	 * @return
	 */
	public boolean isConcluido(){
		return !isEmpty(situacao) && situacao.equals(StatusParticipantesAtualizacaoPedagogica.CONCLUIDO.getId());
	}
	
	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder strClass = new StringBuilder();
		strClass.append("Docente: " + getDocente().getPessoa().getNome() + " (" + getAtividade() + ")");
		return strClass.toString();
	}

	public String getItemView() {
		return "  <td>" + getAtividade().getNome() + "</td>" +
			   "  <td style=\"text-align:center\">" + getAtividade().getDescricaoPeriodo() + "</td>";
	}

	public String getTituloView() {
		return  "  <td>Atividade</td>" +
				"  <td style=\"text-align:center;width: 145px;\">Período</td>";
	}

	/**
	 * Retorna os mapas formatado com o nome da atividade e o período em que se aplica.
	 */
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("nome", null);
		itens.put("periodo", null);
		return itens;
	}
	
	@Override
	public float getQtdBase() {
		// TODO Auto-generated method stub
		return 1;
	}

	public void setRecursosNee(List<RecursoNee> recursosNee) {
		this.recursosNee = recursosNee;
	}

	public List<RecursoNee> getRecursosNee() {
		return recursosNee;
	}

	public Boolean getSolicitacaoRecursoNEE() {
		return solicitacaoRecursoNEE;
	}

	public void setSolicitacaoRecursoNEE(Boolean solicitacaoRecursoNEE) {
		this.solicitacaoRecursoNEE = solicitacaoRecursoNEE;
	}		
	
}
