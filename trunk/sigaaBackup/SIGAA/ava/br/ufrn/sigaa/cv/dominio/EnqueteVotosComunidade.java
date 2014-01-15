/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Usuario;


/**
 * Representa os votos para as respostas da enquete de uma turma.
 * 
 * @author Edson Anibal de Macedo Reis Batista (ambar@info.ufrn.br)
 * @author David Pereira
 */
@Entity @Table(name="enquete_votos", schema="cv")
public class EnqueteVotosComunidade implements PersistDB {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
		
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_resposta")
	private EnqueteRespostaComunidade enqueteResposta;

	@CriadoPor
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_usuario")
	private Usuario usuario;
	
	@CriadoEm
	@Column(name="data_voto")
	private Date dataVoto;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EnqueteRespostaComunidade getEnqueteResposta() {
		return enqueteResposta;
	}

	public void setEnqueteResposta(EnqueteRespostaComunidade enqueteResposta) {
		this.enqueteResposta = enqueteResposta;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getDataVoto() {
		return dataVoto;
	}

	public void setDataVoto(Date dataVoto) {
		this.dataVoto = dataVoto;
	}
	
}