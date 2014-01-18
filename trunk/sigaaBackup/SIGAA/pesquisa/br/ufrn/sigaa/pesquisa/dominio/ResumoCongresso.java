/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/06/2007
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.negocio.ResumoCongressoValidator;

/**
 * Entidade referente aos resumos do congresso de
 * iniciação científica (CIC)
 *
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "resumo_congresso", schema = "pesquisa", uniqueConstraints = {})
public class ResumoCongresso extends AbstractMovimento implements PersistDB, Validatable {

	private int id;

	/** Congresso de Iniciação Científica que o Resumo está vinculado */
	private CongressoIniciacaoCientifica congresso = new CongressoIniciacaoCientifica();
	/**  Código gerado para  resumo, único para cada congresso */
	private String codigo;
	/** Título do resumo */
	private String titulo;
	/** Corpo do resumo */
	private String resumo;
	/** Palavras-chave do resumo */
	private String palavrasChave;
	/** Área de conhecimento na qual o trabalho do resumo se enquadra */
	private AreaConhecimentoCnpq areaConhecimentoCnpq =  new AreaConhecimentoCnpq();

	private Collection<AutorResumoCongresso> autores;
	/** Status do resumo, definido atravÃ©s de constantes na aplicação (SUBMETIDO, APROVADO, etc.) */
	private int status;
	/** Referente aos status que o Resumo do Congresso pode apresentar */
	public static final int SUBMETIDO = 1;
	public static final int APROVADO = 2;
	public static final int NAO_APROVADO = 3;
	public static final int APRESENTADO = 4;
	public static final int NAO_APRESENTADO = 5;
	public static final int NECESSITA_CORRECOES = 6;
	public static final int CORRIGIDO = 7;
	public static final int AGUARDANDO_AUTORIZACAO = 8;
	public static final int NAO_AUTORIZADO = 9;
	public static final int RECUSADO_NECESSITA_CORRECOES = 10;
	public static final int CORRIGIDO_AGUARDANDO_APROVACAO = 11;

	/** Registro do envio */
	private RegistroEntrada registroEntrada;
	private Date dataEnvio;

	/** Registro da avaliação */
	private String parecer;
	private Date dataParecer;

	/** Coloca todos os tipos de status possíveis ao HashMap */
	private static HashMap<Integer, String> tiposStatus;
	static {
		tiposStatus = new HashMap<Integer, String>();
		tiposStatus.put(SUBMETIDO, "SUBMETIDO");
		tiposStatus.put(APROVADO, "APROVADO");
		tiposStatus.put(NAO_APROVADO, "NÃO APROVADO");
		tiposStatus.put(APRESENTADO, "APRESENTADO");
		tiposStatus.put(NAO_APRESENTADO, "NÃO APRESENTADO");
		tiposStatus.put(NECESSITA_CORRECOES, "NECESSITA CORREÇÕES");
		tiposStatus.put(CORRIGIDO, "CORRIGIDO");
		tiposStatus.put(AGUARDANDO_AUTORIZACAO, "AGUARDANDO AUTORIZAÇÃO");
		tiposStatus.put(NAO_AUTORIZADO, "NÃO AUTORIZADO");
		tiposStatus.put(RECUSADO_NECESSITA_CORRECOES, "RECUSADO NECESSITA CORREÇÕES");
		tiposStatus.put(CORRIGIDO_AGUARDANDO_APROVACAO, "CORRIGIDO AGUARDANDO APROVAÇÃO");
	}

	/** Indica o número do painel para organização no dia da apresentação */
	private Integer numeroPainel;
	/** Indica se o registro foi removido ou está ativo*/
	private boolean ativo = true;
	
	/** Verifica se o Resumo foi selecionado. */
	private boolean selecionado;
	
	/** Armazena as correções que devem ser realizadas no resumo do congresso. */
	private String correcao;
	
	/** Identificador da unidade agrupadora do resumo. Utilizado na distribuição de resumos para avaliação */
	private Integer idUnidadeAgrupadora;
	
	/** Identificador da avaliação do Resumo */
	private Integer idAvaliacao;
	
	@Transient
	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public ResumoCongresso() {

	}

	@Transient
	public String getStatusString() {
		return tiposStatus.get(this.status);
	}

	@Transient
	public HashMap<Integer, String> getTiposStatus(){
		return tiposStatus;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_conhecimento_cnpq", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getAreaConhecimentoCnpq() {
		return areaConhecimentoCnpq;
	}

	public void setAreaConhecimentoCnpq(AreaConhecimentoCnpq areaConhecimentoCnpq) {
		this.areaConhecimentoCnpq = areaConhecimentoCnpq;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_resumo_congresso", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "palavras_chave", unique = false, nullable = true, insertable = true, updatable = true)
	public String getPalavrasChave() {
		return palavrasChave;
	}

	public void setPalavrasChave(String palavrasChave) {
		this.palavrasChave = palavrasChave != null ? palavrasChave.trim() : palavrasChave;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo != null ? resumo.trim() : resumo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo != null ? titulo.trim() : titulo;
	}

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "resumo")
	public Collection<AutorResumoCongresso> getAutores() {
		return autores;
	}

	public void setAutores(Collection<AutorResumoCongresso> autores) {
		this.autores = autores;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_parecer", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataParecer() {
		return dataParecer;
	}

	public void setDataParecer(Date dataParecer) {
		this.dataParecer = dataParecer;
	}

	public String getParecer() {
		return parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer != null ? parecer.trim() : parecer;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_congresso")
	public CongressoIniciacaoCientifica getCongresso() {
		return congresso;
	}

	public void setCongresso(CongressoIniciacaoCientifica congresso) {
		this.congresso = congresso;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		// Validar autores
		ValidatorUtil.validateRequired(getAutor().getDiscente().getPessoa().getNome(), "Autor", erros);
		ValidatorUtil.validateRequired(getOrientador().getDocente().getPessoa().getNome(), "Orientador", erros);
		
		if ( getAutores() != null && getAutores().size() > ResumoCongressoValidator.LIMITE_AUTORES  ) {
			erros.addErro("Um trabalho pode ter, no máximo, " + ResumoCongressoValidator.LIMITE_AUTORES + " autores (entre autor, orientador e co-autores).");
		}

		// Validar área de conhecimento
		if (getAreaConhecimentoCnpq().getId() <= 0 ) {
			erros.addErro("Selecione uma das grandes áreas de conhecimento na qual seu trabalho está inserido.");
		}

		// Validar título
		if ( !StringUtils.notEmpty(getTitulo()) )
			ValidatorUtil.validateRequired(null, "Título", erros);
		else if(getTitulo().trim().replace("\r\n", "\n").length() > 200){
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Título", 200);
		}

		// Validar resumo
		if ( !StringUtils.notEmpty(getResumo()) )
			ValidatorUtil.validateRequired(null, "Resumo", erros);
		else if(getResumo().trim().replace("\r\n", "\n").length() > 1500){
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Resumo", 1500);
		}
		setResumo(getResumo().trim().replace("\r\n", "\n"));

		// Validar palavras-chave
		if ( !StringUtils.notEmpty(getPalavrasChave()) )
			ValidatorUtil.validateRequired(null, "Palavras-Chave", erros);
		else if(getPalavrasChave().trim().replace("\r\n", "\n").length() > 70)
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Palavras-Chave", 70);
		
		// Validar autores
		if ( getAutor() != null && getAutor().getCpf() ==  0 ) {
			erros.addErro("O autor " + getAutor().getNome() + " deve informar seu cpf para cadastrar o resumo.");
		}

		return erros;
	}

	/** Verifica se está no período de submissão de um Resumo Congresso */
	@Transient
	public static boolean isPeriodoSubmissao(CalendarioPesquisa calendario) {
		return CalendarUtils.isDentroPeriodo(calendario.getInicioResumoCIC(), calendario.getFimResumoCIC());
	}

	/** Retorna os autores desde que não seja nulo */
	@Transient
	public AutorResumoCongresso getAutor() {
		if ( autores != null && !autores.isEmpty() ) {
			for (AutorResumoCongresso autor : autores) {
				if ( autor.getTipoParticipacao() == AutorResumoCongresso.AUTOR ) {
					return autor;
				}
			}
		}
		return null;
	}

	public boolean hasAutor() {
		return getAutor() != null;
	}

	/** Retorna todos os orientadores desde que não seja nulo */
	@Transient
	public AutorResumoCongresso getOrientador() {
		if ( autores != null && !autores.isEmpty() ) {
			for (AutorResumoCongresso autor : autores) {
				if ( autor.getTipoParticipacao() == AutorResumoCongresso.ORIENTADOR ) {
					return autor;
				}
			}
		}
		return null;
	}

	public boolean hasOrientador() {
		return getOrientador() != null;
	}
	
	/** Retorna todos os co-orientadores desde que não seja nulo */
	@Transient
	public Collection<AutorResumoCongresso> getCoAutores() {
		Collection<AutorResumoCongresso> coAutores = new ArrayList<AutorResumoCongresso>();
		if ( autores != null && !autores.isEmpty() ) {
			for (AutorResumoCongresso autor : autores) {
				if ( autor.getTipoParticipacao() == AutorResumoCongresso.CO_AUTOR ) {
					coAutores.add(autor);
				}
			}
		}
		return coAutores;
	}
	/**
	 * verifica autor passado como parâmetro está contido na lista de autores. 
	 * @param autor
	 * @return
	 */
	public boolean contains(AutorResumoCongresso autor) {
		boolean achou = false;
		if ( autores != null && !autores.isEmpty() && autor != null ) {
			Iterator<AutorResumoCongresso> it = autores.iterator();
			while ( it.hasNext() && !achou ) {
				AutorResumoCongresso autorInserido = it.next();
				achou = autorInserido.equals(autor);
			}

		}
		return achou;
	}

	/**
	 * Retorna os co-orientadores e seus respectivos nomes 
	 * 
	 * @return
	 */
	@Transient
	public String getCoAutoresNomes() {
		StringBuffer coAutoresNomes = new StringBuffer();
		coAutoresNomes.append("");
		for(AutorResumoCongresso co: getCoAutores()){
			coAutoresNomes.append(", " + co.getNome());
		}
		return coAutoresNomes.toString().replaceFirst("[, ]", "");
	}

	@Transient
	public boolean isCertificadoDisponivel(){
		return (status == APRESENTADO && (new Date()).after( congresso.getFim() ));
	}

	@Transient
	public boolean isPassivelAvaliacao() {
		return status == SUBMETIDO || status == CORRIGIDO 
				|| status == AGUARDANDO_AUTORIZACAO || status == CORRIGIDO_AGUARDANDO_APROVACAO;
	}

	@Column(name = "numero_painel", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getNumeroPainel() {
		return numeroPainel;
	}

	public void setNumeroPainel(Integer numeroPainel) {
		this.numeroPainel = numeroPainel;
	}

	@Column(name = "ativo")
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getCorrecao() {
		return correcao;
	}

	public void setCorrecao(String correcao) {
		this.correcao = correcao;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "titulo", "resumo", "palavrasChave", "areaConhecimentoCnpq");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(titulo, resumo, palavrasChave, areaConhecimentoCnpq);
	}

	@Transient
	public Integer getIdUnidadeAgrupadora() {
		return idUnidadeAgrupadora;
	}

	public void setIdUnidadeAgrupadora(Integer idUnidadeAgrupadora) {
		this.idUnidadeAgrupadora = idUnidadeAgrupadora;
	}

	@Transient
	public Integer getIdAvaliacao() {
		return idAvaliacao;
	}

	public void setIdAvaliacao(Integer idAvaliacao) {
		this.idAvaliacao = idAvaliacao;
	}

	
}