/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade usada pra enviar avisos aos participantes dos projeto de pesquisa, monitoria ou extensão
 * @author ilueny santos
 */
@Entity
@Table(name = "aviso_projeto", schema = "projetos")
public class AvisoProjeto extends AbstractMovimento implements Validatable{


	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_aviso_projeto")           
	private int id;	 

	/** conteúdo do aviso */
	private String descricao;
	
	/** título do aviso */
	private String titulo;

	/** data de cadastro */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** data de expiração */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_validade")
	private Date dataValidade;
	
	/** diz se o aviso ainda deve estar publicado */
	@Column(name = "publicar")
	private boolean publicar;
	
	/** projeto ao qual o aviso está relacionado */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto")
	private Projeto projeto = new Projeto();
	
	/** usuario que cadastrou o aviso */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	
	public AvisoProjeto() {
	}

	public AvisoProjeto(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public boolean isPublicar() {
		return publicar;
	}

	public void setPublicar(boolean publicar) {
		this.publicar = publicar;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(dataCadastro, "Data Cadastro", lista);
		ValidatorUtil.validateRequired(dataValidade, "Data de Validade", lista);
		ValidatorUtil.validateRequired(projeto, "Projeto de Ensino", lista);
		ValidatorUtil.validateRequired(titulo, "Título do Aviso", lista);		
		return lista;
	}

}
