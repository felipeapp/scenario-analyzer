package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
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
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 * <p>Classe que guarda as informações sobre as interrupções no funcionamento das bibliotecas.</p>
 * 
 * <p>Interrupções são dias em que o setor de circulação não vai funcionar e nenhum empréstimos deve cair esse dia.
 *  Se existir algum empréstimo com data de devolução igual a data de algum interrução da biblioteca existe uma iconsistência nos dados do sistema.
 *  </p>
 * 
 * @author Fred_Castro
 *
 */

@Entity
@Table (name="interrupcao_biblioteca", schema="biblioteca")
public class InterrupcaoBiblioteca implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_interrupcao_biblioteca", nullable = false)
	private int id;
	
	/** O motivo pelo qual o funcionamento das bibliotecas será interrompido. */
	private String motivo;
	
	/** A data em que a biblioteca não vai funcionar. */
	@Temporal(TemporalType.DATE)
 	private Date data;
	
	/////////////////////// informações para auditoria ///////////////////////
	
	/** Data de cadastro. */
	@CriadoEm
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	/** Registro entrada do usuário que cadastrou. */
	@CriadoPor
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	private RegistroEntrada registroCadastro;
	
	
	/**
	 * Registro entrada do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * Data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	///////////////////////////////////////////////////////////////////////////
	
	/** As bibliotecas que utilizam deste feriado. */
	@ManyToMany(targetEntity=Biblioteca.class, cascade={})
    @JoinTable(name="biblioteca_interrupcao_biblioteca", schema="biblioteca", joinColumns=@JoinColumn(name="id_interrupcao_biblioteca"), inverseJoinColumns=@JoinColumn(name="id_biblioteca"))
	private List <Biblioteca> bibliotecas;
	
	/** Caso uma interrupção seja removida. Somente iterrupção que não alteraram o prazo dos empréstimos poderam ser removidas do sistema. */
	private boolean ativo = true;
	
	
	public InterrupcaoBiblioteca() {
	}

	public InterrupcaoBiblioteca(int id) {
		this.id = id;
	}


	/**
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens m = new ListaMensagens ();
		
		if (StringUtils.isEmpty(motivo))
			m.addErro("O motivo da interrupção deve ser informado.");
		
		if (data == null)
			m.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Interrupção");
		
		if (bibliotecas == null || bibliotecas.isEmpty())
			m.addErro("Selecione pelo menos uma biblioteca.");

		return m;
	}
	
	
	////////////////// sets e gets ////////////////////////
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public List<Biblioteca> getBibliotecas() {
		return bibliotecas;
	}

	public void setBibliotecas(List<Biblioteca> bibliotecas) {
		this.bibliotecas = bibliotecas;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
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
	
}