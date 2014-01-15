/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.RegistroEntrada;

/**
 * Essa entidade registra o bloqueio de produções intelectuais.
 * Caso exista alguma ativa, ele redireciona para uma página de explicação e não
 * deixa o docente cadastrar.
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "bloqueio_cadastro_producao", schema = "prodocente")
public class BloqueioCadastroProducao {

	@Id@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_bloqueio_cadastro_producao", nullable = false)
	private int id;

	private String explicacao;

	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="id_registro_entrada")
	private RegistroEntrada registro;

	@Column(name="data_cadastro")
	private Date dataCadastro;

	private boolean ativo;

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getExplicacao() {
		return explicacao;
	}

	public void setExplicacao(String explicacao) {
		this.explicacao = explicacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegistroEntrada getRegistro() {
		return registro;
	}

	public void setRegistro(RegistroEntrada registro) {
		this.registro = registro;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}