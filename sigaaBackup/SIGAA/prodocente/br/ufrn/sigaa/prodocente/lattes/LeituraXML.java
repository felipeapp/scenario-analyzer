/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.lattes;

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
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe para registrar a importação de um currículo lattes
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(name="leitura_xml", schema="prodocente")
public class LeituraXML implements PersistDB {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="prodocente.hibernate_sequence") })
	@Column(name = "id")
	private int id;

	/** Conteúdo do XML */
	@Column(name="conteudo_xml")
	private String conteudoXml;
	
	/** Conteúdo efetivamente importado */
	@Column(name="conteudo_importado")
	private String conteudoImportado;
	
	/** Conteúdo lido corretamente */
	@Column(name="conteudo_lido")
	private String conteudoLido;
	
	/** Identificador do servidor cujo currículo foi lido */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_servidor")
	private Servidor servidor;
	
	/** Identificador da pessoa cujo currículo foi lido */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_pessoa")
	private Pessoa pessoa;
	
	/** Data da leitura do XML */
	private Date data;
	
	/** Registro de entrada do usuário que efetuou a leitura */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	private RegistroEntrada registro;

	public String getConteudoXml() {
		return conteudoXml;
	}

	public void setConteudoXml(String conteudoXml) {
		this.conteudoXml = conteudoXml;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
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

	public String getConteudoImportado() {
		return conteudoImportado;
	}

	public void setConteudoImportado(String conteudoImportado) {
		this.conteudoImportado = conteudoImportado;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public String getConteudoLido() {
		return conteudoLido;
	}

	public void setConteudoLido(String conteudoLido) {
		this.conteudoLido = conteudoLido;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	
	
}
