/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.jsf;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.integracao.dto.FormacaoAcademicaDTO;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.pesquisa.GrupoPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoTese;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ConstantesProdocente;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoItem;

/**
 * Classe que serve para buscar as atividades passado o grupo e o docente.
 * O método testa cada tipo e busca no AvaliacaoDocenteDao (dao principal para as buscas do prodocente).
 *
 * ESTA CLASSE NÃO DEVE SER MAIS UTILIZADA!
 * Agora este processamento é realizado em RelatorioHelper.processarAtividades() 
 * @author Gleydson
 *
 */
//@Component("buscaAtividadesUtil") @Scope("session")
@Deprecated
public class BuscaAtividadesUtil {

	/** 
	 * ESTE MÉTODO NÃO DEVE SER MAIS UTILIZADO!
	 * Agora este processamento é realizado em RelatorioHelper.processarAtividades() 
	 * 
	 * @throws ArqException 
	 */
	@Deprecated
	public static void buscaAtividades(GrupoItem grupo, Servidor docente,
			int ano, Integer validade, FormacaoAcademicaRemoteService serviceFormacao) throws ArqException {

		int codigo = grupo.getItemRelatorioProdutividade().getNumeroTopico();

		AvaliacaoDocenteDao dao = new AvaliacaoDocenteDao();
		GrupoPesquisaDao grupoPesquisaDao = new GrupoPesquisaDao();
		ProjetoPesquisaDao projetoPesquisaDao = new ProjetoPesquisaDao();
		
		/** Serviço de Formação Escolar que é Invocado no SIGRH */		
		//FormacaoAcademicaRemoteService serviceFormacao = null;
		Collection<FormacaoAcademicaDTO> formacoes = new ArrayList<FormacaoAcademicaDTO>();

		grupo.setAtividade(true);

		try {
			switch (codigo) {

				//item 1
				//TODO não foi feito pois não tem referencia a este numero_topico na tabela
				case ConstantesProdocente.CARGOSDIRECAO_FUNCOESGRATIFICADAS:
				grupo.setAtividades(dao.findCargosDirecaoFuncaoGratificada(
							docente, ano, validade));
				break;

				//item 1.1
				case ConstantesProdocente.ASS_DIR_COORD_UNIDADE_PROG_TIPO_1:
				grupo.setAtividades(dao.findCargosDirecaoFuncaoGratificada(
							docente, ano, validade));
				break;

//				//item 1.2
//				case ConstantesProdocente.ASS_DIR_MUSEUS_COORD_UNIDADE_PROG_TIPO_2:
//				grupo.setAtividades(dao.findCargosDirecaoFuncaoGratificada(
//							docente, ano));
//				break;
//
//				//item 1.3
//				case ConstantesProdocente.MEMBRO_COMISSAO_PERMANENTE:
//				grupo.setAtividades(dao.findCargosDirecaoFuncaoGratificada(
//							docente, ano));
//				break;


				//item 2.1
				case ConstantesProdocente.GRADUACAO:
					grupo.setAtividades(dao.findAtividadeEnsinoGraduacaoDocente(
							docente, ano, validade));
					break;

				//item 2.2
				case ConstantesProdocente.POS_GRADUACAO:
					grupo.setAtividades(dao.findAtividadeEnsinoPosDocente(docente, ano, validade));
					break;

				//item 2.3
				case ConstantesProdocente.ORIENT_ESTAG_SUPERVISIONADO_CORRELATOS:
					grupo.setAtividades(dao.findOrientacoesGraduacaoEstagioDocente(
							docente, ano, validade));
					break;

				//item 2.4
				case ConstantesProdocente.ORIENT_TRAB_PROJ_FINAL_CURSO_CONCLUIDO:
					grupo.setAtividades(dao.findOrientacoesGraduacaoTrabalhoFimCursoConcluidas(
							docente, ano, validade));
					grupo.getAtividades().addAll( dao.findOrientacoesMonografiaGraduacaoConcluidas(docente, ano, validade) );
				break;

				//item 2.5
				case ConstantesProdocente.ORIENT_ESPECIALIZACAO:
					grupo.setAtividades(dao.findOrientacoesPosEspecializacaoDocente(
							docente, ano, validade));
					break;

				//item 2.6
				case ConstantesProdocente.ORIENT_MESTRADO_UFRN_OUTRA_IFES:
					grupo.setAtividades(dao.findOrientacoesPosMestradoDocente(
							docente, ano));
					break;

				//item 2.7
				case ConstantesProdocente.ORIENT_DOUTORADO_UFRN_OUTRA_IFES:
					grupo.setAtividades(dao.findOrientacoesPosDoutoradoDocente(
							docente, ano));
					break;

				//item 2.8
				case ConstantesProdocente.ORIENT_TESE_MESTRADO_CONCLUIDO_UFRN_OUTRAIFES:
					grupo.setAtividades(dao.findOrientacoesPosMestradoConcluidoDocente(
							docente, ano, validade));
					break;

				//item 2.9
				case ConstantesProdocente.ORIENT_TESE_DOUTORADO_CONCLUIDO_UFRN_OUTRAIFES:
					grupo.setAtividades(dao.findOrientacoesPosDoutoradoConcluidoDocente(
							docente, ano, validade));
					break;
					
				//item 2.10
				case ConstantesProdocente.ORIENT_POS_DOUTORADO_UFRN_OUTRA_IFES:
					grupo.setAtividades(dao.findOrientacoesPosDocDocente(docente, ano));
					break;

				//item 3.1
				case ConstantesProdocente.TESE_DOUTORADO_DEF_DOCENTE_APROVACAO:
					//serviceFormacao = getMBean("formacaoAcademicaInvoker");
					formacoes = serviceFormacao.consultarFormacaoAcademica(docente.getId(), null, null, null, 
							null,	CalendarUtils.createDate(01, 01, ( ano - validade )), CalendarUtils.createDate(31, 12, ano), Formacao.DOUTOR);					
					grupo.setAtividades(formacoes);
					break;

				//item 3.2
				case ConstantesProdocente.TESE_MESTRADO_DEF_APROVACAO:
					//serviceFormacao = getMBean("formacaoAcademicaInvoker");
					formacoes = serviceFormacao.consultarFormacaoAcademica(docente.getId(), null, null, null, 
							null, CalendarUtils.createDate(01, 01, ( ano - validade )), CalendarUtils.createDate(31, 12, ano), Formacao.MESTRE);					
					grupo.setAtividades(formacoes);
					break;

				//item 3.3
				case ConstantesProdocente.MONOGRAFIA_ESPECIALIZACAO_DEF_APROVACAO:
					//serviceFormacao = getMBean("formacaoAcademicaInvoker");
					formacoes = serviceFormacao.consultarFormacaoAcademica(docente.getId(), null, null, null, 
							null,	CalendarUtils.createDate(01, 01, ( ano - validade )), CalendarUtils.createDate(31, 12, ano), Formacao.ESPECIALISTA);					
					grupo.setAtividades(formacoes);
					break;

				//item 3.20
				case ConstantesProdocente.REL_FINAL_PESQ_APROV_INSTANCIA_COMPETENTE_UFRN:
					grupo.setAtividades(dao.findRelatorioFinalPesquisa(
							docente, ano, validade));
					grupo.getAtividades().addAll(dao.findProjetosAntigosRelatorioDocente(
							docente, ano, validade, false));
					break;

				//item 4.1
				case ConstantesProdocente.REL_PARCIAL_FINAL_ATIV_INTERNACIONAL_EXT_APROVADO:
					grupo.setAtividades(dao.findRelatorioAtividadeInternacional1(
							docente, ano, validade));
					grupo.getAtividades().addAll(dao.findRelatorioAtividadeInternacional2(
							docente, ano, validade));
					break;


				//item 4.2
				case ConstantesProdocente.REL_PARCIAL_FINAL_ATIV_NACIONAL_EXTENSAO_APROVADO:
					grupo.setAtividades(dao.findRelatorioAtividadeNacional1(
							docente, ano, validade));
					grupo.getAtividades().addAll(dao.findRelatorioAtividadeNacional2(
							docente, ano, validade));
					break;

				//item 4.3
				case ConstantesProdocente.REL_PARCIAL_FINAL_ATIV_REGIONAL_LOCAL_EXTENSAO_APROVADO:
					grupo.setAtividades(dao.findRelatorioAtividadeRegionalLocal1(
							docente, ano, validade));
					grupo.getAtividades().addAll(dao.findRelatorioAtividadeRegionalLocal2(
							docente, ano, validade));
					break;

				//item 4.4
				case ConstantesProdocente.ATIV_CURSOS_EXTENSAO:
					grupo.setAtividades(dao.findRelatorioAtividadeCurso1(
							docente, ano, validade));
					grupo.getAtividades().addAll(dao.findRelatorioAtividadeCurso2(
							docente, ano, validade));
					break;

				//item 4.5
				case ConstantesProdocente.ATIV_MINICURSO_CONSULT_PERICIA_SINDICANCIA:
					grupo.setAtividades(dao.findRelatorioAtividadeAssessoria1(
							docente, ano, validade));
					break;

				//item 4.6
				case ConstantesProdocente.ATIV_ATENDIMENTO_PACIENTE_HOSPITAL:
					grupo.setAtividades(dao.findRelatorioAtividadeAtendimento1(
							docente, ano, validade));
					grupo.getAtividades().addAll(dao.findRelatorioAtividadeAtendimento2(
							docente, ano, validade));
					break;

				//item 4.7
				case ConstantesProdocente.MINICURSO_EVENTO_CIENT_CULT_DESPORT:
					grupo.setAtividades(dao.findRelatorioMiniCursoEvento(
							docente, ano, validade));
					break;

				//item 4.8
				case ConstantesProdocente.PATENTE_PRODUTO_REGISTRADO:
					grupo.setAtividades(dao.findRelatorioPatenteProduto(
							docente, ano, validade));
					break;

				//item 4.9
				case ConstantesProdocente.OBRA_ART_CULT_TEC_CIENT_PREMIADA_INTERNACIONAL:
					grupo.setAtividades(dao.findRelatorioObraArtisticaInternacional(
							docente, ano, validade));
					break;

				//item 4.10
				case ConstantesProdocente.OBRA_ART_CULT_TEC_CIENT_PREMIADA_NACIONAL:
					grupo.setAtividades(dao.findRelatorioObraArtisticaNacional(
							docente, ano, validade));
					break;

				//item 4.11
				case ConstantesProdocente.OBRA_ART_CULT_TEC_CIENT_PREMIADA_REGIONAL_LOCAL:
					grupo.setAtividades(dao.findRelatorioObraArtisticaRegionalLocal(
							docente, ano, validade));
					break;

				//item 4.12
				case ConstantesProdocente.PART_EVENTOS_CIENT_DESPORT_ART_INTERNACIONAL_COORDENADOR:
					grupo.setAtividades(dao.findRelatorioParticipacaoEventoInternacionalCoordenador(
							docente, ano, validade));
					break;

				//item 4.13
				case ConstantesProdocente.PART_EVENTOS_CIENT_DESPORT_ART_NACIONAL_COORDENADOR:
					grupo.setAtividades(dao.findRelatorioParticipacaoEventoNacionalCoordenador(
							docente, ano, validade));
					break;

				//item 4.14
				case ConstantesProdocente.PART_EVENTOS_CIENT_DESPORT_ART_REGIONAL_COORDENADOR:
					grupo.setAtividades(dao.findRelatorioParticipacaoEventoRegionalCoordenador(
							docente, ano, validade));
					break;

				//item 4.15
				case ConstantesProdocente.PART_EVENTOS_CIENT_DESPORT_ART_LOCAL_COORDENADOR:
					grupo.setAtividades(dao.findRelatorioParticipacaoEventoLocalCoordenador(
							docente, ano, validade));
					break;

				//item 4.16
				case ConstantesProdocente.PART_EVENTOS_CIENT_DESPORT_ART_INTERNACIONAL_COMISSAO_ORGANIZADORA:
					grupo.setAtividades(dao.findRelatorioParticipacaoEventoInternacionalMembro(
							docente, ano, validade));
					break;

				//item 4.17
				case ConstantesProdocente.PART_EVENTOS_CIENT_DESPORT_ART_NACIONAL_COMISSAO_ORGANIZADORA:
					grupo.setAtividades(dao.findRelatorioParticipacaoEventoNacionalMembro(
							docente, ano, validade));
					break;

				//item 4.18
				case ConstantesProdocente.PART_EVENTOS_CIENT_DESPORT_ART_REGIONAL_LOCAL_COMISSAO_ORGANIZADORA:
					grupo.setAtividades(dao.findRelatorioParticipacaoEventoRegionalMembro(
							docente, ano, validade));
					break;

				//item 4.19
				case ConstantesProdocente.PART_VISITA_MISSAO_INTERNACIONAL:
					grupo.setAtividades(dao.findRelatorioParticipacaoVisitaMissaoInternacional(
							docente, ano, validade));
					break;

				//item 4.20
				case ConstantesProdocente.PART_EVENTOS_CIENT_DESPORT_ART_INTERNACIONAL_CONFERENCISTA_CONVIDADO:
					grupo.setAtividades(dao.findRelatorioParticipacaoEventoInternacionalConvidado(
							docente, ano, validade));
					break;

				//item 4.21
				case ConstantesProdocente.PART_EVENTOS_CIENT_DESPORT_ART_NACIONAL_CONFERENCISTA_CONVIDADO:
					grupo.setAtividades(dao.findRelatorioParticipacaoEventoNacionalConvidado(
						docente, ano, validade));
					break;

				//item 4.22
				case ConstantesProdocente.PART_EVENTOS_CIENT_DESPORT_ART_REGIONAL_CONFERENCISTA_CONVIDADO:
					grupo.setAtividades(dao.findRelatorioParticipacaoEventoRegionalConvidado(
						docente, ano, validade));
					break;


				//item 4.23
				case ConstantesProdocente.PART_EVENTOS_CIENT_DESPORT_ART_LOCAL_CONFERENCISTA_CONVIDADO:
					grupo.setAtividades(dao.findRelatorioParticipacaoEventoLocalConvidado(
						docente, ano, validade));
					break;



				//item 5.1
				case ConstantesProdocente.CURSO_MESTRADO_DOUTORADO_POSDOUTORADO:
					//serviceFormacao = getMBean("formacaoAcademicaInvoker");
					formacoes = serviceFormacao.consultarFormacaoAcademica(docente.getId(), null, null, null, 							
							null, CalendarUtils.createDate(01, 01, ( ano - validade )), CalendarUtils.createDate(31, 12, ano),
							Formacao.ESPECIALISTA,Formacao.DOUTOR,FormacaoTese.POS_DOUTORADO);					
					grupo.setAtividades(formacoes);
					break;

				//item 5.2
				case ConstantesProdocente.AVAL_DESEMPENHO_ORIENTADOR:
					grupo.setAtividades(dao.findRelatorioAvaliacaoDesempenhoOrientador(
						docente, ano, validade));
					break;

				//item 5.3
				case ConstantesProdocente.TITULO_ESPECIALISTA_OBTIDO:
					//serviceFormacao = getMBean("formacaoAcademicaInvoker");
					formacoes = serviceFormacao.consultarFormacaoAcademica(docente.getId(), null, null, null, 
							null, CalendarUtils.createDate(01, 01, ( ano - validade )), CalendarUtils.createDate(31, 12, ano), Formacao.ESPECIALISTA);					
					grupo.setAtividades(formacoes);
					break;


				//item 6.1
				case ConstantesProdocente.VICE_CHEFE_DEPARTAMENTO:
					grupo.setAtividades(dao.findRelatorioViceChefeDepto(
						docente, ano, validade));
					break;

				//item 6.2
				case ConstantesProdocente.VICE_COORD_CURSO_GRADUACAO_POS:
					grupo.setAtividades(dao.findRelatorioViceCoordenadorCurso(
						docente, ano, validade));
					break;

				//item 6.3
				case ConstantesProdocente.PART_COLEGIADO:
					grupo.setAtividades(dao.findRelatorioParticipacaoColegiado(
						docente, ano, validade));
					break;

				//item 6.4
				case ConstantesProdocente.PART_COMISSAO_NOVOS_CURSOS_REFORMULACAO_PEDAG:
					grupo.setAtividades(dao.findRelatorioParticipacaoComissaoNovosCursos(
						docente, ano, validade));
					break;

				//item 6.5
				case ConstantesProdocente.PART_COMISSAO_PERMANENTE:
					grupo.setAtividades(dao.findRelatorioParticipacaoComissaoPermanente(
						docente, ano, validade));
					break;

				//item 6.6
				case ConstantesProdocente.PART_COMISSAO_TEMPORARIA:
					grupo.setAtividades(dao.findRelatorioParticipacaoComissaoTemporarias(
						docente, ano, validade));
					break;

				//item 6.7
				case ConstantesProdocente.PART_COMISSAO_SINDICANCIA_PROC_DISCIPLINAR:
					grupo.setAtividades(dao.findRelatorioParticipacaoComissaoSindicancia(
						docente, ano, validade));
					break;

				//item 6.8
				case ConstantesProdocente.CHEFIA_COORD_SETOR_ACADEMICO_APOIO:
					grupo.setAtividades(dao.findRelatorioChefiaCoordenacaoSetoresAcademicoApoio(
						docente, ano, validade));
					break;

				//item 6.9
				case ConstantesProdocente.COORDENACAO_BASE_PESQUISA:
					grupo.setAtividades( grupoPesquisaDao.findByCoordenador(docente, null,0,0));
					break;

				//item 6.10
				case ConstantesProdocente.COORDENACAO_CURSO_LATO_SENSU:
					grupo.setAtividades(dao.findRelatorioChefiaCoordenacaoCursoLato(docente, ano, validade));
					grupo.getAtividades().addAll(dao.findRelatorioCoordenacaoCursoLato(docente, ano, validade));
					break;

				//item 6.11
				case ConstantesProdocente.CONSULT_AD_HOC_REVISTA_INTERNACIONAL:
					grupo.setAtividades(dao.findRelatorioConsultorAdHocRevistaInternacional(
						docente, ano, validade));
					break;

				//item 6.12
				case ConstantesProdocente.CONSULT_AD_HOC_REVISTA_NACIONAL:
					grupo.setAtividades(dao.findRelatorioConsultorAdHocRevistaNacional(
						docente, ano, validade));
					break;

				//item 6.13
				case ConstantesProdocente.CONSULT_AD_HOC_REVISTA_REGIONAL_LOCAL:
					grupo.setAtividades(dao.findRelatorioConsultorAdHocRevistaRegionalLocal(
						docente, ano, validade));
					break;

				//item 6.14
				case ConstantesProdocente.CONSULT_AD_HOC_ANAIS_INTERNACIONAL:
					grupo.setAtividades(dao.findRelatorioConsultorAdHocAnaisInternacional(
						docente, ano, validade));
					break;

				//item 6.15
				case ConstantesProdocente.CONSULT_AD_HOC_ANAIS_NACIONAL_REGIONAL:
					grupo.setAtividades(dao.findRelatorioConsultorAdHocAnaisNacionalRegional(
						docente, ano, validade));
					break;

				//item 6.16
				case ConstantesProdocente.CONSULT_AD_HOC_ORGAOS_FORMENTO:
					grupo.setAtividades(dao.findRelatorioConsultorAdHocOrgaoFormento(
						docente, ano, validade));
					break;

				//item 6.17
				case ConstantesProdocente.REPRES_ACADEMICA_PART_ORGAOS_POLITICAS_PUBLICAS:
					grupo.setAtividades(dao.findRelatorioRepresentacaoAcademica(
						docente, ano, validade));
					break;

				//item 6.18
				case ConstantesProdocente.CONSULT_AD_HOC_COMISSAO_NACIONAL_REFORMA_AVALIACAO_CURSO:
					grupo.setAtividades(dao.findRelatorioConsultorAdHocReformaAvaliacaoCurso(
						docente, ano, validade));
					break;


				//item 7.1
				case ConstantesProdocente.PART_BANCA_CONCURSO_PROF_TITULAR_LIVRE_DOCENCIA:
					grupo.setAtividades(dao.findRelatorioBancaConcursoProfessorTitular(
						docente, ano, validade));
					break;

				//item 7.2
				case ConstantesProdocente.PART_BANCA_CONCURSO_PROF_ADJUNTO_ASSISTENTE_AUXILIAR:
					grupo.setAtividades(dao.findRelatorioBancaConcursoProfessorAdjAssAux(
						docente, ano, validade));
					break;

				//item 7.3
				case ConstantesProdocente.PART_BANCA_CONCURSO_PROF_SUBSTITUTO:
					grupo.setAtividades(dao.findRelatorioBancaConcursoProfessorSubstituto(
						docente, ano, validade));
					break;

				//item 7.4
				case ConstantesProdocente.PART_BANCA_CONCURSO_PROF_NIVEL_MEDIO:
					grupo.setAtividades(dao.findRelatorioBancaConcursoProfessorMedio(
						docente, ano, validade));
					break;

				//item 7.5
				case ConstantesProdocente.PART_BANCA_CONCURSO_TECNICO:
					grupo.setAtividades(dao.findRelatorioBancaConcursoTecnico(
						docente, ano, validade));
					break;

				//item 7.6
				case ConstantesProdocente.PART_BANCA_EXAME_TESE_DOUTORADO:
					grupo.setAtividades(dao.findRelatorioBancaTeseDoutorado(
						docente, ano, validade));
					break;

				//item 7.7
				case ConstantesProdocente.PART_BANCA_EXAME_TESE_MESTRADO:
					grupo.setAtividades(dao.findRelatorioBancaTeseMestrado(
						docente, ano, validade));
					break;

				//item 7.8
				case ConstantesProdocente.PART_BANCA_EXAME_QUALIF_DOUTORADO:
					grupo.setAtividades(dao.findRelatorioBancaQualificacaoDoutorado(
						docente, ano, validade));
					break;

				//item 7.9
				case ConstantesProdocente.PART_BANCA_EXAME_QUALIF_MESTRADO:
					grupo.setAtividades(dao.findRelatorioBancaQualificacaoMestrado(
						docente, ano, validade));
					break;

				//item 7.10
				case ConstantesProdocente.PART_BANCA_EXAME_MONOG_ESPECIALIZACAO:
					grupo.setAtividades(dao.findRelatorioBancaMonografiaEspecializacao(
						docente, ano, validade));
					break;

				//item 7.11
				case ConstantesProdocente.PART_BANCA_EXAME_SELECAO_DOUTORADO:
					grupo.setAtividades(dao.findRelatorioBancaSelecaoDoutorado(
						docente, ano, validade));
					break;

				//item 7.12
				case ConstantesProdocente.PART_BANCA_EXAME_SELECAO_MESTRADO:
					grupo.setAtividades(dao.findRelatorioBancaSelecaoMestrado(
						docente, ano, validade));
					break;

				//item 7.13
				case ConstantesProdocente.PART_BANCA_EXAME_MONOG_GRADUACAO:
					grupo.setAtividades(dao.findRelatorioBancaMonografiaGraduacao(
						docente, ano, validade));
					break;

				//item 7.14
				case ConstantesProdocente.PART_BANCA_EXAME_SELECAO_ESPECIALIZACAO:
					grupo.setAtividades(dao.findRelatorioBancaSelecaoEspecializacao(
						docente, ano, validade));
					break;

				//item 7.15
				case ConstantesProdocente.DISC_CURSADA_LATO_SENSU_SEM_AFASTAMENTO:
					grupo.setAtividades(dao.findRelatorioDisciplinaCursadaLato(
						docente, ano, validade));
					break;

				//item 7.16
				case ConstantesProdocente.ORIENTADOR_ACADEMICO:
					grupo.setAtividades(dao.findRelatorioOrientadorAcademico(
						docente, ano, validade));
					break;

				//item 7.17
				case ConstantesProdocente.ORIENTACAO_ALUNOS_GRADUACAO:
					grupo.setAtividades(dao.findRelatorioOrientacaoAlunosGraduacao1(
						docente, ano, validade));
					grupo.getAtividades().addAll(dao.findRelatorioOrientacaoAlunosGraduacao2(
							docente, ano, validade));
					grupo.getAtividades().addAll(dao.findRelatorioOrientacaoAlunosGraduacao3(
							docente, ano, validade));
					grupo.getAtividades().addAll(dao.findRelatorioOrientacaoAlunosGraduacao4(
							docente, ano, validade));
					break;

					//item 8.39
				case ConstantesProdocente.ORIENTACAO_TESE_POS_DOUTORADO:
					grupo.setAtividades(dao.findOrientacoesPosPosDoutoradoConcluidoDocente(
							docente, ano, validade));
					break;

				//item 8.40
				case ConstantesProdocente.ORIENTACAO_ESPECIALIZACAO_CONCLUIDA_NAO_PAGA:
					grupo.setAtividades(dao.findOrientacoesPosEspecializacaoConcluidaDocente(
						docente, ano, validade));
					break;

				//item 8.41
				case ConstantesProdocente.ORIENTACAO_ALUNO_GRADUACAO_INICIACAO_CIENTIFICA:
					grupo.setAtividades(dao.findOrientacaoIniciacaoCientifica(docente, ano, validade));
					grupo.getAtividades().addAll(dao.findOrientacaoIniciacaoCientificaExterna(docente, ano, validade));
					break;

				//item 8.42
				case ConstantesProdocente.ORIENTACAO_ALUNO_GRADUACAO_MONITORIA:
					grupo.setAtividades(dao.findRelatorioOrientacaoAlunosGraduacao3(
						docente, ano, validade));
					break;

				//item 8.49
				case ConstantesProdocente.RELATORIO_FINAL_PESQUISA_APROVADO_COORDENADOR:
					grupo.setAtividades(dao.findRelatorioFinalPesquisaCoordenador(
						docente, ano, validade));
					break;

				//item 8.50
				case ConstantesProdocente.RELATORIO_FINAL_PESQUISA_APROVADO_COLABORADOR:
					grupo.setAtividades(dao.findRelatorioFinalPesquisaColaborador(
						docente, ano, validade));
					grupo.getAtividades().addAll(dao.findProjetosAntigosRelatorioDocente(
							docente, ano, validade, true));
					break;

				// item 8.53
				case ConstantesProdocente.COORDENACAO_PROJETO_PESQUISA_EXTERNO:
					grupo.setAtividades( projetoPesquisaDao.findExternosByMembro(docente, true, ano) );
					break;

				// item 8.54
				case ConstantesProdocente.COLABORACAO_PROJETO_PESQUISA_EXTERNO:
					grupo.setAtividades( projetoPesquisaDao.findExternosByMembro(docente, false, ano) );
					break;

				// item 8.57
				case ConstantesProdocente.CARGA_HORARIA_GRADUACAO_SINTETICO:
					grupo.setAtividades( dao.findCargaHorariaGraduacaoSintetico(docente, ano, validade) );
					break;
					// item 8.58
				case ConstantesProdocente.PARTICIPACAO_COMITE_CIENTIFICO:
					grupo.setAtividades( dao.findParticipacaoComiteCientifico(docente, ano, validade) );
					break;
					// item 8.61
				case ConstantesProdocente.COMPONENTES_CURRICULARES_MINISTRADOS_POS_STRICTO:
					grupo.setAtividades( dao.findComponentesEnsinoPosStricto(docente, ano, validade) );
					break;
			}

		} finally {
			dao.close();
			grupoPesquisaDao.close();
			projetoPesquisaDao.close();
		}

	}


}
