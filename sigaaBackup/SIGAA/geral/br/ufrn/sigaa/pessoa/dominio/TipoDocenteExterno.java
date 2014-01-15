/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Criado em: 04/05/2007
 */
package br.ufrn.sigaa.pessoa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 *
 * Tipo de docente externo
 * Identifica o docente externo se ele é: Professor Visitante, Professor Externo, Docente Externo Lato Sensu...
 *
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "tipo_docente_externo", schema = "ensino")
public class TipoDocenteExterno implements PersistDB {

	public static final int COLABORADOR_VOLUNTARIO = 1;
	public static final int PROFESSOR_CONVENIO_COLABORACAO_TECNICA = 6;
	public static final int DOCENTE_EXTERNO_LATO_SENSU = 8;
	public static final int PROFESSOR_EXTERNO = 7;

	
	@Id
	@GeneratedValue(generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
    				  parameters = {@Parameter(name = "hibernate_sequence", value = "ensino.hibernate_sequence")})	
	@Column(name = "id_tipo_docente_externo", nullable = false)
	private int id;

	private String denominacao;

	/** contador do sequencial para a identificação do tipo de docente externo */
	private int sequencia;

	/** indica se o tipo de docente externo permite que a pessoa também seja servidor docente */
	@Column(name = "permite_servidor", nullable = false)
	private boolean permiteServidor;
	
	public TipoDocenteExterno() {
	}
	
	public TipoDocenteExterno(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public int getSequencia() {
		return sequencia;
	}

	public void setSequencia(int sequencia) {
		this.sequencia = sequencia;
	}

	public boolean isColaboradorVoluntario() {
		return COLABORADOR_VOLUNTARIO == id || PROFESSOR_CONVENIO_COLABORACAO_TECNICA == id;
	}

	public boolean isPermiteServidor() {
		return permiteServidor;
	}

	public void setPermiteServidor(boolean permiteServidor) {
		this.permiteServidor = permiteServidor;
	}

}
