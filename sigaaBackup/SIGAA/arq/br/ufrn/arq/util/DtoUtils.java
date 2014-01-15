/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 14/05/2010
 */
package br.ufrn.arq.util;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.integracao.dto.PermissaoDTO;
import br.ufrn.integracao.dto.UsuarioDTO;

/**
 * Métodos utilitários para transformar classes em seus
 * DTOs equivalentes e vice-versa.
 * 
 * @author David Pereira
 *
 */
public class DtoUtils {

	/**
	 * Transforma um objeto do tipo UsuarioGeral em um objeto do tipo InfoUsuarioDTO
	 * @param infoUsuario
	 * @return
	 */
	public static UsuarioDTO deUsuarioParaDTO(UsuarioGeral usuario) {
		UsuarioDTO dto = new UsuarioDTO();
		dto.setId(usuario.getId());
		dto.setLogin(usuario.getLogin());
		dto.setEmail(usuario.getEmail());
		dto.setNome(usuario.getNome());
		dto.setInativo(usuario.isInativo());
		if (usuario.getPessoa() != null) {
			dto.setIdPessoa(usuario.getPessoa().getId());
			dto.setCpfCnpj(usuario.getPessoa().getCpf_cnpj());
		}
		if (usuario.getUnidade() != null)
			dto.setIdUnidade(usuario.getUnidade().getId());
		
		List<PermissaoDTO> permissoes = new ArrayList<PermissaoDTO>();
		if (dto.getPermissoes() != null){
			for (Permissao p : usuario.getPermissoes()) {
				PermissaoDTO pdto = DtoUtils.dePermissaoParaDTO(p);
				permissoes.add(pdto);
			}
		}
		dto.setPermissoes(permissoes);
		return dto;
	}

	/**
	 * Transforma um objeto do tipo Permissao em um objeto do tipo PermissaoDTO
	 * @param p
	 * @return
	 */
	public static PermissaoDTO dePermissaoParaDTO(Permissao p) {
		PermissaoDTO dto = new PermissaoDTO();
		dto.setId(p.getId());
		dto.setIdPapel(p.getPapel().getId());
		dto.setIdUsuario(p.getUsuario().getId());
		dto.setDesignacao(p.getDesignacao());
		dto.setLoginUsuario(p.getUsuario().getLogin());
		dto.setNomePapel(p.getPapel().getNome());
		if (p.getUnidadePapel() != null)
			dto.setUnidadePermissao(p.getUnidadePapel().getId());
		return dto;
	}

	/**
	 * Transforma um objeto do tipo UsuarioDTO em um objeto do tipo UsuarioGeral
	 * @param usuario
	 * @return
	 */
	public static UsuarioGeral deDtoParaUsuario(UsuarioDTO dto) {
		UsuarioGeral usuario = new UsuarioGeral();
		usuario.setId(dto.getId());
		usuario.setLogin(dto.getLogin());
		usuario.setEmail(dto.getEmail());
		usuario.setNome(dto.getNome());
		usuario.setCpf(dto.getCpfCnpj());
		usuario.setInativo(dto.isInativo());
		if (dto.getIdPessoa() != null)
			usuario.setPessoa(new PessoaGeral(dto.getIdPessoa()));
		
		if (dto.getIdUnidade() != null)
			usuario.setUnidade(new UnidadeGeral(dto.getIdUnidade()));
		
		List<Permissao> permissoes = new ArrayList<Permissao>();
		if (dto.getPermissoes() != null){
			for (PermissaoDTO pdto : dto.getPermissoes()) {
				Permissao p = deDTOParaPermissao(pdto);
				permissoes.add(p);
			}			
		}
		usuario.setPermissoes(permissoes);
		return usuario;
	}

	/**
	 * Transforma um objeto do tipo PermissaoDTO em um objeto do tipo Permissao
	 * @param pdto
	 * @return
	 */
	private static Permissao deDTOParaPermissao(PermissaoDTO pdto) {
		Permissao p = new Permissao();
		p.setId(pdto.getId());
		p.setPapel(new Papel(pdto.getIdPapel()));
		p.setUsuario(new UsuarioGeral(pdto.getIdUsuario()));
		p.setDesignacao(pdto.getDesignacao());
		p.getUsuario().setLogin(pdto.getLoginUsuario());
		p.getPapel().setNome(pdto.getNomePapel());
		if (pdto.getUnidadePermissao() != null)
			p.setUnidadePapel(new UnidadeGeral(pdto.getUnidadePermissao()));
		return p;
	}
	
}
