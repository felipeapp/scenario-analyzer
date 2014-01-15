/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 14/04/2010
 */
package br.ufrn.integracao.servicos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.arq.dao.PermissaoDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.integracao.dto.PermissaoDTO;
import br.ufrn.integracao.dto.UsuarioDTO;
import br.ufrn.integracao.interfaces.AutenticacaoUsuarioRemoteService;

/**
 * Implementação do serviço remoto para autenticação de 
 * usuários em aplicações Desktop.
 * 
 * @author David Pereira
 *
 */
@WebService
public class AutenticacaoUsuarioRemoteServiceImpl implements AutenticacaoUsuarioRemoteService {

	@Override
	public boolean autenticaUsuario(String login, String senha) {
		try {
			return UserAutenticacao.autenticaUsuario(null, login, senha);
		} catch (ArqException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public UsuarioDTO carregaInfoUsuario(String login){
			return null;
	}

	@Override
	public List<PermissaoDTO> carregaPermissoes(UsuarioDTO usuario) {
		PermissaoDAO pDao = new PermissaoDAO();
		
		try {
			List<Permissao> permissoes = pDao.findPermissaosAtivosByUsuario(usuario.getId(), new Date());
			List<PermissaoDTO> resultado = new ArrayList<PermissaoDTO>();
			for (Permissao p : permissoes) {
				PermissaoDTO dto = null;//DtoUtils.dePermissaoParaDTO(p);
				resultado.add(dto);
			}
			return resultado;
		} finally {
			pDao.close();
		}
	}

}
