/*
 * ProcessadorCatalogaAutoridade.java
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.EntidadesMarcadasParaExportacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 *     Cria uma nova autoridade no banco. No processador a �nica coisa adicional at� agora � que
 * precisa validar dos dados MARC, igual os que se faz com os t�tulos.
 *
 * @author jadson
 * @since 29/04/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorCatalogaAutoridade extends AbstractProcessador{

	/**
	 * ver coment�rios na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoCatalogaAutoridade movimento = (MovimentoCatalogaAutoridade) mov;
		
		Autoridade autoridade = movimento.getAutoridade();
		CatalogacaoUtil.configuraPosicaoCamposMarc(autoridade);  ////////// antes de fazer qualquer coisa no processador para se pular uma posi��o e n�o d� NullpointerException  //////////
		
		validate(mov);
		
		AutoridadeDao dao = getDAO(AutoridadeDao.class, movimento);
		
		if(movimento.isFinalizandoCatalogacao()){
			if( movimento.getAutoridade().getCamposControle() != null)
			for (CampoControle c : movimento.getAutoridade().getCamposControle()) {
				if(c.getEtiqueta().equals(Etiqueta.CAMPO_008_AUTORIDADE)){
					
					SimpleDateFormat formatador = new SimpleDateFormat("yyMMdd");
					Date agora = new Date();	
				    //  yyMMdd                +     o resto do campo a partir da posi��o 06
					c.setDado( formatador.format(agora) +  c.getDado().substring(6, c.getDado().length()) );
				}
			}
		}
		
		
		CatalogacaoUtil.configuraDataHoraUltimaIntervencao( autoridade );
		
		
		// Retira os campos que por acaso o usu�rio adicionou, mas n�o digitou nada
		CatalogacaoUtil.retiraCamposDadosVazios(autoridade);
		
		if(autoridade.getCamposControle() == null || autoridade.getCamposControle().size() == 0){
			throw new NegocioException("Para ser salva a Autoridade precisa possuir pelo menos um campo de controle");
		}
		if(autoridade.getCamposDados() == null || autoridade.getCamposDados().size() == 0){
			throw new NegocioException("Para ser salva a Autoridade precisa possuir pelo menos um campo de dados");
		}
		
		CatalogacaoUtil.ordenaCampoDados(autoridade);
		
		try{
		
			if(autoridade.contemEntraPrincipalAutor())
				autoridade.setTipo(Autoridade.TIPO_AUTOR);
			
			if(autoridade.contemEntraPrincipalAssunto())
				autoridade.setTipo(Autoridade.TIPO_ASSUNTO);
			
				
			if(movimento.isFinalizandoCatalogacao())
				autoridade.setCatalogada(true);
			
			boolean atualizouAutoridade = false;
			
			if(autoridade.getId() == 0) {
			
				autoridade.setNumeroDoSistema( dao.getNextSeq("biblioteca", "numero_do_sistema_autoridade_sequence") ); // importante n�o esquecer
				
				CatalogacaoUtil.configuraPosicaoCamposMarc(autoridade);   // Antes de salvar configura novamente para garantir se algum campo foi renovido
				dao.create(autoridade);
			
				if(movimento.isFinalizandoCatalogacao()) // s� come�a a entrar no hist�rico quando � finalizado
					getDAO(AutoridadeDao.class, movimento).registraAlteracaoAutoridade(autoridade, false);
				
				atualizouAutoridade = false;
			
			}else{
				
				/////////////////////////////////////////////////////////////////////////////////////////////////
				// a autoridade j� foi salva na base, o usu�rio j� tinha salvo a autoridade incompleta antes.  //
				/////////////////////////////////////////////////////////////////////////////////////////////////
				
				//Guarda os campos que por acaso foram removidos do t�tulo //
				List<CampoControle> camposControleParaRemocao = new ArrayList<CampoControle>();
				List<CampoDados> camposDadosParaRemocao = new ArrayList<CampoDados>();
				List<SubCampo> subCamposParaRemocao = new ArrayList<SubCampo>();
				
				Autoridade autoridadeBanco =  dao.findByPrimaryKey(autoridade.getId(), Autoridade.class);
				
				verificaCamposRemovidosDaAutoridade(autoridade, autoridadeBanco, camposControleParaRemocao
						, camposDadosParaRemocao, subCamposParaRemocao);
				
				
				dao.detach(autoridadeBanco);
				
				CatalogacaoUtil.configuraPosicaoCamposMarc(autoridade);   // Antes de salvar configura novamente para garantir se algum campo foi renovido
				dao.update(autoridade);
				
				
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
				
				if(movimento.isFinalizandoCatalogacao()) // s� come�a a entrar no hist�rico quando � finalizado
					getDAO(AutoridadeDao.class, movimento).registraAlteracaoAutoridade(autoridade, true);
				
				atualizouAutoridade = true;
				
				/////////////////////////////////////////////////////////////////////////////////////////
				
			}
			
			
			// REALIZA A SINCRONIZACAO DAS BUSCAS DE TITULOS
			CatalogacaoUtil.sincronizaAutoridadeCache(dao, autoridade, atualizouAutoridade);
			
			// Para n�o gerar o hist�rico toda vez que o usu�rio salvar apenas se finalizar a autoridade.
			// e s� marca para exporta��o se finalizar a cataloga��o
			if(movimento.isFinalizandoCatalogacao()){
				
				dao.create(new EntidadesMarcadasParaExportacao(movimento.getAutoridade(), (Usuario) movimento.getUsuarioLogado()));
			}		
			
			
		}finally{
			dao.close();
		}
		
		return null;
	}

	
	/**
	 * ver coment�rios na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCatalogaAutoridade movimento = (MovimentoCatalogaAutoridade) mov;
		
		// Administradores gerais e quem possui papel de bibliotec�rio de cataloga��o pode alterar as cataloga��oes, independente da biblioteca alocada
		checkRole(new int[]{SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}, mov);
		
		ListaMensagens lista = new ListaMensagens();
	
		CatalogacaoValidatorFactory.getRegrasValidacao().validaCamposMarcAutoridade(movimento.getAutoridade(), lista);
		checkValidation(lista);
		
		if(movimento.isFinalizandoCatalogacao()){
			CatalogacaoValidatorFactory.getRegrasValidacao().verificaCamposObrigatoriosAutoridade(movimento.getAutoridade(), lista);
			checkValidation(lista);
		
		
			// se vai finalizar a autoridade //
			// Obs.: N�o pode verificar quando salva porque as autoridades importadas s�o salvo diretamente sem valida��o
		
			CatalogacaoValidatorFactory.getRegrasValidacao().verificaExiteAutoridadeIgual(movimento.getAutoridade(), lista);
			checkValidation(lista);
		}
		
	}

	
	
	
	
	
	
	
	/**
	 *   � preciso verificar quais campos o usu�rio removeu da autoridae que ele est� tentando atualizar
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
			
			// Teste: se o campo de controle que est� no banco n�o existir mais � porque
			// o usu�rio removeu entao apague o.
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
					
					// subcampo que est� no banco foi apagado pelo usu�rio
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
	 * Testa se um campos de dados do t�tulo no banco existe no t�tulo que o usu�rio est� manipulando
	 * Isso para saber se ele foi removido ou n�o pelo usu�rio.
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
