<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form>
	<h2><ufrn:subSistema /> > Carga Horária Dedicada ao Ensino à Distância</h2>
	
	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Utilize o formulário abaixo para buscar por docentes que lecionam em turmas de ensino à distância.</p>
		<p>No resultado da busca, informe os valores para a <b>carga horária semanal</b> dedicada pelo docente no ensino à distância.</p>
	</div>
	<br/>
	<table class="formulario" width="80%">
		<caption class="listagem">Informe o Ano-Período</caption>
		<tbody>
			<tr>
				<th class="obrigatorio">Ano-Período:</th>
				<td>
					<h:inputText value="#{ cargaHorariaEadMBean.obj.ano }" converter="#{ intConverter }" 
						onkeyup="return formatarInteiro(this);" size="5" maxlength="4" id="ano" /> -
					<h:inputText value="#{ cargaHorariaEadMBean.obj.periodo }" converter="#{ intConverter }" 
						onkeyup="return formatarInteiro(this);" size="1" maxlength="1" id="periodo"/>
				</td>
			</tr>
			<tr>
				<th>Nome do Docente:</th>
				<td>
					<h:inputText value="#{ cargaHorariaEadMBean.nomeDocente}" size="60" maxlength="60" id="nomeDocente"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Buscar" action="#{cargaHorariaEadMBean.buscar}" id="buscar"/>
					<h:commandButton value="Cancelar" action="#{cargaHorariaEadMBean.cancelar}" onclick="#{confirm}" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	<br />
	<div class="obrigatorio" style="width: 100%;">Campos de preenchimento obrigatório.</div>
	<br/>
	<c:if test="${not empty cargaHorariaEadMBean.resultadosBusca }">
		<table class="formulario" width="90%">
			<caption>COMPONENTES CURRICULARES POR DOCENTES</caption>
			<thead>
				<tr>
					<th style="text-align: center;">Código</th>
					<th style="text-align: center;">Componente Curricular</th>
					<th style="text-align: right;">CH do <br/>Componente</th>
					<th style="text-align: right;">Turmas</th>
					<th style="text-align: right;">Discentes</th>
					<th style="text-align: right;">CH Semanal<br/>Dedicada</th>
				</tr>
			</thead>
			<tbody>
				<c:set var="grupoDocente" value="" />
				<c:set var="azul" value="true" />
				<c:forEach items="#{ cargaHorariaEadMBean.resultadosBusca }" var="item">
					<c:if test="${not empty item.servidor.id}">
						<c:set var="grupoAtual" value="${ item.servidor.siapeNome }" />
					</c:if>
					<c:if test="${empty item.servidor.id}">
						<c:set var="grupoAtual" value="${ item.docenteExterno.nome }" />
					</c:if>
					<c:if test="${ grupoDocente != grupoAtual }">
						<tr>
							<td colspan="6" class="subFormulario">
								<h:outputText value="#{ item.servidor.nome } (SIAPE: #{ item.servidor.siape } / REGIME: #{ item.servidor.descricaoRegimeTrabalho })" rendered="#{ not empty item.servidor.id }" />
								<h:outputText value="#{ item.docenteExterno.nome } (DOCENTE EXTERNO)" rendered="#{ empty item.servidor.id }" />
							</td>
						</tr>
						<c:set var="grupoDocente" value="${ grupoAtual }" />
						<c:set var="azul" value="true" />
					</c:if>
					<tr class="${ azul ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: center;">${ item.componenteCurricular.codigo }</td>
						<td>${ item.componenteCurricular.nome }</td>
						<td style="text-align: right;">${ item.componenteCurricular.chTotal }h</td>
						<td style="text-align: right;">${ item.qtdTurmas }</td>
						<td style="text-align: right;">${ item.qtdDiscentes }</td>
						<td style="text-align: right;">
							<c:set var="desabilita" value="${false}"/>
							<c:forEach items="#{ cargaHorariaEadMBean.chRemovidas }" var="remover">
								<c:if test="${ remover.id == item.id}">
									<c:set var="desabilita" value="${true}"/>
								</c:if>
							</c:forEach>
							<c:choose>
								<c:when test="${ desabilita }">
									<h:outputText value="#{ item.chDedicada }h" id="chDedicadaRO"/> 
								</c:when>
								<c:otherwise>
									<h:inputText value="#{ item.chDedicada }" size="3" maxlength="2" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="chDedicada" style="text-align:right;"/> h
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<c:forEach items="#{ cargaHorariaEadMBean.cadastrosDesatualizados }" var="observacao">
						<c:if test="${ observacao.value != '' && observacao.key == item.id}">
							<tr class="${ azul ? "linhaPar" : "linhaImpar" }">
								<td></td>
								<td style="text-align: left;" colspan="5">
									<b>Observações:</b> ${ observacao.value } 
								</td>
							</tr>
						</c:if>
					</c:forEach>
					<c:set var="azul" value="${ not azul }" />
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="6">
						<h:commandButton value="Atualizar Cargas Horárias" action="#{cargaHorariaEadMBean.atualizar}" id="atualizar"/>
						<h:commandButton value="Cancelar" action="#{cargaHorariaEadMBean.cancelar}" onclick="#{confirm}" id="cancelar2"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>