/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/08/2008
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.EntidadesMarcadasParaExportacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 *    Processador que realiza a catalogação do título, realiza as validações e depois salva o título e seus 
 * campos na base.
 *
 * @author jadson
 * @since 18/08/2008
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorCatalogaTitulo extends AbstractProcessador{

	
	
	/**
	 * Ver comentário na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {	
		
		MovimentoCatalogaTitulo mov = (MovimentoCatalogaTitulo) movimento;
		TituloCatalograficoDao dao = getDAO(TituloCatalograficoDao.class, mov);
		
		try{
			
			TituloCatalografico titulo = mov.getTitulo();
			CatalogacaoUtil.configuraPosicaoCamposMarc(titulo); ////////// antes de fazer qualquer coisa no processador para se pular uma posição e não dá NullpointerException  //////////
			
			validate(mov);
	
			if(mov.isFinalizandoCatalogacao()){
				if(titulo.getCamposControle() != null)
				for (CampoControle c : titulo.getCamposControle()) {
					if(c.getEtiqueta().equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO)){
						
						SimpleDateFormat formatador = new SimpleDateFormat("yyMMdd");
						Date agora = new Date();	
					    //  yyMMdd                +     o resto do campo a partir da posição 06
						c.setDado( formatador.format(agora) +  c.getDado().substring(6, c.getDado().length()) );
					}		
				}
			
				
				CatalogacaoUtil.configuraDataHoraUltimaIntervencao( titulo );
				
			}
			
			// Retira os campos que por acaso o usuário adicionou, mas não digitou nada
			CatalogacaoUtil.retiraCamposDadosVazios(titulo);
			
			if(titulo.getCamposControle() == null || titulo.getCamposControle().size() == 0){
				throw new NegocioException("Para ser salvo o Título precisa possuir pelo menos um campo de controle");
			}
			if(titulo.getCamposDados() == null || titulo.getCamposDados().size() == 0){
				throw new NegocioException("Para ser salvo o Título precisa possuir pelo menos um campo de dados");
			}
			
			CatalogacaoUtil.ordenaCampoDados(titulo);
			
			titulo.setIdObraDigitalizada( salvaArquivoDigitalizadoNaBase(mov.getArquivoObraDigitalizada()));
			
			if(mov.isFinalizandoCatalogacao())
				titulo.setCatalogado(true);
			
			
			boolean atualizouTitulo = false;
			
			if(titulo.getId() == 0) {
				
				titulo.setNumeroDoSistema(  dao.getNextSeq("biblioteca", "numero_do_sistema_titulo_sequence") );
				
				
				ClassificacoesBibliograficasUtil.removeClassificacoesNaoInformadas(titulo, mov.getClassificacoesUtilizadas());
				
				CatalogacaoUtil.configuraPosicaoCamposMarc(titulo);   // Antes de salvar configura novamente para garantir se algum campo foi renovido
				dao.create(titulo);
				
				if(mov.isFinalizandoCatalogacao()) // só começa a entrar no histório quando é finalizado
					getDAO(TituloCatalograficoDao.class, movimento).registraAlteracaoTitulo(titulo, false);
				
				atualizouTitulo = false;
				
			}else{
				
				/////////////////////////////////////////////////////////////////////////////////////////
				// o título já foi salvo na base, o usuário já tinha salvo o título incompleto antes.  //
				/////////////////////////////////////////////////////////////////////////////////////////
				
				
				TituloCatalografico tituloBanco =  dao.findByPrimaryKey(titulo.getId(), TituloCatalografico.class);
				
				if(tituloBanco == null){
					throw new NegocioException("Você está tentando finalizar a catalogação do Título, mas não foi encontrado nehum Título salvo. Por favor, reinicie o processo."); // Esse erro não era para ocorrer
				}
				
				//Guarda os campos que por acaso foram removidos do título //
				List<CampoControle> camposControleParaRemocao = new ArrayList<CampoControle>();
				List<CampoDados> camposDadosParaRemocao = new ArrayList<CampoDados>();
				List<SubCampo> subCamposParaRemocao = new ArrayList<SubCampo>();
				
				verificaCamposRemovidosDoTitulo(titulo, tituloBanco, camposControleParaRemocao
						, camposDadosParaRemocao, subCamposParaRemocao);
				
				
				dao.detach(tituloBanco);
				
				ClassificacoesBibliograficasUtil.removeClassificacoesNaoInformadas(titulo, mov.getClassificacoesUtilizadas());
				
				
				CatalogacaoUtil.configuraPosicaoCamposMarc(titulo);   // Antes de salvar configura novamente para garantir se algum campo foi renovido
				dao.update(titulo);
				
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
				
				atualizouTitulo = true;
				
				if(mov.isFinalizandoCatalogacao()) // só começa a entrar no histório quando é finalizado
					getDAO(TituloCatalograficoDao.class, movimento).registraAlteracaoTitulo(titulo, true);
				
				/////////////////////////////////////////////////////////////////////////////////////////
				/////////////////////////////////////////////////////////////////////////////////////////
				/////////////////////////////////////////////////////////////////////////////////////////
				
			}
			
			
			// REALIZA A SINCRONIZACAO DAS BUSCAS DE TITULOS
			CatalogacaoUtil.sincronizaTituloCatalograficoCache(dao, titulo, atualizouTitulo, mov.getClassificacoesUtilizadas());
				
			
			
			// Para não gerar o histórico toda vez que o usuário salvar apenas se finalizar o título.
			// e só marca para exportação se finalizar a catalogação
			if(mov.isFinalizandoCatalogacao()){
				dao.create(new EntidadesMarcadasParaExportacao(titulo, (Usuario) mov.getUsuarioLogado()));
			}		
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}
	
	
	
	
	/**
	 * Ver comentário na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento movimento) throws NegocioException, ArqException {

		MovimentoCatalogaTitulo mov = (MovimentoCatalogaTitulo) movimento;
		
		// Administradores gerais e quem possui papel de bibliotecário de catalogação pode alterar as catalogaçãoes, independente da biblioteca alocada
		checkRole(new int[]{SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}, mov);
		
		UploadedFile arquivoObraDigitalizada = mov.getArquivoObraDigitalizada();
		
		ListaMensagens lista = new ListaMensagens();
		
		if( arquivoObraDigitalizada != null ) {
			
			try{
				CatalogacaoUtil.validaFormatoArquivoCatalogacao(arquivoObraDigitalizada);
			}catch(NegocioException ne){
				lista.addAll(ne.getListaMensagens());
			}
		}
		
		CatalogacaoValidatorFactory.getRegrasValidacao().validaCamposMarcTitulo(mov.getTitulo(), mov.getTitulo().getFormatoMaterial(), lista);
		checkValidation(lista);
		
		if(mov.isFinalizandoCatalogacao()){
			CatalogacaoValidatorFactory.getRegrasValidacao().verificaCamposObrigatoriosTitulo(mov.getTitulo(), mov.getClassificacoesUtilizadas(), lista);
			checkValidation(lista);
		
			// Se vai finalizar um título   //
			// OBSERVAÇÃO.: Não pode verificar quando salva porque os título importados são salvo diretamente sem validação
			
			CatalogacaoValidatorFactory.getRegrasValidacao().verificaExisteTituloIgual(mov.getTitulo(), lista);
			checkValidation(lista);
		}
		
	}
	
	
	
	
	/**
	 * Salva o arquivo .pdf do título caso o usuário tenha submetido um.
	 */
	private Integer salvaArquivoDigitalizadoNaBase(UploadedFile arquivoObraDigitalizada) throws ArqException{
		
		if( arquivoObraDigitalizada != null){
		
			Integer idObraDigitalizada = null;
			
			try {
				
				idObraDigitalizada = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idObraDigitalizada, 
						arquivoObraDigitalizada.getBytes(),
						arquivoObraDigitalizada.getContentType(),
						arquivoObraDigitalizada.getName());
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new ArqException(e);
			} 
			
			return idObraDigitalizada;
		
		}else{
			return null;  // valor padrão para titulos que não tem títulos digitalizados
		}
	}
	

	
	
	
	
	
	
	
	/**
	 *   É preciso verificar quais campos o usuário removeu do título que ele está tentando atualizar
	 * em relação aos campos que estavam no banco. Porque apenas atualizando o título esses campos 
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
		if(tituloBanco.getCamposControle() != null)
		for (CampoControle campo : tituloBanco.getCamposControle()) {
			
			/* Teste: se o campo de controle que está no banco não existe mais é porque
			 * o usuário removeu então apaga ele
			 */
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
					
					// sub campo que está no banco foi apagado pelo usuário
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
	 * o método "contains"das coleções de java
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
	 * Teste se um campos de dados do título no banco existe no título que o usuário está manipulando
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
	 * Percorre a lista de subcampos passada para saber se o sub campo existe ou não
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
