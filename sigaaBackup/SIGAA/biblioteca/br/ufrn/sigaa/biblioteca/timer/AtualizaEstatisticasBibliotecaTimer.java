/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 12/03/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.timer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.RegistroEstatisticasConsultaAcervo.TipoOperacaoRegistroConsultas;

/**
* <p>Timer di�rio que deve consultar os registro na tabela <code>registro_estatisticas_biblioteca</code>, realizar o calculo 
* das estatisticas e atualizar o cache para se poder ordenar os resultados das consultas pelas informa��es levantas ao longo do tempo.</p>
*
* <p>O objetivo � melhorar a sem�ntica da busca e ajudar os usu�rios a encontrarem o que desejam dentro de tantos t�tulos e materiais do acervo.</p>
*
*<p>
* <strong>Observa��o: </strong> inserir um registro na tabela INFRA.REGISTRO_TIMER (Banco SISTEMAS_COMUM). Vai possuir 2 par�metros: 
*  horaExecucao: 0h 
*  tipoReplicacao: D = Di�rio
*</p>
*
* 
* @author jadson
*
*/
public class AtualizaEstatisticasBibliotecaTimer extends TarefaTimer{

	
	@Override
	public void run() {
		verificarEstatisticas();
		
	}
	
	/** Verifica as estat�sticas que s�o registradas para a biblioteca atualiza��o o cache utilizado nas consultas do acervo.*/
	public void verificarEstatisticas(){
		
		TituloCatalograficoDao dao = null;	
		
		try{
			
			dao = DAOFactory.getInstance().getDAO(TituloCatalograficoDao.class);
		
			verificarEstatisticasTitulosConsultados(dao);
			verificarEstatisticasTitulosVisualizados(dao);
			verificarEstatisticasTitulosEmprestados(dao);
			marcaRegistrosProcessados(dao);
			apagaRegistroJaProcessados(dao);
			
			enviaEmailNotificacaoAdministradorSistema();
			
		} catch (Exception ex) {
			enviaEmailErroEnvioInformativo(ex);
		}finally{
			if(dao != null) dao.close();
		}
	}

	

	/** Atualiza a quantidade de consulta no cache dos T�tulos mais consultados */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void verificarEstatisticasTitulosConsultados(TituloCatalograficoDao dao) throws DAOException {
		JdbcTemplate template = null;
		template = new JdbcTemplate( Database.getInstance().getSigaaDs());
		
		// recupera apenas os 100 primeiros mais consultados ainda n�o precessados //
		String sqlTitulosMaisConsultados =  
									" SELECT id_titulo_catalografico, sum(quantidade) as quantidadeConsultas "+
									" FROM biblioteca.registro_estatisticas_consultas_acervo_biblioteca"+
									" WHERE processado = falseValue() and tipo_operacao = "+TipoOperacaoRegistroConsultas.REGISTRAR_CONSULTA.ordinal()+
									" GROUP BY id_titulo_catalografico"+
									" ORDER BY quantidadeConsultas desc"+
									BDUtils.limit(100);   // limit a 100 resultados para n�o gerar uma quantidade excessiva de atualiza��es na tabela 
		
		Collection titulos = template.queryForList(sqlTitulosMaisConsultados);
		
		Iterator it = titulos.iterator();
		
		Map<String, Object> mapa = null;
		
		while(it.hasNext()){

			mapa = (Map) it.next();
			
			int idTitulo = (Integer) mapa.get("id_titulo_catalografico");
			BigDecimal quantidade = (BigDecimal) mapa.get("quantidadeConsultas");
			
			dao.atualizaQuantidadeTitulosConsultados(idTitulo, quantidade.longValue());
		
			
		}
		
	}
	
	
	
	
	
	
	
	/** Atualiza a quantidade de visualiza��es no cache dos T�tulos mais visualizados  */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void verificarEstatisticasTitulosVisualizados(TituloCatalograficoDao dao) throws DAOException {
		JdbcTemplate template = null;
		template = new JdbcTemplate( Database.getInstance().getSigaaDs());
		
		// recupera apenas os 100 primeiros mais consultados ainda n�o precessados //
		String sqlTitulosMaisConsultados =  
									" SELECT id_titulo_catalografico, sum(quantidade) as quantidadeConsultas "+
									" FROM biblioteca.registro_estatisticas_consultas_acervo_biblioteca"+
									" WHERE processado = falseValue() and tipo_operacao = "+TipoOperacaoRegistroConsultas.REGISTRAR_VISUALIZACAO.ordinal()+
									" GROUP BY id_titulo_catalografico"+
									" ORDER BY quantidadeConsultas desc"+
									BDUtils.limit(100);   // limit a 100 resultados para n�o gerar uma quantidade excessiva de atualiza��es na tabela 
		
	
		Collection titulos = template.queryForList(sqlTitulosMaisConsultados);
		
		Iterator it = titulos.iterator();
		
		Map<String, Object> mapa = null;
		
		while(it.hasNext()){

			mapa = (Map) it.next();
			
			int idTitulo = (Integer) mapa.get("id_titulo_catalografico");
			BigDecimal quantidade = (BigDecimal) mapa.get("quantidadeConsultas");
			
			dao.atualizaQuantidadeTitulosVisualizados(idTitulo, quantidade.longValue());
		
			
		}
		
	}
	
	
	
	
	

	
	/** Atualiza no cache a quantidade dos t�tulos mais emprestados */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void verificarEstatisticasTitulosEmprestados(TituloCatalograficoDao dao) throws DAOException {
		JdbcTemplate template = null;
		template = new JdbcTemplate( Database.getInstance().getSigaaDs());
		
		// recupera apenas os 100 primeiros mais consultados ainda n�o precessados //
		String sqlTitulosMaisConsultados =  
									" SELECT id_titulo_catalografico, sum(quantidade) as quantidadeConsultas "+
									" FROM biblioteca.registro_estatisticas_consultas_acervo_biblioteca"+
									" WHERE processado = falseValue() and tipo_operacao = "+TipoOperacaoRegistroConsultas.REGISTRAR_EMPRESTIMO.ordinal()+
									" GROUP BY id_titulo_catalografico"+
									" ORDER BY quantidadeConsultas desc"+
									BDUtils.limit(100);   // limit a 100 resultados para n�o gerar uma quantidade excessiva de atualiza��es na tabela 
		
	
		Collection titulos = template.queryForList(sqlTitulosMaisConsultados);
		
		Iterator it = titulos.iterator();
		
		Map<String, Object> mapa = null;
		
		while(it.hasNext()){

			mapa = (Map) it.next();
			
			int idTitulo = (Integer) mapa.get("id_titulo_catalografico");
			BigDecimal quantidade = (BigDecimal) mapa.get("quantidadeConsultas");
			
			dao.atualizaQuantidadeTitulosEmprestados(idTitulo, quantidade.longValue());
			
		}
		
		
	}
	

	
	
	
	
	/** Marca os registros como processados para n�o serem contados novamente */
	private void marcaRegistrosProcessados(TituloCatalograficoDao dao) {
		dao.update(" UPDATE biblioteca.registro_estatisticas_consultas_acervo_biblioteca SET processado = trueValue() where processado = falseValue() ");
	}
	
	
	
	/** Apaga registros j� processados quando a quantidade ficar grande demais para n�o comprometer a performace do sistema. */
	private void apagaRegistroJaProcessados(TituloCatalograficoDao dao) {
		
		JdbcTemplate template = null;
		template = new JdbcTemplate( Database.getInstance().getSigaaDs());
		
		String sqlQtdProcessados =  " select count(*) FROM biblioteca.registro_estatisticas_consultas_acervo_biblioteca  WHERE processado = trueValue() "; 

		Long qtdRegistrosProcessados = template.queryForLong(sqlQtdProcessados);
		
		if(qtdRegistrosProcessados != null && qtdRegistrosProcessados > 100000){ // se a quantidade passou 100 mil de registro processados apaga os processados
			
			dao.update(" DELETE FROM biblioteca.registro_estatisticas_consultas_acervo_biblioteca WHERE processado = trueValue() ");
			
			template.execute(" VACUUM FULL biblioteca.registro_estatisticas_consultas_acervo_biblioteca ");
		}
	}
	
	
	/**
	 * Envia um email com a informa��o do erro na execu���o da rotina para os administradores do sistema.
	 *
	 * @param siglaSigaa
	 * @param assuntoEmail
	 * @param e
	 */
	private void enviaEmailNotificacaoAdministradorSistema(){
		
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "ATUALIZA��O DAS ESTAT�STICAS DE CONSULTA DA BIBLIOTECA - EXECUTADO EM " + new Date();
		
		String mensagem = "Server: " + NetworkUtils.getLocalName() + "<br>Estat�sticas das consultas da biblioteca atualizadas com sucesso!!!  "; 
		
		MailBody mail = new MailBody();
		mail.setEmail(email);
		mail.setAssunto(assunto);
		mail.setMensagem(mensagem);
		
		Mail.send(mail);
	}
	
	
	
	/**
	 * Envia um email para os adminsitrados dos sistema informando que houve um erro na execu��o da rotina.
	 */
	private void enviaEmailErroEnvioInformativo(Exception ex) {
		String email =  ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "["+RepositorioDadosInstitucionais.get("siglaSigaa")+"]"+" Erro ao calcular as estat�sticas de consulta da biblioteca ";
		String mensagem =  "Server: " + NetworkUtils.getLocalName() + "<br>" +
		ex.getMessage() + "<br><br><br>" + Arrays.toString(ex.getStackTrace()).replace(",", "\n") +
		(ex.getCause() != null ? Arrays.toString(ex.getCause().getStackTrace()).replace(",", "\n") : "");
		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem );
		Mail.send(mail);
		
	}

	
}
