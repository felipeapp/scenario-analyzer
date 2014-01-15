<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.colLeft{text-align: left; }
	.colCenter{text-align: center; }
	.colRight{text-align: right; }
</style>
<script type="text/javascript">
function marcarTodos() {
	var elements = document.getElementsByTagName('input');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].type == 'checkbox') {
			elements[i].checked = !elements[i].checked;
		}
	}
}
</script>
<f:view>
	<h2><ufrn:subSistema /> &gt; Conclusão Coletiva de Programa</h2>

	<a4j:keepAlive beanName="conclusaoCursoLatoMBean" />

	<div class="descricaoOperacao">
		<p>Caro Usuário</p>
		<p>Informe o nome de um curso e realize a busca por discentes.</p>
		<p> A data de conclusão dos discentes é a mesma data de conclusão do curso.
		 Informe um ano-período de conclusão e selecione somente aqueles que irão concluir o curso.</p>
	</div>
	<br/>
	<h:form id="formulario">
		<table class="formulario" width="80%">
			<caption>Informe um Curso</caption>
				<tr>
					<th class="required" width="25%">Curso:</th>
					<td>
						<h:inputHidden id="idCurso" value="#{conclusaoCursoLatoMBean.obj.id}"></h:inputHidden>
						<h:inputText id="nomeCurso"
								value="#{conclusaoCursoLatoMBean.obj.nome}" size="80" maxlength="120"/> 
						<ajax:autocomplete source="formulario:nomeCurso" target="formulario:idCurso"
								baseUrl="/sigaa/ajaxCurso" className="autocomplete"
								indicator="indicatorCurso" minimumCharacters="3" parameters="nivel=L"
								parser="new ResponseXmlToHtmlListParser()" /> 
						<span id="indicatorCurso" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
					</td>
				</tr>
				<tfoot>
					<tr>
						<td colspan="2">
						<h:commandButton value="Buscar" action="#{conclusaoCursoLatoMBean.carregarAlunos}" id="buscar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{conclusaoCursoLatoMBean.cancelar}" id="cancelarBusca"/>
						</td>
					</tr>
				</tfoot>
		</table>
		<c:if test="${ not empty conclusaoCursoLatoMBean.discentes }">
			<table class="formulario" width="80%">
				<caption>Dados da Conclusão dos Discentes</caption>
					<tr>
						<th class="rotulo">Data de Conclusão:</th>
						<td>
							<h:outputText id="dataConclusao" value="#{conclusaoCursoLatoMBean.obj.dataFim}"/>
						</td>
					</tr>
					<tr>
						<th class="required" width="25%">Ano-Período de Conclusão:</th>
						<td>
							<h:inputText id="ano" value="#{conclusaoCursoLatoMBean.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this);"/> -
							<h:inputText id="periodo" value="#{conclusaoCursoLatoMBean.periodo}" size="1" maxlength="1" onkeyup="formatarInteiro(this);"/>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="subFormulario">Selecione os alunos que serão concluídos</td>
					</tr>
					<tr>
						<td colspan="2" style="text-align: center;">
							<h:dataTable id="dtTableDiscentes" var="d" value="#{conclusaoCursoLatoMBean.discentes}" styleClass="listing" rowClasses="linhaPar, linhaImpar" 
								style="width:100%" columnClasses="colLeft,colLeft,colLeft,colLeft">
								<h:column headerClass="colCenter">
									<f:facet name="header">
										<f:verbatim>
											<a href="#" onclick="marcarTodos();">Todos</a>
										</f:verbatim>
									</f:facet>
									<h:selectBooleanCheckbox id="checkBoxSelecionado" value="#{d.selecionado}" rendered="#{d.ativo}"/>
								</h:column>
								<h:column headerClass="colRight">
									<f:facet name="header"><h:outputText value="Matrícula"/></f:facet>
									 <h:outputText value="#{d.matricula}"/>
								</h:column>
								<h:column headerClass="colLeft">
									<f:facet name="header"><h:outputText value="Nome"/></f:facet>
								 	<h:outputText value="#{d.pessoa.nome}" />
								</h:column>
								<h:column headerClass="colLeft">
									<f:facet name="header"><h:outputText value="Status"/></f:facet>
									<h:outputText value="#{d.statusString}" />
								</h:column>
							</h:dataTable>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<c:set var="exibirApenasSenha" value="true" scope="request" />
							<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>				
						</td>							
					</tr>	
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton id="concluirAlunos" value="Concluir Alunos" action="#{conclusaoCursoLatoMBean.concluirAlunos}" /> 
							<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{conclusaoCursoLatoMBean.cancelar}" />
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>
	</h:form>
	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>