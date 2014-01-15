/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on  29/04/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.AutoridadeDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 *
 *   Processador que contém as regras para atualizar uma autoridade.
 *
 * @author jadson
 * @since 29/04/2009
 * @version 1.0 criação da classe
 *
 */
public class ProcessadorAtualizaAutoridade extends AbstractProcessador{

	
	/**
	 * Ver comentário na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		
		MovimentoAtualizaAutoridade movimento = (MovimentoAtualizaAutoridade) mov;
	
		Autoridade autoridadeMemoria = movimento.getAutoridade();
		CatalogacaoUtil.configuraPosicaoCamposMarc(autoridadeMemoria);  ////////// antes de fazer qualquer coisa no processador para se pular uma posição e não dá NullpointerException  //////////
		
		AutoridadeDao dao = null;
		
		try{
		
			dao = getDAO(AutoridadeDao.class, mov);
			
			Autoridade autoridadeBanco =  dao.findByPrimaryKey(autoridadeMemoria.getId(), Autoridade.class);
		
	
			//Guarda os campos que por acaso foram removidos do título //
			List<CampoControle> camposControleParaRemocao = new ArrayList<CampoControle>();
			List<CampoDados> camposDadosParaRemocao = new ArrayList<CampoDados>();
			List<SubCampo> subCamposParaRemocao = new ArrayList<SubCampo>();
			
			
			verificaCamposRemovidosDaAutoridade(autoridadeMemoria, autoridadeBanco, camposControleParaRemocao
					, camposDadosParaRemocao, subCamposParaRemocao);
			
			
			CatalogacaoUtil.configuraDataHoraUltimaIntervencao( autoridadeMemoria );
			
			validate(mov);
			
			CatalogacaoUtil.retiraCamposDadosVazios(autoridadeMemoria);
			
			if(autoridadeMemoria.getCamposControle() == null || autoridadeMemoria.getCamposControle().size() == 0){
				throw new NegocioException("Para ser salva a Autoridade precisa possuir pelo menos um campo de controle");
			}
			if(autoridadeMemoria.getCamposDados() == null || autoridadeMemoria.getCamposDados().size() == 0){
				throw new NegocioException("Para ser salva a Autoridade precisa possuir pelo menos um campo de dados");
			}
			
			
			if(autoridadeMemoria.contemEntraPrincipalAutor())
				autoridadeMemoria.setTipo(Autoridade.TIPO_AUTOR);
			
			if(autoridadeMemoria.contemEntraPrincipalAssunto())
				autoridadeMemoria.setTipo(Autoridade.TIPO_ASSUNTO);
			
			
			dao.detach(autoridadeBanco);
			
			CatalogacaoUtil.ordenaCampoDados(autoridadeMemoria);
			
			
			CatalogacaoUtil.configuraPosicaoCamposMarc(autoridadeMemoria);  // Antes de salvar configura novamente para garantir se algum campo foi renovido
			
			dao.update(autoridadeMemoria); 
		
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
			
			
			/* ************************************************************************************
			 * Na atualização de uma autoridade não se marca para a exportação, visto que não é para ser
			 * possível atualizar diretamente as autoridades importadas no sistema e depois exportar.
			 * Teria que baixar a Autoridade novamente do Catálogo coletivo, importar para o sistema mais 
			 * não criar uma nova Autoridade, apenas atualizar.
			 * Nesse momento que deveria ser marcado para exportação.
			 * Serve para manter o histórico de alterações por outras bibliotecas.
			 *
			 * Pelo os que as bibliotecária passaram, elas ainda não fazem isso, e o sistema 
			 * também não suporta essa operação até o momento.
			 *************************************************************************************/
			
			
			// REALIZA A SINCRONIZACAO DAS BUSCAS DE TITULOS
			CatalogacaoUtil.sincronizaAutoridadeCache(dao, autoridadeMemoria, true);
			
			
			// ATUALIZA TODOS OS DADOS DOS TITULOS QUE APONTAVAM PARA ESSA AUTORIDADE
			// OBS.: Tem que ser chamado depois de criar os objetos cache.
			CatalogacaoUtil.atualizaDadosTituloAutoridade(dao, autoridadeMemoria, movimento.getClassificacoesUtilizadas());
			
			
			if(! movimento.isAlteracaoAutomatica())
				getDAO(AutoridadeDao.class, movimento).registraAlteracaoAutoridade(autoridadeMemoria, true);
			
		
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
	public void validate(Movimento mov) throws NegocioException, ArqException {
	
		MovimentoAtualizaAutoridade movimento = (MovimentoAtualizaAutoridade) mov;
		
		// Administradores gerais e quem possui papel de bibliotecário de catalogação pode alterar as catalogaçãoes, independente da biblioteca alocada
		checkRole(new int[]{SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}, mov);
		
		ListaMensagens lista = new ListaMensagens();
	
		CatalogacaoValidatorFactory.getRegrasValidacao().validaCamposMarcAutoridade(movimento.getAutoridade(), lista);
		checkValidation(lista);
		
		CatalogacaoValidatorFactory.getRegrasValidacao().verificaCamposObrigatoriosAutoridade(movimento.getAutoridade(), lista);
		checkValidation(lista);
		
	}
	
	
	
	
	/**
	 *   É preciso verificar quais campos o usuário removeu da autoridade que ele está tentando atualizar
	 * em relação aos campos que estavam no banco. Porque apenas atualizando a autoridade esses campos 
	 * não são removidos. É preciso remover manualmente. 
	 */
	private void verificaCamposRemovidosDaAutoridade(Autoridade autoridadeMemoria, Autoridade autoridadeBanco
			, List<CampoControle> camposControleParaRemocao
			, List<CampoDados> camposDadosParaRemocao
			,List<SubCampo> subCamposParaRemocao){
		
		
		// atualiza campos de controle removidos
		if(autoridadeBanco.getCamposControle() != null && autoridadeMemoria.getCamposControle() != null)
		for (CampoControle campo : autoridadeBanco.getCamposControle()) {
			
			if(! campoControleExiste(autoridadeMemoria.getCamposControle(), campo)){
				camposControleParaRemocao.add(campo);
			}
		}
		
		// atualiza campos de dados e sub campos removidos
		if(autoridadeBanco.getCamposDados() != null && autoridadeMemoria.getCamposDados() != null)
		for (CampoDados campoBanco : autoridadeBanco.getCamposDados()) {
			
			// campo não foi apagado pelo usuário
			if(possuiCorrespondente(autoridadeMemoria.getCamposDados(), campoBanco) ){
				
				// verifica agora os sub campos
				
				CampoDados campoMemoria = getCorrespondente(autoridadeMemoria.getCamposDados(), campoBanco);
				
			
				for (SubCampo subCampoBanco : campoBanco.getSubCampos()) {
					
					// sub campo que está no banco foi apagado pelo usuário
					if(! possuiSubCampoCorrespondente(campoMemoria.getSubCampos(), subCampoBanco) ){
						subCamposParaRemocao.add(subCampoBanco);
					}
				}
				
				
			}else{ // usuário apagou o campo
				
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
