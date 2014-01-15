/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/08/2008
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoVestibularDao;
import br.ufrn.sigaa.arq.dao.vestibular.IsentoTaxaInscricaoDao;
import br.ufrn.sigaa.questionario.negocio.ProcessadorQuestionarioRespostas;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/** Processador responsável pela efetivação da inscrição de candidatos ao Vestibular.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorInscricaoVestibular extends ProcessadorCadastro {

	/** Processa a inscrição do candidato.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException,
			ArqException, RemoteException {
		validate(movimento);
		MovimentoInscricaoVestibular miv = (MovimentoInscricaoVestibular) movimento;
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class, movimento);
		try {
			InscricaoVestibular inscricao = miv.getInscricaoVestibular();
			validaInscricaoIsento(inscricao, movimento);
			int numeroInscricao = dao.getNextSeq("vestibular", "inscricao_vest_seq");
			inscricao.setNumeroInscricao(numeroInscricao);
			// se houver taxa de inscrição, cria uma GRU
			if (inscricao.getProcessoSeletivo().getValorInscricao() > 0) {
				// cria uma GRU para pagamento da taxa de inscrição
				ProcessoSeletivoVestibular ps = inscricao.getProcessoSeletivo();
				String instrucoes = "Edital: " + ps.getNome()
						+ "\nNº de Inscrição: "
						+ inscricao.getNumeroInscricao() + "\nPrimeira Opção: "
						+ inscricao.getOpcoesCurso()[0].getDescricao()
						+ "\nLíngua Estrangeira: "
						+ inscricao.getLinguaEstrangeira().getDenominacao();
				// Configuração da GRU
				GuiaRecolhimentoUniao gru ;
				SimpleDateFormat formato = new SimpleDateFormat("MM/yyyy");
				String competencia = formato.format(ps.getDataVencimentoBoleto());
				gru = GuiaRecolhimentoUniaoHelper.createGRU(
						ps.getIdConfiguracaoGRU(),
						inscricao.getPessoa().getCpf_cnpj(),
						inscricao.getPessoa().getNome(),
						inscricao.getPessoa().getEnderecoContato().getDescricao(),
						instrucoes, 
						competencia,
						ps.getDataVencimentoBoleto(), 
						ps.getValorInscricao());
				inscricao.setIdGRU(gru.getId());
			}
			dao.create(inscricao.getPessoa());
			dao.create(inscricao);
			if (miv.getRespostasQuestionario() != null) {
				miv.getRespostasQuestionario().setInscricaoVestibular(inscricao);
				ProcessadorQuestionarioRespostas proc = new ProcessadorQuestionarioRespostas();
				proc.cadastrarRespostas(miv, miv.getRespostasQuestionario());
			}
			ProcessadorPessoaVestibular proc = new ProcessadorPessoaVestibular();
			proc.enviaEmailInscricao(inscricao.getPessoa(), miv.getSenha());
			return inscricao;
		} finally {
			dao.close();
		}
	}
	
	/** Valida automaticamente inscrição realizada por isento.
	 * @param inscricao
	 * @param dao
	 * @throws DAOException 
	 */
	private void validaInscricaoIsento(InscricaoVestibular inscricao, Movimento movimento) throws DAOException {
		IsentoTaxaInscricaoDao dao = null; 
		InscricaoVestibularDao inscricaoDao  = null;
		
		try{
			dao = getDAO(IsentoTaxaInscricaoDao.class, movimento);
			inscricaoDao = getDAO(InscricaoVestibularDao.class, movimento);
			// se for isento
			if (dao.isCpfInscrito(inscricao.getPessoa().getCpf_cnpj(), inscricao.getProcessoSeletivo().getId())) {
				// invalida as inscrições anteriores
				Collection<InscricaoVestibular> anteriores = inscricaoDao.findByPessoaVestibular(inscricao.getPessoa().getId(), inscricao.getProcessoSeletivo().getId());
				if (!ValidatorUtil.isEmpty(anteriores)) {
					for (InscricaoVestibular anterior : anteriores) {
						if (anterior.isValidada()) {
							anterior.setValidada(false);
							dao.update(anterior);
						}
					}
				}
				// seta a inscrição atual como validada
				inscricao.setValidada(true);
			} else {
				inscricao.setValidada(false);
			}
		
		}finally{
			if(dao != null) dao.close();
			if(inscricaoDao != null) inscricaoDao.close();
		}
		
	}

	/** Valida os dados necessários para a inscrição.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		InscricaoVestibular inscricao = ((MovimentoInscricaoVestibular) mov).getInscricaoVestibular();
		ListaMensagens erros = inscricao.validate();
		if (erros.isErrorPresent())
			throw new NegocioException(erros);
	}
}
