/*
 * ProcessadorAtualizaTitulo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
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
 *    Processador para atualizar os dados de um t�tulo na base.
 *
 * @author jadson
 * @since 27/03/2009
 * @version 1.0 cria��o da classe
 *
 */
public class ProcessadorAtualizaTitulo extends AbstractProcessador{


	/**
	 * 
	 * Ver coment�rio na classe pai.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoAtualizaTitulo mov = (MovimentoAtualizaTitulo) movimento;
	
		TituloCatalograficoDao dao = null;
		
		try{

			dao = getDAO(TituloCatalograficoDao.class, mov);

			TituloCatalografico tituloMemoria = mov.getTitulo();
			
			if(tituloMemoria == null || tituloMemoria.getId() <= 0){
				throw new NegocioException("N�o foi poss�vel atualizar as informa��es do T�tulo pois seus dados foram corrompidos durante o processo de atualiza��o, por favor reinicie o processo.");
			}
			
			CatalogacaoUtil.configuraPosicaoCamposMarc(tituloMemoria);   ////////// antes de fazer qualquer coisa no processador para se pular uma posi��o n�o d� NullpointerException  //////////
			
			TituloCatalografico tituloBanco =  dao.findByPrimaryKey(tituloMemoria.getId(), TituloCatalografico.class);
		
	
			///////////////////   Retira os campos que o usu�rio deixou vazio na tela  //////////////////
			
			CatalogacaoUtil.retiraCamposDadosVazios(tituloMemoria);
			
			if(tituloMemoria.getCamposControle() == null || tituloMemoria.getCamposControle().size() == 0){
				throw new NegocioException("Para ser salvo o T�tulo precisa possuir pelo menos um campo de controle");
			}
			if(tituloMemoria.getCamposDados() == null || tituloMemoria.getCamposDados().size() == 0){
				throw new NegocioException("Para ser salvo o T�tulo precisa possuir pelo menos um campo de dados");
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////////
			
			
			
			//////////   Guarda os campos que por acaso foram removidos do t�tulo ///////////////
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
			
			if(arquivoObraDigitalizada != null ){ // usu�rio passou um arquivo para subistituir o anterior
				
				tituloMemoria.setIdObraDigitalizada( atualizarObraDigitalizadoDotitulo(arquivoObraDigitalizada, 
														tituloMemoria.getIdObraDigitalizada()));
				
			}else{ // se o usu�rio n�o submeteu um novo arquivo
				
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
	 * Ver coment�rio na classe pai.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoAtualizaTitulo movimento = (MovimentoAtualizaTitulo) mov;
		
		// Administradores gerais e quem possui papel de bibliotec�rio de cataloga��o pode alterar as cataloga��oes, independente da biblioteca alocada
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
				
				// O usu�rio tinha inserido um arquivo na cataloga��o, ent�o apaga o antigo antes
				if(idObraDigitalizadaAntiga != null){
					EnvioArquivoHelper.removeArquivo(idObraDigitalizadaAntiga);
				}
				
				// insere o novo arquivo que pode est� substituindo um antigo ou n�o.
				
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
	 * Remove a obra que pertencia ao t�tulo.
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
	 *   � preciso verificar quais campos o usu�rio removeu do t�tulo que ele est� tentando atualizar
	 * em rela��o aos campos que estavam no banco. Porque apenas atualizando o t�tulo, esses campos 
	 * n�o s�o removidos. � preciso remover manualmente. 
	 * 
	 * Tamb�m � preciso verificar aqueles campos que possuem autoridade para 
	 * 
	 */
	private void verificaCamposRemovidosDoTitulo(TituloCatalografico tituloMemoria, TituloCatalografico tituloBanco
			, List<CampoControle> camposControleParaRemocao
			, List<CampoDados> camposDadosParaRemocao
			,List<SubCampo> subCamposParaRemocao){
		
		
		// atualiza campos de controle removidos
		if(tituloBanco.getCamposControle() != null && tituloMemoria.getCamposControle() != null)
		for (CampoControle campo : tituloBanco.getCamposControle()) {
			
			// Teste: se o campo de controle que esta no banco n�o existe mais � porque
			// o usu�rio removeu ent�o apague o.
			if(! campoControleExiste(tituloMemoria.getCamposControle(), campo)){
				camposControleParaRemocao.add(campo);
			}
		}
		
		// atualiza campos de dados e sub campos removidos
		
		if(tituloBanco.getCamposDados() != null && tituloMemoria.getCamposDados() != null)
		for (CampoDados campoBanco : tituloBanco.getCamposDados()) {
			
			// campo n�o foi apagado pelo usu�rio
			if(possuiCorrespondente(tituloMemoria.getCamposDados(), campoBanco) ){
				
				// verifica agora os sub campos
				
				CampoDados campoMemoria = getCorrespondente(tituloMemoria.getCamposDados(), campoBanco);
				
			
				for (SubCampo subCampoBanco : campoBanco.getSubCampos()) {
					
					// sub campo que esta no banco foi apagado pelo usu�rio
					if(! possuiSubCampoCorrespondente(campoMemoria.getSubCampos(), subCampoBanco) ){
						subCamposParaRemocao.add(subCampoBanco);
					}
				}
				
				
			}else{ // Usu�rio apagou o campo
				
				camposDadosParaRemocao.add(campoBanco);
			}
			
		}
		
	}
	
	

	/**
	 * Um m�todo que busca na lista se um campo de controle existe porque n�o dava para usar 
	 * o m�todo "contains" das cole��es de java
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
	 * Testa se um campos de dados do t�tulo no banco existe no t�tulo que o usu�rio est� manipulando
	 * Isso para saber se ele foi removido ou n�o pelo usu�rio
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
	 * Pega o campo correspondente em mem�ria ao campo que est� no banco
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
	 * Percorre a lista de subcampos passada para saber se o subcampo existe ou n�o
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
