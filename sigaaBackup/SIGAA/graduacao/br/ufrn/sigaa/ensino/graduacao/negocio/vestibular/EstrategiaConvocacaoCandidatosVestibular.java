/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/12/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio.vestibular;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ensino.dominio.CotaOfertaVagaCurso;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.SemestreConvocacao;

/**
 * Esta interface define uma estrat�gia de convoca��o de candidatos aprovados em
 * processos seletivos (Vestibular, SiSU, etc). Estas estrat�gias s�o associadas
 * ao Processo Seletivo Vestibular
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
public interface EstrategiaConvocacaoCandidatosVestibular {
	
	/**
	 * M�todo a ser invocado pelo caso de uso para processar as covoca��es de
	 * discentes aprovados segundo as regras definidas.
	 * @param convocacoes 
	 * 
	 * @param ofertaComVagasRemanescentes
	 * @param cotasComVagasRemanescentes
	 * @param semestreConvocacao
	 * @param processoSeletivo
	 * @param dentroNumeroVagas 
	 * @return
	 * @throws DAOException
	 */
	public void convocarCandidatos(
			List<ConvocacaoProcessoSeletivoDiscente> convocacoes, Map<OfertaVagasCurso, Integer> ofertaComVagasRemanescentes,
			Map<CotaOfertaVagaCurso, Integer> cotasComVagasRemanescentes,
			SemestreConvocacao semestreConvocacao,
			ProcessoSeletivoVestibular processoSeletivo, boolean dentroNumeroVagas) throws DAOException;

	/**
	 * Retorna uma descri��o curta desta estrat�gia. Esta descri��o � utilizada,
	 * por exemplo, em combobox (select) na interface para associar a estrat�gia
	 * ao processo seletivo.
	 * 
	 * @return
	 */
	public String getDescricaoCurta();

	/**
	 * Retorna uma descri��o completa desta estrat�gia. Esta descri��o �
	 * utilizada, por exemplo, em listagens detalhadas e relat�rios.
	 * 
	 * @return
	 */
	public String getDescricaoDetalhada();

	/**
	 * Retorna um mapa de ofertas de curso que possuem vagas n�o preenchidas. A
	 * busca por estas vagas pode variar conforme a estrat�gia de convoca��o
	 * 
	 * @param processoSeletivo
	 * @param semestreConvocacao
	 * @return
	 * @throws DAOException
	 */
	public Map<OfertaVagasCurso, Integer> findOfertaComVagasRemanescentes(
			ProcessoSeletivoVestibular processoSeletivo,
			SemestreConvocacao semestreConvocacao,
			boolean incluirPreCadastrado, boolean incluirPendenteCadastro)
			throws DAOException;

	/**
	 * Retorna um mapa de ofertas de cotas em curso que possuem vagas n�o
	 * preenchidas. A busca por estas vagas pode variar conforme a estrat�gia de
	 * convoca��o.
	 * 
	 * @param processoSeletivo
	 * @param semestreConvocacao
	 * @return
	 * @throws DAOException
	 */
	public Map<CotaOfertaVagaCurso, Integer> findCotasComVagasRemanescentes(
			ProcessoSeletivoVestibular processoSeletivo,
			SemestreConvocacao semestreConvocacao,
			boolean incluirPreCadastrado, boolean incluirPendenteCadastro)
			throws DAOException;

}
