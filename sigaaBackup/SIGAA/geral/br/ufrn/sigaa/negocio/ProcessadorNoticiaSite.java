/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Aug 26, 2008
 *
 */
package br.ufrn.sigaa.negocio;

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
import br.ufrn.arq.util.ImageUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.sites.dominio.NoticiaSite;

/**
 * A classe é responsável em persistir os dados referente ao 
 * caso de uso notícia de um site
 * @author Victor Hugo
 */
public class ProcessadorNoticiaSite extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		if(movimento.getCodMovimento() == SigaaListaComando.ATUALIZAR_NOTICIA_SITE ){
			validate(movimento);
			atualizarNoticiaSite(movimento);
		} else if( movimento.getCodMovimento() == SigaaListaComando.REMOVER_ARQUIVO_NOTICIA_SITE ){
			removerNoticiaSite(movimento);
		}
		
		return movimento;
	}
	
	/**
	 * Atualiza os dados de uma notícia previamente cadastrada
	 * @param movimento
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void atualizarNoticiaSite(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoNoticiaSite mov = (MovimentoNoticiaSite) movimento;
		NoticiaSite noticias = mov.getNoticiaSite();
		GenericDAO dao = getDAO(mov);
		
		try {
			
			/* Atualizar foto da notícia */
			if (mov.getFoto() != null) {
				// Remover foto anterior
				if (noticias.getIdFoto() != null) {
					EnvioArquivoHelper.removeArquivo(noticias.getIdFoto());
				}
				
				//redimensiona a imagem da apresentação do portal
				
				byte[] fotoRedimensionada = ImageUtils.redimensionaImagem(mov.getFoto().getBytes(),
						NoticiaSite.FOTO_WIDTH_MAX, NoticiaSite.FOTO_HEIGHT_MAX);

				// Armazenar arquivo com a foto
				int idArquivoFoto = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivoFoto, fotoRedimensionada,
						mov.getFoto().getContentType(), mov.getFoto().getName());

				// Atualizar usuário
				noticias.setIdFoto(idArquivoFoto);
			}
			
			if(noticias.getIdArquivo()!=null && mov.getArquivo() != null){
				EnvioArquivoHelper.removeArquivo(noticias.getIdArquivo());
				noticias.setIdArquivo(null);
			}
			
			/* Atualizar arquivo */
			if (mov.getArquivo() != null) {
				// Remover foto anterior
				if (noticias.getIdArquivo() != null) {
					EnvioArquivoHelper.removeArquivo(noticias.getIdArquivo());
				}

				// Armazenar arquivo 
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, mov.getArquivo().getBytes(),
						mov.getArquivo().getContentType(), mov.getArquivo().getName());

				// Atualizar usuário
				noticias.setIdArquivo(idArquivo);
			}
			

			/* Criar ou atualizar o perfil do docente */
			if (noticias.getId() == 0) {
				dao.create(noticias);
			} else {
				dao.update(noticias);
			}

		} catch (IOException e) {
			throw new ArqException("Erro na recuperação da foto definida", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException("Erro na atualização dos noticias.");
		}
	}
	
	/**
	 * Remove o arquivo anexado a notícia
	 * @param movimento
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public String removerNoticiaSite(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		  
		MovimentoNoticiaSite mov = (MovimentoNoticiaSite) movimento;
		NoticiaSite noticias = mov.getNoticiaSite();
		  
		if(noticias.getIdArquivo() != null){
				EnvioArquivoHelper.removeArquivo(noticias.getIdArquivo());
		}
		getGenericDAO(mov).remove(mov.getNoticiaSite());
        return null;
          
	 }
	
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		checkRole(new int[] {
				SigaaPapeis.SECRETARIA_POS,SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.SECRETARIA_DEPARTAMENTO,SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.SECRETARIA_COORDENACAO,SigaaPapeis.SECRETARIA_GRADUACAO,
				SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR}, movimento);
		MovimentoNoticiaSite mov = (MovimentoNoticiaSite) movimento;
		NoticiaSite noticias = mov.getNoticiaSite();
		ListaMensagens erros = noticias.validate();
		
		if(!isEmpty(noticias.getUnidade()) && noticias.getUnidade().getId()>0)
			noticias.setCurso(null);
		else
			noticias.setUnidade(null);
		
		checkValidation(erros);
	}

}
