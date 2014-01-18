/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 18/03/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.InterrupcaoBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.PoliticaEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ProrrogacaoEmprestimoDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InterrupcaoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PunicaoAtrasoEmprestimoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.integracao.dtos.EmprestimoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.MaterialInformacionalDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.OperacaoBibliotecaDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.TituloCatalograficoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.UsuarioBibliotecaDto;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;


/**
 * <p>Classe auxiliar para realizar opera��es da parte de circula��o. </p>
 * 
 * @author jadson
 *
 */
public class CirculacaoUtil {
	
	
	/**
	 * Retorna a descri��o da pol�tica de empr�stimos a ser utilizada nos empr�stimos do sistema. (Recebendo o v�nculo)
	 *
	 * @param idBiblioteca
	 * @param vinculo
	 * @param idTipoEmprestimo
	 * @param idStatus
	 * @return
	 * @throws DAOException
	 */
	public static List<String> retornaDescricaoPoliticaEmpretimoASerUtilizada(Integer idBiblioteca, VinculoUsuarioBiblioteca vinculo, Integer idStatus, Integer idTipoMaterial, Integer... idsTipoEmprestimo) throws DAOException{
		
		PoliticaEmprestimoDao politicaDao = null;
		
		List<String> retorno = new ArrayList<String>();
		
		if( ( idsTipoEmprestimo != null && idsTipoEmprestimo.length > 0) 
				&& ( idBiblioteca != null && idBiblioteca > 0) 
				&& ( idStatus != null && idStatus > 0) 
				&& vinculo != null){
		
			try {
				politicaDao = DAOFactory.getInstance().getDAO(PoliticaEmprestimoDao.class);
				
				for (Integer idTipoEmprestimo : idsTipoEmprestimo) {

					TipoEmprestimo tipoEmprestimo = politicaDao.findByPrimaryKey(idTipoEmprestimo, TipoEmprestimo.class, new String[]{"id", "semPoliticaEmprestimo"});
					
					PoliticaEmprestimo politica =  politicaDao.findPoliticaEmpretimoAtivaASerUsuadaNoEmprestimo(
																			new Biblioteca(idBiblioteca),
																			vinculo,
																			tipoEmprestimo,
																			new StatusMaterialInformacional(idStatus),
																			new TipoMaterial(idTipoMaterial));
					
					if( politica != null )
						retorno.add( politica.getInformacoesPolitica() );
					
				}
				
			} catch (NegocioException ne) {
				retorno.add( ne.getMessage() );
			}finally {	
				if (politicaDao != null) politicaDao.close();
			}
		}
		
		return retorno; // retorna a descri��o;
	
	}
	
	/**
	 * Retorna a descri��o da pol�tica de empr�stimos a ser utilizada nos empr�stimos do sistema. (Recebendo o id do UsuarioBiblioteca)
	 *
	 * @param idBiblioteca
	 * @param vinculo
	 * @param idTipoEmprestimo
	 * @param idStatus
	 * @return
	 * @throws DAOException
	 */
	public static List<String> retornaDescricaoPoliticaEmpretimoASerUtilizada(Integer idBiblioteca, int idUsuarioBiblioteca, Integer idStatus,  Integer idTipoMaterial, Integer... idsTipoEmprestimo) throws DAOException{
		
		PoliticaEmprestimoDao politicaDao = null;
		
		List<String> retorno = new ArrayList<String>();
		
		if( idUsuarioBiblioteca > 0 ){
		
			try {
				politicaDao = DAOFactory.getInstance().getDAO(PoliticaEmprestimoDao.class);
		
				UsuarioBiblioteca ub =  politicaDao.findByPrimaryKey(idUsuarioBiblioteca, UsuarioBiblioteca.class, new String[]{"vinculo"});
				
				return retornaDescricaoPoliticaEmpretimoASerUtilizada( idBiblioteca, ub.getVinculo(),  idStatus, idTipoMaterial, idsTipoEmprestimo);
				
			}finally {	
				if (politicaDao != null) politicaDao.close();
			}
		}
		
		return retorno; // retorna a descri��o;
	
	}
	
	
	
	/**
	 * M�todo que verifica se o servi�o de empr�timos e renova��o est� ativo na biblioteca do material que est� se tentando emprestar ou renovar. 
	 * @throws NegocioException 
	 * @throws DAOException 
	 *
	 */
	public static void verificaServicoEmprestimosEstaoAtivos(int idMaterial, String codigoBarrasMaterial) throws NegocioException, DAOException{
		
		BibliotecaDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(BibliotecaDao.class);
		
			if( ! dao.isBibliotecaDoMaterailComServicoDeEmprestimoAtivado(idMaterial)){
				throw new NegocioException ("N�o � poss�vel realizar o empr�stimo ou a renova��o do material: "+codigoBarrasMaterial+", pois esse servi�o est� desativado para a biblioteca dele. ");
			}
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	
	
	/**
	 *  <p>M�todo que gera um c�digo de autentica��o para os comprovantes de empr�stimos no sistema.<br/>  </p>
	 * 
	 *  
	 *
	 * @return
	 * @throws DAOException 
	 */
	public static String getCodigoAutenticacaoEmprestimo(List<OperacaoBibliotecaDto> emprestimosRenovadosOp) throws DAOException{
		
		StringBuilder codigosComprovante = new StringBuilder();
		
		ProrrogacaoEmprestimoDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(ProrrogacaoEmprestimoDao.class);
			
			// Concatena v�rios c�digo de autentica��o no comprovante porque pode ser renovados v�rios empr�stimos ao mesmo tempo //
			for(OperacaoBibliotecaDto operacaoBiblioteca : emprestimosRenovadosOp){
				codigosComprovante.append( BibliotecaUtil.geraNumeroAutenticacaoComprovantes(operacaoBiblioteca.idEmprestimo, operacaoBiblioteca.dataRealizacao)+" ");
			}	
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return codigosComprovante.toString();
	}
	
	
	/**
	 *  M�todo que gera um c�digo de autentica��o com base no id das prorroga�es por renova��o dos empr�stimos.<br/>
	 *  Assim se a prorroga��o por renova��o existir no banco para o empr�stimo do usu�rio, o comprovante vai ser verdadeiro<br/>
	 *  Caso o usu�rio tente falsificar o comprovante o id da prorroga��o n�o vai existir.
	 *  
	 *
	 * @return
	 * @throws DAOException 
	 */
	public static String getCodigoAutenticacaoRenovacao(List<OperacaoBibliotecaDto> emprestimosRenovadosOp) throws DAOException{
		
		StringBuilder codigosComprovante = new StringBuilder();
		
		if(emprestimosRenovadosOp == null)
			return "";
		
		ProrrogacaoEmprestimoDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(ProrrogacaoEmprestimoDao.class);
			
			// Concatena v�rios c�digo de autentica��o no comprovante porque pode ser renovados v�rios empr�stimos ao mesmo tempo //
			for(OperacaoBibliotecaDto operacaoBiblioteca : emprestimosRenovadosOp){
				codigosComprovante.append( BibliotecaUtil.geraNumeroAutenticacaoComprovantes(operacaoBiblioteca.idEmprestimo, 
						dao.findUltimaDataRenovacao(operacaoBiblioteca.idEmprestimo ))+" ");
			}	
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return codigosComprovante.toString();
	}
	
	
	/**
	 * <p> Monta as informa��es do empr�stimo que foi devolvido  <p>
	 * <p> Esse m�todo � usado no processador que devolve o material, � necess�rio retorna as informa��es do empr�stimo 
	 * devolvido para por exemplo imprimir o comprovante de devoluca��o.<p>
	 *
	 *
	 * @param e
	 * @return
	 * @throws DAOException 
	 */
	public static EmprestimoDto montaInformacoesEmprestimoDevolvido(Emprestimo e, List<PunicaoAtrasoEmprestimoBiblioteca> punicoesSofridas ) throws DAOException{
		
		
		EmprestimoDao daoEmprestimo = null;
		
		try {
			daoEmprestimo = DAOFactory.getInstance().getDAO(EmprestimoDao.class);
		
			MaterialInformacionalDto mDTO = new MaterialInformacionalDto();
			
			MaterialInformacional m = e.getMaterial();
			mDTO.idMaterial = e.getMaterial().getId();
			mDTO.codigoBarras = m.getCodigoBarras();
			mDTO.tituloDto = new TituloCatalograficoDto();
			mDTO.tituloDto.autor = m.getTituloCatalografico().getCache().getAutor();
			mDTO.tituloDto.titulo = m.getTituloCatalografico().getCache().getTitulo();
			
			UsuarioBibliotecaDto uDto = new UsuarioBibliotecaDto();
	
			uDto.nome = null;
			
			if (e.getUsuarioBiblioteca() != null){
				
				UsuarioBiblioteca ub = e.getUsuarioBiblioteca();
				
				uDto.idUsuarioBiblioteca = ub.getId();
				
				if(ub.getPessoa() != null)
					uDto.cpf = ub.getPessoa().getCpf_cnpj();
				
				uDto.nome = ub.getNome();
				
				uDto.siape = daoEmprestimo.findSiapeServidorRealizouEmprestimo(e.getId());
				uDto.matricula = daoEmprestimo.findMatriculaDiscenteRealizouEmprestimo(e.getId());
				
			}
			
			 
			if(punicoesSofridas != null){
				for (PunicaoAtrasoEmprestimoBiblioteca punicao : punicoesSofridas) {
					uDto.valoresSituacoesUsuario.add(punicao.getSituacaoGeradaPelaPunicao().getValor());
				}
			}
			
			EmprestimoDto eDto = new EmprestimoDto();
			eDto.idEmprestimo = e.getId();
			eDto.dataDevolucaoFormatado = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(  e.getDataDevolucao()  );
			eDto.prazoFormatado = new SimpleDateFormat("dd/MM/yyyy HH:mm").format( e.getPrazo() );
			eDto.usuario = uDto;
			eDto.materialDto = mDTO;
	
			eDto.numeroAutenticacao = BibliotecaUtil.geraNumeroAutenticacaoComprovantes(e.getId(), e.getDataDevolucao());
			
			return eDto;
		
		} finally {
			if (daoEmprestimo != null)daoEmprestimo.close();
		}
	}
	
	
	/**
	 * <p> Retorna a quantidade de dias em atraso do usu�rio. </p>
	 * <p>
	 * 		<ul>
	 *      <li>Ex.: Se prazo for 01/01/2001 13h e a data final for 02/01/2001 11h = 1 dia de atrazo</li>
	 *      <li>Ex.: Se prazo for 01/01/2001 13h e a data final for 02/01/2001 19h = 1 dia de atrazo</li>
	 *      <li>Ex.: Se prazo for 01/01/2001 13h e a data final for 03/01/2001 11h = 2 dia de atrazo</li>
	 *      <li>Ex.: Se prazo for 01/01/2001 13h e a data final for 01/01/2001 23h = 0 dia de atrazo</li>
	 *      </ul>
	 * </p>
	 * 
	 * <p> <strong> Sempre que alterar esse m�todo, lembre de rodar o teste unit�rio dele <code> CalculaDiasEmAtrasoTest </code> </strong> </p>
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * 
	 * @return A quantidade de dias em atraso do usu�rio
	 */
	public static int calculaDiasEmAtrasoBiblioteca(Date prazo, Date dataDevolucao){
		
		if(prazo == null || dataDevolucao == null)
			throw new IllegalArgumentException("Datas inv�lidas para calcula a quantidade de dias em atrazo.");
		
		long time1 = prazo.getTime();
		long time2 = dataDevolucao.getTime();
		
		long tempoEntreData = time2-time1;
		
		// Calcula a quantidade de horas entre data
		
		Long tempoEmHoras = ( tempoEntreData /1000 ) / 60 / 60;
		
		if(tempoEmHoras <= 24){
			
			Calendar cPrazo = Calendar.getInstance();
			cPrazo.setTime(prazo);
			
			Calendar cDataDevolucao = Calendar.getInstance();
			cDataDevolucao.setTime(dataDevolucao);
			
			 // ser o per�odo for menor que 24 horas mas j� � o dia posterior, ent�o est� 1 dia atrasado
			if(cPrazo.get(Calendar.DAY_OF_MONTH) < cDataDevolucao.get(Calendar.DAY_OF_MONTH)){
				return 1; 
			}else{
				return 0;  // ainda est� no mesmo dia
			}
			
		}else{
			
			Date prazoDescosiderandoHora = CalendarUtils.descartarHoras(prazo);
			Date dataDevolucaoDescosiderandoHora = CalendarUtils.descartarHoras(dataDevolucao);
			
			return CalendarUtils.calculoDias(prazoDescosiderandoHora, dataDevolucaoDescosiderandoHora);
		}
		
	}
	
	
	
	/**
	 * <p>Prorroga os prasos considerando as interrup��es cadastradas para a biblioteca </p>
	 *
	 * @param bibliotecaDoMaterial
	 * @return
	 * @throws DAOException 
	 */
	public static Date prorrogaPrazoConsiderandoFimDeSemanaEInterrupcoesDaBiblioteca(Date dataAtual, Biblioteca bibliotecaDoMaterial) throws DAOException{
		
		InterrupcaoBibliotecaDao iBDao = null;
		
		try {
			iBDao = DAOFactory.getInstance().getDAO(InterrupcaoBibliotecaDao.class);
		
			// Primeiro verifica se � final de semana //
			if( isFinalSemanaEBibliotecaNaoFunciona(dataAtual, bibliotecaDoMaterial)){
				dataAtual = prorrogaParaConsiderandoFimDeSemana(dataAtual, bibliotecaDoMaterial);
			}
			
			// Depois, tenta achar o primeiro dia livre, retirando os finais de semana e futuras interrp��es da biblioteca //
			
			List <InterrupcaoBiblioteca> interrupcoesBiblioteca = iBDao.findInterrupcoesAtivasFuturasByBiblioteca(bibliotecaDoMaterial);
			
			for (InterrupcaoBiblioteca interrupcao : interrupcoesBiblioteca){
				if (  interrupcao.getData().equals(CalendarUtils.descartarHoras(dataAtual)) || isFinalSemanaEBibliotecaNaoFunciona(dataAtual, bibliotecaDoMaterial)){
					dataAtual.setTime(CalendarUtils.adicionaUmDia(dataAtual).getTime());
					
				}else{ // encontrou o primeiro dia livre
					return dataAtual;
				}
			}
			
			// Caso v� at� a data da �ltima interru��o e n�o encontre dia livre, testa se o dia depois de todas as interrrup��es
			// � final de semana e prorroga novamente
			if( isFinalSemanaEBibliotecaNaoFunciona(dataAtual, bibliotecaDoMaterial)){
				dataAtual = prorrogaParaConsiderandoFimDeSemana(dataAtual, bibliotecaDoMaterial);
			}
			
			return dataAtual; //Testou todas as prorroga��es futuras, ent�o n�o tem perido de cair em uma
			
		} finally {
			if (iBDao != null) iBDao.close();
		}
	}
	
	/**
	 * <p>Prorroga os prasos considerando as interrup��es cadastradas para a biblioteca </p>
	 *
	 * <p><strong>Para funcionar corretamente, s� deve ser chamado a partir do m�tido 
	 * {@link this#prorrogaPrazoConsiderandoFimDeSemanaEInterrupcoesDaBiblioteca(Date, Biblioteca)} </strong></p>
	 *
	 * @param bibliotecaDoMaterial
	 * @return
	 * @throws DAOException 
	 */
	private static Date prorrogaParaConsiderandoFimDeSemana(Date dataAtual, Biblioteca bibliotecaDoMaterial){
		
		Calendar cAtual = Calendar.getInstance();
		cAtual.setTime(dataAtual);
		
		// Adiantou para o s�bado... se a biblioteca n�o trabalhar no s�bado,
		if (cAtual.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && ! bibliotecaDoMaterial.isFuncionaSabado() )
			cAtual.setTime(CalendarUtils.adicionaUmDia(cAtual.getTime()));
		
		// Adiantou para o domingo... se a biblioteca n�o trabalhar no domingo,
		if (cAtual.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && ! bibliotecaDoMaterial.isFuncionaDomingo())
			cAtual.setTime(CalendarUtils.adicionaUmDia(cAtual.getTime()));
		
		return cAtual.getTime();
	}
	
	/**
	 * Verifica se � final de semana e se a biblioteca passada funciona nesse final de semana
	 *
	 * @param data
	 * @return
	 */
	public static boolean isFinalSemanaEBibliotecaNaoFunciona(Date dataAtual, Biblioteca bibliotecaDoMaterial){
		
		if(   (isSabado(dataAtual) && ! bibliotecaDoMaterial.isFuncionaSabado() ) || ( isDomingo(dataAtual) && ! bibliotecaDoMaterial.isFuncionaDomingo() ) )  
			return true;
		else
			return false;
	}
	
	/**
	 * Verifica se � s�bado
	 *
	 * @param data
	 * @return
	 */
	public static boolean isSabado(Date data){
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
	}
	
	/**
	 * Verifica se � domingo
	 *
	 * @param data
	 * @return
	 */
	public static boolean isDomingo(Date data){
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
	}
	
	
	/**
	 * Calcula o prazo do empr�stimo, retornando uma lista das prorroga��es que este sofrer�
	 * por cair em um final de semana, caso a biblioteca n�o trabalhe no final de semana,
	 * em um feriado ou interrup��o da biblioteca.
	 * 
	 * @param interrupcoesFuturasBiblioteca - As interrup��es futuras da biblioteca, inclusive as que est�o sendo criadas no momento. (S� � passado no caso de uso de criar interrup��o, dos outros caso de uso vem nulo)
	 * @param e
	 * @param bibliotecaMaterial, a biblioteca do material - pode ser mesmo a biblioteca do material ou a biblioteca para o qual o material foi movimentado.
	 * @return
	 * @throws DAOException 
	 */
	public static List <ProrrogacaoEmprestimo> geraProrrogacoesEmprestimo (Emprestimo e, Biblioteca bibliotecaMaterial, List <InterrupcaoBiblioteca> interrupcoesFuturasBiblioteca) throws DAOException{

		Calendar c = Calendar.getInstance();
		
		// As prorroga�oes geradas para o empr�stimo.
		List <ProrrogacaoEmprestimo> prorrogacoes = new ArrayList <ProrrogacaoEmprestimo> ();

		ProrrogacaoEmprestimo p = null;

		boolean verificarNovamente = false;

		/*
		 * Caso n�o seja passada a lista de interrup��es futuras, busca do banco!
		 * 
		 * A maioria dos casos de uso s� busca 1 vez, ent�o n�o h� problemas, o caso de uso de cadastro de prorroga��es pode 
		 * buscar 3.000 a 10.000 vezes ent�o � passado por fora para diminuir a quantidade de buscas. capiche! 
		 * 
		 */
		if(interrupcoesFuturasBiblioteca == null){ 
			InterrupcaoBibliotecaDao interrupcaoDao = null;
			try{
				interrupcaoDao = DAOFactory.getInstance().getDAO(InterrupcaoBibliotecaDao.class);
				interrupcoesFuturasBiblioteca = interrupcaoDao.findInterrupcoesAtivasFuturasByBiblioteca(bibliotecaMaterial);
			}finally{
				if (interrupcaoDao != null) interrupcaoDao.close();
			}
		}
		
		// Se ocorrerem altera��es no prazo, deve checar todas as condi��es novamente.
		do {
			verificarNovamente = false;
			// Primeiramente, checa se o prazo cai no final de semana.
			c.setTime(e.getPrazo());

			// Se for um s�bado e a biblioteca n�o trabalhar no s�bado,
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && !e.getMaterial().getBiblioteca().isFuncionaSabado()){
				p = new ProrrogacaoEmprestimo(e, TipoProrrogacaoEmprestimo.FIM_DE_SEMANA, e.getPrazo());
				p.setDataAtual(CalendarUtils.adicionaUmDia(p.getDataAnterior()));

				c.setTime(p.getDataAtual());

				// Adiantou para o domingo... se a biblioteca n�o trabalhar no domingo,
				if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && !e.getMaterial().getBiblioteca().isFuncionaDomingo())
					p.setDataAtual(CalendarUtils.adicionaUmDia(p.getDataAtual()));

				// Atualiza o prazo do empr�stimo.
				e.setPrazo(p.getDataAtual());

				// � um domingo e a biblioteca n�o trabalha no domingo,
			} else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && !e.getMaterial().getBiblioteca().isFuncionaDomingo()){
				p = new ProrrogacaoEmprestimo(e, TipoProrrogacaoEmprestimo.FIM_DE_SEMANA, e.getPrazo());
				p.setDataAtual(CalendarUtils.adicionaUmDia(p.getDataAnterior()));
				e.setPrazo(p.getDataAtual());
			}

			// Adiciona esta prorroga��o � lista de prorroga��es.
			if (p != null)
				prorrogacoes.add(p);

			// Checa se o prazo cai em uma interrup��o da biblioteca.

			for (InterrupcaoBiblioteca i : interrupcoesFuturasBiblioteca)
				if (i.getData().equals(CalendarUtils.descartarHoras(e.getPrazo()))){
					p = new ProrrogacaoEmprestimo(e, TipoProrrogacaoEmprestimo.INTERRUPCAO_BIBLIOTECA, e.getPrazo());
					p.setDataAtual(CalendarUtils.adicionaUmDia(p.getDataAnterior()));
					e.setPrazo(p.getDataAtual());

					prorrogacoes.add(p);

					verificarNovamente = true;
				}

		} while (verificarNovamente);

		return prorrogacoes;
	}
	
	
	
	
	/**
	 * <p> Calcula o pr�ximo dia �til para o qual os empr�stimos podem ser prorrogados </p>
	 * 
	 * @param daFimAtual - A data atual que os novos empr�stimos v�o ficar
	 * @param biblioteca - A para a qual a interrup��o vai ser cadastrada
	 * @return
	 * @throws DAOException 
	 */
	public static Date calculaProximoDiaUtilInterrupcao(Date dataFimAtual, Biblioteca biblioteca, List <InterrupcaoBiblioteca> interrupcoesFuturasBiblioteca) throws DAOException{

		Calendar c = Calendar.getInstance(); // a data atual do empr�stimo
		c.setTime(dataFimAtual);
		
		boolean verificarNovamente = false;

		do {
			
			verificarNovamente = false;
			
			/* ***************************************************************
			 *  Primeiramente, checa se o prazo atual cai no final de semana
			 ****************************************************************** */
		
			// Se for um s�bado e a biblioteca n�o trabalhar no s�bado, adianta para o domingo....
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && ! biblioteca.isFuncionaSabado()){
				
				c.setTime( CalendarUtils.adicionaUmDia(c.getTime() ) );

				// se a biblioteca n�o trabalhar no domingo, adianta para a segunda-feira
				if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && !biblioteca.isFuncionaDomingo())
					c.setTime( CalendarUtils.adicionaUmDia(c.getTime() ) );

				// Se for diretamente um domingo e a biblioteca n�o trabalha no domingo, adianta para a segunda-feira
			} else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && ! biblioteca.isFuncionaDomingo()){
				c.setTime( CalendarUtils.adicionaUmDia(c.getTime() ) );
			}

			/* ***************************************************************
			 *  Em segundo lugar, checa se o prazo atual cai em alguma outra interrup��o que j� existe.
			 ****************************************************************** */

			for (InterrupcaoBiblioteca interrupcaoFutura : interrupcoesFuturasBiblioteca){
				if ( interrupcaoFutura.getData().equals( CalendarUtils.descartarHoras(c.getTime())) ){
					c.setTime( CalendarUtils.adicionaUmDia(c.getTime() ) );
					verificarNovamente = true;  // se ocorrerem altera��es no prazo devido uma interrup��o futura, deve-se checar todas as condi��es novamente.
				}
			}
			
		} while (verificarNovamente); // fica nisso eternamente at� chegar a uma data dispon�vel

	

		return c.getTime(); // retorna a data dispon�vel
	}
	
	
	/** 
	 * Ordena os materias a serem emprestado de forma que os anexos fiquem por �ltimo
	 * Para garatir que o principal seja emprestado antes do anexo, garantindo que o 
	 * anexo n�o ser� contado.
	 */
	public static void ordenaMateriaisByAnexos( List<MaterialInformacional> materiaisAEmprestar) {
		
		Collections.sort(materiaisAEmprestar, new Comparator<MaterialInformacional>() {
			@Override
			public int compare(MaterialInformacional m1, MaterialInformacional m2) {
				if ( m1 instanceof Exemplar && m2 instanceof Exemplar){
					if( ( (Exemplar) m1).isAnexo() ) return 1; // anexos � sempre � maior, vai para o fim da fila
					if( ( (Exemplar) m2).isAnexo() ) return -1;
				}
				
				if ( m1 instanceof Fasciculo && m2 instanceof Fasciculo){
					if( ( (Fasciculo) m1).isSuplemento() ) return 1; // suplemento � sempre maior, vai para o fim da fila
					if( ( (Fasciculo) m2).isSuplemento() ) return -1;
				}
				return 0; // qualquer outro caso n�o interessa.
			}
		});
	}

	
}