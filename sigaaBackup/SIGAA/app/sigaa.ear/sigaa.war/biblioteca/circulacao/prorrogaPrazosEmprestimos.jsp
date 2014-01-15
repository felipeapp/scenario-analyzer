<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%-- 
<f:view>

	<h2> <ufrn:subSistema /> &gt; Prorrogar Prazos de Empr�stimos </h2>

	<div class="descricaoOperacao">
		<p> Digite o per�odo de dias no qual os prazos dos empr�stimos n�o ser�o contabilizados.</p> 
		<p> <b>TODOS os empr�stimos ativos cujos prazos vencerem no per�odo digitado, ser�o prorrogados para o 1� dia ap�s este per�odo.</b> </p>
	</div>


	<h:form id="formPeriodoProrrogacao">
			
		<table class="formulario" style="width:450px;">
			<caption> Digite o per�odo de prorroga��o </caption>

			<tr>
				<th class="required"> Data de In�cio: </th>
				<td> 
					<t:inputCalendar id="inpCalIni" value="#{prorrogarPrazosEmprestimos.dataInicioPeriodo}" 
						size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
						renderPopupButtonAsImage="true"
						onkeypress="return formataData(this, event)"/>
				</td>
			</tr>

			<tr>
				<th class="required"> Data Final: </th>
				<td>
					<t:inputCalendar id="inpCalFim" value="#{prorrogarPrazosEmprestimos.dataFinalPeriodo}" 
						size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
						renderPopupButtonAsImage="true"
						onkeypress="return formataData(this, event)"/>
				 </td>
			</tr>

			<%-- por seguran�a para evitar que o chefe da circulacao deixe a sua area aberta e alguem faca isso --%
			<tr>
				<th class="required"> Entre com a sua senha do SIGAA: </th>
				<td>  
					<h:inputSecret id="inpSctSenhaSigaa" value="#{prorrogarPrazosEmprestimos.senhaSigaaProrrogarPrazo}" size="30" maxlength="20" /> 
				</td>
			</tr>
				
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Prorrogar" action="#{prorrogarPrazosEmprestimos.prorrogar}" onclick="return confirm('Confirma prorroga��o dos prazos dos empr�stimos?');"/>
						<h:commandButton value="Cancelar" action="#{prorrogarPrazosEmprestimos.cancelar}" />
					</td>
				</tr>
			</tfoot>

		</table>

		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>

	</h:form>


</f:view> --%>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>