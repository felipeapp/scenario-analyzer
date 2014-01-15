/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 13/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDiscenteDao;
import br.ufrn.sigaa.arq.dao.vestibular.PessoaVestibularDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.ResultadoClassificacaoCandidato;
import br.ufrn.sigaa.vestibular.dominio.ResultadoOpcaoCurso;
import br.ufrn.sigaa.vestibular.dominio.ResultadoPessoaConvocacao;
import br.ufrn.sigaa.vestibular.dominio.TipoConvocacao;

/**
 * Processador responsável pela efetivação da convocação dos candidatos aprovados no Vestibular.
 * @author Rafael Gomes
 *
 */
public class ProcessadorConvocacaoProcessoSeletivo extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		
		ConvocacaoProcessoSeletivoDao daoCPS = null;
		ConvocacaoProcessoSeletivoDiscenteDao daoCPSD = null;
		PessoaDao pessoaDao = null;
		PessoaVestibularDao pessoaVestibularDao = null;
		MatrizCurricularDao matrizDao = null;
		int i = 0;
		// mapa de resultado, com as quantidades de convocações e lista de mensagens de erros.
		Map<Integer, List<ResultadoPessoaConvocacao>> mapaResultado = new HashMap<Integer, List<ResultadoPessoaConvocacao>>();
		
		try {
			daoCPS = getDAO(ConvocacaoProcessoSeletivoDao.class, mov);
			daoCPSD = getDAO(ConvocacaoProcessoSeletivoDiscenteDao.class, mov);
			pessoaDao = getDAO(PessoaDao.class, mov);
			pessoaVestibularDao = getDAO(PessoaVestibularDao.class, mov);
			matrizDao = getDAO(MatrizCurricularDao.class, mov);
			
			MovimentoCadastro movimento = ((MovimentoCadastro) mov);
			
			String descricaoPrimeiraChamada = ParametroHelper.getInstance().getParametro(ParametrosGraduacao.DESCRICAO_PRIMEIRA_CHAMADA_VESTIBULAR);

			ConvocacaoProcessoSeletivo obj = movimento.getObjMovimentado();
			ConvocacaoProcessoSeletivo objAux = daoCPS.findByProcessoSeletivoAndDescricao(obj.getProcessoSeletivo(), descricaoPrimeiraChamada);

			// Criando a convocação de processo seletivo para a primeira chamada, caso não exista.
			if(isEmpty(objAux)) {
				daoCPS.create(obj);
			} else {
				obj = objAux;
			}
			// matriz curricular
			MatrizCurricular matriz = (MatrizCurricular) movimento.getObjAuxiliar();
			// cria cache de dados a serem buscados durante o processamento
			Collection<ResultadoClassificacaoCandidato> resultadosCandidatosBuscados = daoCPSD.findResultadoVestibularAprovados(obj.getProcessoSeletivo(), matriz);
			// resultados de opção de curso. O mapa agiliza a recuperação do resultado. 
			Map<Integer, List<ResultadoOpcaoCurso>> resultadosOpcaoCurso = new TreeMap<Integer, List<ResultadoOpcaoCurso>>();
			for (ResultadoOpcaoCurso roc : daoCPSD.findResultadoOpcaoCurso(obj.getProcessoSeletivo(), matriz)) {
				List<ResultadoOpcaoCurso> lista = resultadosOpcaoCurso.get(roc.getResultadoClassificacaoCandidato().getId());
				if (lista == null) {
					lista = new ArrayList<ResultadoOpcaoCurso>(2);
					resultadosOpcaoCurso.put(roc.getResultadoClassificacaoCandidato().getId(), lista);
				}
				lista.add(roc);
			}
			List<Integer> idsInscricaoVestibular = new ArrayList<Integer>(1);
			Collection<Integer> idsPessoaVestibular = new ArrayList<Integer>(1);
			Collection<Long> cpfs = new ArrayList<Long>(1);
			Collection<Integer> idMatrizes = new ArrayList<Integer>(1);
			for ( ResultadoClassificacaoCandidato resultadoCandidato : resultadosCandidatosBuscados ) {
				idsInscricaoVestibular.add(resultadoCandidato.getInscricaoVestibular().getId());
				idsPessoaVestibular.add(resultadoCandidato.getInscricaoVestibular().getPessoa().getId());
				cpfs.add(resultadoCandidato.getInscricaoVestibular().getPessoa().getCpf_cnpj());
				idMatrizes.add(resultadoCandidato.getOpcaoAprovacao());
			}
			// convocados
			Map<Integer, Boolean> mapaConvocados = daoCPSD.mapaConvocados(idsInscricaoVestibular);
			// pessoa Vestibular
			Map<Integer, PessoaVestibular> mapaPessoaVestibular = pessoaVestibularDao.findByPrimaryKey(idsPessoaVestibular);
			// pessoa
			Map<Long, Pessoa> mapaPessoa = pessoaDao.findByCpfCnpj(cpfs);
			// matrizes curriculares
			Map<Integer, MatrizCurricular> mapaMatriz = matrizDao.criaCache(idMatrizes);
			// erros de validação de cadastros pessoais
			List<ResultadoPessoaConvocacao> listaErrosConvocacao = new ArrayList<ResultadoPessoaConvocacao>();
			
			
			for ( ResultadoClassificacaoCandidato resultadoCandidato : resultadosCandidatosBuscados ) {
				ConvocacaoProcessoSeletivoDiscente convocacaoDiscente = new ConvocacaoProcessoSeletivoDiscente();
				PessoaVestibular pVestibular = new PessoaVestibular();
				Pessoa pessoa = new Pessoa();
				
				if ( !mapaConvocados.get(resultadoCandidato.getInscricaoVestibular().getId()) ){
					convocacaoDiscente =  new ConvocacaoProcessoSeletivoDiscente();
					pVestibular = mapaPessoaVestibular.get(resultadoCandidato.getInscricaoVestibular().getPessoa().getId());
					pessoa = mapaPessoa.get(pVestibular.getCpf_cnpj());
					
					pessoa = popularPessoa(pessoa, pVestibular, pessoaDao);
					
					ListaMensagens erros = new ListaMensagens();
					PessoaValidator.validarDadosPessoais(pessoa, null, PessoaValidator.DISCENTE, erros);
					
					if ( erros.isEmpty() ){
						
						DiscenteGraduacao discente = new DiscenteGraduacao();
						discente.setPessoa(pessoa);
						discente.setAnoIngresso( obj.getProcessoSeletivo().getAnoEntrada() );
						discente.setPeriodoIngresso( resultadoCandidato.getSemestreAprovacao() );
						MatrizCurricular matrizCurricular = mapaMatriz.get(resultadoCandidato.getOpcaoAprovacao());
						discente.setMatrizCurricular( matrizCurricular );
						discente.setCurso( matrizCurricular.getCurso() );
						
						discente.setNivel( NivelEnsino.GRADUACAO );
						discente.setStatus(StatusDiscente.PENDENTE_CADASTRO);
						discente.setTipo(Discente.REGULAR);
						discente.setFormaIngresso(obj.getProcessoSeletivo().getFormaIngresso());
						
						ProcessadorDiscente processadorDiscente = new ProcessadorDiscente();
						DiscenteMov discmov = new DiscenteMov();
						discmov.setDiscenteAntigo( false );
						discmov.setCodMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
						discmov.setObjMovimentado(discente);
						discmov.setUsuarioLogado(mov.getUsuarioLogado());
						discmov.setSistema( mov.getSistema() );
						discente = (DiscenteGraduacao) processadorDiscente.execute(discmov);
						
						convocacaoDiscente.setConvocacaoProcessoSeletivo(obj);
						convocacaoDiscente.setInscricaoVestibular(resultadoCandidato.getInscricaoVestibular());
						convocacaoDiscente.setDiscente(discente);
						convocacaoDiscente.setMatrizCurricular(matrizCurricular);
						convocacaoDiscente.setAno(discente.getAnoIngresso());
						convocacaoDiscente.setPeriodo(discente.getPeriodoIngresso());
						convocacaoDiscente.setGrupoCotaConvocado(resultadoCandidato.getGrupoCotaConvocado());
						convocacaoDiscente.setGrupoCotaRemanejado(resultadoCandidato.getGrupoCotaRemanejado());
						
						// resultado de opção do curso
						// caso não tenha sido aprovado em primeira ou segunda opção, verifica se foi em turno distinto ou sem argumento mínimo
						if (resultadoCandidato.isAprovadoTurnoDistinto())
							convocacaoDiscente.setTipo(TipoConvocacao.CONVOCACAO_TURNO_DISTINTO);
						else if (resultadoCandidato.isAprovadoAma())
							convocacaoDiscente.setTipo(TipoConvocacao.CONVOCACAO_SEM_ARGUMENTO_MINIMO_APROVACAO);
						else {
							List<ResultadoOpcaoCurso> lista = resultadosOpcaoCurso.get(resultadoCandidato.getId());
							if (lista != null) {
								for (ResultadoOpcaoCurso resultadoOpcaoCurso : lista) {
									if (resultadoOpcaoCurso.getResultadoClassificacaoCandidato().getId() == resultadoCandidato.getId()
											&& resultadoOpcaoCurso.getMatrizCurricular().getId() == resultadoCandidato.getOpcaoAprovacao()) {
										convocacaoDiscente.setResultado(resultadoOpcaoCurso);
										if (resultadoOpcaoCurso.getOrdemOpcao() == ResultadoOpcaoCurso.PRIMEIRA_OPCAO
												&& convocacaoDiscente.getMatrizCurricular().getId() == matrizCurricular.getId())
											convocacaoDiscente.setTipo(TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO);
										else if (resultadoOpcaoCurso.getOrdemOpcao() == ResultadoOpcaoCurso.SEGUNDA_OPCAO
												&& convocacaoDiscente.getMatrizCurricular().getId() == matrizCurricular.getId())
											convocacaoDiscente.setTipo(TipoConvocacao.CONVOCACAO_SEGUNDA_OPCAO);
										break;
									}
								}
							}
						}
						daoCPSD.create(convocacaoDiscente);
					} else {
						i--;
						listaErrosConvocacao.add(new ResultadoPessoaConvocacao(pessoa, erros));
					}
				}
			}
			mapaResultado.put(new Integer(i), listaErrosConvocacao);
		} finally {
			if (daoCPS != null) daoCPS.close();
			if (daoCPSD != null) daoCPSD.close();
			if (pessoaDao != null) pessoaDao.close();
			if (pessoaVestibularDao != null) pessoaVestibularDao.close();
			if (matrizDao != null) matrizDao.close();
		}
		
		return mapaResultado;
	}

	/**
	 * Método auxiliar utilizado para popular um objeto {@link Pessoa} a partir 
	 * dos dados de um objeto {@link PessoaVestibular} do candidato do vestibular.
	 * @param pVestibular
	 * @return
	 * @throws DAOException 
	 * @throws NegocioException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public Pessoa popularPessoa(Pessoa pessoa, PessoaVestibular pVestibular, PessoaDao dao) throws DAOException, NegocioException {
		boolean existePessoa = false;
		Pessoa pessoaAux = new Pessoa();
		try {
			if ( !isEmpty(pessoa) ) {
				existePessoa = true;
				try {
					pessoaAux = (Pessoa) BeanUtils.cloneBean(pessoa);
				} catch (InstantiationException e) {
					throw new NegocioException(e);
				} catch (NoSuchMethodException e) {
					throw new NegocioException(e);
				}
			} else {
				pessoa = new Pessoa();
			}
			pVestibular.setEnderecoContato( dao.findByPrimaryKey(pVestibular.getEnderecoContato().getId(), Endereco.class) );
			pVestibular.getEnderecoContato().getMunicipio().getUnidadeFederativa();		
			
			BeanUtils.copyProperties(pessoa, pVestibular);
			if (existePessoa) {
				pessoa.setId( pessoaAux.getId() );
				if ( pessoaAux.isFuncionario() 		== null ) pessoa.setFuncionario(pessoaAux.isFuncionario());
				if ( pessoaAux.getDataCadastro() 	== null ) pessoa.setDataCadastro(pessoaAux.getDataCadastro());
				if ( pessoaAux.getOrigem() 			== null ) pessoa.setOrigem(pessoaAux.getOrigem());
			} else {
				pessoa.setId( 0 );
				pessoa.setDataCadastro(new Date());
			}
			
			if (!isEmpty(pVestibular.getNome())) pessoa.setNome(pVestibular.getNome().replace(".", "").toUpperCase());
			if (!isEmpty(pVestibular.getNomeMae())) pessoa.setNomeMae(pVestibular.getNomeMae().replace(".", "").toUpperCase());
			if (!isEmpty(pVestibular.getNomePai())) pessoa.setNomePai(pVestibular.getNomePai().replace(".", "").toUpperCase());
			pessoa.setIdentidade(pVestibular.getIdentidade());
			pessoa.setEnderecoContato( pVestibular.getEnderecoContato() );
			pessoa.setContaBancaria(null);
			pessoa.setValido(true);
			
			pessoa.prepararDados();
			
			return pessoa;
		} catch (IllegalAccessException e) {
			throw new NegocioException(e);
		} catch (InvocationTargetException e) {
			throw new NegocioException(e);
		} finally {
			dao.close();
		}
		
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}