/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Apr 3, 2007
 *
 */
package br.ufrn.sigaa.eleicao.dominio;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Representa um candidato para a eleição para diretor de centro
 * @author Victor Hugo
 */
@Entity
@Table(schema="comum", name = "candidato", uniqueConstraints = {})
public class Candidato implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_candidato", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** id do arquivo da foto do candidato */
	@Column(name = "id_foto")
	private Integer idFoto;

	/** descrição do candidato */
	private String descricao;

	/** id da chapa do candidato */
	private Integer chapa;
	
	/** id da eleição de que o candidato ta participando */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_eleicao", unique = false, insertable = true, updatable = true)
	private Eleicao eleicao = new Eleicao();

	/** referencia para o servidor que é o candidato */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor", unique = false, nullable = true, insertable = true, updatable = true)
	private Servidor servidor = new Servidor();

	/** data de cadastro deste candidato */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	private Date dataCadastro;

	/** registro de entrada do cadastro deste candidato */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Integer getChapa() {
		return chapa;
	}

	public void setChapa(Integer chapa) {
		this.chapa = chapa;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Eleicao getEleicao() {
		return eleicao;
	}

	public void setEleicao(Eleicao eleicao) {
		this.eleicao = eleicao;
	}

	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		ValidatorUtil.validateRequired(chapa, "Chapa", lista);
		ValidatorUtil.validateRequired(eleicao, "Eleição", lista);
		
//		Eleição do DCE é feita para discentes
//		ValidatorUtil.validateRequired(servidor, "Servidor", lista);

		return lista;
		
	}
	
}
