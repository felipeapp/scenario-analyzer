package br.ufrn.sigaa.complexohospitalar.dominio;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.ProgramaResidenciaMedica;

/**
 * CoordenacaoPrograma pode cadastrar um servidor a um programa, 
 * em um determinado período de tempo.
 * 
 * @author Jean Guerethes
 */
@Entity
@Table(name = "coordenacao_residencia", schema="complexo_hospitalar")
public class CoordenacaoProgramaResidenciaMedica implements PersistDB, Validatable{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_coordenacao_residencia_medica", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_programa_residencia_medica", unique = false, nullable = false, insertable = true, updatable = true)
	private ProgramaResidenciaMedica programaResidenciaMedica;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor", unique = false, nullable = false, insertable = true, updatable = true)
	private Servidor servidor;

	@Temporal(TemporalType.TIMESTAMP)
	private Date inicio;

	@Temporal(TemporalType.TIMESTAMP)
	private Date fim;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ProgramaResidenciaMedica getProgramaResidenciaMedica() {
		return programaResidenciaMedica;
	}

	public void setProgramaResidenciaMedica(
			ProgramaResidenciaMedica programaResidenciaMedica) {
		this.programaResidenciaMedica = programaResidenciaMedica;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	
	
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(getServidor(), "Servidor", lista);
		ValidatorUtil.validateRequiredId(getProgramaResidenciaMedica().getId(), "Programa", lista);
		ValidatorUtil.validateRequired(getInicio(), "Data Inicial", lista);
		ValidatorUtil.validateRequired(getFim(), "Data Final", lista);

		return lista;
	}
}
