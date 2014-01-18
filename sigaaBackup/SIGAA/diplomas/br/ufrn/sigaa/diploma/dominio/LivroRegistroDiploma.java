/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
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
 * Os diplomas dos discentes são registrados em livros. Esta classe é que modela o
 * conceito de Livro de Registro de Diploma.<br>
 * Um livro de registro de diplomas é composto de {@link FolhaRegistroDiploma
 * folhas}, as quais são anotados os dados do {@link RegistroDiploma Registro de
 * Diploma}.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Entity
@Table(schema = "diploma", name = "livro_registro_diploma")
public class LivroRegistroDiploma implements Validatable {
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_livro_registro_diploma")
	private int id;
	
	/** Título do livro. */
	private String titulo;
	
	/**
	 * Número sugerido de folhas. Este número não é rígido para os novos livros
	 * cadastrados no SIGAA, dando a possibilidade para o usuário fechar o livro
	 * antes ou depois do número especificado. Para os livros que serão
	 * digitados, este número é fixo.
	 */
	@Column(name = "numero_sugerido_folhas")
	private int numeroSugeridoFolhas;
	
	/** Coleção de folhas de registro do livro. */
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy="livro")
	@JoinColumn(name = "id_livro_registro_diploma", nullable = false)
	@OrderBy("numeroFolha")
	private Collection<FolhaRegistroDiploma> folhas;

	/** Coleção de cursos registrados neste livro. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="cursos_livro_registro_diploma", schema="diploma",
			joinColumns=@JoinColumn(name="id_livro_registro_diploma"),  
			inverseJoinColumns=@JoinColumn(name="id_curso"))
	private Collection<Curso> cursosRegistrados;
	
	/** Indica se o livro está aberto para uso. */
	private boolean ativo = true;
	
	/** Indica se o livro será usado para registro de diplomas de instituições externas. */
	@Column(name = "registro_externo")
	private boolean registroExterno;
	
	/** Nome da Instituição Externa que terá os diplomas registrados no livro. */
	@Column(name = "instituicao")
	private String instituicao;
	
	/** Informa o número de registros por folha. Atualmente, são 4 registros por folha. */
	@Column(name = "numero_registro_por_folha")
	private int numeroRegistroPorFolha;
	
	/** Indica se o livro é antigo, ou seja, anterior ao Registro Automático de Diplomas no SIGAA. */
	@Column(name="livro_antigo")
	private boolean livroAntigo;
	
	/** Nível de Ensino dos discentes registrados neste livro. */
	@Column(name = "nivel", nullable = false)
	private char nivel;
	
	/** Indica que este livro será utilizado para registrar certificados, e não diplomas. */
	@Column(name = "registro_certificado", nullable = false)
	private boolean registroCertificado = false;
	
	/** Número atual de folhas do livro (calculado em consultas). */
	@Transient
	private int numeroFolhas;

	/** Construtor padrão. */
	public LivroRegistroDiploma() {
	}
	
	/** Construtor parametrizado. 
	 * @param id
	 */
	public LivroRegistroDiploma(int id) {
		this();
		this.id = id;
	}
	
	/** Retorna a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}


	/** Seta a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}


	/** Retorna o título do livro. 
	 * @return
	 */
	public String getTitulo() {
		return titulo;
	}


	/** Seta o título do livro.  
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}


	/**
	 * Retorna o número sugerido de folhas. Este número não é rígido para os
	 * novos livros cadastrados no SIGAA, dando a possibilidade para o usuário
	 * fechar o livro antes ou depois do número especificado. Para os livros que
	 * serão digitados, este número é fixo.
	 * 
	 * @return
	 */
	public int getNumeroSugeridoFolhas() {
		return numeroSugeridoFolhas;
	}


	/**
	 * Seta o número sugerido de folhas. Este número não é rígido para os
	 * novos livros cadastrados no SIGAA, dando a possibilidade para o usuário
	 * fechar o livro antes ou depois do número especificado. Para os livros que
	 * serão digitados, este número é fixo.
	 * @param numeroSugeridoFolhas
	 */
	public void setNumeroSugeridoFolhas(int numeroSugeridoFolhas) {
		this.numeroSugeridoFolhas = numeroSugeridoFolhas;
	}


	/** Retorna a coleção de folhas de registro do livro. 
	 * @return
	 */
	public Collection<FolhaRegistroDiploma> getFolhas() {
		return folhas;
	}


	/** Seta a coleção de folhas de registro do livro.
	 * @param folhas
	 */
	public void setFolhas(Collection<FolhaRegistroDiploma> folhas) {
		this.folhas = folhas;
	}


	/** Retorna uma coleção de cursos registrados neste livro. 
	 * @return
	 */
	public Collection<Curso> getCursosRegistrados() {
		return cursosRegistrados;
	}


	/** Seta uma coleção de cursos registrados neste livro.
	 * @param cursosRegistrados
	 */
	public void setCursosRegistrados(Collection<Curso> cursosRegistrados) {
		this.cursosRegistrados = cursosRegistrados;
	}


	/** Indica se o livro está aberto para uso. 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}


	/** Seta se o livro está aberto para uso. 
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	/** Retorna o número de registros por folha. Atualmente, são 4 registros por folha. 
	 * @return
	 */
	public int getNumeroRegistroPorFolha() {
		return numeroRegistroPorFolha;
	}


	/** Seta o número de registros por folha. Atualmente, são 4 registros por folha.
	 * @param numeroRegistroPorFolha
	 */
	public void setNumeroRegistroPorFolha(int numeroRegistroPorFolha) {
		this.numeroRegistroPorFolha = numeroRegistroPorFolha;
	}


	/** Indica se o livro será usado para registro de diplomas de instituições externas. 
	 * @return
	 */
	public boolean isRegistroExterno() {
		return registroExterno;
	}


	/** Seta se o livro será usado para registro de diplomas de instituições externas. 
	 * @param registroExterno
	 */
	public void setRegistroExterno(boolean registroExterno) {
		this.registroExterno = registroExterno;
	}


	/** Retorna o nome da Instituição Externa que terá os diplomas registrados no livro. 
	 * @return
	 */
	public String getInstituicao() {
		return instituicao;
	}


	/** Seta o nome da Instituição Externa que terá os diplomas registrados no livro.
	 * @param instituicao
	 */
	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}


	/** Indica se o livro é antigo, ou seja, anterior ao Registro Automático de Diplomas no SIGAA. 
	 * @return
	 */
	public boolean isLivroAntigo() {
		return livroAntigo;
	}


	/** Seta se o livro é antigo, ou seja, anterior ao Registro Automático de Diplomas no SIGAA. 
	 * @param livroAntigo
	 */
	public void setLivroAntigo(boolean livroAntigo) {
		this.livroAntigo = livroAntigo;
	}	
	
	/**
	 * Retorna uma representação textual do livro no formato: título, seguido de
	 * parêntese, seguido do número de páginas, seguido de vírgula, seguido de
	 * "ABERTO"/"FECHADO", seguido de parêntese.
	 */
	@Override
	public String toString(){
		if (folhas != null)
			return titulo + " ("+folhas.size()+" páginas, "+(ativo?"ABERTO":"FECHADO")+")";
		else 
			return titulo + " (0 páginas, "+(ativo?"ABERTO":"FECHADO")+")";
	}

	/** Valida os atributos: título do livro; número de páginas; pelo menos um curso.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (!NivelEnsino.isValido(nivel)) {
			validateRequired(null, "Nível de Ensino", lista);
		} else {
			validateRequired(titulo, "Título", lista);
			validateRequired(numeroSugeridoFolhas, "Número de Páginas", lista);
			if (isGraduacao() && !registroExterno)
				validateRequired(cursosRegistrados, "Lista de Cursos", lista);
			if (registroExterno)
				validateRequired(instituicao, "Instituição", lista);
		}
		validateMinValue(numeroRegistroPorFolha, 1, "Número de Registros por Folha", lista);
		return lista;
	}
	
	/** Retorna uma String com a lista de cursos registrados no livro, separados por vírgula.
	 * @return
	 */
	public String getDescricaoCursos() {
		StringBuilder aux = new StringBuilder(); 
		if (cursosRegistrados == null || cursosRegistrados.size() == 0) {
			return "Não há cursos especificados para registro neste livro";
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
		// não achou folha com registro livre? Adicione outra folha:
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

	/** Retorna o Nível de Ensino dos discentes registrados neste livro.
	 * @return
	 */
	public char getNivel() {
		return nivel;
	}
	
	public String getNivelDesc() {
		return NivelEnsino.getDescricao(nivel);
	}

	/** Seta o Nível de Ensino dos discentes registrados neste livro.
	 * @param nivel
	 */
	public void setNivel(char nivel) {
		if (nivel == NivelEnsino.LATO)
			this.registroCertificado = true;
		else
			this.registroCertificado = false;
		this.nivel = nivel;
	}

	/** Indica se o livro é para o registro de diplomas de stricto sensu.
	 * @return
	 */
	public boolean isStrictoSensu() {
		return NivelEnsino.STRICTO == nivel;
	}
	
	/** Indica se o livro é para o registro de diplomas de lato sensu.
	 * @return
	 */
	public boolean isLatoSensu() {
		return NivelEnsino.LATO == nivel;
	}
	
	/** Indica se o livro é para o registro de diplomas de graduação.
	 * @return
	 */
	public boolean isGraduacao() {
		return NivelEnsino.GRADUACAO == nivel;
	}

	/** Indica que este livro será utilizado para registrar certificados, e não diplomas. 
	 * @return
	 */
	public boolean isRegistroCertificado() {
		return registroCertificado;
	}

	/** Seta que este livro será utilizado para registrar certificados, e não diplomas. 
	 * @param registroCertificado
	 */
	public void setRegistroCertificado(boolean registroCertificado) {
		this.registroCertificado = registroCertificado;
	}
	
	/** Retorna uma descrição textual do tipo de registro efetuado neste livro (Certificado ou Diploma).
	 * @return
	 */
	public String getTipoRegistroDescricao() {
		if (registroCertificado) 
			return "Certificado";
		else 
			return "Diploma";
	}

	/** Indica que o livro não possui registros de diplomas.
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

	/** Adiciona um curso à lista de cursos registrados neste livro
	 * @param curso
	 */
	public void addCurso(Curso curso) {
		if (cursosRegistrados == null)
			cursosRegistrados = new ArrayList<Curso>();
		cursosRegistrados.add(curso);
	}
}
