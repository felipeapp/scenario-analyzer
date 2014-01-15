/*
 * TipoMaterial.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;

/**
 *
 *      Classe responsável pelos tipos de materiais da biblioteca: Livro, monografia, Vinil, Partituras , Pergaminho, etc
 *
 * @author jadson
 * @since 18/12/2008
 * @version 1.0 criacao da classe
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "tipo_material", schema = "biblioteca")
public class TipoMaterial implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_tipo_material")
	private int id;

	/**guarda a descrição do tipo de material: Livro, monografia, Vinil, Partituras , Pergaminho, etc */
	@Column(name="descricao" , nullable=false)
	private String descricao; 


	private boolean ativo = true;
	
	
	///////////////////// Auditoria //////////////////////////////////
	
	/**
	 * registro de entrada do usuário que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * data de cadastro
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/**
	 * registro entrada do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * data da última atualizacão
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	
	///////////////////////////////////////////////////////////
	
	
	
	/**
	 * Guarda se esse objeto foi selecionado (usados nas páginas JSP)
	 */
	@Transient
	protected boolean selecionado;
	
	
	
	/**
	 * Construtor usado pelo hibernate
	 */
	public TipoMaterial(){
	}
	
	public TipoMaterial( int id ){
		this.id = id;
	}

	/**
	 * Construtor para ser usado no cadastro de novos tipos de materiais
	 * 
	 * @param descricao
	 * @param limitadorPrazoHoras
	 */
	public TipoMaterial(String descricao) {
		this.descricao = descricao;
	}

	public TipoMaterial( int id, String descricao ){
		this(id);
		this.descricao = descricao;
	}


	/**
	 * Validação para o cadastro de Tipos de Materiais.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();

		if(StringUtils.isEmpty(descricao)){
			mensagens.addErro("A descrição do tipo de material não pode ser vazia");
		}else{

			if(descricao.length()>60){
				mensagens.addErro("O tamanho máximo da descrição é 60 caracteres");
			}
		}

		return mensagens;
	}
	
	@Override
	public boolean equals (Object obj){
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode (){
		return HashCodeUtil.hashAll(new Integer(id));
	}

	
	@Override
	public String toString() {
		return "Tipo do Material: "+descricao+"("+id+") ";
	}
	

	// sets e gets

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id  = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
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

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	

}
