/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
 * Created on 21/08/2013
*/
package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Registro da altera��o de um docente de ensino em rede.
 * @author Diego J�come
 *
 */
@Entity
@Table(name = "alteracao_situacao_docente_rede", schema = "ensino_rede")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AlteracaoSituacaoDocenteRede implements PersistDB {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="ensino_rede.alteracao_situacao_docente_rede_seq") }) 	
	@Column(name = "id_alteracao_docente_rede", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Docente no qual a altera��o se refere */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name = "id_docente_rede")
	private DocenteRede docente;
	
	/** Antiga situa��o do docente */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name = "id_situacao_antiga")
	private SituacaoDocenteRede situacaoAntiga;
	
	/** Situa��o ap�s a opera��o */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name = "id_situacao_nova")
	private SituacaoDocenteRede situacaoNova;
	
	/** Usu�rio que realizou a opera��o */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	/** Registro de entrada da opera��o */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registro;
	
	/** Data da opera��o */
	@Column(name="data_alteracao")
	private Date dataAlteracao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DocenteRede getDocente() {
		return docente;
	}

	public void setDocente(DocenteRede docente) {
		this.docente = docente;
	}

	public SituacaoDocenteRede getSituacaoAntiga() {
		return situacaoAntiga;
	}

	public void setSituacaoAntiga(SituacaoDocenteRede situacaoAntiga) {
		this.situacaoAntiga = situacaoAntiga;
	}

	public SituacaoDocenteRede getSituacaoNova() {
		return situacaoNova;
	}

	public void setSituacaoNova(SituacaoDocenteRede situacaoNova) {
		this.situacaoNova = situacaoNova;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public RegistroEntrada getRegistro() {
		return registro;
	}

	public void setRegistro(RegistroEntrada registro) {
		this.registro = registro;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}
	
	/** Descri��o da opera��o segundo a situa��o antiga e nova. */
	public String getDescricaoAlteracao () {
		if (situacaoAntiga != null && situacaoAntiga.getId() == situacaoNova.getId())
			return "MANUTEN��O";
		else if (situacaoNova.getId() == SituacaoDocenteRede.DESLIGADO)
			return "DESLIGAMENTO";
		else if (situacaoNova.getId() == SituacaoDocenteRede.ATIVO)
			return "ATIVA��O";
		else if (situacaoAntiga == null && situacaoNova.getId() == SituacaoDocenteRede.PENDENTE)
			return "SOLICITA��O DE CADASTRO";
		else if (situacaoAntiga != null && situacaoNova.getId() == SituacaoDocenteRede.PENDENTE)
			return "SUSPENS�O";
		else
			return "DESCONHECIDO";
	}
	
}
