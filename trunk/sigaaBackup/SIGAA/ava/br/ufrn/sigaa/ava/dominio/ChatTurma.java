/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '13/06/2011'
 *
 */
package br.ufrn.sigaa.ava.dominio;

import java.text.SimpleDateFormat;
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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Representa o chat aberto para discuss�o entre usu�rios da turma. 
 * 
 * @author Ilueny Santos
 *
 */
@Entity @HumanName(value="Chat Agendado", genero='M')
@Table(name = "chat_turma", schema = "ava")
public class ChatTurma extends AbstractMaterialTurma implements DominioTurmaVirtual {
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_chat_turma")           
	private int id;
	
	/** T�tulo do chat. */
	private String titulo;
	
	/** Descri��o do chat. */
	private String descricao;

	/** Turma ao qual o chat est� vinculado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;

	/** Usu�rio criador do chat. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	/** Data de in�cio do chat. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", nullable = false)
	private Date dataInicio;
	
	/** Data de fechamento do chat. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim", nullable = false)
	private Date dataFim;

	/** Hora de inicio do chat. */
	@Temporal(TemporalType.TIME)
	@Column(name = "hora_inicio", nullable = false)
	private Date horaInicio;
	
	/** Hora de fechamento do chat. */
	@Temporal(TemporalType.TIME)
	@Column(name = "hora_fim", nullable = false)
	private Date horaFim;
	
	
	/** Data de cadastro do chat. */
	@CriadoEm
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	/** Campo utilizado para exclus�o l�gica do registro no banco. */
	@CampoAtivo
	private boolean ativo;

	/** Lista com todas as mensagens enviadas para o chat. */
	private String conteudo;
	
	/** Permite a publica��o do conte�do integral do chat ap�s o seu fechamento. */
	@Column(name = "publicar_conteudo")
	private boolean publicarConteudo = true;
	
	/** Indica se o docente quer fazer um chat exibindo a imagem de sua webcam. */
	@Column(name = "video_chat")
	private boolean videoChat = false;

	/** T�pico de aula ao qual o chat parte. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_topico_aula")
	private TopicoAula aula;

	/** Material Turma. */
	@ManyToOne(fetch=FetchType.EAGER, cascade= CascadeType.ALL)
	@JoinColumn(name = "id_material_turma")
	private MaterialTurma material = new MaterialTurma(TipoMaterialTurma.CHAT);
	

	/** Construtor padr�o. */
	public ChatTurma() {
	}
	
	public ChatTurma(int id) {
		this.id = id;
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public boolean isPublicarConteudo() {
		return publicarConteudo;
	}

	public void setPublicarConteudo(boolean publicarConteudo) {
		this.publicarConteudo = publicarConteudo;
	}

	@Override
	public String getMensagemAtividade() {		
		return "Novo chat agendado.";
	}

	public TopicoAula getAula() {
		return aula;
	}

	public void setAula(TopicoAula aula) {
		this.aula = aula;
	}

	public MaterialTurma getMaterial() {
		return material;
	}

	public void setMaterial(MaterialTurma material) {
		this.material = material;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public Usuario getUsuarioCadastro() {
		return usuario;
	}

	@Override
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
	@Override
	public String getNome() {
		return titulo;
	}

	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(obj, this, "id", "titulo");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((titulo == null) ? 0 : titulo.hashCode());
		return result;
	}

	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Date getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}
	
	@Override
	public String toString() {
		return titulo;
	}
	
	/** Retorna a data e hora de in�cio da grava��o do chat. */
	public Date getDataHoraInicio() {
		return CalendarUtils.definirHorario(dataInicio, horaInicio);
	}

	/** Retorna a data e hora de fim da grava��o do chat. */
	public Date getDataHoraFim() {
		return CalendarUtils.definirHorario(dataFim, horaFim);
	}

	/** Verifica se a grava��o do chat j� iniciou. */
	public boolean isChatIniciou() {
		return new Date().after(getDataHoraInicio());
	}

	/** Verifica se a grava��o do chat j� terminou. */
	public boolean isChatTerminou() {
		return new Date().after(getDataHoraFim());
	}

	public boolean isVideoChat() {
		return videoChat;
	}

	public void setVideoChat(boolean videoChat) {
		this.videoChat = videoChat;
	}

	@Override
	public String getDescricaoGeral() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
		String result = "Inicia em " + sdf.format(dataInicio) + " �s " + stf.format(horaInicio) + 
				" e finaliza em " + sdf.format(dataFim) + " �s " + stf.format(horaFim);
		return result;
	}

}