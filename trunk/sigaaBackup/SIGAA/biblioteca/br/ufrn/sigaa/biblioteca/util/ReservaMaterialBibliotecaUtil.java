/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.PoliticaEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ProrrogacaoEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 * <p>Classe auxiliar com métodos comuns para a parte de reservas de materiais. </p>
 * 
 * @author jadson
 *
 */
public class ReservaMaterialBibliotecaUtil {

	
	/**
	 * Método que verifica se a opção de trabalhar com reservas está ativa no sistema ou não.
	 * 
	 * @return
	 */
	public static boolean isSistemaTrabalhaComReservas(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosBiblioteca.SISTEMA_TRABALHA_COM_RESERVAS);
	}
	
	
	/**
	 * <p>Método que contém o texto padrão do email de reservas disponíveis.</p>
	 * 
	 * <p>Utilizando no processador que devolve o material e na rotina que verifica as reservas EM ESPERA vencidas</p>
	 *
	 * @param nomeUsuario
	 * @param emailUsuario
	 * @param idReserva
	 * @param idTitulo
	 * @param prazoRetirarMaterial
	 * @throws DAOException 
	 */
	public static void enviaEmailReservaDisponivel(EnvioEmailBiblioteca sender, String nomeUsuario
			, String emailUsuario, int idReserva, int idTitulo, Date prazoRetirarMaterial) throws DAOException{
		
		String assunto = " Aviso de Reserva Disponível";
		String titulo = " Reserva em Espera ";
		String mensagemUsuario = "A sua reserva para o Título:  <i>"+BibliotecaUtil.obtemDadosResumidosTitulo(idTitulo)+"</i> está disponível.";
		
		/// Diference entre as data em dias
		int qtdDiasParaFazerEmprestimo = new Long (( prazoRetirarMaterial.getTime() - new Date().getTime() )/1000/60/60).intValue();
		
		if(qtdDiasParaFazerEmprestimo <= 24)
			qtdDiasParaFazerEmprestimo =  1;
		else
			qtdDiasParaFazerEmprestimo = qtdDiasParaFazerEmprestimo/24;
		
		String mensagemAlertaCorpo =  " O senhor(a) tem um prazo de "+qtdDiasParaFazerEmprestimo+" dia(s) a contar de hoje para realizar o empréstimo desse material";
		
		String avisoFimReserva = " Reserva estará disponível até a data de: "+new SimpleDateFormat("dd/MM/yyyy").format(prazoRetirarMaterial);
		
		String codigoAutenticacao = BibliotecaUtil.geraNumeroAutenticacaoComprovantes(idReserva, prazoRetirarMaterial);
		
		sender.enviaEmail( nomeUsuario, emailUsuario, assunto, titulo, EnvioEmailBiblioteca.AVISO_RESERVAS_MATERIAL, mensagemUsuario, null, null, null, null
				, mensagemAlertaCorpo, avisoFimReserva ,  codigoAutenticacao, null);
	}
	
	
	
	/**
	 * 
	 * <p> Calcula uma <strong>previsão<strong> de quando uma reserva poderá ser efetuada.</p>
	 *  
	 * <p>O calcudo supoe que sempre o empréstimo será renovado e apenas 1 vez.</p> 
	 *
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	public static Date calculaPrevisaoEntregaMaterial(int idTitulo, List<ReservaMaterialBiblioteca> reservasJaExistentes) throws DAOException{

		long time = System.currentTimeMillis();
		
		ReservaMaterialBibliotecaDao reservaDao = null;
		ProrrogacaoEmprestimoDao prorrogacaoDao = null;
		PoliticaEmprestimoDao potiticaDao = null;
		
		Calendar previsaoEntrega = Calendar.getInstance();  // a data que vai ser calculada como previsão
		
		// Caso já existem reservas, pega a data da previsão de entrega da última reserva e acrescenta
		// a quantidade de dias da política de empréstimos de um aluno de gradução * a quantidade de renovações permitidas.
		// Gera apeans uma aproximação, considerando que a maioria dos empréstimos são para alunos de gradução e empréstimos normais
		
		try{
			
			reservaDao = DAOFactory.getInstance().getDAO(ReservaMaterialBibliotecaDao.class);
			prorrogacaoDao = DAOFactory.getInstance().getDAO(ProrrogacaoEmprestimoDao.class);
			potiticaDao = DAOFactory.getInstance().getDAO(PoliticaEmprestimoDao.class);
		
			// Já existes reservas feitas, então a previsão vai ser com base na última reserva.
			if(reservasJaExistentes.size() > 0){ 
				
				// O prazo mais curto para devolver um material, por exemplo daqui a 5 dias
				Date primeiroPrazoDevolucao = reservaDao.findPrimeiroPrazoDevolucaoMaterialDoTitulo(idTitulo);
				
				if(primeiroPrazoDevolucao == null){
					previsaoEntrega.setTime(new Date()); // hoje
				}else{
					previsaoEntrega.setTime(primeiroPrazoDevolucao);
				}
				
				// Obtém os valores da política mais utilizada no momento, por exemplo, alunos de gradução (15 dias e 1 renovação) 
				Integer[] valoresPoliticaMaisUsada = potiticaDao.findPrazoEQuantiadeRenovacoesPoliticaMaisUsada();
				
				int quantiadeDiasEmprestimoMaisUsado =  valoresPoliticaMaisUsada[0];
				int quantiadeRenovacoesEmprestimoMaisUsado =  valoresPoliticaMaisUsada[1];
				
				// Realiza o qual da previsão considerando a quantidade de reservas 
				// Normalmente:   15 dias + ( 15 dias * 1 renovação) = 30 dias * quantidade reservas na frente = X dias para realizar o empréstimo
				Integer previsaoDiasParaEfetivarEmprestimo 
					= ( quantiadeDiasEmprestimoMaisUsado + (quantiadeDiasEmprestimoMaisUsado * quantiadeRenovacoesEmprestimoMaisUsado) )
					* reservasJaExistentes.size();
				
				// por exemplo daqui a 5 dias + X dias calculados da previsão.
				previsaoEntrega.add(Calendar.DAY_OF_MONTH, previsaoDiasParaEfetivarEmprestimo);
			
				
			}else{ // Realizando a primeira reserva
			
				// O calculo é realizado com base nos empréstimos dos materiais do título existente atualmente   //
				
				previsaoEntrega.add(Calendar.YEAR, -1); // para ficar com um data anterior
				
				List<Object[]> objects =  reservaDao.findPrazosDevolucaoMaterialDoTitulo(idTitulo);
			
				// Para cada emprestimo ativo dos materiais do titulo  
				for (Object[] temp : objects) {
					
					Integer idEmprestimo = (Integer) temp[0];
					Date prazoAtual =  (Date) temp [1];
					Integer prazoEmpretimo =  (Integer) temp [2];
					Integer qtdRenovacoesPossiveis =  (Integer) temp [3];
					Short tipoPrazo =  (Short) temp [4];
					
					Integer quatidadeRenovacoesRealizadas = prorrogacaoDao.countProrrogacoesPorRenovacaoDoEmprestimo(new Emprestimo(idEmprestimo));
					
					if( qtdRenovacoesPossiveis.equals(0)  // Se já foi renovado e não pode renovar
							|| ( quatidadeRenovacoesRealizadas != null && quatidadeRenovacoesRealizadas.compareTo(qtdRenovacoesPossiveis) >= 0 ) ) {
					
						if(previsaoEntrega.getTime().getTime() < prazoAtual.getTime())
							previsaoEntrega.setTime(prazoAtual);
					}else{   // se o emprestimo ainda pode ser renovado
						
						Calendar previsaoEntregaTemp = Calendar.getInstance();  
						previsaoEntregaTemp.setTime(prazoAtual);
						if(tipoPrazo.equals( PoliticaEmprestimo.TIPO_PRAZO_EM_DIAS) )
							previsaoEntregaTemp.add(java.util.Calendar.DAY_OF_MONTH, prazoEmpretimo);
						else
							previsaoEntregaTemp.add(java.util.Calendar.HOUR_OF_DAY, prazoEmpretimo);
						
						if(previsaoEntrega.getTime().getTime() < previsaoEntregaTemp.getTime().getTime())
							previsaoEntrega.setTime(previsaoEntregaTemp.getTime());
						
					}
					
				} // fim do for que percorre os emprestimos ativos dos materias
				
			}
			
			return previsaoEntrega.getTime();
			
		}finally{
			if(reservaDao != null ) reservaDao.close();
			if(prorrogacaoDao != null ) prorrogacaoDao.close();
			if(potiticaDao != null ) potiticaDao.close();
			
			System.out.println("Calculo da previsão de devolução demorou: "+(System.currentTimeMillis() - time)+" ms");
		}
	}
	
	
	
	/**
	 * <p>Verifica se quantidade de materiais de um título no acervo atenda a quantidade mínima exigida para realização de reservas.</p>
	 *
	 * <p><strong>IMPORTATE: Se já possuir a informação da quantiade de materiais ativos no acervo, passe a quantidade para evitar uma consulta extra no banco.
	 *   Caso não tenha passe o valor <code>NULL</code> que o método buscará essa informação para você.</strong> </p> 
	 *
	 * @param idTituloCatalografico
	 * @param quantiadeMateriasAtivosDoTitulo   
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static void verificaExisteQuantidadeMinimaParaSolicitarReserva(int idTituloCatalografico) throws DAOException, NegocioException{
		
		int quantidadeMinimaParaSolicitarReserva = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.QUANTIDADE_MINIMA_MATERIAIS_SOLICITAR_RESERVA);
		
		int	quantiadeMateriasAtivosDoTitulo = buscaQuantidadeMateriaisAtivosNoBanco(idTituloCatalografico);
		
		if(quantidadeMinimaParaSolicitarReserva > quantiadeMateriasAtivosDoTitulo){
			throw new NegocioException("Não é possível realizar a reserva do título escolhido, pois o título precisa possuir mais de "+quantidadeMinimaParaSolicitarReserva+" material(is) no acervo para se solicitar uma reserva.");
		}	
	}
	
	
	
	/**
	 * Busca a quantidade de materiais ativos de um título no banco.
	 *
	 * @param idTituloCatalografico
	 * @return
	 * @throws DAOException
	 */
	private static int buscaQuantidadeMateriaisAtivosNoBanco(int idTituloCatalografico)throws DAOException{
		ReservaMaterialBibliotecaDao reservaDao = null;
		try{
			reservaDao = DAOFactory.getInstance().getDAO(ReservaMaterialBibliotecaDao.class);
			return reservaDao.countQuantidadeMateriaisAtivosDoTitulo(idTituloCatalografico);
		}finally{
			if(reservaDao != null ) reservaDao.close();
		}
	}
	
	
	/**
	 * <p>Verifica se existe materias disponível para empréstimos na biblioteca, caso existe lança uma exceção porque 
	 * o usuário não pode solicitar reserva desse material </p>
	 *
	 * <p><strong>IMPORTANTE:</strong> Nesse calculo não leva em consideração os materiais que são anexos, nem suplementos, porque 
	 * não faz sentido o usuário tomar empréstado apenas o anexo ou suplemento. Se existir apenas 1 anexo disponvível, o sistema vai
	 * considerar que todos os materiais estão emprestados para o caso das reservas.</p>
	 *
	 * @param idMaterial
	 * @param codigoBarrasMaterial
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static void verificaExisteMaterialParaSolicitarReservaNoAcervo(int idTituloCatalografico) throws NegocioException, DAOException{
		
		ReservaMaterialBibliotecaDao reservaDao = null;
		
		try{
			reservaDao = DAOFactory.getInstance().getDAO(ReservaMaterialBibliotecaDao.class);
		
			int quantidadeMateriasNaoAnexosDisponiveis = reservaDao.countMateriaisNaoAnexoDisponiveisDoTitulo(idTituloCatalografico);
		
			int quantiadeReservasEmEspera = reservaDao.countReservasEmEsperaDoTitulo(idTituloCatalografico);
		
			/* 
			 * Só pode solicitar um reserva se:
			 * 
			 * 1º: Não possuir materiais disponíveis
			 * 2º: Possuir materiais disponíveis mas eles estarem esperando serem emprestados por quem fez a reserva.
			 * 3º: Não possuir materiais disponíveis e possuir pelo menos um material emprestado ( quando o usuário devolver ele vai poder concretizar a reserva)
			 *
			 */
			if(quantidadeMateriasNaoAnexosDisponiveis > 0 && quantidadeMateriasNaoAnexosDisponiveis - quantiadeReservasEmEspera > 0){
				throw new NegocioException("Não é possível realizar a reserva do Título escolhido, pois ele possui "+(quantidadeMateriasNaoAnexosDisponiveis - quantiadeReservasEmEspera)+" material(is) disponível(is) na biblioteca.");
			}else{ // Se não tiver material disponível no acervo, tem que haver pelo menos 1 empréstado para solicitar reserva
				
				int quantiadeMateriasNaoAnexosEmprestados = reservaDao.countMateriaisEmprestadosDoTitulo(idTituloCatalografico);
				
				// Se não tem nenhum na situação "Emprestado" não pode solicitar, pois numca vai ser possível o usuário concertizar a reserva
				// a não ser que possuem materiais diponíveis em reserva com estatos espera.
				if(quantiadeMateriasNaoAnexosEmprestados <= 0){
					
					// E não tem nenhum "disponível", significa que todos estão em outras situações como por exemplo "fora de empréstimo"
					// então também não pode solicitar reserva
					if(quantidadeMateriasNaoAnexosDisponiveis <= 0)
						throw new NegocioException("Não é possível realizar a reserva do Título escolhido, pois ele não possui materiais emprestados na biblioteca.");
				
				}else{
					
					// Se todos os "Emprestados" estão perdidos, também não pode solicitar reserva //
					int quantiadeMateriasEmprestadosNaoAnexoComComunicacaoPerda = reservaDao.countMateriaisEmprestadosComComunicacaoPerdaDoTitulo(idTituloCatalografico);
					
					if(   (quantiadeMateriasNaoAnexosEmprestados - quantiadeMateriasEmprestadosNaoAnexoComComunicacaoPerda) <=0 )
						throw new NegocioException("Não é possível realizar a reserva do Título escolhido, pois todos os seus materiais emprestados estão perdidos. Aguarde a reposição desses materiais.");
				}
				
			}
			
			// OK pode realizar a reserva
			
		}finally{
			if(reservaDao != null ) reservaDao.close();
		}
	}
	
	
	/**
	 * <p>Verifica se Todos os materiais Empréstados do Título estão perdidos </p>
	 *
	 * <p>Nesse caso o sistema vai emitir uma mensagem para o operador cancelar todas as reservas do Título.
	 * Porque pode ser que esses materiais demorem muito a serem respostos estão os usuários não devem ficar 
	 * esperando por reservas que não irão ocorrer.
	 * </p>
	 *
	 * @param idMaterial
	 * @param codigoBarrasMaterial
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static boolean verificaTodosMateriaisEmprestadosTituloEstaoPerdidos(int idTituloCatalografico) throws DAOException{
		
		ReservaMaterialBibliotecaDao reservaDao = null;
		
		try{
			reservaDao = DAOFactory.getInstance().getDAO(ReservaMaterialBibliotecaDao.class);
			
			int quantiadeMateriasNaoAnexosEmprestados = reservaDao.countMateriaisEmprestadosDoTitulo(idTituloCatalografico);
			int quantiadeMateriasEmprestadosNaoAnexoComComunicacaoPerda = reservaDao.countMateriaisEmprestadosComComunicacaoPerdaDoTitulo(idTituloCatalografico);

			if ((quantiadeMateriasNaoAnexosEmprestados - quantiadeMateriasEmprestadosNaoAnexoComComunicacaoPerda) <= 0)
				return true;
			else
				return false;
				
			
		}finally{
			if(reservaDao != null ) reservaDao.close();
		}
	}
	
	
	
	
	/**
	 * Verifica se o usuário ultrapassou a quantidade máxima de reservas permitidas no sistema
	 *
	 * @param idMaterial
	 * @param codigoBarrasMaterial
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static void verificaQuantidadeMaximaDeReservasDoUsuario(int idUsuarioBiblioteca) throws NegocioException, DAOException{
		
		int quantiadeMaximaReservasPermitidas = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.QUANTIDADE_MAXIMA_RESERVAS_ONLINE);
		
		ReservaMaterialBibliotecaDao reservaDao = null;
		
		try{
			reservaDao = DAOFactory.getInstance().getDAO(ReservaMaterialBibliotecaDao.class);
		
			int quantiadeReservasUsuario = reservaDao.countReservasMaterialAtivasDoUsuario(idUsuarioBiblioteca);
		
			if(quantiadeReservasUsuario >= quantiadeMaximaReservasPermitidas){
				throw new NegocioException("Caro usuário, você atingiu o número máximo de reservas permitidas. ("+quantiadeMaximaReservasPermitidas+")");
			}
		
		}finally{
			if(reservaDao != null ) reservaDao.close();
		}
	}
	
	
	/**
	 * Verifica se o usuário está tentando fazer uma reserva de um material que já está empréstado para ele atualmente.
	 *
	 * @param idMaterial
	 * @param codigoBarrasMaterial
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static void verificaUsuarioJaPossuiMaterialEmprestado(int idUsuarioBiblioteca, int idTitulo) throws NegocioException, DAOException{
		
		ReservaMaterialBibliotecaDao reservaDao = null;
		
		try{
			reservaDao = DAOFactory.getInstance().getDAO(ReservaMaterialBibliotecaDao.class);
		
			int quantiadeEmprestimosAtivosDeMateriaisDoTituloParaUsuario = reservaDao.countEmprestimoAtivosParaUsuarioDoTitulo(idUsuarioBiblioteca, idTitulo);
		
			if(quantiadeEmprestimosAtivosDeMateriaisDoTituloParaUsuario > 0){
				throw new NegocioException(" Caro usuário, não é possível realizar reserva de um material que já está emprestado a você. ");
			}
			
		}finally{
			if(reservaDao != null ) reservaDao.close();
		}
	}
	
	/**
	 * Calcula o prazo que o usuário vai ter para realizar a reserva a partir do momento que ela ficou disponível. 
	 * Considerando o prazo configurado no sistema e as interrupções que a biblioteca possa ter.
	 *  
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	public static Date calculaPrazoRetiradaProximaReserva(int idTitulo) throws DAOException{
		
		ReservaMaterialBibliotecaDao daoReserva = null;
		try{
			
			daoReserva = DAOFactory.getInstance().getDAO(ReservaMaterialBibliotecaDao.class); 
			
			// Pega o prazo configurado para a reserva vai ficar esperando pelo usuário //
			int qtdDiasReservaEmEspera =  ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.PRAZO_EM_DIAS_USUARIO_TEM_PARA_EFETIVAR_RESERVA);
			
			// Adiciona os dias e prorroga para o próximo dia útil considurando os finais de semana e interrupções cadastradas //
			Date prazoRetirarMaterial  = CalendarUtils.adicionaDias(new Date(), qtdDiasReservaEmEspera);
			Biblioteca biblioteca = daoReserva.findInfomacoesFuncionamentoBibliotecaMaterialDisponivelDoTitulo(idTitulo);
			if(biblioteca != null)
				prazoRetirarMaterial = CirculacaoUtil.prorrogaPrazoConsiderandoFimDeSemanaEInterrupcoesDaBiblioteca(prazoRetirarMaterial, biblioteca);
		
			return prazoRetirarMaterial;
			
		}finally {
			if(daoReserva != null) daoReserva.close();
		}
		
	}
	
}
