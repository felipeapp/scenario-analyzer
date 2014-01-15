/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 14/12/2006 
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
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.portal.dominio.PerfilServidor;

/**
 * Processador para atualizar o perfil de um docente
 *
 * @author ricardo
 *
 */
public class ProcessadorPerfilDocente extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate(mov);
		PerfilDocenteMov perfilMov = (PerfilDocenteMov) mov;
		PerfilServidor perfil = perfilMov.getPerfilDocente();

		GenericDAO dao = getDAO(mov);

		try {
//			 Buscar dados atualizados do usuário logado
			Usuario usuario = dao.findByPrimaryKey(
					mov.getUsuarioLogado().getId(), Usuario.class);

			/* Atualizar foto do usuário */
			if (perfilMov.getFoto() != null) {



				try {
					// Remover foto anterior
					if (usuario.getIdFoto() != null) {
						EnvioArquivoHelper.removeArquivo(usuario.getIdFoto());
					}
				} catch (Exception e) {}

				byte[] fotoRedimensionada = UFRNUtils.redimensionaJPG(perfilMov.getFoto().getBytes(), PerfilPessoa.WIDTH_FOTO, PerfilPessoa.HEIGTH_FOTO);

				// Armazenar arquivo com a foto
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, fotoRedimensionada,
						perfilMov.getFoto().getContentType(), perfilMov.getFoto().getName());

				// Atualizar usuário
				usuario.setIdFoto(idArquivo);




				// Definir foto para o usuário em sessão
				((Usuario) mov.getUsuarioLogado()).setIdFoto(idArquivo);

				// Atualiza a foto no servidor
				dao.updateField(Usuario.class, usuario.getId(), "idFoto", idArquivo);
				dao.updateField(Servidor.class, usuario.getServidor().getId(), "idFoto", idArquivo);

			}
			dao.clearSession();

			/* Criar ou atualizar o perfil do docente */
			if (perfil.getId() == 0) {
				dao.create(perfil);
			} else {
				dao.update(perfil);
			}

			dao.updateField(Servidor.class, usuario.getServidor().getId(), "perfil", perfilMov.getPerfilDocente().getId());


		} catch (IOException e) {
			throw new ArqException("Erro na recuperação da foto definida", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException("Erro na atualização do perfil do docente");
		}

		return perfilMov;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
