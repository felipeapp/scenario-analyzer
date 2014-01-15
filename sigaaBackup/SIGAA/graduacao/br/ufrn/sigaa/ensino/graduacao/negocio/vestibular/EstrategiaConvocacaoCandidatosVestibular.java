/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Esta interface define uma estratégia de convocação de candidatos aprovados em
 * processos seletivos (Vestibular, SiSU, etc). Estas estratégias são associadas
 * ao Processo Seletivo Vestibular
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public interface EstrategiaConvocacaoCandidatosVestibular {
	
	/**
	 * Método a ser invocado pelo caso de uso para processar as covocações de
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
	 * Retorna uma descrição curta desta estratégia. Esta descrição é utilizada,
	 * por exemplo, em combobox (select) na interface para associar a estratégia
	 * ao processo seletivo.
	 * 
	 * @return
	 */
	public String getDescricaoCurta();

	/**
	 * Retorna uma descrição completa desta estratégia. Esta descrição é
	 * utilizada, por exemplo, em listagens detalhadas e relatórios.
	 * 
	 * @return
	 */
	public String getDescricaoDetalhada();

	/**
	 * Retorna um mapa de ofertas de curso que possuem vagas não preenchidas. A
	 * busca por estas vagas pode variar conforme a estratégia de convocação
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
	 * Retorna um mapa de ofertas de cotas em curso que possuem vagas não
	 * preenchidas. A busca por estas vagas pode variar conforme a estratégia de
	 * convocação.
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
