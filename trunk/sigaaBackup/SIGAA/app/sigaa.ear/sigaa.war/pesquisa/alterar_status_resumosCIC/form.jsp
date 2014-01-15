<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Alterar Status de Resumos CIC</h2>
	<a4j:keepAlive beanName="alterarStatusResumos" />
	<h:form id="form">
		<div class="descricaoOperacao">
			<p align="center">
				Selecione os Resumos para alterar o status.
			</p>
		</div>
		
		<table class="formulario" width="80%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<th></th>
					<td>Congresso: <h:graphicImage url="/img/required.gif"/></td>
					<td>
						<h:selectOneMenu id="congresso" value="#{alterarStatusResumos.congresso.id}" >
							<f:selectItems value="#{congressoIniciacaoCientifica.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{alterarStatusResumos.filtroCentro}" id="checkCentro" styleClass="noborder"/>
					</th>
					<td>
						<label for="checkCentro" onclick="$('form:Centro').checked = !$('form:checkCentro').checked;">Centro/Unidade:</label>
					</td>
					<td>
						<h:selectOneMenu id="departamento" value="#{alterarStatusResumos.unidade.id}" onfocus="$('form:checkCentro').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{alterarStatusResumos.filtroArea}" id="checkArea" styleClass="noborder"/>
					</th>
					<td>
						<label for="checkArea" onclick="$('form:Area').checked = !$('form:checkArea').checked;">Área de Conhecimento:</label>
					</td>
					<td>
						<h:selectOneMenu id="area" value="#{alterarStatusResumos.area.id}" onfocus="$('form:checkArea').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{alterarStatusResumos.allAreaCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{alterarStatusResumos.filtroOrientador}" id="checkOrientador" styleClass="noborder"/>
					</th>
					<td>
						<label for="checkOrientador" onclick="$('form:Orientador').checked = !$('form:checkOrientador').checked;">Orientador:</label>
					</td>
					<td>
						<h:inputHidden id="idCoordenador" value="#{alterarStatusResumos.orientador.id}" /> 
						<h:inputText id="nomeCoordenador" value="#{alterarStatusResumos.orientador.pessoa.nome}"	size="60" onkeyup="CAPS(this);" onclick="$('form:checkOrientador').checked = true;" /> 
						<ajax:autocomplete
							source="form:nomeCoordenador" target="form:idCoordenador"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicatorDocente" minimumCharacters="3"
							parameters="tipo=ufrn,situacao=ativo"
							parser="new ResponseXmlToHtmlListParser()" /> 
						<span id="indicatorDocente" style="display: none;"> 
							<img src="/sigaa/img/indicator.gif" alt="Carregando..." title="Carregando..." /> 
						</span>
					</td>
				</tr>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{alterarStatusResumos.filtroStatus}" id="checkStatus" styleClass="noborder"/>
					</th>
					<td>
						<label for="checkStatus" onclick="$('form:Status').checked = !$('form:checkStatus').checked;">Status:</label>
					</td>
					<td>
						<h:selectOneMenu id="stats" value="#{alterarStatusResumos.obj.status}" onfocus="$('form:checkStatus').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{alterarStatusResumos.allStatusCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{alterarStatusResumos.buscarResumos}" /> 
						<h:commandButton value="Cancelar" action="#{alterarStatusResumos.cancelar}" id="cancelar" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
		
		<br />
		
		<c:set value="${alterarStatusResumos.resultadosBusca}" var="total" />
		<c:if test="${ not empty alterarStatusResumos.resultadosBusca}">
		<table class="listagem" >
			<caption class="listagem">Lista de Resumos CIC (${fn:length(total)})</caption>
			<tr>
				<td>
					<c:if test="${not empty alterarStatusResumos.resultadosBusca }">
						<t:dataTable value="#{alterarStatusResumos.lista}" var="item" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">
							
							<tr>
								<td>
									<t:column>
										<f:facet name="header">
											<h:selectBooleanCheckbox styleClass="chkSelecionaTodos" onclick="selecionarTodos();" />
										</f:facet>
										<h:selectBooleanCheckbox value="#{item.selecionado}" styleClass="todosChecks" />
									</t:column>
								</td>
								<td>
									<t:column>
										<f:facet name="header">
											<f:verbatim>Código</f:verbatim>
										</f:facet>
								 		<h:outputText value="#{item.codigo}" />
									</t:column>
								</td>
								<td>
									<t:column> 
										<f:facet name="header">
											<f:verbatim>Autor</f:verbatim>
										</f:facet>
										<h:outputText value="#{item.autor.nome}"/>
									</t:column>
								</td>
								<td>
									<t:column> 
										<f:facet name="header">
											<f:verbatim>Orientador</f:verbatim>
										</f:facet>
										<h:outputText value="#{item.orientador.nome}"/>
									</t:column>
								</td>
								<td> 
									<t:column>
										<f:facet name="header">
											<f:verbatim>Status</f:verbatim>
										</f:facet>
										<h:outputText value="#{item.statusString}"/>
									</t:column>
								</td>
								<td> 
									<t:column>
										<f:facet name="header">
											<f:verbatim></f:verbatim>
										</f:facet>
										<h:commandLink title="Visualizar" action="#{alterarStatusResumos.visualizarResumo}">
											<f:param name="id" value="#{item.id}"/>
											<h:graphicImage url="/img/view.gif"/>
										</h:commandLink>
									</t:column>
								</td>
							</tr>
						</t:dataTable>
					</c:if>
				</td>	
			</tr>
						
			<tfoot align="center">
				<tr>
					<td colspan="5">
						<h:commandButton value="Avançar >>" action="#{alterarStatusResumos.mudancaStatusSelecionados}" /> 
						<h:commandButton value="Cancelar" action="#{alterarStatusResumos.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	    </c:if>
		
	</h:form>

</f:view>

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

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
