/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Created on 22/06/2009
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ConsultasEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.NotaCirculacaoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.PoliticaEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.NotaCirculacao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca.StatusReserva;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.integracao.dtos.OperacaoBibliotecaDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.RetornoOperacoesCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.RegistraEstatisticasBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * 
 * Classe que contém as regras de negócio de para a realização de um empréstimo.<br>
 * 
 * 
 * @author jadson
 * @since 26/09/2008
 * @version 1.0 criação da classe
 * 
 */
public class ProcessadorRealizaEmprestimo extends ProcessadorCadastro {

	/**
	 * Ver comentário na classe pai
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		long tempo = System.currentTimeMillis();
		
		MovimentoRealizaEmprestimo movimentoRealizaEmprestimo = (MovimentoRealizaEmprestimo) mov;
		UsuarioGeral operador = movimentoRealizaEmprestimo.getUsuarioLogado();

		EmprestimoDao dao = null;
		PermissaoDAO pDao = null;
		MaterialInformacionalDao matDao = null;
		PoliticaEmprestimoDao politicaDao = null;
		NotaCirculacaoDao daoNota = null;
		ReservaMaterialBibliotecaDao reservaDao = null;
		ConsultasEmprestimoDao consultaEmprestimoDao = null;
		BibliotecaDao bibliotecaDao = null;
		
		UsuarioBiblioteca usuarioBiblioteca = movimentoRealizaEmprestimo.getUsuarioBiblioteca();
		List <MaterialInformacional> materiaisAEmprestar = null;

		
		/**
		 * Objeto que por padrão deve ser retornado quando é realizado alguma operação relacionado a empréstimos em circulação.
		 */
		RetornoOperacoesCirculacaoDTO retornoProcessadorCirculacao = new RetornoOperacoesCirculacaoDTO();
		
		try {
			
			dao = getDAO(EmprestimoDao.class, movimentoRealizaEmprestimo);
			pDao = getDAO(PermissaoDAO.class, movimentoRealizaEmprestimo, Sistema.COMUM);
			daoNota = getDAO(NotaCirculacaoDao.class, movimentoRealizaEmprestimo);
			reservaDao  = getDAO(ReservaMaterialBibliotecaDao.class, movimentoRealizaEmprestimo);
			consultaEmprestimoDao = getDAO(ConsultasEmprestimoDao.class, movimentoRealizaEmprestimo);
			bibliotecaDao = getDAO(BibliotecaDao.class, movimentoRealizaEmprestimo);
			
			validate(movimentoRealizaEmprestimo);
			
			
			/// Garregas as permissões do operador ///
			
			operador.setPermissoes(pDao.findPermissoesByUsuario(operador));
			
			operador.setPapeis(new ArrayList <Papel>());
			
			for (Permissao p : operador.getPermissoes()){
				operador.getPapeis().add(p.getPapel());
			}
			
						
			politicaDao = getDAO(PoliticaEmprestimoDao.class, movimentoRealizaEmprestimo);
			matDao = getDAO(MaterialInformacionalDao.class, movimentoRealizaEmprestimo);
			
			// Recupera os empréstimos ativos do usuário do banco //
			List <Emprestimo> emprestimosAtivos = consultaEmprestimoDao.findEmprestimosAtivosByUsuario(usuarioBiblioteca.getId());

			// Empréstimos a se realizar.
			List <Emprestimo> emprestimos = new ArrayList<Emprestimo>();

			// As seserva que serão consolidades pelos empréstimos que vão ser reailzados
			List<ReservaMaterialBiblioteca> reservasConsolidadas = new ArrayList<ReservaMaterialBiblioteca>();
			
			
			// Valida se o usuário possui empréstimos em atraso. Se tiver, já lança execeção e não verifica mais nada
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiEmprestimosAtrazados(emprestimosAtivos);
			
			// Valida se o usuário está com punição na biblioteca. Se possuir, já lança execeção e não verifica mais nada
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoes(usuarioBiblioteca.getIdentificadorPessoa()
					, usuarioBiblioteca.getIdentificadorBiblioteca() );

			
			// ///////////////////////////////////////////////////////////////
			// Para cada um dos Materiais que vai ser emprestado, verificar
			// 
			// Se o material está disponível (SituacaoMaterialInformacional) para empréstimo
			// Se o mesmo material não foi emprestado para o mesmo usuário em menos de 24 horas //
			// Se o status do material é "especial" ou "não circula" //
			// Se não tem mais de um material com mesmo volume de um mesmo título entre os empréstimos //
			// Se as quantidades por regra de empréstimo não estouraram. A quantidade é por biblioteca //
			// (levar em conta no cálculo os anexos, que são contados apenas como 1 material) //
			// 
			// Se o material está bloqueado ou possui alguma nota de circulação
			// Configurar as informações do empréstimo e mudar a SITUAÇÃO do material para emprestado //
			// ////////////////////////////////////////////////////////////////////////////////////////

			materiaisAEmprestar = matDao.findMateriaisAtivosByPrimaryKeyIn(new ArrayList <Integer>(movimentoRealizaEmprestimo.getIdsTiposEmprestimosMateriais().keySet()));

			
			// IMPORTANTE: não esquecer de ordenar para a regra do anexo funcionar
			CirculacaoUtil.ordenaMateriaisByAnexos(materiaisAEmprestar);
			
			
			// A lista de prorrogações criadas.
			List <ProrrogacaoEmprestimo> prorrogacoes = new ArrayList <ProrrogacaoEmprestimo> ();
			
			SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
			
			
			/* Relação de ordem total <ID_TITULO, QTD_MATERIAIS> que guarda a quantidade de materiais emprestados para um determinado Título.
			 * 
			 * Usado na parte de reservas para somar a quantidade de materiais disponíveis no banco com a quantidade de materiais emprestado para o usuário 
			 * mas que o empréstimo não foi persistido.  Para veririca se a quantidade que sobre é maior que a quantidade de reservas EM ESPERA e deixar outros 
			 * usuários levarem os materiais disponíveis
			 */
			Map<Integer, Integer> qtdMateriaisEmprestadosParaTitulo = new TreeMap<Integer, Integer>();
			
			
			for (MaterialInformacional material : materiaisAEmprestar){
				
				// Como está processando vários empréstimos de uma vez, vai parar no primeiro que der erro.
				// Salva a id material atual para saber em qual deu erro e poder avisar corretamente ao usuário.
				movimentoRealizaEmprestimo.setIdMaterialProcessando(material.getId());
				
				verificaPermissaoEBibliotecaEmprestimo(operador, material, movimentoRealizaEmprestimo); 
					
				
				// Uma referência ao tipo empréstimo para este material.
				TipoEmprestimo tipoEmprestimo = dao.refresh(new TipoEmprestimo (movimentoRealizaEmprestimo.getIdsTiposEmprestimosMateriais().get(material.getId())));

				// Verifica se o material está disponível para empréstimos.
				verificaMaterialEstaDisponivel(material, dao);
				
				// Descobre qual é a política de empréstimo para este material.
				PoliticaEmprestimo politicaEmprestimo = politicaDao.findPoliticaEmpretimoAtivaASerUsuadaNoEmprestimo(
																			material.getBiblioteca(),
																			usuarioBiblioteca.getVinculo(),
																			tipoEmprestimo,
																			material.getStatus(),
																			material.getTipoMaterial() );
				
				if (politicaEmprestimo == null)
					throw new NegocioException ("Não há uma Política de Empréstimo ativa para o material de código de barras " + material.getCodigoBarras());

				
				if (usuarioBiblioteca.ehBiblioteca()){
					verificaMaterialPossuiOutroDisponivel(material, movimentoRealizaEmprestimo); // Se for para uma biblioteca, deve verificar se há outro material disponível.
				
					// verifica se o usuário está tentando emprestar um material para a biblioteca que o pussui
					// O empréstio só tem lógica se for para uma biblioteca diferente
					vericaEmprestimoBibliotecaDoMaterial(material, usuarioBiblioteca, movimentoRealizaEmprestimo); 
				}
				
				// Verifica se já se passaram vinte e quatro horas desde o último empréstimo deste material para este usuário.
				verificaTempoLimiteEntreEmprestimos(material, usuarioBiblioteca, dao);

				if (material instanceof Exemplar)
					verificaExemplaresDeUmMesmoTitulo((Exemplar) material, emprestimosAtivos, emprestimos, dao);

				// Verifica se o usuário não está tentando emprestar mais materiais do que pode, para a política atual.
				verificaQuantidadePorRegraDeEmprestimo(material, politicaEmprestimo, emprestimosAtivos, emprestimos, dao);

				// Verifica se o serviço de empréstimos e renovação estão ativos na biblioteca
				CirculacaoUtil.verificaServicoEmprestimosEstaoAtivos(material.getId(), material.getCodigoBarras());
				
				// Verifica se a biblioteca do material realiza empréstimo para o usuário //
				verificaOperacoesBiblioteca(movimentoRealizaEmprestimo, consultaEmprestimoDao, bibliotecaDao, material.getBiblioteca(), tipoEmprestimo);
				
				
				
				// Verifica se possui alguma nota de circulação
				verificaNotasDeCirculacao(daoNota, material, retornoProcessadorCirculacao);
				
				

				
				// Prepara o empréstimo
				Emprestimo e = new Emprestimo();
				e.setDataEmprestimo(new Date());

				// Seta o usuário que está emprestando.
				e.setUsuarioBiblioteca(movimentoRealizaEmprestimo.getUsuarioBiblioteca());
				
				// Seta a política do empréstimo.
				e.setPoliticaEmprestimo(politicaEmprestimo);

				// Seta a situação do material para Emprestado.
				material.setSituacao(new SituacaoMaterialInformacional(situacaoEmprestado.getId()));

				// Associa o material ao empréstimo.
				e.setMaterial(material);
				
				// Calcula e seta o prazo do empréstimo.
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				
				// Se a política de empréstimo é personalizável, o usuário define o prazo.
				if (politicaEmprestimo.isPersonalizavel())
					politicaEmprestimo.setPrazoEmprestimo(movimentoRealizaEmprestimo.getDiasAEmprestar());

				if (politicaEmprestimo.isPrazoContadoEmDias()){
					c.add(Calendar.DAY_OF_MONTH, politicaEmprestimo.getPrazoEmprestimo());
					e.setPrazo(CalendarUtils.configuraTempoDaData(c.getTime(), 23, 59, 59, 999));
				} else {
					c.add(Calendar.HOUR_OF_DAY, politicaEmprestimo.getPrazoEmprestimo());
					e.setPrazo(c.getTime());
				}
				
				
				if(ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas()){
					
					int idTituloDoMaterial = matDao.findIdTituloMaterial(material.getId());
					
					int qtdMateriasTituloReservaEmMemoria = 0;
					
					if( qtdMateriaisEmprestadosParaTitulo.containsKey(idTituloDoMaterial) )
						qtdMateriasTituloReservaEmMemoria = qtdMateriaisEmprestadosParaTitulo.get(idTituloDoMaterial);
					
					
					int quantidadeMateriasNaoAnexosDisponiveis = reservaDao.countMateriaisNaoAnexoDisponiveisDoTitulo(idTituloDoMaterial);
					
					List<ReservaMaterialBiblioteca> reservasEmEspera = reservaDao.findReservasEmEsperaDoTituloDoMaterialEmOrdem(idTituloDoMaterial);
					
					/* IMPORTANTE: caso existam materiais disponíveis a mais que as quantidade de reservas em espera
					 * é porque todas as reservas feitas já estão EM ESPERA e existem materiais sobrando.
					 * 
					 * Se existissem reservas solicitadas era uma inconsistência do sistema, pois na devolução do empréstimo, a medida que os materiais são devolvidos as próximas
					 * reservas da fila deveriam ir para o status EM ESPERA. Se não existir outra reserva na fila, uma outra não pode ser solicitada, pois existe material disponível.
					 * 
					 * Nesse caso pode deixar outros usuários, que não estejam entre os que solicitaram a reserva, levar emprestado o material
					 */
					
					if( (quantidadeMateriasNaoAnexosDisponiveis-qtdMateriasTituloReservaEmMemoria) <= reservasEmEspera.size()){
						
						// Se entrou aqui, é porque somente quem tem reserva pode realizar o empréstimo //
						
					
						ReservaMaterialBiblioteca reservaDoUsuario = getReservaDoUsuario(reservasEmEspera, movimentoRealizaEmprestimo.getUsuarioBiblioteca()); // Pega a primeira reserva em espera
						
						if(reservaDoUsuario != null ){ // Se tem reserva para o usuário que está tentando fazer o empréstimo
							
							reservaDoUsuario.setStatus(StatusReserva.CONCRETIZADA);
							reservaDoUsuario.setEmprestimosGerado(e);
							reservasConsolidadas.add(reservaDoUsuario);
							
						}else{ // se não não deixa fazer o empréstimo
							throw new NegocioException("O empréstimo não pode ser realizado, pois existem reservas em espera para outros usuários .");
						}
						
					}
					
					qtdMateriaisEmprestadosParaTitulo.put(idTituloDoMaterial, ++qtdMateriasTituloReservaEmMemoria);
					
				}
				
				
				//Calcula o prazo correto para o empréstimo, livrando feriados e finais de semana, dependendo da biblioteca do material emprestado.
				prorrogacoes.addAll(CirculacaoUtil.geraProrrogacoesEmprestimo(e, null));
				
				// Adiciona o empréstimo à lista de empréstimos a realizar.
				emprestimos.add(e);
			
			
			} // fecha o for "materiaisAEmprestar"
			
			
			List <Integer> idsMateriais = new ArrayList <Integer> ();
			
			for (Emprestimo e : emprestimos) {
				
				e.setUsuarioRealizouEmprestimo((Usuario) operador);
				//configuraTipoUsuarioEmpretimo(movimentoRealizaEmprestimo, e);
				
				idsMateriais.add(e.getMaterial().getId());
				matDao.create(e); // Realiza o empréstimo
			}
			
			
			if(idsMateriais.size() > 0){
				
				// Seta a situação dos materiais para EMPRESTADO ///
				matDao.atualizaSituacaoDeMateriais(idsMateriais, situacaoEmprestado.getId());
			
			
				// Registra o empréstimo dos Títulos desses materiais  /// 
				RegistraEstatisticasBiblioteca.getInstance().registrarTitulosEmprestados(idsMateriais);
				
			}	
			
			for (ReservaMaterialBiblioteca reserva : reservasConsolidadas) {
				reservaDao.updateFields(ReservaMaterialBiblioteca.class, reserva.getId(), new String[]{"status", "emprestimosGerado.id"}, new Object[]{reserva.getStatus(), reserva.getEmprestimosGerado().getId()});
			} 
			
			
			
			// Registra as prorrogações que os empréstimos sofreram por cair em feriados ou finais de semana, dependendo da biblioteca.
			if (!prorrogacoes.isEmpty())
				for (ProrrogacaoEmprestimo p : prorrogacoes)
					matDao.create(p);

			// Retorna as operações que foram feitas para depois poder desfazer, se for necessário.
			List<OperacaoBibliotecaDto> emprestimosRealizados = new ArrayList<OperacaoBibliotecaDto>();

			for (int i = 0; i < emprestimos.size(); i++) {
				OperacaoBibliotecaDto op = new OperacaoBibliotecaDto(OperacaoBibliotecaDto.OPERACAO_EMPRESTIMO
						, emprestimos.get(i).getMaterial().getId(), emprestimos.get(i).getId(), emprestimos.get(i).getMaterial().getCodigoBarras()
						, BibliotecaUtil.obtemDadosMaterialInformacional( emprestimos.get(i).getMaterial().getId()), emprestimos.get(i).getDataEmprestimo(), emprestimos.get(i).getPrazo() );
				
				emprestimosRealizados.add(op);
			}
			
			retornoProcessadorCirculacao.addOperacoesRealizadas(emprestimosRealizados);

			enviaEmailConfirmacaoEmprestimo(movimentoRealizaEmprestimo, emprestimosRealizados);
			
			return retornoProcessadorCirculacao;
			
		} finally {
			if (dao != null) dao.close();
			
			if (pDao != null) pDao.close();
			
			if (politicaDao != null) politicaDao.close();
			
			if (matDao != null) matDao.close();
			
			if (daoNota != null) daoNota.close();
			
			if (reservaDao != null) reservaDao.close();
			
			if (consultaEmprestimoDao != null) consultaEmprestimoDao.close();
			
			if (bibliotecaDao != null) bibliotecaDao.close();
			
			System.out.println("    ***    Realizar empréstimos demorou: "+ (System.currentTimeMillis()-tempo) +" ms       ***     ");   // tempo <= 1s
		}
	}



	/**
	 *   Envia um email para comprovar o empréstimo
	 *
	 * @param usuarioBibliotecaRenovacao
	 * @throws DAOException
	 */
	private void enviaEmailConfirmacaoEmprestimo(MovimentoRealizaEmprestimo movimento, List <OperacaoBibliotecaDto> emprestimosRealizados) throws DAOException{
		
		/* 
		 * informacoesUsuario[0] == nome Usuario
		 * informacoesUsuario[1] == email Usuario
		 */
		Object[] informacoesUsuario =  getDAO( UsuarioBibliotecaDao.class, movimento).findNomeEmailUsuarioBiblioteca(movimento.getUsuarioBiblioteca());
		
		String assunto = " Confirmação de Realização de Empréstimos ";
		String titulo = " Empréstimos Realizados ";
		String mensagemUsuario = " Os empréstimos abaixo foram realizados com sucesso: ";
		
		List<String> listaInformacoesEmail = new ArrayList<String>();
		
		for (OperacaoBibliotecaDto operacaoEmprestimo : emprestimosRealizados) {
			listaInformacoesEmail.add(" <strong> Data do Empréstimo: </strong>"+ operacaoEmprestimo.getDataRealizacaoFormatada()
					+" <strong> Prazo para Devolução: </strong>"+ operacaoEmprestimo.getPrazoFormatado()
					+" <strong>Material:</strong> " +operacaoEmprestimo.infoMaterial);
		}
		
		String codigoAutenticacao = CirculacaoUtil.getCodigoAutenticacaoEmprestimo(emprestimosRealizados);
		
		new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
				, EnvioEmailBiblioteca.AVISO_EMPRESTIMO, mensagemUsuario, null, null, null, listaInformacoesEmail
				, null, null, codigoAutenticacao, null);
	}
	
	
	
	/** 
	 * <p> Verifica se o material pertence à biblioteca do operador para permitir empréstimo </p> 
	 * <p> <i> ( setor de informação e referência pode fazer empréstimos institucionais de materiais ) </i> </p>
	 *
	 * @throws SegurancaException
	 */
	private void verificaPermissaoEBibliotecaEmprestimo(UsuarioGeral operador, MaterialInformacional material, MovimentoRealizaEmprestimo movimento)throws NegocioException{
		
		try {
			
			if(! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){ // Administradores gerais podem operar em qualquer biblioteca
			
				UFRNUtils.checkRole(
						operador,
						material.getBiblioteca().getUnidade(),
						SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,
						SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF
				);
			}
			
		} catch (SegurancaException ex){
			throw new NegocioException ("O material de código de barras: "+ material.getCodigoBarras() +" não pertence a uma biblioteca onde o senhor(a) possua permissão para operar.");
		}
		
		
		try {
			
			//////////////////////////////////////////////////////////////////////////////
			// Verifica se o usuário está no momento operando na biblioteca do material //
			// Utilizado para os empréstimos feitos no módulo de circulação desktop,    //
			// onde o usuário opera em uma biblioteca por vez                           //   
			//////////////////////////////////////////////////////////////////////////////

			if( movimento.isOperandoBibliotecaEspecifica() ){
				if(! movimento.getIdBibliotecaOperacao().equals(material.getBiblioteca().getId())){
					throw new SegurancaException();
				}
			}
			
		} catch (SegurancaException ex){
			throw new NegocioException ("O material de código de barras: "+ material.getCodigoBarras() +" não pertence a biblioteca onde o senhor(a) está operando no momento.");
		}
		
	}
	
	
	
	
	
	/**
	 * <p>Verifica se a biblioteca realiza de determinado tipo de empréstimo para o tipo do usuário que está tentando emprestar os materiais.</p>
	 * 
	 * <p>Caso não realize, verifica se ele realiza emprétimos para outros tipos de usuários e o aluno pertence a esses outros 
	 * grupos, se pertencer também pode realizar empréstimos.</p>
	 * 
	 * @param mov
	 * @param dao
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	private void verificaOperacoesBiblioteca(MovimentoRealizaEmprestimo mov, ConsultasEmprestimoDao dao, BibliotecaDao bibliotecaDao, Biblioteca biblioteca, TipoEmprestimo tipoEmprestimo) throws DAOException, NegocioException {
		
		if(biblioteca == null)
			throw new NegocioException ("Não foi possível encontrar a biblioteca onde o usuário tem permissão de circulação.  Provavelmente sua permissão foi dada para uma unidade diferente da unidade da sua biblioteca.");
		
		if(! mov.getUsuarioBiblioteca().getVinculo().isVinculoAluno() && ! mov.getUsuarioBiblioteca().getVinculo().isVinculoBiblioteca()) // Não precisa verificar
			return;
		
		
		/**
		 * Verifica se o empréstimos entre bibliotecas está ativo para a biblioteca do materail
		 */
		
		if(mov.getUsuarioBiblioteca().getVinculo().isVinculoBiblioteca()){
			
			Integer idUnidadeSeEstaEmprestandoOMaterial  = bibliotecaDao.findIdUnidadeByBiblioteca(mov.getUsuarioBiblioteca().getBiblioteca());
			
			if( idUnidadeSeEstaEmprestandoOMaterial != null){ // está se emprétando para uma biblioteca interna
				
				boolean bibliotecaEmprestaInstitucionalInterno = bibliotecaDao.isServicoEmpretimosAtivo(biblioteca.getId(), null, "emprestimoInstitucionalInterno"); 
				
				if(! bibliotecaEmprestaInstitucionalInterno){
					throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos institucionais para outras bibliotecas internas do sistema. ");
				}
			}
			
			if( idUnidadeSeEstaEmprestandoOMaterial == null){ // está se emprétando para uma biblioteca externa
				boolean bibliotecaEmprestaInstitucionalExterno = bibliotecaDao.isServicoEmpretimosAtivo(biblioteca.getId(), null, "emprestimoInstitucionalExterno");
				
				if(! bibliotecaEmprestaInstitucionalExterno){
					throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos institucionais para bibliotecas externas ");
				}
			}
			
			return; // NÃO PRECISA VERIFICAR AS OUTRAS REGRAS PARA ALUNOS !!!
		}
		
		
		
		/**
		 * Verifica que tipos de empréstimos estão ativos para alunos
		 */
		
		if(biblioteca.isBibliotecaCentral()) // A regra de emprestimos fazem sentido se for a central, pois não está ligada a nenhum centro.
			return;
		
		ObtemVinculoUsuarioBibliotecaStrategy estrategiaUtilizada = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo();
		
		
		
		List<Character> niveisInfantil = Arrays.asList(estrategiaUtilizada.getNiveisAlunosInfantilBiblioteca());
		List<Character> niveisMedioTecnico = Arrays.asList(estrategiaUtilizada.getNiveisAlunosMedioTecnicoBiblioteca());
		List<Character> niveisGraduacao = Arrays.asList(estrategiaUtilizada.getNiveisAlunosGraduacaoBiblioteca());
		List<Character> niveisPos = Arrays.asList(estrategiaUtilizada.getNiveisAlunosPosGraduacaoBiblioteca());
		
		List<Character> niveisNaoPos = new ArrayList<Character>();
		niveisNaoPos.addAll(niveisInfantil);
		niveisNaoPos.addAll(niveisMedioTecnico);
		niveisNaoPos.addAll(niveisGraduacao);
		
		List<Character> niveisNaoGraduacao = new ArrayList<Character>();
		niveisNaoGraduacao.addAll(niveisInfantil);
		niveisNaoGraduacao.addAll(niveisMedioTecnico);
		
		List<Character> niveisNaoTecnico = new ArrayList<Character>();
		niveisNaoTecnico.addAll(niveisInfantil);
		niveisNaoTecnico.addAll(niveisGraduacao);
		
		List<Character> niveisNaoInfantil = new ArrayList<Character>();
		niveisNaoInfantil.addAll(niveisMedioTecnico);
		niveisNaoInfantil.addAll(niveisGraduacao);
		
		boolean bibliotecaEmprestaAlunosGraduacaoMesmoCentro = bibliotecaDao.isServicoEmpretimosAtivo(biblioteca.getId(), tipoEmprestimo.getId(), "emprestaAlunosGraduacaoMesmoCentro"); 
		boolean bibliotecaEmprestaAlunosGraduacaoOutroCentro = bibliotecaDao.isServicoEmpretimosAtivo(biblioteca.getId(), tipoEmprestimo.getId(), "emprestaAlunosGraduacaoOutroCentro"); 
		boolean bibliotecaEmprestaAlunosPosMesmoCentro = bibliotecaDao.isServicoEmpretimosAtivo(biblioteca.getId(), tipoEmprestimo.getId(), "emprestaAlunosPosMesmoCentro"); 
		boolean bibliotecaEmprestaAlunosPosOutroCentro = bibliotecaDao.isServicoEmpretimosAtivo(biblioteca.getId(), tipoEmprestimo.getId(), "emprestaAlunosPosOutroCentro"); 
		
		Integer idDiscente = mov.getUsuarioBiblioteca().getIdentificacaoVinculo();
		
		Discente d = null;
		
		if (idDiscente != null)
			d = dao.findInformacoesDiscenteVerificarOperacoes(idDiscente);
		
		
		
		if (d != null){
			
			boolean mesmoCentro = verificaAlunoMesmoCentroBiblioteca(dao, biblioteca, d);
			
			boolean outroCentro = ! mesmoCentro;  // para facilitar o entendimento do código
			
			switch (mov.getUsuarioBiblioteca().getVinculo()){
				
				/* 
				 * Caso o empréstimos não seja permitido tem que verificar se pode com o vínculo de gradução ou pós, se tiver. 
				 * Apesar de não usar os outros vínculos para realizar empréstimo.
				 */	
				case ALUNO_INFANTIL:
					
					if (mesmoCentro && ! bibliotecaEmprestaAlunosGraduacaoMesmoCentro){
						
	
						boolean possuiOutrosVinculoAlunoGraduacaoOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisNaoInfantil);
						boolean possuiOutrosVinculoAlunoPosOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisPos);
						
						if( ! possuiOutrosVinculoAlunoGraduacaoOutroCentro && ! possuiOutrosVinculoAlunoPosOutroCentro )
							throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de infantil/técnico/médio/graduação do mesmo centro e o usuário não possui outros vínculos em outros centros. ");
						else{ // se possuiOutrosVinculoAlunoOutroCentro vai poder emprestar se a biblioteca fizer empréstimos para aluno de graduacao mesmo centro
							
							boolean podeFazerEmpretimo = false;
							
							if( possuiOutrosVinculoAlunoGraduacaoOutroCentro && bibliotecaEmprestaAlunosGraduacaoOutroCentro )
								podeFazerEmpretimo = true;
							
							if( possuiOutrosVinculoAlunoPosOutroCentro &&  bibliotecaEmprestaAlunosPosOutroCentro ) 
								podeFazerEmpretimo = true;
							
							if(! podeFazerEmpretimo)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos infantil do mesmo centro. O usuário possui vínculo em outros centros, mas a biblioteca também não realiza empréstimos para alunos de graduação/pós-gradução de outros centros.");
						}
						
					}else{ 
						if (outroCentro && ! bibliotecaEmprestaAlunosGraduacaoOutroCentro){
							
							boolean possuiOutrosVinculoAlunoGraduacaoMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisNaoInfantil);
							boolean possuiOutrosVinculoAlunoPosMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisPos);
							
							if( ! possuiOutrosVinculoAlunoGraduacaoMesmoCentro && ! possuiOutrosVinculoAlunoPosMesmoCentro)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de infantil de outros centros e o usuário não possui outros vínculos de graduação/pós-gradução no mesmo centro.");
							
							else{ // se possuiOutrosVinculoAlunoMesmoCentro vai poder emprestar se a biblioteca fizer empréstimos para aluno de graduacao mesmo centro
								
								boolean podeFazerEmpretimo = false;
								
								if( possuiOutrosVinculoAlunoGraduacaoMesmoCentro && bibliotecaEmprestaAlunosGraduacaoMesmoCentro )
									podeFazerEmpretimo = true;
								
								if( possuiOutrosVinculoAlunoPosMesmoCentro &&  bibliotecaEmprestaAlunosPosMesmoCentro ) 
									podeFazerEmpretimo = true;
								
								if(! podeFazerEmpretimo)
									throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos infantil de outro centro. O usuário possui outros vínculos no mesmo centro, mas a biblioteca também não realiza empréstimos para alunos de graduação/pós-gradução do mesmo centro.");
							}
							
						}
					}
			
			
				/* 
				 * Caso o empréstimos não seja permitido tem que verificar se pode com o vínculo de gradução ou pós, se tiver. 
				 * Apesar de não usar os outros vínculos para realizar empréstimo.
				 */	
				case ALUNO_TECNICO_MEDIO:
					
					if (mesmoCentro && ! bibliotecaEmprestaAlunosGraduacaoMesmoCentro){
						
						boolean possuiOutrosVinculoAlunoGraduacaoOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisNaoTecnico);
						boolean possuiOutrosVinculoAlunoPosOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisPos);
						
						if( ! possuiOutrosVinculoAlunoGraduacaoOutroCentro && ! possuiOutrosVinculoAlunoPosOutroCentro )
							throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de técnico/médio/graduação do mesmo centro e o usuário não possui outros vínculos em outros centros. ");
						else{ // se possuiOutrosVinculoAlunoOutroCentro vai poder emprestar se a biblioteca fizer empréstimos para aluno de graduacao mesmo centro
							
							boolean podeFazerEmpretimo = false;
							
							if( possuiOutrosVinculoAlunoGraduacaoOutroCentro && bibliotecaEmprestaAlunosGraduacaoOutroCentro )
								podeFazerEmpretimo = true;
							
							if( possuiOutrosVinculoAlunoPosOutroCentro &&  bibliotecaEmprestaAlunosPosOutroCentro ) 
								podeFazerEmpretimo = true;
							
							if(! podeFazerEmpretimo)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de técnicos do mesmo centro. O usuário possui vínculo em outros centros, mas a biblioteca também não realiza empréstimos para alunos de graduação/pós-gradução de outros centros.");
						}
						
					}else{ 
						if (outroCentro && ! bibliotecaEmprestaAlunosGraduacaoOutroCentro){
							
							boolean possuiOutrosVinculoAlunoGraduacaoMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisNaoTecnico);
							boolean possuiOutrosVinculoAlunoPosMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisPos);
							
							if( ! possuiOutrosVinculoAlunoGraduacaoMesmoCentro && ! possuiOutrosVinculoAlunoPosMesmoCentro)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de técnico/médio/graduação de outros centros e o usuário não possui outros vínculos de graduação/pós-gradução no mesmo centro.");
							
							else{ // se possuiOutrosVinculoAlunoMesmoCentro vai poder emprestar se a biblioteca fizer empréstimos para aluno de graduacao mesmo centro
								
								boolean podeFazerEmpretimo = false;
								
								if( possuiOutrosVinculoAlunoGraduacaoMesmoCentro && bibliotecaEmprestaAlunosGraduacaoMesmoCentro )
									podeFazerEmpretimo = true;
								
								if( possuiOutrosVinculoAlunoPosMesmoCentro &&  bibliotecaEmprestaAlunosPosMesmoCentro ) 
									podeFazerEmpretimo = true;
								
								if(! podeFazerEmpretimo)
									throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos técnicos/médios de outro centro. O usuário possui outros vínculos no mesmo centro, mas a biblioteca também não realiza empréstimos para alunos de graduação/pós-gradução do mesmo centro.");
							}
							
						}
					}
					
					
				/* Caso o empréstimos não seja permitido tem que verificar se pode com o vínculo de técnico ou pós, se tiver. 
				 * Apesar de não usar os outros vínculos para realizar empréstimo.
				 */	
				case ALUNO_GRADUACAO: 
					if (mesmoCentro && ! bibliotecaEmprestaAlunosGraduacaoMesmoCentro){
						
						boolean possuiOutrosVinculoAlunoGraduacaoOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisNaoGraduacao);
						boolean possuiOutrosVinculoAlunoPosOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisPos);
						
						if( ! possuiOutrosVinculoAlunoGraduacaoOutroCentro && ! possuiOutrosVinculoAlunoPosOutroCentro )
							throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de graduação do mesmo centro e o usuário não possui outros vínculos em outros centros.");
						else{ // se possuiOutrosVinculoAlunoOutroCentro vai poder emprestar se a biblioteca fizer empréstimos para aluno de graduacao mesmo centro
							
							boolean podeFazerEmpretimo = false;
							
							if( possuiOutrosVinculoAlunoGraduacaoOutroCentro && bibliotecaEmprestaAlunosGraduacaoOutroCentro )
								podeFazerEmpretimo = true;
							
							if( possuiOutrosVinculoAlunoPosOutroCentro &&  bibliotecaEmprestaAlunosPosOutroCentro ) 
								podeFazerEmpretimo = true;
							
							if(! podeFazerEmpretimo)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de graduação do mesmo centro. O usuário possui outros vínculos em outros centros, mas a biblioteca também não realiza empréstimos para alunos de técnico/médio/pós-graduação de outro centro.");
						}
					}else{ 
						if (outroCentro && ! bibliotecaEmprestaAlunosGraduacaoOutroCentro){
							
							boolean possuiOutrosVinculoAlunoGraduacaoMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisNaoGraduacao);
							boolean possuiOutrosVinculoAlunoPosMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisPos);
							
							if( ! possuiOutrosVinculoAlunoGraduacaoMesmoCentro && ! possuiOutrosVinculoAlunoPosMesmoCentro)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de graduação de outros centros e o usuário não possui outro vínculo de técnico/médio/pós-gradução no mesmo centro.");
							
							else{ // se possuiOutrosVinculoAlunoMesmoCentro vai poder emprestar se a biblioteca fizer empréstimos para aluno de graduacao mesmo centro
								
								boolean podeFazerEmpretimo = false;
								
								if( possuiOutrosVinculoAlunoGraduacaoMesmoCentro && bibliotecaEmprestaAlunosGraduacaoMesmoCentro )
									podeFazerEmpretimo = true;
								
								if( possuiOutrosVinculoAlunoPosMesmoCentro &&  bibliotecaEmprestaAlunosPosMesmoCentro ) 
									podeFazerEmpretimo = true;
								
								if(! podeFazerEmpretimo)
									throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de graduação de outro centro. O usuário possui vínculo no mesmo centro, mas a biblioteca também não realiza empréstimos para alunos de técnico/médio/pós-graduação do mesmo centro.");
							}
						}
					}
				break;
				
				/* Caso o empréstimos não seja permitido com o vínculo que ele irá usar para realizar os empréstimos tem que verificar se pode 
				 * com algum vínculo de infantil, técnico ou de gradução, se tiver. 
				 * A pesar de não usar esses outro vínculo para realizar empréstimo.
				 */	
				case ALUNO_POS_GRADUCACAO:
					if (mesmoCentro && !  bibliotecaEmprestaAlunosPosMesmoCentro){
						
						boolean possuiOutrosVinculoAlunoOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisNaoPos);
						
						if( ! possuiOutrosVinculoAlunoOutroCentro)
							throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de pós-graduação do mesmo centro e o usuário não possui vínculos de infantil/técnico/médio/graduação em outros centros");
						else{
							if(possuiOutrosVinculoAlunoOutroCentro && ! bibliotecaEmprestaAlunosGraduacaoOutroCentro){
								throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de pós-graduação do mesmo centro. O usuário possui outros vínculos em outros centros, mas a biblioteca também não realiza empréstimos para alunos de infantil/técnico/médio/graduação do outro centro.");
							}
						}
					}else{ 
						if (outroCentro && !   bibliotecaEmprestaAlunosPosOutroCentro){
							
							boolean possuiOutrosVinculoAlunoMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisNaoPos) ;
							
							if( ! possuiOutrosVinculoAlunoMesmoCentro)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de pós-graduação de outros centros e o usuário não possui outro vínculo de infantil/técnico/médio/graduação no mesmo centro.");
							else{
								if(possuiOutrosVinculoAlunoMesmoCentro && ! bibliotecaEmprestaAlunosGraduacaoMesmoCentro){
									throw new NegocioException ("A "+biblioteca.getDescricao()+" não realiza empréstimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de pós-graduação de outro centro. O usuário possui outros vínculos no mesmo centro, mas a biblioteca também não realiza empréstimos para alunos infantil/técnico/médio/graduação do mesmo centro.");
								}
							}
						}
					}
				break;
			default:
				break;
			}
		} else
			throw new NegocioException ("Apesar de o usuário ser do tipo \""+mov.getUsuarioBiblioteca().getVinculo()+"\", não foi encontrado um discente para ele.");
	}


	/**
	 * <p>Método que verifica se o aluno está nomesmo centro da biblioteca ou não para verificar se ele pode realizar empréstimos.</p>
	 * 
	 * <p><strong>Existem 2 casos.</strong>  As bibliotecas setoriais normais, os cursos dos alunos ficam ligadas aos centros.  
	 * Nas biblioteca setoriais de unidades especializadas, os cursos dos alunos ficam ligadas a própria unidade especializada.</p>
	 * 
	 * <p>A lógica é a seguinte: </p>
	 * 
	 * <p>Caso a unidade responsável da biblioteca seja uma unidade especializada. Então verifica se o a unidade do curso do aluno é 
	 * igual a responsável da unidade da biblioteca.  Por exemplo: "Biblioteca de Música" fica dentro da unidades especializada 
	 * "Escola de Música".   Os discentes estão vinculados a cursos da "Escola de Música".</p>
	 * 
	 * <p>Casa a unidade responsável da biblioteca NÃO seja uma unidade especializada. Então verifica se a unidade gestora da unidade 
	 * do curso do aluno(que é um centro) é a unidade gestora da unidade da biblioteca (que também é um centro). 
	 *  Por exemplo: "Biblioteca de Eng. Quimica" está ligada ao "DEPARTAMENTO DE ENGENHARIA QUIMICA" cuja gestora é o "CENTRO DE TECNOLOGIA".</p>
	 * 
	 * @param dao
	 * @param biblioteca
	 * @param d
	 * @return
	 * @throws DAOException
	 */
	private boolean verificaAlunoMesmoCentroBiblioteca(ConsultasEmprestimoDao dao, Biblioteca biblioteca, Discente d) throws DAOException {
		
		boolean mesmoCentro = false;
		
		Biblioteca bibliotecaVeriricacao = dao.findInformacoesBibliotecaVerificarOperacoes(biblioteca.getId());
		
		if( d.getCurso() != null &&  d.getCurso().getUnidade() != null &&  d.getCurso().getUnidade().getGestora() != null 
				&& bibliotecaVeriricacao.getUnidade() != null 
				&& bibliotecaVeriricacao.getUnidade().getGestora() != null 
				&& bibliotecaVeriricacao.getUnidade().getUnidadeResponsavel() != null){
			
			if(bibliotecaVeriricacao.getUnidade().getUnidadeResponsavel().isUnidadeAcademicaEspecializada()){ // Se a unidade responsável da unidade da biblioteca é especializada
				// O curso tem que está ligado diretamente a essa unidade //
				mesmoCentro = d.getCurso().getUnidade().equals(bibliotecaVeriricacao.getUnidade().getUnidadeResponsavel());
			}else{
				// Se não é especializada, verifica se o centro onde o curso está é o mesmo centro da unidade da biblioteca
				mesmoCentro = d.getCurso().getUnidade().getGestora().equals(bibliotecaVeriricacao.getUnidade().getGestora());
			}
		}	
		
		dao.detach(bibliotecaVeriricacao);
		
		return mesmoCentro;
	}

	
	
	/**
	 * <p>Método criado para os casos em que o aluno é aluno de pós ou graduação de um <strong>centro diferente</strong> e a biblioteca <strong>não empresta</strong> 
	 * para alunos de outros centros, mas o aluno possui um vínculo menor, não utilizado para os empréstimos que está no mesmo centro da biblioteca. </p>
	 *  
	 * <p>Neste caso o sistema vai precisar busca outros vínculo menores do usuário e verificar se algum dos vínculo menores estão 
	 * no mesmo centro, se sim, o empréstimo deve ser permitido</p> 
	 * @throws DAOException 
	 *  
	 */
	private boolean possuiOutrosVinculoAlunoMesmoCentro(MovimentoRealizaEmprestimo mov, Biblioteca biblioteca, List<Character> niveisEnsino) throws DAOException{
		
		UsuarioBibliotecaDao  dao = null;
		
		try{
			
			dao = getDAO(UsuarioBibliotecaDao.class, mov);
			
			List<Discente> discentes = dao.findDiscentesBibliotecaByPessoaAndNivel(mov.getUsuarioBiblioteca().getPessoa().getId(), niveisEnsino);
			
			for (Discente discente : discentes) {
				
				if( discente.getCurso() != null &&  discente.getCurso().getUnidade() != null &&  discente.getCurso().getUnidade().getGestora() != null 
						&& biblioteca.getUnidade() != null && biblioteca.getUnidade().getGestora() != null)
					if(discente.getCurso().getUnidade().getGestora().equals(biblioteca.getUnidade().getGestora()))
						return true;
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return false;
	}
	
	
	/**
	 * <p>Método criado para os casos em que o aluno é aluno de pós ou graduação do <strong>mesmo centro </strong> e a biblioteca <strong>não empresta</strong> 
	 * para alunos de mesmo centros, mas o aluno possui um vínculo menor, não utilizado para os empréstimos que está em outro centro da biblioteca. </p>
	 *  
	 * <p>Neste caso o sistema vai precisar busca outros vínculo menores do usuário e verificar se algum dos vínculo menores estão 
	 * em outros centros, se sim, o empréstimo deve ser permitido</p> 
	 * @throws DAOException 
	 *  
	 */
	private boolean possuiOutrosVinculoAlunoOutroCentro(MovimentoRealizaEmprestimo mov, Biblioteca biblioteca, List<Character> niveisEnsino) throws DAOException{
		
		UsuarioBibliotecaDao  dao = null;
		
		try{
			
			dao = getDAO(UsuarioBibliotecaDao.class, mov);
			
			List<Discente> discentes = dao.findDiscentesBibliotecaByPessoaAndNivel(mov.getUsuarioBiblioteca().getPessoa().getId(), niveisEnsino);
			
			for (Discente discente : discentes) {
				
				if( discente.getCurso() != null &&  discente.getCurso().getUnidade() != null &&  discente.getCurso().getUnidade().getGestora() != null 
						&& biblioteca.getUnidade() != null && biblioteca.getUnidade().getGestora() != null)
					if(! discente.getCurso().getUnidade().getGestora().equals(biblioteca.getUnidade().getGestora()))
						return true;
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return false;
	}
	
	
	
	

	/**
	 * Para empréstimos entre bibliotecas, um material deve possuir outro com o mesmo título catalográfico
	 * com situação DISPONÍVEL e status CIRCULA para que esse empréstimo não faça com que a biblioteca
	 * que possui o material não fique sem materiais daquele título.
	 * 
	 * @param material
	 * @param dao
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	private void verificaMaterialPossuiOutroDisponivel(MaterialInformacional material, MovimentoRealizaEmprestimo personalMov) throws DAOException, NegocioException {
		
		MaterialInformacionalDao dao = null;
		try {
			dao = getDAO(MaterialInformacionalDao.class, personalMov);
			
			TituloCatalografico titulo = null;
			
			if (material instanceof Exemplar)
				titulo = ((Exemplar) material).getTituloCatalografico();
			else
				titulo = ((Fasciculo) material).getAssinatura().getTituloCatalografico();
			
			Long quantidadeMateriaisAtivosPodemSerEmprestados = dao.countMateriaisAtivosQuePodemSerEmprestadosByTitulo(titulo.getId());
			
			
			// Se não há mais que dois materiais ativos e circulantes para aquele título, erro.
			if (quantidadeMateriaisAtivosPodemSerEmprestados < 2)
				throw new NegocioException ("O material de código de barras " + material.getCodigoBarras() + " não pode ser emprestado porque seu Título não possui outro material disponível para circulação.");
		
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	
	
	/**
	 *  <p>Método que verifica se o usuário está tentando empretar um material para a biblioteca que já o possui, não caso de empréstimos institucionais </p>
	 *  <p>Um empréstimo só faz sentido se for para uma biblioteca diferente da biblioteca que possui o material </p>
	 *
	 * @param material
	 * @param usuarioBiblioteca
	 * @param personalMov
	 * @throws NegocioException
	 */
	private void vericaEmprestimoBibliotecaDoMaterial(MaterialInformacional material, UsuarioBiblioteca usuarioBiblioteca, MovimentoRealizaEmprestimo personalMov) throws NegocioException{
		
		if(usuarioBiblioteca.getBiblioteca() != null){
			if( usuarioBiblioteca.getBiblioteca().getId() == material.getBiblioteca().getId()){
				throw new NegocioException ("O material de código de barras " + material.getCodigoBarras() + " não pode ser emprestado para a biblioteca onde ele está localizado.");
			}
		}
		
	}
	
	
	
	
	
	/**
	 * Implementa a regra de negócio RN02 : um material não poderá ser emprestado para um mesmo usuário em um período inferior a 24 horas.
	 * 
	 * @param material
	 * @param usuarioBiblioteca
	 * @param dao
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void verificaTempoLimiteEntreEmprestimos(MaterialInformacional material, UsuarioBiblioteca usuarioBiblioteca, EmprestimoDao dao) throws DAOException, NegocioException {

		int prazoMinimoEntreEmprestimosEmHoras  = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.PRAZO_MINIMO_ENTRE_EMPRESTIMOS);
		
		if(prazoMinimoEntreEmprestimosEmHoras > 0){ // Se essa regra está habilitada
		
			Date ultimaDataDevolucao = dao.findDataDevolucaoUltimoEmprestimoAtivoByUsuarioMaterial(usuarioBiblioteca, material);
			
			// Se o usuário já emprestou esse material alguma vez, verifica se a data de devolução do empréstimo é menor que o prazo mínimo
			if (ultimaDataDevolucao != null) {
	
				Date agora = new Date();
				int horas = CalendarUtils.calculaQuantidadeHorasEntreDatas(ultimaDataDevolucao, agora);
	
				// Se for maior ou igual ao prazo mínimo pode emprestar //
				if (horas < prazoMinimoEntreEmprestimosEmHoras)
					throw new NegocioException("Um material não pode ser emprestado para um mesmo usuário em um período inferior a "+prazoMinimoEntreEmprestimosEmHoras+" hora(s)");
			}
		}
	}

	

	/**
	 * Verifica se o usuário está tentando tomar emprestados dois ou mais materiais de um mesmo título.
	 * Pode, se materiais forem de volumes diferentes.
	 * 
	 * @param exemplarQueVaiSerEmprestado
	 * @param emprestimosAtivos
	 * @param emprestimos
	 * @throws NegocioException
	 * @throws DAOException 
	 */
	private void verificaExemplaresDeUmMesmoTitulo(Exemplar exemplarQueVaiSerEmprestado, List<Emprestimo> emprestimosAtivos
			, List<Emprestimo> emprestimos, EmprestimoDao dao)throws NegocioException, DAOException {
		
		List<MaterialInformacional> exemplaresEmprestados = montaMateriaisEmprestados(emprestimosAtivos, emprestimos);

		List<Exemplar> auxExemplares = new ArrayList<Exemplar>();
		
		for (MaterialInformacional m : exemplaresEmprestados){
			m = dao.refresh(m);
			if (m instanceof Exemplar) {
				auxExemplares.add((Exemplar) m);
			}
		}
		
		// Percorre todos os materiais que já foram emprestados e verifica se tem algum que é
		// igual (mesmo título e volume) ao que vai ser emprestado agora.
		// Essa regra não se aplica se os materiais forem anexos, pois o usuário tem
		// o direito de levar todos os anexos de um material
		for (Exemplar exemplar : auxExemplares) {
			// OBS.: O método equals não está funcionando aqui pois o hibernate
			// esta trazendo objetos proxy e não os reais
			if (exemplarQueVaiSerEmprestado.getTituloCatalografico().getId() == exemplar.getTituloCatalografico().getId()
					&&  (  ( exemplarQueVaiSerEmprestado.getNumeroVolume() == null && exemplar.getNumeroVolume() == null ) 
							|| ( exemplarQueVaiSerEmprestado.getNumeroVolume() != null && exemplar.getNumeroVolume() != null && exemplarQueVaiSerEmprestado.getNumeroVolume().equals(exemplar.getNumeroVolume() ) ) 
						)
					&& !possuiRelacionamentoDeAnexoEntreExemplaresEmprestados(exemplarQueVaiSerEmprestado, auxExemplares)) {

				throw new NegocioException(" O empréstimo não pôde ser realizado pois um usuário não pode tomar emprestado dois exemplares iguais."
						+ " Os exemplares: " + exemplarQueVaiSerEmprestado.getCodigoBarras() + " e " + exemplar.getCodigoBarras()
						+ " são do mesmo título e possuem o mesmo volume.");
			}
		}
	}

	/**
	 * Verifica se o material é anexo de algum outro ou vice-versa. Chamado pelo método <code>verificaItensDeUmMesmoTitulo</code>
	 * 
	 * @param itemQueVaiSerEmprestado
	 * @param exemplaresEmprestados
	 * @return
	 */
	private boolean possuiRelacionamentoDeAnexoEntreExemplaresEmprestados(Exemplar itemQueVaiSerEmprestado, List<Exemplar> exemplaresEmprestados) {


			for (Exemplar e : exemplaresEmprestados)
				if (itemQueVaiSerEmprestado.isAnexo() || e.isAnexo())
					if (itemQueVaiSerEmprestado.getTituloCatalografico().getId() == e.getTituloCatalografico().getId())
						return true;
					
			return false;
	}

	/**
	 * Verifica se o material está disponível para empréstimo.
	 * 
	 * @param material
	 * @param dao
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void verificaMaterialEstaDisponivel(MaterialInformacional material, EmprestimoDao dao) throws DAOException, NegocioException {
		if (!material.podeSerEmprestado())
			throw new NegocioException("O material com o código de barras " + material.getCodigoBarras() + " não está disponível para empréstimo ou seu status não permite que ele seja emprestado.");
		
		Emprestimo e = null;

		e = dao.findEmAbertoByCodigoBarras(material.getCodigoBarras());

		if (e != null)
			throw new NegocioException("Ocorreu uma inconsistência na base. Apesar do material com o código de barras " + material.getCodigoBarras()
					+ " estar com situação que permite o empréstimo, existe um empréstimo ativo desse material no sistema - Contate o Suporte ");
	}

	
	
	
	/**
	 * <p>Verifica as quantidades por regra de empréstimo (regular, especial, multimeio, etc)
	 * para o usuário, a partir dos empréstimos ativos que ele possui.</p>
	 * 
	 * <p>Esse método leva em cosideração o cálculo dos anexos. Ou seja, o empréstimo de 1 material + N anexos do material = 1 material para a 
	 * contagem da quantidade levada pelo usuário.. </p>
	 * 
	 * @param materialEstaSendoEmprestado -  O material que está sendo emprestado
	 * @param politicaEmprestimoUsada - A política que está sendo utilizado para o material que está sendo emprestado
	 * @param emprestimosAtivos - Os empréstimos ativos do usuário salvo no banco.
	 * @param emprestimosSendoRealizados - Os empréstios que estão sendo realizados no momento.
	 * @param dao - O dao para as consultas.
	 * 
	 * @throws DAOException 
	 */
	private void verificaQuantidadePorRegraDeEmprestimo( MaterialInformacional materialEstaSendoEmprestado, PoliticaEmprestimo politicaEmprestimoUsada,
			List<Emprestimo> emprestimosAtivos, List<Emprestimo> emprestimosSendoRealizados, EmprestimoDao dao) throws NegocioException, DAOException {
		
		
		// Politica personalizável não precisa verificar porque não possui quantide nem prazo definidos, o usuário leva quantos o bibliotecário permitir //
		
		if(! politicaEmprestimoUsada.isPersonalizavel()){ 
		
			try{
				
				/*
				 * Guarda os ids dos materiais que o usuário já possui para na mesma política de empréstimos.
				 * 
				 * Mesma política não necessariamente significa mesmo objeto, pode ser uma política diferente para os mesmo dados 
				 * (biblioteca, vinculo do usuário, tipo de empréstimo, status do material e tipos do material)
				 */
				List <Integer> idsMateriaisEmprestadosMesmaPolitica = new ArrayList<Integer>();        
				
				
				/* Nesse lista guarda os materiais extritamente já emprestados para o usuário, ou seja, não possui o material atualmente sendo emprestado.*/
				//List<Integer> idsMateriaisJaEmprestadosMesmaPolitica = new ArrayList<Integer>(); 
				
				
				/*
				 * Guarda os ids dos materiais emprestado para o usuário descontando os anexo caso o principal já esteja emprestado. 
				 * Se não tiver, conta todos os anexos como materiais normais.
				 */
				Set<Integer>  idMateriaisDescontandoAnexosEmprestadosParaUsuario = new HashSet<Integer>(); // 
			
				
				
				
				// Adiciona os materiais já emprestados ao usuário //				
				for (Emprestimo e : emprestimosAtivos){
					if(e.getPoliticaEmprestimo() != null && e.getPoliticaEmprestimo().equalByDadosPolitica(politicaEmprestimoUsada))  // Conta os materias da mesma política
						idsMateriaisEmprestadosMesmaPolitica.add(e.getMaterial().getId());
				}
				
				// Adiciona os outros emprestimos que estão sendo feitos agora //
				for (Emprestimo e : emprestimosSendoRealizados){
					if(e.getPoliticaEmprestimo() != null  && e.getPoliticaEmprestimo().equalByDadosPolitica(politicaEmprestimoUsada))
						idsMateriaisEmprestadosMesmaPolitica.add(e.getMaterial().getId());
				}
			
				
				// Adiciona o atualmente emprestado para os calculos //
				idsMateriaisEmprestadosMesmaPolitica.add(materialEstaSendoEmprestado.getId());
				
				
				if(idsMateriaisEmprestadosMesmaPolitica.size() > 1 ){ // só precisa conta se tiver outros materiais emprestados
					
					// se os materiais foram anexos, contém os ids dos principais deles, se não contém os próprios ids 
					idMateriaisDescontandoAnexosEmprestadosParaUsuario = dao.getIdsMateriaisDescontandoAnexosQuandoPrincipalEstaEmprestado(idsMateriaisEmprestadosMesmaPolitica);
				}
				

				
				
				if (politicaEmprestimoUsada.getQuantidadeMateriais() != null
						&& (idMateriaisDescontandoAnexosEmprestadosParaUsuario.size()) > politicaEmprestimoUsada.getQuantidadeMateriais() ){
					throw new NegocioException("A quantidade máxima de empréstimos do tipo \"<strong>"+politicaEmprestimoUsada.getTipoEmprestimo().getDescricao()+"</strong>\" para material do tipo \"<strong>" + materialEstaSendoEmprestado.getStatus().getDescricao() + "</strong>\" foi ultrapassada. "
							+"Permitido: " + politicaEmprestimoUsada.getQuantidadeMateriais() + ". Quantidade de materiais que ficariam emprestados: "+ (idMateriaisDescontandoAnexosEmprestadosParaUsuario.size() ) + ".");
				}
			
			}finally{
				
			}
		
		} // ! politicaEmprestimo.isPersonalizavel()
		
	}

	
	
	
	
	/**
	 * Método que verifica se o material tem alguma nota de circulação.
	 * Caso seja uma nota bloqueante, lança uma exeção para não conseguir realizar o empréstimo, 
	 * caso contrário apenas adiciona a lista que será mostrada ao operador/usuário
	 * 
	 * @param daoNota
	 * @param retorno
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	private void verificaNotasDeCirculacao(NotaCirculacaoDao  daoNota, MaterialInformacional material, RetornoOperacoesCirculacaoDTO retornoProcessadorCirculacao) throws DAOException, NegocioException{
		
		List<NotaCirculacao> notas = daoNota.getNotasAtivasDoMaterial(material.getId());
		
		for (NotaCirculacao notaCirculacao : notas) {
			if(notaCirculacao.isBloquearMaterial()){  // O material está bloqueado, não pode ser realizado o empréstimo 
				throw new NegocioException("O material de código de barras " + material.getCodigoBarras() + " está bloqueado e não pode ser emprestado. O motivo: " + notaCirculacao.getNota());
			}else{
				if(notaCirculacao.isMostrarEmprestimo()){
					retornoProcessadorCirculacao.addMensagemAosUsuarios("Nota de Circulação do material "+material.getCodigoBarras()+" : "+notaCirculacao.getNota());
					
					//Desativa a nota para não ser mais mostrada
					daoNota.updateFields(NotaCirculacao.class, notaCirculacao.getId(), new String []{"mostrarEmprestimo"}, new Object [] {false});
				}
			}
		}  
	}
	
	
	/**
	 *  <p>Verifica se o usuário que está realizando o empréstimo está entre os usuários que possuem reserva "EM ESPERA"
	 *  para o título, são os únicos usuários que podem emprestar materiais desse título. </p> 
	 *  
	 * @throws NegocioException 
 	 *
	 */
	private ReservaMaterialBiblioteca getReservaDoUsuario(List<ReservaMaterialBiblioteca> reservasEmEspera, UsuarioBiblioteca usuarioEmprestimo){
		
		// liner search
		for(ReservaMaterialBiblioteca reservaMaterialBiblioteca : reservasEmEspera) {
			if( reservaMaterialBiblioteca.getUsuarioReserva().getId() == usuarioEmprestimo.getId()){
				return reservaMaterialBiblioteca;
			}
		}
		
		return null;
		
	}
	
	
	
	/**
	 * Junta todos os materiais que já foram emprestados e estão sendo emprestados agora em
	 * um único array para poder calcular o número de empréstimos que o usuário pode fazer.
	 * 
	 * @param emprestimosAtivos
	 * @param emprestimos
	 * @return
	 */
	private List <MaterialInformacional> montaMateriaisEmprestados (List<Emprestimo> emprestimosAtivos, List<Emprestimo> emprestimos) {
		List<MaterialInformacional> materiaisEmprestados = new ArrayList<MaterialInformacional>();

		// Joga tudo em um array só //
		for (Emprestimo e : emprestimosAtivos)
			materiaisEmprestados.add(e.getMaterial());

		for (Emprestimo e : emprestimos)
			materiaisEmprestados.add(e.getMaterial());

		return materiaisEmprestados;
	}
	


	/**
	 * Ver comentário na classe pai
	 * 
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate (Movimento mov) throws NegocioException, ArqException {
		
		MovimentoRealizaEmprestimo personalMov = (MovimentoRealizaEmprestimo) mov;
		UsuarioBiblioteca usuarioBiblioteca = personalMov.getUsuarioBiblioteca();
		
		UsuarioBibliotecaDao  dao = null;
		
		try{
			
			dao = getDAO(UsuarioBibliotecaDao.class, personalMov);
			
			if (personalMov.getIdsTiposEmprestimosMateriais() == null || personalMov.getIdsTiposEmprestimosMateriais().size() == 0)
				throw new IllegalArgumentException("Não foi enviado nenhum material para ser empréstado");
	
			if (usuarioBiblioteca == null || usuarioBiblioteca.getId() <= 0)
				throw new IllegalArgumentException("Não foi passado o usuário biblioteca para realizar o empréstimo");
	
	
			// VERIFICA SE A SENHA ESTÁ CORRETA  //	
			if (!  usuarioBiblioteca.ehBiblioteca()  && (usuarioBiblioteca.getSenha() == null || !usuarioBiblioteca.getSenha().equals(personalMov.getSenhaDigitada()))){
					throw new NegocioException("Senha do Usuário Incorreta");	
			}
			
			// VERIFICA SE O VÍNCULO NÃO FOI ENCERRADO  ///
			if(usuarioBiblioteca.isQuitado())
				throw new NegocioException("O vínculo atual foi quitado, não é mais possível realizar empréstimos com ele");
			
			
			// VERIFICA SE O VÍNCULO ATRIBUIDO NO CADASTRO CONTINUA ATIVO //
			VerificaSituacaoUsuarioBibliotecaUtil.verificaVinculoUtilizadoAtivo(usuarioBiblioteca);
			
			
			//// VERIFICA SE O USUÁRIO FOI BLOQUEADO PARA EMPRÉSTIMO ////////
			if( usuarioBiblioteca.ehBiblioteca() )
				VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioBloqueado(null, usuarioBiblioteca.getBiblioteca().getId());
			else
				VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioBloqueado(usuarioBiblioteca.getPessoa().getId(), null);
			
			
			//// VERIFICA SE O USUÁRIO NÃO POSSUI CADASTRO DUPLICADO NA BIBLIOTECA ////////
			if( ! usuarioBiblioteca.getVinculo().isVinculoBiblioteca()){
				if(new Long(1).compareTo(dao.countUsuariosBibliotecaAtivoNaoQuitadoByPessoa(usuarioBiblioteca.getPessoa().getId())) < 0)
					throw new NegocioException("O usuário possui mais de um vínculo ativo na biblioteca, quite um dos vínculo para poder realizar novos empréstimos. ");
			} else{
				if (new Long(1).compareTo(dao.countUsuariosBibliotecaAtivoNaoQuitadoByPessoa(usuarioBiblioteca.getBiblioteca().getId())) < 0)
					throw new NegocioException("O usuário possui mais de um vínculo ativo na biblioteca, quite um dos vínculo para poder realizar novos empréstimos. ");
			}
					
			//// VERIFICA SE A PESSOA DO USUÁRIO BIBLIOTECA POSSUI CPF OU PASSAPORTE  ////////	
			if(! usuarioBiblioteca.getVinculo().isVinculoBiblioteca()){
				Pessoa pessoa= dao.findByPrimaryKey(usuarioBiblioteca.getPessoa().getId(), Pessoa.class, "cpf_cnpj", "tipo", "valido", "passaporte", "internacional");
				pessoa.setId(usuarioBiblioteca.getPessoa().getId());
				VerificaSituacaoUsuarioBibliotecaUtil.verificaDadosPessoaCorretosUtilizarBiblioteca(pessoa);
			}
			
		} finally {
			if(dao != null) dao.close();
		}
	}
}