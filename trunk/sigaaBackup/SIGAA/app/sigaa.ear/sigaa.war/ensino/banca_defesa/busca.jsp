<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Consulta de Bancas</h2>
	
	<h:form id="formulario">
	
	<a4j:keepAlive beanName="buscaBancaDefesaMBean"/>
	<a4j:keepAlive beanName="bancaDefesaMBean"/>
	
	<div class="descricaoOperacao">
		<p><b>Caro Usuário,</b></p>
		<br/>
		<p>Para Consultar por Bancas de Defesa, deverá ser informado um dos critérios de busca abaixo, ou fazer combinações entre eles.</p>
		<p>Para cada Banca de Defesa será possível: <b>Visualizar</b>, <b>Alterar</b> e/ou <b>Cancelar</b>.</p>
	</div>
	
	<table class="formulario" width=80%>
		<caption> Informe os critérios de busca</caption>
		
		<tbody>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{buscaBancaDefesaMBean.checkDiscente}" styleClass="noborder" id="checkDiscente"/>
				</td>
				
				<td nowrap="nowrap"> Discente: </td>
				
				<td colspan="3">
					<h:inputText value="#{buscaBancaDefesaMBean.discente}" id="discente" maxlength="60" size="60" 
					onfocus="$('formulario:checkDiscente').checked = true;" title="Discente" alt="Discente" />	
				</td>				
			</tr>
			
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{buscaBancaDefesaMBean.checkTituloTrabalho}" styleClass="noborder" id="checkTituloTrabalho"/>
				</td>
				
				<td nowrap="nowrap"> Título do Trabalho: </td>
				
				<td colspan="3">
					<h:inputText value="#{buscaBancaDefesaMBean.tituloTrabalho}" id="tituloTrabalho" maxlength="60" size="60" 
					onfocus="$('formulario:checkTituloTrabalho').checked = true;" title="Título do Trabalho" alt="Título do Trabalho"></h:inputText>	
				</td>				
			</tr>	
			
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{buscaBancaDefesaMBean.checkDocente}" styleClass="noborder" id="checkDocente"/>
				</td>
				
				<td nowrap="nowrap"> Docente: </td>
				
				<td colspan="3">
					<h:inputText value="#{buscaBancaDefesaMBean.docente}" id="docente" maxlength="60" size="60" 
					onfocus = "$('formulario:checkDocente').checked = true;" title="Docente" alt="Docente"/>	
				</td>				
			</tr>							
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{buscaBancaDefesaMBean.checkDataInicio}" styleClass="noborder" id="checDataInicio"/>
				</td>
				
				<td> Data Início: </td>
				
				<td colspan="3">
					<t:inputCalendar 
						value="#{buscaBancaDefesaMBean.dataInicio}" 
						maxlength="10" size="10" 
						onkeypress="return(formataData(this,  event))"
						renderAsPopup="true" renderPopupButtonAsImage="true" 
						onfocus="$('formulario:checDataInicio').checked = true;" 
						id="dtInicio"
						onchange="$('formulario:checDataInicio').checked = true;" 
						popupDateFormat="dd/MM/yyyy" title="Data Início">
						
						<f:converter converterId="convertData"/>
						
					</t:inputCalendar>
				</td>
			</tr>
			
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{buscaBancaDefesaMBean.checkDataFim}" styleClass="noborder" id="checDataFim"/>
				</td>
				
				<td> Data Fim: </td>
				
				<td colspan="3"> 
					<t:inputCalendar 
						value="#{buscaBancaDefesaMBean.dataFim}" 
						maxlength="10" size="10" 
						onkeypress="return(formataData(this,  event))"
						renderAsPopup="true" renderPopupButtonAsImage="true" 
						onfocus="$('formulario:checDataFim').checked = true;" 
						id="dtFim"
						onchange="$('formulario:checDataFim').checked = true;" 
						popupDateFormat="dd/MM/yyyy" title="Data Fim">
						
						<f:converter converterId="convertData"/>
						
					</t:inputCalendar>
				</td>
			</tr>
		</tbody>
		
		<tfoot>
			<tr>
				<td colspan="5">
					<h:commandButton action="#{buscaBancaDefesaMBean.buscar}" value="Buscar" id="buscar"/>
					<h:commandButton id="cancelar" action="#{buscaBancaDefesaMBean.cancelar}" onclick="#{confirm}" value="Cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>

	
	<c:if test="${not empty buscaBancaDefesaMBean.listagem}">
		<br />
		<%@include file="./lista.jsp"%>
	</c:if>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>