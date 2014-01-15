/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 13/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 *
 * <p>Classe que relaciona uma biblioteca no sistema com as classifica��es bibliogr�ficas que ela trabalha. </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(name = "relaciona_classificacao_bibliografica_biblioteca", schema = "biblioteca")
public class RelacionaClassificacaoBibliograficaBiblioteca implements Validatable{
	
	
	/** O id */
	@Id
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column (name="id_classificacao_bibliografica_biblioteca")
	private int id;
	
	/** A biblioteca que utiliza a classifica��o */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_biblioteca", referencedColumnName="id_biblioteca")
	private Biblioteca biblioteca;
	
	/** A classificacao utilizada na biblioteca */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_classificacao_bibliografica", referencedColumnName="id_classificacao_bibliografica")
	private ClassificacaoBibliografica classificacao;

	////////////////////////////INFORMA��ES DE AUDITORIA  ///////////////////////////////////////


	/**
	 * informa��es de quem criou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;

	/**
	 * data de cadastro
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_criacao")
	private Date dataCriacao;

	/**
	 * registro entrada do usu�rio que realizou a �ltima atualiza��o
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * data da �ltima atualiza��o
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	@AtualizadoEm
	private Date dataUltimaAtualizacao;


	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	/** Construtor padr�o */
	public RelacionaClassificacaoBibliograficaBiblioteca() {
		
	}
	
	/** Construtor que cria uma relacionamento vazio para uma determinada biblioteca */
	public RelacionaClassificacaoBibliograficaBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}
	
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		
		if(this.biblioteca == null || this.biblioteca.getId() <= 0){
			lista.addErro("� preciso informar a biblioteca que utilizar� a classifica��o"); // N�o deve ocorrer, pois n�o � preenchido pelo usu�rio
		}else{
			if(this.classificacao == null || this.classificacao.getId() <= 0){
				lista.addErro("Informe a classifica��o utilizada pela biblioteca: "+biblioteca.getDescricao());
			}
		}
		
		return lista;
	}
	
	
	/** 
	 * Dois relacionamento s�o iguais se foram da mesma biblioteca, n�o pode existir dois relacionamento 
	 * iguais para a mesma biblioteca no banco. Pois uma biblioteca s� trabalha com uma classifica��o.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((biblioteca == null) ? 0 : biblioteca.hashCode());
		return result;
	}

	/** 
	 * Dois relacionamento s�o iguais se foram da mesma biblioteca, n�o pode existir dois relacionamento 
	 * iguais para a mesma biblioteca no banco. Pois uma biblioteca s� trabalha com uma classifica��o.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelacionaClassificacaoBibliograficaBiblioteca other = (RelacionaClassificacaoBibliograficaBiblioteca) obj;
		if (biblioteca == null) {
			if (other.biblioteca != null)
				return false;
		} else if (!biblioteca.equals(other.biblioteca))
			return false;
		return true;
	}
	
	
	
	/// sets e gets ///
	
	public Biblioteca getBiblioteca() {return biblioteca; }

	public void setBiblioteca(Biblioteca biblioteca) { this.biblioteca = biblioteca;}

	public ClassificacaoBibliografica getClassificacao() { return classificacao; }

	public void setClassificacao(ClassificacaoBibliografica classificacao) { this.classificacao = classificacao;}

	public int getId() {return id;}

	public void setId(int id) {this.id = id;}

	public RegistroEntrada getRegistroCriacao() {return registroCriacao;}
	public void setRegistroCriacao(RegistroEntrada registroCriacao) {this.registroCriacao = registroCriacao;}
	public Date getDataCriacao() {return dataCriacao;}
	public void setDataCriacao(Date dataCriacao) {this.dataCriacao = dataCriacao;}
	public RegistroEntrada getRegistroUltimaAtualizacao() {return registroUltimaAtualizacao;}
	public void setRegistroUltimaAtualizacao(RegistroEntrada registroUltimaAtualizacao) {this.registroUltimaAtualizacao = registroUltimaAtualizacao;}
	public Date getDataUltimaAtualizacao() {return dataUltimaAtualizacao;}
	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {this.dataUltimaAtualizacao = dataUltimaAtualizacao;}	

	
	
}
