/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/12/2008
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/*******************************************************************************
 * 
 * <p>MBean responsavel por gerar declara��es de participan��o em extens�o </p> 
 * 
 * 
 * <p> <strong>S� usar esse MBean para gerar as declara��es, por favor.</strong></p>
 * 
 * 
 * <p>Ao contr�rios do que geralmente ocorre dos casos e uso, concentar tudo num �nico Mbean 
 * ajuda a menter o sistema. Se o usu�rio fica vendo duas declara��es diferentes dependo do lugar que gerar a declara��o.
 * O m�todo que gera o texto da declara��o no sistema era para ser �nico !!!!!!!!!!!!!!!!!!
 * </p>
 * 
 * <p> A autentica��o dos documentos � realizada pelo SIGAA atrav�s da gera��o de
 * um c�digo de documento e c�digo de verifica��o que pode ser validado atrav�s
 * do portal p�blico do SIGAA.</p> 
 * 
 * 
 * @author Ilueny Santos
 * @author Jadson Santos
 * 
 ******************************************************************************/
@Component("declaracaoExtensaoMBean")
@Scope("request")
public class DeclaracaoExtensaoMBean extends SigaaAbstractController<ParticipanteAcaoExtensao> implements AutValidator {

	/** nomes dos documentos */
	public static final String DECLARACAO = "trf10189_DeclaracaoProex";

	/** O participante para o qual ser� emitirdo a declara��o.
	 *  O id desse participante � passado via  <f:setPropertyActionListener>    */ 
	private ParticipanteAcaoExtensao participante = new ParticipanteAcaoExtensao();

	/**
	 *  Para emiss�o de declara��es de membros de projeto.
	 *  O id desse participante � passado via  <f:setPropertyActionListener>
	 */
	private MembroProjeto membro = new MembroProjeto();
	
	
	/** Se � o proprio particiapante que est� emitindo ou � o coordenador da a��o. porque as regras s�o um pouco diferentes.
	 * setado via  <f:setPropertyActionListener>
	 */
	private Boolean isEmissaoByCoordenador;
	
	
	/** Comprovante de emiss�o de documento autenticado */
	private EmissaoDocumentoAutenticado comprovante;

	
	/** Usado para verificar se o documento esta sendo validado pela �rea publica ou se esta sendo feita uma emiss�o de documento autenticado. */
	private boolean verificando = false;

	
	
	
	/**
	 * <p>Gera a declara��o para o participante selecionado.</p>
	 * 
	 * <p>   Esse m�todo � chamando das p�gina JSP abaixo passando com o id do participante j� populado, 
	 *    ent�o deve-se buscar as outras informa��es usadas para emitir a declara��o.
	 *  </p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/DocumentosAutenticados/lista.jsp</li>
	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li>
	 * 		<li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 * 
	 *      <li>/sigaa.war/public/extensao/paginaGerenciaMinhaInscricaoCursosEventosExtensao.jsp</li>
	 *      <li>/sigaa.war/public/extensao/paginaGerenciaMinhaParticipacaoCursosEventosExtensao.jsp</li>
	 * </ul>
	 *  
	 * @throws DAOException
	 * @throws SegurancaException
	 * 
	 */
	public String emitirDeclaracaoParticipante() throws DAOException, SegurancaException {

		ParticipanteAcaoExtensaoDao dao = null;
		
		try {
			
			dao = getDAO(ParticipanteAcaoExtensaoDao.class);
			
			// Recupera aquelas inforam��es usadas para emitir o certificado ou declara��o de extens�o.
			if(! verificando)
				participante = dao.findInformacoesParticipantesEmitirCertificadosEDeclaracoes(participante.getId()); 
			else{
				// os dados j� vem preenchidos pelo m�todo exibir !!!
			}
			
			if(isEmissaoByCoordenador != null && isEmissaoByCoordenador == true){
				participante.verificaEmissaoDeclaracaoCoordenador();
			}else{
				participante.verificaEmissaoDeclaracaoParticipante();
			}
			
			DadosEmissaoDeclaracao dadosDeclaracao =  montaDadosEmissaoDeclaracaoParticipante();
			
			// montagem do corpo da declara��o
			String textoDeclaracao = getTextoDeclaracao(dadosDeclaracao);

			AtividadeExtensao atividade =  
							( ! ValidatorUtil.isEmpty( participante.getAtividadeExtensao()) ) 
							? participante.getAtividadeExtensao()
							: participante.getSubAtividade().getAtividade(); 
			
			inscreDadosDeclaracao(textoDeclaracao, atividade.getCoordenacao().getPessoa().getNomeAbreviado(), true);
			
			return null;
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao tentar emitir a declara��o do participante.");
			return null;
		}finally{
			if(dao != null) dao.close();
		}

	}
	
	
	/**
	 * <p>Gera a declara��o para o participante selecionado.</p>
	 * 
	 * <p>   Esse m�todo � chamando das p�gina JSP abaixo passando com o id do participante j� populado, 
	 *    ent�o deve-se buscar as outras informa��es usadas para emitir a declara��o.
	 *  </p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/DocumentosAutenticados/lista.jsp</li>
	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li>
	 * 		<li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 * </ul>
	 *  
	 * @throws DAOException
	 * @throws SegurancaException
	 * 
	 */
	public String emitirDeclaracaoMembroProjeto() throws DAOException, SegurancaException {

		AtividadeExtensaoDao dao = null;
		
		try {
			
			dao = getDAO(AtividadeExtensaoDao.class);
			
			// Recupera aquelas inforam��es usadas para emitir o certificado ou declara��o de extens�o.
			if(! verificando){
				membro = dao.findByPrimaryKey(membro.getId(), MembroProjeto.class);
				dao.refresh(membro.getProjeto().getCoordenador());
				AtividadeExtensao atividade = dao.findAcaoByProjeto(membro.getProjeto().getId());
				membro.setAtividade(atividade);
			}
			
			if( ! membro.isPassivelEmissaoDeclaracao() ){
				addMensagemErro("N�o � poss�vel emitir a declara��o para esse participante.");
				return null;
			}
			
			
			DadosEmissaoDeclaracao dadosDeclaracao =  montaDadosEmissaoDeclaracaoMembroProjeto();
			
			// montagem do corpo da declara��o
			String textoDeclaracao = getTextoDeclaracao(dadosDeclaracao);
			
			inscreDadosDeclaracao(textoDeclaracao, membro.getProjeto().getCoordenador().getPessoa().getNomeAbreviado(), false);

			
			return null;

		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao tentar emitir a declara��o do participante.");
			return null;
		}finally{
			if(dao != null) dao.close();
		}

	}
	
	
	
	
	
	
	//////////////         m�todos auxiliares privados                /////////////
	
	
	
	/**
	 * Gera a semente utilizada no c�digo de autentica��o da declara��o.
	 * Deve-se utilizar para gera��o da semente, todos os dados vari�veis da 
	 * declara��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return String com a semente gerada.
	 */
	private String gerarSementeDeclaracao(boolean isDeclaracaoParticipante) {
		
		/*
		 * Se mudar o nome, CPF, passaporte, data de nascimento, titulo a atividade, a unidade, a fun��o,
		 * e a data de incio e fim da atividade invalida a daclara��o.
		 * 
		 * Na declara��o n�o usa a carga hor�ria do participante, pois como o curso/evento n�o terminou ainda, a carga hor�ria pode mudar.
		 * 
		 * Somente no certificado � usado a Carga hor�ria.
		 */
		if(isDeclaracaoParticipante){
			DadosEmissaoDeclaracao dadosDeclaracao =  montaDadosEmissaoDeclaracaoParticipante();
			return dadosDeclaracao.getSemente();
		}else{
			DadosEmissaoDeclaracao dadosDeclaracao =  montaDadosEmissaoDeclaracaoMembroProjeto();
			return dadosDeclaracao.getSemente();
		}
	}
	

	/** Organiza as inforama��es de declara��o em forma objeto para evitar duplica��o de c�digo */
	private DadosEmissaoDeclaracao montaDadosEmissaoDeclaracaoParticipante(){
		
		DadosEmissaoDeclaracao dadosEmissao = new DadosEmissaoDeclaracao();
		
		dadosEmissao.setNomeDeclaracao( participante.getCadastroParticipante().getNome() );
		
		if(participante.getCadastroParticipante().getCpf() != null)
			dadosEmissao.setIdentificacaoDeclaracao( " CPF "+participante.getCadastroParticipante().getCpf());
		else{
			dadosEmissao.setIdentificacaoDeclaracao( "PASSAPORTE "+participante.getCadastroParticipante().getPassaporte()+" DATA NASCIMENTO "+participante.getCadastroParticipante().getDataNascimentoFormatada());
		}
		
		boolean isParticipacaoAtividade = ! ValidatorUtil.isEmpty( participante.getAtividadeExtensao());
		
		boolean iscursoOuEvento = false;
		
		if(isParticipacaoAtividade){
			iscursoOuEvento = (participante.getAtividadeExtensao().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO 
					|| participante.getAtividadeExtensao().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO);
		}else{
			iscursoOuEvento = (participante.getSubAtividade().getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO 
					|| participante.getSubAtividade().getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO);
		}		
		
		dadosEmissao.setTituloAtividade(   ( isParticipacaoAtividade ?  participante.getAtividadeExtensao().getTitulo() : participante.getSubAtividade().getTitulo() ) );
		
		dadosEmissao.setTipoAtividade ( isParticipacaoAtividade ? " na Atividade de Extens�o " : " na Mini Atividade de Extens�o " );
		dadosEmissao.setUnidadeAtividade( isParticipacaoAtividade ? participante.getAtividadeExtensao().getUnidade().getNome() : participante.getSubAtividade().getAtividade().getUnidade().getNome() );	
		
		String cargaHorariaSeCursoEvento = "";
    	if (iscursoOuEvento ) {
    		int ch = participante.getChCertificadoDeclaracao();
    		if(ch > 0 )
    			cargaHorariaSeCursoEvento = ", cumprinto at� o momento uma carga hor�ria de " + participante.getChCertificadoDeclaracao() + " hora(s)"; 
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

	
	/** Organiza as inforama��es de declara��o em forma objeto para evitar duplica��o de c�digo */
	private DadosEmissaoDeclaracao montaDadosEmissaoDeclaracaoMembroProjeto(){
		
		DadosEmissaoDeclaracao dadosEmissao = new DadosEmissaoDeclaracao();
		
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
			dadosEmissao.setIdentificacaoDeclaracao( " MATR�CULA "+membro.getDiscente().getMatricula());
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
		
		dadosEmissao.setTipoAtividade ( " na Atividade de Extens�o " );
		dadosEmissao.setUnidadeAtividade( membro.getProjeto().getUnidade().getNome() );	
		
		String cargaHorariaSeCursoEvento = "";
    	if (iscursoOuEvento ) {
    		int ch = membro.getAtividade().getCursoEventoExtensao().getCargaHoraria();
    		if(ch > 0 )
    			cargaHorariaSeCursoEvento = ", cumprinto at� o momento uma carga hor�ria de " + membro.getAtividade().getCursoEventoExtensao().getCargaHoraria() + " hora(s) semanal"; 
    	}
		
    	dadosEmissao.setCargaHorariaSeCursoEvento(cargaHorariaSeCursoEvento);
    	dadosEmissao.setTipoParticipacao( membro.getFuncaoMembro().getDescricao() );
    	
    	dadosEmissao.setHorasAtividadeDesenvolvida(""+membro.getChCertificadoDeclaracao() );
    	
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
	 * Retorna o texto(corpo) da declara��o.
	 * 
	 * No Forma:
	 * <pre>
	 *  Declaramos para os devidos fins que, o(a) Professor Jos� da Silva, CPF 1234567890,
	 *  est� inscrito como participante na Atividade 'Atividade de Teste' cumprinto at� o momento uma carga hor�ria de 100 hora(s), promovida pelo(a)
	 *  Curso de corte e costura
	 *  na fun��o de ALUNO(A)
	 *  , com 20 horas de atividades desenvolvidas,
	 *  , que ocorrer� no per�odo de 01/12/2012 a 31/12/2012.
	 * </pre>
	 * 
	 * @return texto utilizado na declara��o.
	 */
	private String getTextoDeclaracao(DadosEmissaoDeclaracao dados) {
			
	    	String textoDeclaracao = "";
	    	
	    	textoDeclaracao =  "Declaramos para os devidos fins que,"
	    			+( StringUtils.notEmpty(dados.getTipoParticipante()) ? dados.getTipoParticipante() : "")
	    			+" "+ dados.getNomeDeclaracao();
	    	
			textoDeclaracao += ", " +dados.getIdentificacaoDeclaracao()+", ";	
			
					
			textoDeclaracao +=" est� inscrito como participante "
					+ dados.getTipoAtividade()
					+ " "+dados.getTituloAtividade().toUpperCase()+ "" 
					+  ", que ocorrer� no per�odo de "+ dados.getDataInicioFormatada()+ " a "+ dados.getDataFimFormatada()	
					+ dados.getCargaHorariaSeCursoEvento() + ", promovida pelo(a) "
					+ dados.getUnidadeAtividade()
					+ " na fun��o de "+ dados.getTipoParticipacao();
			
			if(StringUtils.notEmpty(dados.getHorasAtividadeDesenvolvida())){
				textoDeclaracao += ", com "+dados.getHorasAtividadeDesenvolvida()+ " hora(s) semanais de atividades desenvolvidas ";
			}
			
			textoDeclaracao += ".";	
			
			return textoDeclaracao;
	}
	
	
	
	
	/** 
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws JRException 
	 * @throws SQLException 
	 * @throws IOException */
	private void inscreDadosDeclaracao(String texto, String nomeCoordenador, boolean isDeclaracaoParticipante) throws ArqException, NegocioException, JRException, SQLException, IOException{
		// Gerar declara��o
		Connection con = null;
		HashMap<Object, Object> parametros = new HashMap<Object, Object>();

		// gerando c�digo de autentica��o...
		if (!verificando) {
			
			comprovante = geraEmissao(
					TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO,
					(isDeclaracaoParticipante ? ""+participante.getId(): ""+membro.getId() ),                     // O id do participante ou membro identifica o certificado ( serve para buscar no banco e validar as informa��es da declara��o )
					gerarSementeDeclaracao(isDeclaracaoParticipante),                      // a semente para gerar o c�digo de valida��o, se a semente mudar invalida a declara��o            
					(isDeclaracaoParticipante ? "P" : "M"),        // dados auxiliares para saber se a declara��o � para participante ou membro do projeto
					(isDeclaracaoParticipante ?SubTipoDocumentoAutenticado.DECLARACAO_PARTICIPANTE_PUBLICO_ALVO_EXTENSAO: SubTipoDocumentoAutenticado.DECLARACAO_PARTICIPANTE_MEMBRO_EQUIPE_EXTENSAO ) ,
					true);

		}

		// setando par�metros
		parametros.put("codigoSeguranca", comprovante.getCodigoSeguranca());
		parametros.put("siteVerificacao", ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));

		parametros.put("cidade",  RepositorioDadosInstitucionais.get("cidadeInstituicao")+", " + Formatador.getInstance().formatarDataDiaMesAno( comprovante.getDataEmissao()));
		parametros.put("texto", texto);
		parametros.put("coordenacao", nomeCoordenador);
		parametros.put("numero_documento", comprovante.getNumeroDocumento());

		
		con = Database.getInstance().getSigaaConnection();
		JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(DECLARACAO + ".jasper"), parametros, con);

		getCurrentResponse().setContentType("application/pdf");
		getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=DECLARACAO_PARTICIPANTE_PROEX_"+ (isDeclaracaoParticipante ? ""+participante.getId(): ""+membro.getId() ) + ".pdf");
		JasperExportManager.exportReportToPdfStream(prt, getCurrentResponse().getOutputStream());

		FacesContext.getCurrentInstance().responseComplete();
		con.close();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////// M�todos para verifica a auteticidade de docuemento ////////////////
	

	/**
	 * Utilizado na valida��o da autenticidade da declara��o
	 * a partir do portal p�blico.
	 * 
	 * N�o chamado diretamente por jsp.
	 * 
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante, HttpServletRequest req, HttpServletResponse res) {

		
		this.comprovante = comprovante;
		verificando = true;

		boolean isComprovanteParticipante = true;
		
		if(comprovante.getDadosAuxiliares() != null 
				&& comprovante.getDadosAuxiliares().equalsIgnoreCase("P")){
			isComprovanteParticipante = true;
		}else{
			isComprovanteParticipante = false;
		}
		
		if( isComprovanteParticipante){
			
			ParticipanteAcaoExtensaoDao dao = null;
			try {
				dao = getDAO(ParticipanteAcaoExtensaoDao.class);
				
				participante = dao.findInformacoesParticipantesEmitirCertificadosEDeclaracoes( Integer.parseInt(comprovante.getIdentificador() ) );  
	
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
					emitirDeclaracaoParticipante();
				}
	
			} catch (Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
			}finally{
				if(dao != null) dao.close();
			}
		}else{
			
			AtividadeExtensaoDao dao = null;
			
			try {
				
				dao = getDAO(AtividadeExtensaoDao.class);
				membro = dao.findByPrimaryKey( Integer.parseInt(comprovante.getIdentificador() ), MembroProjeto.class);
				dao.refresh(membro.getProjeto().getCoordenador());
				AtividadeExtensao atividade = dao.findAcaoByProjeto(membro.getProjeto().getId());
				membro.setAtividade(atividade);
				
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
					emitirDeclaracaoMembroProjeto();
				}
				
			}catch (Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
			}finally{
				if(dao != null) dao.close();
			}
		}
	}

	
	/**
	 * Utilizado na valida��o da autenticidade da declara��o
	 * a partir do portal p�blico. Valida o c�digo de autentica��o e o n�mero 
	 * do documento.
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {

		boolean isComprovanteParticipante = true;
		
		if(comprovante.getDadosAuxiliares() != null && comprovante.getDadosAuxiliares().equalsIgnoreCase("P")){
			isComprovanteParticipante = true;
		}else{
			isComprovanteParticipante = false;
		}
		
		if( isComprovanteParticipante){
			ParticipanteAcaoExtensaoDao dao = null;
			try {
				
				dao = getDAO(ParticipanteAcaoExtensaoDao.class);
				participante = dao.findInformacoesParticipantesEmitirCertificadosEDeclaracoes( Integer.parseInt(comprovante.getIdentificador() ) );  
	
				String codigoVerificacao = "";
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
					codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSementeDeclaracao(true));
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
			AtividadeExtensaoDao dao = null;
			
			try {
				
				dao = getDAO(AtividadeExtensaoDao.class);
				membro = dao.findByPrimaryKey( Integer.parseInt(comprovante.getIdentificador() ) , MembroProjeto.class);
				dao.refresh(membro.getProjeto().getCoordenador());
				AtividadeExtensao atividade = dao.findAcaoByProjeto(membro.getProjeto().getId());
				membro.setAtividade(atividade);
				
				String codigoVerificacao = "";
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
					codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSementeDeclaracao(false));
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
		
		return false;
	}
	

	
	////////// sets e gets ////////
	
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
	public MembroProjeto getMembro() {
		return membro;
	}
	public void setMembro(MembroProjeto membro) {
		this.membro = membro;
	}
	public Boolean getIsEmissaoByCoordenador() {
		return isEmissaoByCoordenador;
	}
	public void setIsEmissaoByCoordenador(Boolean isEmissaoByCoordenador) {
		this.isEmissaoByCoordenador = isEmissaoByCoordenador;
	}
}





/** Classe para organizar melhor os dados de declara��o. */
class DadosEmissaoDeclaracao{
	
	private String nomeDeclaracao;
	private String identificacaoDeclaracao;
	private String tituloAtividade;
	private String tipoAtividade;
	private String unidadeAtividade;
	private String tipoParticipante;
	private String cargaHorariaSeCursoEvento;
	private String tipoParticipacao;
	private String horasAtividadeDesenvolvida;
	private String dataInicioFormatada;
	private String dataFimFormatada;
	
	public DadosEmissaoDeclaracao() {
		super();
	}
	
	public String getSemente() {
		return nomeDeclaracao+identificacaoDeclaracao+tituloAtividade+unidadeAtividade
				+tipoParticipacao+ dataInicioFormatada+dataFimFormatada;
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
	
	
}

