package br.ufrn.sigaa.projetos.negocio;

import java.util.ArrayList;

import javax.faces.component.UIData;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.jsf.ConstantesNavegacaoProjetos;

/**
 * Classe que implementa a estratégia de distribuição manual para projetos associados.
 * 
 * @author Ilueny Santos
 * @author Leonardo Campos
 */
public class DistribuicaoManualProjetoAssociado extends AbstractEstrategiaDistribuicaoProjeto implements EstrategiaDistribuicaoProjetos {

	public DistribuicaoManualProjetoAssociado() {
	}
	
	public DistribuicaoManualProjetoAssociado(DistribuicaoAvaliacao distribuicao) {
		setDistribuicao(distribuicao);
	}

	@Override
	public void distribuir() throws NegocioException {
		if(projeto == null)
			throw new NegocioException("Não foi possível realizar a distribuição pois não há um projeto carregado.");
		//verificando se ainda existem avaliações pendentes
		for (Avaliacao ava : projeto.getAvaliacoes()) {
			if (ava.getSituacao().getId() == TipoSituacaoAvaliacao.PENDENTE) {
				projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO));
				break;
			}
		}	
	}

	@Override
	public void adicionarAvaliacao(Usuario avaliador) throws DAOException, NegocioException {
		if (distribuicao == null) {
			throw new NegocioException("Não foi possível adicionar a avaliação pois as configurações da distribuição não foram carregadas.");
		}
		if (projeto == null) {
			throw new NegocioException("Não foi possível adicionar a avaliação pois não há um projeto carregado.");
		}
		if (projeto.isPertenceEquipe(avaliador.getPessoa().getId())) {
			throw new NegocioException("Não foi possível adicionar a avaliação pois este avaliador faz parte da equipe do projeto.");
		}
		Avaliacao avaliacao = new Avaliacao();	
		avaliacao.setAtivo(true);
		avaliacao.setAvaliador(avaliador);
		avaliacao.setProjeto(projeto);
		avaliacao.setDistribuicao(distribuicao);
		TipoSituacaoAvaliacao situacao = new TipoSituacaoAvaliacao(TipoSituacaoAvaliacao.PENDENTE);
		situacao.setDescricao("PENDENTE");
		avaliacao.setSituacao(situacao);
		projeto.getAvaliacoes().add(avaliacao);	
	}

	@Override
	public void removerAvaliacao(Avaliacao avaliacao) throws DAOException, NegocioException {
		if (ValidatorUtil.isEmpty(avaliacao)) {
			if(projeto == null)
				throw new NegocioException("Não foi possível remover a avaliação pois não há um projeto carregado.");
			projeto.getAvaliacoes().remove(avaliacao);
		} else {
			avaliacao.setAtivo(false);
		}
	}

	@Override
	public String formularioAvaliadores() {
		return ConstantesNavegacaoProjetos.DISTRIBUIR_PROJETOS_AVALIADORES_MANUAL;
	}

	@Override
	public String formularioProjetos() {
		return ConstantesNavegacaoProjetos.DISTRIBUIR_PROJETOS_PROJETOS_MANUAL;
	}

	@Override
	public Comando getComandoDistribuicao() {
		return SigaaListaComando.DISTRIBUIR_PROJETO_MANUAL;
	}

	@Override
	public void selecionarProjetos(UIData dataTable) throws DAOException {
		projeto = (Projeto) dataTable.getRowData();
		if (ValidatorUtil.isEmpty(projeto.getAvaliacoes()))
			projeto.setAvaliacoes(new ArrayList<Avaliacao>());
	}

}
