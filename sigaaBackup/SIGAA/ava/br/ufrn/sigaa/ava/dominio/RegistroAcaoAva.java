/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/08/2010
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * 
 * Entidade que registra todas as ações que os usuários do sistema realizam sobre a Turma Virtual.
 * 
 * @author Fred_Castro
 * 
 */
@Entity
@Table(name = "registro_acao_ava", schema = "ava")
public class RegistroAcaoAva implements DominioTurmaVirtual {
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="ava.registro_acao_seq") })
	@Column(name = "id_registro_acao_ava")
	private int id;
	
	private String login;
	
	private String nome;
	
	private String descricao;
	
	@Transient
	private EntidadeRegistroAva entidadeRA;
	
	/**
	 * Inidica qual entidade foi utilizada.
	 * @see br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva
	 */
	private int entidade;
	
	/**
	 * Indica qual acao foi realizada. 
	 * @see br.ufrn.sigaa.ava.dominio.AcaoAva
	 */
	private int acao;
	
	@Transient
	private AcaoAva acaoAva = AcaoAva.ACESSAR;
	
	/**
	 * O id do objeto da entidade utilizada.
	 */
	@Column(name="id_entidade")
	private Integer idEntidade;
	
	/**
	 * O id do objeto da entidade complementar utilizada.
	 */
	@Column(name="id_entidade_complementar")
	private Integer idEntidadeComplementar;
	
	/**
	 * Turma em que a ação foi realizada.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_turma")
	private Turma turma;
	
	/**
	 * Registro entrada do usuário que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/**
	 * Data em que a ação foi realizada.
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	public RegistroAcaoAva (){
		
	}
	
	public RegistroAcaoAva (Usuario usuario, String descricao, EntidadeRegistroAva entidadeRA, AcaoAva acaoAva, int idTurma, int ... ids) throws ArqException{
		this.login = usuario.getLogin();
		this.nome = usuario.getPessoa().getNome();
		this.descricao = descricao;
		this.entidadeRA = entidadeRA;
		this.entidade = entidadeRA.getValor();
		this.acaoAva = acaoAva;
		this.acao = acaoAva.getValor();
		this.turma = new Turma(idTurma);
		
		if (acaoAva.isRequerEntidade()){
			if (ids != null && ids.length > 0)
				this.idEntidade = ids[0];
			else
				throw new ArqException ("A entidade é obrigatória para a ação especificada.");
		}
		
		if (acaoAva.isRequerEntidadeComplementar()){
			if (ids != null && ids.length > 1)
				this.idEntidadeComplementar = ids[1];
			else
				throw new ArqException ("A entidade complementar é obrigatória para a ação especificada.");
		}
		
	}

	@Override
	public int getId () {
		return id;
	}

	@Override
	public void setId (int id) {
		this.id = id;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public int getEntidade() {
		entidade = entidadeRA.getValor();
		return entidade;
	}

	public void setEntidade(int entidade) {
		this.entidadeRA = EntidadeRegistroAva.getEntidade(entidade);
		this.entidade = entidade;
	}

	public int getAcao() {
		acao = acaoAva.getValor();
		return acao;
	}

	public void setAcao (int acao) {
		acaoAva = AcaoAva.getAcaoAva(acao);
		this.acao = acao;
	}

	public Integer getIdEntidade() {
		return idEntidade;
	}

	public void setIdEntidade(Integer idEntidade) {
		this.idEntidade = idEntidade;
	}

	public Integer getIdEntidadeComplementar() {
		return idEntidadeComplementar;
	}

	public void setIdEntidadeComplementar(Integer idEntidadeComplementar) {
		this.idEntidadeComplementar = idEntidadeComplementar;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	@Override
	public String getMensagemAtividade() {
		return null;
	}

	public AcaoAva getAcaoAva() {
		return acaoAva;
	}

	public void setAcaoAva(AcaoAva acaoAva) {
		this.acaoAva = acaoAva;
	}

	public EntidadeRegistroAva getEntidadeRA() {
		return entidadeRA;
	}

	public void setEntidadeRA(EntidadeRegistroAva entidadeRA) {
		this.entidadeRA = entidadeRA;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
