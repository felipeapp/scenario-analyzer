/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '30/01/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.ComponenteCursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CorpoDocenteCursoLatoDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.ParametrosPropostaCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.TipoCursoLato;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaComponenteCursoLato;


/**
 * Classe com as validações do Curso lato sensu. 
 *  
 * @author igor
 */
public class CursoLatoValidator { 

	/**
	 * Realiza a validação básica do Curso Lato 
	 * @throws DAOException 
	 */
	public static void validaDadosBasicos(CursoLato curso, ListaMensagens lista, GenericDAO dao) throws DAOException{
		ValidatorUtil.validateRequired(curso.getNome(), "Nome", lista);
		ValidatorUtil.validateRequired(curso.getUnidade(), "Unidade Responsável", lista);
		ValidatorUtil.validateRequired(curso.getTipoCursoLato(), "Tipo do Curso", lista);
		if ( ValidatorUtil.isEmpty( curso.getPolosCursos()) && curso.getModalidadeEducacao().getId() == ModalidadeEducacao.A_DISTANCIA )
			ValidatorUtil.validateRequired(curso.getPolosEAD(), "Polo", lista);
		if ( curso.getModalidadeEducacao().getId() != ModalidadeEducacao.A_DISTANCIA && !ValidatorUtil.isEmpty( curso.getPolosCursos()) )
			lista.addErro("Apenas Cursos a distância devem apresentar polo.");
			
		ValidatorUtil.validaInt(curso.getCargaHoraria(), "Carga Horária", lista);
		ValidatorUtil.validaInt(curso.getNumeroVagas(), "Número de Vagas", lista);
		ValidatorUtil.validateRequired(curso.getModalidadeEducacao(), "Modalidade Educação", lista);
		
		if (curso.getGrandeAreaConhecimentoCnpq().getId() == 0) 
			ValidatorUtil.validateRequired(null, "Grande Área", lista);
		if (curso.getAreaConhecimentoCnpq().getId() == 0) 
			ValidatorUtil.validateRequired(null, "Área", lista);
		else 
			curso.setAreaConhecimentoCnpq(dao.findByPrimaryKey(
					curso.getAreaConhecimentoCnpq().getId(), AreaConhecimentoCnpq.class));
		
		if (curso.getTipoTrabalhoConclusao().getId() == 0)
			ValidatorUtil.validateRequired(null, "Tipo do Trabalho de Conclusão", lista);
		
		ValidatorUtil.validateRequired(curso.getHabilitacaoEspecifica(), "Habilitação Específica", lista);
		ValidatorUtil.validateRequired(curso.getDataInicio(), "Período Inicial do Curso", lista);
		ValidatorUtil.validateRequired(curso.getDataFim(), "Período Final do Curso", lista);
		ValidatorUtil.validaOrdemTemporalDatas(curso.getDataInicio(), curso.getDataFim(), false, "Período do Curso", lista);
		
		if (curso.getTipoCursoLato().getId() != 0) 
			curso.setTipoCursoLato(dao.findByPrimaryKey(curso.getTipoCursoLato().getId(), TipoCursoLato.class));
		
		if (curso.getCargaHoraria() != null && curso.getTipoCursoLato() != null) {
			if (curso.getTipoCursoLato().getChMinima() != null && curso.getCargaHoraria() < curso.getTipoCursoLato().getChMinima()) {
				lista.addMensagem(MensagensArquitetura.VALOR_MAIOR_IGUAL_A, "Carga Horária", 
						curso.getTipoCursoLato().getChMinima());
			}

			if (curso.getTipoCursoLato().getChMaxima() != null && curso.getCargaHoraria() > curso.getTipoCursoLato().getChMaxima()) {
				lista.addMensagem(MensagensArquitetura.VALOR_MENOR_IGUAL_A, "Carga Horária", 
						curso.getTipoCursoLato().getChMaxima());
			}
		}
		
		if ( isEmpty(curso.getPropostaCurso().getMediaMinimaAprovacao()) )
			curso.getPropostaCurso().setMediaMinimaAprovacao(0f);
		
		Collection<Curso> cursos = dao.findByExactField(Curso.class, "nomeAscii", StringUtils.toAscii(curso.getNome()));
		if ( cursos.size() > 0 ) {
			
		}
		
	}

	/**
	 * Valida a coordenação do curso.
	 * 
	 * @param cForm
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaCoordenacaoCurso(CursoLato curso, ListaMensagens lista, GenericDAO dao) throws DAOException{
		dao.clearSession();
		
		if (curso.getCoordenador().getServidor().getPessoa().getNome().equals("")) 
			ValidatorUtil.validateRequired(null, "Coordenador", lista);	
		
		ValidatorUtil.validateRequired(curso.getCoordenador().getEmailContato(), "Email de Contato do Coordenador", lista);
		ValidatorUtil.validateEmail(curso.getCoordenador().getEmailContato(), "Email de Contato do Coordenador", lista);
		
		if(!curso.getCoordenador().getTelefoneContato1().equals("") && 
				curso.getCoordenador().getTelefoneContato1().matches("^[a-zA-ZÁÂÃÀÇÉÊÍÓÔÕÚÜáâãàçéêíóôõúü]*$"))
			lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Telefone de Contato do Coordenador");
		else
			ValidatorUtil.validateRequired(curso.getCoordenador().getTelefoneContato1(), "Telefone de Contato do Coordenador", lista);
		
		
		ValidatorUtil.validateRequired(curso.getCoordenador().getDataInicioMandato(), "Data de Inicio do Mandato do Coordenador", lista);
		ValidatorUtil.validateRequired(curso.getCoordenador().getDataFimMandato(), "Data de Fim do Mandato do Coordenador", lista);

		if (curso.getViceCoordenador().getServidor().getPessoa().getNome().equals("")) 
			ValidatorUtil.validateRequired(null, "Vice-Coordenador", lista);	
		
		ValidatorUtil.validateRequired(curso.getViceCoordenador().getEmailContato(), "Email de Contato do Vice-Coordenador", lista);
		ValidatorUtil.validateEmail(curso.getViceCoordenador().getEmailContato(), "Email de Contato do Vice-Coordenador", lista);
		
		if(!curso.getViceCoordenador().getTelefoneContato1().equals("") && 
				curso.getViceCoordenador().getTelefoneContato1().matches("^[a-zA-ZÁÂÃÀÇÉÊÍÓÔÕÚÜáâãàçéêíóôõúü]*$"))
			lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Telefone de Contato do Vice-Coordenador");
		else
			ValidatorUtil.validateRequired(curso.getViceCoordenador().getTelefoneContato1(), "Telefone de Contato do Vice-Coordenador", lista);

		ValidatorUtil.validateRequired(curso.getViceCoordenador().getDataInicioMandato(), "Data de Inicio do Mandato do Vice-Coordenador", lista);
		ValidatorUtil.validateRequired(curso.getViceCoordenador().getDataFimMandato(), "Data de Fim do Mandato do Vice-Coordenador", lista);

		if (curso.getSecretario().getUsuario().getPessoa().getNome() == null)
			curso.getSecretario().setUsuario(dao.findAndFetch(curso.getSecretario().getUsuario().getId(), Usuario.class, "pessoaSigaa"));	
		
//		ValidatorUtil.validateRequired(curso.getSecretario().getUsuario(), "Secretário(a)", lista);
		if (curso.getSecretario().getUsuario() == null) 
			curso.getSecretario().setUsuario(new Usuario());
		
		if (lista.isEmpty()) {
		
			if (curso.getCoordenador().getDataInicioMandato() != null && curso.getCoordenador().getDataFimMandato() != null) 
				ValidatorUtil.validaOrdemTemporalDatas(curso.getCoordenador().getDataInicioMandato(), 
						curso.getCoordenador().getDataFimMandato(), false, "Data de Inicio Mandato do Coordenador", lista);
	
			if (curso.getPropostaCurso().getSituacaoProposta().getId() != SituacaoProposta.ACEITA && curso.getDataInicio().after(curso.getCoordenador().getDataInicioMandato())) 
				lista.addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Data de Inicio do Mandato do Coordenador", CalendarUtils.format(curso.getDataInicio(), "dd/MM/yyyy"));
			
			if (curso.getDataInicio().after(curso.getCoordenador().getDataFimMandato())) 
				lista.addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Data de Inicio do Mandato do Coordenador", CalendarUtils.format(curso.getDataInicio(), "dd/MM/yyyy"));
	
			if (curso.getDataFim().before(curso.getCoordenador().getDataInicioMandato())) 
				lista.addMensagem(MensagensArquitetura.DATA_ANTERIOR_A, "Data de Fim do Mandato do Coordenador", CalendarUtils.format(curso.getDataFim(), "dd/MM/yyyy"));
			
			if (curso.getDataFim().before(curso.getCoordenador().getDataFimMandato())) 
				lista.addMensagem(MensagensArquitetura.DATA_ANTERIOR_A, "Data de Fim do Mandato do Coordenador", CalendarUtils.format(curso.getDataFim(), "dd/MM/yyyy"));
			
			if (curso.getViceCoordenador().getDataInicioMandato() != null && curso.getViceCoordenador().getDataFimMandato() != null)
				ValidatorUtil.validaOrdemTemporalDatas(curso.getViceCoordenador().getDataInicioMandato(), 
						curso.getViceCoordenador().getDataFimMandato(), false, "Data de Inicio do Mandato do Vice-Coordenador", lista);
			
			if (curso.getPropostaCurso().getSituacaoProposta().getId() != SituacaoProposta.ACEITA && curso.getDataInicio().after(curso.getViceCoordenador().getDataInicioMandato())) 
				lista.addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Data de Inicio do Mandato do Vice-Coordenador", CalendarUtils.format(curso.getDataInicio(), "dd/MM/yyyy"));
			
			if (curso.getDataInicio().after(curso.getViceCoordenador().getDataFimMandato())) 
				lista.addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Data de Inicio do Mandato do Vice-Coordenador", CalendarUtils.format(curso.getDataInicio(), "dd/MM/yyyy"));
	
			if (curso.getDataFim().before(curso.getViceCoordenador().getDataInicioMandato())) 
				lista.addMensagem(MensagensArquitetura.DATA_ANTERIOR_A, "Data de Fim do Mandato do Vice-Coordenador", CalendarUtils.format(curso.getDataFim(), "dd/MM/yyyy"));
			
			if (curso.getDataFim().before(curso.getViceCoordenador().getDataFimMandato())) 
				lista.addMensagem(MensagensArquitetura.DATA_ANTERIOR_A, "Data de Fim do Mandato do Vice-Coordenador", CalendarUtils.format(curso.getDataFim(), "dd/MM/yyyy"));
		
			if ( curso.getCoordenador().getServidor().getId() == curso.getViceCoordenador().getServidor().getId() )
				lista.addErro("O Coordenador não pode ser igual ao Vice-Coordenador.");
		
		}
	}

	/**
	 * Validação das informações do Objetivo e da Importância do Curso. 
	 * 
	 * @param curso
	 * @param lista
	 */
	public static void validateObjetivoImportancia(CursoLato curso, ListaMensagens lista){
		ValidatorUtil.validateRequired(curso.getPropostaCurso().getJustificativa(), "Justificativa e Objetivo", lista);
	}

	/**
	 * Validação do Corpo Docente do Curso Lato Sensu. 
	 * 
	 * @param dao
	 * @param lista
	 * @param curso
	 * @throws DAOException
	 */
	public static void validacaoCorpoDocente(CorpoDocenteCursoLatoDao dao, ListaMensagens lista, CursoLato curso) throws DAOException {
		Collection<CorpoDocenteCursoLato> corpoDocente = dao.findByAllDocenteCurso(curso);
		ParametrosPropostaCursoLato paramentrosProposta = (ParametrosPropostaCursoLato) dao.findLast(ParametrosPropostaCursoLato.class);
		int totalDocentes = corpoDocente.size();
		int totalServidores = 0;
		
		for (CorpoDocenteCursoLato corpoDocenteCursoLato : corpoDocente) {
			if (corpoDocenteCursoLato.getServidor() != null)
				totalServidores++;
		}
		
		if (paramentrosProposta.getPorcentagemServidores() != null && totalDocentes > 0 &&
				(double) ((totalServidores * 100) / totalDocentes) < paramentrosProposta.getPorcentagemServidores()) 
			lista.addErro("Pelo menos " +  paramentrosProposta.getPorcentagemServidores().intValue() + "% de docentes do curso devem ser da " + 
					RepositorioDadosInstitucionais.get("siglaInstituicao"));
	}
	
	/**
	 * Validação realizada em cima do corpo docente do Curso, onde são verificados se o docente não excedeu a carga horária máxima, se a carga horária
	 * dos docentes internos foi atingida, dentre outras validações.
	 * 
	 * @param dao
	 * @param cursoLato
	 * @param lista
	 * @throws DAOException
	 */
	public static void validacaoCargaHorarioCorpoDocente(ComponenteCursoLatoDao dao, CursoLato cursoLato, ListaMensagens lista) throws DAOException {
		Collection<LinhaComponenteCursoLato> corpoDocenteDisciplinaLato = dao.findComponenteCursoLatoByCurso(cursoLato.getId());
		ParametrosPropostaCursoLato parametrosProposta = (ParametrosPropostaCursoLato) dao.findLast(ParametrosPropostaCursoLato.class);
		HashMap<String, Integer> validaCH = new HashMap<String, Integer>();
		
		if (parametrosProposta != null) {
			Double chMaximaDocente = 0.0;
			if (parametrosProposta.getChTotalDocenteCurso() != null)
				chMaximaDocente = ((cursoLato.getCargaHoraria() * parametrosProposta.getChTotalDocenteCurso()) / 100 );
				
			Double chDocentesInterno = 0.0;
			Double chDocentesExterno = 0.0;
			
			forGeral : for (LinhaComponenteCursoLato linha : corpoDocenteDisciplinaLato) {
				Set<String> chaves = linha.getNomeDocente().keySet();
				  for (String chave : chaves){
					  if (linha.isDocenteInterno()) 
						  chDocentesInterno += linha.getNomeDocente().get(chave);
					  else
						  chDocentesExterno += linha.getNomeDocente().get(chave);
					  
					  if (validaCH.isEmpty())
						  validaCH.put(chave, linha.getNomeDocente().get(chave));
					  else
						  if (!validaCH.containsKey(chave))
							  validaCH.put(chave, linha.getNomeDocente().get(chave));
						  else
							  validaCH.put(chave, validaCH.get(chave) + linha.getNomeDocente().get(chave));
					 
					  if (validaCH.get(chave) > chMaximaDocente && chMaximaDocente != 0.0) {
						  lista.addErro("Carga horária do docente " + chave + " excede o máximo permitido.");
						  break forGeral;
					  }
				  }
			}
			
			if (lista.isEmpty()) {
				
				Double chMaximaDocenteInterno = 0.0;
				if (parametrosProposta.getPorcentagemMinimaDocentesInternos() != null) 
					chMaximaDocenteInterno = (cursoLato.getCargaHoraria() * parametrosProposta.getPorcentagemMinimaDocentesInternos()) / 100;
				
				if (chDocentesInterno < chMaximaDocenteInterno && chMaximaDocenteInterno != 0.0) {
					lista.addErro("Pelo menos " + parametrosProposta.getPorcentagemMinimaDocentesInternos().intValue() + "% " +
							"("+chMaximaDocenteInterno+"h) da carga horária do Curso deve ser ministrado por docentes Internos.");
				}
				
				Double chMaximaDocenteExterno = 0.0; 
				if (parametrosProposta.getPorcentagemMaximaDocentesExternos() != null)
					chMaximaDocenteExterno = (cursoLato.getCargaHoraria() * parametrosProposta.getPorcentagemMaximaDocentesExternos()) / 100;
				
				if (chDocentesExterno > chMaximaDocenteExterno && chMaximaDocenteExterno != 0.0) {
					lista.addErro("A carga horária dos Docentes Externos não pode ser superior " + parametrosProposta.getPorcentagemMinimaDocentesInternos().intValue() + "% " +
							"("+ chMaximaDocenteExterno  +"h) da carga horária do Curso.");
				}
				
				Double chTotal = chDocentesExterno + chDocentesInterno;
				if (chTotal < cursoLato.getCargaHoraria().doubleValue()) 
					lista.addErro("A soma da carga horária das disciplinas é inferior a carga horária do Curso Lato Sensu ( "+ cursoLato.getCargaHoraria() + " horas).");
				if (chTotal > cursoLato.getCargaHoraria().doubleValue())
					lista.addErro("A soma da carga horária das disciplinas é superior a carga horária do Curso Lato Sensu ( "+ cursoLato.getCargaHoraria() + " horas).");
			}
		
		}
	}
	
	/**
	 * Validação feita na hora da Seleção.
	 * 
	 * @return
	 */
	public static ListaMensagens validateSelecao(CursoLato cursoLato){
		ListaMensagens lista = new ListaMensagens();
		
		if ( !isEmpty( cursoLato.getPropostaCurso().getMediaMinimaAprovacao() ) && cursoLato.getPropostaCurso().getMediaMinimaAprovacao() > 10 )
			lista.addErro("Nota mínima para aprovação: Não pode ser superior a 10.");
		
		ValidatorUtil.validateRequired(cursoLato.getPropostaCurso().getMediaMinimaAprovacao(), "Nota mínima para aprovação", lista);
		
		ValidatorUtil.validateRequired(cursoLato.getPropostaCurso().getFreqObrigatoria(), "Frequencia Mínima Aprovação", lista);
		
		return lista;
	}
	
	/**
	 * Validação feita na hora de cadastrar um Docente Externo
	 * 
	 * @param cddl
	 * @param estrangeiro
	 * @return
	 */
	public static ListaMensagens validateDocenteExterno(CorpoDocenteCursoLato cdcl, boolean estrangeiro){
		ListaMensagens lista = new ListaMensagens();
		if (estrangeiro) 
			ValidatorUtil.validateRequired(cdcl.getDocenteExterno().getPessoa().getPassaporte(), "Passaporte", lista);	
		else { 
			if (cdcl.getDocenteExterno().getPessoa().getCpf_cnpj() != null ) 
				ValidatorUtil.validateCPF_CNPJ(cdcl.getDocenteExterno().getPessoa().getCpf_cnpj(), "CPF", lista);
			else
				ValidatorUtil.validateRequired(null, "CPF", lista);
		}
		
		ValidatorUtil.validateRequired(cdcl.getDocenteExterno().getPessoa().getNome(), "Nome", lista);
		ValidatorUtil.validateRequired(cdcl.getDocenteExterno().getPessoa().getEmail(), "Email", lista);
		ValidatorUtil.validateEmail(cdcl.getDocenteExterno().getPessoa().getEmail(), "Email", lista);
		ValidatorUtil.validateRequired(cdcl.getDocenteExterno().getFormacao(), "Formação", lista);
		ValidatorUtil.validateRequired(cdcl.getDocenteExterno().getInstituicao(), "Instituição", lista);
		
		return lista;
	}

	
	/**
	 * Validação da adição de um Servidor a Proposta.
	 * 
	 * @param cddl
	 * @param estrangeiro
	 * @return
	 * @throws DAOException 
	 */
	public static ListaMensagens validateAddServidor(Collection<CorpoDocenteCursoLato> corpoDocenteOld, 
			CorpoDocenteCursoLato corpoDocenteCursoLato, CursoLato cursoLato,  GenericDAO dao, boolean estrangeiro) throws DAOException{
		ListaMensagens lista = new ListaMensagens();
		
		for (CorpoDocenteCursoLato cdcl : corpoDocenteOld) {
			if (cdcl.getDocenteExterno() != null && !ValidatorUtil.isEmpty(corpoDocenteCursoLato.getDocenteExterno())) {
				if (cdcl.getDocenteExterno().getId() == corpoDocenteCursoLato.getDocenteExterno().getId()) {
					lista.addErro("Docente já presente no corpo Docente.");
				}
			}
			if (cdcl.getServidor() != null && cdcl.getServidor().getId() == corpoDocenteCursoLato.getServidor().getId()){ 
				lista.addErro("Docente já presente no corpo Docente.");
			}
		}
		
		PerfilPessoa perfilPessoa;
		if (ValidatorUtil.isNotEmpty(corpoDocenteCursoLato.getDocenteExterno()))
			perfilPessoa = PerfilPessoaDAO.getDao().get(corpoDocenteCursoLato.getDocenteExterno().getIdPerfil());
		else
			perfilPessoa = PerfilPessoaDAO.getDao().get(corpoDocenteCursoLato.getServidor().getIdPerfil());
		
		if (ValidatorUtil.isEmpty(perfilPessoa) || ValidatorUtil.isEmpty(perfilPessoa.getEnderecoLattes())) {
			if (((ParametrosPropostaCursoLato) dao.findLast(ParametrosPropostaCursoLato.class)).getCurriculoLattesObrigatorio())
				lista.addErro("O docente não possui o Currículo Lattes cadastrado.");
			else
				lista.addWarning("O Docente cadastrado não possui Currículo Lattes cadastrado.");
		}
		
		if ( !lista.isErrorPresent() ) {
			corpoDocenteCursoLato.setCursoLato(cursoLato);
			corpoDocenteCursoLato.setDocenteExterno(null);
			dao.create(corpoDocenteCursoLato);
		} else 
			corpoDocenteCursoLato = new CorpoDocenteCursoLato();

		return lista;
	}
	
}