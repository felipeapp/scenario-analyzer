/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 01/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.Date;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;

/**
 *
 * <p>A modalidade do participante de um curso ou evento de extensão.  
 *  <strong>$$$$$ A modalidade define o valor da taxa a ser cobrada pelo curso ou evento $$$$$</strong> 
 *  </p>
 *
 * <p> <i> No momento de se criar o período de inscrição para cursos ou eventos com taxa, o 
 * coordenador informa quais modalidades o curso ou evento vai ter. E para cada modalidade o valor da taxa cobrada.</i> </p>
 *
 * <p> <i> No momento de se inscrever o usuário escolhe uma das modalidades associadas, a modalidade que ele 
 * escolher definirar o valor a ser pago.</i> </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(schema = "extensao", name = "modalidade_participante")
public class ModalidadeParticipante implements Validatable{

	/** O id */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="extensao.modalidade_participante_sequence") })
	@Column(name = "id_modalidade_participante", nullable = false)
	private int id;

	/** Nome que identifica a modalidade para o usuário. */
	@Column(name="nome", nullable=false)
	private String nome;
	
	
	/** Para novas inscrições apenas as modalidades ativas são mostradas. 
	 * Caso alguma seja removida a inscrição continua como ela, apenas as novas já não 
	 * vai aparecer a modalidade para ser escolhida.*/
	@Column(name="ativo", nullable=false)
	private boolean ativo = true;

	
	////////////////////////     Informações de auditoria  ////////////////////////////////
	
	/** Data da inscrição. */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	
	/** Registro de entrada do responsável pelo cadastro do participante na ação de extensão. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;
	
	
	/** Data de cadastro. */
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_ultima_atualizacao")
	private Date dataUltimaAtualizacao;
	
	/** Registro de entrada do responsável pelo cadastro da inscrição. */
	@AtualizadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	private RegistroEntrada registroUltimaAtualizacao;
	
	////////////////////////   Informações de auditoria  ////////////////////////////////
	
	
	
	public ModalidadeParticipante(){
		
	}
	
	
	public ModalidadeParticipante(int id){
		this.id = id;
	}

	/**
	 * @param idModalidade
	 * @param nomeModalidade
	 * @param ativa
	 */
	public ModalidadeParticipante(int idModalidade, String nomeModalidade, boolean ativo) {
		this.id = idModalidade;
		this.nome = nomeModalidade;
		this.ativo = ativo;
	}


	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId());
	}


	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	
	///// sets e gets ////
	
	


	public int getId() {	return id; }
	public void setId(int id) {	this.id = id;}
	public String getNome() {	return nome;}
	public void setNome(String nome) {	this.nome = nome;}
	public boolean isAtivo() {	return ativo;}
	public void setAtivo(boolean ativo) {this.ativo = ativo;}
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();

		if(StringUtils.isEmpty(nome))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
		
		return mensagens;
	}


	public Date getDataCadastro() {
		return dataCadastro;
	}


	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	public void setRegistroUltimaAtualizacao(RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}
	
}
