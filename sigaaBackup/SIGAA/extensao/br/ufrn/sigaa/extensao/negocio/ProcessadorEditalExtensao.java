package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensaoRegra;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Processador para manipular publicar/editar editais de extensão
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorEditalExtensao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		GenericDAO dao = getGenericDAO(mov);
		try {
			EditalExtensao editalExtensao = ((MovimentoCadastro) mov).getObjMovimentado();
			if(mov.getCodMovimento().equals(SigaaListaComando.PUBLICAR_EDITAL_EXTENSAO)){
				editalExtensao.getEdital().setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
				editalExtensao.setUsuario((Usuario) mov.getUsuarioLogado());
				if (editalExtensao.getTipo() != Edital.ASSOCIADO) {
					editalExtensao.setTipo(Edital.EXTENSAO);
				}
				editalExtensao.setAtivo(true);
				dao.createOrUpdate(editalExtensao.getEdital());
				dao.createOrUpdate(editalExtensao);
				for (EditalExtensaoRegra regra: editalExtensao.getRegras()) {
					regra.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
					dao.createOrUpdate(regra);
				}
			} else if(mov.getCodMovimento().equals(SigaaListaComando.REMOVER_EDITAL_EXTENSAO)){
				if(editalExtensao.getIdArquivo() != null) {
					EnvioArquivoHelper.removeArquivo(editalExtensao.getIdArquivo());
				}
				// Apenas inativa o registro
				dao.updateField(Edital.class, editalExtensao.getEdital().getId(), "ativo", Boolean.FALSE);
			}
		}finally {
			dao.close();
		}
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO, mov);
	    ListaMensagens lista = new ListaMensagens();
	    EditalExtensao editalExtensao = ((MovimentoCadastro) mov).getObjMovimentado();
	    lista = editalExtensao.validate();	    
	    checkValidation(lista);		
	}

}
