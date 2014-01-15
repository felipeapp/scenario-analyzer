<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h:outputText value="#{projetoMonitoria.create}"/>	
    
	<h2><ufrn:subSistema /> > Quadro Geral dos Projetos de Ensino</h2>


 	<h:form id="formBusca">

	<table class="formulario" width="90%">
	<caption>Busca por Projetos</caption>
	<tbody>

	     <tr>
			<td width="5%"><h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaEdital}" id="selectBuscaEdital"/></td>
	    	<td> <label for="nomeProjeto"> Edital do Projeto: </label> </td>
	    	<td>
	    	<h:selectOneMenu id="selecionaEdital" onchange="javascript:$('formBusca:selectBuscaEdital').checked = true;" value="#{projetoMonitoria.edital.id}" style="width: 300px">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM EDITAL --"  />
								<f:selectItems value="#{editalMonitoria.allCombo}"/>
			</h:selectOneMenu>
	    </tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaAno}" id="selectBuscaAno" /></td>
	    	<td> <label for="anoProjeto"> Ano do Projeto </label> </td>
	    	<td> <h:inputText value="#{projetoMonitoria.buscaAnoProjeto}" size="10" onchange="javascript:$('formBusca:selectBuscaAno').checked = true;"/></td>
	    </tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaSituacao}"  id="selectBuscaSituacao" /></td>
	    	<td> <label for="situacaoProjeto"> Situação do Projeto </label> </td>
	    	<td>

	    	 <h:selectOneMenu id="selecionaSituacao" value="#{projetoMonitoria.buscaSituacaoProjeto.id}" style="width: 350px"  onchange="javascript:$('formBusca:selectBuscaSituacao').checked = true;">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA SITUAÇÃO --"  />	    	 
				<f:selectItems value="#{projetoMonitoria.tipoSituacaoProjetoCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>
	    
	    
	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{projetoMonitoria.checkGerarRelatorio}"  id="selectGerarRelatorio" />
			</td>
	    	<td colspan="2"> <label> <b>Gerar Relatório</b></label> </td>
	    </tr>	     
	    

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ projetoMonitoria.localizarRelatorioGeral }"/>
			<h:commandButton value="Cancelar" action="#{ projetoMonitoria.cancelar }"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>

	<br/>
	<br/>

	<c:set var="projetos" value="${projetoMonitoria.projetosLocalizados}"/>

	<c:if test="${empty projetos}">
	<center><i> Nenhum Projeto a localizado </i></center>
	</c:if>

	<c:if test="${not empty projetos}">

			 <table class="listagem">
			    <caption>Projetos de Monitoria Encontrados (${ fn:length(projetos) })</caption>
		
			 	<tbody>
		       	<c:forEach items="${projetos}" var="projeto" varStatus="status">
		              <c:set var="corDaLinha" value="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" />
		    	          <tr>
			                    <td>
					              	<table width="100%" class="subFormulario">
		              				    <tr  class="${corDaLinha}">
						                    <td width="15%">Projeto:			</td>	<td> <b>${projeto.titulo} </b></td>
					              		</tr>
		              				    
					              		<tr  class="${corDaLinha}">
						                    <td width="15%">Ano:			</td>	<td> <b>${projeto.ano} </b></td>
					              		</tr>
					              		<tr class="${corDaLinha}">						                    
						                    <td>Centro:			</td>	<td> <b>${projeto.unidade.sigla} - ${projeto.unidade.nome}</b> </td>
					              		</tr>
					              		<tr class="${corDaLinha}">						                    
											<td>Situação:		</td>	<td> <b>${projeto.situacaoProjeto.descricao}</b> </td>
					              		</tr>
					              		<tr class="${corDaLinha}">											
											<td>Bolsas Remun.:	</td>	<td> <b>${projeto.bolsasConcedidas}</b> </td>
					              		</tr>
					              		<tr class="${corDaLinha}">											
											<td>Bolsas Não Remun.:</td>	<td> <b>${projeto.bolsasNaoRemuneradas}</b> </td>
					              		</tr>
					              		<tr class="${corDaLinha}">											
								            <td>Coordenador(a):	</td>  	<td> <b>${projeto.coordenacao != null ? projeto.coordenacao.pessoa.nome : ''} </b> </td>
						              	</tr>
						              	<tr class="${corDaLinha}">
						              		<td colspan="7">
								              	<table width="100%">
					              				    <thead>								              		
								              		<tr>
								              			<th width="50%">Lista de Docentes Envolvidos</th>
								              			<%--<th>Lista de Monitores</th>--%>
								              		</tr>
								              		</thead>
								              		
					              				    <tbody>								              		
								              		<tr class="${corDaLinha}">
								              			<td valign="top">
								              			   
								              				<c:forEach items="${projeto.equipeDocentes}" var="ed">
																<b>${ed.servidor.nome}</b>
																<c:if test="${ed.coordenador}">
																	<font color="red">(COORDENADOR(A))</font>
																</c:if>
																<br/>
															</c:forEach>
															
								              			</td>
								              		</tr>
								              		</tbody>
								              	</table>
							              	</td>
						              	<tr>
					              	</table>					              
					              	<hr/>
			              		</td>
			              </tr>
		          </c:forEach>
			 	</tbody>
			 </table>
		
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>