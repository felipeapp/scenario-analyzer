/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Criado em 03/09/2009
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Classe que auxilia a gera��o do relat�rio de usu�rios em atraso.
 * Cada inst�ncia dela cont�m um empr�stimo ou uma suspens�o do usu�rio (ou os dois).
 * 
 * @author Fred_Castro
 * @author Br�ulio
 */
public class RelatorioSituacaoDosUsuarios {
	/** Id do usu�rio biblioteca */
	private int id;
	
	/** Id do empr�stimo que est� em atraso*/
	private int idEmprestimo;
	
	/** Nome do usu�rio*/
	private String nome;
	
	/** Nome da biblioteca */
	private String biblioteca;
	
	/** Nome da unidade */
	private String unidade;
	
	/** Discente, p�s-gradua��o, docente, servidor, etc. */
	private String categoriaDoUsuario;

	/** O CPF ou CNPJ do usu�rio. */
	private String cpfCnpj;
	
	/** Data do empr�stimo */
	private Date dataEmprestimo;
	
	/** Prazo para devolu��o */
	private Date prazo;
	
	/** O valor da multa */
	private BigDecimal valorMulta;
	
	/** Data de devolu��o do empr�stimo, se devolveu. */
	private Date dataDevolucao;
	
	/** Data inicial da suspens�o. */
	private Date inicioSuspensao;
	
	/** Data final da suspens�o. */
	private Date prazoSuspensao;
	
	/** Respons�vel pela suspens�o manual. */
	private String suspensaoManualCadastradaPor;
	
	/** Motivo da suspens�o manual. ou multa manual*/
	private String motivoPunicao;
	
	/** C�digo de barras do material */
	private String codigoBarras;
	
	/** T�tulo do material */
	private String titulo;
	
	/** Autor do material */
	private String autor;
	
	/** Indica se � usu�rio externo ou n�o */
	private boolean usuarioExterno;
	
	/** Matr�cula, se o usu�rio tiver uma (discentes). */
	private String matricula;
	
	/** Siape, se o usu�rio for um funcion�rio. */
	private String siape;
	
	/**
	 * Indica se os dados da puni��o s�o por suspens�o, sen�o obrigatoriamente s�o por multa
	 */
	private boolean isPunicaoPorSuspensao;
	
	//// Gets e sets
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public int getIdEmprestimo() { return idEmprestimo; }
	public void setIdEmprestimo(int idEmprestimo) { this.idEmprestimo = idEmprestimo; }

	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }
	
	public String getBiblioteca() { return biblioteca; }
	public void setBiblioteca(String biblioteca) { this.biblioteca = biblioteca; }
	
	public String getUnidade() { return unidade; }
	public void setUnidade(String unidade) { this.unidade = unidade; }
	
	public Date getDataEmprestimo() { return dataEmprestimo; }
	public void setDataEmprestimo(Date dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }
	
	public Date getPrazo() { return prazo; }
	public void setPrazo(Date prazo) { this.prazo = prazo; }
	
	public String getCodigoBarras() { return codigoBarras; }
	public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
	
	public boolean isUsuarioExterno() { return usuarioExterno; }
	public void setUsuarioExterno(boolean usuarioExterno) { this.usuarioExterno = usuarioExterno; }

	public String getTitulo() { return titulo; }
	public void setTitulo(String titulo) { this.titulo = titulo; }

	public String getAutor() { return autor; }
	public void setAutor(String autor) { this.autor = autor; }

	public String getCpfCnpj() { return cpfCnpj; }
	public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }

	public String getMatricula() { return matricula; }
	public void setMatricula(String matricula) { this.matricula = matricula; }

	public String getSiape() { return siape; }
	public void setSiape(String siape) { this.siape = siape; }
	
	public String getCategoriaDoUsuario() { return categoriaDoUsuario; }
	public void setCategoriaDoUsuario(String categoriaDoUsuario) { this.categoriaDoUsuario = categoriaDoUsuario; }

	public Date getDataDevolucao() { return dataDevolucao; }
	public void setDataDevolucao(Date dataDevolucao) { this.dataDevolucao = dataDevolucao; }

	public Date getPrazoSuspensao() { return prazoSuspensao; }
	public void setPrazoSuspensao(Date prazoSuspensao) { this.prazoSuspensao = prazoSuspensao; }

	public String getSuspensaoManualCadastradaPor() { return suspensaoManualCadastradaPor; }
	public void setSuspensaoManualCadastradaPor(String suspensaoManualCadastradaPor) { this.suspensaoManualCadastradaPor = suspensaoManualCadastradaPor; }

	public String getMotivoPunicao() { return motivoPunicao; }
	public void setMotivoPunicao(String motivoPunicao) { this.motivoPunicao = motivoPunicao; }

	public Date getInicioSuspensao() { return inicioSuspensao; } 
	public void setInicioSuspensao(Date inicioSuspensao) { this.inicioSuspensao = inicioSuspensao; }
	
	public BigDecimal getValorMulta() {return valorMulta; }
	public void setValorMulta(BigDecimal valorMulta) {this.valorMulta = valorMulta;}
	
	public boolean isPunicaoPorSuspensao() {return isPunicaoPorSuspensao;}
	public void setPunicaoPorSuspensao(boolean isPunicaoPorSuspensao) {this.isPunicaoPorSuspensao = isPunicaoPorSuspensao;}

	
	
	
}