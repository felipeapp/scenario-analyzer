<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<c:if test="${!acesso.extensao}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	
	<h2><ufrn:subSistema /> > Relatório para Descentralização de Recursos do FAEx</h2>

	<h:outputText value="#{relatoriosAtividades.create}"/>


	<div class="descricaoOperacao">
		<b>Atenção: </b> o relatório será gerado de acordo com as opções selecionadas. Os campos de unidade 
		responsável e unidade proponente serão preenchidos com todas as unidades que solicitaram recursos para o edital selecionado.   
	</div>



 	<h:form id="form">
	<a4j:region>
	<table class="formulario" width="90%">
	<caption>Total de Recursos Autorizados do FAEx</caption>
	<tbody>


		<tr>
			<th class="required">Edital de Extensão: </th>
			<td>
				<h:selectOneMenu id="buscaEdital" value="#{relatoriosAtividades.edital.id}" 
					valueChangeListener="#{relatoriosAtividades.changeEdital}" onchange="submit();">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM EDITAL --" />
					<f:selectItems value="#{editalExtensao.allCombo}" />
				</h:selectOneMenu> 
			</td>
		</tr>
		
		<tr>
	    	<th width="28%"> Unidade Responsável (Centro): </th>
	    	<td>
				<h:selectOneMenu id="buscaUnidadeResponsavel" value="#{relatoriosAtividades.unidade.id}" style="width:95%">
					<f:selectItem itemValue="0" itemLabel="-- TODAS AS UNIDADES RESPONSÁVEIS --"/>
					<f:selectItems value="#{relatoriosAtividades.unidadesResponsaveisEditalAtualCombo}"/>
				</h:selectOneMenu>
	    	 </td>
	    </tr>

<%--  	
		<tr>
	    	<th width="28%"> Unidade Proponente: </th>
	    	<td>
				<h:selectOneMenu id="buscaUnidadeProponente" value="#{relatoriosAtividades.unidadeProponente.id}" style="width:95%" >
					<f:selectItem itemValue="0" itemLabel="-- TODAS AS UNIDADES PROPONENTES --"/>
					<f:selectItems value="#{relatoriosAtividades.unidadesProponentesEditalAtualCombo}"/>
				</h:selectOneMenu>
	    	 </td>
	    </tr>
--%>

		<tr>
	    	<th width="28%"> Área Temática: </th>
	    	<td>
				<h:selectOneMenu id="buscaAreaTematica" value="#{relatoriosAtividades.areaTematica.id}" style="width:95%" >
					<f:selectItem itemValue="0" itemLabel="-- TODAS AS ÁREAS TEMÁTICAS --"/>
					<f:selectItems value="#{areaTematica.allCombo}"/>
				</h:selectOneMenu>
	    	 </td>
	    </tr>

        <tr>
            <th width="28%"> Dimensão Acadêmica: </th>
            <td>
               <h:selectOneMenu id="buscaAcaoAssociadas" value="#{relatoriosAtividades.buscaAcoesAssociadas}">
                     <f:selectItem itemLabel="-- TODAS --" itemValue="" />
                     <f:selectItem itemLabel="AÇÕES ASSOCIADAS" itemValue="TRUE" />
                     <f:selectItem itemLabel="AÇÕES ISOLADAS" itemValue="FALSE" />
               </h:selectOneMenu>
            </td>
        </tr>


		
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Gerar Relatório" action="#{ relatoriosAtividades.relatorioDescentralizacaoRecursosFaex }" id="gerar_relatorio_"/>
			<h:commandButton value="Cancelar" action="#{ relatoriosAtividades.cancelar }" onclick="#{confirm}" immediate="true"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>
	</a4j:region>

	</h:form>
	
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	

	<br/>

<%-- 

	<c:set var="result" value="${relatoriosAtividades.atividadesLocalizadas}"/>

	<c:if test="${empty result}">
	<center><i> Nenhuma ação de extensão localizada </i></center>
	</c:if>


	<c:if test="${not empty result}">

		 <table class="listagem" width="100%" id="lista">
		    <caption> Resultado da busca para ${ relatoriosAtividades.edital.descricao} e ${ relatoriosAtividades.unidade.nome}. Ações Localizadas (${fn:length(result)})</caption>
	
		      <thead>
		      	<tr>
					<th>Código</th>
		        	<th>Título da Ação</th>
		        	<th>Área Temática</th>
					<th>Unidade Proponente</th>
		        	<th>Unidade Responsável</th>
		        </tr>
		 	</thead>
		 	<tbody>
				 <c:set value="0" var="total_geral_recursos"/>		 	
				 <c:set value="0" var="total_geral_bolsas"/>
	    		 <c:forEach items="${result}" var="atv" varStatus="status">
	       		
		             <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		                    <td style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;"> ${atv.codigo}</td>
		                    <td style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;" width="40%"> ${atv.titulo} <br/><i>Coordenador(a): ${atv.coordenacao.pessoa.nome}</i></td>
		                    <td style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;"> ${atv.areaTematicaPrincipal.descricao} </td>
		                    <td style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;"> ${atv.unidade.nome}</td>                    
		                    <td style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;"> ${atv.unidade.unidadeResponsavel.nome}</td>
		             </tr>
	              
		              <tr>
		                    <td colspan="4"> Recurso</td>
		                    <td align="right"> Valor Concedido</td>
		              </tr>

						<c:set value="0" var="total_acao"/>
		              	<c:forEach items="${atv.orcamentosConsolidados}" var="orc" varStatus="status">
		              	  <c:set value="${total_acao + orc.fundoConcedido}" var="total_acao"/>
			              <tr>
			                    <td colspan="4"> ${orc.elementoDespesa.descricao}</td>
			                    <td align="right">R$	<fmt:formatNumber value="${orc.fundoConcedido}"  pattern="#,##0.00" type="currency" /></td>
			              </tr>
		                </c:forEach>
	
						  <tr>
			                    <td colspan="4"><b><i>Total de Recursos</i></b></td>
			                    <td align="right"><b><i>R$	<fmt:formatNumber value="${total_acao}"   pattern="#,##0.00" type="currency"/></i></b></td>
			              </tr>
			              <tr>
			                    <td colspan="4"><b><i>Total de Bolsas</i></b></td>
			                    <td align="right"><b><i><fmt:formatNumber value="${atv.bolsasConcedidas}"   pattern="##0"/></i></b></td>
			              </tr>
			              
			  			<c:set value="${total_geral_recursos + total_acao}" var="total_geral_recursos"/>
			  			<c:set value="${total_geral_bolsas + atv.bolsasConcedidas}" var="total_geral_bolsas"/>
	          </c:forEach>
	          
		 	</tbody>
		 	<tfoot>
		 	       <tr class="linhaPar">
	              		<td colspan="4"><b>Total Geral de Recursos</b> </td>
	                    <td align="right"><b>R$	<fmt:formatNumber value="${total_geral_recursos}"  pattern="#,##0.00" type="currency"/></b></td>
	              </tr>
	              <tr class="linhaPar">
	              		<td colspan="4"><b>Total Geral de Bolsas</b> </td>
	                    <td align="right"><b><fmt:formatNumber value="${total_geral_bolsas}"  pattern="##0"/></b></td>
	              </tr>	              
	              	              
		 	</tfoot>
		 	
		 </table>
	
	</c:if>
	
	--%>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>