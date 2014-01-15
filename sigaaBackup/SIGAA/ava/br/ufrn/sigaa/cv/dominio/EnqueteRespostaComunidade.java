/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Criado em 29/01/2007 15:36:52
 * Autor: Edson Anibal de Macêdo Reis Batista (ambar@info.ufrn.br)
 */
package br.ufrn.sigaa.cv.dominio;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;


/**
 * Representa as respostas da enquete de uma turma.
 * 
 * @author Edson Anibal de Macedo Reis Batista (ambar@info.ufrn.br)
 * @author David Pereira 
 */
@Entity
@Table(name="resposta_enquete", schema="cv")
public class EnqueteRespostaComunidade implements PersistDB {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id_resposta", nullable=false)
	private int id;

	private String resposta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_enquete")
	private EnqueteComunidade enquete;
	
	@OneToMany(mappedBy="enqueteResposta", cascade=CascadeType.ALL)
	private List<EnqueteVotosComunidade> votos;

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

	public EnqueteComunidade getEnquete() {
		return enquete;
	}

	public void setEnquete(EnqueteComunidade enquete) {
		this.enquete = enquete;
	}

	public List<EnqueteVotosComunidade> getVotos() {
		return votos;
	}

	public void setVotos(List<EnqueteVotosComunidade> votos) {
		this.votos = votos;
	}

	/**
	 * Retorna o total de votos
	 * @return
	 */
	public int getTotalVotos() {
		if (!isEmpty(votos))
			return votos.size();
		return 0;
	}

	/**
	 * Verifica se um dado usuário já votou
	 * 
	 * @param usuario
	 * @return
	 */
	public boolean usuarioVotou(Usuario usuario) {
		if (!isEmpty(votos)) {
			for (EnqueteVotosComunidade voto : votos)
				if (voto.getUsuario().equals(usuario))
					return true;
		}
		return false;
	}
	
	/**
	 * Calcula a porcentagem baseado no número de votos
	 * 
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
		
			BigDecimal resultDiv = votosSize.setScale(2).divide(totalBigD,BigDecimal.ROUND_UP);
			
			return resultDiv.multiply(new BigDecimal(100)).setScale(2);
		} else {
			return new BigDecimal(0.0);
		}
	}
	
}
