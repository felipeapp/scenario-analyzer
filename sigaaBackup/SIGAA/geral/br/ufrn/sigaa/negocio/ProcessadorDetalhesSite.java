/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on Aug 26, 2008
 *
 */
package br.ufrn.sigaa.negocio;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ImageUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.site.DetalhesSiteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.sites.dominio.DetalhesSite;

/**
 * Classe responsável em processar o movimento relacionado a detalhes de uma unidade (programa ou departamento)
 * @author Victor Hugo
 */
public class ProcessadorDetalhesSite extends AbstractProcessador {

	/**
	 * Método definido na arquitetura
	 * JSP: Não é chamada
	 */
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		validate(movimento);
		if(movimento.getCodMovimento() == SigaaListaComando.ATUALIZAR_DETALHES_SITE ){
			atualizarDetalhesSite(movimento);
		} else if( movimento.getCodMovimento() == SigaaListaComando.REMOVER_ARQUIVO_DETALHES_SITE ){
			removerArquivoDetalhesSite(movimento);
		}
		
		return movimento;
	}
	
	/**
	 * Método responsável por inserir ou alterar os detalhes de uma unidade (programa de pós ou departamento)
	 * JSP: Não é chamada
	 * @param movimento
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void atualizarDetalhesSite(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoDetalhesSite mov = (MovimentoDetalhesSite) movimento;
		DetalhesSite detalhes = mov.getDetalhesSite();
		GenericDAO dao = getDAO(DetalhesSiteDao.class, mov);
		
		try {
			
			/* Remove a foto */
			if( !isEmpty(detalhes.getIdFoto()) && mov.isExcluirFoto() ){
				EnvioArquivoHelper.removeArquivo(detalhes.getIdFoto());
				detalhes.setIdFoto(null);
			}
			/* Remove a logo */
			if( !isEmpty(detalhes.getIdLogo()) && mov.isExcluirLogo() ){
				EnvioArquivoHelper.removeArquivo(detalhes.getIdLogo());
				detalhes.setIdLogo(null);
			}
			
			/* Atualizar foto do departamento */
			if (mov.getFoto() != null) {
				//redimensiona a imagem da apresentação do portal
				byte[] fotoRedimensionada = UFRNUtils.redimensionaJPG(mov.getFoto().getBytes(), DetalhesSite.FOTO_WIDTH, DetalhesSite.FOTO_HEIGHT);

				// Armazenar arquivo com a foto
				int idArquivoFoto = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivoFoto, fotoRedimensionada,
						mov.getFoto().getContentType(), mov.getFoto().getName());

				// Atualizar usuário
				detalhes.setIdFoto(idArquivoFoto);
			}
			/* Atualizar logo do departamento */
			if (mov.getLogo() != null) {
				//redimensiona a logo da apresentação do portal
				byte[] logoRedimensionada = ImageUtils.redimensionaImagem(mov.getLogo().getBytes(),
						DetalhesSite.LOGO_WIDTH, DetalhesSite.LOGO_HEIGHT);
				
				// Armazenar arquivo com a foto
				int idArquivoLogo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivoLogo, logoRedimensionada,
						mov.getLogo().getContentType(), mov.getLogo().getName());

				// Atualizar usuário
				detalhes.setIdLogo(idArquivoLogo);
			}

						
			if(detalhes.getSiteProprioObrigatorio() == null || !detalhes.getSiteProprioObrigatorio())
				detalhes.setSiteProprioObrigatorio(false);

			/* Criar ou atualizar o perfil do docente */
			if (detalhes.getId() == 0) {
				dao.create(detalhes);
			} else {
				dao.update(detalhes);
			}

		} catch (IOException e) {
			throw new ArqException("Erro na recuperação da foto definida", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException("Erro na atualização dos detalhes da unidade");
		}finally{
			dao.close();
		}
	}
	
	/**
	 * Remove a foto ou a logo relacionada a uma determinada unidade
	 * JSP: Não é chamada
	 * @param movimento
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public String removerArquivoDetalhesSite(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		  
		MovimentoDetalhesSite mov = (MovimentoDetalhesSite) movimento;
		DetalhesSite detalhes = mov.getDetalhesSite();
		  
		if(detalhes.getIdLogo() != null){
				EnvioArquivoHelper.removeArquivo(detalhes.getIdLogo());
		}else if(detalhes.getIdFoto() != null){
				EnvioArquivoHelper.removeArquivo(detalhes.getIdFoto());
		}
        return null;
          
	 }
	
	/**
	 * Método que verifica se a URL existe na WEB.
	 * JSP: Não é chamada
	 * @param strURL
	 * @return
	 */
	private boolean validaUrl(String strURL){
		
		try {
			URL url = new URL(strURL);
			
			try {
				url.openConnection().getContent();
			} catch (Exception e) {
				return false;
			}
		} catch (IOException e){ 
			return false;	
		}
		return true;
		
	}
	
	/**
	 * Método de validação da arquitetura
	 * JSP: Não é chamada
	 */
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		
		checkRole(new int[] { 
				SigaaPapeis.SECRETARIA_POS,SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.SECRETARIA_DEPARTAMENTO,SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.COORDENADOR_LATO,SigaaPapeis.SECRETARIA_COORDENACAO,SigaaPapeis.DIRETOR_CENTRO,
				SigaaPapeis.COORDENADOR_CURSO,SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.SECRETARIA_GRADUACAO,
				SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR}, movimento);
		
		MovimentoDetalhesSite mov = (MovimentoDetalhesSite) movimento;
		DetalhesSite detalhes = mov.getDetalhesSite();
		ListaMensagens erros = detalhes.validate();

		//Verifica se endereço oficial do programa já existe na base de dados
		if (jaExisteUrl(mov, detalhes, erros))
			erros.addErro("Endereço oficial \""+mov.getDetalhesSite().getUrl()+"\" já está em uso. Escolha outro nome!");
		
		//Verifica se o endereço alternativo do programa existe na WEB 
		if(detalhes.getSiteExtra() != null && detalhes.getSiteExtra().length()>7 && !validaUrl(detalhes.getSiteExtra())){
			erros.addErro("Endereço alternativo \""+mov.getDetalhesSite().getSiteExtra()+
			"\" inválido!");
		}
		
		checkValidation(erros);
	}

	/**
	 * Verifica se já existe URL
	 * 
	 * @param mov
	 * @param detalhes
	 * @param erros
	 * @return
	 * @throws DAOException
	 */
	private boolean jaExisteUrl(MovimentoDetalhesSite mov, DetalhesSite detalhes, ListaMensagens erros) throws DAOException {
		if (detalhes.getUrl() != null){
			
			DetalhesSite det = getDAO(DetalhesSiteDao.class, mov).findByUrl(detalhes.getUrl());
			
			if (det != null) {
				if ( (detalhes.isTipoUnidade() && det.isTipoCurso()) || (detalhes.isTipoCurso() && det.isTipoUnidade()) ) {
					return true;
				} else if (detalhes.isTipoUnidade() && det.isTipoUnidade()) {
					if (detalhes.getUnidade().getId() != det.getUnidade().getId()) {
						return true;
					}
				} else if (detalhes.isTipoCurso() && det.isTipoCurso()) {
					if (detalhes.getCurso().getId() != det.getCurso().getId()) {
						return true;
					}
				}
			}
			
			return false;
		}
		
		return false;
	}

}
