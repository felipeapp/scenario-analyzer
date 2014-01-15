/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Criado em: 20/05/2013
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;

/**
 * <p>Classe que representa as informa��es do termo de Ades�o que novos usu�rios do sistema de bibliotecas precisam concordar.</p>
 * 
 * @author deyvyd
 * @version 1.0 cria��o da classe
 */
@Entity
@Table(name = "termo_adesao_sistema_bibliotecas", schema = "biblioteca")
public class TermoAdesaoSistemaBibliotecas implements PersistDB{
	
	/**
	 * id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })
	@Column(name="id_termo_adesao_sistema_bibliotecas")
	private int id;
	
	
	/** Registro de entrada do usu�rio no sistema. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", nullable=true)
	private RegistroEntrada registroEntrada;
	
	/** O usu�rio biblioteca criado ao fim do cadastro. */
	@ManyToOne(cascade  =  {}, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_usuario_biblioteca", referencedColumnName ="id_usuario_biblioteca", nullable=false)
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/////// Informa��es usadas para gerar o hash  ///////////

	/** Texto do termo de ades�o da biblioteca. */
	@Column(name="texto", nullable = false)
	private String texto;
	
	/** Data que o usu�rio assinou o termo. */
	@Column(name="data", nullable = false)
	private Date data;
	
	/** Nome do usu�rio que concordou com o termo. */
	@Column(name="nome_pessoa", nullable = false)
	private String nomePessoa;
	
	/** CPF/Passaporte do usu�rio que concordou com o termo. Caso o usu�rio n�o tenha CPF, se usa o Passaporte. */
	@Column(name="cpf_passaporte", nullable = false)
	private String cpfPassaporte;
	
	/** Matr�cula do usu�rio  que concordou com o termo. Se for Docente Externo, a matr�cula � nula. */
	@Column(name="matricula")
	private Long matricula;

	/** Curso/Lota��o do usu�rio  que concordou com o termo. */
	@Column(name="nome_unidade")
	private String nomeUnidade;
	
	//////////////////////////////////////////////////
	
	/** Hash para valida��o. */
	@Column(name="hash_md5", nullable = false)
	private String hashMD5;
	
	

	public TermoAdesaoSistemaBibliotecas() {
		
	}

	

	/** Construtor de um novo termo com as informa��es do usu�rio.*/
	public TermoAdesaoSistemaBibliotecas(RegistroEntrada registro, String texto, Date dataAssinatura, String nomePessoa, String identificador) {
		this.registroEntrada = registro;
		this.texto = texto;
		this.data = dataAssinatura;
		this.nomePessoa = nomePessoa;
		this.cpfPassaporte = identificador;
	}



	/**
	 * Gera o hash para provar que as informa��es impressas no termo n�o foram alteradas pelos usu�rios
	 * 
	 * <p> Criado em:  24/07/2013  </p>
	 * 
	 * <strong>Tem que seguir a ordem:  texto do termo + data + nomePessoa + cpfPassaporte + matricula + nomeDaUnidade. Se alterar tem que regerar tudo novamente!!!! </strong>.
	 *
	 *
	 */
	public void geraHash(){
		hashMD5 = UFRNUtils.toMD5(
				( StringUtils.notEmpty(texto) ? texto : "")   
				+ getDataFormatada() 
				+ ( StringUtils.notEmpty(nomePessoa) ? nomePessoa : "")   
				+ ( StringUtils.notEmpty(cpfPassaporte) ? cpfPassaporte : "")     
				+ matricula 
				+ ( StringUtils.notEmpty(nomeUnidade) ? nomeUnidade : "") );
	}
	
	

	/*** retorna a data do termo formatada para visualiza��o */
	public String getDataFormatada() {
		if( data == null) return "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sdf.format(data);
	}
	
	
	
	
	//GETTERS AND SETTERS
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Date getData() {
		return data;
	}
	
	public void setData(Date data) {
		this.data = data;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public UsuarioBiblioteca getUsuarioBibioteca() {
		return usuarioBiblioteca;
	}

	public void setUsuarioBibioteca(UsuarioBiblioteca usuarioBibioteca) {
		this.usuarioBiblioteca = usuarioBibioteca;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getHashMd5() {
		return hashMD5;
	}

	public void setHashMd5(String hashMd5) {
		this.hashMD5 = hashMd5;
	}

	public UsuarioBiblioteca getUsuarioBiblioteca() {
		return usuarioBiblioteca;
	}

	public void setUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca) {
		this.usuarioBiblioteca = usuarioBiblioteca;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}

	public String getCpfPassaporte() {
		return cpfPassaporte;
	}

	public void setCpfPassaporte(String cpfPassaporte) {
		this.cpfPassaporte = cpfPassaporte;
	}

	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public String getNomeUnidade() {
		return nomeUnidade;
	}

	public void setNomeUnidade(String nomeUnidade) {
		this.nomeUnidade = nomeUnidade;
	}

	public String getHashMD5() {
		return hashMD5;
	}

	public void setHashMD5(String hashMD5) {
		this.hashMD5 = hashMD5;
	}


}
