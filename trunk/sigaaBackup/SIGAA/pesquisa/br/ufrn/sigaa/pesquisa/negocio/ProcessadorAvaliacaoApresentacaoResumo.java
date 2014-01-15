/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/10/2008
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoResumoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ResumoCongressoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoApresentacaoResumo;
import br.ufrn.sigaa.pesquisa.dominio.AvaliadorCIC;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.DiaApresentacaoCentro;
import br.ufrn.sigaa.pesquisa.dominio.OrganizacaoPaineisCIC;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;

/**
 * Processador responsável pelas seguintes operações:
 * <ul>
 * 	<li>	1. Distribuição automática das avaliações de apresentação de resumo do CIC.
 * 	<li>	2. Ajustes manuais na distribuição das avaliações de apresentação de resumo
 * 	<li>	3. Avaliação das apresentações de resumo (cadastro da pontuação + observações)
 * </ul> 
 * @author leonardo
 *
 */
public class ProcessadorAvaliacaoApresentacaoResumo extends AbstractProcessador {

	long init = System.currentTimeMillis();
	
	/**
	 * Esse método tem como função executar o processamento das avaliações das apresentações de resumo
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		if(mov.getCodMovimento() == SigaaListaComando.DISTRIBUIR_AVALIACAO_APRESENTACAO_RESUMO_CIC){
			distribuirAvaliacoesAutomatica((MovimentoAvaliacaoResumo) mov);
		} else if(mov.getCodMovimento() == SigaaListaComando.AJUSTAR_DISTRIBUICAO_AVALIACAO_APRESENTACAO_RESUMO_CIC){
			ajustarDistribuicao((MovimentoAvaliacaoResumo) mov);
		} else if(mov.getCodMovimento() == SigaaListaComando.AVALIAR_APRESENTACAO_RESUMO_CIC){
			avaliarApresentacao((MovimentoAvaliacaoResumo) mov);
		} else if(mov.getCodMovimento() == SigaaListaComando.GERAR_NUMERACAO_PAINEIS_RESUMO_CIC){
			gerarNumeracaoPaineis(mov);
		}
		
		return mov;
	}

	/**
	 * Cria e persiste a numeração dos painéis dos resumos.
	 * Se houver uma organização de painéis definida, apaga e cria uma nova  
	 * @param mov
	 * @throws NegocioException 
	 * @throws DAOException
	 */
	private void gerarNumeracaoPaineis(Movimento mov) throws ArqException, NegocioException {
		MovimentoAvaliacaoResumo movimento = (MovimentoAvaliacaoResumo) mov;
		OrganizacaoPaineisCIC organizacao = (OrganizacaoPaineisCIC) movimento.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		
//		Collection<OrganizacaoPaineisCIC> col = dao.findByExactField(OrganizacaoPaineisCIC.class, "congresso.id", organizacao.getCongresso().getId());
//		if(col != null && !col.isEmpty()){
//			OrganizacaoPaineisCIC organizacaoAntiga = col.iterator().next();
//			if(organizacaoAntiga.getDiasApresentacao() != null)
//				organizacaoAntiga.getDiasApresentacao().size();
//			dao.detach(organizacaoAntiga);
//			for(DiaApresentacaoCentro diaApresentacaoCentroAntigo: organizacaoAntiga.getDiasApresentacao()){
//				diaApresentacaoCentroAntigo.setOrganizacao(null);
//				dao.remove(diaApresentacaoCentroAntigo);
//			}
//			organizacaoAntiga.setDiasApresentacao(null);
//			dao.remove(organizacaoAntiga);
//		}
		
		dao.create(organizacao);
		
		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			Statement st = con.createStatement();
			
			Collections.sort(organizacao.getDiasApresentacao());
			int dia = 0;
			int numeracao = 0;
			for(DiaApresentacaoCentro d: organizacao.getDiasApresentacao()){
				if(d.getDia() != dia){
					dia = d.getDia();
					numeracao = 0;
				}
				for(Integer id: d.getListaTrabalhos())
					st.addBatch("UPDATE pesquisa.resumo_congresso SET numero_painel = "+ ++numeracao +" WHERE id_resumo_congresso = "+ id);
			}
			st.executeBatch();
		}catch (SQLException e) {
			e.printStackTrace();
			throw new ArqException("Erro na geração da numeração dos painéis dos resumos!");
		} finally {
			long time = System.currentTimeMillis() - init;
			System.out.println( " \n\nTEMPO DE EXECUCAO: " + time + "\n\n"  );
			try {
				if(con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Remove a distribuição de avaliações anterior, caso exista.
	 * Obtém os resumos aprovados e cria as novas avaliações, distribuindo-as para os avaliadores do centro
	 * correspondente ao centro de cada resumo. O número de avaliações para cada resumo é passado junto com o movimento.
	 * 
	 * @param mov
	 * @throws ArqException
	 */
	private void distribuirAvaliacoesAutomatica(MovimentoAvaliacaoResumo mov) throws ArqException {
		CongressoIniciacaoCientificaDao daoCongresso = getDAO(CongressoIniciacaoCientificaDao.class, mov);
		ResumoCongressoDao daoResumo = getDAO(ResumoCongressoDao.class, mov);
		AvaliacaoResumoDao dao = getDAO(AvaliacaoResumoDao.class, mov);

		
		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			Statement st = con.createStatement();
			
			// As avaliações de apresentação vindas no movimento são as que já foram distribuídas anteriormente
			Collection<AvaliacaoApresentacaoResumo> avaliacoesDistribuidas = mov.getAvaliacoesApresentacao();
			if(avaliacoesDistribuidas != null && !avaliacoesDistribuidas.isEmpty()){
				for(AvaliacaoApresentacaoResumo avaliacaoAntiga: avaliacoesDistribuidas){
					st.addBatch("DELETE FROM pesquisa.nota_item WHERE id_avaliacao_apresentacao_resumo = "+ avaliacaoAntiga.getId());
					st.addBatch("DELETE FROM pesquisa.avaliacao_apresentacao_resumo WHERE id_avaliacao_apresentacao_resumo = "+ avaliacaoAntiga.getId());
				}
				st.executeBatch();
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw new ArqException("Erro ao apagar as avaliações de apresentação de resumos!");
		} finally {
			long time = System.currentTimeMillis() - init;
			System.out.println( " \n\nTEMPO DE EXECUCAO: " + time + "\n\n"  );
			try {
				if(con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		CongressoIniciacaoCientifica congresso = daoCongresso.findAtivo();
		Collection<ResumoCongresso> resumos = daoResumo.filter(congresso.getId(), null, null, null, null, ResumoCongresso.APROVADO, null, null, null, null);
		Collection<AvaliacaoApresentacaoResumo> avaliacoes = new HashSet<AvaliacaoApresentacaoResumo>();
		
		for(ResumoCongresso resumo: resumos){
			for(int i = 0; i < mov.getNumeroAvaliacoes(); i++){
				AvaliacaoApresentacaoResumo avaliacao = new AvaliacaoApresentacaoResumo();
				avaliacao.setResumo(resumo);
				avaliacoes.add(avaliacao);
			}
		}
		
		Map<Integer, Collection<AvaliadorCIC>> mapaAvaliadores = dao.findMapaAvaliadoresApresentacaoResumoByCentro(congresso.getId());
		
		Map<Integer, Iterator<AvaliadorCIC>> mapaIteradores = new HashMap<Integer, Iterator<AvaliadorCIC>>();
		for(Integer i: mapaAvaliadores.keySet()){
			mapaIteradores.put(i, mapaAvaliadores.get(i).iterator());
		}
		
		// Representa as avaliações que não são repetidas para um mesmo avaliador.
		Collection<AvaliacaoApresentacaoResumo> avaliacoesAux = new HashSet<AvaliacaoApresentacaoResumo>();
		// Representa um mapa das avaliações, ou seja, as avaliações de cada avaliador.
		Map<Integer, Set<AvaliacaoApresentacaoResumo>> mapAvaliacoes = new HashMap<Integer, Set<AvaliacaoApresentacaoResumo>>();
		// Mapa com as unidades agrupadoras
		Map<Integer, Integer> mapaUnidadesAgrupadoras = dao.findMapaUnidadesAgrupadoras();
		
		for(AvaliacaoApresentacaoResumo av: avaliacoes){
			int centro = mapaUnidadesAgrupadoras.get(av.getResumo().getIdUnidadeAgrupadora());
			Iterator<AvaliadorCIC> it = mapaIteradores.get(centro);
			AvaliadorCIC avaliador = it.next();
	
			// garantindo que um avaliador não seja associado a um trabalho que ele mesmo orienta.
			while(avaliador.getDocente().getId() == av.getResumo().getOrientador().getDocente().getId()){
				if( !it.hasNext() ){
					it = mapaAvaliadores.get(centro).iterator();
				}
				avaliador = it.next();
			}
			
			av.setAvaliador(avaliador);
			
			if(mapAvaliacoes.get(av.getAvaliador().getDocente().getId()) == null)
				mapAvaliacoes.put(av.getAvaliador().getDocente().getId(), new HashSet<AvaliacaoApresentacaoResumo>());
			
			// Garante que não haverá avaliações repetidas para um mesmo avaliador
			boolean semRepeticao = true;
			for(AvaliacaoApresentacaoResumo avaliacao : mapAvaliacoes.get(av.getAvaliador().getDocente().getId())){
				if(avaliacao.getResumo().getCodigo().equals(av.getResumo().getCodigo()))
					semRepeticao = false;
			}
			if(semRepeticao){
				dao.create(av);
				avaliacoesAux.add(av);
				mapAvaliacoes.get(av.getAvaliador().getDocente().getId()).add(av);
			}
			
			if( !it.hasNext() ){
				it = mapaAvaliadores.get(centro).iterator();
				
			}
			
			mapaIteradores.put(centro, it);
		}
		
		mov.setAvaliacoesApresentacao(avaliacoesAux);
		
	}

	/**
	 * Calcula a média e persiste a avaliação.
	 * @param mov
	 * @param dao
	 * @throws DAOException
	 */
	private void avaliarApresentacao(MovimentoAvaliacaoResumo mov) throws DAOException {
		
		GenericDAO dao = getGenericDAO(mov);
		
		// A avaliação de apresentação é feita individualmente para cada resumo.
		AvaliacaoApresentacaoResumo ava = mov.getAvaliacoesApresentacao().iterator().next();
		if(ava.getId() > 0){
			ava.calcularMedia();
			dao.update(ava);
		}else{
			dao.create(ava);
		}
		
	}

	/**
	 * Realiza ajustes (cria ou remove avaliações) de acordo com as informações passadas no movimento
	 * 
	 * @param mov
	 * @param dao
	 * @throws DAOException
	 */
	private void ajustarDistribuicao(MovimentoAvaliacaoResumo mov) throws DAOException {
		GenericDAO dao = getGenericDAO(mov);
		
		for(AvaliacaoApresentacaoResumo ava: mov.getAvaliacoesApresentacao()){
			
			// Se possui Id e não está marcado, remove do banco
			// Se não possui Id e está marcado, cria novo registro
			if(ava.getId() > 0 && !ava.isSelecionado())
				dao.remove(ava);
			else if(ava.getId() <= 0 && ava.isSelecionado())
				dao.create(ava);
		}
	}

	/**
	 * Método responsável pela validação do processamento das avaliações de apresentação de um resumo.
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens mensagens = new ListaMensagens();
		if(mov.getCodMovimento() == SigaaListaComando.DISTRIBUIR_AVALIACAO_APRESENTACAO_RESUMO_CIC)
		{
			MovimentoAvaliacaoResumo movAvaliacao = (MovimentoAvaliacaoResumo) mov;
			ValidatorUtil.validateRequired(movAvaliacao.getNumeroAvaliacoes(), "Número de avaliações por trabalho", mensagens);
			
		} else if(mov.getCodMovimento() == SigaaListaComando.AJUSTAR_DISTRIBUICAO_AVALIACAO_APRESENTACAO_RESUMO_CIC)
		{
			// TODO validações específicas da operação
			
		} else if(mov.getCodMovimento() == SigaaListaComando.AVALIAR_APRESENTACAO_RESUMO_CIC)
		{
			// TODO validações específicas da operação
			
		} else if(mov.getCodMovimento() == SigaaListaComando.GERAR_NUMERACAO_PAINEIS_RESUMO_CIC)
		{
			// TODO validações específicas da operação
			
		}
		checkValidation(mensagens);
	}

}
