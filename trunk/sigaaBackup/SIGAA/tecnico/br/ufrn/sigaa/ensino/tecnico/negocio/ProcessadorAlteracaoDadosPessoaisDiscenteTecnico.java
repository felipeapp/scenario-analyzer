/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/12/2010
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.PessoaTecnico;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador responsável por persistir as informações do cadastramento de discentes.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorAlteracaoDadosPessoaisDiscenteTecnico extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro m = (MovimentoCadastro) mov;
		
		Pessoa p = m.getObjMovimentado();
		
		GenericDAO dao = null;
		try {
			dao = getGenericDAO(m);
			
			dao.updateFields(Pessoa.class, p.getId(), new String [] {"nome", "nomeAscii", "email", "cpf_cnpj"}, new Object [] { p.getNome(), StringUtils.toAscii(p.getNome().trim().toUpperCase()), p.getEmail(), p.getCpf_cnpj() });
			
			ConvocacaoProcessoSeletivoDiscenteTecnico conv = dao.findByExactField(ConvocacaoProcessoSeletivoDiscenteTecnico.class, "discente.discente.pessoa.id", p.getId(), true);
			PessoaTecnico pt = conv.getInscricaoProcessoSeletivo().getPessoa();
			
			dao.updateFields(PessoaTecnico.class, pt.getId(), new String [] {"nome", "nomeAscii", "email", "cpf_cnpj"}, new Object [] { p.getNome(), StringUtils.toAscii(p.getNome().trim().toUpperCase()), p.getEmail(), p.getCpf_cnpj() });
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}
}
