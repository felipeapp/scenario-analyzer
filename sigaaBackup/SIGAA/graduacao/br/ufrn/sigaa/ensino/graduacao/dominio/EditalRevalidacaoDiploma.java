/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/01/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;

/**
 * Classe de domínio que representa o edital de revalidação de diploma.
 *
 * @author Mario Rizzi
 */
@Entity
@Table(name = "edital_revalidacao_diploma", schema = "graduacao")
public class EditalRevalidacaoDiploma implements Validatable {
		
	
	private static final String URL_DOCUMENTO_EDITAL = "/sigaa/public/revalidacao_diplomas/pdf/edital_para_revalidacao_de_diplomas_2010.pdf";
	
	private static final String PERIODO_REAGENDAMENTO = "05 a 09 de julho de 2010";
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_edital_revalidacao_diploma", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Atribui um texto que descreva qual é o processo de revalidação de diploma*/
	@Column(name = "titulo", nullable = true)
	private String titulo;
	
	/** Atribui a data inicial das inscrições no processo de revalidação de diploma*/
	@Column(name = "inicio_inscricao", unique = false, nullable = true, insertable = true, updatable = true)
	private Date inicioInscricao;
	
	/** Atribui o horário que o inscrito agendou para entrega dos documentos. */
	@Column(name = "fim_inscricao", unique = false, nullable = true, insertable = true, updatable = true)
	private Date finalInscricao;	
	
	/** Atribui a data inicial para o agendamento no processo de revalidação de diploma*/
	@Column(name = "inicio_agenda", unique = false, nullable = true, insertable = true, updatable = true)
	private Date inicioAgenda;
	
	/** Atribui o horário que o inscrito agendou para entrega dos documentos. */
	@Column(name = "fim_agenda", unique = false, nullable = true, insertable = true, updatable = true)
	private Date finalAgenda;

	/** Atribui a data de cadastro da solicitação. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoEm
	private Date dataCadastro;
	
	public EditalRevalidacaoDiploma() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Date getInicioInscricao() {
		return inicioInscricao;
	}

	public void setInicioInscricao(Date inicioInscricao) {
		this.inicioInscricao = inicioInscricao;
	}

	public Date getFinalInscricao() {
		return finalInscricao;
	}

	public void setFinalInscricao(Date finalInscricao) {
		this.finalInscricao = finalInscricao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getInicioAgenda() {
		return inicioAgenda;
	}

	public void setInicioAgenda(Date inicioAgenda) {
		this.inicioAgenda = inicioAgenda;
	}

	public Date getFinalAgenda() {
		return finalAgenda;
	}

	public void setFinalAgenda(Date finalAgenda) {
		this.finalAgenda = finalAgenda;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
	public String getUrlEdital(){
		return URL_DOCUMENTO_EDITAL;
	}
	
	public String getPeriodoReagendamento(){
		return PERIODO_REAGENDAMENTO;
	}
		
}