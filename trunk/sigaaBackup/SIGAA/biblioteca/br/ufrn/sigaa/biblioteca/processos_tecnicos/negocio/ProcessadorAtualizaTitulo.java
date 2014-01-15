/*
 * ProcessadorAtualizaTitulo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 *
 *    Processador para atualizar os dados de um título na base.
 *
 * @author jadson
 * @since 27/03/2009
 * @version 1.0 criação da classe
 *
 */
public class ProcessadorAtualizaTitulo extends AbstractProcessador{


	/**
	 * 
	 * Ver comentário na classe pai.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoAtualizaTitulo mov = (MovimentoAtualizaTitulo) movimento;
	
		TituloCatalograficoDao dao = null;
		
		try{

			dao = getDAO(TituloCatalograficoDao.class, mov);

			TituloCatalografico tituloMemoria = mov.getTitulo();
			
			if(tituloMemoria == null || tituloMemoria.getId() <= 0){
				throw new NegocioException("Não foi possível atualizar as informações do Título pois seus dados foram corrompidos durante o processo de atualização, por favor reinicie o processo.");
			}
			
			CatalogacaoUtil.configuraPosicaoCamposMarc(tituloMemoria);   ////////// antes de fazer qualquer coisa no processador para se pular uma posição não dá NullpointerException  //////////
			
			TituloCatalografico tituloBanco =  dao.findByPrimaryKey(tituloMemoria.getId(), TituloCatalografico.class);
		
	
			///////////////////   Retira os campos que o usuário deixou vazio na tela  //////////////////
			
			CatalogacaoUtil.retiraCamposDadosVazios(tituloMemoria);
			
			if(tituloMemoria.getCamposControle() == null || tituloMemoria.getCamposControle().size() == 0){
				throw new NegocioException("Para ser salvo o Título precisa possuir pelo menos um campo de controle");
			}
			if(tituloMemoria.getCamposDados() == null || tituloMemoria.getCamposDados().size() == 0){
				throw new NegocioException("Para ser salvo o Título precisa possuir pelo menos um campo de dados");
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////////
			
			
			
			//////////   Guarda os campos que por acaso foram removidos do título ///////////////
			/////////////////  (chamado depois de remover os campos vazios) /////////////////////
			List<CampoControle> camposControleParaRemocao = new ArrayList<CampoControle>();
			List<CampoDados> camposDadosParaRemocao = new ArrayList<CampoDados>();
			List<SubCampo> subCamposParaRemocao = new ArrayList<SubCampo>();
			
			verificaCamposRemovidosDoTitulo(tituloMemoria, tituloBanco, camposControleParaRemocao
					, camposDadosParaRemocao, subCamposParaRemocao);
			
			//////////////////////////////////////////////////////////////////////////////////////
			
			
			
			CatalogacaoUtil.configuraDataHoraUltimaIntervencao( tituloMemoria );
						
			
			validate(mov);
			
			UploadedFile arquivoObraDigitalizada = mov.getArquivoObraDigitalizada();
			
			if(arquivoObraDigitalizada != null ){ // usuário passou um arquivo para subistituir o anterior
				
				tituloMemoria.setIdObraDigitalizada( atualizarObraDigitalizadoDotitulo(arquivoObraDigitalizada, 
														tituloMemoria.getIdObraDigitalizada()));
				
			}else{ // se o usuário não submeteu um novo arquivo
				
				if(mov.isApagarArquivoDigitalSalvo()) {   // mas apagou o anterior
					apagarObraDigitalizadaSalva(tituloMemoria.getIdObraDigitalizada());
					tituloMemoria.setIdObraDigitalizada(null);
				}	
			}
			
			
			dao.detach(tituloBanco);
			
			CatalogacaoUtil.ordenaCampoDados(tituloMemoria);
			
			ClassificacoesBibliograficasUtil.removeClassificacoesNaoInformadas(tituloMemoria, mov.getClassificacoesUtilizadas());
			
			CatalogacaoUtil.configuraPosicaoCamposMarc(tituloMemoria);   // Antes de salvar configura novamente para garantir se algum campo foi renovido
			
			dao.update(tituloMemoria); 
			
			
			
			for (CampoControle controle : camposControleParaRemocao) {
				if(controle == null)
					continue;
				
				dao.remove(controle);
			}
			
			for (CampoDados dado : camposDadosParaRemocao) {
				
				if(dado == null)
					continue;
				
				dao.remove(dado);
			}
			
			for (SubCampo subCampo : subCamposParaRemocao) {
				

				if(subCampo == null)
					continue;
				
				dao.remove(subCampo);
			}
			
		
			
			
			// REALIZA A SINCRONIZACAO DAS BUSCAS DE TITULOS
			CatalogacaoUtil.sincronizaTituloCatalograficoCache(dao, tituloMemoria, true, mov.getClassificacoesUtilizadas());
			
			if(! mov.isAlteracaoAutomatica())
				getDAO(TituloCatalograficoDao.class, movimento).registraAlteracaoTitulo(tituloMemoria, true);
		
			
		}finally{
			if(dao != null) dao.close();
		}
		
		
		return null;
	}

	
	
	/**
	 * Ver comentário na classe pai.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoAtualizaTitulo movimento = (MovimentoAtualizaTitulo) mov;
		
		// Administradores gerais e quem possui papel de bibliotecário de catalogação pode alterar as catalogaçãoes, independente da biblioteca alocada
		checkRole(new int[]{SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}, mov);
		
		UploadedFile arquivoObraDigitalizada = movimento.getArquivoObraDigitalizada();
		
		ListaMensagens lista = new ListaMensagens();
	
		if( arquivoObraDigitalizada != null ){
			try{
				CatalogacaoUtil.validaFormatoArquivoCatalogacao(arquivoObraDigitalizada);
			}catch(NegocioException ne){
				lista.addAll(ne.getListaMensagens());
			}
		}
			
		CatalogacaoValidatorFactory.getRegrasValidacao().validaCamposMarcTitulo(movimento.getTitulo(), movimento.getTitulo().getFormatoMaterial(), lista);
		checkValidation(lista);
		
		CatalogacaoValidatorFactory.getRegrasValidacao().verificaCamposObrigatoriosTitulo(movimento.getTitulo(), movimento.getClassificacoesUtilizadas(), lista);
		checkValidation(lista);
		
		CatalogacaoValidatorFactory.getRegrasValidacao().verificaExisteTituloIgual(movimento.getTitulo(), lista);
		checkValidation(lista);
		
	}

	
	
	/**
	 * Remove o arquivo da obra atualizada anterior e cria um novo.
	 */
	private Integer atualizarObraDigitalizadoDotitulo(UploadedFile arquivoObraDigitalizada, Integer idObraDigitalizadaAntiga) throws ArqException{
		
			Integer idObraDigitalizadaNova = null;
			
			try {
				
				// O usuário tinha inserido um arquivo na catalogação, então apaga o antigo antes
				if(idObraDigitalizadaAntiga != null){
					EnvioArquivoHelper.removeArquivo(idObraDigitalizadaAntiga);
				}
				
				// insere o novo arquivo que pode está substituindo um antigo ou não.
				
				idObraDigitalizadaNova = EnvioArquivoHelper.getNextIdArquivo();
				
				EnvioArquivoHelper.inserirArquivo(idObraDigitalizadaNova, 
						arquivoObraDigitalizada.getBytes(),
						arquivoObraDigitalizada.getContentType(),
						arquivoObraDigitalizada.getName());
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new ArqException(e);
			} 
			
			return idObraDigitalizadaNova;
		
	}
	
	
	/**
	 * Remove a obra que pertencia ao título.
	 */
	private void apagarObraDigitalizadaSalva(Integer idObraDigitalizadaAntiga)throws ArqException{
		try {
			
			if(idObraDigitalizadaAntiga != null){
				EnvioArquivoHelper.removeArquivo(idObraDigitalizadaAntiga);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} 
	}
	
	
	
	/**
	 *   É preciso verificar quais campos o usuário removeu do título que ele está tentando atualizar
	 * em relação aos campos que estavam no banco. Porque apenas atualizando o título, esses campos 
	 * não são removidos. É preciso remover manualmente. 
	 * 
	 * Também é preciso verificar aqueles campos que possuem autoridade para 
	 * 
	 */
	private void verificaCamposRemovidosDoTitulo(TituloCatalografico tituloMemoria, TituloCatalografico tituloBanco
			, List<CampoControle> camposControleParaRemocao
			, List<CampoDados> camposDadosParaRemocao
			,List<SubCampo> subCamposParaRemocao){
		
		
		// atualiza campos de controle removidos
		if(tituloBanco.getCamposControle() != null && tituloMemoria.getCamposControle() != null)
		for (CampoControle campo : tituloBanco.getCamposControle()) {
			
			// Teste: se o campo de controle que esta no banco não existe mais é porque
			// o usuário removeu então apague o.
			if(! campoControleExiste(tituloMemoria.getCamposControle(), campo)){
				camposControleParaRemocao.add(campo);
			}
		}
		
		// atualiza campos de dados e sub campos removidos
		
		if(tituloBanco.getCamposDados() != null && tituloMemoria.getCamposDados() != null)
		for (CampoDados campoBanco : tituloBanco.getCamposDados()) {
			
			// campo não foi apagado pelo usuário
			if(possuiCorrespondente(tituloMemoria.getCamposDados(), campoBanco) ){
				
				// verifica agora os sub campos
				
				CampoDados campoMemoria = getCorrespondente(tituloMemoria.getCamposDados(), campoBanco);
				
			
				for (SubCampo subCampoBanco : campoBanco.getSubCampos()) {
					
					// sub campo que esta no banco foi apagado pelo usuário
					if(! possuiSubCampoCorrespondente(campoMemoria.getSubCampos(), subCampoBanco) ){
						subCamposParaRemocao.add(subCampoBanco);
					}
				}
				
				
			}else{ // Usuário apagou o campo
				
				camposDadosParaRemocao.add(campoBanco);
			}
			
		}
		
	}
	
	

	/**
	 * Um método que busca na lista se um campo de controle existe porque não dava para usar 
	 * o método "contains" das coleções de java
	 * 
	 */
	private boolean campoControleExiste(List<CampoControle> lista, CampoControle campo){
		
		for (CampoControle campoControle : lista) {
			if( campoControle.getId() == campo.getId()){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Testa se um campos de dados do título no banco existe no título que o usuário está manipulando
	 * Isso para saber se ele foi removido ou não pelo usuário
	 */
	private boolean possuiCorrespondente(List<CampoDados> camposDadosMemoria, CampoDados campoBanco){
		
		for (CampoDados campoMemoria : camposDadosMemoria) {
			
			if(campoMemoria == null || campoBanco == null)
				continue;
			
			if (campoMemoria.getId() == campoBanco.getId()){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Pega o campo correspondente em memória ao campo que está no banco
	 */
	private CampoDados getCorrespondente(List<CampoDados> camposDadosMemoria, CampoDados campoBanco){
		
		for (CampoDados campoMemoria : camposDadosMemoria) {
			if (campoMemoria.getId() == campoBanco.getId()){
				return campoMemoria;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Percorre a lista de subcampos passada para saber se o subcampo existe ou não
	 */
	private boolean possuiSubCampoCorrespondente(List<SubCampo> subCamposMemoria, SubCampo subCampoBanco){
		
		for (SubCampo subMemoria : subCamposMemoria) {
			
			if(subMemoria == null || subCampoBanco == null)
				continue;
			
			if( subMemoria.getId() == subCampoBanco.getId()){
				return true;
			}
		}
		
		return false;
	}
	
	
	
}
