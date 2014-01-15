/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '25/05/2009'
 *
 */
package br.ufrn.sigaa.diploma.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/** Representa um registro de diploma.
 * 
 * @author �dipo Elder F. Melo
 *
 */
@Entity
@Table(schema = "diploma", name = "registro_diploma")
public class RegistroDiploma implements PersistDB, Validatable {
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_registro_diploma")
	private int id;
	
	/** Discente a quem ser� emitido o diploma. */
	@OneToOne
	@JoinColumn(name = "id_discente")
	private Discente discente;
	
	/** N�mero do protocolo do diploma. */
	private String processo;
	
	/** N�mero do registro do diploma. */
	@Column(name = "numero_registro")
	private Integer numeroRegistro;
	
	/** Folha do registro do diploma. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_folha_registro_diploma",nullable=false)
	private FolhaRegistroDiploma folha;

	/** Data de registro do diploma. */
	@Column(name = "data_registro")
	private Date dataRegistro;
	
	/** Data de expedi��o do diploma. */
	@Column(name = "data_expedicao")
	private Date dataExpedicao;
	
	/** Data de cola��o. */
	@Column(name = "data_colacao")
	private Date dataColacao;
	
	/** Registro de Diploma Coletivo, caso esse registro perten�a a um. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_diploma_coletivo", nullable=true, updatable=true)
	private RegistroDiplomaColetivo registroDiplomaColetivo;
	
	/** Respons�veis pela assinatura no diploma no momento do Registro. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_responsavel_assinatura")
	private ResponsavelAssinaturaDiplomas assinaturaDiploma;
	
	/** Registro de Entrada do usu�rio respons�vel pelo registro. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	/** Indica se o diploma foi impresso corretamente. */
	private boolean impresso = false;
	
	/** Indica que o registro est� livre para uso. */
	private boolean livre = true;
	
	/** Observa��es acerca do registro do diploma. */
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy="registroDiploma")
	@JoinColumn(name = "id_registro_diploma")
	@OrderBy("id")
	private Collection<ObservacaoRegistroDiploma> observacoes;
	
	/** Data/hora de cria��o do registro de diploma. */
	@CriadoEm
	@Column(name = "criado_em")
	private Date criadoEm;
	
	/** Coordenador do curso Lato Sensu. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_coordenacao_curso")
	private CoordenacaoCurso coordenadorCurso;
	
	/** Indica se o registro de diploma � ativo ou n�o. */
	@CampoAtivo
	private boolean ativo;
	
	/** Construtor padr�o. */
	public RegistroDiploma() {
		criadoEm = new Date();
		ativo = true;
	}
	
	/** Construtor parametrizado. */
	public RegistroDiploma(int id) {
		super();
		this.id = id;
	}

	/** Retorna o discente a quem ser� emitido o diploma. 
	 * @return
	 */
	public Discente getDiscente() {
		return discente;
	}

	/** Seta o discente a quem ser� emitido o diploma.
	 * @param discente
	 */
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/** Retorna o n�mero do protocolo do diploma. 
	 * @return
	 */
	public String getProcesso() {
		return processo;
	}

	/** Seta o n�mero do protocolo do diploma.
	 * @param processo
	 */
	public void setProcesso(String processo) {
		this.processo = processo;
	}

	/** Retorna o n�mero do registro do diploma. 
	 * @return
	 */
	public Integer getNumeroRegistro() {
		return numeroRegistro;
	}

	/** Seta o n�mero do registro do diploma.
	 * @param numeroRegistro
	 */
	public void setNumeroRegistro(Integer numeroRegistro) {
		this.numeroRegistro = numeroRegistro;
	}

	/** Retorna a data de registro do diploma. 
	 * @return
	 */
	public Date getDataRegistro() {
		return dataRegistro;
	}

	/** Seta a data de registro do diploma.
	 * @param dataRegistro
	 */
	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	/** Retorna a data de expedi��o do diploma. 
	 * @return
	 */
	public Date getDataExpedicao() {
		return dataExpedicao;
	}

	/** Seta a data de expedi��o do diploma.
	 * @param dataExpedicao
	 */
	public void setDataExpedicao(Date dataExpedicao) {
		this.dataExpedicao = dataExpedicao;
	}

	/** Retorna a data de cola��o. 
	 * @return
	 */
	public Date getDataColacao() {
		return dataColacao;
	}

	/** Seta a data de cola��o.
	 * @param dataColacao
	 */
	public void setDataColacao(Date dataColacao) {
		this.dataColacao = dataColacao;
	}

	/**
	 * Retorna a chave prim�ria de Registro Diploma
	 * 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * Seta a chave prim�ria de Registro Diploma
	 * 
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o Registro de Diploma Coletivo, caso esse registro perten�a a um. 
	 * @return
	 */
	public RegistroDiplomaColetivo getRegistroDiplomaColetivo() {
		return registroDiplomaColetivo;
	}

	/** Seta o Registro de Diploma Coletivo, caso esse registro perten�a a um.
	 * @param registroDiplomaColetivo
	 */
	public void setRegistroDiplomaColetivo(
			RegistroDiplomaColetivo registroDiplomaColetivo) {
		this.registroDiplomaColetivo = registroDiplomaColetivo;
	}

	/** Indica se o diploma foi impresso corretamente. 
	 * @return
	 */
	public boolean isImpresso() {
		return impresso;
	}

	/** Seta se o diploma foi impresso corretamente. 
	 * @param impresso
	 */
	public void setImpresso(boolean impresso) {
		this.impresso = impresso;
	}

	/** Indica que o registro est� livre para uso. 
	 * @return
	 */
	public boolean isLivre() {
		return livre;
	}

	/** Seta que o registro est� livre para uso.
	 * @param livre
	 */
	public void setLivre(boolean livre) {
		this.livre = livre;
	}

	/** Retorna a folha do registro do diploma. 
	 * @return
	 */
	public FolhaRegistroDiploma getFolha() {
		return folha;
	}

	/** Seta a folha do registro do diploma.
	 * @param folha
	 */
	public void setFolha(FolhaRegistroDiploma folha) {
		this.folha = folha;
	}
	
	/** Retorna as observa��es acerca do registro do diploma. 
	 * @return
	 */
	public Collection<ObservacaoRegistroDiploma> getObservacoes() {
		return observacoes;
	}

	/** Seta as observa��es acerca do registro do diploma.
	 * @param observacao
	 */
	public void setObservacoes(Collection<ObservacaoRegistroDiploma> observacoes) {
		this.observacoes = observacoes;
	}
	
	/** Retorna o livro em que este registro est�.
	 * @return
	 */
	public LivroRegistroDiploma getLivroRegistroDiploma(){
		if (this.folha == null)
			return null;
		else
			return this.getFolha().getLivro();
	}
	
	/** Retorna o Registro de Entrada do usu�rio respons�vel pelo registro. 
	 * @return
	 */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Seta o Registro de Entrada do usu�rio respons�vel pelo registro.
	 * @param registroEntrada
	 */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/** Limpa os dados do registro: data de cola��o, data de expedi��o, data de registro,
	 * discente e processo. Seta o registro como livre e n�o impresso.
	 * <b>OBS:</b> n�o muda o n�mero do registro. Esse n�mero � constante.
	 * 
	 */
	public void clean() {
		this.dataColacao = null;
		this.dataExpedicao = null;
		this.dataRegistro = null;
		this.discente = null;
		this.impresso = false;
		this.livre = true;
		this.processo = null;
	}

	/** Valida os dados: discente, data de cola��o, data de expedi��o,
	 * data de registro, n�mero do processo, folha do livro de registro, 
	 * n�mero de registro.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(discente, "Discente", lista);
		ValidatorUtil.validateRequired(dataColacao, "Data de Cola��o", lista);
		ValidatorUtil.validateRequired(dataExpedicao, "Data de Expedi��o", lista);
		ValidatorUtil.validateRequired(dataRegistro, "Data de Registro", lista);
		ValidatorUtil.validateRequired(processo, "N�mero do Processo", lista);
		ValidatorUtil.validateRequired(folha.getNumeroFolha(), "Folha", lista);
		return lista;
	}
	
	/**
	 * Retorna uma representa��o textual do registro do diploma no formato: "N�"
	 * seguido do n�mero do registro seguido de v�rgula, seguido de "livro",
	 * seguido do n�mero do livro, seguido de v�rgula, seguido de "folha"
	 * seguido do n�mero da folha.
	 */
	@Override
	public String toString() {
		return "N� " + numeroRegistro + ", livro " + folha.getLivro().getTitulo() +", folha " + folha.getNumeroFolha(); 
	}

	/** Adiciona uma observa��o ao Registro de Diploma.
	 * @param observacaoRegistroDiploma Observa��o a ser adicionada.
	 */
	public void addObservacao(ObservacaoRegistroDiploma observacaoRegistroDiploma) {
		if (getObservacoes() == null) {
			setObservacoes(new ArrayList<ObservacaoRegistroDiploma>());
		}
		observacaoRegistroDiploma.setRegistroDiploma(this);
		observacoes.add(observacaoRegistroDiploma);
	}
	
	/** Retorna as observa��es ativas (n�o exclu�das) acerca do registro do diploma. 
	 * @return
	 */
	public Collection<ObservacaoRegistroDiploma> getObservacoesAtivas() {
		Collection<ObservacaoRegistroDiploma> ativas = new ArrayList<ObservacaoRegistroDiploma>();
		for (ObservacaoRegistroDiploma obs : getObservacoes()) {
			if (obs.isAtivo())
				ativas.add(obs);
		}
		return ativas;
	}

	/** Retorna a data/hora de cria��o do registro de diploma.
	 * @return Data/hora de cria��o do registro de diploma.
	 */
	public Date getCriadoEm() {
		return criadoEm;
	}

	/** Seta a data/hora de cria��o do registro de diploma.
	 * @param criadoEm Data/hora de cria��o do registro de diploma.
	 */
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	/** Retorna o coordenador do curso Lato Sensu.
	 * @return
	 */
	public CoordenacaoCurso getCoordenadorCurso() {
		return coordenadorCurso;
	}

	/** Seta o coordenador do curso Lato Sensu.
	 * @param coordenadorCurso
	 */
	public void setCoordenadorCurso(CoordenacaoCurso coordenadorCurso) {
		this.coordenadorCurso = coordenadorCurso;
	}

	public ResponsavelAssinaturaDiplomas getAssinaturaDiploma() {
		return assinaturaDiploma;
	}

	public void setAssinaturaDiploma(ResponsavelAssinaturaDiplomas assinaturaDiploma) {
		this.assinaturaDiploma = assinaturaDiploma;
	}

	/** Indica se o registro de diploma � ativo ou n�o. 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se o registro de diploma � ativo ou n�o. 
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	/**
	 * Retorna o nome do diretor que deve aparecer na visualiza��o de um diploma.
	 * O retorno tem seu valor como a propriedade nomeDiretorGraduacao da classe {@link ResponsavelAssinaturaDiplomas}, caso o aluno seja da gradua��o
	 * ou a propriedade nomeDiretorPosGraduacao, da mesma classe, caso contr�rio.
	 * 
	 * @return
	 */
	public String getDiretorRespectivo() {
		if(assinaturaDiploma == null)
			return "";
		return discente.isGraduacao() ? assinaturaDiploma.getNomeDiretorGraduacao() : assinaturaDiploma.getNomeDiretorPosGraduacao();
	}
}
