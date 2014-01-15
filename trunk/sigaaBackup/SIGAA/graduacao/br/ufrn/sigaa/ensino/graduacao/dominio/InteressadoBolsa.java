/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/02/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * O candidato que estiver interessado em uma bolsa e não possui cadastro em um processo
 * seletivo deve ser cadastrado aqui o interesse do aluno em participar do
 * estagio.
 * 
 * @author Henrique André
 * 
 */
@Entity
@Table(schema = "graduacao", name = "interessado_bolsa")
public class InteressadoBolsa implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_interessado")
	private int id;

	/**
	 * Estagio que o aluno esta interessado em participar
	 */
	@Column(name = "id_oportunidade")
	private int idEstagio;

	/**
	 * ID usuário do aluno que deseja pleitear a vaga
	 */
	@Column(name = "id_usuario")
	private int idUsuario;

	@ManyToOne
	@JoinColumn(name = "id_discente")
	private Discente discente;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_dados_aluno")
	private DadosAluno dados;

	@ManyToOne
	@JoinColumn(name = "id_tipo")
	private TipoInteressadoBolsa tipoBolsa;

	@ManyToOne
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	@ManyToOne
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	@Column(name = "ativo")
	private boolean ativo = true;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdEstagio() {
		return idEstagio;
	}

	public void setIdEstagio(int idEstagio) {
		this.idEstagio = idEstagio;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public TipoInteressadoBolsa getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoInteressadoBolsa tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public DadosAluno getDados() {
		return dados;
	}

	public void setDados(DadosAluno dados) {
		this.dados = dados;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isPesquisa() {
		return getTipoBolsa().equals(TipoInteressadoBolsa.PESQUISA);
	}
	
	public boolean isApoioTecnico() {
		return getTipoBolsa().equals(TipoInteressadoBolsa.APOIO_TECNICO);
	}
	
	public boolean isExtensao(){
		return getTipoBolsa().equals(TipoInteressadoBolsa.EXTENSAO);
	}
	
	public boolean isMonitoria(){
		return getTipoBolsa().equals(TipoInteressadoBolsa.MONITORIA);
	}
}
