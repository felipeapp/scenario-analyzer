/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/07/2007
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que representa os autores, co-autores e orientadores 
 * de resumos para o Congresso de Iniciação Científica
 * 
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name = "autor_resumo_congresso", schema = "pesquisa", uniqueConstraints = {})
public class AutorResumoCongresso extends AbstractMovimento implements PersistDB, Validatable, Comparable<AutorResumoCongresso> {

	private int id;

	/** Resumo do congresso do autor */
	private ResumoCongresso resumo;

	/** Autores que são alunos */
	private Discente discente;

	/** Autores que são Docentes */
	private Servidor docente;

	/** Nome do Autor */
	private String nome;

	/** Email do Autor */
	private String email;

	/** Cpf do Autor */
	private long cpf;

	/** Instituição de ensino autor */
	private InstituicoesEnsino instituicaoEnsino;

	/** Tipo de autoria */
	
	private int tipoParticipacao;
	
	/** Tipos de participação na autoria */
	public static final int AUTOR = 1;
	public static final int ORIENTADOR = 2;
	public static final int CO_AUTOR = 3;

	/** Categoria do autor (alunos, docente, externo); */
	private int categoria;
	
	/** Categorias de autores */
	public static final int DISCENTE = 1;
	public static final int DOCENTE = 2;
	public static final int EXTERNO = 3;

	/** Categoria que se enquadra o autor do resumo. Ex.: Discente, Docente, Membro Externo */
	private static HashMap<Integer, String> categorias;
	/** Tipo de participação que o autor do Resumo irá desempenhar. Ex.: Autor, Orientador, Co-autor */
	private static HashMap<Integer, String> tiposParticipacao;
	static {
		categorias = new HashMap<Integer, String>();
		categorias.put(DISCENTE, "DISCENTE");
		categorias.put(DOCENTE, "DOCENTE");
		categorias.put(EXTERNO, "EXTERNO");

		tiposParticipacao = new HashMap<Integer, String>();
		tiposParticipacao.put(AUTOR, "AUTOR");
		tiposParticipacao.put(ORIENTADOR, "ORIENTADOR");
		tiposParticipacao.put(CO_AUTOR, "CO_AUTOR");
	}

	/** Construtor padrão */
	public AutorResumoCongresso() {
		discente = new Discente();
		docente = new Servidor();
	}

	/** Verifica de o autor do Resumo é do tipo Co-autor. */
	@Transient
	public boolean isCoAutor() {
		return tipoParticipacao == CO_AUTOR;
	}

	/** 
	 * Retorna a categoria do autor do Resumo, caso seja de uma instituição externa 
	 * e adiciona o nome da instituição.
	 */
	@Transient
	public String getCategoriaString() {
		String c = categorias.get(categoria);
		if (categoria == EXTERNO) {
			return c + " (" + instituicaoEnsino.getSigla() + ")";
		}
		return c;
	}

	/** Retorna o tipo de participação que o autor do Resumo está inserido */
	@Transient
	public String getTipoParticipacaoString() {
		return tiposParticipacao.get(tipoParticipacao);
	}

	/** Validação realizada na hora de efetuar um cadastro ou alteração de um autor de Resumo */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		switch (categoria) {
		case DISCENTE:
			// Validar discente
			if ( discente == null || discente.getId() <= 0 ) {
				erros.addErro("O discente informado não é valido");
			}
			break;
		case DOCENTE:
			// Validar docente
			if ( docente == null || docente.getId() <= 0 ) {
				erros.addErro("O docente informado não é valido");
			}
			break;
		case EXTERNO:
			// Validar dados do autor externo
			if ( StringUtils.isEmpty(nome) ) {
				ValidatorUtil.validateRequired(null, "Nome", erros);
			} else if ( nome.trim().length() > 200 ) {
				erros.addErro("O nome deve ter, no máximo, 200 caracteres. Por favor, realize as abreviações necessárias.");
			}

			if ( StringUtils.isEmpty(email) ) {
				ValidatorUtil.validateRequired(null, "Email", erros);
			} else if ( email.trim().length() > 200 ) {
				erros.addErro("O email deve ter, no máximo, 200 caracteres.");
			}

			ValidatorUtil.validateCPF_CNPJ(this.cpf, "CPF" , erros);

			break;
		default:
			erros.addErro("É necessário definir a categoria do autor");
			break;
		}
		return erros;
	}
	
	/** Mapeamento do atributo discente */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente", unique = false, nullable = true, insertable = true, updatable = true)
	public Discente getDiscente() {
		return discente;
	}

	/** Seta o discente */
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/** Definição do discente nome, cpf e email do mesmo */
	public void defineDiscente(Discente discente) {
		this.discente = discente;
		this.docente = null;
		if (discente != null) {
			this.categoria = DISCENTE;

			this.setNome( discente.getNome() );
			this.cpf = discente.getPessoa().getCpf_cnpj() != null ? discente.getPessoa().getCpf_cnpj() : 0;
			this.setEmail( discente.getPessoa().getEmail() );
		}
	}

	/** Retorna o email do autor do Resumo*/
	public String getEmail() {
		return email;
	}

	/** Seta o email do autor do Resumo*/
	public void setEmail(String email) {
		this.email = ( email != null ? email.toLowerCase().trim() : email );
	}

	/** Responsável pela geração da chave primária e retorno da mesma */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_autor_resumo_congresso", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	/** Seta a chave primária do autor de resumo */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a instituição de ensino */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_instituicao_ensino", unique = false, nullable = true, insertable = true, updatable = true)
	public InstituicoesEnsino getInstituicaoEnsino() {
		return instituicaoEnsino;
	}

	/** Seta a instituição de ensino */
	public void setInstituicaoEnsino(InstituicoesEnsino instituicaoEnsino) {
		this.instituicaoEnsino = instituicaoEnsino;
	}

	/** Retorna o nome */
	public String getNome() {
		return nome;
	}

	/** Seta o nome */
	public void setNome(String nome) {
		this.nome = ( nome != null ? nome.toUpperCase().trim() : nome );
	}

	/** Retorna o docente */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getDocente() {
		return docente;
	}

	/** Seta o docente */
	public void setDocente(Servidor docente) {
		this.docente = docente;
	}
	
	/** Definição do docente nome, cpf e email do mesmo */
	public void defineDocente(Servidor docente) {
		this.docente = docente;
		this.discente = null;
		if (docente != null) {
			this.categoria = DOCENTE;

			this.setNome( docente.getPessoa().getNome() );
			this.cpf = docente.getPessoa().getCpf_cnpj();
			this.setEmail( docente.getPessoa().getEmail() );
		}
	}

	/** Retorna o tipo de participação */
	@Column(name="tipo_participacao")
	public int getTipoParticipacao() {
		return tipoParticipacao;
	}

	/** Seta o tipo de participação */
	public void setTipoParticipacao(int tipo) {
		this.tipoParticipacao = tipo;
	}

	/** Retorna o cpf */
	public long getCpf() {
		return cpf;
	}

	/** Seta o cpf */
	public void setCpf(long cpf) {
		this.cpf = cpf;
	}

	/** Retorna o resumo */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_resumo_congresso", unique = false, nullable = true, insertable = true, updatable = true)
	public ResumoCongresso getResumo() {
		return resumo;
	}

	/** Seta o resumo */
	public void setResumo(ResumoCongresso resumo) {
		this.resumo = resumo;
	}

	/** Retorna a categoria, por exemplo.: aluno, docente, externo */
	public int getCategoria() {
		return categoria;
	}

	/** Seta a categoria do autor do resumo */
	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}

	public int compareTo(AutorResumoCongresso o) {
		return 0;
	}
	
  /**
   * Serve para comparar se o AutorResumo está instanciado ou não.
   * Caso esteja o método realiza algumas verificações!! 
   *  
   * @param outro
   * @return
   */
	public boolean equals(AutorResumoCongresso outro) {
		if (outro == null) {
			return false;
		}
		if (outro.getCategoria() != this.getCategoria()) {
			return false;
		} else {
			switch (categoria) {
				case DISCENTE:
					if (this.discente != null && outro.getDiscente() != null)
						return this.discente.getId() == outro.getDiscente().getId();
					break;
				case DOCENTE:
					if (this.docente != null && outro.getDocente() != null)
						return this.docente.getId() == outro.getDocente().getId();
					break;
				case EXTERNO:
					if (this.cpf != 0 && outro.getCpf() != 0)
						return this.cpf == outro.getCpf();
					break;
				default:
					return false;
			}
		}
		return false;
	}

	/**
	 * Indica se este status é igual ao status passado como parâmetro.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "cpf");
	}

	/**
	 * Retorna o código hash deste status.
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, cpf, nome);
	}

}
