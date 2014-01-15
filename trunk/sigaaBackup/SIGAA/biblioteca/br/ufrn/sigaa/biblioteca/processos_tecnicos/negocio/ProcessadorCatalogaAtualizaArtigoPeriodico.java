/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 05/05/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.biblioteca.ArtigoDePeriodicoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 *
 *     Cria um artigo de periódico na base. É preciso salvar o título, salvar o artigo e
 *  atualiza o fascículo do artigo. Como era preciso realizar várias ações não dava para usar o
 *  processador de cadastro.
 *
 * @author jadson
 * @since 05/05/2009
 * @version 1.0 criação da classe
 *
 */
public class ProcessadorCatalogaAtualizaArtigoPeriodico extends AbstractProcessador{

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	
		MovimentoCatalogaAtualizaArtigoPeriodico movimento = (MovimentoCatalogaAtualizaArtigoPeriodico) mov;
		
		TituloCatalograficoDao dao =  null;
		
		ArtigoDePeriodico artigo = movimento.getArtigo();
		
		try{
			
			// cria o dao que vai ser usado no outro processador também
			dao = getDAO(TituloCatalograficoDao.class, movimento);
			
			// se está atualizando ou o usuário clicou em salvar mais de um vez.
			if( movimento.isAtualizando() ||  artigo.getId() != 0){
				
				ArtigoDePeriodico artigoBanco = dao.findByPrimaryKey(artigo.getId(), ArtigoDePeriodico.class);
				
				CatalogacaoUtil.configuraDataHoraUltimaIntervencao( artigo );
				
				//Guarda os campos que por acaso foram removidos do título //
				List<CampoControle> camposControleParaRemocao = new ArrayList<CampoControle>();
				List<CampoDados> camposDadosParaRemocao = new ArrayList<CampoDados>();
				
				// atualiza as informações do artigo
				removeCampoAntigos(artigoBanco, camposControleParaRemocao, camposDadosParaRemocao);
				
				dao.detach(artigoBanco);
				
				CatalogacaoUtil.configuraPosicaoCamposMarc(artigo);
				dao.update(artigo); //atualiza o artigo
			    
				for (CampoControle controle : camposControleParaRemocao) {
					dao.remove(controle);
				}
				
				for (CampoDados dado : camposDadosParaRemocao) {
					dao.remove(dado);
				}
				
				// sincroniza com o cache
				CatalogacaoUtil.sincronizaArtigoDePeriodicoCache(dao, artigo, true);
				
				getDAO(ArtigoDePeriodicoDao.class, movimento).registraAlteracaoArtigo(artigo, true);
				
			}else{
				
				artigo.setNumeroDoSistema( dao.getNextSeq("biblioteca", "numero_do_sistema_artigo_sequence") );
				
				CatalogacaoUtil.configuraDataHoraUltimaIntervencao( artigo );
				
				CatalogacaoUtil.configuraPosicaoCamposMarc(artigo);
				dao.create(artigo);
				dao.update(artigo.getFasciculo()); // faz o fascículo "apontar" para o artigo
				
				// sincroniza com o cache
				CatalogacaoUtil.sincronizaArtigoDePeriodicoCache(dao, artigo, false);
				
				getDAO(ArtigoDePeriodicoDao.class, movimento).registraAlteracaoArtigo(artigo, false);
				
			}
			
		}finally{
			if(dao !=  null ) dao.close();
		}
		
		
		return artigo;
	}

	/*
	 * Atualiza as informações que o usuário digitou na tela
	 */
	private void removeCampoAntigos(ArtigoDePeriodico artigoBanco
			, List<CampoControle> camposControleParaRemocao
			, List<CampoDados> camposDadosParaRemocao){
		
		if(artigoBanco.getCamposControle() != null)
		for (CampoControle campo : artigoBanco.getCamposControle()) {
			camposControleParaRemocao.add(campo);
		}
		
		if(artigoBanco.getCamposDados() != null)
		for (CampoDados campo : artigoBanco.getCamposDados()) {
			camposDadosParaRemocao.add(campo);
		}
		
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

	
}
