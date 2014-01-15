/*
 * OperacoesDesfeitasDesktop.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 *    Classe que guarda as opera��es que foram desfeitas no desktop para o caso de auditorias.
 *
 * @author jadson
 * @since 14/09/2009
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "operacoes_desfeitas_desktop", schema = "biblioteca")
public class OperacoesDesfeitasDesktop implements PersistDB{

	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name="id_operacoes_desfeitas_desktop", nullable=false)
	private int id;
	
	
	@OneToOne
	@JoinColumn(name = "id_emprestimo", referencedColumnName = "id_emprestimo")
	private Emprestimo emprestimo; // o empr�stimo sobre o qual a opera��o foi desfeita.
	
	
	@Column(name="operacao", nullable=false)
	private String operacao; //  a descri��o da opera��o que foi desfeita. Ex.: "Desfez Empr�stimo"...
	
	@ManyToOne
	@JoinColumn(name = "id_operador", referencedColumnName = "id_usuario")
	private Usuario operador;   // quem desfez a opera��o;
	
	@ManyToOne
	@JoinColumn(name = "id_autorizador", referencedColumnName = "id_usuario")
	private Usuario autorizador;   // quem autorizou a opera��o a ser desfeita.
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data", nullable=false)
	private Date data; // a data que a opera��o foi desfeita
	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Emprestimo getEmprestimo() {
		return emprestimo;
	}

	public void setEmprestimo(Emprestimo emprestimo) {
		this.emprestimo = emprestimo;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public Usuario getOperador() {
		return operador;
	}

	public void setOperador(Usuario operador) {
		this.operador = operador;
	}

	public Usuario getAutorizador() {
		return autorizador;
	}

	public void setAutorizador(Usuario autorizador) {
		this.autorizador = autorizador;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	
	
}
