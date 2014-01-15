/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/12/2008
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.CertificadoExtensaoHelper;
import br.ufrn.sigaa.extensao.negocio.DiscenteExtensaoValidator;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/*******************************************************************************
 * MBean responsável por gerar certificados das ação de extensão (público alvo). <br>
 * 
 * <p> <strong>Só usar esse MBean para gerar as declarações, por favor.</strong></p>
 * 
 * <p>Ao contrários do que geralmente ocorre dos casos e uso, concentar tudo num único Mbean 
 * ajuda a menter o sistema. Se o usuário fica vendo duas certificados diferentes dependo do lugar que gerar o certificado.
 * O método que gera o texto do certificado no sistema era para ser único !!!!!!!!!!!!!!!!!!
 * </p>
 * 
 * <p>
 * A autenticação dos documentos é realizada pelo SIGAA através da geração de
 * um código de documento e código de verificação que pode ser validado através
 * do portal público do SIGAA.
 * </p>
 * 
 * @author Ilueny Santos
 * @author Jadson Santos
 * 
 ******************************************************************************/
@Component("certificadoExtensaoMBean")
@Scope("request")
public class CertificadoExtensaoMBean extends SigaaAbstractController<ParticipanteAcaoExtensao> implements AutValidator {

	/** O tamanho máximo do texto do certificado.  Se for maior que isso não imprime, não dá para fazer milagre. */
	public static int TAMANHO_MAXIMO_TEXTO_CERTIFICADO = 820;
	
	
	/** O participante para o qual será emitirdo a declaração.
	 *  O id desse participante é passado via  <f:setPropertyActionListener>    */ 
	private ParticipanteAcaoExtensao participante = new ParticipanteAcaoExtensao();

	/**
	 *  Para emissão de declarações de membros de projeto.
	 *  O id desse participante é passado via  <f:setPropertyActionListener>
	 */
	private MembroProjeto membro = new MembroProjeto();
	
	/** O participante para o qual será emitirdo a declaração.
	 *  O id desse participante é passado via  <f:setPropertyActionListener>    */ 
	private DiscenteExtensao discenteExtensao = new DiscenteExtensao();
	
	
	/** Se é o proprio particiapante que está emitindo ou é o coordenador da ação. porque as regras são um pouco diferentes.
	 * setado via  <f:setPropertyActionListener>
	 */
	private Boolean isEmissaoByCoordenador;
	
	
	/** Atributo utilizado para representar o comprovante */
	private EmissaoDocumentoAutenticado comprovante;

	
	/** Atributo utilizado para saber se está verificando o certificado */
	private boolean verificando = false;

	
	public static final int EMISSAO_CERTIFICADO_PARA_PARTICIPANTE = 1; 
	
	public static final int EMISSAO_CERTIFICADO_PARA_MEMBRO = 2;
	
	public static final int EMISSAO_CERTIFICADO_PARA_DISCENTE_EXTENSAO = 3;
	
	private int tipoEmissaoCertificado = EMISSAO_CERTIFICADO_PARA_PARTICIPANTE;
	
	
	
	/**
	 * Gera o certificado para o participante selecionado.
	 * 
	 *  <p>   Esse método é chamando das página JSP abaixo passando com o id do participante já populado, 
	 *    então deve-se buscar as outras informações usadas para emitir a declaração.
	 *  </p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/DocumentosAutenticados/lista.jsp</li>
	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li>
	 *      <li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 * 
	 */
	public String emitirCertificadoParticipante() throws DAOException, SegurancaException {

		tipoEmissaoCertificado = EMISSAO_CERTIFICADO_PARA_PARTICIPANTE;
		
		ParticipanteAcaoExtensaoDao dao = null;
		
		try {
			
			dao = getDAO(ParticipanteAcaoExtensaoDao.class);
			
			if(! verificando ){
				participante = dao.findInformacoesParticipantesEmitirCertificadosEDeclaracoes( participante.getId() );
			}else{
				// já foi populado pelo métido exibir
			}
			
			if(isEmissaoByCoordenador != null && isEmissaoByCoordenador == true){
				participante.verificaEmissaoCertificadoCoordenador();
			}else{
				participante.verificaEmissaoCertificadoParticipante();
			}
			
			DadosEmissaoCertificado dadosDeclaracao =  montaDadosEmissaoCertificadoParticipante();

			// montagem do corpo da declaração
			String textoCertificado = getTextoCertificado(dadosDeclaracao);

			verificaTamanhoMaximoTextoCertificado(textoCertificado);
			
			AtividadeExtensao atividade =  
					( ! ValidatorUtil.isEmpty( participante.getAtividadeExtensao()) ) 
					? participante.getAtividadeExtensao()
					: participante.getSubAtividade().getAtividade(); 
			
			// gerando código de autenticação...
			if (!verificando) {

				/*
				 * checkRole(
				 * SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
				 * SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				 * SigaaPapeis.GESTOR_EXTENSAO);
				 */

				comprovante = geraEmissao(
						TipoDocumentoAutenticado.CERTIFICADO,
						((Integer) participante.getId()).toString(),
						dadosDeclaracao.getSemente(),
						"P",
						SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_PUBLICO_ALVO_EXTENSAO,
						true);
			}

			CertificadoExtensaoHelper.emitirCertificado(comprovante.getCodigoSeguranca(), textoCertificado, comprovante.getNumeroDocumento()
					, atividade.getCoordenacao().getPessoa().getNomeAbreviado()
					, comprovante.getDataEmissao(), ""+participante.getId(), getCurrentResponse());

			return null;

		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informações do certificado.");
			return null;
		}

	}


	/** Limita a quantidade de caracteres do certificado, grante que se tiver esse tamanho vai imprimir tudo.*/
	private void verificaTamanhoMaximoTextoCertificado(String textoCertificado) throws NegocioException {
		if(textoCertificado.length() > TAMANHO_MAXIMO_TEXTO_CERTIFICADO){
			throw new NegocioException("Não é possível emitir o certificado pois a quantidade de texto do certificado extrapolou a quantidade e máxima permitida."
						+" Quantidade máxima permitida: "+TAMANHO_MAXIMO_TEXTO_CERTIFICADO+" caracteres, quantidade de de caracteres a ser impressa: "+textoCertificado.length());
		}
	}
	
	
	
	/**
	 * Gera o certificado para o membro de projeto selecionado.
	 * 
	 *  <p>   Esse método é chamando das página JSP abaixo passando com o id do participante já populado, 
	 *    então deve-se buscar as outras informações usadas para emitir a declaração.
	 *  </p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/DocumentosAutenticados/lista.jsp</li>
	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li>
	 *      <li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 * 
	 */
	public String emitirCertificadoMembroProjeto() throws DAOException, SegurancaException {

		tipoEmissaoCertificado = EMISSAO_CERTIFICADO_PARA_MEMBRO;
		
		AtividadeExtensaoDao dao = null;
		MembroProjetoDao daoMembroProjeto = null;
		
		
		try {
			
			dao = getDAO(AtividadeExtensaoDao.class);
			daoMembroProjeto = getDAO(MembroProjetoDao.class);
			
			if(! verificando ){
				membro = daoMembroProjeto.findInformacoesMembroProjetoParaEmissaoCertificado(membro.getId());
				
			}else{
				// já foi populado pelo métido exibir
			}
			
			if ((membro.getDataInicio() != null) && (membro.getDataFim() != null)) {
				ValidatorUtil.validaOrdemTemporalDatas(membro.getDataInicio(), membro.getDataFim(), true, "Período", erros);
				if(hasErrors()){
					addMensagens(erros);
					if(membro != null){
						clearMensagens();
						addMensagemWarning("Solicite ao Coordenador do Projeto que altere suas datas de início e fim, colocando-as dentro do período do Projeto.");
					}
					addMensagemErro("Não é possível emitir o certificado para esse membro do projeto.");
					return null;
				}
			}
			
			DadosEmissaoCertificado dadosDeclaracao =  montaDadosEmissaoCertificadoMembroProjeto();

			// montagem do corpo da declaração
			String textoCertificado = getTextoCertificado(dadosDeclaracao);

			verificaTamanhoMaximoTextoCertificado(textoCertificado);
			
			// gerando código de autenticação...
			if (!verificando) {

				/*
				 * checkRole(
				 * SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
				 * SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				 * SigaaPapeis.GESTOR_EXTENSAO);
				 */

				comprovante = geraEmissao(
						TipoDocumentoAutenticado.CERTIFICADO,
						((Integer) membro.getId()).toString(),
						dadosDeclaracao.getSemente(),
						"M",
						SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_MEMBRO_EQUIPE_EXTENSAO,
						true);
			}

			CertificadoExtensaoHelper.emitirCertificado(comprovante.getCodigoSeguranca(), textoCertificado, comprovante.getNumeroDocumento()
					, membro.getProjeto().getCoordenador().getPessoa().getNomeAbreviado()
					, comprovante.getDataEmissao(), ""+membro.getId(), getCurrentResponse());

			return null;

		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informações do certificado.");
			return null;
		}finally{
			if(dao != null) dao.close();
			if(daoMembroProjeto != null) daoMembroProjeto.close();
		}

	}
	
	
	
	/**
	 * Gera o certificado para o participante selecionado.
	 * 
	 *  <p>   Esse método é chamando das página JSP abaixo passando com o id do participante já populado, 
	 *    então deve-se buscar as outras informações usadas para emitir a declaração.
	 *  </p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/DocumentosAutenticados/lista.jsp</li>
	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li>
	 *      <li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 * 
	 */
	public String emitirCertificadoDiscenteExtensao() throws DAOException, SegurancaException {

		tipoEmissaoCertificado = EMISSAO_CERTIFICADO_PARA_DISCENTE_EXTENSAO;
		
		GenericDAO dao = null;
		
		try {
			
			dao = getGenericDAO();
			
			if(! verificando ){
				discenteExtensao = dao.findByPrimaryKey(discenteExtensao.getId(), DiscenteExtensao.class);
				discenteExtensao.getAtividade().setMembrosEquipe(dao.findByExactField(MembroProjeto.class, "projeto.id", discenteExtensao.getAtividade().getProjeto().getId()));
				
			}else{
				// já foi populado pelo métido exibir
			}
			
			ListaMensagens lista = new ListaMensagens();
			DiscenteExtensaoValidator.validaEmissaoCertificado(discenteExtensao, lista);
			if (ValidatorUtil.isNotEmpty(lista)) {
				addMensagens(lista);
				addMensagemErro("Não é possível emitir o certificado para esse discente de extensão.");
				return null;
			}
			
			DadosEmissaoCertificado dadosDeclaracao =  montaDadosEmissaoCertificadoDiscenteExtensao();

			// montagem do corpo da declaração
			String textoCertificado = getTextoCertificado(dadosDeclaracao);

			verificaTamanhoMaximoTextoCertificado(textoCertificado);
			
			AtividadeExtensao atividade =  discenteExtensao.getAtividade();
			
			// gerando código de autenticação...
			if (!verificando) {

				/*
				 * checkRole(
				 * SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
				 * SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				 * SigaaPapeis.GESTOR_EXTENSAO);
				 */

				comprovante = geraEmissao(
						TipoDocumentoAutenticado.CERTIFICADO,
						((Integer) discenteExtensao.getId()).toString(),
						dadosDeclaracao.getSemente(),
						"D",
						SubTipoDocumentoAutenticado.CERTIFICADO_DISCENTE_EXTENSAO,
						true);
			}

			CertificadoExtensaoHelper.emitirCertificado(comprovante.getCodigoSeguranca(), textoCertificado, comprovante.getNumeroDocumento()
					, atividade.getCoordenacao().getPessoa().getNomeAbreviado()
					, comprovante.getDataEmissao(), ""+discenteExtensao.getId(), getCurrentResponse());

			return null;
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informações do certificado.");
			return null;
		}

	}
	
	
	
	
	
	////////////////// Métodos auxiliares privados  //////////////////////////////

	
	
	/** Organiza as inforamações de declaração em forma objeto para evitar duplicação de código */
	private DadosEmissaoCertificado montaDadosEmissaoCertificadoParticipante(){
		
		DadosEmissaoCertificado dadosEmissao = new DadosEmissaoCertificado();
		
		dadosEmissao.setNomeDeclaracao( participante.getCadastroParticipante().getNome() );
		
		if(participante.getCadastroParticipante().getCpf() != null)
			dadosEmissao.setIdentificacaoDeclaracao( " CPF "+participante.getCadastroParticipante().getCpf());
		else{
			dadosEmissao.setIdentificacaoDeclaracao( "PASSAPORTE "+participante.getCadastroParticipante().getPassaporte()+" DATA NASCIMENTO "+participante.getCadastroParticipante().getDataNascimentoFormatada());
		}
		
		boolean isParticipacaoAtividade = ! ValidatorUtil.isEmpty( participante.getAtividadeExtensao());
		
		dadosEmissao.setObservacaoCertificado( participante.getObservacaoCertificado() );
		
		boolean iscursoOuEvento = false;
		
		
		if(isParticipacaoAtividade){
			
			iscursoOuEvento = (participante.getAtividadeExtensao().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO 
					|| participante.getAtividadeExtensao().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO);
		}else{
			
			
			iscursoOuEvento = (participante.getSubAtividade().getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO 
					|| participante.getSubAtividade().getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO);
		}		
		
		
		dadosEmissao.setTituloAtividade(   ( isParticipacaoAtividade ?  participante.getAtividadeExtensao().getTitulo() : participante.getSubAtividade().getTitulo() ) );
		
		
		////////////Montas as Informações sobre o coordenador ///////////////////
		MembroProjeto coordenador =  null;
		
		if(isParticipacaoAtividade)
			coordenador = participante.getAtividadeExtensao().getCoordenacao();
		else
			coordenador = participante.getSubAtividade().getAtividade().getCoordenacao();
			
		String tituloCoordenador = getTituloCoordenador(coordenador);
	
		dadosEmissao.setCoordenadorAtividade( tituloCoordenador+ coordenador.getPessoa().getNome());
	
		//////////////////////////////////////////////////////////////////
		
		dadosEmissao.setTipoAtividade ( isParticipacaoAtividade ? " da Atividade de Extensão " : " da Mini Atividade de Extensão " );
		dadosEmissao.setUnidadeAtividade( isParticipacaoAtividade ? participante.getAtividadeExtensao().getUnidade().getNome() : participante.getSubAtividade().getAtividade().getUnidade().getNome() );	
		
		String cargaHorariaSeCursoEvento = "";
    	if (iscursoOuEvento ) {
    		Integer ch = participante.getChCertificadoDeclaracao();
    		if(ch != null && ch > 0 )
    			cargaHorariaSeCursoEvento = ", com carga horária semanal de " + ch + " hora(s)"; 
    	}
		
    	dadosEmissao.setCargaHorariaSeCursoEvento(cargaHorariaSeCursoEvento);
    	dadosEmissao.setTipoParticipacao( participante.getTipoParticipacao().getDescricao() );
    	
		String dataInicioFormatada = null;
    	
		if(isParticipacaoAtividade){
			dataInicioFormatada  = Formatador.getInstance().formatarDataDiaMesAno(participante.getAtividadeExtensao().getDataInicio());
		} else{
			dataInicioFormatada  = Formatador.getInstance().formatarDataDiaMesAno(participante.getSubAtividade().getInicio());
		}
    	
    	
    	String dataFimFormatada = null;
    	
    	if(isParticipacaoAtividade) {
    		dataFimFormatada = Formatador.getInstance().formatarDataDiaMesAno(  participante.getAtividadeExtensao().getDataFim() );
    	} else {
    		dataFimFormatada = Formatador.getInstance().formatarDataDiaMesAno(  participante.getSubAtividade().getFim() );
    	}
		
    	dadosEmissao.setDataInicioFormatada(dataInicioFormatada);
    	dadosEmissao.setDataFimFormatada(dataFimFormatada);
    	
    	return dadosEmissao;
	}

	
	/** Organiza as informações de declaração em forma objeto para evitar duplicação de código */
	private DadosEmissaoCertificado montaDadosEmissaoCertificadoMembroProjeto(){
		
		DadosEmissaoCertificado dadosEmissao = new DadosEmissaoCertificado();
		
		dadosEmissao.setNomeDeclaracao( membro.getPessoa().getNome() );
		
		
		boolean iscursoOuEvento = membro.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO
				|| membro.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO;
		
		
		String tipoParticipante = "";
		switch (membro.getCategoriaMembro().getId()) {
		case CategoriaMembro.DOCENTE:
			tipoParticipante = " o(a) Professor(a)";
			dadosEmissao.setIdentificacaoDeclaracao( " SIAPE "+membro.getServidor().getSiape());
			break;
		case CategoriaMembro.DISCENTE:
			tipoParticipante = " o(a) Discente ";
			dadosEmissao.setIdentificacaoDeclaracao( " MATRÍCULA "+membro.getDiscente().getMatricula());
			break;
		case CategoriaMembro.EXTERNO:
			tipoParticipante = "";
			dadosEmissao.setIdentificacaoDeclaracao( " CPF "+membro.getPessoa().getCpfCnpjFormatado());
			break;
		case CategoriaMembro.SERVIDOR:
			tipoParticipante = " o(a) Servidor(a) ";
			dadosEmissao.setIdentificacaoDeclaracao( " SIAPE "+membro.getServidor().getSiape());
			break;
		}
			
		dadosEmissao.setTipoParticipante(tipoParticipante);
		
		dadosEmissao.setTituloAtividade(  membro.getProjeto().getTitulo() );
		
		//////////// Montas as Informações sobre o coordenador ///////////////////
		MembroProjeto coordenador =  membro.getAtividade().getCoordenacao();
		
		String tituloCoordenador = getTituloCoordenador(coordenador);
		
		dadosEmissao.setCoordenadorAtividade( tituloCoordenador + coordenador.getNomeMembroProjeto());
		
		//////////////////////////////////////////////////////////////////
		
		dadosEmissao.setTipoAtividade ( " da Atividade de Extensão " );
		dadosEmissao.setUnidadeAtividade( membro.getProjeto().getUnidade().getNome() );	
		
		dadosEmissao.setHorasAtividadeDesenvolvida(""+membro.getChCertificadoDeclaracao() );
		
		
		String cargaHorariaSeCursoEvento = "";
    	if (iscursoOuEvento && membro.getAtividade().getCursoEventoExtensao() != null) {
    		Integer ch = membro.getAtividade().getCursoEventoExtensao().getCargaHoraria();
    		if(ch != null && ch > 0 )
    			cargaHorariaSeCursoEvento = ", com carga horária de " + ch + " hora(s)";
    	}
		
    	dadosEmissao.setCargaHorariaSeCursoEvento(cargaHorariaSeCursoEvento);
    	dadosEmissao.setTipoParticipacao( membro.getFuncaoMembro().getDescricao() );
    	
    	String dataInicioFormatada = null;
    	if (membro.getProjeto().getDataInicio() != null) {
    	    dataInicioFormatada  = Formatador.getInstance().formatarDataDiaMesAno(membro.getProjeto().getDataInicio());
    	}
    	
    	String dataFimFormatada = null;
    	if (membro.getProjeto().getDataFim() != null) {
    	   dataFimFormatada  = Formatador.getInstance().formatarDataDiaMesAno(membro.getProjeto().getDataFim());
    	    
    	}
		
    	dadosEmissao.setDataInicioFormatada(dataInicioFormatada);
    	dadosEmissao.setDataFimFormatada(dataFimFormatada);
    	
    	return dadosEmissao;
	}

	/**
	 * Retorna o Título do Coordenador para impressão no certificado.
	 *
	 * @param coordenador
	 * @return
	 */
	private String getTituloCoordenador(MembroProjeto coordenador) {
		String tituloCoordenador;
		if(coordenador.getCategoriaMembro().getId() == CategoriaMembro.DOCENTE)
			tituloCoordenador = " pelo(a) Professor(a) ";
		else if(coordenador.getCategoriaMembro().getId() == CategoriaMembro.SERVIDOR)
			tituloCoordenador = " pelo(a) Servidor(a) ";
		else if(coordenador.getCategoriaMembro().getId() == CategoriaMembro.DISCENTE)
			tituloCoordenador = " pelo(a) Discente ";
		else
			tituloCoordenador = " por ";
		return tituloCoordenador;
	}
	

	
	/** Organiza as inforamações de declaração em forma objeto para evitar duplicação de código */
	private DadosEmissaoCertificado montaDadosEmissaoCertificadoDiscenteExtensao(){
		
		DadosEmissaoCertificado dadosEmissao = new DadosEmissaoCertificado();
		
		dadosEmissao.setNomeDeclaracao( discenteExtensao.getDiscente().getPessoa().getNome());
		dadosEmissao.setIdentificacaoDeclaracao( " MATRÍCULA "+discenteExtensao.getDiscente().getMatricula());
		
		dadosEmissao.setTituloAtividade( discenteExtensao.getAtividade().getTitulo().trim().toUpperCase() );
		
		
		////////////Montas as Informações sobre o coordenador ///////////////////
		MembroProjeto coordenador =  discenteExtensao.getAtividade().getCoordenacao();
		
		String tituloCoordenador = getTituloCoordenador(coordenador);
		
		dadosEmissao.setCoordenadorAtividade( tituloCoordenador+ coordenador.getNomeMembroProjeto());
		
		//////////////////////////////////////////////////////////////////
		
		
		
		
		dadosEmissao.setTipoAtividade ( " da Atividade de Extensão " );
		dadosEmissao.setUnidadeAtividade( discenteExtensao.getAtividade().getUnidade().getNome() );	
		
    	dadosEmissao.setTipoParticipacao( discenteExtensao.getTipoVinculo().getDescricao() );
    	
    	String dataInicioFormatada = null;
    	if (discenteExtensao.getAtividade().getDataInicio() != null) {
    	    dataInicioFormatada  = Formatador.getInstance().formatarDataDiaMesAno(discenteExtensao.getAtividade().getDataInicio());
    	}
    	
    	String dataFimFormatada = null;
    	if (discenteExtensao.getAtividade().getDataFim() != null) {
    	   dataFimFormatada  = Formatador.getInstance().formatarDataDiaMesAno(discenteExtensao.getAtividade().getDataFim());
    	    
    	}
		
    	dadosEmissao.setDataInicioFormatada(dataInicioFormatada);
    	dadosEmissao.setDataFimFormatada(dataFimFormatada);
    	
    	return dadosEmissao;
	}
	
	/**
	 * Retorna o texto(corpo) do certificado.
	 * 
	 * @return
	 */
	private String getTextoCertificado(DadosEmissaoCertificado dados){
	    
		
		StringBuilder textoDeclaracao = new StringBuilder("");
    	
    	textoDeclaracao.append("Certificamos que,"
    			+( StringUtils.notEmpty(dados.getTipoParticipante()) ? dados.getTipoParticipante() : "")
    			+" "+ dados.getNomeDeclaracao() ) ;
    	
    	textoDeclaracao.append(", " +dados.getIdentificacaoDeclaracao()+", ");	
		
				
    	textoDeclaracao.append(" participou "
				+ dados.getTipoAtividade()
				+ " "+dados.getTituloAtividade().toUpperCase()+""
				+ ", coordenada "+dados.getCoordenadorAtividade()+""
				+ ( dados.getCargaHorariaSeCursoEvento() != null ? dados.getCargaHorariaSeCursoEvento() : "" ) + ", promovida pelo(a) "
				+ dados.getUnidadeAtividade()
				+ " na função de "+ dados.getTipoParticipacao() );
				
		if(StringUtils.notEmpty(dados.getHorasAtividadeDesenvolvida())){
			textoDeclaracao.append(", com "+dados.getHorasAtividadeDesenvolvida()+ " hora(s) semanais de atividades desenvolvidas");
		}				
		
		if(StringUtils.notEmpty(dados.getFrequenciaMinima())){
			textoDeclaracao.append(", "+dados.getFrequenciaMinima() );
		}
		
		if(StringUtils.notEmpty( dados.getObservacaoCertificado() ))
			textoDeclaracao.append(". Com a seguinte observação: "+dados.getObservacaoCertificado()  );
		
		textoDeclaracao.append( ". No período de "+ dados.getDataInicioFormatada()+ " a "+ dados.getDataFimFormatada() );
		
		textoDeclaracao.append(".");	
		
		return textoDeclaracao.toString().replace("\n", " ").replace("\r", " "); // retira qualquer possível quebra de linha no nome.
	    
	}

	
	
	
	
	
	//////////////////////////   Método para validar o certificado ////////////////
	

	/**
	 * Utilizado na validação da autenticidade do certificado
	 * a partir do portal público.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não chamado diretamente por jsp.</li>
	 * </ul>
	 * 
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante, HttpServletRequest req, HttpServletResponse res) {

		
		this.comprovante = comprovante;

		// verifica para que tipo de participante foi emitido o certificado 
		if(comprovante.getDadosAuxiliares() != null && comprovante.getDadosAuxiliares().equalsIgnoreCase("P")){
			tipoEmissaoCertificado = EMISSAO_CERTIFICADO_PARA_PARTICIPANTE;
		}else{
			if(comprovante.getDadosAuxiliares() != null && comprovante.getDadosAuxiliares().equalsIgnoreCase("M")){
				tipoEmissaoCertificado = EMISSAO_CERTIFICADO_PARA_MEMBRO;
			}else{
				if(comprovante.getDadosAuxiliares() != null && comprovante.getDadosAuxiliares().equalsIgnoreCase("D")){
					tipoEmissaoCertificado = EMISSAO_CERTIFICADO_PARA_DISCENTE_EXTENSAO;
				}else{
					return;
				}
			}
		}
		
		if( tipoEmissaoCertificado == EMISSAO_CERTIFICADO_PARA_PARTICIPANTE){
			ParticipanteAcaoExtensaoDao dao = null;
			try {
				
				dao = getDAO(ParticipanteAcaoExtensaoDao.class);
				participante = dao.findInformacoesParticipantesEmitirCertificadosEDeclaracoes( Integer.parseInt(comprovante.getIdentificador() ) ); 
	
				verificando = true;
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO)
					emitirCertificadoParticipante();
	
			} catch (Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
			}finally{
				if(dao != null) dao.close();
			}
		}else{
			
			if( tipoEmissaoCertificado == EMISSAO_CERTIFICADO_PARA_MEMBRO){
				
				MembroProjetoDao daoMembroProjeto = null;
				
				try {
					
					daoMembroProjeto = getDAO(MembroProjetoDao.class);
				
					membro = daoMembroProjeto.findInformacoesMembroProjetoParaEmissaoCertificado(Integer.parseInt(comprovante.getIdentificador() )  );
					
					verificando = true;
					if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO)
						emitirCertificadoMembroProjeto();
					
				} catch (Exception e) {
					addMensagemErroPadrao();
					notifyError(e);
				}finally{
					if(daoMembroProjeto != null) daoMembroProjeto.close();
				}
			}else{
				if( tipoEmissaoCertificado == EMISSAO_CERTIFICADO_PARA_DISCENTE_EXTENSAO){
					
					GenericDAO dao = null;
					
					try {
						
						dao = getGenericDAO();
						
						discenteExtensao = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), DiscenteExtensao.class);
						discenteExtensao.getAtividade().setMembrosEquipe(getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", discenteExtensao.getAtividade().getProjeto().getId()));
						
						verificando = true;
						if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO)
							emitirCertificadoDiscenteExtensao();
					} catch (Exception e) {
						addMensagemErroPadrao();
						notifyError(e);
					}finally{
						if(dao != null) dao.close();
					}
				}
			}
		}
		
	}
	
	
	/**
	 * Utilizado na validação da autenticidade do certificado
	 * a partir do portal público. Valida o código de autenticação e o número 
	 * do documento.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não chamado diretamente por jsp.</li>
	 * </ul>
	 * 
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		
		// verifica para que tipo de participante foi emitido o certificado 
		if(comprovante.getDadosAuxiliares() != null && comprovante.getDadosAuxiliares().equalsIgnoreCase("P")){
			tipoEmissaoCertificado = EMISSAO_CERTIFICADO_PARA_PARTICIPANTE;
		}else{
			if(comprovante.getDadosAuxiliares() != null && comprovante.getDadosAuxiliares().equalsIgnoreCase("M")){
				tipoEmissaoCertificado = EMISSAO_CERTIFICADO_PARA_MEMBRO;
			}else{
				if(comprovante.getDadosAuxiliares() != null && comprovante.getDadosAuxiliares().equalsIgnoreCase("D")){
					tipoEmissaoCertificado = EMISSAO_CERTIFICADO_PARA_DISCENTE_EXTENSAO;
				}else{
					return false;
				}
			}
		}
		
		
		if( tipoEmissaoCertificado == EMISSAO_CERTIFICADO_PARA_PARTICIPANTE){
			ParticipanteAcaoExtensaoDao dao = null;
			try {
				
				dao = getDAO(ParticipanteAcaoExtensaoDao.class);
				participante = dao.findInformacoesParticipantesEmitirCertificadosEDeclaracoes( Integer.parseInt(comprovante.getIdentificador() ) );  
	
				String codigoVerificacao = "";
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO) {
					codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante,  montaDadosEmissaoCertificadoParticipante().getSemente());
				}
	
				if (codigoVerificacao.equals(comprovante.getCodigoSeguranca())) {
					return true;
				}
	
			} catch (Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
			}finally{
				if(dao != null) dao.close();
			}
		}else{
			
			if( tipoEmissaoCertificado == EMISSAO_CERTIFICADO_PARA_MEMBRO){
				
				MembroProjetoDao daoMembroProjeto = null;
				
				try {
					
					daoMembroProjeto = getDAO(MembroProjetoDao.class);
					membro = daoMembroProjeto.findInformacoesMembroProjetoParaEmissaoCertificado( Integer.parseInt(comprovante.getIdentificador())  );
					
					String codigoVerificacao = "";
					if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO) {
						codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, montaDadosEmissaoCertificadoMembroProjeto().getSemente());
					}
					
					if (codigoVerificacao.equals(comprovante.getCodigoSeguranca())) {
						return true;
					}
					
				}catch (Exception e) {
					addMensagemErroPadrao();
					notifyError(e);
				}finally{
					if(daoMembroProjeto != null) daoMembroProjeto.close();
				}
			}else{
				
				if( tipoEmissaoCertificado == EMISSAO_CERTIFICADO_PARA_DISCENTE_EXTENSAO){
				
					GenericDAO dao = null;
					
					try {
						
						dao = getGenericDAO();
						
						discenteExtensao = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), DiscenteExtensao.class);
						discenteExtensao.getAtividade().setMembrosEquipe(getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", discenteExtensao.getAtividade().getProjeto().getId()));
						
						String codigoVerificacao = "";
						if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO) {
							codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante,montaDadosEmissaoCertificadoDiscenteExtensao().getSemente());
						}
						
						if (codigoVerificacao.equals(comprovante.getCodigoSeguranca())) {
							return true;
						}
						
					}catch (Exception e) {
						addMensagemErroPadrao();
						notifyError(e);
					}finally{
						if(dao != null) dao.close();
					}
				}
				
			}
		}
		
		return false;
	}
	

	///////////////////////// set  e get //////////////////////////////////
	
	
	public ParticipanteAcaoExtensao getParticipante() {
		return participante;
	}

	public void setParticipante(ParticipanteAcaoExtensao participante) {
		this.participante = participante;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	public boolean isVerificando() {
		return verificando;
	}

	public void setVerificando(boolean verificando) {
		this.verificando = verificando;
	}

	public Boolean getIsEmissaoByCoordenador() {
		return isEmissaoByCoordenador;
	}

	public void setIsEmissaoByCoordenador(Boolean isEmissaoByCoordenador) {
		this.isEmissaoByCoordenador = isEmissaoByCoordenador;
	}

	public MembroProjeto getMembro() {
		return membro;
	}

	public void setMembro(MembroProjeto membro) {
		this.membro = membro;
	}
	
	public DiscenteExtensao getDiscenteExtensao() {
		return discenteExtensao;
	}

	public void setDiscenteExtensao(DiscenteExtensao discenteExtensao) {
		this.discenteExtensao = discenteExtensao;
	}
	
}






/** Classe para organizar melhor os dados do certificado. */
class DadosEmissaoCertificado{
	
	private String nomeDeclaracao;
	private String identificacaoDeclaracao;
	private String tituloAtividade;
	private String tipoAtividade;
	private String coordenadorAtividade;
	private String unidadeAtividade;
	private String horasAtividadeDesenvolvida;
	private String tipoParticipante;
	private String cargaHorariaSeCursoEvento;
	private String tipoParticipacao;
	private String dataInicioFormatada;
	private String dataFimFormatada;
	private String frequenciaMinima;
	private String observacaoCertificado; // para participantes o coordenador pode informar alguma observação.
	
	public DadosEmissaoCertificado() {
		super();
	}
	
	public String getSemente() {
		return nomeDeclaracao+identificacaoDeclaracao+tituloAtividade+unidadeAtividade
				+cargaHorariaSeCursoEvento+tipoParticipacao+ dataInicioFormatada+dataFimFormatada
				+horasAtividadeDesenvolvida;
	}


	public String getNomeDeclaracao() {
		return nomeDeclaracao;
	}
	public void setNomeDeclaracao(String nomeDeclaracao) {
		this.nomeDeclaracao = nomeDeclaracao;
	}
	public String getIdentificacaoDeclaracao() {
		return identificacaoDeclaracao;
	}
	public void setIdentificacaoDeclaracao(String identificacaoDeclaracao) {
		this.identificacaoDeclaracao = identificacaoDeclaracao;
	}
	public String getTituloAtividade() {
		return tituloAtividade;
	}
	public void setTituloAtividade(String tituloAtividade) {
		this.tituloAtividade = tituloAtividade;
	}
	public String getTipoAtividade() {
		return tipoAtividade;
	}
	public void setTipoAtividade(String tipoAtividade) {
		this.tipoAtividade = tipoAtividade;
	}
	public String getUnidadeAtividade() {
		return unidadeAtividade;
	}
	public void setUnidadeAtividade(String unidadeAtividade) {
		this.unidadeAtividade = unidadeAtividade;
	}
	public String getTipoParticipante() {
		return tipoParticipante;
	}
	public void setTipoParticipante(String tipoParticipante) {
		this.tipoParticipante = tipoParticipante;
	}
	public String getCargaHorariaSeCursoEvento() {
		return cargaHorariaSeCursoEvento;
	}
	public void setCargaHorariaSeCursoEvento(String cargaHorariaSeCursoEvento) {
		this.cargaHorariaSeCursoEvento = cargaHorariaSeCursoEvento;
	}
	public String getTipoParticipacao() {
		return tipoParticipacao;
	}
	public void setTipoParticipacao(String tipoParticipacao) {
		this.tipoParticipacao = tipoParticipacao;
	}
	public String getHorasAtividadeDesenvolvida() {
		return horasAtividadeDesenvolvida;
	}
	public void setHorasAtividadeDesenvolvida(String horasAtividadeDesenvolvida) {
		this.horasAtividadeDesenvolvida = horasAtividadeDesenvolvida;
	}

	public String getDataInicioFormatada() {
		return dataInicioFormatada;
	}
	public void setDataInicioFormatada(String dataInicioFormatada) {
		this.dataInicioFormatada = dataInicioFormatada;
	}
	public String getDataFimFormatada() {
		return dataFimFormatada;
	}
	public void setDataFimFormatada(String dataFimFormatada) {
		this.dataFimFormatada = dataFimFormatada;
	}
	public String getCoordenadorAtividade() {
		return coordenadorAtividade;
	}
	public void setCoordenadorAtividade(String coordenadorAtividade) {
		this.coordenadorAtividade = coordenadorAtividade;
	}
	public String getFrequenciaMinima() {
		return frequenciaMinima;
	}
	public void setFrequenciaMinima(String frequenciaMinima) {
		this.frequenciaMinima = frequenciaMinima;
	}
	public String getObservacaoCertificado() {
		return observacaoCertificado;
	}
	public void setObservacaoCertificado(String observacaoCertificado) {
		this.observacaoCertificado = observacaoCertificado;
	}
}



