/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Oct 22, 2008
 *
 */

package br.ufrn.sigaa.ensino.stricto.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.negocio.MovimentoDocumentoSite;
import br.ufrn.sigaa.sites.dominio.DocumentoSite;

/**
 * Processador responsável por realizar operações sobre a entidade DocumentoSite
 * @author Victor Hugo
 */
public class ProcessadorDocumentoSite extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

	
		if(movimento.getCodMovimento() == SigaaListaComando.CADASTRAR_DOCUMENTO_SITE ){
			validate(movimento);
			cadastrarDocumentoSite(movimento);
		} else if( movimento.getCodMovimento() == SigaaListaComando.REMOVER_DOCUMENTO_SITE ){
			removerDocumentoSite(movimento);
		}
		
		return movimento;
	}
	
	public void cadastrarDocumentoSite(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoDocumentoSite mov = (MovimentoDocumentoSite) movimento;
		DocumentoSite obj = mov.getDocumentoSite();
		GenericDAO dao = getDAO(mov);
		
		try {
			/* Atualizar Arquivo */
			if (mov.getArquivo() != null) {
				if( obj.getIdArquivo() > 0 ){
					// Remover arquivo anterior
					EnvioArquivoHelper.removeArquivo(obj.getIdArquivo());
				}

				// Armazenar o arquivo 
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, mov.getArquivo().getBytes(),
						mov.getArquivo().getContentType(), mov.getArquivo().getName());

				// Atualizar DocumentoSite
				obj.setIdArquivo(idArquivo);
			}
			
			/* Criar ou atualizar a notícia do programa */
			if (obj.getId() == 0) {
				dao.create(obj);
			} else {
				dao.update(obj);
			}

		} catch (IOException e) {
			throw new ArqException("Erro na recuperação do arquivo", e);
		}
	}

	/**
	 * Apaga o arquivo e remove o registro de DocumentoSite
	 * @param movimento
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public String removerDocumentoSite(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		  
		MovimentoDocumentoSite mov = (MovimentoDocumentoSite) movimento;
		DocumentoSite obj = mov.getDocumentoSite();
		  
		EnvioArquivoHelper.removeArquivo(obj.getIdArquivo());
		getGenericDAO(mov).remove(obj);
		
        return null;
          
	 }

	public void validate(Movimento movimento) throws NegocioException, ArqException {
		checkRole(new int[] { 
				SigaaPapeis.SECRETARIA_POS,SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.SECRETARIA_DEPARTAMENTO,SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.SECRETARIA_COORDENACAO,SigaaPapeis.SECRETARIA_GRADUACAO,
				SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR}, movimento);
		MovimentoDocumentoSite mov = (MovimentoDocumentoSite) movimento;
		DocumentoSite obj = mov.getDocumentoSite();
		ListaMensagens erros = obj.validate();
		
		/**
		 * se for cadastro ou alteração é obrigatório informar o arquivo
		 */
		if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_DOCUMENTO_SITE) 
				&& obj.getIdArquivo() == 0 && mov.getArquivo() == null ){
			erros.addErro("É obrigatório selecionar um arquivo.");
		}
		
		if(!isEmpty(obj.getUnidade()) && obj.getUnidade().getId()>0)
			obj.setCurso(null);
		else
			obj.setUnidade(null);
		
		checkValidation(erros);
	}

}
