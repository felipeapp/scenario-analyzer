/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 11/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.sigaa.arq.dao.biblioteca.relatorios.RelatoriosMultasBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca.StatusMulta;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategyFactory;

/**
 *
 * <p>Classe utilizada para organizar os dados que ser�o exibidos no relat�rios de multas  do sistema </p>
 * 
 * @see RelatoriosMultasBibliotecaDao
 * 
 * @author jadson
 *
 */
public class ResultadoRelatorioMultas implements Comparable<ResultadoRelatorioMultas>{


	/** O id do usu�rio multado para agrupar os resultados*/
	Integer idUsuarioBiblioteca;
	
	/** cpf ou passarporte + nome do usu�rio que foi mutado*/
	String infoUsuario; 
	
	/** Informa��es das multas do usu�rios*/
	List<ResultadoInternoRelatorioMulta> infoMultasUsuario = new ArrayList<ResultadoInternoRelatorioMulta>();
	
	
	/** o somat�rio das multas para o usu�rio no per�odo do relat�rio */
	BigDecimal valorTotalUsuario = new BigDecimal(0);
	
	
	public ResultadoRelatorioMultas(Integer idUsuarioBiblioteca) {
		this.idUsuarioBiblioteca = idUsuarioBiblioteca;
	}
	
	
	/**
	 * 
	 * Monta as informa��es das multas pagas
	 *
	 * @param valor
	 * @param usuarioEstorno
	 * @param dataExtorno
	 * @param motivoExtorno
	 */
	public void setInfoMultaQuitadas(BigDecimal valor, String usuarioQuitou, Date dataQuitacao, Integer status, String observacaoPagamento, BigInteger numeroReferencia) {
		
		infoMultasUsuario.add(new ResultadoInternoRelatorioMulta(
				new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiaMulta().getValorFormatado(valor)
				,usuarioQuitou
				, new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dataQuitacao)
				,StatusMulta.getStatusMulta(status).getDescricao()
				,observacaoPagamento
				,numeroReferencia
				));
		
		this.valorTotalUsuario = valorTotalUsuario.add(valor);
	}
	
	
	/**
	 * 
	 * Monta as informa��es das multas ainda n�o pagas
	 *
	 * @param valor
	 * @param usuarioEstorno
	 * @param dataExtorno
	 * @param motivoExtorno
	 */
	public void setInfoMultaNaoQuitadas(BigDecimal valor) {
		infoMultasUsuario.add(new ResultadoInternoRelatorioMulta(
				new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiaMulta().getValorFormatado(valor)
				));
		
		valorTotalUsuario = valorTotalUsuario.add(valor);
	}
	
	
	/**
	 * 
	 * Monta as informa��es das multas estornadas
	 *
	 * @param valor
	 * @param usuarioEstorno
	 * @param dataExtorno
	 * @param motivoExtorno
	 */
	public void setInfoMultaEstornada(BigDecimal valor, String usuarioEstorno, Date dataExtorno, String motivoExtorno ) {
		
		infoMultasUsuario.add(new ResultadoInternoRelatorioMulta(
				new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiaMulta().getValorFormatado(valor)
				,usuarioEstorno
				, new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dataExtorno)
				,motivoExtorno
				));
		
		valorTotalUsuario = valorTotalUsuario.add(valor);
		
	}

	

	public BigDecimal getValorTotalUsuario() {
		return valorTotalUsuario;
	}
	
	public String getValorTotalUsuarioFormatado() {
		return new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiaMulta().getValorFormatado(valorTotalUsuario);
	}


	public Integer getIdUsuarioBiblioteca() {
		return idUsuarioBiblioteca;
	}

	public String getInfoUsuario() {
		return infoUsuario;
	}

	public void setInfoUsuario(String infoUsuario) {
		this.infoUsuario = infoUsuario;
	}

	

	public List<ResultadoInternoRelatorioMulta> getInfoMultasUsuario() {
		return infoMultasUsuario;
	}


	public void setInfoMultasUsuario(List<ResultadoInternoRelatorioMulta> infoMultasUsuario) {
		this.infoMultasUsuario = infoMultasUsuario;
	}


	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ResultadoRelatorioMultas o) {
		return idUsuarioBiblioteca.compareTo(o.idUsuarioBiblioteca);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((idUsuarioBiblioteca == null) ? 0 : idUsuarioBiblioteca
						.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultadoRelatorioMultas other = (ResultadoRelatorioMultas) obj;
		if (idUsuarioBiblioteca == null) {
			if (other.idUsuarioBiblioteca != null)
				return false;
		} else if (!idUsuarioBiblioteca.equals(other.idUsuarioBiblioteca))
			return false;
		return true;
	}
	
}
