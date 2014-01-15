/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 17/05/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 *
 * <p>Classe que representa um serviço de empréstimos que a biblioteca internas podem realizar.</p>
 *
 * <p>Essa classe é formada por um conjunto de booleanos e a lista de tipos de empréstimos aos quais esse conjunto de booleanos é aplicado.
 * Só pode existir um boolean ativo por vez. Indicando qual o serviço o objeto se refere, a variável booleana "ativo" informa se o referido 
 * serviço está ativo ou não. </p>
 *
 * <p> <i> Vai ser criado um novo objeto para cada serviço prestado pela biblioteca, como esses serviços envolvem mudança na regra de <br/>
 * negócio do sistema eles são predifinidos e não vai existir um cadastro do sistema para isso. Criação de um novo serviço envolve mudança no código. </i> </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(name = "servicos_emprestimos_biblioteca", schema = "biblioteca")
public class ServicosEmprestimosBiblioteca implements Validatable{
	
	/** O identificador */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })
	@Column(name = "id_servicos_emprestimos_biblioteca")
	private int id;
	
	/** A biblioteca  a quem esse serviço pertence. Unidirecional, a biblioteca só precisa saber os serviços 
	 *  de empréstimos que ele presta no momento de realizar empréstimos. Então não precisa carregar as informações desses 
	 *  serviços para todas as partes do sistema. */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", nullable = false)
	private Biblioteca biblioteca;
	
	/** Verdadeiro se esse serviço é o serviço de empréstimo entre biblioteca, */
	@Column(name="emprestimo_institucional_interno", nullable=false)
	private boolean emprestimoInstitucionalInterno = false;

	/** Verdadeiro se esse serviço é o serviço de empréstimo entre biblioteca, */
	@Column(name="emprestimo_institucional_externo", nullable=false)
	private boolean emprestimoInstitucionalExterno = false;
	
	/** Verdadeiro se esse serviço é o serviço de empréstimo entre alunos de infantil/tecnico/graduação do mesmo centro. 
	 * Até o nomento só existe distinção em relação aos alunos de pós. O resto é considerado de graduação nesse caso. */
	@Column(name="empresta_alunos_graduacao_mesmo_centro", nullable=false)
	private boolean emprestaAlunosGraduacaoMesmoCentro = false;
	
	/** Verdadeiro se esse serviço é o serviço de empréstimo entre alunos de pós do mesmo centro */
	@Column(name="empresta_alunos_pos_mesmo_centro", nullable=false)
	private boolean emprestaAlunosPosMesmoCentro = false;


	/** Verdadeiro se esse serviço é o serviço de empréstimo entre alunos de infantil/tecnico/graduação outro centro.
	 * Até o nomento só existe distinção em relação aos alunos de pós. O resto é considerado de graduação nesse caso.*/
	@Column(name="empresta_alunos_graduacao_outro_centro", nullable=false)
	private boolean emprestaAlunosGraduacaoOutroCentro = false;	

	/** Verdadeiro se esse serviço é o serviço de empréstimo entre alunos de pós de outro centro */
	@Column(name="empresta_alunos_pos_outro_centro", nullable=false)
	private boolean emprestaAlunosPosOutroCentro = false;

	
	
	/**
	 * Se o serviço vai está ativo ou não. Configurado no cadastro da biblioteca.
	 */
	@Column(name="ativo", nullable=false)
	private boolean ativo = false;
	
	/**
	 * Os serviços de empréstimos podem ser realizados apenas para um tipo de empréstimos específico.
	 * 
	 * Essa informação guarda os tipos de empréstimos que o serviço realiza. Unidirecional, o tipo de empréstimo 
	 * não precisa saber quais o serviços que o utilizam.
	 */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="biblioteca.servicos_emprestimos_biblioteca_tipo_emprestimo", 
			joinColumns={@JoinColumn(name="id_servicos_emprestimos_biblioteca")}, inverseJoinColumns={@JoinColumn(name="id_tipo_emprestimo")})
	private List<TipoEmprestimo> tiposEmprestimos;


	public boolean isEmprestimoInstitucionalInterno() {
		return emprestimoInstitucionalInterno;
	}

	public void setEmprestimoInstitucionalInterno(boolean emprestimoInstitucionalInterno) {
		this.emprestimoInstitucionalInterno = emprestimoInstitucionalInterno;
	}

	public boolean isEmprestimoInstitucionalExterno() {
		return emprestimoInstitucionalExterno;
	}

	public void setEmprestimoInstitucionalExterno(
			boolean emprestimoInstitucionalExterno) {
		this.emprestimoInstitucionalExterno = emprestimoInstitucionalExterno;
	}

	public boolean isEmprestaAlunosPosMesmoCentro() {
		return emprestaAlunosPosMesmoCentro;
	}

	public void setEmprestaAlunosPosMesmoCentro(boolean emprestaAlunosPosMesmoCentro) {
		this.emprestaAlunosPosMesmoCentro = emprestaAlunosPosMesmoCentro;
	}

	public boolean isEmprestaAlunosGraduacaoMesmoCentro() {
		return emprestaAlunosGraduacaoMesmoCentro;
	}

	public void setEmprestaAlunosGraduacaoMesmoCentro(boolean emprestaAlunosGraduacaoMesmoCentro) {
		this.emprestaAlunosGraduacaoMesmoCentro = emprestaAlunosGraduacaoMesmoCentro;
	}

	public boolean isEmprestaAlunosPosOutroCentro() {
		return emprestaAlunosPosOutroCentro;
	}

	public void setEmprestaAlunosPosOutroCentro(boolean emprestaAlunosPosOutroCentro) {
		this.emprestaAlunosPosOutroCentro = emprestaAlunosPosOutroCentro;
	}

	public boolean isEmprestaAlunosGraduacaoOutroCentro() {
		return emprestaAlunosGraduacaoOutroCentro;
	}

	public void setEmprestaAlunosGraduacaoOutroCentro(boolean emprestaAlunosGraduacaoOutroCentro) {
		this.emprestaAlunosGraduacaoOutroCentro = emprestaAlunosGraduacaoOutroCentro;
	}

	public java.util.List<TipoEmprestimo> getTiposEmprestimos() {
		return tiposEmprestimos;
	}

	public void setTiposEmprestimos(java.util.List<TipoEmprestimo> tiposEmprestimos) {
		this.tiposEmprestimos = tiposEmprestimos;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	/**
	 * Verifica se os dados dos serviços estão corretos antes de atualizar nobanco
	 *
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		
		ListaMensagens erros = new ListaMensagens();
		
		if(biblioteca == null)
			erros.addErro("O serviço de empréstimos precisa estar associado a uma biblioteca");
		
		int qtdVerdadeiros = 0;
		
		
		if( emprestimoInstitucionalInterno)
			qtdVerdadeiros++;

		if( emprestimoInstitucionalExterno)
			qtdVerdadeiros++;
		
		if( emprestaAlunosGraduacaoMesmoCentro)
			qtdVerdadeiros++;
		
		if( emprestaAlunosPosMesmoCentro)
			qtdVerdadeiros++;
		
		
		if( emprestaAlunosGraduacaoOutroCentro)
			qtdVerdadeiros++;
		
		if( emprestaAlunosPosOutroCentro)
			qtdVerdadeiros++;
		
		if(qtdVerdadeiros > 1)
			erros.addErro("Um serviço de empréstimos só pode representar um serviço");
		
		return erros;
	}
	
}
