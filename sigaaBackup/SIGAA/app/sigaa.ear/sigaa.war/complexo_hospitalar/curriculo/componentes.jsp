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
	<h2 class="title"><ufrn:subSistema /> &gt; Estrutura Curricular &gt; Disciplinas</h2>
		<h:form id="formulario">
		<h:inputHidden value="#{curriculo.curriculoComponente.id}"/>
		<table class="formulario" width="100%">
			<caption class="formulario">Adicione Disciplinas</caption>
			
			<tr>
				<th style="font-weight: normal;" class="required">Disciplina: </th>
				<td>
					<h:inputHidden id="idDisciplina" value="#{curriculo.curriculoComponente.componente.id}"></h:inputHidden>
					<h:inputText id="nomeDisciplina" value="#{curriculo.curriculoComponente.componente.nome}" size="80" />
						<ajax:autocomplete
						source="formulario:nomeDisciplina" target="formulario:idDisciplina"
						baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
						indicator="indicatorDisciplina" minimumCharacters="4"
						parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDisciplina" style="display:none; ">
						<img src="/sigaa/img/indicator.gif" /> </span>
				</td>
			</tr>
			
			<tr>
				<th style="font-weight: normal;" class="required">Nível da Residência: </th>
				<td>
					<h:selectOneMenu value="#{curriculo.nivelDisciplinaResidencia}" id="nivelEntrada">
						<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0" />
						<f:selectItem itemLabel="1º Ano (R1)" itemValue="1" />
						<f:selectItem itemLabel="2º Ano (R2)" itemValue="2" />
						<f:selectItem itemLabel="3º Ano (R3)" itemValue="3" />
						<f:selectItem itemLabel="4º Ano (R4)" itemValue="4" />
						<f:selectItem itemLabel="5º Ano (R5)" itemValue="5" />
					</h:selectOneMenu>
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
				<h:commandButton value="Adicionar" action="#{curriculo.adicionarDisciplinaResidenciaMedica}" id="adicionarResid" />
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
				<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;" />: Alternar entre Obrigatória / Optativa
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover da Estrutura Curricular
			</div>
		</center>
		
			<table class="listagem" width="100%" >
				<caption>Disciplinas desse Currículo</caption>
				<thead>
					<tr>
						<th>Nível da Residência</th>
						<th>Componente</th>
						<th>Status</th>
						<th colspan="3"></th>
					</tr>
				</thead>
				<c:set value="0" var="ch" />
				<tbody>				
				<c:forEach items="${curriculo.obj.curriculoComponentes}" var="cc" varStatus="status" >
					<c:set value="${ch + cc.componente.chTotal }" var="ch" />
					
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" >
						
						<td>${cc.semestreOferta}</td>
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
										title="Remover da Estrutura Curricular"
										actionListener="#{curriculo.removerComponente}"
										onclick="#{confirmDelete}" />
								</c:if>
							</h:form>
						</td>
					</tr>
				</c:forEach>
				</tbody>
				<tr class="caixaCinzaMedio">
					<td colspan="5"><b>CH Total:</b> ${ch}h. &nbsp;&nbsp;&nbsp; <b>Total de Créditos:</b> <fmt:formatNumber pattern="##" value="${ch/15 }" /></td>
				</tr>
			</table>
		</c:if>
		<h:form id="botoes">
			<h:commandButton id="voltar" action="#{curriculo.telaDadosGerais}" value="<< Dados Gerais" /> 
			<h:commandButton id="cancelar" action="#{curriculo.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true"/> 
			<h:commandButton id="submissao" action="#{curriculo.submeterComponentes}" value="Próximo Passo >>" />
		</h:form>
		<br/>
		
		<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>