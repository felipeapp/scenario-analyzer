/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.ensino.tecnico.dao.PessoaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.PessoaTecnico;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador respons�vel por persistir as informa��es do cadastramento de discentes.
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
		PessoaTecnicoDao pTecDao = new PessoaTecnicoDao();
		PessoaDao pDao = new PessoaDao();
		
		try {
			dao = getGenericDAO(m);
			
			PessoaTecnico pTec = pTecDao.findByCPF(p.getCpf_cnpj());
			Pessoa pAux = pDao.findByCpf(p.getCpf_cnpj());
			
			dao.updateFields(Pessoa.class, pAux.getId(), new String [] {"nome", "nomeAscii", "email", "cpf_cnpj"}, new Object [] { p.getNome(), StringUtils.toAscii(p.getNome().trim().toUpperCase()), p.getEmail(), p.getCpf_cnpj() });
			
			//ConvocacaoProcessoSeletivoDiscenteTecnico conv = dao.findByExactField(ConvocacaoProcessoSeletivoDiscenteTecnico.class, "discente.discente.pessoa.id", p.getId(), true);
			//PessoaTecnico pt = conv.getInscricaoProcessoSeletivo().getPessoa();
			
			dao.updateFields(PessoaTecnico.class, pTec.getId(), new String [] {"nome", "nomeAscii", "email", "cpf_cnpj"}, new Object [] { p.getNome(), StringUtils.toAscii(p.getNome().trim().toUpperCase()), p.getEmail(), p.getCpf_cnpj() });
			
		} finally {
			if (dao != null || pTecDao != null || pDao != null) {
				dao.close();
				pTecDao.close();
				pDao.close();
			}
		}
		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}
}