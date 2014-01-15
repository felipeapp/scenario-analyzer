/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 11/05/2011
 */
package br.ufrn.integracao.interfaces;

import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.FeriadoDTO;

/**
 * Interface para o web service de consulta
 * de feriados.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface ConsultaFeriadosRemoteService {

	public List<FeriadoDTO> buscaFeriadosLocais(int ano, String municipio, String siglaEstado);
	
	public List<FeriadoDTO> buscarFeriadosNoAno(int ano);
	
	public List<FeriadoDTO> buscarFeriadosPorIntervaloDatas(Date dataInicio, Date dataFim);
	
}
