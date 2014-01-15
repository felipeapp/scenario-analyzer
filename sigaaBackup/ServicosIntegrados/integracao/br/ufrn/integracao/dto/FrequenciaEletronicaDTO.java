/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 27/08/2009
 *
 */	
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Cria objeto sem comportamento para ser transferido entre as camadas (tiers) remotas.
 * Pattern Data Transfer Object.
 * Objeto utilizado pelo cliente desktop de Chamada Biom�trica.
 *
 * @author agostinho campos
 *
 */
public class FrequenciaEletronicaDTO implements Serializable {

	private int idFrequenciaEletronica;
    private int horarios;
    
    /**
     * Refer�ncia a turma para qual a chamada ir� ser realizada. Pode ser uma turma convencional ou uma subturma.
     */
    private int idTurma;
    
    private String nomeTurma;
    private String codigoTurma;
    private String codigoComponente;
    
    private Set<DiscenteDTO> discentesTurma = new LinkedHashSet<DiscenteDTO>();
    
    /**
     * Indica se o login foi autorizado com os dados pelo usu�rio. 
     */
    private boolean loginAutorizado;
    
    /**
     * Caso a turma possua sub-turmas, o usu�rio do desktop deve selecionar uma das sub-turmas
     * para abrir o sistema de chamada.
     */
    private List<FrequenciaEletronicaDTO> subTurmas = new ArrayList<FrequenciaEletronicaDTO>();

	/**
	 * Limite de tempo m�ximo que permite mesmo os alunos chegando atrasados, 
	 * receberem presen�a completa  da aula correspondente, quando registram a presen�a
	 * por biometria.
	 */
	private int minToleranciaAula;
	/**
	 * Hora definida no servidor, serve como hor�rio de referencia para o aplicativo 
	 * de chamada por biometria.
	 */
    private Date horaServidor;
    
	public int getMinToleranciaAula() {
		return minToleranciaAula;
	}
	public void setMinToleranciaAula(int minToleranciaAula) {
		this.minToleranciaAula = minToleranciaAula;
	}
	
	public String getCodigoTurma() {
		return codigoTurma;
	}
	public void setCodigoTurma(String codigoTurma) {
		this.codigoTurma = codigoTurma;
	}
	public int getIdTurma() {
		return idTurma;
	}
	public void setIdTurma(int idTurma) {
		this.idTurma = idTurma;
	}
	public String getNomeTurma() {
		return nomeTurma;
	}
	public void setNomeTurma(String nomeTurma) {
		this.nomeTurma = nomeTurma;
	}
	public String getCodigoComponente() {
		return codigoComponente;
	}
	public void setCodigoComponente(String codigoComponente) {
		this.codigoComponente = codigoComponente;
	}
	public Set<DiscenteDTO> getDiscentesTurma() {
		return discentesTurma;
	}
	public void setDiscentesTurma(Set<DiscenteDTO> discentesTurma) {
		this.discentesTurma = discentesTurma;
	}

    public int getHorarios() {
        return horarios;
    }

    public void setHorarios(int horarios) {
        this.horarios = horarios;
    }
    
	public int getIdFrequenciaEletronica() {
		return idFrequenciaEletronica;
	}
	public void setIdFrequenciaEletronica(int idFrequenciaEletronica) {
		this.idFrequenciaEletronica = idFrequenciaEletronica;
	}
	public Date getHoraServidor() {
		return horaServidor;
	}
	public void setHoraServidor(Date horaServidor) {
		this.horaServidor = horaServidor;
	}
	public List<FrequenciaEletronicaDTO> getSubTurmas() {
		return subTurmas;
	}
	public void setSubTurmas(List<FrequenciaEletronicaDTO> subTurmas) {
		this.subTurmas = subTurmas;
	}
	public boolean isLoginAutorizado() {
		return loginAutorizado;
	}
	public void setLoginAutorizado(boolean loginAutorizado) {
		this.loginAutorizado = loginAutorizado;
	}
	
}
