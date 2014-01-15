/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on Jul 14, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.stricto.ParametrosProgramaPosDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.ParametrosProgramaPos;
import br.ufrn.sigaa.parametros.dominio.MensagensStrictoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;


/**
 * MBean Responsável por gerenciar os parâmetros do programa de Pós-Graduação
 * @author Victor Hugo
 */
@Component("parametrosProgramaPosBean") @Scope("request")
public class ParametrosProgramaPosMBean extends SigaaAbstractController<ParametrosProgramaPos> {

	public ParametrosProgramaPosMBean() {
		obj = new ParametrosProgramaPos();
		obj.setPrograma( new Unidade() );
	}
	
	/** 
	 *  Inicia os atributos da classe. 
	 * 	Método não invocado por JSPs
	 */
	public void initObj(){
		obj = new ParametrosProgramaPos();
		obj.setPrograma( new Unidade() );
		obj.setPrograma( getProgramaStricto() );
	}

	/**
	 * Inicia o caso de uso de cadastro/alteração de parâmetros de um programa
	 * caso o programa já tenha parâmetros estes devem ser carregados,
	 * caso não tenha será criado um novo.
	 * <br><br>
	 * Método chamado pela seguinte JSP: /stricto/menu_coordenador.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS );

		ParametrosProgramaPosDao dao = getDAO( ParametrosProgramaPosDao.class );
		obj = dao.findByPrograma( getProgramaStricto() );
		if( obj == null ){
			initObj();
			setConfirmButton("Cadastrar parâmetros");
			prepareMovimento(ArqListaComando.CADASTRAR);
			setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		}
		else{
			setConfirmButton( "Alterar parâmetros" );
			prepareMovimento(ArqListaComando.ALTERAR);
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		}

		return forward( getFormPage() );
	}
	
	/**
	 * Realiza a validação antes de cadastrar.
	 * <br><br>
	 * Método chamado pela seguinte JSP: /stricto/parametros_programa_pos/form.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrarValidar() throws SegurancaException, ArqException, NegocioException{
		int maximoDisciplinas = ParametroHelper.getInstance().getParametroInt(ParametrosStrictoSensu.MAXIMO_DISCIPLINAS_ALUNO_ESPECIAL_POS);
		int prazoMinimoCadastroBanca = ParametroHelper.getInstance().getParametroInt(ParametrosStrictoSensu.PRAZO_MAXIMO_CADASTRO_BANCA);
		
		if (obj.getMaxDisciplinasAlunoEspecial() > maximoDisciplinas){
			addMensagem(MensagensStrictoSensu.MAXIMO_DISCIPLINAS_ALUNO_ESPECIAL_ESTOURADO, maximoDisciplinas);
		}
		
		if (obj.getPrazoMinCadastroBancaQualificacao()!=null)
			validateMinValue(obj.getPrazoMinCadastroBancaQualificacao(), prazoMinimoCadastroBanca, "Prazo mínimo para a banca de qualificação após sua solicitação", erros);
		
		if (obj.getPrazoMinCadastroBancaQualificacao()!=null)
			validateMinValue(obj.getPrazoMinCadastroBancaDefesa(), prazoMinimoCadastroBanca, "Prazo mínimo para a banca de defesa após sua solicitação", erros);

		if (hasErrors())
			return null;
		
		return cadastrar();
	}

	@Override
	public String getDirBase() {
		return "/stricto/parametros_programa_pos";
	}

	@Override
	public String forwardCadastrar() {
		return getSubSistema().getLink();
	}
}
