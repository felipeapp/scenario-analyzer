package br.ufrn.sigaa.ensino.metropoledigital.negocio;

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
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeAluno;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ConvocacaoIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.RegistroQuantitativoConvocadosGrupo;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaGrupo;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaProcessoSeletivo;
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
import br.ufrn.sigaa.ensino.tecnico.dominio.SituacaoCandidatoTecnico;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.SituacaoCandidato;
/**
 * Processador respons�vel pela efetiva��o da convoca��o dos candidatos aprovados no processo seletivo do IMD.
 * 
 * @author Rafael Barros
 *
 */
public class ProcessadorConvocacaoProcessoSeletivoTecnicoIMD extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);

		//DAO's que ser�o utilizados no processo de convoca��o
		ConvocacaoProcessoSeletivoTecnicoDao daoCPS = null;
		ConvocacaoProcessoSeletivoDiscenteTecnicoDao daoCPSD = null;
		ConvocacaoIMDDao convDao = null;
		PessoaDao pessoaDao = null;
		PessoaTecnicoDao pessoaTecnicoDao = null;
		
		// Vari�vel de controle que ser� decrementada. J� EXISTENTE E N�O FOI ENCONTRADA UTILIDADE ??? 
		//int i = 0;
		
		// Atributo fixa que ir� armazenar a quantidade m�xima de discentes a ser convocada. Esse atributo ser� informada via formul�rio.
		int qtdInformada = 0; 
		
		// Atributo vari�vel que ir� armazenar a quantidade m�xima de discentes a ser convocada. Esse atributo ficar� sendo decrementado durante a execu��o da convoca��o.
		int qtdMaximaASerConvocada = 0;
		
		// Atributo que ir� armazenar a quantidade total de discentes que foram convocados em todo o processamento.
		int totalGeralConvocados = 0; 
		
		// mapa de resultado, com as quantidades de convoca��es e lista de mensagens de erros.
		Object [] mapaResultado = new Object[2];
		
		try {
			// DAO's que ser�o utilizados no processo de convoca��o
			daoCPS = getDAO(ConvocacaoProcessoSeletivoTecnicoDao.class, mov);
			daoCPSD = getDAO(ConvocacaoProcessoSeletivoDiscenteTecnicoDao.class, mov);
			convDao = getDAO(ConvocacaoIMDDao.class, mov);
			pessoaDao = getDAO(PessoaDao.class, mov);
			pessoaTecnicoDao = getDAO(PessoaTecnicoDao.class, mov);
			
			MovimentoCadastro movimento = ((MovimentoCadastro) mov);
			ConvocacaoProcessoSeletivoTecnico obj = movimento.getObjMovimentado();
	
			// Vari�vel que ir� armazenar a quantidade total de vagas que o processo seletivo est� ofertando
			Long totalVagasGeral = convDao.findTotalVagasPS(obj.getProcessoSeletivo().getId());			
			
			// Atribui��o dos atributos que ir�o armazenar e manipular quantidade total a ser convocada
			qtdInformada = Integer.parseInt(obj.getQuantidadeDiscentesSemReserva().toString());
			qtdMaximaASerConvocada = qtdInformada;
			
			// Inicializar a vari�vel local que ir� armazenar a quantidade total do grupo de vagas anterior que foi executada no la�o
			int totalGrupoAnterior = 0;
			
			// Vari�vel que ir� armazenar a op��o p�lo grupo anterior que foi executada no la�o
			OpcaoPoloGrupo opcaoPoloGrupoAnterior = new OpcaoPoloGrupo();

			// Comando que retorna a cole��o de reservas de vagas para um determinado Processo Seletivo
			Collection<ReservaVagaProcessoSeletivo> listaReservas = convDao.findReservasByPS(obj.getProcessoSeletivo().getId(), obj.getOpcao().getId(), obj.getGrupo().getId());  // order by reserva.tipo.prioridade asc
			
			// Cria��o de uma cole��o de resultados que ir� armazenar os discentes na supl�ncia
			Collection <ResultadoClassificacaoCandidatoTecnico> resultadosCandidatosBuscados = new ArrayList <ResultadoClassificacaoCandidatoTecnico> ();
			
			// La�o sobre as reservas de vagas encontradas para o processo seletivo, op��o p�lo grupo e o grupo de reserva de vagas informados pelo usu�rio
			for ( ReservaVagaProcessoSeletivo reserva : listaReservas ) {
				
				// Condi��o que verifica se a op��o p�lo grupo foi alterada durante a intera��o do la�o
				// Caso seja verdadeiro, a vari�vel totalGrupoAnterior � ZERADA, ou seja, quando a op��o p�lo grupo for alterada, o incremento da distribui��o das vagas entre os grupos � zerado.
				if(opcaoPoloGrupoAnterior.getId() != reserva.getOpcao().getId()) {
					totalGrupoAnterior = 0;
					
				}	
				
				// Setando a quantidade de vagas remanescentes atrav�s de uma consulta que retorna a quantidade de vagas remanescentes em todo o P�LO, ou seja, a quantidade de vagas que sobraram entre todas as op��es p�lo grupo de um determinado p�lo
				reserva.setRemanescentes(convDao.findTotalRemanescentePoloByPSAndPolo(obj.getProcessoSeletivo().getId(), reserva.getOpcao().getPolo().getId(), StatusDiscente.EXCLUIDO)); // find count todos os convocados que compareceram.
				
				
				/*System.out.println("QTD REMANESCENTES: " + reserva.getRemanescentes());
				System.out.println("GRUPO: " + reserva.getTipoReserva().getDenominacao());
				System.out.println("POLO: " + reserva.getOpcao().getDescricao());*/
				
				// Vari�vel que ir� controlar a quantidade total a ser convocada de um determinado grupo de reserva de vagas
				// totalConvocar vai receber a quantidade de vagas remanescentes + a quantidade de vagas do grupo anterior que sobraram
				
				/*
				 * 
				 * BLOQUEAR QUE O SISTEMA CONVOQUE ACIMA DA QUANTIDADE DE VAGAS PARA UMA RESERVA DE VAGA NO PROCESSO SELETIVO
				 * VALIDA��O RETIRADA DEVIDO PROCESSO SELETIVO 2014.1 NECESSITAR CONVOCAR TODOS OS CANDIDATOS, INDEPENDENTE SE H� VAGAS OU N�O.
				 * 
				 */
				//int totalConvocar =  Integer.parseInt(reserva.getRemanescentes().toString()) + totalGrupoAnterior;
				
				int totalConvocar =  Integer.parseInt(reserva.getRemanescentes().toString()) + totalGrupoAnterior + qtdMaximaASerConvocada;

				// Condi��o que verifica se ainda existem quantidade de discentes a serem convocados.
				if ( totalConvocar > 0 && qtdMaximaASerConvocada > 0) {
					
					// Cria��o de uma cole��o de objetos que ir�o armazenar os discentes a serem convocados
					Collection<ResultadoClassificacaoCandidatoTecnico> resultadoDiscentes = null;
					
					resultadoDiscentes = convDao.findCandidatosRemanescentes(obj.getProcessoSeletivo().getId(), reserva.getOpcao().getId(), reserva.getTipoReserva().getId(), qtdMaximaASerConvocada);
					
					/*
					 * 
					 * BLOQUEAR QUE O SISTEMA CONVOQUE ACIMA DA QUANTIDADE DE VAGAS PARA UMA RESERVA DE VAGA NO PROCESSO SELETIVO
					 * VALIDA��O RETIRADA DEVIDO PROCESSO SELETIVO 2014.1 NECESSITAR CONVOCAR TODOS OS CANDIDATOS, INDEPENDENTE SE H� VAGAS OU N�O.
					 * 
					 */
					/*
					// Condi��o que verifica se a quantidade m�xima informada � menor ou igual a quantidade a ser convocada para o grupo de reserva de vagas
					// Caso seja verdadeiro, a consulta que retorna os discentes remanescentes deve trazer no m�ximo a quantidade m�xima informada
					if(qtdMaximaASerConvocada <= totalConvocar) {
						//Consulta que retorna os discentes a serem convocados de acordo com o processo seletivo, a op��o p�lo grupo e o grupo de reserva de vagas
						resultadoDiscentes = convDao.findCandidatosRemanescentes(obj.getProcessoSeletivo().getId(), reserva.getOpcao().getId(), reserva.getTipoReserva().getId(), qtdMaximaASerConvocada);
					} 
					// Caso seja false, a consulta que retorna os discentes remanescentes deve trazer no m�ximo a quantidade a ser convocada do grupo de reserva de vagas
					else {
						//Consulta que retorna os discentes a serem convocados de acordo com o processo seletivo, a op��o p�lo grupo e o grupo de reserva de vagas
						resultadoDiscentes = convDao.findCandidatosRemanescentes(obj.getProcessoSeletivo().getId(), reserva.getOpcao().getId(), reserva.getTipoReserva().getId(), totalConvocar);
					}*/
					
					// Vari�vel que ir� armazenar o total convocado de um grupo de reserva de vagas
					int totalGrupo = resultadoDiscentes.size();
					
					/*
					System.out.println("TOTAL GRUPO: " + totalGrupo);
					System.out.println("TOTAL M�XIMO QUE PODE SER CONVOCADO: " + totalConvocar);
					System.out.println("QTD M�XIMA INFORMADA: " + qtdMaximaASerConvocada);
					
					int contador = 0;
					for ( ResultadoClassificacaoCandidatoTecnico registro : resultadoDiscentes ) {
						//convIMDDAO.updateField("situacao", CONVOCADO );
						
						System.out.println("REGISTRO " + contador + ": " + registro.getId() + " - INSC. " + registro.getInscricaoProcessoSeletivo().getNumeroInscricao() + " - POS. " + registro.getClassificacaoAprovado() + " - ARG. " + registro.getArgumentoFinal() + " - NOME: " + registro.getInscricaoProcessoSeletivo().getPessoa().getNome() );						
						contador++;
					}*/
					
					// Incremento da vari�vel que armazena a quantidade total de discentes convocados em TODO o processamento
					totalGeralConvocados += resultadoDiscentes.size();
					
					// Decremento da vari�vel que armazena a quantidade m�xima a ser convocada que foi informada pelo usu�rio
					qtdMaximaASerConvocada -= resultadoDiscentes.size();
					
					// Condi��o que verificar se a lista de discentes convocados para um determinado grupo N�O est� vazia
					// Caso seja verdadeiro, adiciona-se toda a lista na cole��o geral de objetos dos discentes convocados em todo o processamento
					if(resultadoDiscentes != null && ! resultadoDiscentes.isEmpty()) {
						resultadosCandidatosBuscados.addAll(resultadoDiscentes);
					}
					
					// Condi��o que verifica se o total a ser convocado pelo grupo � maior do que a quantidade que realmente foi convocada
					// Caso seja verdadeiro, � armazenada a quantidade restante de vagas do grupo. Essa quantidade de vagas ser� disponibilizada para o grupo seguinte.
					if ( totalGrupo < totalConvocar ) {
						totalGrupoAnterior =  totalConvocar - totalGrupo ;
					}
				}
				
				// Condi��o que verifica se a quantidade m�xima j� foi atingida
				// Caso seja verdadeiro, o la�o de repeti��o � finalizado.
				if(qtdMaximaASerConvocada <= 0) {
					break;
				}
			}
/*
			if (resultadosCandidatosBuscados.isEmpty()) {
				throw new NegocioException ("N�o h� candidatos a convocar com esse filtro.");
			}
			
			if (obj.getOpcao() != null && obj.getOpcao().getId() == 0) {
				obj.setOpcao(null);
			}
				
			daoCPS.create(obj);
				
			if (obj.getOpcao() == null) {
				obj.setOpcao(new OpcaoPoloGrupo());
			}
*/
			
			
			// Condi��o que verifica se algum discente foi convocado
			if(resultadosCandidatosBuscados.isEmpty()){
				throw new NegocioException ("N�o h� candidatos a convocar com esse filtro.");
			}
			
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
			// erros de valida��o de cadastros pessoais
			List<ResultadoPessoaConvocacaoTecnico> listaErrosConvocacao = new ArrayList<ResultadoPessoaConvocacaoTecnico>();
			// Convoca��es bem sucedidas;
			List <ResultadoClassificacaoCandidatoTecnico> sucessos = new ArrayList <ResultadoClassificacaoCandidatoTecnico> ();
			
			
			// Cria��o do registro geral da convoca��o
			ConvocacaoProcessoSeletivoTecnico convocacaoTemp = new ConvocacaoProcessoSeletivoTecnico();
			convocacaoTemp.setDescricao(obj.getDescricao());
			convocacaoTemp.setDataCadastro(obj.getDataCadastro());
			convocacaoTemp.setDataConvocacao(obj.getDataConvocacao());
			convocacaoTemp.setProcessoSeletivo(obj.getProcessoSeletivo());
			convocacaoTemp.setQuantidadeDiscentesComReserva(obj.getQuantidadeDiscentesComReserva());
			convocacaoTemp.setQuantidadeDiscentesSemReserva(obj.getQuantidadeDiscentesSemReserva());
			convocacaoTemp.setTodosAprovados(obj.isTodosAprovados());
			convocacaoTemp.setOpcao(obj.getOpcao());
			convocacaoTemp.setGrupo(obj.getGrupo());
			convocacaoTemp.setConvocadoPor(obj.getConvocadoPor());
			
			if(convocacaoTemp.getOpcao().getId() <= 0)
			{
				convocacaoTemp.setOpcao(null);
			}
			
			if(convocacaoTemp.getGrupo().getId() <= 0)
			{
				convocacaoTemp.setGrupo(null);
			}
			daoCPS.create(convocacaoTemp);
			
			
			
			for ( ResultadoClassificacaoCandidatoTecnico resultadoCandidato : resultadosCandidatosBuscados ) {
				//System.out.println("Processando convoca��o " + (i++));
				
				ReservaVagaGrupo grupoInsc = convDao.findGrupoByInscricao(resultadoCandidato.getInscricaoProcessoSeletivo());
				resultadoCandidato.getInscricaoProcessoSeletivo().setGrupo(grupoInsc);
				
				resultadoCandidato.setSituacaoCandidato( SituacaoCandidatoTecnico.APROVADO);
				daoCPS.createOrUpdate(resultadoCandidato);
				
				ConvocacaoProcessoSeletivoDiscenteTecnico convocacaoDiscente = new ConvocacaoProcessoSeletivoDiscenteTecnico ();
				PessoaTecnico pTecnico = new PessoaTecnico ();
				Pessoa pessoa = new Pessoa();
				
				//if ( !mapaConvocados.get(resultadoCandidato.getInscricaoProcessoSeletivo().getId()) ){
					convocacaoDiscente =  new ConvocacaoProcessoSeletivoDiscenteTecnico ();
					pTecnico = mapaPessoaVestibular.get(resultadoCandidato.getInscricaoProcessoSeletivo().getPessoa().getId());
					pessoa = mapaPessoa.get(pTecnico.getCpf_cnpj());
					
					pessoa = popularPessoa(pessoa, pTecnico, pessoaDao);
					
					ListaMensagens erros = new ListaMensagens();
					PessoaValidator.validarDadosPessoais(pessoa, null, PessoaValidator.DISCENTE, erros);
					
					if ( erros.isEmpty() ){
						
						DiscenteTecnico discente = new DiscenteTecnico();
						discente.setPessoa(pessoa);
						
						if(pessoa.getEnderecoContato() != null){
							if(pessoa.getEnderecoContato().getId() <= 0) {
								pessoa.setEnderecoContato(null);
							} else if(pessoa.getEnderecoContato().getUnidadeFederativa() != null) {
								if(pessoa.getEnderecoContato().getUnidadeFederativa().getId() <= 0) {
									pessoa.getEnderecoContato().setUnidadeFederativa(null);
								} else {
									pessoa.getEnderecoContato().setTipoLogradouro(new TipoLogradouro(TipoLogradouro.RUA));
								}
							}
							
						} else {
							pessoa.setEnderecoContato(null);
						}
						
						
						
						if(pessoa.getIdentidade() != null) {
							if(pessoa.getIdentidade().getUnidadeFederativa() != null) {
								if(pessoa.getIdentidade().getUnidadeFederativa().getId() <= 0) {
									pessoa.getIdentidade().setUnidadeFederativa(null);
								}
							} 
						} else {
							pessoa.setIdentidade(null);
						}
						
						if(pessoa.getUnidadeFederativa() != null) {
							if(pessoa.getUnidadeFederativa().getId() <= 0) {
								pessoa.setUnidadeFederativa(null);
							}
						}
						
						if(pessoa.getEstadoCivil() != null){
							if(pessoa.getEstadoCivil().getId() <= 0) {
								pessoa.setEstadoCivil(null);
							}
						} else {
							
						}
						
						
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
						
						
						
						convocacaoDiscente.setConvocacaoProcessoSeletivo(convocacaoTemp);
						convocacaoDiscente.setInscricaoProcessoSeletivo(resultadoCandidato.getInscricaoProcessoSeletivo());
						convocacaoDiscente.setDiscente(discente);
						convocacaoDiscente.setPeriodo(discente.getPeriodoIngresso());
						convocacaoDiscente.setAno(discente.getAnoIngresso());
						
						daoCPSD.create(convocacaoDiscente);
							
						sucessos.add(resultadoCandidato);
						
					} else {
						//i--;
						ResultadoPessoaConvocacaoTecnico itemErro = new ResultadoPessoaConvocacaoTecnico (pessoa, erros);
						listaErrosConvocacao.add(itemErro);
					}
				//}
			}
			
			mapaResultado[0] = sucessos;
			mapaResultado[1] = listaErrosConvocacao;
			
			System.out.println("SUCESSOS:");
			for(ResultadoClassificacaoCandidatoTecnico reg: sucessos ){
				System.out.println("REG: " + reg.getId() + " - " + reg.getClassificacaoAprovado() + " = " + reg.getArgumentoFinal() + " = " + reg.getInscricaoProcessoSeletivo().getNumeroInscricao() + " = " + reg.getInscricaoProcessoSeletivo().getPessoa().getNome());
			}
			
			System.out.println("ERROS: ");
			for(ResultadoPessoaConvocacaoTecnico reg2: listaErrosConvocacao){
				System.out.println("ERR: " + reg2.getListaMensagens() + " = " + reg2.getPessoa().getNome());
			}
			
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
	 * M�todo auxiliar utilizado para popular um objeto {@link Pessoa} a partir 
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
			if(pTecnico.getEnderecoContato() != null) {
				if(pTecnico.getEnderecoContato().getId() <= 0) {
					pTecnico.setEnderecoContato(null);
				}
			} else {
				pTecnico.setEnderecoContato(null);
			}
			
			if(pTecnico.getIdentidade() != null) {
				if(pTecnico.getIdentidade().getUnidadeFederativa() != null) {
					if(pTecnico.getIdentidade().getUnidadeFederativa().getId() <= 0) {
						pTecnico.getIdentidade().setUnidadeFederativa(null);
					}
				} else  {
					pTecnico.getIdentidade().setUnidadeFederativa(null);
				}
			} else {
				pTecnico.setIdentidade(null);
			}
			
			if(pTecnico.getUnidadeFederativa() != null) {
				if(pTecnico.getUnidadeFederativa().getId() <= 0) {
					pTecnico.setUnidadeFederativa(null);
				}
			} else {
				pTecnico.setUnidadeFederativa(null);
			}
			
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
			
			//pessoa.setNomeMae(pTecnico.getNomeMae().replace(".", "").toUpperCase());
			pessoa.setNomeMae(StringUtils.isEmpty(pTecnico.getNomeMae()) ? null : pTecnico.getNomeMae().replace(".", "").toUpperCase());
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