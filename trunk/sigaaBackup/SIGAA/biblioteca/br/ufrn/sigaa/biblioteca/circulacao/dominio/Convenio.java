package br.ufrn.sigaa.biblioteca.circulacao.dominio;

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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;

/**
 * Entidade que representa os conv�nios da biblioteca.
 * 
 * @author Fred_Castro
 *
 */

@Entity
@Table (name="convenio", schema="biblioteca")
public class Convenio implements Validatable{
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_convenio")
	private int id;
	
	/** Nome do v�nculo. */
	private String nome;
	
	@Temporal(TemporalType.DATE)
	@Column(name="data_inicio")
	/** Data de in�cio do v�nculo. */
	private Date dataInicio;
	
	@Temporal(TemporalType.DATE)
	@Column(name="data_fim")
	/** Data em que o v�nculo termina. */
	private Date dataFim;
	
	private boolean ativo = true;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	/** Registro entrada do usu�rio que cadastrou. */
	private RegistroEntrada registroCadastro;
	
	@Column(name="data_cadastro")
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	/** Data de cadastro. */
	private Date dataCadastro;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	/** Registro entrada do usu�rio que realizou a �ltima atualiza��o. */
	private RegistroEntrada registroAtualizacao;

	@Column(name="data_atualizacao")
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	/** Data da �ltima atualiza��o. */
	private Date dataAtualizacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public ListaMensagens validate() {
		ListaMensagens l = new ListaMensagens();
		
		if (StringUtils.isEmpty(nome))
			l.addErro("Informe um nome para o conv�nio.");
		
		if (dataInicio == null)
			l.addErro("Informe a data de in�cio para o conv�nio.");
		
		if (dataFim == null)
			l.addErro("Informe a data para o fim do conv�nio.");
		
		if (dataInicio != null && dataFim != null && CalendarUtils.estorouPrazo(dataFim, dataInicio))
			l.addErro("A data final deve ser maior que a inicial.");
			
		return l;
	}
}
