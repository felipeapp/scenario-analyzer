/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 12/02/2007 
 *
 */
package br.ufrn.sigaa.portal.negocio;

import java.io.IOException;
import java.rmi.RemoteException;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.portal.dominio.PerfilDiscente;

/**
 * Processador para atualizar o perfil de um docente
 *
 * @author ricardo
 *
 */
public class ProcessadorPerfilDiscente extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate(mov);
		PerfilDiscenteMov perfilMov = (PerfilDiscenteMov) mov;
		PerfilDiscente perfil = perfilMov.getPerfilDiscente();

		GenericDAO dao = getDAO(mov);
//		 Buscar dados atualizados do usuário logado
		Usuario usuario = dao.findByPrimaryKey(
				mov.getUsuarioLogado().getId(), Usuario.class);

		try {
			/* Atualizar foto do usuário */
			if (perfilMov.getFoto() != null) {

				// Remover foto anterior
				if (usuario.getIdFoto() != null) {
					EnvioArquivoHelper.removeArquivo(usuario.getIdFoto());
				}

				//redimensiona a foto do usuário
				byte[] fotoRedimensionada = UFRNUtils.redimensionaJPG(perfilMov.getFoto().getBytes(), PerfilPessoa.WIDTH_FOTO, PerfilPessoa.HEIGTH_FOTO);

				// Armazenar arquivo com a foto
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo,fotoRedimensionada,
						perfilMov.getFoto().getContentType(), perfilMov.getFoto().getName());

				// Atualizar usuário
				usuario.setIdFoto(idArquivo);
				dao.update(usuario);

				// Definir foto para o usuário em sessão
				((Usuario) mov.getUsuarioLogado()).setIdFoto(idArquivo);

				dao.updateField(Discente.class, usuario.getDiscenteAtivo().getId(), "idFoto", idArquivo);

			}

			/* Criar ou atualizar o perfil do docente */
			if (perfil.getId() == 0) {
				dao.create(perfilMov.getPerfilDiscente());
			} else {
				dao.update(perfilMov.getPerfilDiscente());
			}

			dao.updateField(Discente.class, usuario.getDiscenteAtivo().getId(), "perfil", perfilMov.getPerfilDiscente().getId());

		} catch (IOException e) {
			throw new ArqException("Erro na recuperação da foto definida", e);
		}

		return perfilMov;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	
	}

}
