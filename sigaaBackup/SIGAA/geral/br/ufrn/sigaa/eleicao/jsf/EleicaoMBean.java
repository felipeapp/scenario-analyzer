/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '03/04/2007'
 *
 */
package br.ufrn.sigaa.eleicao.jsf;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.eleicao.EleicaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.eleicao.dominio.Eleicao;

/**
 * Managed Bean responsável por gerenciar a view das telas relacionadas a Eleição
 * 
 * @author Victor Hugo
 */
@Component("eleicao") @Scope("request")
public class EleicaoMBean extends SigaaAbstractController<Eleicao> {

	private final String VIEW_FORM = "/administracao/cadastro/Eleicao/form.jsf";

	public EleicaoMBean() {
		obj = new Eleicao();
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
	}

	@Override
	public String getFormPage() {
		return VIEW_FORM;
	}

	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException, SegurancaException, DAOException {

		if( getConfirmButton().equalsIgnoreCase("cadastrar") ){
			obj.setDataCadastro(new Date());
			obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		}
		//setando hora na data inicio e data fim
		//dataInicio
		Calendar c1 = Calendar.getInstance();
		//c1.setTimeZone(TimeZone.getTimeZone("GMT+3"));
		c1.setTime( obj.getHoraInicio() );

		Calendar c2 = Calendar.getInstance();
		//c2.setTimeZone(TimeZone.getTimeZone("GMT+3"));
		c2.setTime( obj.getDataInicio() );
		c2.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY) );
		c2.set(Calendar.MINUTE, c1.get(Calendar.MINUTE) );

		obj.setDataInicio( c2.getTime() );

		//dataFim
		Calendar c3 = Calendar.getInstance();
		//c3.setTimeZone(TimeZone.getTimeZone("GMT+3"));
		c3.setTime( obj.getHoraFim() );

		Calendar c4 = Calendar.getInstance();
		//c4.setTimeZone(TimeZone.getTimeZone("GMT+3"));
		c4.setTime( obj.getDataFim() );
		c4.set(Calendar.HOUR_OF_DAY, c3.get(Calendar.HOUR_OF_DAY) );
		c4.set(Calendar.MINUTE, c3.get(Calendar.MINUTE) );

		obj.setDataFim( c4.getTime() );

		if( obj.getCentro().getId() == 0 )
			obj.setCentro(null);
	}

	@Override
	protected void afterCadastrar() {
		if( getConfirmButton().equalsIgnoreCase("cadastrar") ){
			obj = new Eleicao();
		}
	}

	@Override
	public String forwardCadastrar() {
		return cancelar();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(Eleicao.class, "id", "titulo");
	}

	/**
	 * Lista as eleições abertas no momento.
	 * só ha votação online para aluno de ensino a distância.
	 * @return
	 * @throws DAOException
	 */
	public Collection<Eleicao> getEleicoesAbertas() throws DAOException{

		if( getDiscenteUsuario() != null && getDiscenteUsuario().getCurso() != null ){
			if( getDiscenteUsuario().getCurso().isADistancia() || getDiscenteUsuario().getCurso().isProbasica() ){
				EleicaoDao dao = getDAO(EleicaoDao.class);
				return dao.findEleicoesAbertas( null );
			}
		}
		return null;
	}
	
	@Override
	public Collection<Eleicao> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "titulo";
	}

	/**
	 * Remove uma eleição cadastrada
	 */
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, Eleicao.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		return super.cadastrar();
	}
	
	@Override
	public void afterAtualizar() throws ArqException {
		
		Calendar cInicio = Calendar.getInstance();
		cInicio.setTime(obj.getDataInicio());
		int horaInicio = cInicio.get(Calendar.HOUR_OF_DAY);
		int minutoInicio = cInicio.get(Calendar.MINUTE);
		Calendar novoIncio = CalendarUtils.getInstance(0, 0, 0, horaInicio, minutoInicio);
		obj.setHoraInicio(novoIncio.getTime());

		
		Calendar cFim = Calendar.getInstance();
		cFim.setTime(obj.getDataFim());
		int horaFim = cFim.get(Calendar.HOUR_OF_DAY);
		int minutoFim = cFim.get(Calendar.MINUTE);
		Calendar novoFim = CalendarUtils.getInstance(0, 0, 0, horaFim, minutoFim);
		obj.setHoraFim(novoFim.getTime());	
	}
}