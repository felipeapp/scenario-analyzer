/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Criado em 03/09/2009
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Classe que auxilia a geração do relatório de usuários em atraso.
 * Cada instância dela contém um empréstimo ou uma suspensão do usuário (ou os dois).
 * 
 * @author Fred_Castro
 * @author Bráulio
 */
public class RelatorioSituacaoDosUsuarios {
	/** Id do usuário biblioteca */
	private int id;
	
	/** Id do empréstimo que está em atraso*/
	private int idEmprestimo;
	
	/** Nome do usuário*/
	private String nome;
	
	/** Nome da biblioteca */
	private String biblioteca;
	
	/** Nome da unidade */
	private String unidade;
	
	/** Discente, pós-graduação, docente, servidor, etc. */
	private String categoriaDoUsuario;

	/** O CPF ou CNPJ do usuário. */
	private String cpfCnpj;
	
	/** Data do empréstimo */
	private Date dataEmprestimo;
	
	/** Prazo para devolução */
	private Date prazo;
	
	/** O valor da multa */
	private BigDecimal valorMulta;
	
	/** Data de devolução do empréstimo, se devolveu. */
	private Date dataDevolucao;
	
	/** Data inicial da suspensão. */
	private Date inicioSuspensao;
	
	/** Data final da suspensão. */
	private Date prazoSuspensao;
	
	/** Responsável pela suspensão manual. */
	private String suspensaoManualCadastradaPor;
	
	/** Motivo da suspensão manual. ou multa manual*/
	private String motivoPunicao;
	
	/** Código de barras do material */
	private String codigoBarras;
	
	/** Título do material */
	private String titulo;
	
	/** Autor do material */
	private String autor;
	
	/** Indica se é usuário externo ou não */
	private boolean usuarioExterno;
	
	/** Matrícula, se o usuário tiver uma (discentes). */
	private String matricula;
	
	/** Siape, se o usuário for um funcionário. */
	private String siape;
	
	/**
	 * Indica se os dados da punição são por suspensão, senão obrigatoriamente são por multa
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