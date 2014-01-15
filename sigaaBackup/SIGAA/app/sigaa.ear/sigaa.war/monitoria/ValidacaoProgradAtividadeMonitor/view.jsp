<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>


<style>
<!--

/* Paineis de op�oes */
.titulo {
	background: #EFF3FA;
}
.subtitulo{
	text-align:left; 
	background:#EDF1F8; 
	color:#333366; 
	font-variant:small-caps;
	font-weight:bold;
	letter-spacing:1px;
	margin:1px 0; 
	border-collapse:collapse; 
	border-spacing:2px;
	font-size:1em; 
	font-family:Verdana,sans-serif; 
	font-size: 12px
}

-->
</style>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:outputText value="#{ atividadeMonitor.create }"/>
	<h2><ufrn:subSistema /> > Relat�rio de Atividades do Monitor</h2>
	<h:form>

	
	<table class="visualizacao" width="100%">
		<caption class="listagem">Atividades do Monitor</caption>
		
		<tr>
			<th width="20%">T�tulo do Projeto:</th>
			<td><h:outputText value="#{ atividadeMonitor.atividade.discenteMonitoria.projetoEnsino.anoTitulo }" /></td>
		</tr>
		
	
		<tr>
			<th>Discente:</th>
			<td><h:outputText value="#{ atividadeMonitor.atividade.discenteMonitoria.discente.matriculaNome }" /></td>
		</tr>
	
	
		<tr>
			<th>Per�odo:</th>
			<td><fmt:formatNumber value="${ atividadeMonitor.atividade.mes }" pattern="00"/>/<h:outputText value="#{ atividadeMonitor.atividade.ano }"/></td>
		</tr>
		
		<tr>
			<th>Data de Envio:</th>
			<td><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ atividadeMonitor.atividade.dataCadastro }"/></td>
		</tr>
	
	
	
		<tr>
			<th>Atividades Realizadas:</th>
			<td align="justify" width="85%"><h:outputText value="#{ atividadeMonitor.atividade.atividades }"/> <br/>
			</td>		
		</tr>
		
	    <tr>
            <td colspan="2" class="subFormulario" > 
                      An�lise pelo orientador/coordenador do projeto
            </td>
        </tr>
	
		<c:if test="${not empty atividadeMonitor.atividade.dataValidacaoOrientador}">		
			<tr>
				<th>Situa��o:</th>
				<td>${ atividadeMonitor.atividade.validadoOrientador == true ? 'VALIDADO': 'N�O VALIDADO' } em <fmt:formatDate  pattern="dd/MM/yyyy HH:mm:ss" value="${ atividadeMonitor.atividade.dataValidacaoOrientador }"/></td>
			</tr>
		
			<tr>
				<th>Freq��ncia:</th>
				<td><h:outputText value="#{ atividadeMonitor.atividade.frequencia }"/>%</td>
			</tr>
		
			<tr>
				<th>Observa��es:</th>
				<td  width="85%"><h:outputText value="#{ atividadeMonitor.atividade.observacaoOrientador }"/><br/></td>
			</tr>
	   	</c:if>
	   			  
		<c:if test="${empty atividadeMonitor.atividade.dataValidacaoOrientador}">
			<tr>
				<td colspan="2"> 
					<br>
						<center style="color: red ">Relat�rio n�o foi analisado pelo(a) orientador(a)/coordenador(a)</center>
					<br>
				</td>
			</tr>
		</c:if>
		
        
		<c:if test="${not empty atividadeMonitor.atividade.dataValidacaoPrograd}">	
	        <tr>
	            <td colspan="2" class="subFormulario" > 
			            An�lise pela Pr�-Reitoria de Gradua��o
	            </td>
	        </tr>
	        <tr>
	            <th>Parecer da Pr�-Reitoria de Gradua��o:</th>
	            <td>${ atividadeMonitor.atividade.validadoPrograd == true ? 'VALIDADO': 'N�O VALIDADO' } em <fmt:formatDate  pattern="dd/MM/yyyy HH:mm:ss" value="${ atividadeMonitor.atividade.dataValidacaoPrograd }"/> 
	            	<h:outputText value="<font color='red'>[Relat�rio cadastrado por um Gestor de Monitoria.]</font>" rendered="#{ atividadeMonitor.atividade.cadastradoPorGestor }" escape="false"/><br/>
	            </td>
	        </tr>
	    
	        <tr>
	            <th>Freq��ncia:</th>
	            <td><h:outputText value="#{ atividadeMonitor.atividade.frequencia }"/>%</td>
	        </tr>
	    
	        <tr>
	            <th>Observa��es:</th>
	            <td align="justify">
	            	<h:outputText value="#{ atividadeMonitor.atividade.observacaoPrograd }"/><br/>	            	
	            </td>
	        </tr>
	    </c:if>	
		<tfoot>
			<tr>
				<td colspan="2">
					<center><input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" /></center>
				</td>
			</tr>	    
		</tfoot>
	</table>
	
		
	
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>