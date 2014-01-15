/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/08/2011
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.faces.component.UIData;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.projetos.AvaliadorProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.AvaliadorProjeto;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliador;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoAvaliacao;
import br.ufrn.sigaa.projetos.jsf.ConstantesNavegacaoProjetos;

/**
 * Classe que implementa a estratégia de distribuição automática para projetos associados.
 * 
 * @author Leonardo Campos
 *
 */
public class DistribuicaoAutomaticaProjetoAssociado extends
		AbstractEstrategiaDistribuicaoProjeto implements
		EstrategiaDistribuicaoProjetos {

	private final Integer SEM_AREA = -1;
	
	public DistribuicaoAutomaticaProjetoAssociado() {
	}
	
	public DistribuicaoAutomaticaProjetoAssociado(DistribuicaoAvaliacao distribuicao) {
		setDistribuicao(distribuicao);
	}
	
	@Override
	public void adicionarAvaliacao(Usuario avaliador) throws DAOException,
			NegocioException {
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
	public String formularioAvaliadores() {
		return ConstantesNavegacaoProjetos.DISTRIBUIR_PROJETOS_AVALIADORES_AUTO;
	}

	@Override
	public String formularioProjetos() {
		return ConstantesNavegacaoProjetos.DISTRIBUIR_PROJETOS_PROJETOS_AUTO;
	}

	@Override
	public Comando getComandoDistribuicao() {
		return SigaaListaComando.DISTRIBUIR_PROJETO_AUTOMATICO;
	}

	@Override
	public void removerAvaliacao(Avaliacao avaliacao) throws DAOException,
			NegocioException {
		
	}

	@Override
	public void selecionarProjetos(UIData dataTable) throws DAOException,
			NegocioException {
		projetos = new ArrayList<Projeto>();
		for(int i=0; i < dataTable.getRowCount(); i++) {
			dataTable.setRowIndex(i);
			 Projeto p = (Projeto) dataTable.getRowData();
			if(p.isSelecionado())
				projetos.add(p);
		}
		
		distribuir();
	}

	@Override
	public void distribuir() throws DAOException, NegocioException {
		List<AvaliadorProjeto> avaliadores = new ArrayList<AvaliadorProjeto>(getAvaliadoresDisponiveis());
		Collections.shuffle(avaliadores);
		
		Map<Integer, Queue<AvaliadorProjeto>> mapaArea = new HashMap<Integer, Queue<AvaliadorProjeto>>();
		
		for(AvaliadorProjeto a: avaliadores) {
			pushItem(mapaArea, a, a.getAreaConhecimento1() != null ? a.getAreaConhecimento1().getId() : SEM_AREA);
		}
		
		for(Projeto p: projetos) {
			setProjeto(p);
			popularAvaliadoresArea(mapaArea);
		}
	}

	/**
	 * Seleciona os avaliadores para o projeto de acordo com sua área de conhecimento. 
	 * @param mapaArea
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void popularAvaliadoresArea(Map<Integer, Queue<AvaliadorProjeto>> mapaArea) throws DAOException, NegocioException {
		
		Integer area = projeto.getAreaConhecimentoCnpq().getId();
		Integer num = getNumeroAvaliadoresProjeto() != null ? new Integer(getNumeroAvaliadoresProjeto()) : new Integer(0);
		
		Queue<AvaliadorProjeto> fila = mapaArea.get(area);
		
		if(fila != null) {
			Queue<AvaliadorProjeto> filaTemp = new LinkedList<AvaliadorProjeto>(fila);
			
			while(num > 0 && filaTemp != null && !filaTemp.isEmpty()) {
				// Verifica se o próximo avaliador da fila já foi distribuído para o projeto
				for(Avaliacao a: projeto.getAvaliacoesAtivas()){
					if(a.getAvaliador().getPessoa().getId() == filaTemp.peek().getUsuario().getPessoa().getId()){
						filaTemp.remove();
						fila.offer(fila.poll());
						continue;
					}
				}
				// Verifica se o próximo avaliador da fila pertence a equipe do projeto
				if(projeto.isPertenceEquipe(filaTemp.peek().getUsuario().getPessoa().getId())) {
					filaTemp.remove();
					fila.offer(fila.poll());
					continue;
				}
				adicionarAvaliacao(filaTemp.peek().getUsuario());
				filaTemp.remove();
				fila.offer(fila.poll());
				num--;
			}
		}
	}

	/**
	 * Acrescenta o avaliador na lista correta dentro do mapa de acordo com a
	 * área de conhecimento informada.
	 * 
	 * @param mapa
	 * @param avaliador
	 * @param area
	 */
	private void pushItem(Map<Integer, Queue<AvaliadorProjeto>> mapa,
			AvaliadorProjeto avaliador, Integer area) {
		if(mapa.get(area) != null){
			mapa.get(avaliador.getAreaConhecimento1().getId()).offer(avaliador);
		} else {
			Queue<AvaliadorProjeto> lista = new LinkedList<AvaliadorProjeto>();
			lista.offer(avaliador);
			mapa.put(area, lista);
		}
	}

	@Override
	public List<AvaliadorProjeto> getAvaliadoresDisponiveis()
			throws DAOException, NegocioException {
		if (distribuicao == null)
			throw new NegocioException(
					"Não foi possível realizar a distribuição pois as configurações não foram carregadas.");
		
		AvaliadorProjetoDao dao = DAOFactory.getInstance().getDAO(AvaliadorProjetoDao.class);
		List<AvaliadorProjeto> avaliadores = new ArrayList<AvaliadorProjeto>();
		try {

			if (distribuicao.getTipoAvaliador().getId() == TipoAvaliador.CONSULTORES_AD_HOC) {
				avaliadores = dao.findByAreaConhecimento(null);
			} else {
				avaliadores = dao.findUsuariosByComissao(MembroComissao.MEMBRO_COMISSAO_INTEGRADA);
			}

			return avaliadores;
		} finally {
			dao.close();
		}
	}
}
