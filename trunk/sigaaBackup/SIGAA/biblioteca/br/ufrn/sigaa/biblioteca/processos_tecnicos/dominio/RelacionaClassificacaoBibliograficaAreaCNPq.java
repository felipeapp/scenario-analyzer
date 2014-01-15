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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 *
 * <p>Realiza o mapeamento entre as classifica��s bibliograficas da biblioteca e as �reas de conhecimento CNPq. </p>
 *
 * <p> <i> Retira a depend�ncia da entidade AreaConhecimentoCNPq com a biblioteca</i> </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(name = "relaciona_classificacao_bibliografica_area_cnpq", schema = "biblioteca")
public class RelacionaClassificacaoBibliograficaAreaCNPq implements PersistDB{

	/** O id */
	@Id
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column (name="id_relaciona_classificacao_bibliografica_area_cnpq")
	private int id;
	
	/** A classifica��o utilizada no sistema(CDU, CDD, XXXX, ...) para a patir das suas classes se deseja saber a ar�as correspondentes do CNPq */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_classificacao_bibliografica", referencedColumnName="id_classificacao_bibliografica")
	private ClassificacaoBibliografica classificacao;
	
	/**A �rea CNPq para a respectiva classifica��o que se deseja fazer o relacinamento.  IMPORTANTE: Apenas as grandes �reas de conhecimento do CNPQ s�o utilizadas na biblioteca. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_area_conhecimento_cnpq", referencedColumnName="id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq area;

	/** <p> Define as classes da respecitivas classifica��o bibliogr�fica que s�o englobadas pela respectiva �rea de conhecimento CNPq. </p> 
	 *  <p> Se a classe da classifica��o dessa entidade estiver no intervalo dessa vari�vel essa classe pertence a repectiva �rea. </p>*/
	@Column(name="classes_inclusao")
	private String classesInclusao;

	
	/** <p> Classes que n�o s�o englobadas por esta �rea. Utilizado para conseguir mapear intervalos complexos de classes. </p>
	 *  <p> Caso a classe de classifica��o dessa entidade esteja no intervalo dessa vari�vel, mesmo estando na vari�vel acima, a classe n�o pertence a
	 *  �rea dessa entidade. </p>*/
	@Column(name="classes_exclusao")
	private String classesExclusao;

	
	
	
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
	
	
	
	
	
	/** Construtor para os frameworks que requerem o construtor padr�o. */
	public RelacionaClassificacaoBibliograficaAreaCNPq() {
	}
	
	/** Construtor com o identificador do objeto persistido */
	public RelacionaClassificacaoBibliograficaAreaCNPq(int id) {
		this.id = id;
	}

	/** Construtor com as informa��es necess�rioas para identificar o objeto */
	public RelacionaClassificacaoBibliograficaAreaCNPq(ClassificacaoBibliografica classificacao, AreaConhecimentoCnpq area) {
		this.classificacao = classificacao;
		this.area = area;
	}


	/**
	 * O relacionamento � igual se ele � de uma mesma classifica��o para uma mesma �rea.<br/>
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result
				+ ((classificacao == null) ? 0 : classificacao.hashCode());
		return result;
	}

	
	/**
	 * O relacionamento � igual se ele � de uma mesma classifica��o para uma mesma �rea.<br/>
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelacionaClassificacaoBibliograficaAreaCNPq other = (RelacionaClassificacaoBibliograficaAreaCNPq) obj;
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area))
			return false;
		if (classificacao == null) {
			if (other.classificacao != null)
				return false;
		} else if (!classificacao.equals(other.classificacao))
			return false;
		return true;
	}
	
	
	/////   sets  e   gets /////
	

	public ClassificacaoBibliografica getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(ClassificacaoBibliografica classificacao) {
		this.classificacao = classificacao;
	}

	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public String getClassesInclusao() {
		return classesInclusao;
	}

	public void setClassesInclusao(String classesInclusao) {
		this.classesInclusao = classesInclusao;
	}

	public String getClassesExclusao() {
		return classesExclusao;
	}

	public void setClassesExclusao(String classesExclusao) {
		this.classesExclusao = classesExclusao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public RegistroEntrada getRegistroCriacao() {return registroCriacao;}
	public void setRegistroCriacao(RegistroEntrada registroCriacao) {this.registroCriacao = registroCriacao;}
	public Date getDataCriacao() {return dataCriacao;}
	public void setDataCriacao(Date dataCriacao) {this.dataCriacao = dataCriacao;}
	public RegistroEntrada getRegistroUltimaAtualizacao() {return registroUltimaAtualizacao;}
	public void setRegistroUltimaAtualizacao(RegistroEntrada registroUltimaAtualizacao) {this.registroUltimaAtualizacao = registroUltimaAtualizacao;}
	public Date getDataUltimaAtualizacao() {return dataUltimaAtualizacao;}
	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {this.dataUltimaAtualizacao = dataUltimaAtualizacao;}	
	
}
