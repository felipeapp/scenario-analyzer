package br.ufrn.sigaa.ensino.latosensu.relatorios;

import java.util.Collection;
import java.util.HashMap;

/**
 * Classe auxiliar para gerar a visualização dos componentes do curso lato sensu
 * 
 * @author guerethes
 */
public class LinhaComponenteCursoLato {

	/** Armazena o nome do Curso Lato Sensu */
	private String nomeCurso;
	
	/** Armazena o nome dos Docentes do Curso Lato Sensu */
	private HashMap<String, Integer> nomeDocente = new HashMap<String, Integer>();
	
	/** Armazena a chave primária da equipe Lato Sensu do Curso Lato Sensu */
	private Integer idEquipeLato;
	
	/** Armazena a chave primária do servidor do Curso Lato Sensu */
	private Integer idServidor;
	
	/** Armazena a chave primária do docente Externo do Curso Lato Sensu */
	private Integer idDocenteExterno;
	
	/** Armazena a chave primária da disciplina do Curso Lato Sensu */
	private Integer idDisciplina;
	
	/** Armazena a carga horária do Curso Lato Sensu */
	private Integer cargaHoraria;
	
	/** Armazena a chave primária da proposta do Curso Lato Sensu */
	private Integer idProposta;
	
	/** Armazena o código do Curso Lato Sensu */
	private String codigo;
	
	/** Armazena a bibliografia do Curso Lato Sensu */
	private String bibliografia;
	
	/** Armazena a ementa do Curso Lato Sensu */
	private String ementa;
	
	/** Armazena a carga horária total do Curso Lato Sensu */
	private Integer cargaHorariaTotal;
	
	/** Informa de o docente é interno ou externo */
	private boolean docenteInterno;
	
	public LinhaComponenteCursoLato() {}

	public String getNomeCurso() {
		return nomeCurso;
	}

	public void setNomeCurso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}

	public HashMap<String, Integer> getNomeDocente() {
		return nomeDocente;
	}

	public void setNomeDocente(HashMap<String, Integer> nomeDocente) {
		this.nomeDocente = nomeDocente;
	}

	public Integer getIdEquipeLato() {
		return idEquipeLato;
	}

	public void setIdEquipeLato(Integer idEquipeLato) {
		this.idEquipeLato = idEquipeLato;
	}

	public Integer getIdServidor() {
		return idServidor;
	}

	public void setIdServidor(Integer idServidor) {
		this.idServidor = idServidor;
	}

	public Integer getIdDocenteExterno() {
		return idDocenteExterno;
	}

	public void setIdDocenteExterno(Integer idDocenteExterno) {
		this.idDocenteExterno = idDocenteExterno;
	}

	public Integer getIdDisciplina() {
		return idDisciplina;
	}

	public void setIdDisciplina(Integer idDisciplina) {
		this.idDisciplina = idDisciplina;
	}

	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public Integer getIdProposta() {
		return idProposta;
	}

	public void setIdProposta(Integer idProposta) {
		this.idProposta = idProposta;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getBibliografia() {
		return bibliografia;
	}

	public void setBibliografia(String bibliografia) {
		this.bibliografia = bibliografia;
	}

	public String getEmenta() {
		return ementa;
	}

	public void setEmenta(String ementa) {
		this.ementa = ementa;
	}

	public Integer getCargaHorariaTotal() {
		return cargaHorariaTotal;
	}

	public void setCargaHorariaTotal(Integer cargaHorariaTotal) {
		this.cargaHorariaTotal = cargaHorariaTotal;
	}

	public boolean isDocenteInterno() {
		return docenteInterno;
	}

	public void setDocenteInterno(boolean docenteInterno) {
		this.docenteInterno = docenteInterno;
	}
	
	/**
	 * Compara se o id está paresente na Coleção passada.
	 */
	public static boolean compareTo(Collection<LinhaComponenteCursoLato> linha, int id){
		for (LinhaComponenteCursoLato linhaComponenteCursoLato : linha) {
			if (linhaComponenteCursoLato.getIdDisciplina() == id )
				return true;
		}
		return false;
	}
	
}