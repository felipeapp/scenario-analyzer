/*
 * ProcessadorCadastraPoliticaEmprestimo.java
 *
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
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
 * <p>Processador que contém as regras de negócio para cadastrar uma política de empréstimo.</p>
 *
 *  <p>
 *  Situação anterior: <br/>
 *  Cada  <b><i>Tipo de Empréstimo</i></b> e <b><i>Status</i></b> definiam o prazo e quantidade: <br/> 
 *  NORMAL -> REGULAR = quantidade e prazo    <br/>
 *  NORMAL -> ESPECIAL = quantidade e prazo   <br/>
 *  ESPECIAL -> REGULAR = quantidade e prazo   <br/>
 *  ESPECIAL -> ESPECIAL = quantidade e prazo   <br/>
 *  FOTO_CÓPIA -> REGULAR = quantidade e prazo  <br/>
 *  FOTO_CÓPIA -> ESPECIAL = quantidade e prazo  <br/>
 *  </p>
 *  
 *  <p>
 *  A nova situação deverá ser assim: <br/>
 *  Uma política de um determinado  <b><i>Tipo de Empréstimo</i></b> pode está associado a 0 a N  <b><i>Status de Material</i></b> e 0 a N  <b><i>Tipos de materiais</i></b>: <br/>
 *  
 *  NORMAL -> REGULAR (qualquer tipo de material ) = quantidade e prazo   <br/>
 *  ESPECIAL -> REGULAR e ESPECIAL (qualquer tipo de material ) = quantidade e prazo  <br/>
 *  FOTO_CÓPIA -> REGULAR e ESPECIAL (qualquer tipo de material ) = quantidade e prazo  <br/>
 *  NOVO_TIPO -> REGULAR e ESPECIAL (para o tipo de material disco) = quantidade e prazo (usado apenas na biblioteca de música) <br/>
 *  </p>
 *
 *  <p>
 *  	O usuário vai poder cadastrar mais de uma política para o mesmo tipo de empréstimo, desde que a situação e tipo de material não coincidam.<br/>
 *  	Assim, por exemplo, se o usuário cadastrar:<br/>
 *  	NORMAL -> REGULAR (qualquer tipo de material ) = quantidade e prazo <br/>
 *  	NORMAL -> ESPECIAL (qualquer tipo de material ) = quantidade e prazo <br/>
 *  	Vai ficar igual ao que está hoje. Ou seja todas as situações atuais são atendidas pela nova, mas a nova permite mais coisas, é mais flexisível.<br/>
 *  	<br/><br/>
 *  	Para a coleção de status e tipos de materiais são válidas as regras: <br/>
 *      Caso não possua nenhum, a política é válida para todos os status/tipo de material para a trinca:
 * 		<b>[biblioteca, vínculo usuário, tipo de empréstimo] </b>.</p>   <br/> <br/>
 * 
 * 		O sistema não deve permitir adicionar o mesmo status/tipo de material para <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b>. 
 * 		Por exemplo, se já exitir uma política para um determinado <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b> com status "REGULAR", 
 * 		não pode ser cadastrada outra política para os mesmos <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b> que contenha o status "REGULAR". <br/>
 * 		Se existir uma política <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b> com status/tipo de material vazio, ou seja, para todos os status/tipo de materiais, não 
 * 		pode ser cadatrado outra para os mesmos <b>[biblioteca, vínculo usuário, tipo de empréstimo]</b>.
 *  
 *  <p>
 *
 *  <p> <strong>Essas regras visam impedir ambiguidade, que para um determinado status exitam duas políticas que possam ser usadas. SEMPRE DEVE EXISTIR 0 OU 1 POLÍTICA PARA UM MATERIAL. </strong>
 *  Na nova modelagem é permitido que o material não possu políticas cadastradas para ele no sistema. Nesse caso o material não pode ser emprestado, por exemplo: 
 *  para a regra da UFRN um material com o Status ESPECIAL não podem ser empréstado pelo empréstimo do tipo NORMAL. </p>
 *
 * @author jadson - jadson@info.ufrn.br 
 * @since 03/06/2009
 * @version 1.0 criacao da classe
 * @version 2.0 - jadson em 20/02/2012 - flexibilização das políticas de empréstimo para atender as resoluções da BCZM. 
 * Essa alteração é retocompatível com a situação atual. Ou seja, não precisa mudar as regras atuais para suporta a nova modelagem da política. 
 */
public class ProcessadorCadastraAlteraPoliticaEmprestimo extends AbstractProcessador{

	
	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		
		/* valida se a permissão do usuário alterar e se os dados foram informados corretamente */
		validate(mov); 
		
		PoliticaEmprestimoDao dao = null;
		
		try{
		
			dao = getDAO(PoliticaEmprestimoDao.class, mov);
			
			MovimentoCadastraPoliticaEmprestimo movimento = (MovimentoCadastraPoliticaEmprestimo) mov;
			
			/*
			 * As políticas que o usuário deixou no sistema, se tem algum que não está aqui deve ser desativada, porque o usuário removeu na tela.
			 * 
			 * Pelo caso de uso de cadastro, o usuário altera todas as políticas da mesma biblioteca e vínculo por vez.
			 * 
			 */
			List<PoliticaEmprestimo> politicasMesmaBibliotecaVinculoAlteradasPeloUsuario = movimento.getPoliticasEmprestimo();
			
			/*
			 * Só precisa verifica as políticas passadas no caso de uso, pois o usuário sempre altera todas da mesma biblioteca e vínculo de uma só vez
			 * 
			 * Então essa políticas nunca vão conflitar com políticas de outras bibliotecas ou vínculos de usuário.
			 * 
			 * O que não está nessa lista, ou é de outra biblioteca e vínculo ou está desativado!
			 */
			verificaPoliticasConflitantesEntreSi(politicasMesmaBibliotecaVinculoAlteradasPeloUsuario);
			
			verificaTipoEmprestimoAtribuidosCorretos(politicasMesmaBibliotecaVinculoAlteradasPeloUsuario);
			
			verificaStatusMaterialAtribuidosCorretos(politicasMesmaBibliotecaVinculoAlteradasPeloUsuario);
			
			for (PoliticaEmprestimo politicaEmprestimo : politicasMesmaBibliotecaVinculoAlteradasPeloUsuario) {
				
				if(politicaEmprestimo.getId() == 0){  // está criando a política agora !!
						
					dao.create(politicaEmprestimo);
					
				}else{ // já existia a política está apenas atualizando
					
					PoliticaEmprestimo politicaAtualBanco =  dao.findDadosAlteracaoPoliticaById(politicaEmprestimo.getId());
										
					if( dadosDaPoliticaForamAlterados(politicaEmprestimo, politicaAtualBanco)){
						
						/* Realiza a "alteração" nos valores da política */
						
						// desativa a anterior, empréstimos já realizado vão continuar com a regra antiga
						dao.updateField(PoliticaEmprestimo.class, politicaAtualBanco.getId(), "ativo", false);
						dao.update(politicaAtualBanco);
						
						dao.detach(politicaAtualBanco);
						
						// cria a nova política, novos empréstimos vão usar essa política
						politicaEmprestimo.setId(0);
						dao.create(politicaEmprestimo);	
						
					}
				}
					
			} // for politicas que o usuário deixou no sistema
				
			
			// falta desativa aquelas política que por acaso o usuário tenha removido //
			// Como o usuário altera todas as políticas da mesma biblioteca e vínculo por vez, só precisa buscas essas no banco
			
			
			List<PoliticaEmprestimo> politicasNoBancoParaMesmaBibliotecaEVinculo = 
					dao.findPoliticasEmpretimoAtivasAlteraveisByBibliotecaEVinculo(politicasMesmaBibliotecaVinculoAlteradasPeloUsuario.get(0).getBiblioteca(), politicasMesmaBibliotecaVinculoAlteradasPeloUsuario.get(0).getVinculo());
				
			for (PoliticaEmprestimo politicaNoBanco : politicasNoBancoParaMesmaBibliotecaEVinculo) {
				if( ! politicasMesmaBibliotecaVinculoAlteradasPeloUsuario.contains(politicaNoBanco) ){ // O usuário removeu a política
					dao.updateField(PoliticaEmprestimo.class, politicaNoBanco.getId(), "ativo", false);
				}
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	/** Como o usuário pode escolher os tipos de empréstimos da política, verifica se ele atribuiu um tipo correto. 
	 * @throws NegocioException */
	private void verificaTipoEmprestimoAtribuidosCorretos(List<PoliticaEmprestimo> politicasAlteradasCasoDeUso) throws NegocioException {
		
		for (PoliticaEmprestimo politicaEmprestimo : politicasAlteradasCasoDeUso) {
			if(politicaEmprestimo.getVinculo().isVinculoBiblioteca() && ! politicaEmprestimo.getTipoEmprestimo().isInstitucional()
					|| (! politicaEmprestimo.getVinculo().isVinculoBiblioteca() && politicaEmprestimo.getTipoEmprestimo().isInstitucional())
					|| ( politicaEmprestimo.getTipoEmprestimo().isSemPoliticaEmprestimo() )  )
				throw new NegocioException("O tipo de empréstimo: "+politicaEmprestimo.getTipoEmprestimo().getDescricao()+" não pode ser atribuído a política "+politicaEmprestimo.getDescricaoPolitica());
		}
		
	}

	/** Como o usuário pode escolher os status da política, verifica se ele escolher o correto.
	 * @throws NegocioException */
	private void verificaStatusMaterialAtribuidosCorretos(List<PoliticaEmprestimo> politicasAlteradasCasoDeUso) throws NegocioException {
		
		for (PoliticaEmprestimo politicaEmprestimo : politicasAlteradasCasoDeUso) {
			if( politicaEmprestimo.getStatusMateriais() != null )
			for(StatusMaterialInformacional status : politicaEmprestimo.getStatusMateriais()){
				if(! status.isPermiteEmprestimo())
					throw new NegocioException("O Status: "+status.getDescricao()+" não pode ser atribuído a política "+politicaEmprestimo.getDescricaoPolitica());
			}
		}
		
	}
	

	/**
	 * Verifica se a política que o usuário está alterando no momento são conflitantes entre si.
	 * 
	 * @param politicasAlteradasCasoDeUso
	 * @throws NegocioException
	 *  
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/</li>
	 * </ul>
	 *
	 */
	private void verificaPoliticasConflitantesEntreSi(List<PoliticaEmprestimo> politicasAlteradasCasoDeUso) throws NegocioException {
		
		// Tem que verifica todas as combinações das políticas passe se algum conflita entre si //
		for (int i = 0; i < politicasAlteradasCasoDeUso.size(); i++) {
			for (int j = i+1; j < politicasAlteradasCasoDeUso.size(); j++) {
				if(politicasSaoConflitantes(politicasAlteradasCasoDeUso.get(i), politicasAlteradasCasoDeUso.get(j)))
					throw new NegocioException("Existem duas políticas de emprétimos: "+politicasAlteradasCasoDeUso.get(i).getDescricaoPolitica()+" conflitantes.");
			}
		}
	}


	/** Implementa a regra de verifica se eles são conflitantes.*/
	private boolean politicasSaoConflitantes(PoliticaEmprestimo politicaEmprestimo, PoliticaEmprestimo politicaEmprestimo2) {
		
		//Se tem o mesmo tipo de empréstimo
		if(politicaEmprestimo.getTipoEmprestimo().getId() == politicaEmprestimo2.getTipoEmprestimo().getId()){
		
			
			boolean isConflitoStatus = false;
			// Para status tem 2 casos:
			// Status == null
			// Status != null
			
			if(politicaEmprestimo.getStatusMateriais() == null){ // não pode existir outra política para a mesma biblioteca, vinculo e tipo emprestimo, então sempre é conflito 
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
			
			
			// Para tipo de material também tem 2 casos:
			// tipo de material  == null
			// tipo de material  != null
			boolean isConflitoTipoMaterial = false;
			
			if(politicaEmprestimo.getTiposMateriais() == null){ // não pode existir outra política para a mesma biblioteca, vinculo e tipo emprestimo, então sempre é conflito 
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
	 * <p>Testa se os dados da política foram alterados pelo usuário ou não.</p>
	 * 
	 * <p>Porque como o usuário altera todas os políticas em uma mesma página, e as políticas nunca são alteradas no banco, sempre são desativadas e criadas novas.
	 * Impede o sistema de ficar desativando e criando novas políticas sem necessidade se o usuário mandou gravar alterações sem ter mudado nada.</p>
	 * 
	 */
	private boolean dadosDaPoliticaForamAlterados(PoliticaEmprestimo politicaNova, PoliticaEmprestimo politicaAtualBanco) throws NegocioException{
		
		
		// Prevenindo um NullPointerException, que não conseguir reproduzir //
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
			
			throw new NegocioException("Não foi possível alterar a política de emprétimo, dados da política inválidos");
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
					
					/* se o usuário logado é só administrador local, ele só pode alterar as políticas da sua biblioteca, para  * 
					 * o caso em que o sistema trabalhe com uma política diferente por biblioteca.                             */	
					try{
						checkRole(politicaEmprestimo.getBiblioteca().getUnidade() , movimento, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
					}catch (SegurancaException se) {
						lista.addErro(" Usuário não tem Permissão para Alterar as Políticas de Empréstimo de outra Biblioteca.");
						break;
					}
					
				}
				
				// O VALIDADOR DO PRÓPRIO OBJETO 
				lista.addAll( politicaEmprestimo.validate()); 
				
			}
		
		}finally{
			if(dao != null) dao.close();
			
			checkValidation(lista);
		}
		
		
	}

}
