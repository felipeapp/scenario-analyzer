/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Classe que representa um servi�o de empr�stimos que a biblioteca internas podem realizar.</p>
 *
 * <p>Essa classe � formada por um conjunto de booleanos e a lista de tipos de empr�stimos aos quais esse conjunto de booleanos � aplicado.
 * S� pode existir um boolean ativo por vez. Indicando qual o servi�o o objeto se refere, a vari�vel booleana "ativo" informa se o referido 
 * servi�o est� ativo ou n�o. </p>
 *
 * <p> <i> Vai ser criado um novo objeto para cada servi�o prestado pela biblioteca, como esses servi�os envolvem mudan�a na regra de <br/>
 * neg�cio do sistema eles s�o predifinidos e n�o vai existir um cadastro do sistema para isso. Cria��o de um novo servi�o envolve mudan�a no c�digo. </i> </p>
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
	
	/** A biblioteca  a quem esse servi�o pertence. Unidirecional, a biblioteca s� precisa saber os servi�os 
	 *  de empr�stimos que ele presta no momento de realizar empr�stimos. Ent�o n�o precisa carregar as informa��es desses 
	 *  servi�os para todas as partes do sistema. */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", nullable = false)
	private Biblioteca biblioteca;
	
	/** Verdadeiro se esse servi�o � o servi�o de empr�stimo entre biblioteca, */
	@Column(name="emprestimo_institucional_interno", nullable=false)
	private boolean emprestimoInstitucionalInterno = false;

	/** Verdadeiro se esse servi�o � o servi�o de empr�stimo entre biblioteca, */
	@Column(name="emprestimo_institucional_externo", nullable=false)
	private boolean emprestimoInstitucionalExterno = false;
	
	/** Verdadeiro se esse servi�o � o servi�o de empr�stimo entre alunos de infantil/tecnico/gradua��o do mesmo centro. 
	 * At� o nomento s� existe distin��o em rela��o aos alunos de p�s. O resto � considerado de gradua��o nesse caso. */
	@Column(name="empresta_alunos_graduacao_mesmo_centro", nullable=false)
	private boolean emprestaAlunosGraduacaoMesmoCentro = false;
	
	/** Verdadeiro se esse servi�o � o servi�o de empr�stimo entre alunos de p�s do mesmo centro */
	@Column(name="empresta_alunos_pos_mesmo_centro", nullable=false)
	private boolean emprestaAlunosPosMesmoCentro = false;


	/** Verdadeiro se esse servi�o � o servi�o de empr�stimo entre alunos de infantil/tecnico/gradua��o outro centro.
	 * At� o nomento s� existe distin��o em rela��o aos alunos de p�s. O resto � considerado de gradua��o nesse caso.*/
	@Column(name="empresta_alunos_graduacao_outro_centro", nullable=false)
	private boolean emprestaAlunosGraduacaoOutroCentro = false;	

	/** Verdadeiro se esse servi�o � o servi�o de empr�stimo entre alunos de p�s de outro centro */
	@Column(name="empresta_alunos_pos_outro_centro", nullable=false)
	private boolean emprestaAlunosPosOutroCentro = false;

	
	
	/**
	 * Se o servi�o vai est� ativo ou n�o. Configurado no cadastro da biblioteca.
	 */
	@Column(name="ativo", nullable=false)
	private boolean ativo = false;
	
	/**
	 * Os servi�os de empr�stimos podem ser realizados apenas para um tipo de empr�stimos espec�fico.
	 * 
	 * Essa informa��o guarda os tipos de empr�stimos que o servi�o realiza. Unidirecional, o tipo de empr�stimo 
	 * n�o precisa saber quais o servi�os que o utilizam.
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
	 * Verifica se os dados dos servi�os est�o corretos antes de atualizar nobanco
	 *
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		
		ListaMensagens erros = new ListaMensagens();
		
		if(biblioteca == null)
			erros.addErro("O servi�o de empr�stimos precisa estar associado a uma biblioteca");
		
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
			erros.addErro("Um servi�o de empr�stimos s� pode representar um servi�o");
		
		return erros;
	}
	
}
