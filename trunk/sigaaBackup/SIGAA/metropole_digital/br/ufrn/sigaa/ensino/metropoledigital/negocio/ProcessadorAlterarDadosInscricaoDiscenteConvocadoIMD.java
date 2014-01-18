package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaCandidato;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaGrupo;
import br.ufrn.sigaa.ensino.tecnico.dao.CadastramentoDiscenteTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dao.PessoaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.InscricaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.PessoaTecnico;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
/***
 * 
 * Processador responsável pelo processamentos dos objetos a serem alterados nos dados pessoais dos discentes convocados do IMD.
 * 
 * @author Rafael Barros
 *
 */
public class ProcessadorAlterarDadosInscricaoDiscenteConvocadoIMD extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoAlterarDadosDiscenteConvocadoIMD m = (MovimentoAlterarDadosDiscenteConvocadoIMD) mov;
		
		InscricaoProcessoSeletivoTecnico insc = m.getInscricao();
		Pessoa p = m.getPessoa();
		DiscenteTecnico disc = m.getDiscenteTecnico();
		ReservaVagaCandidato reservaCandidato = m.getReservaCandidato();
		long cpfCnpjAtual = m.getCpfCnpjAtual();
		
		CadastramentoDiscenteTecnicoDao dao = new CadastramentoDiscenteTecnicoDao();
		PessoaTecnicoDao pTecDao = new PessoaTecnicoDao();
		PessoaDao pDao = new PessoaDao();
		
		try {
			
			//SALVAR OS DADOS PESSOAIS EM PESSOA E PESSOA TECNICO
			PessoaTecnico pTec = pTecDao.findByCPF(cpfCnpjAtual);
			Pessoa pAux = pDao.findByCpf(cpfCnpjAtual);
			dao.updateFields(Pessoa.class, pAux.getId(), new String [] {"nome", "nomeAscii", "email", "cpf_cnpj"}, new Object [] { p.getNome(), StringUtils.toAscii(p.getNome().trim().toUpperCase()), p.getEmail(), p.getCpf_cnpj() });
			dao.updateFields(PessoaTecnico.class, pTec.getId(), new String [] {"nome", "nomeAscii", "email", "cpf_cnpj"}, new Object [] { p.getNome(), StringUtils.toAscii(p.getNome().trim().toUpperCase()), p.getEmail(), p.getCpf_cnpj() });
			
			// SALVAR OS DADOS ATUALIZADOS DA INSCRIÇÃO
			ReservaVagaGrupo grupo = insc.getGrupo();
			insc.setBaixaRenda(grupo.isBaixaRenda());
			insc.setEscolaPublica(grupo.isEscolaPublica());
			insc.setEtnia(grupo.isEtnia());
			dao.update(insc);
			
			// SALVAR OS DADOS DO DISCENTE
			dao.update(disc);
			
			// SALVAR OS DADOS DA RESERVA VAGA CANDIDATO
			dao.update(reservaCandidato);
			
		} finally {
			dao.close();
			pDao.close();
			pTecDao.close();
		}
		
		return null;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
	
}
