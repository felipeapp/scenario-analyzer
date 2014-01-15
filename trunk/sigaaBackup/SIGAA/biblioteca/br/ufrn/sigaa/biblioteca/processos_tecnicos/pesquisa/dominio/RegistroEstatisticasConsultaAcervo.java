package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio;

import java.util.Date;
import java.util.List;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;



/**
 * Guarda temporariamente os dados para serem passados ao consumidor e registrar as estat�sticas da biblioteca.
 * 
 * @author jadson
 *
 */
public class RegistroEstatisticasConsultaAcervo{

	/** Opera��es de registros que podem ser feita */
	public enum TipoOperacaoRegistroConsultas{ 
		/** Registra os T�tulos mais consultados no acervo. */
		REGISTRAR_CONSULTA, 
		/** Registra os T�tulos mais visualizados pelos usu�rios. */
		REGISTRAR_VISUALIZACAO, 
		/** Registra os T�tulos mais emprestados pelos usu�rios. */
		REGISTRAR_EMPRESTIMO}

	/** Os ids dos materiais emprestados */
	private List<Integer> idsMateriaisEmprestados;
	
	/** Os t�tulos consultados */
	private List<CacheEntidadesMarc> titulosConsultados;
	
	/** O id do T�tulo visualizado  */
	private int idTituloVisualizado;
	
	/** A data em que a opera��o ocorreu */
	private Date dataRegistro;
	
	/** O tipo de opera��o, se visualizar, consultar ou emprestar */
	private TipoOperacaoRegistroConsultas operacaoRegistro;
	

	public RegistroEstatisticasConsultaAcervo() {
		
	}

	
	/** Cria um novo registro a ser salvo para os T�tulos consultados  */
	public RegistroEstatisticasConsultaAcervo(List<CacheEntidadesMarc> titulosConsultados, Date dataRegistro) {
		this.titulosConsultados = titulosConsultados;
		this.operacaoRegistro = TipoOperacaoRegistroConsultas.REGISTRAR_CONSULTA;
		this.dataRegistro = dataRegistro;
	}
	
	/** Cria um novo registro a ser salvo para os T�tulos emprestados  */
	public RegistroEstatisticasConsultaAcervo(Date dataRegistro, List<Integer> idsMateriaisEmprestados) {
		this.idsMateriaisEmprestados = idsMateriaisEmprestados;
		this.operacaoRegistro = TipoOperacaoRegistroConsultas.REGISTRAR_EMPRESTIMO;
		this.dataRegistro = dataRegistro;
	}

	/** Cria um novo registro a ser salvo para os T�tulos visualizados  */
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
