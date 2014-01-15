/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 19/05/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;

/**
 *
  * <p>Processador que remove uma forma de documento  do sistema </p>
 *
 * <p> <i> Caso uma nova forma seja passada, deve migrar todos os materiais para a nova forma. Senão apenas apagar 
 * o relacionamento de todos os materiais com a forma passada.</i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorRemoveFormaDocumento extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		validate(mov);
		
		MovimentoRemoveFormatoDocumento movimento = (MovimentoRemoveFormatoDocumento) mov;
		
		GenericDAO dao = null;
		
		try{
			
			dao = getGenericDAO( mov);
			
			FormaDocumento formDocumentoASerRemovida = movimento.getFormaDocumento();
			FormaDocumento novaFormaDocumento = movimento.getNovaFormaDocumento();
			dao.updateField(FormaDocumento.class, formDocumentoASerRemovida.getId(), "ativo", false);
			
			
			// Migra os materias da forma de documento removida para nova //
			if(novaFormaDocumento != null && novaFormaDocumento.getId() > 0){
				dao.update("UPDATE biblioteca.material_informacional_formato_documento SET id_forma_documento = ? WHERE id_forma_documento  = ?", new Object[]{novaFormaDocumento.getId(), formDocumentoASerRemovida.getId()});
			}else{
				// Apaga os relacionamento dos materiais com a forma de documento removida //
				dao.update("DELETE FROM biblioteca.material_informacional_formato_documento WHERE id_forma_documento = ? ", new Object[]{formDocumentoASerRemovida.getId()} );
			}
		}finally{
			if(dao != null) dao.close();
		}
			
		return null;
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();

		MovimentoRemoveFormatoDocumento movimento = (MovimentoRemoveFormatoDocumento) mov;

		FormaDocumento formDocumentoASerRemovida = movimento.getFormaDocumento();
		FormaDocumento novaFormaDocumento = movimento.getNovaFormaDocumento();

		if (novaFormaDocumento == null || novaFormaDocumento.getId() == -1)
			erros.addErro("Escolha a Forma de Documento para a qual os materiais serão migrados");
		
		if(formDocumentoASerRemovida == null || ! formDocumentoASerRemovida.isAtivo())
			erros.addErro("A forma do documento já foi removida.");
		
		
		if (formDocumentoASerRemovida != null && novaFormaDocumento != null && formDocumentoASerRemovida.getId() == novaFormaDocumento.getId())
			erros.addErro("Os materiais não podem ser migrados para a Forma de Documento que está sendo removida.");

		checkValidation(erros);
		
	}

}
