/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe que cont�m as regras de neg�cio de para a realiza��o de um empr�stimo.<br>
 * 
 * 
 * @author jadson
 * @since 26/09/2008
 * @version 1.0 cria��o da classe
 * 
 */
public class ProcessadorRealizaEmprestimo extends ProcessadorCadastro {

	/**
	 * Ver coment�rio na classe pai
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
		 * Objeto que por padr�o deve ser retornado quando � realizado alguma opera��o relacionado a empr�stimos em circula��o.
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
			
			
			/// Garregas as permiss�es do operador ///
			
			operador.setPermissoes(pDao.findPermissoesByUsuario(operador));
			
			operador.setPapeis(new ArrayList <Papel>());
			
			for (Permissao p : operador.getPermissoes()){
				operador.getPapeis().add(p.getPapel());
			}
			
						
			politicaDao = getDAO(PoliticaEmprestimoDao.class, movimentoRealizaEmprestimo);
			matDao = getDAO(MaterialInformacionalDao.class, movimentoRealizaEmprestimo);
			
			// Recupera os empr�stimos ativos do usu�rio do banco //
			List <Emprestimo> emprestimosAtivos = consultaEmprestimoDao.findEmprestimosAtivosByUsuario(usuarioBiblioteca.getId());

			// Empr�stimos a se realizar.
			List <Emprestimo> emprestimos = new ArrayList<Emprestimo>();

			// As seserva que ser�o consolidades pelos empr�stimos que v�o ser reailzados
			List<ReservaMaterialBiblioteca> reservasConsolidadas = new ArrayList<ReservaMaterialBiblioteca>();
			
			
			// Valida se o usu�rio possui empr�stimos em atraso. Se tiver, j� lan�a exece��o e n�o verifica mais nada
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiEmprestimosAtrazados(emprestimosAtivos);
			
			// Valida se o usu�rio est� com puni��o na biblioteca. Se possuir, j� lan�a exece��o e n�o verifica mais nada
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoes(usuarioBiblioteca.getIdentificadorPessoa()
					, usuarioBiblioteca.getIdentificadorBiblioteca() );

			
			// ///////////////////////////////////////////////////////////////
			// Para cada um dos Materiais que vai ser emprestado, verificar
			// 
			// Se o material est� dispon�vel (SituacaoMaterialInformacional) para empr�stimo
			// Se o mesmo material n�o foi emprestado para o mesmo usu�rio em menos de 24 horas //
			// Se o status do material � "especial" ou "n�o circula" //
			// Se n�o tem mais de um material com mesmo volume de um mesmo t�tulo entre os empr�stimos //
			// Se as quantidades por regra de empr�stimo n�o estouraram. A quantidade � por biblioteca //
			// (levar em conta no c�lculo os anexos, que s�o contados apenas como 1 material) //
			// 
			// Se o material est� bloqueado ou possui alguma nota de circula��o
			// Configurar as informa��es do empr�stimo e mudar a SITUA��O do material para emprestado //
			// ////////////////////////////////////////////////////////////////////////////////////////

			materiaisAEmprestar = matDao.findMateriaisAtivosByPrimaryKeyIn(new ArrayList <Integer>(movimentoRealizaEmprestimo.getIdsTiposEmprestimosMateriais().keySet()));

			
			// IMPORTANTE: n�o esquecer de ordenar para a regra do anexo funcionar
			CirculacaoUtil.ordenaMateriaisByAnexos(materiaisAEmprestar);
			
			
			// A lista de prorroga��es criadas.
			List <ProrrogacaoEmprestimo> prorrogacoes = new ArrayList <ProrrogacaoEmprestimo> ();
			
			SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
			
			
			/* Rela��o de ordem total <ID_TITULO, QTD_MATERIAIS> que guarda a quantidade de materiais emprestados para um determinado T�tulo.
			 * 
			 * Usado na parte de reservas para somar a quantidade de materiais dispon�veis no banco com a quantidade de materiais emprestado para o usu�rio 
			 * mas que o empr�stimo n�o foi persistido.  Para veririca se a quantidade que sobre � maior que a quantidade de reservas EM ESPERA e deixar outros 
			 * usu�rios levarem os materiais dispon�veis
			 */
			Map<Integer, Integer> qtdMateriaisEmprestadosParaTitulo = new TreeMap<Integer, Integer>();
			
			
			for (MaterialInformacional material : materiaisAEmprestar){
				
				// Como est� processando v�rios empr�stimos de uma vez, vai parar no primeiro que der erro.
				// Salva a id material atual para saber em qual deu erro e poder avisar corretamente ao usu�rio.
				movimentoRealizaEmprestimo.setIdMaterialProcessando(material.getId());
				
				verificaPermissaoEBibliotecaEmprestimo(operador, material, movimentoRealizaEmprestimo); 
					
				
				// Uma refer�ncia ao tipo empr�stimo para este material.
				TipoEmprestimo tipoEmprestimo = dao.refresh(new TipoEmprestimo (movimentoRealizaEmprestimo.getIdsTiposEmprestimosMateriais().get(material.getId())));

				// Verifica se o material est� dispon�vel para empr�stimos.
				verificaMaterialEstaDisponivel(material, dao);
				
				// Descobre qual � a pol�tica de empr�stimo para este material.
				PoliticaEmprestimo politicaEmprestimo = politicaDao.findPoliticaEmpretimoAtivaASerUsuadaNoEmprestimo(
																			material.getBiblioteca(),
																			usuarioBiblioteca.getVinculo(),
																			tipoEmprestimo,
																			material.getStatus(),
																			material.getTipoMaterial() );
				
				if (politicaEmprestimo == null)
					throw new NegocioException ("N�o h� uma Pol�tica de Empr�stimo ativa para o material de c�digo de barras " + material.getCodigoBarras());

				
				if (usuarioBiblioteca.ehBiblioteca()){
					verificaMaterialPossuiOutroDisponivel(material, movimentoRealizaEmprestimo); // Se for para uma biblioteca, deve verificar se h� outro material dispon�vel.
				
					// verifica se o usu�rio est� tentando emprestar um material para a biblioteca que o pussui
					// O empr�stio s� tem l�gica se for para uma biblioteca diferente
					vericaEmprestimoBibliotecaDoMaterial(material, usuarioBiblioteca, movimentoRealizaEmprestimo); 
				}
				
				// Verifica se j� se passaram vinte e quatro horas desde o �ltimo empr�stimo deste material para este usu�rio.
				verificaTempoLimiteEntreEmprestimos(material, usuarioBiblioteca, dao);

				if (material instanceof Exemplar)
					verificaExemplaresDeUmMesmoTitulo((Exemplar) material, emprestimosAtivos, emprestimos, dao);

				// Verifica se o usu�rio n�o est� tentando emprestar mais materiais do que pode, para a pol�tica atual.
				verificaQuantidadePorRegraDeEmprestimo(material, politicaEmprestimo, emprestimosAtivos, emprestimos, dao);

				// Verifica se o servi�o de empr�stimos e renova��o est�o ativos na biblioteca
				CirculacaoUtil.verificaServicoEmprestimosEstaoAtivos(material.getId(), material.getCodigoBarras());
				
				// Verifica se a biblioteca do material realiza empr�stimo para o usu�rio //
				verificaOperacoesBiblioteca(movimentoRealizaEmprestimo, consultaEmprestimoDao, bibliotecaDao, material.getBiblioteca(), tipoEmprestimo);
				
				
				
				// Verifica se possui alguma nota de circula��o
				verificaNotasDeCirculacao(daoNota, material, retornoProcessadorCirculacao);
				
				

				
				// Prepara o empr�stimo
				Emprestimo e = new Emprestimo();
				e.setDataEmprestimo(new Date());

				// Seta o usu�rio que est� emprestando.
				e.setUsuarioBiblioteca(movimentoRealizaEmprestimo.getUsuarioBiblioteca());
				
				// Seta a pol�tica do empr�stimo.
				e.setPoliticaEmprestimo(politicaEmprestimo);

				// Seta a situa��o do material para Emprestado.
				material.setSituacao(new SituacaoMaterialInformacional(situacaoEmprestado.getId()));

				// Associa o material ao empr�stimo.
				e.setMaterial(material);
				
				// Calcula e seta o prazo do empr�stimo.
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				
				// Se a pol�tica de empr�stimo � personaliz�vel, o usu�rio define o prazo.
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
					
					/* IMPORTANTE: caso existam materiais dispon�veis a mais que as quantidade de reservas em espera
					 * � porque todas as reservas feitas j� est�o EM ESPERA e existem materiais sobrando.
					 * 
					 * Se existissem reservas solicitadas era uma inconsist�ncia do sistema, pois na devolu��o do empr�stimo, a medida que os materiais s�o devolvidos as pr�ximas
					 * reservas da fila deveriam ir para o status EM ESPERA. Se n�o existir outra reserva na fila, uma outra n�o pode ser solicitada, pois existe material dispon�vel.
					 * 
					 * Nesse caso pode deixar outros usu�rios, que n�o estejam entre os que solicitaram a reserva, levar emprestado o material
					 */
					
					if( (quantidadeMateriasNaoAnexosDisponiveis-qtdMateriasTituloReservaEmMemoria) <= reservasEmEspera.size()){
						
						// Se entrou aqui, � porque somente quem tem reserva pode realizar o empr�stimo //
						
					
						ReservaMaterialBiblioteca reservaDoUsuario = getReservaDoUsuario(reservasEmEspera, movimentoRealizaEmprestimo.getUsuarioBiblioteca()); // Pega a primeira reserva em espera
						
						if(reservaDoUsuario != null ){ // Se tem reserva para o usu�rio que est� tentando fazer o empr�stimo
							
							reservaDoUsuario.setStatus(StatusReserva.CONCRETIZADA);
							reservaDoUsuario.setEmprestimosGerado(e);
							reservasConsolidadas.add(reservaDoUsuario);
							
						}else{ // se n�o n�o deixa fazer o empr�stimo
							throw new NegocioException("O empr�stimo n�o pode ser realizado, pois existem reservas em espera para outros usu�rios .");
						}
						
					}
					
					qtdMateriaisEmprestadosParaTitulo.put(idTituloDoMaterial, ++qtdMateriasTituloReservaEmMemoria);
					
				}
				
				
				//Calcula o prazo correto para o empr�stimo, livrando feriados e finais de semana, dependendo da biblioteca do material emprestado.
				prorrogacoes.addAll(CirculacaoUtil.geraProrrogacoesEmprestimo(e, null));
				
				// Adiciona o empr�stimo � lista de empr�stimos a realizar.
				emprestimos.add(e);
			
			
			} // fecha o for "materiaisAEmprestar"
			
			
			List <Integer> idsMateriais = new ArrayList <Integer> ();
			
			for (Emprestimo e : emprestimos) {
				
				e.setUsuarioRealizouEmprestimo((Usuario) operador);
				//configuraTipoUsuarioEmpretimo(movimentoRealizaEmprestimo, e);
				
				idsMateriais.add(e.getMaterial().getId());
				matDao.create(e); // Realiza o empr�stimo
			}
			
			
			if(idsMateriais.size() > 0){
				
				// Seta a situa��o dos materiais para EMPRESTADO ///
				matDao.atualizaSituacaoDeMateriais(idsMateriais, situacaoEmprestado.getId());
			
			
				// Registra o empr�stimo dos T�tulos desses materiais  /// 
				RegistraEstatisticasBiblioteca.getInstance().registrarTitulosEmprestados(idsMateriais);
				
			}	
			
			for (ReservaMaterialBiblioteca reserva : reservasConsolidadas) {
				reservaDao.updateFields(ReservaMaterialBiblioteca.class, reserva.getId(), new String[]{"status", "emprestimosGerado.id"}, new Object[]{reserva.getStatus(), reserva.getEmprestimosGerado().getId()});
			} 
			
			
			
			// Registra as prorroga��es que os empr�stimos sofreram por cair em feriados ou finais de semana, dependendo da biblioteca.
			if (!prorrogacoes.isEmpty())
				for (ProrrogacaoEmprestimo p : prorrogacoes)
					matDao.create(p);

			// Retorna as opera��es que foram feitas para depois poder desfazer, se for necess�rio.
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
			
			System.out.println("    ***    Realizar empr�stimos demorou: "+ (System.currentTimeMillis()-tempo) +" ms       ***     ");   // tempo <= 1s
		}
	}



	/**
	 *   Envia um email para comprovar o empr�stimo
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
		
		String assunto = " Confirma��o de Realiza��o de Empr�stimos ";
		String titulo = " Empr�stimos Realizados ";
		String mensagemUsuario = " Os empr�stimos abaixo foram realizados com sucesso: ";
		
		List<String> listaInformacoesEmail = new ArrayList<String>();
		
		for (OperacaoBibliotecaDto operacaoEmprestimo : emprestimosRealizados) {
			listaInformacoesEmail.add(" <strong> Data do Empr�stimo: </strong>"+ operacaoEmprestimo.getDataRealizacaoFormatada()
					+" <strong> Prazo para Devolu��o: </strong>"+ operacaoEmprestimo.getPrazoFormatado()
					+" <strong>Material:</strong> " +operacaoEmprestimo.infoMaterial);
		}
		
		String codigoAutenticacao = CirculacaoUtil.getCodigoAutenticacaoEmprestimo(emprestimosRealizados);
		
		new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
				, EnvioEmailBiblioteca.AVISO_EMPRESTIMO, mensagemUsuario, null, null, null, listaInformacoesEmail
				, null, null, codigoAutenticacao, null);
	}
	
	
	
	/** 
	 * <p> Verifica se o material pertence � biblioteca do operador para permitir empr�stimo </p> 
	 * <p> <i> ( setor de informa��o e refer�ncia pode fazer empr�stimos institucionais de materiais ) </i> </p>
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
			throw new NegocioException ("O material de c�digo de barras: "+ material.getCodigoBarras() +" n�o pertence a uma biblioteca onde o senhor(a) possua permiss�o para operar.");
		}
		
		
		try {
			
			//////////////////////////////////////////////////////////////////////////////
			// Verifica se o usu�rio est� no momento operando na biblioteca do material //
			// Utilizado para os empr�stimos feitos no m�dulo de circula��o desktop,    //
			// onde o usu�rio opera em uma biblioteca por vez                           //   
			//////////////////////////////////////////////////////////////////////////////

			if( movimento.isOperandoBibliotecaEspecifica() ){
				if(! movimento.getIdBibliotecaOperacao().equals(material.getBiblioteca().getId())){
					throw new SegurancaException();
				}
			}
			
		} catch (SegurancaException ex){
			throw new NegocioException ("O material de c�digo de barras: "+ material.getCodigoBarras() +" n�o pertence a biblioteca onde o senhor(a) est� operando no momento.");
		}
		
	}
	
	
	
	
	
	/**
	 * <p>Verifica se a biblioteca realiza de determinado tipo de empr�stimo para o tipo do usu�rio que est� tentando emprestar os materiais.</p>
	 * 
	 * <p>Caso n�o realize, verifica se ele realiza empr�timos para outros tipos de usu�rios e o aluno pertence a esses outros 
	 * grupos, se pertencer tamb�m pode realizar empr�stimos.</p>
	 * 
	 * @param mov
	 * @param dao
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	private void verificaOperacoesBiblioteca(MovimentoRealizaEmprestimo mov, ConsultasEmprestimoDao dao, BibliotecaDao bibliotecaDao, Biblioteca biblioteca, TipoEmprestimo tipoEmprestimo) throws DAOException, NegocioException {
		
		if(biblioteca == null)
			throw new NegocioException ("N�o foi poss�vel encontrar a biblioteca onde o usu�rio tem permiss�o de circula��o.  Provavelmente sua permiss�o foi dada para uma unidade diferente da unidade da sua biblioteca.");
		
		if(! mov.getUsuarioBiblioteca().getVinculo().isVinculoAluno() && ! mov.getUsuarioBiblioteca().getVinculo().isVinculoBiblioteca()) // N�o precisa verificar
			return;
		
		
		/**
		 * Verifica se o empr�stimos entre bibliotecas est� ativo para a biblioteca do materail
		 */
		
		if(mov.getUsuarioBiblioteca().getVinculo().isVinculoBiblioteca()){
			
			Integer idUnidadeSeEstaEmprestandoOMaterial  = bibliotecaDao.findIdUnidadeByBiblioteca(mov.getUsuarioBiblioteca().getBiblioteca());
			
			if( idUnidadeSeEstaEmprestandoOMaterial != null){ // est� se empr�tando para uma biblioteca interna
				
				boolean bibliotecaEmprestaInstitucionalInterno = bibliotecaDao.isServicoEmpretimosAtivo(biblioteca.getId(), null, "emprestimoInstitucionalInterno"); 
				
				if(! bibliotecaEmprestaInstitucionalInterno){
					throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos institucionais para outras bibliotecas internas do sistema. ");
				}
			}
			
			if( idUnidadeSeEstaEmprestandoOMaterial == null){ // est� se empr�tando para uma biblioteca externa
				boolean bibliotecaEmprestaInstitucionalExterno = bibliotecaDao.isServicoEmpretimosAtivo(biblioteca.getId(), null, "emprestimoInstitucionalExterno");
				
				if(! bibliotecaEmprestaInstitucionalExterno){
					throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos institucionais para bibliotecas externas ");
				}
			}
			
			return; // N�O PRECISA VERIFICAR AS OUTRAS REGRAS PARA ALUNOS !!!
		}
		
		
		
		/**
		 * Verifica que tipos de empr�stimos est�o ativos para alunos
		 */
		
		if(biblioteca.isBibliotecaCentral()) // A regra de emprestimos fazem sentido se for a central, pois n�o est� ligada a nenhum centro.
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
			
			boolean outroCentro = ! mesmoCentro;  // para facilitar o entendimento do c�digo
			
			switch (mov.getUsuarioBiblioteca().getVinculo()){
				
				/* 
				 * Caso o empr�stimos n�o seja permitido tem que verificar se pode com o v�nculo de gradu��o ou p�s, se tiver. 
				 * Apesar de n�o usar os outros v�nculos para realizar empr�stimo.
				 */	
				case ALUNO_INFANTIL:
					
					if (mesmoCentro && ! bibliotecaEmprestaAlunosGraduacaoMesmoCentro){
						
	
						boolean possuiOutrosVinculoAlunoGraduacaoOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisNaoInfantil);
						boolean possuiOutrosVinculoAlunoPosOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisPos);
						
						if( ! possuiOutrosVinculoAlunoGraduacaoOutroCentro && ! possuiOutrosVinculoAlunoPosOutroCentro )
							throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de infantil/t�cnico/m�dio/gradua��o do mesmo centro e o usu�rio n�o possui outros v�nculos em outros centros. ");
						else{ // se possuiOutrosVinculoAlunoOutroCentro vai poder emprestar se a biblioteca fizer empr�stimos para aluno de graduacao mesmo centro
							
							boolean podeFazerEmpretimo = false;
							
							if( possuiOutrosVinculoAlunoGraduacaoOutroCentro && bibliotecaEmprestaAlunosGraduacaoOutroCentro )
								podeFazerEmpretimo = true;
							
							if( possuiOutrosVinculoAlunoPosOutroCentro &&  bibliotecaEmprestaAlunosPosOutroCentro ) 
								podeFazerEmpretimo = true;
							
							if(! podeFazerEmpretimo)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos infantil do mesmo centro. O usu�rio possui v�nculo em outros centros, mas a biblioteca tamb�m n�o realiza empr�stimos para alunos de gradua��o/p�s-gradu��o de outros centros.");
						}
						
					}else{ 
						if (outroCentro && ! bibliotecaEmprestaAlunosGraduacaoOutroCentro){
							
							boolean possuiOutrosVinculoAlunoGraduacaoMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisNaoInfantil);
							boolean possuiOutrosVinculoAlunoPosMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisPos);
							
							if( ! possuiOutrosVinculoAlunoGraduacaoMesmoCentro && ! possuiOutrosVinculoAlunoPosMesmoCentro)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de infantil de outros centros e o usu�rio n�o possui outros v�nculos de gradua��o/p�s-gradu��o no mesmo centro.");
							
							else{ // se possuiOutrosVinculoAlunoMesmoCentro vai poder emprestar se a biblioteca fizer empr�stimos para aluno de graduacao mesmo centro
								
								boolean podeFazerEmpretimo = false;
								
								if( possuiOutrosVinculoAlunoGraduacaoMesmoCentro && bibliotecaEmprestaAlunosGraduacaoMesmoCentro )
									podeFazerEmpretimo = true;
								
								if( possuiOutrosVinculoAlunoPosMesmoCentro &&  bibliotecaEmprestaAlunosPosMesmoCentro ) 
									podeFazerEmpretimo = true;
								
								if(! podeFazerEmpretimo)
									throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos infantil de outro centro. O usu�rio possui outros v�nculos no mesmo centro, mas a biblioteca tamb�m n�o realiza empr�stimos para alunos de gradua��o/p�s-gradu��o do mesmo centro.");
							}
							
						}
					}
			
			
				/* 
				 * Caso o empr�stimos n�o seja permitido tem que verificar se pode com o v�nculo de gradu��o ou p�s, se tiver. 
				 * Apesar de n�o usar os outros v�nculos para realizar empr�stimo.
				 */	
				case ALUNO_TECNICO_MEDIO:
					
					if (mesmoCentro && ! bibliotecaEmprestaAlunosGraduacaoMesmoCentro){
						
						boolean possuiOutrosVinculoAlunoGraduacaoOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisNaoTecnico);
						boolean possuiOutrosVinculoAlunoPosOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisPos);
						
						if( ! possuiOutrosVinculoAlunoGraduacaoOutroCentro && ! possuiOutrosVinculoAlunoPosOutroCentro )
							throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de t�cnico/m�dio/gradua��o do mesmo centro e o usu�rio n�o possui outros v�nculos em outros centros. ");
						else{ // se possuiOutrosVinculoAlunoOutroCentro vai poder emprestar se a biblioteca fizer empr�stimos para aluno de graduacao mesmo centro
							
							boolean podeFazerEmpretimo = false;
							
							if( possuiOutrosVinculoAlunoGraduacaoOutroCentro && bibliotecaEmprestaAlunosGraduacaoOutroCentro )
								podeFazerEmpretimo = true;
							
							if( possuiOutrosVinculoAlunoPosOutroCentro &&  bibliotecaEmprestaAlunosPosOutroCentro ) 
								podeFazerEmpretimo = true;
							
							if(! podeFazerEmpretimo)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de t�cnicos do mesmo centro. O usu�rio possui v�nculo em outros centros, mas a biblioteca tamb�m n�o realiza empr�stimos para alunos de gradua��o/p�s-gradu��o de outros centros.");
						}
						
					}else{ 
						if (outroCentro && ! bibliotecaEmprestaAlunosGraduacaoOutroCentro){
							
							boolean possuiOutrosVinculoAlunoGraduacaoMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisNaoTecnico);
							boolean possuiOutrosVinculoAlunoPosMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisPos);
							
							if( ! possuiOutrosVinculoAlunoGraduacaoMesmoCentro && ! possuiOutrosVinculoAlunoPosMesmoCentro)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de t�cnico/m�dio/gradua��o de outros centros e o usu�rio n�o possui outros v�nculos de gradua��o/p�s-gradu��o no mesmo centro.");
							
							else{ // se possuiOutrosVinculoAlunoMesmoCentro vai poder emprestar se a biblioteca fizer empr�stimos para aluno de graduacao mesmo centro
								
								boolean podeFazerEmpretimo = false;
								
								if( possuiOutrosVinculoAlunoGraduacaoMesmoCentro && bibliotecaEmprestaAlunosGraduacaoMesmoCentro )
									podeFazerEmpretimo = true;
								
								if( possuiOutrosVinculoAlunoPosMesmoCentro &&  bibliotecaEmprestaAlunosPosMesmoCentro ) 
									podeFazerEmpretimo = true;
								
								if(! podeFazerEmpretimo)
									throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos t�cnicos/m�dios de outro centro. O usu�rio possui outros v�nculos no mesmo centro, mas a biblioteca tamb�m n�o realiza empr�stimos para alunos de gradua��o/p�s-gradu��o do mesmo centro.");
							}
							
						}
					}
					
					
				/* Caso o empr�stimos n�o seja permitido tem que verificar se pode com o v�nculo de t�cnico ou p�s, se tiver. 
				 * Apesar de n�o usar os outros v�nculos para realizar empr�stimo.
				 */	
				case ALUNO_GRADUACAO: 
					if (mesmoCentro && ! bibliotecaEmprestaAlunosGraduacaoMesmoCentro){
						
						boolean possuiOutrosVinculoAlunoGraduacaoOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisNaoGraduacao);
						boolean possuiOutrosVinculoAlunoPosOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisPos);
						
						if( ! possuiOutrosVinculoAlunoGraduacaoOutroCentro && ! possuiOutrosVinculoAlunoPosOutroCentro )
							throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de gradua��o do mesmo centro e o usu�rio n�o possui outros v�nculos em outros centros.");
						else{ // se possuiOutrosVinculoAlunoOutroCentro vai poder emprestar se a biblioteca fizer empr�stimos para aluno de graduacao mesmo centro
							
							boolean podeFazerEmpretimo = false;
							
							if( possuiOutrosVinculoAlunoGraduacaoOutroCentro && bibliotecaEmprestaAlunosGraduacaoOutroCentro )
								podeFazerEmpretimo = true;
							
							if( possuiOutrosVinculoAlunoPosOutroCentro &&  bibliotecaEmprestaAlunosPosOutroCentro ) 
								podeFazerEmpretimo = true;
							
							if(! podeFazerEmpretimo)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de gradua��o do mesmo centro. O usu�rio possui outros v�nculos em outros centros, mas a biblioteca tamb�m n�o realiza empr�stimos para alunos de t�cnico/m�dio/p�s-gradua��o de outro centro.");
						}
					}else{ 
						if (outroCentro && ! bibliotecaEmprestaAlunosGraduacaoOutroCentro){
							
							boolean possuiOutrosVinculoAlunoGraduacaoMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisNaoGraduacao);
							boolean possuiOutrosVinculoAlunoPosMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisPos);
							
							if( ! possuiOutrosVinculoAlunoGraduacaoMesmoCentro && ! possuiOutrosVinculoAlunoPosMesmoCentro)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de gradua��o de outros centros e o usu�rio n�o possui outro v�nculo de t�cnico/m�dio/p�s-gradu��o no mesmo centro.");
							
							else{ // se possuiOutrosVinculoAlunoMesmoCentro vai poder emprestar se a biblioteca fizer empr�stimos para aluno de graduacao mesmo centro
								
								boolean podeFazerEmpretimo = false;
								
								if( possuiOutrosVinculoAlunoGraduacaoMesmoCentro && bibliotecaEmprestaAlunosGraduacaoMesmoCentro )
									podeFazerEmpretimo = true;
								
								if( possuiOutrosVinculoAlunoPosMesmoCentro &&  bibliotecaEmprestaAlunosPosMesmoCentro ) 
									podeFazerEmpretimo = true;
								
								if(! podeFazerEmpretimo)
									throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de gradua��o de outro centro. O usu�rio possui v�nculo no mesmo centro, mas a biblioteca tamb�m n�o realiza empr�stimos para alunos de t�cnico/m�dio/p�s-gradua��o do mesmo centro.");
							}
						}
					}
				break;
				
				/* Caso o empr�stimos n�o seja permitido com o v�nculo que ele ir� usar para realizar os empr�stimos tem que verificar se pode 
				 * com algum v�nculo de infantil, t�cnico ou de gradu��o, se tiver. 
				 * A pesar de n�o usar esses outro v�nculo para realizar empr�stimo.
				 */	
				case ALUNO_POS_GRADUCACAO:
					if (mesmoCentro && !  bibliotecaEmprestaAlunosPosMesmoCentro){
						
						boolean possuiOutrosVinculoAlunoOutroCentro = possuiOutrosVinculoAlunoOutroCentro(mov, biblioteca, niveisNaoPos);
						
						if( ! possuiOutrosVinculoAlunoOutroCentro)
							throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de p�s-gradua��o do mesmo centro e o usu�rio n�o possui v�nculos de infantil/t�cnico/m�dio/gradua��o em outros centros");
						else{
							if(possuiOutrosVinculoAlunoOutroCentro && ! bibliotecaEmprestaAlunosGraduacaoOutroCentro){
								throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de p�s-gradua��o do mesmo centro. O usu�rio possui outros v�nculos em outros centros, mas a biblioteca tamb�m n�o realiza empr�stimos para alunos de infantil/t�cnico/m�dio/gradua��o do outro centro.");
							}
						}
					}else{ 
						if (outroCentro && !   bibliotecaEmprestaAlunosPosOutroCentro){
							
							boolean possuiOutrosVinculoAlunoMesmoCentro = possuiOutrosVinculoAlunoMesmoCentro(mov, biblioteca, niveisNaoPos) ;
							
							if( ! possuiOutrosVinculoAlunoMesmoCentro)
								throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de p�s-gradua��o de outros centros e o usu�rio n�o possui outro v�nculo de infantil/t�cnico/m�dio/gradua��o no mesmo centro.");
							else{
								if(possuiOutrosVinculoAlunoMesmoCentro && ! bibliotecaEmprestaAlunosGraduacaoMesmoCentro){
									throw new NegocioException ("A "+biblioteca.getDescricao()+" n�o realiza empr�stimos do tipo "+tipoEmprestimo.getDescricao()+" para alunos de p�s-gradua��o de outro centro. O usu�rio possui outros v�nculos no mesmo centro, mas a biblioteca tamb�m n�o realiza empr�stimos para alunos infantil/t�cnico/m�dio/gradua��o do mesmo centro.");
								}
							}
						}
					}
				break;
			default:
				break;
			}
		} else
			throw new NegocioException ("Apesar de o usu�rio ser do tipo \""+mov.getUsuarioBiblioteca().getVinculo()+"\", n�o foi encontrado um discente para ele.");
	}


	/**
	 * <p>M�todo que verifica se o aluno est� nomesmo centro da biblioteca ou n�o para verificar se ele pode realizar empr�stimos.</p>
	 * 
	 * <p><strong>Existem 2 casos.</strong>  As bibliotecas setoriais normais, os cursos dos alunos ficam ligadas aos centros.  
	 * Nas biblioteca setoriais de unidades especializadas, os cursos dos alunos ficam ligadas a pr�pria unidade especializada.</p>
	 * 
	 * <p>A l�gica � a seguinte: </p>
	 * 
	 * <p>Caso a unidade respons�vel da biblioteca seja uma unidade especializada. Ent�o verifica se o a unidade do curso do aluno � 
	 * igual a respons�vel da unidade da biblioteca.  Por exemplo: "Biblioteca de M�sica" fica dentro da unidades especializada 
	 * "Escola de M�sica".   Os discentes est�o vinculados a cursos da "Escola de M�sica".</p>
	 * 
	 * <p>Casa a unidade respons�vel da biblioteca N�O seja uma unidade especializada. Ent�o verifica se a unidade gestora da unidade 
	 * do curso do aluno(que � um centro) � a unidade gestora da unidade da biblioteca (que tamb�m � um centro). 
	 *  Por exemplo: "Biblioteca de Eng. Quimica" est� ligada ao "DEPARTAMENTO DE ENGENHARIA QUIMICA" cuja gestora � o "CENTRO DE TECNOLOGIA".</p>
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
			
			if(bibliotecaVeriricacao.getUnidade().getUnidadeResponsavel().isUnidadeAcademicaEspecializada()){ // Se a unidade respons�vel da unidade da biblioteca � especializada
				// O curso tem que est� ligado diretamente a essa unidade //
				mesmoCentro = d.getCurso().getUnidade().equals(bibliotecaVeriricacao.getUnidade().getUnidadeResponsavel());
			}else{
				// Se n�o � especializada, verifica se o centro onde o curso est� � o mesmo centro da unidade da biblioteca
				mesmoCentro = d.getCurso().getUnidade().getGestora().equals(bibliotecaVeriricacao.getUnidade().getGestora());
			}
		}	
		
		dao.detach(bibliotecaVeriricacao);
		
		return mesmoCentro;
	}

	
	
	/**
	 * <p>M�todo criado para os casos em que o aluno � aluno de p�s ou gradua��o de um <strong>centro diferente</strong> e a biblioteca <strong>n�o empresta</strong> 
	 * para alunos de outros centros, mas o aluno possui um v�nculo menor, n�o utilizado para os empr�stimos que est� no mesmo centro da biblioteca. </p>
	 *  
	 * <p>Neste caso o sistema vai precisar busca outros v�nculo menores do usu�rio e verificar se algum dos v�nculo menores est�o 
	 * no mesmo centro, se sim, o empr�stimo deve ser permitido</p> 
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
	 * <p>M�todo criado para os casos em que o aluno � aluno de p�s ou gradua��o do <strong>mesmo centro </strong> e a biblioteca <strong>n�o empresta</strong> 
	 * para alunos de mesmo centros, mas o aluno possui um v�nculo menor, n�o utilizado para os empr�stimos que est� em outro centro da biblioteca. </p>
	 *  
	 * <p>Neste caso o sistema vai precisar busca outros v�nculo menores do usu�rio e verificar se algum dos v�nculo menores est�o 
	 * em outros centros, se sim, o empr�stimo deve ser permitido</p> 
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
	 * Para empr�stimos entre bibliotecas, um material deve possuir outro com o mesmo t�tulo catalogr�fico
	 * com situa��o DISPON�VEL e status CIRCULA para que esse empr�stimo n�o fa�a com que a biblioteca
	 * que possui o material n�o fique sem materiais daquele t�tulo.
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
			
			
			// Se n�o h� mais que dois materiais ativos e circulantes para aquele t�tulo, erro.
			if (quantidadeMateriaisAtivosPodemSerEmprestados < 2)
				throw new NegocioException ("O material de c�digo de barras " + material.getCodigoBarras() + " n�o pode ser emprestado porque seu T�tulo n�o possui outro material dispon�vel para circula��o.");
		
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	
	
	/**
	 *  <p>M�todo que verifica se o usu�rio est� tentando empretar um material para a biblioteca que j� o possui, n�o caso de empr�stimos institucionais </p>
	 *  <p>Um empr�stimo s� faz sentido se for para uma biblioteca diferente da biblioteca que possui o material </p>
	 *
	 * @param material
	 * @param usuarioBiblioteca
	 * @param personalMov
	 * @throws NegocioException
	 */
	private void vericaEmprestimoBibliotecaDoMaterial(MaterialInformacional material, UsuarioBiblioteca usuarioBiblioteca, MovimentoRealizaEmprestimo personalMov) throws NegocioException{
		
		if(usuarioBiblioteca.getBiblioteca() != null){
			if( usuarioBiblioteca.getBiblioteca().getId() == material.getBiblioteca().getId()){
				throw new NegocioException ("O material de c�digo de barras " + material.getCodigoBarras() + " n�o pode ser emprestado para a biblioteca onde ele est� localizado.");
			}
		}
		
	}
	
	
	
	
	
	/**
	 * Implementa a regra de neg�cio RN02 : um material n�o poder� ser emprestado para um mesmo usu�rio em um per�odo inferior a 24 horas.
	 * 
	 * @param material
	 * @param usuarioBiblioteca
	 * @param dao
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void verificaTempoLimiteEntreEmprestimos(MaterialInformacional material, UsuarioBiblioteca usuarioBiblioteca, EmprestimoDao dao) throws DAOException, NegocioException {

		int prazoMinimoEntreEmprestimosEmHoras  = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.PRAZO_MINIMO_ENTRE_EMPRESTIMOS);
		
		if(prazoMinimoEntreEmprestimosEmHoras > 0){ // Se essa regra est� habilitada
		
			Date ultimaDataDevolucao = dao.findDataDevolucaoUltimoEmprestimoAtivoByUsuarioMaterial(usuarioBiblioteca, material);
			
			// Se o usu�rio j� emprestou esse material alguma vez, verifica se a data de devolu��o do empr�stimo � menor que o prazo m�nimo
			if (ultimaDataDevolucao != null) {
	
				Date agora = new Date();
				int horas = CalendarUtils.calculaQuantidadeHorasEntreDatas(ultimaDataDevolucao, agora);
	
				// Se for maior ou igual ao prazo m�nimo pode emprestar //
				if (horas < prazoMinimoEntreEmprestimosEmHoras)
					throw new NegocioException("Um material n�o pode ser emprestado para um mesmo usu�rio em um per�odo inferior a "+prazoMinimoEntreEmprestimosEmHoras+" hora(s)");
			}
		}
	}

	

	/**
	 * Verifica se o usu�rio est� tentando tomar emprestados dois ou mais materiais de um mesmo t�tulo.
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
		
		// Percorre todos os materiais que j� foram emprestados e verifica se tem algum que �
		// igual (mesmo t�tulo e volume) ao que vai ser emprestado agora.
		// Essa regra n�o se aplica se os materiais forem anexos, pois o usu�rio tem
		// o direito de levar todos os anexos de um material
		for (Exemplar exemplar : auxExemplares) {
			// OBS.: O m�todo equals n�o est� funcionando aqui pois o hibernate
			// esta trazendo objetos proxy e n�o os reais
			if (exemplarQueVaiSerEmprestado.getTituloCatalografico().getId() == exemplar.getTituloCatalografico().getId()
					&&  (  ( exemplarQueVaiSerEmprestado.getNumeroVolume() == null && exemplar.getNumeroVolume() == null ) 
							|| ( exemplarQueVaiSerEmprestado.getNumeroVolume() != null && exemplar.getNumeroVolume() != null && exemplarQueVaiSerEmprestado.getNumeroVolume().equals(exemplar.getNumeroVolume() ) ) 
						)
					&& !possuiRelacionamentoDeAnexoEntreExemplaresEmprestados(exemplarQueVaiSerEmprestado, auxExemplares)) {

				throw new NegocioException(" O empr�stimo n�o p�de ser realizado pois um usu�rio n�o pode tomar emprestado dois exemplares iguais."
						+ " Os exemplares: " + exemplarQueVaiSerEmprestado.getCodigoBarras() + " e " + exemplar.getCodigoBarras()
						+ " s�o do mesmo t�tulo e possuem o mesmo volume.");
			}
		}
	}

	/**
	 * Verifica se o material � anexo de algum outro ou vice-versa. Chamado pelo m�todo <code>verificaItensDeUmMesmoTitulo</code>
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
	 * Verifica se o material est� dispon�vel para empr�stimo.
	 * 
	 * @param material
	 * @param dao
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void verificaMaterialEstaDisponivel(MaterialInformacional material, EmprestimoDao dao) throws DAOException, NegocioException {
		if (!material.podeSerEmprestado())
			throw new NegocioException("O material com o c�digo de barras " + material.getCodigoBarras() + " n�o est� dispon�vel para empr�stimo ou seu status n�o permite que ele seja emprestado.");
		
		Emprestimo e = null;

		e = dao.findEmAbertoByCodigoBarras(material.getCodigoBarras());

		if (e != null)
			throw new NegocioException("Ocorreu uma inconsist�ncia na base. Apesar do material com o c�digo de barras " + material.getCodigoBarras()
					+ " estar com situa��o que permite o empr�stimo, existe um empr�stimo ativo desse material no sistema - Contate o Suporte ");
	}

	
	
	
	/**
	 * <p>Verifica as quantidades por regra de empr�stimo (regular, especial, multimeio, etc)
	 * para o usu�rio, a partir dos empr�stimos ativos que ele possui.</p>
	 * 
	 * <p>Esse m�todo leva em cosidera��o o c�lculo dos anexos. Ou seja, o empr�stimo de 1 material + N anexos do material = 1 material para a 
	 * contagem da quantidade levada pelo usu�rio.. </p>
	 * 
	 * @param materialEstaSendoEmprestado -  O material que est� sendo emprestado
	 * @param politicaEmprestimoUsada - A pol�tica que est� sendo utilizado para o material que est� sendo emprestado
	 * @param emprestimosAtivos - Os empr�stimos ativos do usu�rio salvo no banco.
	 * @param emprestimosSendoRealizados - Os empr�stios que est�o sendo realizados no momento.
	 * @param dao - O dao para as consultas.
	 * 
	 * @throws DAOException 
	 */
	private void verificaQuantidadePorRegraDeEmprestimo( MaterialInformacional materialEstaSendoEmprestado, PoliticaEmprestimo politicaEmprestimoUsada,
			List<Emprestimo> emprestimosAtivos, List<Emprestimo> emprestimosSendoRealizados, EmprestimoDao dao) throws NegocioException, DAOException {
		
		
		// Politica personaliz�vel n�o precisa verificar porque n�o possui quantide nem prazo definidos, o usu�rio leva quantos o bibliotec�rio permitir //
		
		if(! politicaEmprestimoUsada.isPersonalizavel()){ 
		
			try{
				
				/*
				 * Guarda os ids dos materiais que o usu�rio j� possui para na mesma pol�tica de empr�stimos.
				 * 
				 * Mesma pol�tica n�o necessariamente significa mesmo objeto, pode ser uma pol�tica diferente para os mesmo dados 
				 * (biblioteca, vinculo do usu�rio, tipo de empr�stimo, status do material e tipos do material)
				 */
				List <Integer> idsMateriaisEmprestadosMesmaPolitica = new ArrayList<Integer>();        
				
				
				/* Nesse lista guarda os materiais extritamente j� emprestados para o usu�rio, ou seja, n�o possui o material atualmente sendo emprestado.*/
				//List<Integer> idsMateriaisJaEmprestadosMesmaPolitica = new ArrayList<Integer>(); 
				
				
				/*
				 * Guarda os ids dos materiais emprestado para o usu�rio descontando os anexo caso o principal j� esteja emprestado. 
				 * Se n�o tiver, conta todos os anexos como materiais normais.
				 */
				Set<Integer>  idMateriaisDescontandoAnexosEmprestadosParaUsuario = new HashSet<Integer>(); // 
			
				
				
				
				// Adiciona os materiais j� emprestados ao usu�rio //				
				for (Emprestimo e : emprestimosAtivos){
					if(e.getPoliticaEmprestimo() != null && e.getPoliticaEmprestimo().equalByDadosPolitica(politicaEmprestimoUsada))  // Conta os materias da mesma pol�tica
						idsMateriaisEmprestadosMesmaPolitica.add(e.getMaterial().getId());
				}
				
				// Adiciona os outros emprestimos que est�o sendo feitos agora //
				for (Emprestimo e : emprestimosSendoRealizados){
					if(e.getPoliticaEmprestimo() != null  && e.getPoliticaEmprestimo().equalByDadosPolitica(politicaEmprestimoUsada))
						idsMateriaisEmprestadosMesmaPolitica.add(e.getMaterial().getId());
				}
			
				
				// Adiciona o atualmente emprestado para os calculos //
				idsMateriaisEmprestadosMesmaPolitica.add(materialEstaSendoEmprestado.getId());
				
				
				if(idsMateriaisEmprestadosMesmaPolitica.size() > 1 ){ // s� precisa conta se tiver outros materiais emprestados
					
					// se os materiais foram anexos, cont�m os ids dos principais deles, se n�o cont�m os pr�prios ids 
					idMateriaisDescontandoAnexosEmprestadosParaUsuario = dao.getIdsMateriaisDescontandoAnexosQuandoPrincipalEstaEmprestado(idsMateriaisEmprestadosMesmaPolitica);
				}
				

				
				
				if (politicaEmprestimoUsada.getQuantidadeMateriais() != null
						&& (idMateriaisDescontandoAnexosEmprestadosParaUsuario.size()) > politicaEmprestimoUsada.getQuantidadeMateriais() ){
					throw new NegocioException("A quantidade m�xima de empr�stimos do tipo \"<strong>"+politicaEmprestimoUsada.getTipoEmprestimo().getDescricao()+"</strong>\" para material do tipo \"<strong>" + materialEstaSendoEmprestado.getStatus().getDescricao() + "</strong>\" foi ultrapassada. "
							+"Permitido: " + politicaEmprestimoUsada.getQuantidadeMateriais() + ". Quantidade de materiais que ficariam emprestados: "+ (idMateriaisDescontandoAnexosEmprestadosParaUsuario.size() ) + ".");
				}
			
			}finally{
				
			}
		
		} // ! politicaEmprestimo.isPersonalizavel()
		
	}

	
	
	
	
	/**
	 * M�todo que verifica se o material tem alguma nota de circula��o.
	 * Caso seja uma nota bloqueante, lan�a uma exe��o para n�o conseguir realizar o empr�stimo, 
	 * caso contr�rio apenas adiciona a lista que ser� mostrada ao operador/usu�rio
	 * 
	 * @param daoNota
	 * @param retorno
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	private void verificaNotasDeCirculacao(NotaCirculacaoDao  daoNota, MaterialInformacional material, RetornoOperacoesCirculacaoDTO retornoProcessadorCirculacao) throws DAOException, NegocioException{
		
		List<NotaCirculacao> notas = daoNota.getNotasAtivasDoMaterial(material.getId());
		
		for (NotaCirculacao notaCirculacao : notas) {
			if(notaCirculacao.isBloquearMaterial()){  // O material est� bloqueado, n�o pode ser realizado o empr�stimo 
				throw new NegocioException("O material de c�digo de barras " + material.getCodigoBarras() + " est� bloqueado e n�o pode ser emprestado. O motivo: " + notaCirculacao.getNota());
			}else{
				if(notaCirculacao.isMostrarEmprestimo()){
					retornoProcessadorCirculacao.addMensagemAosUsuarios("Nota de Circula��o do material "+material.getCodigoBarras()+" : "+notaCirculacao.getNota());
					
					//Desativa a nota para n�o ser mais mostrada
					daoNota.updateFields(NotaCirculacao.class, notaCirculacao.getId(), new String []{"mostrarEmprestimo"}, new Object [] {false});
				}
			}
		}  
	}
	
	
	/**
	 *  <p>Verifica se o usu�rio que est� realizando o empr�stimo est� entre os usu�rios que possuem reserva "EM ESPERA"
	 *  para o t�tulo, s�o os �nicos usu�rios que podem emprestar materiais desse t�tulo. </p> 
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
	 * Junta todos os materiais que j� foram emprestados e est�o sendo emprestados agora em
	 * um �nico array para poder calcular o n�mero de empr�stimos que o usu�rio pode fazer.
	 * 
	 * @param emprestimosAtivos
	 * @param emprestimos
	 * @return
	 */
	private List <MaterialInformacional> montaMateriaisEmprestados (List<Emprestimo> emprestimosAtivos, List<Emprestimo> emprestimos) {
		List<MaterialInformacional> materiaisEmprestados = new ArrayList<MaterialInformacional>();

		// Joga tudo em um array s� //
		for (Emprestimo e : emprestimosAtivos)
			materiaisEmprestados.add(e.getMaterial());

		for (Emprestimo e : emprestimos)
			materiaisEmprestados.add(e.getMaterial());

		return materiaisEmprestados;
	}
	


	/**
	 * Ver coment�rio na classe pai
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
				throw new IllegalArgumentException("N�o foi enviado nenhum material para ser empr�stado");
	
			if (usuarioBiblioteca == null || usuarioBiblioteca.getId() <= 0)
				throw new IllegalArgumentException("N�o foi passado o usu�rio biblioteca para realizar o empr�stimo");
	
	
			// VERIFICA SE A SENHA EST� CORRETA  //	
			if (!  usuarioBiblioteca.ehBiblioteca()  && (usuarioBiblioteca.getSenha() == null || !usuarioBiblioteca.getSenha().equals(personalMov.getSenhaDigitada()))){
					throw new NegocioException("Senha do Usu�rio Incorreta");	
			}
			
			// VERIFICA SE O V�NCULO N�O FOI ENCERRADO  ///
			if(usuarioBiblioteca.isQuitado())
				throw new NegocioException("O v�nculo atual foi quitado, n�o � mais poss�vel realizar empr�stimos com ele");
			
			
			// VERIFICA SE O V�NCULO ATRIBUIDO NO CADASTRO CONTINUA ATIVO //
			VerificaSituacaoUsuarioBibliotecaUtil.verificaVinculoUtilizadoAtivo(usuarioBiblioteca);
			
			
			//// VERIFICA SE O USU�RIO FOI BLOQUEADO PARA EMPR�STIMO ////////
			if( usuarioBiblioteca.ehBiblioteca() )
				VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioBloqueado(null, usuarioBiblioteca.getBiblioteca().getId());
			else
				VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioBloqueado(usuarioBiblioteca.getPessoa().getId(), null);
			
			
			//// VERIFICA SE O USU�RIO N�O POSSUI CADASTRO DUPLICADO NA BIBLIOTECA ////////
			if( ! usuarioBiblioteca.getVinculo().isVinculoBiblioteca()){
				if(new Long(1).compareTo(dao.countUsuariosBibliotecaAtivoNaoQuitadoByPessoa(usuarioBiblioteca.getPessoa().getId())) < 0)
					throw new NegocioException("O usu�rio possui mais de um v�nculo ativo na biblioteca, quite um dos v�nculo para poder realizar novos empr�stimos. ");
			} else{
				if (new Long(1).compareTo(dao.countUsuariosBibliotecaAtivoNaoQuitadoByPessoa(usuarioBiblioteca.getBiblioteca().getId())) < 0)
					throw new NegocioException("O usu�rio possui mais de um v�nculo ativo na biblioteca, quite um dos v�nculo para poder realizar novos empr�stimos. ");
			}
					
			//// VERIFICA SE A PESSOA DO USU�RIO BIBLIOTECA POSSUI CPF OU PASSAPORTE  ////////	
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