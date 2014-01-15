/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/01/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AlteracaoStatusAluno;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.MudancaCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoMudancaCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.CalculoPrazoMaximoFactory;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.TipoConvocacao;

/**
 * Processador responsável por persistir as informações de uma convocação de
 * candidatos do vestibular para vagas remanescentes.
 * 
 * @author Leonardo Campos
 * 
 */
public class ProcessadorConvocacaoVestibular extends AbstractProcessador {

	/** Cadastra o discente convocado.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		PessoaDao dao = getDAO(PessoaDao.class, mov);
		EstruturaCurricularDao estruturaDao = getDAO(EstruturaCurricularDao.class, mov);
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class, mov);
		
		try{
			
			MovimentoConvocacaoVestibular movimento = (MovimentoConvocacaoVestibular) mov;
			ConvocacaoProcessoSeletivo convocacao = movimento.getObjMovimentado();
			dao.create(convocacao);
			// mapa de últimos curriculos de acordo com a matriz curricular
			Collection<Integer> idMatrizes = new ArrayList<Integer>();
			for(ConvocacaoProcessoSeletivoDiscente conv: movimento.getConvocacoes()){
				idMatrizes.add(conv.getDiscente().getMatrizCurricular().getId());
			}
			Map<Integer, Curriculo> mapa = estruturaDao.findMaisRecentesByMatrizesLeve(idMatrizes);
			
			StringBuffer sb = new StringBuffer();
			for(ConvocacaoProcessoSeletivoDiscente conv: movimento.getConvocacoes()){
				System.out.println("Cadastrando discente: " + conv.getDiscente().getNome());
				conv.setAno(conv.getDiscente().getAnoEntrada());
				conv.setPeriodo(conv.getDiscente().getPeriodoIngresso());
				conv.setMatrizCurricular(conv.getDiscente().getMatrizCurricular());
				if (conv.getConvocacaoAnterior() != null
						&& (conv.getConvocacaoAnterior().getPendenteCancelamento() == null || 
							conv.getConvocacaoAnterior().getPendenteCancelamento() == false)) {
					// Já foi convocado anteriormente
					
					try {
						// Verifica se na convocação anterior o discente possui matrículas em componentes e cancela todas
						Collection<MatriculaComponente> matriculasDiscente = matriculaDao.findByDiscenteOtimizado(conv.getConvocacaoAnterior().getDiscente(), TipoComponenteCurricular.getAll(), SituacaoMatricula.getSituacoesTodas());
						
						if( !isEmpty(matriculasDiscente) ) {
							ProcessadorAlteracaoStatusMatricula processadorAlteracaoStatusMatricula = new ProcessadorAlteracaoStatusMatricula();
							MovimentoOperacaoMatricula movMatricula = new MovimentoOperacaoMatricula();
							movMatricula.setMatriculas(matriculasDiscente);
							movMatricula.setNovaSituacao(SituacaoMatricula.CANCELADO);
							movMatricula.setCodMovimento(SigaaListaComando.ALTERAR_STATUS_MATRICULA);
							movMatricula.setUsuarioLogado( mov.getUsuarioLogado() );
							movMatricula.setSistema( mov.getSistema() );
							
							processadorAlteracaoStatusMatricula.execute(movMatricula);
						}
					} catch (NegocioException e) {
						for(MensagemAviso m: e.getListaMensagens().getMensagens())
							sb.append(conv.getDiscente().getNome() + ": " + m.getMensagem() + "<br/>");
					}
					
					if(!conv.getTipo().equals(TipoConvocacao.CONVOCACAO_MUDANCA_SEMESTRE)){
						registrarMudancaCurricular(mov, dao, conv);
						registrarAlteracaoStatusAluno(mov, dao, conv);
					}
					
					if(conv.getConvocacaoAnterior().getDiscente().getStatus() == StatusDiscente.EXCLUIDO){
						Discente discente = dao.findByPrimaryKey(conv.getDiscente().getId(), Discente.class);
						dao.updateFields(Discente.class, conv.getDiscente().getId(),
								new String[] { "status", "matricula", "matriculaAntiga" }, 
								new Object[] { StatusDiscente.PENDENTE_CADASTRO, discente.getMatriculaAntiga(), null });
					}
					
					conv.getDiscente().setCurriculo( mapa.get(conv.getDiscente().getMatrizCurricular().getId()) );
					int prazoMaximo = CalculoPrazoMaximoFactory.getCalculoGraduacao(conv.getDiscente()).calcular(conv.getDiscente(), movimento);
					conv.getDiscente().setPrazoConclusao(prazoMaximo);
					
					dao.updateFields(Discente.class, conv.getDiscente().getDiscente().getId(), new String[]{"periodoIngresso", "prazoConclusao", "curriculo", "curso"}, 
							new Object[]{conv.getDiscente().getPeriodoIngresso(), conv.getDiscente().getPrazoConclusao(), conv.getDiscente().getCurriculo(), conv.getDiscente().getCurso()});
					dao.updateNoFlush(conv.getDiscente());
					dao.createNoFlush(conv);
				} else {
					// Gerar nova convocação
					
					PessoaVestibular pVestibular = dao.findByPrimaryKey(conv.getInscricaoVestibular().getPessoa().getId(), PessoaVestibular.class);
					long cpf = conv.getDiscente().getPessoa().getCpf_cnpj();
					Pessoa pessoa = dao.findMaisRecenteByCPF(cpf);
					
					if ( ValidatorUtil.isEmpty(pessoa) ){
						pessoa = popularPessoa(pVestibular);
					} else {
						pessoa.setEnderecoContato( (Endereco) HibernateUtils.getTarget( pVestibular.getEnderecoContato()) );
						if (pessoa.getContaBancaria() != null) 
							pessoa.setContaBancaria( (ContaBancaria) HibernateUtils.getTarget( pessoa.getContaBancaria()));
						if (isEmpty(pessoa.getNomeMae()))
							pessoa.setNomeMae(isEmpty(pVestibular.getNomeMae()) ? "NÃO INFORMADO" : pVestibular.getNomeMae());
						if (isEmpty(pessoa.getNomePai()))
							pessoa.setNomePai(isEmpty(pVestibular.getNomePai()) ? "NÃO INFORMADO" : pVestibular.getNomePai());
					}
					
					conv.getDiscente().setPessoa(pessoa);
					
					try {
						ProcessadorDiscente processadorDiscente = new ProcessadorDiscente();
						DiscenteMov discmov = new DiscenteMov();
						discmov.setDiscenteAntigo( false );
						discmov.setCodMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
						discmov.setObjMovimentado(conv.getDiscente());
						discmov.setUsuarioLogado(mov.getUsuarioLogado());
						discmov.setSistema( mov.getSistema() );
						conv.setDiscente( (DiscenteGraduacao) processadorDiscente.execute(discmov) );
					} catch (NegocioException e) {
						for(MensagemAviso m: e.getListaMensagens().getMensagens())
							sb.append(conv.getDiscente().getNome() + ": " + m.getMensagem() + "<br/>");
					}
					
					if(sb.length() == 0){
						dao.createNoFlush(conv);
					}
				}
			}
			
			if(sb.length() > 0){
				NegocioException ne = new NegocioException();
				ne.addErro(sb.toString());
				throw ne;
			}
			
			for(CancelamentoConvocacao canc: movimento.getCancelamentos()){
				dao.createNoFlush(canc);
			}
		} finally {
			dao.close();
			estruturaDao.close();
			matriculaDao.close();
		}
		
		return null;
	}

	/** Registra a mudança da matriz curricular do discente.
	 * @param mov
	 * @param dao
	 * @param convocacao
	 * @throws DAOException
	 */
	private void registrarMudancaCurricular(Movimento mov, GenericDAO dao,
			ConvocacaoProcessoSeletivoDiscente convocacao) throws DAOException {
		MatrizCurricular antiga = convocacao.getConvocacaoAnterior().getDiscente().getMatrizCurricular();
		MatrizCurricular nova = convocacao.getDiscente().getMatrizCurricular();
		Integer tipoMudanca = null;
		
		if (!antiga.getCurso().equals(nova.getCurso())){
			tipoMudanca = TipoMudancaCurricular.MUDANCA_CURSO;
		} else if (antiga.getHabilitacao() != null &&
				!antiga.getHabilitacao().equals(nova.getHabilitacao())) {
			tipoMudanca = TipoMudancaCurricular.MUDANCA_HABILITACAO;
		} else if (!antiga.getGrauAcademico().equals(nova.getGrauAcademico())){
			tipoMudanca = TipoMudancaCurricular.MUDANCA_GRAU_ACADEMICO;
		} else if (!antiga.getTurno().equals(nova.getTurno())){
			tipoMudanca = TipoMudancaCurricular.MUDANCA_TURNO;
		}
		
		if(tipoMudanca != null){
			MudancaCurricular m = new MudancaCurricular();
			m.setDiscente(convocacao.getDiscente());
			m.setData(new Date());
			m.setMatrizAntiga(antiga);
			m.setMatrizNova(nova);
			m.setTipoMudanca(tipoMudanca);
			m.setEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			dao.createNoFlush(m);
		}
	}

	/**
	 * Registra a alteração de status do discente.
	 * 
	 * @param mov
	 * @param dao
	 * @param d
	 * @throws DAOException
	 */
	private void registrarAlteracaoStatusAluno(Movimento mov, GenericDAO dao, ConvocacaoProcessoSeletivoDiscente convocacao) throws DAOException {
		DiscenteGraduacao d = convocacao.getDiscente();
		AlteracaoStatusAluno alteracao = new AlteracaoStatusAluno();
		alteracao.setAno(d.getAnoIngresso());
		alteracao.setPeriodo(d.getPeriodoIngresso());
		alteracao.setData(new Date());
		alteracao.setMovimento(mov.getCodMovimento().getId());
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setStatus(convocacao.getConvocacaoAnterior().getDiscente().getStatus());
		alteracao.setDiscente(d.getDiscente());
		alteracao.setObservacao("Status alterado após convocação para vaga remanescente do vestibular.");
		dao.createNoFlush(alteracao);
		dao.updateField(Discente.class,  d.getId(), "status", d.getStatus());
	}
	
	/**
	 * Método auxiliar utilizado para popular um objeto {@link Pessoa} a partir 
	 * dos dados de um objeto {@link PessoaVestibular} do candidato do vestibular.
	 * @param pVestibular
	 * @return
	 */
	public Pessoa popularPessoa(PessoaVestibular pVestibular) {
		Pessoa pessoa = new Pessoa();
		PessoaDao dao = new PessoaDao();
		try {
			pVestibular.setEnderecoContato( dao.findByPrimaryKey(pVestibular.getEnderecoContato().getId(), Endereco.class) );
			BeanUtils.copyProperties(pessoa, pVestibular);
			pessoa.setId(0);
			pessoa.setCpf_cnpj(pVestibular.getCpf_cnpj());
			pessoa.setNome(pVestibular.getNome().replace(".", "").toUpperCase());
			if (!isEmpty(pVestibular.getNomeMae()))
			pessoa.setNomeMae(pVestibular.getNomeMae().replace(".", "").toUpperCase());
			if (!isEmpty(pVestibular.getNomePai()))
				pessoa.setNomePai(pVestibular.getNomePai().replace(".", "").toUpperCase());
			pessoa.setIdentidade(pVestibular.getIdentidade());
			pessoa.setDataNascimento(pVestibular.getDataNascimento());
			pessoa.setPassaporte(null);
			pessoa.setEnderecoContato( pVestibular.getEnderecoContato() );
			pessoa.setContaBancaria(null);
			
			pessoa.prepararDados();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		
		return pessoa;
	}
	
	
	/** Valida os dados antes de cadastrar
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS, mov);
		

	}

}
