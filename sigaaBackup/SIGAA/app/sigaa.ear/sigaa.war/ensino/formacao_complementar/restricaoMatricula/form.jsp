<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>

<f:view>
<h2> <ufrn:subSistema /> &gt; Cadastro de Curso</h2>
	
<h:form id="form">
<p:resources/>
	<table class="formulario" style="width: 90%">
	  <caption>Dados do Curso</caption>
		<h:inputHidden value="#{cursoTecnicoMBean.obj.id}" />
			
			<c:forEach items="#{ restricaoDiscenteMatriculaMBean.restricoes }" var="linha">
								
				<tr>
					<td class="subFormulario" colspan="5"> ${ linha.descricao } </td>
				</tr>
				<tr>
					<th width="30%"> Início: </th>
					<td width="15%">
						<p:inputMask value="#{linha.inicio}" mask="99/99/9999" size="8"/>
					</td>
					<th width="15%"> Hora Início: </th>
					<td>
						<h:inputText value="#{linha.horaInicio}" id="horaInicio" size="2" title="horaInicio"
							maxlength="2" onkeyup="formatarInteiro(this);" converter="#{ intConveter }">
							<a4j:support event="onchange" ajaxSingle="true" reRender="qtde" />  
						</h:inputText> :
						<h:inputText value="#{linha.minutoInicio}" id="minutoInicio" size="2" title="minutoInicio"
							maxlength="2" onkeyup="formatarInteiro(this);" converter="#{ intConveter }"/> 
					</td>
				</tr>
				<tr>
					<th> Fim: </th>
					<td> 
						<p:inputMask value="#{linha.fim}" mask="99/99/9999" size="8"/>
					</td>
					<th> Hora Fim: </th>
					<td> 
						<h:inputText value="#{linha.horaFim}" id="horaFim" size="2" title="horaFim"
							maxlength="2" onkeyup="formatarInteiro(this);" converter="#{ intConveter }"/> :
						<h:inputText value="#{linha.minutoFim}" id="minutoFim" size="2" title="minutoFim"
							maxlength="2" onkeyup="formatarInteiro(this);" converter="#{ intConveter }"/> 
					</td>
				</tr>
				
				<tr>
					<th>Instruções:</th>
					<td colspan="4">
						<h:inputTextarea value="#{linha.instrucoes}" rows="20" cols="100" />
					</td>
				</tr>
				
				
			</c:forEach>
				
		<tfoot>
		   <tr>
				<td colspan="5">
					<h:commandButton value="#{restricaoDiscenteMatriculaMBean.confirmButton}" action="#{restricaoDiscenteMatriculaMBean.cadastrar}" id="cadastrar" />
					<h:commandButton value="Cancelar" action="#{restricaoDiscenteMatriculaMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		   </tr>
		</tfoot>
		
	</table>
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>