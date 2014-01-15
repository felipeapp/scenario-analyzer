/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '04/04/2007'
 *
 */
package br.ufrn.sigaa.eleicao.negocio;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.eleicao.dominio.Candidato;

/**
 * Processador responsável por cadastrar um candidato para eleição de centro
 * necessário pois possui foto
 * 
 * @author Victor Hugo
 *
 */
public class ProcessadorCandidato extends AbstractProcessador {

	public Object execute(Movimento cMov) throws NegocioException, ArqException, RemoteException {

		validate(cMov);
		CandidatoMov mov = (CandidatoMov) cMov;
		Candidato candidato = (Candidato) mov.getObjMovimentado();

		if (candidato.getServidor().getId() == 0)
			candidato.setServidor(null);
		
		GenericDAO dao = getGenericDAO(mov);
		try {
			
			//setando campos default
			candidato.setDataCadastro(new Date());
			candidato.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
			
			/* Atualizar foto do usuário */
			if (mov.getFoto() != null) {

				// Buscar dados atualizados do usuário logado
				//Usuario usuario = (Usuario) dao.findByPrimaryKey(
					//	mov.getUsuarioLogado().getId(), Usuario.class);

				// Remover foto anterior
				/*if (usuario.getIdFoto() != null) {
					EnvioArquivoHelper.removeArquivo(usuario.getIdFoto());
				}*/

				// Armazenar arquivo com a foto
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, mov.getFoto().getBytes(),
						mov.getFoto().getContentType(), mov.getFoto().getName());

				// Atualizar usuário
				candidato.setIdFoto(idArquivo);
				

			}
			
			dao.create(candidato);

			/* Criar ou atualizar o perfil do docente */
			/*if (perfil.getId() == 0) {
				dao.create(perfilMov.getPerfilDiscente());
			} else {
				dao.update(perfilMov.getPerfilDiscente());
			}*/

		} catch (IOException e) {
			throw new NegocioException("Erro na recuperação da foto definida");
		} finally {
			dao.close();
		}

		return mov;
		
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
