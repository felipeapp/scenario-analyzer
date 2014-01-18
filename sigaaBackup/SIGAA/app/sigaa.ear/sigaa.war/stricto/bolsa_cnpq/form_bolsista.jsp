<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<a4j:keepAlive beanName="bolsasCnpqStrictoMBean" />

	<h2><ufrn:subSistema /> &gt; Bolsistas CNPq</h2>

	<h:form id="form">
		<table class="formulario" style="width: 80%">
			<caption class="formulario">Dados do Bolsista</caption>
			<tr>
				<th> Discente: </th>
				<td>
					${ bolsasCnpqStrictoMBean.obj.discente.matriculaNome }
				</td> 
			</tr>
			<tr>
				<th> Nível: </th>
				<td>
					${ bolsasCnpqStrictoMBean.obj.discente.nivelDesc }
				</td> 
			</tr>
			<tr>
				<th class="required"> Período da Bolsa: </th>
				<td> 
					<t:inputCalendar value="#{bolsasCnpqStrictoMBean.obj.bolsa.inicio}" size="10" maxlength="10"
						id="dataInicio" onkeydown="formataData(this,event)"
						renderAsPopup="true" renderPopupButtonAsImage="true" /> a 
					<t:inputCalendar value="#{bolsasCnpqStrictoMBean.obj.bolsa.fim}" size="10" maxlength="10"
						id="dataFim" onkeydown="formataData(this,event)"
						renderAsPopup="true" renderPopupButtonAsImage="true" /> 
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar" action="#{bolsasCnpqStrictoMBean.cadastrar}" /> 
						<h:commandButton value="Cancelar" action="#{bolsasCnpqStrictoMBean.cancelar}" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<center>
		<br />
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		<br />
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
