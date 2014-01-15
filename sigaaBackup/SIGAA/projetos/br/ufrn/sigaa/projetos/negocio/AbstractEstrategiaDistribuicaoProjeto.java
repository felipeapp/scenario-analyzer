/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe contendo atributos e m�todos comuns �s estrat�gias de distribui��o de projetos.
 * 
 * @author Leonardo Campos
 *
 */
public class AbstractEstrategiaDistribuicaoProjeto {

	/** Configura��es utilizadas para a distribui��o dos projetos */
	protected DistribuicaoAvaliacao distribuicao;
	
	/** Projeto selecionado para distribui��o manual */
	protected Projeto projeto;
	
	/** Lista de projetos selecionados para distribui��o autom�tica */
	protected List<Projeto> projetos;
	
	/** Total de avalia��es realizadas, utilizado como filtro para a busca de projetos a ser distribu�dos. */
	protected Integer totalAvaliacoesRealizadas;
	
	/** Quantidade de avaliadores para os quais se deseja destinar os projetos na distribui��o autom�tica */
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
	 * Lista todos os projetos compat�veis com a distribui��o selecionada.
	 * Estes projetos podem ser distribu�dos para novas avalia��es.
	 * 
	 */
	public List<Projeto> getProjetosParaDistribuir() throws DAOException,
			NegocioException {
		if(distribuicao == null)
			throw new NegocioException("N�o foi poss�vel obter os projetos para avaliar pois as configura��es da distribui��o n�o foram carregadas.");
		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		List<Projeto> result = new ArrayList<Projeto>();
		try {

			List<Projeto> projetos  = dao.findProjetosParaDistribuir(distribuicao);

			// Somente projetos que ainda n�o foram avaliados por membros do comit� interno
			// e com avalia��es discrepantes ou projetos com poucos avaliadores.
			if (distribuicao.getTipoAvaliador().getId() == TipoAvaliador.COMITE_INTEGRADO_ENSINO_PESQUISA_EXTENSAO 
					|| distribuicao.getTipoAvaliador().getId() == TipoAvaliador.COMISSAO_PESQUISA) {
				if (totalAvaliacoesRealizadas == null) {
					result = projetos;					
				}else {
					for (Projeto pj : projetos) {					
						if (pj.getAvaliacaoComiteInterno() == null && //n�o avaliado pelo comit� interno ainda
								pj.getTotalAvaliacoesAtivasRealizadas() == totalAvaliacoesRealizadas) {
							result.add(pj);
						}
					}
				}
			}

			// Somente projetos submetidos ou aguardando avalia��o.	
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
	 * Lista todos os avaliadores dispon�veis para realizar avalia��o de projetos.
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public List<AvaliadorProjeto> getAvaliadoresDisponiveis()
			throws DAOException, NegocioException {
		if (distribuicao == null)
			throw new NegocioException(
					"N�o foi poss�vel realizar a distribui��o pois as configura��es n�o foram carregadas.");
		if (projeto == null)
			throw new NegocioException(
					"N�o foi poss�vel realizar a distribui��o pois n�o h� um projeto carregado.");
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
	 * Lista todos os projetos aptos para finaliza��o das avalia��es.
	 * Ap�s a finaliza��o os projetos recebem uma m�dia e ficam habilitados para a 
	 * fase final do processo, a classifica��o.
	 * 
	 */
	public List<Projeto> getProjetosAvaliados() throws DAOException, NegocioException {
		if(distribuicao == null)
			throw new NegocioException("N�o foi poss�vel obter os projetos para avaliar pois as configura��es da distribui��o n�o foram carregadas.");
		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		List<Projeto> result = new ArrayList<Projeto>();
		try {

			List<Projeto> projetos  = dao.findProjetosParaAvaliar(distribuicao);

			if (distribuicao.getTipoAvaliador().getId() == TipoAvaliador.COMITE_INTEGRADO_ENSINO_PESQUISA_EXTENSAO) {
				if (totalAvaliacoesRealizadas == null) {
					result = projetos; //todos os projetos, utilizado na consolida��o das avalia��es		
				}else {
					for (Projeto pj : projetos) {
						if (pj.getAvaliacaoComiteInterno() != null && //j� avaliado pelo comit� interno
								pj.getTotalAvaliacoesAtivasRealizadas() == totalAvaliacoesRealizadas) {  //Selecionando projetos com a quantidade de avalia��es solicitada
							result.add(pj);
						}
					}
				}
			}

			// Somente projetos que foram avaliados pelos consultores ad hoc 
			if (distribuicao.getTipoAvaliador().getId() == TipoAvaliador.CONSULTORES_AD_HOC) {
				if (totalAvaliacoesRealizadas == null) {
					result = projetos; //todos os projetos, utilizado na consolida��o das avalia��es		
				}else {
					for (Projeto pj : projetos) {
						if (pj.getAvaliacaoComiteInterno() == null && //n�o avaliado pelo comit� interno
								pj.getTotalAvaliacoesAtivasRealizadas() == totalAvaliacoesRealizadas) { //Selecionando projetos com a quantidade de avalia��es solicitada
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
