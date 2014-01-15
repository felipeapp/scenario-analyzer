/*
 * MovimentoAcoesAuxiliaresExportaTituloAutoridade.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArquivoDeCargaNumeroControleFGV;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RegistroExportacaoCooperacaoTecnica;

/**
 *  Passa os dados para o processador.
 *
 * @author jadson
 * @since 08/06/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoAcoesAuxiliaresExportaTituloAutoridade extends AbstractMovimentoAdapter{

	private List<RegistroExportacaoCooperacaoTecnica> listaRegistros;
	private List<CacheEntidadesMarc> entidadesExportadas;
	private boolean exportacaoTitulos;
	private List<ArquivoDeCargaNumeroControleFGV> arquivosCarregados; // atualiza os números de controle usados na exportação para a FGV
	
	public MovimentoAcoesAuxiliaresExportaTituloAutoridade(List<RegistroExportacaoCooperacaoTecnica> listaRegistros, 
			 List<CacheEntidadesMarc> entidadesExportadas, boolean exportacaoTitulos
			 , List<ArquivoDeCargaNumeroControleFGV> arquivosCarregados ){
		this.listaRegistros = listaRegistros;
		this.entidadesExportadas = entidadesExportadas;
		this.exportacaoTitulos = exportacaoTitulos;
		this.arquivosCarregados = arquivosCarregados;
	}
	
	public List<RegistroExportacaoCooperacaoTecnica> getListaRegistros() {
		return listaRegistros;
	}

	public List<CacheEntidadesMarc> getEntidadesExportadas() {
		return entidadesExportadas;
	}

	public boolean isExportacaoTitulos() {
		return exportacaoTitulos;
	}

	public List<ArquivoDeCargaNumeroControleFGV> getArquivosCarregados() {
		return arquivosCarregados;
	}
	
	
}
