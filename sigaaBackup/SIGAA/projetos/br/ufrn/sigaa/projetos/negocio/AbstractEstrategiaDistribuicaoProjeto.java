/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/08/2011
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.projetos.AvaliadorProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.AvaliadorProjeto;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliador;

/**
 * Classe contendo atributos e métodos comuns às estratégias de distribuição de projetos.
 * 
 * @author Leonardo Campos
 *
 */
public class AbstractEstrategiaDistribuicaoProjeto {

	/** Configurações utilizadas para a distribuição dos projetos */
	protected DistribuicaoAvaliacao distribuicao;
	
	/** Projeto selecionado para distribuição manual */
	protected Projeto projeto;
	
	/** Lista de projetos selecionados para distribuição automática */
	protected List<Projeto> projetos;
	
	/** Total de avaliações realizadas, utilizado como filtro para a busca de projetos a ser distribuídos. */
	protected Integer totalAvaliacoesRealizadas;
	
	/** Quantidade de avaliadores para os quais se deseja destinar os projetos na distribuição automática */
	protected Integer numeroAvaliadoresProjeto;

	public DistribuicaoAvaliacao getDistribuicao() {
		return distribuicao;
	}

	public void setDistribuicao(DistribuicaoAvaliacao distribuicao) {
		this.distribuicao = distribuicao;
	}
	
	public Integer getTotalAvaliacoesRealizadas() {
		return totalAvaliacoesRealizadas;
	}

	public void setTotalAvaliacoesRealizadas(Integer totalAvaliacoesRealizadas) {
		this.totalAvaliacoesRealizadas = totalAvaliacoesRealizadas;
	}

	public List<Projeto> getProjetos() {
		return projetos;
	}

	public void setProjetos(List<Projeto> projetos) {
		this.projetos = projetos;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}
	
	/**
	 * Lista todos os projetos compatíveis com a distribuição selecionada.
	 * Estes projetos podem ser distribuídos para novas avaliações.
	 * 
	 */
	public List<Projeto> getProjetosParaDistribuir() throws DAOException,
			NegocioException {
		if(distribuicao == null)
			throw new NegocioException("Não foi possível obter os projetos para avaliar pois as configurações da distribuição não foram carregadas.");
		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		List<Projeto> result = new ArrayList<Projeto>();
		try {

			List<Projeto> projetos  = dao.findProjetosParaDistribuir(distribuicao);

			// Somente projetos que ainda não foram avaliados por membros do comitê interno
			// e com avaliações discrepantes ou projetos com poucos avaliadores.
			if (distribuicao.getTipoAvaliador().getId() == TipoAvaliador.COMITE_INTEGRADO_ENSINO_PESQUISA_EXTENSAO 
					|| distribuicao.getTipoAvaliador().getId() == TipoAvaliador.COMISSAO_PESQUISA) {
				if (totalAvaliacoesRealizadas == null) {
					result = projetos;					
				}else {
					for (Projeto pj : projetos) {					
						if (pj.getAvaliacaoComiteInterno() == null && //não avaliado pelo comitê interno ainda
								pj.getTotalAvaliacoesAtivasRealizadas() == totalAvaliacoesRealizadas) {
							result.add(pj);
						}
					}
				}
			}

			// Somente projetos submetidos ou aguardando avaliação.	
			if (distribuicao.getTipoAvaliador().getId() == TipoAvaliador.CONSULTORES_AD_HOC) {
				if (totalAvaliacoesRealizadas == null) {
					result = projetos;					
				}else {
					for (Projeto pj : projetos) {
						if (pj.getTotalAvaliacoesAtivasRealizadas() == totalAvaliacoesRealizadas) {
							result.add(pj);
						}
					}
				}
			}

		}finally {
			dao.close();
		}
		return result;
	}
	
	/**
	 * Lista todos os avaliadores disponíveis para realizar avaliação de projetos.
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public List<AvaliadorProjeto> getAvaliadoresDisponiveis()
			throws DAOException, NegocioException {
		if (distribuicao == null)
			throw new NegocioException(
					"Não foi possível realizar a distribuição pois as configurações não foram carregadas.");
		if (projeto == null)
			throw new NegocioException(
					"Não foi possível realizar a distribuição pois não há um projeto carregado.");
		AvaliadorProjetoDao dao = DAOFactory.getInstance().getDAO(
				AvaliadorProjetoDao.class);
		List<AvaliadorProjeto> avaliadores = new ArrayList<AvaliadorProjeto>();
		try {

			if (distribuicao.getTipoAvaliador().getId() == TipoAvaliador.CONSULTORES_AD_HOC) {
				avaliadores = dao.findByAreaConhecimento(projeto
						.getAreaConhecimentoCnpq().getId());
			} else if (distribuicao.getTipoAvaliador().getId() == TipoAvaliador.COMITE_INTEGRADO_ENSINO_PESQUISA_EXTENSAO) {
				avaliadores = dao
						.findUsuariosByComissao(MembroComissao.MEMBRO_COMISSAO_INTEGRADA);
			} else if (distribuicao.getTipoAvaliador().getId() == TipoAvaliador.COMISSAO_PESQUISA) {
				avaliadores = dao
						.findUsuariosByComissao(MembroComissao.MEMBRO_COMISSAO_PESQUISA);
			}

			for (Avaliacao ava : projeto.getAvaliacoesAtivas()) {
				avaliadores.remove(ava.getAvaliador());
			}

			return avaliadores;
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Lista todos os projetos aptos para finalização das avaliações.
	 * Após a finalização os projetos recebem uma média e ficam habilitados para a 
	 * fase final do processo, a classificação.
	 * 
	 */
	public List<Projeto> getProjetosAvaliados() throws DAOException, NegocioException {
		if(distribuicao == null)
			throw new NegocioException("Não foi possível obter os projetos para avaliar pois as configurações da distribuição não foram carregadas.");
		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		List<Projeto> result = new ArrayList<Projeto>();
		try {

			List<Projeto> projetos  = dao.findProjetosParaAvaliar(distribuicao);

			if (distribuicao.getTipoAvaliador().getId() == TipoAvaliador.COMITE_INTEGRADO_ENSINO_PESQUISA_EXTENSAO) {
				if (totalAvaliacoesRealizadas == null) {
					result = projetos; //todos os projetos, utilizado na consolidação das avaliações		
				}else {
					for (Projeto pj : projetos) {
						if (pj.getAvaliacaoComiteInterno() != null && //já avaliado pelo comitê interno
								pj.getTotalAvaliacoesAtivasRealizadas() == totalAvaliacoesRealizadas) {  //Selecionando projetos com a quantidade de avaliações solicitada
							result.add(pj);
						}
					}
				}
			}

			// Somente projetos que foram avaliados pelos consultores ad hoc 
			if (distribuicao.getTipoAvaliador().getId() == TipoAvaliador.CONSULTORES_AD_HOC) {
				if (totalAvaliacoesRealizadas == null) {
					result = projetos; //todos os projetos, utilizado na consolidação das avaliações		
				}else {
					for (Projeto pj : projetos) {
						if (pj.getAvaliacaoComiteInterno() == null && //não avaliado pelo comitê interno
								pj.getTotalAvaliacoesAtivasRealizadas() == totalAvaliacoesRealizadas) { //Selecionando projetos com a quantidade de avaliações solicitada
							result.add(pj);
						}		
					}
				}
			}

		}finally {
			dao.close();
		}
		return result;
	}

	public Integer getNumeroAvaliadoresProjeto() {
		return numeroAvaliadoresProjeto;
	}

	public void setNumeroAvaliadoresProjeto(Integer numeroAvaliadoresProjeto) {
		this.numeroAvaliadoresProjeto = numeroAvaliadoresProjeto;
	}
}
