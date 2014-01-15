<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>


<f:view>
	<h2><ufrn:subSistema /> > Distribuir Relatórios de Projetos de Ensino</h2>
 	
 	<div class="descricaoOperacao">
		 Localize o relatório através da busca abaixo e clique no ícone para distribui-lo aos Membros da Comissão de Monitoria.
		 Somente poderão ser distribuídos para Comissão de Monitoria os Relatórios de Projeto de Ensino cuja situação sejam:
		 'AGUARDANDO DISTRIBUIÇÃO DO RELATÓRIO', 'AGUARDANDO AVALIAÇÃO'. Os demais relatórios NÃO poderão ser distribuídos.
	</div>

 	
 	<h:form id="formBusca">

	<table class="formulario" width="90%">
	<caption>Busca por Relatórios de Projeto</caption>
	<tbody>
	
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{relatorioProjetoMonitoria.checkBuscaProjeto}" id="selectBuscaProjeto"/>
			</td>
		
			<td><label> Projeto: </label></td>
			<td>
				 <h:inputText id="tituloProjeto" value="#{ relatorioProjetoMonitoria.tituloProjeto }" size="60"  onchange="javascript:$('formBusca:selectBuscaProjeto').checked = true;"/>
			</td>
		</tr>		


	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{relatorioProjetoMonitoria.checkBuscaServidor}" id="selectBuscaServidor"/>
			</td>
	    	<td> <label for="nome"> Coordenador(a): </label> </td>
	    	<td> 
	    	
				<h:inputText value="#{relatorioProjetoMonitoria.buscaServidor.pessoa.nome}" id="nome" size="60" onchange="javascript:$('formBusca:selectBuscaServidor').checked = true;"/>
				<h:inputHidden value="#{relatorioProjetoMonitoria.buscaServidor.id}" id="idServidor"/>
	
				<ajax:autocomplete source="formBusca:nome" target="formBusca:idServidor"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
					parser="new ResponseXmlToHtmlListParser()" />
	
				<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>	    	
	    	
	    	</td>
	    </tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{relatorioProjetoMonitoria.checkBuscaAno}" id="selectBuscaAno" />

			</td>
	    	<td> <label for="anoProjeto"> Ano do Projeto </label> </td>
	    	<td> <h:inputText value="#{relatorioProjetoMonitoria.buscaAnoProjeto}" size="10" onchange="javascript:$('formBusca:selectBuscaAno').checked = true;"/></td>
	    </tr>


		<tr>
			<td>
			
			<h:selectBooleanCheckbox value="#{relatorioProjetoMonitoria.checkBuscaTipoRelatorio}"  id="selectBuscaTipoRelatorio" />
			</td>
	    	<td> <label for="tipoRelatorio">Tipo de Relatório </label> </td>
	    	<td>

	    	 <h:selectOneMenu value="#{relatorioProjetoMonitoria.buscaTipoRelatorio.id}" onchange="javascript:$('formBusca:selectBuscaTipoRelatorio').checked = true;">
				<f:selectItems value="#{relatorioProjetoMonitoria.allTiposRelatoriosProjetoCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>


	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ relatorioProjetoMonitoria.localizarProjetoDistribuir }"/>
			<h:commandButton value="Cancelar" action="#{ relatorioProjetoMonitoria.cancelar }"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	<br/>
	<br/>


	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif"style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText rendered="#{acesso.monitoria}" value=": Visualizar Projeto"/>	    	
	   <!-- <h:graphicImage value="/img/seta.gif"style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText rendered="#{acesso.monitoria}" value=": Distribuir Relatório para Avaliação"/>--> 
	</div>	    
	<br/>

	<c:set var="relatorios" value="#{relatorioProjetoMonitoria.relatoriosLocalizados}"/>

	<c:if test="${empty relatorios}">
	<center><i> Nenhum Relatório localizado </i></center>
	</c:if>

	<c:if test="${not empty relatorios}">

		 <table class="listagem">
		    <caption>Relatórios de Projetos de Ensino Encontrados (${fn:length(relatorios)})</caption>
	
		      <thead>
		      	<tr>
		      		<th style="text-align: center;"> <h:selectBooleanCheckbox styleClass="chkSelecionaTodos" onclick="selecionarTodos();" /> </th>
		        	<th>Ano</th>
		        	<th width="65%">Projeto</th>
		        	<th>Tipo Relatório</th>	        	
		        	<th>Data Envio</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
	       	<c:forEach items="#{relatorios}" var="relatorio" varStatus="status">
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						
						<td style="text-align: center;"> <h:selectBooleanCheckbox value="#{relatorio.selecionado}" styleClass="todosChecks" 
							rendered="#{((acesso.monitoria) && (relatorio.permitidoDistribuirComissaoMonitoria))}" /> </td>
	            		<td> ${relatorio.projetoEnsino.ano}</td>
	                    <td> ${relatorio.projetoEnsino.titulo}</td>
	                    <td> ${relatorio.tipoRelatorio.descricao} </td>
						<td> <fmt:formatDate value="${relatorio.dataEnvio}" pattern="dd/MM/yyyy"/> </td>
						
						<td width="2%">	
							<h:commandLink  title="Visualizar Projeto de Monitoria" action="#{ projetoMonitoria.view }" immediate="true" id="btView">
						      <f:param name="id" value="#{relatorio.projetoEnsino.id}"/>
						      <h:graphicImage url="/img/view.gif" />
							</h:commandLink>
						</td>
						
						<!--
						trecho comentado para resolver problema da tarefa 95055 
						<td width="2%">
							<h:commandLink title="Distribuir para Avaliar" action="#{ distribuicaoRelatorio.selecionarRelatorio }" immediate="true" id="btDistribuir" 
								rendered="#{((acesso.monitoria) && (relatorio.permitidoDistribuirComissaoMonitoria))}">
							      <f:param name="id" value="#{relatorio.id}"/>
							      <h:graphicImage url="/img/seta.gif"  />
							</h:commandLink>
						</td>	-->
	              </tr>
	          </c:forEach>
		 	</tbody>
		 	
			<tfoot>
				<tr>
					<td colspan="7">
						<center>
							<h:commandButton value="Distribuir" action="#{ distribuicaoRelatorio.distribuirRelatorio }"/>
						</center>
			    	</td>
			    </tr>
			</tfoot>

		 </table>
		 
	</c:if>

<script type="text/javascript">
	
	function selecionarTodos(){
		var todosSelecionados = document.getElementsByClassName("chkSelecionaTodos")[0];
		var checks = document.getElementsByClassName("todosChecks");
		
		 for (i=0;i<checks.length;i++){
			 if(todosSelecionados.checked)
				 checks[i].checked = true;
		 	 else
		 		checks[i].checked = false;
		 }
	}
		
</script>	

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>