/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/12/2011
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.tecnico.dao.PessoaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.ImportacaoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.LeiauteArquivoImportacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.MapeamentoAtributoCampoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.PessoaTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ResultadoClassificacaoCandidatoTecnico;

/**
 * Processador responsável pela efetivação do cadastro de discentes
 * aprovados em processo seletivo de nível técnico.
 *  
 * @author Édipo Elder F. de Melo
 * @author Fred_Castro
 *
 */
public class ProcessadorImportaAprovadosTecnico extends
		AbstractProcessador {

	/** Executa o cadastramento dos dados.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		MovimentoCadastro movimento = ((MovimentoCadastro) mov);
		if (movimento.getCodMovimento().equals(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO)) {
			importaAprovados(movimento);
		} else {
			// cadastra o leiaute do arquivo de importação
			GenericDAO dao = getGenericDAO(mov);
			try {
				LeiauteArquivoImportacaoTecnico leiaute = movimento.getObjMovimentado();
				for (MapeamentoAtributoCampoTecnico mapeamento : leiaute.getMapeamentoAtributos())
					mapeamento.setLeiauteArquivoImportacao(leiaute);
				// se estiver atualizando o leiaute, remove os mapeamentos removidos pelo usuário
				if (leiaute.getId() > 0) {
					LeiauteArquivoImportacaoTecnico leiauteBanco = dao.findByPrimaryKey(leiaute.getId(), LeiauteArquivoImportacaoTecnico.class);
					dao.detach(leiauteBanco);
					for (MapeamentoAtributoCampoTecnico mapeamento : leiauteBanco.getMapeamentoAtributos()) {
						if (!leiaute.getMapeamentoAtributos().contains(mapeamento)) {
							dao.remove(mapeamento);
						}
					}
				}
				dao.createOrUpdate(leiaute);
				for (MapeamentoAtributoCampoTecnico mapeamento : leiaute.getMapeamentoAtributos()) {
					if (mapeamento.getId() == 0)
						dao.create(mapeamento);
				}
			} finally {
				dao.close();
			}
		}
		return null;
	}

	/** Importa os discentes aprovados em outros concursos
	 * @param movimento
	 * @throws DAOException
	 */
	private void importaAprovados(MovimentoCadastro movimento) throws DAOException {
		PessoaTecnicoDao dao = null;
		try {
			dao = getDAO(PessoaTecnicoDao.class, movimento);
					
			ImportacaoDiscenteTecnico importacaoDiscentes = (ImportacaoDiscenteTecnico) movimento.getObjMovimentado();
			// persiste o resultado importado
			for ( ResultadoClassificacaoCandidatoTecnico resultado : importacaoDiscentes.getResultadosImportados() ) {

				// persiste dados pessoais que não estão no sistema
				if (resultado.getInscricaoProcessoSeletivo().getPessoa().getId() == 0) {
					PessoaTecnico pessoaTecnico = resultado.getInscricaoProcessoSeletivo().getPessoa();
					pessoaTecnico.anularAtributosVazios();
					dao.create(resultado.getInscricaoProcessoSeletivo().getPessoa());
				}
				dao.create(resultado.getInscricaoProcessoSeletivo());
				dao.create(resultado);
			}
			dao.create(importacaoDiscentes);
		} finally {
			if (dao != null) dao.close();
		}
	}

	/** Valida a execução do processador.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

// TODO DEFINIR OS PAPEIS
//		int papeis[] = {SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.ADMINISTRADOR_SIGAA};
//		checkRole(papeis, mov);
		ListaMensagens lista = new ListaMensagens();
		MovimentoCadastro movimento = ((MovimentoCadastro) mov);
		if (movimento.getCodMovimento().equals(SigaaListaComando.IMPORTAR_APROVADOS_TECNICO)) {
			ImportacaoDiscenteTecnico importacaoDiscentes = (ImportacaoDiscenteTecnico) movimento.getObjMovimentado();
			validateRequired(importacaoDiscentes.getResultadosImportados(), "Resultados", lista);
		} else {
		}
		checkValidation(lista);
	}

}