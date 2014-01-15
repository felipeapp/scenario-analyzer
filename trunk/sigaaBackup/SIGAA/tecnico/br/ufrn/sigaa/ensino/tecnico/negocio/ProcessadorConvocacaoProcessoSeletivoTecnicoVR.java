/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/01/2011
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

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
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOperacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorAlteracaoStatusMatricula;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.ensino.tecnico.dominio.CancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.PessoaTecnico;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;

/**
 * Processador responsável por persistir as informações de uma convocação de
 * candidatos do vestibular para vagas remanescentes.
 * 
 * @author Leonardo Campos
 * 
 */
public class ProcessadorConvocacaoProcessoSeletivoTecnicoVR extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		PessoaDao dao = getDAO(PessoaDao.class, mov);
		EstruturaCurricularDao estruturaDao = getDAO(EstruturaCurricularDao.class, mov);
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class, mov);
		
		try{
			
			MovimentoConvocacaoProcessoSeletivoTecnico movimento = (MovimentoConvocacaoProcessoSeletivoTecnico) mov;
			ConvocacaoProcessoSeletivoTecnico convocacao = movimento.getObjMovimentado();
			dao.create(convocacao);

			StringBuffer sb = new StringBuffer();
			for(ConvocacaoProcessoSeletivoDiscenteTecnico conv: movimento.getConvocacoes()){
				conv.setAno(conv.getDiscente().getAnoEntrada());
				
				if (conv.getConvocacaoAnterior() != null && (conv.getPendenteCancelamento() == null || conv.getPendenteCancelamento() == false)){
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
					
					registrarAlteracaoStatusAluno(mov, dao, conv);
					
					if(conv.getConvocacaoAnterior().getDiscente().getStatus() == StatusDiscente.EXCLUIDO){
						Discente discente = dao.findByPrimaryKey(conv.getDiscente().getId(), Discente.class);
						dao.updateFields(Discente.class, conv.getDiscente().getId(),
								new String[] { "status", "matricula", "matriculaAntiga" }, 
								new Object[] { StatusDiscente.PENDENTE_CADASTRO, discente.getMatriculaAntiga(), null });
					}
					
					// TODO Qual o prazo máximo no caso de discente do IMD ?
					//int prazoMaximo = CalculoPrazoMaximoFactory.getCalculoGraduacao(conv.getDiscente()).calcular(conv.getDiscente(), movimento);
					//conv.getDiscente().setPrazoConclusao(prazoMaximo);
					
					dao.updateFields(Discente.class, conv.getDiscente().getDiscente().getId(), new String[]{"periodoIngresso", "prazoConclusao", "curriculo", "curso"}, 
							new Object[]{conv.getDiscente().getPeriodoIngresso(), conv.getDiscente().getPrazoConclusao(), conv.getDiscente().getCurriculo(), conv.getDiscente().getCurso()});
					dao.updateNoFlush(conv.getDiscente());
					dao.createNoFlush(conv);
				} else {
					// Gerar nova convocação
					
					PessoaTecnico pTecnico = dao.findByPrimaryKey(conv.getInscricaoProcessoSeletivo().getPessoa().getId(), PessoaTecnico.class);
					long cpf = conv.getDiscente().getPessoa().getCpf_cnpj();
					Pessoa pessoa = dao.findMaisRecenteByCPF(cpf);
					
					if ( ValidatorUtil.isEmpty(pessoa) ){
						pessoa = popularPessoa(pTecnico);
					} else {
						pessoa.setEnderecoContato( (Endereco) HibernateUtils.getTarget( pTecnico.getEnderecoContato()) );
						if (pessoa.getContaBancaria() != null) 
							pessoa.setContaBancaria( (ContaBancaria) HibernateUtils.getTarget( pessoa.getContaBancaria()));
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
						conv.setDiscente( (DiscenteTecnico) processadorDiscente.execute(discmov) );
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
			
			for(CancelamentoConvocacaoTecnico canc: movimento.getCancelamentos())
				dao.createNoFlush(canc);
		} finally {
			dao.close();
			estruturaDao.close();
			matriculaDao.close();
		}
		
		return null;
	}

	/**
	 * Registra a alteração de status do discente.
	 * 
	 * @param mov
	 * @param dao
	 * @param d
	 * @throws DAOException
	 */
	private void registrarAlteracaoStatusAluno(Movimento mov, GenericDAO dao, ConvocacaoProcessoSeletivoDiscenteTecnico convocacao) throws DAOException {
		DiscenteTecnico d = convocacao.getDiscente();
		AlteracaoStatusAluno alteracao = new AlteracaoStatusAluno();
		alteracao.setAno(d.getAnoIngresso());
		alteracao.setPeriodo(d.getPeriodoIngresso());
		alteracao.setData(new Date());
		alteracao.setMovimento(mov.getCodMovimento().getId());
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setStatus(convocacao.getConvocacaoAnterior().getDiscente().getStatus());
		alteracao.setDiscente(d.getDiscente());
		alteracao.setObservacao("Status alterado após convocação para vaga remanescente do processo seletivo técnico.");
		dao.createNoFlush(alteracao);
		dao.updateField(Discente.class,  d.getId(), "status", d.getStatus());
	}
	
	/**
	 * Método auxiliar utilizado para popular um objeto {@link Pessoa} a partir 
	 * dos dados de um objeto {@link PessoaVestibular} do candidato do vestibular.
	 * @param pTecnico
	 * @return
	 */
	public Pessoa popularPessoa (PessoaTecnico pTecnico) {
		Pessoa pessoa = new Pessoa();
		PessoaDao dao = new PessoaDao();
		try {
			pTecnico.setEnderecoContato( dao.findByPrimaryKey(pTecnico.getEnderecoContato().getId(), Endereco.class) );
			BeanUtils.copyProperties(pessoa, pTecnico);
			pessoa.setId(0);
			pessoa.setCpf_cnpj(pTecnico.getCpf_cnpj());
			pessoa.setNome(pTecnico.getNome().replace(".", "").toUpperCase());
			pessoa.setNomeMae(pTecnico.getNomeMae().replace(".", "").toUpperCase());
			pessoa.setNomePai(pTecnico.getNomePai().replace(".", "").toUpperCase());
			pessoa.setIdentidade(pTecnico.getIdentidade());
			pessoa.setDataNascimento(pTecnico.getDataNascimento());
			pessoa.setPassaporte(null);
			pessoa.setEnderecoContato( pTecnico.getEnderecoContato() );
			pessoa.setContaBancaria(null);
			
			pessoa.prepararDados();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		
		return pessoa;
	}
	
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS, mov);
		

	}

}
