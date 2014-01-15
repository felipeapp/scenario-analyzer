/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Criado em 29/01/2007 15:36:52
 * Autor: Edson Anibal de Mac�do Reis Batista (ambar@info.ufrn.br)
 */
package br.ufrn.sigaa.ava.dominio;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;


/**
 * Representa as respostas da enquete de uma turma.
 * 
 * @author Edson Anibal de Macedo Reis Batista (ambar@info.ufrn.br)
 * @author David Pereira 
 */
@Entity
@Table(name="resposta_enquete", schema="ava")
public class EnqueteResposta implements PersistDB {

	/** Chave Prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_resposta", nullable = false)
	private int id;

	/** Descri��o de uma das op��es da enquete. */
	private String resposta;

	/** Enquete � qual a resposta se refere. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_enquete")
	private Enquete enquete;
	
	/** Lista de votos. */
	@OneToMany(mappedBy="enqueteResposta", cascade=CascadeType.ALL)
	private List<EnqueteVotos> votos;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public Enquete getEnquete() {
		return enquete;
	}

	public void setEnquete(Enquete enquete) {
		this.enquete = enquete;
	}

	public List<EnqueteVotos> getVotos() {
		return votos;
	}

	public void setVotos(List<EnqueteVotos> votos) {
		this.votos = votos;
	}
	
	/**
	 *  Retorna o total de votos da enquete.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/Enquete/mostrar.jsp</li>
	 * </ul>
	 * @return
	 */ 
	public int getTotalVotos() {
		if (!isEmpty(votos))
			return votos.size();
		return 0;
	}

	/**
	 * Verifica se o usu�rio votou.
	 * N�o invocado por JSPs.
	 * @param usuario
	 * @return
	 */
	public boolean usuarioVotou(Usuario usuario) {
		if (!isEmpty(votos)) {
			for (EnqueteVotos voto : votos)
				if (voto.getUsuario().equals(usuario))
					return true;
		}
		return false;
	}
	
	/**
	 *  Retorna a porcentagem de votos da enquete.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/Enquete/mostrar.jsp</li>
	 * </ul>
	 * @return
	 */ 
	public BigDecimal getPorcentagemVotos() {
		int total = enquete.getTotalVotos();
		if (total == 0) { 
			total = 1;
		}
		if (!isEmpty(votos)) {
			BigDecimal votosSize = new BigDecimal(votos.size());
			BigDecimal totalBigD = new BigDecimal(total);
		
			BigDecimal resultDiv = votosSize.setScale(2).divide(totalBigD,BigDecimal.ROUND_HALF_EVEN);
			
			return resultDiv.multiply(new BigDecimal(100)).setScale(2);
		} else {
			return new BigDecimal(0.0);
		}
	}
	
}
