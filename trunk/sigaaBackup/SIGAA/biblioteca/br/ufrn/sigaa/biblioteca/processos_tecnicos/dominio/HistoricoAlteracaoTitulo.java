/*
 * HistoricoAlteracaoTitulo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 *
 *   Guarda o histórico de alteração de títulos.
 *
 * @author jadson
 * @since 26/08/2009
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "historico_alteracao_titulo", schema = "biblioteca")
public class HistoricoAlteracaoTitulo implements PersistDB{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.historicos_alteracao_sequence") })
	@Column(name="id_historico_alteracao_titulo")
	private int id;
	
	/**
	 * O título que foi alterado.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_titulo")
	private TituloCatalografico titulo;
	
	/**
	 * Indica quem alterou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/**
	 * Normalmente contém todas as informações do título no momento da alteração, separadas por "\n"
	 */
	@Column(name="descricao_operacao")
	private String descricaoOperacao;
	
	/**
	 * data da alteração no título.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_operacao")
	private Date dataOperacao;
	
	
	
	/*  Para históricos que foram migrados. */
	private String codmerg;


	
	
	// sets e gets

	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public TituloCatalografico getTitulo() {
		return titulo;
	}



	public void setTitulo(TituloCatalografico titulo) {
		this.titulo = titulo;
	}



	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}



	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}



	public String getDescricaoOperacao() {
		return descricaoOperacao;
	}



	public void setDescricaoOperacao(String descricaoOperacao) {
		this.descricaoOperacao = descricaoOperacao;
	}



	public Date getDataOperacao() {
		return dataOperacao;
	}



	public void setDataOperacao(Date dataOperacao) {
		this.dataOperacao = dataOperacao;
	}



	public String getCodmerg() {
		return codmerg;
	}



	public void setCodmerg(String codmerg) {
		this.codmerg = codmerg;
	}
	
	
}
