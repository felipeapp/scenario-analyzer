/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 *
 * Criado em 09/09/2009
 * Autor: Agostinho
 * 
 */
package br.ufrn.sigaa.ava.jsf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ava.dao.RelatorioAcessoDao;
import br.ufrn.sigaa.ava.dominio.LogLeituraSigaaTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.LogLeituraSigaaTurmaVirtualDetalhes;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Managed Bean respons�vel por gerar os relat�rios de acesso a Turma Virtual 
 * 
 * @author Agostinho
 */
@Component
@Scope("request")
public class RelatorioAcessoTurmaVirtualMBean extends ControllerTurmaVirtual {

	/** Lista de logs de leituras da turma virtual. */
	private List<LogLeituraSigaaTurmaVirtual> logLeituraTurmaVirtual = new ArrayList<LogLeituraSigaaTurmaVirtual>();
	/**  Nome do arquivo que foi visualizado. */
	private String nomeArquivoVisualizado;
	/** Logs de leitura de um determinado usu�rio. */
	private LogLeituraSigaaTurmaVirtual logUsuario;
	/** Faixa de dias que foram realizados logs.  */
	int[] dias = new int[10];
	/**Numero total de acessos na turma virtual*/
	Integer quantidadeTotalAcessos;
	/** indica que o relat�rio est� sendo acessado pelos v�deos */
	private Boolean video = false;
	/**
	 * Relat�rio dos arquivos baixados pelos usu�rios.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /ava/aulas.jsp
	 * @throws DAOException 
	 * @throws DAOException 
	 */
	public String gerarRelatorioArquivosAcessadosUsuarios() throws DAOException {
		
		video = getParameterBoolean("video");
		if (video == null)
			video = false;
		
	    logLeituraTurmaVirtual = getDAO(RelatorioAcessoDao.class).findArquivosAcessadosByDiscentes(turma().getId(), getParameterInt("id", 0));
	    if (logLeituraTurmaVirtual!=null) {
	    	
	    	setarNomesOrdenar(logLeituraTurmaVirtual);

	    	if (logLeituraTurmaVirtual.size() != 0)
	    		nomeArquivoVisualizado = logLeituraTurmaVirtual.get(0).getDetalhes().get(0).getDescricaoArquivoBaixado();
	    
	    	if (logLeituraTurmaVirtual == null) {
	    		logLeituraTurmaVirtual = new ArrayList<LogLeituraSigaaTurmaVirtual>();
	    		nomeArquivoVisualizado = "";
	    	}
	    	
	    	SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMMM 'de' yyyy '�s' HH:mm:ss");
	    	for (LogLeituraSigaaTurmaVirtual l : logLeituraTurmaVirtual)
	    		for (LogLeituraSigaaTurmaVirtualDetalhes d : l.getDetalhes())
	    			d.setDataExtenso(sdf.format(d.getData()));
	    	
	    }
	    return forward("/ava/Relatorios/relatorio_acesso_arquivos.jsp");
	}
	
	/**
	 * Exibe relat�rio sint�tico de acesso dos Discentes a Turma Virtual.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /ava/menu.jsp
	 * @return
	 * @throws DAOException
	 */
    public String gerarRelatorioSintetico() throws DAOException {
		
    	logLeituraTurmaVirtual.clear();
		
		List<LogLeituraSigaaTurmaVirtual> listSqlEntrouTurmaVirtual = getDAO(RelatorioAcessoDao.class).relatorioSinteticoAcessoTurmaVirtual(turma().getId(), RelatorioAcessoDao.ENTROU_TURMA_VIRTUAL, "sqlEntrouTurmaVirtual");
		List<LogLeituraSigaaTurmaVirtual> listSqlArquivo = getDAO(RelatorioAcessoDao.class).relatorioSinteticoAcessoTurmaVirtual(turma().getId(), RelatorioAcessoDao.ACESSO_ARQUIVOS, "sqlArquivo");
		List<LogLeituraSigaaTurmaVirtual> listSqlConteudoTurma = getDAO(RelatorioAcessoDao.class).relatorioSinteticoAcessoTurmaVirtual(turma().getId(), RelatorioAcessoDao.CONTEUDO_TURMA, "sqlConteudoTurma");
		
		if (listSqlEntrouTurmaVirtual !=null)
			setarNomesOrdenar(listSqlEntrouTurmaVirtual);
		
		if (listSqlArquivo !=null)
			setarNomesOrdenar(listSqlArquivo);
		
		if (listSqlConteudoTurma !=null)
			setarNomesOrdenar(listSqlConteudoTurma);

		if (listSqlEntrouTurmaVirtual !=null)
			for (LogLeituraSigaaTurmaVirtual iterSuperior : listSqlEntrouTurmaVirtual) {
				if (!logLeituraTurmaVirtual.contains(iterSuperior))
					logLeituraTurmaVirtual.add(iterSuperior);
			}
	    
		if (listSqlEntrouTurmaVirtual !=null)
			for (LogLeituraSigaaTurmaVirtual iterSuperior : listSqlEntrouTurmaVirtual) { 
	    	
	    	if (!logLeituraTurmaVirtual.contains(iterSuperior))
	    		logLeituraTurmaVirtual.add(iterSuperior);
	    	else {
	    		for (LogLeituraSigaaTurmaVirtual atualEntrouTurma : listSqlEntrouTurmaVirtual) {

	    			if (listSqlArquivo!=null)
	    				if (listSqlArquivo.indexOf(atualEntrouTurma) != -1)
	    					atualEntrouTurma.setQntArquivo( listSqlArquivo.get( listSqlArquivo.indexOf(atualEntrouTurma) ).getQntArquivo() );
    			
	    			if (listSqlConteudoTurma!=null)
	    				if (listSqlConteudoTurma.indexOf(atualEntrouTurma) != -1)
	    					atualEntrouTurma.setQntConteudoTurma( listSqlConteudoTurma.get( listSqlConteudoTurma.indexOf(atualEntrouTurma) ).getQntConteudoTurma() );
	    				
	    				atualEntrouTurma.setQntEntrouTurmaVirtual(atualEntrouTurma.getQntEntrouTurmaVirtual());
		    			logLeituraTurmaVirtual.set( (logLeituraTurmaVirtual.indexOf(atualEntrouTurma)), atualEntrouTurma);
	    			}
    		}
	    }
	    
    	return forward("/ava/Relatorios/relatorio_acesso_sintetico.jsp");
    }
    
	/**
	 * Exibe relat�rio detalhado de acesso a Turma Virtual.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /ava/Relatorios/relatorio_acesso_sintetico.jsp
	 * @return
	 * @throws DAOException
	 */
	public void gerarRelatorioDetalhado( ActionEvent evt ) throws DAOException {
		int idUsuario = getParameterInt("id", 0);
		ArrayList<LogLeituraSigaaTurmaVirtual> logs = (ArrayList<LogLeituraSigaaTurmaVirtual>) getDAO(RelatorioAcessoDao.class).relatorioAcessoDiscentesTurmaVirtual(idUsuario, turma().getId());
		
		if ( logs != null )
			for ( LogLeituraSigaaTurmaVirtual log : logs ){
				if ( log.getIdUsuario() == idUsuario ) 
					logUsuario = log;
			}
			
		if ( logLeituraTurmaVirtual != null )
			for ( LogLeituraSigaaTurmaVirtual log: logLeituraTurmaVirtual ){
				if ( log.getIdUsuario() == idUsuario ) 
					logUsuario.setNomeDiscente(log.getNomeDiscente());
			}
		
		if ( logUsuario.getIdUsuario() == idUsuario){
			
			boolean acessouArquivos = false;
			for ( LogLeituraSigaaTurmaVirtualDetalhes detalhes : logUsuario.getDetalhes() )
				if ( detalhes.getArquivoBaixado() != 0 ){
					acessouArquivos = true;
					break;
				}
			
			if (acessouArquivos) {
				List<LogLeituraSigaaTurmaVirtual> usuario = new ArrayList<LogLeituraSigaaTurmaVirtual>();
				usuario.add(logUsuario);
				int [] arquivosBaixados = new int [logUsuario.getDetalhes().size()];
				int i = 0;
					for ( LogLeituraSigaaTurmaVirtualDetalhes detalhes : logUsuario.getDetalhes() ){
						arquivosBaixados[i] = detalhes.getArquivoBaixado();
						i++;
					}
				getDAO(RelatorioAcessoDao.class).findArquivoByDiscente(arquivosBaixados,usuario);
			}
		}
	}
	
	/**
	 * Seta os nomes dos usu�rios e ordena a exibi��o dos dados no relat�rio.
	 * 
	 * @param usersAcessaramTurmaVirtual
	 * @throws DAOException
	 */
	private void setarNomesOrdenar(List<LogLeituraSigaaTurmaVirtual> usersAcessaramTurmaVirtual) throws DAOException {
		
		RelatorioAcessoDao dao = getDAO(RelatorioAcessoDao.class);
        List<Usuario> usersLocalizados = new ArrayList<Usuario>();
        
        usersLocalizados = dao.findUsuariosAcessaramTurmaVirtual( gerarIdsUsuarios(usersAcessaramTurmaVirtual) );
          
	    // seta nomes dos usu�rios 
        for (int i = 0; i < usersLocalizados.size(); i++) {
        	for (int j = 0; j < usersAcessaramTurmaVirtual.size(); j++) {
        		if (usersLocalizados.get(i).getId() == usersAcessaramTurmaVirtual.get(j).getIdUsuario()) {
        			usersAcessaramTurmaVirtual.get(j).setNomeDiscente(usersLocalizados.get(i).getPessoa().getNome());
                }
            }
        }
          
  		// caso o usu�rio deseje visualizar o relat�rio detalhado, localiza os arquivos baixados pelo Discente
		usersAcessaramTurmaVirtual = dao.findArquivoByDiscente( gerarIdsArquivosBaixados(), logLeituraTurmaVirtual);
          
          final Comparator<LogLeituraSigaaTurmaVirtual> NOME = new Comparator<LogLeituraSigaaTurmaVirtual>() {
              public int compare(LogLeituraSigaaTurmaVirtual o1, LogLeituraSigaaTurmaVirtual o2) {
                  int primeiraComparacao = o1.getNomeDiscente().compareTo(o2.getNomeDiscente());
                  return (primeiraComparacao != 0 ? primeiraComparacao : o1.getNomeDiscente().compareTo(o2.getNomeDiscente()));
              }
          }; 
          
          Collections.sort(usersAcessaramTurmaVirtual, NOME);
	}

	/**
	 * Gerar um array com os IDs dos arquivos baixados na Turma Virtual.
	 * 
	 * @return
	 */
	private int[] gerarIdsArquivosBaixados() {
		
		int total = 0;
		for (int i = 0; i < logLeituraTurmaVirtual.size(); i++)
			for (int j = 0; j < logLeituraTurmaVirtual.get(i).getDetalhes().size(); j++)
				if ( total < logLeituraTurmaVirtual.get(i).getDetalhes().size() )
					total = logLeituraTurmaVirtual.get(i).getDetalhes().size();
		
		int[] idsArquivosBaixados = new int[total];
		
  		for (int i = 0; i < logLeituraTurmaVirtual.size(); i++) {
  			for (int j = 0; j < logLeituraTurmaVirtual.get(i).getDetalhes().size(); j++) {
  				idsArquivosBaixados[j] = logLeituraTurmaVirtual.get(i).getDetalhes().get(j).getArquivoBaixado();
  			}
  		}
		return idsArquivosBaixados;
	}

	/**
	 * Gerar um array com os IDs dos usu�rios que acessaram a Turma Virtual.
	 * 
	 * @param usersAcessaramTurmaVirtual
	 * @return
	 */
	private int[] gerarIdsUsuarios(
			List<LogLeituraSigaaTurmaVirtual> usersAcessaramTurmaVirtual) {
		int[] idsUsuarios = new int[usersAcessaramTurmaVirtual.size()];
		for (int i = 0; i < usersAcessaramTurmaVirtual.size(); i++)
			idsUsuarios[i] = usersAcessaramTurmaVirtual.get(i).getIdUsuario();
		return idsUsuarios;
	}
	
	/**
	 * Gera um gr�fico com os acessos dos alunos na turma Virtual selecionada, no per�odo e ano atual.
	 * <br />
	 * JSP:
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String graficoAcessoTurmaVirtual() {
		
		int mes = 0;
		logLeituraTurmaVirtual = getDAO(RelatorioAcessoDao.class).graficoRelatorioAcessoTurmaVirtual(turma().getId(), turma().getPeriodo(), turma().getAno(), null, mes);		
	
		if (logLeituraTurmaVirtual == null || logLeituraTurmaVirtual.size() == 0 ) {
			logLeituraTurmaVirtual =  new ArrayList<LogLeituraSigaaTurmaVirtual>();
			addMensagemErro("N�o foi encontrado nenhum acesso nesse semestre para essa turma.");
			return null;
		}
		
		quantidadeTotalAcessos = new Integer(0);
		
		for(LogLeituraSigaaTurmaVirtual l : logLeituraTurmaVirtual) {
			quantidadeTotalAcessos += l.getQntEntrouTurmaVirtual();
		}
	
		return forward("/ava/Relatorios/graficoTurmaVirtual.jsp");
	}
	
	/**
	 * Esse m�todo tem como finalidade detalhar as semanas, para que o docente possa visualizar como foi 
	 * distribuido o acesso dos seus discente � turma virtual.
	 * 
	 * <br />
	 * JSP:
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/ava/Relatorios/graficoTurmaVirtual.jsp</li>
	 * </ul>
	 */
	public String detalharSemana() {
		
		Calendar c = Calendar.getInstance();
		int mes = getParameterInt("mes");
		int semana = getParameterInt("semana");
		int j = 0, e=1;
		for (int i = 1; i <= 31; i++) {
			c.set(turma().getAno(), mes, i);
				e=1;
				if (semana == c.get(Calendar.DAY_OF_WEEK_IN_MONTH)) {
					for (; j < j+e; j++, e--) {
						dias[j] = i;
					}
				}
		}
		
		logLeituraTurmaVirtual = getDAO(RelatorioAcessoDao.class).graficoRelatorioAcessoTurmaVirtual(
					turma().getId(), turma().getPeriodo(), turma().getAno(), dias, mes);
		
		for (LogLeituraSigaaTurmaVirtual lista : logLeituraTurmaVirtual){
			lista.setDetalharSemana(true);
		}
		
		return forward("/ava/Relatorios/graficoTurmaVirtual.jsp");
	}
	
	/**
	 * Esse m�todo tem como finalidade detalhar os meses, para que o docente possa visualizar como foi 
	 * distribuido o acesso dos seus discente � turma virtual.
	 * 
	 * <br />
	 * JSP:
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/ava/Relatorios/graficoTurmaVirtual.jsp</li>
	 * </ul>
	 */
	public String detalharMes() {
		
		int mes = getParameterInt("mes");
		
		logLeituraTurmaVirtual = getDAO(RelatorioAcessoDao.class).graficoRelatorioAcessoTurmaVirtual(
					turma().getId(), turma().getPeriodo(), turma().getAno(), null, mes);
		
		for (LogLeituraSigaaTurmaVirtual lista : logLeituraTurmaVirtual){
			lista.setDetalharMes(true);
		}
		
		return forward("/ava/Relatorios/graficoTurmaVirtual.jsp");
	}

	
	public List<LogLeituraSigaaTurmaVirtual> getLogLeituraTurmaVirtual() {
		return logLeituraTurmaVirtual;
	}

	public void setLogLeituraTurmaVirtual(
			List<LogLeituraSigaaTurmaVirtual> logLeituraTurmaVirtual) {
		this.logLeituraTurmaVirtual = logLeituraTurmaVirtual;
	}

	public String getNomeArquivoVisualizado() {
		return nomeArquivoVisualizado;
	}

	public void setNomeArquivoVisualizado(String nomeArquivoVisualizado) {
		this.nomeArquivoVisualizado = nomeArquivoVisualizado;
	}

	public int[] getDias() {
		return dias;
	}

	public void setDias(int[] dias) {
		this.dias = dias;
	}

	public void setLogUsuario(LogLeituraSigaaTurmaVirtual logUsuario) {
		this.logUsuario = logUsuario;
	}

	public LogLeituraSigaaTurmaVirtual getLogUsuario() {
		return logUsuario;
	}

	public Integer getQuantidadeTotalAcessos() {
		return quantidadeTotalAcessos;
	}

	public void setQuantidadeTotalAcessos(Integer quantidadeTotalAcessos) {
		this.quantidadeTotalAcessos = quantidadeTotalAcessos;
	}

	public void setVideo(Boolean video) {
		this.video = video;
	}

	public Boolean getVideo() {
		return video;
	}

}