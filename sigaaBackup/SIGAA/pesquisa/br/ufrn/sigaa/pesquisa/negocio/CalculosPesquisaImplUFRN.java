package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.prodocente.EmissaoRelatorioDao;
import br.ufrn.sigaa.arq.dao.prodocente.EmissaoRelatorioMediaDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CotaDocente;
import br.ufrn.sigaa.pesquisa.dominio.Cotas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.RelatorioHelper;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorioItem;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorioMedia;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoItem;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoRelatorioProdutividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.RelatorioProdutividade;

/**
 * Implementação da UFRN para os cálculos do módulo de pesquisa.
 * @author Victor Hugo
 */
public class CalculosPesquisaImplUFRN implements CalculosPesquisa {

	/**
	 * Antes de ser executado a distribuição das cotas é criado uma grade de distribuição. Esta grade é uma coleção de cotas que é criado para cada docente e cada tipo de cota.
	 * A partir dela é feita a distribuição da cotas, seja manual ou automática.
	 */
	@Override
	public TreeSet<CotaDocente> criarGradeDistribuicaoCotas(
			ClassificacaoRelatorio classificacao, EditalPesquisa edital) throws DAOException, NegocioException {
		
		ProjetoPesquisaDao projetoDao = new ProjetoPesquisaDao();
        PlanoTrabalhoDao planoDao = new PlanoTrabalhoDao();
		ListaMensagens erros = new ListaMensagens();
		
		
		if ( isEmpty( classificacao ) ) {
			erros.addErro("O relatório de ranking de docentes selecionado não foi encontrado");
		}
		
		if( isEmpty( edital ) ){
			erros.addErro("O edital ... não foi encontrado");
		}
		
		if( !erros.isEmpty() ){
			throw new NegocioException(erros);
		}
        
		
		classificacao = projetoDao.refresh(classificacao);
		edital = projetoDao.refresh(edital);
		
        // Coleção ordenada de cotas de docentes
        TreeSet<CotaDocente> cotasDocentes = new TreeSet<CotaDocente>();

        try{
        	
	     // Buscar FPPIs dos docentes desta classificação
        	classificacao = projetoDao.findByPrimaryKey(classificacao.getId(), ClassificacaoRelatorio.class);
	        Collection<EmissaoRelatorio> emissoes = classificacao.getEmissaoRelatorioCollection();
	        Map<Integer, Double> notasProjeto = projetoDao.findNotaProjetosByAnoEdital( edital );
	        Set<Integer> solicitacoesCota = planoDao.findSolicitacoesByEdital(edital);
        
	        if ( isEmpty( solicitacoesCota ) ) {
	        	throw new NegocioException("Não há solicitações de cotas para o edital selecionado.");
	        }
	        
	        EmissaoRelatorio emissaoUm = emissoes.iterator().next();
	        if( emissaoUm == null || emissaoUm.getFppi() == null ){
	        	throw new NegocioException("É necessário calcular os FPPIs deste relatório antes de gerar a distribuição das cotas.");
	        }
	        
	        // Percorrer emissões e popular as cotas com os FPPIs e médias de projetos
	        for ( EmissaoRelatorio emissao : emissoes ) {
	            Servidor docente = emissao.getServidor();
	            if(!solicitacoesCota.contains(docente.getId()))
	                continue;

	            Double nota = notasProjeto.get(docente.getId());
	            nota = nota != null ? nota : 0.0;

	            CotaDocente cotaDocente = new CotaDocente();
	            cotaDocente.setEmissaoRelatorio(emissao);
	            cotaDocente.setEdital(edital);
	            cotaDocente.setDocente(docente);
	            cotaDocente.setFppi(emissao.getFppi());
	            cotaDocente.setMediaProjetos(nota);
	            for(Cotas c: edital.getCotas()){
	                Cotas cNova = new Cotas();
	                cNova.setTipoBolsa(c.getTipoBolsa());
	                cNova.setQuantidade(0);
	                cotaDocente.addCotas(cNova);
	            }
	            cotasDocentes.add(cotaDocente);
	        }
	        
	        
	        /** chamada do método que faz a distribuição automática */
	        distribuirCotas(cotasDocentes, edital);
	        
        
        } finally{
        	projetoDao.close();
        	planoDao.close();
        }
        
		return cotasDocentes;
	}
	
	
	/**
	 * NA UFRN as cotas são distribuídas manualmente, não sendo necessário portanto, a implementação deste método que faz a distribuição automáticamente.
	 */
	@Override
	public void distribuirCotas(TreeSet<CotaDocente> grade, EditalPesquisa edital)
			throws DAOException {
		
	}


	/**
	 * Este método calcula o Índice de Produtividade Individual - IPI - do docente
	 */
	public void calcularIpiDocente(EmissaoRelatorio emissaoRelatorio, FormacaoAcademicaRemoteService serviceFormacao) throws ArqException {
		
		//monta o relatório do docente
		RelatorioProdutividade relatorioProdutividade = RelatorioHelper.montarRelatorioProdutividade(
					emissaoRelatorio.getClassificacaoRelatorio().getRelatorioProdutividade() ,
					emissaoRelatorio.getServidor(),
					emissaoRelatorio.getClassificacaoRelatorio().getAnoVigencia(), serviceFormacao);

		Collection<EmissaoRelatorioItem> itensEmissaoRelatorio = new ArrayList<EmissaoRelatorioItem>();

		Double ipi = 0.0;

		//Iteração para associarmos os grupos itens do relatório com os itens da emissão do relatório e
		//somamos os pontos obtidos em cada item do relatório respeitando o limite do valor
		//máximo do grupo a qual o item pertence
		for (GrupoRelatorioProdutividade grupoRelatorioProdutividade : relatorioProdutividade.getGrupoRelatorioProdutividadeCollection()) {
			Double pontuacaoGrupo = 0.0;
			for (GrupoItem grupoItem : grupoRelatorioProdutividade.getGrupoItemCollection()) {

				EmissaoRelatorioItem emissaoRelatorioItem = new EmissaoRelatorioItem();
				emissaoRelatorioItem.setGrupoItem(grupoItem);
				emissaoRelatorioItem.setPontos( (double) grupoItem.getTotalPontos());
				emissaoRelatorioItem.setEmissaoRelatorio(emissaoRelatorio);

				itensEmissaoRelatorio.add(emissaoRelatorioItem);

				if(grupoRelatorioProdutividade.getPontuacaoMaxima() == 0 ||
						(pontuacaoGrupo + grupoItem.getTotalPontos()) <= grupoRelatorioProdutividade.getPontuacaoMaxima()){
					pontuacaoGrupo += grupoItem.getTotalPontos();
				} else {
					pontuacaoGrupo = (double) grupoRelatorioProdutividade.getPontuacaoMaxima();
				}
			}
			ipi+=pontuacaoGrupo;
		}

		emissaoRelatorio.setEmissaoRelatorioItemCollection(itensEmissaoRelatorio);
		emissaoRelatorio.setIpi(ipi);
		
	}

	
	
	/**
	 * Este método efetua o cálculo dos Fatores de Produção em Pesquisa
	 * Individual - FPPIs dos docentes de um determinado relatório de
	 * classificação.
	 */
	@Override
	public void calcularFPPI(ClassificacaoRelatorio classificacao) throws ArqException {
		

		EmissaoRelatorioMediaDao mediaDao = new EmissaoRelatorioMediaDao();
		EmissaoRelatorioDao emissaoDao = new EmissaoRelatorioDao();

		try{
			// Remover cálculos anteriores
			mediaDao.removeByClassificacao(classificacao);
	
			ArrayList<EmissaoRelatorio> ranking = RelatorioHelper.montarRankingProdutividade(emissaoDao, classificacao, TipoUnidadeAcademica.DEPARTAMENTO);
			Map<Integer, EmissaoRelatorioMedia> mapaMedias = new HashMap<Integer, EmissaoRelatorioMedia>();
			
			NormalDistribution normal = new NormalDistributionImpl(0, 1);
			Mean mean = new Mean();
			StandardDeviation stdDev = new StandardDeviation();
			
			int idUnidade = 0;
			ArrayList<Double> listaIpi = new ArrayList<Double>();
			Object[] rankingArray = ranking.toArray();
			for(int i = 0; i < rankingArray.length; i++){
				EmissaoRelatorio emissao = (EmissaoRelatorio) rankingArray[i];
				if( idUnidade != emissao.getServidor().getUnidade().getGestora().getId() ){
					// mudou de centro/unidade
					if(idUnidade > 0){
						calcularEstatisticas(classificacao, mapaMedias, mean,
								stdDev, idUnidade, listaIpi);
					}
					idUnidade = emissao.getServidor().getUnidade().getGestora().getId();
					listaIpi = new ArrayList<Double>();
					mean.clear();
					stdDev.clear();
				} 
				
				listaIpi.add(emissao.getIpi());
			}
			
			if(listaIpi !=null && !listaIpi.isEmpty()){
				calcularEstatisticas(classificacao, mapaMedias, mean, stdDev,
						idUnidade, listaIpi);
			}
	
			// Persiste as médias
			for(EmissaoRelatorioMedia erm: mapaMedias.values()){
				emissaoDao.createNoFlush(erm);
			}
			
			// Utiliza as médias para calcular o FPPI e atualiza o ranking
			for(EmissaoRelatorio emissao: ranking){
				EmissaoRelatorioMedia emissaoMedia = mapaMedias.get(emissao.getServidor().getUnidade().getGestora().getId());
				
				double ipi = emissao.getIpi();
				double ipiMedio = emissaoMedia.getIpiMedio();
				double ipiDesvPad = emissaoMedia.getIpiDesvpad();
				
				Double fppi = 0.0;
				fppi = 10.0 * normal.cumulativeProbability( (ipi - ipiMedio) / ipiDesvPad );
				emissao.setFppi(truncate(fppi));
				emissao.setClassificacaoRelatorio(classificacao);
				emissaoDao.updateNoFlush(emissao);
			}
			
		} catch (MathException e) {
            e.printStackTrace();
            throw new ArqException("Erro ao calcular a probabilidade cumulativa.");
		}finally{
			mediaDao.close();
			emissaoDao.close();
		}
	}

	
	/**
	 * Calcula as estatísicas de Média e Desvio Padrão da lista de IPIs passada
	 * como argumento e coloca no mapa de médias.
	 * 
	 * @param classificacao
	 * @param mapaMedias
	 * @param mean
	 * @param stdDev
	 * @param idUnidade
	 * @param listaIpi
	 */
	private void calcularEstatisticas(ClassificacaoRelatorio classificacao,
			Map<Integer, EmissaoRelatorioMedia> mapaMedias, Mean mean,
			StandardDeviation stdDev, int idUnidade, ArrayList<Double> listaIpi) {
		EmissaoRelatorioMedia emissaoMedia = new EmissaoRelatorioMedia();
		emissaoMedia.setClassificacaoRelatorio(classificacao);
		emissaoMedia.setUnidade(new Unidade(idUnidade));
		double[] arrayDouble = toArrayDouble(listaIpi);
		Double media = mean.evaluate(arrayDouble);
		emissaoMedia.setIpiMedio(truncate(media));
		emissaoMedia.setIpiDesvpad(truncate(stdDev.evaluate(arrayDouble, media)));
		
		mapaMedias.put(idUnidade, emissaoMedia);
	}
	
	/**
	 * método auxiliar para truncar um double
	 * @param value
	 * @return
	 */
	private double truncate(double value) {  
		return Math.round(value * 100) / 100d;  
	}  
	
	/**
	 * Método auxiliar para transformar uma lista num array.
	 * @param listaIpi
	 * @return
	 */
	private double[] toArrayDouble(ArrayList<Double> listaIpi) {
		int tam = listaIpi.size();
		
		double[] array = new double[tam];
		Object[] arrayObject = listaIpi.toArray();
		
		for (int i = 0; i < arrayObject.length; i++) {
			array[i] = (Double) arrayObject[i];
		}
		
		return array;
	}
}
