<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h2><ufrn:subSistema /> > Cálculos de Integralização de Discentes</h2>
<br>
	<h:outputText value="#{calculosDiscente.create}"></h:outputText>
	<h:form id="formulario">
		<table class="formulario">
			<caption>Selecione um Discente e o Tipo de Cálculo</caption>
			<tr>
				<th class="required" style="vertical-align: middle;">Discente:</th>
				<td>
				<h:inputHidden id="idDiscente" value="#{calculosDiscente.discente.id}"></h:inputHidden>
				<h:inputText id="nomeDiscente" value="#{calculosDiscente.discente.pessoa.nome}" size="90" />
					<ajax:autocomplete
					source="formulario:nomeDiscente" target="formulario:idDiscente"
					baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
					indicator="indicator" minimumCharacters="3"
					parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicator" style="display:none; ">
					<img src="/sigaa/img/indicator.gif" /> </span>
				</td>
			</tr>
			<tr>
				<th>Novo regulamento:</th>
				<td>
				<h:selectOneRadio id="novoRegulamento" value="#{calculosDiscente.novo}">
					<f:selectItems value="#{calculosDiscente.simNao}"/>
				</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th>Zerar Integralizações: </th>
				<td>
				<h:selectOneRadio id="zerarIntegralizacoes" value="#{calculosDiscente.zerarIntegralizacoes}">
					<f:selectItems value="#{calculosDiscente.simNao}"/>
				</h:selectOneRadio>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton value="Calcular" action="#{calculosDiscente.calcular}" id="btnCalcular"></h:commandButton>
					<h:commandButton value="Cancelar" action="#{calculosDiscente.cancelar}" id="btnCancelar" onclick="#{confirm }"></h:commandButton>
					</td>
				</tr>
			</tfoot>
		</table>
		<c:if test="${calculosDiscente.discente.id > 0}">
		<br>
		<center>
		<table class="listagem" style="width: 100%">
			<caption>${calculosDiscente.discente.matriculaNome}</caption>
			<tr>
				<th><strong>Matriz: </strong></th>
				<td>${calculosDiscente.discente.matrizCurricular.descricao}</td>
			</tr>
			<c:if test="${ !calculosDiscente.novo }">
			<tr>
				<th><strong>IRA: </strong></th>
				<td>${calculosDiscente.discente.ira}</td>
			</tr>
			</c:if>
			<c:if test="${ calculosDiscente.novo }">
				<c:forEach var="indice" items="${calculosDiscente.indices}">
				<tr>
					<th><strong>${ indice.indice.sigla }: </strong></th>
					<td>${indice.valor}</td>
				</tr>
				</c:forEach>
			</c:if>
			<tr>
				<th><strong>Status: </strong></th>
				<td>${calculosDiscente.discente.statusString}</td>
			</tr>
			<tr>
				<th><strong>Período Atual: </strong></th>
				<td>${calculosDiscente.discente.periodoAtual}</td>
			</tr>
			<tr>
				<th><strong>Perfil Inicial: </strong></th>
				<td>${calculosDiscente.discente.perfilInicial}</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Cargas Horárias</td>
			</tr>
			<tr>
				<th><strong>Componentes Curriculares Obrigatórios Pendentes: </strong></th>
				<td>${calculosDiscente.discente.totalAtividadesPendentes == null ? 0 : calculosDiscente.discente.totalAtividadesPendentes }</td>
			</tr>
			<tr>
				<th><strong>Total de CH Integralizada: </strong></th>
				<td>${calculosDiscente.discente.chTotalIntegralizada}</td>
			</tr>
			<tr>
				<th><strong>Total de CH Pendente: </strong></th>
				<td>${calculosDiscente.discente.chTotalPendente}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Componentes Optativos Integralizada: </strong></th>
				<td>${calculosDiscente.discente.chOptativaIntegralizada}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Componentes Optativos Pendente: </strong></th>
				<td>${calculosDiscente.discente.chOptativaPendente}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Componentes (Exceto Atividades) Obrigatórios Integralizada: </strong></th>
				<td>${calculosDiscente.discente.chNaoAtividadeObrigInteg}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Componentes (Exceto Atividades) Obrigatórios Pendente: </strong></th>
				<td>${calculosDiscente.discente.chNaoAtividadeObrigPendente}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Atividades Obrigatórias Integralizada: </strong></th>
				<td>${calculosDiscente.discente.chAtividadeObrigInteg}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Atividades Obrigatórias Pendente: </strong></th>
				<td>${calculosDiscente.discente.chAtividadeObrigPendente}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Aula Integralizada: </strong></th>
				<td>${calculosDiscente.discente.chAulaIntegralizada}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Aula Pendente: </strong></th>
				<td>${calculosDiscente.discente.chAulaPendente}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Laboratório Integralizada: </strong></th>
				<td>${calculosDiscente.discente.chLabIntegralizada}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Laboratório Pendente: </strong></th>
				<td>${calculosDiscente.discente.chLabPendente}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Estágio Integralizada: </strong></th>
				<td>${calculosDiscente.discente.chEstagioIntegralizada}</td>
			</tr>
			<tr>
				<th><strong>Total de CH de Estágio Pendente: </strong></th>
				<td>${calculosDiscente.discente.chEstagioPendente}</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Créditos</td>
			</tr>
			<tr>
				<th><strong>Total de Créditos Integralizados: </strong></th>
				<td>${calculosDiscente.discente.crTotalIntegralizados}</td>
			</tr>
			<tr>
				<th><strong>Total de Créditos Pendentes: </strong></th>
				<td>${calculosDiscente.discente.crTotalPendentes}</td>
			</tr>
			<tr>
				<th><strong>Total de Créditos de Componentes Extra-Curriculares Integralizados: </strong></th>
				<td>${calculosDiscente.discente.crExtraIntegralizados}</td>
			</tr>
			<tr>
				<th><strong>Total de Créditos de Componentes (Exceto Atividades) Obrigatórios Integralizados: </strong></th>
				<td>${calculosDiscente.discente.crNaoAtividadeObrigInteg}</td>
			</tr>
			<tr>
				<th><strong>Total de Créditos de Componentes (Exceto Atividades) Obrigatórios Pendentes: </strong></th>
				<td>${calculosDiscente.discente.crNaoAtividadeObrigPendente}</td>
			</tr>
			<tr>
				<th><strong>Total de Créditos de Laboratório Integralizados: </strong></th>
				<td>${calculosDiscente.discente.crLabIntegralizado}</td>
			</tr>
			<tr>
				<th><strong>Total de Créditos de Laboratório Pendentes: </strong></th>
				<td>${calculosDiscente.discente.crLabPendente}</td>
			</tr>
			<tr>
				<th><strong>Total de Créditos de Estágio Integralizados: </strong></th>
				<td>${calculosDiscente.discente.crEstagioIntegralizado}</td>
			</tr>
			<tr>
				<th><strong>Total de Créditos de Estágio Pendentes: </strong></th>
				<td>${calculosDiscente.discente.crEstagioPendente}</td>
			</tr>
			<tr>
				<th><strong>Total de Créditos de Aula Integralizados: </strong></th>
				<td>${calculosDiscente.discente.crAulaIntegralizado}</td>
			</tr>
			<tr>
				<th><strong>Total de Créditos de Aula Pendentes: </strong></th>
				<td>${calculosDiscente.discente.crAulaPendente}</td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Matrículas</td>
			</tr>
			<tr>
			<td colspan="2">
				<c:if test="${empty calculosDiscente.matriculas }">
						<p class="vazio" style="padding-left: 2%;"> Nenhuma matrícula encontrada. </p>
					</c:if>
					<c:if test="${not empty calculosDiscente.matriculas }">
						<table width="100%">
						<thead>
							<th width="8%" style="text-align: center;">Ano/Período</th>
							<th>Componente Curricular</th>
							<th width="3%" style="text-align: right;">CH</th>
							<th width="6%" style="text-align: right; padding-right: 2%;">Créditos</th>
							<th width="10%">Tipo Integralização</th>
							<th width="10%" style="text-align: center;">Situação da Matrícula</th>
						</thead>
						<c:forEach items="${calculosDiscente.matriculas}" var="mat" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td style="text-align: center;">${mat.anoPeriodo}</td>
							<td>${mat.componenteDescricaoResumida}</td>
							<td style="text-align: right;">${mat.componente.chTotal}</td>
							<td style="text-align: right; padding-right: 2%;">${mat.componente.crTotal}</td>
							<td>${mat.tipoIntegralizacaoDescricao}</td>
							<td style="text-align: center;">${mat.situacaoMatricula.descricao}</td>
						</tr>
						</c:forEach>
						</table>
					</c:if>
			</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">Grupos de Optativas</td>
			</tr>
			<tr>
				<td colspan="2">
					<c:if test="${empty calculosDiscente.gruposOptativas }">
						<p class="vazio" style="padding-left: 2%;"> Nenhum grupo de optativas encontrado. </p>
					</c:if>
					<c:if test="${not empty calculosDiscente.gruposOptativas }">
							<table width="100%">
							<thead>
								<th>Descrição</th>
								<th width="8%" nowrap="nowrap" style="text-align: right;">CH Pendente</th>
								<th width="8%" style="text-align: right;">CH Mínima</th>
							</thead>
							<c:forEach var="grupo" items="${ calculosDiscente.gruposOptativas }" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<td>${ grupo.descricao }</td>
								<td style="text-align: right;">${ grupo.chPendente }</td>
								<td style="text-align: right;">${ grupo.chMinima }</td>
							</tr>
							</c:forEach>
							</table>	
					</c:if>
				</td>
			</tr>
			
		</table>
		</center>
	</c:if>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
</f:view>
<script type="text/javascript">
<!--
$('formulario:nomeDiscente').focus();
//-->
</script>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>