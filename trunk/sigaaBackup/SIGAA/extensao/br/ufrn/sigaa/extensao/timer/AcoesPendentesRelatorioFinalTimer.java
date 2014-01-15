package br.ufrn.sigaa.extensao.timer;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Classe responsável por criar um timer para encerrar as Ações Pendentes 
 */
public class AcoesPendentesRelatorioFinalTimer extends TarefaTimer {
	
	/**
	 * Método utilizado para iniciar o timer
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 */
	public void run() {
		try {
			encerrarAcoesPendencias();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Lista todas as ações de extensão que serão finalizadas nos próximos 30 dias
	 * e envia e-mail para os coordenadores avisando do cadastramento do relatório.
	 * @throws ArqException 
	 * @throws NegocioException 
	 * @throws SQLException 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void encerrarAcoesPendencias() throws NegocioException, ArqException, SQLException {
		JdbcTemplate template = new JdbcTemplate(Database.getInstance().getSigaaDs());

		try {
			final int diasEncerrar = ParametroHelper.getInstance().getParametroInt(ParametrosExtensao.DIAS_ENCERRAR_ACOES_PENDENCIAS_EXECUCAO);
	
			String sql = "select a.id_atividade, a.id_tipo_situacao_projeto, a.id_projeto from extensao.atividade a "
					+ " inner join projetos.projeto p on a.id_projeto = p.id_projeto "
					+ " where p.ativo = trueValue() and a.id_tipo_situacao_projeto = ? "
					+ " and ( ( p.data_fim - now() ) <= cast('"+ diasEncerrar +" day' as interval) )";
	
			List<AtividadeExtensao> atividades =  template.query(sql, new Object[] { TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO }, new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					AtividadeExtensao atv = new AtividadeExtensao(rs.getInt("id_atividade"));
					atv.setSituacaoProjeto(new TipoSituacaoProjeto(rs.getInt("id_tipo_situacao_projeto")));
					atv.setProjeto(new Projeto(rs.getInt("id_projeto")));
					return atv;
				}
			});
	
			if(atividades != null){
				Usuario usuario = new Usuario(Usuario.TIMER_SISTEMA);
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setCodMovimento(SigaaListaComando.ENCERRAR_ACOES_COM_DATA_FIM_MAIOR_QUE_PRAZO);
				mov.setColObjMovimentado(atividades);
				facade.prepare(SigaaListaComando.ENCERRAR_ACOES_COM_DATA_FIM_MAIOR_QUE_PRAZO.getId(), usuario, Sistema.SIGAA);
				facade.execute(mov, usuario, Sistema.SIGAA);
			}
		}finally {
			if(template != null) template.getDataSource().getConnection().close();
		}
	}


	/**
	 * Método principal responsável por chamar o método run()
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param args
	 * @throws RemoteException
	 * @throws NamingException
	 * @throws CreateException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static void main(String[] args) throws RemoteException, NamingException, CreateException, NegocioException, ArqException {

		System.out.println("\n\n\n INVOCANDO TIMER \n\n\n ");

		AcoesPendentesRelatorioFinalTimer timer = new AcoesPendentesRelatorioFinalTimer();
		try {
			timer.encerrarAcoesPendencias();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("\n\n\n TIMER REALIZADO COM SUCESSO \n\n\n ");

	}

}
