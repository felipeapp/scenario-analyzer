/*
 * ProcessadorCadastraPoliticaEmprestimo.java
 *
 * Universidade Federal do Rio Grande no Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em:  03/06/2009
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.PoliticaEmprestimoDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;

/**
 * <p>Processador que cont�m as regras de neg�cio para cadastrar uma pol�tica de empr�stimo.</p>
 *
 *  <p>
 *  Situa��o anterior: <br/>
 *  Cada  <b><i>Tipo de Empr�stimo</i></b> e <b><i>Status</i></b> definiam o prazo e quantidade: <br/> 
 *  NORMAL -> REGULAR = quantidade e prazo    <br/>
 *  NORMAL -> ESPECIAL = quantidade e prazo   <br/>
 *  ESPECIAL -> REGULAR = quantidade e prazo   <br/>
 *  ESPECIAL -> ESPECIAL = quantidade e prazo   <br/>
 *  FOTO_C�PIA -> REGULAR = quantidade e prazo  <br/>
 *  FOTO_C�PIA -> ESPECIAL = quantidade e prazo  <br/>
 *  </p>
 *  
 *  <p>
 *  A nova situa��o dever� ser assim: <br/>
 *  Uma pol�tica de um determinado  <b><i>Tipo de Empr�stimo</i></b> pode est� associado a 0 a N  <b><i>Status de Material</i></b> e 0 a N  <b><i>Tipos de materiais</i></b>: <br/>
 *  
 *  NORMAL -> REGULAR (qualquer tipo de material ) = quantidade e prazo   <br/>
 *  ESPECIAL -> REGULAR e ESPECIAL (qualquer tipo de material ) = quantidade e prazo  <br/>
 *  FOTO_C�PIA -> REGULAR e ESPECIAL (qualquer tipo de material ) = quantidade e prazo  <br/>
 *  NOVO_TIPO -> REGULAR e ESPECIAL (para o tipo de material disco) = quantidade e prazo (usado apenas na biblioteca de m�sica) <br/>
 *  </p>
 *
 *  <p>
 *  	O usu�rio vai poder cadastrar mais de uma pol�tica para o mesmo tipo de empr�stimo, desde que a situa��o e tipo de material n�o coincidam.<br/>
 *  	Assim, por exemplo, se o usu�rio cadastrar:<br/>
 *  	NORMAL -> REGULAR (qualquer tipo de material ) = quantidade e prazo <br/>
 *  	NORMAL -> ESPECIAL (qualquer tipo de material ) = quantidade e prazo <br/>
 *  	Vai ficar igual ao que est� hoje. Ou seja todas as situa��es atuais s�o atendidas pela nova, mas a nova permite mais coisas, � mais flexis�vel.<br/>
 *  	<br/><br/>
 *  	Para a cole��o de status e tipos de materiais s�o v�lidas as regras: <br/>
 *      Caso n�o possua nenhum, a pol�tica � v�lida para todos os status/tipo de material para a trinca:
 * 		<b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo] </b>.</p>   <br/> <br/>
 * 
 * 		O sistema n�o deve permitir adicionar o mesmo status/tipo de material para <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b>. 
 * 		Por exemplo, se j� exitir uma pol�tica para um determinado <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b> com status "REGULAR", 
 * 		n�o pode ser cadastrada outra pol�tica para os mesmos <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b> que contenha o status "REGULAR". <br/>
 * 		Se existir uma pol�tica <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b> com status/tipo de material vazio, ou seja, para todos os status/tipo de materiais, n�o 
 * 		pode ser cadatrado outra para os mesmos <b>[biblioteca, v�nculo usu�rio, tipo de empr�stimo]</b>.
 *  
 *  <p>
 *
 *  <p> <strong>Essas regras visam impedir ambiguidade, que para um determinado status exitam duas pol�ticas que possam ser usadas. SEMPRE DEVE EXISTIR 0 OU 1 POL�TICA PARA UM MATERIAL. </strong>
 *  Na nova modelagem � permitido que o material n�o possu pol�ticas cadastradas para ele no sistema. Nesse caso o material n�o pode ser emprestado, por exemplo: 
 *  para a regra da UFRN um material com o Status ESPECIAL n�o podem ser empr�stado pelo empr�stimo do tipo NORMAL. </p>
 *
 * @author jadson - jadson@info.ufrn.br 
 * @since 03/06/2009
 * @version 1.0 criacao da classe
 * @version 2.0 - jadson em 20/02/2012 - flexibiliza��o das pol�ticas de empr�stimo para atender as resolu��es da BCZM. 
 * Essa altera��o � retocompat�vel com a situa��o atual. Ou seja, n�o precisa mudar as regras atuais para suporta a nova modelagem da pol�tica. 
 */
public class ProcessadorCadastraAlteraPoliticaEmprestimo extends AbstractProcessador{

	
	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		
		/* valida se a permiss�o do usu�rio alterar e se os dados foram informados corretamente */
		validate(mov); 
		
		PoliticaEmprestimoDao dao = null;
		
		try{
		
			dao = getDAO(PoliticaEmprestimoDao.class, mov);
			
			MovimentoCadastraPoliticaEmprestimo movimento = (MovimentoCadastraPoliticaEmprestimo) mov;
			
			/*
			 * As pol�ticas que o usu�rio deixou no sistema, se tem algum que n�o est� aqui deve ser desativada, porque o usu�rio removeu na tela.
			 * 
			 * Pelo caso de uso de cadastro, o usu�rio altera todas as pol�ticas da mesma biblioteca e v�nculo por vez.
			 * 
			 */
			List<PoliticaEmprestimo> politicasMesmaBibliotecaVinculoAlteradasPeloUsuario = movimento.getPoliticasEmprestimo();
			
			/*
			 * S� precisa verifica as pol�ticas passadas no caso de uso, pois o usu�rio sempre altera todas da mesma biblioteca e v�nculo de uma s� vez
			 * 
			 * Ent�o essa pol�ticas nunca v�o conflitar com pol�ticas de outras bibliotecas ou v�nculos de usu�rio.
			 * 
			 * O que n�o est� nessa lista, ou � de outra biblioteca e v�nculo ou est� desativado!
			 */
			verificaPoliticasConflitantesEntreSi(politicasMesmaBibliotecaVinculoAlteradasPeloUsuario);
			
			verificaTipoEmprestimoAtribuidosCorretos(politicasMesmaBibliotecaVinculoAlteradasPeloUsuario);
			
			verificaStatusMaterialAtribuidosCorretos(politicasMesmaBibliotecaVinculoAlteradasPeloUsuario);
			
			for (PoliticaEmprestimo politicaEmprestimo : politicasMesmaBibliotecaVinculoAlteradasPeloUsuario) {
				
				if(politicaEmprestimo.getId() == 0){  // est� criando a pol�tica agora !!
						
					dao.create(politicaEmprestimo);
					
				}else{ // j� existia a pol�tica est� apenas atualizando
					
					PoliticaEmprestimo politicaAtualBanco =  dao.findDadosAlteracaoPoliticaById(politicaEmprestimo.getId());
										
					if( dadosDaPoliticaForamAlterados(politicaEmprestimo, politicaAtualBanco)){
						
						/* Realiza a "altera��o" nos valores da pol�tica */
						
						// desativa a anterior, empr�stimos j� realizado v�o continuar com a regra antiga
						dao.updateField(PoliticaEmprestimo.class, politicaAtualBanco.getId(), "ativo", false);
						dao.update(politicaAtualBanco);
						
						dao.detach(politicaAtualBanco);
						
						// cria a nova pol�tica, novos empr�stimos v�o usar essa pol�tica
						politicaEmprestimo.setId(0);
						dao.create(politicaEmprestimo);	
						
					}
				}
					
			} // for politicas que o usu�rio deixou no sistema
				
			
			// falta desativa aquelas pol�tica que por acaso o usu�rio tenha removido //
			// Como o usu�rio altera todas as pol�ticas da mesma biblioteca e v�nculo por vez, s� precisa buscas essas no banco
			
			
			List<PoliticaEmprestimo> politicasNoBancoParaMesmaBibliotecaEVinculo = 
					dao.findPoliticasEmpretimoAtivasAlteraveisByBibliotecaEVinculo(politicasMesmaBibliotecaVinculoAlteradasPeloUsuario.get(0).getBiblioteca(), politicasMesmaBibliotecaVinculoAlteradasPeloUsuario.get(0).getVinculo());
				
			for (PoliticaEmprestimo politicaNoBanco : politicasNoBancoParaMesmaBibliotecaEVinculo) {
				if( ! politicasMesmaBibliotecaVinculoAlteradasPeloUsuario.contains(politicaNoBanco) ){ // O usu�rio removeu a pol�tica
					dao.updateField(PoliticaEmprestimo.class, politicaNoBanco.getId(), "ativo", false);
				}
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	/** Como o usu�rio pode escolher os tipos de empr�stimos da pol�tica, verifica se ele atribuiu um tipo correto. 
	 * @throws NegocioException */
	private void verificaTipoEmprestimoAtribuidosCorretos(List<PoliticaEmprestimo> politicasAlteradasCasoDeUso) throws NegocioException {
		
		for (PoliticaEmprestimo politicaEmprestimo : politicasAlteradasCasoDeUso) {
			if(politicaEmprestimo.getVinculo().isVinculoBiblioteca() && ! politicaEmprestimo.getTipoEmprestimo().isInstitucional()
					|| (! politicaEmprestimo.getVinculo().isVinculoBiblioteca() && politicaEmprestimo.getTipoEmprestimo().isInstitucional())
					|| ( politicaEmprestimo.getTipoEmprestimo().isSemPoliticaEmprestimo() )  )
				throw new NegocioException("O tipo de empr�stimo: "+politicaEmprestimo.getTipoEmprestimo().getDescricao()+" n�o pode ser atribu�do a pol�tica "+politicaEmprestimo.getDescricaoPolitica());
		}
		
	}

	/** Como o usu�rio pode escolher os status da pol�tica, verifica se ele escolher o correto.
	 * @throws NegocioException */
	private void verificaStatusMaterialAtribuidosCorretos(List<PoliticaEmprestimo> politicasAlteradasCasoDeUso) throws NegocioException {
		
		for (PoliticaEmprestimo politicaEmprestimo : politicasAlteradasCasoDeUso) {
			if( politicaEmprestimo.getStatusMateriais() != null )
			for(StatusMaterialInformacional status : politicaEmprestimo.getStatusMateriais()){
				if(! status.isPermiteEmprestimo())
					throw new NegocioException("O Status: "+status.getDescricao()+" n�o pode ser atribu�do a pol�tica "+politicaEmprestimo.getDescricaoPolitica());
			}
		}
		
	}
	

	/**
	 * Verifica se a pol�tica que o usu�rio est� alterando no momento s�o conflitantes entre si.
	 * 
	 * @param politicasAlteradasCasoDeUso
	 * @throws NegocioException
	 *  
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/</li>
	 * </ul>
	 *
	 */
	private void verificaPoliticasConflitantesEntreSi(List<PoliticaEmprestimo> politicasAlteradasCasoDeUso) throws NegocioException {
		
		// Tem que verifica todas as combina��es das pol�ticas passe se algum conflita entre si //
		for (int i = 0; i < politicasAlteradasCasoDeUso.size(); i++) {
			for (int j = i+1; j < politicasAlteradasCasoDeUso.size(); j++) {
				if(politicasSaoConflitantes(politicasAlteradasCasoDeUso.get(i), politicasAlteradasCasoDeUso.get(j)))
					throw new NegocioException("Existem duas pol�ticas de empr�timos: "+politicasAlteradasCasoDeUso.get(i).getDescricaoPolitica()+" conflitantes.");
			}
		}
	}


	/** Implementa a regra de verifica se eles s�o conflitantes.*/
	private boolean politicasSaoConflitantes(PoliticaEmprestimo politicaEmprestimo, PoliticaEmprestimo politicaEmprestimo2) {
		
		//Se tem o mesmo tipo de empr�stimo
		if(politicaEmprestimo.getTipoEmprestimo().getId() == politicaEmprestimo2.getTipoEmprestimo().getId()){
		
			
			boolean isConflitoStatus = false;
			// Para status tem 2 casos:
			// Status == null
			// Status != null
			
			if(politicaEmprestimo.getStatusMateriais() == null){ // n�o pode existir outra pol�tica para a mesma biblioteca, vinculo e tipo emprestimo, ent�o sempre � conflito 
				isConflitoStatus = true;
			}else{  // se a outra for == null ou tiver o mesmo status, conflita
				
				if(politicaEmprestimo2.getStatusMateriais() == null) 
					isConflitoStatus = true;
				else{
					for(StatusMaterialInformacional s2: politicaEmprestimo2.getStatusMateriais() ){
						if(politicaEmprestimo.getStatusMateriais().contains(s2))
							isConflitoStatus = true;
					}
				}
			}
			
			
			// Para tipo de material tamb�m tem 2 casos:
			// tipo de material  == null
			// tipo de material  != null
			boolean isConflitoTipoMaterial = false;
			
			if(politicaEmprestimo.getTiposMateriais() == null){ // n�o pode existir outra pol�tica para a mesma biblioteca, vinculo e tipo emprestimo, ent�o sempre � conflito 
				isConflitoTipoMaterial = true;
			}else{  // se a outra for == null ou tiver o mesmo status, conflita
				
				if(politicaEmprestimo2.getTiposMateriais() == null) 
					isConflitoTipoMaterial = true;
				else{
					for(TipoMaterial t2: politicaEmprestimo2.getTiposMateriais() ){
						if(politicaEmprestimo.getTiposMateriais().contains(t2))
							isConflitoTipoMaterial = true;
					}
				}
			}
			
			// Pode ter conflito no tipo de mataterial se forem status diferentes, e vice-versa
			return isConflitoStatus && isConflitoTipoMaterial;
		}
		
		return false;
	}



	/**
	 * <p>Testa se os dados da pol�tica foram alterados pelo usu�rio ou n�o.</p>
	 * 
	 * <p>Porque como o usu�rio altera todas os pol�ticas em uma mesma p�gina, e as pol�ticas nunca s�o alteradas no banco, sempre s�o desativadas e criadas novas.
	 * Impede o sistema de ficar desativando e criando novas pol�ticas sem necessidade se o usu�rio mandou gravar altera��es sem ter mudado nada.</p>
	 * 
	 */
	private boolean dadosDaPoliticaForamAlterados(PoliticaEmprestimo politicaNova, PoliticaEmprestimo politicaAtualBanco) throws NegocioException{
		
		
		// Prevenindo um NullPointerException, que n�o conseguir reproduzir //
		if(politicaNova == null 
				|| politicaAtualBanco == null 
				|| politicaAtualBanco.getQuantidadeMateriais() == null 
				|| politicaAtualBanco.getPrazoEmprestimo() == null 
				|| politicaAtualBanco.getTipoPrazo() == null
				|| politicaAtualBanco.getQuantidadeRenovacoes() == null
				|| politicaNova == null
				|| politicaNova.getQuantidadeMateriais() == null 
				|| politicaNova.getPrazoEmprestimo() == null 
				|| politicaNova.getTipoPrazo() == null
				|| politicaNova.getQuantidadeRenovacoes() == null
				){
			
			throw new NegocioException("N�o foi poss�vel alterar a pol�tica de empr�timo, dados da pol�tica inv�lidos");
		}
		
		return ! politicaNova.strictlyEqualByDadosPolitica(politicaAtualBanco);
		
	}
	
	
	
	
	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastraPoliticaEmprestimo movimento = (MovimentoCadastraPoliticaEmprestimo) mov;
		
		List<PoliticaEmprestimo> politicas = movimento.getPoliticasEmprestimo();
		
		ListaMensagens lista = new ListaMensagens();
		
		
		GenericDAO dao = null;
		
		try{
		
			dao = getGenericDAO(movimento);
		
			for (PoliticaEmprestimo politicaEmprestimo : politicas) {			
				
				if(  ! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL) ){
					
					/* se o usu�rio logado � s� administrador local, ele s� pode alterar as pol�ticas da sua biblioteca, para  * 
					 * o caso em que o sistema trabalhe com uma pol�tica diferente por biblioteca.                             */	
					try{
						checkRole(politicaEmprestimo.getBiblioteca().getUnidade() , movimento, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
					}catch (SegurancaException se) {
						lista.addErro(" Usu�rio n�o tem Permiss�o para Alterar as Pol�ticas de Empr�stimo de outra Biblioteca.");
						break;
					}
					
				}
				
				// O VALIDADOR DO PR�PRIO OBJETO 
				lista.addAll( politicaEmprestimo.validate()); 
				
			}
		
		}finally{
			if(dao != null) dao.close();
			
			checkValidation(lista);
		}
		
		
	}

}
