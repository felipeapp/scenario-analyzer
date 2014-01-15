<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Consultar Dados Quantitativos de Membros das Equipes de Extens�o</h2>
	<a4j:keepAlive beanName="relatorioPlanejamentoMBean" />
	
	<h:messages showDetail="true" />
	<h:form id="form">

		<table class="formulario" width="80%">
			<caption>Consultar Relat�rio Gerais de Extens�o</caption>
			<tbody>
				<tr>
			    	<th class="required"> <label for="situacaoAcao"> Situa��o da A��o: </label> </th>
			    	<td>
				    	 <h:selectOneMenu  id="buscaSituacao" value="#{relatorioPlanejamentoMBean.situacaoAcao.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				    	 	<f:selectItems value="#{tipoSituacaoProjeto.situacoesAcaoExtensaoValidas}" />
			 			 </h:selectOneMenu>
			    	 
			    	</td>
			    </tr>
			    
				<tr>
					<th class="required">Per�odo de execu��o da a��o:</th>
					<td>
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatorioPlanejamentoMBean.dataInicio}" id="dataInicio" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
						a
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatorioPlanejamentoMBean.dataFim}" id="dataFim" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
					</td>
				</tr>
		
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton value="Gerar Relat�rio" action="#{ relatorioPlanejamentoMBean.gerarRelatorioDocentesPorNivel }"
							id="BotaoRelatorio" /> 
					<h:commandButton value="Cancelar" action="#{ relatorioPlanejamentoMBean.cancelar }"
							id="BotaoCancelar" onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>
		<div>
		<tr>
			<center><img src="/shared/img/required.gif" /> <span
				class="fontePequena"> Campos de preenchimento obrigat�rio. </span></center>
		</tr>
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>