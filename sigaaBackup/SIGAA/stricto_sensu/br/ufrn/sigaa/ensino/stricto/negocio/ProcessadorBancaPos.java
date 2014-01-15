/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 09/11/2007
 *
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Arquivo;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DiscenteStrictoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.EquipeProgramaDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.dominio.MembroBancaPos;
import br.ufrn.sigaa.mensagens.TemplatesDocumentos;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.prodocente.atividades.dominio.TeseOrientada;
import br.ufrn.sigaa.prodocente.producao.dominio.Banca;
import br.ufrn.sigaa.prodocente.producao.dominio.NaturezaExame;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.sites.dominio.NoticiaSite;

/**
 * Cadastro de banca de pós-graduação stricto sensu e seus membros
 * 
 * @author André Dantas
 * 
 */
public class ProcessadorBancaPos extends ProcessadorCadastro {
	/**
	 * Executar as operações
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		if (SigaaListaComando.CADASTRAR_BANCA_POS.equals(mov.getCodMovimento()) 
				|| SigaaListaComando.CADASTRAR_BANCA_POS_ORIENTADOR.equals(mov.getCodMovimento())) {
			return criar((MovimentoCadastro) mov);
		} else if (SigaaListaComando.CADASTRAR_ATA_BANCA_POS.equals(mov
				.getCodMovimento())) {
			return salvarAta((MovimentoAtaBanca) mov);
		} else if( SigaaListaComando.CANCELAR_BANCA_POS.equals( mov.getCodMovimento() ) ){
			return cancelarBancaPos(mov); 
		} else if( SigaaListaComando.CADASTRAR_DEFESA_ALUNO_CONCLUIDO.equals( mov.getCodMovimento() ) ){
			cadastrarDefesaAlunoConcluido((MovimentoBancaAlunoConcluido) mov);
		} else if( SigaaListaComando.APROVAR_BANCA_POS.equals( mov.getCodMovimento() ) ){
			return aprovarBanca((MovimentoCadastro) mov);
		}
		return mov;
	}

	/**
	 * Cria uma nova banca.
	 */
	@Override
	protected Object criar(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		
		BancaPos b = mov.getObjMovimentado();
		BancaPosDao dao = getDAO(BancaPosDao.class, mov);
		
		dao.removerMembrosBanca(b);
		prepararInsercaoMembrosBanca(mov);
		
		boolean notificar = false;
		// Verifica se é uma nova banca que não seja antiga e que não tenho ocorrido..
		// Esta verificação é necessária para enviar email para os docentes e cadastrar noticia no portal do programa
		if (!b.isDefesaAntiga() && b.getData().getTime() >= new Date().getTime())
			notificar = true;
		
		if (b.getDadosDefesa() != null) {
			//Verifica se existe os dados da defesa persistido. 
			if (b.getDadosDefesa().getId() > 0) {
				DadosDefesa dadosDefesaPersistido = 
						dao.findByPrimaryKey( b.getDadosDefesa().getId(), DadosDefesa.class, "id");
				if( isEmpty( dadosDefesaPersistido ) )
					b.getDadosDefesa().setId(0);
			} 
			dao.createOrUpdate(b.getDadosDefesa());
		}
		
		verificarNulos(b);
		
		boolean pendente = SigaaListaComando.CADASTRAR_BANCA_POS_ORIENTADOR.equals(mov.getCodMovimento());
		if (pendente) {
			b.setStatus(BancaPos.PENDENTE_APROVACAO);
			notificar = false;
		}

		if (b.getId() == 0)
			dao.create(b);
		else
			dao.update(b);
		
		// envia email para a equipe do programa
		if (pendente)
			notificarCoordenacaoBancaPendente(mov, b);			
		else if (b.getStatus() == BancaPos.ATIVO){
			
			// Atualizar TeseOrientada
			atualizarTeseOrientada(mov);
			
			// Cadastrar no prodocente
			cadastroParticipacaoBanca(mov, b);	
			
			if (notificar)
				notificarParticipantes(mov, b);
		}
		
		return b;
	}

	
	/**
	 * Notifica a coordenação do programa de criação de banca por orientador
	 * @param mov
	 * @param b
	 * @throws DAOException
	 */
	private void notificarCoordenacaoBancaPendente(MovimentoCadastro mov,
			BancaPos b) throws DAOException {
		CoordenacaoCursoDao daoCoordenador = getDAO(CoordenacaoCursoDao.class, mov);
		try {
			Unidade programa = b.getDadosDefesa().getDiscente().getGestoraAcademica();
			String usuario = mov.getUsuarioLogado().getNome();
			
			Collection<CoordenacaoCurso> listaCoordenacao = daoCoordenador.findByPrograma(
					b.getDadosDefesa().getDiscente().getGestoraAcademica().getId(), TipoUnidadeAcademica.PROGRAMA_POS, true, null);
			
			StringBuilder introducao = new StringBuilder();
			introducao.append( "Uma nova " );
			introducao.append( b.getTipoDescricao() );
			introducao.append( " de " );
			introducao.append( b.getNivel() );
			introducao.append( " foi registrada no SIGAA por "+usuario+" para o programa de " + programa.getNomeAscii() + "." );	
			
			introducao.append( "<br/><b> Esta banca encontra-se Pendente de Aprovação, neste caso para que seja validada," +
					" o Programa deve aprová-la acessando o seguinte caminho:</b><br/><br/>" );
			introducao.append( " <b>SIGAA -> Portal Coord. Stricto Sensu -> Aluno -> Conclusão -> Validar Bancas Pendentes</b><br/><br/>" );
			
			introducao.append( " As informações do trabalho em questão são:" );					
			
			//Envia o e-mail para todas as pessoas contidas na lista
			for (CoordenacaoCurso c : listaCoordenacao) {	
				if (!ValidatorUtil.isEmpty(c.getEmailContato())){
					
					Map<String, String> params = new HashMap<String, String>();
					params.put("ASSUNTO", "SIGAA - " + programa.getNomeAscii() + " - Nova Banca cadastrada.");	
					
					params.put("SAUDACAO", "Prezado(a) "+ c.getServidor().getNome());
					
					params.put("INTRODUCAO", introducao.toString());
					
					params.put("CORPO",getCorpoEmail(programa, b));
					
					String nome = String.valueOf( c.getServidor().getNome() );
					String email = String.valueOf( c.getEmailContato() );
					
					Mail.enviaComTemplate(nome, email, TemplatesDocumentos.EMAIL_AVISO_CADASTRO_BANCA, params);
					
				}
			}		
			
		} finally {
			if (daoCoordenador != null)
				daoCoordenador.close();
		}
	}
	
	/**
	 * Aprova a banca passada no movimento
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	protected Object aprovarBanca(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		BancaPos b = (mov).getObjMovimentado();
		
		b.setStatus(BancaPos.ATIVO);
			
		getGenericDAO(mov).update(b);
		
		b.getMembrosBanca().iterator();
		notificarParticipantes(mov, b);
		
		return b;
	}

	/**
	 * Notifica os participantes da banca e publica  notícia no portal
	 * @param mov
	 * @param b
	 * @param novaBanca
	 * @throws DAOException
	 */
	protected void notificarParticipantes(MovimentoCadastro mov, BancaPos b) throws DAOException {
			// envia email para os discentes do programa
			enviarEmailTodosDiscentesPrograma((Unidade)mov.getObjAuxiliar(), mov, b);			
			// envia email para a equipe do programa
			enviarEmailEquipePrograma((Unidade)mov.getObjAuxiliar(), mov, b);			
			// Publica noticia no portal público
			publicarNoticia((Unidade)mov.getObjAuxiliar(), mov, b);
	}
	
	/**
	 * Prepara os objetos para inserir os membros da banca.
	 * @param mov
	 * @throws DAOException
	 */
	private void prepararInsercaoMembrosBanca(MovimentoCadastro mov) throws DAOException {
		
		BancaPos b = mov.getObjMovimentado();
		BancaPosDao dao = getDAO(BancaPosDao.class, mov);
		
		for (MembroBancaPos m : b.getMembrosBanca()) {
			m.setId(0);
			
			
			if (m.getMaiorFormacao() != null && m.getMaiorFormacao().getId() == 0)
				m.setMaiorFormacao(null);
			
			if (m.isExterno() && m.getPessoaMembroExterno().getId() == 0) {
				int idPessoa = SincronizadorPessoas.getNextIdPessoa();
				m.getPessoaMembroExterno().setId(idPessoa);
				dao.create(m.getPessoaMembroExterno());
			} else if (m.isExterno() && m.getPessoaMembroExterno() != null) {
				String nome = StringUtils.escapeBackSlash(m.getPessoaMembroExterno().getNome());								
				dao.updateFields(Pessoa.class, m.getPessoaMembroExterno().getId(), 
						new String[]{"nome","nomeAscii","email","sexo"},
						new Object[]{nome, StringUtils.toAscii(nome), 
							m.getPessoaMembroExterno().getEmail(), 
							m.getPessoaMembroExterno().getSexo() });
			}
		}
	}
	
	/**
	 * Publica um Notícia no portal Público, informando que foi cadastrada uma nova banca.
	 * @param programa
	 * @param mov
	 * @param bancaPos
	 * @throws DAOException
	 */
	public void publicarNoticia(Unidade programa, Movimento mov, BancaPos bancaPos) throws DAOException{
		GenericDAO dao = getGenericDAO(mov);
		try {
			StringBuilder titulo = new StringBuilder("Banca de " + bancaPos.getTipoDescricao() + ": " );
			titulo.append(bancaPos.getDadosDefesa().getDiscente().getNome());
				
			StringBuilder msg = new StringBuilder();
			msg.append(" Uma banca de " + bancaPos.getTipoDescricao() + " de " + bancaPos.getNivel() + " foi cadastrada pelo programa. ");
			msg.append(getCorpoEmail(programa, bancaPos));
						
			NoticiaSite noticia = new NoticiaSite();
			noticia.setTitulo(titulo.toString());
			noticia.setDescricao(msg.toString());
			noticia.setUnidade(programa);
			noticia.setPublicada(true);
			noticia.setLocale("pt_BR");
			dao.create(noticia);

		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Retorna o Corpo da mensagem que será enviada por email.
	 * @param programa
	 * @param bancaPos
	 * @return
	 */
	/**
	 * Retorna o Corpo da mensagem que será enviada por email.
	 * @param programa
	 * @param bancaPos
	 * @return
	 */
	private String getCorpoEmail(Unidade programa, BancaPos bancaPos){
		
		StringBuilder msg = new StringBuilder();
		
		msg.append("<br> DISCENTE: "+ bancaPos.getDadosDefesa().getDiscente().getNome());	
		msg.append("<br> DATA: "+ CalendarUtils.format(bancaPos.getData(), "dd/MM/yyyy"));		
		
		if (!isEmpty(bancaPos.getData()))
			msg.append("<br> HORA: " + CalendarUtils.format(bancaPos.getData(), "HH:mm"));
		
		msg.append("<br> LOCAL: " + bancaPos.getLocal());
		msg.append("<br> TÍTULO: " + bancaPos.getDadosDefesa().getTitulo().trim());
		msg.append("<br> PALAVRAS-CHAVES: " + bancaPos.getDadosDefesa().getPalavrasChave().trim());
		msg.append("<br> PÁGINAS: " + bancaPos.getDadosDefesa().getPaginas());
		msg.append("<br> GRANDE ÁREA: "	+ bancaPos.getDadosDefesa().getArea().getGrandeArea().getNome());
		msg.append("<br> ÁREA: " + bancaPos.getDadosDefesa().getArea().getArea().getNome());
		
		if ( !ValidatorUtil.isEmpty(bancaPos.getDadosDefesa().getArea().getSubarea()) )
			msg.append("<br> SUBÁREA: "+ bancaPos.getDadosDefesa().getArea().getSubarea().getNome());
		
		if ( !ValidatorUtil.isEmpty(bancaPos.getDadosDefesa().getArea().getEspecialidade()) )
			msg.append("<br> ESPECIALIDADE: " + bancaPos.getDadosDefesa().getArea().getEspecialidade().getNome());
		
		msg.append("<br> RESUMO: " + bancaPos.getDadosDefesa().getResumo());
		msg.append("<br> MEMBROS DA BANCA: <br>");
		
		for (MembroBancaPos m : bancaPos.getMembrosBanca()){
			msg.append(m.getTipoDescricao());
			msg.append(" - ");
			msg.append(m.getMembroIdentificacao());
			msg.append("<br>");
		}	
		
		return msg.toString();		
	}
	
	/**
	 * Ao cadastrar uma banca enviar os dados de resumo da banca para 
	 * todos os Docentes da Equipe do programa informado por e-mail.
	 * 
	 * @throws DAOException
	 */
	private void enviarEmailEquipePrograma(Unidade programa, Movimento mov, BancaPos bancaPos) throws DAOException {		

		EquipeProgramaDao equipeDao = getDAO(EquipeProgramaDao.class, mov);
		SecretariaUnidadeDao secretariaDao = getDAO(SecretariaUnidadeDao.class, mov);
		
		try {
			//Lista das pessoas que recebrão a notificação da banca
			List<Pessoa> listaPessoa = new ArrayList<Pessoa>();

			//Adiciona à lista de pessoas a equipe do programa
			List<EquipePrograma> listaEquipe = equipeDao.findByPrograma(programa.getId());
			for (EquipePrograma ep : listaEquipe)
				listaPessoa.add(ep.getPessoa());
			
			//Adiciona à lista de pessoas o(a) secretário(a) do programa
			Collection<SecretariaUnidade> listaSecretaria =	secretariaDao.findByUnidade(programa.getId(),null);
			for (SecretariaUnidade su : listaSecretaria){
				if( isEmpty(su.getUsuario().getPessoa().getEmail()) )
					su.getUsuario().getPessoa().setEmail(su.getUsuario().getEmail());
				if( !isEmpty(su.getUsuario().getPessoa().getEmail()) ) 
					listaPessoa.add(su.getUsuario().getPessoa());
			}	
			
			StringBuilder introducao = new StringBuilder();
			introducao.append( "Uma nova " );
			introducao.append( bancaPos.getTipoDescricao() );
			introducao.append( " de " );
			introducao.append( bancaPos.getNivel() );
			introducao.append( " foi registrada no SIGAA para o " + programa.getNomeAscii() + "." );
			introducao.append( " Este e-mail é enviado para todos os membros do programa para divulgação da apresentação.<br>" );
			introducao.append( " As informações do trabalho em questão são:" );				
			
			//Envia o e-mail para todas as pessoas contidas na lista
			for (Pessoa p : listaPessoa) {	
				if (!ValidatorUtil.isEmpty(p.getEmail())){
					
					Map<String, String> params = new HashMap<String, String>();
					params.put("ASSUNTO", "SIGAA - " + programa.getNome() + " - Nova Banca cadastrada.");	
					
					params.put("SAUDACAO", "Prezad" + (p.getSexo() == Pessoa.SEXO_FEMININO ? "a " : "o ") + p.getNome());
					
					params.put("INTRODUCAO", introducao.toString());
					
					params.put("CORPO",getCorpoEmail(programa, bancaPos));
					
					String nome = String.valueOf( p.getNome() );
					String email = String.valueOf( p.getEmail() );
					
					Mail.enviaComTemplate(nome, email, TemplatesDocumentos.EMAIL_AVISO_CADASTRO_BANCA, params);
					
				}
			}		
			
		} finally {
			equipeDao.close();
			secretariaDao.close();
		}
		
	}	

	
	/**
	 * Ao cadastrar uma banca enviar os dados de resumo da banca para 
	 * todos os discentes do programa informado por e-mail.
	 * 
	 * @throws DAOException
	 */
	private void enviarEmailTodosDiscentesPrograma(Unidade programa, Movimento mov, BancaPos bancaPos) throws DAOException {
		
		// busca por email de todos alunos de um dado programa
		DiscenteStrictoDao discenteStrictoDAO = getDAO(DiscenteStrictoDao.class, mov);
		
		try{
			
			Collection<DiscenteStricto> lista = discenteStrictoDAO.findDiscentesByPrograma(programa.getId());
			
			for (DiscenteStricto discenteStricto : lista) {
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("ASSUNTO", "SIGAA - " + programa.getNome() + " - Nova Banca cadastrada.");	
				
				params.put("SAUDACAO", "Olá " + discenteStricto.getNome());
				
				StringBuilder introducao = new StringBuilder();
				introducao.append( "Uma Banca  de " );
				introducao.append( bancaPos.getTipoDescricao() );
				introducao.append( " de " );
				introducao.append( bancaPos.getNivel() );
				introducao.append( " foi cadastrada conforme as informações abaixo:" );
				
				params.put("INTRODUCAO", introducao.toString() );
				
				params.put("CORPO", getCorpoEmail(programa, bancaPos));
				
				String nome = String.valueOf( discenteStricto.getNome() );
				String email = String.valueOf( discenteStricto.getPessoa().getEmail() );
				
				Mail.enviaComTemplate(nome, email, TemplatesDocumentos.EMAIL_AVISO_CADASTRO_BANCA, params);
	
			}
			
		}finally{
			discenteStrictoDAO.close();
		}
	}

	/**  
	 * Atualiza os dados da Tese Orientada pelo orientador do discente, inclusive a data da defesa.
	 * Aplicado somente para banca de defesa. 
	 */
	private void atualizarTeseOrientada(MovimentoCadastro mov) throws DAOException {
		OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class, mov);		
		BancaPos banca = mov.getObjMovimentado();
		try{
			if( banca.isDefesa() ){
				OrientacaoAcademica orientacao = orientacaoDao.findOrientadorAtivoByDiscente(banca.getDadosDefesa().getDiscente().getId());
				if (orientacao != null) {
					TeseOrientada tese = orientacaoDao.findTeseOrientadaByDiscenteOrientador(orientacao.getDiscente(), orientacao.getServidor());
					if (tese != null) {
						tese.setTitulo(banca.getDadosDefesa().getTitulo());
						tese.setPaginas(banca.getDadosDefesa().getPaginas());
						tese.setArea(banca.getArea());
						tese.setSubArea(banca.getSubArea());
						tese.setDataPublicacao(banca.getData());
						tese.setDadosDefesa(banca.getDadosDefesa());
						
						orientacaoDao.updateNoFlush(tese);
					}
				}
			}	
		}finally{
			orientacaoDao.close();
		}
	}

	/**  Cadastra banca no prodocente para os membros internos da banca (apenas os servidores) */
	private void cadastroParticipacaoBanca(MovimentoCadastro mov, BancaPos b) throws DAOException {
		
		ProducaoDao producaoDao = getDAO(ProducaoDao.class, mov);
		
		try{
			
			for (MembroBancaPos membro : b.getMembrosBanca()) {
				if (membro.isServidor()) {
					Banca banca = null;
					
					banca = producaoDao.findBancaByServidorAndBancaPos(membro.getServidor(), b);
					
					if (banca == null)
						banca = new Banca();
					
					banca.setBancaPos(b);
					banca.setAnoReferencia(CalendarUtils.getAno(b.getData()));
					banca.setArea(b.getGrandeArea());
					banca.setSubArea(b.getArea());
					banca.setAtivo(true);
					banca.setAutor(b.getDadosDefesa().getDiscente().getNome());
					banca.setData(b.getData());
					banca.setDataCadastro(new Date());
					banca.setDataProducao(b.getData());
					banca.setTitulo(StringUtils.toAsciiHtml( StringUtils.stripHtmlTags( b.getDadosDefesa().getTitulo() ) ));
					banca.setInstituicao(new InstituicoesEnsino(InstituicoesEnsino.UFRN));
					if (b.getDadosDefesa().getDiscente().getCurso() != null)
						banca.setMunicipio(b.getDadosDefesa().getDiscente().getCurso().getMunicipio());
					else
						banca.setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
					banca.setPais(new Pais(Pais.BRASIL));
					banca.setConsolidado(null);
					banca.setDataConsolidacao(null);
					banca.setDataValidacao(null);
					banca.setValidado(false);
					banca.setUsuarioConsolidador(null);
					banca.setUsuarioValidador(null);
					banca.setSequenciaProducao(null);
					banca.setServidor(membro.getServidor());
					banca.setRegistro(mov.getUsuarioLogado().getRegistroEntrada());
					banca.setTipoBanca(new TipoBanca(TipoBanca.CURSO));
					banca.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
					banca.setTipoProducao(TipoProducao.BANCA_CURSO_SELECOES);
					banca.setDepartamento(membro.getServidor().getUnidade());
					
					if (b.isQualificacao()) {
						if (b.isMestrado())
							banca.setNaturezaExame(NaturezaExame.QUALIFICACAO_MESTRADO);
						else
							banca.setNaturezaExame(NaturezaExame.QUALIFICACAO_DOUTORADO);
					} else {
						if (b.isMestrado())
							banca.setNaturezaExame(NaturezaExame.DISSERTACAO_MESTRADO);
						else
							banca.setNaturezaExame(NaturezaExame.TESE_DOUTORADO);
					}
					
					banca.setCategoriaFuncional(null);
					banca.setInformacao(b.getDescricaoMembros());
					
					if (banca.getId() == 0)
						producaoDao.createNoFlush(banca);
					else
						producaoDao.updateNoFlush(banca);
				}
			}
			
		}finally{
			producaoDao.close();
		}
	}
	
	/**
	 * 
	 * Cadastro de upload da ata de parecer da banca
	 * 
	 */

	protected Object salvarAta(MovimentoAtaBanca mov) throws DAOException,
			NegocioException, ArqException {

		checkRole(new int[] { SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG }, mov);

		BancaPos banca = mov.getBanca();
		Arquivo ata = mov.getAta();

		GenericDAO dao = getGenericDAO(mov);

		try {
			if (ata != null && ata.getSize() != 0) {
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, ata.getBytes(),
						ata.getContentType(), ata.getName());

				banca.setIdArquivo(new Integer(idArquivo));

				dao.updateField(BancaPos.class, banca.getId(), "idArquivo",
						banca.getIdArquivo());

			}
		} catch (Exception e) {
			throw new ArqException(e);
		} finally {
			dao.close();
		}

		return banca;
	}
	
	/**
	 * Este método cadastra dados de defesa para alunos já concluídos
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	protected Object cadastrarDefesaAlunoConcluido(MovimentoBancaAlunoConcluido mov) throws NegocioException, ArqException {
		
		BancaPos banca = mov.getBanca();
		GenericDAO dao = getGenericDAO(mov);
		try{
			if (banca.getDadosDefesa() != null) {
				if (banca.getDadosDefesa().getId() == 0) {
					dao.create(banca.getDadosDefesa());
				} else {
					dao.update(banca.getDadosDefesa());
				}
			}
			
			if( !banca.getDadosDefesa().getDiscente().isConcluido() )
				throw new NegocioException( "O cadastro de defesas antigas só pode ser realizada para discentes com o status concluído." );
	
			UploadedFile arquivo = mov.getArquivo(); 
			
			if(mov.getArquivo() != null){
				int idArquivo;
				try {
					idArquivo = EnvioArquivoHelper.getNextIdArquivo();
					EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
					banca.getDadosDefesa().setIdArquivo(idArquivo);
				} catch (IOException e) {
					throw new ArqException(e);
				}
			} 	
			
			dao.create(banca);
		} finally {
			dao.close();
		}
		return null;
	}
	
	/**
	 * Método responsável por realizar o processo de cancelamento de banca de pós.
	 * @param mov
	 * @return
	 * @throws DAOException 
	 */
	private Object cancelarBancaPos(Movimento mov) throws DAOException {
		
		BancaPos banca = ((MovimentoCadastro) mov).getObjMovimentado();
		
		banca.setStatus(BancaPos.CANCELADA);
			
		getGenericDAO(mov).update(banca);
		
		return banca;
	}
	
	/**
	 * Verifica os atributos nulos. 
	 * @param b
	 */
	private void verificarNulos(BancaPos b) {
		for (MembroBancaPos m : b.getMembrosBanca()) {
			if (m.isExterno()) {
				m.setDocentePrograma(null);
			} else {
				m.setPessoaMembroExterno(null);
			}
		}
	}

	/**
	 * Valida a Banca.
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		BancaPos banca = ((MovimentoCadastro) mov).getObjMovimentado();
		
		checkValidation(banca.validate());
	}

}
