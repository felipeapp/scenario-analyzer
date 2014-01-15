package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;
import java.util.List;

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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Representa a notifica��o feita aos inscritos em um processo seletivo.
 * @author M�rio Rizzi
 *
 */
@Entity
@Table(name="notificacao_processo_seletivo", schema="ensino")
public class NotificacaoProcessoSeletivo implements Validatable {

	/**
	 * Atributo que define a unicidade da classe.
	 */
	@Id
	@Column(name="id_notificacao_processo_seletivo")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/**
	 * Atributo que define o status dos inscritos que receber�o a mensagem.
	 */
	@Column(name="status_inscricao")
	private String statusInscricao;
	
	/**
	 * Atributo que define o processo seletivo associado.
	 */
	@ManyToOne
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivo processoSeletivo;
	
	/**
	 * Atributo que define o t�tulo da mensagem.
	 */
	private String titulo;
	
	/**
	 * Atributo que define o corpo da mensagem.
	 */
	private String mensagem;
	
	/**
	 * Atributo que define as opera��es executadas.	  
	 */
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	private Date data;
	
	/**
	 * Atributo que define as opera��es executadas.	  
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Carrega a string contendo todos o status da inscri��es para qual a notifica��o foi enviada.
	 * @param status
	 */
	@Transient
	public void carregarStatusInscricao( List<String> status){
		
		StringBuilder strStatusInscricao = new StringBuilder();
		if( !isEmpty(status) ){
			for (String s : status){
				strStatusInscricao.append( getDescricaoStatusInscricao(Integer.valueOf(s)) );
				strStatusInscricao.append( ", " );
			}
			strStatusInscricao.deleteCharAt(strStatusInscricao.length()-2);
		}	
		setStatusInscricao( strStatusInscricao.toString() );
		
	}	
	
	/**
	 * Retorna a descri��o do status da inscri��o
	 * @param status
	 * @return
	 */
	@Transient
	private String getDescricaoStatusInscricao(Integer status){
		return StatusInscricaoSelecao.todosStatus.get(status);
	}
	
	public String getStatusInscricao() {
		return statusInscricao;
	}

	public void setStatusInscricao(String statusInscricao) {
		this.statusInscricao = statusInscricao;
	}
	
	public ProcessoSeletivo getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivo processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Override
	public ListaMensagens validate() {
	
		ListaMensagens erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(getTitulo(), "Assunto", erros);
		ValidatorUtil.validateRequired(getMensagem(), "Mensagem", erros);
		
		return erros;
		
	}
	

}
