/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Classe auxiliar com m�todos comuns para a parte de reservas de materiais. </p>
 * 
 * @author jadson
 *
 */
public class ReservaMaterialBibliotecaUtil {

	
	/**
	 * M�todo que verifica se a op��o de trabalhar com reservas est� ativa no sistema ou n�o.
	 * 
	 * @return
	 */
	public static boolean isSistemaTrabalhaComReservas(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosBiblioteca.SISTEMA_TRABALHA_COM_RESERVAS);
	}
	
	
	/**
	 * <p>M�todo que cont�m o texto padr�o do email de reservas dispon�veis.</p>
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
		
		String assunto = " Aviso de Reserva Dispon�vel";
		String titulo = " Reserva em Espera ";
		String mensagemUsuario = "A sua reserva para o T�tulo:  <i>"+BibliotecaUtil.obtemDadosResumidosTitulo(idTitulo)+"</i> est� dispon�vel.";
		
		/// Diference entre as data em dias
		int qtdDiasParaFazerEmprestimo = new Long (( prazoRetirarMaterial.getTime() - new Date().getTime() )/1000/60/60).intValue();
		
		if(qtdDiasParaFazerEmprestimo <= 24)
			qtdDiasParaFazerEmprestimo =  1;
		else
			qtdDiasParaFazerEmprestimo = qtdDiasParaFazerEmprestimo/24;
		
		String mensagemAlertaCorpo =  " O senhor(a) tem um prazo de "+qtdDiasParaFazerEmprestimo+" dia(s) a contar de hoje para realizar o empr�stimo desse material";
		
		String avisoFimReserva = " Reserva estar� dispon�vel at� a data de: "+new SimpleDateFormat("dd/MM/yyyy").format(prazoRetirarMaterial);
		
		String codigoAutenticacao = BibliotecaUtil.geraNumeroAutenticacaoComprovantes(idReserva, prazoRetirarMaterial);
		
		sender.enviaEmail( nomeUsuario, emailUsuario, assunto, titulo, EnvioEmailBiblioteca.AVISO_RESERVAS_MATERIAL, mensagemUsuario, null, null, null, null
				, mensagemAlertaCorpo, avisoFimReserva ,  codigoAutenticacao, null);
	}
	
	
	
	/**
	 * 
	 * <p> Calcula uma <strong>previs�o<strong> de quando uma reserva poder� ser efetuada.</p>
	 *  
	 * <p>O calcudo supoe que sempre o empr�stimo ser� renovado e apenas 1 vez.</p> 
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
		
		Calendar previsaoEntrega = Calendar.getInstance();  // a data que vai ser calculada como previs�o
		
		// Caso j� existem reservas, pega a data da previs�o de entrega da �ltima reserva e acrescenta
		// a quantidade de dias da pol�tica de empr�stimos de um aluno de gradu��o * a quantidade de renova��es permitidas.
		// Gera apeans uma aproxima��o, considerando que a maioria dos empr�stimos s�o para alunos de gradu��o e empr�stimos normais
		
		try{
			
			reservaDao = DAOFactory.getInstance().getDAO(ReservaMaterialBibliotecaDao.class);
			prorrogacaoDao = DAOFactory.getInstance().getDAO(ProrrogacaoEmprestimoDao.class);
			potiticaDao = DAOFactory.getInstance().getDAO(PoliticaEmprestimoDao.class);
		
			// J� existes reservas feitas, ent�o a previs�o vai ser com base na �ltima reserva.
			if(reservasJaExistentes.size() > 0){ 
				
				// O prazo mais curto para devolver um material, por exemplo daqui a 5 dias
				Date primeiroPrazoDevolucao = reservaDao.findPrimeiroPrazoDevolucaoMaterialDoTitulo(idTitulo);
				
				if(primeiroPrazoDevolucao == null){
					previsaoEntrega.setTime(new Date()); // hoje
				}else{
					previsaoEntrega.setTime(primeiroPrazoDevolucao);
				}
				
				// Obt�m os valores da pol�tica mais utilizada no momento, por exemplo, alunos de gradu��o (15 dias e 1 renova��o) 
				Integer[] valoresPoliticaMaisUsada = potiticaDao.findPrazoEQuantiadeRenovacoesPoliticaMaisUsada();
				
				int quantiadeDiasEmprestimoMaisUsado =  valoresPoliticaMaisUsada[0];
				int quantiadeRenovacoesEmprestimoMaisUsado =  valoresPoliticaMaisUsada[1];
				
				// Realiza o qual da previs�o considerando a quantidade de reservas 
				// Normalmente:   15 dias + ( 15 dias * 1 renova��o) = 30 dias * quantidade reservas na frente = X dias para realizar o empr�stimo
				Integer previsaoDiasParaEfetivarEmprestimo 
					= ( quantiadeDiasEmprestimoMaisUsado + (quantiadeDiasEmprestimoMaisUsado * quantiadeRenovacoesEmprestimoMaisUsado) )
					* reservasJaExistentes.size();
				
				// por exemplo daqui a 5 dias + X dias calculados da previs�o.
				previsaoEntrega.add(Calendar.DAY_OF_MONTH, previsaoDiasParaEfetivarEmprestimo);
			
				
			}else{ // Realizando a primeira reserva
			
				// O calculo � realizado com base nos empr�stimos dos materiais do t�tulo existente atualmente   //
				
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
					
					if( qtdRenovacoesPossiveis.equals(0)  // Se j� foi renovado e n�o pode renovar
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
			
			System.out.println("Calculo da previs�o de devolu��o demorou: "+(System.currentTimeMillis() - time)+" ms");
		}
	}
	
	
	
	/**
	 * <p>Verifica se quantidade de materiais de um t�tulo no acervo atenda a quantidade m�nima exigida para realiza��o de reservas.</p>
	 *
	 * <p><strong>IMPORTATE: Se j� possuir a informa��o da quantiade de materiais ativos no acervo, passe a quantidade para evitar uma consulta extra no banco.
	 *   Caso n�o tenha passe o valor <code>NULL</code> que o m�todo buscar� essa informa��o para voc�.</strong> </p> 
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
			throw new NegocioException("N�o � poss�vel realizar a reserva do t�tulo escolhido, pois o t�tulo precisa possuir mais de "+quantidadeMinimaParaSolicitarReserva+" material(is) no acervo para se solicitar uma reserva.");
		}	
	}
	
	
	
	/**
	 * Busca a quantidade de materiais ativos de um t�tulo no banco.
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
	 * <p>Verifica se existe materias dispon�vel para empr�stimos na biblioteca, caso existe lan�a uma exce��o porque 
	 * o usu�rio n�o pode solicitar reserva desse material </p>
	 *
	 * <p><strong>IMPORTANTE:</strong> Nesse calculo n�o leva em considera��o os materiais que s�o anexos, nem suplementos, porque 
	 * n�o faz sentido o usu�rio tomar empr�stado apenas o anexo ou suplemento. Se existir apenas 1 anexo disponv�vel, o sistema vai
	 * considerar que todos os materiais est�o emprestados para o caso das reservas.</p>
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
			 * S� pode solicitar um reserva se:
			 * 
			 * 1�: N�o possuir materiais dispon�veis
			 * 2�: Possuir materiais dispon�veis mas eles estarem esperando serem emprestados por quem fez a reserva.
			 * 3�: N�o possuir materiais dispon�veis e possuir pelo menos um material emprestado ( quando o usu�rio devolver ele vai poder concretizar a reserva)
			 *
			 */
			if(quantidadeMateriasNaoAnexosDisponiveis > 0 && quantidadeMateriasNaoAnexosDisponiveis - quantiadeReservasEmEspera > 0){
				throw new NegocioException("N�o � poss�vel realizar a reserva do T�tulo escolhido, pois ele possui "+(quantidadeMateriasNaoAnexosDisponiveis - quantiadeReservasEmEspera)+" material(is) dispon�vel(is) na biblioteca.");
			}else{ // Se n�o tiver material dispon�vel no acervo, tem que haver pelo menos 1 empr�stado para solicitar reserva
				
				int quantiadeMateriasNaoAnexosEmprestados = reservaDao.countMateriaisEmprestadosDoTitulo(idTituloCatalografico);
				
				// Se n�o tem nenhum na situa��o "Emprestado" n�o pode solicitar, pois numca vai ser poss�vel o usu�rio concertizar a reserva
				// a n�o ser que possuem materiais dipon�veis em reserva com estatos espera.
				if(quantiadeMateriasNaoAnexosEmprestados <= 0){
					
					// E n�o tem nenhum "dispon�vel", significa que todos est�o em outras situa��es como por exemplo "fora de empr�stimo"
					// ent�o tamb�m n�o pode solicitar reserva
					if(quantidadeMateriasNaoAnexosDisponiveis <= 0)
						throw new NegocioException("N�o � poss�vel realizar a reserva do T�tulo escolhido, pois ele n�o possui materiais emprestados na biblioteca.");
				
				}else{
					
					// Se todos os "Emprestados" est�o perdidos, tamb�m n�o pode solicitar reserva //
					int quantiadeMateriasEmprestadosNaoAnexoComComunicacaoPerda = reservaDao.countMateriaisEmprestadosComComunicacaoPerdaDoTitulo(idTituloCatalografico);
					
					if(   (quantiadeMateriasNaoAnexosEmprestados - quantiadeMateriasEmprestadosNaoAnexoComComunicacaoPerda) <=0 )
						throw new NegocioException("N�o � poss�vel realizar a reserva do T�tulo escolhido, pois todos os seus materiais emprestados est�o perdidos. Aguarde a reposi��o desses materiais.");
				}
				
			}
			
			// OK pode realizar a reserva
			
		}finally{
			if(reservaDao != null ) reservaDao.close();
		}
	}
	
	
	/**
	 * <p>Verifica se Todos os materiais Empr�stados do T�tulo est�o perdidos </p>
	 *
	 * <p>Nesse caso o sistema vai emitir uma mensagem para o operador cancelar todas as reservas do T�tulo.
	 * Porque pode ser que esses materiais demorem muito a serem respostos est�o os usu�rios n�o devem ficar 
	 * esperando por reservas que n�o ir�o ocorrer.
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
	 * Verifica se o usu�rio ultrapassou a quantidade m�xima de reservas permitidas no sistema
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
				throw new NegocioException("Caro usu�rio, voc� atingiu o n�mero m�ximo de reservas permitidas. ("+quantiadeMaximaReservasPermitidas+")");
			}
		
		}finally{
			if(reservaDao != null ) reservaDao.close();
		}
	}
	
	
	/**
	 * Verifica se o usu�rio est� tentando fazer uma reserva de um material que j� est� empr�stado para ele atualmente.
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
				throw new NegocioException(" Caro usu�rio, n�o � poss�vel realizar reserva de um material que j� est� emprestado a voc�. ");
			}
			
		}finally{
			if(reservaDao != null ) reservaDao.close();
		}
	}
	
	/**
	 * Calcula o prazo que o usu�rio vai ter para realizar a reserva a partir do momento que ela ficou dispon�vel. 
	 * Considerando o prazo configurado no sistema e as interrup��es que a biblioteca possa ter.
	 *  
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	public static Date calculaPrazoRetiradaProximaReserva(int idTitulo) throws DAOException{
		
		ReservaMaterialBibliotecaDao daoReserva = null;
		try{
			
			daoReserva = DAOFactory.getInstance().getDAO(ReservaMaterialBibliotecaDao.class); 
			
			// Pega o prazo configurado para a reserva vai ficar esperando pelo usu�rio //
			int qtdDiasReservaEmEspera =  ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.PRAZO_EM_DIAS_USUARIO_TEM_PARA_EFETIVAR_RESERVA);
			
			// Adiciona os dias e prorroga para o pr�ximo dia �til considurando os finais de semana e interrup��es cadastradas //
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
