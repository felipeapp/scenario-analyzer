/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/10/2006
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.HistoricoPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;

/**
 * Processador responsável por efetuar a distribuição dos planos de trabalho
 * para os consultores, para posterior avaliação
 *
 * @author ricardo
 *
 */
@Deprecated
public class ProcessadorDistribuicaoPlanosTrabalho extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate(mov);

		MovimentoDistribuicaoPesquisa distribuicao = (MovimentoDistribuicaoPesquisa) mov;


		Map<Consultor, AreaConhecimentoCnpq> mapaDistribuicao = distribuicao.getMapaDistribuicaoManual();
		Collection<PlanoTrabalho> planosDistribuidos = new ArrayList<PlanoTrabalho>();

		GenericDAO dao = getDAO(mov);
		try {

			// Distribuir as avaliações de acordo com o mapa de distribuição
			for( Consultor consultor : mapaDistribuicao.keySet()){
				AreaConhecimentoCnpq area = mapaDistribuicao.get(consultor);

				// Rever consulta de projetos por status
				Collection<PlanoTrabalho> planosDaArea = dao.findByExactField(PlanoTrabalho.class, "projetoPesquisa.areaConhecimentoCnpq.id", area.getId());

				// Verificar se há consultores para a área do plano de trabalho
				if ((planosDaArea != null) && (planosDaArea.size() > 0)){

					for( PlanoTrabalho pt : planosDaArea ){

						HistoricoPlanoTrabalho historico = new HistoricoPlanoTrabalho();
						historico.setPlanoTrabalho(pt);
						historico.setStatus(TipoStatusPlanoTrabalho.EM_AVALIACAO);
						historico.setData(new Date());
						historico.setRegistroEntrada(  mov.getUsuarioLogado().getRegistroEntrada() );
						dao.create(historico);

						pt.setConsultor(consultor);
						pt.setStatus(TipoStatusPlanoTrabalho.EM_AVALIACAO);

						dao.update(pt);
						planosDistribuidos.add(pt);
					}

				}

			}

		} catch (DAOException e) {
			throw new ArqException("Erro na distribuição dos planos de trabalho!");
		} finally {
			dao.close();
		}

		return planosDistribuidos;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);
	}

}
