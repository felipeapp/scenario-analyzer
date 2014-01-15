<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="estornoConvocacaoVestibularMBean"></a4j:keepAlive>
	<h2>
		<ufrn:subSistema />&gt; Estornar Cadastramento/Cancelamento de Convocação de Discentes
	</h2>
	<div class="descricaoOperacao">
		<p>
			Esta operação permite estornar um cancelamento de convocação de
			candidato aprovado no Vestibular. O discente estornado volta a ter o
			status de <b>PENDENTE DE CADASTRO</b>.
		</p>
	</div>

	<h:form id="form">

<table class="formulario" width="80%">
	<caption>Informe os Parâmetros para a Busca</caption>
	<tr>
		<th class="obrigatorio" width="25%">Processo Seletivo Vestibular:</th>
		<td>
			<h:selectOneMenu id="selectPsVestibular" value="#{estornoConvocacaoVestibularMBean.processoSeletivo.id}" 
				valueChangeListener="#{estornoConvocacaoVestibularMBean.changeProcessoSeletivo}">
				<f:selectItem itemValue="" itemLabel="-- SELECIONE --" />
				<f:selectItems id="itensPsVestibular" value="#{convocacaoVestibular.processoSeletivoVestibularCombo}" />
				<a4j:support event="onchange" reRender="form"/>
				<a4j:status>
					<f:facet name="start" >
						<h:graphicImage value="/img/indicator.gif" />
					</f:facet>
				</a4j:status>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th>Matriz Curricular:</th>
		<td>
			<h:selectOneMenu id="selectMatriz" value="#{estornoConvocacaoVestibularMBean.matrizCurricular.id}" 
			 	style="width: 80%;">
				<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
				<f:selectItems id="listaMatrizes" value="#{estornoConvocacaoVestibularMBean.matrizesCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<c:if test="${ estornoConvocacaoVestibularMBean.processoSeletivo.id > 0 }">
		<tr>
			<th>Ano-Período de Ingresso:</th>
			<td>
				<h:outputText value="#{estornoConvocacaoVestibularMBean.processoSeletivo.anoEntrada}.#{estornoConvocacaoVestibularMBean.processoSeletivo.periodoEntrada}"
					rendered="#{not estornoConvocacaoVestibularMBean.processoSeletivo.entradaDoisPeriodos}"/>
				<h:selectOneMenu id="periodoEntrada"
					value="#{estornoConvocacaoVestibularMBean.processoSeletivo.periodoEntrada}"
					rendered="#{estornoConvocacaoVestibularMBean.processoSeletivo.entradaDoisPeriodos}">
					<f:selectItem itemValue="1" itemLabel="#{estornoConvocacaoVestibularMBean.processoSeletivo.anoEntrada}.1"/>
					<f:selectItem itemValue="2" itemLabel="#{estornoConvocacaoVestibularMBean.processoSeletivo.anoEntrada}.2"/>
				</h:selectOneMenu>
			</td>
		</tr>
	</c:if>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="btnBuscar" value="Buscar Discentes" action="#{estornoConvocacaoVestibularMBean.buscar}"/>
				<h:commandButton value="Cancelar" action="#{ estornoConvocacaoVestibularMBean.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
			</td>
		</tr>
	</tfoot>
</table>
<br/>
<c:if test="${ not empty estornoConvocacaoVestibularMBean.resultadosBusca }">
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Estornar Convocação
	</div>
	<br />
	<table class="listagem">
		<caption>Discentes encontrados (${fn:length(estornoConvocacaoVestibularMBean.resultadosBusca)})</caption>
		<thead>
			<tr>
				<th style="text-align: center;">Ingresso</th>
				<th style="text-align: center;">Matrícula</th>
				<th>Nome</th>
				<th>Status</th>
				<th></th>
			</tr>
		</thead>
		<c:set var="matrizAnterior" value="" />
		<c:forEach items="#{estornoConvocacaoVestibularMBean.resultadosBusca}" var="conv" varStatus="loop">
			<c:if test="${ matrizAnterior != conv.convocacaoProcessoSeletivo.descricao }">
				<tr>
					<td colspan="6" class="subFormulario">${conv.convocacaoProcessoSeletivo.descricao}</td>
				</tr>
				<c:set var="matrizAnterior" value="${ conv.convocacaoProcessoSeletivo.descricao }" /> 
			</c:if>
			<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td style="text-align: center;"><h:outputText value="#{conv.discente.anoPeriodoIngresso}" /></td>
				<td style="text-align: center;"><h:outputText value="#{conv.discente.matricula}" /></td>
				<td><h:outputText value="#{conv.discente.pessoa.nome}"/></td>
				<td><h:outputText value="#{conv.discente.statusString}"/></td>
				<td>
					<h:commandLink styleClass="noborder" title="Estornar Convocação" action="#{estornoConvocacaoVestibularMBean.selecionaConvocacao}" id="estornar">
						<f:param name="id" value="#{conv.id}" />
						<h:graphicImage url="/img/seta.gif" />
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
	</table>
</c:if>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>