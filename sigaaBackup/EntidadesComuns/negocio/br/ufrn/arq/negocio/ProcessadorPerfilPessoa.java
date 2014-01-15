/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 2007/10/05
 */
package br.ufrn.arq.negocio;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.imageio.IIOException;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.negocio.MovimentoPerfilPessoa;

/**
 * Processador para cadastrar um perfil de uma pessoa
 * 
 * @author David Pereira
 *
 */
public class ProcessadorPerfilPessoa extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		MovimentoPerfilPessoa perfilMov = (MovimentoPerfilPessoa) mov;
		PerfilPessoa perfil = perfilMov.getPerfil();
		
		PerfilPessoaDAO dao = PerfilPessoaDAO.getDao();
		UsuarioGeral usuario = mov.getUsuarioLogado();
		try {
			Integer idArquivo = null;
			
			/* Atualizar foto do usuário */
			if (perfilMov.getFoto() != null || perfilMov.getIdFoto()!= null) {

				// Remover foto anterior
				try {
					if (perfilMov.getIdFoto() != null)
						EnvioArquivoHelper.removeArquivo(perfilMov.getIdFoto());
				} catch (Exception e) {}

				if (perfilMov.getFoto() != null){
					byte[] fotoRedimensionada = UFRNUtils.redimensionaJPG(perfilMov.getFoto().getBytes(),
						PerfilPessoa.WIDTH_FOTO, PerfilPessoa.HEIGTH_FOTO);

					// Armazenar arquivo com a foto
					idArquivo = EnvioArquivoHelper.getNextIdArquivo();
					EnvioArquivoHelper.inserirArquivo(idArquivo, fotoRedimensionada, 
							perfilMov.getFoto().getContentType(), perfilMov.getFoto().getName());
				}
				
				if(idArquivo != null || perfilMov.getIdFoto()!= null){
					// Definir foto para o usuário em sessao
					usuario.setIdFoto(idArquivo);
					// Atualizar usuário
					dao.atualizaFotoUsuario(usuario.getId(), idArquivo);
				}
				
			}
			
			/* Criar ou atualizar o perfil do usuário */
			if (perfil.getId() == 0) {
				dao.create(perfil);
			} else {
				dao.update(perfil);
			}

			// Atualiza o perfil do usuário de acordo com o seu tipo
			if (perfil.getIdServidor() != null) {
				dao.atualizaPerfilServidor(perfil.getIdServidor(), perfil, idArquivo);
			} else if (perfil.getIdDiscente() != null) {
				dao.atualizaPerfilDiscente(perfil.getIdDiscente(), perfil, idArquivo);
			} else if (perfil.getIdDocenteExterno() != null) {
				dao.atualizaPerfilDocenteExterno(perfil.getIdDocenteExterno(), perfil, idArquivo);
			} else if (perfil.getIdPessoa() != null) {
				dao.atualizaPerfilPessoa(perfil.getIdPessoa(), perfil);
			} else {
				dao.atualizaPerfilTutor(perfil.getIdTutor(), perfil, idArquivo);
			}

		} catch (IIOException e) {
			throw new NegocioException("Erro no carregamento da foto. Verifique se o arquivo submetido corresponde a uma imagem!");
		} catch (IOException e) {
			throw new ArqException("Erro na recuperação da foto definida", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException("Erro na atualização do perfil", e);
		}

		return perfilMov;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
