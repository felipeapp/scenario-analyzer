/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/05/2010
 */
package br.ufrn.sigaa.ensino.stricto.negocio.calculos;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.AproveitamentoCreditoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DiscenteStrictoDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AlteracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DadosCalculosDiscente;
import br.ufrn.sigaa.ensino.dominio.EquivalenciaEspecifica;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoIntegralizacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;
import br.ufrn.sigaa.ensino.stricto.dominio.AlteracaoDiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.util.RepositorioInformacoesCalculoDiscente;

/**
 * Calcula e atualiza os campos (em DiscenteStricto) dos cr�ditos e CH
 * integralizados pelo discente.
 * 
 * @author David Pereira
 *
 */
public class AtualizaTotaisIntegralizadosStricto extends CalculosDiscenteChainNode<DiscenteStricto> {

	@Override
	public void processar(DiscenteStricto d, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {
		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class, mov.getUsuarioLogado());
		DiscenteDao ddao = getDAO(DiscenteDao.class, mov);
		MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class, mov);
		ComponenteCurricularDao ccdao = getDAO(ComponenteCurricularDao.class, mov);
		AproveitamentoCreditoDao aproveitamentoDao = getDAO(AproveitamentoCreditoDao.class, mov);
		MovimentacaoAlunoDao movdao = getDAO(MovimentacaoAlunoDao.class, mov);
		
		try {
			// conjunto de altera��es das matr�culas que devem ser realizadas no
			// final do m�todo. ao ser identificada uma altera��o no registro de 
			// matriculaComponente � criado e adicionado nessa cole��o.
			Collection<AlteracaoMatricula> alteracoes = new ArrayList<AlteracaoMatricula>();
			
			// loop das matr�culas do aluno (pagas e matriculadas)
			Collection<MatriculaComponente> matriculas = mcdao.findPagaseMatriculadasByDiscente(d);
			
			// cole��o de componentes que o discente j� pagou (ou foram
			// integralizados, como aproveitamento, aprova��o, ... )
			Collection<ComponenteCurricular> componentesPagosEMatriculados = ddao.findComponentesCurriculares(d, SituacaoMatricula.getSituacoesPagasEMatriculadas(),null);
			
			// Pegar componentes obrigat�rios do curr�culo (sem contar os blocos)
			TreeSet<Integer> componentesObrigCurriculo = dao.findComponentesDoCurriculoByDiscente(d, true);
			
			int crObrigatorios = 0;
			int chObrigatoria = 0;
			int crOptativos = 0;
			int chOptativa = 0;
			
			// verifica as matr�culas que s�o obrigat�rias da grade
			for (MatriculaComponente matricula : matriculas) {
				
				if(SituacaoMatricula.getSituacoesPagas().contains(matricula.getSituacaoMatricula())) {
					crOptativos += matricula.getComponenteCrTotal();
					chOptativa += matricula.getComponenteCHTotal();
				}
				
				boolean obrigatoria = componentesObrigCurriculo.contains(matricula.getComponente().getId());
				
				if(obrigatoria && SituacaoMatricula.getSituacoesPagas().contains(matricula.getSituacaoMatricula())) {					
					crObrigatorios += matricula.getComponenteCrTotal();
					chObrigatoria += matricula.getComponenteCHTotal();
				}

				if ( obrigatoria && !TipoIntegralizacao.OBRIGATORIA.equals(matricula.getTipoIntegralizacao()) ) {
					AlteracaoMatricula alteracao = createAlteracaoMatricula(matricula, mov, TipoIntegralizacao.OBRIGATORIA);
					matricula.setTipoIntegralizacao(TipoIntegralizacao.OBRIGATORIA);
					
					if (alteracao != null)
						alteracoes.add(alteracao);
				}
			}
			
			// cole��o de componentes de todos os curr�culos do CURSO do aluno,
			// que o aluno ainda n�o integralizou
			Collection<CurriculoComponente> pendentesObrigatoriaCurriculo = ddao.findComponentesPendentesByDiscente(d, true,true);


			// analisa as equivalentes a obrigat�rias do curso
			DadosCalculosDiscente dados = RepositorioInformacoesCalculoDiscente.INSTANCE.buscarInformacoes(d.getId());
			Date primeiraData = dados.getDataInicio();
			Date ultimaData = dados.getDataFim();

			Map<Integer, List<Object[]>> mapaEquivalencias;
			if (d.isRegular())
				mapaEquivalencias = ccdao.findEquivalenciasComponentesByIntervaloDatas(pendentesObrigatoriaCurriculo, dados.getCurriculo().getId(), primeiraData, ultimaData);
			else
				mapaEquivalencias = new HashMap<Integer, List<Object[]>>();
			
			for (CurriculoComponente ccPendente : pendentesObrigatoriaCurriculo) {
				List<Object[]> equivalencias = mapaEquivalencias.get(ccPendente.getComponente().getId());
				
				// s� � necess�rio verificar caso o componente pendente tenha alguma
				// equival�ncia, sen�o � l�gico
				// que o aluno n�o pagou nada equivalente a ele... MAS se a
				// equival�ncia estiver do lado do componente integralizado ?!?!?!

				if (!isEmpty(equivalencias)) {
					if (ccPendente.getObrigatoria()	&& ccPendente.getCurriculo().getId() == d.getCurriculo().getId()) {
						// ... e se o aluno possui um conjunto de equivalentes
						// entre seus componentes integralizados.
						
						for (Object[] infoEquivalencia : equivalencias) {
							String equivalencia = (String) infoEquivalencia[0];
							Integer idEspecifica = (Integer) infoEquivalencia[1];
							EquivalenciaEspecifica especifica = null;
							if (!isEmpty(idEspecifica))
								especifica = ddao.findByPrimaryKey(idEspecifica, EquivalenciaEspecifica.class);
							
							if (equivalencia != null && ExpressaoUtil.eval(equivalencia, ccPendente.getComponente(), componentesPagosEMatriculados)) {
								crObrigatorios += ccPendente.getComponente().getCrTotal();
								chObrigatoria += ccPendente.getComponente().getChTotal();
								
								// seta nas matr�culas dos componentes como equ_obr
								Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(equivalencia, ccPendente.getComponente(), componentesPagosEMatriculados);
								if (equivalentes != null) {
									for (ComponenteCurricular componente : equivalentes) {
										// pega a matr�cula do equivalente
										MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(matriculas, componente);
										// Verifica se a equival�ncia que foi encontrada � do tipo espec�fica e estava valendo na data da matr�cula
										if (especifica == null || (especifica != null && especifica.isEquivalenciaValendoNaData(mat.getDataCadastro()))) {
											AlteracaoMatricula alteracao = createAlteracaoMatricula(mat,mov,TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA);
											// verifica prioridade, seta caso seja qualquer outra coisa menos OBRIGATORIA
											if (!TipoIntegralizacao.OBRIGATORIA.equals(mat.getTipoIntegralizacao()) && alteracao != null) {
												mat.setTipoIntegralizacao(TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA);
												alteracao.setExpressao(ArvoreExpressao.getMatchesExpression(equivalencia, ccPendente.getComponente(), componentesPagosEMatriculados));
												alteracoes.add(alteracao);
											}
										}
									}
									
									break;
								}									
							}
						}
					}
				}
			}
			mcdao.persistirAlteracoes(alteracoes);
			/**
			 * Um gestor Stricto pode 'Cadastrar Aproveitamento de Cr�dito' compulsoriamente. Este aproveitamento de cr�ditos fica
			 * registrado na Entidade AproveitamentoCredito. Como esses aproveitamentos s�o exibidos na listagem de
			 * 'Disciplinas/Atividades Cursadas/Cursando', deve-se somar aos cr�ditos totais integralizados esses aproveitamentos.
			 * Nesses aproveitamentos o gestor informa apenas o total de cr�ditos a aproveitar e uma observa��o.
			 */
			Integer creditosAproveitadosCompulsoriamente =  aproveitamentoDao.findTotalAproveitamentosByDiscente(d.getId());
			
			crOptativos -= crObrigatorios;
			chOptativa -= chObrigatoria;
			
			d.setChTotalIntegralizada((short) (chObrigatoria + chOptativa));
			d.setChObrigatoriaIntegralizada((short) chObrigatoria);
			d.setChOptativaIntegralizada((short) chOptativa);
			
			d.setCrTotaisIntegralizados((short) (crOptativos + crObrigatorios + creditosAproveitadosCompulsoriamente));
			d.setCrTotaisObrigatoriosIntegralizado((short) crObrigatorios);
			
			dao.atualizaTotaisIntegralizados(d);

			registraAlteracaoCalculos(d.getId(), dao, mov);
		} finally {
			dao.close();
			ddao.close();
			mcdao.close();
			movdao.close();
			ccdao.close();
			aproveitamentoDao.close();
		}
	}
	
	/** 
	 * Registra a altera��o dos c�lculos realizados para o discente.
	 * 
	 * @param idDiscenteStricto
	 * @param dao
	 * @param mov
	 * @throws DAOException
	 */
	private void registraAlteracaoCalculos(int idDiscenteStricto, DiscenteStrictoDao dao, Movimento mov) throws DAOException {
		DiscenteStricto ds = dao.findByPrimaryKey(idDiscenteStricto, DiscenteStricto.class);

		AlteracaoDiscenteStricto alteracao = new AlteracaoDiscenteStricto();
		alteracao.setDiscente(ds);
		alteracao.setData(new Date());
		alteracao.setOperacao(mov.getCodMovimento().getId());
		alteracao.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());

		alteracao.setCrTotaisIntegralizados(ds.getCrTotaisIntegralizados());
		alteracao.setCrTotaisObrigatorios(ds.getCrTotaisObrigatoriosIntegralizado());
		alteracao.setChTotalIntegralizada(ds.getChTotalIntegralizada());
		alteracao.setChObrigatoriaIntegralizada(ds.getChObrigatoriaIntegralizada());
		alteracao.setChOptativaIntegralizada(ds.getChOptativaIntegralizada());
		
		dao.create(alteracao);
	}
	
	/**
	 * Cria um registro de altera��o de situa��o de matr�cula de um discente
	 * 
	 * @param matricula
	 * @param mov
	 * @param novoTipo
	 * @return
	 */
	private AlteracaoMatricula createAlteracaoMatricula(MatriculaComponente matricula, Movimento mov, String novoTipo) {
		// s� � criada uma inst�ncia de AlteracaoMatricula se o tipo de integer.
		// for realmente diferente
		if (novoTipo != null && novoTipo.equalsIgnoreCase(matricula.getTipoIntegralizacao())) {
			return null;
		}
		AlteracaoMatricula alteracao = new AlteracaoMatricula();
		alteracao.setMatricula(matricula);
		alteracao.setDataAlteracao(new Date());
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setCodMovimento(mov.getCodMovimento().getId());
		alteracao.setTipoIntegralizacaoAntigo(matricula.getTipoIntegralizacao());
		alteracao.setTipoIntegralizacaoNovo(novoTipo);
		alteracao.setSituacaoAntiga(matricula.getSituacaoMatricula());
		alteracao.setSituacaoNova(matricula.getSituacaoMatricula());

		return alteracao;
	}
	
}
