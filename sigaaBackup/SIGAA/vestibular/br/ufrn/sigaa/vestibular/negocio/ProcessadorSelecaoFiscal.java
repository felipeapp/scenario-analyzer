/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/05/2008
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoFiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.ProcessoSeletivoVestibularDao;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.parametros.dominio.ParametrosVestibular;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.vestibular.dominio.ConceitoFiscal;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.QuantidadeFiscalPorMunicipio;
import br.ufrn.sigaa.vestibular.dominio.ResumoProcessamentoSelecao;

/**
 * Processador responsável pela a seleção de fiscais para um Processo Seletivo
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class ProcessadorSelecaoFiscal extends AbstractProcessador {

	/**
	 * Executa o processamento da seleção de fiscais.
	 * 
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		// inicializa todas variáveis
		// Processo Seletivo ao qual os fiscais estão sendo selecionados para trabalhar. 
		ProcessoSeletivoVestibular processoSeletivo;

		// DAO utilizado no processamento para consultas à Inscrições dos Fiscais. 
		InscricaoFiscalDao inscricaoFiscalDao;
		// DAO utilizado no processamento para consultas ao Processo Seletivo. 
		ProcessoSeletivoVestibularDao processoSeletivoDao;
		// DAO utilizado no processamento para consultas à fiscais. 
		FiscalDao fiscalDao;
		// Indica se o processamento deve ser simulado. 
		boolean simulacaoProcessamento;
		// Mapa com um resumo quantitativo do processamento. 
		List<ResumoProcessamentoSelecao> resumoProcessamento = new ArrayList<ResumoProcessamentoSelecao>();

		// Quantidade de fiscais por Município. 
		Collection<QuantidadeFiscalPorMunicipio> quantidadeFiscaisPorMunicipio;
		
		// lista de fiscais por curso. 
		Map<String, List<InscricaoFiscal>> listaFiscalPorCurso;
		
		quantidadeFiscaisPorMunicipio = ((MovimentoSelecaoFiscal) mov).getFiscaisPorMunicipio();
		simulacaoProcessamento = ((MovimentoSelecaoFiscal) mov).isSimulaProcessamento();
		processoSeletivo = ((MovimentoSelecaoFiscal) mov).getProcessoSeletivoVestibular();
		
		inscricaoFiscalDao = new InscricaoFiscalDao();
		processoSeletivoDao = new ProcessoSeletivoVestibularDao();
		fiscalDao = new FiscalDao();
		
		try {
			
			if (!simulacaoProcessamento) {
				processoSeletivo = processoSeletivoDao.findByPrimaryKey(processoSeletivo.getId(), ProcessoSeletivoVestibular.class);
				// Grava o resultado da simulação
				Collection<Fiscal> fiscais = ((MovimentoSelecaoFiscal) mov).getFiscaisSelecionados();
				for (Fiscal fiscal : fiscais) {
					// seta valores iniciais para fiscais (conceito, presenças, etc.)
					fiscal.setPresenteAplicacao(new Boolean(true));
					fiscal.setPresenteReuniao(new Boolean(true));
					fiscal.setConceito(ConceitoFiscal.SUFICIENTE);
					fiscalDao.createNoFlush(fiscal);
				}
				// Grava o resumo do resultado da simulação
				List<ResumoProcessamentoSelecao> resumos = ((MovimentoSelecaoFiscal) mov).getResumoProcessamento();
				for (ResumoProcessamentoSelecao resumo : resumos) {
					resumo.setProcessoSeletivoVestibular(processoSeletivo);
					fiscalDao.createNoFlush(resumo);
				}
				// registra que a seleção de fiscais do processo seletivo foi
				// processada a fim de liberar a consulta do resultado do
				// processamento e outros fins
				// recupera todas informações do PS, a fim de evitar erro de persistência
				// com objetos transientes.
				processoSeletivo.setSelecaoFiscalProcessada(true);
				processoSeletivoDao.update(processoSeletivo);
				// encerra o processamento
				return new Integer(fiscais.size());
			} else {
				// Zera o resumo do processamento
				resumoProcessamento = new ArrayList<ResumoProcessamentoSelecao>();
				// lista de novos fiscais selecionados a ser gravada ao final do
				// processamento
				ArrayList<Fiscal> novosFiscaisGeral = new ArrayList<Fiscal>();
				// lista de fiscais selecionados por município
				ArrayList<Fiscal> fiscaisSelecionadosMunicipio;
				// lista de reservas selecionados por município
				ArrayList<Fiscal> reservasSelecionadosMunicipio;
				// Quantos fiscais a selecionar
				int quantidade, quantidadeSelecionado;
				
				// para cada município
				for (QuantidadeFiscalPorMunicipio quantidadeFiscalMunicipio : quantidadeFiscaisPorMunicipio) {
					// se a quantidade de fiscais a selecionar for zero
					if (quantidadeFiscalMunicipio.getNumFiscais() <= 0)
						// pule para o próximo município
						continue;
					Municipio municipio = quantidadeFiscalMunicipio;
					
					// zera a lista de selecionados por município
					fiscaisSelecionadosMunicipio = new ArrayList<Fiscal>();
					// zera a lista de selecionados por município
					reservasSelecionadosMunicipio = new ArrayList<Fiscal>();
	
					// quantidade de fiscais titulares já selecionados
					quantidadeSelecionado = (int) fiscalDao
							.findTotalFiscaisMunicipio(processoSeletivo, municipio,
									false);
	
					// Quantidade a selecionar
					quantidade = quantidadeFiscalMunicipio.getNumFiscais()
							- fiscaisSelecionadosMunicipio.size()
							- quantidadeSelecionado;
					// seleciona servidores
					fiscaisSelecionadosMunicipio.addAll(selecionaServidores(resumoProcessamento, municipio, quantidade, mov));
	
					// Quantidade a selecionar
					quantidade = quantidadeFiscalMunicipio.getNumFiscais()
							- fiscaisSelecionadosMunicipio.size()
							- quantidadeSelecionado;
					// seleciona fiscais antigos
					fiscaisSelecionadosMunicipio.addAll(selecionaFiscaisAntigos(resumoProcessamento, municipio, quantidade, mov));
					
					// Quantidade a selecionar
					quantidade = quantidadeFiscalMunicipio.getNumFiscais()
							- fiscaisSelecionadosMunicipio.size()
							- quantidadeSelecionado;
					// seleciona fiscais residentes
					fiscaisSelecionadosMunicipio.addAll(selecionaFiscaisResidentes(resumoProcessamento,	municipio, quantidade, mov));
	
					// carrega a lista de inscritos por curso do município
					listaFiscalPorCurso = carregaListaAConcorrer(resumoProcessamento, municipio, fiscaisSelecionadosMunicipio, mov);
	
					// Quantidade a selecionar
					quantidade = quantidadeFiscalMunicipio.getNumFiscais()
							- fiscaisSelecionadosMunicipio.size()
							- quantidadeSelecionado;
					// seleciona fiscais titulares novos
					fiscaisSelecionadosMunicipio.addAll(selecionaFiscalMunicipio(resumoProcessamento, municipio, quantidade, false, mov, listaFiscalPorCurso));
	
					// quantidade de fiscais reservas já selecionados
					quantidadeSelecionado = (int) fiscalDao
							.findTotalFiscaisMunicipio(processoSeletivo, municipio,
									true);
					// determina a quantidade de reservas
					quantidade = quantidadeFiscalMunicipio.getNumFiscais()
							* quantidadeFiscalMunicipio.getPercentualReserva()
							/ 100 - quantidadeSelecionado;
					// seleciona reservas
					reservasSelecionadosMunicipio.addAll(selecionaFiscalMunicipio(resumoProcessamento, municipio, quantidade, true, mov, listaFiscalPorCurso));
	
					// joga na lista de selecionados geral
					novosFiscaisGeral.addAll(fiscaisSelecionadosMunicipio);
					novosFiscaisGeral.addAll(reservasSelecionadosMunicipio);
				}
	
				// Retorna os fiscais selecionados na simulação
				((MovimentoSelecaoFiscal) mov).setFiscaisSelecionados(novosFiscaisGeral);
				
				Collections.sort(resumoProcessamento);
				
				return resumoProcessamento;
			}
		} catch (ArqException e) {
			e.printStackTrace();
			throw e;
		} finally {
			fiscalDao.close();
			inscricaoFiscalDao.close();
			processoSeletivoDao.close();
		}
	}

	/**
	 * Carrega a lista de inscrições de fiscais a concorrer na seleção
	 * (que não são servidores ou recadastro) de um determinado município
	 * excetuando as inscrições de uma lista de fiscais (selecionados no
	 * processamento).
	 * 
	 * @see br.ufrn.sigaa.arq.dao.vestibular.InscricaoFiscalDao
	 * @param municipio
	 * @param excetoFiscais
	 * @return 
	 * @throws DAOException
	 */
	private Map<String, List<InscricaoFiscal>> carregaListaAConcorrer(List<ResumoProcessamentoSelecao> resumoProcessamento, Municipio municipio,
			ArrayList<Fiscal> excetoFiscais, Movimento mov) throws DAOException {
		InscricaoFiscalDao inscricaoFiscalDao = getDAO(InscricaoFiscalDao.class, mov);
		ProcessoSeletivoVestibular processoSeletivo = ((MovimentoSelecaoFiscal) mov).getProcessoSeletivoVestibular();
		Map<String, List<InscricaoFiscal>> listaFiscalPorCurso = inscricaoFiscalDao
				.findAllConcorrendoByProcessoSeletivoMunicipio(processoSeletivo
						.getId(), municipio.getId(), excetoFiscais);
		atualizaResumoInscritos(resumoProcessamento, municipio.getNome() + " - A CONCORRER", listaFiscalPorCurso);
		inscricaoFiscalDao.close();
		return listaFiscalPorCurso;
	}

	/**
	 * Seleciona fiscais por município, dada uma quantidade.
	 * 
	 * @param municipio
	 *            Município ao qual a seleção se restringe.
	 * @param quantidade
	 *            Quantidade de fiscais a selecionar
	 * @param reserva
	 *            Indica se os fiscais serão convocados como reserva ou como
	 *            titulares. (Reserva = true / Titular = false)
	 * @return Lista de Fiscal selecionados
	 * @throws DAOException
	 */
	private List<Fiscal> selecionaFiscalMunicipio(List<ResumoProcessamentoSelecao> resumoProcessamento, Municipio municipio,
			int quantidade, boolean reserva, Movimento mov, Map<String, List<InscricaoFiscal>> listaFiscalPorCurso) throws DAOException {
		// lista de fiscais selecionados
		ArrayList<Fiscal> fiscais = new ArrayList<Fiscal>();
		if (quantidade > 0) {
			// calcula a quantidade de fiscais a selecionar por município
			int totalConcorrendoMunicipio = 0;
			for (String curso : listaFiscalPorCurso.keySet())
				totalConcorrendoMunicipio += listaFiscalPorCurso.get(curso)
						.size();
			// determina o percentual de seleção por curso:
			double percentual = (double) quantidade / totalConcorrendoMunicipio;
			System.out.println("Percentual a selecionar do município "+municipio.getNome() + ": " + Formatador.getInstance().formatarDecimal1(percentual*100)+"%");
			// para cada curso do município
			for (String curso : listaFiscalPorCurso.keySet()) {
				// determine a quantidade inteira a selecionar
				int numFiscais = (int) Math.ceil(listaFiscalPorCurso.get(curso)
						.size()
						* percentual);
				// pelo menos um por curso
				if (numFiscais == 0)
					numFiscais = 1;
				// adicione à lista, os fiscais selecionados
				fiscais.addAll(selecionaPorCurso(resumoProcessamento, municipio, curso, numFiscais, reserva, mov, listaFiscalPorCurso));
			}
		}
		return fiscais;
	}

	/**
	 * Seleciona fiscais de um determinado curso, dada uma quantidade. Caso haja
	 * empate no IRA na última colocação, será convocado todos Discentes que
	 * tenham o mesmo IRA. O mapa contendo as inscrições por curso utilizado neste método
	 * foi carregada anteriormente no método {@link #carregaListaAConcorrer(Município, ArrayList)}.
	 * 
	 * @param curso
	 *            Curso ao qual a seleção se restringe
	 * @param numFiscais
	 *            número de fiscais a selecionar
	 * @param reserva
	 *            Indica se os fiscais serão convocados como reserva ou como
	 *            titulares. (Reserva = true / Titular = false)
	 * @param mov 
	 * @param listaFiscalPorCurso 
	 * @return Lista de fiscais selecionados do curso.
	 * @throws DAOException
	 */
	private ArrayList<Fiscal> selecionaPorCurso(List<ResumoProcessamentoSelecao> resumoProcessamento, Municipio municipio, String curso, int numFiscais,
			boolean reserva, Movimento mov, Map<String, List<InscricaoFiscal>> listaFiscalPorCurso) throws DAOException {
		ArrayList<Fiscal> fiscais = new ArrayList<Fiscal>();
		Double iraMinimo = null;
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);
		int indiceSelecao = ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO);
		// ordena os alunos por ira ou média geral
		Collections.sort(listaFiscalPorCurso.get(curso), new Comparator<InscricaoFiscal>() {
			private int indiceSelecao = ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO);
			@Override
			public int compare(InscricaoFiscal o1, InscricaoFiscal o2) {
				int cmp = o1.getDiscente().getNivel() - o2.getDiscente().getNivel();
				if (cmp == 0) {
					if (o1.getDiscente().isGraduacao()) {
						double i1 = o1.getDiscente().getDiscente().getIndice(indiceSelecao).getValor();
						double i2 = o2.getDiscente().getDiscente().getIndice(indiceSelecao).getValor();
						cmp = i1 > i2 ? -1 : 1; 
					} else {
						DiscenteDao dao = null;
						try{
							cmp = (int) (((DiscenteStricto) o1.getDiscente()).getMediaGeral() - ((DiscenteStricto) o2.getDiscente()).getMediaGeral());
						}catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (dao != null) dao.close();
						}
					}
				}
				return cmp;
			}
		});
		// para cada fiscal do curso (ordenado por ira quando recuperado no DAO)
		for (InscricaoFiscal inscricaoFiscal : listaFiscalPorCurso.get(curso)) {
			Fiscal fiscal = new Fiscal(inscricaoFiscal);
			if (fiscal.getDiscente().isLato()) {
				inscricaoFiscal.setObservacao("Discente lato sensu não é selecionado como fiscal");
				break;
			} else if (fiscal.getDiscente().isTecnico()) {
				inscricaoFiscal.setObservacao("Discente de curso técnico não é selecionado como fiscal");
				break;
			}
			if (numFiscais > 0) {
				// ira do último fiscal selecionado
				if (inscricaoFiscal.getDiscente().isGraduacao()) {
					DiscenteGraduacao grad = (DiscenteGraduacao) inscricaoFiscal.getDiscente();
					iraMinimo = grad.getDiscente().getIndice(indiceSelecao).getValor();
				} else if (inscricaoFiscal.getDiscente().isStricto()) {
					DiscenteStricto stricto = (DiscenteStricto) inscricaoFiscal.getDiscente();
					iraMinimo = (double) stricto.getMediaGeral();
				}
				
				fiscal.setReserva(reserva);
				fiscais.add(fiscal);
				numFiscais--;
				// Atualiza o resumo do processamento
				if (reserva)
					incrementaResumo(resumoProcessamento, municipio.getNome() + " - A CONCORRER", curso, 0, 1, iraMinimo);
				else 
					incrementaResumo(resumoProcessamento, municipio.getNome() + " - A CONCORRER", curso, 1, 0, iraMinimo);
			} else {
				// atingiu o número de fiscais a selecionar mas deu empate
				// no ira, seleciona também
				if (iraMinimo == null) iraMinimo = 0.0;
				if (fiscal.getDiscente().isGraduacao()) {
					DiscenteGraduacao grad = (DiscenteGraduacao) inscricaoFiscal.getDiscente();
					if (grad.getDiscente().getIndice(indiceSelecao).getValor() == iraMinimo.doubleValue()) {
						fiscal.setReserva(reserva);
						fiscais.add(fiscal);
						// Atualiza o resumo do processamento
						if (reserva)
							incrementaResumo(resumoProcessamento, municipio.getNome() + " - A CONCORRER", curso, 0, 1, iraMinimo);
						else 
							incrementaResumo(resumoProcessamento, municipio.getNome() + " - A CONCORRER", curso, 1, 0, iraMinimo);
					}
				} else if (fiscal.getDiscente().isStricto()) {
					DiscenteStricto stricto = (DiscenteStricto) inscricaoFiscal.getDiscente();
					if (stricto.getMediaGeral() == iraMinimo.doubleValue()) {
						fiscal.setReserva(reserva);
						fiscais.add(fiscal);
						// Atualiza o resumo do processamento
						if (reserva)
							incrementaResumo(resumoProcessamento, municipio.getNome() + " - A CONCORRER", curso, 0, 1, iraMinimo);
						else 
							incrementaResumo(resumoProcessamento, municipio.getNome() + " - A CONCORRER", curso, 1, 0, iraMinimo);
					}
					
				} else {
					break;
				}
			}
		}
		discenteDao.close();
		// remove da lista de fiscais por curso, os fiscais selecionados, a fim
		// de evitar que ocorra seleção da mesma pessoa como fiscal titular e
		// substituto
		for (Fiscal novo : fiscais) {
			listaFiscalPorCurso.get(curso).remove(novo.getInscricaoFiscal());
		}
		return fiscais;
	}

	/**
	 * Seleciona automaticamente Discentes antigos (RECADASTRO) de um
	 * determinado município.
	 * 
	 * @param municipio
	 *            Município ao qual a seleção se restringe
	 * @param quantidade
	 *            Quantidade máxima de fiscais a selecionar.
	 * @param mov 
	 * @return Lista contendo Fiscais selecionados
	 * @throws DAOException
	 */
	private ArrayList<Fiscal> selecionaFiscaisAntigos(List<ResumoProcessamentoSelecao> resumoProcessamento, Municipio municipio,
			int quantidade, Movimento mov) throws DAOException {
		InscricaoFiscalDao inscricaoFiscalDao = getDAO(InscricaoFiscalDao.class, mov);
		ProcessoSeletivoVestibular processoSeletivo = ((MovimentoSelecaoFiscal) mov).getProcessoSeletivoVestibular();
		ArrayList<Fiscal> fiscais = new ArrayList<Fiscal>();
		Map<String, List<InscricaoFiscal>> inscricaoAntigos;
		if (quantidade > 0) {
			inscricaoAntigos = inscricaoFiscalDao.findAllInscricoesFiscaisRecadastro(
					processoSeletivo.getId(), municipio.getId());
			atualizaResumoInscritos(resumoProcessamento, municipio.getNome()  +" - RECADASTRO", inscricaoAntigos);
			// percorre a lista de fiscais antigos
			for (String chave : inscricaoAntigos.keySet()) {
				List<InscricaoFiscal> inscricoes = inscricaoAntigos.get(chave);
				for (InscricaoFiscal inscricaoFiscal : inscricoes) {
					Fiscal fiscal = new Fiscal(inscricaoFiscal);
					fiscal.setReserva(false);
					fiscais.add(fiscal);
					incrementaResumo(resumoProcessamento, municipio.getNome() + " - RECADASTRO", chave, 1, 0, null);
					// se atingiu o número de fiscais, encerra o loop
					if (fiscais.size() >= quantidade)
						break;
				}
				if (fiscais.size() >= quantidade)
					break;
			}
		}
		inscricaoFiscalDao.close();
		return fiscais;
	}
	
	/**
	 * Seleciona automaticamente Discentes residentes de um
	 * determinado município.
	 * 
	 * @param municipio
	 *            Município ao qual a seleção se restringe
	 * @param quantidade
	 *            Quantidade máxima de fiscais a selecionar.
	 * @param mov 
	 * @return Lista contendo Fiscais selecionados
	 * @throws DAOException
	 */
	private ArrayList<Fiscal> selecionaFiscaisResidentes(List<ResumoProcessamentoSelecao> resumoProcessamento, Municipio municipio,
			int quantidade, Movimento mov) throws DAOException {
		InscricaoFiscalDao inscricaoFiscalDao = getDAO(InscricaoFiscalDao.class, mov);
		ProcessoSeletivoVestibular processoSeletivo = ((MovimentoSelecaoFiscal) mov).getProcessoSeletivoVestibular();
		ArrayList<Fiscal> fiscais = new ArrayList<Fiscal>();
		Collection<InscricaoFiscal> inscricaoResidentes = new ArrayList<InscricaoFiscal>();
		if (quantidade > 0) {
			inscricaoResidentes.addAll(inscricaoFiscalDao
				.findDiscentesByTipoBolsaAuxilio(
						TipoBolsaAuxilio.RESIDENCIA_GRADUACAO,
						processoSeletivo.getId(), municipio.getId()));
			inscricaoResidentes.addAll(inscricaoFiscalDao
				.findDiscentesByTipoBolsaAuxilio(
						TipoBolsaAuxilio.RESIDENCIA_POS,
						processoSeletivo.getId(), municipio.getId()));
			// seta a quantidade de inscritos
			ResumoProcessamentoSelecao resumo = new ResumoProcessamentoSelecao();
			resumo.setGrupoSelecao(municipio.getNome() + " - AUTOMÁTICO");
			resumo.setSubgrupoSelecao("RESIDENTE");
			int index = resumoProcessamento.indexOf(resumo);
			if (index > -1)
				resumo = resumoProcessamento.get(index);
			else 
				resumoProcessamento.add(resumo);
			resumo.setInscritos(inscricaoResidentes.size());
			// percorre a lista de residentes
			for (InscricaoFiscal inscricaoFiscal : inscricaoResidentes) {
				// seleciona o fiscal
				Fiscal fiscal = new Fiscal(inscricaoFiscal);
				fiscal.setReserva(false);
				fiscais.add(fiscal);
				// Atualiza o resumo do processamento
				incrementaResumo(resumoProcessamento, municipio.getNome()+ " - AUTOMÁTICO", "RESIDENTE", 1, 0, null);
				// se atingiu o número de fiscais, encerra o loop
				if (fiscais.size() >= quantidade)
					break;
			}
		}
		inscricaoFiscalDao.close();
		return fiscais;
	}

	/**
	 * Seleciona automaticamente Servidores de um determinado município.
	 * 
	 * @param municipio
	 *            Município ao qual a seleção se restringe
	 * @param quantidade
	 *            Quantidade máxima de fiscais a selecionar.
	 * @param mov 
	 * @param processoSeletivo 
	 * @return Lista contendo Fiscais selecionados
	 * @throws DAOException
	 */
	private ArrayList<Fiscal> selecionaServidores(List<ResumoProcessamentoSelecao> resumoProcessamento, Municipio municipio, int quantidade, Movimento mov) throws DAOException {
		InscricaoFiscalDao inscricaoFiscalDao = getDAO(InscricaoFiscalDao.class, mov);
		ProcessoSeletivoVestibular processoSeletivo = ((MovimentoSelecaoFiscal) mov).getProcessoSeletivoVestibular();
		ArrayList<Fiscal> fiscais = new ArrayList<Fiscal>();
		Collection<InscricaoFiscal> servidoresInscritos;
		if (quantidade > 0) {
			servidoresInscritos = inscricaoFiscalDao
					.findServidoresByProcessoSeletivoMunicipio(processoSeletivo
							.getId(), municipio.getId());
			// seta a quantidade de inscritos
			ResumoProcessamentoSelecao resumo = new ResumoProcessamentoSelecao();
			resumo.setGrupoSelecao(municipio.getNome() + " - AUTOMÁTICO");
			resumo.setSubgrupoSelecao("SERVIDOR");
			int index = resumoProcessamento.indexOf(resumo);
			if (index > -1)
				resumo = resumoProcessamento.get(index);
			else 
				resumoProcessamento.add(resumo);
			resumo.setInscritos(servidoresInscritos.size());
			// percorre a lista de fiscais servidores
			ServidorDao servidorDao = getDAO(ServidorDao.class, mov);
			for (InscricaoFiscal inscricaoFiscal : servidoresInscritos) {
				// seleciona o servidor se tiver admissão > 1 ano
				if (inscricaoFiscal.getServidor().getDataAdmissao() == null) {
					inscricaoFiscal.setObservacao("Não consta data de admissão do servidor");
					inscricaoFiscalDao.updateNoFlush(inscricaoFiscal);
				} else if (CalendarUtils.diferencaDias(inscricaoFiscal
						.getServidor().getDataAdmissao(), new Date()) < 365) {
					// verifica se há outro servidor associado à pessoa
					boolean outroVinculoAntigo = false;
					for (Servidor servidorAntigo : servidorDao.findByPessoaAndVinculos(inscricaoFiscal.getPessoa().getId(), Ativo.APOSENTADO, Ativo.EXCLUIDO)) {
						if (CalendarUtils.diferencaDias(servidorAntigo.getDataAdmissao(), new Date()) < 365) {
							outroVinculoAntigo = true;
							break;
						}
					}
					if (!outroVinculoAntigo) {
						inscricaoFiscal.setObservacao("Servidor com menos de um ano de admissão");
						inscricaoFiscalDao.updateNoFlush(inscricaoFiscal);
					}
				} else {
					Fiscal fiscal = new Fiscal(inscricaoFiscal);
					fiscal.setReserva(false);
					fiscais.add(fiscal);
				}
				// se atingiu ao número de fiscais, encerra o loop
				if (fiscais.size() >= quantidade)
					break;
			}
			servidorDao.close();
		}
		inscricaoFiscalDao.close();
		// Atualiza o resumo do processamento
		incrementaResumo(resumoProcessamento, municipio.getNome() + " - AUTOMÁTICO", "SERVIDOR", fiscais.size(), 0, null);
		return fiscais;
	}
	
	/** Atualiza o resumo do processamento, incrementando o número de titulares e reservas, dado os valores informados.
	 * @param municipio Município processado
	 * @param curso Curso de origem do fiscal. O curso será "SERVIDOR", caso o fiscal seja Servidor.
	 * @param incrementoTitulares valor a ser adicionado ao total de titulares selecionados.
	 * @param incrementoReservas valor a ser adicionado ao total de reservas selecionados.
	 */
	private void incrementaResumo(List<ResumoProcessamentoSelecao> resumoProcessamento, String municipio, String curso, int incrementoTitulares, int incrementoReservas, Double ira){
		// Quantidades e mapas iniciais, caso null
		ResumoProcessamentoSelecao resumo = new ResumoProcessamentoSelecao();
		resumo.setGrupoSelecao(municipio);
		resumo.setSubgrupoSelecao(curso);
		int index = resumoProcessamento.indexOf(resumo);
		if (index > -1)
			resumo = resumoProcessamento.get(index);
		else 
			resumoProcessamento.add(resumo);
		// incrementa a quantidade no resumo
		resumo.incrementaTitulares(incrementoTitulares);
		resumo.incrementaReservas(incrementoReservas);
		resumo.setIraMaximo(ira);
		resumo.setIraMinimo(ira);
	}

	/** Valida o movimento, checando se possui o papel {@link SigaaPapeis#VESTIBULAR}.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.VESTIBULAR, mov);
		Collection<InscricaoFiscal> lista = getDAO(InscricaoFiscalDao.class, mov).findInscricoesComPerfilDubio();
		if (lista == null || !lista.isEmpty())
			throw new NegocioException("Existe inscrições de fiscais com problemas de perfil (discente/servidor).");
	}
	
	/** Atualiza o número de inscritos no resumo do processamento da seleção.
	 * @param grupo
	 * @param lista
	 */
	private void atualizaResumoInscritos(List<ResumoProcessamentoSelecao> resumoProcessamento, String grupo, Map<String, List<InscricaoFiscal>> lista) {
		for (String chave : lista.keySet()) {
			ResumoProcessamentoSelecao resumo = new ResumoProcessamentoSelecao();
			resumo.setGrupoSelecao(grupo);
			resumo.setSubgrupoSelecao(chave);
			int index = resumoProcessamento.indexOf(resumo);
			if (index > -1)
				resumo = resumoProcessamento.get(index);
			else 
				resumoProcessamento.add(resumo);
			// seta a quantidade de inscritos
			resumo.setInscritos(lista.get(chave).size());
		}
	}
	
}
