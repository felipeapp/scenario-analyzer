package br.ufrn.comum.wireless;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade responsavel pela AUTENTICACAO na rede Wireless
 * da UFRN quando o visitante for de fora da instituicao.
 * 
 * Autenticacao:
 * No 1 acesso, o visitante informa CPF, alguns dados pessoais e cria uma senha.
 * A partir do 2 acesso ele apenas precisa informar o CPF e a senha.
 * 
 * Autorizacao:
 * Apos ser autenticado, o visitante é autorizado a acessar a Wireless por 
 * um periodo de tempo pre-determinado pelo departamento ao qual está 
 * vinculado (de 1 a N dias).
 * 
 * @author  agostinho
 *
 */

@Entity
@Table(name="auten_wireless_users_ext", schema="comum")
public class AutenticacaoUsersExt implements PersistDB {
	
	@Id
	@Column(name="id")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	private String senha;
	
	@Column(name="registro_geral")
	private String registroGeral;
	
	@Column(name="instituicao_origem")
	private String instituicaoOrigem;
	
	private String nome;
	
	@OneToMany(cascade= {CascadeType.ALL}, mappedBy="autenticacao")
	@JoinColumn(name = "id")
	private List<AutorizacaoUsersExt> autorizacoes;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getRegistroGeral() {
		return registroGeral;
	}

	public void setRegistroGeral(String registroGeral) {
		this.registroGeral = registroGeral;
	}

	public String getInstituicaoOrigem() {
		return instituicaoOrigem;
	}

	public void setInstituicaoOrigem(String instituicaoOrigem) {
		this.instituicaoOrigem = instituicaoOrigem;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<AutorizacaoUsersExt> getAutorizacoes() {
		return autorizacoes;
	}

	public void setAutorizacoes(List<AutorizacaoUsersExt> autorizacoes) {
		this.autorizacoes = autorizacoes;
	}

}
