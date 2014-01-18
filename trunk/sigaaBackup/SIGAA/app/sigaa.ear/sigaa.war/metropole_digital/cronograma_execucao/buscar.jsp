<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
function mascara(o,f){
    obj = o
    fun = f
    setTimeout("gerarmascara()",1)
}

function gerarmascara(){
    obj.value=fun(obj.value)
}

function masknumeros(texto){
    texto = texto.replace(/\D/g,"")
    return texto
}
</script>
<f:view>
<a4j:keepAlive beanName="cronogramaExecucao"/>
<h2> <ufrn:subSistema /> > Listagem dos Cronogramas</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Informe os critérios da busca</caption>
 		<tbody>
 		
 			<tr>
				<th width="25%" class="obrigatorio">Curso:</th>
				<td>
					<h:selectOneMenu value="#{cronogramaExecucao.idCronograma}" valueChangeListener="#{cronogramaExecucao.carregarModulos}" id="curso">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cronogramaExecucao.cursosCombo}" />
				 		<a4j:support event="onchange" reRender="modulo" />  
					</h:selectOneMenu>
				</td>
			</tr>
			
			<!--Lista de Módulos -->
			<tr>
				<th>Módulo:</th>
				<td>
					<h:selectOneMenu value="#{cronogramaExecucao.idModulo}" id="modulo">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cronogramaExecucao.modulosCombo}" /> 
					</h:selectOneMenu>
				</td>
			</tr>
			
			<!-- Ano Período -->
			<tr>
				<th>Ano - Período</th>
				<td>
					<h:inputText value="#{cronogramaExecucao.filtroAno}" size="4" maxlength="4" id="ano" onkeypress="return mascara(this,masknumeros);" /> 
					- <h:inputText value="#{cronogramaExecucao.filtroPeriodo}" 
  					size="2" maxlength="1" id="periodo" onkeypress="return mascara(this,masknumeros);" /> 
				</td>
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="2">
					<h:commandButton value="Buscar" action="#{cronogramaExecucao.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{cronogramaExecucao.cancelar}"  id="cancelar"  />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />

<c:if test="${not empty cronogramaExecucao.listaCronogramas}">

<div class="infoAltRem">
	<html:img page="/img/view_calendario.png" style="overflow: visible;"/>: Visualizar Períodos
<%-- 	<html:img page="/img/view.gif" style="overflow: visible;"/>: Visualizar Cronograma --%>
	<html:img page="/img/alterar.gif" style="overflow: visible;"/>: Alterar Cronograma
	<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover Cronograma
</div>
	
	<table class="listagem">
	  <caption>Cronogramas Cadastrados</caption>
		<thead>
			<tr>
				<th width="5%" style="text-align: left;">Ano</th>
				<th width="5%" style="text-align: left;">Período</th>
				<th width="40%" style="text-align: left;">Descrição</th>
				<th width="50%" style="text-align: left;">Módulo</th>
				<th colspan="4"></th>
			</tr>
		</thead>
		<tbody>
		
		   <c:forEach var="linha" items="#{cronogramaExecucao.listaCronogramas}" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td style="text-align: left;">${linha.ano}</td>
					<td style="text-align: left;">${linha.periodo} </td>
					<td style="text-align: left;">${linha.descricao} </td>
					<td style="text-align: left;">${linha.modulo.descricao}</td>
					
					
					<td width="2%" align="right">
						<h:commandLink action="#{cronogramaExecucao.visualizarPeriodos}" >
							<h:graphicImage value="/img/view_calendario.png" style="overflow: visible;" title="Visualizar Períodos" alt="Visualizar Períodos" />  
							<f:param name="id" value="#{linha.id}"/> 
						</h:commandLink>
					</td>
					
<!-- 					<td width="2%" align="right"> -->
<%-- 						<h:commandLink action="#{cronogramaExecucao.visualizar}" > --%>
<%-- 							<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Visualizar Cronograma" alt="Visualizar Cronograma" />   --%>
<%-- 							<f:param name="id" value="#{linha.id}"/>  --%>
<%-- 						</h:commandLink> --%>
<!-- 					</td> -->
					
					<td width="2%" align="right">
						<h:commandLink action="#{cronogramaExecucao.preAlterar}" >
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar Cronograma" alt="Alterar Cronograma" />  
							<f:param name="id" value="#{linha.id}"/> 
						</h:commandLink>
					</td>					
					<td width="2%" align="right">
						<h:commandLink action="#{cronogramaExecucao.remover}" onclick="#{confirmDelete}" id="remover" >
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Cronograma" alt="Remover Cronograma" />
							<f:param name="id" value="#{linha.id}"/>
						</h:commandLink>
					</td>
				</tr>
		   </c:forEach>
		</tbody>
	</table>
</c:if>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>