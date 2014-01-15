package br.ufrn.sigaa.apedagogica.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeArquivo;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.GrupoAtividadesAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;

/**
 * Classe que processa todas informações referentes ao grupo de atividades de atualização pedagógica.
 * @author Mário Rizzi
 *
 */
public class ProcessadorGrupoAtividadeAP  extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
			
		validate(mov);
		if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_GRUPO_ATIVIDADE_ATUALIZACAO_PEDAGOGICA) )
			atualizar(mov);
		if( mov.getCodMovimento().equals(SigaaListaComando.REMOVER_GRUPO_ATIVIDADE_ATUALIZACAO_PEDAGOGICA) )
			remover(mov);
				
		return mov;
	}
	
	/**
	 * Perisiti os dados do grupo e suas atividades.
	 * @param mov
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void atualizar(Movimento mov) throws ArqException, NegocioException{
		
		GenericDAO dao = getGenericDAO(mov);
		MovimentoGrupoAtividadesAP movGrupo = (MovimentoGrupoAtividadesAP) mov;
		
		List<AtividadeArquivo> atividades = movGrupo.getListaAtividadesAnexo();
		GrupoAtividadesAtualizacaoPedagogica grupo = movGrupo.getObjMovimentado();
		try{
		
			AtividadeAtualizacaoPedagogica atividade = null;
			int idArquivo = 0;

			/* 
			 * 	Intera sobre a lista das atividades passadas pelo movimento,
			 *	setando o arquivo persistido, adicionando a atividade a coleção do grupo.
			 */
			for (AtividadeArquivo map : atividades) {
				atividade = (AtividadeAtualizacaoPedagogica) map.getAtividade();
				UploadedFile arquivo = (UploadedFile) map.getArquivo();
				if ( !isEmpty(arquivo) ) {	
					
					/*
					 * No caso de substituir o arquivo remove o arquivo anterior.
					 */
					if( !isEmpty(atividade.getIdArquivo()) )
						EnvioArquivoHelper.removeArquivo(atividade.getIdArquivo());
						
					idArquivo = EnvioArquivoHelper.getNextIdArquivo();
					EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), 
							arquivo.getContentType(), arquivo.getName());
					atividade.setIdArquivo(idArquivo);
				}
				grupo.addAtividade(atividade);
				
			}
			dao.createOrUpdate(grupo);

			
		} catch (IOException e) {
			throw new ArqException(e);
		}finally{
			dao.close();
		}
		
	}
	
	/**
	 * Remove o grupo, atividades e arquivos associados.
	 * @param mov
	 * @throws ArqException
	 */
	public void remover(Movimento mov) throws ArqException{
	
		GenericDAO dao = getGenericDAO(mov);
		MovimentoGrupoAtividadesAP movGrupo = (MovimentoGrupoAtividadesAP) mov;
		GrupoAtividadesAtualizacaoPedagogica grupo = movGrupo.getObjMovimentado();
	
		try{
			grupo = dao.refresh(grupo);
			for (AtividadeAtualizacaoPedagogica a : grupo.getAtividades()) {
				if( !isEmpty(a.getIdArquivo()) )
					EnvioArquivoHelper.removeArquivo(a.getIdArquivo());
			}
			dao.remove(grupo);
			
		}finally{
			dao.close();
		}
		
	}

	/**
	 * Realiza as verificações de negócio que envolvem a persistência dos 
	 * dados do grupo de atividades.
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoGrupoAtividadesAP movGrupo = (MovimentoGrupoAtividadesAP) mov;
		GrupoAtividadesAtualizacaoPedagogica grupo = movGrupo.getObjMovimentado();
		ListaMensagens erros = grupo.validate();
		
		checkValidation(erros);
		
	}

}
