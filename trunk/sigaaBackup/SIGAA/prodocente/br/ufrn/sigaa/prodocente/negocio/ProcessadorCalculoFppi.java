/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '25/05/2008'
 *
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.CalculosPesquisa;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;

/**
 * Processador para efetuar o c�lculo dos Fatores de Produ��o em Pesquisa
 * Individual - FPPIs dos docentes de um determinado relat�rio de
 * classifica��o.
 * 
 * @author Ricardo Wendell
 * @author Leonardo Campos
 */
public class ProcessadorCalculoFppi extends AbstractProcessador {

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoCadastro classificacaoMovimento = (MovimentoCadastro) mov;
		ClassificacaoRelatorio classificacao = classificacaoMovimento.getObjMovimentado();
		
		String implementacao = ParametroHelper.getInstance().getParametro(ParametrosPesquisa.IMPLEMENTACAO_COMPORTAMENTOS_PESQUISA);
        CalculosPesquisa calculos = ReflectionUtils.newInstance( implementacao );
        calculos.calcularFPPI(classificacao);
		
		
		/*
		MovimentoCadastro classificacaoMovimento = (MovimentoCadastro) mov;
		ClassificacaoRelatorio classificacao = classificacaoMovimento.getObjMovimentado();

		EmissaoRelatorioMediaDao mediaDao = getDAO(EmissaoRelatorioMediaDao.class, mov);
		EmissaoRelatorioDao emissaoDao = getDAO(EmissaoRelatorioDao.class, mov);

		// Remover c�lculos anteriores
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

		// Persiste as m�dias
		for(EmissaoRelatorioMedia erm: mapaMedias.values()){
			emissaoDao.createNoFlush(erm);
		}
		
		// Utiliza as m�dias para calcular o FPPI e atualiza o ranking
		for(EmissaoRelatorio emissao: ranking){
			EmissaoRelatorioMedia emissaoMedia = mapaMedias.get(emissao.getServidor().getUnidade().getGestora().getId());
			
			double ipi = emissao.getIpi();
			double ipiMedio = emissaoMedia.getIpiMedio();
			double ipiDesvPad = emissaoMedia.getIpiDesvpad();
			
			Double fppi = 0.0;
            try {
				fppi = 10.0 * normal.cumulativeProbability( (ipi - ipiMedio) / ipiDesvPad );
				
				System.out.println( " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " );
				System.out.println( " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " );
				
				System.out.println( "ipi: " + ipi );
				System.out.println( "ipiMedio:" + ipiMedio );
				System.out.println( " ipiDesvPad: " + ipiDesvPad );
				System.out.println( "prob: " +  normal.cumulativeProbability( (ipi - ipiMedio) / ipiDesvPad ));
				
				System.out.println( " FPPI CALCULADO: " + fppi );
				
				System.out.println( " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " );
				System.out.println( " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " );
			} catch (MathException e) {
	            e.printStackTrace();
	            throw new ArqException("Erro ao calcular a probabilidade cumulativa.");
	        }
			
			emissao.setFppi(truncate(fppi));
			emissao.setClassificacaoRelatorio(classificacao);
			emissaoDao.updateNoFlush(emissao);
		}

		mediaDao.close();
		emissaoDao.close();
		
		
		
		*/
		
		return null;
	}

	/**
	 * Calcula as estat�sicas de M�dia e Desvio Padr�o da lista de IPIs passada
	 * como argumento e coloca no mapa de m�dias.
	 * 
	 * @param classificacao
	 * @param mapaMedias
	 * @param mean
	 * @param stdDev
	 * @param idUnidade
	 * @param listaIpi
	 */
	/*private void calcularEstatisticas(ClassificacaoRelatorio classificacao,
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

	private double truncate(double value) {  
		return Math.round(value * 100) / 100d;  
	}  */
	
	/**
	 * M�todo auxiliar para transformar uma lista num array.
	 * @param listaIpi
	 * @return
	 */
	/*private double[] toArrayDouble(ArrayList<Double> listaIpi) {
		int tam = listaIpi.size();
		
		double[] array = new double[tam];
		Object[] arrayObject = listaIpi.toArray();
		
		for (int i = 0; i < arrayObject.length; i++) {
			array[i] = (Double) arrayObject[i];
		}
		
		return array;
	}*/

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
