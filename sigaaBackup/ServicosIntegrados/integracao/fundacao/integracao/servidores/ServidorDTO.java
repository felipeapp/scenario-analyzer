package fundacao.integracao.servidores;

import java.io.Serializable;
import java.util.Date;

import fundacao.integracao.comum.UnidadeDTO;

/**
 * 
 * DTO de integração com o sistema da Fundação. 
 * 
 * @author Gleydson Lima
 *
 */
public class ServidorDTO implements Serializable {

	private int id;

	private String siape;

	private Long cpf;

	private String nome;

	private byte categoria;

	private int situacao;

	private int ativo;
	
	private Date desligamento;
	
	private Date exclusao;
	
	private String cargo;
	
	private UnidadeDTO unidade;
	
	private Date ultimaAtualizacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSiape() {
		return siape;
	}

	public void setSiape(String siape) {
		this.siape = siape;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public byte getCategoria() {
		return categoria;
	}

	public void setCategoria(byte categoria) {
		this.categoria = categoria;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}

	public int getAtivo() {
		return ativo;
	}

	public void setAtivo(int ativo) {
		this.ativo = ativo;
	}
	public Date getDesligamento() {
		return desligamento;
	}

	public void setDesligamento(Date desligamento) {
		this.desligamento = desligamento;
	}

	public Date getExclusao() {
		return exclusao;
	}

	public void setExclusao(Date exclusao) {
		this.exclusao = exclusao;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public UnidadeDTO getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadeDTO unidade) {
		this.unidade = unidade;
	}
	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	public Date getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

}
