/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/04/2009
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/** Aviso/notícia de um Processo Seletivo Vestibular.
 * @author Édipo Elder F. Melo
 *
 */
@Entity
@Table(name = "aviso", schema = "vestibular", uniqueConstraints = {})
public class AvisoProcessoSeletivoVestibular implements PersistDB, Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_aviso")
	private int id;

	/** Chamada (resumo, título) do aviso. */ 
	private String chamada;
	
	/** Corpo do aviso. */
	private String corpo;
	
	/** ID do arquivo anexo. */
	@Column(name="id_arquivo")
	private Integer idArquivo;
	
	/** Data/hora do início da publicação do aviso. */
	@Column(name="inicio_publicacao")
	private Date inicioPublicacao;
	
	/** Processo Seletivo ao qual o aviso pertence. */
	@ManyToOne
	@JoinColumn(name = "id_processo_seletivo", unique = false, nullable = true, insertable = true, updatable = true)
	private ProcessoSeletivoVestibular processoSeletivo;

	/** Indica se o aviso é ativo (visível) ou não. */
	private boolean ativo;
	
	/** Construtor padrão. */
	public AvisoProcessoSeletivoVestibular() {
		processoSeletivo = new ProcessoSeletivoVestibular();
		ativo = true;
	}
	
	/** Retorna o ID.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta o ID.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
		
	}

	/** Valida se a chamada da notícia e a data de publicação não são vazias. 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();
		if ( ValidatorUtil.isEmpty(this.chamada)) 
			mensagens.addErro("A chamada do aviso não pode ser vazia");
		if ( ValidatorUtil.isEmpty(this.processoSeletivo)) 
			mensagens.addErro("Selecione um Processo Seletivo válido");
		if (ValidatorUtil.isEmpty(this.inicioPublicacao))
			mensagens.addErro("Informe uma data de publicação válida");
		else
			ValidatorUtil.validateRequired(inicioPublicacao, "Data de início de publicação", mensagens);
		return mensagens;
	}

	/** Retorna a chamada (resumo, título) do aviso. 
	 * @return
	 */
	public String getChamada() {
		return chamada;
	}

	/** Seta a chamada (resumo, título) do aviso.
	 * @param chamada
	 */
	public void setChamada(String chamada) {
		this.chamada = chamada;
	}

	/** Retorna o corpo do aviso. 
	 * @return
	 */
	public String getCorpo() {
		return corpo;
	}

	/** Seta o corpo do aviso.
	 * @param corpo
	 */
	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}

	/** Retorna o ID do arquivo anexo. 
	 * @return
	 */
	public Integer getIdArquivo() {
		return idArquivo;
	}

	/** Seta o ID do arquivo anexo. 
	 * @param idArquivo
	 */
	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	/** Retorna o Processo Seletivo ao qual o aviso pertence. 
	 * @return
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	/** Seta o Processo Seletivo ao qual o aviso pertence. 
	 * @param processoSeletivo
	 */
	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	/** Retorna a data/hora do início da publicação do aviso. 
	 * @return
	 */
	public Date getInicioPublicacao() {
		return inicioPublicacao;
	}

	/** Seta a data/hora do início da publicação do aviso.
	 * @param inicioPublicacao
	 */
	public void setInicioPublicacao(Date inicioPublicacao) {
		this.inicioPublicacao = inicioPublicacao;
	}

	/** Retorna uma representação textual do aviso no formato: data, seguido de vírgula,
	 * seguido da chamada, seguido de vírgula, seguido do texto "anexo" - caso possua arquivo anexo.
	 */
	@Override
	public String toString() {
		return getInicioPublicacao() + ", " + getChamada() + ", "
				+ (idArquivo != null && idArquivo > 0 ? "anexo" : "");
	}

	/** Indica se o aviso é ativo (visível) ou não. 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se o aviso é ativo (visível) ou não. 
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}
