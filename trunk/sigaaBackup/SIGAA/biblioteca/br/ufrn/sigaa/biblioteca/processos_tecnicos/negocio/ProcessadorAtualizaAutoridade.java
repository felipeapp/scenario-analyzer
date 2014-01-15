/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 *   Processador que cont�m as regras para atualizar uma autoridade.
 *
 * @author jadson
 * @since 29/04/2009
 * @version 1.0 cria��o da classe
 *
 */
public class ProcessadorAtualizaAutoridade extends AbstractProcessador{

	
	/**
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		
		MovimentoAtualizaAutoridade movimento = (MovimentoAtualizaAutoridade) mov;
	
		Autoridade autoridadeMemoria = movimento.getAutoridade();
		CatalogacaoUtil.configuraPosicaoCamposMarc(autoridadeMemoria);  ////////// antes de fazer qualquer coisa no processador para se pular uma posi��o e n�o d� NullpointerException  //////////
		
		AutoridadeDao dao = null;
		
		try{
		
			dao = getDAO(AutoridadeDao.class, mov);
			
			Autoridade autoridadeBanco =  dao.findByPrimaryKey(autoridadeMemoria.getId(), Autoridade.class);
		
	
			//Guarda os campos que por acaso foram removidos do t�tulo //
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
			 * Na atualiza��o de uma autoridade n�o se marca para a exporta��o, visto que n�o � para ser
			 * poss�vel atualizar diretamente as autoridades importadas no sistema e depois exportar.
			 * Teria que baixar a Autoridade novamente do Cat�logo coletivo, importar para o sistema mais 
			 * n�o criar uma nova Autoridade, apenas atualizar.
			 * Nesse momento que deveria ser marcado para exporta��o.
			 * Serve para manter o hist�rico de altera��es por outras bibliotecas.
			 *
			 * Pelo os que as bibliotec�ria passaram, elas ainda n�o fazem isso, e o sistema 
			 * tamb�m n�o suporta essa opera��o at� o momento.
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
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
	
		MovimentoAtualizaAutoridade movimento = (MovimentoAtualizaAutoridade) mov;
		
		// Administradores gerais e quem possui papel de bibliotec�rio de cataloga��o pode alterar as cataloga��oes, independente da biblioteca alocada
		checkRole(new int[]{SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}, mov);
		
		ListaMensagens lista = new ListaMensagens();
	
		CatalogacaoValidatorFactory.getRegrasValidacao().validaCamposMarcAutoridade(movimento.getAutoridade(), lista);
		checkValidation(lista);
		
		CatalogacaoValidatorFactory.getRegrasValidacao().verificaCamposObrigatoriosAutoridade(movimento.getAutoridade(), lista);
		checkValidation(lista);
		
	}
	
	
	
	
	/**
	 *   � preciso verificar quais campos o usu�rio removeu da autoridade que ele est� tentando atualizar
	 * em rela��o aos campos que estavam no banco. Porque apenas atualizando a autoridade esses campos 
	 * n�o s�o removidos. � preciso remover manualmente. 
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
			
			// campo n�o foi apagado pelo usu�rio
			if(possuiCorrespondente(autoridadeMemoria.getCamposDados(), campoBanco) ){
				
				// verifica agora os sub campos
				
				CampoDados campoMemoria = getCorrespondente(autoridadeMemoria.getCamposDados(), campoBanco);
				
			
				for (SubCampo subCampoBanco : campoBanco.getSubCampos()) {
					
					// sub campo que est� no banco foi apagado pelo usu�rio
					if(! possuiSubCampoCorrespondente(campoMemoria.getSubCampos(), subCampoBanco) ){
						subCamposParaRemocao.add(subCampoBanco);
					}
				}
				
				
			}else{ // usu�rio apagou o campo
				
				camposDadosParaRemocao.add(campoBanco);
			}
			
		}
		
	}
	
	

	/**
	 * Um m�todo que busca na lista se um campo de controle existe porque n�o dava para usar 
	 * o m�todo "contains"das cole��es de java
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
	 * Teste se um campos de dados do t�tulo no banco existe no t�tulo que o usu�rio est� manipulando
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
	 * Percorre a lista de subcampos passada para saber se o sub campo existe ou n�o
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
