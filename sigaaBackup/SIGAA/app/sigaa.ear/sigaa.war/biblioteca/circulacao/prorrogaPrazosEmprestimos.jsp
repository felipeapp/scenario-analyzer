<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%-- 
<f:view>

	<h2> <ufrn:subSistema /> &gt; Prorrogar Prazos de Empréstimos </h2>

	<div class="descricaoOperacao">
		<p> Digite o período de dias no qual os prazos dos empréstimos não serão contabilizados.</p> 
		<p> <b>TODOS os empréstimos ativos cujos prazos vencerem no período digitado, serão prorrogados para o 1º dia após este período.</b> </p>
	</div>


	<h:form id="formPeriodoProrrogacao">
			
		<table class="formulario" style="width:450px;">
			<caption> Digite o período de prorrogação </caption>

			<tr>
				<th class="required"> Data de Início: </th>
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

			<%-- por segurança para evitar que o chefe da circulacao deixe a sua area aberta e alguem faca isso --%
			<tr>
				<th class="required"> Entre com a sua senha do SIGAA: </th>
				<td>  
					<h:inputSecret id="inpSctSenhaSigaa" value="#{prorrogarPrazosEmprestimos.senhaSigaaProrrogarPrazo}" size="30" maxlength="20" /> 
				</td>
			</tr>
				
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Prorrogar" action="#{prorrogarPrazosEmprestimos.prorrogar}" onclick="return confirm('Confirma prorrogação dos prazos dos empréstimos?');"/>
						<h:commandButton value="Cancelar" action="#{prorrogarPrazosEmprestimos.cancelar}" />
					</td>
				</tr>
			</tfoot>

		</table>

		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

	</h:form>


</f:view> --%>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>