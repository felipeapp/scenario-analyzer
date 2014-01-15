<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>
<style>
	table.listagem tr.projeto td{
		background: #C8D5EC;
		font-weight: bold;
	}
</style>

<c:set var="ASSUMIU_MONITORIA" value="<%= String.valueOf(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) %>" scope="application"/>	 	
<c:set var="MONITORIA_FINALIZADA" value="<%= String.valueOf(SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA) %>" scope="application"/>	 	

<f:view>
<h:messages showDetail="true"/>
	<h2><ufrn:subSistema /> > Relat�rio de Monitores do M�s</h2>
	<h:outputText value="#{comissaoMonitoria.create}"/>

 	<h:form id="formBuscaProjeto">
 	
 	
 	<div class="descricaoOperacao">
 		<b>Aten��o:</b><br/>
 		Este relat�rio � baseado no hist�rico das situa��es dos discentes de monitoria durante o per�odo de execu��o dos projetos no m�s/ano informado.<br/>
 		Informa qual a situa��o dos discentes em um determinado per�odo.<br/>
 		A situa��o informada pode n�o refletir a situa��o atual do discente.
 	</div>
 	
 	

	<table class="formulario" width="90%">
	<caption>Monitores do M�s</caption>
	<tbody>
	    
		 <tr>
			<td width="5%"></td>
	    	<td><label for="mes"> M�s / Ano: </label></td>
	    	<td>
    				<h:selectOneMenu value="#{ comissaoMonitoria.mes }" id="mes">
							<f:selectItem itemLabel="Janeiro" 	itemValue="1"/>								
							<f:selectItem itemLabel="Fevereiro" itemValue="2"/>																
							<f:selectItem itemLabel="Mar�o" 	itemValue="3"/>
							<f:selectItem itemLabel="Abril" 	itemValue="4"/>
							<f:selectItem itemLabel="Maio" 		itemValue="5"/>
							<f:selectItem itemLabel="Junho" 	itemValue="6"/>
							<f:selectItem itemLabel="Julho" 	itemValue="7"/>
							<f:selectItem itemLabel="Agosto" 	itemValue="8"/>
							<f:selectItem itemLabel="Setembro" 	itemValue="9"/>
							<f:selectItem itemLabel="Outubro" 	itemValue="10"/>
							<f:selectItem itemLabel="Novembro" 	itemValue="11"/>
							<f:selectItem itemLabel="Dezembro" 	itemValue="12"/>
					</h:selectOneMenu>
					/
					<h:inputText value="#{ comissaoMonitoria.ano }" id="ano" size="5"/>
	    	</td>
	    	<td><label for="Situacao"> Situa��o: </label></td>
	    	<td>
    				<h:selectOneMenu value="#{ comissaoMonitoria.idSituacao }" id="situacao">
							<f:selectItems value="#{discenteMonitoria.allSituacaoDiscenteMonitoriaCombo }"/>
					</h:selectOneMenu>
	    	</td>
	    </tr>		    
	    
	    
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkGerarRelatorio}"  id="selectGerarRelatorio" />
			</td>
		    	<td colspan="4"> <label> <b>Gerar Relat�rio</b></label> </td>
	    </tr>	     
       

	</tbody>
	<tfoot>
		<tr>
			<td colspan="5">
			<h:commandButton value="Buscar" action="#{ comissaoMonitoria.relatorioMonitoresMes }"/>
			<h:commandButton value="Cancelar" action="#{ comissaoMonitoria.cancelar }"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>
				
	<c:set var="historicosDiscentes" value="${comissaoMonitoria.historicosDiscentes}"/>
	
	<br/>
	<br/>	
	
	<c:if test="${empty historicosDiscentes}">
		<center><i> Nenhum Discente a localizado </i></center>
	</c:if>
	
	<c:if test="${not empty historicosDiscentes}">
		<br />
		<table class="listagem">
			<caption> Lista de situa��es dos monitores </caption>
			
			<thead>
				<tr>
					<th> Projeto </th>
					<th> Situa��o</th>
					<th> V�nculo </th>
					<th> Data da Situa��o</th>				
				</tr>
			</thead>
			
			<tbody>
				<c:set var="idprojeto" value=""/>	 				
				<c:forEach items="${historicosDiscentes}" var="hist" varStatus="status">

						<c:if test="${hist.discenteMonitoria.projetoEnsino.id != idprojeto }">
								<c:set var="idprojeto" value="${ hist.discenteMonitoria.projetoEnsino.id }"/>
							<tr class="projeto">
									<td colspan="4" >
										${hist.discenteMonitoria.projetoEnsino.anoTitulo}
									</td>
							</tr>							
						</c:if>

						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td style="padding-left: 20px;">${hist.discenteMonitoria.discente.matriculaNome}</td>
							<td>${hist.situacaoDiscenteMonitoria.descricao}</td>
							<td>${hist.discenteMonitoria.tipoVinculo.descricao}</td>
							<td><fmt:formatDate value="${hist.data}" pattern="dd/MM/yyyy"/></td>
						</tr>
					
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>