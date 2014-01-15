/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 13/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.tecnico.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeAluno;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.ensino.tecnico.dao.ConvocacaoProcessoSeletivoDiscenteTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dao.ConvocacaoProcessoSeletivoTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dao.PessoaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.PessoaTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ResultadoClassificacaoCandidatoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ResultadoPessoaConvocacaoTecnico;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
/**
 * Processador responsável pela efetivação da convocação dos candidatos aprovados no Vestibular.
 * @author Rafael Gomes
 *
 */
public class ProcessadorConvocacaoProcessoSeletivoTecnico extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		
		ConvocacaoProcessoSeletivoTecnicoDao daoCPS = null;
		ConvocacaoProcessoSeletivoDiscenteTecnicoDao daoCPSD = null;
		PessoaDao pessoaDao = null;
		PessoaTecnicoDao pessoaTecnicoDao = null;
		int i = 0;
		// mapa de resultado, com as quantidades de convocações e lista de mensagens de erros.
		Object [] mapaResultado = new Object[2];
		
		try {
			daoCPS = getDAO(ConvocacaoProcessoSeletivoTecnicoDao.class, mov);
			daoCPSD = getDAO(ConvocacaoProcessoSeletivoDiscenteTecnicoDao.class, mov);
			pessoaDao = getDAO(PessoaDao.class, mov);
			pessoaTecnicoDao = getDAO(PessoaTecnicoDao.class, mov);
			
			MovimentoCadastro movimento = ((MovimentoCadastro) mov);
			
			ConvocacaoProcessoSeletivoTecnico obj = movimento.getObjMovimentado();

			// cria cache de dados a serem buscados durante o processamento
			if (obj.isTodosAprovados()){
				obj.setQuantidadeDiscentesComReserva(Integer.MAX_VALUE);
				obj.setQuantidadeDiscentesSemReserva(Integer.MAX_VALUE);
			}
			Collection <ResultadoClassificacaoCandidatoTecnico> resultadosCandidatosBuscadosComReserva = daoCPSD.findResultadoPSAprovados(obj.getProcessoSeletivo(), obj.getOpcao() == null ? 0 : obj.getOpcao().getId(), obj.getQuantidadeDiscentesComReserva(), 0, obj.isTodosAprovados());
			Collection <ResultadoClassificacaoCandidatoTecnico> resultadosCandidatosBuscadosSemReserva = daoCPSD.findResultadoPSAprovados(obj.getProcessoSeletivo(), obj.getOpcao() == null ? 0 : obj.getOpcao().getId(), 0, obj.getQuantidadeDiscentesSemReserva(), obj.isTodosAprovados());
			
			Collection <ResultadoClassificacaoCandidatoTecnico> resultadosCandidatosBuscados = new ArrayList <ResultadoClassificacaoCandidatoTecnico> ();
			resultadosCandidatosBuscados.addAll(resultadosCandidatosBuscadosComReserva);
			resultadosCandidatosBuscados.addAll(resultadosCandidatosBuscadosSemReserva);

			if (resultadosCandidatosBuscados.isEmpty())
				throw new NegocioException ("Não há candidatos a convocar com esse filtro.");
			
			if (obj.getOpcao() != null && obj.getOpcao().getId() == 0)
				obj.setOpcao(null);
				
			daoCPS.create(obj);
				
			if (obj.getOpcao() == null)
				obj.setOpcao(new OpcaoPoloGrupo());
			
			

			List<Integer> idsInscricaoVestibular = new ArrayList<Integer>(1);
			Collection<Integer> idsPessoaVestibular = new ArrayList<Integer>(1);
			Collection<Long> cpfs = new ArrayList<Long>(1);
			
			for (ResultadoClassificacaoCandidatoTecnico resultadoCandidato : resultadosCandidatosBuscados ) {
				idsInscricaoVestibular.add(resultadoCandidato.getInscricaoProcessoSeletivo().getId());
				idsPessoaVestibular.add(resultadoCandidato.getInscricaoProcessoSeletivo().getPessoa().getId());
				cpfs.add(resultadoCandidato.getInscricaoProcessoSeletivo().getPessoa().getCpf_cnpj());
			}
			
			// convocados
			Map<Integer, Boolean> mapaConvocados = daoCPSD.mapaConvocados(idsInscricaoVestibular);
			// pessoa Vestibular
			Map<Integer, PessoaTecnico> mapaPessoaVestibular = pessoaTecnicoDao.findByPrimaryKey(idsPessoaVestibular);
			// pessoa
			Map<Long, Pessoa> mapaPessoa = pessoaDao.findByCpfCnpj(cpfs);
			// erros de validação de cadastros pessoais
			List<ResultadoPessoaConvocacaoTecnico> listaErrosConvocacao = new ArrayList<ResultadoPessoaConvocacaoTecnico>();
			// Convocações bem sucedidas;
			List <ResultadoClassificacaoCandidatoTecnico> sucessos = new ArrayList <ResultadoClassificacaoCandidatoTecnico> ();
			
			for ( ResultadoClassificacaoCandidatoTecnico resultadoCandidato : resultadosCandidatosBuscados ) {
				System.out.println("Processando convocação " + (i++));
				
				ConvocacaoProcessoSeletivoDiscenteTecnico convocacaoDiscente = new ConvocacaoProcessoSeletivoDiscenteTecnico ();
				PessoaTecnico pTecnico = new PessoaTecnico ();
				Pessoa pessoa = new Pessoa();
				
				if ( !mapaConvocados.get(resultadoCandidato.getInscricaoProcessoSeletivo().getId()) ){
					convocacaoDiscente =  new ConvocacaoProcessoSeletivoDiscenteTecnico ();
					pTecnico = mapaPessoaVestibular.get(resultadoCandidato.getInscricaoProcessoSeletivo().getPessoa().getId());
					pessoa = mapaPessoa.get(pTecnico.getCpf_cnpj());
					
					pessoa = popularPessoa(pessoa, pTecnico, pessoaDao);
					
					ListaMensagens erros = new ListaMensagens();
					PessoaValidator.validarDadosPessoais(pessoa, null, PessoaValidator.DISCENTE, erros);
					
					if ( erros.isEmpty() ){
						
						DiscenteTecnico discente = new DiscenteTecnico();
						discente.setPessoa(pessoa);
						discente.getPessoa().getEnderecoContato().setTipoLogradouro(new TipoLogradouro(TipoLogradouro.RUA));
						discente.setAnoIngresso( obj.getProcessoSeletivo().getAnoEntrada() );
						discente.setPeriodoIngresso(1);
						discente.setCurso( new CursoTecnico (ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL_PARA_CONVOCACAO)) );
						discente.setGestoraAcademica(new Unidade(ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL)));
						discente.setTurmaEntradaTecnico(null);
						discente.setEstruturaCurricularTecnica(null);
						// Discente normal
						discente.setTipoRegimeAluno(new TipoRegimeAluno(2));
						
						discente.setNivel( NivelEnsino.TECNICO );
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
						discente = (DiscenteTecnico) processadorDiscente.execute(discmov);
						
						convocacaoDiscente.setConvocacaoProcessoSeletivo(obj);
						convocacaoDiscente.setInscricaoProcessoSeletivo(resultadoCandidato.getInscricaoProcessoSeletivo());
						convocacaoDiscente.setDiscente(discente);
						convocacaoDiscente.setAno(discente.getAnoIngresso());
						
						daoCPSD.create(convocacaoDiscente);
						
						sucessos.add(resultadoCandidato);
					} else {
						i--;
						listaErrosConvocacao.add(new ResultadoPessoaConvocacaoTecnico (pessoa, erros));
					}
				}
			}
			
			mapaResultado[0] = sucessos;
			mapaResultado[1] = listaErrosConvocacao;
		} finally {
			if (daoCPS != null) daoCPS.close();
			if (daoCPSD != null) daoCPSD.close();
			if (pessoaDao != null) pessoaDao.close();
			if (pessoaTecnicoDao != null) pessoaTecnicoDao.close();
			
			MovimentoCadastro movCad = (MovimentoCadastro) mov;
			ConvocacaoProcessoSeletivoTecnico obj = movCad.getObjMovimentado();
			if (obj.getOpcao() == null) obj.setOpcao(new OpcaoPoloGrupo());
		}
		
		return mapaResultado;
	}

	/**
	 * Método auxiliar utilizado para popular um objeto {@link Pessoa} a partir 
	 * dos dados de um objeto {@link PessoaVestibular} do candidato do vestibular.
	 * @param pTecnico
	 * @return
	 * @throws DAOException 
	 * @throws NegocioException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public Pessoa popularPessoa (Pessoa pessoa, PessoaTecnico pTecnico, PessoaDao dao) throws DAOException, NegocioException {
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
			pTecnico.setEnderecoContato( dao.findByPrimaryKey(pTecnico.getEnderecoContato().getId(), Endereco.class) );
			pTecnico.getEnderecoContato().getMunicipio().getUnidadeFederativa();		
			
			BeanUtils.copyProperties(pessoa, pTecnico);
			if (existePessoa) {
				pessoa.setId( pessoaAux.getId() );
				if ( pessoaAux.isFuncionario() 		== null ) pessoa.setFuncionario(pessoaAux.isFuncionario());
				if ( pessoaAux.getDataCadastro() 	== null ) pessoa.setDataCadastro(pessoaAux.getDataCadastro());
				if ( pessoaAux.getOrigem() 			== null ) pessoa.setOrigem(pessoaAux.getOrigem());
			} else {
				pessoa.setId( 0 );
				pessoa.setDataCadastro(new Date());
			}
			
			pessoa.setNome(pTecnico.getNome().replace(".", "").toUpperCase());
			pessoa.setNomeMae(pTecnico.getNomeMae().replace(".", "").toUpperCase());
			pessoa.setNomePai(StringUtils.isEmpty(pTecnico.getNomePai()) ? null : pTecnico.getNomePai().replace(".", "").toUpperCase());
			pessoa.setIdentidade(pTecnico.getIdentidade());
			pessoa.setEnderecoContato( pTecnico.getEnderecoContato() );
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