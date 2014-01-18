/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Nov 14, 2007
 *
 */
package br.ufrn.sigaa.questionario.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Esta entidade representa um questionário que pode ser de qualquer tipo no sistema
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "questionario", schema = "questionario")
public class Questionario implements Validatable {

	/** Chave primária do Questionário */
	@Id
	@Column(name = "id_questionario", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;

	/** título do questionário */
	private String titulo;

	/** Contem o tipo do questionário, exemplo questionário da turma virtual, avaliação institucional, etc */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_questionario", unique = false, nullable = true, insertable = true, updatable = true)
	private TipoQuestionario tipo;

	/** Unidade do questionário */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade", unique = false, nullable = true, insertable = true, updatable = true)
	private Unidade unidade;
	
	/** conjunto perguntas deste questionário */
	@OrderBy("ordem")
	@OneToMany(mappedBy = "questionario", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private List<PerguntaQuestionario> perguntas;

	/** diz se o questionário está ativo ou não */
	private boolean ativo;

	/** a data de inicio do período que o questionário deve estar disponível */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio", unique = false, nullable = true, insertable = true, updatable = true)
	private Date inicio;

	/** a data de fim do período que o questionário deve estar disponível */
	@Temporal(TemporalType.DATE)
	@Column(name = "fim", unique = false, nullable = true, insertable = true, updatable = true)
	private Date fim;

	/** registro entrada do usuário que cadastrou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** data que foi cadastrado */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoEm
	private Date dataCadastro;

	/** data que este questionário foi desativado */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoEm
	private Date dataAtualizacao;

	/** registro entrada do usuário que desativou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_Atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Informação se o gabarito possue Gabarito */
	@Column( name = "possui_definicao_gabarito" )
	private boolean possuiDefinicaoGabarito;
	
	/**
	 * atualiza o atributo ordem para de acordo com a posição na lista que a pergunta está!
	 */
	public void atualizarOrdemPerguntas(){
		if( !isEmpty(perguntas) ){
			for( int i = 0; i < perguntas.size(); i++ ){
				perguntas.get(i).setOrdem(i+1);
			}
		}
	}

	/**
	 * além de atualizar o atributo ordem das perguntas atualiza o campo ordem das alternativas
	 */
	public void atualizarOrdemPerguntasEAlternativas(){
		atualizarOrdemPerguntas();
		for( PerguntaQuestionario p : perguntas ){
			p.atualizarOrdemAlternativas();
		}
	}

	public Questionario(){
		
	}
	
	public Questionario(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public TipoQuestionario getTipo() {
		return tipo;
	}

	public void setTipo(TipoQuestionario tipo) {
		this.tipo = tipo;
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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/** Verifica se o questionário contém uma pergunta específica */
	public boolean containsPergunta(PerguntaQuestionario pergunta) {
		if(perguntas == null)
			return false;
		else{
			return perguntas.contains(pergunta);
		}
	}
	
	/** Adiciona um pergunta ao questionário */
	public void adicionaPergunta(PerguntaQuestionario pergunta) {
		if(perguntas == null)
			perguntas = new ArrayList<PerguntaQuestionario>();
		
		pergunta.setQuestionario(this);
		perguntas.add(pergunta);
	}
	
	
	/** Retorna a lista de perguntas do Questionário */
	public List<PerguntaQuestionario> getPerguntas() {
		
		if ( perguntas != null ) {
			List<PerguntaQuestionario> perguntasAtivas = new ArrayList<PerguntaQuestionario>(0);
			for (PerguntaQuestionario p : perguntas)
				if( p != null && p.isAtivo() )
					perguntasAtivas.add(p);
			perguntas = perguntasAtivas;
		}
		return perguntas;
		
	}
	
	public void setPerguntas(List<PerguntaQuestionario> perguntas) {
		this.perguntas = perguntas;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public boolean isPossuiDefinicaoGabarito() {
		return possuiDefinicaoGabarito;
	}

	public void setPossuiDefinicaoGabarito(boolean possuiDefinicaoGabarito) {
		this.possuiDefinicaoGabarito = possuiDefinicaoGabarito;
	}
	
	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		validateRequired(tipo, "Tipo do Questionário", lista);
		validateRequired(titulo, "Título", lista);
		if (isNecessarioPeriodoPublicacao()) {
			validateRequired(inicio, "Data Inicial", lista);
			validateRequired(fim, "Data Final", lista);
			ValidatorUtil.validaInicioFim(inicio, fim, "Data Inicial é maior que a Data Final", lista);
		}
		return lista;
	}

	public boolean isProcessoSeletivo() {
		return tipo != null && tipo.isProcessoSeletivo();
	}
	
	public boolean isQuestionarioInscricaoAtividade() {
		return tipo != null && tipo.isQuestionarioInscricaoAtividade();
	}
	
	public boolean isRelatorioEstagio(){
		return tipo != null && tipo.getId() > 0 && tipo.isQuestionarioRelatorioEstagio();
	}

	public boolean isAcaoExtensao(){
		return tipo != null && tipo.getId() > 0 && tipo.isAcaoExtensao();
	}
	
	/**
	 * Indica se o questionário tem período de publicação. Alguns tipos de
	 * questionários como, por exemplo, Processo Seletivo e Auto Avaliação do
	 * Stricto Sensu não tem este período. O período de aplicação do
	 * questionário, nestes casos, é controlado pelo próprio caso de uso.
	 * 
	 * @return
	 */
	public boolean isNecessarioPeriodoPublicacao() {
		return tipo != null && tipo.getId() > 0 
				&& !isProcessoSeletivo()
				&& !isRelatorioEstagio() 
				&& !isQuestionarioInscricaoAtividade()
				&& !tipo.isAutoAvaliacaoStrictoSensu()
				&& !tipo.isAutoAvaliacaoLatoSensu();
	}

	public boolean isRespostasObrigatorias() {
		return tipo.isRespostasObrigatorias();
	}
	
}