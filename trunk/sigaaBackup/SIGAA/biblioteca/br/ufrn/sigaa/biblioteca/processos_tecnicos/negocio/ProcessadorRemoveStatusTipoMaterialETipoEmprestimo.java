/*
 * ProcessadorRemoveStatusOuTipoEmprestimo.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 24/02/2010
 * Autor: jadson
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.biblioteca.PoliticaEmprestimoDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;

/**
 *
 * <p>Processador que cont�m as regras de neg�cio para remover um <code>Status do Material</code>, um <code>Tipo de Material</code> um 
 *   <code>Tipo de Empr�timo</code> do Sistema.</p>
 *
 *  <p> Quando qualquer uma desses objetos forem removidos (destivados), as pol�ticas de empr�stimos
 *  que estavam ativas para eles precisam ser desativadas ou removida a associa��o existente. <br/><br/>
 *  
 *  
 *  <strong>Esse processador tem um papel muito importante para manter consist�ncia do sistema !!!</strong>
 *  </p>
 *
 * @author jadson
 * @version 2.0 - jadson em 21/02/2012 - A rela��o entre pol�tica de empr�stimos, status, tipo material e tipo empr�stimo mudou.
 */
public class ProcessadorRemoveStatusTipoMaterialETipoEmprestimo extends AbstractProcessador{

	/**
	 * Ver coment�rio na classe pai
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoRemoveStatusTipoMaterialETipoEmprestimo movimento = (MovimentoRemoveStatusTipoMaterialETipoEmprestimo) mov;
		
		PoliticaEmprestimoDao dao = null;
		
		validate(movimento);
		
		try{
			
			dao = getDAO(PoliticaEmprestimoDao.class, mov);
			
			if(movimento.isRemoverStatus()){   // REMOVENDO UM STATUS
				StatusMaterialInformacional statusASerRemovido = movimento.getStatus();
				dao.updateField(StatusMaterialInformacional.class, statusASerRemovido.getId(), "ativo", false);
				
				// AGORA AQUI N�O MAIS DESATIVA AS POL�TICAS PARA O STATUS REMOVIDO, APENAS RETIRA A ASSOCIA��O
				List<PoliticaEmprestimo> politicasAtivas = dao.findPoliticasEmpretimoAtivasByStatusMaterial(statusASerRemovido.getId());
				
				if(politicasAtivas != null)
				for (PoliticaEmprestimo politicasAtiva: politicasAtivas) {
					
					if(politicasAtiva.getStatusMateriais() != null){
					
						forInterno:
						for (StatusMaterialInformacional statusAssociado : politicasAtiva.getStatusMateriais()) {
							if(statusAssociado.getId() == statusASerRemovido.getId() ){
								dao.update(" DELETE FROM biblioteca.politica_emprestimo_status_material WHERE id_politica_emprestimo = ? AND id_status_material_informacional = ? ", politicasAtiva.getId(), statusASerRemovido.getId() );
								break forInterno;
							}
						}
					}
				}
				
				// TRANSFERE OS MATERIAIS PARA O NOVO STATUS
				
				StatusMaterialInformacional statusNovo = movimento.getNovoStatusParaOsMateriais();
				
				// Migra os materias do status removido para o novo
				dao.update("UPDATE biblioteca.material_informacional SET id_status_material_informacional = ? WHERE id_status_material_informacional  = ?", new Object[]{statusNovo.getId(), statusASerRemovido.getId()});
				
			}else{                             // REMOVENDO UM TIPO DE EMPR�STIMO
				

				if(movimento.isRemoverTipoMaterial()){   // REMOVENDO UM TIPO DE MATERIAL
					
					// DESATIVA ELE
					TipoMaterial tipoMaterialASerRemovido = movimento.getTipoMaterial();
					dao.updateField(TipoMaterial.class, tipoMaterialASerRemovido.getId(), "ativo", false);
					
					// AGORA AQUI N�O MAIS DESATIVA AS POL�TICAS PARA O STATUS TIPO DE MATERIAL REMOVIDO, APENAS RETIRA A ASSOCIA��O
					List<PoliticaEmprestimo> politicasAtivas = dao.findPoliticasEmpretimoAtivasByTipoMaterial(tipoMaterialASerRemovido.getId());
					
					if(politicasAtivas != null)
						for (PoliticaEmprestimo politicasAtiva: politicasAtivas) {
							
							if(politicasAtiva.getTiposMateriais()!= null){
							
								forInterno:
								for (TipoMaterial tipoMaterialAssociado : politicasAtiva.getTiposMateriais()) {
									if(tipoMaterialAssociado.getId() == tipoMaterialASerRemovido.getId() ){
										dao.update(" DELETE FROM biblioteca.politica_emprestimo_tipo_material WHERE id_politica_emprestimo = ? AND id_tipo_material = ? ", politicasAtiva.getId(), tipoMaterialASerRemovido.getId() );
										break forInterno;
									}
								}
							}
						}
					
					// TRANSFERE OS MATERIAIS PARA O NOVO TIPO DE MATERIAL
					
					TipoMaterial tipoMaterialNovo = movimento.getNovoTipoMaterial();
					
					// Migra os materias do status removido para o novo
					dao.update("UPDATE biblioteca.material_informacional SET id_tipo_material = ? WHERE id_tipo_material  = ?", new Object[]{tipoMaterialNovo.getId(), tipoMaterialASerRemovido.getId()});
					
				}else{                             // REMOVENDO UM TIPO DE EMPR�STIMO
				
					
					// DESATIVA ELE
					TipoEmprestimo tipoEmprestimo = movimento.getTipoEmprestimo();
					dao.updateField(TipoEmprestimo.class, tipoEmprestimo.getId(), "ativo", false);
					
					// E DESATIVA TODAS AS POL�TICAS PARA O TIPO DE EMPR�TIMO REMOVIDO
					List<PoliticaEmprestimo> politicasAtivas = dao.findPoliticasEmpretimoAtivasByTipoEmprestimo(tipoEmprestimo.getId());
					
					for (PoliticaEmprestimo politicaEmprestimo : politicasAtivas) {
						dao.updateField(PoliticaEmprestimo.class, politicaEmprestimo.getId(), "ativo", false);
					}
				}
				
			}
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 * Ver coment�rio na classe pai
	 * 
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoRemoveStatusTipoMaterialETipoEmprestimo movimento = (MovimentoRemoveStatusTipoMaterialETipoEmprestimo) mov;
		
		if(movimento.isRemoverStatus()){   // REMOVENDO UM STATUS
			
			StatusMaterialInformacional statusASerRemovido = movimento.getStatus();
			StatusMaterialInformacional statusNovo = movimento.getNovoStatusParaOsMateriais();
			
			if(statusNovo.getId() == -1)
				erros.addErro("Escolha o status para o qual os materiais ser�o migrados");
			
			if(statusASerRemovido.getId() == statusNovo.getId())
				erros.addErro("Os materiais n�o podem ser migrados para o status que est� sendo removido");
		}
		
		
		if(movimento.isRemoverTipoMaterial()){   // REMOVENDO UM TIPO DE MATERIAL
			
			TipoMaterial tipoMaterialASerRemovido = movimento.getTipoMaterial();
			TipoMaterial novoTipoMaterial = movimento.getNovoTipoMaterial();
			
			if(novoTipoMaterial.getId() == -1)
				erros.addErro("Escolha o Tipo de Material para o qual os materiais ser�o migrados");
			
			if(tipoMaterialASerRemovido.getId() == novoTipoMaterial.getId())
				erros.addErro("Os materiais n�o podem ser migrados para o Tipo de Material que est� sendo removido");
		}
		
		checkValidation(erros);
		
	}

}
