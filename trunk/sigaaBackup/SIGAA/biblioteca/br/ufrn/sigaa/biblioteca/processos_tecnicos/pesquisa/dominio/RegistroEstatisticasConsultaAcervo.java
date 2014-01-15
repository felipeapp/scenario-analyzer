package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio;

import java.util.Date;
import java.util.List;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;



/**
 * Guarda temporariamente os dados para serem passados ao consumidor e registrar as estatísticas da biblioteca.
 * 
 * @author jadson
 *
 */
public class RegistroEstatisticasConsultaAcervo{

	/** Operações de registros que podem ser feita */
	public enum TipoOperacaoRegistroConsultas{ 
		/** Registra os Títulos mais consultados no acervo. */
		REGISTRAR_CONSULTA, 
		/** Registra os Títulos mais visualizados pelos usuários. */
		REGISTRAR_VISUALIZACAO, 
		/** Registra os Títulos mais emprestados pelos usuários. */
		REGISTRAR_EMPRESTIMO}

	/** Os ids dos materiais emprestados */
	private List<Integer> idsMateriaisEmprestados;
	
	/** Os títulos consultados */
	private List<CacheEntidadesMarc> titulosConsultados;
	
	/** O id do Título visualizado  */
	private int idTituloVisualizado;
	
	/** A data em que a operação ocorreu */
	private Date dataRegistro;
	
	/** O tipo de operação, se visualizar, consultar ou emprestar */
	private TipoOperacaoRegistroConsultas operacaoRegistro;
	

	public RegistroEstatisticasConsultaAcervo() {
		
	}

	
	/** Cria um novo registro a ser salvo para os Títulos consultados  */
	public RegistroEstatisticasConsultaAcervo(List<CacheEntidadesMarc> titulosConsultados, Date dataRegistro) {
		this.titulosConsultados = titulosConsultados;
		this.operacaoRegistro = TipoOperacaoRegistroConsultas.REGISTRAR_CONSULTA;
		this.dataRegistro = dataRegistro;
	}
	
	/** Cria um novo registro a ser salvo para os Títulos emprestados  */
	public RegistroEstatisticasConsultaAcervo(Date dataRegistro, List<Integer> idsMateriaisEmprestados) {
		this.idsMateriaisEmprestados = idsMateriaisEmprestados;
		this.operacaoRegistro = TipoOperacaoRegistroConsultas.REGISTRAR_EMPRESTIMO;
		this.dataRegistro = dataRegistro;
	}

	/** Cria um novo registro a ser salvo para os Títulos visualizados  */
	public RegistroEstatisticasConsultaAcervo(int idTituloVisualizado, Date dataRegistro) {
		this.idTituloVisualizado = idTituloVisualizado;
		this.operacaoRegistro = TipoOperacaoRegistroConsultas.REGISTRAR_VISUALIZACAO;
		this.dataRegistro = dataRegistro;
	}

	
	
	// sets e gets //
	
	
	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public TipoOperacaoRegistroConsultas getOperacaoRegistro() {
		return operacaoRegistro;
	}

	public void setOperacaoRegistro(TipoOperacaoRegistroConsultas operacaoRegistro) {
		this.operacaoRegistro = operacaoRegistro;
	}


	public List<Integer> getIdsMateriaisEmprestados() {
		return idsMateriaisEmprestados;
	}


	public void setIdsMateriaisEmprestados(List<Integer> idsMateriaisEmprestados) {
		this.idsMateriaisEmprestados = idsMateriaisEmprestados;
	}


	public List<CacheEntidadesMarc> getTitulosConsultados() {
		return titulosConsultados;
	}


	public void setTitulosConsultados(List<CacheEntidadesMarc> titulosConsultados) {
		this.titulosConsultados = titulosConsultados;
	}


	public int getIdTituloVisualizado() {
		return idTituloVisualizado;
	}


	public void setIdTituloVisualizado(int idTituloVisualizado) {
		this.idTituloVisualizado = idTituloVisualizado;
	}


	

	
}
