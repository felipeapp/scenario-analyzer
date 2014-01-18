/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '26/01/2007'
 *
 */
package br.ufrn.sigaa.diploma.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Curso;

/**
 * Os diplomas dos discentes s�o registrados em livros. Esta classe � que modela o
 * conceito de Livro de Registro de Diploma.<br>
 * Um livro de registro de diplomas � composto de {@link FolhaRegistroDiploma
 * folhas}, as quais s�o anotados os dados do {@link RegistroDiploma Registro de
 * Diploma}.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity
@Table(schema = "diploma", name = "livro_registro_diploma")
public class LivroRegistroDiploma implements Validatable {
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_livro_registro_diploma")
	private int id;
	
	/** T�tulo do livro. */
	private String titulo;
	
	/**
	 * N�mero sugerido de folhas. Este n�mero n�o � r�gido para os novos livros
	 * cadastrados no SIGAA, dando a possibilidade para o usu�rio fechar o livro
	 * antes ou depois do n�mero especificado. Para os livros que ser�o
	 * digitados, este n�mero � fixo.
	 */
	@Column(name = "numero_sugerido_folhas")
	private int numeroSugeridoFolhas;
	
	/** Cole��o de folhas de registro do livro. */
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy="livro")
	@JoinColumn(name = "id_livro_registro_diploma", nullable = false)
	@OrderBy("numeroFolha")
	private Collection<FolhaRegistroDiploma> folhas;

	/** Cole��o de cursos registrados neste livro. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="cursos_livro_registro_diploma", schema="diploma",
			joinColumns=@JoinColumn(name="id_livro_registro_diploma"),  
			inverseJoinColumns=@JoinColumn(name="id_curso"))
	private Collection<Curso> cursosRegistrados;
	
	/** Indica se o livro est� aberto para uso. */
	private boolean ativo = true;
	
	/** Indica se o livro ser� usado para registro de diplomas de institui��es externas. */
	@Column(name = "registro_externo")
	private boolean registroExterno;
	
	/** Nome da Institui��o Externa que ter� os diplomas registrados no livro. */
	@Column(name = "instituicao")
	private String instituicao;
	
	/** Informa o n�mero de registros por folha. Atualmente, s�o 4 registros por folha. */
	@Column(name = "numero_registro_por_folha")
	private int numeroRegistroPorFolha;
	
	/** Indica se o livro � antigo, ou seja, anterior ao Registro Autom�tico de Diplomas no SIGAA. */
	@Column(name="livro_antigo")
	private boolean livroAntigo;
	
	/** N�vel de Ensino dos discentes registrados neste livro. */
	@Column(name = "nivel", nullable = false)
	private char nivel;
	
	/** Indica que este livro ser� utilizado para registrar certificados, e n�o diplomas. */
	@Column(name = "registro_certificado", nullable = false)
	private boolean registroCertificado = false;
	
	/** N�mero atual de folhas do livro (calculado em consultas). */
	@Transient
	private int numeroFolhas;

	/** Construtor padr�o. */
	public LivroRegistroDiploma() {
	}
	
	/** Construtor parametrizado. 
	 * @param id
	 */
	public LivroRegistroDiploma(int id) {
		this();
		this.id = id;
	}
	
	/** Retorna a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}


	/** Seta a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}


	/** Retorna o t�tulo do livro. 
	 * @return
	 */
	public String getTitulo() {
		return titulo;
	}


	/** Seta o t�tulo do livro.  
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}


	/**
	 * Retorna o n�mero sugerido de folhas. Este n�mero n�o � r�gido para os
	 * novos livros cadastrados no SIGAA, dando a possibilidade para o usu�rio
	 * fechar o livro antes ou depois do n�mero especificado. Para os livros que
	 * ser�o digitados, este n�mero � fixo.
	 * 
	 * @return
	 */
	public int getNumeroSugeridoFolhas() {
		return numeroSugeridoFolhas;
	}


	/**
	 * Seta o n�mero sugerido de folhas. Este n�mero n�o � r�gido para os
	 * novos livros cadastrados no SIGAA, dando a possibilidade para o usu�rio
	 * fechar o livro antes ou depois do n�mero especificado. Para os livros que
	 * ser�o digitados, este n�mero � fixo.
	 * @param numeroSugeridoFolhas
	 */
	public void setNumeroSugeridoFolhas(int numeroSugeridoFolhas) {
		this.numeroSugeridoFolhas = numeroSugeridoFolhas;
	}


	/** Retorna a cole��o de folhas de registro do livro. 
	 * @return
	 */
	public Collection<FolhaRegistroDiploma> getFolhas() {
		return folhas;
	}


	/** Seta a cole��o de folhas de registro do livro.
	 * @param folhas
	 */
	public void setFolhas(Collection<FolhaRegistroDiploma> folhas) {
		this.folhas = folhas;
	}


	/** Retorna uma cole��o de cursos registrados neste livro. 
	 * @return
	 */
	public Collection<Curso> getCursosRegistrados() {
		return cursosRegistrados;
	}


	/** Seta uma cole��o de cursos registrados neste livro.
	 * @param cursosRegistrados
	 */
	public void setCursosRegistrados(Collection<Curso> cursosRegistrados) {
		this.cursosRegistrados = cursosRegistrados;
	}


	/** Indica se o livro est� aberto para uso. 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}


	/** Seta se o livro est� aberto para uso. 
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	/** Retorna o n�mero de registros por folha. Atualmente, s�o 4 registros por folha. 
	 * @return
	 */
	public int getNumeroRegistroPorFolha() {
		return numeroRegistroPorFolha;
	}


	/** Seta o n�mero de registros por folha. Atualmente, s�o 4 registros por folha.
	 * @param numeroRegistroPorFolha
	 */
	public void setNumeroRegistroPorFolha(int numeroRegistroPorFolha) {
		this.numeroRegistroPorFolha = numeroRegistroPorFolha;
	}


	/** Indica se o livro ser� usado para registro de diplomas de institui��es externas. 
	 * @return
	 */
	public boolean isRegistroExterno() {
		return registroExterno;
	}


	/** Seta se o livro ser� usado para registro de diplomas de institui��es externas. 
	 * @param registroExterno
	 */
	public void setRegistroExterno(boolean registroExterno) {
		this.registroExterno = registroExterno;
	}


	/** Retorna o nome da Institui��o Externa que ter� os diplomas registrados no livro. 
	 * @return
	 */
	public String getInstituicao() {
		return instituicao;
	}


	/** Seta o nome da Institui��o Externa que ter� os diplomas registrados no livro.
	 * @param instituicao
	 */
	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}


	/** Indica se o livro � antigo, ou seja, anterior ao Registro Autom�tico de Diplomas no SIGAA. 
	 * @return
	 */
	public boolean isLivroAntigo() {
		return livroAntigo;
	}


	/** Seta se o livro � antigo, ou seja, anterior ao Registro Autom�tico de Diplomas no SIGAA. 
	 * @param livroAntigo
	 */
	public void setLivroAntigo(boolean livroAntigo) {
		this.livroAntigo = livroAntigo;
	}	
	
	/**
	 * Retorna uma representa��o textual do livro no formato: t�tulo, seguido de
	 * par�ntese, seguido do n�mero de p�ginas, seguido de v�rgula, seguido de
	 * "ABERTO"/"FECHADO", seguido de par�ntese.
	 */
	@Override
	public String toString(){
		if (folhas != null)
			return titulo + " ("+folhas.size()+" p�ginas, "+(ativo?"ABERTO":"FECHADO")+")";
		else 
			return titulo + " (0 p�ginas, "+(ativo?"ABERTO":"FECHADO")+")";
	}

	/** Valida os atributos: t�tulo do livro; n�mero de p�ginas; pelo menos um curso.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (!NivelEnsino.isValido(nivel)) {
			validateRequired(null, "N�vel de Ensino", lista);
		} else {
			validateRequired(titulo, "T�tulo", lista);
			validateRequired(numeroSugeridoFolhas, "N�mero de P�ginas", lista);
			if (isGraduacao() && !registroExterno)
				validateRequired(cursosRegistrados, "Lista de Cursos", lista);
			if (registroExterno)
				validateRequired(instituicao, "Institui��o", lista);
		}
		validateMinValue(numeroRegistroPorFolha, 1, "N�mero de Registros por Folha", lista);
		return lista;
	}
	
	/** Retorna uma String com a lista de cursos registrados no livro, separados por v�rgula.
	 * @return
	 */
	public String getDescricaoCursos() {
		StringBuilder aux = new StringBuilder(); 
		if (cursosRegistrados == null || cursosRegistrados.size() == 0) {
			return "N�o h� cursos especificados para registro neste livro";
		}
		else {
			int k = 0;
			for (Curso curso : cursosRegistrados) {
				aux.append(curso.getDescricao() + (cursosRegistrados.size() - k > 0?", ":""));
			}
		}
		return aux.toString();
	}

	/** Retorna o primeiro registro livre do livro. 
	 * @return
	 */
	public synchronized RegistroDiploma getRegistroLivre() {
		// Caso o livro seja novo, adiciona uma folha:
		if (folhas == null) {
			folhas = new ArrayList<FolhaRegistroDiploma>();
			numeroFolhas = 0;
		}
		if (folhas.size() == 0) {
			folhas.add(novaFolha());
			numeroFolhas = 1;
		}
		// percorre as folhas em busca de um registro livre
		for (FolhaRegistroDiploma folha : folhas ){
			if (folha.hasRegistroLivre())
				return folha.getPrimeiroRegistroLivre();
		}
		// n�o achou folha com registro livre? Adicione outra folha:
		FolhaRegistroDiploma novaFolha = novaFolha();
		folhas.add(novaFolha);
		numeroFolhas++;
		return novaFolha.getPrimeiroRegistroLivre();
	}
	
	/** Retorna uma nova folha de registro
	 * @return
	 */
	public FolhaRegistroDiploma novaFolha() {
		FolhaRegistroDiploma folha = new FolhaRegistroDiploma(folhas.size() + 1, numeroRegistroPorFolha);
		folha.setLivro(this);
		return folha;
	}

	/** Retorna o N�vel de Ensino dos discentes registrados neste livro.
	 * @return
	 */
	public char getNivel() {
		return nivel;
	}
	
	public String getNivelDesc() {
		return NivelEnsino.getDescricao(nivel);
	}

	/** Seta o N�vel de Ensino dos discentes registrados neste livro.
	 * @param nivel
	 */
	public void setNivel(char nivel) {
		if (nivel == NivelEnsino.LATO)
			this.registroCertificado = true;
		else
			this.registroCertificado = false;
		this.nivel = nivel;
	}

	/** Indica se o livro � para o registro de diplomas de stricto sensu.
	 * @return
	 */
	public boolean isStrictoSensu() {
		return NivelEnsino.STRICTO == nivel;
	}
	
	/** Indica se o livro � para o registro de diplomas de lato sensu.
	 * @return
	 */
	public boolean isLatoSensu() {
		return NivelEnsino.LATO == nivel;
	}
	
	/** Indica se o livro � para o registro de diplomas de gradua��o.
	 * @return
	 */
	public boolean isGraduacao() {
		return NivelEnsino.GRADUACAO == nivel;
	}

	/** Indica que este livro ser� utilizado para registrar certificados, e n�o diplomas. 
	 * @return
	 */
	public boolean isRegistroCertificado() {
		return registroCertificado;
	}

	/** Seta que este livro ser� utilizado para registrar certificados, e n�o diplomas. 
	 * @param registroCertificado
	 */
	public void setRegistroCertificado(boolean registroCertificado) {
		this.registroCertificado = registroCertificado;
	}
	
	/** Retorna uma descri��o textual do tipo de registro efetuado neste livro (Certificado ou Diploma).
	 * @return
	 */
	public String getTipoRegistroDescricao() {
		if (registroCertificado) 
			return "Certificado";
		else 
			return "Diploma";
	}

	/** Indica que o livro n�o possui registros de diplomas.
	 * @return
	 */
	public boolean isVazio(){
		if (folhas != null) {
			for (FolhaRegistroDiploma folha : folhas) {
				if (folha.getRegistros() != null) return false;
			}
			return true;
		} else {
			return true;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, titulo);
	}

	public int getNumeroFolhas() {
		return numeroFolhas;
	}

	public void setNumeroFolhas(int numeroFolhas) {
		this.numeroFolhas = numeroFolhas;
	}

	/** Adiciona um curso � lista de cursos registrados neste livro
	 * @param curso
	 */
	public void addCurso(Curso curso) {
		if (cursosRegistrados == null)
			cursosRegistrados = new ArrayList<Curso>();
		cursosRegistrados.add(curso);
	}
}
