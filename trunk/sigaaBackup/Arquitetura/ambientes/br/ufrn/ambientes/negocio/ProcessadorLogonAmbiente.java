/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2010
 */
package br.ufrn.ambientes.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.ambientes.dao.UsuarioAmbienteDao;
import br.ufrn.ambientes.dominio.ConstantesParametrosAmbientes;
import br.ufrn.ambientes.dominio.RegistroEntradaAmbiente;
import br.ufrn.ambientes.dominio.UsuarioAmbiente;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;

/**
 * Processador para registrar o logon
 * de um usuário em um tipo de ambiente.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class ProcessadorLogonAmbiente extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoLogonAmbiente mla = (MovimentoLogonAmbiente) mov;
		
		UsuarioAmbienteDao dao = getDAO(UsuarioAmbienteDao.class, mov);
		
		try {
		int ambiente = ParametroHelper.getInstance().getParametroInt(ConstantesParametrosAmbientes.TIPO_AMBIENTE_ATUAL);
			UsuarioAmbiente usuario = dao.findByLogin(mla.getLogin(), ambiente);
			
			String senha = UFRNUtils.toMD5(mla.getSenha(), ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.SALT_SENHAS_USUARIOS));
			
			if (usuario != null && usuario.getSenha().equals(senha)) {
				RegistroEntradaAmbiente registro = new RegistroEntradaAmbiente();
				registro.setData(new Date());
				registro.setIp(mla.getIp());
				registro.setIpInternoNat(mla.getIpInternoNat());
				registro.setServer(mla.getServer());
				registro.setUserAgent(mla.getUserAgent());
				registro.setResolucao(mla.getResolucao());
				registro.setUsuario(usuario);
				
				dao.create(registro);
				
				usuario.setEntradaAmbiente(registro);
			} else {
				throw new NegocioException("Usuário ou senha inválidos.");
			}
			
			return usuario;
			
		} finally {
			dao.close();
		}
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
