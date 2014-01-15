<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Tem certeza que deseja remover este componente deste curriculo?')) return false" scope="request"/>

<style>
<!--
.area{
	background: #C8D5EC;
	font-weight: bold;
	padding-left: 20px;
}
-->
</style>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"><ufrn:subSistema /> &gt; Estrutura Curricular &gt; Disciplinas</h2>
		<h:form id="formulario">
		<h:inputHidden value="#{curriculo.curriculoComponente.id}"/>
		<table class="formulario" width="100%">
			<caption class="formulario">Adicione Disciplinas</caption>
			
			<tr>
				<th class="required" style="font-weight: normal;">Área de Concentração: </th>
				<td> <h:selectOneMenu id="area" onchange="submit()" value="#{curriculo.curriculoComponente.areaConcentracao.id}" >
						<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --" />
						<f:selectItem itemValue="0" itemLabel="COMUM A TODAS AS ÁREAS" />
						<f:selectItems value="#{curriculo.possiveisAreas}" />
					</h:selectOneMenu> </td>
			</tr>
			
			<tr>
				<th class="required" style="font-weight: normal;">Disciplina: </th>
				<td>
					<c:set var="paramBusca" value="programa=${curriculo.obj.curso.unidade.id}"/>
					<c:if test="${curriculo.curriculoComponente.areaConcentracao.id == 0}">
						<c:set var="paramBusca" value="todosOsProgramas=true"/>
					</c:if>										
					<h:inputHidden id="idDisciplina" value="#{curriculo.curriculoComponente.componente.id}"></h:inputHidden>
					<h:inputText id="nomeDisciplina" value="#{curriculo.curriculoComponente.componente.nome}" size="80" />
						<ajax:autocomplete
						source="formulario:nomeDisciplina" target="formulario:idDisciplina"
						baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
						parameters="${paramBusca}"
						indicator="indicatorDisciplina" minimumCharacters="5"
						parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDisciplina" style="display:none; ">
						<img src="/sigaa/img/indicator.gif" /> </span>
				</td>
			</tr>
			
			<c:if test="${curriculo.podeAlterarChEObrigatorias}">
				<tr>
					<th style="font-weight: normal;" class="required">Obrigatória: </th>
					<td> <h:selectOneRadio id="obrigatoria" value="#{curriculo.curriculoComponente.obrigatoria}">
						<f:selectItem itemValue="true" itemLabel="Sim" />
						<f:selectItem itemValue="false" itemLabel="Não"/>
					</h:selectOneRadio>
					</td>
				</tr>
			</c:if>
			<tfoot>
			<tr>
			
			<td colspan="2" align="center">
				<h:commandButton value="Adicionar" action="#{curriculo.adicionarDisciplina}" id="adicionarDisc" />
			</td>
			
			</tr>
			</tfoot>
		</table>
		</h:form>
				
		<br/>
		<c:if test="${empty curriculo.obj.curriculoComponentes}">
			<center>
				<i>Não há disciplinas para esse currículo</i>
			</center>
		</c:if>
		
		<c:if test="${not empty curriculo.obj.curriculoComponentes}">
		<center>
			<div class="infoAltRem">
			
				<c:if test="${acesso.ppg or acesso.secretariaPosGraduacao}">
					<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;" />: Alternar entre Obrigatória / Optativa 
				</c:if>
				
				<c:if test="${acesso.ppg }">
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover da Estrutura Curricular<br />
				</c:if>
				
			</div>
		</center>
			
			<table class="listagem" width="100%" >
				<caption>Disciplinas desse Currículo</caption>
				<thead>
					<tr>
						<th>Componente</th>
						<th>Status</th>
						<th colspan="3"></th>
					</tr>
				</thead>
				<c:set value="-1" var="areaAtual" />
				<tbody>				
				<c:forEach items="${curriculo.obj.curriculoComponentes}" var="cc" varStatus="status" >
					<c:if test="${areaAtual != cc.areaConcentracao.id}">
						<c:set value="${cc.areaConcentracao.id}" var="areaAtual" />		
						<tr style="background-color: #C8D5EC">				
							<td colspan="5"><b>Área de Concentração: ${cc.areaConcentracaoDescricao }</b></td>
						</tr>
					</c:if>
					
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" >
						
						<td>${cc.componente.descricao}</td>
						
						<td><i>${(cc.obrigatoria)?'Obrigatória':'Optativa'}</i></td>
						<td>
							<h:form id="formAlterar">
									<input type="hidden" value="${status.count}" name="linhaComponente" id="hiddenStatusCount"/>
									<h:commandButton image="/img/alterar_old.gif" styleClass="noborder" id="alterar"
										title="Alternar entre Optativa / Obrigatória"
										actionListener="#{curriculo.alterarDisciplinaOptativaObrigatoria}" />
							</h:form>
						</td>
						<td>
							<h:form id="formRemoverCurriculoComponentes">
								<c:if test="${!cc.obrigatoria || curriculo.podeAlterarChEObrigatorias}">
									<input type="hidden" value="${cc.componente.id}" name="idComponente" />
									<h:commandButton image="/img/delete.gif" styleClass="noborder" id="remCC"
										title="Remover Este Componente do Currículo"
										actionListener="#{curriculo.removerComponente}"
										onclick="#{confirmDelete}" />
								</c:if>
							</h:form>
						</td>
					</tr>
				</c:forEach>
				</tbody>
				<tr class="caixaCinzaMedio">
					<td colspan="5"><b>CH Total:</b> ${curriculo.chTotalComponentes}h. &nbsp;&nbsp;&nbsp; <b>Total de Créditos:</b> <fmt:formatNumber pattern="##" value="${curriculo.crTotalComponentes}" /></td>
				</tr>
			</table>
		</c:if>
		<h:form id="botoes">
			<h:commandButton id="voltar" action="#{curriculo.telaDadosGerais}" value="<< Dados Gerais" /> 
			<h:commandButton id="cancelar" action="#{curriculo.cancelar}" value="Cancelar" onclick="#{confirm}" /> 
			<h:commandButton id="submissao" action="#{curriculo.submeterComponentes}" value="Próximo Passo >>" />
		</h:form>
		<br>
		
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br><br></center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>