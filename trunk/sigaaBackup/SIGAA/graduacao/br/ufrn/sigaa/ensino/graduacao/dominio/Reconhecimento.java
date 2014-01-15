/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 * Created on 10 de Janeiro de 2007
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entity class Reconhecimento
 *
 * @author Gleydson
 */
@Entity
@Table(name = "reconhecimento", schema = "graduacao")
public class Reconhecimento implements Validatable {

	
	@Id 
	@GeneratedValue(generator="seqGenerator") 
	@GenericGenerator(	name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
						parameters={ @Parameter(name="sequence_name", value="graduacao.reconhecimento_seq") })  
	@Column(name = "id_reconhecimento", nullable = false)
	private int id;

	@Column(name = "data_decreto")
	@Temporal(TemporalType.DATE)
	private Date dataDecreto;

	@Column(name = "data_publicacao")
	@Temporal(TemporalType.DATE)
	private Date dataPublicacao;

	@Column(name = "portaria_decreto")
	private String portariaDecreto;

	@Column(name = "validade")
	@Temporal(TemporalType.DATE)
	private Date validade;

	@ManyToOne
	@JoinColumn(name = "id_matriz")
	private MatrizCurricular matriz;

	/** Creates a new instance of Reconhecimento */
	public Reconhecimento() {
	}

	public Date getDataDecreto() {
		return dataDecreto;
	}

	public void setDataDecreto(Date dataDecreto) {
		this.dataDecreto = dataDecreto;
	}

	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MatrizCurricular getMatriz() {
		return matriz;
	}

	public void setMatriz(MatrizCurricular matriz) {
		this.matriz = matriz;
	}

	public String getPortariaDecreto() {
		return portariaDecreto;
	}

	public void setPortariaDecreto(String portariaDecreto) {
		this.portariaDecreto = portariaDecreto;
	}

	public Date getValidade() {
		return validade;
	}

	public void setValidade(Date validade) {
		this.validade = validade;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(matriz, "Matriz Curricular", erros);
		ValidatorUtil.validateRequired(dataDecreto, "Decreto", erros);
		ValidatorUtil.validateRequired(dataPublicacao, "Publicação", erros);
		ValidatorUtil.validateRequired(portariaDecreto, "Portaria de Decreto", erros);

		return erros;
	}

}
